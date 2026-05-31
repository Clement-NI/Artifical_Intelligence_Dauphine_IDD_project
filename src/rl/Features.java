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
 * Les features sont normalisées (~[0,1]) pour que la descente de gradient TD
 * soit stable et que les poids appris restent interprétables.
 *
 * Au-delà des features de base (éviter les fantômes, viser la gomme la plus
 * proche), trois features "stratégiques" ont été ajoutées pour relever le taux
 * de victoire, car le mode d'échec dominant est de se faire acculer en allant
 * chercher les dernières gommes :
 *   - distance GRADUÉE (BFS) au fantôme dangereux le plus proche : alerte plus
 *     tôt qu'un simple test "à un pas" ;
 *   - nombre d'issues de la case d'arrivée (détection de cul-de-sac) : évite
 *     d'entrer dans un couloir sans échappatoire quand un fantôme est proche ;
 *   - capture d'une super gomme quand un fantôme dangereux est proche : permet
 *     de renverser la situation.
 */
public final class Features {

    /** Noms des features, dans l'ordre du vecteur retourné. */
    public static final String[] NAMES = {
        "bias",
        "#-ghosts-1-step-away",
        "eats-food",
        "closest-food",
        "closest-scared-ghost",
        "danger-ghost-proximity",   // 1/(1+BFS dist au fantôme dangereux le plus proche)
        "dead-end-near-ghost",      // case d'arrivée à faible nombre d'issues ET fantôme proche
        "eats-capsule-when-hunted", // mange une super gomme avec un fantôme dangereux proche
    };

    public static final int N = NAMES.length;

    // Déplacements (drow, dcol) pour UP, DOWN, LEFT, RIGHT.
    static final int[] DR = {-1, 1, 0, 0};
    static final int[] DC = {0, 0, -1, 1};

    /** Distance (Manhattan) en deçà de laquelle un fantôme dangereux est "proche". */
    private static final int DANGER_RADIUS = 4;

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

        // Parcours des fantômes : compte les dangereux à un pas, repère le
        // fantôme apeuré le plus proche (Manhattan).
        int ghostsNear = 0;
        double closestScared = -1;
        boolean dangerWithinRadius = false;
        for (int g = 0; g < v.numGhosts(); g++) {
            int gr = v.ghostR(g), gc = v.ghostC(g);
            if (gr < 0 || gc < 0) continue; // fantôme de position inconnue
            int d = Math.abs(gr - nr) + Math.abs(gc - nc);
            if (v.ghostScared(g)) {
                if (closestScared < 0 || d < closestScared) closestScared = d;
            } else {
                if (d <= 1) ghostsNear++;
                if (d <= DANGER_RADIUS) dangerWithinRadius = true;
            }
        }
        f[1] = ghostsNear;

        // f2 : mange une gomme (seulement si aucun fantôme dangereux à un pas).
        boolean eats = v.hasFood(nr, nc);
        if (ghostsNear == 0 && eats) {
            f[2] = 1.0;
        }

        // f3 : distance (BFS) à la gomme la plus proche, normalisée.
        int dist = closestFoodDistance(v, nr, nc);
        if (dist >= 0) {
            f[3] = ((double) dist) / (rows * cols);
        }

        // f4 : proximité d'un fantôme apeuré (incite à le manger), normalisée.
        if (closestScared >= 0) {
            f[4] = 1.0 / (1.0 + closestScared);
        }

        // f5 : proximité GRADUÉE (BFS) du fantôme dangereux le plus proche.
        //      Plus il est près, plus la valeur est grande (donc un poids négatif
        //      appris pénalise les cases trop exposées, bien avant le contact).
        int dGhost = closestDangerousGhostDistance(v, nr, nc);
        if (dGhost >= 0) {
            f[5] = 1.0 / (1.0 + dGhost);
        }

        // f6 : VRAI cul-de-sac dangereux. On ne déclenche que pour une impasse
        //      réelle (au plus une issue praticable), et seulement si un fantôme
        //      dangereux est dans le rayon. Important : dans un labyrinthe Pac-Man
        //      la plupart des cases sont des couloirs à 2 issues ; il ne faut donc
        //      PAS les pénaliser, sous peine de paralyser l'agent.
        if (dangerWithinRadius && countLiberties(v, nr, nc) <= 1) {
            f[6] = 1.0;
        }

        // f7 : mange une super gomme alors qu'un fantôme dangereux est proche
        //      (retournement de situation : les fantômes deviennent comestibles).
        if (eats && isCapsule(v, nr, nc) && dangerWithinRadius) {
            f[7] = 1.0;
        }

        return f;
    }

    // --- Aides ----------------------------------------------------------------

    /** Nombre de cases adjacentes praticables (non-murs) : "issues" de la case. */
    private static int countLiberties(GameView v, int r, int c) {
        int n = 0;
        for (int k = 0; k < 4; k++) {
            if (!v.isWall(r + DR[k], c + DC[k])) n++;
        }
        return n;
    }

    /**
     * Heuristique pour distinguer une super gomme d'une gomme normale via
     * {@link GameView}. L'interface n'expose pas le type, mais les super gommes
     * du jeu sont peu nombreuses ; on considère qu'une gomme est une "capsule"
     * si elle est isolée (aucune gomme adjacente), ce qui est le cas des super
     * gommes placées dans les coins/recoins des cartes du projet.
     *
     * Note : cette feature reste utile même approximative — un mauvais étiquetage
     * occasionnel est absorbé par l'apprentissage du poids associé.
     */
    private static boolean isCapsule(GameView v, int r, int c) {
        if (!v.hasFood(r, c)) return false;
        for (int k = 0; k < 4; k++) {
            if (v.hasFood(r + DR[k], c + DC[k])) return false;
        }
        return true;
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

    /**
     * BFS depuis (sr,sc) jusqu'au fantôme dangereux (non apeuré) le plus proche,
     * en distance de couloirs (tient compte des murs).
     * @return la distance, ou -1 s'il n'y a aucun fantôme dangereux atteignable.
     */
    private static int closestDangerousGhostDistance(GameView v, int sr, int sc) {
        int rows = v.rows(), cols = v.cols();
        // Positions des fantômes dangereux.
        boolean any = false;
        boolean[][] target = new boolean[rows][cols];
        for (int g = 0; g < v.numGhosts(); g++) {
            if (v.ghostScared(g)) continue;
            int gr = v.ghostR(g), gc = v.ghostC(g);
            if (gr < 0 || gc < 0 || gr >= rows || gc >= cols) continue;
            target[gr][gc] = true;
            any = true;
        }
        if (!any) return -1;

        boolean[][] seen = new boolean[rows][cols];
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{sr, sc, 0});
        seen[sr][sc] = true;
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1], d = cur[2];
            if (target[r][c]) return d;
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
