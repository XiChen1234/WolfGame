package com.wolfgame.core;

import com.wolfgame.strategy.Strategy;

/**
 * 玩家类，包含玩家基本信息、角色、状态和策略
 */
public class Player {
    private int id;              // 玩家ID
    private String name;         // 玩家名称
    private Role role;           // 角色
    private boolean isAlive;     // 是否存活
    private boolean isPoisoned;  // 是否被女巫毒死（用于猎人技能判断）
    private Strategy strategy;   // AI策略
    
    public Player(int id, String name, Role role, Strategy strategy) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.isAlive = true;
        this.isPoisoned = false;
        this.strategy = strategy;
    }
    
    //  getter和setter方法
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Role getRole() {
        return role;
    }
    
    public boolean isAlive() {
        return isAlive;
    }
    
    public void setAlive(boolean alive) {
        isAlive = alive;
    }
    
    public boolean isPoisoned() {
        return isPoisoned;
    }
    
    public void setPoisoned(boolean poisoned) {
        isPoisoned = poisoned;
    }
    
    public Strategy getStrategy() {
        return strategy;
    }
    
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
    
    /**
     * 获取玩家所属阵营
     */
    public Camp getCamp() {
        return role.getCamp();
    }
    
    /**
     * 判断玩家是否是狼人
     */
    public boolean isWolf() {
        return role == Role.WOLF;
    }
    
    @Override
    public String toString() {
        return name + "(" + role.getName() + ")" + (isAlive ? "[存活]" : "[已出局]");
    }
}