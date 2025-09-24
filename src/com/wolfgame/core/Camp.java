package com.wolfgame.core;

/**
 * 阵营枚举类，定义游戏中的两大阵营
 */
public enum Camp {
    WOLF("狼人"),  // 狼人阵营
    GOOD("好人");  // 好人阵营
    
    private final String name;
    
    Camp(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}