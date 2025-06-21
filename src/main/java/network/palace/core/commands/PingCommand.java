package network.palace.core.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.commands.ping.PingInfo;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;

/**
 * Represents a command for players to check their current ping with the server.
 * This command provides feedback on a player's latency in milliseconds and offers
 * additional details through a subcommand if required.
 * <p>
 * The main purpose of this command is to inform players of their connection quality to the server,
 * providing color-coded details for easy interpretation.
 * <p>
 * Color coding of ping:
 * - Green for low latency (under 100 ms)
 * - Yellow for moderate latency (between 100 ms and 349 ms)
 * - Red for high latency (350 ms or above)
 * <p>
 * Subcommands:
 * - "ping info": May provide additional information related to ping.
 * <p>
 * Extends:
 * - CoreCommand
 * <p>
 * Throws:
 * - CommandException when an issue in handling the command arises.
 */
@CommandMeta(description = "Tell players their current ping with the server")
public class PingCommand extends CoreCommand {

    /**
     * Constructs a new PingCommand instance.
     * <p>
     * Initializes the ping command to allow players to check their
     * current ping (network latency) with the server. A subcommand
     * "ping info" is registered to provide additional information
     * about ping and how it may affect gameplay.
     */
    public PingCommand() {
        super("ping");
        this.registerSubCommand(new PingInfo());
    }

    /**
     * Handles the execution of the "ping" command, providing players with their current ping (network latency) in milliseconds.
     * Based on the player's ping value, the message is displayed with color-coded feedback for better clarity.
     *
     * @param player The player who executed the command.
     * @param args The command arguments provided by the player.
     * @throws CommandException If an issue occurs while handling the command.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        int ping = player.getPing();
        if (ping < 0) {
            player.sendMessage(ChatColor.RED + "We're having an issue calculating your ping right now, try again in a few minutes!");
            return;
        }
        ChatColor color = this.getColor(ping);
        player.sendMessage(ChatColor.GOLD + "Your ping with our server is " + color + ping + " milliseconds. " +
                ChatColor.GOLD + "Type /ping info to learn more about your ping.");
    }

    /**
     * Determines the color associated with a player's ping value.
     * Low latency (under 100 ms) returns green, moderate latency
     * (between 100 ms and 349 ms inclusive) returns yellow, and
     * high latency (350 ms or above) returns red.
     *
     * @param ping The player's ping value in milliseconds.
     * @return The {@link ChatColor} representing the latency category:
     *         green for low latency, yellow for moderate latency, and red for high latency.
     */
    private ChatColor getColor(int ping) {
        if (ping < 100) {
            return ChatColor.GREEN;
        }
        if (ping >= 350) return ChatColor.RED;
        return ChatColor.YELLOW;
    }
}

