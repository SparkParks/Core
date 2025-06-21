package network.palace.core.commands.permissions;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Represents a command that manages rank-based permissions and information.
 * This command allows for operations such as getting, setting, unsetting,
 * testing permissions for a given rank, and listing members of a rank.
 * It is designed to be accessed by both players and console command senders.
 */
@CommandMeta(description = "Rank commands")
public class RankCommand extends CoreCommand {

    /**
     * Constructs a new RankCommand instance.
     * This command is used to manage or display rank-related information within the application.
     * <p>
     * The command inherits its primary name, "rank", from the CoreCommand superclass.
     */
    public RankCommand() {
        super("rank");
    }

    /**
     * Handles the execution of the "rank" command when invoked by a player.
     * This method processes the command input provided by the player and delegates
     * the handling to another method for further processing.
     *
     * @param player the CPlayer object representing the player who executed the command
     * @param args the arguments provided by the player with the command
     * @throws CommandException if an error occurs during command execution
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        handle(player.getBukkitPlayer(), args);
    }

    /**
     * Handles the execution of a command initiated by the console.
     * This method delegates the command processing to the {@link #handle} method.
     *
     * @param commandSender the ConsoleCommandSender instance representing the sender of the command
     * @param args the arguments provided with the command
     * @throws CommandException if an error occurs during the command execution
     */
    @Override
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        handle(commandSender, args);
    }

    /**
     * Handles the execution of the rank command for a given sender.
     * Processes various subcommands such as "get", "set", "unset", "test", and "members"
     * to manage or retrieve rank-related permissions and information.
     *
     * @param sender the sender who executed the command, either a player or console
     * @param args the command arguments provided by the sender
     * @throws CommandException if an error occurs during command execution
     */
    protected void handle(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            helpMenu(sender);
            return;
        }
        Rank rank = Rank.fromString(args[0]);
        switch (args[1].toLowerCase()) {
            case "get": {
                if (args.length < 3) {
                    helpMenu(sender);
                    return;
                }
                String node = args[2];
                Map<String, Boolean> perms = Core.getPermissionManager().getPermissions(rank);
                if (!perms.containsKey(node)) {
                    sender.sendMessage(ChatColor.YELLOW + "Rank " + rank.getFormattedName() + ChatColor.YELLOW + " doesn't set a value for " + ChatColor.AQUA + node);
                    return;
                }
                boolean val = perms.getOrDefault(node, false);
                if (val) {
                    sender.sendMessage(ChatColor.GREEN + "Rank " + rank.getFormattedName() + ChatColor.GREEN + " sets true for " + ChatColor.AQUA + node);
                } else {
                    sender.sendMessage(ChatColor.RED + "Rank " + rank.getFormattedName() + ChatColor.RED + " sets false for " + ChatColor.AQUA + node);
                }
                return;
            }
            case "set": {
                if (args.length < 3) {
                    helpMenu(sender);
                    return;
                }
                String node = args[2];
                boolean val;
                if (args.length > 3) {
                    val = Boolean.parseBoolean(args[3]);
                } else {
                    val = true;
                }
                Core.runTaskAsynchronously(Core.getInstance(), () -> {
                    Core.getMongoHandler().setPermission(node, rank, val);
                    Core.getPermissionManager().refresh();
                    if (val) {
                        sender.sendMessage(ChatColor.GREEN + "Rank " + rank.getFormattedName() + ChatColor.GREEN + " now sets true for " + ChatColor.AQUA + node);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Rank " + rank.getFormattedName() + ChatColor.RED + " now sets false for " + ChatColor.AQUA + node);
                    }
                });
                return;
            }
            case "unset": {
                if (args.length < 3) {
                    helpMenu(sender);
                    return;
                }
                String node = args[2];
                Core.runTaskAsynchronously(Core.getInstance(), () -> {
                    Core.getMongoHandler().unsetPermission(node, rank);
                    Core.getPermissionManager().refresh();
                    sender.sendMessage(ChatColor.YELLOW + "Rank " + rank.getFormattedName() + ChatColor.YELLOW + " no longer sets " + ChatColor.AQUA + node);
                });
                return;
            }
            case "test": {
                if (args.length < 3) {
                    helpMenu(sender);
                    return;
                }
                String node = args[2];
                Map<String, Boolean> perms = Core.getPermissionManager().getPermissions(rank);
                boolean val;
                if (perms.containsKey(node)) {
                    val = perms.get(node);
                } else {
                    Permission perm = Bukkit.getServer().getPluginManager().getPermission(node);
                    if (perm == null) {
                        val = false;
                    } else {
                        val = perm.getDefault().getValue(rank.isOp());
                    }
                }
                sender.sendMessage(ChatColor.YELLOW + "Rank " + rank.getFormattedName() + (val ? ChatColor.GREEN : ChatColor.RED) +
                        " does" + (val ? "" : "n't") + " have this permission");
                return;
            }
            case "members": {
                List<Rank> blocked = Arrays.asList(Rank.GUEST, Rank.PASSHOLDER);
                List<String> members = null;
                if (!blocked.contains(rank)) {
                    members = Core.getMongoHandler().getMembers(rank);
                }
                if (members == null) {
                    sender.sendMessage(ChatColor.RED + "Too many members to list!");
                } else {
                    sender.sendMessage(rank.getFormattedName() + " Members (" + members.size() + "):");
                    for (String s : members) {
                        sender.sendMessage(ChatColor.GREEN + "- " + rank.getTagColor() + s);
                    }
                }
                return;
            }
        }
        helpMenu(sender);
    }

    /**
     * Displays the help menu for the rank-related permissions commands.
     * This method provides a detailed list of available subcommands
     * with brief descriptions to guide the user on their usage.
     *
     * @param sender the sender who executed the command, can be a player or console
     */
    private void helpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "/perm rank [rank] get [permission] " + ChatColor.AQUA + "- Get the value of a permission for a rank");
        sender.sendMessage(ChatColor.GREEN + "/perm rank [rank] set [permission] <true/false> " + ChatColor.AQUA + "- Set the value of a permission for a rank");
        sender.sendMessage(ChatColor.GREEN + "/perm rank [rank] unset [permission] " + ChatColor.AQUA + "- Remove the value of a permission for a rank");
        sender.sendMessage(ChatColor.GREEN + "/perm rank [rank] test [permission] " + ChatColor.AQUA + "- Test whether a rank does or doesn't have a permission");
        sender.sendMessage(ChatColor.GREEN + "/perm rank [rank] members " + ChatColor.AQUA + "- List the members of a rank");
    }
}
