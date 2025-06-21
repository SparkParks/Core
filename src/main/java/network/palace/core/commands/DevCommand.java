package network.palace.core.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;

/**
 * Represents a developer-level command within the application's command system.
 * <p>
 * This command allows developers to execute specific tasks designated by the
 * "tomsTesting" command identifier. It is restricted to users with the
 * developer rank.
 */
@CommandMeta(rank = Rank.DEVELOPER)
public class DevCommand extends CoreCommand {

    /**
     * Constructs an instance of the DevCommand class.
     * <p>
     * This command is specifically designed for developer use, identified by the
     * command name "tomsTesting". It allows developers to execute actions based
     * on the arguments provided while ensuring that the proper rank restrictions
     * are enforced.
     */
    public DevCommand() {
        super("tomsTesting");
    }

    /**
     * Handles the execution of the "tomsTesting" developer command.
     * <p>
     * This method processes the command arguments to add adventure coins to the
     * specified player. Only accessible by users with the developer rank.
     *
     * @param player The player instance to whom the command is applied.
     * @param args An array of strings representing the command arguments.
     *             The first argument (args[0]) specifies the number of adventure
     *             coins to add.
     * @throws CommandException If an error occurs while executing the command or
     *                          parsing the arguments.
     */
    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.addAdventureCoins(Integer.parseInt(args[0]), "Dev Command");
    }
}
