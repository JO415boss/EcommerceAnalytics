# Contributions

## Tableau de répartition

Responsables et relecteurs imposés par le sujet (Questions 0.1 et 0.3) :

| Question | Membre responsable | Membre relecteur |
|---|---|---|
| Q1.1 à Q1.3 – structure SBT, build.sbt, README | Membre A | Membre C |
| Q2.1 à Q2.4 – ingestion, validation, rapport de qualité | Membre A | Membre B |
| Q3.1 à Q3.3 – UDF temporelle, enrichissement, fenêtres | Membre B | Membre A |
| Q4.1 et Q4.2 – KPI marchands, cohortes de rétention | Membre C | Membre B |
| Q5.1 et Q5.2 – optimisations Spark (cache, broadcast) | Membre C | Membres A et B |
| Q6.1 – application principale (`MainApp.scala`, intitulée « EcommerceAnalyticsApp » dans l'énoncé) | Membre C | Intégration validée par les 3 membres |
| Q7.1 – application.conf | Membre A | Membre C |
| Partie 0 (organisation) et Partie 8 (livrables, soutenance) | Collectif | Chaque membre relit le module d'un autre membre |

Questions bonus (facultatives, à ne traiter qu'après le tronc commun) :

- Q2.5 (intégrité référentielle) → Membre A
- Q3.4 (transactions suspectes) → Membre B
- Q4.3 (RFM), Q4.4 (produits/catégories), Q5.3 (gain des optimisations), Q6.2 (exécution modulaire) → Membre C

## Découpage des fichiers source (Question 1.1)

Chacun ne modifie que ses propres fichiers (règle anti-conflit du sujet) :

| Fichier / répertoire | Propriétaire |
|---|---|
| Structure SBT : `build.sbt`, `project/*`, `.gitignore`, `run-sbt.cmd` | Membre A |
| `src/main/scala/com/ecommerce/models/` (case classes) | Membre A |
| `src/main/scala/com/ecommerce/utils/` (`SparkSessionBuilder`, `ConfigLoader`, `DataFrameWriterUtils`) | Membre A – objet commun appelé par les 3 membres |
| `DataIngestion.scala`, `DataValidation.scala` | Membre A |
| `src/main/resources/application.conf`, `README.md` | Membre A |
| `DataTransformation.scala`, `TimeFeatures.scala` | Membre B |
| `Analytics.scala`, `SparkOptimizations.scala`, `MainApp.scala` | Membre C |
| `EQUIPE.md`, `CONTRIBUTIONS.md`, `README.md` | Collectif (rédaction initiale : membre A) |

## Charge de travail

> La Question 0.3 demande une estimation honnête de la charge **en heures** et la
> liste des difficultés rencontrées, pour chaque membre : à renseigner par chacun
> au fil de son travail.

### Membre A – Joseph N SADIO (depuis le 03/10/2026) – Data Ingestion & Platform Engineer

- Rôle (Question 0.1) : Parties 1, 2 et 7 – structure SBT, `build.sbt`, case
  classes, `DataIngestion.scala`, validations, rapport de qualité des données,
  `application.conf`, `README.md`.
- Questions : Q1.1 à Q1.3, Q2.1 à Q2.4, Q7.1 ; bonus Q2.5. Relecteur de la Partie 3.
- Mise en place GitHub : dépôt distant, `.gitignore`, identité Git, poussée
  initiale des documents de suivi (détail dans la section « Travail sur GitHub »
  du `README.md`).
- Documentation : `README.md` (Q1.3), `EQUIPE.md`, `CONTRIBUTIONS.md` –
  `README.md` réécrit en version simple le 03/10/2026
  (commit `52a3ba5`) pour permettre au membre B de démarrer seul, puis enrichi
  le 04/10/2026 de tout le guide Git (l'ancien `GITHUB.md` y a été fusionné pour
  supprimer un doublon documentaire, Q8.1). La Partie 8 reste un livrable
  collectif.
- Construction du socle du projet (fichiers 1 à 5 du tableau ci-dessous),
  chacun validé avant d'être poussé.
- Outillage de qualité (04/10/2026) : `tools/check-markdown-tables.ps1` vérifie la
  cohérence des tableaux Markdown du dépôt (code de sortie `1` si une ligne est
  tronquée) et `tools/install-git-hooks.ps1` installe le hook `pre-commit` qui
  l'exécute avant chaque commit (voir `README.md`, section « Travail sur GitHub », §8).
- Heures travaillées : vendredi 19h → dimanche 2h24 (amplitude ≈ 31 h ;
  travail effectif estimé ≈ 8 h : sessions visibles dans `git log` du 03/10
  16h24 au 04/10 02h07 UTC). Les commits et le push GitHub n'ont fonctionné
  normalement qu'à partir de samedi.
- Suite du travail : 05/10/2026 (harmonisation des documents de suivi, commits
  `77d83e6` et `b924ccf`) puis 06/10/2026 (`git pull` de synchronisation,
  relecture de la Partie 3, mise à jour de l'état du README) ; heures à
  additionner ci-dessus avant la remise (Question 0.3).
- Difficultés rencontrées : configuration GitHub (plus gros problème) —
  identité des commits (`JO415boss` vs `Joseph N SADIO`, config globale vs
  locale), premier push et synchronisation de `main` ; résolu le samedi.

**Prêt pour le membre B** : le clone, `run-sbt.cmd compile` et toutes les
commandes Git décrites dans le `README.md` ont été testés sur le
poste du membre A ; il ne manque que le point d'entrée `MainApp` (fichier 6,
Membre C) puis l'écriture des questions par chacun.

#### Fichiers créés par le membre A (cadence : un seul fichier à la fois, 1 fichier = 1 commit)

| # | Fichier | Rôle (par ordre d'urgence) | Date | Commit |
|---|---|---|---|---|
| 1 | `project/build.properties` | épingle sbt 1.9.9 pour tous les membres | 03/10/2026 | `d301bba` |
| 2 | `project/plugins.sbt` | plugin sbt-assembly (livraison d'un JAR unique) | 03/10/2026 | `2948b1c` |
| 3 | `build.sbt` | socle : Scala 2.13.12, Spark 3.5.1, Typesafe Config, exclusions assembly | 03/10/2026 | `51c2779` |
| 4 | `run-sbt.cmd` | lance sbt avec `HADOOP_HOME` (natives Windows) | 03/10/2026 | `f911d76` |
| 5 | `src/main/resources/application.conf` | chemins d'entrée `data/...` et sortie `output/` (Q7.1) | 03/10/2026 | `76c4520` |
| 6 | `src/main/resources/application.conf` (mise à jour) | config complète Q7.1 : spark, optimization, validation | 04/10/2026 | `31ed40f` |
| 7 | `models/DataModels.scala` | case classes Transaction/User/Product/Merchant + QualityReportRow (Q2.1, Q2.4) | 04/10/2026 | `664703f` |
| 8 | `utils/ConfigLoader.scala` | chargement application.conf + valeurs par defaut (Q7.1) | 04/10/2026 | `4acb184` |
| 9 | `utils/SparkSessionBuilder.scala` | SparkSession depuis application.conf (Q6.1/Q7.1) | 04/10/2026 | `80264e2` |
| 10 | `utils/DataFrameWriterUtils.scala` | double écriture CSV+Parquet, rapport qualité fichier unique (Q1.1/Q2.4) | 04/10/2026 | `d240c79` |
| 11 | `analytics/DataIngestion.scala` | lectures CSV/JSON/Parquet typées Dataset[T] + try-catch (Q2.1/Q2.3) | 04/10/2026 | `80b08fd` |
| 12 | `analytics/DataValidation.scala` | règles Q2.2, (valides, rejetés + rejection_reason) (Q2.2/Q2.3) | 04/10/2026 | `972bb25` |
| 13 | `analytics/DataQualityReport.scala` | rapport qualité Q2.4 + écriture des 4 rejets (Q2.2/Q2.4) | 04/10/2026 | `6261546` |
| 14 | `README.md` | sections Q1.3 (prérequis, compilation, exécution, spark-submit) + état | 04/10/2026 | `edb205e` |

**Les 14 fichiers du tableau ci-dessus sont tous poussés sur `main`** : la Partie 2 est
terminée (Q2.1 à Q2.4), seul le bonus Q2.5 (intégrité référentielle : comptage des
`user_id` / `product_id` / `merchant_id` orphelins) reste optionnel. Ajouté le 04/10/2026 :
tout le guide Git de l'équipe est désormais dans la section « Travail sur GitHub » du
`README.md` (fusion de l'ancien `GITHUB.md`).

Validation du fichier 5 : les 4 chemins d'entrée référencés
(`data/transactions.csv`, `data/users.json`, `data/products.parquet`,
`data/merchants.csv`) ont été vérifiés existants sur le poste.

Validation : le fichier 3 a été corrigé (`f.data.getName` au lieu de `f.getName`,
type `Attributed[File]` de sbt-assembly, commit `cc07aeb`) puis **`sbt compile`
est passé avec succès le 03/10/2026** puis à nouveau le 04/10/2026 (`.\run-sbt.cmd compile` → `[success]` en 46 s) avec les 7 fichiers Scala du membre A (models, 3 utils, DataIngestion, DataValidation) – `build.sbt`, `project/plugins.sbt` et
`project/build.properties` sont donc validés ensemble. Le fichier 4 a lui aussi
été **validé par une exécution réelle** (`.\run-sbt.cmd compile` -> `[success]`
le 03/10/2026) ; à noter : ce launcher sbt accepte ni `-batch` ni `-no-colors`,
il faut invoquer directement `sbt compile` (ou `run-sbt.cmd compile`).
### Membre B – SECK Mamour – Data Transformation Engineer

- Rôle (Question 0.1) : Partie 3 – UDF `extractTimeFeatures` (Q3.1), fonction
  `enrichTransactionData` : jointures et enrichissement (Q3.2), fenêtres
  glissantes et détection de comportements (Q3.3) ; bonus Q3.4 (transactions
  suspectes).
- Fichiers propriétaires : `TimeFeatures.scala`, `DataTransformation.scala`.
- Relecteur de la Partie 2 (Q2.1 à Q2.4).
- Compte GitHub : `mamourseck179-maker` – invitation en collaborateur (accès
  écriture) envoyée le 03/10/2026 ; une fois acceptée :
  `git clone https://github.com/JO415boss/EcommerceAnalytics.git` puis
  `git pull origin main` avant de commencer.
- Heures travaillées : à compléter (obligatoire avant la remise).
- Difficultés rencontrées : à compléter.

### Membre C – SYLVA Frederic – Analytics & Performance Engineer

- Rôle (Question 0.1) : Parties 4, 5 et 6 – KPI marchands (Q4.1), cohortes de
  rétention (Q4.2), optimisations Spark (Q5.1, Q5.2), application principale
  `MainApp.scala` (Q6.1) ; bonus Q4.3 (RFM), Q4.4 (produits/catégories),
  Q5.3 (mesure du gain), Q6.2 (exécution modulaire).
- Fichiers propriétaires : `Analytics.scala`, `SparkOptimizations.scala`,
  `MainApp.scala`.
- Relecteur des Parties 1 et 7 (membre A).
- Compte GitHub : `sylvafrederic00-lang` – invitation en collaborateur (accès
  écriture) envoyée le 03/10/2026 ; une fois acceptée :
  `git clone https://github.com/JO415boss/EcommerceAnalytics.git` puis
  `git pull origin main` avant de commencer.
- Heures travaillées : à compléter (obligatoire avant la remise).
- Difficultés rencontrées : à compléter.

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

| Partie | Auteur (responsable) | Relecteur prévu | Date | Remarques |
|---|---|---|---|---|
| Partie 1 | Membre A | Membre C | à faire | |
| Partie 2 | Membre A | Membre B |07/10/2026 | Revu : Q2.1 - schéma explicite (transactions), inféré (merchants), Parquet (products), try-catch avec affichage des lignes lues, chemins lus dans application.conf avec valeurs par défaut ; Q2.2 - règles conformes au sujet, rejection_reason avec plusieurs raisons séparées par concat_ws, preferred_categories converti en chaîne pour le CSV des rejets, seuils externalisés ; Q2.4 - countNulls toutes colonnes confondues. Remarque bloquante : sur les vraies données, readUsers échoue (Cannot up cast age from BIGINT to INT), le dataset users est vide ; correction proposée : cast("int") sur age avant .as[User]. Remarque mineure : une valeur nulle de amount est étiquetée "amount <= 0". À vérifier : arrondi de taux_rejet et fichier CSV unique. |
| Partie 3 | Membre B | Membre A | 06/10/2026 | Revu le 06/10/2026 (TimeFeatures.scala, DataTransformation.scala) : Q3.1 — fonction extract pure, null et longueur != 14 gérés, Locale.ENGLISH figé (résultats déterministes) ; Q3.2 — colonnes homonymes renommées avant les jointures left, struct time_features éclatée ; Q3.3 — fenêtre range -7 jours sur timestamp casté en long, collect_set + size à la place de countDistinct (interdit en fenêtre), lag et datediff. sbt compile vert le 06/10/2026 sur les 9 fichiers Scala. À confirmer lors de l'intégration : frontière age_group à 25 ans (classée Adulte, documentée dans le code) et product.category non retenue dans les colonnes finales. |
| Partie 4 | Membre C | Membre B | à faire | |
| Partie 5 | Membre C | Membres A et B | à faire | |
| Partie 6 | Membre C | Les 3 membres (intégration) | à faire | |
| Partie 7 | Membre A | Membre C | à faire | |