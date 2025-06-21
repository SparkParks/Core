package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The MsgCommand class handles the functionality for sending private messages between players.
 * It extends the CoreCommand class and is annotated with @CommandMeta to provide metadata
 * about the command, including its description, aliases, and required rank.
 */
@CommandMeta(description = "Send message", aliases = {"tell", "t", "w", "whisper", "m"}, rank = Rank.CM)
public class MsgCommand extends CoreCommand {

    /**
     * Constructs an instance of the MsgCommand class.
     * The command is identified by the name "msg".
     * This command is used to handle private messaging functionality, enabling users to
     * send private messages to other players.
     */
    public MsgCommand() {
        super("msg");
    }

    /**
     * Handles the execution of the unspecific "msg" command for sending private messages between players.
     * If the sender is a player or the input arguments are insufficient, the command execution will terminate,
     * otherwise it processes the message and sends it to the target player.
     *
     * @param sender the entity that executed the command, typically a console or command block. Must not be a player.
     * @param args the command arguments provided by the sender.
     *             The first argument specifies the target player's name,
     *             and the subsequent arguments form the message to send.
     * @throws CommandException if an error occurs during command execution or processing.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (sender instanceof Player) return;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/msg [player] [message]");
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer(args[0]);
        if (player == null) return;
        StringBuilder msg = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            msg.append(args[i]).append(" ");
        }
        player.sendMessage(ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', msg.toString()));
    }
}
