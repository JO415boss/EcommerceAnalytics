# EcommerceAnalytics

Pipeline d'analyse e-commerce avec **Apache Spark 3.5.1**, **Scala 2.13.12** et **SBT 1.9.9**.

Il lit quatre jeux de données (transactions, users, products, merchants), les valide,
les enrichit, puis calcule les KPI métiers : KPI marchands, cohortes de rétention,
segmentation RFM, top produits et ventes par catégorie. Tout est piloté par
`application.conf` et orchestré par `MainApp`.

## Prérequis (Q1.3)

- **Java 17**, **SBT 1.9.9** (épinglé dans `project/build.properties`), **Scala 2.13.12**
  et **Spark 3.5.1** (dépendances gérées dans `build.sbt`).
- Sous Windows : natives Hadoop (`hadoop.dll` dans `%USERPROFILE%\hadoop\bin` ;
  `HADOOP_HOME` est configuré automatiquement par `run-sbt.cmd`).
- Données dans `data/` (ignorées par Git) : `transactions.csv`, `users.json`,
  `products.parquet`, `merchants.csv` — chemins lus dans `application.conf`.

## Compiler et exécuter (Q1.3, Q6.1)

```bash
run-sbt.cmd compile     # compiler
run-sbt.cmd test        # lancer les tests
run-sbt.cmd assembly    # générer le JAR exécutable (target/scala-2.13/*.jar)
run-sbt.cmd "run"       # pipeline complet, en mode local[*]
```

Exécution modulaire (bonus Q6.2, membre C) — le premier argument choisit l'étape :

```bash
run-sbt.cmd "run ingestion"        # lecture + validation + rapport qualité + rejets
run-sbt.cmd "run transformation"   # transactions enrichies
run-sbt.cmd "run analytics"        # KPI et optimisations (Q4, Q5)
```

La `SparkSession`, les chemins et les seuils viennent tous de `application.conf`
(`app.*`) via `utils/ConfigLoader` et `utils/SparkSessionBuilder` : aucune valeur
n'est codée en dur (Q7.1).

## Déploiement sur cluster (Q1.3)

```bash
# Le JAR est allégé (Spark/Hadoop exclus, fournis par le cluster) :
spark-submit --class com.ecommerce.analytics.MainApp --master yarn \
  target/scala-2.13/ecommerceanalytics_2.13-1.0.0.jar
```

## Données et sorties

- Entrées : `data/` (voir Prérequis) ; sorties : `output/`.
- Convention du sujet (Q6.1) : chaque résultat est écrit **deux fois** —
  `output/csv/<nom>/` (en-tête, `coalesce(1)` pour les petits résultats) et
  `output/parquet/<nom>/`, en mode `overwrite`. Le rapport de qualité est un
  **fichier CSV unique** `output/rapport_qualite_yyyymmdd.csv`. Toutes les
  écritures passent par `utils/DataFrameWriterUtils`.
- Résultats produits : `transactions_enrichies`, `kpi_marchands`,
  `cohortes_retention`, `rfm_utilisateurs`, `top_produits`, `ventes_categories`,
  `gain_optimisations`, plus le rapport de qualité et les rejets.
- Pipeline exécuté et validé le 09/10/2026 sur les vraies données
  (138 047 transactions lues, 136 157 valides).

## Organisation du groupe (Q0.1, Q1.1)

Dépôt : `https://github.com/JO415boss/EcommerceAnalytics.git`, branche `main`.

| Membre | Rôle | Fichiers qu'il pousse |
|---|---|---|
| A — Joseph N SADIO | Data Ingestion & Platform (Parties 1, 2, 7) | `build.sbt`, `project/`, `models/`, `utils/`, `DataIngestion`, `DataValidation`, `DataQualityReport`, `application.conf` |
| B — SECK Mamour | Data Transformation (Partie 3) | `TimeFeatures.scala`, `DataTransformation.scala` |
| C — SYLVA Frederic | Analytics & Performance (Parties 4, 5, 6) | `Analytics.scala`, `SparkOptimizations.scala`, `MainApp.scala` |
| Collectif | Partie 8 (livrables, soutenance) | `EQUIPE.md`, `CONTRIBUTIONS.md`, `README.md` |

## Règles du groupe

- Chacun ne modifie que **ses fichiers** (tableau ci-dessus) : c'est ce qui évite
  les conflits annoncés par la Q1.1.
- Avant de commencer : `git pull origin main` — une fois le travail committé :
  `git push origin main`.
- Messages de commit préfixés par le nom : `Nom : description`.
- Relectures croisées obligatoires, inscrites dans `CONTRIBUTIONS.md`.
- Ne jamais pousser `target/`, `data/`, `output/` (exclus par `.gitignore`).
- Avant la remise : codes étudiants et relectures croisées complétés dans
  `EQUIPE.md` et `CONTRIBUTIONS.md`.

## Où lire quoi

| Fichier | Rôle |
|---|---|
| `EQUIPE.md` | membres, rôles et codes étudiants |
| `CONTRIBUTIONS.md` | répartition des questions, heures travaillées, relectures croisées, décisions techniques |
