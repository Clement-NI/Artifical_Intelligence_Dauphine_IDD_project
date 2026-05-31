package rl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simulateur Pac-Man "headless" (sans interface graphique) servant
 * d'environnement d'apprentissage par renforcement.
 *
 * Il reprend fidèlement les règles du jeu du projet :
 *  - murs '#', gommes '.', super gommes '*' ;
 *  - score : +10 par gomme, +50 par super gomme, +200 par fantôme apeuré mangé ;
 *  - une super gomme rend les fantômes apeurés (comestibles) pendant un temps ;
 *  - collision avec un fantôme non apeuré = mort.
 *
 * Récompense RL (reward shaping classique de Berkeley) :
 *  - -1 par pas de temps (incite à finir vite) ;
 *  - + variation de score (gommes / fantômes) ;
 *  - +500 si le niveau est gagné (toutes les gommes mangées) ;
 *  - -500 en cas de mort.
 *
 * L'état est totalement observable et exposé via {@link GameView}, ce qui
 * permet de réutiliser exactement le même calcul de features que dans le vrai
 * jeu (voir {@code logic.QLearningAI}).
 */
public class PacmanEnv implements GameView {

    public static final int SCORE_GOMME = 10;
    public static final int SCORE_SUPER = 50;
    public static final int SCORE_GHOST = 200;
    public static final int SCARE_TIME = 18;
    public static final int WIN_REWARD = 500;
    public static final int LOSE_REWARD = -500;
    public static final double GHOST_CHASE_PROB = 0.8;

    private final char[][] layout;     // mise en page d'origine (murs, gommes)
    private final int rows, cols;
    private final int pacSpawnR, pacSpawnC;
    private final int[][] ghostSpawns; // {r, c} par fantôme

    // État courant
    private boolean[][] food;
    private boolean[][] power;
    private int pacR, pacC;
    private int[] ghR, ghC;
    private int[] scared;              // tics d'apeurement restants par fantôme
    private int totalFood;
    private int foodLeft;
    private int score;
    private int steps;
    private int maxSteps;
    private boolean done;

    private final Random rng;

    public PacmanEnv(String[] map, long seed) {
        this.rng = new Random(seed);
        this.rows = map.length;
        this.cols = map[0].length();
        this.layout = new char[rows][cols];

        int pr = 1, pc = 1, tf = 0;
        List<int[]> ghosts = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char ch = map[r].charAt(c);
                layout[r][c] = ch;
                if (ch == '.' || ch == '*') tf++;
                if (ch == 'P') { pr = r; pc = c; }
                if (ch == 'G') ghosts.add(new int[]{r, c});
            }
        }
        this.pacSpawnR = pr;
        this.pacSpawnC = pc;
        this.totalFood = tf;
        this.ghostSpawns = ghosts.toArray(new int[0][]);
        this.maxSteps = rows * cols * 4;
    }

    public void setMaxSteps(int m) { this.maxSteps = m; }

    /** Réinitialise l'environnement et retourne l'état initial (this). */
    public PacmanEnv reset() {
        food = new boolean[rows][cols];
        power = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char ch = layout[r][c];
                food[r][c] = (ch == '.' || ch == '*');
                power[r][c] = (ch == '*');
            }
        }
        foodLeft = totalFood;
        pacR = pacSpawnR;
        pacC = pacSpawnC;
        ghR = new int[ghostSpawns.length];
        ghC = new int[ghostSpawns.length];
        scared = new int[ghostSpawns.length];
        for (int i = 0; i < ghostSpawns.length; i++) {
            ghR[i] = ghostSpawns[i][0];
            ghC[i] = ghostSpawns[i][1];
            scared[i] = 0;
        }
        score = 0;
        steps = 0;
        done = false;
        return this;
    }

    public boolean isDone() { return done; }
    public int getScore() { return score; }
    public int getSteps() { return steps; }
    public boolean won() { return foodLeft == 0; }

    /** Actions légales depuis la position courante de Pac-Man. */
    public List<Integer> legalActions() {
        List<Integer> legal = new ArrayList<>(4);
        for (int a = 0; a < 4; a++) {
            int nr = pacR + Features.DR[a];
            int nc = pacC + Features.DC[a];
            if (!isWall(nr, nc)) legal.add(a);
        }
        if (legal.isEmpty()) legal.add(0); // sécurité : ne devrait pas arriver
        return legal;
    }

    /**
     * Applique une action et fait avancer la simulation d'un pas.
     * @return la récompense RL du pas.
     */
    public double step(int action) {
        if (done) return 0.0;
        steps++;
        double reward = -1.0; // coût du temps

        // 1) Déplacement de Pac-Man
        int nr = pacR + Features.DR[action];
        int nc = pacC + Features.DC[action];
        if (!isWall(nr, nc)) {
            pacR = nr;
            pacC = nc;
        }

        // 2) Gomme éventuelle
        if (food[pacR][pacC]) {
            food[pacR][pacC] = false;
            foodLeft--;
            if (power[pacR][pacC]) {
                reward += SCORE_SUPER;
                score += SCORE_SUPER;
                power[pacR][pacC] = false;
                for (int i = 0; i < scared.length; i++) scared[i] = SCARE_TIME;
            } else {
                reward += SCORE_GOMME;
                score += SCORE_GOMME;
            }
        }

        // 3) Collision après le déplacement de Pac-Man
        reward += resolveCollisions();
        if (done) return reward;

        // 4) Victoire ?
        if (foodLeft == 0) {
            reward += WIN_REWARD;
            score += WIN_REWARD;
            done = true;
            return reward;
        }

        // 5) Déplacement des fantômes
        moveGhosts();
        for (int i = 0; i < scared.length; i++) {
            if (scared[i] > 0) scared[i]--;
        }

        // 6) Collision après le déplacement des fantômes
        reward += resolveCollisions();
        if (done) return reward;

        if (steps >= maxSteps) {
            done = true; // épisode tronqué
        }
        return reward;
    }

    /** Gère les collisions Pac-Man / fantômes ; renvoie la récompense associée. */
    private double resolveCollisions() {
        double reward = 0.0;
        for (int i = 0; i < ghR.length; i++) {
            if (ghR[i] == pacR && ghC[i] == pacC) {
                if (scared[i] > 0) {
                    reward += SCORE_GHOST;
                    score += SCORE_GHOST;
                    ghR[i] = ghostSpawns[i][0];
                    ghC[i] = ghostSpawns[i][1];
                    scared[i] = 0;
                } else {
                    reward += LOSE_REWARD;
                    done = true;
                    return reward;
                }
            }
        }
        return reward;
    }

    /** Déplace chaque fantôme : poursuite gloutonne (apeuré -> fuite). */
    private void moveGhosts() {
        for (int i = 0; i < ghR.length; i++) {
            List<int[]> moves = new ArrayList<>();
            for (int a = 0; a < 4; a++) {
                int nr = ghR[i] + Features.DR[a];
                int nc = ghC[i] + Features.DC[a];
                if (!isWall(nr, nc)) moves.add(new int[]{nr, nc});
            }
            if (moves.isEmpty()) continue;

            int[] target;
            boolean chase = rng.nextDouble() < GHOST_CHASE_PROB;
            if (!chase) {
                target = moves.get(rng.nextInt(moves.size()));
            } else {
                // distance de Manhattan à Pac-Man : minimiser (poursuite) ou
                // maximiser (fuite si apeuré).
                boolean flee = scared[i] > 0;
                int bestVal = flee ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                target = moves.get(0);
                for (int[] m : moves) {
                    int d = Math.abs(m[0] - pacR) + Math.abs(m[1] - pacC);
                    if (flee ? d > bestVal : d < bestVal) {
                        bestVal = d;
                        target = m;
                    }
                }
            }
            ghR[i] = target[0];
            ghC[i] = target[1];
        }
    }

    // --- Implémentation de GameView -------------------------------------------

    @Override public int rows() { return rows; }
    @Override public int cols() { return cols; }

    @Override public boolean isWall(int r, int c) {
        if (r < 0 || c < 0 || r >= rows || c >= cols) return true;
        return layout[r][c] == '#';
    }

    @Override public boolean hasFood(int r, int c) {
        if (r < 0 || c < 0 || r >= rows || c >= cols) return false;
        return food[r][c];
    }

    @Override public int pacR() { return pacR; }
    @Override public int pacC() { return pacC; }
    @Override public int numGhosts() { return ghR.length; }
    @Override public int ghostR(int i) { return ghR[i]; }
    @Override public int ghostC(int i) { return ghC[i]; }
    @Override public boolean ghostScared(int i) { return scared[i] > 0; }

    // --- Cartes intégrées (reproductibles, sans dépendance fichier) -----------

    /** Petite carte utilisée pour des tests rapides et déterministes. */
    public static final String[] SMALL_MAP = {
        "#########",
        "#..*.*..#",
        "#.#.#.#.#",
        "#...G...#",
        "#.#.#.#.#",
        "#...P...#",
        "#.#.#.#.#",
        "#..*.*..#",
        "#########",
    };

    /** Carte moyenne avec deux fantômes, plus représentative. */
    public static final String[] MEDIUM_MAP = {
        "###########",
        "#....*....#",
        "#.###.###.#",
        "#.#.....#.#",
        "#.#.###.#.#",
        "#...#G#...#",
        "#.#.###.#.#",
        "#.#..P..#.#",
        "#.###.###.#",
        "#G..*.*..G#",
        "###########",
    };
}
