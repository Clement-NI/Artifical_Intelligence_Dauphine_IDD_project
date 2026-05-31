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

    /** Poids appris par défaut (cf. sortie de rl.RLTrainer), dans l'ordre de Features.NAMES. */
    private static final double[] DEFAULT_WEIGHTS = {
        23.4913,    // bias
        -418.0380,  // #-ghosts-1-step-away
        53.4116,    // eats-food
        -28.6930,   // closest-food
        67.0589,    // closest-scared-ghost
    };

    private static final String WEIGHTS_PATH = "doc/qweights.txt";

    private static QLearningAgent agent;

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
        }
        return agent;
    }

    /**
     * Choisit le prochain mouvement de Pac-Man via la politique RL apprise.
     *
     * @param beliefState l'état de jeu observé
     * @return l'une des constantes {@code PacManLauncher.UP/DOWN/LEFT/RIGHT}
     */
    public static String findNextMove(BeliefState beliefState) {
        GameView view = new BeliefStateView(beliefState);
        List<Integer> legal = legalActions(view);
        int a = agent().greedyAction(view, legal);
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

        @Override public int pacR() { return pr; }
        @Override public int pacC() { return pc; }
        @Override public int numGhosts() { return gr.length; }
        @Override public int ghostR(int i) { return gr[i]; }
        @Override public int ghostC(int i) { return gc[i]; }
        @Override public boolean ghostScared(int i) { return scared[i]; }
    }
}
