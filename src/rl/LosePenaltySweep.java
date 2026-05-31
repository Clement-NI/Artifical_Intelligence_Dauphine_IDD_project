package rl;

import java.util.List;

/**
 * Experience : l'alourdissement de la penalite de mort augmente-t-il le taux de
 * victoire ?
 *
 * Idee testee (suggeree a juste titre : "mourir = fin de partie, c'est pire que
 * tout") : si LOSE_REWARD est trop faible face a la valeur cumulee des gommes,
 * l'agent prend des risques mortels pour grappiller des gommes proches. On
 * reentraine donc une politique pour la penalite de mort courante
 * (PacmanEnv.LOSE_REWARD, reglable via -Dpacman.lose=...) et on mesure le taux
 * de victoire sur les vraies cartes.
 *
 * Usage (une valeur par lancement de JVM) :
 *   java -Dpacman.lose=-500  -cp out rl.LosePenaltySweep
 *   java -Dpacman.lose=-1500 -cp out rl.LosePenaltySweep
 *   java -Dpacman.lose=-3000 -cp out rl.LosePenaltySweep
 */
public class LosePenaltySweep {

    static final long SEED = 42L;
    static final int N_EPISODES = 30000;
    static final double GAMMA = 0.95;
    static final double ALPHA = 0.0008;
    static final double EPS_START = 1.0;
    static final double EPS_END = 0.05;
    static final String[] MAP_FILES = {"doc/map1.map", "doc/map2.map", "doc/map3.map"};
    static final int EVAL_GAMES = 200;

    public static void main(String[] args) {
        String[][] maps = new String[MAP_FILES.length][];
        for (int i = 0; i < MAP_FILES.length; i++) {
            maps[i] = RealMapRunner.loadRealMap(MAP_FILES[i]);
        }

        System.out.println("=== Effet de la penalite de mort sur le taux de victoire ===");
        System.out.printf("LOSE_REWARD = %d  (gommes ~1700 + bonus victoire 500)%n",
                PacmanEnv.LOSE_REWARD);

        // --- Entrainement (memes hyperparametres que RealMapTrainer) ---
        QLearningAgent agent = new QLearningAgent(ALPHA, GAMMA, EPS_START, SEED);
        PacmanEnv[] envs = new PacmanEnv[maps.length];
        for (int i = 0; i < maps.length; i++) envs[i] = new PacmanEnv(maps[i], SEED + i);

        for (int ep = 0; ep < N_EPISODES; ep++) {
            agent.setEpsilon(EPS_START + (EPS_END - EPS_START) * (ep / (double) N_EPISODES));
            PacmanEnv env = envs[ep % envs.length];
            env.reset();
            while (!env.isDone()) {
                List<Integer> legal = env.legalActions();
                int a = agent.act(env, legal);
                Snapshot s = Snapshot.of(env);
                double r = env.step(a);
                agent.update(s, a, r, env, env.legalActions(), env.isDone());
            }
        }

        // --- Diagnostic : poids appris (doivent differer selon LOSE_REWARD) ---
        double[] ww = agent.getWeights();
        StringBuilder sb = new StringBuilder("  poids: ");
        for (double x : ww) sb.append(String.format("%.1f ", x));
        System.out.println(sb);

        // --- Evaluation gloutonne ---
        QLearningAgent greedy = new QLearningAgent(agent.getWeights());
        double sumWin = 0;
        for (int i = 0; i < maps.length; i++) {
            RealMapRunner.Stat st = RealMapRunner.evaluate(greedy, maps[i], EVAL_GAMES);
            System.out.printf("  %-12s victoires %5.1f%%  | score moyen %7.1f%n",
                    MAP_FILES[i], 100.0 * st.winRate, st.avgScore);
            sumWin += st.winRate;
        }
        System.out.printf("  >>> MOYENNE victoires : %.1f%%%n", 100.0 * sumWin / maps.length);
    }
}
