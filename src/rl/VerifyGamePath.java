package rl;

/**
 * Verifie que le chemin de decision REELLEMENT utilise en jeu fonctionne :
 * reconstruction d'un PacmanEnv depuis un GameView (comme le fait
 * logic.QLearningAI) puis recherche a horizon. On rejoue des parties ou, a
 * chaque pas, on RECONSTRUIT l'environnement depuis l'etat courant (via le
 * constructeur PacmanEnv(GameView)) avant de choisir l'action, exactement comme
 * en jeu. Cela teste le constructeur GameView + le lookahead de bout en bout.
 *
 * Execution : java -cp out rl.VerifyGamePath
 */
public class VerifyGamePath {

    static final double GAMMA = 0.95;
    static final String[] MAPS = {"doc/map1.map", "doc/map2.map", "doc/map3.map"};
    static final int GAMES = 120;

    public static void main(String[] args) throws Exception {
        double[] w = QLearningAgent.load("doc/qweights.txt");
        QLearningAgent q = new QLearningAgent(w);
        int depth = Integer.getInteger("pacman.depth", 3);
        LookaheadPolicy policy = new LookaheadPolicy(q, depth, GAMMA);

        System.out.printf("=== Verif chemin de jeu (reconstruction GameView + lookahead prof.%d) ===%n", depth);
        double sum = 0;
        for (String mp : MAPS) {
            String[] map = RealMapRunner.loadRealMap(mp);
            PacmanEnv real = new PacmanEnv(map, 555);
            int wins = 0;
            for (int g = 0; g < GAMES; g++) {
                real.reset();
                policy.resetHistory();
                while (!real.isDone()) {
                    // comme en jeu : on reconstruit un env depuis l'etat courant
                    PacmanEnv viewEnv = new PacmanEnv((GameView) real);
                    int a = (depth <= 1) ? q.greedyAction(viewEnv, viewEnv.legalActions())
                                         : policy.choose(viewEnv);
                    real.step(a);
                }
                if (real.won()) wins++;
            }
            double wr = 100.0 * wins / GAMES;
            System.out.printf("  %-12s victoires %.1f%%%n", mp, wr);
            sum += wr;
        }
        System.out.printf("  >>> MOYENNE (chemin de jeu) : %.1f%%%n", sum / MAPS.length);
    }
}
