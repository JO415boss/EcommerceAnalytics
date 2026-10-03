# Travail sur GitHub – ce que chacun doit faire et les commandes

Dépôt distant du groupe : `https://github.com/JO415boss/EcommerceAnalytics.git`
(dépôt créé et publié ; branche principale : **main**).

> **État au 03/10/2026 (membre A)** : dépôt initialisé, `.gitignore` en place,
> documents de suivi poussés sur `main`. Les autres membres n'ont plus qu'à
> cloner (section 2, « Nouveau poste ») puis à suivre le cycle de la section 3.

| Membre | Nom | Questions / périmètre | Fichiers qu'il pousse |
|---|---|---|---|
| A | Joseph N SADIO | Mise en place Git (dépôt, `.gitignore`, poussée initiale) + Partie 8 | ses fichiers modifiés uniquement |
| Collectif | — | Partie 8 (documentation) | `CONTRIBUTIONS.md`, `EQUIPE.md`, `README.md`, `GITHUB.md` |

Règle d'or pour éviter les conflits : **chacun ne modifie que ses propres fichiers**
(liste ci-dessus). La mise en commun se fait par `git pull` avant de commencer
et `git push` une fois le travail committé.

---

## 1. Une seule fois – créer et publier le dépôt

✅ **Déjà réalisé** le 03/10/2026 par le membre A : le dépôt
`https://github.com/JO415boss/EcommerceAnalytics.git` existe (branche `main`).

Ensuite, depuis la racine du projet (`C:\Users\pc\Documents\EcommerceAnalytics`) :

```bat
git remote add origin https://github.com/JO415boss/EcommerceAnalytics.git
git fetch origin
```

Puis inviter les collaborateurs : sur GitHub → **Settings → Collaborators → Add people**.

## 2. Une seule fois – configuration de chaque membre

**Sur chaque poste**, identifier l'auteur des commits (Question 0.2 du sujet).

```bat
git config --global user.name  "Joseph N SADIO"          :: exemple : membre A
git config --global user.email "sadiojoseph4@gmail.com"   :: votre vraie adresse
```

(Adaptez chaque adresse à la vraie adresse e-mail du membre.)

Puis **l'un des deux cas** :

- **Poste déjà avec le projet** (dossier local existant, pas encore relié à
  GitHub — *cas du poste du membre A, déjà exécuté et vérifié le 03/10/2026*) :

```bat
cd "C:\Users\pc\Documents\EcommerceAnalytics"
git remote add origin https://github.com/JO415boss/EcommerceAnalytics.git
git add <vos-fichiers>                          :: 1er commit local SANS LEQUEL le pull échoue
git commit -m "Nom : travaux déjà présents sur le poste"
git fetch origin
git pull origin main --no-rebase --allow-unrelated-histories
git push -u origin main
```

> **Déjà fait ici** : sur le poste du membre A, `origin` est déjà déclaré
> (`git remote -v`), l'historique local et distant sont fusionnés et synchronisés
> sur `main` — relancer ce bloc provoquerait `fatal: remote origin already exists`,
> c'est normal : passez directement au cycle de la section 3. Règle à retenir :
> **toujours `git add` + `git commit` avant un premier `git pull`**.

- **Nouveau poste** (projet pas encore récupéré) :

```bat
cd "C:\Users\pc\Documents"
git clone https://github.com/JO415boss/EcommerceAnalytics.git
cd EcommerceAnalytics
```

Vérification que l'identité est bonne :

```bat
git config user.name
git config user.email
```

## 3. Cycle de travail – chaque session de travail (tous les membres)

```bat
cd "C:\Users\pc\Documents\EcommerceAnalytics"

:: 1. toujours synchroniser AVANT de commencer
git pull origin main

:: 2. travailler uniquement sur ses propres fichiers (voir tableau ci-dessus)

:: 3. vérifier ce qui a changé
git status --short
git diff

:: 4. ajouter SES fichiers (jamais git add . à l'aveugle)
git add <fichier-modifié>                        :: un seul fichier à la fois

:: 5. commit avec le nom de l'auteur devant (nom identifiable dans git log)
git commit -m "Nom : description du changement"

:: 6. publier sur GitHub
git push origin main
```

Messages de commit attendus :

| Auteur | Préfixe obligatoire dans le message |
|---|---|
| chaque membre | `Nom : ...` |

## 4. Vérifier qui a fait quoi (commandes de contrôle)

```bat
git log --oneline                          :: historique complet
git log --oneline --author="Joseph N SADIO"  :: commits d'un auteur
git log --stat                            :: détail des fichiers par commit
```

Ces commandes servent de preuve pour le journal de contribution (`CONTRIBUTIONS.md`).

## 5. À ne JAMAIS pousser sur GitHub

`.gitignore` créé le 03/10/2026 par le membre A ; il exclut déjà : `target/`,
`project/target/`, `.idea/`, `data/`, `output/`, `*.log`. Ne jamais forcer
l'ajout de ces dossiers :

```bat
git status --short     :: ne doit jamais afficher target/ ni output/
```

## 6. Résolution des conflits (si `git push` est refusé)

```bat
git pull origin main --no-rebase
:: si Git signale un conflit : ouvrir le fichier, choisir la bonne version,
:: puis :
git add <fichier_conflit>
git commit -m "Membre X : résolution de conflit sur <fichier>"
git push origin main
```

En pratique, la règle « chacun touche uniquement à ses fichiers » rend les conflits
quasi impossibles.

## 7. (Facultatif) Branche + Pull Request – non exigé par le sujet

Le sujet n'exige ni branches ni PR ; si le groupe veut quand même utiliser GitHub
plus proprement :

```bat
git checkout -b feature/sujet-q3           :: créer et basculer sur sa branche
git add src/main/scala/com/ecommerce/analytics/DataTransformation.scala
git commit -m "Nom : features temporelles Q3.1"
git push -u origin feature/sujet-q3
```

Puis sur GitHub : **Compare & pull request** → le relecteur désigné dans
`CONTRIBUTIONS.md` relit le diff et clique sur **Merge pull request**.
