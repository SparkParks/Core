package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.events.PlayerToggleAllowFlightEvent;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;

/**
 * Represents a command that allows players to toggle the flight ability for themselves or others,
 * based on rank and server conditions. It also contains functionality for server administrators
 * to enable or disable flight for players of the SHAREHOLDER rank.
 * <p>
 * This command operates under specific constraints:
 * - A player must have a rank of at least TRAINEE to toggle flight for others or themselves.
 * - SHAREHOLDER flight toggling is restricted based on server configuration and server type.
 * <p>
 * The command also interacts with external plugins such as "ParkManager" and "Lobby" to enforce
 * flight restrictions on specific server types.
 * <p>
 * Command Details:
 * - Command: "/fly"
 * - Description: Toggles a player's ability to fly.
 * - Rank: Requires at least TRAINEE rank to execute.
 * <p>
 * Admin Functionality:
 * - Supports the "sharetoggle" argument for toggling the server-wide flight ability for
 *   SHAREHOLDER rank players, provided "ParkManager" is installed.
 * <p>
 * Configuration:
 * - Reads and writes the "shareholderFlightDisabled" status from the Core configuration file
 *   located at "plugins/Core/config.yml".
 * <p>
 * Exceptions:
 * - Will throw a {@link CommandException} if the command execution fails or has invalid arguments.
 */
@CommandMeta(description = "Toggle player flight", rank = Rank.SHAREHOLDER)
public class FlyCommand extends CoreCommand {
    /**
     * Indicates whether the shareholder flight feature is disabled.
     * <p>
     * This variable is used to manage the state of flight functionality for shareholders,
     * controlling whether they are restricted from enabling or using flight-related commands
     * or capabilities.
     */
    private boolean shareholderFlightDisabled;

    /**
     * Constructs a FlyCommand instance with the command name set to "fly".
     * Initializes the state of shareholder flight restrictions based on the
     * configuration settings.
     */
    public FlyCommand() {
        super("fly");
        shareholderFlightDisabled = Core.getCoreConfig().getBoolean("shareholderFlightDisabled", false);
    }

    /**
     * Handles the execution of the "fly" command for the specified player and arguments.
     * This method allows toggling flight for either the command executor or a target player,
     * and also manages shareholder flight restrictions when specific conditions are met.
     *
     * @param player The player executing the command.
     * @param args   The arguments provided with the command. The first argument can be
     *               a target player's name or a special keyword for managing settings.
     * @throws CommandException If an error occurs during the execution of the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1 || player.getRank().getRankId() < Rank.TRAINEE.getRankId()) {
            toggleFlight(player, player);
            return;
        }
        if (args[0].equalsIgnoreCase("sharetoggle") && Bukkit.getPluginManager().getPlugin("ParkManager") != null) {
            shareholderFlightDisabled = !shareholderFlightDisabled;
            player.sendMessage((shareholderFlightDisabled ? ChatColor.RED : ChatColor.GREEN) + "Shareholder flight has been " +
                    (shareholderFlightDisabled ? "disabled" : "enabled") + " for this server.");
            Core.getCoreConfig().set("shareholderFlightDisabled", shareholderFlightDisabled);
            try {
                Core.getCoreConfig().save(new File("plugins/Core/config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        CPlayer target = Core.getPlayerManager().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        toggleFlight(player, target);
    }

    /**
     * Toggles the flight ability of the specified target player. If the sender is a shareholder,
     * additional conditions are checked before toggling flight. The method ensures that server-specific
     * restrictions are adhered to and informs both the sender and target player of the updated flight status.
     *
     * @param sender The player executing the toggle flight command. This may include shareholders
     *               with special restrictions.
     * @param target The target player whose flight status is being toggled.
     */
    private void toggleFlight(CPlayer sender, CPlayer target) {
        if (sender.getRank().equals(Rank.SHAREHOLDER)) {
            if (Bukkit.getPluginManager().getPlugin("ParkManager") == null) {
                if (Bukkit.getPluginManager().getPlugin("Lobby") == null) {
                    // since ParkManager isn't present, isn't a park server, don't let shareholders fly
                    sender.sendMessage(ChatColor.RED + "You can only use this on Park servers!");
                    return;
                }
            }
            if (shareholderFlightDisabled) {
                sender.sendMessage(ChatColor.RED + "This command is disabled on this server!");
                return;
            }
        }
        // call event to allow Vanish to hide the player if needed
        new PlayerToggleAllowFlightEvent(sender, !target.getAllowFlight()).call();
        if (target.getAllowFlight()) {
            target.setAllowFlight(false);
            target.sendMessage(ChatColor.RED + "You can no longer fly!");
            if (!sender.getUniqueId().equals(target.getUniqueId()))
                sender.sendMessage(ChatColor.RED + target.getName() + " can no longer fly!");
        } else {
            target.setAllowFlight(true);
            target.sendMessage(ChatColor.GREEN + "You can now fly!");
            if (!sender.getUniqueId().equals(target.getUniqueId()))
                sender.sendMessage(ChatColor.GREEN + target.getName() + " can now fly!");
        }
    }
}
