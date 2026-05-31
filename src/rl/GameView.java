package rl;

/**
 * Vue en lecture seule d'un état de jeu Pac-Man, totalement observable.
 *
 * Cette interface est l'unique dépendance de {@link Features}. Elle est
 * implémentée :
 *  - par {@link PacmanEnv} pour l'entraînement (simulateur headless), et
 *  - par l'adaptateur dans {@code logic.QLearningAI} qui lit un
 *    {@code BeliefState} du vrai jeu.
 *
 * Grâce à cette abstraction, EXACTEMENT le même calcul de features (donc les
 * mêmes poids appris) est utilisé à l'entraînement et en jeu.
 *
 * Convention de grille : x = ligne (row), y = colonne (column).
 * Actions : 0=UP (ligne-1), 1=DOWN (ligne+1), 2=LEFT (col-1), 3=RIGHT (col+1).
 */
public interface GameView {
    int rows();
    int cols();

    /** true si la case (r,c) est un mur (ou hors grille). */
    boolean isWall(int r, int c);

    /** true s'il reste une gomme (normale ou super) sur la case (r,c). */
    boolean hasFood(int r, int c);

    /**
     * true si la case (r,c) porte une SUPER gomme (capsule) encore presente.
     * Par defaut false : les implementations qui distinguent les super gommes
     * (simulateur, vrai jeu) le surchargent.
     */
    default boolean hasCapsule(int r, int c) { return false; }

    int pacR();
    int pacC();

    int numGhosts();
    int ghostR(int i);
    int ghostC(int i);

    /** true si le fantôme i est "apeuré" (comestible) suite à une super gomme. */
    boolean ghostScared(int i);
}
