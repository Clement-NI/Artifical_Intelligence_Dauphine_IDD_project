package rl;

import java.io.IOException;
import java.util.List;

/**
 * Entraîne la politique RL DIRECTEMENT sur les vraies cartes du jeu
 * (doc/map1..3.map), en alternant les cartes d'un épisode à l'autre, puis
 * la valide (convergence) et la teste (taux de victoire par carte).
 *
 * Contrairement à {@link RLTrainer} (qui entraîne sur une petite carte 11x11),
 * ce programme apprend sur les niveaux 25x25 réellement joués, ce qui donne une
 * politique nettement plus performante sur ces cartes. Les poids sont
 * sauvegardés dans {@code doc/qweights.txt}.
 *
 * Exécution : {@code java -cp out rl.RealMapTrainer}
 */
public class RealMapTrainer {

    static final long SEED = 42L;
    static final int N_EPISODES = 30000;
    static final double GAMMA = 0.95;
    static final double ALPHA = 0.0008;
    static final double EPS_START = 1.0;
    static final double EPS_END = 0.05;
    static final String WEIGHTS_PATH = "doc/qweights.txt";
    static final String[] MAP_FILES = {"doc/map1.map", "doc/map2.map", "doc/map3.map"};

    public static void main(String[] args) {
        // Chargement des vraies cartes (converties au format du simulateur).
        String[][] maps = new String[MAP_FILES.length][];
        for (int i = 0; i < MAP_FILES.length; i++) {
            maps[i] = RealMapRunner.loadRealMap(MAP_FILES[i]);
            if (maps[i] == null) {
                System.out.println("Carte illisible : " + MAP_FILES[i]);
                System.exit(1);
            }
        }

        System.out.println("=== Entrainement RL sur les vraies cartes (25x25) ===");
        System.out.printf("%d cartes | %d episodes | features : %d%n%n",
                MAP_FILES.length, N_EPISODES, Features.N);

        QLearningAgent agent = new QLearningAgent(ALPHA, GAMMA, EPS_START, SEED);

        // Un environnement réutilisable par carte (graines distinctes).
        PacmanEnv[] envs = new PacmanEnv[maps.length];
        for (int i = 0; i < maps.length; i++) {
            envs[i] = new PacmanEnv(maps[i], SEED + i);
        }

        // ----- 1) APPLIQUER : entraînement en alternant les cartes ------------
        double[] returns = new double[N_EPISODES];
        for (int ep = 0; ep < N_EPISODES; ep++) {
            double eps = EPS_START + (EPS_END - EPS_START) * (ep / (double) N_EPISODES);
            agent.setEpsilon(eps);

            PacmanEnv env = envs[ep % envs.length];
            env.reset();
            double epReturn = 0.0;
            while (!env.isDone()) {
                List<Integer> legal = env.legalActions();
                int a = agent.act(env, legal);
                Snapshot s = Snapshot.of(env);
                double r = env.step(a);
                List<Integer> legalNext = env.legalActions();
                agent.update(s, a, r, env, legalNext, env.isDone());
                epReturn += r;
            }
            returns[ep] = epReturn;
        }

        // ----- 2) VALIDER : convergence ---------------------------------------
        int window = 1500;
        double startAvg = avg(returns, 0, window);
        double endAvg = avg(returns, N_EPISODES - window, N_EPISODES);
        System.out.println("--- VALIDATION (convergence) ---");
        System.out.printf("Retour moyen (%d premiers) : %.1f%n", window, startAvg);
        System.out.printf("Retour moyen (%d derniers) : %.1f%n", window, endAvg);
        System.out.printf("Amelioration : %+.1f%n", endAvg - startAvg);
        double[] w = agent.getWeights();
        System.out.println("Poids appris :");
        for (int i = 0; i < w.length; i++) {
            System.out.printf("   %-22s = %+.4f%n", Features.NAMES[i], w[i]);
        }
        boolean validationOk = endAvg > startAvg + 50.0;
        System.out.println(validationOk
                ? "[OK] Validation : la politique s'est nettement amelioree."
                : "[ECHEC] Validation : pas d'amelioration suffisante.");

        // ----- 3) TESTER : taux de victoire par carte -------------------------
        System.out.println("\n--- TEST (taux de victoire par carte, 300 parties) ---");
        QLearningAgent greedy = new QLearningAgent(agent.getWeights());
        int passed = 0, everWon = 0;
        for (int i = 0; i < maps.length; i++) {
            RealMapRunner.Stat st = RealMapRunner.evaluate(greedy, maps[i], 300);
            System.out.printf("%-12s | victoires %5.1f%%  | score moyen %7.1f  | meilleur %d%n",
                    MAP_FILES[i], 100.0 * st.winRate, st.avgScore, st.bestScore);
            if (st.winRate > 0) everWon++;
            if (st.winRate >= 0.5) passed++;
        }
        System.out.printf("%nCartes terminees au moins une fois : %d / %d%n", everWon, maps.length);
        System.out.printf("Cartes reussies (>= 50%% victoires) : %d / %d%n", passed, maps.length);

        // ----- Sauvegarde -----------------------------------------------------
        try {
            greedy.save(WEIGHTS_PATH);
            System.out.println("\nPoids sauvegardes dans " + WEIGHTS_PATH);
        } catch (IOException e) {
            System.out.println("Sauvegarde impossible : " + e.getMessage());
        }
    }

    static double avg(double[] a, int from, int to) {
        double s = 0;
        for (int i = from; i < to; i++) s += a[i];
        return s / (to - from);
    }
}
