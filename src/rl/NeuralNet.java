package rl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

/**
 * Reseau de neurones minimal (perceptron multicouche a 1 couche cachee),
 * implemente entierement a la main (sans bibliotheque externe), pour le DQN.
 *
 *   entree x (taille I)  ->  cachee h = tanh(W1 x + b1)  (taille H)
 *                        ->  sortie q = W2 h + b2        (taille O = 4 actions)
 *
 * La sortie est lineaire : ce sont les Q-valeurs Q(s, a) pour les 4 actions.
 * On entraine par descente de gradient sur l'erreur quadratique d'UNE seule
 * sortie (l'action jouee), comme dans le DQN (les autres actions n'ont pas de
 * cible et ne propagent pas de gradient).
 */
public class NeuralNet {

    final int I, H, O;
    final double[][] W1; // [H][I]
    final double[] b1;   // [H]
    final double[][] W2; // [O][H]
    final double[] b2;   // [O]

    public NeuralNet(int inputs, int hidden, int outputs, long seed) {
        this.I = inputs;
        this.H = hidden;
        this.O = outputs;
        this.W1 = new double[H][I];
        this.b1 = new double[H];
        this.W2 = new double[O][H];
        this.b2 = new double[O];
        Random rng = new Random(seed);
        // Initialisation de type Xavier/He pour des entrees/sorties stables.
        double s1 = Math.sqrt(2.0 / I);
        double s2 = Math.sqrt(2.0 / H);
        for (int j = 0; j < H; j++)
            for (int i = 0; i < I; i++) W1[j][i] = rng.nextGaussian() * s1;
        for (int o = 0; o < O; o++)
            for (int j = 0; j < H; j++) W2[o][j] = rng.nextGaussian() * s2;
    }

    /** Passe avant : retourne les Q-valeurs (taille O). */
    public double[] forward(double[] x) {
        double[] h = new double[H];
        for (int j = 0; j < H; j++) {
            double s = b1[j];
            double[] w = W1[j];
            for (int i = 0; i < I; i++) s += w[i] * x[i];
            h[j] = Math.tanh(s);
        }
        double[] q = new double[O];
        for (int o = 0; o < O; o++) {
            double s = b2[o];
            double[] w = W2[o];
            for (int j = 0; j < H; j++) s += w[j] * h[j];
            q[o] = s;
        }
        return q;
    }

    /**
     * Un pas de descente de gradient pour faire tendre Q(x)[action] vers
     * {@code target}. Retourne l'erreur TD (q[action] - target) avant mise a
     * jour, utile pour le suivi.
     */
    public double trainStep(double[] x, int action, double target, double lr) {
        // --- avant (en gardant h) ---
        double[] h = new double[H];
        double[] pre = new double[H];
        for (int j = 0; j < H; j++) {
            double s = b1[j];
            double[] w = W1[j];
            for (int i = 0; i < I; i++) s += w[i] * x[i];
            pre[j] = s;
            h[j] = Math.tanh(s);
        }
        double q = b2[action];
        double[] w2a = W2[action];
        for (int j = 0; j < H; j++) q += w2a[j] * h[j];

        double err = q - target;          // dL/dq[action]
        // borne le gradient pour la stabilite (les cibles peuvent etre grandes)
        double g = Math.max(-1.0, Math.min(1.0, err));

        // --- retropropagation (seule la sortie 'action' a un gradient) ---
        // couche de sortie
        double[] dh = new double[H];
        for (int j = 0; j < H; j++) {
            dh[j] = w2a[j] * g;
            w2a[j] -= lr * g * h[j];
        }
        b2[action] -= lr * g;
        // couche cachee (tanh' = 1 - h^2)
        for (int j = 0; j < H; j++) {
            double dpre = dh[j] * (1.0 - h[j] * h[j]);
            double[] w1 = W1[j];
            for (int i = 0; i < I; i++) w1[i] -= lr * dpre * x[i];
            b1[j] -= lr * dpre;
        }
        return err;
    }

    /** Copie profonde des poids dans this depuis other (reseau cible). */
    public void copyFrom(NeuralNet o) {
        for (int j = 0; j < H; j++) System.arraycopy(o.W1[j], 0, W1[j], 0, I);
        System.arraycopy(o.b1, 0, b1, 0, H);
        for (int a = 0; a < O; a++) System.arraycopy(o.W2[a], 0, W2[a], 0, H);
        System.arraycopy(o.b2, 0, b2, 0, O);
    }

    // --- Persistance simple (texte) ------------------------------------------

    public void save(String path) throws Exception {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path))) {
            w.write(I + " " + H + " " + O); w.newLine();
            for (int j = 0; j < H; j++) { for (int i = 0; i < I; i++) { w.write(W1[j][i] + " "); } }
            w.newLine();
            for (int j = 0; j < H; j++) w.write(b1[j] + " ");
            w.newLine();
            for (int a = 0; a < O; a++) { for (int j = 0; j < H; j++) { w.write(W2[a][j] + " "); } }
            w.newLine();
            for (int a = 0; a < O; a++) w.write(b2[a] + " ");
            w.newLine();
        }
    }

    public static NeuralNet load(String path) throws Exception {
        try (BufferedReader r = new BufferedReader(new FileReader(path))) {
            String[] dim = r.readLine().trim().split("\\s+");
            NeuralNet n = new NeuralNet(Integer.parseInt(dim[0]),
                    Integer.parseInt(dim[1]), Integer.parseInt(dim[2]), 0);
            String[] t = r.readLine().trim().split("\\s+");
            int k = 0;
            for (int j = 0; j < n.H; j++) for (int i = 0; i < n.I; i++) n.W1[j][i] = Double.parseDouble(t[k++]);
            t = r.readLine().trim().split("\\s+");
            for (int j = 0; j < n.H; j++) n.b1[j] = Double.parseDouble(t[j]);
            t = r.readLine().trim().split("\\s+");
            k = 0;
            for (int a = 0; a < n.O; a++) for (int j = 0; j < n.H; j++) n.W2[a][j] = Double.parseDouble(t[k++]);
            t = r.readLine().trim().split("\\s+");
            for (int a = 0; a < n.O; a++) n.b2[a] = Double.parseDouble(t[a]);
            return n;
        }
    }
}
