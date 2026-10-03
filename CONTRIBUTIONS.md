# Contributions

## Tableau de répartition

| Question | Membre responsable | Membre relecteur |
|---|---|---|
| Q1.1 à Q1.3 | à attribuer | à attribuer |
| Q2.1 à Q2.4 | à attribuer | à attribuer |
| Q3.1 à Q3.3 | à attribuer | à attribuer |
| Q4.1 à Q4.2 | à attribuer | à attribuer |
| Q5.1 à Q5.2 | à attribuer | à attribuer |
| Q6.1 | à attribuer | à attribuer |
| Q7.1 | à attribuer | à attribuer |
| Partie 8 | à attribuer | à attribuer |

## Charge de travail

### Membre A – Joseph N SADIO (depuis le 03/10/2026)

- Mise en place GitHub : dépôt distant, `.gitignore`, identité Git, poussée
  initiale des documents de suivi (détail dans `GITHUB.md`).
- Partie 8 (documentation) : `README.md`, `EQUIPE.md`, `CONTRIBUTIONS.md`,
  `GITHUB.md`, `PRESENTATION.md`.
- Questions techniques : à attribuer lors de la répartition.

#### Fichiers créés par le membre A (cadence : un seul fichier à la fois, 1 fichier = 1 commit)

| # | Fichier | Rôle (par ordre d'urgence) | Date | Commit |
|---|---|---|---|---|
| 1 | `project/build.properties` | épingle sbt 1.9.9 pour tous les membres | 03/10/2026 | `d301bba` |

À venir dans cet ordre, un fichier à la fois : `project/plugins.sbt` (plugin
sbt-assembly), `build.sbt`, `run-sbt.cmd`, `src/main/resources/application.conf`
(Q7.1), point d'entrée `MainApp`. Chaque fichier sera ajouté au tableau avec son
commit dès qu'il sera poussé.

### Membres B et C

- Comptes GitHub : SECK Mamour = `mamourseck179-maker`, SYLVA Frederic = `sylvafrederic00-lang`.
- Invitation en collaborateur (accès écriture) envoyée le 03/10/2026 ;
  à accepter depuis la boîte de réception GitHub ou la page des invitations du dépôt.
- Une fois l'invitation acceptée : `git clone https://github.com/JO415boss/EcommerceAnalytics.git`
  puis `git pull origin main` avant de commencer, et déclarer leur charge ici.

## Décisions techniques du groupe

- Version Scala : 2.13.12
- Version Spark : 3.5.1
- Stratégie de jointure : jointures dimensionnelles gauches (`left`) pour conserver
  chaque transaction valide même si une dimension est absente (Q3.2) ; `broadcast`
  configurable sur le référentiel marchands (petite table, Q5.2). Transactions
  enrichies : `left` vers users/products/marchands ; KPI marchands : `left` depuis
  marchands vers les métriques pour garder les marchands sans vente.
- Format de sortie : CSV + Parquet via l'objet commun `DataFrameWriterUtils`
  (Q1.1) ; coalesce(1) pour les petits résultats ; rapport qualité en un seul
  fichier CSV `output/rapport_qualite_yyyymmdd.csv` (Q2.4).
- Gestion de la configuration : Typesafe Config (`application.conf`, Q7.1) ; chemins
  `data/...` relatifs à la racine du projet SBT et `output/` à la racine également.
- JAR : `sbt assembly` (plugin sbt-assembly, justifié par la livraison d'un JAR unique)
  avec `assembly / assemblyExcludedJars` filtrant `spark-*`, `hadoop-*` et `scala-library`.
  Le JAR livré est donc léger et se soumet avec `spark-submit`, qui fournit Spark sur
  le cluster : comportement équivalent à un scope `provided`, sans en subir le défaut
  (un scope `provided` réel casse `sbt run` et `sbt test` en local, Spark n'étant plus
  sur le classpath d'exécution). Les dépendances restent en `Compile` dans `build.sbt`
  uniquement pour que l'exécution et les tests locaux fonctionnent.

## Relectures croisées

Chaque relecture doit être inscrite ici après avoir été effectuée : date, relecteur et remarques.
