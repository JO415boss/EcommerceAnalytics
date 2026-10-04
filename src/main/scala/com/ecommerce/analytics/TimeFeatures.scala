package com.ecommerce.analytics

import java.time.LocalDateTime
import java.time.format.{DateTimeFormatter, TextStyle}
import java.util.Locale

import scala.util.Try

import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf

/** Résultat de l'UDF : chaque champ devient une colonne après select("time_features.*") */
case class TimeFeaturesResult(
  hour: Option[Int],
  day_of_week: Option[String],
  month: Option[String],
  is_weekend: Option[Int],
  day_period: Option[String],
  is_working_hours: Option[Int]
)

object TimeFeatures {

  private val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

  private val emptyResult =
    TimeFeaturesResult(None, None, None, None, None, None)

  /** Fonction pure, testable sans Spark. Ne lève jamais d'exception. */
  def extract(ts: String): TimeFeaturesResult = {
    if (ts == null || ts.trim.length != 14) emptyResult
    else {
      Try(LocalDateTime.parse(ts.trim, formatter)).toOption match {
        case None => emptyResult
        case Some(dt) =>
          val h = dt.getHour
          val dow = dt.getDayOfWeek
          val dayName = dow.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
          val monthName = dt.getMonth.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
          val weekend = if (dow.getValue >= 6) 1 else 0
          val period =
            if (h >= 6 && h < 12) "Morning"
            else if (h >= 12 && h < 18) "Afternoon"
            else if (h >= 18 && h < 22) "Evening"
            else "Night"
          val working = if (h >= 9 && h <= 17) 1 else 0
          TimeFeaturesResult(Some(h), Some(dayName), Some(monthName),
            Some(weekend), Some(period), Some(working))
      }
    }
  }

  /** UDF Spark : withColumn("time_features", TimeFeatures.extractTimeFeatures(col("timestamp"))) */
  val extractTimeFeatures: UserDefinedFunction = udf((ts: String) => extract(ts))
}