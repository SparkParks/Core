package network.palace.core.commands.permissions;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.RankTag;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;

/**
 * Represents a command handler for managing tag-related commands.
 * This class extends the CoreCommand and provides functionality for fetching
 * and displaying members associated with a specific tag.
 */
@CommandMeta(description = "Tag commands")
public class TagCommand extends CoreCommand {

    /**
     * Default constructor for the TagCommand class.
     * <p>
     * Constructs a new TagCommand instance with the default name "tag".
     * The command is designed to handle operations related to tags within the application,
     * such as fetching and displaying members associated with specific tags.
     * <p>
     * This constructor calls the superclass constructor to set the name of the command.
     */
    public TagCommand() {
        super("tag");
    }

    /**
     * Handles the execution of a command issued by a specific player.
     * This method wraps the player-specific command handling into the
     * appropriate logic for processing the given command arguments.
     *
     * @param player The player who executed the command. This parameter provides
     *               context such as the command sender and associated data
     *               relevant to the player.
     * @param args   The arguments passed with the command. This array contains
     *               the tokens that accompany the command, usually intended
     *               to specify additional parameters or options.
     * @throws CommandException If an error occurs while processing the command
     *                          or if the command execution fails.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        handle(player.getBukkitPlayer(), args);
    }

    /**
     * Handles the execution of a command issued by the console.
     * This method delegates the specific command handling to a generic handler
     * and passes the relevant parameters for processing.
     *
     * @param commandSender The console sender who issued the command. This represents the context
     *                      of the console as the executor of the command.
     * @param args          The arguments provided with the command. This array contains the tokens
     *                      accompanying the command, representing additional parameters or options.
     * @throws CommandException If an error occurs during the execution of the command or if it fails
     *                          to process the provided arguments properly.
     */
    @Override
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        handle(commandSender, args);
    }

    /**
     * Handles a command sent by a sender, processing the provided arguments to display
     * information about specific members associated with a tag if applicable.
     *
     * If the number of arguments is less than 2 or the command is not specifying "members",
     * a help menu will be shown to the sender. Otherwise, retrieves members associated with
     * the specified tag and sends them to the sender.
     *
     * @param sender The entity sending the command. It can be a player, console, or other valid
     *               command senders in the system.
     * @param args   An array of String arguments accompanying the command. The first argument
     *               specifies the tag, and the second argument should be "members" to proceed.
     * @throws CommandException If an error occurs during the execution of the command.
     */
    protected void handle(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            helpMenu(sender);
            return;
        }
        if (!args[1].equalsIgnoreCase("members")) {
            helpMenu(sender);
            return;
        }
        RankTag tag = RankTag.fromString(args[0]);

        List<String> members = Core.getMongoHandler().getMembers(tag);
        if (members == null) {
            sender.sendMessage(ChatColor.RED + "Too many members to list!");
        } else {
            sender.sendMessage(tag.getTag() + tag.getColor() + "Members (" + members.size() + "):");
            for (String s : members) {
                sender.sendMessage(ChatColor.GREEN + "- " + tag.getColor() + s);
            }
        }
    }

    /**
     * Displays the help menu for tag-related commands to the specified command sender.
     * This method provides a descriptive example of how to use a command for listing
     * players associated with a specific tag, enhancing usability and understanding
     * of the command functionality.
     *
     * @param sender The entity receiving the help menu message. This could be a player,
     *               console, or other types of command senders supported by the system.
     */
    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "/perm tag [tag] members " + ChatColor.AQUA + "- List players with a specific tag");
    }
}
