package network.palace.core.commands;

import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.commands.permissions.ListCommand;
import network.palace.core.commands.permissions.*;
import network.palace.core.player.Rank;

/**
 * Represents the PermissionCommand class, which extends CoreCommand and
 * serves as a container for permission-related subcommands.
 * <p>
 * The PermissionCommand is designed to handle various permission-related
 * operations, facilitated through its registered subcommands. It is configured
 * to execute only subcommands and does not handle direct command execution.
 */
@CommandMeta(description = "Permissions command", rank = Rank.COORDINATOR)
public class PermissionCommand extends CoreCommand {

    /**
     * Constructs an instance of the PermissionCommand class. This constructor initializes
     * the command with the name "perm" and registers a set of specific subcommands that
     * handle various permission-related operations.
     * <p>
     * The following subcommands are registered:
     * - ListCommand: Provides functionality to list ranks and tags.
     * - PlayerCommand: Handles operations related to players and permissions.
     * - RankCommand: Manages rank-related operations.
     * - RefreshCommand: Allows refreshing of certain permission-related states.
     * - TagCommand: Facilitates management of tags associated with ranks or permissions.
     * <p>
     * This command is designed to operate exclusively through its subcommands and does
     * not handle direct execution outside the registered subcommands.
     */
    public PermissionCommand() {
        super("perm");
        registerSubCommand(new ListCommand());
        registerSubCommand(new PlayerCommand());
        registerSubCommand(new RankCommand());
        registerSubCommand(new RefreshCommand());
        registerSubCommand(new TagCommand());
    }

    /**
     * Determines whether this command operates exclusively through subcommands.
     * This method indicates that the command does not handle direct execution
     * and ensures that only registered subcommands are utilized.
     *
     * @return true if the command uses only subcommands; false otherwise
     */
    @Override
    protected boolean isUsingSubCommandsOnly() {
        return true;
    }
}
