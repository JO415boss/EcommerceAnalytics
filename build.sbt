// Versions choisies par le groupe : voir CONTRIBUTIONS.md
// (Scala 2.13.12, Spark 3.5.1, JAR construit avec sbt-assembly).

ThisBuild / organization := "com.ecommerce"
ThisBuild / version      := "1.0.0"
ThisBuild / scalaVersion := "2.13.12"

val sparkVersion = "3.5.1"

lazy val root = (project in file("."))
  .settings(
    name := "EcommerceAnalytics",

    // Spark reste en Compile pour que "sbt run" et "sbt test" marchent en local.
    // Il est retiré du JAR final : sur le cluster, Spark est déjà installé.
    // Détails : CONTRIBUTIONS.md.
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-sql" % sparkVersion,
      "com.typesafe"     %  "config"    % "1.4.3",
      "org.scalatest"    %% "scalatest" % "3.2.18" % Test
    ),

    Compile / run / fork := true,

    // Java17 bloque certains accès internes de Spark : on les autorise ici,
    // sinon la SparkSession plante au démarrage (valable aussi pour runMain).
    Compile / run / javaOptions ++= Seq(
      "--add-opens=java.base/java.lang=ALL-UNNAMED",
      "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED",
      "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
      "--add-opens=java.base/java.io=ALL-UNNAMED",
      "--add-opens=java.base/java.net=ALL-UNNAMED",
      "--add-opens=java.base/java.nio=ALL-UNNAMED",
      "--add-opens=java.base/java.util=ALL-UNNAMED",
      "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED",
      "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED",
      "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
      "--add-opens=java.base/sun.nio.cs=ALL-UNNAMED",
      "--add-opens=java.base/sun.security.action=ALL-UNNAMED",
      "--add-opens=java.base/sun.util.calendar=ALL-UNNAMED",
      "--add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED"
    ),

    assembly / mainClass := Some("com.ecommerce.analytics.MainApp"),
    assembly / assemblyExcludedJars := {
      val cp = (assembly / fullClasspath).value
      cp.filter { f =>
        val n = f.data.getName
        n.startsWith("spark-") || n.startsWith("hadoop-") || n.startsWith("scala-library")
      }
    },
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _ @ _*) => MergeStrategy.discard
      case _                            => MergeStrategy.first
    }
  )
