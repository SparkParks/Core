package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.economy.honor.HonorMapping;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;

/**
 * MyHonorCommand is a command implementation that allows players to retrieve
 * their current honor level and count.
 * <p>
 * It provides information about the player's current honor state, the level
 * they are on, and the progress needed to reach the next level. If the player
 * has reached the highest level, it will notify them accordingly.
 * <p>
 * This command processes the following:
 * - Retrieves and displays the player's current honor count.
 * - Retrieves and displays the player's current honor level.
 * - Calculates and displays the progress required to reach the next level, if applicable.
 * <p>
 * The command ensures formatting for honor counts and levels using a
 * {@link DecimalFormat}.
 * <p>
 * Command name: "myhonor"
 * <p>
 * Extends: CoreCommand
 */
@CommandMeta(description = "Get your current honor count and level")
public class MyHonorCommand extends CoreCommand {
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
     * Constructs a MyHonorCommand instance.
     * <p>
     * This command is named "myhonor" and is used to allow players to view
     * their current honor count, honor level, and progress toward the next level.
     * The command ensures proper formatting of numerical values for better readability.
     */
    public MyHonorCommand() {
        super("myhonor");
    }

    /**
     * Handles the logic for the "myhonor" command, displaying the player's current
     * honor level, honor count, and progress toward the next level.
     *
     * This method retrieves the player's current honor and level, calculates progress
     * toward the next honor level, and sends messages with this information back to the player.
     * If the player is at the highest level, it notifies them accordingly.
     *
     * @param player the player executing the command, whose honor data is being queried
     * @param args   the arguments passed to the command; not used within this method
     * @throws CommandException if an error occurs while handling the command
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        int honor = player.getHonor();
        int level = Core.getHonorManager().getLevel(honor).getLevel();
        HonorMapping nextLevel = Core.getHonorManager().getNextLevel(honor);
        float progress = Core.getHonorManager().progressToNextLevel(honor);
        player.sendMessage(ChatColor.GREEN + "You are Level " + format(level) + " with " + format(honor) +
                " Honor.");
        if (level < Core.getHonorManager().getTopLevel()) {
            player.sendMessage(ChatColor.GREEN + "You are " + (nextLevel.getHonor() - honor) + " Honor (" +
                    (int) (progress * 100.0f) + "%) away from Level " + nextLevel.getLevel());
        } else {
            player.sendMessage(ChatColor.GREEN + "You're at the highest level.");
        }
    }

    /**
     * Formats the given integer value based on a predefined formatting logic.
     *
     * @param i the integer value to be formatted
     * @return a string representation of the formatted integer
     */
    private String format(int i) {
        return format.format(i);
    }
}
