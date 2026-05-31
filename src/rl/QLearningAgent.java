package rl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Agent d'apprentissage par renforcement : Q-learning approché (linéaire).
 *
 *   Q(s, a) = w . f(s, a)
 *
 * Mise à jour par différence temporelle (TD) après chaque transition
 * (s, a, r, s') :
 *
 *   delta = (r + gamma * max_{a'} Q(s', a')) - Q(s, a)
 *   w_i  <- w_i + alpha * delta * f_i(s, a)
 *
 * Exploration : epsilon-greedy avec décroissance d'epsilon.
 */
public class QLearningAgent {

    private final double[] weights;
    private double alpha;        // taux d'apprentissage
    private final double gamma;  // facteur d'actualisation
    private double epsilon;      // taux d'exploration
    private final Random rng;

    public QLearningAgent(double alpha, double gamma, double epsilon, long seed) {
        this.weights = new double[Features.N];
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.rng = new Random(seed);
    }

    /** Construit un agent à partir de poids déjà appris (politique gloutonne). */
    public QLearningAgent(double[] learnedWeights) {
        this.weights = learnedWeights.clone();
        this.alpha = 0.0;
        this.gamma = 0.9;
        this.epsilon = 0.0;
        this.rng = new Random(0);
    }

    public double[] getWeights() {
        return weights.clone();
    }

    public void setEpsilon(double eps) {
        this.epsilon = eps;
    }

    public void setAlpha(double a) {
        this.alpha = a;
    }

    /** Q(s, a) = produit scalaire des poids et des features. */
    public double qValue(GameView v, int action) {
        double[] f = Features.compute(v, action);
        double q = 0.0;
        for (int i = 0; i < weights.length; i++) {
            q += weights[i] * f[i];
        }
        return q;
    }

    /** Meilleure valeur d'action dans l'état v parmi les actions légales. */
    public double maxQ(GameView v, List<Integer> legal) {
        if (legal.isEmpty()) return 0.0;
        double best = Double.NEGATIVE_INFINITY;
        for (int a : legal) {
            best = Math.max(best, qValue(v, a));
        }
        return best;
    }

    /** Action gloutonne (argmax Q), les égalités étant départagées aléatoirement. */
    public int greedyAction(GameView v, List<Integer> legal) {
        double best = Double.NEGATIVE_INFINITY;
        int chosen = legal.get(0);
        int ties = 0;
        for (int a : legal) {
            double q = qValue(v, a);
            if (q > best + 1e-9) {
                best = q;
                chosen = a;
                ties = 1;
            } else if (Math.abs(q - best) <= 1e-9) {
                ties++;
                if (rng.nextInt(ties) == 0) {
                    chosen = a;
                }
            }
        }
        return chosen;
    }

    /** Sélection epsilon-greedy utilisée pendant l'entraînement. */
    public int act(GameView v, List<Integer> legal) {
        if (rng.nextDouble() < epsilon) {
            return legal.get(rng.nextInt(legal.size()));
        }
        return greedyAction(v, legal);
    }

    /**
     * Mise à jour TD des poids après une transition.
     *
     * @param s       état de départ
     * @param action  action effectuée
     * @param reward  récompense reçue
     * @param sNext   état d'arrivée
     * @param legalNext  actions légales dans sNext
     * @param done    true si sNext est terminal
     */
    public void update(GameView s, int action, double reward,
                       GameView sNext, List<Integer> legalNext, boolean done) {
        double[] f = Features.compute(s, action);
        double sample = reward;
        if (!done) {
            sample += gamma * maxQ(sNext, legalNext);
        }
        double delta = sample - dot(f);
        for (int i = 0; i < weights.length; i++) {
            weights[i] += alpha * delta * f[i];
        }
    }

    private double dot(double[] f) {
        double q = 0.0;
        for (int i = 0; i < weights.length; i++) {
            q += weights[i] * f[i];
        }
        return q;
    }

    // --- Persistance des poids -------------------------------------------------

    public void save(String path) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path))) {
            for (int i = 0; i < weights.length; i++) {
                w.write(Features.NAMES[i] + " " + weights[i]);
                w.newLine();
            }
        }
    }

    public static double[] load(String path) throws IOException {
        double[] w = new double[Features.N];
        try (BufferedReader r = new BufferedReader(new FileReader(path))) {
            String line;
            int i = 0;
            while ((line = r.readLine()) != null && i < w.length) {
                String[] parts = line.trim().split("\\s+");
                w[i++] = Double.parseDouble(parts[parts.length - 1]);
            }
        }
        return w;
    }
}
