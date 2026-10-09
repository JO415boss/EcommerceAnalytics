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
- Convention imposée par le sujet (Q6.1) : chaque résultat est écrit **deux fois**,
  `output/csv/<nom>/` (en-tête `true`, `coalesce(1)` pour les petits résultats) et
  `output/parquet/<nom>/`, en mode `overwrite`. Seul le rapport de qualité est un
  **fichier CSV unique** (`output/rapport_qualite_yyyymmdd.csv`). Ces écritures passent
  toutes par `utils/DataFrameWriterUtils`, jamais réécrites au cas par cas.
- Exécution modulaire (bonus Q6.2, membre C) : `MainApp` accepte un argument d'étape
  (`ingestion`, `transformation`, `analytics`, `all` par défaut).

## Travail sur GitHub (organisation du groupe)

Dépôt distant : `https://github.com/JO415boss/EcommerceAnalytics.git`, branche principale
**`main`**. Le dépôt est publié depuis le 03/10/2026 et les trois membres sont invités.

| Membre | Périmètre | Fichiers qu'il pousse |
|---|---|---|
| A — Joseph N SADIO | Parties 1, 2, 7 + mise en place Git + Partie 8 | ses fichiers modifiés uniquement |
| Collectif | Partie 8 (documentation) | `CONTRIBUTIONS.md`, `EQUIPE.md`, `README.md` |

**Règle d'or** : chacun ne modifie que **ses propres fichiers** — c'est ce qui évite les
conflits annoncés par la Q1.1. La mise en commun se fait par `git pull` avant de commencer et
`git push` une fois le travail committé.

### 1. Créer et publier le dépôt — ✅ déjà réalisé le 03/10/2026

```bat
git remote add origin https://github.com/JO415boss/EcommerceAnalytics.git
git fetch origin
```

Sur GitHub : **Settings → Collaborators → Add people** pour inviter les deux autres membres.

### 2. Configurer son poste — une seule fois (Q0.2)

L'identité de l'auteur doit être correcte **avant le premier commit**, sinon les commits ne
sont pas identifiables dans `git log` et la Q0.2 n'est pas démontrable.

```bat
git config --global user.name  "Joseph N SADIO"          :: exemple : membre A
git config --global user.email "sadiojoseph4@gmail.com"  :: votre vraie adresse
```

Puis selon le cas :

- **Projet déjà présent sur le poste, pas encore relié à GitHub** (cas du membre A, déjà fait) :

```bat
git remote add origin https://github.com/JO415boss/EcommerceAnalytics.git
git add <vos-fichiers>                      :: 1er commit local SANS LEQUEL le pull échoue
git commit -m "Nom : travaux déjà présents sur le poste"
git fetch origin
git pull origin main --no-rebase --allow-unrelated-histories
git push -u origin main
```

- **Nouveau poste** (projet pas encore récupéré) :

```bat
git clone https://github.com/JO415boss/EcommerceAnalytics.git
cd EcommerceAnalytics
```

Vérification : `git config user.name` et `git config user.email`.


### 3. Cycle de travail — à chaque session, pour tous les membres

```bat
cd "C:\Users\pc\Documents\EcommerceAnalytics"

:: 1. toujours synchroniser AVANT de commencer
git pull origin main

:: 2. travailler uniquement sur ses propres fichiers (voir tableau ci-dessus)

:: 3. vérifier ce qui a changé
git status --short
git diff

:: 4. ajouter SES fichiers (jamais git add . à l'aveugle)
git add <fichier-modifié>                  :: un seul fichier à la fois

:: 5. commit préfixé par le nom de l'auteur (identifiable dans git log)
git commit -m "Nom : description du changement"

:: 6. publier sur GitHub
git push origin main
```

### 4. Vérifier qui a fait quoi (commandes de preuve pour la Q0.2 / Q0.3)

```bat
git log --oneline                             :: historique complet
git log --oneline --author="Joseph N SADIO"   :: commits d'un auteur
git log --stat                                :: fichiers touchés par commit
```

### 5. À ne jamais pousser sur GitHub

`.gitignore` (créé par le membre A le 03/10/2026) exclut `target/`, `project/target/`,
`project/project/`, `.bsp/`, `.metals/`, `.bloop/`, `.idea/`, `data/`, `output/`, `logs/`,
`*.log`, `*.class`. Ne jamais forcer l'ajout de ces dossiers :

```bat
git status --short     :: ne doit jamais afficher target/, data/ ni output/
```

### 6. Résolution des conflits (si `git push` est refusé)

```bat
git pull origin main --no-rebase
:: si Git signale un conflit : ouvrir le fichier, choisir la bonne version, puis :
git add <fichier_conflit>
git commit -m "Nom : résolution de conflit sur <fichier>"
git push origin main
```

La règle « chacun touche uniquement à ses fichiers » rend les conflits quasi impossibles :
aucun conflit n'a eu lieu depuis le début du projet.

### 7. (Facultatif) Branche + Pull Request — non exigé par le sujet

```bat
git checkout -b feature/sujet-q3
git add src/main/scala/com/ecommerce/analytics/DataTransformation.scala
git commit -m "Nom : features temporelles Q3.1"
git push -u origin feature/sujet-q3
```

Puis sur GitHub : **Compare & pull request** → le relecteur désigné dans `CONTRIBUTIONS.md`
relit le diff et clique sur **Merge pull request**.

### 8. Contrôle automatique de la documentation (hook local, facultatif)

Les tableaux Markdown tronqués sont invisibles à la relecture rapide et donnent une
impression de travail bâclé. Le dépôt fournit donc un vérificateur versionné :

```bat
powershell -NoProfile -ExecutionPolicy Bypass -File tools\check-markdown-tables.ps1
```

Il contrôle tous les fichiers `*.md` suivis par Git (colonnes cohérentes dans chaque
tableau, tuyaux échappés `\|` correctement ignorés) et renvoie le code de sortie `1` en
cas d'anomalie. Pour l'exécuter automatiquement **avant chaque commit**, installer le
hook local (fichier propre à chaque poste, jamais versionné) :

```bat
powershell -NoProfile -ExecutionPolicy Bypass -File tools\install-git-hooks.ps1
:: dans Git Bash, rendre le hook executable :
chmod +x .git/hooks/pre-commit
```


## Où lire quoi

| Fichier | Rôle |
|---|---|
| `EQUIPE.md` | membres, rôles (Parties 1-7) et comptes GitHub |
| `CONTRIBUTIONS.md` | répartition des questions, charge de travail, décisions techniques |
| `tools/` | outillage de qualité : `check-markdown-tables.ps1` (vérifie les tableaux Markdown), `install-git-hooks.ps1` (installe le hook `pre-commit`) |

Le mode d'emploi Git du groupe n'est plus dans un fichier séparé : il constitue la
section **« Travail sur GitHub »** ci-dessus (l'ancien `GITHUB.md` y a été fusionné le
04/10/2026 pour supprimer un doublon documentaire).

## État actuel

- ✅ Dépôt GitHub, build SBT et configuration créés et validés par le membre A
  (fichiers et commits détaillés dans `CONTRIBUTIONS.md`).
- ✅ Partie 1 (Q1.1 à Q1.3), Partie 2 (Q2.1 à Q2.4) et Partie 7 (Q7.1) terminées
  par le membre A : 7 fichiers Scala compilés avec succès.
- ✅ Partie 3 (Q3.1 à Q3.3) livrée par le membre B (`TimeFeatures.scala`,
  `DataTransformation.scala`) et relue par le membre A le 06/10/2026 (détail dans
  `CONTRIBUTIONS.md`, section « Relectures croisées »).
- ⏳ Reste au membre C les Parties 4 à 6 (`Analytics.scala`,
  `SparkOptimizations.scala`, `MainApp.scala`).
- En l'état, `run-sbt.cmd compile` fonctionne ; `sbt run` et `sbt assembly` ne
  produiront un exécutable qu'une fois le point d'entrée ajouté.
- ✅ `sbt compile` validé le 06/10/2026 sur les 9 fichiers Scala du projet
  (7 du membre A, 2 du membre B).
- ✅ 09/10/2026 : lecture de `users.json` corrigée (relecture de la Partie 2)
  et commentaires du code du membre A rendus plus simples.

## Règles du groupe

- Chacun ne modifie que **ses fichiers** (tableau dans la section « Travail sur GitHub »
  ci-dessus).
- Avant de commencer : `git pull origin main` — après : `git push origin main`.
- Messages de commit préfixés par le nom : `Nom : description`.
- Avant la remise : codes étudiants et relectures croisées complétés dans
  `EQUIPE.md` et `CONTRIBUTIONS.md`.
