package logic;

import java.util.*;

import view.Gomme;


/**
 * class used to represent plan. It will provide for a given set of results an action to perform in each result
 */
//class Plans{
//	ArrayList<Result> results;
//	ArrayList<ArrayList<String>> actions;
//
//	/**
//	 * construct an empty plan
//	 */
//	public Plans() {
//		this.results = new ArrayList<Result>();
//		this.actions = new ArrayList<ArrayList<String>>();
//	}
//
//	/**
//	 * add a new pair of belief-state and corresponding (equivalent) actions
//	 * @param beliefBeliefState the belief state to add
//	 * @param action a list of alternative actions to perform. Only one of them is chosen but their results should be similar
//	 */
//	public void addPlan(Result beliefBeliefState, ArrayList<String> action) {
//		this.results.add(beliefBeliefState);
//		this.actions.add(action);
//	}
//
//	/**
//	 * return the number of belief-states/actions pairs
//	 * @return the number of belief-states/actions pairs
//	 */
//	public int size() {
//		return this.results.size();
//	}
//
//	/**
//	 * return one of the belief-state of the plan
//	 * @param index index of the belief-state
//	 * @return the belief-state corresponding to the index
//	 */
//	public Result getResult(int index) {
//		return this.results.get(index);
//	}
//
//	/**
//	 * return the list of actions performed for a given belief-state
//	 * @param index index of the belief-state
//	 * @return the set of actions to perform for the belief-state corresponding to the index
//	 */
//	public ArrayList<String> getAction(int index){
//		return this.actions.get(index);
//	}
//}
//
///**
// * class used to represent a transition function i.e., a set of possible belief states the agent may be in after performing an action
// */
//class Result{
//	private ArrayList<BeliefState> beliefStates;
//
//	/**
//	 * construct a new result
//	 * @param states the set of states corresponding to the new belief state
//	 */
//	public Result(ArrayList<BeliefState> states) {
//		this.beliefStates = states;
//	}
//
//	/**
//	 * returns the number of belief states
//	 * @return the number of belief states
//	 */
//	public int size() {
//		return this.beliefStates.size();
//	}
//
//	/**
//	 * return one of the belief state
//	 * @param index the index of the belief state to return
//	 * @return the belief state to return
//	 */
//	public BeliefState getBeliefState(int index) {
//		return this.beliefStates.get(index);
//	}
//
//	/**
//	 * return the list of belief-states
//	 * @return the list of belief-states
//	 */
//	public ArrayList<BeliefState> getBeliefStates(){
//		return this.beliefStates;
//	}
//}
//
//
///**
// * class implement the AI to choose the next move of the Pacman
// */
////public class AI{
////	/**
////	 * function that compute the next action to do (among UP, DOWN, LEFT, RIGHT)
////	 * @param beliefState the current belief-state of the agent
////	 * @param deepth the deepth of the search (size of the largest sequence of action checked)
////	 * @return a string describing the next action (among PacManLauncher.UP/DOWN/LEFT/RIGHT)
////	 */
////	public static String findNextMove(BeliefState beliefState) {
////		return PacManLauncher.RIGHT;
////	}
////}
//



















































































































































































































//public class AI {
//
//    // 当前目标豆子位置
//    private static int targetX = -1;
//    private static int targetY = -1;
//
//    // 鬼的记忆：记住每个鬼最后看到的位置和剩余记忆时间
//    private static Map<Integer, int[]> ghostMemory = new HashMap<>();  // ghostId -> [x, y, remainingTurns]
//    private static final int MEMORY_DURATION = 4;  // 记住鬼的位置4步
//
//    // 绕圈检测：记录最近的位置
//    private static LinkedList<String> positionHistory = new LinkedList<>();
//    private static final int HISTORY_SIZE = 16;  // 记录最近16步
//    private static final int LOOP_THRESHOLD = 3;  // 同一位置出现3次以上算绕圈
//
//    // 绕圈时的随机目标
//    private static int randomTargetX = -1;
//    private static int randomTargetY = -1;
//    private static int randomTargetCooldown = 0;  // 锁定随机目标的步数
//
//    /**
//     * 主入口
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
//        // 记录位置历史
//        recordPosition(pacman.x, pacman.y);
//
//        // 更新鬼的记忆
//        updateGhostMemory(state, pacman);
//
//        // 检查是否有危险（可见的鬼 或 记忆中的鬼）
//        boolean hasDanger = hasDangerousGhost(state, pacman);
//
//        if (hasDanger) {
//            // 有危险！
//            resetTarget();  // 清除普通目标
//
//            // 检测是否在绕圈
//            if (isLooping()) {
//                // 绕圈了！找一个随机的安全豆子
//                return escapeToRandomGum(state, pacman, map);
//            } else {
//                // 正常逃跑
//                return escapeAndEat(state, pacman, map);
//            }
//        } else {
//            // 安全，专心吃豆
//            resetRandomTarget();  // 清除随机目标
//            clearHistory();  // 清除历史（安全时不需要检测绕圈）
//            return goToNearestGum(state, pacman, map);
//        }
//    }
//
//    /**
//     * 记录位置
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
//     * 检测是否在绕圈
//     */
//    private static boolean isLooping() {
//        if (positionHistory.size() < HISTORY_SIZE / 2) {
//            return false;
//        }
//
//        // 统计每个位置出现的次数
//        Map<String, Integer> counts = new HashMap<>();
//        for (String pos : positionHistory) {
//            counts.put(pos, counts.getOrDefault(pos, 0) + 1);
//        }
//
//        // 如果有位置出现次数 >= 阈值，说明在绕圈
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
//     * 绕圈时：找一个随机的安全豆子，锁定它
//     */
//    private static String escapeToRandomGum(BeliefState state, Position pacman, char[][] map) {
//        List<int[]> dangers = getAllDangerPositions(state, pacman);
//
//        // 如果还在锁定随机目标
//        if (randomTargetCooldown > 0 && randomTargetX >= 0 && randomTargetY >= 0) {
//            char cell = map[randomTargetX][randomTargetY];
//            if (cell == '.' || cell == '*') {
//                // 目标还在，继续走
//                randomTargetCooldown--;
//                return moveTowardSafely(pacman.x, pacman.y, randomTargetX, randomTargetY, map, dangers);
//            } else {
//                // 目标被吃了，重新找
//                resetRandomTarget();
//            }
//        }
//
//        // 找一个随机的安全豆子
//        int[] randomGum = findRandomSafeGum(pacman, map, dangers);
//        if (randomGum != null) {
//            randomTargetX = randomGum[0];
//            randomTargetY = randomGum[1];
//            randomTargetCooldown = 10;  // 锁定10步
//            clearHistory();  // 清除历史，重新开始检测
//            return moveTowardSafely(pacman.x, pacman.y, randomTargetX, randomTargetY, map, dangers);
//        }
//
//        // 找不到随机目标，用普通逃跑
//        return escapeAndEat(state, pacman, map);
//    }
//
//    /**
//     * 找一个随机的安全豆子
//     */
//    private static int[] findRandomSafeGum(Position pacman, char[][] map, List<int[]> dangers) {
//        // BFS找所有豆子
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
//                // 检查这个豆子是否安全（离鬼足够远）
//                double minDist = Double.MAX_VALUE;
//                for (int[] danger : dangers) {
//                    double dist = Math.abs(danger[0] - x) + Math.abs(danger[1] - y);
//                    minDist = Math.min(minDist, dist);
//                }
//                if (minDist >= 3) {  // 离鬼至少3格
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
//        // 随机选一个
//        Random rand = new Random();
//        return safeGums.get(rand.nextInt(safeGums.size()));
//    }
//
//    /**
//     * 朝目标移动，避开危险
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
//                    // 第一步不能踩危险区域
//                    if (currKey.equals(startKey)) {
//                        boolean isDangerous = false;
//                        for (int[] danger : dangers) {
//                            if (danger[0] == nx && danger[1] == ny) {
//                                isDangerous = true;
//                                break;
//                            }
//                            // 也避免离鬼太近
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
//        // 找不到安全路径，用评分法
//        return escapeByScoring(fromX, fromY, map, dangers);
//    }
//
//    /**
//     * 评分法选方向（备用）
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
//     * 更新鬼的记忆
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
//     * 检查是否有危险的鬼
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
//     * 获取所有危险位置
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
//     * 没有鬼时：BFS找最近豆子
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
//     * 有鬼时：正常逃跑逻辑
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
//    // 当前目标豆子位置
//    private static int targetX = -1;
//    private static int targetY = -1;
//
//    // 鬼的记忆：记住每个鬼最后看到的位置和剩余记忆时间
//    private static Map<Integer, int[]> ghostMemory = new HashMap<>();  // ghostId -> [x, y, remainingTurns]
//    private static final int MEMORY_DURATION = 4;  // 记住鬼的位置4步
//
//    /**
//     * 主入口
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
//        // 更新鬼的记忆
//        updateGhostMemory(state, pacman);
//
//        // 检查是否有危险（可见的鬼 或 记忆中的鬼）
//        boolean hasDanger = hasDangerousGhost(state, pacman);
//
//        if (hasDanger) {
//            // 有危险！避开鬼同时尽量吃豆
//            resetTarget();  // 清除锁定目标
//            return escapeAndEat(state, pacman, map);
//        } else {
//            // 安全，专心吃豆
//            return goToNearestGum(state, pacman, map);
//        }
//    }
//
//    /**
//     * 更新鬼的记忆
//     */
//    private static void updateGhostMemory(BeliefState state, Position pacman) {
//        // 1. 减少所有记忆的剩余时间
//        Iterator<Map.Entry<Integer, int[]>> it = ghostMemory.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<Integer, int[]> entry = it.next();
//            int[] mem = entry.getValue();
//            mem[2]--;  // 减少剩余时间
//            if (mem[2] <= 0) {
//                it.remove();  // 记忆过期，删除
//            }
//        }
//
//        // 2. 更新可见鬼的位置
//        for (int i = 0; i < state.getNbrOfGhost(); i++) {
//            // 跳过害怕状态的鬼
//            if (state.getCompteurPeur(i) > 0) {
//                ghostMemory.remove(i);  // 害怕的鬼不用记
//                continue;
//            }
//
//            TreeSet<Position> ghosts = state.getGhostPositions(i);
//            for (Position ghost : ghosts) {
//                // 如果这个鬼可见，更新记忆
//                if (BeliefState.isVisible(ghost.x, ghost.y, pacman.x, pacman.y)) {
//                    ghostMemory.put(i, new int[]{ghost.x, ghost.y, MEMORY_DURATION});
//                }
//            }
//        }
//    }
//
//    /**
//     * 检查是否有危险的鬼（可见的 或 记忆中距离近的）
//     */
//    private static boolean hasDangerousGhost(BeliefState state, Position pacman) {
//        // 1. 检查可见的鬼
//        for (int i = 0; i < state.getNbrOfGhost(); i++) {
//            if (state.getCompteurPeur(i) > 0) continue;
//
//            TreeSet<Position> ghosts = state.getGhostPositions(i);
//            for (Position ghost : ghosts) {
//                if (BeliefState.isVisible(ghost.x, ghost.y, pacman.x, pacman.y)) {
//                    int dist = Math.abs(ghost.x - pacman.x) + Math.abs(ghost.y - pacman.y);
//                    if (dist <= 5) {
//                        return true;
//                    }
//                }
//            }
//        }
//
//        // 2. 检查记忆中的鬼
//        for (int[] mem : ghostMemory.values()) {
//            int dist = Math.abs(mem[0] - pacman.x) + Math.abs(mem[1] - pacman.y);
//            if (dist <= 4) {  // 记忆中的鬼，距离阈值稍小
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * 获取所有危险位置（可见鬼 + 记忆中的鬼）
//     */
//    private static List<int[]> getAllDangerPositions(BeliefState state, Position pacman) {
//        List<int[]> dangers = new ArrayList<>();
//
//        // 1. 可见的鬼
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
//        // 2. 记忆中的鬼（不可见但还在记忆中）
//        for (Map.Entry<Integer, int[]> entry : ghostMemory.entrySet()) {
//            int[] mem = entry.getValue();
//            // 检查这个位置是否已经在dangers中
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
//     * 没有鬼时：BFS找最近豆子，锁定目标直到吃到
//     */
//    private static String goToNearestGum(BeliefState state, Position pacman, char[][] map) {
//        // 检查目标是否还有效（还有豆子）
//        if (targetX >= 0 && targetY >= 0) {
//            char cell = map[targetX][targetY];
//            if (cell != '.' && cell != '*') {
//                resetTarget();
//            }
//        }
//
//        // 如果没有目标，BFS找最近的豆子
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
//        // 向目标移动
//        return moveToward(pacman.x, pacman.y, targetX, targetY, map);
//    }
//
//    /**
//     * 有鬼时：避开鬼，同时尽量接近豆子
//     */
//    private static String escapeAndEat(BeliefState state, Position pacman, char[][] map) {
//        String[] directions = {PacManLauncher.UP, PacManLauncher.DOWN,
//                               PacManLauncher.LEFT, PacManLauncher.RIGHT};
//        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//
//        // 获取所有危险位置
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
//            // 1. 安全性：离所有危险位置越远越好
//            double minGhostDist = Double.MAX_VALUE;
//            for (int[] danger : dangers) {
//                double dist = Math.abs(danger[0] - nx) + Math.abs(danger[1] - ny);
//                minGhostDist = Math.min(minGhostDist, dist);
//            }
//
//            if (minGhostDist <= 1) {
//                score -= 10000;  // 太近了，非常危险
//            } else if (minGhostDist <= 2) {
//                score -= 3000;
//            } else {
//                score += minGhostDist * 100;
//            }
//
//            // 2. 吃豆奖励
//            char cell = map[nx][ny];
//            if (cell == '*') {
//                score += 5000;
//            } else if (cell == '.') {
//                score += 2000;
//            }
//
//            // 3. 接近最近豆子的奖励
//            if(state.getNbrOfSuperGommes()>0){
//                int[] nearestGum = bfsFindNearestSuperGum(nx, ny, map);
//                if (nearestGum != null) {
//                    int distToGum = Math.abs(nearestGum[0] - nx) + Math.abs(nearestGum[1] - ny);
//                    score += (20 - Math.min(distToGum, 20)) * 50;
//                }
//            }else {
//                int[] nearestGum = bfsFindNearestGum(nx, ny, map);
//                if (nearestGum != null) {
//                    int distToGum = Math.abs(nearestGum[0] - nx) + Math.abs(nearestGum[1] - ny);
//                    score += (20 - Math.min(distToGum, 20)) * 50;
//                }
//            }
//
//            // 4. 开放空间奖励
//            int openDirs = countOpenDirections(nx, ny, map);
//            score += openDirs * 200;
//
//            // 5. 避免走向拐角（鬼可能就在后面）
//            if (openDirs <= 1) {
//                score -= 500;  // 死胡同惩罚
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
//    /**
//     * BFS找最近的豆子
//     */
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
//    /**
//     * 向目标位置移动（BFS找最短路径）
//     */
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
//        // 找不到路径
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
//    /**
//     * 计算开放方向数
//     */
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
//    /**
//     * 检查位置是否有效
//     */
//    private static boolean isValid(int x, int y, char[][] map) {
//        return x >= 0 && x < map.length &&
//               y >= 0 && y < map[0].length &&
//               map[x][y] != '#';
//    }
//
//    /**
//     * 重置目标
//     */
//    private static void resetTarget() {
//        targetX = -1;
//        targetY = -1;
//    }
//
//    /**
//     * 完全重置
//     */
//    private static void resetAll() {
//        resetTarget();
//        ghostMemory.clear();
//    }
//}


//public class AI {
//
//    private static final int MAX_DEPTH = 2;
//
//    // 目标豆子
//    private static int targetX = -1;
//    private static int targetY = -1;
//
//    // 鬼的记忆：记住每个鬼最后看到的位置和剩余记忆时间
//    private static Map<Integer, int[]> ghostMemory = new HashMap<>();  // ghostId -> [x, y, remainingTurns]
//    private static final int MEMORY_DURATION = 4;
//
//    // 绕圈检测
//    private static LinkedList<String> positionHistory = new LinkedList<>();
//    private static final int HISTORY_SIZE = 16;
//    private static final int LOOP_THRESHOLD = 3;
//
//    // 绕圈时的随机目标
//    private static int randomTargetX = -1;
//    private static int randomTargetY = -1;
//    private static int randomTargetCooldown = 0;
//
//    // 上次方向（用于打破绕圈）
//    private static String lastDirection = null;
//    private static int sameDirectionCount = 0;  // 连续同方向次数
//
//    /**
//     * 主入口
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
//        // 记录位置历史
//        recordPosition(pacman.x, pacman.y);
//
//        // 更新鬼的记忆
//        updateGhostMemory(state, pacman);
//
//        // 检查是否无敌
//        boolean isInvincible = false;
//        for (int i = 0; i < state.getNbrOfGhost(); i++) {
//            if (state.getCompteurPeur(i) > 0) {
//                isInvincible = true;
//                break;
//            }
//        }
//
//        // 用AND-OR搜索
//        return andOrSearch(state, isLooping(), isInvincible);
//    }
//
//    /**
//     * AND-OR搜索主入口
//     */
//    private static String andOrSearch(BeliefState state, boolean looping, boolean isInvincible) {
//        Plans plans = state.extendsBeliefState();
//
//        if (plans.size() == 0) {
//            return PacManLauncher.RIGHT;
//        }
//
//        Position pacman = state.getPacmanPosition();
//        List<int[]> dangers = isInvincible ? new ArrayList<>() : getAllDangerPositions(state, pacman);
//
//        String bestAction = null;
//        double bestValue = Double.NEGATIVE_INFINITY;
//
//        for (int i = 0; i < plans.size(); i++) {
//            ArrayList<String> actionList = plans.getAction(i);
//            Result result = plans.getResult(i);
//
//            if (actionList.isEmpty()) continue;
//
//            String action = actionList.get(0);
//            double value = andNode(result, state, MAX_DEPTH, dangers, isInvincible);
//
//            // 强方向惯性：防止横跳
//            if (lastDirection != null) {
//                if (isOpposite(action, lastDirection)) {
//                    // 掉头：重惩罚（除非绕圈）
//                    if (looping) {
//                        value += 500;  // 绕圈时鼓励掉头
//                    } else {
//                        value -= 2000;  // 正常时重惩罚掉头
//                    }
//                } else if (action.equals(lastDirection)) {
//                    // 保持方向
//                    if (looping) {
//                        value -= 3000;  // 绕圈时惩罚同方向
//                    } else {
//                        value += 500;  // 正常时鼓励保持
//                    }
//                }
//            }
//
//            if (value > bestValue) {
//                bestValue = value;
//                bestAction = action;
//            }
//        }
//
//        // 记录方向
//        if (bestAction != null) {
//            if (bestAction.equals(lastDirection)) {
//                sameDirectionCount++;
//            } else {
//                sameDirectionCount = 0;
//            }
//            lastDirection = bestAction;
//        }
//
//        // 换方向时清空历史
//        if (looping) {
//            positionHistory.clear();
//        }
//
//        return bestAction != null ? bestAction : PacManLauncher.RIGHT;
//    }
//
//    /**
//     * 判断方向是否相反
//     */
//    private static boolean isOpposite(String d1, String d2) {
//        if (d1 == null || d2 == null) return false;
//        return (d1.equals(PacManLauncher.UP) && d2.equals(PacManLauncher.DOWN)) ||
//               (d1.equals(PacManLauncher.DOWN) && d2.equals(PacManLauncher.UP)) ||
//               (d1.equals(PacManLauncher.LEFT) && d2.equals(PacManLauncher.RIGHT)) ||
//               (d1.equals(PacManLauncher.RIGHT) && d2.equals(PacManLauncher.LEFT));
//    }
//
//    /**
//     * AND节点：处理不确定性，跳过死亡取平均
//     */
//    private static double andNode(Result result, BeliefState prevState, int depth, List<int[]> dangers, boolean isInvincible) {
//        if (result == null || result.size() == 0) {
//            return evaluate(prevState, dangers, isInvincible);
//        }
//
//        ArrayList<Double> aliveValues = new ArrayList<>();
//        int maxToProcess = 10;
//        int processed = 0;
//
//        for (int i = 0; i < result.size() && processed < maxToProcess; i++) {
//            BeliefState nextState = result.getBeliefState(i);
//            if (nextState == null) continue;
//            processed++;
//
//            if (nextState.getLife() < prevState.getLife()) {
//                continue;  // 跳过死亡
//            }
//
//            double value;
//            if (depth <= 1) {
//                value = evaluate(nextState, dangers, isInvincible);
//            } else {
//                value = orNode(nextState, depth - 1, dangers, isInvincible);
//            }
//            aliveValues.add(value);
//        }
//
//        if (aliveValues.isEmpty()) {
//            return -1000000;
//        }
//
//        double sum = 0;
//        for (double v : aliveValues) {
//            sum += v;
//        }
//        return sum / aliveValues.size();
//    }
//
//    /**
//     * OR节点：选最大值
//     */
//    private static double orNode(BeliefState state, int depth, List<int[]> dangers, boolean isInvincible) {
//        if (depth <= 0) {
//            return evaluate(state, dangers, isInvincible);
//        }
//
//        Plans plans = state.extendsBeliefState();
//        if (plans.size() == 0) {
//            return evaluate(state, dangers, isInvincible);
//        }
//
//        double bestValue = Double.NEGATIVE_INFINITY;
//        for (int i = 0; i < plans.size(); i++) {
//            Result result = plans.getResult(i);
//            double value = andNode(result, state, depth, dangers, isInvincible);
//            bestValue = Math.max(bestValue, value);
//        }
//        return bestValue;
//    }
//
//    /**
//     * 评估函数
//     */
//    private static double evaluate(BeliefState state, List<int[]> dangers, boolean isInvincible) {
//        if (state == null) return 0;
//
//        Position pacman = state.getPacmanPosition();
//        char[][] map = state.getMap();
//        double score = 0;
//
//        // 1. 生命
//        score += state.getLife() * 1000000;
//
//        // 2. 当前格子有豆子（最高优先！）
//        char cell = map[pacman.x][pacman.y];
//        if (cell == '*') {
//            score += 10000;  // 大豆子
//        } else if (cell == '.') {
//            score += 5000;   // 普通豆子
//        }
//
//        // 3. 距离最近豆子（强引导，必须吃豆！）
//        int[] nearestGum = bfsFindNearest(pacman.x, pacman.y, map, '.');
//        if (nearestGum != null) {
//            int dist = Math.abs(nearestGum[0] - pacman.x) + Math.abs(nearestGum[1] - pacman.y);
//            score -= dist * 200;  // 距离越远扣分越多
//        }
//
//        // 4. 鬼的威胁（无敌时跳过！）
//        if (!isInvincible && !dangers.isEmpty()) {
//            double maxGhostDanger = 0;
//            for (int[] danger : dangers) {
//                int dist = Math.abs(danger[0] - pacman.x) + Math.abs(danger[1] - pacman.y);
//                double dangerVal;
//                if (dist <= 1) {
//                    dangerVal = 8000;
//                } else if (dist <= 2) {
//                    dangerVal = 2000;
//                } else if (dist <= 3) {
//                    dangerVal = 500;
//                } else {
//                    dangerVal = 0;
//                }
//                maxGhostDanger = Math.max(maxGhostDanger, dangerVal);
//            }
//            score -= maxGhostDanger;
//        }
//
//        // 5. 开放方向（只在有鬼时重要）
//        if (!isInvincible && !dangers.isEmpty()) {
//            int openDirs = countOpenDirections(pacman.x, pacman.y, map);
//            score += openDirs * 50;
//            if (openDirs <= 1) {
//                score -= 200;
//            }
//        }
//
//        return score;
//    }
//
//    // ==================== 你的原有逻辑 ====================
//
//    /**
//     * 记录位置
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
//     * 检测是否在绕圈
//     */
//    private static boolean isLooping() {
//        if (positionHistory.size() < HISTORY_SIZE / 2) {
//            return false;
//        }
//        Map<String, Integer> counts = new HashMap<>();
//        for (String pos : positionHistory) {
//            counts.put(pos, counts.getOrDefault(pos, 0) + 1);
//        }
//        for (int count : counts.values()) {
//            if (count >= LOOP_THRESHOLD) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 更新鬼的记忆
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
//     * 检查是否有危险的鬼
//     */
//    private static boolean hasDangerousGhost(BeliefState state, Position pacman) {
//        for (int i = 0; i < state.getNbrOfGhost(); i++) {
//            if (state.getCompteurPeur(i) > 0) continue;
//            TreeSet<Position> ghosts = state.getGhostPositions(i);
//            for (Position ghost : ghosts) {
//                if (BeliefState.isVisible(ghost.x, ghost.y, pacman.x, pacman.y)) {
//                    int dist = Math.abs(ghost.x - pacman.x) + Math.abs(ghost.y - pacman.y);
//                    if (dist <= 10) return true;
//                }
//            }
//        }
//        for (int[] mem : ghostMemory.values()) {
//            int dist = Math.abs(mem[0] - pacman.x) + Math.abs(mem[1] - pacman.y);
//            if (dist <= 4) return true;
//        }
//        return false;
//    }
//
//    /**
//     * 获取所有危险位置（可见鬼 + 记忆中的鬼）
//     */
//    private static List<int[]> getAllDangerPositions(BeliefState state, Position pacman) {
//        List<int[]> dangers = new ArrayList<>();
//
//        for (int i = 0; i < state.getNbrOfGhost(); i++) {
//            if (state.getCompteurPeur(i) > 0) continue;
//            TreeSet<Position> ghosts = state.getGhostPositions(i);
//            for (Position ghost : ghosts) {
//                if (BeliefState.isVisible(ghost.x, ghost.y, pacman.x, pacman.y)) {
//                    dangers.add(new int[]{ghost.x, ghost.y});
//                }
//            }
//        }
//
//        for (int[] mem : ghostMemory.values()) {
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
//     * 绕圈时：找随机安全豆子
//     */
//    private static String escapeToRandomGum(BeliefState state, Position pacman, char[][] map) {
//        List<int[]> dangers = getAllDangerPositions(state, pacman);
//
//        if (randomTargetCooldown > 0 && randomTargetX >= 0 && randomTargetY >= 0) {
//            char cell = map[randomTargetX][randomTargetY];
//            if (cell == '.' || cell == '*') {
//                randomTargetCooldown--;
//                return moveTowardSafely(pacman.x, pacman.y, randomTargetX, randomTargetY, map, dangers);
//            } else {
//                resetRandomTarget();
//            }
//        }
//
//        int[] randomGum = findRandomSafeGum(pacman, map, dangers);
//        if (randomGum != null) {
//            randomTargetX = randomGum[0];
//            randomTargetY = randomGum[1];
//            randomTargetCooldown = 10;
//            positionHistory.clear();
//            return moveTowardSafely(pacman.x, pacman.y, randomTargetX, randomTargetY, map, dangers);
//        }
//
//        // 找不到，用AND-OR
//        return andOrSearch(state,false,false);
//    }
//
//    /**
//     * 找随机安全豆子
//     */
//    private static int[] findRandomSafeGum(Position pacman, char[][] map, List<int[]> dangers) {
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
//                double minDist = Double.MAX_VALUE;
//                for (int[] danger : dangers) {
//                    double dist = Math.abs(danger[0] - x) + Math.abs(danger[1] - y);
//                    minDist = Math.min(minDist, dist);
//                }
//                if (minDist >= 3) {
//                    safeGums.add(new int[]{x, y});
//                }
//            }
//
//            for (int[] delta : deltas) {
//                int nx = x + delta[0], ny = y + delta[1];
//                String key = nx + "," + ny;
//                if (isValid(nx, ny, map) && !visited.contains(key)) {
//                    visited.add(key);
//                    queue.offer(new int[]{nx, ny});
//                }
//            }
//        }
//
//        if (safeGums.isEmpty()) return null;
//        Random rand = new Random();
//        return safeGums.get(rand.nextInt(safeGums.size()));
//    }
//
//    /**
//     * 安全地朝目标移动
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
//                int nx = x + deltas[i][0], ny = y + deltas[i][1];
//                String nextKey = nx + "," + ny;
//
//                if (isValid(nx, ny, map) && !firstMove.containsKey(nextKey)) {
//                    if (currKey.equals(startKey)) {
//                        boolean isDangerous = false;
//                        for (int[] danger : dangers) {
//                            int dist = Math.abs(danger[0] - nx) + Math.abs(danger[1] - ny);
//                            if (dist <= 1) {
//                                isDangerous = true;
//                                break;
//                            }
//                        }
//                        if (isDangerous) continue;
//                    }
//
//                    firstMove.put(nextKey, currKey.equals(startKey) ? directions[i] : firstMove.get(currKey));
//                    queue.offer(new int[]{nx, ny});
//                }
//            }
//        }
//
//        // 备用：评分法
//        String bestMove = null;
//        double bestScore = Double.NEGATIVE_INFINITY;
//        for (int i = 0; i < 4; i++) {
//            int nx = fromX + deltas[i][0], ny = fromY + deltas[i][1];
//            if (!isValid(nx, ny, map)) continue;
//
//            double score = 0;
//            double minGhostDist = Double.MAX_VALUE;
//            for (int[] danger : dangers) {
//                double dist = Math.abs(danger[0] - nx) + Math.abs(danger[1] - ny);
//                minGhostDist = Math.min(minGhostDist, dist);
//            }
//            score += minGhostDist * 100;
//            if (minGhostDist <= 1) score -= 10000;
//
//            if (score > bestScore) {
//                bestScore = score;
//                bestMove = directions[i];
//            }
//        }
//        return bestMove != null ? bestMove : PacManLauncher.RIGHT;
//    }
//
//    // ==================== 工具方法 ====================
//
//    private static int[] bfsFindNearest(int startX, int startY, char[][] map, char target) {
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
//            if (target == '.' && (cell == '.' || cell == '*')) {
//                return new int[]{x, y};
//            }
//            if (cell == target) {
//                return new int[]{x, y};
//            }
//
//            for (int[] delta : deltas) {
//                int nx = x + delta[0], ny = y + delta[1];
//                String key = nx + "," + ny;
//                if (isValid(nx, ny, map) && !visited.contains(key)) {
//                    visited.add(key);
//                    queue.offer(new int[]{nx, ny});
//                }
//            }
//        }
//        return null;
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
//        return x >= 0 && x < map.length && y >= 0 && y < map[0].length && map[x][y] != '#';
//    }
//
//    private static void resetRandomTarget() {
//        randomTargetX = -1;
//        randomTargetY = -1;
//        randomTargetCooldown = 0;
//    }
//
//    private static void resetAll() {
//        targetX = -1;
//        targetY = -1;
//        resetRandomTarget();
//        positionHistory.clear();
//        ghostMemory.clear();
//        lastDirection = null;
//        sameDirectionCount = 0;
//    }
//}




/* Helper containers expected by BeliefState.extendsBeliefState() */
class Plans {
    ArrayList<Result> results;
    ArrayList<ArrayList<String>> actions;

    public Plans() {
        this.results = new ArrayList<Result>();
        this.actions = new ArrayList<ArrayList<String>>();
    }

    public void addPlan(Result beliefState, ArrayList<String> action) {
        this.results.add(beliefState);
        this.actions.add(action);
    }

    public int size() { return this.results.size(); }

    public Result getResult(int index) { return this.results.get(index); }

    public ArrayList<String> getAction(int index) { return this.actions.get(index); }
}

class Result {
    private ArrayList<BeliefState> beliefStates;

    public Result(ArrayList<BeliefState> states) { this.beliefStates = states; }

    public int size() { return this.beliefStates.size(); }

    public BeliefState getBeliefState(int index) { return this.beliefStates.get(index); }

    public ArrayList<BeliefState> getBeliefStates() { return this.beliefStates; }
}



public class AI {

    /* ===================== Search config ===================== */

    private static final int MAX_DEPTH = 2; // keep small to be fast

    /* ===================== Root baseline for reward ===================== */

    private static int rootDots = -1;
    private static int rootSupers = -1;

    /* ===================== Direction memory ===================== */

    private static String lastDirection = null;

    /* ===================== Target mechanism ===================== */

    private static int targetX = Integer.MIN_VALUE;
    private static int targetY = Integer.MIN_VALUE;

    // If no score / loop -> swap target
    private static int lastScore = Integer.MIN_VALUE;
    private static int noScoreStreak = 0;

    private static final int NO_SCORE_SWAP_THRESHOLD = 10;

    // Loop detection (short history)
    private static final LinkedList<String> positionHistory = new LinkedList<>();
    private static final int HISTORY_SIZE = 16;
    private static final int LOOP_THRESHOLD = 3;

    /* ===================== Ghost forgetting (must keep) ===================== */

    private static final int FORGET_AFTER_TURNS = 30;
    private static boolean[] seenEver = null;
    private static int[] unseenTurns = null;
    private static int nGhostMemo = -1;

    /* ===================== Heuristic weights ===================== */

    private static final int LIFE_WEIGHT = 1_000_000;

    private static final int DOT_REWARD = 800;
    private static final int SUPER_REWARD = 6000;

    private static final int TARGET_REACH_BONUS = 15000;
    private static final int TARGET_DIST_PENALTY = 250;

    private static final int REVERSE_PENALTY = 1200;
    private static final int KEEP_DIR_BONUS = 200;

    private static final int DEATH_PENALTY = -1_000_000;

    // Danger penalties based on min possible ghost distance
    private static final int DANGER_D1 = 120_000;
    private static final int DANGER_D2 = 40_000;
    private static final int DANGER_D3 = 15_000;

    private static final Random RNG = new Random();

    /* ===================== Entry ===================== */

    public static String findNextMove(BeliefState state) {
        if (state == null || state.getLife() <= 0) {
            resetAll();
            return PacManLauncher.RIGHT;
        }

        // Root food counters for reward shaping
        rootDots = state.getNbrOfGommes();
        rootSupers = state.getNbrOfSuperGommes();

        // Score stagnation tracking (for target swap)
        if (lastScore == Integer.MIN_VALUE) lastScore = state.getScore();
        if (state.getScore() == lastScore) noScoreStreak++;
        else noScoreStreak = 0;
        lastScore = state.getScore();

        // Loop tracking
        recordPosition(state.getPacmanPosition());

        // Ghost forgetting mechanism update (MUST KEEP)
        updateGhostForgetting(state);

        // Update / choose target gum (nearest). If stuck, swap target.
        updateTarget(state);

        String act = orNode(state, MAX_DEPTH);
        if (act == null) act = PacManLauncher.RIGHT;
        lastDirection = act;
        return act;
    }

    /* ===================== OR / AND search ===================== */

    private static String orNode(BeliefState state, int depth) {
        Plans plans = state.extendsBeliefState();
        if (plans == null || plans.size() == 0) return PacManLauncher.RIGHT;

        // IMPORTANT:
        // Teacher's BeliefState.extendsBeliefState() can include a special "listNull" plan
        // that groups actions hitting a wall (#). Those actions do NOT move Pacman
        // (BeliefState.move(0,0,...) is used), so Pacman will look like he "doesn't move".
        // We must ignore those wall-actions whenever there exists at least one action
        // that actually changes Pacman's position.
        boolean anyMovingAction = false;
        for (int i = 0; i < plans.size(); i++) {
            ArrayList<String> al = plans.getAction(i);
            if (al == null || al.isEmpty()) continue;
            Result r = plans.getResult(i);
            if (actionActuallyMoves(r, state)) {
                anyMovingAction = true;
                break;
            }
        }

        boolean panic = isPanic(state);
        boolean forbidReverse = panic && ghostBehind(state);

        String bestAct = null;
        double bestVal = Double.NEGATIVE_INFINITY;

        // tie-breaks
        int bestDT = Integer.MAX_VALUE;
        boolean bestKeep = false;

        // If we forbid reverse but reverse is the only possible move, allow it.
        boolean hasNonReverse = false;
        if (forbidReverse && lastDirection != null) {
            for (int i = 0; i < plans.size(); i++) {
                ArrayList<String> al = plans.getAction(i);
                if (al == null || al.isEmpty()) continue;
                String a = al.get(0);
                // Only count real moves when possible
                if (anyMovingAction && !actionActuallyMoves(plans.getResult(i), state)) continue;
                if (!isOpposite(a, lastDirection)) {
                    hasNonReverse = true;
                    break;
                }
            }
        }

        for (int i = 0; i < plans.size(); i++) {
            ArrayList<String> actionList = plans.getAction(i);
            Result result = plans.getResult(i);
            if (actionList == null || actionList.isEmpty()) continue;

            // Skip wall-actions if we have at least one real moving action available
            if (anyMovingAction && !actionActuallyMoves(result, state)) {
                continue;
            }

            String act = actionList.get(0);

            // panic + ghost behind => disable reverse if there is any other option
            if (forbidReverse && hasNonReverse && lastDirection != null && isOpposite(act, lastDirection)) {
                continue;
            }

            double val = andNode(result, state, depth);

            // Direction inertia & reverse penalty (soft; hard-disable only in panic+behind)
            if (lastDirection != null) {
                if (act.equals(lastDirection)) {
                    val += KEEP_DIR_BONUS;
                } else if (isOpposite(act, lastDirection)) {
                    val -= REVERSE_PENALTY;
                }
            }

            // Tie-break with target distance
            int dT = estimateDistanceToTargetAfterAction(state, act);
            boolean keep = (lastDirection != null && act.equals(lastDirection));

            if (val > bestVal) {
                bestVal = val;
                bestAct = act;
                bestDT = dT;
                bestKeep = keep;
            } else if (val == bestVal) {
                if (dT < bestDT) {
                    bestAct = act;
                    bestDT = dT;
                    bestKeep = keep;
                } else if (dT == bestDT) {
                    if (keep && !bestKeep) {
                        bestAct = act;
                        bestKeep = true;
                    } else if (keep == bestKeep) {
                        if (RNG.nextBoolean()) bestAct = act;
                    }
                }
            }
        }

        return bestAct != null ? bestAct : PacManLauncher.RIGHT;
    }

    private static double andNode(Result result, BeliefState prevState, int depth) {
        if (prevState == null) return DEATH_PENALTY;
        if (result == null || result.size() == 0) return evaluate(prevState);

        if (depth <= 1) {
            // leaf: average alive
            return averageAlive(result, prevState);
        }

        // internal: average alive of recursive OR values
        ArrayList<Double> aliveVals = new ArrayList<>();
        int maxProcess = 12;
        int processed = 0;

        for (int i = 0; i < result.size() && processed < maxProcess; i++) {
            BeliefState s = result.getBeliefState(i);
            if (s == null) continue;
            processed++;

            // death outcome skip
            if (s.getLife() < prevState.getLife()) continue;

            double v = orValue(s, depth - 1);
            aliveVals.add(v);
        }

        if (aliveVals.isEmpty()) return DEATH_PENALTY;
        double sum = 0;
        for (double v : aliveVals) sum += v;
        return sum / aliveVals.size();
    }

    private static double orValue(BeliefState state, int depth) {
        if (state == null || state.getLife() <= 0) return DEATH_PENALTY;
        if (depth <= 0) return evaluate(state);

        Plans plans = state.extendsBeliefState();
        if (plans == null || plans.size() == 0) return evaluate(state);

        double best = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < plans.size(); i++) {
            Result res = plans.getResult(i);
            best = Math.max(best, andNode(res, state, depth));
        }
        return best;
    }

    private static double averageAlive(Result result, BeliefState prevState) {
        ArrayList<Double> aliveVals = new ArrayList<>();

        int maxProcess = 12;
        int processed = 0;

        for (int i = 0; i < result.size() && processed < maxProcess; i++) {
            BeliefState s = result.getBeliefState(i);
            if (s == null) continue;
            processed++;

            if (s.getLife() < prevState.getLife()) continue;
            aliveVals.add(evaluate(s));
        }

        if (aliveVals.isEmpty()) return DEATH_PENALTY;
        double sum = 0;
        for (double v : aliveVals) sum += v;
        return sum / aliveVals.size();
    }

    /* ===================== Evaluation ===================== */

    private static double evaluate(BeliefState state) {
        if (state == null || state.getLife() <= 0) return DEATH_PENALTY;

        Position pac = state.getPacmanPosition();
        if (pac == null) return DEATH_PENALTY;

        double score = 0;

        // Life is huge
        score += state.getLife() * LIFE_WEIGHT;

        // Food gained since root
        int eatenDots = (rootDots >= 0) ? (rootDots - state.getNbrOfGommes()) : 0;
        int eatenSupers = (rootSupers >= 0) ? (rootSupers - state.getNbrOfSuperGommes()) : 0;
        if (eatenDots > 0) score += eatenDots * DOT_REWARD;
        if (eatenSupers > 0) score += eatenSupers * SUPER_REWARD;

        // Current cell bonus (immediate)
        char[][] map = state.getMap();
        if (map != null) {
            char c = map[pac.x][pac.y];
            if (c == '.') score += 1200;
            else if (c == '*') score += 4500;
        }

        // Target shaping
        if (hasTarget()) {
            int dT = bfsDistance(map, pac.x, pac.y, targetX, targetY, 350);
            if (dT >= 9999) dT = manhattan(pac.x, pac.y, targetX, targetY);
            score -= dT * TARGET_DIST_PENALTY;
            if (dT == 0) score += TARGET_REACH_BONUS;
        } else {
            // No target should be rare; push to find one
            score -= 20000;
        }

        // Danger penalty based on min distance to any non-feared, non-forgotten ghost
        int minGhostDist = minPossibleGhostDist(state);
        if (minGhostDist == 0) return DEATH_PENALTY;
        if (minGhostDist == 1) score -= DANGER_D1;
        else if (minGhostDist == 2) score -= DANGER_D2;
        else if (minGhostDist == 3) score -= DANGER_D3;

        // Open directions: prefer junctions when danger is near
        int open = countOpenDirections(state);
        if (minGhostDist <= 3) {
            score += open * 150;
            if (open <= 1) score -= 8000;
        } else {
            score += open * 40;
            if (open <= 1) score -= 400;
        }

        return score;
    }

    /* ===================== Target selection & swap ===================== */

    private static void updateTarget(BeliefState state) {
        if (state == null) return;
        char[][] map = state.getMap();
        Position pac = state.getPacmanPosition();
        if (map == null || pac == null) return;

        // If target eaten / invalid -> clear
        if (hasTarget()) {
            char cell = safeCell(map, targetX, targetY);
            if (cell != '.' && cell != '*') {
                clearTarget();
            }
            if (pac.x == targetX && pac.y == targetY) {
                clearTarget();
            }
        }

        // If looping or no-score too long -> swap target
        boolean looping = isLooping();
        if (noScoreStreak >= NO_SCORE_SWAP_THRESHOLD || looping) {
            // pick an alternative target to break the loop
            int[] alt = pickNearestFood(map, pac.x, pac.y, true);
            if (alt != null) {
                targetX = alt[0];
                targetY = alt[1];
            } else {
                clearTarget();
            }
            // reset streak a bit so we don't constantly retarget every frame
            noScoreStreak = 0;
            // clear history to avoid immediate re-loop detection
            positionHistory.clear();
            return;
        }

        // Always have a target: nearest gum
        if (!hasTarget()) {
            int[] t = pickNearestFood(map, pac.x, pac.y, false);
            if (t != null) {
                targetX = t[0];
                targetY = t[1];
            }
        }
    }

    /**
     * BFS find nearest food ('.' or '*').
     * If wantAlternative=true, try not to return the current target when possible.
     */
    private static int[] pickNearestFood(char[][] map, int sx, int sy, boolean wantAlternative) {
        int H = map.length;
        int W = map[0].length;
        boolean[][] vis = new boolean[H][W];
        ArrayDeque<int[]> q = new ArrayDeque<>();
        q.add(new int[]{sx, sy, 0});
        vis[sx][sy] = true;

        int[] first = null;
        int[] second = null;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int x = cur[0], y = cur[1];

            char c = map[x][y];
            if (c == '.' || c == '*') {
                if (first == null) {
                    first = new int[]{x, y};
                } else {
                    // different from first
                    if (x != first[0] || y != first[1]) {
                        second = new int[]{x, y};
                        break;
                    }
                }
            }

            for (int k = 0; k < 4; k++) {
                int nx = x + DX[k];
                int ny = y + DY[k];
                if (nx < 0 || ny < 0 || nx >= H || ny >= W) continue;
                if (vis[nx][ny]) continue;
                if (map[nx][ny] == '#') continue;
                vis[nx][ny] = true;
                q.add(new int[]{nx, ny, cur[2] + 1});
            }
        }

        if (!wantAlternative) {
            return first;
        }

        // Alternative requested: try not to keep current target
        if (first == null) return null;

        if (!hasTarget()) return first;
        if (first[0] == targetX && first[1] == targetY) {
            return (second != null) ? second : first;
        }
        return first;
    }

    private static boolean hasTarget() {
        return targetX != Integer.MIN_VALUE && targetY != Integer.MIN_VALUE;
    }

    private static void clearTarget() {
        targetX = Integer.MIN_VALUE;
        targetY = Integer.MIN_VALUE;
    }

    /* ===================== Panic / behind logic (kept simple) ===================== */

    private static boolean isPanic(BeliefState state) {
        // Panic = a dangerous ghost could be very close.
        int d = minPossibleGhostDist(state);
        return d <= 2;
    }

    /**
     * "Ghost behind" check (no probability, only positions that are visible OR just recently unseen).
     * Used only to forbid reverse in PANIC.
     */
    private static boolean ghostBehind(BeliefState state) {
        if (state == null) return false;
        if (lastDirection == null) return false;

        Position pac = state.getPacmanPosition();
        if (pac == null) return false;

        String behind = oppositeOf(lastDirection);
        if (behind == null) return false;

        int nG = state.getNbrOfGhost();
        for (int i = 0; i < nG; i++) {
            if (isForgotten(i)) continue;
            if (state.getCompteurPeur(i) > 0) continue; // feared ghosts don't chase

            TreeSet<Position> poss = state.getGhostPositions(i);
            if (poss == null || poss.isEmpty()) continue;

            boolean anyVisible = false;
            for (Position g : poss) {
                if (BeliefState.isVisible(g.x, g.y, pac.x, pac.y)) {
                    anyVisible = true;
                    break;
                }
            }

            // If not visible and also unseen for long, skip
            if (!anyVisible) {
                if (unseenTurns != null && i < unseenTurns.length && unseenTurns[i] > 2) {
                    continue;
                }
            }

            for (Position g : poss) {
                int dx = g.x - pac.x;
                int dy = g.y - pac.y;
                int dist = Math.abs(dx) + Math.abs(dy);
                if (dist == 0 || dist > 3) continue;
                if (isInDirection(behind, dx, dy)) return true;
            }
        }
        return false;
    }

    private static String oppositeOf(String dir) {
        if (dir == null) return null;
        if (dir.equals(PacManLauncher.UP)) return PacManLauncher.DOWN;
        if (dir.equals(PacManLauncher.DOWN)) return PacManLauncher.UP;
        if (dir.equals(PacManLauncher.LEFT)) return PacManLauncher.RIGHT;
        if (dir.equals(PacManLauncher.RIGHT)) return PacManLauncher.LEFT;
        return null;
    }

    private static boolean isInDirection(String dir, int dx, int dy) {
        int adx = Math.abs(dx);
        int ady = Math.abs(dy);
        if (dir.equals(PacManLauncher.UP)) return dx < 0 && adx >= ady;
        if (dir.equals(PacManLauncher.DOWN)) return dx > 0 && adx >= ady;
        if (dir.equals(PacManLauncher.LEFT)) return dy < 0 && ady >= adx;
        if (dir.equals(PacManLauncher.RIGHT)) return dy > 0 && ady >= adx;
        return false;
    }

    /* ===================== Ghost forgetting (MUST KEEP) ===================== */

    private static void updateGhostForgetting(BeliefState state) {
        int nG = 0;
        try { nG = state.getNbrOfGhost(); } catch (Exception ignored) {}

        if (seenEver == null || unseenTurns == null || nGhostMemo != nG) {
            seenEver = new boolean[nG];
            unseenTurns = new int[nG];
            nGhostMemo = nG;
        }

        Position pac = state.getPacmanPosition();
        if (pac == null) return;

        for (int i = 0; i < nG; i++) {
            TreeSet<Position> poss;
            try { poss = state.getGhostPositions(i); } catch (Exception e) { poss = null; }
            if (poss == null || poss.isEmpty()) {
                if (seenEver[i]) {
                    unseenTurns[i]++;
                    if (unseenTurns[i] > FORGET_AFTER_TURNS) {
                        seenEver[i] = false;
                        unseenTurns[i] = 0;
                    }
                }
                continue;
            }

            boolean visible = false;
            for (Position g : poss) {
                if (BeliefState.isVisible(g.x, g.y, pac.x, pac.y)) {
                    visible = true;
                    break;
                }
            }

            if (visible) {
                seenEver[i] = true;
                unseenTurns[i] = 0;
            } else {
                if (seenEver[i]) {
                    unseenTurns[i]++;
                    if (unseenTurns[i] > FORGET_AFTER_TURNS) {
                        seenEver[i] = false;
                        unseenTurns[i] = 0;
                    }
                }
            }
        }
    }

    private static boolean isForgotten(int ghostIdx) {
        if (seenEver == null || unseenTurns == null) return false;
        if (ghostIdx < 0 || ghostIdx >= seenEver.length) return false;
        // If never seen or already forgotten => ignored
        return !seenEver[ghostIdx];
    }

    /* ===================== Loop tracking ===================== */

    private static void recordPosition(Position p) {
        if (p == null) return;
        String key = p.x + "," + p.y;
        positionHistory.addLast(key);
        if (positionHistory.size() > HISTORY_SIZE) positionHistory.removeFirst();
    }

    private static boolean isLooping() {
        if (positionHistory.size() < HISTORY_SIZE / 2) return false;
        HashMap<String, Integer> cnt = new HashMap<>();
        for (String s : positionHistory) {
            cnt.put(s, cnt.getOrDefault(s, 0) + 1);
        }
        for (int v : cnt.values()) {
            if (v >= LOOP_THRESHOLD) return true;
        }
        return false;
    }

    /* ===================== Required helper methods ===================== */

    /** Reset everything (requested by you). */
    private static void resetAll() {
        lastDirection = null;

        clearTarget();

        lastScore = Integer.MIN_VALUE;
        noScoreStreak = 0;

        positionHistory.clear();

        seenEver = null;
        unseenTurns = null;
        nGhostMemo = -1;

        rootDots = -1;
        rootSupers = -1;
    }

    /** Count open directions around Pacman. */
    private static int countOpenDirections(BeliefState state) {
        if (state == null) return 0;
        char[][] map = state.getMap();
        Position p = state.getPacmanPosition();
        if (map == null || p == null) return 0;

        int H = map.length;
        int W = map[0].length;

        int cnt = 0;
        if (isWalkable(map, H, W, p.x - 1, p.y)) cnt++;
        if (isWalkable(map, H, W, p.x + 1, p.y)) cnt++;
        if (isWalkable(map, H, W, p.x, p.y - 1)) cnt++;
        if (isWalkable(map, H, W, p.x, p.y + 1)) cnt++;
        return cnt;
    }

    /** Manhattan distance helper (requested). */
    private static int manhattan(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        if (dx < 0) dx = -dx;
        int dy = y1 - y2;
        if (dy < 0) dy = -dy;
        return dx + dy;
    }

    /* ===================== Other helpers ===================== */

    private static boolean isOpposite(String a, String b) {
        if (a == null || b == null) return false;
        return (a.equals(PacManLauncher.UP) && b.equals(PacManLauncher.DOWN)) ||
               (a.equals(PacManLauncher.DOWN) && b.equals(PacManLauncher.UP)) ||
               (a.equals(PacManLauncher.LEFT) && b.equals(PacManLauncher.RIGHT)) ||
               (a.equals(PacManLauncher.RIGHT) && b.equals(PacManLauncher.LEFT));
    }

    /**
     * Teacher's Plans may contain actions that hit a wall and therefore do not move Pacman.
     * Such actions lead to BeliefState.move(0,0,...) and Pacman appears "not moving".
     *
     * We detect this by checking whether at least one successor state changes Pacman's position.
     */
    private static boolean actionActuallyMoves(Result result, BeliefState current) {
        if (result == null || current == null || result.size() == 0) return false;
        Position p0 = current.getPacmanPosition();
        if (p0 == null) return false;
        for (BeliefState bs : result.getBeliefStates()) {
            if (bs == null) continue;
            Position p1 = bs.getPacmanPosition();
            if (p1 == null) continue;
            if (p1.x != p0.x || p1.y != p0.y) return true;
        }
        return false;
    }

    private static char safeCell(char[][] map, int x, int y) {
        if (map == null) return '#';
        if (x < 0 || y < 0 || x >= map.length || y >= map[0].length) return '#';
        return map[x][y];
    }

    private static boolean isWalkable(char[][] map, int H, int W, int x, int y) {
        if (x < 0 || y < 0 || x >= H || y >= W) return false;
        return map[x][y] != '#';
    }

    private static int bfsDistance(char[][] map, int sx, int sy, int tx, int ty, int maxD) {
        if (map == null) return 9999;
        if (sx == tx && sy == ty) return 0;

        int H = map.length;
        int W = map[0].length;
        boolean[][] vis = new boolean[H][W];
        ArrayDeque<int[]> q = new ArrayDeque<>();
        q.add(new int[]{sx, sy, 0});
        vis[sx][sy] = true;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int x = cur[0], y = cur[1], d = cur[2];
            if (d >= maxD) continue;

            for (int k = 0; k < 4; k++) {
                int nx = x + DX[k];
                int ny = y + DY[k];
                if (nx < 0 || ny < 0 || nx >= H || ny >= W) continue;
                if (vis[nx][ny]) continue;
                if (map[nx][ny] == '#') continue;

                if (nx == tx && ny == ty) return d + 1;

                vis[nx][ny] = true;
                q.add(new int[]{nx, ny, d + 1});
            }
        }
        return 9999;
    }

    private static int estimateDistanceToTargetAfterAction(BeliefState s, String act) {
        if (s == null || !hasTarget()) return 9999;
        Position p = s.getPacmanPosition();
        if (p == null) return 9999;
        int nx = p.x, ny = p.y;
        if (PacManLauncher.UP.equals(act)) nx--;
        else if (PacManLauncher.DOWN.equals(act)) nx++;
        else if (PacManLauncher.LEFT.equals(act)) ny--;
        else if (PacManLauncher.RIGHT.equals(act)) ny++;
        return bfsDistance(s.getMap(), nx, ny, targetX, targetY, 220);
    }

    private static int minPossibleGhostDist(BeliefState state) {
        if (state == null) return 9999;
        Position pac = state.getPacmanPosition();
        if (pac == null) return 9999;

        int minD = 9999;
        int nG = state.getNbrOfGhost();
        for (int i = 0; i < nG; i++) {
            if (isForgotten(i)) continue;
            if (state.getCompteurPeur(i) > 0) continue; // feared

            TreeSet<Position> poss = state.getGhostPositions(i);
            if (poss == null || poss.isEmpty()) continue;

            for (Position g : poss) {
                int d = manhattan(pac.x, pac.y, g.x, g.y);
                if (d < minD) minD = d;
                if (minD == 0) return 0;
            }
        }
        return minD;
    }

    private static final int[] DX = {-1, 1, 0, 0};
    private static final int[] DY = {0, 0, -1, 1};
}











