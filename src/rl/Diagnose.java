package rl;

import java.util.List;

/**
 * Diagnostic des DEFAITES : pourquoi la politique apprise perd-elle ?
 *
 * Pour ameliorer le taux de victoire, il faut d'abord savoir comment l'agent
 * echoue. On classe chaque partie perdue en deux causes :
 *   - MORT     : mange par un fantome (collision) ;
 *   - TIMEOUT  : episode tronque (maxSteps atteint) sans avoir fini les gommes
 *                -> l'agent "tourne en rond" et ne nettoie pas la carte.
 *
 * On reporte aussi le nombre moyen de gommes restantes en cas de timeout : s'il
 * est faible, l'agent finit presque la carte (probleme de "dernieres gommes") ;
 * s'il est eleve, il est bloque tot.
 *
 * Execution : java -cp out rl.Diagnose
 */
public class Diagnose {

    static final double[] DEFAULT_WEIGHTS = {91.6445, -511.0326, 45.5287, -17.1416, 62.8738};
    static final String[] MAPS = {"doc/map1.map", "doc/map2.map", "doc/map3.map"};
    static final int N = 300;

    public static void main(String[] args) {
        double[] w;
        try {
            w = new java.io.File("doc/qweights.txt").exists()
                    ? QLearningAgent.load("doc/qweights.txt") : DEFAULT_WEIGHTS;
        } catch (Exception e) { w = DEFAULT_WEIGHTS; }
        QLearningAgent agent = new QLearningAgent(w);

        System.out.println("=== Diagnostic des defaites (politique gloutonne) ===");
        System.out.printf("%d parties par carte%n%n", N);

        for (String mapPath : MAPS) {
            String[] map = RealMapRunner.loadRealMap(mapPath);
            int totalFood = countFood(map);
            PacmanEnv env = new PacmanEnv(map, 4242);

            int wins = 0, deaths = 0, timeouts = 0;
            long sumRemainOnTimeout = 0;
            long sumStepsWin = 0;

            for (int i = 0; i < N; i++) {
                env.reset();
                while (!env.isDone()) {
                    List<Integer> legal = env.legalActions();
                    env.step(agent.greedyAction(env, legal));
                }
                if (env.won()) {
                    wins++;
                    sumStepsWin += env.getSteps();
                } else {
                    // Distinguer mort vs timeout : on regarde les gommes restantes.
                    int remain = remainingFood(env);
                    if (env.getSteps() >= maxStepsOf(map)) {
                        timeouts++;
                        sumRemainOnTimeout += remain;
                    } else {
                        deaths++;
                    }
                }
            }

            int losses = deaths + timeouts;
            System.out.printf("%-12s (%d gommes)%n", mapPath, totalFood);
            System.out.printf("   victoires : %3d/%d (%.0f%%)  | pas moyens si victoire : %d%n",
                    wins, N, 100.0 * wins / N, wins > 0 ? sumStepsWin / wins : 0);
            System.out.printf("   defaites  : %3d  ->  morts : %d (%.0f%% des defaites)  |  "
                    + "timeouts : %d (%.0f%% des defaites)%n",
                    losses, deaths, losses > 0 ? 100.0 * deaths / losses : 0,
                    timeouts, losses > 0 ? 100.0 * timeouts / losses : 0);
            if (timeouts > 0) {
                System.out.printf("   en cas de timeout : %.1f gommes restantes en moyenne "
                        + "(sur %d)%n", (double) sumRemainOnTimeout / timeouts, totalFood);
            }
            System.out.println();
        }
    }

    static int maxStepsOf(String[] map) {
        return map.length * map[0].length() * 4;
    }

    static int remainingFood(PacmanEnv env) {
        int left = 0;
        for (int r = 0; r < env.rows(); r++)
            for (int c = 0; c < env.cols(); c++)
                if (env.hasFood(r, c)) left++;
        return left;
    }

    static int countFood(String[] map) {
        int n = 0;
        for (String row : map)
            for (int c = 0; c < row.length(); c++)
                if (row.charAt(c) == '.' || row.charAt(c) == '*') n++;
        return n;
    }
}
