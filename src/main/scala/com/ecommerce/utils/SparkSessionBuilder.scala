package com.ecommerce.utils

import com.typesafe.config.Config
import org.apache.spark.sql.SparkSession

/** Construction de la SparkSession (Partie 6, Q6.1).
  * Tous les parametres Spark sont externalises dans application.conf (Q7.1) :
  * master, shuffle partitions, cache et broadcast. Chaque valeur lue via
  * ConfigLoader possede une valeur par defaut si la cle est absente.
  */
object SparkSessionBuilder {

  def build(conf: Config): SparkSession = {
    val master = ConfigLoader.getString(conf, "app.spark.master", "local[*]")
    val shufflePartitions =
      ConfigLoader.getInt(conf, "app.spark.shuffle.partitions", 200).toString

    SparkSession
      .builder()
      .appName(ConfigLoader.getString(conf, "app.name", "EcommerceAnalytics"))
      .master(master)
      .config("spark.sql.shuffle.partitions", shufflePartitions)
      .getOrCreate()
  }
}