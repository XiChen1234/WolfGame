package com.wolfgame.strategy;

import com.wolfgame.core.Player;
import java.util.List;

/**
 * 策略接口，定义AI决策方法
 */
public interface Strategy {
    /**
     * 狼人选择击杀目标
     * @param wolves 狼人列表
     * @param allPlayers 所有玩家列表
     * @return 被击杀的玩家
     */
    Player chooseKillTarget(List<Player> wolves, List<Player> allPlayers);
    
    /**
     * 预言家选择查验目标
     * @param prophet 预言家玩家
     * @param allPlayers 所有玩家列表
     * @return 被查验的玩家
     */
    Player chooseCheckTarget(Player prophet, List<Player> allPlayers);
    
    /**
     * 女巫选择是否使用解药
     * @param witch 女巫玩家
     * @param victim 夜晚被狼人击杀的玩家
     * @param allPlayers 所有玩家列表
     * @return 是否使用解药
     */
    boolean useAntidote(Player witch, Player victim, List<Player> allPlayers);
    
    /**
     * 女巫选择是否使用毒药及毒杀目标
     * @param witch 女巫玩家
     * @param allPlayers 所有玩家列表
     * @return 被毒杀的玩家，若不使用毒药则返回null
     */
    Player usePoison(Player witch, List<Player> allPlayers);
    
    /**
     * 猎人选择是否开枪及开枪目标
     * @param hunter 猎人玩家
     * @param isPoisoned 是否被毒死
     * @param allPlayers 所有玩家列表
     * @return 被开枪带走的玩家，若不开枪则返回null
     */
    Player useGun(Player hunter, boolean isPoisoned, List<Player> allPlayers);
    
    /**
     * 玩家选择投票目标
     * @param voter 投票玩家
     * @param candidates 候选玩家列表（存活玩家）
     * @param allPlayers 所有玩家列表
     * @return 投票目标玩家
     */
    Player vote(Player voter, List<Player> candidates, List<Player> allPlayers);
    
    /**
     * 获取策略名称
     */
    String getName();
}