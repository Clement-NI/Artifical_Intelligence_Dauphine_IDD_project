package rl;

/**
 * Copie immuable d'un état {@link PacmanEnv} à un instant donné.
 *
 * {@code PacmanEnv} étant mutable (il avance "en place" à chaque pas), la mise
 * à jour TD a besoin de figer l'état de départ (s) avant d'appeler
 * {@code step()} qui produit (s'). {@code Snapshot} fournit cette photo en ne
 * copiant que ce dont {@link Features} a besoin.
 */
public final class Snapshot implements GameView {

    private final int rows, cols;
    private final boolean[][] wall;
    private final boolean[][] food;
    private final boolean[][] capsule;
    private final int pacR, pacC;
    private final int[] ghR, ghC;
    private final boolean[] scared;

    private Snapshot(PacmanEnv e) {
        this.rows = e.rows();
        this.cols = e.cols();
        this.wall = new boolean[rows][cols];
        this.food = new boolean[rows][cols];
        this.capsule = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                wall[r][c] = e.isWall(r, c);
                food[r][c] = e.hasFood(r, c);
                capsule[r][c] = e.hasCapsule(r, c);
            }
        }
        this.pacR = e.pacR();
        this.pacC = e.pacC();
        int g = e.numGhosts();
        this.ghR = new int[g];
        this.ghC = new int[g];
        this.scared = new boolean[g];
        for (int i = 0; i < g; i++) {
            ghR[i] = e.ghostR(i);
            ghC[i] = e.ghostC(i);
            scared[i] = e.ghostScared(i);
        }
    }

    public static Snapshot of(PacmanEnv e) {
        return new Snapshot(e);
    }

    @Override public int rows() { return rows; }
    @Override public int cols() { return cols; }
    @Override public boolean isWall(int r, int c) {
        if (r < 0 || c < 0 || r >= rows || c >= cols) return true;
        return wall[r][c];
    }
    @Override public boolean hasFood(int r, int c) {
        if (r < 0 || c < 0 || r >= rows || c >= cols) return false;
        return food[r][c];
    }
    @Override public boolean hasCapsule(int r, int c) {
        if (r < 0 || c < 0 || r >= rows || c >= cols) return false;
        return capsule[r][c];
    }
    @Override public int pacR() { return pacR; }
    @Override public int pacC() { return pacC; }
    @Override public int numGhosts() { return ghR.length; }
    @Override public int ghostR(int i) { return ghR[i]; }
    @Override public int ghostC(int i) { return ghC[i]; }
    @Override public boolean ghostScared(int i) { return scared[i]; }
}
