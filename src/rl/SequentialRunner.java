package rl;

import java.util.List;

/**
 * Mesure empirique de la probabilité de terminer les TROIS cartes d'affilée
 * (map1 -> map2 -> map3), comme un enchaînement de niveaux dans le vrai jeu.
 *
 * On joue N "campagnes" : chaque campagne enchaîne les cartes tant que Pac-Man
 * gagne ; un échec sur une carte arrête la campagne. On reporte, pour chaque
 * profondeur, la fraction de campagnes ayant atteint ce niveau, et la
 * probabilité de réussir les 3 cartes.
 */
public class SequentialRunner {

    static final double[] DEFAULT_WEIGHTS = {87.6060, -496.5099, 48.1285, -21.2270, 62.4680, -108.3977, 0.0, 0.0};
    static final String[] MAPS = {"doc/map1.map", "doc/map2.map", "doc/map3.map"};
    static final int N_RUNS = 5000;

    public static void main(String[] args) {
        double[] w;
        try {
            w = new java.io.File("doc/qweights.txt").exists()
                    ? QLearningAgent.load("doc/qweights.txt") : DEFAULT_WEIGHTS;
        } catch (Exception e) { w = DEFAULT_WEIGHTS; }
        QLearningAgent agent = new QLearningAgent(w);

        String[][] maps = new String[MAPS.length][];
        PacmanEnv[] envs = new PacmanEnv[MAPS.length];
        for (int i = 0; i < MAPS.length; i++) {
            maps[i] = RealMapRunner.loadRealMap(MAPS[i]);
            envs[i] = new PacmanEnv(maps[i], 777 + i);
        }

        int[] reached = new int[MAPS.length + 1]; // reached[k] = campagnes ayant gagné k cartes
        for (int run = 0; run < N_RUNS; run++) {
            int cleared = 0;
            for (int m = 0; m < MAPS.length; m++) {
                if (playOne(agent, envs[m])) cleared++;
                else break;
            }
            reached[cleared]++;
        }

        // Conversion en probabilités cumulées.
        System.out.println("=== Probabilite de terminer les cartes d'affilee ===");
        System.out.printf("%d campagnes (map1 -> map2 -> map3)%n%n", N_RUNS);
        int atLeast = N_RUNS;
        for (int k = 1; k <= MAPS.length; k++) {
            atLeast -= reached[k - 1];
            System.out.printf("Terminer au moins %d carte(s) : %5.1f%%%n",
                    k, 100.0 * atLeast / N_RUNS);
        }
        double all3 = 100.0 * reached[MAPS.length] / N_RUNS;
        System.out.printf("%n>>> Probabilite de passer LES 3 CARTES : %.1f%% <<<%n", all3);
    }

    /** Joue une partie (politique gloutonne) ; true si la carte est terminée. */
    private static boolean playOne(QLearningAgent agent, PacmanEnv env) {
        env.reset();
        while (!env.isDone()) {
            List<Integer> legal = env.legalActions();
            env.step(agent.greedyAction(env, legal));
        }
        return env.won();
    }
}
