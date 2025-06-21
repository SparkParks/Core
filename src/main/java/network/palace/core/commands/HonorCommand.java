package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.economy.honor.HonorMapping;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

/**
 * The HonorCommand class handles functionality related to managing and querying
 * player honor levels within the system.
 * <p>
 * This includes displaying honor information about players, as well as modifying
 * their honor counts for various purposes.
 * <p>
 * Usage:
 * - Retrieve information about a player's honor level and progress.
 * - Modify a player's honor using commands to add, subtract, or set specific values.
 * <p>
 * Commands:
 * - `/honor [player]`:
 *   Displays the honor level, count, and progress to the next level for the specified player.
 * - `/honor [set|add|minus] [amount] [player]`:
 *   Sets, adds, or subtracts the specified amount of honor for the given player.
 * <p>
 * Command Restrictions:
 * - The `set`, `add`, and `minus` actions require the player to have appropriate permissions.
 * - The syntax of the commands must follow the format specified above to avoid errors.
 * <p>
 * Key Features:
 * - Provides asynchronous handling for querying and updating player data for scalability.
 * - Includes feedback to players and command senders on invalid input or non-existent players.
 * - Supports honor level progression with a view of progress toward the next milestone.
 * <p>
 * Behavior:
 * - If a player is already at the maximum honor level, additional actions display appropriate messages.
 * - The commands differentiate between console, player, and block command senders for flexible execution.
 * <p>
 * Error Handling:
 * - Commands are automatically validated for correct syntax and data type for inputs.
 * - Players not found or honor actions performed without the target player are handled gracefully.
 */
@CommandMeta(description = "Get your current honor count and level", rank = Rank.DEVELOPER)
public class HonorCommand extends CoreCommand {
    /**
     * A formatted decimal number representation.
     * <p>
     * This variable is used to format numeric values
     * into a string representation following the pattern "#,###".
     * The pattern adds thousand separators to numbers.
     * <p>
     * Example formatted value:
     * - Input: 1234
     * - Output: "1,234"
     */
    private final DecimalFormat format = new DecimalFormat("#,###");

    /**
     * Constructs an HonorCommand instance with the command name set to "honor".
     * <p>
     * The HonorCommand is used to execute operations related to the "honor"
     * command functionality. It serves as the initializer for this specific
     * command, defining its name and allowing subsequent handling when the
     * command is executed.
     */
    public HonorCommand() {
        super("honor");
    }

    /**
     * Handles the execution of a generic honor-related command issued by a sender.
     * Provides appropriate responses or actions based on the provided arguments and sender type.
     * Supports operations such as retrieving a player's honor status and modifying it.
     *
     * @param sender The command sender (e.g., Player, Console, or Command Block) issuing the command.
     * @param args   The arguments provided with the command, which determine the operation to be performed.
     *               - If empty, displays the help menu.
     *               - If of length 1, retrieves honor information of the specified player (must be executed by a player).
     *               - If of length 2, modifies the player's honor based on the action and amount (requires the sender to meet additional criteria).
     *               - If of length 3, modifies another player's honor based on the action, the amount, and the source.
     *
     * @throws CommandException If an error occurs during command processing or execution.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        boolean isPlayer = sender instanceof Player;
        if (args.length == 0) {
            helpMenu(sender);
            return;
        }
        if (args.length == 1 && isPlayer) {
            final String user = args[0];
            Core.runTaskAsynchronously(Core.getInstance(), () -> {
                UUID uuid = Core.getMongoHandler().usernameToUUID(user);
                if (uuid == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found");
                    return;
                }
                int honor = Core.getMongoHandler().getHonor(uuid);
                int level = Core.getHonorManager().getLevel(honor).getLevel();
                HonorMapping nextLevel = Core.getHonorManager().getNextLevel(honor);
                float progress = Core.getHonorManager().progressToNextLevel(honor);
                sender.sendMessage(ChatColor.GREEN + args[0] + " is Level " + format(level) + " with " + format(honor) +
                        " Honor.");
                if (level < Core.getHonorManager().getTopLevel()) {
                    sender.sendMessage(ChatColor.GREEN + "They are " + (nextLevel.getHonor() - honor) + " Honor (" +
                            (int) (progress * 100.0f) + "%) away from Level " + nextLevel.getLevel());
                } else {
                    sender.sendMessage(ChatColor.GREEN + "They are at the highest level.");
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
     * Processes the honor-related action for a player based on the specified parameters.
     * <p>
     * Depending on the action specified, this method can set, add, or subtract the player's honor.
     * If the action is not recognized, no operation is performed.
     *
     * @param player The player whose honor is being modified.
     * @param amount The amount of honor to be set, added, or subtracted.
     * @param source The source or context for modifying the player's honor.
     * @param action The action to be performed on the player's honor ("set", "add", or "minus").
     * @return A boolean indicating whether the action was successfully processed.
     *         Returns true if the action is recognized and applied, otherwise false.
     */
    private boolean process(CPlayer player, int amount, String source, String action) {
        switch (action.toLowerCase()) {
            case "set":
                player.setHonor(amount, source);
                return true;
            case "add":
                player.giveHonor(amount, source);
                return true;
            case "minus":
                player.giveHonor(-amount, source);
                return true;
            default:
                return false;
        }
    }

    /**
     * Displays the help menu for the "honor" command to the sender.
     * <p>
     * The help menu provides detailed command usage instructions, including:
     * - Retrieving the honor status of a player.
     * - Adjusting a player's honor (set, add, or subtract).
     *
     * @param sender The entity (e.g., player or console) that issued the command,
     *               which will receive the help menu as a series of messages.
     */
    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW
                + "/honor [player] - Gets the amount of honor a player has.");
        sender.sendMessage(ChatColor.YELLOW
                + "/honor [set,add,minus] [amount] <player> - Changes the amount of honor a player has.");
    }

    /**
     * Formats the provided integer into a string representation using the format instance.
     * <p>
     * The specific formatting applied depends on the configuration of the format
     * object within the class.
     *
     * @param i The integer to be formatted.
     * @return A string representation of the formatted integer.
     */
    private String format(int i) {
        return format.format(i);
    }
}
