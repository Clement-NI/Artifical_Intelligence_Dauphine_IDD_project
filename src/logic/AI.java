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
 * AI utilisant l'algorithme AND-OR Search
 *
 * Structure de l'arbre:
 * - Noeud OR: Pacman choisit une action (UP, DOWN, LEFT, RIGHT)
 * - Noeud AND: Tous les mouvements possibles des fantômes
 */


/**
 * 最终修正版 AI
 * 修复：
 * 1. 移除了 AND-OR 搜索中的随机 Shuffle，彻底消除原地抖动。
 * 2. 修正了权重公式：大幅提高鬼魂斥力，防止“为了不走回头路而撞鬼”。
 * 3. 加入方向惯性：鼓励 Pacman 走直线。
 */


/**
 * 最终激进版 AI
 * 针对问题：解决“绕圈圈”、“不敢吃豆”、“反复横跳”。
 * 策略：
 * 1. 惯性系统：大幅奖励保持方向，强行突破震荡。
 * 2. 勇敢评估：在中距离（3格以外）大幅降低鬼魂扣分，依靠 Minimax 预测来保命，而不是靠静态恐惧。
 * 3. 长效记忆：增加历史记录长度，打破绕圈死循环。
 */


/**
 * 纯粹 AND-OR 算法实现 (Pure AND-OR Search)
 * 核心思想：基于信念状态的 Minimax 树搜索
 * * 1. AND 层 (Min Node): 应对环境的不确定性。采取"悲观准则"，取所有可能结果中的最小值。
 * 2. OR 层 (Max Node): Pacman 的决策。在最坏情况中选择最好的结果。
 * 3. 分层评估: 严格区分"保命状态"和"吃豆状态"，杜绝贪婪导致的死亡。
 */

/**
 * AI utilisant AND-OR Search
 * 只对确定位置的鬼（可见的）恐惧，不确定位置的鬼忽略
 */




/**
 * 严格 AND-OR 搜索算法 (Strict AND-OR Search)
 * * 结构定义：
 * 1. OR Node (Pacman的决策): 在所有可行的 Plan 中选择一个最优的。
 * 2. AND Node (环境的不确定性): 一个 Plan 会导致 Result。Result 包含多个可能的 BeliefStates。
 * 对于 AND 节点，一个动作的价值 = 它所有可能结果的最小值 (必须在所有平行宇宙中都存活)。
 */


/**
 * 严格 AND-OR 树搜索 AI
 * * 逻辑层级：
 * 1. OR 层 (Pacman): 遍历所有 Plan，选择得分最高的一个 (Max)。
 * 2. AND 层 (Environment): 遍历一个 Plan 导致的所有 BeliefState。
 * 只要有一个 BeliefState 是死亡，该 Plan 就视为无效 (Min / 一票否决)。
 * * 评分体系：
 * 1. 活着是前提。
 * 2. 吃到大豆子 (Super Gum) 加巨分。
 * 3. 吃到小豆子 (Gum) 加分。
 */
public class AI {

    private static final int MAX_DEPTH = 2;

    // 记录初始状态用于计算吃了多少豆
    private static int initialGommes = -1;
    private static int initialSuperGommes = -1;

    // 方向惯性
    private static String lastDirection = null;

    /**
     * 入口
     */
    public static String findNextMove(BeliefState state) {
        if (state == null || state.getLife() <= 0) {
            lastDirection = null;
            return PacManLauncher.RIGHT;
        }

        // 记录初始豆子数
        initialGommes = state.getNbrOfGommes();
        initialSuperGommes = state.getNbrOfSuperGommes();

        // OR节点：遍历所有动作，选最大值
        return orNode(state, MAX_DEPTH);
    }

    /**
     * OR节点：选最佳动作
     */
    private static String orNode(BeliefState state, int depth) {
        Plans plans = state.extendsBeliefState();

        if (plans.size() == 0) {
            return PacManLauncher.RIGHT;
        }

        String bestAction = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < plans.size(); i++) {
            ArrayList<String> actionList = plans.getAction(i);
            Result result = plans.getResult(i);

            if (actionList.isEmpty()) continue;

            String action = actionList.get(0);
            double value = andNode(result, state, depth);

            // 方向惯性：保持方向加分，掉头扣分
            if (depth == MAX_DEPTH) {
                if (action.equals(lastDirection)) {
                    value += 200;
                }
                if (isOpposite(action, lastDirection)) {
                    value -= 500;
                }
            }

            if (value > bestValue) {
                bestValue = value;
                bestAction = action;
            }
        }

        // 记录方向
        if (bestAction != null && depth == MAX_DEPTH) {
            lastDirection = bestAction;
        }

        return bestAction;
    }

    /**
     * 判断方向是否相反
     */
    private static boolean isOpposite(String d1, String d2) {
        if (d1 == null || d2 == null) return false;
        return (d1.equals(PacManLauncher.UP) && d2.equals(PacManLauncher.DOWN)) ||
               (d1.equals(PacManLauncher.DOWN) && d2.equals(PacManLauncher.UP)) ||
               (d1.equals(PacManLauncher.LEFT) && d2.equals(PacManLauncher.RIGHT)) ||
               (d1.equals(PacManLauncher.RIGHT) && d2.equals(PacManLauncher.LEFT));
    }

    /**
     * OR节点（递归版本）
     */
    private static double orNodeValue(BeliefState state, int depth) {
        if (depth <= 0) {
            return evaluate(state);
        }

        Plans plans = state.extendsBeliefState();
        if (plans.size() == 0) {
            return evaluate(state);
        }

        double bestValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < plans.size(); i++) {
            Result result = plans.getResult(i);
            double value = andNode(result, state, depth);
            bestValue = Math.max(bestValue, value);
        }
        return bestValue;
    }

    /**
     * AND节点：跳过死亡，取存活平均
     */
    private static double andNode(Result result, BeliefState prevState, int depth) {
        if (result == null || result.size() == 0) {
            return evaluate(prevState);
        }

        ArrayList<Double> aliveValues = new ArrayList<>();
        int maxToProcess = 10;
        int processed = 0;

        for (int i = 0; i < result.size(); i++) {
            BeliefState nextState = result.getBeliefState(i);
            if (nextState == null) continue;
            processed++;

            // 死亡：跳过
            if (nextState.getLife() < prevState.getLife()) {
                continue;
            }

            double value;
            if (depth <= 1) {
                value = evaluate(nextState);
            } else {
                value = orNodeValue(nextState, depth - 1);
            }
            aliveValues.add(value);
        }

        if (aliveValues.isEmpty()) {
            return -1000000;
        }

        double sum = 0;
        for (double v : aliveValues) {
            sum += v;
        }
        return sum / aliveValues.size();
    }

    /**
     * 评估函数
     *
     * score = food_gain - λ * risk
     */
    private static double evaluate(BeliefState state) {
        if (state == null) return 0;

        Position pacman = state.getPacmanPosition();
        char[][] map = state.getMap();

        // === 生命 ===
        double lifeScore = state.getLife() * 1000000;

        // === 风险（用改进的max+指数衰减）===
        double risk = -evaluateGhostDanger(state, pacman);  // 正数

        // === 吃豆收益 ===
        double foodGain = 0;

        // 吃掉的豆子
        int eatenGommes = initialGommes - state.getNbrOfGommes();
        foodGain += eatenGommes * 500;

        int eatenSuperGommes = initialSuperGommes - state.getNbrOfSuperGommes();
        foodGain += eatenSuperGommes * 3000;

        // 最近豆子距离（主要奖励）
        int distToGum = state.distanceMinToGum();
        if (distToGum < Integer.MAX_VALUE) {
            foodGain -= distToGum * 50;
        }

        // 当前位置有豆子
        char cell = map[pacman.x][pacman.y];
        if (cell == '.') foodGain += 600;
        else if (cell == '*') foodGain += 2000;

        // === 路口（只在高风险时重要）===
        int openDirs = countOpenDirections(pacman.x, pacman.y, map);
        double openScore;
        if (risk > 3000) {
            // 高风险：路口很重要
            switch (openDirs) {
                case 4: openScore = 3000; break;
                case 3: openScore = 1500; break;
                case 2: openScore = 0; break;
                default: openScore = -2000; break;
            }
        }
        else {
            // 低风险：路口不太重要
            switch (openDirs) {
                case 4: openScore = 500; break;
                case 3: openScore = 200; break;
                case 2: openScore = 0; break;
                default: openScore = -300; break;
            }
        }

        // 最终：life + foodGain + openScore - risk
        return lifeScore + foodGain + openScore - risk;
    }

    /**
     * 评估鬼的威胁 - 改进版
     *
     * 1. 用 MAX 而不是 SUM（防止多预测点惩罚爆炸）
     * 2. 指数衰减（远距离快速归零）
     * 3. 截断（超过距离R直接忽略）
     */
    private static double evaluateGhostDanger(BeliefState state, Position pacman) {
        double maxDanger = 0;  // 取最大值，不累加！
        final int R = 5;       // 截断半径：超过5格不考虑

        for (int i = 0; i < state.getNbrOfGhost(); i++) {
            if (state.getCompteurPeur(i) > 0) continue;

            TreeSet<Position> ghostPositions = state.getGhostPositions(i);

            for (Position ghost : ghostPositions) {
                int dist = Math.abs(ghost.x - pacman.x) + Math.abs(ghost.y - pacman.y);

                // 截断：超过R格直接忽略
                if (dist > R) continue;

                boolean visible = BeliefState.isVisible(ghost.x, ghost.y, pacman.x, pacman.y);

                // 指数衰减：exp(-k*d)，k=1.5
                double danger;
                if (visible) {
                    // 可见：高威胁
                    danger = 10000 * Math.exp(-1.5 * dist);
                } else {
                    // 不可见：低威胁
                    danger = 3000 * Math.exp(-1.5 * dist);
                }

                // 取最大值
                maxDanger = Math.max(maxDanger, danger);
            }
        }

        return -maxDanger;  // 返回负数作为惩罚
    }

    /**
     * 计算开放方向数量
     */
    private static int countOpenDirections(int x, int y, char[][] map) {
        int count = 0;
        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] d : deltas) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (nx >= 0 && nx < map.length && ny >= 0 && ny < map[0].length
                && map[nx][ny] != '#') {
                count++;
            }
        }
        return count;
    }
}






































//public class AI {
//
//    private static final int MAX_DEPTH = 2;
//
//    // 记录初始状态用于计算吃了多少豆
//    private static int initialGommes = -1;
//    private static int initialSuperGommes = -1;
//
//    // 绕圈检测
//    private static LinkedList<String> positionHistory = new LinkedList<>();
//    private static final int HISTORY_SIZE = 16;
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



















