package com.wolfgame.core;

/**
 * 角色枚举类，定义游戏中的所有角色类型及其特性
 */
public enum Role {
    WOLF("狼人", Camp.WOLF, true),
    PROPHET("预言家", Camp.GOOD, true),
    WITCH("女巫", Camp.GOOD, true),
    HUNTER("猎人", Camp.GOOD, true),
    VILLAGER("村民", Camp.GOOD, false);
    
    private final String name;     // 角色名称
    private final Camp camp;       // 所属阵营
    private final boolean hasSkill; // 是否有特殊技能
    
    Role(String name, Camp camp, boolean hasSkill) {
        this.name = name;
        this.camp = camp;
        this.hasSkill = hasSkill;
    }
    
    public String getName() {
        return name;
    }
    
    public Camp getCamp() {
        return camp;
    }
    
    public boolean isHasSkill() {
        return hasSkill;
    }
}