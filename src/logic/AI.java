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
//
//public class AI {
//
//    private static final int MAX_DEPTH = 2;
//
//    // 记录初始状态用于计算吃了多少豆
//    private static int initialGommes = -1;
//    private static int initialSuperGommes = -1;
//
//    // 方向惯性
//    private static String lastDirection = null;
//
//    /**
//     * 入口
//     */
//    public static String findNextMove(BeliefState state) {
//        if (state == null || state.getLife() <= 0) {
//            lastDirection = null;
//            return PacManLauncher.RIGHT;
//        }
//
//        // 记录初始豆子数
//        initialGommes = state.getNbrOfGommes();
//        initialSuperGommes = state.getNbrOfSuperGommes();
//
//        // OR节点：遍历所有动作，选最大值
//        return orNode(state, MAX_DEPTH);
//    }
//
//    /**
//     * OR节点：选最佳动作
//     */
//    private static String orNode(BeliefState state, int depth) {
//        Plans plans = state.extendsBeliefState();
//
//        if (plans.size() == 0) {
//            return PacManLauncher.RIGHT;
//        }
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
//            double value = andNode(result, state, depth);
//
//            // 方向惯性：保持方向加分，掉头扣分
//            if (depth == MAX_DEPTH) {
//                if (action.equals(lastDirection)) {
//                    value += 200;
//                }
//                if (isOpposite(action, lastDirection)) {
//                    value -= 500;
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
//        if (bestAction != null && depth == MAX_DEPTH) {
//            lastDirection = bestAction;
//        }
//
//        return bestAction;
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
//     * OR节点（递归版本）
//     */
//    private static double orNodeValue(BeliefState state, int depth) {
//        if (depth <= 0) {
//            return evaluate(state);
//        }
//
//        Plans plans = state.extendsBeliefState();
//        if (plans.size() == 0) {
//            return evaluate(state);
//        }
//
//        double bestValue = Double.NEGATIVE_INFINITY;
//        for (int i = 0; i < plans.size(); i++) {
//            Result result = plans.getResult(i);
//            double value = andNode(result, state, depth);
//            bestValue = Math.max(bestValue, value);
//        }
//        return bestValue;
//    }
//
//    /**
//     * AND节点：跳过死亡，取存活平均
//     */
//    private static double andNode(Result result, BeliefState prevState, int depth) {
//        if (result == null || result.size() == 0) {
//            return evaluate(prevState);
//        }
//
//        ArrayList<Double> aliveValues = new ArrayList<>();
//        int maxToProcess = 10;
//        int processed = 0;
//
//        for (int i = 0; i < result.size(); i++) {
//            BeliefState nextState = result.getBeliefState(i);
//            if (nextState == null) continue;
//            processed++;
//
//            // 死亡：跳过
//            if (nextState.getLife() < prevState.getLife()) {
//                continue;
//            }
//
//            double value;
//            if (depth <= 1) {
//                value = evaluate(nextState);
//            } else {
//                value = orNodeValue(nextState, depth - 1);
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
//     * 评估函数
//     *
//     * score = food_gain - λ * risk
//     */
//    private static double evaluate(BeliefState state) {
//        if (state == null) return 0;
//
//        Position pacman = state.getPacmanPosition();
//        char[][] map = state.getMap();
//
//        // === 生命 ===
//        double lifeScore = state.getLife() * 1000000;
//
//        // === 风险（用改进的max+指数衰减）===
//        double risk = -evaluateGhostDanger(state, pacman);  // 正数
//
//        // === 吃豆收益 ===
//        double foodGain = 0;
//
//        // 吃掉的豆子
//        int eatenGommes = initialGommes - state.getNbrOfGommes();
//        foodGain += eatenGommes * 500;
//
//        int eatenSuperGommes = initialSuperGommes - state.getNbrOfSuperGommes();
//        foodGain += eatenSuperGommes * 3000;
//
//        // 最近豆子距离（主要奖励）
//        int distToGum = state.distanceMinToGum();
//        if (distToGum < Integer.MAX_VALUE) {
//            foodGain -= distToGum * 50;
//        }
//
//        // 当前位置有豆子
//        char cell = map[pacman.x][pacman.y];
//        if (cell == '.') foodGain += 1000;
//        else if (cell == '*') foodGain += 2000;
//
//        // === 路口（只在高风险时重要）===
//        int openDirs = countOpenDirections(pacman.x, pacman.y, map);
//        double openScore;
//        if (risk > 3000) {
//            // 高风险：路口很重要
//            switch (openDirs) {
//                case 4: openScore = 1000; break;
//                case 3: openScore = 500; break;
//                case 2: openScore = 0; break;
//                default: openScore = -2000; break;
//            }
//        }
//        else {
//            // 低风险：路口不太重要
//            switch (openDirs) {
//                case 4: openScore = 500; break;
//                case 3: openScore = 200; break;
//                case 2: openScore = 0; break;
//                default: openScore = -300; break;
//            }
//        }
//
//        // 最终：life + foodGain + openScore - risk
//        return lifeScore + foodGain + openScore - risk;
//    }
//
//    /**
//     * 评估鬼的威胁 - 改进版
//     *
//     * 1. 用 MAX 而不是 SUM（防止多预测点惩罚爆炸）
//     * 2. 指数衰减（远距离快速归零）
//     * 3. 截断（超过距离R直接忽略）
//     */
//    private static double evaluateGhostDanger(BeliefState state, Position pacman) {
//        double maxDanger = 0;  // 取最大值，不累加！
//        final int R = 5;       // 截断半径：超过5格不考虑
//
//        for (int i = 0; i < state.getNbrOfGhost(); i++) {
//            if (state.getCompteurPeur(i) > 0) continue;
//
//            TreeSet<Position> ghostPositions = state.getGhostPositions(i);
//
//            for (Position ghost : ghostPositions) {
//                int dist = Math.abs(ghost.x - pacman.x) + Math.abs(ghost.y - pacman.y);
//
//                // 截断：超过R格直接忽略
//                if (dist > R) continue;
//
//                boolean visible = BeliefState.isVisible(ghost.x, ghost.y, pacman.x, pacman.y);
//
//                // 指数衰减：exp(-k*d)，k=1.5
//                double danger;
//                if (visible) {
//                    // 可见：高威胁
//                    danger = 10000 * Math.exp(-1.5 * dist);
//                } else {
//                    // 不可见：低威胁
//                    danger = 3000 * Math.exp(-1.5 * dist);
//                }
//
//                // 取最大值
//                maxDanger = Math.max(maxDanger, danger);
//            }
//        }
//
//        return -maxDanger;  // 返回负数作为惩罚
//    }
//
//    /**
//     * 计算开放方向数量
//     */
//    private static int countOpenDirections(int x, int y, char[][] map) {
//        int count = 0;
//        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//
//        for (int[] d : deltas) {
//            int nx = x + d[0];
//            int ny = y + d[1];
//            if (nx >= 0 && nx < map.length && ny >= 0 && ny < map[0].length
//                && map[nx][ny] != '#') {
//                count++;
//            }
//        }
//        return count;
//    }
//}









/**
 * AND-OR Search AI for Pacman (compatible with your BeliefState API).
 *
 * OR node : Pacman chooses an action (max).
 * AND node: all possible outcomes due to ghosts uncertainty (min / worst-case).
 *
 * Only edits AI.java.
 */

/* ===== Helper containers expected by BeliefState.extendsBeliefState() ===== */


/* =============================== AI =============================== */









//public class AI {
//
//    private static final int MAX_DEPTH = 3;
//
//    // 记录初始状态
//    private static int initialGommes = -1;
//    private static int initialSuperGommes = -1;
//
//    // 绕圈检测
//    private static LinkedList<String> positionHistory = new LinkedList<>();
//    private static final int HISTORY_SIZE = 55;
//    private static int lastScore = 0;
//    private static int noScoreCount = 0;
//
//    // 方向惯性
//    private static String lastDirection = null;
//
//    /**
//     * 主入口
//     */
//    public static String findNextMove(BeliefState state) {
//        if (state == null || state.getLife() <= 0) {
//            resetState();
//            return PacManLauncher.RIGHT;
//        }
//
//        // =================================================================
//        // 【逻辑 1】无敌状态（Buff）：激进吃鬼吃豆
//        // =================================================================
//        if (isBuffActive(state)) {
//            String action = getNearestTargetMove(state);
//            if (action != null) {
//                lastDirection = action;
//            }
//            return action != null ? action : PacManLauncher.RIGHT;
//        }
//
//        // --- 常规状态更新 ---
//        Position pacman = state.getPacmanPosition();
//        String currentPos = pacman.x + "," + pacman.y;
//
//        positionHistory.addLast(currentPos);
//        if (positionHistory.size() > HISTORY_SIZE) {
//            positionHistory.removeFirst();
//        }
//
//        int currentScore = state.getScore();
//        if (currentScore > lastScore) {
//            noScoreCount = 0;
//        } else {
//            noScoreCount++;
//        }
//        lastScore = currentScore;
//
//        initialGommes = state.getNbrOfGommes();
//        initialSuperGommes = state.getNbrOfSuperGommes();
//
//        // =================================================================
//        // 【逻辑 2】常规模式：AND-OR 树决策
//        // =================================================================
//        String bestAction = orNode(state, MAX_DEPTH);
//
//        if (bestAction != null) {
//            lastDirection = bestAction;
//        }
//
//        return bestAction != null ? bestAction : PacManLauncher.RIGHT;
//    }
//
//    // ==================================================
//    // ===      无敌模式下的激进寻路逻辑 (BFS)        ===
//    // ==================================================
//
//    private static boolean isBuffActive(BeliefState state) {
//        for (int i = 0; i < state.getNbrOfGhost(); i++) {
//            if (state.getCompteurPeur(i) < 5) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private static String getNearestTargetMove(BeliefState state) {
//        Position pacman = state.getPacmanPosition();
//        char[][] map = state.getMap();
//        int w = map.length;
//        int h = map[0].length;
//
//        Queue<Node> queue = new LinkedList<>();
//        boolean[][] visited = new boolean[w][h];
//
//        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//        String[] dirNames = {PacManLauncher.UP, PacManLauncher.DOWN, PacManLauncher.LEFT, PacManLauncher.RIGHT};
//
//        int oppositeDirIdx = -1;
//        if (lastDirection != null) {
//            if (lastDirection.equals(PacManLauncher.UP)) oppositeDirIdx = 1;
//            else if (lastDirection.equals(PacManLauncher.DOWN)) oppositeDirIdx = 0;
//            else if (lastDirection.equals(PacManLauncher.LEFT)) oppositeDirIdx = 3;
//            else if (lastDirection.equals(PacManLauncher.RIGHT)) oppositeDirIdx = 2;
//        }
//
//        List<Integer> validStartingIndices = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            int nx = pacman.x + dirs[i][0];
//            int ny = pacman.y + dirs[i][1];
//            if (nx >= 0 && nx < w && ny >= 0 && ny < h && map[nx][ny] != '#') {
//                validStartingIndices.add(i);
//            }
//        }
//
//        if (validStartingIndices.size() > 1 && oppositeDirIdx != -1) {
//            validStartingIndices.remove(Integer.valueOf(oppositeDirIdx));
//        }
//
//        visited[pacman.x][pacman.y] = true;
//        for (int i : validStartingIndices) {
//            int nx = pacman.x + dirs[i][0];
//            int ny = pacman.y + dirs[i][1];
//            queue.add(new Node(nx, ny, i));
//            visited[nx][ny] = true;
//        }
//
//        while (!queue.isEmpty()) {
//            Node curr = queue.poll();
//            if (isEdibleGhost(state, curr.x, curr.y)) return dirNames[curr.firstDirIdx];
//            if (map[curr.x][curr.y] == '.' || map[curr.x][curr.y] == '*') return dirNames[curr.firstDirIdx];
//
//            for (int[] d : dirs) {
//                int nx = curr.x + d[0];
//                int ny = curr.y + d[1];
//                if (nx >= 0 && nx < w && ny >= 0 && ny < h && map[nx][ny] != '#' && !visited[nx][ny]) {
//                    visited[nx][ny] = true;
//                    queue.add(new Node(nx, ny, curr.firstDirIdx));
//                }
//            }
//        }
//
//        if (!validStartingIndices.isEmpty()) {
//            if (lastDirection != null) {
//                int lastDirIdx = -1;
//                if (lastDirection.equals(PacManLauncher.UP)) lastDirIdx = 0;
//                else if (lastDirection.equals(PacManLauncher.DOWN)) lastDirIdx = 1;
//                else if (lastDirection.equals(PacManLauncher.LEFT)) lastDirIdx = 2;
//                else if (lastDirection.equals(PacManLauncher.RIGHT)) lastDirIdx = 3;
//
//                if (validStartingIndices.contains(lastDirIdx)) {
//                    return lastDirection;
//                }
//            }
//            return dirNames[validStartingIndices.get(0)];
//        }
//        return PacManLauncher.RIGHT;
//    }
//
//    private static boolean isEdibleGhost(BeliefState state, int x, int y) {
//        for (int i = 0; i < state.getNbrOfGhost(); i++) {
//            if (state.getCompteurPeur(i) > 0) {
//                for (Position p : state.getGhostPositions(i)) {
//                    if (p.x == x && p.y == y) return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    static class Node {
//        int x, y;
//        int firstDirIdx;
//        public Node(int x, int y, int idx) {
//            this.x = x; this.y = y; this.firstDirIdx = idx;
//        }
//    }
//
//    // ==================================================
//    // ===       常规 AND-OR 逻辑 (核心修改区域)      ===
//    // ==================================================
//
//    private static int getLoopPenalty(String nextPos) {
//        int visitCount = 0;
//        for (String pos : positionHistory) {
//            if (pos.equals(nextPos)) visitCount++;
//        }
//        if (visitCount >= 4) return 500 * visitCount;
//        else if (visitCount >= 2) return 200 * visitCount;
//        return 0;
//    }
//
//    private static void resetState() {
//        positionHistory.clear();
//        lastScore = 0;
//        noScoreCount = 0;
//        initialGommes = -1;
//        initialSuperGommes = -1;
//        lastDirection = null;
//    }
//
//    // 获取最近鬼的距离（辅助方法）
//    private static int getMinGhostDist(BeliefState state) {
//        Position pacman = state.getPacmanPosition();
//        int minGhostDist = Integer.MAX_VALUE;
//        for (int i = 0; i < state.getNbrOfGhost(); i++) {
//            if (state.getCompteurPeur(i) > 0) continue;
//            for (Position p : state.getGhostPositions(i)) {
//                int d = Math.abs(p.x - pacman.x) + Math.abs(p.y - pacman.y);
//                if (d < minGhostDist) minGhostDist = d;
//            }
//        }
//        return minGhostDist;
//    }
//
//    private static String orNode(BeliefState state, int depth) {
//        Plans plans = state.extendsBeliefState();
//        if (plans.size() == 0) return PacManLauncher.RIGHT;
//
//        Position pacman = state.getPacmanPosition();
//        String bestAction = null;
//        double bestValue = Double.NEGATIVE_INFINITY;
//
//        String fallbackAction = null;
//        double fallbackValue = Double.NEGATIVE_INFINITY;
//
//        // --- 【修改点 1】检测是否被鬼紧随 ---
//        // 在决策层判断危险程度
//        int minGhostDist = getMinGhostDist(state);
//        boolean isChased = minGhostDist < 4; // 距离小于4视为被紧随
//
//        for (int i = 0; i < plans.size(); i++) {
//            ArrayList<String> actionList = plans.getAction(i);
//            Result result = plans.getResult(i);
//            if (actionList.isEmpty()) continue;
//
//            String action = actionList.get(0);
//            if (fallbackAction == null) fallbackAction = action;
//
//            double value = andNode(result, state, depth);
//
//            if (value > fallbackValue) {
//                fallbackValue = value;
//                fallbackAction = action;
//            }
//
//            if (depth == MAX_DEPTH) {
//                int[] nextPos = getNextPosition(pacman, action);
//                String nextPosStr = nextPos[0] + "," + nextPos[1];
//
//                value -= getLoopPenalty(nextPosStr);
//
//                if (noScoreCount > 5) {
//                    value -= getLoopPenalty(nextPosStr) * (noScoreCount / 5);
//                }
//
//                if (action.equals(lastDirection)) value += 100;
//
//                // --- 【修改点 2】动态回头惩罚 ---
//                if (isOpposite(action, lastDirection)) {
//                    if (isChased) {
//                        value -= 10000; // 被追时，回头就是找死，给予极刑
//                    } else {
//                        value -= 200;   // 平时，稍微惩罚一下回头
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
//        if (bestAction == null) bestAction = fallbackAction;
//        return bestAction != null ? bestAction : PacManLauncher.RIGHT;
//    }
//
//    private static boolean isOpposite(String d1, String d2) {
//        if (d1 == null || d2 == null) return false;
//        return (d1.equals(PacManLauncher.UP) && d2.equals(PacManLauncher.DOWN)) ||
//               (d1.equals(PacManLauncher.DOWN) && d2.equals(PacManLauncher.UP)) ||
//               (d1.equals(PacManLauncher.LEFT) && d2.equals(PacManLauncher.RIGHT)) ||
//               (d1.equals(PacManLauncher.RIGHT) && d2.equals(PacManLauncher.LEFT));
//    }
//
//    private static int[] getNextPosition(Position pacman, String action) {
//        int nx = pacman.x, ny = pacman.y;
//        if (action.equals(PacManLauncher.UP)) nx--;
//        else if (action.equals(PacManLauncher.DOWN)) nx++;
//        else if (action.equals(PacManLauncher.LEFT)) ny--;
//        else if (action.equals(PacManLauncher.RIGHT)) ny++;
//        return new int[]{nx, ny};
//    }
//
//    private static double orNodeValue(BeliefState state, int depth) {
//        if (depth <= 0) return evaluate(state);
//        Plans plans = state.extendsBeliefState();
//        if (plans.size() == 0) return evaluate(state);
//
//        double bestValue = Double.NEGATIVE_INFINITY;
//        for (int i = 0; i < plans.size(); i++) {
//            Result result = plans.getResult(i);
//            bestValue = Math.max(bestValue, andNode(result, state, depth));
//        }
//        return bestValue;
//    }
//
//    private static double andNode(Result result, BeliefState prevState, int depth) {
//        if (result == null || result.size() == 0) return evaluate(prevState);
//
//        ArrayList<Double> aliveValues = new ArrayList<>();
//        int processed = 0;
//        int maxToProcess = 10;
//
//        for (int i = 0; i < result.size() && processed < maxToProcess; i++) {
//            BeliefState nextState = result.getBeliefState(i);
//            if (nextState == null) continue;
//            processed++;
//
//            if (nextState.getLife() < prevState.getLife()) continue;
//
//            double value;
//            if (depth <= 1) value = evaluate(nextState);
//            else value = orNodeValue(nextState, depth - 1);
//            aliveValues.add(value);
//        }
//
//        if (aliveValues.isEmpty()) return -1000000;
//
//        double sum = 0;
//        for (double v : aliveValues) sum += v;
//        return sum / aliveValues.size();
//    }
//
//    private static double evaluate(BeliefState state) {
//        if (state == null) return 0;
//        double score = 0;
//        Position pacman = state.getPacmanPosition();
//        char[][] map = state.getMap();
//
//        int eatenGommes = initialGommes - state.getNbrOfGommes();
//        score += eatenGommes * 500;
//
//        int eatenSuperGommes = initialSuperGommes - state.getNbrOfSuperGommes();
//        score += eatenSuperGommes * 5000;
//
//        int distToGum = state.distanceMinToGum();
//        if (distToGum < Integer.MAX_VALUE) score -= distToGum * 50;
//
//        // 计算鬼的距离
//        int minGhostDist = getMinGhostDist(state);
//        boolean isChased = minGhostDist < 3; // 被紧随状态
//
//        char currentCell = map[pacman.x][pacman.y];
//        if (currentCell == '.') {
//            // --- 【修改点 3】被紧随时的吃豆激励 ---
//            if (isChased) {
//                score += 1000; // 被追时，吃豆=移动=活命，加分！
//            } else {
//                score += 300;  // 平时分
//            }
//        } else if (currentCell == '*') {
//            // --- 【修改点 4】被紧随时的吃大豆激励 ---
//            if (isChased) {
//                score += 20000; // 救命稻草，给予巨额奖励，甚至高于生命值判断
//            } else {
//                score += 2000;  // 平时分
//            }
//        }
//
//        // 鬼的斥力场
//        if(minGhostDist <= 1){
//            score -= 10000;
//        }
//        if (minGhostDist < 8) {
//            score += minGhostDist * 300;
//        }
//
//        return score;
//    }
//}





























//public class AI {
//
//    private static final int MAX_DEPTH = 5;
//
//    // 记录初始状态用于计算吃了多少豆
//    private static int initialGommes = -1;
//    private static int initialSuperGommes = -1;
//
//    // 绕圈检测
//    private static LinkedList<String> positionHistory = new LinkedList<>();
//    private static final int HISTORY_SIZE = 55;
//    private static int lastScore = 0;
//    private static int noScoreCount = 0;
//
//    // 方向惯性
//    private static String lastDirection = null;
//
//    /**
//     * 入口：OR节点（选最佳动作）
//     */
//    public static String findNextMove(BeliefState state) {
//        if (state == null || state.getLife() <= 0) {
//            resetState();
//            return PacManLauncher.RIGHT;
//        }
//
//        Position pacman = state.getPacmanPosition();
//        String currentPos = pacman.x + "," + pacman.y;
//
//        // 记录位置历史
//        positionHistory.addLast(currentPos);
//        if (positionHistory.size() > HISTORY_SIZE) {
//            positionHistory.removeFirst();
//        }
//
//        // 检测是否得分
//        int currentScore = state.getScore();
//        if (currentScore > lastScore) {
//            noScoreCount = 0;
//        } else {
//            noScoreCount++;
//        }
//        lastScore = currentScore;
//
//        // 记录初始豆子数
//        initialGommes = state.getNbrOfGommes();
//        initialSuperGommes = state.getNbrOfSuperGommes();
//
//        // OR节点：遍历所有动作，选最大值
//        return orNode(state, MAX_DEPTH);
//    }
//
//    /**
//     * 检测是否在绕圈 - 降低惩罚
//     */
//    private static int getLoopPenalty(String nextPos) {
//        int visitCount = 0;
//        for (String pos : positionHistory) {
//            if (pos.equals(nextPos)) {
//                visitCount++;
//            }
//        }
//
//        // 降低惩罚，避免卡住
//        if (visitCount >= 4) {
//            return 500 * visitCount;
//        } else if (visitCount >= 2) {
//            return 200 * visitCount;
//        }
//        return 0;
//    }
//
//    /**
//     * 重置状态
//     */
//    private static void resetState() {
//        positionHistory.clear();
//        lastScore = 0;
//        noScoreCount = 0;
//        initialGommes = -1;
//        initialSuperGommes = -1;
//        lastDirection = null;
//    }
//
//    /**
//     * OR节点：列举所有可行动作，选择分数最高的
//     */
//    private static String orNode(BeliefState state, int depth) {
//        Plans plans = state.extendsBeliefState();
//
//        if (plans.size() == 0) {
//            return PacManLauncher.RIGHT;
//        }
//
//        Position pacman = state.getPacmanPosition();
//        String bestAction = null;
//        double bestValue = Double.NEGATIVE_INFINITY;
//
//        // 备选：即使全是危险，也选一个最不危险的
//        String fallbackAction = null;
//        double fallbackValue = Double.NEGATIVE_INFINITY;
//
//        for (int i = 0; i < plans.size(); i++) {
//            ArrayList<String> actionList = plans.getAction(i);
//            Result result = plans.getResult(i);
//
//            if (actionList.isEmpty()) continue;
//
//            String action = actionList.get(0);
//
//            // 记录第一个可用动作作为fallback
//            if (fallbackAction == null) {
//                fallbackAction = action;
//            }
//
//            // AND节点：评估这个动作的所有可能结果
//            double value = andNode(result, state, depth);
//
//            // 更新fallback（选最大的，即使是负数）
//            if (value > fallbackValue) {
//                fallbackValue = value;
//                fallbackAction = action;
//            }
//
//            // 只在顶层OR节点应用额外调整
//            if (depth == MAX_DEPTH) {
//                int[] nextPos = getNextPosition(pacman, action);
//                String nextPosStr = nextPos[0] + "," + nextPos[1];
//
//                // 绕圈惩罚
//                value -= getLoopPenalty(nextPosStr);
//
//                // 长时间没得分，额外惩罚回访
//                if (noScoreCount > 5) {
//                    value -= getLoopPenalty(nextPosStr) * (noScoreCount / 5);
//                }
//
//                // 方向惯性：鼓励保持方向（防止卡住）
//                if (action.equals(lastDirection)) {
//                    value += 100;
//                }
//
//                // 避免掉头
//                if (isOpposite(action, lastDirection)) {
//                    value -= 200;
//                }
//            }
//
//            if (value > bestValue) {
//                bestValue = value;
//                bestAction = action;
//            }
//        }
//
//        // 如果没找到好的动作，用fallback
//        if (bestAction == null) {
//            bestAction = fallbackAction;
//        }
//
//        // 记录这次的方向
//        if (bestAction != null) {
//            lastDirection = bestAction;
//        }
//
//        return bestAction != null ? bestAction : PacManLauncher.RIGHT;
//    }
//
//    /**
//     * 判断两个方向是否相反
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
//     * 获取执行动作后的位置
//     */
//    private static int[] getNextPosition(Position pacman, String action) {
//        int nx = pacman.x, ny = pacman.y;
//        if (action.equals(PacManLauncher.UP)) nx--;
//        else if (action.equals(PacManLauncher.DOWN)) nx++;
//        else if (action.equals(PacManLauncher.LEFT)) ny--;
//        else if (action.equals(PacManLauncher.RIGHT)) ny++;
//        return new int[]{nx, ny};
//    }
//
//    /**
//     * OR节点（递归版本，返回值）
//     */
//    private static double orNodeValue(BeliefState state, int depth) {
//        if (depth <= 0) {
//            return evaluate(state);
//        }
//
//        Plans plans = state.extendsBeliefState();
//
//        if (plans.size() == 0) {
//            return evaluate(state);
//        }
//
//        double bestValue = Double.NEGATIVE_INFINITY;
//
//        for (int i = 0; i < plans.size(); i++) {
//            Result result = plans.getResult(i);
//            double value = andNode(result, state, depth);
//            bestValue = Math.max(bestValue, value);
//        }
//
//        return bestValue;
//    }
//
//    /**
//     * AND节点：处理动作执行后的所有可能状态
//     * - 跳过死亡状态（除非全死）
//     * - 限制最多处理10个状态（防止爆炸）
//     * - 对存活状态取平均值
//     */
//    private static double andNode(Result result, BeliefState prevState, int depth) {
//        if (result == null || result.size() == 0) {
//            return evaluate(prevState);
//        }
//
//        ArrayList<Double> aliveValues = new ArrayList<>();
//        int deathCount = 0;
//        int processed = 0;
//        int maxToProcess = 10;  // 限制处理数量
//
//        // 遍历所有可能的结果状态
//        for (int i = 0; i < result.size() && processed < maxToProcess; i++) {
//            BeliefState nextState = result.getBeliefState(i);
//            if (nextState == null) continue;
//
//            processed++;
//
//            // 检查是否死亡（生命减少）
//            if (nextState.getLife() < prevState.getLife()) {
//                deathCount++;
//                continue;
//            }
//
//            // 存活：计算值
//            double value;
//            if (depth <= 1) {
//                value = evaluate(nextState);
//            } else {
//                value = orNodeValue(nextState, depth - 1);
//            }
//            aliveValues.add(value);
//        }
//
//        // 如果全死了
//        if (aliveValues.isEmpty()) {
//            return -1000000;
//        }
//
//        // 计算存活状态的平均值
//        double sum = 0;
//        for (double v : aliveValues) {
//            sum += v;
//        }
//        return sum / aliveValues.size();
//    }
//
//    /**
//     * 评估函数 - 增强版，增加区分度
//     */
//    private static double evaluate(BeliefState state) {
//        if (state == null) return 0;
//
//        double score = 0;
//
//        Position pacman = state.getPacmanPosition();
//        char[][] map = state.getMap();
//
//        // 1. 吃掉的普通豆子数量（高奖励，鼓励吃豆）
//        int eatenGommes = initialGommes - state.getNbrOfGommes();
//        score += eatenGommes * 500;
//
//        // 2. 吃掉的大豆子数量（超高奖励）
//        int eatenSuperGommes = initialSuperGommes - state.getNbrOfSuperGommes();
//        score += eatenSuperGommes * 5000;
//
//        // 3. 距离最近豆子（权重加大，强引导）
//        int distToGum = state.distanceMinToGum();
//        if (distToGum < Integer.MAX_VALUE) {
//            score -= distToGum * 50;
//        }
//
//        // 4. 生命值
//        score += state.getLife() * 10000;
//
//        // 5. 游戏分数（实际得分）
//        score += state.getScore() * 2;
//
//        // 6. 检查当前位置是否有豆子（即时奖励）
//        char currentCell = map[pacman.x][pacman.y];
//        if (currentCell == '.') {
//            score += 300;
//        } else if (currentCell == '*') {
//            score += 2000;
//        }
//
//        // 7. 开放路口奖励：可走方向越多越好（避免走进死胡同）
//        int openDirections = countOpenDirections(pacman.x, pacman.y, map);
//        score += openDirections * 100;  // 每个开放方向加100分
//
//        return score;
//    }
//
//    /**
//     * 计算开放方向数量
//     */
//    private static int countOpenDirections(int x, int y, char[][] map) {
//        int count = 0;
//        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
//
//        for (int[] d : deltas) {
//            int nx = x + d[0];
//            int ny = y + d[1];
//            if (nx >= 0 && nx < map.length && ny >= 0 && ny < map[0].length
//                && map[nx][ny] != '#') {
//                count++;
//            }
//        }
//        return count;
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
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//




/**
 * AND-OR search AI for Pacman based strictly on BeliefState API.
 *
 * OR node  : Pacman chooses an action (max).
 * AND node : all possible outcomes caused by ghosts uncertainty (min / worst-case).
 *
 * Only AI.java edited.
 */


import java.util.*;

/**
 * AND-OR search Pacman AI (BeliefState-compatible).
 *
 * OR node : Pacman chooses an action (max).
 * AND node: all possible outcomes due to ghost uncertainty (min / worst-case).
 *
 * Adds:
 * - Anti-oscillation (corridor wandering 6-7 cells).
 * - Gum-drive to break ties toward food.
 * - Information gain bias to reduce uncertainty, but gated by safety:
 *   If a non-feared visible ghost is close (<=4) at root, do NOT chase info.
 *   Also penalize "approach visible dangerous ghost" in infoScore.
 *
 * Only AI.java edited.
 */

/* Helper containers expected by BeliefState.extendsBeliefState() */


import java.util.*;

/**
 * AND-OR search Pacman AI (BeliefState-compatible).
 *
 * OR node : Pacman chooses an action (max).
 * AND node: all possible outcomes due to ghost uncertainty (min / worst-case).
 *
 * Additions:
 * - Hard safety constraint: if a visible non-feared ghost is very close (<=2),
 *   only allow moves that strictly increase distance to the closest such ghost.
 * - Anti-oscillation for corridor wandering (6-7 cells).
 * - Gum-drive to break ties toward food.
 * - Information-gain bias (reduce uncertainty), gated by safety.
 *
 * Only AI.java edited.
 */

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















/**
 * AND-OR Pacman AI (as requested)
 *
 * OR node:
 * - enumerate all actions
 * - for each action: afterPacman(action) then expand into multiple states via predictGhosts(...)
 * - compute AND score and pick the best action
 *
 * AND node:
 * - among ghost outcomes, take WORST-CASE (min) value
 * - value prefers being closer to target, with extra rewards for eating dots/super
 * - death => -100000
 *
 * findNextMove:
 * - return orNode(root, MAX_DEPTH)
 */


/**
 * AND-OR Pacman AI (fixed):
 * - Real-turn ghost memory: only track after first confirmed sighting; forget after >10 unseen turns; eaten => delete memory.
 * - Ghost prediction when unseen: propagate probability distribution via simple transition on the map.
 * - OR anti-dither tie-break: prefer closer-to-target, then keep direction, else random.
 * - Evaluation: huge death penalty, discrete ghost distance penalties, strong target reward & target distance shaping.
 */


/**
 * AND-OR Pacman AI (full version, with all fixes):
 *
 * ✅ OR 节点：
 *   - 枚举所有行动
 *   - 对每个行动：afterPacman(action) -> AND 节点
 *   - 同分 tie-break：更接近目标 > 保持方向 > 随机
 *   - 如果进入 PANIC（看到鬼且很近）：优先选“更安全”的动作（第一时间躲）
 *
 * ✅ AND 节点：
 *   - 用 BeliefStateII.predictGhosts(...) 拓展多个状态
 *   - 取最坏情况（min）
 *
 * ✅ evaluate：
 *   - 死亡：-100000
 *   - “靠近鬼”离散惩罚（只对当前 BeliefStateII 里存在的鬼）
 *       同格：巨大惩罚（并且直接判为 DEATH_PENALTY，避免赌命）
 *       距离 1：-5000
 *       距离 2：-4000
 *       距离 3：-2000
 *     （按概率加权）
 *   - 大大鼓励吃目标（目标消失或到达：+TARGET_EAT_BONUS）
 *   - 鼓励靠近目标（距离 shaping）
 *   - 不使用 “最近豆子” 作为 fallback（避免目标一直变）
 *
 * ✅ 真实回合级“记鬼/忘鬼/重新记鬼/吃鬼删记忆” 全在 AI.java 实现：
 *   - 只有第一次 LOS 看见才开始记
 *   - 连续 >10 回合没看见 -> 忘掉（当不存在）
 *   - 吃到鬼（fear>0 且可见同格）-> 立刻删记忆，直到再次看见才重新记
 *   - 没看见时用 AI 里的一步 transition 扩散分布（不“乱放”）
 */
public class AI {

    /* ===================== Search config ===================== */

    private static final int MAX_DEPTH = 3;

    private static final int TOPK_PER_GHOST = 3;
    private static final int BEAM_WIDTH = 20;

    /* ===================== Scoring ===================== */

    private static final int DEATH_PENALTY = -100000;

    // Eating rewards (你要的“吃豆奖励”)
    private static final int REWARD_DOT = 800;
    private static final int REWARD_SUPER = 7000;

    // Target shaping
    private static final int TARGET_EAT_BONUS = 60000;     // 吃到目标 / 目标消失
    private static final int TARGET_REACH_BONUS = 8000;    // 走到目标格（dT==0）
    private static final int TARGET_DIST_PENALTY = 450;    // 距离越远扣越多

    // anti-dither
    private static final int REVERSE_PENALTY = 1000000;
    private static final int STAY_PENALTY = 6000;

    // target unreachable
    private static final int UNREACHABLE_TARGET_PENALTY = 45000;

    /* ===================== Root baseline ===================== */

    private static int rootDots;
    private static int rootSupers;

    private static String lastDir = null;
    private static final Random RNG = new Random();

    /* ===================== Target mechanism ===================== */

    private static int targetX = Integer.MIN_VALUE;
    private static int targetY = Integer.MIN_VALUE;

    private static int lastRealScore = Integer.MIN_VALUE;
    private static int noScoreStreak = 0;
    private static final int NO_SCORE_SWAP_THRESHOLD = 10;


    /* ===================== PANIC (看到鬼先躲) ===================== */

    // 触发条件：P(d<=1) 或 P(d<=2) 大于阈值
    private static final double PANIC_NEAR1 = 0.1;
    private static final double PANIC_NEAR2 = 0.2;

    // PANIC 时压制“冲目标”的欲望（防止第一时间不躲）
    private static final int PANIC_HARD_PENALTY = 80000;
    private static final int PANIC_SOFT_PENALTY = 50000;

    /* ===================== Ghost memory (REAL-TURN) ===================== */

    private static final int FORGET_AFTER_TURNS = 7;

    // 只有第一次看见才记；没看见就按预测扩散；>10 没看见就忘；吃鬼就删记忆
    private static boolean[] seenEver = null;
    private static int[] unseenTurns = null;
    private static HashMap<Integer, HashMap<Position, Double>> memDist = new HashMap<>();
    private static int memNGhost = -1;

    /* ===================== Entry ===================== */

    public static String findNextMove(BeliefState state) {
        if (state == null || state.getLife() <= 0) {
            resetMemory();
            return PacManLauncher.RIGHT;
        }

        // 真实分数长时间不变 -> 强制换目标，防止绕圈 0 分
        if (lastRealScore == Integer.MIN_VALUE) lastRealScore = state.getScore();
        if (state.getScore() == lastRealScore) noScoreStreak++;
        else noScoreStreak = 0;
        lastRealScore = state.getScore();

        rootDots = state.getNbrOfGommes();
        rootSupers = state.getNbrOfSuperGommes();

        BeliefStateII root = new BeliefStateII(state);

        // 真实回合级：记鬼/忘鬼/预测 并写回到 root（过滤掉“没记住”的鬼）
        syncGhostMemoryAndFilter(state, root);

        // 更新目标（目标失效/不可达/0分太久都换）
        updateTarget(root);

        // 按你要求：findNextMove 直接 return OR 节点
        return orNode(root, MAX_DEPTH);
    }

    private static void resetMemory() {
        lastDir = null;
        targetX = Integer.MIN_VALUE;
        targetY = Integer.MIN_VALUE;

        lastRealScore = Integer.MIN_VALUE;
        noScoreStreak = 0;

        seenEver = null;
        unseenTurns = null;
        memDist.clear();
        memNGhost = -1;
    }

    /* ===================== OR node ===================== */

    private static String orNode(BeliefStateII s, int depth) {
        if (s == null || s.getLife() <= 0) return null;

        List<String> actions = s.legalPacActions();
        if (actions == null || actions.isEmpty()) return null;

        boolean panic = isPanicNow(s);

        String bestAct = actions.get(0);
        int bestScore = Integer.MIN_VALUE;

        // tie-break helpers
        int bestDT = Integer.MAX_VALUE;
        boolean bestKeepDir = false;

        // panic helpers
        int bestSafe = Integer.MIN_VALUE;

        for (String act : actions) {
            BeliefStateII afterP = s.afterPacman(act);

            // AND 分数（最坏情况）
            int sc = andNode(afterP, depth);

            // anti-reverse
            if (lastDir != null && isOpposite(lastDir, act)) sc -= REVERSE_PENALTY;

            // tie-break distance-to-target
            int dT = estimateDistanceToTarget(afterP);
            boolean keepDir = (lastDir != null && lastDir.equals(act));

            // panic：优先安全
            int safe = safetyScore(afterP);

            if (!panic) {
                // 正常：主看 AND 分数
                if (sc > bestScore) {
                    bestScore = sc;
                    bestAct = act;
                    bestDT = dT;
                    bestKeepDir = keepDir;
                } else if (sc == bestScore) {
                    // tie-break 1：更接近目标
                    if (dT < bestDT) {
                        bestAct = act;
                        bestDT = dT;
                        bestKeepDir = keepDir;
                    } else if (dT == bestDT) {
                        // tie-break 2：保持方向
                        if (keepDir && !bestKeepDir) {
                            bestAct = act;
                            bestKeepDir = true;
                        } else if (keepDir == bestKeepDir) {
                            // tie-break 3：随机
                            if (RNG.nextBoolean()) bestAct = act;
                        }
                    }
                }
            }
            else {
                // PANIC：主看安全分，再看 AND 分数，再 tie-break
                if (safe > bestSafe) {
                    bestSafe = safe;
                    bestScore = sc;
                    bestAct = act;
                    bestDT = dT;
                    bestKeepDir = keepDir;
                } else if (safe == bestSafe) {
                    if (sc > bestScore) {
                        bestScore = sc;
                        bestAct = act;
                        bestDT = dT;
                        bestKeepDir = keepDir;
                    } else if (sc == bestScore) {
                        if (dT < bestDT) {
                            bestAct = act;
                            bestDT = dT;
                            bestKeepDir = keepDir;
                        } else if (dT == bestDT) {
                            if (keepDir && !bestKeepDir) {
                                bestAct = act;
                                bestKeepDir = true;
                            } else if (keepDir == bestKeepDir) {
                                if (RNG.nextBoolean()) bestAct = act;
                            }
                        }
                    }
                }
            }
        }

        lastDir = bestAct;
        return bestAct;
    }

    /* ===================== AND node ===================== */

    private static int andNode(BeliefStateII s, int depth) {
        if (s == null) return DEATH_PENALTY;
        if (s.getLife() <= 0) return DEATH_PENALTY;

        if (depth <= 0) return evaluate(s);

        List<BeliefStateII.Outcome> outs = s.predictGhosts(TOPK_PER_GHOST, BEAM_WIDTH);
        if (outs == null || outs.isEmpty()) return evaluate(s);

        // 最坏情况（min）
        int worst = Integer.MAX_VALUE;
        for (BeliefStateII.Outcome o : outs) {
            BeliefStateII st = o.state;
            int v = (depth - 1 <= 0) ? evaluate(st) : orValue(st, depth - 1);
            if (v < worst) worst = v;
        }
        return worst;
    }

    private static int orValue(BeliefStateII s, int depth) {
        if (s == null) return DEATH_PENALTY;
        if (s.getLife() <= 0) return DEATH_PENALTY;
        if (depth <= 0) return evaluate(s);

        int best = Integer.MIN_VALUE;
        for (String act : s.legalPacActions()) {
            BeliefStateII afterP = s.afterPacman(act);
            int v = andNode(afterP, depth);
            if (v > best) best = v;
        }
        return best;
    }

    /* ===================== Evaluation (按你要的) ===================== */

    private static int evaluate(BeliefStateII s) {
        if (s == null) return DEATH_PENALTY;
        if (s.getLife() <= 0) return DEATH_PENALTY;

        Position p = s.getPacmanPos();
        if (p == null) return DEATH_PENALTY;

        // 没目标就给大负分，逼 updateTarget 去选
        if (!hasTarget()) return -30000;

        int val = 0;

        // 1) 吃豆奖励（次要，目标更重要）
        int eatenDots = rootDots - s.getNbrGommes();
        int eatenSupers = rootSupers - s.getNbrSuper();
        if (eatenDots > 0) val += eatenDots * REWARD_DOT;
        if (eatenSupers > 0) val += eatenSupers * REWARD_SUPER;

        // 2) 目标奖励 & 靠近目标奖励
        char tc = safeCell(s.getMap(), targetX, targetY);
        boolean targetGone = (tc != '.' && tc != '*');
        boolean atTarget = (p.x == targetX && p.y == targetY);

        if (atTarget || targetGone) {
            val += TARGET_EAT_BONUS;
        }

        int dT = bfsDistance(s.getMap(), p.x, p.y, targetX, targetY, 450);
        if (dT >= 9999) {
            // 目标不可达：重罚（并且上层 updateTarget 会换目标）
            val -= UNREACHABLE_TARGET_PENALTY;
            dT = manhattan(p.x, p.y, targetX, targetY);
        }

        val -= dT * TARGET_DIST_PENALTY;
        val += Math.max(0, 12000 - dT * 900);
        if (dT == 0) val += TARGET_REACH_BONUS;

        // 3) 鬼离散惩罚（只对 BeliefStateII 里存在的鬼；忘了的鬼不在列表里）
        boolean panicTriggered = false;

        for (int gi = 0; gi < s.getNbrGhost(); gi++) {
            BeliefStateII.GhostBelief gb = s.getGhostBelief(gi);
            if (gb == null || gb.prob == null || gb.prob.isEmpty()) continue;

            // 恐惧状态鬼不算危险（你如果想更保守，删掉这一行）
            if (gb.fear > 0) continue;

            double p0 = 0.0, p1 = 0.0, p2 = 0.0, p3 = 0.0;

            for (Map.Entry<Position, Double> ent : gb.prob.entrySet()) {
                Position g = ent.getKey();
                double pr = ent.getValue();
                int d = manhattan(p.x, p.y, g.x, g.y);
                if (d == 0) p0 += pr;
                else if (d == 1) p1 += pr;
                else if (d == 2) p2 += pr;
                else if (d == 3) p3 += pr;
            }

            // 同格：不赌命，直接判成 DEATH（你要“扣大分”，这里更强：直接当必死分支）
            if (p0 > 0.0) return DEATH_PENALTY;

            // 你指定的离散惩罚（概率加权）
            val -= (int)(p1 * 5000.0);
            val -= (int)(p2 * 5000.0);
            val -= (int)(p3 * 2000.0);

            double near1 = p1;            // 因为 p0 已经 return
            double near2 = p1 + p2;

            if (near1 >= PANIC_NEAR1 || near2 >= PANIC_NEAR2) panicTriggered = true;
        }

        // 4) PANIC 时压制“冲目标”（保证第一时间躲）
        if (panicTriggered) {
            // near1 更强 / near2 次强：这里简化成硬压制
            val -= PANIC_SOFT_PENALTY;
        }

        // 5) 原地惩罚（防止抖动）
        Position old = s.getPacmanOldPos();
        if (old != null && old.x == p.x && old.y == p.y) val -= STAY_PENALTY;

        return val;
    }

    /* ===================== Target logic ===================== */

    private static void updateTarget(BeliefStateII root) {
        if (root == null) return;
        char[][] map = root.getMap();
        Position p = root.getPacmanPos();
        if (map == null || p == null) return;

        // 0分太久 -> 强制换目标
        if (noScoreStreak >= NO_SCORE_SWAP_THRESHOLD) {
            clearTarget();
            noScoreStreak = 0;
        }

        // 目标失效/已吃
        if (hasTarget()) {
            char c = safeCell(map, targetX, targetY);
            if (c != '.' && c != '*') clearTarget();
            if (p.x == targetX && p.y == targetY) clearTarget();
        }

        // 目标不可达 -> 换
        if (hasTarget()) {
            int d = bfsDistance(map, p.x, p.y, targetX, targetY, 450);
            if (d >= 9999) clearTarget();
        }

        if (hasTarget()) return;

        // 选新目标：优先超级豆，否则普通豆；随机
        int[] t = pickNearestTarget(map, p.x, p.y);
        if (t != null) {
            targetX = t[0];
            targetY = t[1];
        }
    }

    private static int[] pickNearestTarget(char[][] map, int sx, int sy) {
    int H = map.length, W = map[0].length;
    boolean[][] vis = new boolean[H][W];
    ArrayDeque<int[]> q = new ArrayDeque<>();
    q.add(new int[]{sx, sy, 0});
    vis[sx][sy] = true;

    int[] bestDot = null;
    int[] bestSuper = null;

    int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};

    while (!q.isEmpty()) {
        int[] cur = q.poll();
        int x = cur[0], y = cur[1], d = cur[2];

        char c = map[x][y];
        if (c == '*') return new int[]{x, y}; // 最近超级豆，直接返回（最优）
        if (c == '.' && bestDot == null) bestDot = new int[]{x, y};

        for (int[] dd : dirs) {
            int nx = x + dd[0], ny = y + dd[1];
            if (nx<0||ny<0||nx>=H||ny>=W) continue;
            if (vis[nx][ny]) continue;
            if (map[nx][ny] == '#') continue;
            vis[nx][ny] = true;
            q.add(new int[]{nx, ny, d+1});
        }
    }
    return bestDot; // 没有超级豆就返回最近普通豆；都没有则 null
}


    private static int[] pickRandomTarget(char[][] map) {
        int H = map.length, W = map[0].length;
        ArrayList<int[]> supers = new ArrayList<>();
        ArrayList<int[]> dots = new ArrayList<>();

        for (int x = 0; x < H; x++) {
            for (int y = 0; y < W; y++) {
                if (map[x][y] == '*') supers.add(new int[]{x, y});
                else if (map[x][y] == '.') dots.add(new int[]{x, y});
            }
        }

        if (!supers.isEmpty()) return supers.get(RNG.nextInt(supers.size()));
        if (!dots.isEmpty()) return dots.get(RNG.nextInt(dots.size()));
        return null;
    }

    private static boolean hasTarget() {
        return targetX != Integer.MIN_VALUE && targetY != Integer.MIN_VALUE;
    }

    private static void clearTarget() {
        targetX = Integer.MIN_VALUE;
        targetY = Integer.MIN_VALUE;
    }

    /* ===================== PANIC helpers ===================== */

    // 当前状态是否应该“第一时间躲”
    private static boolean isPanicNow(BeliefStateII s) {
        if (s == null) return false;
        Position p = s.getPacmanPos();
        if (p == null) return false;

        for (int gi = 0; gi < s.getNbrGhost(); gi++) {
            BeliefStateII.GhostBelief gb = s.getGhostBelief(gi);
            if (gb == null || gb.prob == null || gb.prob.isEmpty()) continue;
            if (gb.fear > 0) continue;

            double near1 = 0.0, near2 = 0.0;

            for (Map.Entry<Position, Double> ent : gb.prob.entrySet()) {
                Position g = ent.getKey();
                double pr = ent.getValue();
                int d = manhattan(p.x, p.y, g.x, g.y);
                if (d <= 1) near1 += pr;
                if (d <= 2) near2 += pr;
            }

            if (near1 >= PANIC_NEAR1 || near2 >= PANIC_NEAR2) return true;
        }
        return false;
    }

    // 动作后的安全分：越大越安全（PANIC 下 OR 直接优先它）
    private static int safetyScore(BeliefStateII afterP) {
        if (afterP == null || afterP.getLife() <= 0) return Integer.MIN_VALUE;
        Position p = afterP.getPacmanPos();
        if (p == null) return Integer.MIN_VALUE;

        double minExp = 9999.0;
        int minDet = 9999;

        for (int gi = 0; gi < afterP.getNbrGhost(); gi++) {
            BeliefStateII.GhostBelief gb = afterP.getGhostBelief(gi);
            if (gb == null || gb.prob == null || gb.prob.isEmpty()) continue;
            if (gb.fear > 0) continue;

            double exp = 0.0;

            for (Map.Entry<Position, Double> ent : gb.prob.entrySet()) {
                Position g = ent.getKey();
                double pr = ent.getValue();
                int d = manhattan(p.x, p.y, g.x, g.y);
                exp += pr * d;
                if (d < minDet) minDet = d;
            }
            if (exp < minExp) minExp = exp;
        }

        int sc = 0;
        sc += (minDet >= 9999 ? 0 : minDet * 25000);
        sc += (minExp >= 9999.0 ? 0 : (int)(minExp * 4000.0));
        return sc;
    }

    /* ===================== Ghost memory: sync + overwrite BeliefStateII ===================== */

    private static void syncGhostMemoryAndFilter(BeliefState state, BeliefStateII root) {
        int nGhost;
        try { nGhost = state.getNbrOfGhost(); }
        catch (Exception e) { nGhost = 0; }

        if (seenEver == null || memNGhost != nGhost) {
            seenEver = new boolean[nGhost];
            unseenTurns = new int[nGhost];
            memDist.clear();
            memNGhost = nGhost;
        }

        Position pac = state.getPacmanPos();
        if (pac == null) return;

        char[][] map = state.getMap();

        boolean[] visibleNow = new boolean[nGhost];
        boolean[] onPacVisible = new boolean[nGhost];
        int[] fearNow = new int[nGhost];

        for (int i = 0; i < nGhost; i++) {
            fearNow[i] = safeFear(state, i);

            TreeSet<Position> poss;
            try { poss = state.getGhostPositions(i); }
            catch (Exception e) { poss = null; }

            if (poss == null || poss.isEmpty()) {
                // 没有任何候选（引擎给不了） -> 如果之前记过就继续预测，否则忽略
                if (seenEver[i]) {
                    unseenTurns[i]++;
                    if (unseenTurns[i] > FORGET_AFTER_TURNS) {
                        seenEver[i] = false;
                        unseenTurns[i] = 0;
                        memDist.remove(i);
                    } else {
                        HashMap<Position, Double> oldDist = memDist.get(i);
                        if (oldDist != null && !oldDist.isEmpty() && map != null) {
                            HashMap<Position, Double> pred = predictOneStepGhost(oldDist, map);
                            pruneVisibleStates(pred, pac);
                            if (pred.isEmpty()) {
                                seenEver[i] = false;
                                unseenTurns[i] = 0;
                                memDist.remove(i);
                            } else {
                                normalize(pred);
                                memDist.put(i, pred);
                            }
                        }
                    }
                }
                continue;
            }

            // 判断是否可见
            for (Position g : poss) {
                if (g == null) continue;
                if (BeliefState.isVisible(g.x, g.y, pac.x, pac.y)) {
                    visibleNow[i] = true;
                    if (g.x == pac.x && g.y == pac.y) onPacVisible[i] = true;
                }
            }

            // 吃鬼：fear>0 且可见同格 -> 立刻删记忆（直到再看见）
            if (visibleNow[i] && onPacVisible[i] && fearNow[i] > 0) {
                seenEver[i] = false;
                unseenTurns[i] = 0;
                memDist.remove(i);
                continue;
            }

            if (visibleNow[i]) {
                // 第一次（或再次）看见 -> 开始/继续记
                seenEver[i] = true;
                unseenTurns[i] = 0;

                // 把分布塌缩到“可见格子集合”（严格，不乱放）
                ArrayList<Position> visList = new ArrayList<>();
                for (Position g : poss) {
                    if (g != null && BeliefState.isVisible(g.x, g.y, pac.x, pac.y)) visList.add(g);
                }

                if (!visList.isEmpty()) {
                    HashMap<Position, Double> dist = new HashMap<>();
                    double p0 = 1.0 / visList.size();
                    for (Position g : visList) dist.put(new Position(g.x, g.y, g.dir), p0);
                    normalize(dist);
                    memDist.put(i, dist);
                } else {
                    // 理论上不会发生
                    memDist.remove(i);
                }
            } else {
                // 没看见
                if (seenEver[i]) {
                    unseenTurns[i]++;

                    if (unseenTurns[i] > FORGET_AFTER_TURNS) {
                        seenEver[i] = false;
                        unseenTurns[i] = 0;
                        memDist.remove(i);
                    } else {
                        HashMap<Position, Double> oldDist = memDist.get(i);
                        if (oldDist != null && !oldDist.isEmpty() && map != null) {
                            HashMap<Position, Double> pred = predictOneStepGhost(oldDist, map);
                            pruneVisibleStates(pred, pac);
                            if (pred.isEmpty()) {
                                // STRICT：空了就忘
                                seenEver[i] = false;
                                unseenTurns[i] = 0;
                                memDist.remove(i);
                            } else {
                                normalize(pred);
                                memDist.put(i, pred);
                            }
                        }
                    }
                }
            }
        }

        // 写回 root：删除没记住的鬼；把记住的鬼的 prob 覆盖成 memDist
        for (int i = nGhost - 1; i >= 0; i--) {
            if (i >= root.getNbrGhost()) continue;

            if (!seenEver[i]) {
                root.ghosts.remove(i);
            } else {
                BeliefStateII.GhostBelief gb = root.ghosts.get(i);
                gb.fear = fearNow[i];

                // 你的 BeliefStateII STRICT 版本里有 unseenTurns 字段；没有的话这行删掉也能编译
                try { gb.unseenTurns = unseenTurns[i]; } catch (Exception ignored) {}

                HashMap<Position, Double> dist = memDist.get(i);
                gb.prob.clear();
                if (dist != null) {
                    for (Map.Entry<Position, Double> e : dist.entrySet()) {
                        Position pos = e.getKey();
                        gb.prob.put(new Position(pos.x, pos.y, pos.dir), e.getValue());
                    }
                }
                gb.normalize();

                if (gb.prob.isEmpty()) {
                    root.ghosts.remove(i);
                    seenEver[i] = false;
                    unseenTurns[i] = 0;
                    memDist.remove(i);
                }
            }
        }
    }

    private static int safeFear(BeliefState s, int i) {
        try { return s.getCompteurPeur(i); } catch (Exception e) { return 0; }
    }

    /* ===================== Ghost prediction helpers (AI-side) ===================== */

    // 一步预测：把概率扩散到合法邻居；偏好直行，弱化反向
    private static HashMap<Position, Double> predictOneStepGhost(HashMap<Position, Double> cur, char[][] map) {
        HashMap<Position, Double> out = new HashMap<>();
        int H = map.length, W = map[0].length;

        for (Map.Entry<Position, Double> ent : cur.entrySet()) {
            Position g = ent.getKey();
            double pg = ent.getValue();

            ArrayList<Position> moves = new ArrayList<>(4);
            if (isWalkable(map, H, W, g.x - 1, g.y)) moves.add(new Position(g.x - 1, g.y, 'U'));
            if (isWalkable(map, H, W, g.x + 1, g.y)) moves.add(new Position(g.x + 1, g.y, 'D'));
            if (isWalkable(map, H, W, g.x, g.y - 1)) moves.add(new Position(g.x, g.y - 1, 'L'));
            if (isWalkable(map, H, W, g.x, g.y + 1)) moves.add(new Position(g.x, g.y + 1, 'R'));

            if (moves.isEmpty()) {
                addProb(out, new Position(g.x, g.y, g.dir), pg);
                continue;
            }

            double sumW = 0.0;
            double[] w = new double[moves.size()];
            for (int k = 0; k < moves.size(); k++) {
                Position n = moves.get(k);
                double wi = 1.0;
                if (n.dir == g.dir) wi *= 2.2;
                if (isOppositeDir(g.dir, n.dir)) wi *= 0.35;
                w[k] = wi;
                sumW += wi;
            }

            for (int k = 0; k < moves.size(); k++) {
                double pr = pg * (w[k] / sumW);
                addProb(out, moves.get(k), pr);
            }
        }

        return out;
    }

    // 没看见时，分布中“可见格子”不可能存在：删掉
    private static void pruneVisibleStates(HashMap<Position, Double> dist, Position pac) {
        if (dist == null || dist.isEmpty() || pac == null) return;
        Iterator<Map.Entry<Position, Double>> it = dist.entrySet().iterator();
        while (it.hasNext()) {
            Position pos = it.next().getKey();
            if (BeliefState.isVisible(pos.x, pos.y, pac.x, pac.y)) it.remove();
        }
    }

    private static boolean isWalkable(char[][] map, int H, int W, int x, int y) {
        if (x < 0 || y < 0 || x >= H || y >= W) return false;
        return map[x][y] != '#';
    }

    private static void normalize(HashMap<Position, Double> m) {
        double s = 0.0;
        for (double v : m.values()) s += v;
        if (s <= 0) return;
        for (Map.Entry<Position, Double> e : m.entrySet()) e.setValue(e.getValue() / s);
    }

    private static void addProb(HashMap<Position, Double> m, Position p, double v) {
        Double old = m.get(p);
        if (old == null) m.put(p, v);
        else m.put(p, old + v);
    }

    private static boolean isOppositeDir(char a, char b) {
        return (a == 'U' && b == 'D') || (a == 'D' && b == 'U') ||
               (a == 'L' && b == 'R') || (a == 'R' && b == 'L');
    }

    /* ===================== Distance helpers ===================== */

    private static int estimateDistanceToTarget(BeliefStateII s) {
        if (s == null || !hasTarget()) return 9999;
        Position p = s.getPacmanPos();
        if (p == null) return 9999;

        int d = bfsDistance(s.getMap(), p.x, p.y, targetX, targetY, 250);
        if (d >= 9999) d = manhattan(p.x, p.y, targetX, targetY);
        return d;
    }

    private static int bfsDistance(char[][] map, int sx, int sy, int tx, int ty, int maxD) {
        if (map == null) return 9999;
        if (sx == tx && sy == ty) return 0;

        int H = map.length, W = map[0].length;
        boolean[][] vis = new boolean[H][W];
        ArrayDeque<int[]> q = new ArrayDeque<>();
        q.add(new int[]{sx, sy, 0});
        vis[sx][sy] = true;

        while (!q.isEmpty()) {
            int[] cur = q.removeFirst();
            int x = cur[0], y = cur[1], d = cur[2];
            if (d >= maxD) continue;

            for (int k = 0; k < 4; k++) {
                int nx = x + DX[k], ny = y + DY[k];
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

    private static char safeCell(char[][] map, int x, int y) {
        if (map == null) return '#';
        if (x < 0 || y < 0 || x >= map.length || y >= map[0].length) return '#';
        return map[x][y];
    }

    private static int manhattan(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2; if (dx < 0) dx = -dx;
        int dy = y1 - y2; if (dy < 0) dy = -dy;
        return dx + dy;
    }

    private static boolean isOpposite(String a, String b) {
        if (a == null || b == null) return false;
        return (a.equals(PacManLauncher.UP) && b.equals(PacManLauncher.DOWN)) ||
               (a.equals(PacManLauncher.DOWN) && b.equals(PacManLauncher.UP)) ||
               (a.equals(PacManLauncher.LEFT) && b.equals(PacManLauncher.RIGHT)) ||
               (a.equals(PacManLauncher.RIGHT) && b.equals(PacManLauncher.LEFT));
    }

    private static final int[] DX = {-1, 1, 0, 0};
    private static final int[] DY = {0, 0, -1, 1};
}













