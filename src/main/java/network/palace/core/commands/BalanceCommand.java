package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * The BalanceCommand class handles economy-related commands for managing player
 * balances. The command supports actions such as querying, setting, adding, or
 * subtracting currency from a player's balance.
 * <p>
 * This command is designed for use by players, console, or block command senders.
 * It includes validation checks for input arguments and handles asynchronous
 * database interaction to retrieve or update player currency balances.
 * <p>
 * The command provides the following functionalities:
 * - Querying the balance of the command sender.
 * - Querying the balance of another player by username.
 * - Modifying the balance of a player using actions (set, add, minus) by amount.
 * <p>
 * This command is marked as deprecated and may be removed or replaced in future updates.
 * It is recommended to avoid using this command in new development.
 */
@Deprecated
@CommandMeta(aliases = "bal", description = "Manage economy balances", rank = Rank.CM)
public class BalanceCommand extends CoreCommand {

    /**
     * Constructs a new BalanceCommand instance.
     * This command is used to handle balance-related actions for players within the system.
     */
    public BalanceCommand() {
        super("balance");
    }

    /**
     * Handles various balance-related actions for a command sender, which can be a player, console,
     * or command block. Based on the provided arguments, the method determines the appropriate action to take,
     * such as retrieving a player's balance, modifying balances, or showing a help menu.
     *
     * @param sender The command sender executing the command. This can be a {@code Player},
     *               {@code ConsoleCommandSender}, or {@code BlockCommandSender}.
     * @param args   The arguments passed along with the command. These determine the specific action:
     *               - No arguments: Display balance for the sender if they are a player, or show help menu otherwise.
     *               - One argument: Retrieve the balance of the specified player.
     *               - Two arguments: Perform a balance-related action affecting the sender.
     *               - Three arguments: Perform a balance-related action affecting another player, providing an action
     *                 type, amount, and identifying source of the request.
     * @throws CommandException If there is an error executing or processing the command.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        boolean isPlayer = sender instanceof Player;
        if (args.length == 0) {
            if (isPlayer) {
                Core.runTaskAsynchronously(Core.getInstance(), () -> sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD +
                        "Your Balance: " + ChatColor.GREEN + CurrencyType.BALANCE.getIcon() +
                        Core.getMongoHandler().getCurrency(((Player) sender).getUniqueId(), CurrencyType.BALANCE)));
            } else {
                helpMenu(sender);
            }
            return;
        }
        if (args.length == 1) {
            final String user = args[0];
            Core.runTaskAsynchronously(Core.getInstance(), () -> {
                UUID uuid = Core.getMongoHandler().usernameToUUID(user);
                int bal = Core.getMongoHandler().getCurrency(uuid, CurrencyType.BALANCE);
                if (uuid != null) {
                    Core.runTask(() -> sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Balance for " +
                            user + ": " + ChatColor.GREEN + CurrencyType.BALANCE.getIcon() + bal));
                } else {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                }
            });
            return;
        }
        if (args.length == 2) {
            if (!isPlayer) {
                helpMenu(sender);
            } else {
                String action = args[0];
                CPlayer tp = Core.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                if (!MiscUtil.checkIfInt(args[1])) {
                    helpMenu(sender);
                    return;
                }
                if (!process(tp, Integer.parseInt(args[1]), tp.getName(), action)) helpMenu(sender);
            }
            return;
        }
        if (args.length == 3) {
            String action = args[0];
            CPlayer tp = Core.getPlayerManager().getPlayer(Bukkit.getPlayer(args[2]));
            if (tp == null) {
                sender.sendMessage(ChatColor.GREEN + args[2] + ChatColor.RED + " is not online!");
                return;
            }
            if (!MiscUtil.checkIfInt(args[1])) {
                helpMenu(sender);
                return;
            }
            String source;
            if (sender instanceof BlockCommandSender) {
                BlockCommandSender s = (BlockCommandSender) sender;
                Location loc = s.getBlock().getLocation();
                source = "Command Block x: " + loc.getBlockX() + " y: " + loc.getBlockY() + " z: " + loc.getBlockZ();
            } else {
                source = sender instanceof Player ? sender.getName() : "Console";
            }
            if (!process(tp, Integer.parseInt(args[1]), source, action)) helpMenu(sender);
            return;
        }
        helpMenu(sender);
    }

    /**
     * Processes a balance-related action for a specific player. Determines the type of
     * action to be performed based on the provided action parameter, such as setting,
     * adding, or subtracting a balance amount.
     *
     * @param player The player for whom the balance action is being performed.
     * @param amount The amount to set, add, or subtract from the player's balance.
     * @param source The source or reason for the balance modification.
     * @param action The action to perform. Valid values are "set", "add", or "minus".
     * @return {@code true} if the action was successfully processed;
     *         {@code false} if the action was invalid.
     */
    private boolean process(CPlayer player, int amount, String source, String action) {
        switch (action.toLowerCase()) {
            case "set":
                player.setBalance(amount, source);
                return true;
            case "add":
                player.addBalance(amount, source);
                return true;
            case "minus":
                player.addBalance(-amount, source);
                return true;
            default:
                return false;
        }
    }

    /**
     * Displays a help menu with information about available balance-related commands
     * to the specified command sender. This includes command usage details for fetching,
     * setting, adding, or subtracting a player's balance.
     *
     * @param sender The command sender receiving the help menu. This can be a {@code Player},
     *               {@code ConsoleCommandSender}, or {@code BlockCommandSender}. The output
     *               is sent directly to the sender.
     */
    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW
                + "/balance [player] - Gets the amount of money a player has.");
        sender.sendMessage(ChatColor.YELLOW
                + "/balance [set,add,minus] [amount] <player> - Changes the amount of money a player has.");
    }
}
