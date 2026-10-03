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

## Diapositive 3 — Qualité des données

Présenter le rapport produit `output/rapport_qualite_yyyymmdd.csv` :

| Jeu de données | Lignes lues | Lignes valides | Lignes rejetées | Taux de rejet |
|---|---:|---:|---:|---:|
| Transactions | À mesurer | À mesurer | À mesurer | À mesurer |
| Utilisateurs | À mesurer | À mesurer | À mesurer | À mesurer |
| Produits | À mesurer | À mesurer | À mesurer | À mesurer |
| Marchands | À mesurer | À mesurer | À mesurer | À mesurer |

Expliquer les règles de validation et montrer un exemple de `rejection_reason`.

## Diapositive 4 — Résultats analytiques

- KPI marchands : chiffre d’affaires, volumes, clients distincts, commission et rangs.
- Cohortes : taille initiale, activité par mois, taux de rétention et meilleure cohorte au troisième mois.
- Ajouter ici un tableau ou un graphique construit à partir des sorties réellement générées.

## Diapositive 5 — Choix et performances Spark

- Versions : Scala 2.13.12 et Spark 3.5.1.
- Jointures dimensionnelles gauches pour conserver les transactions; broadcast du référentiel marchands configurable.
- Cache/persist configurable; transactions persistées en `MEMORY_AND_DISK_SER`.
- Durées observées : [compléter après exécution; ne pas annoncer de gain non mesuré].

## Diapositive 9 — Questions et limites

- Chaque membre doit pouvoir expliquer les choix et le fonctionnement des modules des autres membres.
- Documenter les limites réellement constatées durant l’exécution et les pistes d’amélioration.