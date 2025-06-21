package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The TeleportCommand class is a command implementation for teleporting players to specific
 * locations or other players within the game. This command supports various usage scenarios,
 * including teleporting to other players, teleporting one player to another, or teleporting
 * to specific coordinates.
 * <p>
 * The command includes the following functionalities:
 * - Checks whether the sender is a player or a console.
 * - Supports both absolute and relative coordinate teleportation.
 * - Validates if players are inside vehicles or have permissions for teleportation.
 * - Provides guidance and feedback messages for usage and error handling.
 * - Enforces rank-based restrictions and permissions (e.g., guides and trainees).
 * <p>
 * The command is highly configurable via its annotations:
 * - Description: "Teleport a player".
 * - Aliases: "tp".
 * - Rank requirement: TRAINEE.
 * - Rank tag: GUIDE.
 * <p>
 * Parameters:
 * - A single argument to teleport the sender to another player.
 * - Two arguments to teleport one player to another.
 * - Three arguments to teleport the sender to specific coordinates.
 * - Four arguments to teleport a specified player to specific coordinates.
 * <p>
 * Exceptions:
 * - Handles invalid player names with helpful error messages.
 * - Ensures numeric validation for coordinate-based teleportation.
 * - Prevents teleportation of players inside vehicles unless specific conditions are met.
 */
@CommandMeta(description = "Teleport a player", aliases = "tp", rank = Rank.TRAINEE, tag = RankTag.GUIDE)
public class TeleportCommand extends CoreCommand {

    /**
     * Constructs an instance of the TeleportCommand class.
     * <p>
     * This command is responsible for handling teleportation operations within the
     * application's command system. The specific functionality and behavior of the
     * command are implemented in the inherited methods from the superclass. The
     * TeleportCommand is initialized with the command name "teleport" to ensure
     * consistent identification and usage across the application.
     */
    public TeleportCommand() {
        super("teleport");
    }

    /**
     * Handles the teleportation command for various scenarios, including teleporting players
     * to other players, teleporting to specific coordinates, or teleporting other players
     * to specific locations.
     *
     * @param sender The command sender who invoked the teleport command. Can be a player or console.
     * @param args The arguments provided to the command. These dictate the teleport action, such
     *             as target player(s) or specific coordinates.
     * @throws CommandException If an error occurs during the execution or processing of the command.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                CPlayer tp1 = Core.getPlayerManager().getPlayer(args[0]);
                CPlayer tp2 = Core.getPlayerManager().getPlayer(args[1]);
                if (tp1 == null || tp2 == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                if (tp1.isInsideVehicle()) {
                    sender.sendMessage(ChatColor.RED + tp1.getName() + " is in a vehicle, you can't teleport them!");
                    return;
                }
                if (tp2.isInsideVehicle() && !tp2.getGamemode().equals(GameMode.SPECTATOR)) {
                    sender.sendMessage(ChatColor.RED + tp2.getName() + " is in a vehicle, you can't teleport to them! " +
                            "They must be in Spectator Mode to teleport to players on rides.");
                    return;
                }
                tp1.teleport(tp2);
                sender.sendMessage(ChatColor.GRAY + tp1.getName() + " has been teleported to " + tp2.getName());
                return;
            }
            if (args.length == 4) {
                try {
                    CPlayer tp = Core.getPlayerManager().getPlayer(args[0]);
                    double x = args[1].startsWith("~") ? tp.getLocation().getX() + num(args[1].substring(1)) : num(args[1]);
                    double y = args[2].startsWith("~") ? tp.getLocation().getY() + num(args[2].substring(1)) : num(args[2]);
                    double z = args[3].startsWith("~") ? tp.getLocation().getZ() + num(args[3].substring(1)) : num(args[3]);
                    Location loc = new Location(tp.getWorld(), x, y, z, tp
                            .getLocation().getYaw(), tp.getLocation().getPitch());
                    if (tp.isInsideVehicle()) {
                        sender.sendMessage(ChatColor.RED + tp.getName() + " is in a vehicle, you can't teleport them!");
                        return;
                    }
                    tp.teleport(loc);
                    sender.sendMessage(ChatColor.GRAY + tp.getName() + " has been teleported to " + x + ", " + y + ", "
                            + z);
                    return;
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Error with numbers!");
                    return;
                }
            }
            sender.sendMessage(ChatColor.RED + "/tp [Player] <Target> or <x> <y> <z>");
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer((Player) sender);
        boolean guide = player.hasTag(RankTag.GUIDE) && player.getRank().getRankId() < Rank.TRAINEE.getRankId();
        if (args.length == 1) {
            CPlayer tp = Core.getPlayerManager().getPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            if (tp.isInsideVehicle() && !player.getUniqueId().equals(tp.getUniqueId()) && !player.getGamemode().equals(GameMode.SPECTATOR)) {
                sender.sendMessage(ChatColor.RED + tp.getName() + " is in a vehicle, you can't teleport to them! " +
                        "You must be in Spectator Mode to teleport to players on rides.");
                return;
            }
            player.teleport(tp);
            player.sendMessage(ChatColor.GRAY + "You teleported to " + tp.getName());
            return;
        }
        if (args.length == 2 && !guide) {
            CPlayer tp1 = Core.getPlayerManager().getPlayer(args[0]);
            CPlayer tp2 = Core.getPlayerManager().getPlayer(args[1]);
            if (tp1 == null || tp2 == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            if (tp1.isInsideVehicle()) {
                sender.sendMessage(ChatColor.RED + tp1.getName() + " is in a vehicle, you can't teleport them!");
                return;
            }
            if (tp2.isInsideVehicle() && !tp2.getGamemode().equals(GameMode.SPECTATOR)) {
                sender.sendMessage(ChatColor.RED + tp2.getName() + " is in a vehicle, you can't teleport to them!");
                return;
            }
            tp1.teleport(tp2);
            player.sendMessage(ChatColor.GRAY + tp1.getName()
                    + " has been teleported to " + tp2.getName());
            return;
        }
        if (args.length == 3 && !guide) {
            try {
                double x = args[0].startsWith("~") ? player.getLocation().getX() + num(args[0].substring(1)) : num(args[0]);
                double y = args[1].startsWith("~") ? player.getLocation().getY() + num(args[1].substring(1)) : num(args[1]);
                double z = args[2].startsWith("~") ? player.getLocation().getZ() + num(args[2].substring(1)) : num(args[2]);
                Location loc = new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
                player.teleport(loc);
                player.sendMessage(ChatColor.GRAY + "You teleported to " + x + ", " + y + ", " + z);
                return;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Error with numbers!");
                return;
            }
        }
        if (args.length == 4 && !guide) {
            try {
                CPlayer tp = Core.getPlayerManager().getPlayer(args[0]);
                double x = args[1].startsWith("~") ? player.getLocation().getX() + num(args[1].substring(1)) : num(args[1]);
                double y = args[2].startsWith("~") ? player.getLocation().getY() + num(args[2].substring(1)) : num(args[2]);
                double z = args[3].startsWith("~") ? player.getLocation().getZ() + num(args[3].substring(1)) : num(args[3]);
                Location loc = new Location(tp.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
                if (tp.isInsideVehicle()) {
                    sender.sendMessage(ChatColor.RED + tp.getName() + " is in a vehicle, you can't teleport to them!");
                    return;
                }
                tp.teleport(loc);
                player.sendMessage(ChatColor.GRAY + tp.getName() + " has been teleported to " + x + ", " + y + ", " + z);
                return;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Error with numbers!");
                return;
            }
        }
        if (guide) {
            player.sendMessage(ChatColor.AQUA + "/tp [Player] - Teleport to a player");
        } else {
            player.sendMessage(ChatColor.RED + "/tp [Player] <Target> or /tp <x> <y> <z> or /tp [Player] <x> <y> <z>");
        }
    }

    /**
     * Converts the provided string into a double value. If the input string is null
     * or cannot be parsed into a valid double, the method returns 0.
     *
     * @param s The string to be parsed into a double value. Can be null or a valid numeric string.
     * @return The double representation of the input string, or 0 if the input is null
     *         or cannot be parsed into a valid double.
     */
    private double num(String s) {
        if (s == null) {
            return 0;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}

