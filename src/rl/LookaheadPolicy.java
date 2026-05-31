package rl;

import java.util.ArrayDeque;
import java.util.Deque;
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

    // --- Anti-boucle : memoire des cases recemment visitees -------------------
    /** Nombre de cases recentes memorisees. */
    private static final int HISTORY = 12;
    /** Penalite (en unites de Q) pour une action menant sur une case recente. */
    private static final double LOOP_PENALTY = 30.0;
    private final Deque<Long> recent = new ArrayDeque<>();

    public LookaheadPolicy(QLearningAgent q, int depth, double gamma) {
        this.q = q;
        this.depth = Math.max(1, depth);
        this.gamma = gamma;
    }

    /** Reinitialise la memoire anti-boucle (a appeler en debut de partie/niveau). */
    public void resetHistory() {
        recent.clear();
    }

    private static long key(int r, int c) {
        return (((long) r) << 20) | (c & 0xFFFFF);
    }

    /**
     * Action choisie depuis l'etat reel (non modifie : on travaille sur des
     * copies). Au-dela du retour estime par la recherche, on PENALISE les actions
     * qui ramenent sur une case visitee tres recemment : cela casse les
     * allers-retours / tours en rond sterile, SANS empecher de repasser sur une
     * case lointaine dans le temps (la memoire est courte).
     */
    public int choose(PacmanEnv env) {
        List<Integer> legal = env.legalActions();
        double best = Double.NEGATIVE_INFINITY;
        int bestA = legal.get(0);
        for (int a : legal) {
            PacmanEnv sim = env.copy(0);          // graine ignoree (stepDet est deterministe)
            double r = sim.stepDet(a);
            double v = (depth == 1)
                    ? q.qValue(env, a)            // profondeur 1 = valeur gloutonne
                    : r + gamma * value(sim, depth - 1);
            // malus anti-boucle : la case d'arrivee a-t-elle ete vue recemment ?
            if (recent.contains(key(sim.pacR(), sim.pacC()))) {
                v -= LOOP_PENALTY;
            }
            if (v > best) { best = v; bestA = a; }
        }
        // memorise la case ou l'on arrive effectivement
        PacmanEnv after = env.copy(0);
        after.stepDet(bestA);
        recent.addLast(key(after.pacR(), after.pacC()));
        while (recent.size() > HISTORY) recent.removeFirst();
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
