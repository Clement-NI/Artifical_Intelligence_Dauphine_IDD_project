# Artifical_Intelligence_Dauphine_IDD_project
C'est le projet final pour le cours l'intelligence artificielle. Nous somme un groupe de 3 personnes.

## Apprentissage par renforcement (Reinforcement Learning)

En complément de l'IA heuristique (`src/logic/AI.java`), le paquet `src/rl`
ajoute un agent qui **apprend** à jouer à Pac-Man par renforcement, et le valide
puis le teste.

### Méthode

Q-learning approché (linéaire), au sens du projet Pac-Man de Berkeley (CS188) :

```
Q(s, a) = somme_i  w_i * f_i(s, a)
```

Les poids `w_i` sont appris par différence temporelle (TD) après chaque
transition `(s, a, r, s')` :

```
delta = (r + gamma * max_a' Q(s', a')) - Q(s, a)
w_i  <- w_i + alpha * delta * f_i(s, a)
```

L'exploration est `epsilon`-greedy avec décroissance d'`epsilon`.

### Organisation du code

| Fichier | Rôle |
|---|---|
| `src/rl/GameView.java`      | Vue en lecture seule d'un état de jeu (abstraction commune). |
| `src/rl/Features.java`      | Extraction des caractéristiques `f(s, a)`. |
| `src/rl/QLearningAgent.java`| Agent Q-learning approché (Q-valeur, mise à jour TD, ε-greedy, persistance). |
| `src/rl/PacmanEnv.java`     | Simulateur Pac-Man *headless* servant d'environnement d'entraînement. |
| `src/rl/Snapshot.java`      | Photo immuable d'un état (pour la mise à jour TD). |
| `src/rl/RLTrainer.java`     | **Applique** l'apprentissage, le **valide** (convergence) et le **teste** (vs aléatoire). |
| `src/rl/RLTest.java`        | Tests d'intégration déterministes de la politique apprise. |
| `src/rl/RealMapRunner.java` | Évalue la politique apprise sur les vraies cartes (`doc/map1..3.map`). |
| `src/logic/QLearningAI.java`| Pont avec le vrai jeu : politique apprise utilisable à la place de `AI.findNextMove`. |
| `doc/qweights.txt`          | Poids appris (générés par l'entraînement). |

### Compiler

```bash
mkdir -p out
find src -name '*.java' | javac -d out @/dev/stdin
```

### Entraîner, valider et tester

```bash
# 1) Entraînement + validation (convergence) + test (vs aléatoire) ; sauve doc/qweights.txt
java -cp out rl.RLTrainer

# 2) Tests d'intégration de la politique apprise
java -cp out rl.RLTest

# 3) Évaluation sur les vraies cartes du jeu (25x25), sans interface graphique
java -cp out rl.RealMapRunner
```

Les deux programmes renvoient un code de sortie 0 en cas de succès (utilisable
en intégration continue).

### Résultats obtenus

- **Validation (convergence)** : le retour moyen par épisode passe d'environ
  −415 (début) à +176 (fin), soit une amélioration de ~+591.
- **Test (politique apprise vs aléatoire)** :

  | Politique     | Score moyen | Retour moyen | Victoires |
  |---------------|------------:|-------------:|----------:|
  | Apprise (RL)  |       876.0 |        531.5 |     47.8% |
  | Aléatoire     |       126.4 |       −408.1 |      0.0% |

- **Tests d'intégration** : l'agent fuit un fantôme dangereux, va chercher une
  gomme proche, et poursuit un fantôme apeuré — tous passent.
- **Évaluation sur les vraies cartes** (200 parties par carte, politique apprise
  uniquement sur la carte 11x11, jamais sur ces cartes 25x25) :

  | Carte | Taux de victoire | Score moyen | Meilleur score |
  |-------|-----------------:|------------:|---------------:|
  | map1 (violet) | 44 % | 1773 | 2960 |
  | map2 (green)  | 32 % | 1848 | 3310 |
  | map3 (pink)   | 47 % | 1687 | 2630 |

  L'agent réussit à **terminer les 3 cartes** (au moins une victoire chacune),
  ce qui montre que la politique apprise se généralise à des niveaux inédits.

### Utiliser la politique apprise dans le jeu

Dans `src/logic/PacManLauncher.java` (méthode `animate`), remplacer :

```java
AI.findNextMove(this.maps.getVisibleBeliefState().get(0))
```

par :

```java
QLearningAI.findNextMove(this.maps.getVisibleBeliefState().get(0))
```

`QLearningAI` charge automatiquement `doc/qweights.txt` (ou, à défaut, des poids
appris par défaut intégrés au code).
