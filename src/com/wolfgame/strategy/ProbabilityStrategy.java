package com.wolfgame.strategy;

import com.wolfgame.core.Player;
import com.wolfgame.core.Role;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 概率策略类，根据概率分布选择行动，特别是预言家会根据可疑度概率选择查验对象
 */
public class ProbabilityStrategy implements Strategy {
    private final Random random = new Random();
    private final String name = "概率策略";
    // 存储每个玩家的可疑度，值越高越可疑
    private final Map<Player, Double> suspicionLevel = new HashMap<>();
    
    @Override
    public Player chooseKillTarget(List<Player> wolves, List<Player> allPlayers) {
        // 狼人根据概率选择目标，优先攻击有技能的好人
        List<Player> skillRoles = allPlayers.stream()
                .filter(p -> !p.isWolf() && p.isAlive() && p.getRole().isHasSkill())
                .toList();
        
        if (!skillRoles.isEmpty()) {
            // 70%概率攻击有技能的角色
            if (random.nextDouble() < 0.7) {
                return skillRoles.get(random.nextInt(skillRoles.size()));
            }
        }
        
        // 30%概率攻击普通村民
        List<Player> villagers = allPlayers.stream()
                .filter(p -> !p.isWolf() && p.isAlive() && p.getRole() == Role.VILLAGER)
                .toList();
        if (!villagers.isEmpty()) {
            return villagers.get(random.nextInt(villagers.size()));
        }
        
        return null;
    }
    
    @Override
    public Player chooseCheckTarget(Player prophet, List<Player> allPlayers) {
        // 预言家根据可疑度概率选择查验对象
        initializeSuspicionLevel(allPlayers);
        
        List<Player> targets = allPlayers.stream()
                .filter(p -> !p.equals(prophet) && p.isAlive())
                .toList();
        
        if (targets.isEmpty()) {
            return null;
        }
        
        // 计算总可疑度
        double totalSuspicion = targets.stream()
                .mapToDouble(p -> suspicionLevel.getOrDefault(p, 1.0))
                .sum();
        
        // 根据可疑度概率选择目标
        double randomValue = random.nextDouble() * totalSuspicion;
        double currentSum = 0;
        
        for (Player target : targets) {
            currentSum += suspicionLevel.getOrDefault(target, 1.0);
            if (randomValue <= currentSum) {
                // 增加被查验玩家的可疑度
                suspicionLevel.put(target, suspicionLevel.getOrDefault(target, 1.0) + 0.5);
                return target;
            }
        }
        
        // 如果有问题，返回随机目标
        return targets.get(random.nextInt(targets.size()));
    }
    
    @Override
    public boolean useAntidote(Player witch, Player victim, List<Player> allPlayers) {
        // 80%概率救自己，60%概率救有技能的好人，30%概率救村民
        if (victim != null && victim.isAlive()) {
            if (victim.equals(witch)) {
                return random.nextDouble() < 0.8;
            } else if (victim.getRole().isHasSkill()) {
                return random.nextDouble() < 0.6;
            } else {
                return random.nextDouble() < 0.3;
            }
        }
        return false;
    }
    
    @Override
    public Player usePoison(Player witch, List<Player> allPlayers) {
        // 40%概率使用毒药，并优先毒杀可疑度高的玩家
        if (random.nextDouble() < 0.4) {
            initializeSuspicionLevel(allPlayers);
            
            List<Player> targets = allPlayers.stream()
                    .filter(p -> !p.equals(witch) && p.isAlive())
                    .toList();
            
            if (targets.isEmpty()) {
                return null;
            }
            
            // 选择可疑度最高的玩家
            Player mostSuspicious = targets.get(0);
            double maxSuspicion = suspicionLevel.getOrDefault(mostSuspicious, 1.0);
            
            for (Player target : targets) {
                double suspicion = suspicionLevel.getOrDefault(target, 1.0);
                if (suspicion > maxSuspicion) {
                    maxSuspicion = suspicion;
                    mostSuspicious = target;
                }
            }
            
            return mostSuspicious;
        }
        return null;
    }
    
    @Override
    public Player useGun(Player hunter, boolean isPoisoned, List<Player> allPlayers) {
        // 如果不是被毒死，80%概率开枪，并优先带走可疑度高的玩家
        if (!isPoisoned && random.nextDouble() < 0.8) {
            initializeSuspicionLevel(allPlayers);
            
            List<Player> targets = allPlayers.stream()
                    .filter(p -> !p.equals(hunter) && p.isAlive())
                    .toList();
            
            if (targets.isEmpty()) {
                return null;
            }
            
            // 选择可疑度最高的玩家
            Player mostSuspicious = targets.get(0);
            double maxSuspicion = suspicionLevel.getOrDefault(mostSuspicious, 1.0);
            
            for (Player target : targets) {
                double suspicion = suspicionLevel.getOrDefault(target, 1.0);
                if (suspicion > maxSuspicion) {
                    maxSuspicion = suspicion;
                    mostSuspicious = target;
                }
            }
            
            return mostSuspicious;
        }
        return null;
    }
    
    @Override
    public Player vote(Player voter, List<Player> candidates, List<Player> allPlayers) {
        // 根据可疑度概率投票
        initializeSuspicionLevel(allPlayers);
        
        if (candidates.isEmpty()) {
            return null;
        }
        
        // 狼人优先投好人，好人优先投可疑度高的玩家
        List<Player> filteredCandidates = candidates;
        if (voter.isWolf()) {
            filteredCandidates = candidates.stream()
                    .filter(p -> !p.isWolf() && p.isAlive())
                    .toList();
        }
        
        if (filteredCandidates.isEmpty()) {
            return candidates.get(random.nextInt(candidates.size()));
        }
        
        // 计算总可疑度
        double totalSuspicion = filteredCandidates.stream()
                .mapToDouble(p -> suspicionLevel.getOrDefault(p, 1.0))
                .sum();
        
        // 根据可疑度概率选择目标
        double randomValue = random.nextDouble() * totalSuspicion;
        double currentSum = 0;
        
        for (Player candidate : filteredCandidates) {
            currentSum += suspicionLevel.getOrDefault(candidate, 1.0);
            if (randomValue <= currentSum) {
                return candidate;
            }
        }
        
        return filteredCandidates.get(random.nextInt(filteredCandidates.size()));
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * 初始化所有玩家的可疑度
     */
    private void initializeSuspicionLevel(List<Player> allPlayers) {
        for (Player player : allPlayers) {
            if (!suspicionLevel.containsKey(player)) {
                suspicionLevel.put(player, 1.0); // 初始可疑度为1.0
            }
        }
    }
}