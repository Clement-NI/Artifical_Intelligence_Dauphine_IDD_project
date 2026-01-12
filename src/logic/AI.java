package logic;

import java.util.*;

import view.Gomme;


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
//public class AI{
//	/**
//	 * function that compute the next action to do (among UP, DOWN, LEFT, RIGHT)
//	 * @param beliefState the current belief-state of the agent
//	 * @param deepth the deepth of the search (size of the largest sequence of action checked)
//	 * @return a string describing the next action (among PacManLauncher.UP/DOWN/LEFT/RIGHT)
//	 */
//	public static String findNextMove(BeliefState beliefState) {
//		return PacManLauncher.RIGHT;
//	}
//}



/**
 * AI implementation for Pac-Man using expectimax search with belief states
 */

/**
 * AI implementation for Pac-Man using expectimax search with belief states
 * Handles partial observability - ghosts are only visible when in line of sight
 */




/**
 * 改进的吃豆人AI - 防止来回摇摆，使用路径规划
 */









/**
 * 吃豆人AI - 简单策略
 * 1. 看到危险鬼 → 躲
 * 2. 没看到鬼 → 去吃最近的豆
 * 3. 附加：禁止访问最近去过的位置（防摆动）
 */



/**
 * 吃豆人AI - 简单策略
 * 1. 看到危险鬼 → 躲
 * 2. 没看到鬼 → 去吃最近的豆
 * 3. 附加：禁止访问最近去过的位置（防摆动）
 */


/**
 * 完整 A* 启发式 AI 实现
 * 逻辑：
 * 1. 风险加权的 A* 算法：寻找最短路径的同时，避开所有幽灵可能出现的位置。
 * 2. 惯性机制：记录 lastAction，减少在空旷地带或墙边的无意义抖动。
 * 3. 视野检测：发现直线距离内无遮挡的幽灵时，强制切换至逃跑模式。
 * 4. 死胡同惩罚：防止被幽灵堵死在狭窄区域。
 */




/**
 * 终极稳定版 A* AI
 * 1. 强制生存优先级：如果鬼在 2 格内，直接忽略豆子进行避险。
 * 2. 强力转向惩罚：大幅增加“掉头”代价，彻底解决左右横跳。
 * 3. 全局风险评估：使用 getNbrOfGhost() 遍历所有可能位置。
 */














/**
 * 终极稳定版 AI：意志锁定 + 历史记忆型 A*
 */



/**
 * 终极防撞版 AI
 * 吸收 Berkeley search.py 的图搜索思维：
 * 1. 将 (x, y, lastAction) 视为状态。
 * 2. 将鬼周围 1 格视为物理障碍。
 * 3. 严格禁止反方向移动。
 */



































/**
 * 吃豆人AI - 基于 A* 算法和智能策略
 * 参考：https://stackoverflow.com/questions/3366648/pacman-character-ai-suggestions-for-optimal-next-direction
 */

/**
 * 吃豆人AI - 风险收益平衡版
 * 核心：不再简单地"有鬼就逃"，而是评估"吃豆的收益是否大于风险"
 */
//public class AI {
//
//    // 当前目标豆子位置
//    private static int targetX = -1;
//    private static int targetY = -1;
//
//    /**
//     * 主入口
//     */
//    public static String findNextMove(BeliefState state) {
//        if (state == null || state.getLife() <= 0) {
//            resetTarget();
//            return PacManLauncher.RIGHT;
//        }
//
//        Position pacman = state.getPacmanPosition();
//        char[][] map = state.getMap();
//
//        // 检查是否有可见的危险鬼
//        boolean hasVisibleGhost = hasVisibleDangerousGhost(state, pacman);
//
//        if (hasVisibleGhost) {
//            // 有鬼！避开鬼同时尽量吃豆
//            resetTarget();  // 清除锁定目标
//            return escapeAndEat(state, pacman, map);
//        } else {
//            // 没鬼，专心吃豆
//            return goToNearestGum(state, pacman, map);
//        }
//    }
//
//    /**
//     * 检查是否有可见的危险鬼（距离<=4且不在害怕状态）
//     */
//    private static boolean hasVisibleDangerousGhost(BeliefState state, Position pacman) {
//        for (int i = 0; i < state.getNbrOfGhost(); i++) {
//            // 跳过害怕状态的鬼
//            if (state.getCompteurPeur(i) > 0) continue;
//
//            TreeSet<Position> ghosts = state.getGhostPositions(i);
//            for (Position ghost : ghosts) {
//                // 检查是否可见
//                if (BeliefState.isVisible(ghost.x, ghost.y, pacman.x, pacman.y)) {
//                    int dist = Math.abs(ghost.x - pacman.x) + Math.abs(ghost.y - pacman.y);
//                    if (dist <= 5) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
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
//                // 豆子已被吃掉，重新找
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
//                // 没有豆子了
//                return PacManLauncher.RIGHT;
//            }
//        }
//
//        // 向目标移动
//        return moveToward(pacman.x, pacman.y, targetX, targetY, map, state, false);
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
//        String bestMove = null;
//        double bestScore = Double.NEGATIVE_INFINITY;
//
//        for (int i = 0; i < 4; i++) {
//            int nx = pacman.x + deltas[i][0];
//            int ny = pacman.y + deltas[i][1];
//
//            // 检查是否可走
//            if (!isValid(nx, ny, map)) continue;
//
//            double score = 0.0;
//
//            // 1. 安全性：离所有可见鬼越远越好
//            double minGhostDist = Double.MAX_VALUE;
//            for (int g = 0; g < state.getNbrOfGhost(); g++) {
//                if (state.getCompteurPeur(g) > 0) continue;
//
//                TreeSet<Position> ghosts = state.getGhostPositions(g);
//                for (Position ghost : ghosts) {
//                    if (BeliefState.isVisible(ghost.x, ghost.y, pacman.x, pacman.y)) {
//                        double dist = Math.abs(ghost.x - nx) + Math.abs(ghost.y - ny);
//                        minGhostDist = Math.min(minGhostDist, dist);
//                    }
//                }
//            }
//
//            if (minGhostDist <= 1) {
//                score -= 10000;  // 太近了，非常危险
//            } else if (minGhostDist <= 2) {
//                score -= 3000;
//            } else {
//                score += minGhostDist * 100;  // 距离越远越好
//            }
//
//            // 2. 吃豆奖励
//            char cell = map[nx][ny];
//            if (cell == '*') {
//                score += 5000;  // 超级豆优先
//            } else if (cell == '.') {
//                score += 2000;  // 普通豆
//            }
//
//            // 3. 接近最近豆子的奖励
//            int[] nearestGum = bfsFindNearestGum(nx, ny, map);
//            if (nearestGum != null) {
//                int distToGum = Math.abs(nearestGum[0] - nx) + Math.abs(nearestGum[1] - ny);
//                score += (20 - Math.min(distToGum, 20)) * 50;
//            }
//
//            // 4. 开放空间奖励（有更多逃跑路线）
//            int openDirs = countOpenDirections(nx, ny, map);
//            score += openDirs * 200;
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
//            // 检查是否是豆子
//            char cell = map[x][y];
//            if (cell == '.' || cell == '*') {
//                return new int[]{x, y};
//            }
//
//            // 扩展邻居
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
//        return null;  // 没找到
//    }
//
//    /**
//     * 向目标位置移动（使用BFS找路径）
//     */
//    private static String moveToward(int fromX, int fromY, int toX, int toY,
//                                      char[][] map, BeliefState state, boolean avoidGhost) {
//        // BFS找到目标的第一步
//        Queue<int[]> queue = new LinkedList<>();
//        Map<String, String> parent = new HashMap<>();  // 记录路径
//        Map<String, String> firstMove = new HashMap<>();  // 记录第一步方向
//
//        String[] directions = {PacManLauncher.UP, PacManLauncher.DOWN,
//                               PacManLauncher.LEFT, PacManLauncher.RIGHT};
//        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//
//        String startKey = fromX + "," + fromY;
//        queue.offer(new int[]{fromX, fromY});
//        parent.put(startKey, null);
//
//        while (!queue.isEmpty()) {
//            int[] curr = queue.poll();
//            int x = curr[0], y = curr[1];
//            String currKey = x + "," + y;
//
//            // 找到目标
//            if (x == toX && y == toY) {
//                return firstMove.get(currKey);
//            }
//
//            // 扩展邻居
//            for (int i = 0; i < 4; i++) {
//                int nx = x + deltas[i][0];
//                int ny = y + deltas[i][1];
//                String nextKey = nx + "," + ny;
//
//                if (isValid(nx, ny, map) && !parent.containsKey(nextKey)) {
//                    parent.put(nextKey, currKey);
//
//                    // 记录第一步方向
//                    if (currKey.equals(startKey)) {
//                        firstMove.put(nextKey, directions[i]);
//                    } else {
//                        firstMove.put(nextKey, firstMove.get(currKey));
//                    }
//
//                    queue.offer(new int[]{nx, ny});
//                }
//            }
//        }
//
//        // 找不到路径，随便走一步
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
//}

public class AI {

    // 当前目标豆子位置
    private static int targetX = -1;
    private static int targetY = -1;

    // 鬼的记忆：记住每个鬼最后看到的位置和剩余记忆时间
    private static Map<Integer, int[]> ghostMemory = new HashMap<>();  // ghostId -> [x, y, remainingTurns]
    private static final int MEMORY_DURATION = 4;  // 记住鬼的位置4步

    /**
     * 主入口
     */
    public static String findNextMove(BeliefState state) {
        if (state == null || state.getLife() <= 0) {
            resetAll();
            return PacManLauncher.RIGHT;
        }

        Position pacman = state.getPacmanPosition();
        char[][] map = state.getMap();

        // 更新鬼的记忆
        updateGhostMemory(state, pacman);

        // 检查是否有危险（可见的鬼 或 记忆中的鬼）
        boolean hasDanger = hasDangerousGhost(state, pacman);

        if (hasDanger) {
            // 有危险！避开鬼同时尽量吃豆
            resetTarget();  // 清除锁定目标
            return escapeAndEat(state, pacman, map);
        } else {
            // 安全，专心吃豆
            return goToNearestGum(state, pacman, map);
        }
    }

    /**
     * 更新鬼的记忆
     */
    private static void updateGhostMemory(BeliefState state, Position pacman) {
        // 1. 减少所有记忆的剩余时间
        Iterator<Map.Entry<Integer, int[]>> it = ghostMemory.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, int[]> entry = it.next();
            int[] mem = entry.getValue();
            mem[2]--;  // 减少剩余时间
            if (mem[2] <= 0) {
                it.remove();  // 记忆过期，删除
            }
        }

        // 2. 更新可见鬼的位置
        for (int i = 0; i < state.getNbrOfGhost(); i++) {
            // 跳过害怕状态的鬼
            if (state.getCompteurPeur(i) > 0) {
                ghostMemory.remove(i);  // 害怕的鬼不用记
                continue;
            }

            TreeSet<Position> ghosts = state.getGhostPositions(i);
            for (Position ghost : ghosts) {
                // 如果这个鬼可见，更新记忆
                if (BeliefState.isVisible(ghost.x, ghost.y, pacman.x, pacman.y)) {
                    ghostMemory.put(i, new int[]{ghost.x, ghost.y, MEMORY_DURATION});
                }
            }
        }
    }

    /**
     * 检查是否有危险的鬼（可见的 或 记忆中距离近的）
     */
    private static boolean hasDangerousGhost(BeliefState state, Position pacman) {
        // 1. 检查可见的鬼
        for (int i = 0; i < state.getNbrOfGhost(); i++) {
            if (state.getCompteurPeur(i) > 0) continue;

            TreeSet<Position> ghosts = state.getGhostPositions(i);
            for (Position ghost : ghosts) {
                if (BeliefState.isVisible(ghost.x, ghost.y, pacman.x, pacman.y)) {
                    int dist = Math.abs(ghost.x - pacman.x) + Math.abs(ghost.y - pacman.y);
                    if (dist <= 5) {
                        return true;
                    }
                }
            }
        }

        // 2. 检查记忆中的鬼
        for (int[] mem : ghostMemory.values()) {
            int dist = Math.abs(mem[0] - pacman.x) + Math.abs(mem[1] - pacman.y);
            if (dist <= 4) {  // 记忆中的鬼，距离阈值稍小
                return true;
            }
        }

        return false;
    }

    /**
     * 获取所有危险位置（可见鬼 + 记忆中的鬼）
     */
    private static List<int[]> getAllDangerPositions(BeliefState state, Position pacman) {
        List<int[]> dangers = new ArrayList<>();

        // 1. 可见的鬼
        for (int i = 0; i < state.getNbrOfGhost(); i++) {
            if (state.getCompteurPeur(i) > 0) continue;

            TreeSet<Position> ghosts = state.getGhostPositions(i);
            for (Position ghost : ghosts) {
                if (BeliefState.isVisible(ghost.x, ghost.y, pacman.x, pacman.y)) {
                    dangers.add(new int[]{ghost.x, ghost.y});
                }
            }
        }

        // 2. 记忆中的鬼（不可见但还在记忆中）
        for (Map.Entry<Integer, int[]> entry : ghostMemory.entrySet()) {
            int[] mem = entry.getValue();
            // 检查这个位置是否已经在dangers中
            boolean exists = false;
            for (int[] d : dangers) {
                if (d[0] == mem[0] && d[1] == mem[1]) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                dangers.add(new int[]{mem[0], mem[1]});
            }
        }

        return dangers;
    }

    /**
     * 没有鬼时：BFS找最近豆子，锁定目标直到吃到
     */
    private static String goToNearestGum(BeliefState state, Position pacman, char[][] map) {
        // 检查目标是否还有效（还有豆子）
        if (targetX >= 0 && targetY >= 0) {
            char cell = map[targetX][targetY];
            if (cell != '.' && cell != '*') {
                resetTarget();
            }
        }

        // 如果没有目标，BFS找最近的豆子
        if (targetX < 0 || targetY < 0) {
            int[] nearest = bfsFindNearestGum(pacman.x, pacman.y, map);
            if (nearest != null) {
                targetX = nearest[0];
                targetY = nearest[1];
            } else {
                return PacManLauncher.RIGHT;
            }
        }

        // 向目标移动
        return moveToward(pacman.x, pacman.y, targetX, targetY, map);
    }

    /**
     * 有鬼时：避开鬼，同时尽量接近豆子
     */
    private static String escapeAndEat(BeliefState state, Position pacman, char[][] map) {
        String[] directions = {PacManLauncher.UP, PacManLauncher.DOWN,
                               PacManLauncher.LEFT, PacManLauncher.RIGHT};
        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        // 获取所有危险位置
        List<int[]> dangers = getAllDangerPositions(state, pacman);

        String bestMove = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < 4; i++) {
            int nx = pacman.x + deltas[i][0];
            int ny = pacman.y + deltas[i][1];

            if (!isValid(nx, ny, map)) continue;

            double score = 0.0;

            // 1. 安全性：离所有危险位置越远越好
            double minGhostDist = Double.MAX_VALUE;
            for (int[] danger : dangers) {
                double dist = Math.abs(danger[0] - nx) + Math.abs(danger[1] - ny);
                minGhostDist = Math.min(minGhostDist, dist);
            }

            if (minGhostDist <= 1) {
                score -= 10000;  // 太近了，非常危险
            } else if (minGhostDist <= 2) {
                score -= 3000;
            } else {
                score += minGhostDist * 100;
            }

            // 2. 吃豆奖励
            char cell = map[nx][ny];
            if (cell == '*') {
                score += 5000;
            } else if (cell == '.') {
                score += 2000;
            }

            // 3. 接近最近豆子的奖励
            int[] nearestGum = bfsFindNearestGum(nx, ny, map);
            if (nearestGum != null) {
                int distToGum = Math.abs(nearestGum[0] - nx) + Math.abs(nearestGum[1] - ny);
                score += (20 - Math.min(distToGum, 20)) * 50;
            }

            // 4. 开放空间奖励
            int openDirs = countOpenDirections(nx, ny, map);
            score += openDirs * 200;

            // 5. 避免走向拐角（鬼可能就在后面）
            if (openDirs <= 1) {
                score -= 500;  // 死胡同惩罚
            }

            if (score > bestScore) {
                bestScore = score;
                bestMove = directions[i];
            }
        }

        return bestMove != null ? bestMove : PacManLauncher.RIGHT;
    }

    /**
     * BFS找最近的豆子
     */
    private static int[] bfsFindNearestGum(int startX, int startY, char[][] map) {
        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(new int[]{startX, startY});
        visited.add(startX + "," + startY);

        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int x = curr[0], y = curr[1];

            char cell = map[x][y];
            if (cell == '.' || cell == '*') {
                return new int[]{x, y};
            }

            for (int[] delta : deltas) {
                int nx = x + delta[0];
                int ny = y + delta[1];
                String key = nx + "," + ny;

                if (isValid(nx, ny, map) && !visited.contains(key)) {
                    visited.add(key);
                    queue.offer(new int[]{nx, ny});
                }
            }
        }

        return null;
    }

    /**
     * 向目标位置移动（BFS找最短路径）
     */
    private static String moveToward(int fromX, int fromY, int toX, int toY, char[][] map) {
        Queue<int[]> queue = new LinkedList<>();
        Map<String, String> firstMove = new HashMap<>();

        String[] directions = {PacManLauncher.UP, PacManLauncher.DOWN,
                               PacManLauncher.LEFT, PacManLauncher.RIGHT};
        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        String startKey = fromX + "," + fromY;
        queue.offer(new int[]{fromX, fromY});
        firstMove.put(startKey, null);

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int x = curr[0], y = curr[1];
            String currKey = x + "," + y;

            if (x == toX && y == toY) {
                return firstMove.get(currKey);
            }

            for (int i = 0; i < 4; i++) {
                int nx = x + deltas[i][0];
                int ny = y + deltas[i][1];
                String nextKey = nx + "," + ny;

                if (isValid(nx, ny, map) && !firstMove.containsKey(nextKey)) {
                    if (currKey.equals(startKey)) {
                        firstMove.put(nextKey, directions[i]);
                    } else {
                        firstMove.put(nextKey, firstMove.get(currKey));
                    }
                    queue.offer(new int[]{nx, ny});
                }
            }
        }

        // 找不到路径
        for (int i = 0; i < 4; i++) {
            int nx = fromX + deltas[i][0];
            int ny = fromY + deltas[i][1];
            if (isValid(nx, ny, map)) {
                return directions[i];
            }
        }

        return PacManLauncher.RIGHT;
    }

    /**
     * 计算开放方向数
     */
    private static int countOpenDirections(int x, int y, char[][] map) {
        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int count = 0;
        for (int[] delta : deltas) {
            if (isValid(x + delta[0], y + delta[1], map)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 检查位置是否有效
     */
    private static boolean isValid(int x, int y, char[][] map) {
        return x >= 0 && x < map.length &&
               y >= 0 && y < map[0].length &&
               map[x][y] != '#';
    }

    /**
     * 重置目标
     */
    private static void resetTarget() {
        targetX = -1;
        targetY = -1;
    }

    /**
     * 完全重置
     */
    private static void resetAll() {
        resetTarget();
        ghostMemory.clear();
    }
}









