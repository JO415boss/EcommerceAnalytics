package com.ecommerce.analytics

import com.ecommerce.models.QualityReportRow
import com.ecommerce.utils.DataFrameWriterUtils
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.apache.spark.sql.functions._
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Rapport de qualite des donnees (Q2.4).
  * Produit un DataFrame de synthese avec une ligne par dataset :
  * dataset, nb_lignes_lues, nb_lignes_valides, nb_lignes_rejetees,
  * taux_rejet (%, arrondi a 2 decimales), nb_valeurs_nulles (toutes
  * colonnes confondues, sur les donnees lues).
  * Le rapport est affiche en console puis sauvegarde en CSV unique
  * nomme rapport_qualite_yyyymmdd.csv (date d'execution).
  */
object DataQualityReport {

  private def countNulls(df: org.apache.spark.sql.DataFrame): Long = {
    val perCol = df.columns.map(c => sum(when(col(c).isNull, 1).otherwise(0)).as(c))
    df.select(perCol: _*).collect()(0).toSeq.map {
      case n: Number => n.longValue()
      case _ => 0L
    }.sum
  }

  private def row(
      dataset: String,
      nbLues: Long,
      nbValides: Long,
      nbNulles: Long
  ): QualityReportRow = {
    val rejetees = nbLues - nbValides
    val taux =
      if (nbLues == 0) 0.0
      else BigDecimal(rejetees.toDouble / nbLues * 100)
        .setScale(2, BigDecimal.RoundingMode.HALF_UP)
        .toDouble
    QualityReportRow(dataset, nbLues, nbValides, rejetees, taux, nbNulles)
  }

  /** Construit, affiche et sauvegarde le rapport de qualite.
    * @return le chemin du fichier CSV genere.
    */
  def buildAndSave(
      spark: SparkSession,
      outputPath: String,
      txRead: Dataset[_], txValid: Dataset[_],
      usersRead: Dataset[_], usersValid: Dataset[_],
      productsRead: Dataset[_], productsValid: Dataset[_],
      merchantsRead: Dataset[_], merchantsValid: Dataset[_]
  ): String = {
    import spark.implicits._
    val rows = Seq(
      row("transactions", txRead.count(), txValid.count(), countNulls(txRead.toDF())),
      row("users", usersRead.count(), usersValid.count(), countNulls(usersRead.toDF())),
      row("products", productsRead.count(), productsValid.count(), countNulls(productsRead.toDF())),
      row("merchants", merchantsRead.count(), merchantsValid.count(), countNulls(merchantsRead.toDF()))
    )
    val report = spark.createDataset(rows)
    report.show(truncate = false)
    val suffix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    DataFrameWriterUtils.writeQualityReport(report.toDF(), outputPath, suffix)
    s"$outputPath/rapport_qualite_$suffix.csv"
  }

  /** Ecrit les 4 DataFrames de rejets (Q2.2) en CSV + Parquet. */
  def saveRejections(
      outputPath: String,
      tx: Dataset[Row], users: Dataset[Row],
      products: Dataset[Row], merchants: Dataset[Row]
  ): Unit = {
    DataFrameWriterUtils.writeCsvParquet(tx.toDF(), "rejets_transactions", outputPath)
    DataFrameWriterUtils.writeCsvParquet(users.toDF(), "rejets_users", outputPath)
    DataFrameWriterUtils.writeCsvParquet(products.toDF(), "rejets_products", outputPath)
    DataFrameWriterUtils.writeCsvParquet(merchants.toDF(), "rejets_merchants", outputPath)
  }
}