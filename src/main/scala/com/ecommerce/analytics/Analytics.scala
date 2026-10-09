package com.ecommerce.analytics

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

/** Analytique business (Partie 4, Membre C).
  * Toutes les entrees sont des DataFrame de transactions deja valides et
  * enrichis par le membre B (DataTransformation.enrichAll) : chaque methode
  * renvoie un DataFrame que MainApp ecrit en CSV + Parquet via
  * DataFrameWriterUtils (convention Q6.1).
  *
  * Questions traitees :
  * - Q4.1  kpiMarchands        : KPI par marchand (CA, panier moyen, clients).
  * - Q4.2  cohortesRetention    : matrice de retention mensuelle par cohortes.
  * - Q4.3  segmentationRfm      (bonus) : RFM par utilisateur + segment.
  * - Q4.4  topProduits et ventesParCategorie (bonus).
  *
  * @param optimisations instance partagee pour le broadcast (Q5.2),
  *                      active ou non selon app.optimization.enable-broadcast.
  */
class Analytics(optimisations: SparkOptimizations) {

  /** Q4.1 : KPI marchands.
    * On part du referentiel marchands (LEFT JOIN vers les metriques) pour
    * garder aussi les marchands sans vente — decision technique du groupe
    * inscrite dans CONTRIBUTIONS.md.
    * Metriques : nombre de transactions, CA total, panier moyen,
    * nombre de clients uniques, produits vendus et commission estimee
    * (ca_total x commission_rate).
    */
  def kpiMarchands(transactions: DataFrame, merchants: DataFrame): DataFrame = {
    val metriques = transactions
      .filter(col("merchant_id").isNotNull)
      .groupBy("merchant_id")
      .agg(
        count(lit(1)).as("nb_transactions"),
        round(sum("amount"), 2).as("ca_total"),
        round(avg("amount"), 2).as("panier_moyen"),
        countDistinct("user_id").as("nb_clients_uniques"),
        countDistinct("product_id").as("nb_produits_vendus")
      )

    val referentiel = optimisations.maybeBroadcast(
      merchants.select(
        col("merchant_id"),
        col("name").as("merchant_name"),
        col("category").as("merchant_category"),
        col("region"),
        col("commission_rate")
      )
    )

    referentiel
      .join(metriques, Seq("merchant_id"), "left")
      // marchand sans vente : metriques a zero plutot que null
      .withColumn("nb_transactions", coalesce(col("nb_transactions"), lit(0L)))
      .withColumn("ca_total", coalesce(col("ca_total"), lit(0.0)))
      .withColumn("panier_moyen", coalesce(col("panier_moyen"), lit(0.0)))
      .withColumn("nb_clients_uniques", coalesce(col("nb_clients_uniques"), lit(0L)))
      .withColumn("nb_produits_vendus", coalesce(col("nb_produits_vendus"), lit(0L)))
      .withColumn(
        "commission_estimee",
        round(col("ca_total") * coalesce(col("commission_rate"), lit(0.0)), 2)
      )
      .select(
        "merchant_id", "merchant_name", "merchant_category", "region", "commission_rate",
        "nb_transactions", "ca_total", "panier_moyen",
        "nb_clients_uniques", "nb_produits_vendus", "commission_estimee"
      )
      .orderBy(desc("ca_total"), col("merchant_id"))
  }

  /** Q4.2 : cohortes de retention.
    * Une cohort = les utilisateurs dont le premier achat a lieu dans le meme
    * mois. Pour chaque cohort on compte, mois apres mois (mois_index 0, 1, 2...),
    * combien d'utilisateurs ont encore achete, et le taux de retention
    * (actifs / taille de la cohort, en %).
    * Sortie : cohort_month, mois_index, taille_cohorte,
    *          nb_utilisateurs_actifs, taux_retention_pct.
    */
  def cohortesRetention(transactions: DataFrame): DataFrame = {
    val avecDate = transactions
      .filter(col("transaction_date").isNotNull && col("user_id").isNotNull)

    val premierAchat = avecDate
      .groupBy("user_id")
      .agg(min("transaction_date").as("premier_achat"))

    val avecCohorte = avecDate
      .join(premierAchat, Seq("user_id"), "inner")
      .withColumn("cohort_month", date_format(col("premier_achat"), "yyyy-MM"))
      .withColumn("mois_activite", date_format(col("transaction_date"), "yyyy-MM"))

    // un utilisateur n'apparait qu'une fois par (cohort, mois d'activite)
    val activite = avecCohorte
      .select("user_id", "cohort_month", "mois_activite")
      .distinct()
      .withColumn(
        "mois_index",
        round(
          months_between(to_date(col("mois_activite")), to_date(col("cohort_month"))),
          0
        ).cast("int")
      )

    val matrice = activite
      .groupBy("cohort_month", "mois_index")
      .agg(countDistinct("user_id").as("nb_utilisateurs_actifs"))

    val tailleCohorte = matrice
      .filter(col("mois_index") === 0)
      .select(
        col("cohort_month").as("cohort_ref"),
        col("nb_utilisateurs_actifs").as("taille_cohorte")
      )

    matrice
      .join(tailleCohorte, matrice("cohort_month") === tailleCohorte("cohort_ref"), "left")
      .drop("cohort_ref")
      .withColumn(
        "taux_retention_pct",
        round(
          col("nb_utilisateurs_actifs") / coalesce(col("taille_cohorte"), lit(1L)) * 100,
          2
        )
      )
      .select(
        "cohort_month", "mois_index", "taille_cohorte",
        "nb_utilisateurs_actifs", "taux_retention_pct"
      )
      .orderBy("cohort_month", "mois_index")
  }

  /** Q4.3 (bonus) : segmentation RFM par utilisateur.
    * - Recency  : jours depuis le dernier achat (rapport a la date du
    *              dernier achet du jeu : determine, pas de System.today).
    * - Frequency : nombre de transactions.
    * - Monetary  : montant total depense.
    * Chaque composante est notee de 1 a 5 avec ntile(5) (5 = meilleur),
    * rfm_score = r*100 + f*10 + m, puis un segment lisible est deduit.
    */
  def segmentationRfm(transactions: DataFrame): DataFrame = {
    val avecDate = transactions.filter(col("transaction_date").isNotNull)

    val reference = avecDate.agg(max("transaction_date")).head().getTimestamp(0)

    val base = avecDate
      .filter(col("user_id").isNotNull)
      .groupBy("user_id")
      .agg(
        datediff(lit(reference), max("transaction_date")).as("recency_jours"),
        count(lit(1)).as("frequence"),
        round(sum("amount"), 2).as("monetaire")
      )

    val wRecency = Window.orderBy(col("recency_jours").asc, col("user_id"))
    val wFreqMon = Window.orderBy(col("frequence").desc, col("monetaire").desc, col("user_id"))

    base
      // 6 - ntile : 5 est toujours le meilleur score
      .withColumn("r_score", lit(6) - ntile(5).over(wRecency))
      .withColumn("f_score", lit(6) - ntile(5).over(wFreqMon))
      .withColumn("m_score", lit(6) - ntile(5).over(wFreqMon))
      .withColumn("rfm_score", col("r_score") * 100 + col("f_score") * 10 + col("m_score"))
      .withColumn(
        "segment",
        when(col("r_score") >= 4 && col("f_score") >= 4 && col("m_score") >= 4, "Meilleurs clients")
          .when(col("r_score") <= 2 && col("f_score") >= 4, "A risque")
          .when(col("r_score") <= 2 && col("f_score") >= 2, "A recontacter")
          .when(col("r_score") >= 4 && col("f_score") <= 2, "Nouveaux clients")
          .when(col("f_score") >= 3 && col("m_score") >= 3, "Fideles")
          .otherwise("Occasionnels")
      )
      .select(
        "user_id", "recency_jours", "frequence", "monetaire",
        "r_score", "f_score", "m_score", "rfm_score", "segment"
      )
      .orderBy(desc("rfm_score"), col("user_id"))
  }

  /** Q4.4 (bonus) : top produits par chiffre d'affaires. */
  def topProduits(transactions: DataFrame): DataFrame = {
    transactions
      .filter(col("product_id").isNotNull)
      .groupBy("product_id", "product_name", "category")
      .agg(
        count(lit(1)).as("nb_ventes"),
        round(sum("amount"), 2).as("ca_total"),
        round(avg("amount"), 2).as("panier_moyen"),
        round(avg("rating"), 2).as("note_moyenne")
      )
      .withColumn("rang", row_number().over(Window.orderBy(desc("ca_total"))))
      .select("rang", "product_id", "product_name", "category",
        "nb_ventes", "ca_total", "panier_moyen", "note_moyenne")
      .orderBy("rang")
  }

  /** Q4.4 (bonus) : ventes par categorie avec part de CA (%). */
  def ventesParCategorie(transactions: DataFrame): DataFrame = {
    transactions
      .filter(col("category").isNotNull)
      .groupBy("category")
      .agg(
        count(lit(1)).as("nb_ventes"),
        round(sum("amount"), 2).as("ca_total"),
        round(avg("amount"), 2).as("panier_moyen"),
        countDistinct("user_id").as("nb_clients_uniques")
      )
      .withColumn(
        "part_du_ca_pct",
        round(col("ca_total") / sum(col("ca_total")).over() * 100, 2)
      )
      .orderBy(desc("ca_total"))
  }
}
