package com.wolfgame.util;

import com.wolfgame.core.Camp;
import com.wolfgame.core.Player;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日志类，负责记录游戏过程和结果
 */
public class Log {
    private int gameId;           // 游戏ID
    private StringBuilder logContent; // 日志内容
    private SimpleDateFormat dateFormat; // 日期格式化
    
    public Log(int gameId) {
        this.gameId = gameId;
        this.logContent = new StringBuilder();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // 记录日志开始时间
        logContent.append("=== 狼人杀游戏 #").append(gameId).append(" 日志 ===\n");
        logContent.append("开始时间: " + dateFormat.format(new Date())).append("\n\n");
    }
    
    /**
     * 记录角色分配信息
     */
    public void logRoleAssignment(List<Player> players) {
        logContent.append("【角色分配】\n");
        for (Player player : players) {
            logContent.append(player.getName()).append(" -> ").append(player.getRole().getName()).append("\n");
        }
        logContent.append("\n");
    }
    
    /**
     * 记录白天开始
     */
    public void logDayStart(int dayCount) {
        logContent.append("===== 第").append(dayCount).append("天 =====\n");
    }
    
    /**
     * 记录夜晚开始
     */
    public void logNightStart() {
        logContent.append("【夜晚】\n");
    }
    
    /**
     * 记录狼人击杀
     */
    public void logWolfKill(Player victim) {
        if (victim != null) {
            logContent.append("狼人选择击杀：").append(victim.getName()).append("\n");
        }
    }
    
    /**
     * 记录预言家查验
     */
    public void logProphetCheck(Player prophet, Player target, boolean isWolf) {
        if (prophet != null && target != null) {
            logContent.append(prophet.getName()).append("查验了").append(target.getName())
                    .append("，结果：").append(isWolf ? "狼人" : "好人").append("\n");
        }
    }
    
    /**
     * 记录女巫救人
     */
    public void logWitchSave(Player victim) {
        if (victim != null) {
            logContent.append("女巫使用了解药，救活了").append(victim.getName()).append("\n");
        }
    }
    
    /**
     * 记录女巫下毒
     */
    public void logWitchPoison(Player target) {
        if (target != null) {
            logContent.append("女巫使用了毒药，毒死了").append(target.getName()).append("\n");
        }
    }
    
    /**
     * 记录猎人开枪
     */
    public void logHunterShoot(Player hunter, Player target) {
        if (hunter != null && target != null) {
            logContent.append(hunter.getName()).append("开枪带走了").append(target.getName()).append("\n");
        }
    }
    
    /**
     * 记录夜晚结束
     */
    public void logNightEnd(List<Player> deadPlayers) {
        if (!deadPlayers.isEmpty()) {
            logContent.append("夜晚结束，").append(
                    deadPlayers.stream()
                            .map(Player::getName)
                            .collect(Collectors.joining(", "))
            ).append(" 出局\n");
        } else {
            logContent.append("夜晚结束，无人出局\n");
        }
        logContent.append("\n");
    }
    
    /**
     * 记录白天讨论开始
     */
    public void logDayStartDiscussion() {
        logContent.append("【白天】\n");
        logContent.append("玩家们开始发言和讨论...\n");
    }
    
    /**
     * 记录投票
     */
    public void logVote(Player voter, Player target) {
        if (voter != null && target != null) {
            logContent.append(voter.getName()).append(" 投票给了 ").append(target.getName()).append("\n");
        }
    }
    
    /**
     * 记录公投结果
     */
    public void logLynching(Player player, int votes) {
        if (player != null) {
            logContent.append(player.getName()).append(" 获得了").append(votes).append("票，被公投出局\n");
        }
    }
    
    /**
     * 记录投票平局
     */
    public void logVoteTie() {
        logContent.append("投票结果平局，无人出局\n");
    }
    
    /**
     * 记录白天结束
     */
    public void logDayEnd() {
        logContent.append("\n");
    }
    
    /**
     * 记录游戏结果
     */
    public void logGameResult(Camp winnerCamp, List<Player> players) {
        logContent.append("=== 游戏结束 ===\n");
        logContent.append("胜利者：").append(winnerCamp.getName()).append("阵营\n");
        
        // 记录存活玩家
        logContent.append("存活玩家：\n");
        List<Player> alivePlayers = players.stream()
                .filter(Player::isAlive)
                .collect(Collectors.toList());
        
        if (!alivePlayers.isEmpty()) {
            for (Player player : alivePlayers) {
                logContent.append(player.getName()).append("(").append(player.getRole().getName()).append(")\n");
            }
        } else {
            logContent.append("无\n");
        }
        
        // 记录结束时间
        logContent.append("结束时间: " + dateFormat.format(new Date())).append("\n");
        logContent.append("====================\n");
    }
    
    /**
     * 将日志保存到文件
     */
    public void saveToFile() {
        File logDir = new File("res/logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        String fileName = "res/logs/log" + gameId + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(logContent.toString());
            System.out.println("游戏日志已保存至：" + fileName);
        } catch (IOException e) {
            System.err.println("保存日志文件失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取日志内容
     */
    public String getLogContent() {
        return logContent.toString();
    }
}