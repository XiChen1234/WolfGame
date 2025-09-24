package com.wolfgame.strategy;

import com.wolfgame.core.Player;
import com.wolfgame.core.Role;
import java.util.List;
import java.util.Random;

/**
 * 基础逻辑策略类，基于简单逻辑规则选择目标
 */
public class BasicLogicStrategy implements Strategy {
    private final Random random = new Random();
    private final String name = "基础逻辑策略";
    
    @Override
    public Player chooseKillTarget(List<Player> wolves, List<Player> allPlayers) {
        // 优先攻击有技能的好人角色（预言家、女巫、猎人）
        List<Player> skillRoles = allPlayers.stream()
                .filter(p -> !p.isWolf() && p.isAlive() && p.getRole().isHasSkill())
                .toList();
        if (!skillRoles.isEmpty()) {
            return skillRoles.get(random.nextInt(skillRoles.size()));
        }
        
        // 如果没有有技能的好人，则攻击普通村民
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
        // 优先查验未确定身份的玩家（这里简化处理，随机选择非预言家且存活的玩家）
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
        // 如果女巫自己或有技能的好人角色被攻击，则使用解药
        if (victim != null && victim.isAlive()) {
            return victim.equals(witch) || victim.getRole().isHasSkill();
        }
        return false;
    }
    
    @Override
    public Player usePoison(Player witch, List<Player> allPlayers) {
        // 优先毒杀疑似狼人（这里简化处理，随机选择非女巫且存活的玩家）
        List<Player> targets = allPlayers.stream()
                .filter(p -> !p.equals(witch) && p.isAlive())
                .toList();
        if (targets.isEmpty()) {
            return null;
        }
        // 30%的概率使用毒药
        if (random.nextDouble() < 0.3) {
            return targets.get(random.nextInt(targets.size()));
        }
        return null;
    }
    
    @Override
    public Player useGun(Player hunter, boolean isPoisoned, List<Player> allPlayers) {
        // 如果不是被毒死，则开枪
        if (!isPoisoned) {
            // 优先带走疑似狼人（这里简化处理，随机选择非猎人且存活的玩家）
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
        // 狼人优先投票给好人
        if (voter.isWolf()) {
            List<Player> goodPlayers = candidates.stream()
                    .filter(p -> !p.isWolf() && p.isAlive())
                    .toList();
            if (!goodPlayers.isEmpty()) {
                return goodPlayers.get(random.nextInt(goodPlayers.size()));
            }
        }
        
        // 好人随机投票（简化处理）
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