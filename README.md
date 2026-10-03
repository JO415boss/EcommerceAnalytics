# EcommerceAnalytics

Pipeline d'analyse e-commerce distribué avec Apache Spark 3.5.1, Scala 2.13.12 et SBT.

## Prérequis

- JDK 11 ou 17
- SBT 1.9.9
- Spark 3.5.1 pour `spark-submit` sur un cluster

Sous Windows uniquement, Spark a besoin des natives Hadoop (`hadoop.dll`) pour lister les répertoires locaux (`data/products.parquet`, `output/...`). Copiez `hadoop.dll` et `winutils.exe` d'Hadoop 3.3.x dans `%HADOOP_HOME%\bin` (par exemple `C:\Users\<user>\hadoop\bin`) et définissez `HADOOP_HOME` ; `run-sbt.cmd` le fait automatiquement. Sous Linux/macOS aucune configuration n'est requise.

Les dépendances Spark sont présentes en `Compile` (utile pour `sbt run` et `sbt test` en local) mais **exclues du JAR final** par `assembly / assemblyExcludedJars` (`spark-*`, `hadoop-*`, `scala-library`). Le JAR reste donc léger et Spark doit être disponible sur le cluster pour le `spark-submit`. Ce choix est justifié dans `CONTRIBUTIONS.md`.

## Arborescence

```text
EcommerceAnalytics/
├── build.sbt, run-sbt.cmd
├── README.md, EQUIPE.md, CONTRIBUTIONS.md, PRESENTATION.md, GITHUB.md
├── docs/                        # sujet du projet (PDF + Markdown) et texte extrait
├── scripts/                     # utilitaires Python (profilage, extraction PDF)
├── data/                        # entrées du projet (cf. application.conf) + data_groupe3.zip
├── logs/                        # journaux sbt (écrits par run-sbt.cmd), ignorés par Git
├── output/                      # sorties du pipeline (CSV + Parquet), ignoré par Git
└── src/
    ├── main/scala/com/ecommerce/   # dossiers analytics/, models/, utils/ (vides : sources retirées)
    └── main/resources/
        ├── application.conf
        └── data/                # copie des 4 entrées (arborescence de la Q1.1)
```

## Données et configuration

Les quatre entrées sont fournies à deux emplacements : `data/` à la racine (celui réellement lu, `app.data.input.* = "data/..."`) et `src/main/resources/data/` (arborescence imposée par la Q1.1). Il s'agit de transactions CSV, utilisateurs JSON en lignes, produits Parquet (répertoire de 12 fichiers) et marchands CSV. Les chemins sont configurés dans `src/main/resources/application.conf` sous `app.data.input`; `app.data.output.path` définit la destination des résultats.

Lancer les commandes depuis le dossier `EcommerceAnalytics`. Pour une exécution sur cluster, adaptez les chemins d’entrée et de sortie vers des emplacements accessibles au cluster, par exemple HDFS, dans la configuration Typesafe Config.

## Compiler et tester

```bash
sbt clean compile
sbt test
```

Aucun test résiduel : `src/test` a été vidé en même temps que les sources.

## Exécuter en local

Tous les fichiers scala (dont la classe principale `com.ecommerce.analytics.MainApp`) ayant été retirés du projet, il n'y a plus de point d'entrée : `sbt run` et le `spark-submit` ne sont pas utilisables en l'état.

## Construire le JAR

```bash
sbt assembly
```

Sans classe principale, le JAR généré n'est pas directement submissible : il faut d'abord réintroduire une application d'entrée.

## Résultats

Les fichiers sous `output/csv/` et `output/parquet/` proviennent d'exécutions antérieures du pipeline (avant le retrait des sources) ; ils ne sont plus reproductibles en l'état.

## Soutenance et remise

`PRESENTATION.md` contient le support de travail pour la soutenance. Complétez-le avec les métriques réellement obtenues après exécution. Avant la remise, complétez les noms, prénoms, codes étudiants, estimations d’effort et relectures dans `EQUIPE.md` et `CONTRIBUTIONS.md`, puis incluez le JAR généré, un échantillon de `output/` et le support final dans l’archive ZIP.
