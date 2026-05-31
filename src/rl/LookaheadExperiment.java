package rl;

import java.util.List;

/**
 * Experience : le modele lineaire appris predit-il bien sur PLUSIEURS pas ?
 *
 * On compare, sur les vraies cartes, le taux de victoire et le score moyen de
 * la politique gloutonne (profondeur 1) a ceux d'une recherche a horizon
 * croissant (profondeur 2, 3, 4) qui utilise la Q-valeur apprise aux feuilles.
 *
 * Interpretation :
 *   - si les performances montent nettement avec la profondeur, c'est que le
 *     modele lineaire seul ne "voit pas assez loin" mais reste un bon evaluateur
 *     local : la prediction multi-pas l'aide ;
 *   - si elles stagnent, soit la Q-valeur encode deja le futur, soit le modele
 *     lineaire est trop pauvre pour qu'anticiper aide.
 *
 * Execution : java -cp out rl.LookaheadExperiment
 */
public class LookaheadExperiment {

    static final double[] DEFAULT_WEIGHTS = {91.6445, -511.0326, 45.5287, -17.1416, 62.8738};
    static final String[] MAPS = {"doc/map1.map", "doc/map2.map", "doc/map3.map"};
    static final int N_GAMES = 80;          // parties par (carte, profondeur)
    static final int SAMPLES = 3;           // tirages Monte-Carlo pour les fantomes
    static final double GAMMA = 0.9;
    static final int[] DEPTHS = {1, 2, 3, 4};

    public static void main(String[] args) {
        double[] w;
        try {
            w = new java.io.File("doc/qweights.txt").exists()
                    ? QLearningAgent.load("doc/qweights.txt") : DEFAULT_WEIGHTS;
        } catch (Exception e) { w = DEFAULT_WEIGHTS; }
        QLearningAgent q = new QLearningAgent(w);

        System.out.println("=== Le modele lineaire predit-il bien sur plusieurs pas ? ===");
        System.out.printf("Recherche a horizon N avec Q-valeur apprise aux feuilles.%n");
        System.out.printf("%d parties/carte, %d tirages Monte-Carlo, gamma=%.2f%n%n",
                N_GAMES, SAMPLES, GAMMA);

        System.out.printf("%-12s", "Carte");
        for (int d : DEPTHS) System.out.printf(" | prof.%d (victoire / score)", d);
        System.out.println();

        double[] aggWin = new double[DEPTHS.length];
        double[] aggScore = new double[DEPTHS.length];

        for (String mapPath : MAPS) {
            String[] map = RealMapRunner.loadRealMap(mapPath);
            System.out.printf("%-12s", mapPath);
            for (int di = 0; di < DEPTHS.length; di++) {
                Res r = run(q, map, DEPTHS[di]);
                System.out.printf(" | %5.1f%% / %6.0f      ", 100.0 * r.winRate, r.avgScore);
                aggWin[di] += r.winRate;
                aggScore[di] += r.avgScore;
            }
            System.out.println();
        }

        System.out.printf("%n%-12s", "MOYENNE");
        for (int di = 0; di < DEPTHS.length; di++) {
            System.out.printf(" | %5.1f%% / %6.0f      ",
                    100.0 * aggWin[di] / MAPS.length, aggScore[di] / MAPS.length);
        }
        System.out.println("\n");

        // Conclusion automatique simple.
        double base = 100.0 * aggWin[0] / MAPS.length;
        double deep = 100.0 * aggWin[DEPTHS.length - 1] / MAPS.length;
        System.out.printf("Profondeur 1 (gloutonne) : %.1f%% de victoires en moyenne.%n", base);
        System.out.printf("Profondeur %d (lookahead) : %.1f%% de victoires en moyenne.%n",
                DEPTHS[DEPTHS.length - 1], deep);
        System.out.printf("Effet de la prediction multi-pas : %+.1f points.%n", deep - base);
    }

    static class Res { double winRate, avgScore; }

    static Res run(QLearningAgent q, String[] map, int depth) {
        LookaheadAgent agent = new LookaheadAgent(q, depth, SAMPLES, GAMMA);
        PacmanEnv env = new PacmanEnv(map, 4242);
        int wins = 0;
        double sumScore = 0;
        for (int i = 0; i < N_GAMES; i++) {
            env.reset();
            while (!env.isDone()) {
                int a = agent.chooseAction(env);
                env.step(a);
            }
            if (env.won()) wins++;
            sumScore += env.getScore();
        }
        Res r = new Res();
        r.winRate = wins / (double) N_GAMES;
        r.avgScore = sumScore / N_GAMES;
        return r;
    }
}
