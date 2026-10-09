package com.ecommerce.analytics

import com.ecommerce.utils.ConfigLoader
import com.typesafe.config.Config
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.broadcast
import org.apache.spark.storage.StorageLevel

/** Optimisations Spark (Partie 5, Membre C).
  *
  * Questions traitees :
  * - Q5.1 maybeCache / unpersist : mise en cache des DataFrame reutilises
  *   (MEMORY_AND_DISK), activee ou non par app.optimization.enable-cache.
  * - Q5.2 maybeBroadcast : hint de broadcast sur les petites tables de
  *   reference (merchants), active ou non par app.optimization.enable-broadcast.
  * - Q5.3 measureGain (bonus) : mesure du gain en duree (ms) apporte par le
  *   cache et par le broadcast, ecrite en CSV + Parquet par MainApp.
  *
  * Les deux interrupteurs viennent de application.conf (Q7.1) : aucun reglage
  * n'est ecrit en dur dans ce fichier.
  *
  * @param conf configuration Typesafe chargee par ConfigLoader.
  */
class SparkOptimizations(conf: Config) {

  /** Q5.1 : activation du cache (defaut : true si la cle manque). */
  val enableCache: Boolean =
    ConfigLoader.getBoolean(conf, "app.optimization.enable-cache", true)

  /** Q5.2 : activation du broadcast (defaut : true si la cle manque). */
  val enableBroadcast: Boolean =
    ConfigLoader.getBoolean(conf, "app.optimization.enable-broadcast", true)

  /** Q5.1 : met en cache un DataFrame si le cache est actif.
    * MEMORY_AND_DISK : le cache ne fait jamais echouer le job si la table
    * ne tient pas en memoire, elle est deplacee sur disque.
    */
  def maybeCache(df: DataFrame, name: String): DataFrame = {
    if (enableCache) {
      val cached = df.persist(StorageLevel.MEMORY_AND_DISK)
      println(s"[optimization] cache active : $name (MEMORY_AND_DISK)")
      cached
    } else {
      println(s"[optimization] cache desactive (app.optimization.enable-cache = false)")
      df
    }
  }

  /** Q5.2 : renvoie la table avec un hint de broadcast si le broadcast est actif.
    * A appliquer sur la petite table d'une jointure (ici le referentiel
    * marchands) pour eviter le cote "shuffle" de la jointure.
    */
  def maybeBroadcast(df: DataFrame): DataFrame =
    if (enableBroadcast) broadcast(df) else df

  /** Libere le cache pris par maybeCache. */
  def unpersist(df: DataFrame): Unit =
    if (enableCache) df.unpersist(blocking = false)

  /** Chronometre un bloc d'action et renvoie sa duree en millisecondes. */
  def timeMs(block: => Any): Long = {
    val debut = System.nanoTime()
    block
    (System.nanoTime() - debut) / 1000000L
  }

  /** Q5.3 (bonus) : mesure du gain des optimisations.
    * Deux comparaisons, chacune sur une copie isolee pour ne pas perturber le
    * cache utilise par la suite dans MainApp :
    * 1. comptage de transactions sans cache puis avec cache (une premiere
    *    action sans cache sert de mesure "froid") ;
    * 2. jointure transactions x marchands sans broadcast puis avec broadcast.
    * Chaque variante produit une ligne : test, variante, duree_ms, gain_pct
    * (gain_pct = (sans - avec) / sans x 100, 0 pour la variante de base).
    */
  def measureGain(spark: SparkSession, transactions: DataFrame, merchants: DataFrame): DataFrame = {
    import spark.implicits._

    // ---- 1. gain du cache (Q5.1) ----
    val sansCacheDf = transactions.select("*")
    val dureeSansCache = timeMs { sansCacheDf.count() }
    val aMettreEnCache = transactions.select("*").persist(StorageLevel.MEMORY_AND_DISK)
    val dureeAvecCache = timeMs { aMettreEnCache.count() }
    aMettreEnCache.unpersist(blocking = false)

    // ---- 2. gain du broadcast (Q5.2) ----
    val base = transactions.select("merchant_id").filter(colIsNotNull("merchant_id"))
    val sansBc = timeMs {
      base.join(merchants.select("merchant_id"), Seq("merchant_id"), "inner").count()
    }
    val avecBc = timeMs {
      base.join(broadcast(merchants.select("merchant_id")), Seq("merchant_id"), "inner").count()
    }

    val gainCache = pourcent(dureeSansCache, dureeAvecCache)
    val gainBc = pourcent(sansBc, avecBc)

    Seq(
      ("comptage_transactions", "sans_cache", dureeSansCache, 0.0),
      ("comptage_transactions", "avec_cache", dureeAvecCache, gainCache),
      ("jointure_marchands", "sans_broadcast", sansBc, 0.0),
      ("jointure_marchands", "avec_broadcast", avecBc, gainBc)
    ).toDF("test", "variante", "duree_ms", "gain_pct")
  }

  private def colIsNotNull(nom: String) =
    org.apache.spark.sql.functions.col(nom).isNotNull

  /** (sans - avec) / sans x 100, arrondi a 2 decimales ; 0 si duree nulle. */
  private def pourcent(sans: Long, avec: Long): Double =
    if (sans <= 0) 0.0
    else BigDecimal((sans - avec).toDouble / sans * 100)
      .setScale(2, BigDecimal.RoundingMode.HALF_UP)
      .toDouble
}
