package com.wolfgame.stat;

import com.wolfgame.core.Camp;
import com.wolfgame.core.Game;
import com.wolfgame.core.Player;
import com.wolfgame.core.Role;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 统计类，负责收集和分析游戏数据
 */
public class Statistics {
    private int totalGames;                 // 总游戏轮数
    private Map<Camp, Integer> winCount;    // 各阵营胜利次数
    private Map<Role, Integer> survivalCount; // 各角色存活次数
    private Map<Role, Integer> killCount;   // 各角色击杀次数
    private Map<Role, Double> accuracyRate; // 各角色准确率（如预言家查验准确率）
    private List<Integer> gameDuration;     // 每局游戏天数
    private SimpleDateFormat dateFormat;    // 日期格式化
    
    public Statistics() {
        this.totalGames = 0;
        this.winCount = new HashMap<>();
        this.survivalCount = new HashMap<>();
        this.killCount = new HashMap<>();
        this.accuracyRate = new HashMap<>();
        this.gameDuration = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // 初始化统计数据
        for (Camp camp : Camp.values()) {
            winCount.put(camp, 0);
        }
        
        for (Role role : Role.values()) {
            survivalCount.put(role, 0);
            killCount.put(role, 0);
            accuracyRate.put(role, 0.0);
        }
    }
    
    /**
     * 收集一局游戏的数据
     */
    public void collectGameData(Game game) {
        if (!game.isGameOver()) {
            return;
        }
        
        totalGames++;
        
        // 记录胜利阵营
        Camp winnerCamp = game.getWinnerCamp();
        winCount.put(winnerCamp, winCount.getOrDefault(winnerCamp, 0) + 1);
        
        // 记录游戏天数
        gameDuration.add(game.getDayCount());
        
        // 统计角色数据
        for (Player player : game.getPlayers()) {
            Role role = player.getRole();
            
            // 统计存活次数
            if (player.isAlive()) {
                survivalCount.put(role, survivalCount.getOrDefault(role, 0) + 1);
            }
            
            // 这里简化处理击杀统计，实际项目中可能需要更复杂的逻辑
            if (role == Role.WOLF) {
                killCount.put(role, killCount.getOrDefault(role, 0) + 1);
            }
        }
        
        // 简化处理准确率统计（实际项目中需要记录具体的查验结果等）
    }
    
    /**
     * 生成统计报告
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("===== 狼人杀游戏模拟统计报告 =====\n");
        report.append("生成时间: " + dateFormat.format(new Date())).append("\n");
        report.append("总游戏轮数: " + totalGames).append("\n\n");
        
        // 阵营胜率统计
        report.append("【阵营胜率统计】\n");
        for (Camp camp : Camp.values()) {
            int count = winCount.getOrDefault(camp, 0);
            double rate = totalGames > 0 ? (double) count / totalGames * 100 : 0;
            report.append(camp.getName()).append("阵营: 胜利").append(count).append("次, 胜率: ")
                    .append(String.format("%.2f", rate)).append("%\n");
        }
        report.append("\n");
        
        // 角色存活率统计
        report.append("【角色存活率统计】\n");
        for (Role role : Role.values()) {
            int count = survivalCount.getOrDefault(role, 0);
            double rate = totalGames > 0 ? (double) count / totalGames * 100 : 0;
            report.append(role.getName()).append(": 存活").append(count).append("次, 存活率: ")
                    .append(String.format("%.2f", rate)).append("%\n");
        }
        report.append("\n");
        
        // 游戏时长统计
        if (!gameDuration.isEmpty()) {
            report.append("【游戏时长统计】\n");
            OptionalDouble avgDuration = gameDuration.stream().mapToInt(Integer::intValue).average();
            OptionalInt maxDuration = gameDuration.stream().mapToInt(Integer::intValue).max();
            OptionalInt minDuration = gameDuration.stream().mapToInt(Integer::intValue).min();
            
            report.append("平均天数: ")
                    .append(avgDuration.isPresent() ? String.format("%.2f", avgDuration.getAsDouble()) : "0")
                    .append("天\n");
            report.append("最长天数: ")
                    .append(maxDuration.isPresent() ? maxDuration.getAsInt() : 0)
                    .append("天\n");
            report.append("最短天数: ")
                    .append(minDuration.isPresent() ? minDuration.getAsInt() : 0)
                    .append("天\n");
        }
        report.append("\n");
        
        report.append("==============================\n");
        
        return report.toString();
    }
    
    /**
     * 保存统计报告到文件
     */
    public void saveReportToFile() {
        String report = generateReport();
        File reportDir = new File("res/");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }
        String fileName = "res/report.txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(report);
            System.out.println("统计报告已保存至：" + fileName);
        } catch (IOException e) {
            System.err.println("保存统计报告失败：" + e.getMessage());
        }
    }
    
    /**
     * 打印统计报告到控制台
     */
    public void printReport() {
        System.out.println(generateReport());
    }
    
    // getter方法
    public int getTotalGames() {
        return totalGames;
    }
    
    public Map<Camp, Integer> getWinCount() {
        return winCount;
    }
    
    public Map<Role, Integer> getSurvivalCount() {
        return survivalCount;
    }
    
    public Map<Role, Integer> getKillCount() {
        return killCount;
    }
    
    public List<Integer> getGameDuration() {
        return gameDuration;
    }
}