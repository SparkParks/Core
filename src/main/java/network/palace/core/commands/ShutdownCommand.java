package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.messagequeue.packets.EmptyServerPacket;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.io.IOException;

/**
 * A command for managing the server shutdown process, allowing for scheduled shutdowns
 * with optional delays or immediate cancellation of any ongoing shutdown.
 * This command is restricted to users with the appropriate rank.
 * <p>
 * Features:
 * - Schedule a server shutdown with a specified delay in seconds.
 * - Broadcast countdown messages to players at specific intervals before shutdown.
 * - Cancel a pending shutdown.
 * - Automatically saves all worlds and attempts to notify proxies before shutting down.
 */
@CommandMeta(description = "Safely stop the server.", aliases = "sd", rank = Rank.DEVELOPER)
public class ShutdownCommand extends CoreCommand {
    /**
     * Represents the unique identifier for a task associated with the execution
     * of the command. This variable is used internally to track and manage tasks
     * initiated by the command.
     */
    private int taskID = 0;

    /**
     * Constructs a new instance of the ShutdownCommand.
     * <p>
     * This command is used to initiate a server shutdown sequence. It is typically
     * executed by administrators or by processes requiring controlled server termination.
     * <p>
     * The command is initialized with the name "shutdown" to serve as its identifier
     * in the command system.
     */
    public ShutdownCommand() {
        super("shutdown");
    }

    /**
     * Handles the execution of the "shutdown" command with various arguments.
     * This method processes commands for scheduling or canceling a server shutdown.
     *
     * @param sender The entity or player who issued the command.
     * @param args The arguments provided with the command.
     *             - If empty, displays usage information.
     *             - If the first argument is "cancel", cancels a pending shutdown if any.
     *             - If the first argument is a number, schedules a shutdown after the specified delay (in seconds).
     * @throws CommandException If an error occurs during the execution of the command.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/shutdown [delay]");
            sender.sendMessage(ChatColor.RED + "/shutdown cancel " + ChatColor.AQUA + "- Cancel existing shutdown");
            return;
        }
        if (args[0].equalsIgnoreCase("cancel")) {
            if (taskID == 0) {
                sender.sendMessage(ChatColor.RED + "No shutdown is pending!");
                return;
            }
            Core.cancelTask(taskID);
            taskID = 0;
            sender.sendMessage(ChatColor.GREEN + "Shutdown cancelled!");
            return;
        }
        if (taskID != 0) {
            sender.sendMessage(ChatColor.RED + "A shutdown is already pending! Run '/shutdown cancel' to cancel it.");
            return;
        }
        final int delay;
        try {
            delay = Integer.parseInt(args[0]);
        } catch (NumberFormatException ignored) {
            sender.sendMessage(ChatColor.RED + "/shutdown [delay]");
            return;
        }
        sender.sendMessage(ChatColor.RED + "Shutting the server down in " + delay + " seconds...");
        taskID = Core.runTaskTimer(Core.getInstance(), new Runnable() {
            int i = delay, count = 19;

            @Override
            public void run() {
                if (++count % 20 != 0) return;
                count = 0;
                if (i > 0) {
                    message(i);
                    i--;
                    return;
                }
                if (i-- < 0) return;
                Core.setStarting(true);
                Bukkit.getWorlds().forEach(World::save);
                try {
                    Core.getMessageHandler().sendMessage(new EmptyServerPacket(Core.getInstanceName()), Core.getMessageHandler().ALL_PROXIES);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Core.runTaskTimer(Core.getInstance(), () -> {
                    if (Bukkit.getOnlinePlayers().size() <= 0) {
                        Bukkit.shutdown();
                    }
                }, 40L, 40L);
            }
        }, 0L, 1L);
    }

    /**
     * Broadcasts a server restart message to all players if the specified time in seconds
     * corresponds to specific intervals (e.g., 1 minute, 30 seconds, etc.).
     * The message includes the name of the server and a countdown until restart.
     *
     * @param seconds The time in seconds until the restart. The method only broadcasts a message
     *                if the value is exactly 30 seconds, 60 seconds, or a multiple of 60 seconds
     *                (e.g., 2 minutes or more).
     */
    public void message(int seconds) {
        if (seconds <= 0) {
            return;
        }
        String time;
        if (seconds > 60 && seconds % 60 == 0) {
            time = seconds / 60 + " minutes";
        } else if (seconds == 60) {
            time = "1 minute";
        } else if (seconds == 30) {
            time = "30 seconds";
        } else {
            return;
        }
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "[Server] " + ChatColor.GREEN + "This server (" + Core.getInstanceName() + ") will restart in " + time);
    }
}
