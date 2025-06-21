package network.palace.core.commands.disabled;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * The MeCommand class represents a disabled command implementation that extends CoreCommand.
 * This command is associated with the "me" action and informs the sender that the command is disabled.
 * <p>
 * This class overrides the {@code handleCommandUnspecific} method to provide specific behavior for the command.
 */
@CommandMeta(description = "Disable me command")
public class MeCommand extends CoreCommand {

    /**
     * Creates a new instance of the MeCommand class.
     * <p>
     * The MeCommand is a disabled command that is instantiated with the name "me".
     * Its functionality is restricted to inform the sender that it is disabled.
     */
    public MeCommand() {
        super("me");
    }

    /**
     * Handles the execution of an unspecified command. This method provides
     * a default response, indicating that the command is disabled.
     *
     * @param sender the entity (e.g., player, console, etc.) that executed the command
     * @param args the arguments provided with the command
     * @throws CommandException if there is an issue processing the command
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.RED + "Disabled");
    }
}
