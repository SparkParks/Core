package network.palace.core.commands.disabled;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.messagequeue.packets.EmptyServerPacket;
import network.palace.core.player.CPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.io.IOException;

/**
 * Represents a command to stop the server, specifically designed
 * to restrict the usage of the stop command by players and command blocks.
 * This command ensures that only the console may issue stop commands,
 * while also handling the shutdown process systematically.
 * <p>
 * Extends the functionality of {@link CoreCommand}.
 * <p>
 * Features include:
 * - Initialization of the stop process only from the console.
 * - Gradual server shutdown procedure with world-saving, player notification,
 *   and eventual server termination.
 * - Attempts to gracefully remove online players while monitoring retries.
 * <p>
 * The class overrides command handling for different sender types:
 * - {@code ConsoleCommandSender}: Performs the shutdown operation.
 * - {@code CPlayer}: Sends a disabled message to the player.
 * - {@code BlockCommandSender}: Sends a disabled message to the command block.
 */
@CommandMeta(description = "Disable stop command for players and command blocks")
public class StopCommand extends CoreCommand {

    /**
     * Tracks the number of consecutive attempts to initiate the server shutdown process.
     * <p>
     * This variable is incremented each time the server shutdown task runs
     * and there are still players online. If the number exceeds a predefined limit,
     * the remaining players are forcibly removed before completing the shutdown.
     */
    private int timesTried = 0;

    /**
     * Constructs a new StopCommand instance.
     * <p>
     * The StopCommand is initialized with the name "stop" and is designed to handle
     * the "stop" command functionality within the server environment. Its implementation
     * emphasizes restricting stop command usage to the console, ensuring systematic server shutdown.
     */
    public StopCommand() {
        super("stop");
    }

    /**
     * Handles the execution of the "stop" command, which initiates the server shutdown process.
     * This method is designed to be executed exclusively by the console sender to safely and systematically
     * stop the server, including saving worlds, sending notifications, and kicking players if needed.
     *
     * @param commandSender the console command sender who executes the command
     * @param args the array of arguments provided with the command
     * @throws CommandException if an error occurs while processing the command
     */
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        commandSender.sendMessage(ChatColor.RED + "Shutting the server down...");
        Core.setStarting(true);
        Bukkit.getWorlds().forEach(World::save);
        try {
            Core.getMessageHandler().sendMessage(new EmptyServerPacket(Core.getInstanceName()), Core.getMessageHandler().ALL_PROXIES);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Core.runTaskTimer(Core.getInstance(), () -> {
            if (timesTried >= 5) {
                Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(ChatColor.RED + "Server is stopping. Please rejoin in a few!"));
            }
            if (Bukkit.getOnlinePlayers().size() <= 0) {
                Bukkit.shutdown();
            } else {
                timesTried++;
            }
        }, 40L, 40L);
    }

    /**
     * Handles the "stop" command when issued by a player.
     * This implementation sends a message to the player indicating that the command is disabled.
     *
     * @param player the player who issued the command
     * @param args the array of arguments provided with the command
     * @throws CommandException if an error occurs while processing the command
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.RED + "Disabled");
    }

    /**
     * Handles the execution of the "stop" command when issued from a command block.
     * This implementation sends a message to the command block indicating that
     * the command is disabled.
     *
     * @param commandSender the command block that issued the command
     * @param args the array of arguments provided with the command
     * @throws CommandException if an error occurs while processing the command
     */
    @Override
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {
        commandSender.sendMessage(ChatColor.RED + "Disabled");
    }
}
