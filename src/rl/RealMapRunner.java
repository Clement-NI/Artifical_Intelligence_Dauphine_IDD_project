package rl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Évalue la politique apprise sur les VRAIES cartes du jeu (doc/map1..3.map),
 * sans interface graphique.
 *
 * Les cartes d'origine (25x25) utilisent un format légèrement différent de
 * celui du simulateur :
 *   - 1re ligne : paramètres "25;couleur;" (ignorée) ;
 *   - 'F' = fantôme, 'O' = case vide, 'B' = Pacman sur case de fantôme.
 * On les convertit vers le format de {@link PacmanEnv} : 'F'->'G', 'B'->'P'.
 *
 * On compte, par carte, le taux de victoire (toutes les gommes mangées) et le
 * score moyen sur plusieurs parties, puis le nombre de cartes "réussies"
 * (au moins une victoire, et victoire majoritaire).
 */
public class RealMapRunner {

    static final double[] DEFAULT_WEIGHTS = {87.6060, -496.5099, 48.1285, -21.2270, 62.4680, -108.3977};
    static final String[] MAPS = {"doc/map1.map", "doc/map2.map", "doc/map3.map"};
    static final int N_GAMES = 200;

    public static void main(String[] args) {
        double[] w;
        try {
            w = new java.io.File("doc/qweights.txt").exists()
                    ? QLearningAgent.load("doc/qweights.txt")
                    : DEFAULT_WEIGHTS;
        } catch (Exception e) {
            w = DEFAULT_WEIGHTS;
        }
        QLearningAgent agent = new QLearningAgent(w);

        System.out.println("=== Evaluation de la politique apprise sur les vraies cartes ===");
        System.out.printf("%d parties par carte, politique gloutonne.%n%n", N_GAMES);

        int passed = 0, everWon = 0;
        for (String path : MAPS) {
            String[] map = loadRealMap(path);
            if (map == null) {
                System.out.println(path + " : illisible, ignoree.");
                continue;
            }
            Stat s = evaluate(agent, map, N_GAMES);
            System.out.printf("%-14s | %dx%d, %d fantomes, %d gommes%n",
                    path, map.length, map[0].length(), s.ghosts, s.food);
            System.out.printf("               | victoires %5.1f%%  | score moyen %7.1f  | "
                    + "gommes mangees moy. %5.1f/%d  | meilleur score %d%n%n",
                    100.0 * s.winRate, s.avgScore, s.avgEaten, s.food, s.bestScore);
            if (s.winRate > 0) everWon++;
            if (s.winRate >= 0.5) passed++;
        }

        System.out.println("--- Bilan ---");
        System.out.printf("Cartes ou l'agent gagne au moins une fois        : %d / %d%n", everWon, MAPS.length);
        System.out.printf("Cartes \"reussies\" (>= 50%% de victoires)          : %d / %d%n", passed, MAPS.length);
    }

    // --- Évaluation d'une carte ----------------------------------------------

    static class Stat {
        double winRate, avgScore, avgEaten;
        int bestScore, food, ghosts;
    }

    static Stat evaluate(QLearningAgent agent, String[] map, int n) {
        PacmanEnv env = new PacmanEnv(map, 12345);
        Stat st = new Stat();
        double sumScore = 0, sumEaten = 0;
        int wins = 0, best = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            env.reset();
            int foodAtStart = countFood(map);
            while (!env.isDone()) {
                List<Integer> legal = env.legalActions();
                int a = agent.greedyAction(env, legal);
                env.step(a);
            }
            sumScore += env.getScore();
            // gommes mangées = (score gomme) approximé via foodAtStart - foodLeft :
            // on relit via le score n'est pas fiable (rebonds), on recompte par état.
            sumEaten += eatenEstimate(env, foodAtStart);
            best = Math.max(best, env.getScore());
            if (env.won()) wins++;
        }
        st.food = countFood(map);
        st.ghosts = countGhosts(map);
        st.winRate = wins / (double) n;
        st.avgScore = sumScore / n;
        st.avgEaten = sumEaten / n;
        st.bestScore = best;
        return st;
    }

    // gommes mangées : reconstituées depuis le nombre restant exposé par la vue.
    private static int eatenEstimate(PacmanEnv env, int foodAtStart) {
        int left = 0;
        for (int r = 0; r < env.rows(); r++)
            for (int c = 0; c < env.cols(); c++)
                if (env.hasFood(r, c)) left++;
        return foodAtStart - left;
    }

    private static int countFood(String[] map) {
        int n = 0;
        for (String row : map)
            for (int c = 0; c < row.length(); c++)
                if (row.charAt(c) == '.' || row.charAt(c) == '*') n++;
        return n;
    }

    private static int countGhosts(String[] map) {
        int n = 0;
        for (String row : map)
            for (int c = 0; c < row.length(); c++)
                if (row.charAt(c) == 'G') n++;
        return n;
    }

    // --- Chargement & conversion d'une vraie carte ---------------------------

    static String[] loadRealMap(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            List<String> rows = new ArrayList<>();
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // ligne de paramètres
                if (line.isEmpty()) continue;
                StringBuilder sb = new StringBuilder(line.length());
                for (int i = 0; i < line.length(); i++) {
                    char ch = line.charAt(i);
                    switch (ch) {
                        case 'F': sb.append('G'); break; // fantôme
                        case 'B': sb.append('P'); break; // Pacman (sur case fantôme)
                        default:  sb.append(ch);          // '#', '.', '*', 'O', 'P'
                    }
                }
                rows.add(sb.toString());
            }
            return rows.toArray(new String[0]);
        } catch (Exception e) {
            return null;
        }
    }
}
