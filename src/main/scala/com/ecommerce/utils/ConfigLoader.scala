package com.ecommerce.utils

import com.typesafe.config.{Config, ConfigFactory}

/** Chargement de application.conf (Q7.1).
  * Aucun chemin, seuil ou reglage n'est ecrit en dur dans le code Scala :
  * tout passe par ce chargeur. Si une cle manque dans le fichier,
  * la valeur par defaut donnee en argument est utilisee.
  */
object ConfigLoader {

  /** Charge application.conf (fichier place dans src/main/resources). */
  def load(): Config = ConfigFactory.load()

  def getString(conf: Config, path: String, default: String): String =
    if (conf.hasPath(path)) conf.getString(path) else default

  def getInt(conf: Config, path: String, default: Int): Int =
    if (conf.hasPath(path)) conf.getInt(path) else default

  def getLong(conf: Config, path: String, default: Long): Long =
    if (conf.hasPath(path)) conf.getLong(path) else default

  def getDouble(conf: Config, path: String, default: Double): Double =
    if (conf.hasPath(path)) conf.getDouble(path) else default

  def getBoolean(conf: Config, path: String, default: Boolean): Boolean =
    if (conf.hasPath(path)) conf.getBoolean(path) else default
}