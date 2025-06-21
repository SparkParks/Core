package network.palace.core.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;

/**
 * The TagToggleCommand is a command that toggles the visibility of username tags
 * on the player's scoreboard. It is a core command accessible to players with the
 * VIP rank or higher.
 * <p>
 * This command allows players to enable or disable the display of username tags
 * according to their preference. When executed, the command modifies the
 * scoreboard settings and provides feedback to the player about the current
 * visibility status of tags.
 */
@CommandMeta(description = "Hide username tags", rank = Rank.VIP)
public class TagToggleCommand extends CoreCommand {

    /**
     * Constructs an instance of the TagToggleCommand class.
     * <p>
     * This command is used to toggle the visibility of username tags on a player's
     * scoreboard. It is designed for players with a VIP rank or higher and allows
     * customization of the tag display based on user preferences.
     * <p>
     * The command uses "tagtoggle" as its identifier and interacts with the player's
     * scoreboard to enable or disable tag visibility, providing feedback on the
     * status change to the player.
     */
    public TagToggleCommand() {
        super("tagtoggle");
    }

    /**
     * Handles the execution of the "tagtoggle" command, toggling the visibility
     * of username tags in the player's scoreboard. Feedback is provided to the
     * player indicating whether the tags are now visible or hidden.
     *
     * @param player the player who executed the command
     * @param args the arguments provided with the command
     * @throws CommandException if an error occurs during the execution of the command
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.getScoreboard().toggleTags();
        if (player.getScoreboard().getTagsVisible()) {
            player.sendMessage(ChatColor.GREEN + "Tags are now visible!");
        } else {
            player.sendMessage(ChatColor.RED + "Tags are no longer visible!");
        }
    }
}
