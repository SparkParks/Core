package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.economy.honor.TopHonorReport;
import network.palace.core.player.Rank;
import network.palace.core.utils.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;
import java.util.List;

/**
 * The TopHonorCommand class represents a command that displays an honor leaderboard.
 * This command retrieves and displays the top players based on their honor points
 * from a database and organizes them into a leaderboard format.
 * <p>
 * Key features include:
 * - Retrieving honor leaderboard data asynchronously.
 * - Handling an optional argument to specify the number of top players to display (default 10, maximum 10).
 * - Formatting leaderboard outputs with player name, honor values, and levels.
 */
@CommandMeta(description = "View honor leaderboard")
public class TopHonorCommand extends CoreCommand {
    /**
     * A pre-configured {@link DecimalFormat} instance used for formatting numerical values
     * with a pattern of "#,###", ensuring numbers are displayed with comma-separated groups
     * of thousands.
     * <p>
     * This formatting is typically utilized to enhance the readability of numerical values,
     * such as scores or monetary amounts, in the context of the command's output.
     */
    private final DecimalFormat format = new DecimalFormat("#,###");

    /**
     * Constructs an instance of the TopHonorCommand class.
     * <p>
     * This command is identified by the name "tophonor" and is designed to display
     * a leaderboard of top players ranked by their honor points. The command
     * supports an optional argument to specify the number of players to display,
     * with a default and maximum of 10.
     * <p>
     * Key functionality includes:
     * - Retrieving the honor leaderboard data asynchronously from the database.
     * - Sorting the leaderboard by honor points in descending order.
     * - Formatting the output with player names, honor points, and honor levels.
     */
    public TopHonorCommand() {
        super("tophonor");
    }

    /**
     * Handles the execution of the "tophonor" command when no specific subcommand is provided.
     * Asynchronously retrieves the top players ranked by honor points, formats the data,
     * and sends a formatted leaderboard message to the command sender.
     *
     * @param sender The sender of the command, which could be a player or console.
     * @param args Optional arguments passed with the command. The first argument, if provided
     *             and is a valid integer, defines the limit for the number of players displayed
     *             in the leaderboard. Defaults to 10 and cannot exceed 10.
     * @throws CommandException If an error occurs while processing the command.
     */
    @Override
    protected void handleCommandUnspecific(final CommandSender sender, final String[] args) throws CommandException {
        sender.sendMessage(ChatColor.GREEN + "Gathering leaderboard data...");
        Core.runTaskAsynchronously(Core.getInstance(), () -> {
            int limit = 10;
            if (args.length > 0 && MiscUtil.checkIfInt(args[0]) && (limit = Integer.parseInt(args[0])) > 10) {
                limit = 10;
            }
            List<TopHonorReport> list = Core.getMongoHandler().getTopHonor(limit);
            list.sort((o1, o2) -> o2.getHonor() - o1.getHonor());

            StringBuilder msg = new StringBuilder(ChatColor.GOLD + "Honor Leaderboard: Top " + limit + " Players\n");

            for (int i = 0; i < list.size(); i++) {
                TopHonorReport report = list.get(i);
                Rank rank = Core.getMongoHandler().getRank(report.getName());
                msg.append(report.getPlace()).append(". ").append(rank.getTagColor()).append(report.getName()).append(": ")
                        .append(ChatColor.GOLD).append(format(report.getHonor())).append(ChatColor.GRAY).append(" (Level")
                        .append(" ").append(Core.getHonorManager().getLevel(report.getHonor()).getLevel()).append(")");
                if (i <= list.size() - 1) {
                    msg.append("\n").append(ChatColor.GOLD);
                }
            }
            sender.sendMessage(msg.toString());
        });
    }

    /**
     * Formats the given integer using the predefined formatting logic.
     *
     * @param i The integer to be formatted.
     * @return A string representation of the formatted integer.
     */
    private String format(int i) {
        return this.format.format(i);
    }
}

