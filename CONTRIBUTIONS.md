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

- Q2.5 (intégrité référentielle) → Membre A (réalisé le 09/10/2026)
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

**Rôle (Question 0.1)** : Parties 1, 2 et 7 — structure SBT, `build.sbt`, case
classes, `DataIngestion.scala`, `DataValidation.scala`, `DataQualityReport.scala`,
`application.conf`, `README.md`.

**Questions traitées** : Q1.1 à Q1.3, Q2.1 à Q2.4, Q7.1 ; bonus Q2.5 (09/10/2026).
Relecteur des Parties 3 et 5.

**Heures travaillées (Question 0.3, estimation honnête d'après `git log`)** :
03–04/10 ≈ 8 h (socle SBT + Partie 2) ; 05/10 ≈ 1 h ; 06/10 ≈ 1 h ;
09/10 ≈ 3 h (correctif `users.json`, bonus Q2.5, `--add-opens`, exécution
complète du pipeline sur les vraies données, relecture de la Partie 5).
**Total ≈ 13 h.**

**Difficultés rencontrées** :
1. GitHub — identité des commits (`JO415boss` vs `Joseph N SADIO`, config globale
   vs locale) et premier push ; résolu le 04/10/2026.
2. Spark qui plantait au démarrage sous Java 17 → options `--add-opens` ajoutées
   à `build.sbt` (commit `16b6297`).
3. Champ `age` de `users.json` lu en BIGINT alors que la case class attend un
   `Int` (remonté par le membre B lors de la relecture de la Partie 2) →
   `cast("int")` ajouté dans `DataIngestion` (commit `07a3e96`).

**Livraison** : 14 fichiers poussés sur `main`, un fichier = un commit
(tableau ci-dessous). Mise en place du dépôt distant, `.gitignore`, identité Git
et poussée initiale des documents de suivi ; le guide Git de l'équipe est dans
la section « Travail sur GitHub » du `README.md`.

**État au 09/10/2026** : `sbt compile` vert, puis exécution complète du pipeline
(`run` + `test`, 47 min) sur les vraies données — 138 047 transactions lues,
136 157 valides, rapport qualité à 9 colonnes (dont les 3 du bonus Q2.5) et
11 résultats écrits en CSV + Parquet dans `output/`.

#### Fichiers créés par le membre A (cadence : un seul fichier à la fois, 1 fichier = 1 commit)

| # | Fichier | Rôle (par ordre d'urgence) | Date | Commit |
|---|---|---|---|---|
| 1 | `project/build.properties` | épingle sbt 1.9.9 pour tous les membres | 03/10/2026 | `d301bba` |
| 2 | `project/plugins.sbt` | plugin sbt-assembly (livraison d'un JAR unique) | 03/10/2026 | `2948b1c` |
| 3 | `build.sbt` | socle : Scala 2.13.12, Spark 3.5.1, Typesafe Config, exclusions assembly ; options Java 17 (`--add-opens`) ajoutées le 09/10/2026 | 03/10/2026 | `51c2779` |
| 4 | `run-sbt.cmd` | lance sbt avec `HADOOP_HOME` (natives Windows) | 03/10/2026 | `f911d76` |
| 5 | `src/main/resources/application.conf` | chemins d'entrée `data/...` et sortie `output/` (Q7.1) | 03/10/2026 | `76c4520` |
| 6 | `src/main/resources/application.conf` (mise à jour) | config complète Q7.1 : spark, optimization, validation | 04/10/2026 | `31ed40f` |
| 7 | `models/DataModels.scala` | case classes Transaction/User/Product/Merchant + QualityReportRow (Q2.1, Q2.4, bonus Q2.5) | 04/10/2026 | `664703f` |
| 8 | `utils/ConfigLoader.scala` | chargement application.conf + valeurs par defaut (Q7.1) | 04/10/2026 | `4acb184` |
| 9 | `utils/SparkSessionBuilder.scala` | SparkSession depuis application.conf (Q6.1/Q7.1) | 04/10/2026 | `80264e2` |
| 10 | `utils/DataFrameWriterUtils.scala` | double écriture CSV+Parquet, rapport qualité fichier unique (Q1.1/Q2.4) | 04/10/2026 | `d240c79` |
| 11 | `analytics/DataIngestion.scala` | lectures CSV/JSON/Parquet typées Dataset[T] + try-catch (Q2.1/Q2.3) | 04/10/2026 | `80b08fd` |
| 12 | `analytics/DataValidation.scala` | règles Q2.2, (valides, rejetés + rejection_reason) (Q2.2/Q2.3) | 04/10/2026 | `972bb25` |
| 13 | `analytics/DataQualityReport.scala` | rapport qualité Q2.4 + écriture des 4 rejets (Q2.2/Q2.4) + bonus Q2.5 (colonnes orphelins, 09/10/2026) | 04/10/2026 | `6261546` |
| 14 | `README.md` | sections Q1.3 (prérequis, compilation, exécution, spark-submit) + état | 04/10/2026 | `edb205e` |

**Les 14 fichiers du tableau ci-dessus sont tous poussés sur `main`** : la Partie 2 est
terminée (Q2.1 à Q2.4) et le bonus Q2.5 est réalisé le 09/10/2026 (le rapport de qualité
compte les transactions dont `user_id`, `product_id` ou `merchant_id` est absent du
référentiel : 3 colonnes `*_orphelins`, 0 hors ligne `transactions`). Ajouté le 04/10/2026 :
tout le guide Git de l'équipe est désormais dans la section « Travail sur GitHub » du
`README.md` (fusion de l'ancien `GITHUB.md`).

**Validation** : `sbt compile` vert à chaque étape (03/10, 04/10, 09/10/2026) ;
les 4 chemins d'entrée d'`application.conf` vérifiés existants sur le poste ;
Parties 1, 2 et 7 exécutées sur les vraies données le 09/10/2026 (rapport
qualité à 9 colonnes, rejets CSV + Parquet dans `output/`).

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
- Heures travaillées : du 04/10 vers 13h (clonage du dépôt) au 08/10, environ 10 à 20 h de travail effectif : installation de l'environnement (Git, Java, sbt, Hadoop), Q3.1, Q3.2, Q3.3, tests sur les données réelles, relecture de la Partie 2..
- Difficultés rencontrées : configuration de l'environnement sous Windows : installation de Git, blocage réseau sur git clone et git push (échec de connexion à github.com:443) contourné avec l'option -4 (IPv4), installation de sbt (ZIP lancé à la place du MSI) ; rejet d'un git push ("fetch first") résolu par git pull --rebase ; Spark 3.5.1 sur Java 17 : erreur d'accès au module sun.nio.ch, résolue avec des options --add-opens ; hadoop.dll et winutils.exe absents sous Windows, nécessaires à la lecture du Parquet ; jeux de données absents du dépôt (fournis à part) ; horodatages mal formés dans les données (8, 12 ou 16 caractères) qui font échouer to_timestamp, donc enrichAll ne doit recevoir que les transactions déjà validées ; bug de lecture des utilisateurs (age lu en BIGINT, case class en Int) signalé au Membre A.

### Membre C – SYLVA Frederic – Analytics & Performance Engineer

- Rôle (Question 0.1) : Parties 4, 5 et 6 – KPI marchands (Q4.1), cohortes de
  rétention (Q4.2), optimisations Spark (Q5.1, Q5.2), application principale
  `MainApp.scala` (Q6.1) ; bonus Q4.3 (RFM), Q4.4 (produits/catégories),
  Q5.3 (mesure du gain), Q6.2 (exécution modulaire).
- Fichiers propriétaires : `Analytics.scala`, `SparkOptimizations.scala`,
  `MainApp.scala`.
- Relecteur des Parties 1 et 7 (membre A) : relectures faites le 09/10/2026 avant d'écrire le code, inscrites dans le tableau « Relectures croisées ».
- Compte GitHub : `sylvafrederic00-lang` – invitation en collaborateur (accès
  écriture) envoyée le 03/10/2026 ; une fois acceptée :
  `git clone https://github.com/JO415boss/EcommerceAnalytics.git` puis
  `git pull origin main` avant de commencer.
- 09/10/2026 à 12h19 : livraison des Parties 4, 5 et 6 (`Analytics.scala`,
  `SparkOptimizations.scala`, `MainApp.scala`) ; `sbt compile` validé sur les
  12 fichiers Scala (commit `d807337`). Exécution modulaire :
  `run-sbt.cmd "run [ingestion|transformation|analytics|all]"`.
  Choix techniques : KPI marchands en `left` depuis le référentiel pour garder
  les marchands sans vente ; un seul cache du DataFrame enrichi partagé par
  tous les KPI ; mesure du gain (Q5.3) faite **avant** le cache, pour comparer
  un calcul froid et un calcul chaud ; CSV des transactions enrichies non
  coalescé (≈ 138 000 lignes) pour éviter un seul fichier trop gros.
- Heures travaillées : sessions du 09/10/2026 — 11h36 à 12h47 (lecture du
  dépôt, Parties 4 à 6, compilation) puis 13h50 à 15h30 (exécution sur les
  vraies données, corrections Q5.2/Q5.3, re-run) — environ 3 h au total.
- 09/10/2026 à 12h47 : mise à jour de cette section (commit `5abc029`).
- 09/10/2026 à 13h07 : ajout des commentaires sur les choix techniques ci-dessus
  (suite de la discussion de relecture).
- 09/10/2026 à 15h24 : pipeline complet exécuté sur les vraies données
  (138 047 transactions lues / 136 157 valides ; 12 000 users, 6 000 produits,
  600 marchands) — toutes les sorties écrites dans `output/` (CSV + Parquet).
  Deux corrections issues du run (commit `d43cf1d`) : hint broadcast déplacé
  sur le côté droit de la jointure gauche (Q5.2 — Spark ignorait le hint à
  gauche) et mesure Q5.3 en best-of-2 avec matérialisation du cache.
  Gains mesurés : cache **+78 %**, broadcast **+73 %**.
- Difficultés rencontrées : aucune bloquante ; premier `sbt compile` ≈ 6 min
  (téléchargement des dépendances) ; hint broadcast initial ignoré par Spark
  et mesure à froid unique bruitée sur cette VM (RAM limitée) — toutes deux
  corrigées comme indiqué ci-dessus.

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
| Partie 1 | Membre A | Membre C | 09/10/2026 | Revu le 09/10/2026 avant d'écrire les Parties 4-6 (build.sbt, project/, run-sbt.cmd, arborescence src) : structure conforme Q1.1 (packages analytics / models / utils, un fichier par rôle) ; build.sbt épingle Scala 2.13.12 et Spark 3.5.1, --add-opens Java 17 et assembly/mainClass déjà en place ; run-sbt.cmd configure HADOOP_HOME sous Windows ; sbt compile vert. Aucune remarque bloquante. Mineure : shuffle.partitions = 8, réglage pensé pour le local — à ajuster sur cluster. |
| Partie 2 | Membre A | Membre B |07/10/2026 | Revu : Q2.1 - schéma explicite (transactions), inféré (merchants), Parquet (products), try-catch avec affichage des lignes lues, chemins lus dans application.conf avec valeurs par défaut ; Q2.2 - règles conformes au sujet, rejection_reason avec plusieurs raisons séparées par concat_ws, preferred_categories converti en chaîne pour le CSV des rejets, seuils externalisés ; Q2.4 - countNulls toutes colonnes confondues. Remarque bloquante : sur les vraies données, readUsers échoue (Cannot up cast age from BIGINT to INT), le dataset users est vide ; correction proposée : cast("int") sur age avant .as[User]. Remarque mineure : une valeur nulle de amount est étiquetée "amount <= 0". À vérifier : arrondi de taux_rejet et fichier CSV unique. Mise à jour du 09/10/2026 (membre A) : cast sur `age` appliqué (commit `07a3e96`) puis lecture users validée sur les vraies données (12 000 lignes) ; `taux_rejet` arrondi à 2 décimales et rapport en CSV unique confirmés sur l'exécution du 09/10 ; seule reste la remarque mineure (montant nul étiqueté `amount <= 0`). |
| Partie 3 | Membre B | Membre A | 06/10/2026 | Revu le 06/10/2026 (TimeFeatures.scala, DataTransformation.scala) : Q3.1 — fonction extract pure, null et longueur != 14 gérés, Locale.ENGLISH figé (résultats déterministes) ; Q3.2 — colonnes homonymes renommées avant les jointures left, struct time_features éclatée ; Q3.3 — fenêtre range -7 jours sur timestamp casté en long, collect_set + size à la place de countDistinct (interdit en fenêtre), lag et datediff. sbt compile vert le 06/10/2026 sur les 9 fichiers Scala. À confirmer lors de l'intégration : frontière age_group à 25 ans (classée Adulte, documentée dans le code) et product.category non retenue dans les colonnes finales. |
| Partie 4 | Membre C | Membre B | à faire | |
| Partie 5 | Membre C | Membres A et B | 09/10/2026 (membre A) | Revue par le membre A le 09/10/2026 (`SparkOptimizations.scala` + intégration dans `MainApp.scala`) : Q5.1 — cache `MEMORY_AND_DISK` activable par `app.optimization.enable-cache` via `ConfigLoader`, `unpersist` bien appelé après usage ; Q5.2 — hint `broadcast` sur le référentiel marchands, activable par `app.optimization.enable-broadcast` ; Q5.3 — mesure best-of-2 (bruit limité), cache matérialisé hors chronométrage, `gain_pct` arrondi à 2 décimales et limites honnêtement documentées, résultats écrits en CSV + Parquet via l'objet commun. Les deux interrupteurs passent par `application.conf` (Q7.1 respecté) ; compilation intégrée verte sur les 12 fichiers Scala. Aucune remarque bloquante. Mineur : la mesure Q5.3 chronomètre des actions de comptage uniques, le gain du cache y est structurellement sous-estimé — le code le documente déjà. Relecture du membre B à inscrire. |
| Partie 6 | Membre C | Les 3 membres (intégration) | 09/10/2026 (membre A) | Intégration validée par le membre A le 09/10/2026 : `run-sbt.cmd run test` exécuté en entier sur les vraies données (47 min, `[success]`) — les 11 résultats écrits deux fois (CSV + Parquet) dans `output/`, rapport qualité en CSV unique, SparkContext arrêté proprement ; aucun doublon de nom de sortie entre les modules ; `MainApp` n'appelle que les objets communs (`ConfigLoader`, `SparkSessionBuilder`, `DataFrameWriterUtils`) et les modules des 3 membres. Aucune remarque bloquante. |
| Partie 7 | Membre A | Membre C | 09/10/2026 | Revu le 09/10/2026 avant d'écrire les Parties 4-6 (application.conf, ConfigLoader, SparkSessionBuilder) : configuration externalisée conforme Q7.1 (chemins data/output, spark, optimization, validation), valeurs par défaut dans ConfigLoader, SparkSessionBuilder construite uniquement depuis application.conf (aucune valeur en dur) ; les clés app.optimization.* sont bien consommées par DataTransformation et par mes fichiers (Q5.1/Q5.2). Aucune remarque bloquante. Mineure : shuffle.partitions = 8 à ajuster sur cluster. |