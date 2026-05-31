package rl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * DQN (Deep Q-Network) : Q-learning avec reseau de neurones, rejeu d'experience
 * et reseau cible. Objectif : depasser le plafond (~43-48%) du modele lineaire,
 * qui ne pouvait pas representer un encerclement par plusieurs fantomes.
 *
 * Ingredients DQN classiques, tous a la main :
 *   - reseau q (online) + reseau cible qHat (copie periodique) ;
 *   - tampon de rejeu (experience replay) pour decorreler les transitions ;
 *   - cible y = r + gamma * max_a' qHat(s', a')  (0 si terminal) ;
 *   - exploration epsilon-greedy decroissante.
 *
 * Etat : {@link StateEncoder} (riche, par direction). Sorties : 4 Q-valeurs.
 *
 * Execution : java -cp out rl.DQNTrainer
 */
public class DQNTrainer {

    static final long SEED = 42L;
    static final int EPISODES = Integer.getInteger("dqn.episodes", 40000);
    static final int HIDDEN = 64;
    static final double GAMMA = 0.95;
    static final double LR = 0.0005;
    static final double EPS_START = 1.0, EPS_END = 0.05;
    static final int BUFFER = 50000;
    static final int BATCH = 32;
    static final int TARGET_SYNC = 1000;   // pas entre deux copies vers le reseau cible
    static final String[] MAP_FILES = {"doc/map1.map", "doc/map2.map", "doc/map3.map"};
    static final String NET_PATH = "doc/dqn.txt";
    static final int EVAL_GAMES = 150;

    static final int[] DR = StateEncoder.DR, DC = StateEncoder.DC;

    public static void main(String[] args) throws Exception {
        String[][] maps = new String[MAP_FILES.length][];
        PacmanEnv[] envs = new PacmanEnv[MAP_FILES.length];
        for (int i = 0; i < MAP_FILES.length; i++) {
            maps[i] = RealMapRunner.loadRealMap(MAP_FILES[i]);
            envs[i] = new PacmanEnv(maps[i], SEED + i);
        }

        int din = StateEncoder.SIZE;
        NeuralNet q = new NeuralNet(din, HIDDEN, 4, SEED);
        NeuralNet qHat = new NeuralNet(din, HIDDEN, 4, SEED);
        qHat.copyFrom(q);

        Random rng = new Random(SEED);
        // Tampon de rejeu (tableaux circulaires).
        double[][] bS = new double[BUFFER][];
        int[] bA = new int[BUFFER];
        double[] bR = new double[BUFFER];
        double[][] bS2 = new double[BUFFER][];
        boolean[] bDone = new boolean[BUFFER];
        // actions legales de s' (masque), pour le max cible
        boolean[][] bLegal2 = new boolean[BUFFER][];

        int bufLen = 0, bufPos = 0, stepCount = 0;
        double recentReturn = 0; int recentN = 0;

        System.out.println("=== DQN : entrainement ===");
        System.out.printf("etat=%d, cachee=%d, episodes=%d, gamma=%.2f, lr=%.4f%n",
                din, HIDDEN, EPISODES, GAMMA, LR);

        for (int ep = 0; ep < EPISODES; ep++) {
            double eps = EPS_START + (EPS_END - EPS_START) * Math.min(1.0, ep / (double)(EPISODES * 0.8));
            PacmanEnv env = envs[ep % envs.length];
            env.reset();
            double[] s = StateEncoder.encode(env);
            double epReturn = 0;

            while (!env.isDone()) {
                boolean[] legalMask = legalMask(env);
                int a = chooseAction(q, s, legalMask, eps, rng);

                double r = env.step(a);
                epReturn += r;
                boolean done = env.isDone();
                double[] s2 = StateEncoder.encode(env);
                boolean[] legal2 = legalMask(env);

                // stocker la transition
                bS[bufPos] = s; bA[bufPos] = a; bR[bufPos] = r; bS2[bufPos] = s2;
                bDone[bufPos] = done; bLegal2[bufPos] = legal2;
                bufPos = (bufPos + 1) % BUFFER;
                if (bufLen < BUFFER) bufLen++;

                // apprendre depuis un mini-lot
                if (bufLen >= BATCH) {
                    for (int b = 0; b < BATCH; b++) {
                        int j = rng.nextInt(bufLen);
                        double target = bR[j];
                        if (!bDone[j]) {
                            double[] qn = qHat.forward(bS2[j]);
                            double best = Double.NEGATIVE_INFINITY;
                            for (int k = 0; k < 4; k++)
                                if (bLegal2[j][k] && qn[k] > best) best = qn[k];
                            if (best == Double.NEGATIVE_INFINITY) best = 0;
                            target += GAMMA * best;
                        }
                        q.trainStep(bS[j], bA[j], target, LR);
                    }
                }

                stepCount++;
                if (stepCount % TARGET_SYNC == 0) qHat.copyFrom(q);
                s = s2;
            }
            recentReturn += epReturn; recentN++;
            if ((ep + 1) % 5000 == 0) {
                System.out.printf("  ep %6d | eps %.2f | retour moyen recent %.1f%n",
                        ep + 1, eps, recentReturn / recentN);
                recentReturn = 0; recentN = 0;
            }
        }

        q.save(NET_PATH);
        System.out.println("Reseau sauvegarde dans " + NET_PATH);

        // --- Evaluation gloutonne ---
        System.out.println("\n=== DQN : evaluation (gloutonne) ===");
        double sumWin = 0;
        for (int i = 0; i < MAP_FILES.length; i++) {
            double[] res = evaluate(q, maps[i], EVAL_GAMES);
            System.out.printf("  %-12s victoires %5.1f%%  | score moyen %7.0f%n",
                    MAP_FILES[i], 100.0 * res[0], res[1]);
            sumWin += res[0];
        }
        System.out.printf("  >>> MOYENNE victoires DQN : %.1f%%%n", 100.0 * sumWin / MAP_FILES.length);
        System.out.println("  (rappel modele lineaire 6 features : ~43,7%)");
    }

    static boolean[] legalMask(PacmanEnv env) {
        boolean[] m = new boolean[4];
        List<Integer> legal = env.legalActions();
        for (int a : legal) m[a] = true;
        return m;
    }

    static int chooseAction(NeuralNet q, double[] s, boolean[] legal, double eps, Random rng) {
        // exploration : action legale aleatoire
        if (rng.nextDouble() < eps) {
            List<Integer> ls = new ArrayList<>();
            for (int a = 0; a < 4; a++) if (legal[a]) ls.add(a);
            return ls.isEmpty() ? 0 : ls.get(rng.nextInt(ls.size()));
        }
        return greedy(q, s, legal);
    }

    static int greedy(NeuralNet q, double[] s, boolean[] legal) {
        double[] qv = q.forward(s);
        int best = -1; double bv = Double.NEGATIVE_INFINITY;
        for (int a = 0; a < 4; a++) {
            if (legal[a] && qv[a] > bv) { bv = qv[a]; best = a; }
        }
        return best < 0 ? 0 : best;
    }

    /** @return {winRate, avgScore} */
    static double[] evaluate(NeuralNet q, String[] map, int n) {
        PacmanEnv env = new PacmanEnv(map, 99999);
        int wins = 0; double sumScore = 0;
        for (int i = 0; i < n; i++) {
            env.reset();
            while (!env.isDone()) {
                double[] s = StateEncoder.encode(env);
                env.step(greedy(q, s, legalMask(env)));
            }
            if (env.won()) wins++;
            sumScore += env.getScore();
        }
        return new double[]{wins / (double) n, sumScore / n};
    }
}
