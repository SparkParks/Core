package network.palace.core.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.economy.BankMenu;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * BankCommand is a command that allows players to view their bank menu.
 * This command is designed to be executed by players only.
 * <p>
 * The command performs the following:
 * - Opens the Bank Menu for the player who executes the command.
 * - Restricts execution of the command from the console or block command senders
 *   by sending an appropriate error message.
 * <p>
 * Extends:
 * - CoreCommand: Provides the base implementation for server commands.
 * <p>
 * Methods:
 * - `handleCommand(CPlayer player, String[] args)`: Handles the command execution for player input.
 * - `handleCommand(ConsoleCommandSender commandSender, String[] args)`: Restricts console command usage.
 * - `handleCommand(BlockCommandSender commandSender, String[] args)`: Restricts block command usage.
 */
@CommandMeta(description = "Allows you to view your bank")
public class BankCommand extends CoreCommand {

    /**
     * Constructs a new BankCommand instance.
     * <p>
     * This command allows players to access their bank menu
     * within the game. The command is designed to ensure
     * it is executed only by players, restricting usage
     * from console or block command senders.
     */
    public BankCommand() {
        super("bank");
    }

    /**
     * Handles the execution of the bank command for a player.
     * <p>
     * This method opens the bank menu for the player who issued the command.
     * It ensures that the player receives a graphical interface (BankMenu)
     * to interact with their bank.
     *
     * @param player The player who executed the command. Must not be null.
     * @param args The arguments supplied with the command. Can be empty or null
     *             depending on the command invocation.
     * @throws CommandException If an error occurs while processing the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        BankMenu inv = new BankMenu(player);
        inv.openMenu();
    }

    /**
     * Handles the execution of the command when sent by a console command sender.
     * <p>
     * This method ensures that the command is restricted to players only
     * and informs the console sender that the command cannot be used
     * from the console.
     *
     * @param commandSender The sender of the command, specifically a console command sender. Must not be null.
     * @param args The arguments supplied with the command. Can be empty or null depending on the command invocation.
     * @throws CommandException If an error occurs while processing the command.
     */
    @Override
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        commandSender.sendMessage(ChatColor.RED + "This command can only be used by players");
    }

    /**
     * Handles the execution of the command when sent by a block command sender.
     * <p>
     * This method restricts usage of the command to players only and informs
     * the block command sender that they are not permitted to execute the command.
     *
     * @param commandSender The sender of the command, specifically a block command sender. Must not be null.
     * @param args The arguments supplied with the command. Can be empty or null depending on the command invocation.
     * @throws CommandException If an error occurs while processing the command.
     */
    @Override
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {
        commandSender.sendMessage(ChatColor.RED + "This command can only be used by players");
    }
}
