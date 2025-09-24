import com.wolfgame.core.Game;
import com.wolfgame.core.Role;
import com.wolfgame.stat.Statistics;
// import com.wolfgame.strategy.BasicLogicStrategy;
// import com.wolfgame.strategy.ProbabilityStrategy;
import com.wolfgame.strategy.RandomStrategy;
import com.wolfgame.strategy.Strategy;
import java.util.HashMap;
import java.util.Map;

/**
 * 程序入口类，负责解析命令行参数、初始化模拟器、运行多轮游戏并生成统计报告
 */
public class Main {
    
    public static void main(String[] args) {
        // 解析命令行参数，设置默认值
        int totalRounds = parseTotalRounds(args);
        long randomSeed = parseRandomSeed(args);
        
        System.out.println("=== 狼人杀游戏模拟器 ===");
        System.out.println("模拟轮数: " + totalRounds);
        System.out.println("随机种子: " + randomSeed);
        System.out.println("开始模拟游戏...");
        
        // 初始化统计类
        Statistics statistics = new Statistics();
        
        // 配置角色策略映射（这里使用默认策略配置，可以通过命令行参数调整）
        Map<Role, Strategy> roleStrategies = getDefaultStrategies();
        
        // 运行多轮游戏
        for (int i = 1; i <= totalRounds; i++) {
            // 每轮使用不同的随机种子，但保持一定的可预测性
            long seed = randomSeed + i;
            
            // 创建并运行游戏
            Game game = new Game(i, roleStrategies, seed);
            game.run();
            
            // 收集游戏数据
            statistics.collectGameData(game);
            
            // 打印进度
            if (i % 100 == 0 || i == totalRounds) {
                System.out.println("已完成 " + i + "/" + totalRounds + " 轮游戏");
            }
        }
        
        // 生成并输出统计报告
        statistics.printReport();
        statistics.saveReportToFile();
        
        System.out.println("\n模拟完成！所有游戏日志和统计报告已保存至logs文件夹。");
    }
    
    /**
     * 解析命令行参数，获取模拟轮数
     */
    private static int parseTotalRounds(String[] args) {
        int defaultRounds = 10;
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("--rounds") && i + 1 < args.length) {
                    try {
                        return Integer.parseInt(args[i + 1]);
                    } catch (NumberFormatException e) {
                        System.err.println("警告：无效的轮数参数，使用默认值：" + defaultRounds);
                    }
                }
            }
        }
        return defaultRounds;
    }
    
    /**
     * 解析命令行参数，获取随机种子
     */
    private static long parseRandomSeed(String[] args) {
        long defaultSeed = System.currentTimeMillis();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("--seed") && i + 1 < args.length) {
                    try {
                        return Long.parseLong(args[i + 1]);
                    } catch (NumberFormatException e) {
                        System.err.println("警告：无效的随机种子参数，使用默认值");
                    }
                }
            }
        }
        return defaultSeed;
    }
    
    /**
     * 获取默认的角色策略配置
     */
    private static Map<Role, Strategy> getDefaultStrategies() {
        Map<Role, Strategy> strategies = new HashMap<>();
        
        // 为不同角色分配不同策略
        // strategies.put(Role.WOLF, new BasicLogicStrategy());
        // strategies.put(Role.PROPHET, new ProbabilityStrategy());
        // strategies.put(Role.WITCH, new BasicLogicStrategy());
        // strategies.put(Role.HUNTER, new BasicLogicStrategy());
        // strategies.put(Role.VILLAGER, new RandomStrategy());
        
        strategies.put(Role.WOLF, new RandomStrategy());
        strategies.put(Role.PROPHET, new RandomStrategy());
        strategies.put(Role.WITCH, new RandomStrategy());
        strategies.put(Role.HUNTER, new RandomStrategy());
        strategies.put(Role.VILLAGER, new RandomStrategy());
        
        return strategies;
    }
}
