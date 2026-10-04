package com.ecommerce.utils

import java.io.File
import org.apache.spark.sql.{DataFrame, SaveMode}

/** Objet commun d'ecriture des resultats (Q1.1).
  * Chaque membre appelle ces fonctions plutot que de dupliquer le code :
  * - writeCsvParquet(df, name, outputPath) : double ecriture
  *   <output>/csv/<name>/ (header = true, coalesce(1) pour les petits
  *   resultats) et <output>/parquet/<name>/, en mode overwrite.
  * - writeQualityReport(df, outputPath, dateSuffix) : rapport de qualite
  *   (Q2.4) en un seul fichier CSV rapport_qualite_yyyymmdd.csv.
  */
object DataFrameWriterUtils {

  /** Double ecriture CSV + Parquet d'un resultat nomme. */
  def writeCsvParquet(
      df: DataFrame,
      name: String,
      outputPath: String,
      coalesceSingleFile: Boolean = true
  ): Unit = {
    val prepared = if (coalesceSingleFile) df.coalesce(1) else df
    prepared.write
      .mode(SaveMode.Overwrite)
      .option("header", "true")
      .csv(s"$outputPath/csv/$name")
    df.write
      .mode(SaveMode.Overwrite)
      .parquet(s"$outputPath/parquet/$name")
  }

  /** Rapport de qualite (Q2.4) : un seul fichier CSV nomme, avec en-tete. */
  def writeQualityReport(
      df: DataFrame,
      outputPath: String,
      dateSuffix: String
  ): Unit = {
    val tmpDir = new File(outputPath, "rapport_qualite_tmp").getPath
    df.coalesce(1).write
      .mode(SaveMode.Overwrite)
      .option("header", "true")
      .csv(tmpDir)
    val part = new File(tmpDir).listFiles().find(_.getName.startsWith("part-"))
    part match {
      case Some(f) =>
        val dest = new File(outputPath, s"rapport_qualite_$dateSuffix.csv")
        if (dest.exists()) dest.delete()
        f.renameTo(dest)
        new File(tmpDir).listFiles().foreach(_.delete())
        new File(tmpDir).delete()
      case None =>
        throw new IllegalStateException(
          "writeQualityReport : aucun part-*.csv genere dans " + tmpDir
        )
    }
  }
}