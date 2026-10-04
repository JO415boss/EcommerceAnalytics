# EcommerceAnalytics

Pipeline d'analyse e-commerce avec **Apache Spark 3.5.1**, **Scala 2.13.12** et **SBT 1.9.9**.

## Prérequis (Q1.3)

- **Java 17** (testé avec Eclipse Adoptium 17.0.20), **SBT 1.9.9** (épinglé dans
  `project/build.properties`), **Scala 2.13.12** et **Spark 3.5.1** (dépendances
  gérées dans `build.sbt`).
- Sous Windows : natives Hadoop (`hadoop.dll` dans `%USERPROFILE%\hadoop\bin`,
  variable `HADOOP_HOME` configurée automatiquement par `run-sbt.cmd`).
- Sous Linux/macOS : Java + SBT suffisent.
- Données : `data/transactions.csv`, `data/users.json`,
  `data/products.parquet`, `data/merchants.csv` (chemins dans
  `src/main/resources/application.conf`) ; ignorées par Git.

## Compilation et JAR (Q1.3)

```bash
git clone https://github.com/JO415boss/EcommerceAnalytics.git
cd EcommerceAnalytics

run-sbt.cmd compile     # compiler (validé le 04/10/2026)
run-sbt.cmd test        # lancer les tests
run-sbt.cmd assembly    # générer le JAR exécutable (target/scala-2.13/*.jar)
```

## Exécution locale (Q1.3)

```bash
run-sbt.cmd "run"   # lance com.ecommerce.analytics.MainApp en mode local[*]
```

La `SparkSession` est construite depuis `application.conf` (`app.spark.*`)
par `utils/SparkSessionBuilder` : aucune valeur n'est codée en dur.

## Déploiement sur cluster (Q1.3)

```bash
# Le JAR est allégé (Spark/Hadoop exclus, fournis par le cluster) :
spark-submit --class com.ecommerce.analytics.MainApp --master yarn \
  target/scala-2.13/ecommerceanalytics_2.13-1.0.0.jar
```

## Données

Quatre entrées dans `data/` : `transactions.csv`, `users.json`,
`products.parquet` (répertoire), `merchants.csv`.

- Chemins : `src/main/resources/application.conf` (`app.data.input.*`, `app.data.output`).
- Sorties : `output/` ; `data/`, `output/` et `target/` sont ignorés par Git.

## Où lire quoi

| Fichier | Rôle |
|---|---|
| `EQUIPE.md` | membres, rôles (Parties 1-7) et comptes GitHub |
| `CONTRIBUTIONS.md` | répartition des questions, charge de travail, décisions techniques |
| `GITHUB.md` | commandes Git du groupe |
| `PRESENTATION.md` | support de 

## État actuel

- ✅ Dépôt GitHub, build SBT et configuration créés et validés par le membre A
  (fichiers et commits détaillés dans `CONTRIBUTIONS.md`).
- ✅ Partie 1 (Q1.1 à Q1.3), Partie 2 (Q2.1 à Q2.4) et Partie 7 (Q7.1) terminées
  par le membre A : 8 fichiers Scala compilés avec succès.
- ⏳ Reste au membre B la Partie 3 (Q3.1 à Q3.3) et au membre C les Parties 4 à 6.
- En l'état, `run-sbt.cmd compile` fonctionne ; `sbt run` et `sbt assembly` ne
  produiront un exécutable qu'une fois le point d'entrée ajouté.

## Règles du groupe

- Chacun ne modifie que **ses fichiers** (tableau dans `GITHUB.md`).
- Avant de commencer : `git pull origin main` — après : `git push origin main`.
- Messages de commit préfixés par le nom : `Nom : description`.
- Avant la remise : codes étudiants et relectures croisées complétés dans
  `EQUIPE.md` et `CONTRIBUTIONS.md`.
