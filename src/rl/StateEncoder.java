package rl;

import java.util.ArrayDeque;

/**
 * Encodeur d'ETAT pour le DQN : transforme une vue de jeu en un vecteur de
 * caracteristiques RICHE et independant de l'action (le reseau sort lui-meme
 * les 4 Q-valeurs).
 *
 * Le diagnostic a montre que 100% des defaites sont des collisions, et que le
 * modele lineaire echoue surtout face a un ENCERCLEMENT (deux fantomes par des
 * directions differentes). On encode donc explicitement, pour CHAQUE direction
 * (haut/bas/gauche/droite), l'information de danger et d'attractivite, ce qu'un
 * modele lineaire a features locales ne pouvait pas exploiter :
 *
 *   Par direction d (4) :
 *     - mur dans cette direction (0/1)
 *     - distance-couloir BFS au fantome dangereux le plus proche en partant de
 *       la case voisine d, bornee et normalisee (1 = tres proche, 0 = loin/sur)
 *     - distance-couloir BFS a la gomme la plus proche depuis la case d (norm.)
 *     - distance-couloir BFS au fantome APEURE le plus proche depuis d (norm.)
 *   Global :
 *     - un fantome dangereux est-il a <=2 pas (0/1)
 *     - nombre de fantomes dangereux a <=4 pas (norm.)
 *     - reste-t-il des super gommes (0/1)
 *     - fraction de gommes restantes
 *
 * Dimension = 4*4 + 4 = 20.
 */
public final class StateEncoder {

    static final int[] DR = {-1, 1, 0, 0};
    static final int[] DC = {0, 0, -1, 1};
    static final int DANGER_CAP = 8;   // portee max consideree pour le danger
    static final int FOOD_CAP = 30;    // portee max consideree pour la nourriture

    public static final int SIZE = 4 * 4 + 4; // 20

    private StateEncoder() {}

    public static double[] encode(GameView v) {
        double[] f = new double[SIZE];
        int pr = v.pacR(), pc = v.pacC();
        int idx = 0;

        for (int d = 0; d < 4; d++) {
            int nr = pr + DR[d], nc = pc + DC[d];
            boolean wall = v.isWall(nr, nc);
            f[idx++] = wall ? 1.0 : 0.0;
            if (wall) {
                // case impraticable : danger nul, nourriture/effraye "infiniment loin"
                f[idx++] = 0.0;
                f[idx++] = 0.0;
                f[idx++] = 0.0;
            } else {
                int dg = bfsToGhost(v, nr, nc, false, DANGER_CAP);
                f[idx++] = dg < 0 ? 0.0 : (double)(DANGER_CAP - dg) / DANGER_CAP; // proche->grand
                int df = bfsToFood(v, nr, nc, FOOD_CAP);
                f[idx++] = df < 0 ? 0.0 : (double)(FOOD_CAP - df) / FOOD_CAP;      // proche->grand
                int ds = bfsToGhost(v, nr, nc, true, DANGER_CAP);
                f[idx++] = ds < 0 ? 0.0 : (double)(DANGER_CAP - ds) / DANGER_CAP;  // effraye proche->grand
            }
        }

        // Global
        int near2 = 0, near4 = 0;
        for (int g = 0; g < v.numGhosts(); g++) {
            if (v.ghostScared(g)) continue;
            int gr = v.ghostR(g), gc = v.ghostC(g);
            if (gr < 0) continue;
            int man = Math.abs(gr - pr) + Math.abs(gc - pc);
            if (man <= 2) near2 = 1;
            if (man <= 4) near4++;
        }
        f[idx++] = near2;
        f[idx++] = Math.min(near4, 4) / 4.0;
        f[idx++] = anyCapsule(v) ? 1.0 : 0.0;
        f[idx++] = foodFraction(v);
        return f;
    }

    // --- BFS utilitaires ------------------------------------------------------

    /** BFS distance-couloir vers le fantome (dangereux ou apeure) le plus proche. */
    private static int bfsToGhost(GameView v, int sr, int sc, boolean scared, int cap) {
        int rows = v.rows(), cols = v.cols();
        boolean[][] seen = new boolean[rows][cols];
        ArrayDeque<int[]> q = new ArrayDeque<>();
        q.add(new int[]{sr, sc, 0});
        seen[sr][sc] = true;
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0], c = cur[1], d = cur[2];
            for (int g = 0; g < v.numGhosts(); g++) {
                if (v.ghostScared(g) == scared && v.ghostR(g) == r && v.ghostC(g) == c
                        && v.ghostR(g) >= 0) {
                    return d;
                }
            }
            if (d >= cap) continue;
            for (int k = 0; k < 4; k++) {
                int rr = r + DR[k], cc = c + DC[k];
                if (rr < 0 || cc < 0 || rr >= rows || cc >= cols) continue;
                if (seen[rr][cc] || v.isWall(rr, cc)) continue;
                seen[rr][cc] = true;
                q.add(new int[]{rr, cc, d + 1});
            }
        }
        return -1;
    }

    private static int bfsToFood(GameView v, int sr, int sc, int cap) {
        int rows = v.rows(), cols = v.cols();
        boolean[][] seen = new boolean[rows][cols];
        ArrayDeque<int[]> q = new ArrayDeque<>();
        q.add(new int[]{sr, sc, 0});
        seen[sr][sc] = true;
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0], c = cur[1], d = cur[2];
            if (v.hasFood(r, c)) return d;
            if (d >= cap) continue;
            for (int k = 0; k < 4; k++) {
                int rr = r + DR[k], cc = c + DC[k];
                if (rr < 0 || cc < 0 || rr >= rows || cc >= cols) continue;
                if (seen[rr][cc] || v.isWall(rr, cc)) continue;
                seen[rr][cc] = true;
                q.add(new int[]{rr, cc, d + 1});
            }
        }
        return -1;
    }

    private static boolean anyCapsule(GameView v) {
        // Une "capsule" (super gomme) : gomme isolee (heuristique, cf. Features).
        for (int r = 0; r < v.rows(); r++)
            for (int c = 0; c < v.cols(); c++)
                if (v.hasFood(r, c)) {
                    boolean iso = true;
                    for (int k = 0; k < 4; k++) if (v.hasFood(r + DR[k], c + DC[k])) iso = false;
                    if (iso) return true;
                }
        return false;
    }

    private static double foodFraction(GameView v) {
        int n = 0;
        for (int r = 0; r < v.rows(); r++)
            for (int c = 0; c < v.cols(); c++)
                if (v.hasFood(r, c)) n++;
        // normalisation grossiere par la taille de la grille
        return Math.min(1.0, n / (double)(v.rows() * v.cols()));
    }
}
