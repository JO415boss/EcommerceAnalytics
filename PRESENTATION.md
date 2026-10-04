# Soutenance — EcommerceAnalytics

Support de travail pour une présentation collective de 5 minutes, trois présentations individuelles de 5 minutes et 5 minutes de questions. Compléter les éléments entre crochets après exécution du pipeline; ne pas présenter de métriques non mesurées.

## Diapositive 1 — Contexte et objectif

- Projet : système distribué d’analyse de données e-commerce.
- Sources : environ 138 000 transactions, 12 000 utilisateurs, 6 000 produits et 600 marchands.
- Objectif : fiabiliser les sources, enrichir les transactions et produire des indicateurs métiers.

## Diapositive 2 — Architecture

1. Ingestion CSV, JSON et Parquet avec Spark SQL.
2. Validation des quatre jeux de données et conservation des rejets avec leur motif.
3. Jointure des dimensions et enrichissement temporel des transactions.
4. Calcul des KPI marchands et de la rétention des cohortes.
5. Écriture du rapport qualité, des rejets et des résultats en CSV et Parquet.

## Diapositive 3 — Organisation du groupe (Questions 0.1 à 0.3)

- Trois rôles complémentaires imposés par le sujet : A = ingestion/plateforme (Parties 1, 2, 7),
  B = transformations (Partie 3), C = analytique et performances (Parties 4, 5, 6).
- Découpage strict des fichiers : **chacun ne modifie que les siens** ; relectures croisées
  imposées (détail dans `EQUIPE.md` et `CONTRIBUTIONS.md`).
- Discipline Git : un fichier = un commit, message préfixé du nom, `git pull` avant /
  `git push` après (procédure complète dans le `README.md`, section « Travail sur GitHub »).
- Preuves : `git log --oneline --author="<nom>"` et le tableau des 14 fichiers du membre A.

## Diapositive 4 — Qualité des données

Présenter le rapport produit `output/rapport_qualite_yyyymmdd.csv` :

| Jeu de données | Lignes lues | Lignes valides | Lignes rejetées | Taux de rejet |
|---|---:|---:|---:|---:|
| Transactions | À mesurer | À mesurer | À mesurer | À mesurer |
| Utilisateurs | À mesurer | À mesurer | À mesurer | À mesurer |
| Produits | À mesurer | À mesurer | À mesurer | À mesurer |
| Marchands | À mesurer | À mesurer | À mesurer | À mesurer |

Expliquer les règles de validation et montrer un exemple de `rejection_reason`.

## Diapositive 5 — Résultats analytiques

- KPI marchands : chiffre d’affaires, volumes, clients distincts, commission et rangs.
- Cohortes : taille initiale, activité par mois, taux de rétention et meilleure cohorte au troisième mois.
- Ajouter ici un tableau ou un graphique construit à partir des sorties réellement générées.

## Diapositive 6 — Choix et performances Spark

- Versions : Scala 2.13.12 et Spark 3.5.1.
- Jointures dimensionnelles gauches pour conserver les transactions; broadcast du référentiel marchands configurable.
- Cache/persist configurable; transactions persistées en `MEMORY_AND_DISK_SER`.
- Durées observées : [compléter après exécution; ne pas annoncer de gain non mesuré].

## Diapositive 7 — Questions et limites

- Chaque membre doit pouvoir expliquer les choix et le fonctionnement des modules des autres membres.
- Documenter les limites réellement constatées durant l’exécution et les pistes d’amélioration.

---

## Exposé individuel — Membre A (5 minutes)

**1. Mon module** (Parties 1, 2 et 7) : structure SBT (`build.sbt`, `project/*`,
`run-sbt.cmd`), `application.conf`, les case classes, `ConfigLoader`,
`SparkSessionBuilder`, `DataFrameWriterUtils` (objet commun d'écriture), puis
`DataIngestion` (4 formats), `DataValidation` (4 jeux de règles) et `DataQualityReport`
(rapport `rapport_qualite_yyyymmdd.csv` + écriture des rejets).

**2. Ma difficulté principale** : la mise en route de GitHub (dépôts créés dans le
désordre, identité des commits non conforme, historiques non liés). Cause racine : aucune
procédure écrite **avant** de toucher au dépôt. Solution : procédure Git versionnée
(aujourd'hui la section « Travail sur GitHub » du `README.md`), identité configurée en
premier, et lanceur `run-sbt.cmd` qui règle `HADOOP_HOME` pour les postes Windows.

**3. Démonstration de code** : ouvrir `DataValidation.validateTransactions()` (règles Q2.2
avec `rejection_reason` et test explicite des `NULL`), puis
`DataFrameWriterUtils.writeQualityReport()` (un seul fichier CSV : `coalesce(1)`,
renommage du `part-*.csv`, nettoyage du temporaire).

**4. Ce que je sais expliquer même si ce n'est pas mon code** : UDF temporelle et
enrichissement de B ; KPI marchands, cohortes et optimisations de C ; nom exact des
sorties attendues (Q6.1) et argument d'étape `ingestion|transformation|analytics|all`
(bonus Q6.2).

## Exposé individuel — Membres B et C (à compléter par eux)

- **Membre B** : UDF `extractTimeFeatures`, `DataTransformation.scala` (jointures
  d'enrichissement), fonctions de fenêtrage (Q3.3) ; difficultés rencontrées et
  démonstration de code.
- **Membre C** : `Analytics.scala` (KPI, cohortes), `SparkOptimizations.scala`
  (`cache`/`broadcast`), `MainApp.scala` (orchestration, gestion d'erreurs, sortie) ;
  difficultés rencontrées, durées mesurées et démonstration de code.
