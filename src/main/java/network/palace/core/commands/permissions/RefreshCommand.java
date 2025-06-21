package network.palace.core.commands.permissions;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * The RefreshCommand class is a command that refreshes permissions for all ranks.
 * This command can be executed by both player and console command senders.
 * It performs the permission refresh operation asynchronously.
 */
@CommandMeta(description = "Refresh permissions for all ranks")
public class RefreshCommand extends CoreCommand {

    /**
     * Constructs a new RefreshCommand instance.
     * This command is used to refresh permissions for all ranks in the system.
     * The command can be executed by both player and console command senders.
     * The permission refresh operation is performed asynchronously.
     */
    public RefreshCommand() {
        super("refresh");
    }

    /**
     * Handles the execution of the "refresh" command initiated by a player.
     * This method processes the player's input and invokes the corresponding logic to refresh permissions.
     *
     * @param player the player who executed the command
     * @param args the arguments provided by the player when executing the command
     * @throws CommandException if there is an error while executing the command
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        handle(player.getBukkitPlayer(), args);
    }

    /**
     * Handles the execution of the command when triggered by the console command sender.
     * Delegates the processing to a common handler method for shared execution logic.
     *
     * @param commandSender the console sender that executed the command
     * @param args the arguments provided along with the command
     * @throws CommandException if an error occurs during the execution of the command
     */
    @Override
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        handle(commandSender, args);
    }

    /**
     * Handles the execution of the command by both players and console command senders.
     * This method runs the permission refresh operation asynchronously to avoid blocking
     * the main server thread. It provides feedback to the sender before and after the
     * operation is completed.
     *
     * @param sender the sender (player or console) that executed the command
     * @param args the arguments provided with the command
     * @throws CommandException if an error occurs during the execution of the command
     */
    protected void handle(CommandSender sender, String[] args) throws CommandException {
        Core.runTaskAsynchronously(Core.getInstance(), () -> {
            sender.sendMessage(ChatColor.YELLOW + "Refreshing permissions...");
            Core.getPermissionManager().refresh();
            sender.sendMessage(ChatColor.YELLOW + "Permissions refreshed.");
        });
    }
}
