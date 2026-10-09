package com.ecommerce.analytics

import com.ecommerce.models.{Merchant, Product, Transaction, User}
import com.ecommerce.utils.ConfigLoader
import com.typesafe.config.Config
import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.sql.functions._

/** Validation des quatre jeux de donnees (Q2.2).
  * Chaque fonction renvoie (valides, rejetes) : rien n'est perdu, chaque
  * ligne rejetee porte une colonne rejection_reason qui explique pourquoi
  * (plusieurs raisons possibles, separes par " | ").
  * Regles du sujet : transactions montant > 0 et timestamp de 14 caracteres ;
  * users 16 <= age <= 100 et annual_income > 0 ; products price > 0 et
  * 1 <= rating <= 5 ; merchants 0 <= commission_rate <= 1.
  * Les seuils viennent de application.conf (Q7.1), avec defaut si absents.
  * Le nombre de lignes valides est affiche (Q2.3).
  */
class DataValidation(spark: SparkSession, conf: Config) {

  import spark.implicits._

  private val tsLength: Int =
    ConfigLoader.getInt(conf, "app.validation.transaction.timestamp-length", 14)
  private val minAge: Int =
    ConfigLoader.getInt(conf, "app.validation.user.min-age", 16)
  private val maxAge: Int =
    ConfigLoader.getInt(conf, "app.validation.user.max-age", 100)
  private val minRating: Double =
    ConfigLoader.getDouble(conf, "app.validation.product.min-rating", 1.0)
  private val maxRating: Double =
    ConfigLoader.getDouble(conf, "app.validation.product.max-rating", 5.0)
  private val minCommission: Double =
    ConfigLoader.getDouble(conf, "app.validation.merchant.min-commission-rate", 0.0)
  private val maxCommission: Double =
    ConfigLoader.getDouble(conf, "app.validation.merchant.max-commission-rate", 1.0)

  def validateTransactions(ds: Dataset[Transaction]): (Dataset[Transaction], Dataset[org.apache.spark.sql.Row]) = {
    val df = ds.toDF()
    val badAmount = col("amount").isNull || !(col("amount") > 0)
    val badTs = col("timestamp").isNull || !(length(col("timestamp")) === tsLength)
    val reason = concat_ws(
      " | ",
      when(badAmount, lit("amount <= 0")),
      when(badTs, lit("timestamp_invalide"))
    )
    val ok = !badAmount && !badTs
    val valid = df.filter(ok).as[Transaction]
    val rejected = df.filter(!ok).withColumn("rejection_reason", reason)
    println(s"[validation] transactions valides : ${valid.count()} lignes")
    (valid, rejected)
  }

  def validateUsers(ds: Dataset[User]): (Dataset[User], Dataset[org.apache.spark.sql.Row]) = {
    val df = ds.toDF()
    val badAge = col("age").isNull || !(col("age") >= minAge && col("age") <= maxAge)
    val badIncome = col("annual_income").isNull || !(col("annual_income") > 0)
    val reason = concat_ws(
      " | ",
      when(badAge, lit("age_hors_intervalle")),
      when(badIncome, lit("income <= 0"))
    )
    val ok = !badAge && !badIncome
    val valid = df.filter(ok).as[User]
    val rejected = df
      .filter(!ok)
      .withColumn("preferred_categories", concat_ws(",", col("preferred_categories")))
      .withColumn("rejection_reason", reason)
    println(s"[validation] users valides : ${valid.count()} lignes")
    (valid, rejected)
  }

  def validateProducts(ds: Dataset[Product]): (Dataset[Product], Dataset[org.apache.spark.sql.Row]) = {
    val df = ds.toDF()
    val badPrice = col("price").isNull || !(col("price") > 0)
    val badRating = col("rating").isNull || !(col("rating") >= minRating && col("rating") <= maxRating)
    val reason = concat_ws(
      " | ",
      when(badPrice, lit("price <= 0")),
      when(badRating, lit("rating_hors_intervalle"))
    )
    val ok = !badPrice && !badRating
    val valid = df.filter(ok).as[Product]
    val rejected = df.filter(!ok).withColumn("rejection_reason", reason)
    println(s"[validation] products valides : ${valid.count()} lignes")
    (valid, rejected)
  }

  def validateMerchants(ds: Dataset[Merchant]): (Dataset[Merchant], Dataset[org.apache.spark.sql.Row]) = {
    val df = ds.toDF()
    val badRate = col("commission_rate").isNull ||
      !(col("commission_rate") >= minCommission && col("commission_rate") <= maxCommission)
    val reason = when(badRate, lit("commission_hors_intervalle"))
    val ok = !badRate
    val valid = df.filter(ok).as[Merchant]
    val rejected = df.filter(!ok).withColumn("rejection_reason", reason)
    println(s"[validation] merchants valides : ${valid.count()} lignes")
    (valid, rejected)
  }
}