package logic;

import java.util.*;

/**
 * BeliefStateII (STRICT VERSION):
 *
 * Requirement:
 * - If a ghost is not CONFIRMED visible for > 10 pacman turns, DROP it completely (disappear).
 * - NO "random placement" / NO "fallback prior". If belief becomes empty -> DROP.
 *
 * Observation consistency:
 * - If any belief mass is on visible cells, we treat the ghost as seen and collapse to those visible cells.
 * - If ghost is not seen, remove any states that would be visible (impossible while unseen).
 *
 * Used ONLY by AI for planning.
 */
public class BeliefStateII {

    private static final int DROP_AFTER_TURNS = 10;

    public static class GhostBelief {
        public final HashMap<Position, Double> prob = new HashMap<>();
        public int fear = 0;

        // STRICT: count how many pacman turns since last confirmed sighting
        public int unseenTurns = 0;

        public double totalProb() {
            double s = 0;
            for (double v : prob.values()) s += v;
            return s;
        }

        public void normalize() {
            double s = totalProb();
            if (s <= 0) return;
            for (Map.Entry<Position, Double> e : prob.entrySet()) {
                e.setValue(e.getValue() / s);
            }
        }
    }

    private final char[][] map;
    private final int H, W;

    private Position pac;
    private Position pacOld;

    private int score;
    int life;
    private int nbrGommes;
    private int nbrSuper;

    private int turn;

    final ArrayList<GhostBelief> ghosts = new ArrayList<>();

    public BeliefStateII(BeliefState bs) {
        this.map = deepCopy(bs.getMap());
        this.H = map.length;
        this.W = map[0].length;

        this.pac = copyPos(bs.getPacmanPos());
        this.pacOld = copyPos(bs.getPacmanOldPosition());

        this.score = bs.getScore();
        this.life = bs.getLife();
        this.nbrGommes = bs.getNbrOfGommes();
        this.nbrSuper = bs.getNbrOfSuperGommes();

        this.turn = 0;

        int nGhost;
        try { nGhost = bs.getNbrOfGhost(); }
        catch (Exception e) { nGhost = guessGhostCount(bs); }

        for (int i = 0; i < nGhost; i++) {
            GhostBelief gb = new GhostBelief();
            TreeSet<Position> poss = bs.getGhostPositions(i);
            if (poss != null && !poss.isEmpty()) {
                double p = 1.0 / poss.size();
                for (Position g : poss) gb.prob.put(copyPos(g), p);
            }
            try { gb.fear = bs.getCompteurPeur(i); } catch (Exception e) { gb.fear = 0; }

            // unseenTurns starts at 0; at turn0 we haven't advanced time yet
            gb.unseenTurns = 0;

            gb.normalize();
            ghosts.add(gb);
        }

        // Apply observation consistency at turn 0 (may mark some ghosts as seen)
        applyObservationAndDrop(ghosts, pac);
    }

    private BeliefStateII(char[][] map, int H, int W, Position pac, Position pacOld,
                          int score, int life, int nbrGommes, int nbrSuper,
                          int turn,
                          ArrayList<GhostBelief> ghosts) {
        this.map = map;
        this.H = H;
        this.W = W;
        this.pac = pac;
        this.pacOld = pacOld;
        this.score = score;
        this.life = life;
        this.nbrGommes = nbrGommes;
        this.nbrSuper = nbrSuper;
        this.turn = turn;
        this.ghosts.addAll(ghosts);
    }

    /* ========================== Getters ========================== */

    public char[][] getMap() { return map; }
    public Position getPacmanPos() { return pac; }
    public Position getPacmanOldPos() { return pacOld; }
    public int getScore() { return score; }
    public int getLife() { return life; }
    public int getNbrGommes() { return nbrGommes; }
    public int getNbrSuper() { return nbrSuper; }
    public int getNbrGhost() { return ghosts.size(); }
    public GhostBelief getGhostBelief(int i) { return ghosts.get(i); }

    /* ========================== Pacman step ========================== */

    public boolean isWall(int x, int y) {
        return x < 0 || y < 0 || x >= H || y >= W || map[x][y] == '#';
    }

    public List<String> legalPacActions() {
        ArrayList<String> acts = new ArrayList<>(4);
        if (!isWall(pac.x - 1, pac.y)) acts.add(PacManLauncher.UP);
        if (!isWall(pac.x + 1, pac.y)) acts.add(PacManLauncher.DOWN);
        if (!isWall(pac.x, pac.y - 1)) acts.add(PacManLauncher.LEFT);
        if (!isWall(pac.x, pac.y + 1)) acts.add(PacManLauncher.RIGHT);
        if (acts.isEmpty()) acts.add(PacManLauncher.RIGHT);
        return acts;
    }

    public BeliefStateII afterPacman(String act) {
        int dx = 0, dy = 0;
        char dir = 'U';
        if (PacManLauncher.UP.equals(act)) { dx = -1; dy = 0; dir = 'U'; }
        else if (PacManLauncher.DOWN.equals(act)) { dx = 1; dy = 0; dir = 'D'; }
        else if (PacManLauncher.LEFT.equals(act)) { dx = 0; dy = -1; dir = 'L'; }
        else if (PacManLauncher.RIGHT.equals(act)) { dx = 0; dy = 1; dir = 'R'; }

        int nx = pac.x + dx, ny = pac.y + dy;

        char[][] m2 = deepCopy(map);
        Position oldP = copyPos(pac);
        Position newP = copyPos(pac);

        if (!isWall(nx, ny)) {
            newP.x = nx; newP.y = ny; newP.dir = dir;
        } else {
            newP.dir = dir;
        }

        int score2 = score;
        int life2 = life;
        int g2 = nbrGommes;
        int s2 = nbrSuper;

        char cell = m2[newP.x][newP.y];
        if (cell == '.') {
            m2[newP.x][newP.y] = ' ';
            g2 = Math.max(0, g2 - 1);
            score2 += 10;
        } else if (cell == '*') {
            m2[newP.x][newP.y] = ' ';
            s2 = Math.max(0, s2 - 1);
            score2 += 50;
        }

        ArrayList<GhostBelief> ghosts2 = deepCopyGhosts(ghosts);

        if (cell == '*') {
            for (GhostBelief gb : ghosts2) gb.fear = Ghost.TIME_PEUR;
        } else {
            for (GhostBelief gb : ghosts2) if (gb.fear > 0) gb.fear--;
        }

        int turn2 = this.turn + 1;

        // STRICT: after each pacman move, advance unseenTurns and drop if needed
        for (GhostBelief gb : ghosts2) gb.unseenTurns++;

        applyObservationAndDrop(ghosts2, newP);

        return new BeliefStateII(m2, H, W, newP, oldP, score2, life2, g2, s2, turn2, ghosts2);
    }

    /* ========================== Ghost prediction ========================== */

    public List<Outcome> predictGhosts(int topKPerGhost, int beamWidth) {
        ArrayList<Outcome> beam = new ArrayList<>();
        beam.add(new Outcome(this, 1.0));

        for (int gi = 0; gi < ghosts.size(); gi++) {
            ArrayList<Outcome> nextBeam = new ArrayList<>();
            GhostBelief gb = ghosts.get(gi);

            HashMap<Position, Double> nextDist = transitionGhost(gb);

            ArrayList<Map.Entry<Position, Double>> top = new ArrayList<>(nextDist.entrySet());
            top.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
            if (top.size() > topKPerGhost) top.subList(topKPerGhost, top.size()).clear();

            for (Outcome partial : beam) {
                for (Map.Entry<Position, Double> e : top) {
                    double p = partial.prob * e.getValue();
                    BeliefStateII st = partial.state.cloneShallow();

                    GhostBelief gbi = st.ghosts.get(gi);
                    gbi.prob.clear();
                    gbi.prob.put(copyPos(e.getKey()), 1.0);
                    gbi.normalize();

                    if (gbi.fear <= 0) {
                        Position gp = e.getKey();
                        if (gp.x == st.pac.x && gp.y == st.pac.y) st.life = 0;
                    }

                    nextBeam.add(new Outcome(st, p));
                }
            }

            nextBeam.sort((a, b) -> Double.compare(b.prob, a.prob));
            if (nextBeam.size() > beamWidth) nextBeam.subList(beamWidth, nextBeam.size()).clear();
            beam = nextBeam;
        }

        double sum = 0;
        for (Outcome o : beam) sum += o.prob;
        if (sum > 0) for (Outcome o : beam) o.prob /= sum;

        return beam;
    }

    private HashMap<Position, Double> transitionGhost(GhostBelief gb) {
        HashMap<Position, Double> out = new HashMap<>();

        // If a ghost belief is empty, in STRICT mode it should have been dropped already.
        // Still, keep it safe:
        if (gb.prob.isEmpty()) return out;

        for (Map.Entry<Position, Double> e : gb.prob.entrySet()) {
            Position g = e.getKey();
            double pg = e.getValue();

            ArrayList<Position> neigh = legalGhostMoves(g);

            if (neigh.isEmpty()) {
                addProb(out, copyPos(g), pg);
                continue;
            }

            boolean vis = BeliefState.isVisible(g.x, g.y, pac.x, pac.y);

            if (vis && gb.fear <= 0) {
                Position best = neigh.get(0);
                int bestD = manhattan(best.x, best.y, pac.x, pac.y);
                for (Position n : neigh) {
                    int d = manhattan(n.x, n.y, pac.x, pac.y);
                    if (d < bestD) { bestD = d; best = n; }
                }
                addProb(out, best, pg);
                continue;
            }

            if (gb.fear > 0) {
                double sumW = 0;
                double[] w = new double[neigh.size()];
                for (int i = 0; i < neigh.size(); i++) {
                    int d = manhattan(neigh.get(i).x, neigh.get(i).y, pac.x, pac.y);
                    w[i] = 1.0 + d;
                    sumW += w[i];
                }
                for (int i = 0; i < neigh.size(); i++) {
                    addProb(out, neigh.get(i), pg * (w[i] / sumW));
                }
                continue;
            }

            double sumW = 0;
            double[] w = new double[neigh.size()];
            for (int i = 0; i < neigh.size(); i++) {
                Position n = neigh.get(i);
                double wi = 1.0;
                if (n.dir == g.dir) wi *= 2.5;
                if (isOppositeDir(g.dir, n.dir)) wi *= 0.25;
                w[i] = wi;
                sumW += wi;
            }
            for (int i = 0; i < neigh.size(); i++) {
                addProb(out, neigh.get(i), pg * (w[i] / sumW));
            }
        }

        double s = 0;
        for (double v : out.values()) s += v;
        if (s > 0) {
            for (Map.Entry<Position, Double> e : out.entrySet()) {
                e.setValue(e.getValue() / s);
            }
        }
        return out;
    }

    private ArrayList<Position> legalGhostMoves(Position g) {
        ArrayList<Position> res = new ArrayList<>(4);
        if (!isWall(g.x - 1, g.y)) res.add(new Position(g.x - 1, g.y, 'U'));
        if (!isWall(g.x + 1, g.y)) res.add(new Position(g.x + 1, g.y, 'D'));
        if (!isWall(g.x, g.y - 1)) res.add(new Position(g.x, g.y - 1, 'L'));
        if (!isWall(g.x, g.y + 1)) res.add(new Position(g.x, g.y + 1, 'R'));
        return res;
    }

    /**
     * STRICT drop logic:
     * - If unseenTurns > DROP_AFTER_TURNS -> DROP
     * - If any mass is visible -> collapse to visible, unseenTurns=0 (CONFIRMED SEEN)
     * - Else prune visible states; if empty after prune -> DROP (NO RANDOM PLACEMENT)
     */
    private void applyObservationAndDrop(ArrayList<GhostBelief> gbs, Position pacPos) {
        if (pacPos == null) return;

        for (int i = gbs.size() - 1; i >= 0; i--) {
            GhostBelief gb = gbs.get(i);

            if (gb.unseenTurns > DROP_AFTER_TURNS) {
                gbs.remove(i);
                continue;
            }

            if (gb.prob.isEmpty()) {
                // STRICT: empty belief means we have no valid states -> drop (no random)
                gbs.remove(i);
                continue;
            }

            HashMap<Position, Double> visible = new HashMap<>();
            HashMap<Position, Double> invisible = new HashMap<>();

            for (Map.Entry<Position, Double> e : gb.prob.entrySet()) {
                Position pos = e.getKey();
                double pr = e.getValue();
                if (BeliefState.isVisible(pos.x, pos.y, pacPos.x, pacPos.y)) {
                    visible.put(copyPos(pos), pr);
                } else {
                    invisible.put(copyPos(pos), pr);
                }
            }

            if (!visible.isEmpty()) {
                // CONFIRMED SEEN -> collapse to visible, reset unseenTurns
                gb.prob.clear();
                gb.prob.putAll(visible);
                gb.normalize();
                gb.unseenTurns = 0;
            } else {
                // Not seen -> visible states are impossible; keep only invisible
                gb.prob.clear();
                gb.prob.putAll(invisible);
                gb.normalize();

                if (gb.prob.isEmpty()) {
                    // STRICT: do NOT "place" it anywhere -> drop it
                    gbs.remove(i);
                }
            }
        }
    }

    /* ========================== Utility / cloning ========================== */

    public BeliefStateII cloneShallow() {
        return new BeliefStateII(map, H, W, copyPos(pac), copyPos(pacOld),
                score, life, nbrGommes, nbrSuper,
                turn,
                deepCopyGhosts(ghosts));
    }

    public static class Outcome {
        public final BeliefStateII state;
        public double prob;
        public Outcome(BeliefStateII s, double p) { this.state = s; this.prob = p; }
    }

    private static void addProb(HashMap<Position, Double> m, Position p, double v) {
        Double old = m.get(p);
        if (old == null) m.put(p, v);
        else m.put(p, old + v);
    }

    private static int manhattan(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2; if (dx < 0) dx = -dx;
        int dy = y1 - y2; if (dy < 0) dy = -dy;
        return dx + dy;
    }

    private static boolean isOppositeDir(char a, char b) {
        return (a == 'U' && b == 'D') || (a == 'D' && b == 'U') ||
               (a == 'L' && b == 'R') || (a == 'R' && b == 'L');
    }

    private static Position copyPos(Position p) {
        if (p == null) return null;
        return new Position(p.x, p.y, p.dir);
    }

    private static char[][] deepCopy(char[][] m) {
        char[][] c = new char[m.length][];
        for (int i = 0; i < m.length; i++) c[i] = Arrays.copyOf(m[i], m[i].length);
        return c;
    }

    private static ArrayList<GhostBelief> deepCopyGhosts(ArrayList<GhostBelief> src) {
        ArrayList<GhostBelief> out = new ArrayList<>();
        for (GhostBelief g : src) {
            GhostBelief ng = new GhostBelief();
            ng.fear = g.fear;
            ng.unseenTurns = g.unseenTurns;
            for (Map.Entry<Position, Double> e : g.prob.entrySet()) {
                ng.prob.put(copyPos(e.getKey()), e.getValue());
            }
            ng.normalize();
            out.add(ng);
        }
        return out;
    }

    private static int guessGhostCount(BeliefState s) {
        int n = 0;
        while (true) {
            try {
                TreeSet<Position> pos = s.getGhostPositions(n);
                if (pos == null) break;
                n++;
                if (n > 16) break;
            } catch (Exception e) {
                break;
            }
        }
        return n;
    }

    public int distanceToNearestGum() {
        boolean[][] vis = new boolean[H][W];
        ArrayDeque<int[]> q = new ArrayDeque<>();
        q.add(new int[]{pac.x, pac.y, 0});
        vis[pac.x][pac.y] = true;

        while (!q.isEmpty()) {
            int[] cur = q.removeFirst();
            int x = cur[0], y = cur[1], d = cur[2];

            char c = map[x][y];
            if (c == '.' || c == '*') return d;

            int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
            for (int[] dd : dirs) {
                int nx = x + dd[0], ny = y + dd[1];
                if (nx < 0 || ny < 0 || nx >= H || ny >= W) continue;
                if (vis[nx][ny]) continue;
                if (map[nx][ny] == '#') continue;
                vis[nx][ny] = true;
                q.add(new int[]{nx, ny, d + 1});
            }
        }
        return 9999;
    }
}
