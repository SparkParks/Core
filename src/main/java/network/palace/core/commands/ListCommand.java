package network.palace.core.commands;

import com.google.common.base.Joiner;
import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The ListCommand class is used to display a list of all players currently online on the server.
 * It retrieves and formats the list of player names, then sends the formatted message to the
 * command sender.
 * <p>
 * This command can be invoked using the aliases provided in the {@code @CommandMeta} annotation.
 * It requires at least the {@code Rank.TRAINEE} rank to execute.
 */
@CommandMeta(aliases = {"who"}, description = "Lists all players on the server.", rank = Rank.TRAINEE)
public class ListCommand extends CoreCommand {

    /**
     * Constructs an instance of the ListCommand class.
     * <p>
     * The ListCommand is used to display a list of all players currently online on the server.
     * This command, named "list", is intended to be executed by users with the appropriate rank
     * and permissions, as defined in the containing class.
     */
    public ListCommand() {
        super("list");
    }

    /**
     * Handles the execution of the "list" command when no specific subcommand is provided.
     * This method retrieves a list of all online players, formats the player names into
     * a single string, and then sends the formatted list to the command sender.
     *
     * @param sender The CommandSender instance representing the entity that issued the command.
     *               This can be a player or the console.
     * @param args   The arguments provided with the command. These are unused in this specific method
     *               but are required by the method signature.
     * @throws CommandException If an error occurs during command processing or execution.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        List<String> playerNames = Core.getPlayerManager().getOnlinePlayers().stream().map(CPlayer::getName).sorted().collect(Collectors.toList());
        String playerList = Joiner.on(" ").skipNulls().join(playerNames);
        // Formatter
        String playersOnlineFormat = Core.getLanguageFormatter().getFormat(sender, "command.list.playersOnline")
                .replaceAll("<players-online>", playerList).replaceAll("<player-amount>", String.valueOf(playerNames.size()));
        sender.sendMessage(playersOnlineFormat);
    }
}
