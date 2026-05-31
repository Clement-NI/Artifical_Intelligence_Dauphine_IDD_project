package rl;

import java.util.ArrayDeque;

/**
 * Extracteur de caractéristiques (features) pour l'apprentissage par
 * renforcement approché (approximate Q-learning), au sens du projet Pac-Man
 * de Berkeley (CS188).
 *
 * On apprend une fonction de valeur d'action linéaire :
 *
 *      Q(s, a) = somme_i  w_i * f_i(s, a)
 *
 * où les f_i sont calculées en regardant la case sur laquelle Pac-Man
 * arriverait après l'action a (simulation d'un pas de Pac-Man uniquement).
 *
 * Les features sont volontairement peu nombreuses et normalisées (~[0,1]) pour
 * que l'apprentissage par descente de gradient TD soit stable et que les poids
 * appris soient interprétables.
 */
public final class Features {

    /** Noms des features, dans l'ordre du vecteur retourné. */
    public static final String[] NAMES = {
        "bias",
        "#-ghosts-1-step-away",
        "eats-food",
        "closest-food",
        "closest-scared-ghost",
    };

    public static final int N = NAMES.length;

    // Déplacements (drow, dcol) pour UP, DOWN, LEFT, RIGHT.
    static final int[] DR = {-1, 1, 0, 0};
    static final int[] DC = {0, 0, -1, 1};

    private Features() {}

    /**
     * Calcule le vecteur de features f(s, a).
     *
     * @param v   l'état courant (s)
     * @param action  l'action a (0..3)
     * @return un vecteur de dimension {@link #N}
     */
    public static double[] compute(GameView v, int action) {
        double[] f = new double[N];

        int rows = v.rows();
        int cols = v.cols();

        // Case sur laquelle Pac-Man arrive après l'action (sinon il reste sur place).
        int nr = v.pacR() + DR[action];
        int nc = v.pacC() + DC[action];
        if (v.isWall(nr, nc)) {
            nr = v.pacR();
            nc = v.pacC();
        }

        // f0 : biais
        f[0] = 1.0;

        // f1 : nombre de fantômes NON apeurés à un pas de la case d'arrivée.
        int ghostsNear = 0;
        double closestScared = -1;
        for (int g = 0; g < v.numGhosts(); g++) {
            int gr = v.ghostR(g), gc = v.ghostC(g);
            int d = Math.abs(gr - nr) + Math.abs(gc - nc);
            if (v.ghostScared(g)) {
                if (closestScared < 0 || d < closestScared) closestScared = d;
            } else if (d <= 1) {
                ghostsNear++;
            }
        }
        f[1] = ghostsNear;

        // f2 : mange une gomme (seulement si aucun fantôme dangereux à proximité).
        if (ghostsNear == 0 && v.hasFood(nr, nc)) {
            f[2] = 1.0;
        }

        // f3 : distance (BFS sur les couloirs) à la gomme la plus proche, normalisée.
        int dist = closestFoodDistance(v, nr, nc);
        if (dist >= 0) {
            f[3] = ((double) dist) / (rows * cols);
        }

        // f4 : proximité d'un fantôme apeuré (incite à le manger), normalisée.
        if (closestScared >= 0) {
            f[4] = 1.0 / (1.0 + closestScared);
        }

        return f;
    }

    /**
     * BFS sur les cases non-murs depuis (sr,sc) jusqu'à la gomme la plus proche.
     * @return la distance en cases, ou -1 s'il n'y a plus de gomme.
     */
    private static int closestFoodDistance(GameView v, int sr, int sc) {
        int rows = v.rows(), cols = v.cols();
        boolean[][] seen = new boolean[rows][cols];
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{sr, sc, 0});
        seen[sr][sc] = true;
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1], d = cur[2];
            if (v.hasFood(r, c)) {
                return d;
            }
            for (int k = 0; k < 4; k++) {
                int rr = r + DR[k], cc = c + DC[k];
                if (rr < 0 || cc < 0 || rr >= rows || cc >= cols) continue;
                if (seen[rr][cc] || v.isWall(rr, cc)) continue;
                seen[rr][cc] = true;
                queue.add(new int[]{rr, cc, d + 1});
            }
        }
        return -1;
    }
}
