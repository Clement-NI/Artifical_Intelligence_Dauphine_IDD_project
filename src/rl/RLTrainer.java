package rl;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Programme principal d'apprentissage par renforcement.
 *
 * Il réalise les trois étapes demandées :
 *   1. APPLIQUER   : entraîne un agent Q-learning approché sur le simulateur
 *                    Pac-Man (épisodes, exploration epsilon-greedy, mises à
 *                    jour TD).
 *   2. VALIDER     : vérifie que l'apprentissage converge (le retour moyen
 *                    augmente significativement entre le début et la fin de
 *                    l'entraînement).
 *   3. TESTER      : compare la politique apprise (gloutonne) à une politique
 *                    aléatoire sur des épisodes d'évaluation, et exige que
 *                    l'agent appris la surpasse nettement.
 *
 * Les poids appris sont sauvegardés dans {@code doc/qweights.txt} pour être
 * réutilisés en jeu par {@code logic.QLearningAI}.
 *
 * Exécution : {@code java -cp out rl.RLTrainer}
 * Code de sortie 0 si validation ET test réussis, 1 sinon (utilisable en CI).
 */
public class RLTrainer {

    static final long SEED = 42L;
    static final int N_EPISODES = 6000;
    static final double GAMMA = 0.9;
    static final double ALPHA = 0.002;
    static final double EPS_START = 1.0;
    static final double EPS_END = 0.05;
    static final String WEIGHTS_PATH = "doc/qweights.txt";

    public static void main(String[] args) {
        String[] map = PacmanEnv.MEDIUM_MAP;
        System.out.println("=== Apprentissage par renforcement (Q-learning approché) ===");
        System.out.println("Carte : " + map[0].length() + "x" + map.length
                + " | features : " + Features.N + " | episodes : " + N_EPISODES);

        QLearningAgent agent = new QLearningAgent(ALPHA, GAMMA, EPS_START, SEED);
        PacmanEnv env = new PacmanEnv(map, SEED);

        // ----- 1) APPLIQUER : boucle d'entraînement ---------------------------
        double[] returns = new double[N_EPISODES];
        for (int ep = 0; ep < N_EPISODES; ep++) {
            double eps = EPS_START + (EPS_END - EPS_START) * (ep / (double) N_EPISODES);
            agent.setEpsilon(eps);

            env.reset();
            double epReturn = 0.0;
            while (!env.isDone()) {
                List<Integer> legal = env.legalActions();
                int a = agent.act(env, legal);

                // On capture une "photo" de l'état courant pour la mise à jour TD,
                // car env est mutable.
                Snapshot s = Snapshot.of(env);
                double r = env.step(a);
                List<Integer> legalNext = env.legalActions();
                agent.update(s, a, r, env, legalNext, env.isDone());
                epReturn += r;
            }
            returns[ep] = epReturn;
        }

        // ----- 2) VALIDER : convergence de l'apprentissage --------------------
        int window = 300;
        double startAvg = avg(returns, 0, window);
        double endAvg = avg(returns, N_EPISODES - window, N_EPISODES);
        System.out.println("\n--- VALIDATION (convergence) ---");
        System.out.printf("Retour moyen (%d premiers episodes) : %.1f%n", window, startAvg);
        System.out.printf("Retour moyen (%d derniers episodes) : %.1f%n", window, endAvg);
        System.out.printf("Amelioration : %+.1f%n", endAvg - startAvg);
        printLearningCurve(returns, 10);

        double[] w = agent.getWeights();
        System.out.println("Poids appris :");
        for (int i = 0; i < w.length; i++) {
            System.out.printf("   %-22s = %+.4f%n", Features.NAMES[i], w[i]);
        }
        boolean validationOk = endAvg > startAvg + 50.0;
        System.out.println(validationOk
                ? "[OK] Validation : la politique s'est nettement amelioree."
                : "[ECHEC] Validation : pas d'amelioration suffisante.");

        // ----- 3) TESTER : politique apprise vs aléatoire ---------------------
        System.out.println("\n--- TEST (politique apprise vs aleatoire) ---");
        int nEval = 400;
        QLearningAgent greedy = new QLearningAgent(agent.getWeights());
        Eval learned = evaluate(greedy, map, nEval, true, 7L);
        Eval random = evaluate(null, map, nEval, false, 7L);

        System.out.printf("%-12s | score moyen %8.1f | retour moyen %8.1f | victoires %5.1f%%%n",
                "Appris (RL)", learned.avgScore, learned.avgReturn, 100.0 * learned.winRate);
        System.out.printf("%-12s | score moyen %8.1f | retour moyen %8.1f | victoires %5.1f%%%n",
                "Aleatoire", random.avgScore, random.avgReturn, 100.0 * random.winRate);

        boolean testOk = learned.avgReturn > random.avgReturn + 50.0
                && learned.avgScore > random.avgScore;
        System.out.println(testOk
                ? "[OK] Test : la politique apprise surpasse nettement l'aleatoire."
                : "[ECHEC] Test : la politique apprise ne surpasse pas l'aleatoire.");

        // ----- Sauvegarde des poids ------------------------------------------
        try {
            greedy.save(WEIGHTS_PATH);
            System.out.println("\nPoids sauvegardes dans " + WEIGHTS_PATH);
        } catch (IOException e) {
            System.out.println("Impossible de sauvegarder les poids : " + e.getMessage());
        }

        System.out.println("\n=== RESULTAT GLOBAL : "
                + ((validationOk && testOk) ? "SUCCES" : "ECHEC") + " ===");
        if (!(validationOk && testOk)) {
            System.exit(1);
        }
    }

    // --- Évaluation ------------------------------------------------------------

    static class Eval {
        double avgScore, avgReturn, winRate;
    }

    /**
     * Évalue une politique sur {@code n} épisodes.
     * @param agent  agent glouton, ou null pour une politique aléatoire
     */
    static Eval evaluate(QLearningAgent agent, String[] map, int n, boolean greedy, long seed) {
        PacmanEnv env = new PacmanEnv(map, seed * 1000 + 1);
        Random rnd = new Random(seed);
        double sumScore = 0, sumReturn = 0;
        int wins = 0;
        for (int i = 0; i < n; i++) {
            env.reset();
            double ret = 0.0;
            while (!env.isDone()) {
                List<Integer> legal = env.legalActions();
                int a;
                if (greedy && agent != null) {
                    a = agent.greedyAction(env, legal);
                } else {
                    a = legal.get(rnd.nextInt(legal.size()));
                }
                ret += env.step(a);
            }
            sumScore += env.getScore();
            sumReturn += ret;
            if (env.won()) wins++;
        }
        Eval e = new Eval();
        e.avgScore = sumScore / n;
        e.avgReturn = sumReturn / n;
        e.winRate = wins / (double) n;
        return e;
    }

    // --- Utilitaires -----------------------------------------------------------

    static double avg(double[] a, int from, int to) {
        double s = 0;
        for (int i = from; i < to; i++) s += a[i];
        return s / (to - from);
    }

    static void printLearningCurve(double[] returns, int buckets) {
        System.out.println("Courbe d'apprentissage (retour moyen par tranche) :");
        int size = returns.length / buckets;
        for (int b = 0; b < buckets; b++) {
            double m = avg(returns, b * size, (b + 1) * size);
            int bars = (int) Math.max(0, (m + 600) / 40); // décalage pour visualisation
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < bars && k < 60; k++) sb.append('#');
            System.out.printf("  tranche %2d : %+8.1f  %s%n", b + 1, m, sb);
        }
    }
}
