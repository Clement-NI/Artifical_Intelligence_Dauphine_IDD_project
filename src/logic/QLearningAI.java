package logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import rl.Features;
import rl.GameView;
import rl.QLearningAgent;

/**
 * Intégration au jeu réel de la politique apprise par renforcement.
 *
 * Cette classe est le pont entre :
 *  - le moteur de jeu Pac-Man du projet (qui manipule des {@link BeliefState}), et
 *  - l'agent d'apprentissage par renforcement du paquet {@code rl}
 *    (Q-learning approché, entraîné par {@code rl.RLTrainer}).
 *
 * Elle expose la MÊME signature que {@code AI.findNextMove(BeliefState)}, si bien
 * qu'il suffit de remplacer, dans {@code PacManLauncher.animate()}, l'appel
 * {@code AI.findNextMove(...)} par {@code QLearningAI.findNextMove(...)} pour que
 * ce soit la politique apprise qui pilote Pac-Man.
 *
 * Les poids sont chargés depuis {@code doc/qweights.txt} (produit par
 * l'entraînement) ; à défaut, des poids appris par défaut sont utilisés, de
 * sorte que la classe fonctionne même sans réentraînement.
 */
public class QLearningAI {

    /** Déplacements (drow, dcol) pour UP, DOWN, LEFT, RIGHT. */
    private static final int[] DR = {-1, 1, 0, 0};
    private static final int[] DC = {0, 0, -1, 1};
    private static final String[] DIR = {
        PacManLauncher.UP, PacManLauncher.DOWN, PacManLauncher.LEFT, PacManLauncher.RIGHT
    };

    /** Poids appris par défaut (cf. sortie de rl.RealMapTrainer), dans l'ordre de Features.NAMES. */
    private static final double[] DEFAULT_WEIGHTS = {
        87.6060,    // bias
        -496.5099,  // #-ghosts-1-step-away
        48.1285,    // eats-food
        -21.2270,   // closest-food
        62.4680,    // closest-scared-ghost
        -108.3977,  // #-dangerous-ghosts-2-steps
        0.0,        // eats-capsule (placeholder, remplace apres reentrainement)
        0.0,        // closer-to-capsule-when-hunted (placeholder)
        0.0,        // ghost-safe-distance (placeholder)
    };

    private static final String WEIGHTS_PATH = "doc/qweights.txt";

    /**
     * Profondeur de la recherche a horizon (lookahead). Reglable via
     * -Dpacman.depth=N. Defaut 3 : la valeur verifiee comme la meilleure
     * (~52% de victoires, contre ~43% en gloutonne profondeur 1).
     */
    private static final int DEPTH = Integer.getInteger("pacman.depth", 3);
    private static final double GAMMA = 0.95;

    private static QLearningAgent agent;
    private static rl.LookaheadPolicy policy;

    /** Charge l'agent une seule fois (poids du fichier, sinon poids par défaut). */
    private static synchronized QLearningAgent agent() {
        if (agent == null) {
            double[] w = DEFAULT_WEIGHTS;
            try {
                if (new File(WEIGHTS_PATH).exists()) {
                    w = QLearningAgent.load(WEIGHTS_PATH);
                }
            } catch (Exception e) {
                // en cas de souci de lecture, on garde les poids par défaut
                w = DEFAULT_WEIGHTS;
            }
            agent = new QLearningAgent(w);
            policy = new rl.LookaheadPolicy(agent, DEPTH, GAMMA);
        }
        return agent;
    }

    /**
     * Choisit le prochain mouvement de Pac-Man via la politique RL apprise,
     * AVEC recherche a horizon (lookahead) de profondeur {@link #DEPTH}.
     *
     * On reconstruit un {@link rl.PacmanEnv} a partir de l'etat observe puis on
     * y deroule la recherche deterministe (cf. {@code rl.LookaheadPolicy}). En
     * profondeur 1, cela revient a la politique gloutonne d'origine.
     *
     * @param beliefState l'état de jeu observé
     * @return l'une des constantes {@code PacManLauncher.UP/DOWN/LEFT/RIGHT}
     */
    public static String findNextMove(BeliefState beliefState) {
        agent(); // initialise agent + policy
        GameView view = new BeliefStateView(beliefState);
        int a;
        if (DEPTH <= 1) {
            a = agent.greedyAction(view, legalActions(view));
        } else {
            // La recherche a horizon a besoin d'un modele simulable : on
            // reconstruit l'etat courant dans un PacmanEnv, puis on cherche.
            rl.PacmanEnv sim = new rl.PacmanEnv(view);
            a = policy.choose(sim);
        }
        return DIR[a];
    }

    /** Actions menant sur une case non-mur. */
    private static List<Integer> legalActions(GameView v) {
        List<Integer> legal = new ArrayList<>(4);
        for (int a = 0; a < 4; a++) {
            int nr = v.pacR() + DR[a];
            int nc = v.pacC() + DC[a];
            if (!v.isWall(nr, nc)) legal.add(a);
        }
        if (legal.isEmpty()) legal.add(0); // sécurité
        return legal;
    }

    /**
     * Adaptateur exposant un {@link BeliefState} sous l'interface {@link GameView},
     * afin de réutiliser EXACTEMENT le calcul de features (et donc les poids
     * appris) employé pendant l'entraînement headless.
     *
     * Particularité du vrai jeu : l'état est partiellement observable. Pour
     * chaque fantôme, {@code getGhostPositions} peut contenir plusieurs positions
     * candidates ; on retient ici la plus proche de Pac-Man (hypothèse la plus
     * prudente face au danger).
     */
    private static final class BeliefStateView implements GameView {
        private final char[][] map;
        private final int rows, cols;
        private final int pr, pc;
        private final int[] gr, gc;
        private final boolean[] scared;

        BeliefStateView(BeliefState bs) {
            this.map = bs.getMap();
            this.rows = map.length;
            this.cols = map[0].length;
            Position p = bs.getPacmanPosition();
            this.pr = p.x;
            this.pc = p.y;

            int n = bs.getNbrOfGhost();
            this.gr = new int[n];
            this.gc = new int[n];
            this.scared = new boolean[n];
            for (int i = 0; i < n; i++) {
                TreeSet<Position> cand = bs.getGhostPositions(i);
                int br = -1, bc = -1, bd = Integer.MAX_VALUE;
                if (cand != null) {
                    for (Position g : cand) {
                        int d = Math.abs(g.x - pr) + Math.abs(g.y - pc);
                        if (d < bd) { bd = d; br = g.x; bc = g.y; }
                    }
                }
                this.gr[i] = br;
                this.gc[i] = bc;
                this.scared[i] = bs.getCompteurPeur(i) > 0;
            }
        }

        @Override public int rows() { return rows; }
        @Override public int cols() { return cols; }

        @Override public boolean isWall(int r, int c) {
            if (r < 0 || c < 0 || r >= rows || c >= cols) return true;
            return map[r][c] == '#';
        }

        @Override public boolean hasFood(int r, int c) {
            if (r < 0 || c < 0 || r >= rows || c >= cols) return false;
            char ch = map[r][c];
            return ch == '.' || ch == '*';
        }

        @Override public boolean hasCapsule(int r, int c) {
            if (r < 0 || c < 0 || r >= rows || c >= cols) return false;
            return map[r][c] == '*';
        }

        @Override public int pacR() { return pr; }
        @Override public int pacC() { return pc; }
        @Override public int numGhosts() { return gr.length; }
        @Override public int ghostR(int i) { return gr[i]; }
        @Override public int ghostC(int i) { return gc[i]; }
        @Override public boolean ghostScared(int i) { return scared[i]; }
    }
}
