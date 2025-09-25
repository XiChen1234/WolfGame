package com.wolfgame.core;

import com.wolfgame.strategy.Strategy;
import com.wolfgame.util.Log;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 游戏主控制类，负责游戏流程的控制和胜负判定
 */
public class Game {
    private List<Player> players; // 所有玩家列表
    private int dayCount; // 天数计数
    private boolean isGameOver; // 游戏是否结束
    private Camp winnerCamp; // 胜利阵营
    private final Log gameLog; // 游戏日志
    private final Map<Role, Strategy> roleStrategies; // 角色策略映射
    private final Random random; // 随机数生成器
    private boolean antidoteUsed; // 解药是否已使用
    private final boolean poisonUsed; // 毒药是否已使用

    public Game(int gameId, Map<Role, Strategy> roleStrategies, long randomSeed) {
        this.roleStrategies = roleStrategies;
        this.random = new Random(randomSeed);
        this.dayCount = 0;
        this.isGameOver = false;
        this.antidoteUsed = false;
        this.poisonUsed = false;
        this.gameLog = new Log(gameId);
        initializePlayers();
    }

    /**
     * 初始化玩家列表，分配角色和策略
     */
    private void initializePlayers() {
        players = new ArrayList<>();
        List<Role> roles = new ArrayList<>();

        // 添加标准7人局角色配置
        roles.add(Role.WOLF);
        roles.add(Role.WOLF);
        roles.add(Role.PROPHET);
        roles.add(Role.WITCH);
        roles.add(Role.HUNTER);
        roles.add(Role.VILLAGER);
        roles.add(Role.VILLAGER);

        // 随机打乱角色顺序
        Collections.shuffle(roles, random);

        // 创建玩家并分配角色和策略
        for (int i = 0; i < roles.size(); i++) {
            Role role = roles.get(i);
            Strategy strategy = roleStrategies.getOrDefault(role, roleStrategies.get(Role.VILLAGER));
            players.add(new Player(i + 1, "玩家" + (i + 1), role, strategy));
        }

        // 记录角色分配信息
        gameLog.logRoleAssignment(players);
    }

    /**
     * 运行一局游戏
     */
    public void run() {
        while (!isGameOver) {
            dayCount++;
            gameLog.logDayStart(dayCount);

            // 夜晚阶段
            nightPhase();
            if (checkGameOver())
                break;

            // 白天阶段
            dayPhase();
            checkGameOver();
        }

        // 记录游戏结果
        gameLog.logGameResult(winnerCamp, players);
        // 保存日志到文件
        gameLog.saveToFile();
    }

    /**
     * 夜晚阶段处理
     */
    private void nightPhase() {
        gameLog.logNightStart();

        // 获取存活玩家
        List<Player> alivePlayers = getAlivePlayers();
        List<Player> wolves = alivePlayers.stream()
                .filter(Player::isWolf)
                .collect(Collectors.toList());

        // 1. 狼人选择击杀目标
        Player victim = null;
        if (!wolves.isEmpty()) {
            // 狼人共同商议选择击杀目标（这里简化为使用第一个狼人的策略）
            victim = wolves.getFirst().getStrategy().chooseKillTarget(wolves, players);
            if (victim != null) {
                gameLog.logWolfKill(victim);
            }
        }

        // 2. 预言家查验身份
        Player prophet = alivePlayers.stream()
                .filter(p -> p.getRole() == Role.PROPHET)
                .findFirst().orElse(null);
        if (prophet != null) {
            Player checkTarget = prophet.getStrategy().chooseCheckTarget(prophet, players);
            if (checkTarget != null) {
                boolean isWolf = checkTarget.isWolf();
                gameLog.logProphetCheck(prophet, checkTarget, isWolf);
            }
        }

        // 3. 女巫使用解药或毒药
        Player witch = alivePlayers.stream()
                .filter(p -> p.getRole() == Role.WITCH)
                .findFirst().orElse(null);
        if (witch != null) {
            // 检查是否使用解药
            if (victim != null && !antidoteUsed && witch.getStrategy().useAntidote(witch, victim, players)) {
                gameLog.logWitchSave(victim);
                victim = null; // 被害人被救活
                antidoteUsed = true; // 解药已使用
            }

            // 检查是否使用毒药
            if (!poisonUsed && !antidoteUsed) {
                Player poisonTarget = witch.getStrategy().usePoison(witch, players);
                if (poisonTarget != null) {
                    gameLog.logWitchPoison(poisonTarget);
                    poisonTarget.setPoisoned(true);
                    poisonTarget.setAlive(false);
                    // 猎人被毒死时无法开枪
                    checkHunterSkill(poisonTarget, true);
                }
            }
        }

        // 执行狼人击杀
        if (victim != null) {
            victim.setAlive(false);
            // 猎人被狼人杀死时可以开枪
            checkHunterSkill(victim, false);
        }

        // 记录夜晚结束信息
        gameLog.logNightEnd(getDeadPlayersThisNight());
    }

    /**
     * 检查猎人技能是否触发
     */
    private void checkHunterSkill(Player player, boolean isPoisoned) {
        if (player.getRole() == Role.HUNTER && !player.isAlive()) {
            Player gunTarget = player.getStrategy().useGun(player, isPoisoned, players);
            if (gunTarget != null && gunTarget.isAlive()) {
                gameLog.logHunterShoot(player, gunTarget);
                gunTarget.setAlive(false);
                // 被猎人带走的玩家如果是猎人，也需要检查技能，但这里简化处理
            }
        }
    }

    /**
     * 白天阶段处理
     */
    private void dayPhase() {
        gameLog.logDayStartDiscussion();

        // 获取存活玩家
        List<Player> alivePlayers = getAlivePlayers();
        if (alivePlayers.isEmpty()) {
            return;
        }

        // 模拟发言讨论（简化处理，直接进入投票阶段）

        // 投票阶段
        Map<Player, Integer> voteCount = new HashMap<>();
        for (Player voter : alivePlayers) {
            Player voteTarget = voter.getStrategy().vote(voter, alivePlayers, players);
            if (voteTarget != null && voteTarget.isAlive()) {
                voteCount.put(voteTarget, voteCount.getOrDefault(voteTarget, 0) + 1);
                gameLog.logVote(voter, voteTarget);
            }
        }

        // 统计投票结果
        if (!voteCount.isEmpty()) {
            // 找出得票最多的玩家
            Player mostVoted = null;
            int maxVotes = 0;
            boolean tie = false;

            for (Map.Entry<Player, Integer> entry : voteCount.entrySet()) {
                if (entry.getValue() > maxVotes) {
                    maxVotes = entry.getValue();
                    mostVoted = entry.getKey();
                    tie = false;
                } else if (entry.getValue() == maxVotes) {
                    tie = true;
                }
            }

            // 处理投票结果
            if (!tie && mostVoted != null) {
                mostVoted.setAlive(false);
                gameLog.logLynching(mostVoted, maxVotes);
                // 检查猎人技能
                checkHunterSkill(mostVoted, mostVoted.isPoisoned());
            } else {
                gameLog.logVoteTie();
            }
        }

        gameLog.logDayEnd();
    }

    /**
     * 检查游戏是否结束
     */
    private boolean checkGameOver() {
        List<Player> alivePlayers = getAlivePlayers();
        int wolfCount = (int) alivePlayers.stream().filter(Player::isWolf).count();
        int goodCount = alivePlayers.size() - wolfCount;

        // 狼人胜利条件：狼人数量等于或大于好人数量
        if (wolfCount >= goodCount) {
            isGameOver = true;
            winnerCamp = Camp.WOLF;
            return true;
        }

        // 好人胜利条件：所有狼人被淘汰
        if (wolfCount == 0) {
            isGameOver = true;
            winnerCamp = Camp.GOOD;
            return true;
        }

        return false;
    }

    /**
     * 获取存活玩家列表
     */
    private List<Player> getAlivePlayers() {
        return players.stream()
                .filter(Player::isAlive)
                .collect(Collectors.toList());
    }

    /**
     * 获取本晚死亡的玩家列表
     */
    private List<Player> getDeadPlayersThisNight() {
        return players.stream()
                .filter(p -> !p.isAlive())
                .collect(Collectors.toList());
    }

    // getter方法
    public boolean isGameOver() {
        return isGameOver;
    }

    public Camp getWinnerCamp() {
        return winnerCamp;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getDayCount() {
        return dayCount;
    }
}