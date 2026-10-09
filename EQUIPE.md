# Équipe

| Nom    | Prénom | Code étudiant | Rôle (Question 0.1 du sujet) | Questions traitées |
|--------|--------|---|---|---|
| SADIO | Joseph Niaga (Joseph N) | 1510384 | Membre A – Data Ingestion & Platform Engineer | Q1.1 à Q1.3, Q2.1 à Q2.4, Q7.1 (bonus : Q2.5) ; Partie 8 en commun |
| SECK | Mamour | 1405548 | Membre B – Data Transformation Engineer | Q3.1 à Q3.3 (bonus : Q3.4) ; Partie 8 en commun |
| SYLVA | Frederic | 1614003 | Membre C – Analytics & Performance Engineer | Q4.1, Q4.2, Q5.1, Q5.2, Q6.1 (bonus : Q4.3, Q4.4, Q5.3, Q6.2) ; Partie 8 en commun |

## Périmètre de chaque rôle (d'après le sujet)

| Rôle | Périmètre principal | Livrables dont le membre est propriétaire |
|---|---|---|
| Membre A | Parties 1, 2 et 7 | Structure SBT, `build.sbt`, case classes, `DataIngestion.scala`, validations, rapport de qualité des données, `application.conf`, `README.md` |
| Membre B | Partie 3 | UDF `extractTimeFeatures`, `DataTransformation.scala`, jointures d'enrichissement, fonctions de fenêtrage, détection de comportements |
| Membre C | Parties 4, 5 et 6 | `Analytics.scala`, KPI marchands, cohortes, segmentation RFM, optimisations Spark, `MainApp.scala`, écriture des résultats |

Les Parties 8 et 9 (tests, qualité, documentation, soutenance) sont réalisées
collectivement : chaque membre y contribue pour la portion du code dont il est
propriétaire.

## Responsables et relecteurs par partie (imposés par le sujet)

| Partie | Responsable | Relecteur |
|---|---|---|
| Partie 1 – Configuration et structure du projet | Membre A | Membre C |
| Partie 2 – Ingestion et validation des données | Membre A | Membre B |
| Partie 3 – Transformations avancées | Membre B | Membre A |
| Partie 4 – Analytique business | Membre C | Membre B |
| Partie 5 – Optimisations Spark | Membre C | Membres A et B |
| Partie 6 – Application principale | Membre C | Intégration validée par les 3 membres |
| Partie 7 – Configuration externalisée | Membre A | Membre C |
| Parties 8 et 9 – Livrables et soutenance | Collectif | — |

Comptes GitHub des membres : **A** = `JO415boss`, **B** = `mamourseck179-maker`,
**C** = `sylvafrederic00-lang`.

Invitations en collaborateur (accès écriture) envoyées le 03/10/2026 à B et C ;
elles restent valables jusqu'à leur acceptation sur github.com.

## Mise en place Git (membre A – du 03/10/2026 au 06/10/2026)

- Dépôt distant : `https://github.com/JO415boss/EcommerceAnalytics.git` (branche `main`).
- `.gitignore`, identité Git (`Joseph N SADIO <sadiojoseph4@gmail.com>`) et
  poussée initiale des documents réalisés par le membre A.
- **04/10/2026** : le guide Git du groupe (ancien `GITHUB.md`) est désormais la section
  « Travail sur GitHub » du `README.md` ; `GITHUB.md` a été supprimé pour éviter un doublon
  documentaire (la Q8.1 ne demande que `README.md`, `EQUIPE.md` et `CONTRIBUTIONS.md`).
- Fichiers de préparation personnels (`TRAVAIL_MEMBRE_*.md`) : ignorés par Git, jamais
  poussés et jamais inclus dans l'archive ZIP.
- **06/10/2026** : re-synchronisation (`git pull origin main`) avant reprise du
  travail, revue croisée de la Partie 3 (membre B) et poussée des mises à jour
  de documentation sur `main`.

## Règles de travail

- Les relectures croisées sont obligatoires.
- Les commits doivent être faits avec le nom du membre.
