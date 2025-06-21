package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;

/**
 * Represents a command for teleporting a player to the world's default spawn location.
 * The command is available for players with at least the rank defined in the {@code @CommandMeta}.
 * <p>
 * This command:
 * - Teleports the player to the spawn location of the default world.
 * - Informs the player with a message upon successful teleportation.
 * <p>
 * Extends the functionality of {@link CoreCommand}.
 */
@CommandMeta(description = "Get back to the world spawn", rank = Rank.TRAINEE)
public class SpawnCommand extends CoreCommand {

    /**
     * Constructs an instance of the SpawnCommand class.
     * This command is designed to teleport a player to the server's default spawn location.
     * The command name is set to "spawn" and is available for execution based on the rank defined
     * in the associated {@code @CommandMeta}.
     */
    public SpawnCommand() {
        super("spawn");
    }

    /**
     * Handles the execution of the "spawn" command, teleporting the player
     * to the default world's spawn location and sending them a confirmation message.
     *
     * @param player The player executing the command.
     * @param args   The arguments provided with the command. Not used in this implementation.
     * @throws CommandException If an error occurs during command execution.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.teleport(Core.getDefaultWorld().getSpawnLocation());
        player.sendMessage(ChatColor.GRAY + "Teleported you to the spawn.");
    }
}
