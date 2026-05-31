package rl;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests déterministes de la politique apprise (sans interface graphique).
 *
 * Ils valident le MÊME chemin de décision que celui utilisé en jeu par
 * {@code logic.QLearningAI} : {@link Features#compute} + {@link QLearningAgent}.
 * On charge les poids appris (doc/qweights.txt) ou, à défaut, des poids appris
 * par défaut, puis on vérifie sur des situations construites à la main que
 * l'agent prend la décision attendue :
 *
 *   T1 : un fantôme dangereux adjacent  -> l'agent s'en éloigne.
 *   T2 : une gomme adjacente, aucun danger -> l'agent va la manger.
 *   T3 : un fantôme apeuré tout proche  -> l'agent se rapproche pour le manger.
 *
 * Code de sortie 0 si tous les tests passent, 1 sinon (utilisable en CI).
 */
public class RLTest {

    static final double[] DEFAULT_WEIGHTS = {87.6060, -496.5099, 48.1285, -21.2270, 62.4680, -108.3977, 0.0, 0.0, 0.0};

    public static void main(String[] args) {
        double[] w;
        try {
            w = new java.io.File("doc/qweights.txt").exists()
                    ? QLearningAgent.load("doc/qweights.txt")
                    : DEFAULT_WEIGHTS;
        } catch (Exception e) {
            w = DEFAULT_WEIGHTS;
        }
        QLearningAgent agent = new QLearningAgent(w);

        int failures = 0;
        failures += check("T1 - fuite face a un fantome dangereux", testAvoidGhost(agent));
        failures += check("T2 - capture d'une gomme proche", testEatFood(agent));
        failures += check("T3 - poursuite d'un fantome apeure", testChaseScared(agent));

        System.out.println(failures == 0
                ? "\n[OK] Tous les tests d'integration RL passent."
                : "\n[ECHEC] " + failures + " test(s) en echec.");
        if (failures != 0) System.exit(1);
    }

    // T1 : Pac-Man en (2,2). Fantôme dangereux en (2,3) -> ne doit PAS aller à droite.
    private static boolean testAvoidGhost(QLearningAgent agent) {
        String[] grid = {
            "#####",
            "#...#",
            "#...#",
            "#...#",
            "#####",
        };
        TestView v = new TestView(grid, 2, 2);
        v.addGhost(2, 3, false);   // dangereux, à droite
        int a = agent.greedyAction(v, legal(v));
        System.out.println("   action choisie = " + name(a) + " (RIGHT=danger)");
        return a != 3; // ne va pas dans le fantôme
    }

    // T2 : Pac-Man en (1,1). Gomme en (1,2) à droite, aucun fantôme -> va à droite.
    private static boolean testEatFood(QLearningAgent agent) {
        String[] grid = {
            "#####",
            "#.P.#",   // gomme à gauche et à droite ; on en met une seule
            "#####",
        };
        // Construit explicitement : gomme uniquement à droite.
        TestView v = new TestView(new String[]{
            "####",
            "#P.#",
            "####",
        }, 1, 1);
        int a = agent.greedyAction(v, legal(v));
        System.out.println("   action choisie = " + name(a) + " (attendu RIGHT vers la gomme)");
        return a == 3;
    }

    // T3 : Pac-Man en (2,1). Fantôme APEURE en (2,3) -> se rapproche (va à droite).
    private static boolean testChaseScared(QLearningAgent agent) {
        String[] grid = {
            "#####",
            "#####",
            "#...#",   // couloir, pas de gomme
            "#####",
            "#####",
        };
        // couloir sans gomme pour isoler l'effet "fantôme apeuré"
        TestView v = new TestView(new String[]{
            "#####",
            "#OOO#",
            "#####",
        }, 1, 1);
        v.addGhost(1, 3, true);  // apeuré, à droite
        int a = agent.greedyAction(v, legal(v));
        System.out.println("   action choisie = " + name(a) + " (attendu RIGHT vers le fantome apeure)");
        return a == 3;
    }

    // --- utilitaires ----------------------------------------------------------

    private static List<Integer> legal(GameView v) {
        List<Integer> l = new ArrayList<>();
        for (int a = 0; a < 4; a++) {
            int nr = v.pacR() + Features.DR[a];
            int nc = v.pacC() + Features.DC[a];
            if (!v.isWall(nr, nc)) l.add(a);
        }
        if (l.isEmpty()) l.add(0);
        return l;
    }

    private static String name(int a) {
        return new String[]{"UP", "DOWN", "LEFT", "RIGHT"}[a];
    }

    private static int check(String label, boolean ok) {
        System.out.println((ok ? "[OK]   " : "[ECHEC]") + " " + label);
        return ok ? 0 : 1;
    }

    /** Vue de jeu construite à la main pour les tests ('#' mur, '.'/'*' gomme, autres = vide). */
    static final class TestView implements GameView {
        private final char[][] map;
        private final int rows, cols, pr, pc;
        private final List<int[]> ghosts = new ArrayList<>(); // {r, c, scared}

        TestView(String[] grid, int pacR, int pacC) {
            this.rows = grid.length;
            this.cols = grid[0].length();
            this.map = new char[rows][cols];
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    map[r][c] = grid[r].charAt(c);
            this.pr = pacR;
            this.pc = pacC;
        }

        void addGhost(int r, int c, boolean scared) {
            ghosts.add(new int[]{r, c, scared ? 1 : 0});
        }

        @Override public int rows() { return rows; }
        @Override public int cols() { return cols; }
        @Override public boolean isWall(int r, int c) {
            if (r < 0 || c < 0 || r >= rows || c >= cols) return true;
            return map[r][c] == '#';
        }
        @Override public boolean hasFood(int r, int c) {
            if (r < 0 || c < 0 || r >= rows || c >= cols) return false;
            return map[r][c] == '.' || map[r][c] == '*';
        }
        @Override public int pacR() { return pr; }
        @Override public int pacC() { return pc; }
        @Override public int numGhosts() { return ghosts.size(); }
        @Override public int ghostR(int i) { return ghosts.get(i)[0]; }
        @Override public int ghostC(int i) { return ghosts.get(i)[1]; }
        @Override public boolean ghostScared(int i) { return ghosts.get(i)[2] == 1; }
    }
}
