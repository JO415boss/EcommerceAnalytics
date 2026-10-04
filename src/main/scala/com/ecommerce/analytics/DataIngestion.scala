package com.ecommerce.analytics

import com.ecommerce.models.{Merchant, Product, Transaction, User}
import com.ecommerce.utils.ConfigLoader
import com.typesafe.config.Config
import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.sql.types._

/** Ingestion multi-format des quatre jeux de donnees (Q2.1).
  * - transactions.csv : schema defini explicitement.
  * - users.json : une ligne = un objet, champ imbrique preferred_categories.
  * - products.parquet : format optimise, charge tel quel.
  * - merchants.csv : schema infere par Spark.
  * Les chemins proviennent de application.conf, jamais codes en dur (Q7.1).
  * Blocs try-catch : toute erreur de lecture est affichee sans faire
  * echouer brutalement le job (Q2.3). Le nombre de lignes lues est
  * affiche avant validation (Q2.3).
  */
class DataIngestion(spark: SparkSession, conf: Config) {

  import spark.implicits._

  private val txPath: String =
    ConfigLoader.getString(conf, "app.data.input.transactions", "data/transactions.csv")
  private val usersPath: String =
    ConfigLoader.getString(conf, "app.data.input.users", "data/users.json")
  private val productsPath: String =
    ConfigLoader.getString(conf, "app.data.input.products", "data/products.parquet")
  private val merchantsPath: String =
    ConfigLoader.getString(conf, "app.data.input.merchants", "data/merchants.csv")

  private val transactionSchema: StructType = StructType(
    Seq(
      StructField("transaction_id", StringType, nullable = true),
      StructField("user_id", StringType, nullable = true),
      StructField("product_id", StringType, nullable = true),
      StructField("merchant_id", StringType, nullable = true),
      StructField("amount", DoubleType, nullable = true),
      StructField("timestamp", StringType, nullable = true),
      StructField("location", StringType, nullable = true),
      StructField("payment_method", StringType, nullable = true),
      StructField("category", StringType, nullable = true)
    )
  )

  def readTransactions(): Dataset[Transaction] = {
    try {
      val df = spark.read
        .option("header", "true")
        .schema(transactionSchema)
        .csv(txPath)
      println(s"[ingestion] transactions lues : ${df.count()} lignes ($txPath)")
      df.as[Transaction]
    } catch {
      case e: Exception =>
        println(s"[ingestion] ERREUR lecture transactions ($txPath) : ${e.getMessage}")
        spark.emptyDataset[Transaction]
    }
  }

  def readUsers(): Dataset[User] = {
    try {
      val df = spark.read.json(usersPath)
      println(s"[ingestion] users lus : ${df.count()} lignes ($usersPath)")
      df.as[User]
    } catch {
      case e: Exception =>
        println(s"[ingestion] ERREUR lecture users ($usersPath) : ${e.getMessage}")
        spark.emptyDataset[User]
    }
  }

  def readProducts(): Dataset[Product] = {
    try {
      val df = spark.read.parquet(productsPath)
      println(s"[ingestion] products lus : ${df.count()} lignes ($productsPath)")
      df.as[Product]
    } catch {
      case e: Exception =>
        println(s"[ingestion] ERREUR lecture products ($productsPath) : ${e.getMessage}")
        spark.emptyDataset[Product]
    }
  }

  def readMerchants(): Dataset[Merchant] = {
    try {
      val df = spark.read
        .option("header", "true")
        .option("inferSchema", "true")
        .csv(merchantsPath)
      println(s"[ingestion] merchants lus : ${df.count()} lignes ($merchantsPath)")
      df.as[Merchant]
    } catch {
      case e: Exception =>
        println(s"[ingestion] ERREUR lecture merchants ($merchantsPath) : ${e.getMessage}")
        spark.emptyDataset[Merchant]
    }
  }
}