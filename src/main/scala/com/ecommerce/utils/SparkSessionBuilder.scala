package com.ecommerce.utils

import com.typesafe.config.Config
import org.apache.spark.sql.SparkSession

/** Construction de la SparkSession (Partie 6, Q6.1).
  * Tous les reglages de Spark viennent de application.conf (Q7.1) :
  * machine utilisee et nombre de partitions. Chaque valeur a un
  * defaut si la cle est absente du fichier.
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