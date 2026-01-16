package logic;

import java.util.*;
//projet

/**
 * class used to represent plan. It will provide for a given set of results an action to perform in each result
 */
class Plans{
	ArrayList<Result> results;
	ArrayList<ArrayList<String>> actions;
	
	/**
	 * construct an empty plan
	 */
	public Plans() {
		this.results = new ArrayList<Result>();
		this.actions = new ArrayList<ArrayList<String>>();
	}
	
	/**
	 * add a new pair of belief-state and corresponding (equivalent) actions 
	 * @param beliefBeliefState the belief state to add
	 * @param action a list of alternative actions to perform. Only one of them is chosen but their results should be similar
	 */
	public void addPlan(Result beliefBeliefState, ArrayList<String> action) {
		this.results.add(beliefBeliefState);
		this.actions.add(action);
	}
	
	/**
	 * return the number of belief-states/actions pairs
	 * @return the number of belief-states/actions pairs
	 */
	public int size() {
		return this.results.size();
	}
	
	/**
	 * return one of the belief-state of the plan
	 * @param index index of the belief-state
	 * @return the belief-state corresponding to the index
	 */
	public Result getResult(int index) {
		return this.results.get(index);
	}
	
	/**
	 * return the list of actions performed for a given belief-state
	 * @param index index of the belief-state
	 * @return the set of actions to perform for the belief-state corresponding to the index
	 */
	public ArrayList<String> getAction(int index){
		return this.actions.get(index);
	}
}

/**
 * class used to represent a transition function i.e., a set of possible belief states the agent may be in after performing an action
 */
class Result{
	private ArrayList<BeliefState> beliefStates;

	/**
	 * construct a new result
	 * @param states the set of states corresponding to the new belief state
	 */
	public Result(ArrayList<BeliefState> states) {
		this.beliefStates = states;
	}

	/**
	 * returns the number of belief states
	 * @return the number of belief states
	 */
	public int size() {
		return this.beliefStates.size();
	}

	/**
	 * return one of the belief state
	 * @param index the index of the belief state to return
	 * @return the belief state to return
	 */
	public BeliefState getBeliefState(int index) {
		return this.beliefStates.get(index);
	}
	
	/**
	 * return the list of belief-states
	 * @return the list of belief-states
	 */
	public ArrayList<BeliefState> getBeliefStates(){
		return this.beliefStates;
	}
}


/**
 * class implement the AI to choose the next move of the Pacman
 */

//public class AI {
//
//    // Current target gum position
//    private static int targetX = -1;
//    private static int targetY = -1;
//
//    // Ghost memory: remembers the last seen position of each ghost and the remaining memory time
//    private static Map<Integer, int[]> ghostMemory = new HashMap<>();  // ghostId -> [x, y, remainingTurns]
//    private static final int MEMORY_DURATION = 4;  // Remember ghost position for 4 steps
//
//    // Loop detection: records recent positions
//    private static LinkedList<String> positionHistory = new LinkedList<>();
//    private static final int HISTORY_SIZE = 16;  // Record the last 16 steps
//    private static final int LOOP_THRESHOLD = 3;  // If the same position appears 3+ times, it's considered looping
//
//    // Random target while looping
//    private static int randomTargetX = -1;
//    private static int randomTargetY = -1;
//    private static int randomTargetCooldown = 0;  // Number of steps to lock the random target
//
//    /**
//     * Main entry point
//     */
//    public static String findNextMove(BeliefState state) {
//        if (state == null || state.getLife() <= 0) {
//            resetAll();
//            return PacManLauncher.RIGHT;
//        }
//
//        Position pacman = state.getPacmanPosition();
//        char[][] map = state.getMap();
//
//        // Record position history
//        recordPosition(pacman.x, pacman.y);
//
//        // Update ghost memory
//        updateGhostMemory(state, pacman);
//
//        // Check whether there is danger (visible ghosts OR remembered ghosts)
//        boolean hasDanger = hasDangerousGhost(state, pacman);
//
//        if (hasDanger) {
//            // Danger!
//            resetTarget();  // Clear normal target
//
//            // Check if looping
//            if (isLooping()) {
//                // Loop detected! Find a random safe gum
//                return escapeToRandomGum(state, pacman, map);
//            } else {
//                // Normal escape
//                return escapeAndEat(state, pacman, map);
//            }
//        } else {
//            // Safe, focus on eating gums
//            resetRandomTarget();  // Clear random target
//            clearHistory();  // Clear history (no need to detect loops when safe)
//            return goToNearestGum(state, pacman, map);
//        }
//    }
//
//    /**
//     * Record position
//     */
//    private static void recordPosition(int x, int y) {
//        String pos = x + "," + y;
//        positionHistory.addLast(pos);
//        if (positionHistory.size() > HISTORY_SIZE) {
//            positionHistory.removeFirst();
//        }
//    }
//
//    /**
//     * Check whether it's looping
//     */
//    private static boolean isLooping() {
//        if (positionHistory.size() < HISTORY_SIZE / 2) {
//            return false;
//        }
//
//        // Count how many times each position appears
//        Map<String, Integer> counts = new HashMap<>();
//        for (String pos : positionHistory) {
//            counts.put(pos, counts.getOrDefault(pos, 0) + 1);
//        }
//
//        // If any position count >= threshold, it means looping
//        for (int count : counts.values()) {
//            if (count >= LOOP_THRESHOLD) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * While looping: find a random safe gum and lock onto it
//     */
//    private static String escapeToRandomGum(BeliefState state, Position pacman, char[][] map) {
//        List<int[]> dangers = getAllDangerPositions(state, pacman);
//
//        // If still locking onto a random target
//        if (randomTargetCooldown > 0 && randomTargetX >= 0 && randomTargetY >= 0) {
//            char cell = map[randomTargetX][randomTargetY];
//            if (cell == '.' || cell == '*') {
//                // Target still exists, keep moving
//                randomTargetCooldown--;
//                return moveTowardSafely(pacman.x, pacman.y, randomTargetX, randomTargetY, map, dangers);
//            } else {
//                // Target was eaten, find a new one
//                resetRandomTarget();
//            }
//        }
//
//        // Find a random safe gum
//        int[] randomGum = findRandomSafeGum(pacman, map, dangers);
//        if (randomGum != null) {
//            randomTargetX = randomGum[0];
//            randomTargetY = randomGum[1];
//            randomTargetCooldown = 10;  // Lock for 10 steps
//            clearHistory();  // Clear history and restart detection
//            return moveTowardSafely(pacman.x, pacman.y, randomTargetX, randomTargetY, map, dangers);
//        }
//
//        // If no random target found, use normal escape
//        return escapeAndEat(state, pacman, map);
//    }
//
//    /**
//     * Find a random safe gum
//     */
//    private static int[] findRandomSafeGum(Position pacman, char[][] map, List<int[]> dangers) {
//        // BFS to find all gums
//        Queue<int[]> queue = new LinkedList<>();
//        Set<String> visited = new HashSet<>();
//        List<int[]> safeGums = new ArrayList<>();
//
//        queue.offer(new int[]{pacman.x, pacman.y});
//        visited.add(pacman.x + "," + pacman.y);
//
//        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//
//        while (!queue.isEmpty()) {
//            int[] curr = queue.poll();
//            int x = curr[0], y = curr[1];
//
//            char cell = map[x][y];
//            if (cell == '.' || cell == '*') {
//                // Check if this gum is safe (far enough from ghosts)
//                double minDist = Double.MAX_VALUE;
//                for (int[] danger : dangers) {
//                    double dist = Math.abs(danger[0] - x) + Math.abs(danger[1] - y);
//                    minDist = Math.min(minDist, dist);
//                }
//                if (minDist >= 3) {  // At least 3 tiles away from ghosts
//                    safeGums.add(new int[]{x, y});
//                }
//            }
//
//            for (int[] delta : deltas) {
//                int nx = x + delta[0];
//                int ny = y + delta[1];
//                String key = nx + "," + ny;
//
//                if (isValid(nx, ny, map) && !visited.contains(key)) {
//                    visited.add(key);
//                    queue.offer(new int[]{nx, ny});
//                }
//            }
//        }
//
//        if (safeGums.isEmpty()) {
//            return null;
//        }
//
//        // Pick one randomly
//        Random rand = new Random();
//        return safeGums.get(rand.nextInt(safeGums.size()));
//    }
//
//    /**
//     * Move toward the target while avoiding danger
//     */
//    private static String moveTowardSafely(int fromX, int fromY, int toX, int toY, char[][] map, List<int[]> dangers) {
//        String[] directions = {PacManLauncher.UP, PacManLauncher.DOWN,
//                               PacManLauncher.LEFT, PacManLauncher.RIGHT};
//        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//
//        Queue<int[]> queue = new LinkedList<>();
//        Map<String, String> firstMove = new HashMap<>();
//
//        String startKey = fromX + "," + fromY;
//        queue.offer(new int[]{fromX, fromY});
//        firstMove.put(startKey, null);
//
//        while (!queue.isEmpty()) {
//            int[] curr = queue.poll();
//            int x = curr[0], y = curr[1];
//            String currKey = x + "," + y;
//
//            if (x == toX && y == toY) {
//                return firstMove.get(currKey);
//            }
//
//            for (int i = 0; i < 4; i++) {
//                int nx = x + deltas[i][0];
//                int ny = y + deltas[i][1];
//                String nextKey = nx + "," + ny;
//
//                if (isValid(nx, ny, map) && !firstMove.containsKey(nextKey)) {
//                    // The first step cannot enter a dangerous area
//                    if (currKey.equals(startKey)) {
//                        boolean isDangerous = false;
//                        for (int[] danger : dangers) {
//                            if (danger[0] == nx && danger[1] == ny) {
//                                isDangerous = true;
//                                break;
//                            }
//                            // Also avoid being too close to ghosts
//                            int dist = Math.abs(danger[0] - nx) + Math.abs(danger[1] - ny);
//                            if (dist <= 1) {
//                                isDangerous = true;
//                                break;
//                            }
//                        }
//                        if (isDangerous) continue;
//                    }
//
//                    if (currKey.equals(startKey)) {
//                        firstMove.put(nextKey, directions[i]);
//                    } else {
//                        firstMove.put(nextKey, firstMove.get(currKey));
//                    }
//                    queue.offer(new int[]{nx, ny});
//                }
//            }
//        }
//
//        // No safe path found, fallback to scoring method
//        return escapeByScoring(fromX, fromY, map, dangers);
//    }
//
//    /**
//     * Choose direction by scoring (fallback)
//     */
//    private static String escapeByScoring(int px, int py, char[][] map, List<int[]> dangers) {
//        String[] directions = {PacManLauncher.UP, PacManLauncher.DOWN,
//                               PacManLauncher.LEFT, PacManLauncher.RIGHT};
//        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//
//        String bestMove = null;
//        double bestScore = Double.NEGATIVE_INFINITY;
//
//        for (int i = 0; i < 4; i++) {
//            int nx = px + deltas[i][0];
//            int ny = py + deltas[i][1];
//
//            if (!isValid(nx, ny, map)) continue;
//
//            double score = 0.0;
//
//            double minGhostDist = Double.MAX_VALUE;
//            for (int[] danger : dangers) {
//                double dist = Math.abs(danger[0] - nx) + Math.abs(danger[1] - ny);
//                minGhostDist = Math.min(minGhostDist, dist);
//            }
//
//            if (minGhostDist <= 1) {
//                score -= 10000;
//            } else if (minGhostDist <= 2) {
//                score -= 3000;
//            } else {
//                score += minGhostDist * 100;
//            }
//
//            char cell = map[nx][ny];
//            if (cell == '*') score += 5000;
//            else if (cell == '.') score += 2000;
//
//            int openDirs = countOpenDirections(nx, ny, map);
//            score += openDirs * 200;
//            if (openDirs <= 1) score -= 500;
//
//            if (score > bestScore) {
//                bestScore = score;
//                bestMove = directions[i];
//            }
//        }
//
//        return bestMove != null ? bestMove : PacManLauncher.RIGHT;
//    }
//
//    /**
//     * Update ghost memory
//     */
//    private static void updateGhostMemory(BeliefState state, Position pacman) {
//        Iterator<Map.Entry<Integer, int[]>> it = ghostMemory.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<Integer, int[]> entry = it.next();
//            int[] mem = entry.getValue();
//            mem[2]--;
//            if (mem[2] <= 0) {
//                it.remove();
//            }
//        }
//
//        for (int i = 0; i < state.getNbrOfGhost(); i++) {
//            if (state.getCompteurPeur(i) > 0) {
//                ghostMemory.remove(i);
//                continue;
//            }
//
//            TreeSet<Position> ghosts = state.getGhostPositions(i);
//            for (Position ghost : ghosts) {
//                if (BeliefState.isVisible(ghost.x, ghost.y, pacman.x, pacman.y)) {
//                    ghostMemory.put(i, new int[]{ghost.x, ghost.y, MEMORY_DURATION});
//                }
//            }
//        }
//    }
//
//    /**
//     * Check whether there is a dangerous ghost
//     */
//    private static boolean hasDangerousGhost(BeliefState state, Position pacman) {
//        for (int i = 0; i < state.getNbrOfGhost(); i++) {
//            if (state.getCompteurPeur(i) > 0) continue;
//
//            TreeSet<Position> ghosts = state.getGhostPositions(i);
//            for (Position ghost : ghosts) {
//                if (BeliefState.isVisible(ghost.x, ghost.y, pacman.x, pacman.y)) {
//                    int dist = Math.abs(ghost.x - pacman.x) + Math.abs(ghost.y - pacman.y);
//                    if (dist <= 10) {
//                        return true;
//                    }
//                }
//            }
//        }
//
//        for (int[] mem : ghostMemory.values()) {
//            int dist = Math.abs(mem[0] - pacman.x) + Math.abs(mem[1] - pacman.y);
//            if (dist <= 4) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * Get all dangerous positions
//     */
//    private static List<int[]> getAllDangerPositions(BeliefState state, Position pacman) {
//        List<int[]> dangers = new ArrayList<>();
//
//        for (int i = 0; i < state.getNbrOfGhost(); i++) {
//            if (state.getCompteurPeur(i) > 0) continue;
//
//            TreeSet<Position> ghosts = state.getGhostPositions(i);
//            for (Position ghost : ghosts) {
//                if (BeliefState.isVisible(ghost.x, ghost.y, pacman.x, pacman.y)) {
//                    dangers.add(new int[]{ghost.x, ghost.y});
//                }
//            }
//        }
//
//        for (Map.Entry<Integer, int[]> entry : ghostMemory.entrySet()) {
//            int[] mem = entry.getValue();
//            boolean exists = false;
//            for (int[] d : dangers) {
//                if (d[0] == mem[0] && d[1] == mem[1]) {
//                    exists = true;
//                    break;
//                }
//            }
//            if (!exists) {
//                dangers.add(new int[]{mem[0], mem[1]});
//            }
//        }
//
//        return dangers;
//    }
//
//    /**
//     * When there is no ghost: BFS to find the nearest gum
//     */
//    private static String goToNearestGum(BeliefState state, Position pacman, char[][] map) {
//        if (targetX >= 0 && targetY >= 0) {
//            char cell = map[targetX][targetY];
//            if (cell != '.' && cell != '*') {
//                resetTarget();
//            }
//        }
//
//        if (targetX < 0 || targetY < 0) {
//            int[] nearest = bfsFindNearestGum(pacman.x, pacman.y, map);
//            if (nearest != null) {
//                targetX = nearest[0];
//                targetY = nearest[1];
//            } else {
//                return PacManLauncher.RIGHT;
//            }
//        }
//
//        return moveToward(pacman.x, pacman.y, targetX, targetY, map);
//    }
//
//    /**
//     * When there are ghosts: normal escape logic
//     */
//    private static String escapeAndEat(BeliefState state, Position pacman, char[][] map) {
//        String[] directions = {PacManLauncher.UP, PacManLauncher.DOWN,
//                               PacManLauncher.LEFT, PacManLauncher.RIGHT};
//        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//
//        List<int[]> dangers = getAllDangerPositions(state, pacman);
//
//        String bestMove = null;
//        double bestScore = Double.NEGATIVE_INFINITY;
//
//        for (int i = 0; i < 4; i++) {
//            int nx = pacman.x + deltas[i][0];
//            int ny = pacman.y + deltas[i][1];
//
//            if (!isValid(nx, ny, map)) continue;
//
//            double score = 0.0;
//
//            double minGhostDist = Double.MAX_VALUE;
//            for (int[] danger : dangers) {
//                double dist = Math.abs(danger[0] - nx) + Math.abs(danger[1] - ny);
//                minGhostDist = Math.min(minGhostDist, dist);
//            }
//
//            if (minGhostDist <= 1) {
//                score -= 10000;
//            } else if (minGhostDist <= 2) {
//                score -= 3000;
//            } else {
//                score += minGhostDist * 100;
//            }
//
//            char cell = map[nx][ny];
//            if (cell == '*') {
//                score += 5000;
//            } else if (cell == '.') {
//                score += 2000;
//            }
//
//            if (state.getNbrOfSuperGommes() > 0) {
//                int[] nearestGum = bfsFindNearestSuperGum(nx, ny, map);
//                if (nearestGum != null) {
//                    int distToGum = Math.abs(nearestGum[0] - nx) + Math.abs(nearestGum[1] - ny);
//                    score += (20 - Math.min(distToGum, 20)) * 50;
//                }
//            } else {
//                int[] nearestGum = bfsFindNearestGum(nx, ny, map);
//                if (nearestGum != null) {
//                    int distToGum = Math.abs(nearestGum[0] - nx) + Math.abs(nearestGum[1] - ny);
//                    score += (20 - Math.min(distToGum, 20)) * 50;
//                }
//            }
//
//            int openDirs = countOpenDirections(nx, ny, map);
//            score += openDirs * 200;
//
//            if (openDirs <= 1) {
//                score -= 500;
//            }
//
//            if (score > bestScore) {
//                bestScore = score;
//                bestMove = directions[i];
//            }
//        }
//
//        return bestMove != null ? bestMove : PacManLauncher.RIGHT;
//    }
//
//    private static int[] bfsFindNearestSuperGum(int startX, int startY, char[][] map) {
//        Queue<int[]> queue = new LinkedList<>();
//        Set<String> visited = new HashSet<>();
//
//        queue.offer(new int[]{startX, startY});
//        visited.add(startX + "," + startY);
//
//        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//
//        while (!queue.isEmpty()) {
//            int[] curr = queue.poll();
//            int x = curr[0], y = curr[1];
//
//            char cell = map[x][y];
//            if (cell == '*') {
//                return new int[]{x, y};
//            }
//
//            for (int[] delta : deltas) {
//                int nx = x + delta[0];
//                int ny = y + delta[1];
//                String key = nx + "," + ny;
//
//                if (isValid(nx, ny, map) && !visited.contains(key)) {
//                    visited.add(key);
//                    queue.offer(new int[]{nx, ny});
//                }
//            }
//        }
//
//        return null;
//    }
//
//    private static int[] bfsFindNearestGum(int startX, int startY, char[][] map) {
//        Queue<int[]> queue = new LinkedList<>();
//        Set<String> visited = new HashSet<>();
//
//        queue.offer(new int[]{startX, startY});
//        visited.add(startX + "," + startY);
//
//        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//
//        while (!queue.isEmpty()) {
//            int[] curr = queue.poll();
//            int x = curr[0], y = curr[1];
//
//            char cell = map[x][y];
//            if (cell == '.' || cell == '*') {
//                return new int[]{x, y};
//            }
//
//            for (int[] delta : deltas) {
//                int nx = x + delta[0];
//                int ny = y + delta[1];
//                String key = nx + "," + ny;
//
//                if (isValid(nx, ny, map) && !visited.contains(key)) {
//                    visited.add(key);
//                    queue.offer(new int[]{nx, ny});
//                }
//            }
//        }
//
//        return null;
//    }
//
//    private static String moveToward(int fromX, int fromY, int toX, int toY, char[][] map) {
//        Queue<int[]> queue = new LinkedList<>();
//        Map<String, String> firstMove = new HashMap<>();
//
//        String[] directions = {PacManLauncher.UP, PacManLauncher.DOWN,
//                               PacManLauncher.LEFT, PacManLauncher.RIGHT};
//        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//
//        String startKey = fromX + "," + fromY;
//        queue.offer(new int[]{fromX, fromY});
//        firstMove.put(startKey, null);
//
//        while (!queue.isEmpty()) {
//            int[] curr = queue.poll();
//            int x = curr[0], y = curr[1];
//            String currKey = x + "," + y;
//
//            if (x == toX && y == toY) {
//                return firstMove.get(currKey);
//            }
//
//            for (int i = 0; i < 4; i++) {
//                int nx = x + deltas[i][0];
//                int ny = y + deltas[i][1];
//                String nextKey = nx + "," + ny;
//
//                if (isValid(nx, ny, map) && !firstMove.containsKey(nextKey)) {
//                    if (currKey.equals(startKey)) {
//                        firstMove.put(nextKey, directions[i]);
//                    } else {
//                        firstMove.put(nextKey, firstMove.get(currKey));
//                    }
//                    queue.offer(new int[]{nx, ny});
//                }
//            }
//        }
//
//        for (int i = 0; i < 4; i++) {
//            int nx = fromX + deltas[i][0];
//            int ny = fromY + deltas[i][1];
//            if (isValid(nx, ny, map)) {
//                return directions[i];
//            }
//        }
//
//        return PacManLauncher.RIGHT;
//    }
//
//    private static int countOpenDirections(int x, int y, char[][] map) {
//        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//        int count = 0;
//        for (int[] delta : deltas) {
//            if (isValid(x + delta[0], y + delta[1], map)) {
//                count++;
//            }
//        }
//        return count;
//    }
//
//    private static boolean isValid(int x, int y, char[][] map) {
//        return x >= 0 && x < map.length &&
//               y >= 0 && y < map[0].length &&
//               map[x][y] != '#';
//    }
//
//    private static void resetTarget() {
//        targetX = -1;
//        targetY = -1;
//    }
//
//    private static void resetRandomTarget() {
//        randomTargetX = -1;
//        randomTargetY = -1;
//        randomTargetCooldown = 0;
//    }
//
//    private static void clearHistory() {
//        positionHistory.clear();
//    }
//
//    private static void resetAll() {
//        resetTarget();
//        resetRandomTarget();
//        clearHistory();
//        ghostMemory.clear();
//    }
//}


//public class AI {
//
//    /* ===================== Search config ===================== */
//
//    private static final int MAX_DEPTH = 2; // keep small to be fast
//
//    /* ===================== Root baseline for reward ===================== */
//
//    private static int rootDots = -1;
//    private static int rootSupers = -1;
//
//    /* ===================== Direction memory ===================== */
//
//    private static String lastDirection = null;
//
//    /* ===================== Target mechanism ===================== */
//
//    private static int targetX = Integer.MIN_VALUE;
//    private static int targetY = Integer.MIN_VALUE;
//
//    // If no score / loop -> swap target
//    private static int lastScore = Integer.MIN_VALUE;
//    private static int noScoreStreak = 0;
//
//    private static final int NO_SCORE_SWAP_THRESHOLD = 10;
//
//    // Loop detection (short history)
//    private static final LinkedList<String> positionHistory = new LinkedList<>();
//    private static final int HISTORY_SIZE = 16;
//    private static final int LOOP_THRESHOLD = 3;
//
//    /* ===================== Ghost forgetting (must keep) ===================== */
//
//    private static final int FORGET_AFTER_TURNS = 10;
//    private static boolean[] seenEver = null;
//    private static int[] unseenTurns = null;
//    private static int nGhostMemo = -1;
//
//    /* ===================== Heuristic weights ===================== */
//
//    private static final int LIFE_WEIGHT = 1_000_000;
//
//    private static final int DOT_REWARD = 800;
//    private static final int SUPER_REWARD = 6000;
//
//    private static final int TARGET_REACH_BONUS = 15000;
//    private static final int TARGET_DIST_PENALTY = 250;
//
//    private static final int REVERSE_PENALTY = 1200;
//    private static final int KEEP_DIR_BONUS = 200;
//
//    private static final int DEATH_PENALTY = -1_000_000;
//
//    // Danger penalties based on min possible ghost distance
//    private static final int DANGER_D1 = 120_000;
//    private static final int DANGER_D2 = 40_000;
//    private static final int DANGER_D3 = 15_000;
//
//    private static final Random RNG = new Random();
//
//    /* ===================== Entry ===================== */
//
//    public static String findNextMove(BeliefState state) {
//        if (state == null || state.getLife() <= 0) {
//            resetAll();
//            return PacManLauncher.RIGHT;
//        }
//
//        // Root food counters for reward shaping
//        rootDots = state.getNbrOfGommes();
//        rootSupers = state.getNbrOfSuperGommes();
//
//        // Score stagnation tracking (for target swap)
//        if (lastScore == Integer.MIN_VALUE) lastScore = state.getScore();
//        if (state.getScore() == lastScore) noScoreStreak++;
//        else noScoreStreak = 0;
//        lastScore = state.getScore();
//
//        // Loop tracking
//        recordPosition(state.getPacmanPosition());
//
//        // Ghost forgetting mechanism update (MUST KEEP)
//        updateGhostForgetting(state);
//
//        // Update / choose target gum (nearest). If stuck, swap target.
//        updateTarget(state);
//
//        String act = orNode(state, MAX_DEPTH);
//        if (act == null) act = PacManLauncher.RIGHT;
//        lastDirection = act;
//        return act;
//    }
//
//    /* ===================== OR / AND search ===================== */
//
//    private static String orNode(BeliefState state, int depth) {
//        Plans plans = state.extendsBeliefState();
//        if (plans == null || plans.size() == 0) return PacManLauncher.RIGHT;
//
//        // IMPORTANT:
//        // Teacher's BeliefState.extendsBeliefState() can include a special "listNull" plan
//        // that groups actions hitting a wall (#). Those actions do NOT move Pacman
//        // (BeliefState.move(0,0,...) is used), so Pacman will look like he "doesn't move".
//        // We must ignore those wall-actions whenever there exists at least one action
//        // that actually changes Pacman's position.
//        boolean anyMovingAction = false;
//        for (int i = 0; i < plans.size(); i++) {
//            ArrayList<String> al = plans.getAction(i);
//            if (al == null || al.isEmpty()) continue;
//            Result r = plans.getResult(i);
//            if (actionActuallyMoves(r, state)) {
//                anyMovingAction = true;
//                break;
//            }
//        }
//
//        boolean panic = isPanic(state);
//        boolean forbidReverse = panic && ghostBehind(state);
//
//        String bestAct = null;
//        double bestVal = Double.NEGATIVE_INFINITY;
//
//        // tie-breaks
//        int bestDT = Integer.MAX_VALUE;
//        boolean bestKeep = false;
//
//        // If we forbid reverse but reverse is the only possible move, allow it.
//        boolean hasNonReverse = false;
//        if (forbidReverse && lastDirection != null) {
//            for (int i = 0; i < plans.size(); i++) {
//                ArrayList<String> al = plans.getAction(i);
//                if (al == null || al.isEmpty()) continue;
//                String a = al.get(0);
//                // Only count real moves when possible
//                if (anyMovingAction && !actionActuallyMoves(plans.getResult(i), state)) continue;
//                if (!isOpposite(a, lastDirection)) {
//                    hasNonReverse = true;
//                    break;
//                }
//            }
//        }
//
//        for (int i = 0; i < plans.size(); i++) {
//            ArrayList<String> actionList = plans.getAction(i);
//            Result result = plans.getResult(i);
//            if (actionList == null || actionList.isEmpty()) continue;
//
//            // Skip wall-actions if we have at least one real moving action available
//            if (anyMovingAction && !actionActuallyMoves(result, state)) {
//                continue;
//            }
//
//            String act = actionList.get(0);
//
//            // panic + ghost behind => disable reverse if there is any other option
//            if (forbidReverse && hasNonReverse && lastDirection != null && isOpposite(act, lastDirection)) {
//                continue;
//            }
//
//            double val = andNode(result, state, depth);
//
//            // Direction inertia & reverse penalty (soft; hard-disable only in panic+behind)
//            if (lastDirection != null) {
//                if (act.equals(lastDirection)) {
//                    val += KEEP_DIR_BONUS;
//                } else if (isOpposite(act, lastDirection)) {
//                    val -= REVERSE_PENALTY;
//                }
//            }
//
//            // Tie-break with target distance
//            int dT = estimateDistanceToTargetAfterAction(state, act);
//            boolean keep = (lastDirection != null && act.equals(lastDirection));
//
//            if (val > bestVal) {
//                bestVal = val;
//                bestAct = act;
//                bestDT = dT;
//                bestKeep = keep;
//            } else if (val == bestVal) {
//                if (dT < bestDT) {
//                    bestAct = act;
//                    bestDT = dT;
//                    bestKeep = keep;
//                } else if (dT == bestDT) {
//                    if (keep && !bestKeep) {
//                        bestAct = act;
//                        bestKeep = true;
//                    } else if (keep == bestKeep) {
//                        if (RNG.nextBoolean()) bestAct = act;
//                    }
//                }
//            }
//        }
//
//        return bestAct != null ? bestAct : PacManLauncher.RIGHT;
//    }
//
//    private static double andNode(Result result, BeliefState prevState, int depth) {
//        if (prevState == null) return DEATH_PENALTY;
//        if (result == null || result.size() == 0) return evaluate(prevState);
//
//        if (depth <= 1) {
//            // leaf: average alive
//            return averageAlive(result, prevState);
//        }
//
//        // internal: average alive of recursive OR values
//        ArrayList<Double> aliveVals = new ArrayList<>();
//        int maxProcess = 12;
//        int processed = 0;
//
//        for (int i = 0; i < result.size() && processed < maxProcess; i++) {
//            BeliefState s = result.getBeliefState(i);
//            if (s == null) continue;
//            processed++;
//
//            // death outcome skip
//            if (s.getLife() < prevState.getLife()) continue;
//
//            double v = orValue(s, depth - 1);
//            aliveVals.add(v);
//        }
//
//        if (aliveVals.isEmpty()) return DEATH_PENALTY;
//        double sum = 0;
//        for (double v : aliveVals) sum += v;
//        return sum / aliveVals.size();
//    }
//
//    private static double orValue(BeliefState state, int depth) {
//        if (state == null || state.getLife() <= 0) return DEATH_PENALTY;
//        if (depth <= 0) return evaluate(state);
//
//        Plans plans = state.extendsBeliefState();
//        if (plans == null || plans.size() == 0) return evaluate(state);
//
//        double best = Double.NEGATIVE_INFINITY;
//        for (int i = 0; i < plans.size(); i++) {
//            Result res = plans.getResult(i);
//            best = Math.max(best, andNode(res, state, depth));
//        }
//        return best;
//    }
//
//    private static double averageAlive(Result result, BeliefState prevState) {
//        ArrayList<Double> aliveVals = new ArrayList<>();
//
//        int maxProcess = 12;
//        int processed = 0;
//
//        for (int i = 0; i < result.size() && processed < maxProcess; i++) {
//            BeliefState s = result.getBeliefState(i);
//            if (s == null) continue;
//            processed++;
//
//            if (s.getLife() < prevState.getLife()) continue;
//            aliveVals.add(evaluate(s));
//        }
//
//        if (aliveVals.isEmpty()) return DEATH_PENALTY;
//        double sum = 0;
//        for (double v : aliveVals) sum += v;
//        return sum / aliveVals.size();
//    }
//
//    /* ===================== Evaluation ===================== */
//
//    private static double evaluate(BeliefState state) {
//        if (state == null || state.getLife() <= 0) return DEATH_PENALTY;
//
//        Position pac = state.getPacmanPosition();
//        if (pac == null) return DEATH_PENALTY;
//
//        double score = 0;
//
//        // Life is huge
//        score += state.getLife() * LIFE_WEIGHT;
//
//        // Food gained since root
//        int eatenDots = (rootDots >= 0) ? (rootDots - state.getNbrOfGommes()) : 0;
//        int eatenSupers = (rootSupers >= 0) ? (rootSupers - state.getNbrOfSuperGommes()) : 0;
//        if (eatenDots > 0) score += eatenDots * DOT_REWARD;
//        if (eatenSupers > 0) score += eatenSupers * SUPER_REWARD;
//
//        // Current cell bonus (immediate)
//        char[][] map = state.getMap();
//        if (map != null) {
//            char c = map[pac.x][pac.y];
//            if (c == '.') score += 1200;
//            else if (c == '*') score += 4500;
//        }
//
//        // Target shaping
//        if (hasTarget()) {
//            int dT = bfsDistance(map, pac.x, pac.y, targetX, targetY, 350);
//            if (dT >= 9999) dT = manhattan(pac.x, pac.y, targetX, targetY);
//            score -= dT * TARGET_DIST_PENALTY;
//            if (dT == 0) score += TARGET_REACH_BONUS;
//        } else {
//            // No target should be rare; push to find one
//            score -= 20000;
//        }
//
//        // Danger penalty based on min distance to any non-feared, non-forgotten ghost
//        int minGhostDist = minPossibleGhostDist(state);
//        if (minGhostDist == 0) return DEATH_PENALTY;
//        if (minGhostDist == 1) score -= DANGER_D1;
//        else if (minGhostDist == 2) score -= DANGER_D2;
//        else if (minGhostDist == 3) score -= DANGER_D3;
//
//        // Open directions: prefer junctions when danger is near
//        int open = countOpenDirections(state);
//        if (minGhostDist <= 3) {
//            score += open * 150;
//            if (open <= 1) score -= 8000;
//        } else {
//            score += open * 40;
//            if (open <= 1) score -= 400;
//        }
//
//        return score;
//    }
//
//    /* ===================== Target selection & swap ===================== */
//
//    private static void updateTarget(BeliefState state) {
//        if (state == null) return;
//        char[][] map = state.getMap();
//        Position pac = state.getPacmanPosition();
//        if (map == null || pac == null) return;
//
//        // If target eaten / invalid -> clear
//        if (hasTarget()) {
//            char cell = safeCell(map, targetX, targetY);
//            if (cell != '.' && cell != '*') {
//                clearTarget();
//            }
//            if (pac.x == targetX && pac.y == targetY) {
//                clearTarget();
//            }
//        }
//
//        // If looping or no-score too long -> swap target
//        boolean looping = isLooping();
//        if (noScoreStreak >= NO_SCORE_SWAP_THRESHOLD || looping) {
//            // pick an alternative target to break the loop
//            int[] alt = pickNearestFood(map, pac.x, pac.y, true);
//            if (alt != null) {
//                targetX = alt[0];
//                targetY = alt[1];
//            } else {
//                clearTarget();
//            }
//            // reset streak a bit so we don't constantly retarget every frame
//            noScoreStreak = 0;
//            // clear history to avoid immediate re-loop detection
//            positionHistory.clear();
//            return;
//        }
//
//        // Always have a target: nearest gum
//        if (!hasTarget()) {
//            int[] t = pickNearestFood(map, pac.x, pac.y, false);
//            if (t != null) {
//                targetX = t[0];
//                targetY = t[1];
//            }
//        }
//    }
//
//    /**
//     * BFS find nearest food ('.' or '*').
//     * If wantAlternative=true, try not to return the current target when possible.
//     */
//    private static int[] pickNearestFood(char[][] map, int sx, int sy, boolean wantAlternative) {
//        int H = map.length;
//        int W = map[0].length;
//        boolean[][] vis = new boolean[H][W];
//        ArrayDeque<int[]> q = new ArrayDeque<>();
//        q.add(new int[]{sx, sy, 0});
//        vis[sx][sy] = true;
//
//        int[] first = null;
//        int[] second = null;
//
//        while (!q.isEmpty()) {
//            int[] cur = q.poll();
//            int x = cur[0], y = cur[1];
//
//            char c = map[x][y];
//            if (c == '.' || c == '*') {
//                if (first == null) {
//                    first = new int[]{x, y};
//                } else {
//                    // different from first
//                    if (x != first[0] || y != first[1]) {
//                        second = new int[]{x, y};
//                        break;
//                    }
//                }
//            }
//
//            for (int k = 0; k < 4; k++) {
//                int nx = x + DX[k];
//                int ny = y + DY[k];
//                if (nx < 0 || ny < 0 || nx >= H || ny >= W) continue;
//                if (vis[nx][ny]) continue;
//                if (map[nx][ny] == '#') continue;
//                vis[nx][ny] = true;
//                q.add(new int[]{nx, ny, cur[2] + 1});
//            }
//        }
//
//        if (!wantAlternative) {
//            return first;
//        }
//
//        // Alternative requested: try not to keep current target
//        if (first == null) return null;
//
//        if (!hasTarget()) return first;
//        if (first[0] == targetX && first[1] == targetY) {
//            return (second != null) ? second : first;
//        }
//        return first;
//    }
//
//    private static boolean hasTarget() {
//        return targetX != Integer.MIN_VALUE && targetY != Integer.MIN_VALUE;
//    }
//
//    private static void clearTarget() {
//        targetX = Integer.MIN_VALUE;
//        targetY = Integer.MIN_VALUE;
//    }
//
//    /* ===================== Panic / behind logic (kept simple) ===================== */
//
//    private static boolean isPanic(BeliefState state) {
//        // Panic = a dangerous ghost could be very close.
//        int d = minPossibleGhostDist(state);
//        return d <= 2;
//    }
//
//    /**
//     * "Ghost behind" check (no probability, only positions that are visible OR just recently unseen).
//     * Used only to forbid reverse in PANIC.
//     */
//    private static boolean ghostBehind(BeliefState state) {
//        if (state == null) return false;
//        if (lastDirection == null) return false;
//
//        Position pac = state.getPacmanPosition();
//        if (pac == null) return false;
//
//        String behind = oppositeOf(lastDirection);
//        if (behind == null) return false;
//
//        int nG = state.getNbrOfGhost();
//        for (int i = 0; i < nG; i++) {
//            if (isForgotten(i)) continue;
//            if (state.getCompteurPeur(i) > 0) continue; // feared ghosts don't chase
//
//            TreeSet<Position> poss = state.getGhostPositions(i);
//            if (poss == null || poss.isEmpty()) continue;
//
//            boolean anyVisible = false;
//            for (Position g : poss) {
//                if (BeliefState.isVisible(g.x, g.y, pac.x, pac.y)) {
//                    anyVisible = true;
//                    break;
//                }
//            }
//
//            // If not visible and also unseen for long, skip
//            if (!anyVisible) {
//                if (unseenTurns != null && i < unseenTurns.length && unseenTurns[i] > 2) {
//                    continue;
//                }
//            }
//
//            for (Position g : poss) {
//                int dx = g.x - pac.x;
//                int dy = g.y - pac.y;
//                int dist = Math.abs(dx) + Math.abs(dy);
//                if (dist == 0 || dist > 3) continue;
//                if (isInDirection(behind, dx, dy)) return true;
//            }
//        }
//        return false;
//    }
//
//    private static String oppositeOf(String dir) {
//        if (dir == null) return null;
//        if (dir.equals(PacManLauncher.UP)) return PacManLauncher.DOWN;
//        if (dir.equals(PacManLauncher.DOWN)) return PacManLauncher.UP;
//        if (dir.equals(PacManLauncher.LEFT)) return PacManLauncher.RIGHT;
//        if (dir.equals(PacManLauncher.RIGHT)) return PacManLauncher.LEFT;
//        return null;
//    }
//
//    private static boolean isInDirection(String dir, int dx, int dy) {
//        int adx = Math.abs(dx);
//        int ady = Math.abs(dy);
//        if (dir.equals(PacManLauncher.UP)) return dx < 0 && adx >= ady;
//        if (dir.equals(PacManLauncher.DOWN)) return dx > 0 && adx >= ady;
//        if (dir.equals(PacManLauncher.LEFT)) return dy < 0 && ady >= adx;
//        if (dir.equals(PacManLauncher.RIGHT)) return dy > 0 && ady >= adx;
//        return false;
//    }
//
//    /* ===================== Ghost forgetting (MUST KEEP) ===================== */
//
//    private static void updateGhostForgetting(BeliefState state) {
//        int nG = 0;
//        try { nG = state.getNbrOfGhost(); } catch (Exception ignored) {}
//
//        if (seenEver == null || unseenTurns == null || nGhostMemo != nG) {
//            seenEver = new boolean[nG];
//            unseenTurns = new int[nG];
//            nGhostMemo = nG;
//        }
//
//        Position pac = state.getPacmanPosition();
//        if (pac == null) return;
//
//        for (int i = 0; i < nG; i++) {
//            TreeSet<Position> poss;
//            try { poss = state.getGhostPositions(i); } catch (Exception e) { poss = null; }
//            if (poss == null || poss.isEmpty()) {
//                if (seenEver[i]) {
//                    unseenTurns[i]++;
//                    if (unseenTurns[i] > FORGET_AFTER_TURNS) {
//                        seenEver[i] = false;
//                        unseenTurns[i] = 0;
//                    }
//                }
//                continue;
//            }
//
//            boolean visible = false;
//            for (Position g : poss) {
//                if (BeliefState.isVisible(g.x, g.y, pac.x, pac.y)) {
//                    visible = true;
//                    break;
//                }
//            }
//
//            if (visible) {
//                seenEver[i] = true;
//                unseenTurns[i] = 0;
//            } else {
//                if (seenEver[i]) {
//                    unseenTurns[i]++;
//                    if (unseenTurns[i] > FORGET_AFTER_TURNS) {
//                        seenEver[i] = false;
//                        unseenTurns[i] = 0;
//                    }
//                }
//            }
//        }
//    }
//
//    private static boolean isForgotten(int ghostIdx) {
//        if (seenEver == null || unseenTurns == null) return false;
//        if (ghostIdx < 0 || ghostIdx >= seenEver.length) return false;
//        // If never seen or already forgotten => ignored
//        return !seenEver[ghostIdx];
//    }
//
//    /* ===================== Loop tracking ===================== */
//
//    private static void recordPosition(Position p) {
//        if (p == null) return;
//        String key = p.x + "," + p.y;
//        positionHistory.addLast(key);
//        if (positionHistory.size() > HISTORY_SIZE) positionHistory.removeFirst();
//    }
//
//    private static boolean isLooping() {
//        if (positionHistory.size() < HISTORY_SIZE / 2) return false;
//        HashMap<String, Integer> cnt = new HashMap<>();
//        for (String s : positionHistory) {
//            cnt.put(s, cnt.getOrDefault(s, 0) + 1);
//        }
//        for (int v : cnt.values()) {
//            if (v >= LOOP_THRESHOLD) return true;
//        }
//        return false;
//    }
//
//    /* ===================== Required helper methods ===================== */
//
//    /** Reset everything (requested by you). */
//    private static void resetAll() {
//        lastDirection = null;
//
//        clearTarget();
//
//        lastScore = Integer.MIN_VALUE;
//        noScoreStreak = 0;
//
//        positionHistory.clear();
//
//        seenEver = null;
//        unseenTurns = null;
//        nGhostMemo = -1;
//
//        rootDots = -1;
//        rootSupers = -1;
//    }
//
//    /** Count open directions around Pacman. */
//    private static int countOpenDirections(BeliefState state) {
//        if (state == null) return 0;
//        char[][] map = state.getMap();
//        Position p = state.getPacmanPosition();
//        if (map == null || p == null) return 0;
//
//        int H = map.length;
//        int W = map[0].length;
//
//        int cnt = 0;
//        if (isWalkable(map, H, W, p.x - 1, p.y)) cnt++;
//        if (isWalkable(map, H, W, p.x + 1, p.y)) cnt++;
//        if (isWalkable(map, H, W, p.x, p.y - 1)) cnt++;
//        if (isWalkable(map, H, W, p.x, p.y + 1)) cnt++;
//        return cnt;
//    }
//
//    /** Manhattan distance helper (requested). */
//    private static int manhattan(int x1, int y1, int x2, int y2) {
//        int dx = x1 - x2;
//        if (dx < 0) dx = -dx;
//        int dy = y1 - y2;
//        if (dy < 0) dy = -dy;
//        return dx + dy;
//    }
//
//    /* ===================== Other helpers ===================== */
//
//    private static boolean isOpposite(String a, String b) {
//        if (a == null || b == null) return false;
//        return (a.equals(PacManLauncher.UP) && b.equals(PacManLauncher.DOWN)) ||
//               (a.equals(PacManLauncher.DOWN) && b.equals(PacManLauncher.UP)) ||
//               (a.equals(PacManLauncher.LEFT) && b.equals(PacManLauncher.RIGHT)) ||
//               (a.equals(PacManLauncher.RIGHT) && b.equals(PacManLauncher.LEFT));
//    }
//
//    /**
//
//     * We detect this by checking whether at least one successor state changes Pacman's position.
//     */
//    private static boolean actionActuallyMoves(Result result, BeliefState current) {
//        if (result == null || current == null || result.size() == 0) return false;
//        Position p0 = current.getPacmanPosition();
//        if (p0 == null) return false;
//        for (BeliefState bs : result.getBeliefStates()) {
//            if (bs == null) continue;
//            Position p1 = bs.getPacmanPosition();
//            if (p1 == null) continue;
//            if (p1.x != p0.x || p1.y != p0.y) return true;
//        }
//        return false;
//    }
//
//    private static char safeCell(char[][] map, int x, int y) {
//        if (map == null) return '#';
//        if (x < 0 || y < 0 || x >= map.length || y >= map[0].length) return '#';
//        return map[x][y];
//    }
//
//    private static boolean isWalkable(char[][] map, int H, int W, int x, int y) {
//        if (x < 0 || y < 0 || x >= H || y >= W) return false;
//        return map[x][y] != '#';
//    }
//
//    private static int bfsDistance(char[][] map, int sx, int sy, int tx, int ty, int maxD) {
//        if (map == null) return 9999;
//        if (sx == tx && sy == ty) return 0;
//
//        int H = map.length;
//        int W = map[0].length;
//        boolean[][] vis = new boolean[H][W];
//        ArrayDeque<int[]> q = new ArrayDeque<>();
//        q.add(new int[]{sx, sy, 0});
//        vis[sx][sy] = true;
//
//        while (!q.isEmpty()) {
//            int[] cur = q.poll();
//            int x = cur[0], y = cur[1], d = cur[2];
//            if (d >= maxD) continue;
//
//            for (int k = 0; k < 4; k++) {
//                int nx = x + DX[k];
//                int ny = y + DY[k];
//                if (nx < 0 || ny < 0 || nx >= H || ny >= W) continue;
//                if (vis[nx][ny]) continue;
//                if (map[nx][ny] == '#') continue;
//
//                if (nx == tx && ny == ty) return d + 1;
//
//                vis[nx][ny] = true;
//                q.add(new int[]{nx, ny, d + 1});
//            }
//        }
//        return 9999;
//    }
//
//    private static int estimateDistanceToTargetAfterAction(BeliefState s, String act) {
//        if (s == null || !hasTarget()) return 9999;
//        Position p = s.getPacmanPosition();
//        if (p == null) return 9999;
//        int nx = p.x, ny = p.y;
//        if (PacManLauncher.UP.equals(act)) nx--;
//        else if (PacManLauncher.DOWN.equals(act)) nx++;
//        else if (PacManLauncher.LEFT.equals(act)) ny--;
//        else if (PacManLauncher.RIGHT.equals(act)) ny++;
//        return bfsDistance(s.getMap(), nx, ny, targetX, targetY, 220);
//    }
//
//    private static int minPossibleGhostDist(BeliefState state) {
//        if (state == null) return 9999;
//        Position pac = state.getPacmanPosition();
//        if (pac == null) return 9999;
//
//        int minD = 9999;
//        int nG = state.getNbrOfGhost();
//        for (int i = 0; i < nG; i++) {
//            if (isForgotten(i)) continue;
//            if (state.getCompteurPeur(i) > 0) continue; // feared
//
//            TreeSet<Position> poss = state.getGhostPositions(i);
//            if (poss == null || poss.isEmpty()) continue;
//
//            for (Position g : poss) {
//                int d = manhattan(pac.x, pac.y, g.x, g.y);
//                if (d < minD) minD = d;
//                if (minD == 0) return 0;
//            }
//        }
//        return minD;
//    }
//
//    private static final int[] DX = {-1, 1, 0, 0};
//    private static final int[] DY = {0, 0, -1, 1};
//}



public class AI{
	private static final double INF = 1e18;
	private static final int DEFAULT_MAX_DEPTH = 8;
	private static final long DEFAULT_TIME_BUDGET_NS = 50_000_000; // ~50ms
	private static final double AND_RISK_WEIGHT = 0.20; // weight on average (0=min only, 1=avg only)
	private static final int MAX_OUTCOME_EVAL = 12;
	private static final int MS_OUTCOME = 0;
	private static final int MS_DEAD = 1;
	private static final int MS_FORCED_DEAD = 2;
	private static final int MS_WORST_VISIBLE = 3;
	private static final int MS_WORST_NONFEAR = 4;
	private static final int MS_GUM_DELTA = 5;
	private static final int MS_NEXT_GUM_DIST = 6;
	private static final int MS_DEGREE = 7;
	private static final int MS_GHOST_HOUSE_BAD = 8;
	private static final int MS_PLANNED_MATCH = 9;
	private static final int MS_TRAP_PENALTY = 10;
	private static final int MS_BIAS = 11;

	private static int lastPacmanRow = -1;
	private static int lastPacmanCol = -1;
	private static String lastMove = PacManLauncher.LEFT;
	private static int stagnantMoves = 0;
	private static int lastScore = -1;
	private static int stepsWithoutScore = 0;
	private static int lastGommes = -1;
	private static int lastWallHash = 0;

	private static final Map<BeliefState, double[]> valueCache = new TreeMap<BeliefState, double[]>();
	private static final Map<BeliefState, Double> evalCache = new TreeMap<BeliefState, Double>();
	private static final Map<String, Integer> bfsCache = new HashMap<String, Integer>();
	private static final Map<BeliefState, Boolean> escapeCache = new TreeMap<BeliefState, Boolean>();
	private static final Map<BeliefState, Boolean> escape2Cache = new TreeMap<BeliefState, Boolean>();

	private static final Map<Integer, boolean[][]> ghostHouseCache = new HashMap<Integer, boolean[][]>();
	private static final Map<Integer, Integer> ghostHouseSizeCache = new HashMap<Integer, Integer>();
	private static boolean[][] ghostHouseMask = null;
	private static int ghostHouseCells = 0;

	private static Double getCachedValue(BeliefState state, int depth) {
		if(state == null || depth < 0)
			return null;
		double[] values = valueCache.get(state);
		if(values == null || depth >= values.length)
			return null;
		double v = values[depth];
		return Double.isNaN(v) ? null : Double.valueOf(v);
	}

	private static void putCachedValue(BeliefState state, int depth, double value) {
		if(state == null || depth < 0)
			return;
		double[] values = valueCache.get(state);
		if(values == null || depth >= values.length) {
			int newLen = values == null ? 0 : values.length;
			newLen = Math.max(newLen, DEFAULT_MAX_DEPTH + 2);
			newLen = Math.max(newLen, depth + 1);
			double[] next = new double[newLen];
			Arrays.fill(next, Double.NaN);
			if(values != null)
				System.arraycopy(values, 0, next, 0, values.length);
			values = next;
			valueCache.put(state, values);
		}
		values[depth] = value;
	}

	private static int wallHash(char[][] map) {
		if(map == null)
			return 0;
		int h = 1;
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[i].length; j++) {
				if(map[i][j] == '#')
					h = 31 * h + (i * 29 + j);
			}
		}
		return h;
	}

	private static void resetForNewGame(BeliefState beliefState) {
		valueCache.clear();
		evalCache.clear();
		bfsCache.clear();
		escapeCache.clear();
		escape2Cache.clear();
		Position p = beliefState.getPacmanPosition();
		lastPacmanRow = p.getRow();
		lastPacmanCol = p.getColumn();
		lastMove = PacManLauncher.LEFT;
		stagnantMoves = 0;
		lastScore = beliefState.getScore();
		stepsWithoutScore = 0;
		lastGommes = beliefState.getNbrOfGommes();
		lastWallHash = wallHash(beliefState.getMap());
		ghostHouseMask = ghostHouseCache.get(lastWallHash);
		Integer sz = ghostHouseSizeCache.get(lastWallHash);
		ghostHouseCells = sz == null ? 0 : sz.intValue();
		if(ghostHouseMask == null && beliefState.getNbrOfGommes() >= 100) {
			boolean canBuild = true;
			for(int i = 0; i < beliefState.getNbrOfGhost(); i++) {
				TreeSet<Position> ps = beliefState.getGhostPositions(i);
				if(ps == null || ps.size() != 1) {
					canBuild = false;
					break;
				}
			}
			if(canBuild) {
				boolean[][] mask = computeGhostHouseMask(beliefState);
				int count = countTrue(mask);
				ghostHouseCache.put(lastWallHash, mask);
				ghostHouseSizeCache.put(lastWallHash, count);
				ghostHouseMask = mask;
				ghostHouseCells = count;
			}
		}
	}

	private static void maybeResetMemory(BeliefState beliefState) {
		if(beliefState == null)
			return;
		if(lastWallHash == 0) {
			resetForNewGame(beliefState);
			return;
		}

		int currentWallHash = wallHash(beliefState.getMap());
		if(lastWallHash != 0 && currentWallHash != lastWallHash) {
			resetForNewGame(beliefState);
			return;
		}
		if(lastScore >= 0 && beliefState.getScore() < lastScore) {
			resetForNewGame(beliefState);
			return;
		}
		if(lastGommes >= 0 && beliefState.getNbrOfGommes() > lastGommes) {
			resetForNewGame(beliefState);
			return;
		}

		if(lastScore < 0)
			lastScore = beliefState.getScore();
		if(beliefState.getScore() > lastScore) {
			lastScore = beliefState.getScore();
			stepsWithoutScore = 0;
		} else {
			stepsWithoutScore++;
		}

		Position p = beliefState.getPacmanPosition();
		if(lastPacmanRow != -1) {
			int jump = Math.abs(p.getRow() - lastPacmanRow) + Math.abs(p.getColumn() - lastPacmanCol);
			if(jump > 1) {
				resetForNewGame(beliefState);
				return;
			}
		}
		boolean moved = !(p.getRow() == lastPacmanRow && p.getColumn() == lastPacmanCol);
		if(!moved) {
			stagnantMoves++;
		} else {
			stagnantMoves = 0;
		}
		lastPacmanRow = p.getRow();
		lastPacmanCol = p.getColumn();
		if(stagnantMoves > 20 || beliefState.getNbrOfGommes() == 0 || beliefState.getLife() <= 0) {
			lastMove = PacManLauncher.LEFT;
			stagnantMoves = 0;
			lastScore = beliefState.getScore();
			stepsWithoutScore = 0;
		}
		lastGommes = beliefState.getNbrOfGommes();
		lastWallHash = currentWallHash;
		if(currentWallHash != 0 && currentWallHash == lastWallHash) {
			ghostHouseMask = ghostHouseCache.get(currentWallHash);
			Integer sz = ghostHouseSizeCache.get(currentWallHash);
			ghostHouseCells = sz == null ? 0 : sz.intValue();
		}
	}

	private static int legalMoveCount(BeliefState state) {
		Position p = state.getPacmanPosition();
		int r = p.getRow(), c = p.getColumn();
		int count = 0;
		char[][] map = state.getMap();
		if(r > 0 && map[r - 1][c] != '#')
			count++;
		if(r + 1 < map.length && map[r + 1][c] != '#')
			count++;
		if(c > 0 && map[r][c - 1] != '#')
			count++;
		if(c + 1 < map[0].length && map[r][c + 1] != '#')
			count++;
		return count;
	}

	private static double corridorTrapPenalty(char[][] map, int sr, int sc) {
		if(map == null)
			return 0.0;
		if(openDegree(map, sr, sc) != 2)
			return 0.0;
		int n = map.length;
		boolean[][] visited = new boolean[n][n];
		LinkedList<int[]> q = new LinkedList<int[]>();
		q.add(new int[] { sr, sc, 0 });
		visited[sr][sc] = true;

		int bestJunction = Integer.MAX_VALUE;
		int bestDeadEnd = Integer.MAX_VALUE;
		while(!q.isEmpty()) {
			int[] cur = q.pollFirst();
			int r = cur[0], c = cur[1], d = cur[2];
			int deg = openDegree(map, r, c);
			if(d > 0 && deg != 2) {
				if(deg >= 3)
					bestJunction = Math.min(bestJunction, d);
				if(deg <= 1)
					bestDeadEnd = Math.min(bestDeadEnd, d);
				continue;
			}
			if(r > 0 && map[r - 1][c] != '#' && !visited[r - 1][c]) {
				visited[r - 1][c] = true;
				q.add(new int[] { r - 1, c, d + 1 });
			}
			if(r + 1 < n && map[r + 1][c] != '#' && !visited[r + 1][c]) {
				visited[r + 1][c] = true;
				q.add(new int[] { r + 1, c, d + 1 });
			}
			if(c > 0 && map[r][c - 1] != '#' && !visited[r][c - 1]) {
				visited[r][c - 1] = true;
				q.add(new int[] { r, c - 1, d + 1 });
			}
			if(c + 1 < n && map[r][c + 1] != '#' && !visited[r][c + 1]) {
				visited[r][c + 1] = true;
				q.add(new int[] { r, c + 1, d + 1 });
			}
		}

		if(bestJunction == Integer.MAX_VALUE && bestDeadEnd == Integer.MAX_VALUE)
			return 0.0;
		if(bestJunction == Integer.MAX_VALUE)
			return 3000.0;
		if(bestDeadEnd != Integer.MAX_VALUE && bestDeadEnd < bestJunction) {
			return 1200.0 + (bestJunction - bestDeadEnd) * 350.0;
		}
		return 0.0;
	}

	private static final class CorridorEnd {
		final int r;
		final int c;
		final int dist;
		final int deg;

		CorridorEnd(int r, int c, int dist, int deg) {
			this.r = r;
			this.c = c;
			this.dist = dist;
			this.deg = deg;
		}
	}

	private static CorridorEnd[] corridorEnds(char[][] map, int sr, int sc) {
		if(map == null)
			return null;
		if(openDegree(map, sr, sc) != 2)
			return null;
		int n = map.length;
		int m = map[0].length;
		int[][] dirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

		int[] nr = new int[2];
		int[] nc = new int[2];
		int count = 0;
		for(int[] d : dirs) {
			int r = sr + d[0];
			int c = sc + d[1];
			if(r < 0 || c < 0 || r >= n || c >= m)
				continue;
			if(map[r][c] != '#') {
				if(count < 2) {
					nr[count] = r;
					nc[count] = c;
				}
				count++;
			}
		}
		if(count != 2)
			return null;

		int limit = n * m + 5;
		CorridorEnd[] ends = new CorridorEnd[2];
		for(int k = 0; k < 2; k++) {
			int pr = sr, pc = sc;
			int cr = nr[k], cc = nc[k];
			int dist = 1;
			while(dist < limit && openDegree(map, cr, cc) == 2) {
				int nextR = -1, nextC = -1;
				for(int[] d : dirs) {
					int r = cr + d[0];
					int c = cc + d[1];
					if(r < 0 || c < 0 || r >= n || c >= m)
						continue;
					if(map[r][c] == '#')
						continue;
					if(r == pr && c == pc)
						continue;
					nextR = r;
					nextC = c;
					break;
				}
				if(nextR == -1)
					break;
				pr = cr;
				pc = cc;
				cr = nextR;
				cc = nextC;
				dist++;
			}
			ends[k] = new CorridorEnd(cr, cc, dist, openDegree(map, cr, cc));
		}
		return ends;
	}

	private static int minManhattanToNonFearGhostFromCell(BeliefState state, int r, int c) {
		if(state == null)
			return Integer.MAX_VALUE;
		int best = Integer.MAX_VALUE;
		for(int i = 0; i < state.getNbrOfGhost(); i++) {
			if(state.getCompteurPeur(i) > 0)
				continue;
			TreeSet<Position> ps = state.getGhostPositions(i);
			if(ps == null || ps.isEmpty())
				continue;
			for(Position g : ps) {
				int d = Math.abs(g.getRow() - r) + Math.abs(g.getColumn() - c);
				if(d < best)
					best = d;
			}
		}
		return best;
	}

	private static boolean anyFearActive(BeliefState state) {
		if(state == null)
			return false;
		for(int i = 0; i < state.getNbrOfGhost(); i++) {
			if(state.getCompteurPeur(i) > 0)
				return true;
		}
		return false;
	}

	private static double corridorPinchPenalty(BeliefState state, int sr, int sc) {
		if(state == null)
			return 0.0;
		char[][] map = state.getMap();
		if(map == null)
			return 0.0;
		if(anyFearActive(state))
			return 0.0;

		int deg = openDegree(map, sr, sc);
		if(deg >= 3)
			return 0.0;

		int minG = minManhattanToNonFearGhostFromCell(state, sr, sc);
		if(minG == Integer.MAX_VALUE)
			return 0.0;

		double penalty = 0.0;
		if(deg <= 1) {
			if(minG <= 1)
				penalty = 120000.0;
			else if(minG == 2)
				penalty = 45000.0;
			else if(minG == 3)
				penalty = 18000.0;
			else if(minG <= 5)
				penalty = 6000.0;
		} else if(deg == 2) {
			CorridorEnd[] ends = corridorEnds(map, sr, sc);
			if(ends == null)
				return 0.0;
			int g1 = minManhattanToNonFearGhostFromCell(state, ends[0].r, ends[0].c);
			int g2 = minManhattanToNonFearGhostFromCell(state, ends[1].r, ends[1].c);
			if(g1 == Integer.MAX_VALUE)
				g1 = 50;
			if(g2 == Integer.MAX_VALUE)
				g2 = 50;
			int m1 = g1 - ends[0].dist;
			int m2 = g2 - ends[1].dist;

			if(minG <= 1)
				penalty += 80000.0;
			else if(minG == 2)
				penalty += 30000.0;
			else if(minG == 3)
				penalty += 12000.0;
			else if(minG == 4)
				penalty += 5000.0;

			boolean end1Unsafe = m1 <= 1;
			boolean end2Unsafe = m2 <= 1;
			if(end1Unsafe && end2Unsafe)
				penalty += 50000.0;
			else if(end1Unsafe || end2Unsafe)
				penalty += 14000.0;

			if(ends[0].deg <= 1 && m2 <= 2)
				penalty += 32000.0;
			if(ends[1].deg <= 1 && m1 <= 2)
				penalty += 32000.0;
		}

		double scale = 1.0;
		int goms = state.getNbrOfGommes();
		if(goms <= 4)
			scale *= 0.35;
		else if(goms <= 8)
			scale *= 0.55;
		else if(goms <= 12)
			scale *= 0.75;
		if(stepsWithoutScore > 220)
			scale *= 0.55;
		if(stepsWithoutScore > 600)
			scale *= 0.45;
		return penalty * scale;
	}


	private static int minManhattanToGhost(BeliefState state, int ghostId) {
		TreeSet<Position> positions = state.getGhostPositions(ghostId);
		Position p = state.getPacmanPosition();
		int pr = p.getRow(), pc = p.getColumn();
		int best = Integer.MAX_VALUE;
		for(Position g : positions) {
			int d = Math.abs(g.getRow() - pr) + Math.abs(g.getColumn() - pc);
			if(d < best)
				best = d;
		}
		return best;
	}

	private static int minManhattanToNonFearGhost(BeliefState state) {
		int best = Integer.MAX_VALUE;
		for(int i = 0; i < state.getNbrOfGhost(); i++) {
			if(state.getCompteurPeur(i) > 0)
				continue;
			best = Math.min(best, minManhattanToGhost(state, i));
		}
		return best;
	}

	private static int minVisibleNonFearGhostDist(BeliefState state) {
		Position p = state.getPacmanPosition();
		int pr = p.getRow(), pc = p.getColumn();
		int best = Integer.MAX_VALUE;
		for(int i = 0; i < state.getNbrOfGhost(); i++) {
			if(state.getCompteurPeur(i) > 0)
				continue;
			for(Position g : state.getGhostPositions(i)) {
				if(!BeliefState.isVisible(pr, pc, g.getRow(), g.getColumn()))
					continue;
				int d = Math.abs(g.getRow() - pr) + Math.abs(g.getColumn() - pc);
				if(d < best)
					best = d;
			}
		}
		return best;
	}

	private static Position closestVisibleNonFearGhost(BeliefState state) {
		Position p = state.getPacmanPosition();
		int pr = p.getRow(), pc = p.getColumn();
		Position best = null;
		int bestDist = Integer.MAX_VALUE;
		for(int i = 0; i < state.getNbrOfGhost(); i++) {
			if(state.getCompteurPeur(i) > 0)
				continue;
			for(Position g : state.getGhostPositions(i)) {
				if(!BeliefState.isVisible(pr, pc, g.getRow(), g.getColumn()))
					continue;
				int d = Math.abs(g.getRow() - pr) + Math.abs(g.getColumn() - pc);
				if(d < bestDist) {
					bestDist = d;
					best = g;
				}
			}
		}
		return best;
	}

	private static int[] deltaForMove(String move) {
		if(PacManLauncher.UP.equals(move))
			return new int[] { -1, 0 };
		if(PacManLauncher.DOWN.equals(move))
			return new int[] { 1, 0 };
		if(PacManLauncher.LEFT.equals(move))
			return new int[] { 0, -1 };
		return new int[] { 0, 1 };
	}

	private static boolean moveHitsWall(BeliefState state, String move) {
		if(state == null || move == null)
			return true;
		Position p = state.getPacmanPosition();
		int[] d = deltaForMove(move);
		int nr = p.getRow() + d[0];
		int nc = p.getColumn() + d[1];
		char[][] map = state.getMap();
		if(nr < 0 || nc < 0 || nr >= map.length || nc >= map[0].length)
			return true;
		return map[nr][nc] == '#';
	}

	private static boolean moveIntoSuperGom(BeliefState state, String move) {
		if(state == null || move == null)
			return false;
		if(moveHitsWall(state, move))
			return false;
		Position p = state.getPacmanPosition();
		int[] d = deltaForMove(move);
		int nr = p.getRow() + d[0];
		int nc = p.getColumn() + d[1];
		char[][] map = state.getMap();
		return map[nr][nc] == '*';
	}

	private static boolean movesIntoPossibleNonFearGhostCell(BeliefState state, String move) {
		if(state == null || move == null)
			return false;
		if(moveHitsWall(state, move))
			return false;
		Position p = state.getPacmanPosition();
		int[] d = deltaForMove(move);
		int nr = p.getRow() + d[0];
		int nc = p.getColumn() + d[1];

		for(int i = 0; i < state.getNbrOfGhost(); i++) {
			if(state.getCompteurPeur(i) > 0)
				continue;
			TreeSet<Position> ps = state.getGhostPositions(i);
			if(ps == null || ps.isEmpty())
				continue;
			for(Position g : ps) {
				if(g.getRow() == nr && g.getColumn() == nc)
					return true;
			}
		}
		return false;
	}

	private static boolean stepsIntoChasingGhost(BeliefState state, String move) {
		if(state == null || move == null)
			return false;
		Position p = state.getPacmanPosition();
		int pr = p.getRow(), pc = p.getColumn();
		int[] d = deltaForMove(move);
		int nr = pr + d[0];
		int nc = pc + d[1];
		char[][] map = state.getMap();
		if(nr < 0 || nc < 0 || nr >= map.length || nc >= map[0].length || map[nr][nc] == '#') {
			nr = pr;
			nc = pc;
		}

		for(int i = 0; i < state.getNbrOfGhost(); i++) {
			if(state.getCompteurPeur(i) > 0)
				continue;
			for(Position g : state.getGhostPositions(i)) {
				int gr = g.getRow();
				int gc = g.getColumn();
				if(!BeliefState.isVisible(gr, gc, pr, pc))
					continue;
				int ngr = gr;
				int ngc = gc;
				if(gr > pr)
					ngr = gr - 1;
				else if(gr < pr)
					ngr = gr + 1;
				else if(gc > pc)
					ngc = gc - 1;
				else if(gc < pc)
					ngc = gc + 1;
				if(ngr == nr && ngc == nc)
					return true;
			}
		}
		return false;
	}

	private static String evasiveMove(BeliefState state, Position ghost) {
		Position p = state.getPacmanPosition();
		int pr = p.getRow(), pc = p.getColumn();
		char[][] map = state.getMap();
		String[] moves = { PacManLauncher.UP, PacManLauncher.DOWN, PacManLauncher.LEFT, PacManLauncher.RIGHT };
		String bestMove = null;
		double bestScore = -INF;
		for(String m : moves) {
			int[] d = deltaForMove(m);
			int nr = pr + d[0];
			int nc = pc + d[1];
			if(nr < 0 || nc < 0 || nr >= map.length || nc >= map[0].length)
				continue;
			if(map[nr][nc] == '#')
				continue;
			int dist = Math.abs(ghost.getRow() - nr) + Math.abs(ghost.getColumn() - nc);
			boolean visible = BeliefState.isVisible(nr, nc, ghost.getRow(), ghost.getColumn());
			double score = dist + (visible ? -1000.0 : 0.0);
			if(score > bestScore) {
				bestScore = score;
				bestMove = m;
			}
		}
		return bestMove;
	}

	private static boolean isMoveTowardVisibleNonFearGhost(BeliefState state, String move) {
		if(move == null)
			return false;
		Position p = state.getPacmanPosition();
		int pr = p.getRow(), pc = p.getColumn();
		int[] d = deltaForMove(move);
		int nr = pr + d[0];
		int nc = pc + d[1];
		char[][] map = state.getMap();
		if(nr < 0 || nc < 0 || nr >= map.length || nc >= map[0].length || map[nr][nc] == '#')
			return false;

		for(int i = 0; i < state.getNbrOfGhost(); i++) {
			if(state.getCompteurPeur(i) > 0)
				continue;
			for(Position g : state.getGhostPositions(i)) {
				if(!BeliefState.isVisible(pr, pc, g.getRow(), g.getColumn()))
					continue;
				int curDist = Math.abs(g.getRow() - pr) + Math.abs(g.getColumn() - pc);
				int newDist = Math.abs(g.getRow() - nr) + Math.abs(g.getColumn() - nc);
				boolean stillVisible = BeliefState.isVisible(nr, nc, g.getRow(), g.getColumn());
				if(stillVisible && newDist < curDist)
					return true;
			}
		}
		return false;
	}

	private static String evasiveMoveFromVisibleGhosts(BeliefState state) {
		Position p = state.getPacmanPosition();
		int pr = p.getRow(), pc = p.getColumn();
		char[][] map = state.getMap();
		String[] moves = { PacManLauncher.UP, PacManLauncher.DOWN, PacManLauncher.LEFT, PacManLauncher.RIGHT };
		String bestMove = null;
		double bestScore = -INF;
		for(String m : moves) {
			int[] d = deltaForMove(m);
			int nr = pr + d[0];
			int nc = pc + d[1];
			if(nr < 0 || nc < 0 || nr >= map.length || nc >= map[0].length)
				continue;
			if(map[nr][nc] == '#')
				continue;

			int minDist = Integer.MAX_VALUE;
			boolean anyVisible = false;
			for(int i = 0; i < state.getNbrOfGhost(); i++) {
				if(state.getCompteurPeur(i) > 0)
					continue;
				for(Position g : state.getGhostPositions(i)) {
					int dist = Math.abs(g.getRow() - nr) + Math.abs(g.getColumn() - nc);
					minDist = Math.min(minDist, dist);
					if(!anyVisible && BeliefState.isVisible(nr, nc, g.getRow(), g.getColumn()))
						anyVisible = true;
				}
			}
			int deg = openDegree(map, nr, nc);
			double trap = corridorTrapPenalty(map, nr, nc) + corridorPinchPenalty(state, nr, nc);
			double score = (minDist == Integer.MAX_VALUE ? 50.0 : minDist) * 12.0;
			score += deg * 15.0;
			if(anyVisible)
				score -= 1500.0;
			if(deg <= 1)
				score -= 2500.0;
			score -= trap;
			if(score > bestScore) {
				bestScore = score;
				bestMove = m;
			}
		}
		return bestMove;
	}

	private static int openDegree(char[][] map, int r, int c) {
		int deg = 0;
		if(r > 0 && map[r - 1][c] != '#')
			deg++;
		if(r + 1 < map.length && map[r + 1][c] != '#')
			deg++;
		if(c > 0 && map[r][c - 1] != '#')
			deg++;
		if(c + 1 < map[0].length && map[r][c + 1] != '#')
			deg++;
		return deg;
	}

	private static boolean isInLineOfSight(int r1, int c1, int r2, int c2) {
		return BeliefState.isVisible(r1, c1, r2, c2);
	}

	private static double cellDanger(BeliefState state, int r, int c) {
		double danger = 0.0;
		for(int i = 0; i < state.getNbrOfGhost(); i++) {
			if(state.getCompteurPeur(i) > 0)
				continue;

			TreeSet<Position> positions = state.getGhostPositions(i);
			if(positions == null || positions.isEmpty())
				continue;

			double sum = 0.0;
			int minDist = Integer.MAX_VALUE;
			double minWeight = 0.0;
			for(Position g : positions) {
				int d = Math.abs(g.getRow() - r) + Math.abs(g.getColumn() - c);
				if(d < minDist)
					minDist = d;
				if(d <= 0)
					return 1e12;
				if(d == 1) {
					sum += 2000.0;
					continue;
				}
				double w = 1.0 / (d * (double)d);
				if(isInLineOfSight(r, c, g.getRow(), g.getColumn()))
					w *= 6.0;
				sum += w;
			}
			int size = positions.size();
			sum /= size;

			if(minDist == 1) {
				minWeight = 2000.0;
			} else {
				minWeight = 1.0 / (minDist * (double)minDist);
			}

			double minCoeff;
			if(size <= 4) {
				minCoeff = 0.35;
			} else if(size <= 10) {
				minCoeff = 0.20;
			} else if(size <= 25) {
				minCoeff = 0.12;
			} else {
				minCoeff = 0.06;
			}
			if(state.getNbrOfGommes() <= 12 || stepsWithoutScore > 160) {
				minCoeff *= 0.35;
			}
			danger += (1.0 - minCoeff) * sum + minCoeff * minWeight;
		}
		return danger * 25000.0;
	}

	private static String planWithDijkstra(BeliefState state, double dangerScale, boolean seekSuper) {
		char[][] map = state.getMap();
		int n = map.length;
		Position p = state.getPacmanPosition();
		int sr = p.getRow(), sc = p.getColumn();

		boolean hasNormalGom = false;
		for(int i = 0; i < n && !hasNormalGom; i++) {
			for(int j = 0; j < n; j++) {
				if(map[i][j] == '.') {
					hasNormalGom = true;
					break;
				}
			}
		}
		boolean avoidSuperGom = !seekSuper && hasNormalGom;
		boolean fearActiveNow = false;
		for(int i = 0; i < state.getNbrOfGhost(); i++) {
			if(state.getCompteurPeur(i) > 0) {
				fearActiveNow = true;
				break;
			}
		}
		boolean avoidGhostHouse = ghostHouseCells >= 20 && !fearActiveNow;
		boolean currentlyInGhostHouse = avoidGhostHouse && isGhostHouseCell(sr, sc);
		int superLeft = state.getNbrOfSuperGommes();
		boolean isMap2 = lastWallHash == 1836722794;
		int lastSuperHold = isMap2 ? 15 : 18;
		double superAvoidPenalty = isMap2 ? 12000.0 : 5000.0;
		if(superLeft == 1 && state.getNbrOfGommes() > lastSuperHold && stepsWithoutScore < 600) {
			superAvoidPenalty = isMap2 ? 30000.0 : 20000.0;
		}
		if(state.getNbrOfGommes() <= 12 || stepsWithoutScore > 600) {
			superAvoidPenalty = 0.0;
		} else if(state.getNbrOfGommes() <= 25 || stepsWithoutScore > 220) {
			superAvoidPenalty = isMap2 ? 6000.0 : 900.0;
		}
		double deadEndPenaltyBase;
		if(state.getNbrOfGommes() <= 12 || stepsWithoutScore > 600) {
			deadEndPenaltyBase = 0.0;
		} else if(state.getNbrOfGommes() <= 20 || stepsWithoutScore > 220) {
			deadEndPenaltyBase = 60.0;
		} else {
			deadEndPenaltyBase = state.getNbrOfGommes() > 40 ? 2000.0 : 400.0;
		}

		boolean blockLastSuperMap2 = isMap2 && avoidSuperGom && superLeft == 1 && state.getNbrOfGommes() > lastSuperHold
			&& stepsWithoutScore < 600;
		for(int attempt = 0; attempt < 2; attempt++) {
			boolean blockSuper = blockLastSuperMap2 && attempt == 0;

			double[][] dist = new double[n][n];
			for(int i = 0; i < n; i++) {
				Arrays.fill(dist[i], INF);
			}
			int[][] prevR = new int[n][n];
			int[][] prevC = new int[n][n];
			for(int i = 0; i < n; i++) {
				Arrays.fill(prevR[i], -1);
				Arrays.fill(prevC[i], -1);
			}

			PriorityQueue<double[]> pq = new PriorityQueue<double[]>(Comparator.comparingDouble(a -> a[2]));
			dist[sr][sc] = 0.0;
			pq.add(new double[] { sr, sc, 0.0 });

			int tr = -1, tc = -1;
			while(!pq.isEmpty()) {
				double[] cur = pq.poll();
				int r = (int)cur[0];
				int c = (int)cur[1];
				double curCost = cur[2];
				if(curCost != dist[r][c])
					continue;

				char cell = map[r][c];
				boolean isTarget;
				if(seekSuper) {
					isTarget = cell == '*';
				} else if(hasNormalGom) {
					isTarget = cell == '.';
				} else {
					isTarget = cell == '.' || cell == '*';
				}
				if(isTarget && !(r == sr && c == sc)) {
					tr = r;
					tc = c;
					break;
				}

				if(r > 0 && map[r - 1][c] != '#' && !(blockSuper && map[r - 1][c] == '*')) {
					double extra = avoidSuperGom && map[r - 1][c] == '*' ? superAvoidPenalty : 0.0;
					double deadEndPenalty = (!fearActiveNow && openDegree(map, r - 1, c) <= 1) ? deadEndPenaltyBase : 0.0;
					double ghostHousePenalty = avoidGhostHouse && isGhostHouseCell(r - 1, c) ? (currentlyInGhostHouse ? 4000.0 : 45000.0) : 0.0;
					double nd = curCost + 1.0 + extra + deadEndPenalty + ghostHousePenalty + dangerScale * cellDanger(state, r - 1, c);
					if(nd < dist[r - 1][c]) {
						dist[r - 1][c] = nd;
						prevR[r - 1][c] = r;
						prevC[r - 1][c] = c;
						pq.add(new double[] { r - 1, c, nd });
					}
				}
				if(r + 1 < n && map[r + 1][c] != '#' && !(blockSuper && map[r + 1][c] == '*')) {
					double extra = avoidSuperGom && map[r + 1][c] == '*' ? superAvoidPenalty : 0.0;
					double deadEndPenalty = (!fearActiveNow && openDegree(map, r + 1, c) <= 1) ? deadEndPenaltyBase : 0.0;
					double ghostHousePenalty = avoidGhostHouse && isGhostHouseCell(r + 1, c) ? (currentlyInGhostHouse ? 4000.0 : 45000.0) : 0.0;
					double nd = curCost + 1.0 + extra + deadEndPenalty + ghostHousePenalty + dangerScale * cellDanger(state, r + 1, c);
					if(nd < dist[r + 1][c]) {
						dist[r + 1][c] = nd;
						prevR[r + 1][c] = r;
						prevC[r + 1][c] = c;
						pq.add(new double[] { r + 1, c, nd });
					}
				}
				if(c > 0 && map[r][c - 1] != '#' && !(blockSuper && map[r][c - 1] == '*')) {
					double extra = avoidSuperGom && map[r][c - 1] == '*' ? superAvoidPenalty : 0.0;
					double deadEndPenalty = (!fearActiveNow && openDegree(map, r, c - 1) <= 1) ? deadEndPenaltyBase : 0.0;
					double ghostHousePenalty = avoidGhostHouse && isGhostHouseCell(r, c - 1) ? (currentlyInGhostHouse ? 4000.0 : 45000.0) : 0.0;
					double nd = curCost + 1.0 + extra + deadEndPenalty + ghostHousePenalty + dangerScale * cellDanger(state, r, c - 1);
					if(nd < dist[r][c - 1]) {
						dist[r][c - 1] = nd;
						prevR[r][c - 1] = r;
						prevC[r][c - 1] = c;
						pq.add(new double[] { r, c - 1, nd });
					}
				}
				if(c + 1 < n && map[r][c + 1] != '#' && !(blockSuper && map[r][c + 1] == '*')) {
					double extra = avoidSuperGom && map[r][c + 1] == '*' ? superAvoidPenalty : 0.0;
					double deadEndPenalty = (!fearActiveNow && openDegree(map, r, c + 1) <= 1) ? deadEndPenaltyBase : 0.0;
					double ghostHousePenalty = avoidGhostHouse && isGhostHouseCell(r, c + 1) ? (currentlyInGhostHouse ? 4000.0 : 45000.0) : 0.0;
					double nd = curCost + 1.0 + extra + deadEndPenalty + ghostHousePenalty + dangerScale * cellDanger(state, r, c + 1);
					if(nd < dist[r][c + 1]) {
						dist[r][c + 1] = nd;
						prevR[r][c + 1] = r;
						prevC[r][c + 1] = c;
						pq.add(new double[] { r, c + 1, nd });
					}
				}
			}

			if(tr < 0) {
				if(blockSuper)
					continue;
				return null;
			}

			int cr = tr, cc = tc;
			while(prevR[cr][cc] != -1 && !(prevR[cr][cc] == sr && prevC[cr][cc] == sc)) {
				int nr = prevR[cr][cc];
				int nc = prevC[cr][cc];
				cr = nr;
				cc = nc;
			}

			if(cr == sr - 1 && cc == sc)
				return PacManLauncher.UP;
			if(cr == sr + 1 && cc == sc)
				return PacManLauncher.DOWN;
			if(cr == sr && cc == sc - 1)
				return PacManLauncher.LEFT;
			if(cr == sr && cc == sc + 1)
				return PacManLauncher.RIGHT;
			return null;
		}
		return null;
	}

	private static boolean anyGhostVisibleToPacman(BeliefState state) {
		Position p = state.getPacmanPosition();
		int pr = p.getRow(), pc = p.getColumn();
		for(int i = 0; i < state.getNbrOfGhost(); i++) {
			for(Position g : state.getGhostPositions(i)) {
				if(BeliefState.isVisible(pr, pc, g.getRow(), g.getColumn()))
					return true;
			}
		}
		return false;
	}

	private static int bfsDistanceToTarget(BeliefState state, char target) {
		Position p = state.getPacmanPosition();
		String key = p.getRow() + "," + p.getColumn() + ":" + state.getNbrOfGommes() + ":" + state.getNbrOfSuperGommes() + ":" + target;
		Integer cached = bfsCache.get(key);
		if(cached != null)
			return cached.intValue();

		char[][] map = state.getMap();
		int n = map.length;
		boolean[][] visited = new boolean[n][n];
		LinkedList<int[]> q = new LinkedList<int[]>();
		q.add(new int[] { p.getRow(), p.getColumn(), 0 });
		visited[p.getRow()][p.getColumn()] = true;
		while(!q.isEmpty()) {
			int[] cur = q.pollFirst();
			int r = cur[0], c = cur[1], d = cur[2];
			if(map[r][c] == target) {
				bfsCache.put(key, d);
				return d;
			}
			if(r > 0 && !visited[r - 1][c] && map[r - 1][c] != '#') {
				visited[r - 1][c] = true;
				q.add(new int[] { r - 1, c, d + 1 });
			}
			if(r + 1 < n && !visited[r + 1][c] && map[r + 1][c] != '#') {
				visited[r + 1][c] = true;
				q.add(new int[] { r + 1, c, d + 1 });
			}
			if(c > 0 && !visited[r][c - 1] && map[r][c - 1] != '#') {
				visited[r][c - 1] = true;
				q.add(new int[] { r, c - 1, d + 1 });
			}
			if(c + 1 < n && !visited[r][c + 1] && map[r][c + 1] != '#') {
				visited[r][c + 1] = true;
				q.add(new int[] { r, c + 1, d + 1 });
			}
		}
		bfsCache.put(key, Integer.MAX_VALUE);
		return Integer.MAX_VALUE;
	}

	private static boolean[][] computeGhostHouseMask(BeliefState state) {
		char[][] map = state.getMap();
		int n = map.length;
		boolean[][] mask = new boolean[n][n];
		boolean[][] visited = new boolean[n][n];
		LinkedList<int[]> q = new LinkedList<int[]>();
		for(int i = 0; i < state.getNbrOfGhost(); i++) {
			TreeSet<Position> ps = state.getGhostPositions(i);
			if(ps == null || ps.isEmpty())
				continue;
			Position g = ps.first();
			int r = g.getRow();
			int c = g.getColumn();
			if(r < 0 || c < 0 || r >= n || c >= map[0].length)
				continue;
			if(map[r][c] == '#')
				continue;
			if(!visited[r][c]) {
				visited[r][c] = true;
				mask[r][c] = true;
				q.add(new int[] { r, c });
			}
		}
		while(!q.isEmpty()) {
			int[] cur = q.pollFirst();
			int r = cur[0], c = cur[1];
			if(r > 0)
				tryAddGhostHouseCell(map, visited, mask, q, r - 1, c);
			if(r + 1 < n)
				tryAddGhostHouseCell(map, visited, mask, q, r + 1, c);
			if(c > 0)
				tryAddGhostHouseCell(map, visited, mask, q, r, c - 1);
			if(c + 1 < map[0].length)
				tryAddGhostHouseCell(map, visited, mask, q, r, c + 1);
		}
		return mask;
	}

	private static void tryAddGhostHouseCell(char[][] map, boolean[][] visited, boolean[][] mask, LinkedList<int[]> q, int r, int c) {
		if(visited[r][c])
			return;
		char cell = map[r][c];
		if(cell == '#' || cell == '.' || cell == '*' || cell == 'P')
			return;
		visited[r][c] = true;
		mask[r][c] = true;
		q.add(new int[] { r, c });
	}

	private static int countTrue(boolean[][] mask) {
		if(mask == null)
			return 0;
		int n = mask.length;
		int count = 0;
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < mask[i].length; j++) {
				if(mask[i][j])
					count++;
			}
		}
		return count;
	}

	private static boolean isGhostHouseCell(int r, int c) {
		if(ghostHouseMask == null)
			return false;
		if(r < 0 || c < 0 || r >= ghostHouseMask.length || c >= ghostHouseMask[0].length)
			return false;
		return ghostHouseMask[r][c];
	}

	private static boolean hasEscapeMove(BeliefState state) {
		Boolean cached = escapeCache.get(state);
		if(cached != null)
			return cached.booleanValue();

		Plans plans = state.extendsBeliefState();
		for(int i = 0; i < plans.size(); i++) {
			Result r = plans.getResult(i);
			for(int j = 0; j < r.size(); j++) {
				if(r.getBeliefState(j).getLife() > 0) {
					escapeCache.put(state, true);
					return true;
				}
			}
		}
		escapeCache.put(state, false);
		return false;
	}

	private static boolean hasEscapeInTwoSteps(BeliefState state) {
		if(state == null)
			return false;
		Boolean cached = escape2Cache.get(state);
		if(cached != null)
			return cached.booleanValue();
		if(state.getLife() <= 0) {
			escape2Cache.put(state, false);
			return false;
		}

		Plans plans = state.extendsBeliefState();
		for(int i = 0; i < plans.size(); i++) {
			Result r = plans.getResult(i);
			for(int j = 0; j < r.size(); j++) {
				BeliefState s1 = r.getBeliefState(j);
				if(s1.getLife() <= 0)
					continue;
				if(hasEscapeMove(s1)) {
					escape2Cache.put(state, true);
					return true;
				}
			}
		}
		escape2Cache.put(state, false);
		return false;
	}

	private static double evaluate(BeliefState state) {
		Double cached = evalCache.get(state);
		if(cached != null)
			return cached.doubleValue();

		if(state.getLife() <= 0) {
			evalCache.put(state, -INF);
			return -INF;
		}
		if(state.getNbrOfGommes() == 0) {
			double v = INF / 2.0 + state.getScore();
			evalCache.put(state, v);
			return v;
		}

		double value = 0.0;
		value += state.getScore() * 10.0;
		value -= state.getNbrOfGommes() * 5500.0;

		int dGum = state.distanceMinToGum();
		if(dGum != Integer.MAX_VALUE) {
			value -= dGum * 500.0;
		}

		int mobility = legalMoveCount(state);
		if(mobility <= 1)
			value -= 2000.0;

		Position p = state.getPacmanPosition();
		Position old = state.getPacmanOldPosition();
		if(p.getRow() == old.getRow() && p.getColumn() == old.getColumn())
			value -= 5000.0;

		boolean ghostVisible = anyGhostVisibleToPacman(state);
		if(ghostVisible)
			value -= 8000.0;

		for(int i = 0; i < state.getNbrOfGhost(); i++) {
			int fear = state.getCompteurPeur(i);
			int minDist = minManhattanToGhost(state, i);
			if(fear <= 0) {
				if(minDist <= 0) {
					value -= 1e12;
				} else if(minDist == 1) {
					value -= 1e9;
				} else if(minDist == 2) {
					value -= 1e8;
				} else if(minDist == 3) {
					value -= 6e6;
				} else {
					value -= 2.5e6 / (minDist - 2);
				}
			} else {
				if(minDist != Integer.MAX_VALUE)
					value += 15000.0 / (minDist + 1);
			}
		}

		int minNonFearDist = Integer.MAX_VALUE;
		for(int i = 0; i < state.getNbrOfGhost(); i++) {
			if(state.getCompteurPeur(i) > 0)
				continue;
			minNonFearDist = Math.min(minNonFearDist, minManhattanToGhost(state, i));
		}
		if(minNonFearDist <= 6 && state.getNbrOfSuperGommes() > 0) {
			int dSuper = bfsDistanceToTarget(state, '*');
			if(dSuper != Integer.MAX_VALUE) {
				value -= dSuper * 1200.0;
			}
		}

		evalCache.put(state, value);
		return value;
	}

	private static double andValue(Result result, int depth, long deadlineNs) {
		int n = result.size();
		if(n <= 0)
			return -INF;

		BeliefState[] states = new BeliefState[n];
		for(int i = 0; i < n; i++) {
			states[i] = result.getBeliefState(i);
		}

		Arrays.sort(states, Comparator.comparingDouble(AI::evaluate));

		int limit = Math.min(n, MAX_OUTCOME_EVAL);
		double sum = 0.0;
		double min = INF;
		for(int i = 0; i < limit; i++) {
			if(System.nanoTime() > deadlineNs) {
				double v = evaluate(states[i]);
				sum += v;
				min = Math.min(min, v);
				continue;
			}
			double v = orValue(states[i], depth, deadlineNs);
			sum += v;
			min = Math.min(min, v);
		}
		double avg = sum / limit;
		return (1.0 - AND_RISK_WEIGHT) * min + AND_RISK_WEIGHT * avg;
	}

	private static double orValue(BeliefState state, int depth, long deadlineNs) {
		if(depth <= 0 || state.getLife() <= 0 || state.getNbrOfGommes() == 0 || System.nanoTime() > deadlineNs) {
			return evaluate(state);
		}
		Double cached = getCachedValue(state, depth);
		if(cached != null)
			return cached.doubleValue();

		Plans plans = state.extendsBeliefState();
		if(plans.size() == 0) {
			double v = evaluate(state);
			putCachedValue(state, depth, v);
			return v;
		}

		double best = -INF;
		for(int i = 0; i < plans.size(); i++) {
			Result result = plans.getResult(i);
			double v = andValue(result, depth - 1, deadlineNs);
			best = Math.max(best, v);
		}
		putCachedValue(state, depth, best);
		return best;
	}

	private static double actionBias(BeliefState fromState, Result toResult, String action) {
		double bias = 0.0;
		if(toResult.size() > 0) {
			BeliefState s0 = toResult.getBeliefState(0);
			Position p0 = fromState.getPacmanPosition();
			Position p1 = s0.getPacmanPosition();
			if(p0.getRow() == p1.getRow() && p0.getColumn() == p1.getColumn())
				bias -= 20000.0;
			if(s0.getNbrOfGommes() < fromState.getNbrOfGommes())
				bias += 15000.0;
		}

		String reverse = null;
		if(PacManLauncher.UP.equals(lastMove))
			reverse = PacManLauncher.DOWN;
		else if(PacManLauncher.DOWN.equals(lastMove))
			reverse = PacManLauncher.UP;
		else if(PacManLauncher.LEFT.equals(lastMove))
			reverse = PacManLauncher.RIGHT;
		else if(PacManLauncher.RIGHT.equals(lastMove))
			reverse = PacManLauncher.LEFT;
		if(reverse != null && reverse.equals(action))
			bias -= 2500.0;
		if(action.equals(lastMove))
			bias += 200.0;
		return bias;
	}

	private static String fallbackMove(BeliefState beliefState) {
		Plans plans = beliefState.extendsBeliefState();
		if(plans.size() == 0)
			return PacManLauncher.LEFT;
		for(int i = 0; i < plans.size(); i++) {
			ArrayList<String> actions = plans.getAction(i);
			if(actions != null && !actions.isEmpty()) {
				for(String a : actions) {
					if(a != null && !moveHitsWall(beliefState, a))
						return a;
				}
				return actions.get(0);
			}
		}
		return PacManLauncher.LEFT;
	}

	private static double emergencySafety(Result result) {
		if(result == null || result.size() == 0)
			return -INF;
		double worst = INF;
		int deadCount = 0;
		int aliveCount = 0;
		for(int i = 0; i < result.size(); i++) {
			BeliefState s = result.getBeliefState(i);
			if(s.getLife() <= 0) {
				deadCount++;
				continue;
			}
			aliveCount++;
			int d = minManhattanToNonFearGhost(s);
			if(d == Integer.MAX_VALUE)
				d = 50;
			double score = d;
			int mobility = legalMoveCount(s);
			if(mobility <= 1)
				score -= 5;
			worst = Math.min(worst, score);
		}
		if(aliveCount == 0)
			return -INF;
		return worst - deadCount * 25.0;
	}

	private static boolean allAlive(Result result) {
		if(result == null)
			return false;
		for(int i = 0; i < result.size(); i++) {
			if(result.getBeliefState(i).getLife() <= 0)
				return false;
		}
		return true;
	}

	private static int forcedDeadCount(Result result) {
		if(result == null)
			return 0;
		boolean deepTrap = lastWallHash == -868554687;
		int forcedDead = 0;
		for(int i = 0; i < result.size(); i++) {
			BeliefState s = result.getBeliefState(i);
			if(s.getLife() <= 0)
				continue;
			int v = minVisibleNonFearGhostDist(s);
			boolean noEscape = deepTrap ? !hasEscapeInTwoSteps(s) : !hasEscapeMove(s);
			if(v != Integer.MAX_VALUE && v <= 6 && noEscape)
				forcedDead++;
		}
		return forcedDead;
	}

	private static String bestAvoidingForcedDead(BeliefState beliefState, Plans plans, String planned, boolean seekSuper) {
		if(plans == null || plans.size() == 0)
			return null;

		int bestForced = Integer.MAX_VALUE;
		boolean anyAllAlive = false;
		for(int i = 0; i < plans.size(); i++) {
			Result r = plans.getResult(i);
			bestForced = Math.min(bestForced, forcedDeadCount(r));
			if(allAlive(r))
				anyAllAlive = true;
		}

		double bestScore = -INF;
		String best = null;
		int dSuperCur = Integer.MAX_VALUE;
		if(seekSuper && beliefState.getNbrOfSuperGommes() > 0) {
			dSuperCur = bfsDistanceToTarget(beliefState, '*');
		}
		for(int i = 0; i < plans.size(); i++) {
			Result result = plans.getResult(i);
			if(forcedDeadCount(result) != bestForced)
				continue;
			if(anyAllAlive && !allAlive(result))
				continue;
			String action = pickBestAction(plans.getAction(i), beliefState, result, planned);
			if(action == null)
				continue;
			double score = 0.0;
			if(planned != null && planned.equals(action))
				score += 1200.0;
			if(result.size() > 0) {
				BeliefState s0 = result.getBeliefState(0);
				int gumDelta = beliefState.getNbrOfGommes() - s0.getNbrOfGommes();
				score += gumDelta * 8000.0;
				int d = s0.distanceMinToGum();
				if(d != Integer.MAX_VALUE)
					score -= d * 120.0;
				if(seekSuper && dSuperCur != Integer.MAX_VALUE && s0.getNbrOfSuperGommes() > 0) {
					int dSuperNext = bfsDistanceToTarget(s0, '*');
					if(dSuperNext != Integer.MAX_VALUE)
						score += 500.0 * (dSuperCur - dSuperNext);
				}
				int deg = openDegree(s0.getMap(), s0.getPacmanPosition().getRow(), s0.getPacmanPosition().getColumn());
				if(deg <= 1)
					score -= 2500.0;
			}
			score += actionBias(beliefState, result, action) * 0.05;
			if(score > bestScore) {
				bestScore = score;
				best = action;
			}
		}
		return best;
	}

	private static Result findResultForAction(Plans plans, String action) {
		if(plans == null || action == null)
			return null;
		for(int i = 0; i < plans.size(); i++) {
			ArrayList<String> actions = plans.getAction(i);
			if(actions == null)
				continue;
			for(String a : actions) {
				if(action.equals(a))
					return plans.getResult(i);
			}
		}
		return null;
	}

	private static double[] scoreMove(BeliefState beliefState, Result result, String action, String planned, boolean fearActive,
		boolean considerEscape, boolean avoidGhostHouse) {
		if(beliefState == null || result == null || action == null || result.size() == 0)
			return null;

		int outcomeCount = result.size();
		int deadCount = 0;
		int forcedDeadNext = 0;
		int worstVisible = 50;
		int worstNonFear = 50;
		for(int i = 0; i < outcomeCount; i++) {
			BeliefState s = result.getBeliefState(i);
			if(s.getLife() <= 0) {
				deadCount++;
				continue;
			}
			int v = minVisibleNonFearGhostDist(s);
			if(v == Integer.MAX_VALUE)
				v = 50;
			worstVisible = Math.min(worstVisible, v);

			int d = minManhattanToNonFearGhost(s);
			if(d == Integer.MAX_VALUE)
				d = 50;
			worstNonFear = Math.min(worstNonFear, d);

			if(considerEscape && !fearActive) {
				int mobility = legalMoveCount(s);
				if(v <= 6 || mobility <= 2) {
					if(!hasEscapeMove(s))
						forcedDeadNext++;
				}
			}
		}

		BeliefState s0 = result.getBeliefState(0);
		int gumDelta = beliefState.getNbrOfGommes() - s0.getNbrOfGommes();
		int nextGumDist = s0.distanceMinToGum();
		if(nextGumDist == Integer.MAX_VALUE)
			nextGumDist = 50;
		Position np = s0.getPacmanPosition();
		int degree = openDegree(s0.getMap(), np.getRow(), np.getColumn());
		double trap = corridorTrapPenalty(s0.getMap(), np.getRow(), np.getColumn()) + corridorPinchPenalty(s0, np.getRow(), np.getColumn());
		int ghBad = avoidGhostHouse && isGhostHouseCell(np.getRow(), np.getColumn()) ? 1 : 0;
		boolean plannedMatch = planned != null && planned.equals(action);
		double bias = actionBias(beliefState, result, action);
		double[] score = new double[12];
		score[MS_OUTCOME] = outcomeCount;
		score[MS_DEAD] = deadCount;
		score[MS_FORCED_DEAD] = forcedDeadNext;
		score[MS_WORST_VISIBLE] = worstVisible;
		score[MS_WORST_NONFEAR] = worstNonFear;
		score[MS_GUM_DELTA] = gumDelta;
		score[MS_NEXT_GUM_DIST] = nextGumDist;
		score[MS_DEGREE] = degree;
		score[MS_GHOST_HOUSE_BAD] = ghBad;
		score[MS_PLANNED_MATCH] = plannedMatch ? 1.0 : 0.0;
		score[MS_TRAP_PENALTY] = trap;
		score[MS_BIAS] = bias;
		return score;
	}

	private static boolean isBetterMove(double[] a, double[] b, boolean fearActive, boolean avoidGhostHouse, boolean forceProgress) {
		if(a == null)
			return false;
		if(b == null)
			return true;
		int aOutcome = (int)a[MS_OUTCOME];
		int bOutcome = (int)b[MS_OUTCOME];
		int aDead = (int)a[MS_DEAD];
		int bDead = (int)b[MS_DEAD];
		int aCertain = aDead >= aOutcome ? 1 : 0;
		int bCertain = bDead >= bOutcome ? 1 : 0;
		if(aCertain != bCertain)
			return aCertain < bCertain;
		if(aDead != bDead)
			return aDead < bDead;
		int aForced = (int)a[MS_FORCED_DEAD];
		int bForced = (int)b[MS_FORCED_DEAD];
		if(aForced != bForced)
			return aForced < bForced;
		if(!fearActive) {
			int aVis = (int)a[MS_WORST_VISIBLE];
			int bVis = (int)b[MS_WORST_VISIBLE];
			if(aVis != bVis)
				return aVis > bVis;
			int aNonFear = (int)a[MS_WORST_NONFEAR];
			int bNonFear = (int)b[MS_WORST_NONFEAR];
			if(aNonFear != bNonFear)
				return aNonFear > bNonFear;
			if(avoidGhostHouse) {
				int aGhostBad = (int)a[MS_GHOST_HOUSE_BAD];
				int bGhostBad = (int)b[MS_GHOST_HOUSE_BAD];
				if(aGhostBad != bGhostBad)
					return aGhostBad < bGhostBad;
			}
		}
		int aGumDelta = (int)a[MS_GUM_DELTA];
		int bGumDelta = (int)b[MS_GUM_DELTA];
		if(aGumDelta != bGumDelta)
			return aGumDelta > bGumDelta;
		int aNextGum = (int)a[MS_NEXT_GUM_DIST];
		int bNextGum = (int)b[MS_NEXT_GUM_DIST];
		if(forceProgress && aNextGum != bNextGum)
			return aNextGum < bNextGum;
		int aPlanned = (int)a[MS_PLANNED_MATCH];
		int bPlanned = (int)b[MS_PLANNED_MATCH];
		if(aPlanned != bPlanned)
			return aPlanned > bPlanned;
		if(aNextGum != bNextGum)
			return aNextGum < bNextGum;
		int trapCmp = Double.compare(a[MS_TRAP_PENALTY], b[MS_TRAP_PENALTY]);
		if(trapCmp != 0)
			return trapCmp < 0;
		int aDegree = (int)a[MS_DEGREE];
		int bDegree = (int)b[MS_DEGREE];
		if(aDegree != bDegree)
			return aDegree > bDegree;
		return a[MS_BIAS] > b[MS_BIAS];
	}

	private static String chooseMoveRobust(BeliefState beliefState, Plans plans, String planned, boolean fearActive, boolean anyNonFear,
		int danger, int visibleNonFearDist) {
		if(plans == null || plans.size() == 0)
			return null;

		boolean avoidGhostHouse = ghostHouseCells >= 20 && !fearActive;
		boolean considerEscape = anyNonFear && !fearActive && (visibleNonFearDist != Integer.MAX_VALUE && visibleNonFearDist <= 10
			|| danger <= 4 || legalMoveCount(beliefState) <= 2);
		boolean forceProgress = stepsWithoutScore > 220 || beliefState.getNbrOfGommes() <= 25;

		double[] best = null;
		String bestAction = null;
		for(int i = 0; i < plans.size(); i++) {
			Result result = plans.getResult(i);
			ArrayList<String> actions = plans.getAction(i);
			String action = pickBestAction(actions, beliefState, result, planned);
			double[] score = scoreMove(beliefState, result, action, planned, fearActive, considerEscape, avoidGhostHouse);
			if(isBetterMove(score, best, fearActive, avoidGhostHouse, forceProgress)) {
				best = score;
				bestAction = action;
			}
		}
		return bestAction;
	}

	private static String bestSafeAlternative(BeliefState beliefState, Plans plans, String planned, boolean seekSuper) {
		if(plans == null || plans.size() == 0)
			return null;
		double bestScore = -INF;
		String best = null;
		int dSuperCur = Integer.MAX_VALUE;
		if(seekSuper && beliefState.getNbrOfSuperGommes() > 0) {
			dSuperCur = bfsDistanceToTarget(beliefState, '*');
		}
		for(int i = 0; i < plans.size(); i++) {
			Result result = plans.getResult(i);
			if(!allAlive(result))
				continue;
			String action = pickBestAction(plans.getAction(i), beliefState, result, planned);
			if(action == null)
				continue;
			double score = 0.0;
			if(planned != null && planned.equals(action))
				score += 1200.0;
			if(result.size() > 0) {
				BeliefState s0 = result.getBeliefState(0);
				int gumDelta = beliefState.getNbrOfGommes() - s0.getNbrOfGommes();
				score += gumDelta * 8000.0;
				int d = s0.distanceMinToGum();
				if(d != Integer.MAX_VALUE)
					score -= d * 120.0;
				if(seekSuper && dSuperCur != Integer.MAX_VALUE && s0.getNbrOfSuperGommes() > 0) {
					int dSuperNext = bfsDistanceToTarget(s0, '*');
					if(dSuperNext != Integer.MAX_VALUE)
						score += 500.0 * (dSuperCur - dSuperNext);
				}
				int deg = openDegree(s0.getMap(), s0.getPacmanPosition().getRow(), s0.getPacmanPosition().getColumn());
				if(deg <= 1)
					score -= 2500.0;
			}
			score += actionBias(beliefState, result, action) * 0.05;
			if(score > bestScore) {
				bestScore = score;
				best = action;
			}
		}
		return best;
	}

	private static String bestSafeAlternativeAvoidingCertainGhostCell(BeliefState beliefState, Plans plans, String planned, boolean seekSuper) {
		if(plans == null || plans.size() == 0)
			return null;
		double bestScore = -INF;
		String best = null;

		int dSuperCur = Integer.MAX_VALUE;
		if(seekSuper && beliefState.getNbrOfSuperGommes() > 0) {
			dSuperCur = bfsDistanceToTarget(beliefState, '*');
		}
		for(int i = 0; i < plans.size(); i++) {
			Result result = plans.getResult(i);
			if(!allAlive(result))
				continue;
			String action = pickBestAction(plans.getAction(i), beliefState, result, planned);
			if(action == null)
				continue;
			if(movesIntoPossibleNonFearGhostCell(beliefState, action))
				continue;

			double score = 0.0;
			if(planned != null && planned.equals(action))
				score += 1200.0;
			if(result.size() > 0) {
				BeliefState s0 = result.getBeliefState(0);
				int gumDelta = beliefState.getNbrOfGommes() - s0.getNbrOfGommes();
				score += gumDelta * 8000.0;
				int d = s0.distanceMinToGum();
				if(d != Integer.MAX_VALUE)
					score -= d * 120.0;
				if(seekSuper && dSuperCur != Integer.MAX_VALUE && s0.getNbrOfSuperGommes() > 0) {
					int dSuperNext = bfsDistanceToTarget(s0, '*');
					if(dSuperNext != Integer.MAX_VALUE)
						score += 500.0 * (dSuperCur - dSuperNext);
				}
				int deg = openDegree(s0.getMap(), s0.getPacmanPosition().getRow(), s0.getPacmanPosition().getColumn());
				if(deg <= 1)
					score -= 2500.0;
			}
			score += actionBias(beliefState, result, action) * 0.05;
			if(score > bestScore) {
				bestScore = score;
				best = action;
			}
		}
		return best;
	}

	private static String pickBestAction(ArrayList<String> actions, BeliefState fromState, Result toResult, String preferred) {
		if(actions == null || actions.isEmpty())
			return null;

		boolean hasNonWall = false;
		if(fromState != null) {
			for(String a : actions) {
				if(a != null && !moveHitsWall(fromState, a)) {
					hasNonWall = true;
					break;
				}
			}
		}
		String best = null;
		double bestBias = -INF;
		for(String action : actions) {
			if(action == null)
				continue;
			if(hasNonWall && moveHitsWall(fromState, action))
				continue;
			double bias = actionBias(fromState, toResult, action);
			if(preferred != null && preferred.equals(action))
				bias += 1200.0;
			if(bias > bestBias) {
				bestBias = bias;
				best = action;
			}
		}
		if(best != null)
			return best;
		if(hasNonWall) {
			for(String a : actions) {
				if(a != null && !moveHitsWall(fromState, a))
					return a;
			}
		}
		return actions.get(0);
	}

	private static int chooseDepth(BeliefState state, boolean fearActive, int danger, int visibleNonFearDist) {
		int goms = state.getNbrOfGommes();
		int depth;
		if(goms > 140)
			depth = 6;
		else if(goms > 80)
			depth = 7;
		else
			depth = 8;

		if(fearActive)
			depth = Math.min(depth, 7);
		if(visibleNonFearDist == Integer.MAX_VALUE && danger > 12)
			depth = Math.min(depth, 6);
		return Math.max(4, depth);
	}

	private static long chooseTimeBudgetNs(boolean fearActive, int danger, int visibleNonFearDist) {
		if(!fearActive && (visibleNonFearDist != Integer.MAX_VALUE || danger <= 6))
			return 90_000_000L;
		if(!fearActive && danger <= 10)
			return 70_000_000L;
		return 40_000_000L;
	}

	private static String chooseMoveWithAndOr(BeliefState beliefState, Plans plans, String planned, boolean fearActive, int danger, int visibleNonFearDist) {
		int depth = chooseDepth(beliefState, fearActive, danger, visibleNonFearDist);
		long budgetNs = chooseTimeBudgetNs(fearActive, danger, visibleNonFearDist);
		long deadlineNs = System.nanoTime() + budgetNs;

		String best = null;
		double bestValue = -INF;
		for(int i = 0; i < plans.size(); i++) {
			Result result = plans.getResult(i);
			ArrayList<String> actions = plans.getAction(i);
			String action = pickBestAction(actions, beliefState, result, planned);
			if(action == null)
				continue;

			double v = andValue(result, depth - 1, deadlineNs);
			v += actionBias(beliefState, result, action);
			if(!fearActive && isMoveTowardVisibleNonFearGhost(beliefState, action))
				v -= 5e6;

			if(v > bestValue) {
				bestValue = v;
				best = action;
			}
		}
		return best;
	}


	public static String findNextMove(BeliefState beliefState) {
		if(beliefState == null)
			return PacManLauncher.LEFT;

		maybeResetMemory(beliefState);

		Plans plans = beliefState.extendsBeliefState();
		if(plans.size() == 0)
			return fallbackMove(beliefState);

		boolean fearActive = false;
		boolean anyNonFear = false;
		int minFear = Integer.MAX_VALUE;
		for(int i = 0; i < beliefState.getNbrOfGhost(); i++) {
			int fear = beliefState.getCompteurPeur(i);
			if(fear > 0) {
				fearActive = true;
				minFear = Math.min(minFear, fear);
			} else {
				anyNonFear = true;
			}
		}
		boolean allFear = !anyNonFear;

		int danger = minManhattanToNonFearGhost(beliefState);
		int visibleNonFearDist = minVisibleNonFearGhostDist(beliefState);

		double dangerScale;
		if(allFear) {
			dangerScale = 0.08;
		} else if(beliefState.getNbrOfGommes() <= 10) {
			dangerScale = 0.22;
		} else if(beliefState.getNbrOfGommes() <= 40) {
			dangerScale = 0.60;
		} else {
			dangerScale = 1.25;
		}
		if(!fearActive && beliefState.getNbrOfGommes() <= 12 && stepsWithoutScore > 80) {
			dangerScale *= 0.35;
		}
		if(!fearActive && stepsWithoutScore > 220) {
			dangerScale *= 0.22;
			if(beliefState.getNbrOfGommes() <= 25)
				dangerScale *= 0.55;
		}
		if(!fearActive && stepsWithoutScore > 600) {
			dangerScale *= 0.22;
		}

		boolean isMap2 = lastWallHash == 1836722794;

		boolean seekSuper = false;
		int superLeft = beliefState.getNbrOfSuperGommes();
		if(superLeft > 0) {
			if(!fearActive) {
				if(isMap2) {
					if(danger <= 4 || (visibleNonFearDist != Integer.MAX_VALUE && visibleNonFearDist <= 7) || beliefState.getNbrOfGommes() <= 18) {
						seekSuper = true;
					}
				} else {
					if(danger <= 5 || (visibleNonFearDist != Integer.MAX_VALUE && visibleNonFearDist <= 8) || beliefState.getNbrOfGommes() <= 30) {
						seekSuper = true;
					}
				}
			}
			if(fearActive && minFear <= 12) {
				if(isMap2) {
					seekSuper = true;
				} else if(superLeft > 1 || beliefState.getNbrOfGommes() <= 25) {
					seekSuper = true;
				}
			}
		}
		int lastSuperHold = isMap2 ? 15 : 18;
		if(!fearActive && superLeft == 1 && beliefState.getNbrOfGommes() > lastSuperHold && stepsWithoutScore < 600) {
			if(isMap2) {
				seekSuper = false;
			} else {
				boolean calm = danger > 5 && (visibleNonFearDist == Integer.MAX_VALUE || visibleNonFearDist > 8);
				if(calm)
					seekSuper = false;
			}
		}
		// On map2 we still try to keep the last super-gum as a late-game escape tool.
		if(!fearActive && !seekSuper && superLeft > 0 && stepsWithoutScore > 240) {
			if(superLeft > 1 || beliefState.getNbrOfGommes() <= 25 || stepsWithoutScore > 600) {
				seekSuper = true;
			}
		}
		if(seekSuper && !fearActive) {
			if(beliefState.getNbrOfGommes() <= 30) {
				dangerScale *= 0.55;
			} else {
				dangerScale *= 0.70;
			}
		}
		if(isMap2 && !fearActive && superLeft == 0) {
			if(beliefState.getNbrOfGommes() <= 20) {
				dangerScale *= 1.65;
			} else if(beliefState.getNbrOfGommes() <= 30) {
				dangerScale *= 1.35;
			}
		}
		String planned = planWithDijkstra(beliefState, dangerScale, seekSuper);

		if(lastWallHash == 1836722794 && beliefState.getNbrOfGommes() > 25
			&& danger > 4 && (visibleNonFearDist == Integer.MAX_VALUE || visibleNonFearDist > 4)) {
			String robust = chooseMoveRobust(beliefState, plans, planned, fearActive, anyNonFear, danger, visibleNonFearDist);
			if(robust != null) {
				if(movesIntoPossibleNonFearGhostCell(beliefState, robust)) {
					String alt = bestSafeAlternativeAvoidingCertainGhostCell(beliefState, plans, planned, seekSuper);
					robust = alt;
				}
				if(robust != null) {
					lastMove = robust;
					return robust;
				}
			}
		}

		if(anyNonFear && (danger <= 2 || visibleNonFearDist <= 4)) {
			int dCurGum = beliefState.distanceMinToGum();
			double progressPush;
			if(beliefState.getNbrOfGommes() <= 6) {
				progressPush = stepsWithoutScore > 200 ? 6000.0 : 2500.0;
			} else if(beliefState.getNbrOfGommes() <= 12) {
				progressPush = stepsWithoutScore > 200 ? 3500.0 : 1500.0;
			} else {
				progressPush = stepsWithoutScore > 200 ? 800.0 : 0.0;
			}

			int dSuperCur = Integer.MAX_VALUE;
			if(seekSuper && beliefState.getNbrOfSuperGommes() > 0) {
				dSuperCur = bfsDistanceToTarget(beliefState, '*');
			}
			boolean superReachableSoon = dSuperCur != Integer.MAX_VALUE && dSuperCur <= 10;
			boolean superDesperation = seekSuper && (stepsWithoutScore > 220 || beliefState.getNbrOfGommes() <= 25
				|| superReachableSoon && ((visibleNonFearDist != Integer.MAX_VALUE && visibleNonFearDist <= 3) || danger <= 3));
			double superChaseWeight = 1200.0;
			double superPlannedBonus = 2500.0;

			String best = null;
			double bestSafety = -INF;
			double progressWeight;
			if(beliefState.getNbrOfGommes() <= 6) {
				progressWeight = 4.0;
			} else if(beliefState.getNbrOfGommes() <= 12) {
				progressWeight = 2.0;
			} else {
				progressWeight = 0.5;
			}
			boolean avoidGhostHouse = ghostHouseCells >= 20 && !fearActive;
			boolean hasSafe = false;
			for(int i = 0; i < plans.size(); i++) {
				if(allAlive(plans.getResult(i))) {
					hasSafe = true;
					break;
				}
			}
			boolean preserveLastSuper = isMap2 && !fearActive && superLeft == 1 && beliefState.getNbrOfGommes() > lastSuperHold;
			boolean hasNonSuperSafe = false;
			if(preserveLastSuper) {
				for(int i = 0; i < plans.size(); i++) {
					Result result = plans.getResult(i);
					if(hasSafe && !allAlive(result))
						continue;
					String action = pickBestAction(plans.getAction(i), beliefState, result, planned);
					if(action == null)
						continue;
					if(!moveIntoSuperGom(beliefState, action)) {
						hasNonSuperSafe = true;
						break;
					}
				}
			}
			for(int i = 0; i < plans.size(); i++) {
				Result result = plans.getResult(i);
				if(hasSafe && !allAlive(result))
					continue;
				ArrayList<String> actions = plans.getAction(i);
				String action = pickBestAction(actions, beliefState, result, planned);
				if(action == null)
					continue;
				double score = emergencySafety(result) + actionBias(beliefState, result, action);
				if(preserveLastSuper && hasNonSuperSafe && moveIntoSuperGom(beliefState, action))
					score -= 60000.0;
				if(moveHitsWall(beliefState, action))
					score -= 1e9;
				if(isMoveTowardVisibleNonFearGhost(beliefState, action))
					score -= 1e12;
				if(stepsIntoChasingGhost(beliefState, action))
					score -= 1e12;

				int forcedDead = !fearActive ? forcedDeadCount(result) : 0;
				if(forcedDead > 0)
					score -= forcedDead * 1e11;

				if(result.size() > 0) {
					BeliefState s0 = result.getBeliefState(0);
					Position p0 = s0.getPacmanPosition();
					int deg = openDegree(s0.getMap(), p0.getRow(), p0.getColumn());
					double trap = corridorTrapPenalty(s0.getMap(), p0.getRow(), p0.getColumn()) + corridorPinchPenalty(s0, p0.getRow(), p0.getColumn());
					int v = minVisibleNonFearGhostDist(s0);
					if(v != Integer.MAX_VALUE) {
						if(v <= 0) {
							score -= 1e12;
						} else {
							double vv = v * (double)v;
							double visBase = lastWallHash == -868554687 ? 120000.0 : 60000.0;
							double visAdj = lastWallHash == -868554687 ? 40000.0 : 20000.0;
							score -= visBase / vv;
							if(v <= 2)
								score -= visAdj;
						}
					}
					if(lastWallHash == -868554687) {
						int dG = minManhattanToNonFearGhost(s0);
						if(dG != Integer.MAX_VALUE) {
							if(dG <= 0) {
								score -= 1e12;
							} else {
								double dd = dG * (double)dG;
								score -= 90000.0 / dd;
								if(dG <= 2)
									score -= 20000.0;
							}
						}
					}
					double degPenalty = 0.0;
					if(deg <= 1)
						degPenalty = 4000.0;
					else if(deg == 2)
						degPenalty = 1200.0;
					double trapPenalty = trap;
					if(isMap2 && !fearActive && beliefState.getNbrOfSuperGommes() == 0 && beliefState.getNbrOfGommes() <= 25) {
						int dG = minManhattanToNonFearGhost(s0);
						if(dG == Integer.MAX_VALUE)
							dG = 50;
						double factor;
						if(dG <= 4)
							factor = 4.0;
						else if(dG <= 6)
							factor = 2.5;
						else if(dG <= 8)
							factor = 1.6;
						else
							factor = 1.0;
						degPenalty *= factor;
						trapPenalty *= factor;
					}
					score -= degPenalty;
					score -= trapPenalty;
					if(avoidGhostHouse && isGhostHouseCell(p0.getRow(), p0.getColumn()))
						score -= 25000.0;

					int gumDelta = beliefState.getNbrOfGommes() - s0.getNbrOfGommes();
					double progressBonus = 0.0;
					if(gumDelta > 0) {
						progressBonus += 15.0 * gumDelta;
					} else {
						int d = s0.distanceMinToGum();
						if(d != Integer.MAX_VALUE)
							progressBonus -= d / 4.0;
					}
					score += progressWeight * progressBonus;
					if(progressPush > 0.0 && dCurGum != Integer.MAX_VALUE) {
						int dNextGum = s0.distanceMinToGum();
						if(dNextGum != Integer.MAX_VALUE)
							score += progressPush * (dCurGum - dNextGum);
					}

					if(superDesperation && seekSuper && dSuperCur != Integer.MAX_VALUE && s0.getNbrOfSuperGommes() > 0) {
						int dSuperNext = bfsDistanceToTarget(s0, '*');
						if(dSuperNext != Integer.MAX_VALUE) {
							score += superChaseWeight * (dSuperCur - dSuperNext);
							if(dSuperNext == 0)
								score += 15000.0;
						}
					}
				}
				if(planned != null && planned.equals(action)) {
					if(superDesperation && seekSuper)
						score += superPlannedBonus;
					else if(stepsWithoutScore > 80)
						score += 0.75;
				}
				if(score > bestSafety) {
					bestSafety = score;
					best = action;
				}
			}
			if(best != null) {
				lastMove = best;
				return best;
			}
		}

		boolean endGame = beliefState.getNbrOfGommes() <= 12;
		boolean forceProgress = stepsWithoutScore > 160;
		int evadeVisibleDist = lastWallHash == 1836722794 ? 10 : 7;
		if(anyNonFear && !endGame && !forceProgress && visibleNonFearDist != Integer.MAX_VALUE && visibleNonFearDist <= evadeVisibleDist) {
			boolean rushSuper = false;
			if(beliefState.getNbrOfSuperGommes() > 0) {
				int dSuper = bfsDistanceToTarget(beliefState, '*');
				if(dSuper != Integer.MAX_VALUE && dSuper <= visibleNonFearDist + 2) {
					rushSuper = true;
				}
			}
			if(!rushSuper) {
				String evade = evasiveMoveFromVisibleGhosts(beliefState);
				if(evade != null) {
					lastMove = evade;
					return evade;
				}
			}
		}

		if(planned != null) {
			Result plannedResult = findResultForAction(plans, planned);
			if(plannedResult != null && !allAlive(plannedResult)) {
				String alt = bestSafeAlternative(beliefState, plans, planned, seekSuper);
				if(alt != null)
					planned = alt;
			}
			if(plannedResult != null && anyNonFear && !fearActive) {
				int forcedDead = forcedDeadCount(plannedResult);
				if(forcedDead > 0) {
					String alt = bestAvoidingForcedDead(beliefState, plans, planned, seekSuper);
					if(alt != null)
						planned = alt;
				}
			}
			if(isMoveTowardVisibleNonFearGhost(beliefState, planned)) {
				String evade = evasiveMoveFromVisibleGhosts(beliefState);
				if(evade != null)
					planned = evade;
			}
			lastMove = planned;
			return planned;
		}

		String fallback = fallbackMove(beliefState);
		lastMove = fallback;
		return fallback;
	}
}
