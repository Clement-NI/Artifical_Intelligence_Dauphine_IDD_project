package rl;

import java.io.IOException;
import java.util.List;

/**
 * Experience demandee : Q-learning traditionnel (lineaire) + prediction sur
 * plusieurs pas + encouragement a manger les SUPER gommes.
 *
 * Trois leviers combines :
 *   1. modele lineaire 8 features, dont 2 nouvelles ciblant les capsules
 *      ("eats-capsule", "closer-to-capsule-when-hunted") ;
 *   2. recompense de super gomme augmentee (PacmanEnv.SCORE_SUPER, defaut 200,
 *      reglable -Dpacman.super=...) : manger une capsule rend les fantomes
 *      comestibles, ce qui supprime la cause n.1 des defaites (collision) ;
 *   3. recherche deterministe a horizon (LookaheadPolicy) a l'evaluation, pour
 *      "predire plusieurs pas".
 *
 * On entraine la politique lineaire, puis on l'evalue en profondeur 1 (gloutonne)
 * et en profondeur 2 et 3 (lookahead), et on compare au plafond historique
 * (~43,7% du modele 6 features).
 *
 * Sauvegarde les poids 8 features dans doc/qweights.txt (utilises par le jeu).
 *
 * Execution : java -cp out rl.CapsuleLookaheadExperiment
 */
public class CapsuleLookaheadExperiment {

    static final long SEED = 42L;
    static final int N_EPISODES = 30000;
    static final double GAMMA = 0.95;
    static final double ALPHA = 0.0008;
    static final double EPS_START = 1.0, EPS_END = 0.05;
    static final String[] MAP_FILES = {"doc/map1.map", "doc/map2.map", "doc/map3.map"};
    static final String WEIGHTS_PATH = "doc/qweights.txt";
    static final int EVAL_GAMES = 150;

    public static void main(String[] args) throws IOException {
        String[][] maps = new String[MAP_FILES.length][];
        PacmanEnv[] envs = new PacmanEnv[MAP_FILES.length];
        for (int i = 0; i < MAP_FILES.length; i++) {
            maps[i] = RealMapRunner.loadRealMap(MAP_FILES[i]);
            envs[i] = new PacmanEnv(maps[i], SEED + i);
        }

        System.out.println("=== Q-learning lineaire + capsules + lookahead ===");
        System.out.printf("features=%d | SCORE_SUPER=%d | episodes=%d%n%n",
                Features.N, PacmanEnv.SCORE_SUPER, N_EPISODES);

        // --- Entrainement (Q-learning lineaire, comme RealMapTrainer) ---
        QLearningAgent agent = new QLearningAgent(ALPHA, GAMMA, EPS_START, SEED);
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

        double[] w = agent.getWeights();
        System.out.println("Poids appris :");
        for (int i = 0; i < w.length; i++)
            System.out.printf("   %-30s = %+.3f%n", Features.NAMES[i], w[i]);
        System.out.println();

        QLearningAgent greedy = new QLearningAgent(agent.getWeights());

        // --- Evaluation : profondeur 1 (gloutonne) vs lookahead 2 et 3 ---
        int[] depths = {1, 2, 3};
        double bestAvg = -1; int bestDepth = 1;
        for (int depth : depths) {
            double sumWin = 0;
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < MAP_FILES.length; i++) {
                double[] res = evalDepth(greedy, maps[i], depth, EVAL_GAMES);
                line.append(String.format("  %s %4.1f%%", shortName(MAP_FILES[i]), 100.0 * res[0]));
                sumWin += res[0];
            }
            double avg = 100.0 * sumWin / MAP_FILES.length;
            System.out.printf("profondeur %d :%s  | MOYENNE %.1f%%%n", depth, line, avg);
            if (avg > bestAvg) { bestAvg = avg; bestDepth = depth; }
        }

        System.out.printf("%nMeilleure config : profondeur %d -> %.1f%% (plafond lineaire precedent ~43,7%%)%n",
                bestDepth, bestAvg);

        // Sauvegarde des poids (la profondeur de jeu se regle separement).
        greedy.save(WEIGHTS_PATH);
        System.out.println("Poids 8 features sauvegardes dans " + WEIGHTS_PATH);
    }

    /** @return {winRate, avgScore} en jouant a la profondeur donnee. */
    static double[] evalDepth(QLearningAgent q, String[] map, int depth, int n) {
        LookaheadPolicy policy = new LookaheadPolicy(q, depth, GAMMA);
        PacmanEnv env = new PacmanEnv(map, 99999);
        int wins = 0; double sumScore = 0;
        for (int i = 0; i < n; i++) {
            env.reset();
            while (!env.isDone()) {
                int a = (depth == 1) ? q.greedyAction(env, env.legalActions())
                                     : policy.choose(env);
                env.step(a);
            }
            if (env.won()) wins++;
            sumScore += env.getScore();
        }
        return new double[]{wins / (double) n, sumScore / n};
    }

    static String shortName(String p) {
        return p.replace("doc/", "").replace(".map", "");
    }
}
