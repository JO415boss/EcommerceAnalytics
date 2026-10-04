package com.ecommerce.analytics

import org.apache.spark.sql.{Column, DataFrame, Dataset}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

/**
 * Transformations avancees (Partie 3, Membre B).
 * @param useBroadcast lu depuis application.conf (app.optimization.enable-broadcast)
 */
class DataTransformation(useBroadcast: Boolean = true) {

  private def maybeBroadcast(df: DataFrame): DataFrame =
    if (useBroadcast) broadcast(df) else df

  /** Tranche d'age (Q3.2). Le sujet laisse 25 ans hors des tranches : ici 25 -> Adulte. */
  def ageGroup(age: Column): Column =
    when(age.isNull, lit(null).cast("string"))
      .when(age < 25, "Jeune")
      .when(age <= 44, "Adulte")
      .when(age <= 64, "\u00c2ge Moyen") // \u00c2 = A accent circonflexe (evite les soucis d'encodage)
      .otherwise("Senior")

  /** Q3.2 : jointures + UDF temporelle + fenetres rang / total + tranche d'age. */
  def enrichTransactionData(
      transactions: Dataset[_],
      users: Dataset[_],
      products: Dataset[_],
      merchants: Dataset[_]): DataFrame = {

    // Colonnes homonymes renommees avant la jointure (name, category, price)
    val usersSel = users.toDF()
      .select("user_id", "age", "annual_income", "city", "customer_segment")
    val productsSel = products.toDF().select(
      col("product_id"),
      col("name").as("product_name"),
      col("price").as("product_price"),
      col("rating"),
      col("stock"))
    val merchantsSel = merchants.toDF().select(
      col("merchant_id"),
      col("name").as("merchant_name"),
      col("category").as("merchant_category"),
      col("region"),
      col("commission_rate"))

    // LEFT JOIN partout : une ligne par transaction valide, meme si une reference est orpheline
    val joined = transactions.toDF()
      .join(usersSel, Seq("user_id"), "left")
      .join(maybeBroadcast(productsSel), Seq("product_id"), "left")
      .join(maybeBroadcast(merchantsSel), Seq("merchant_id"), "left")

    val withTime = joined
      .withColumn("transaction_date", to_timestamp(col("timestamp"), "yyyyMMddHHmmss"))
      .withColumn("time_features", TimeFeatures.extractTimeFeatures(col("timestamp")))
      .select(col("*"), col("time_features.*")) // eclate la struct en colonnes simples
      .drop("time_features")
      .withColumn("age_group", ageGroup(col("age")))

    val wOrdered = Window.partitionBy("user_id").orderBy("transaction_date", "transaction_id")
    val wUser = Window.partitionBy("user_id")

    withTime
      .withColumn("transaction_rank", row_number().over(wOrdered))
      .withColumn("total_transactions_user", count(lit(1)).over(wUser))
  }

  /** Q3.3 : montant cumule 7 jours, utilisateur actif, delai entre achats. */
  def addBehaviorFeatures(df: DataFrame): DataFrame = {
    val secondsPerDay = 86400L
    val wRange7 = Window.partitionBy("user_id")
      .orderBy(col("transaction_date").cast("long"))
      .rangeBetween(-7 * secondsPerDay, Window.currentRow)
    val wOrdered = Window.partitionBy("user_id").orderBy("transaction_date", "transaction_id")
    val txDay = to_date(col("transaction_date"))

    df
      .withColumn("montant_cumule_7j", sum("amount").over(wRange7))
      // countDistinct est interdit sur une fenetre : collect_set + size donne le meme resultat
      .withColumn("is_active_user",
        when(size(collect_set(txDay).over(wRange7)) >= 5, 1).otherwise(0))
      .withColumn("jours_depuis_achat_precedent",
        datediff(txDay, lag(txDay, 1).over(wOrdered)))
  }

  /** Q3.2 + Q3.3 : DataFrame final "transactions_enrichies", colonnes dans l'ordre du sujet. */
  def enrichAll(
      transactions: Dataset[_],
      users: Dataset[_],
      products: Dataset[_],
      merchants: Dataset[_]): DataFrame = {
    val enriched = addBehaviorFeatures(
      enrichTransactionData(transactions, users, products, merchants))
    enriched.select(
      "transaction_id", "user_id", "product_id", "merchant_id", "amount", "timestamp",
      "transaction_date", "location", "payment_method", "category",
      "age", "annual_income", "city", "customer_segment", "age_group",
      "product_name", "product_price", "rating", "stock",
      "merchant_name", "merchant_category", "region", "commission_rate",
      "hour", "day_of_week", "month", "is_weekend", "day_period", "is_working_hours",
      "transaction_rank", "total_transactions_user",
      "montant_cumule_7j", "is_active_user", "jours_depuis_achat_precedent")
  }
}