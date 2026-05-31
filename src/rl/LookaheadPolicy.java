package rl;

import java.util.List;

/**
 * Politique a recherche DETERMINISTE a horizon limite (lookahead), au-dessus du
 * modele lineaire appris.
 *
 * A chaque decision, on developpe l'arbre des actions de Pac-Man sur {@code depth}
 * pas, les fantomes repondant par leur poursuite gloutonne (deterministe, via
 * {@link PacmanEnv#stepDet(int)}). La valeur d'une feuille est la Q-valeur
 * apprise (modele lineaire). On choisit l'action de retour cumule actualise
 * maximal.
 *
 * Cout maitrise : le branchement ne vient que de Pac-Man (<=4 actions, souvent 2
 * dans un couloir) et les fantomes sont deterministes -> pas d'explosion comme
 * avec l'echantillonnage Monte-Carlo.
 */
public class LookaheadPolicy {

    private final QLearningAgent q;
    private final int depth;
    private final double gamma;

    public LookaheadPolicy(QLearningAgent q, int depth, double gamma) {
        this.q = q;
        this.depth = Math.max(1, depth);
        this.gamma = gamma;
    }

    /** Action choisie depuis l'etat reel (non modifie : on travaille sur des copies). */
    public int choose(PacmanEnv env) {
        List<Integer> legal = env.legalActions();
        if (depth == 1) return q.greedyAction(env, legal);
        double best = Double.NEGATIVE_INFINITY;
        int bestA = legal.get(0);
        for (int a : legal) {
            PacmanEnv sim = env.copy(0);          // graine ignoree (stepDet est deterministe)
            double r = sim.stepDet(a);
            double v = r + gamma * value(sim, depth - 1);
            if (v > best) { best = v; bestA = a; }
        }
        return bestA;
    }

    /** Valeur d'un etat avec profondeur restante d (max sur les actions). */
    private double value(PacmanEnv env, int d) {
        if (env.isDone()) return 0.0;
        List<Integer> legal = env.legalActions();
        if (d == 0) return q.maxQ(env, legal);
        double best = Double.NEGATIVE_INFINITY;
        for (int a : legal) {
            PacmanEnv sim = env.copy(0);
            double r = sim.stepDet(a);
            best = Math.max(best, r + gamma * value(sim, d - 1));
        }
        return best;
    }
}
