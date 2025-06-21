package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * The HelpopCommand class serves as a staff chat command handler in the application.
 * It allows users with appropriate rank permissions to send messages to a dedicated
 * staff chat channel utilizing "/helpop" or its aliases like "/ac".
 * <p>
 * The command behavior varies based on the type of command sender (console, block, or player).
 * It ensures that only users with a rank equal to or higher than the specified limit can see
 * the messages in the staff chat channel.
 */
@CommandMeta(aliases = "ac", description = "Staff Chat command", rank = Rank.TRAINEE)
public class HelpopCommand extends CoreCommand {

    /**
     * Constructs an instance of the HelpopCommand class.
     * <p>
     * The HelpopCommand class serves as a staff communication tool, allowing the
     * execution of the "/helpop" command, which is an alias for "/ac". This command
     * is used to send messages to the staff chat channel, visible only to users with
     * the required rank permission.
     * <p>
     * Upon execution, the command routes messages from different types of command
     * senders (console, block, or player) to the staff chat channel. It ensures that
     * messages remain restricted to users with sufficient permissions, aiding in
     * streamlined staff communication.
     * <p>
     * The command is initialized with the identifier "helpop" but can also be
     * accessed using other aliases such as "/ac".
     */
    public HelpopCommand() {
        super("helpop");
    }

    /**
     * Handles the execution of the "/ac" command when executed from the console.
     * <p>
     * This method processes the input arguments to construct and send a message
     * to the staff chat channel. It ensures that the command is used correctly and
     * formats the message accordingly for delivery.
     *
     * @param sender The ConsoleCommandSender who initiated the command.
     * @param args An array of strings representing the arguments provided with
     *             the command. These arguments are concatenated to form the
     *             message to be sent.
     * @throws CommandException If an error occurs while executing the command or
     *                          processing the arguments.
     */
    @Override
    protected void handleCommand(ConsoleCommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/ac [Message]");
            return;
        }
        StringBuilder msg = new StringBuilder();
        for (String s : args) {
            msg.append(s).append(" ");
        }
        message("Console", msg.toString());
    }

    /**
     * Handles the execution of the "/ac" command when executed from a command block.
     * <p>
     * This method constructs a message using the provided arguments and appends
     * the location of the block that executed the command. The formatted message
     * is then sent to the staff chat channel, visible only to users with the
     * appropriate permissions.
     *
     * @param sender The BlockCommandSender instance that initiated the command.
     * @param args An array of strings representing the arguments provided with
     *             the command. These arguments are concatenated to form the
     *             message to be sent.
     * @throws CommandException If an error occurs while executing the command
     *                          or processing the arguments.
     */
    @Override
    protected void handleCommand(BlockCommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/ac [Message]");
            return;
        }
        StringBuilder msg = new StringBuilder();
        for (String s : args) {
            msg.append(s).append(" ");
        }
        Location loc = sender.getBlock().getLocation();
        message("CB (x:" + loc.getBlockX() + " y:" + loc.getBlockY() + " z:" + loc.getBlockZ() + ")", msg.toString());
    }

    /**
     * Handles the execution of the "/ac" command when executed by a player.
     * <p>
     * This method processes the provided arguments to construct and send a message
     * to the staff chat channel. If no arguments are provided, a usage message
     * is sent to the player. Otherwise, the arguments are concatenated into a
     * single message and delivered to the staff chat.
     *
     * @param player The player executing the command.
     * @param args An array of strings representing the arguments provided with
     *             the command. These arguments are concatenated to form the
     *             message to be sent.
     * @throws CommandException If an error occurs while executing the command
     *                          or processing the arguments.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/ac [Message]");
            return;
        }
        StringBuilder msg = new StringBuilder();
        for (String s : args) {
            msg.append(s).append(" ");
        }
        message(player.getName(), msg.toString());
    }

    /**
     * Sends a formatted message to all online staff members and logs it in the server console.
     * <p>
     * The method formats the input message and sender details, ensuring it adheres to the
     * staff chat format. The message is delivered only to players with ranks equal to or
     * higher than the required minimum rank. It is also logged in the server console.
     *
     * @param sender The name of the user or entity sending the message.
     * @param message The content of the message to be sent.
     */
    private void message(String sender, String message) {
        String msg = ChatColor.DARK_RED + "[CM CHAT] " + ChatColor.GRAY + sender + ": " + ChatColor.WHITE +
                ChatColor.translateAlternateColorCodes('&', message);
        for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
            if (tp.getRank().getRankId() >= Rank.TRAINEE.getRankId()) {
                tp.sendMessage(msg);
            }
        }
        Bukkit.getLogger().info(msg);
    }
}
