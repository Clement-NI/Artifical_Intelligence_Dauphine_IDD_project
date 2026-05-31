package rl;

import java.util.List;

/**
 * Verifie la robustesse du resultat lookahead : rejoue la politique lineaire
 * chargee depuis doc/qweights.txt aux profondeurs 1 et 3, sur PLUSIEURS graines
 * d'evaluation differentes, pour confirmer (ou non) le gain de la profondeur 3.
 *
 * Execution : java -cp out rl.VerifyLookahead
 */
public class VerifyLookahead {

    static final double GAMMA = 0.95;
    static final String[] MAP_FILES = {"doc/map1.map", "doc/map2.map", "doc/map3.map"};
    static final int GAMES = 150;
    static final long[] SEEDS = {111, 222, 333};

    public static void main(String[] args) throws Exception {
        double[] w = QLearningAgent.load("doc/qweights.txt");
        QLearningAgent q = new QLearningAgent(w);

        String[][] maps = new String[MAP_FILES.length][];
        for (int i = 0; i < MAP_FILES.length; i++) maps[i] = RealMapRunner.loadRealMap(MAP_FILES[i]);

        System.out.println("=== Robustesse lookahead (poids charges, plusieurs graines) ===");
        for (int depth : new int[]{1, 3}) {
            double grand = 0; int cnt = 0;
            System.out.printf("profondeur %d :%n", depth);
            for (long seed : SEEDS) {
                double sumWin = 0;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < MAP_FILES.length; i++) {
                    double wr = winRate(q, maps[i], depth, seed);
                    sb.append(String.format("  %s %4.1f%%", MAP_FILES[i].replace("doc/","").replace(".map",""), 100*wr));
                    sumWin += wr;
                }
                double avg = 100.0 * sumWin / MAP_FILES.length;
                System.out.printf("  graine %4d :%s | moy %.1f%%%n", seed, sb, avg);
                grand += avg; cnt++;
            }
            System.out.printf("  >>> moyenne globale profondeur %d : %.1f%%%n%n", depth, grand / cnt);
        }
    }

    static double winRate(QLearningAgent q, String[] map, int depth, long seed) {
        LookaheadPolicy policy = new LookaheadPolicy(q, depth, GAMMA);
        PacmanEnv env = new PacmanEnv(map, seed);
        int wins = 0;
        for (int i = 0; i < GAMES; i++) {
            env.reset();
            policy.resetHistory();
            while (!env.isDone()) {
                int a = (depth == 1) ? q.greedyAction(env, env.legalActions()) : policy.choose(env);
                env.step(a);
            }
            if (env.won()) wins++;
        }
        return wins / (double) GAMES;
    }
}
