package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Represents a command that displays the duration for which the server has been online.
 * This command provides information about the server's uptime by calculating the
 * time difference between the server's start time and the current time.
 * <p>
 * The command uses calendar-based calculations to determine the duration in a
 * human-readable format, including units such as years, months, days, hours,
 * minutes, and seconds.
 * <p>
 * This class extends the {@code CoreCommand} class, inheriting its core
 * command functionalities, and overrides the necessary method to handle the
 * specific logic of displaying uptime.
 * <p>
 * Annotations:
 * - {@code @CommandMeta}: Specifies metadata for the command, including its
 *   aliases and description.
 * <p>
 * Methods:
 * - {@code OnlineCommand()}: Initializes the command with the identifier "online".
 * - {@code handleCommandUnspecific(CommandSender, String[])}: Handles the command
 *   logic to compute and send the server's uptime to the command sender.
 * - {@code formatDateDiff(Calendar, Calendar)}: A private utility method to
 *   format the difference between two calendar dates into a human-readable
 *   string representation.
 * - {@code dateDiff(int, Calendar, Calendar, boolean)}: A private utility method
 *   to calculate the difference in a specific time unit between two calendar dates
 *   while respecting the direction (future or past).
 */
@CommandMeta(aliases = "ot", description = "View the amount of time the server has been online")
public class OnlineCommand extends CoreCommand {

    /**
     * Constructs a new instance of the OnlineCommand class.
     * <p>
     * This command is specifically designed to execute functionality associated with
     * the command name "online". It is intended to be integrated within the command
     * system, inheriting from the CoreCommand superclass. The exact behavior of the
     * command is defined by the methods that handle its execution.
     */
    public OnlineCommand() {
        super("online");
    }

    /**
     * Handles the execution of an unspecified command and provides feedback to the sender
     * about how long the server has been online.
     * <p>
     * This method uses the server's start time to calculate the duration the server
     * has been running and informs the command sender with a formatted message.
     *
     * @param sender The entity that issued the command (e.g., player, console, etc.).
     * @param args The arguments passed along with the command.
     * @throws CommandException If an error occurs during the execution or processing of the command.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        long startTime = Core.getInstance().getStartTime();
        Calendar c = new GregorianCalendar();
        c.setTime(new Date(startTime));
        String date = formatDateDiff(c, new GregorianCalendar());
        sender.sendMessage(ChatColor.GREEN + "This server " + ChatColor.AQUA + "(" + Core.getInstanceName() + ") " +
                ChatColor.GREEN + "has been online for " + date + ".");
    }

    /**
     * Formats the difference between two calendar dates into a human-readable string.
     * The output string shows the time difference in terms of years, months, days, hours,
     * minutes, and seconds, depending on the difference between the dates.
     *
     * @param fromDate The starting calendar date.
     * @param toDate The ending calendar date.
     * @return A formatted string representing the difference between the two dates.
     *         Returns "Now" if the dates are identical or the calculated difference is zero.
     */
    private static String formatDateDiff(Calendar fromDate, Calendar toDate) {
        boolean future = false;
        if (toDate.equals(fromDate)) {
            return "Now";
        }
        if (toDate.after(fromDate)) {
            future = true;
        }
        StringBuilder sb = new StringBuilder();
        int[] types = {1, 2, 5, 11, 12, 13};

        String[] names = {"Years", "Years", "Months", "Months", "Days",
                "Days", "Hours", "Hours", "Minutes", "Minutes", "Seconds",
                "Seconds"};

        int accuracy = 0;
        for (int i = 0; i < types.length; i++) {
            if (accuracy > 2) {
                break;
            }
            int diff = dateDiff(types[i], fromDate, toDate, future);
            if (diff > 0) {
                accuracy++;
                sb.append(" ").append(diff).append(" ").append(names[(i * 2)]);
            }
        }
        if (sb.length() == 0) {
            return "Now";
        }
        return sb.toString().trim();
    }

    /**
     * Calculates the difference between two calendar dates based on the specified calendar field type.
     * The difference is incremented or decremented depending on whether a future or past difference is being calculated.
     *
     * @param type The calendar field type to calculate the difference for (e.g., Calendar.YEAR, Calendar.MONTH).
     * @param fromDate The starting date in the calendar to calculate the difference from.
     * @param toDate The ending date in the calendar to calculate the difference to.
     * @param future A boolean indicating whether to calculate the difference for future (true) or past (false) dates.
     * @return The difference between the two dates in terms of the specified calendar field.
     */
    private static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while ((future) && (!fromDate.after(toDate)) || (!future)
                && (!fromDate.before(toDate))) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }
        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }
}
