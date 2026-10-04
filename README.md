# EcommerceAnalytics

Pipeline d'analyse e-commerce avec **Apache Spark 3.5.1**, **Scala 2.13.12** et **SBT 1.9.9**.

## Démarrage rapide (membre B ou C)

```bash
git clone https://github.com/JO415boss/EcommerceAnalytics.git
cd EcommerceAnalytics

run-sbt.cmd compile     # compiler
run-sbt.cmd test        # tests
run-sbt.cmd assembly    # JAR final
```

Sous Windows, Spark a besoin des natives Hadoop (`hadoop.dll`) : `run-sbt.cmd`
configure `HADOOP_HOME` automatiquement si `C:\Users\<utilisateur>\hadoop` existe.
Sous Linux/macOS, rien de plus à installer.

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
- ⏳ Il reste à écrire le point d'entrée `MainApp` puis le code des questions
  Q1.1 → Q7.1.
- En l'état, `run-sbt.cmd compile` fonctionne ; `sbt run` et `sbt assembly` ne
  produiront un exécutable qu'une fois le point d'entrée ajouté.

## Règles du groupe

- Chacun ne modifie que **ses fichiers** (tableau dans `GITHUB.md`).
- Avant de commencer : `git pull origin main` — après : `git push origin main`.
- Messages de commit préfixés par le nom : `Nom : description`.
- Avant la remise : codes étudiants et relectures croisées complétés dans
  `EQUIPE.md` et `CONTRIBUTIONS.md`.
