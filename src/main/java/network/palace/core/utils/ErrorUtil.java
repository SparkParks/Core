package network.palace.core.utils;

import network.palace.core.Core;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Utility class for displaying runtime errors to certain server members based on their rank.
 * Facilitates sending formatted error messages to players with appropriate permissions.
 */
public class ErrorUtil {

    /**
     * Displays an error message for the given exception, providing a formatted message
     * and stack trace details to online players with sufficient rank for debugging purposes.
     *
     * @param e the exception to display error details for
     * @param plugin the plugin instance associated with the error, used for additional context information
     */
    public static void displayError(Exception e, JavaPlugin plugin) {
        if (shouldStop()) return;
        String exceptionMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
        ArrayList<String> errorInfo = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            StackTraceElement element = e.getStackTrace()[i];
            errorInfo.add(element.getClassName() + "." + element.getMethodName() + "()" + ":" + element.getLineNumber());
        }
        errorInfo.add("\n");
        FormattedMessage message = new FormattedMessage(exceptionMessage).color(ChatColor.RED);
        message.multilineTooltip(ChatColor.RED + "Details (" + plugin.getName() + ")", Arrays.toString(errorInfo.toArray()));
        Core.getPlayerManager().getOnlinePlayers().stream().filter(player -> player.getRank().getRankId() >= Rank.DEVELOPER.getRankId()).forEach(message::send);
//        if (plugin instanceof Plugin) ((Plugin) plugin).getRollbarHandler().error(e);
//        else Core.getInstance().getRollbarHandler().error(e);
    }

    /**
     * Displays an error message for the given exception, providing a formatted
     * message and stack trace details to online players with sufficient rank
     * for debugging purposes.
     *
     * @param e the exception to display error details for
     */
    public static void displayError(Exception e) {
        if (shouldStop()) return;
        String exceptionMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
        ArrayList<String> errorInfo = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            StackTraceElement element = e.getStackTrace()[i];
            errorInfo.add(element.getClassName() + "." + element.getMethodName() + "()" + ":" + element.getLineNumber());
        }
        errorInfo.add("\n");
        FormattedMessage message = new FormattedMessage(exceptionMessage).color(ChatColor.RED);
        message.multilineTooltip(ChatColor.RED + "Details (Core)", Arrays.toString(errorInfo.toArray()));
        Core.getPlayerManager().getOnlinePlayers().stream().filter(player -> player.getRank().getRankId() >= Rank.DEVELOPER.getRankId()).forEach(message::send);
//        Core.getInstance().getRollbarHandler().error(e);
    }

    /**
     * Displays an error message to all online players with sufficient rank and provides
     * debugging information, such as the error details and associated tooltip.
     *
     * @param error the error message to display
     */
    public static void displayError(String error) {
        if (shouldStop()) return;
        FormattedMessage message = new FormattedMessage(error).color(ChatColor.RED);
        message.multilineTooltip(ChatColor.RED + "Details (Core)");
        Core.getPlayerManager().getOnlinePlayers().stream().filter(player -> player.getRank().getRankId() >= Rank.DEVELOPER.getRankId()).forEach(message::send);
//        Core.getInstance().getRollbarHandler().error(error);
    }

    /**
     * Displays an error message to all online players who have sufficient rank
     * and includes additional debugging information with a tooltip.
     *
     * @param error the error message to display
     * @param plugin the plugin instance associated with the error, providing additional context
     */
    public static void displayError(String error, JavaPlugin plugin) {
        if (shouldStop()) return;
        FormattedMessage message = new FormattedMessage(error).color(ChatColor.RED);
        message.multilineTooltip(ChatColor.RED + "Details (" + plugin.getName() + ")");
        Core.getPlayerManager().getOnlinePlayers().stream().filter(player -> player.getRank().getRankId() >= Rank.DEVELOPER.getRankId()).forEach(message::send);
//        Core.getInstance().getRollbarHandler().error(error);
    }

    /**
     * Determines whether the system should stop by evaluating the state of both
     * the dashboard and SQL functionalities.
     *
     * @return {@code true} if both the dashboard and SQL functionalities are disabled;
     *         {@code false} otherwise
     */
    private static boolean shouldStop() {
        return Core.isDashboardAndSqlDisabled();
    }
}
