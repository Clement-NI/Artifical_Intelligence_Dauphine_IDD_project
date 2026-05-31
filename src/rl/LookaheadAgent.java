package rl;

import java.util.List;

/**
 * Agent qui combine le modele lineaire appris (Q-learning approche) avec une
 * recherche a horizon limite (lookahead) de profondeur N.
 *
 * Objectif : repondre experimentalement a la question "ce modele lineaire
 * peut-il bien predire sur plusieurs pas ?". On utilise la Q-valeur apprise
 * comme fonction d'evaluation aux feuilles, et on deroule explicitement N pas
 * de jeu :
 *
 *   - noeud Pac-Man  : on MAXIMISE sur les actions legales ;
 *   - transition des fantomes : elle est stochastique dans l'environnement, on
 *     l'ESTIME par echantillonnage Monte-Carlo (plusieurs tirages moyennes) ;
 *   - aux feuilles (profondeur 0) : valeur = max_a Q(s, a) du modele lineaire.
 *
 * Si predire plusieurs pas aide, le taux de victoire doit croitre avec N. Si le
 * modele lineaire "voit deja assez loin" (la Q-valeur encode le retour futur),
 * l'amelioration sera faible. C'est exactement ce que l'experience mesure.
 *
 * Profondeur 1 (sans rollout) revient a la politique gloutonne d'origine.
 */
public class LookaheadAgent {

    private final QLearningAgent q;   // modele lineaire appris (evaluation aux feuilles)
    private final int depth;          // nombre de pas de Pac-Man anticipes
    private final int samples;        // tirages Monte-Carlo pour la reponse des fantomes
    private final double gamma;
    private long seedCounter = 1234567;

    public LookaheadAgent(QLearningAgent q, int depth, int samples, double gamma) {
        this.q = q;
        this.depth = Math.max(1, depth);
        this.samples = Math.max(1, samples);
        this.gamma = gamma;
    }

    /** Choisit la meilleure action depuis l'etat reel par recherche a N pas. */
    public int chooseAction(PacmanEnv env) {
        List<Integer> legal = env.legalActions();
        // Profondeur 1 pure = politique gloutonne du modele lineaire.
        if (depth == 1) {
            return q.greedyAction(env, legal);
        }
        double best = Double.NEGATIVE_INFINITY;
        int bestA = legal.get(0);
        for (int a : legal) {
            double v = evalAction(env, a, depth);
            if (v > best) {
                best = v;
                bestA = a;
            }
        }
        return bestA;
    }

    /**
     * Valeur esperee d'appliquer l'action {@code a} dans {@code env}, avec
     * (depth-1) pas supplementaires de recherche apres la reponse des fantomes.
     * La stochasticite des fantomes est estimee par moyenne sur {@code samples}
     * tirages.
     */
    private double evalAction(PacmanEnv env, int a, int d) {
        double total = 0.0;
        for (int s = 0; s < samples; s++) {
            PacmanEnv sim = env.copy(seedCounter++);
            double r = sim.step(a);                 // un pas complet (Pac-Man + fantomes)
            double future;
            if (sim.isDone() || d <= 1) {
                future = sim.isDone() ? 0.0 : leafValue(sim);
            } else {
                future = bestActionValue(sim, d - 1);
            }
            total += r + gamma * future;
        }
        return total / samples;
    }

    /** max_a valeur(a) a l'etat donne, avec profondeur restante d. */
    private double bestActionValue(PacmanEnv env, int d) {
        List<Integer> legal = env.legalActions();
        double best = Double.NEGATIVE_INFINITY;
        for (int a : legal) {
            best = Math.max(best, evalAction(env, a, d));
        }
        return best;
    }

    /** Evaluation aux feuilles : la Q-valeur apprise du modele lineaire. */
    private double leafValue(PacmanEnv env) {
        return q.maxQ(env, env.legalActions());
    }
}
