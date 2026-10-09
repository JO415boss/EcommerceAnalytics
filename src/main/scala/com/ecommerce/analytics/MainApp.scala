package com.ecommerce.analytics

import com.ecommerce.models.{Merchant, Product, Transaction, User}
import com.ecommerce.utils.{ConfigLoader, DataFrameWriterUtils, SparkSessionBuilder}
import com.typesafe.config.Config
import org.apache.spark.sql.{Dataset, SparkSession}

/** Application principale EcommerceAnalyticsApp (Partie 6, Q6.1).
  *
  * Elle orchestre tout le pipeline :
  *   ingestion (lecture + validation + rapport de qualite + rejets)
  *   -> transformation (enrichissement du membre B)
  *   -> analytics (KPI, cohortes et optimisations du membre C).
  *
  * Bonus Q6.2 : execution modulaire — le premier argument choisit l'etape
  * lancee, "all" par defaut :
  *   run-sbt.cmd "run ingestion"        # Q2 : lecture, validation, rapport
  *   run-sbt.cmd "run transformation"   # Q3 : transactions enrichies
  *   run-sbt.cmd "run analytics"        # Q4 + Q5 : KPI et optimisations
  *   run-sbt.cmd "run"                  # tout le pipeline
  *
  * Chemins d'entree/sortie lus dans application.conf (Q7.1) ; toutes les
  * ecritures passent par DataFrameWriterUtils : chaque resultat est ecrit deux
  * fois, dans output/csv/<nom>/ et output/parquet/<nom>/, en mode overwrite
  * (convention Q6.1 du sujet).
  */
object MainApp {

  /** Resultat de l'etape ingestion : jeux lus + versions valides (Q2.2)
    * + DataFrames de rejets (Q2.3) necessaires au rapport de qualite.
    */
  private case class JeuxValides(
      spark: SparkSession,
      txRead: Dataset[Transaction], txValides: Dataset[Transaction],
      txRejetes: org.apache.spark.sql.DataFrame,
      usersRead: Dataset[User], usersValides: Dataset[User],
      usersRejetes: org.apache.spark.sql.DataFrame,
      productsRead: Dataset[Product], productsValides: Dataset[Product],
      productsRejetes: org.apache.spark.sql.DataFrame,
      merchantsRead: Dataset[Merchant], merchantsValides: Dataset[Merchant],
      merchantsRejetes: org.apache.spark.sql.DataFrame
  )

  def main(args: Array[String]): Unit = {
    val etape = args.headOption.getOrElse("all").toLowerCase
    val etapesConnues = Set("ingestion", "transformation", "analytics", "all")
    if (!etapesConnues.contains(etape)) {
      println("Usage : MainApp [ingestion|transformation|analytics|all]")
      println(s"Etape inconnue : '$etape'")
      System.exit(1)
    }

    val conf = ConfigLoader.load()
    val outputPath =
      ConfigLoader.getString(conf, "app.data.output.path", "output/")
    val spark = SparkSessionBuilder.build(conf)

    try {
      etape match {
        case "ingestion" =>
          val jeux = chargerDonnees(spark, conf)
          ecrireRapportEtRejets(jeux, outputPath)

        case "transformation" =>
          val jeux = chargerDonnees(spark, conf)
          ecrireTransactionsEnrichies(jeux, conf, outputPath)

        case "analytics" =>
          val jeux = chargerDonnees(spark, conf)
          ecrireAnalyses(spark, conf, jeux, outputPath)

        case "all" =>
          val jeux = chargerDonnees(spark, conf)
          ecrireRapportEtRejets(jeux, outputPath)
          ecrireTransactionsEnrichies(jeux, conf, outputPath)
          ecrireAnalyses(spark, conf, jeux, outputPath)
      }
      println(s"[main] etape '$etape' terminee, sorties dans $outputPath")
    } finally {
      spark.stop()
    }
  }

  /** Etape ingestion (Partie 2, membre A) : lecture des 4 fichiers puis
    * validation ; le rapport de qualite et les 4 fichiers de rejets sont ecrits.
    */
  private def chargerDonnees(spark: SparkSession, conf: Config): JeuxValides = {
    val ingestion = new DataIngestion(spark, conf)
    val validation = new DataValidation(spark, conf)

    val txRead = ingestion.readTransactions()
    val usersRead = ingestion.readUsers()
    val productsRead = ingestion.readProducts()
    val merchantsRead = ingestion.readMerchants()

    val (txValides, txRejetes) = validation.validateTransactions(txRead)
    val (usersValides, usersRejetes) = validation.validateUsers(usersRead)
    val (productsValides, productsRejetes) = validation.validateProducts(productsRead)
    val (merchantsValides, merchantsRejetes) = validation.validateMerchants(merchantsRead)

    JeuxValides(spark, txRead, txValides, txRejetes,
      usersRead, usersValides, usersRejetes,
      productsRead, productsValides, productsRejetes,
      merchantsRead, merchantsValides, merchantsRejetes)
  }

  /** Rapport de qualite (Q2.4) + rejets (Q2.3), fichiers du membre A appeles
    * tels quels : MainApp est le seul a les lancer dans le pipeline.
    */
  private def ecrireRapportEtRejets(jeux: JeuxValides, outputPath: String): Unit = {
    DataQualityReport.buildAndSave(
      jeux.spark, outputPath,
      jeux.txRead, jeux.txValides,
      jeux.usersRead, jeux.usersValides,
      jeux.productsRead, jeux.productsValides,
      jeux.merchantsRead, jeux.merchantsValides
    )
    DataQualityReport.saveRejections(
      outputPath,
      jeux.txRejetes, jeux.usersRejetes,
      jeux.productsRejetes, jeux.merchantsRejetes
    )
    println(s"[main] rapport de qualite et rejets ecrits dans $outputPath")
  }

  /** Etape transformation (Partie 3, membre B) : seules les transactions deja
    * valides sont enrichies (contraignante documentee par le membre B), puis
    * le DataFrame "transactions_enrichies" est ecrit en CSV + Parquet.
    * Le CSV brut fait ~138 000 lignes : on garde les partitions d'origine
    * (coalesceSingleFile = false) pour ne pas serialiser tout dans un fichier.
    */
  private def ecrireTransactionsEnrichies(
      jeux: JeuxValides, conf: Config, outputPath: String): Unit = {
    val enrichies = construireTransformation(conf).enrichAll(
      jeux.txValides, jeux.usersValides,
      jeux.productsValides, jeux.merchantsValides
    )
    ecrire(enrichies, "transactions_enrichies", outputPath, coalesceSingleFile = false)
  }

  /** Etape analytics (Parties 4 et 5, membre C) : ecrit les resultats Q4.1,
    * Q4.2, bonus Q4.3/Q4.4 et la mesure de gain Q5.3, dans l'ordre :
    * d'abord la mesure (calcul a froid, avant le cache), puis le cache du
    * DataFrame enrichi (Q5.1) reutilise par tous les KPI.
    */
  private def ecrireAnalyses(
      spark: SparkSession, conf: Config,
      jeux: JeuxValides, outputPath: String): Unit = {
    val optimisations = new SparkOptimizations(conf)
    val analytics = new Analytics(optimisations)

    val enrichies = construireTransformation(conf).enrichAll(
      jeux.txValides, jeux.usersValides,
      jeux.productsValides, jeux.merchantsValides
    )
    val marchands = jeux.merchantsValides.toDF()

    // Q5.3 : mesure AVANT le cache, pour comparer un calcul froid et un calcul chaud
    val gain = optimisations.measureGain(spark, enrichies, marchands)
    ecrire(gain, "gain_optimisations", outputPath)

    // Q5.1 : un seul cache partage par tous les KPI (evite de recalculer l'enrichissement)
    val enrichiesCache = optimisations.maybeCache(enrichies, "transactions_enrichies")
    try {
      ecrire(analytics.kpiMarchands(enrichiesCache, marchands), "kpi_marchands", outputPath)         // Q4.1
      ecrire(analytics.cohortesRetention(enrichiesCache), "cohortes_retention", outputPath)           // Q4.2
      ecrire(analytics.segmentationRfm(enrichiesCache), "rfm_utilisateurs", outputPath)               // Q4.3 bonus
      ecrire(analytics.topProduits(enrichiesCache), "top_produits", outputPath)                       // Q4.4 bonus
      ecrire(analytics.ventesParCategorie(enrichiesCache), "ventes_categories", outputPath)           // Q4.4 bonus
    } finally {
      optimisations.unpersist(enrichiesCache)
    }
  }

  private def construireTransformation(conf: Config): DataTransformation =
    new DataTransformation(
      ConfigLoader.getBoolean(conf, "app.optimization.enable-broadcast", true)
    )

  /** Affiche un apercu en console puis ecrit le resultat en CSV + Parquet
    * via l'objet commun DataFrameWriterUtils (convention Q6.1).
    */
  private def ecrire(
      df: org.apache.spark.sql.DataFrame,
      nom: String,
      outputPath: String,
      coalesceSingleFile: Boolean = true): Unit = {
    df.show(20, truncate = false)
    DataFrameWriterUtils.writeCsvParquet(df, nom, outputPath, coalesceSingleFile)
    println(s"[main] $nom ecrit : $outputPath/csv/$nom et $outputPath/parquet/$nom")
  }
}
