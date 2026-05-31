# Pac-Man par Apprentissage par Renforcement — Support de présentation

> Q-learning approché (linéaire) + recherche à horizon, appliqué au Pac-Man du projet.
> Taux de victoire : **~43% → ~58%**.

---

## 1. En une phrase

On entraîne un agent à jouer à Pac-Man par **Q-learning approché (approximation
linéaire de la fonction de valeur)**, puis on ajoute au moment de jouer une
**recherche à horizon limité (lookahead)**. Le taux de victoire passe d'environ
**43% à 58%**.

---

## 2. Modélisation : un MDP (processus de décision markovien)

| Élément | Dans Pac-Man |
|---|---|
| État `s` | carte, position de Pac-Man, positions des fantômes, état apeuré, gommes restantes |
| Action `a` | HAUT / BAS / GAUCHE / DROITE |
| Récompense `r` | gomme +10, super gomme +200, fantôme apeuré mangé +200, victoire +500, mort −500, chaque pas −1 |
| `γ` | facteur d'actualisation = 0,95 |

**Objectif** : trouver une politique `π` qui maximise le retour cumulé actualisé
attendu `E[ Σ γ^t r_t ]`.

---

## 3. Pourquoi pas un Q-learning tabulaire ?

Le Q-learning classique stocke une table `Q(s,a)`. Mais l'espace d'états de
Pac-Man est gigantesque (position × positions de tous les fantômes × quelles
gommes restent) : **impossible à stocker ou à apprendre**.

**Solution : approximer `Q` par une combinaison linéaire de caractéristiques :**

```
Q(s,a) = Σ_i  w_i · f_i(s,a)  =  wᵀ · f(s,a)
```

On n'apprend que **n = 9 poids** `w_i`, au lieu d'un nombre astronomique
d'entrées. Les `f_i(s,a)` sont des caractéristiques conçues à la main (§5).

---

## 4. Formule d'apprentissage : différence temporelle (TD)

À chaque transition vécue `(s, a, r, s')` :

**① Cible TD :**
```
y = r + γ · max_a' Q(s', a')        (= r si s' est terminal)
```

**② Erreur TD :**
```
δ = y − Q(s, a)
```

**③ Mise à jour de chaque poids (gradient) :**
```
w_i ← w_i + α · δ · f_i(s, a)
```

avec `α` = taux d'apprentissage (0,0008).

> Intuition : l'écart `δ` entre ce qu'on prédisait `Q(s,a)` et ce qu'on a
> réellement observé `y` est redistribué sur chaque poids proportionnellement à
> sa contribution `f_i`. C'est la descente de gradient version modèle linéaire.

**Exploration** : ε-greedy — avec probabilité ε on explore au hasard, sinon on
prend `argmax_a Q(s,a)`. ε décroît de 1,0 à 0,05 (explorer puis exploiter).

---

## 5. Les 9 caractéristiques f_i(s,a)

Chacune est calculée sur la case où Pac-Man arrive après l'action `a`,
normalisée vers [0,1].

| # | Caractéristique | Poids appris | Sens |
|--:|---|--:|---|
| 0 | bias | +112 | constante |
| 1 | fantôme dangereux à 1 pas | **−529** | ne JAMAIS coller un fantôme (le + important) |
| 2 | mange une gomme | +63 | encourager à manger |
| 3 | distance BFS à la gomme la + proche | −29 | aller vers les gommes |
| 4 | distance au fantôme apeuré le + proche | +29 | aller le manger (points) |
| 5 | nb de fantômes dangereux à 2 pas | −99 | préavis : s'écarter à temps |
| 6 | mange une SUPER gomme | **+154** | aller chercher les super gommes |
| 7 | se rapproche d'une super gomme quand traqué | **+129** | utiliser la super gomme comme parade |
| 8 | distance de sécurité aux fantômes (bornée) | — | rester loin des fantômes |

> **Point clé à présenter** : le signe et l'amplitude des poids sont
> **interprétables** — on lit directement la stratégie apprise. C'est l'avantage
> majeur du modèle linéaire sur un réseau de neurones.

---

## 6. Boucle d'entraînement (schéma)

```
Pour chaque épisode (30000, alternant les 3 vraies cartes) :
    ε ← décroît linéairement de 1,0 à 0,05
    réinitialiser l'environnement
    tant que la partie n'est pas finie :
        a ← action ε-greedy                 ← exploration / exploitation
        exécuter a, observer r, s'
        y ← r + γ · max_a' Q(s', a')        ← cible TD
        δ ← y − Q(s, a)                     ← erreur TD
        w ← w + α · δ · f(s, a)             ← mise à jour des poids
        s ← s'
sauvegarder les poids w dans qweights.txt
```

---

## 7. Renforcer la décision : recherche à horizon (lookahead)

Au moment de jouer, on ne regarde pas un seul pas : on anticipe sur 3 pas.

```
a* = argmax_a [ r(s,a) + γ · max_a' ( r' + γ · max_a'' … Q_feuille ) ]
```

- les actions de Pac-Man sont MAXIMISÉES ;
- les fantômes répondent par une poursuite gloutonne déterministe ;
- les feuilles de l'arbre sont évaluées par le `Q` linéaire appris ;
- la profondeur 3 fait passer le taux de victoire de 43% à 58%.

---

## 8. Fil narratif conseillé pour la présentation

Présenter l'**itération guidée par les données** (et non un simple résultat) :

1. **Base** : Q-learning linéaire → 43%, on plafonne.
2. **Diagnostic** : on mesure que **100% des défaites sont des collisions**
   (mort par fantôme), 0% par dépassement de temps.
3. **Idée** : manger une super gomme rend les fantômes comestibles → supprime la
   cause n°1 des défaites.
4. **Amélioration** : récompense super gomme ↑ + features capsule + lookahead →
   **55%**, puis anti-boucle + distance de securite -> **~58%**.
5. **Échec assumé** : on a aussi essayé un DQN (réseau de neurones) — résultat
   PIRE (20%). Conclusion : « plus complexe ≠ meilleur ».

> Ce fil montre une **démarche scientifique** (hypothèse → expérience →
> vérification), bien plus convaincant qu'un chiffre brut.

---

## 9. Démonstrations en direct

```bash
# Compiler
javac -encoding UTF-8 -d out @sources.txt

# Voir l'IA jouer (lookahead profondeur 3, ~58%)
java -cp out logic.PacManLauncher

# Comparer : politique gloutonne (profondeur 1, ~43%)
java -Dpacman.depth=1 -cp out logic.PacManLauncher

# Montrer le diagnostic « 100% de morts par collision »
java -cp out rl.Diagnose

# Reproduire l'évaluation (taux de victoire par carte)
java -cp out rl.VerifyLookahead
```

---

## 10. Chiffres clés à retenir

| Approche | Taux de victoire moyen |
|---|--:|
| Linéaire gloutonne (profondeur 1) | ~43% |
| **Linéaire + capsules + lookahead prof. 3 (6 features)** | ~55% |
| **+ anti-boucle + distance de sécurité (9 features)** | **~58-60%** |
| DQN (réseau de neurones) — échec | ~20% |

Vérifié sur 3 graines d'évaluation indépendantes.
