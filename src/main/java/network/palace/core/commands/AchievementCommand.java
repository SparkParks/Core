package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.achievements.CoreAchievement;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Represents a command that allows awarding an achievement to a specific player.
 * <p>
 * This command is designed for use by privileged users with the required rank,
 * and requires the user to specify an achievement ID and a target player's username as arguments.
 * <p>
 * Expected usage format:
 * /ach [ID] [Username]
 * <p>
 * Functionality:
 * <p>
 * - Validates input arguments to ensure proper ID and username format.
 * <p>
 * - Retrieves an achievement by its ID using the system's achievement manager.
 * <p>
 * - Fetches the target player using the system's player manager.
 * <p>
 * - Awards the specified achievement to the target player, if all conditions are met.
 * <p>
 * - Sends appropriate feedback messages to the command sender if any validation fails.
 */
@CommandMeta(description = "Award an achievement to a player", rank = Rank.CM)
public class AchievementCommand extends CoreCommand {

    /**
     * Constructs an AchievementCommand instance.
     * <p>
     * This command is used to award achievements to players. The command is registered with
     * the alias "ach". It requires the user executing the command to have the necessary rank
     * and permissions as specified by the command's metadata.
     */
    public AchievementCommand() {
        super("ach");
    }

    /**
     * Handles the execution of a command to award an achievement to a specified player.
     * <p>
     * This method validates the input arguments provided with the command, retrieves the achievement
     * using its ID, and fetches the player using their username. If valid, the achievement is awarded
     * to the player. Feedback is provided to the sender if input validation fails, the achievement is
     * not found, or the player cannot be located.
     *
     * @param sender the sender executing the command
     * @param args the command arguments, where:
     *             args[0] is expected to be the achievement ID (integer),
     *             args[1] is expected to be the player's username
     * @throws CommandException if an unexpected error occurs during command execution
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "/ach [ID] [Username]");
            return;
        }
        if (!MiscUtil.checkIfInt(args[0])) {
            sender.sendMessage(ChatColor.RED + "/ach [ID] [Username]");
            return;
        }
        Integer id = Integer.parseInt(args[0]);
        CoreAchievement ach = Core.getAchievementManager().getAchievement(id);
        if (ach == null) {
            sender.sendMessage(ChatColor.RED + "There is no achievement with ID " + id + "!");
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        player.giveAchievement(id);
    }
}
