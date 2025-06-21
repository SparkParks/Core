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
 * The TokenCommand class is responsible for managing economy tokens through various commands.
 * It inherits from the CoreCommand class and provides functionality for querying and modifying
 * token balances for players. Tokens can be retrieved or manipulated via specified actions.
 * <p>
 * This class is marked as deprecated and may not be supported in future updates.
 */
@Deprecated
@CommandMeta(description = "Manage economy tokens", rank = Rank.CM)
public class TokenCommand extends CoreCommand {

    /**
     * Constructs a new instance of the TokenCommand class.
     * <p>
     * This command is designed to manage token-related operations within the
     * application's command system. It initializes the command with the name "token"
     * and allows for additional functionality to be implemented for handling specific
     * token-related actions.
     */
    public TokenCommand() {
        super("token");
    }

    /**
     * Handles an unspecific command for managing token-related operations.
     * This method processes the command based on the number of arguments provided,
     * executes the appropriate action (e.g., retrieving token balance, modifying tokens),
     * and sends relevant responses to the command sender.
     *
     * @param sender The entity that issued the command. This can be a player, console, or a block command sender.
     * @param args   The arguments provided with the command. These arguments determine the specific action
     *               to be executed (e.g., querying token balance, adjusting token amounts).
     * @throws CommandException If an error occurs during the command's execution or processing.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        boolean isPlayer = sender instanceof Player;
        if (args.length == 0) {
            if (isPlayer) {
                Core.runTaskAsynchronously(Core.getInstance(), () -> sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD +
                        "Your Tokens: " + ChatColor.GREEN + CurrencyType.TOKENS.getIcon() +
                        Core.getMongoHandler().getCurrency(((Player) sender).getUniqueId(), CurrencyType.TOKENS)));
            } else {
                helpMenu(sender);
            }
            return;
        }
        if (args.length == 1) {
            final String user = args[0];
            Core.runTaskAsynchronously(Core.getInstance(), () -> {
                UUID uuid = Core.getMongoHandler().usernameToUUID(user);
                int tokens = Core.getMongoHandler().getCurrency(uuid, CurrencyType.TOKENS);
                if (uuid != null) {
                    Core.runTask(() -> sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tokens for " +
                            user + ": " + ChatColor.GREEN + CurrencyType.TOKENS.getIcon() + tokens));
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
     * Processes a token adjustment or update for a specified player based on the given action.
     * This method handles operations such as setting, adding, or subtracting tokens.
     *
     * @param player The player whose tokens are being modified.
     * @param amount The amount of tokens to be added, subtracted, or set.
     * @param source The source or reason for the token transaction.
     * @param action The action to perform on the tokens. Expected values are "set", "add", or "minus".
     * @return {@code true} if the action was successfully processed; {@code false} if the action was invalid.
     */
    private boolean process(CPlayer player, int amount, String source, String action) {
        switch (action.toLowerCase()) {
            case "set":
                player.setTokens(amount, source);
                return true;
            case "add":
                player.addTokens(amount, source);
                return true;
            case "minus":
                player.addTokens(-amount, source);
                return true;
            default:
                return false;
        }
    }

    /**
     * Displays the help menu with information about available token-related commands.
     *
     * @param sender The entity that requested the help menu. This can be a player,
     *               console, or another command sender.
     */
    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW
                + "/tokens [player] - Gets the amount of tokens a player has.");
        sender.sendMessage(ChatColor.YELLOW
                + "/tokens [set,add,minus] [amount] <player> - Changes the amount of tokens a player has.");
    }
}
