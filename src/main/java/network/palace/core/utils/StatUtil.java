package network.palace.core.utils;

import com.comphenix.protocol.utility.MinecraftReflection;
import network.palace.core.Core;
import network.palace.core.messagequeue.packets.LogStatisticPacket;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Utility class for collecting and logging server statistics such as TPS (ticks per second)
 * and online player count. Statistics are collected periodically and sent to a designated
 * logging system.
 *
 * The class encapsulates the initialization of a periodic task that reports statistics,
 * ensuring that appropriate configuration values are set and maintained.
 */
public class StatUtil {

    /**
     * Constructs an instance of the StatUtil class and initializes a periodic task
     * to collect and log server statistics such as ticks per second (TPS) and online
     * player count. The statistics are reported to a designated logging system.
     *
     * This constructor ensures that proper configuration values are checked and maintained
     * before scheduling the periodic task. The timing for the task is fixed at 40 ticks
     * for initial delay and 600 ticks for subsequent executions.
     *
     * @throws IOException if there is an error accessing or saving the configuration file.
     */
    public StatUtil() throws IOException {
        FileConfiguration config = Core.getCoreConfig();
        if (!config.contains("playground")) {
            config.set("playground", false);
            config.save(new File("plugins/Core/config.yml"));
        }
        int production = Core.isPlayground() ? 0 : 1;
        Core.runTaskTimer(Core.getInstance(), () -> {
            try {
                Class<?> minecraftServer = MinecraftReflection.getMinecraftServerClass();
                Object serverInstance = minecraftServer.getDeclaredMethod("getServer").invoke(minecraftServer);
                double[] recentTps = (double[]) serverInstance.getClass().getField("recentTps").get(serverInstance);

                HashMap<String, Object> tags = new HashMap<>();
                tags.put("server_name", Core.getInstanceName());
                tags.put("production", production);

                HashMap<String, Object> values = new HashMap<>();
                values.put("tps", (float) Math.min(Math.round(recentTps[0] * 100.0) / 100.0, 20.0));
                logStatistic("ticks_per_second", tags, values);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException | IOException e) {
                e.printStackTrace();
            }
            try {
                HashMap<String, Object> tags = new HashMap<>();
                tags.put("server_name", Core.getInstanceName());
                tags.put("production", production);

                HashMap<String, Object> values = new HashMap<>();
                values.put("count", Bukkit.getOnlinePlayers().size());

                logStatistic("server_player_count", tags, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 40L, 600L);
    }

    /**
     * Logs statistical data to a designated logging handler by sending a log packet.
     * The data includes a table name and associated tags and values.
     *
     * @param tableName the name of the table where the log data will be stored
     * @param tags a map of tag names to their corresponding values, providing additional metadata
     * @param values a map of statistical data names to their corresponding values
     * @throws IOException if an error occurs during the message handling process
     */
    public void logStatistic(String tableName, HashMap<String, Object> tags, HashMap<String, Object> values) throws IOException {
        Core.getMessageHandler().sendMessage(new LogStatisticPacket(tableName, tags, values), Core.getMessageHandler().STATISTICS);
    }
}
