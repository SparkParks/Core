package network.palace.core.commands.permissions;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * This class represents the "list" command in the application. It allows users to display
 * a list of ranks or rank tags depending on the provided arguments.
 * The command can be executed by both a player and the console, with context-specific handling.
 * <p>
 * Functionality includes:
 * - Displaying a list of ranks and their associated properties such as IDs, database names, and display names.
 * - Displaying a list of rank tags and their associated properties such as database names, chat tags, and scoreboard tags.
 */
@CommandMeta(description = "List ranks")
public class ListCommand extends CoreCommand {

    /**
     * Creates a new instance of the ListCommand.
     * This command is used to list available ranks or rank tags within the application.
     * It serves as a utility to display key attributes of ranks or rank tags when executed.
     * <p>
     * The command inherits its name, "list", from the CoreCommand class.
     */
    public ListCommand() {
        super("list");
    }

    /**
     * Handles the execution of the "list" command when triggered by a player.
     * This method delegates the command handling to a more generic handler, passing the BukkitPlayer instance of the player.
     *
     * @param player The player executing the command, represented as a {@link CPlayer}.
     * @param args The arguments provided with the command, passed as a string array.
     * @throws CommandException If an error occurs during command execution.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        handle(player.getBukkitPlayer(), args);
    }

    /**
     * Handles the execution of the "list" command when triggered by the console.
     * This method delegates the command handling to a generic handler and passes
     * the relevant parameters for processing.
     *
     * @param commandSender The console sender that executed the command, represented as {@link ConsoleCommandSender}.
     * @param args The arguments provided along with the command, passed as a string array.
     * @throws CommandException If an error occurs during the execution of the command.
     */
    @Override
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        handle(commandSender, args);
    }

    /**
     * Handles the execution of the "list" command by a sender, either a player or the console.
     * This method processes the provided arguments to display available ranks or rank tags.
     * The command can show a formatted list of ranks or tags based on the input.
     *
     * @param sender The sender executing the command, which can be a player or console.
     * @param args The arguments provided with the command, used to determine the specific functionality to execute.
     * @throws CommandException If an error occurs during the execution of the command.
     */
    protected void handle(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "- /perm list ranks " + ChatColor.AQUA + "- List ranks");
            sender.sendMessage(ChatColor.GREEN + "- /perm list tags " + ChatColor.AQUA + "- List tags");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "ranks": {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Palace Ranks:");
                sender.sendMessage(ChatColor.GREEN + "- id:dbname:display name");
                for (Rank rank : Rank.values()) {
                    sender.sendMessage(ChatColor.GREEN + "- " + rank.getRankId() + ":" + rank.getDBName() + ":" + rank.getFormattedName());
                }
                break;
            }
            case "tags": {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "Palace Rank Tags:");
                sender.sendMessage(ChatColor.GREEN + "- dbname:chat tag:scoreboard tag");
                for (RankTag tag : RankTag.values()) {
                    if (tag.equals(RankTag.NONE)) continue;
                    sender.sendMessage(ChatColor.GREEN + "- " + tag.getDBName() + ":" + tag.getTag() + ":" + tag.getScoreboardTag());
                }
                break;
            }
        }
    }
}
