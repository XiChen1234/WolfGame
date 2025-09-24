package com.wolfgame.strategy;

import com.wolfgame.core.Player;
import java.util.List;
import java.util.Random;

/**
 * 随机策略类，随机选择目标或行动，不考虑任何游戏状态
 */
public class RandomStrategy implements Strategy {
    private final Random random = new Random();
    private final String name = "随机策略";
    
    @Override
    public Player chooseKillTarget(List<Player> wolves, List<Player> allPlayers) {
        // 随机选择一个非狼人且存活的玩家
        List<Player> targets = allPlayers.stream()
                .filter(p -> !p.isWolf() && p.isAlive())
                .toList();
        if (targets.isEmpty()) {
            return null;
        }
        return targets.get(random.nextInt(targets.size()));
    }
    
    @Override
    public Player chooseCheckTarget(Player prophet, List<Player> allPlayers) {
        // 随机选择一个非预言家且存活的玩家
        List<Player> targets = allPlayers.stream()
                .filter(p -> !p.equals(prophet) && p.isAlive())
                .toList();
        if (targets.isEmpty()) {
            return null;
        }
        return targets.get(random.nextInt(targets.size()));
    }
    
    @Override
    public boolean useAntidote(Player witch, Player victim, List<Player> allPlayers) {
        // 50%的概率使用解药
        return random.nextBoolean();
    }
    
    @Override
    public Player usePoison(Player witch, List<Player> allPlayers) {
        // 50%的概率使用毒药
        if (random.nextDouble() < 0.5) {
            // 随机选择一个非女巫且存活的玩家
            List<Player> targets = allPlayers.stream()
                    .filter(p -> !p.equals(witch) && p.isAlive())
                    .toList();
            if (targets.isEmpty()) {
                return null;
            }
            return targets.get(random.nextInt(targets.size()));
        }
        return null;
    }
    
    @Override
    public Player useGun(Player hunter, boolean isPoisoned, List<Player> allPlayers) {
        // 如果不是被毒死，有50%的概率开枪
        if (!isPoisoned && random.nextDouble() < 0.5) {
            // 随机选择一个非猎人且存活的玩家
            List<Player> targets = allPlayers.stream()
                    .filter(p -> !p.equals(hunter) && p.isAlive())
                    .toList();
            if (targets.isEmpty()) {
                return null;
            }
            return targets.get(random.nextInt(targets.size()));
        }
        return null;
    }
    
    @Override
    public Player vote(Player voter, List<Player> candidates, List<Player> allPlayers) {
        // 随机选择一个候选玩家
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(random.nextInt(candidates.size()));
    }
    
    @Override
    public String getName() {
        return name;
    }
}