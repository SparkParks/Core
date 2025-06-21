package network.palace.core.command;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;
import network.palace.core.plugin.Plugin;
import network.palace.core.utils.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/// Old documentation
/// CoreCommand is the superclass for any commands that are created to hook into the plugin system in core.
///
/// All commands are handled through three main methods, splitting the different sender types into different calls, and simplifying the method to just args and a sender.
///
/// Commands must throw exceptions in the event of a failure, and you can handle these exceptions using [#handleCommandException(CommandException,String[],org.bukkit.command.CommandSender)].
///
/// You can also use our sub command system which supports tab completion and recursive sub-commands by using the constructor [#CoreCommand(String,CoreCommand...)].
///
/// You can override tab-completion using [#handleTabComplete(org.bukkit.command.CommandSender,org.bukkit.command.Command,String,String[])]
///
/// If you require usage of a sub-command, please override [#isUsingSubCommandsOnly()] and have it return true.
///
/// @see org.bukkit.command.CommandExecutor
/// @see org.bukkit.command.TabCompleter

/**
 * Represents the core functionality and structure for commands, including the management of
 * sub-commands and command processing for different sender types. This is an abstract foundation
 * for creating custom commands in a plugin environment.
 * <p>
 * The CoreCommand class integrates with Bukkit's {@link CommandExecutor}
 * and {@link TabCompleter} interfaces, providing standardized methods
 * for handling command execution and tab completion.
 * <p>
 * Fields:
 * - `subCommands`: A list of sub-commands registered under this command.
 * - `name`: The name of the main command.
 * - `superCommand`: The parent command, if applicable, for nested commands.
 * - `rank`: Required rank level to execute this command.
 * - `description`: A textual description of the command.
 * <p>
 * Constructors:
 * - Protected constructors allow for the creation of commands with or without sub-commands.
 * <p>
 * Key Methods:
 * - `registerSubCommand`: Allows registration of sub-commands dynamically after initialization.
 * - `unregisterSubCommand`: Removes specific sub-commands from this command.
 * - `getSubCommands`: Returns an immutable list of registered sub-commands.
 * - `regenerateHelpCommand`: Regenerates a help command based on available sub-commands.
 * - `onCommand`: Handles the execution of the main command or its sub-commands.
 * - `onTabComplete`: Handles the tab completion logic for the command.
 * - `handleCommandException`: Designed to be overridden to provide custom exception handling
 *   for errors during command execution.
 * - `getSubCommandFor`: Retrieves a specific sub-command by name.
 * - `getSubCommandsForPartial`: Retrieves sub-commands that partially match a provided string.
 * - Default and customizable handle methods: Provide functionality for dealing
 *   with commands dispatched by console, players, block command senders, or unknown types.
 * - `handleTabComplete`: Supports custom tab-completion behavior that subclasses can override.
 * - `isUsingSubCommandsOnly`: Hook for determining behavior related to sub-command-only usage.
 * - `getFormattedName`: Provides a formatted name for display or representation purposes.
 * - Overridden `toString`: Returns a string representation of the command.
 */
public abstract class CoreCommand implements CommandExecutor, TabCompleter {

    /**
     * A map containing the sub-commands associated with a particular command.
     * Each entry maps the name of the sub-command to its corresponding {@link CoreCommand} instance.
     * <p>
     * This map is used to manage and organize sub-commands tied to a root command,
     * allowing for dynamic registration, unregistration, and command execution handling.
     * <p>
     * Keys in the map represent the names or identifiers of the sub-commands,
     * while the values represent the specific {@link CoreCommand} instances associated
     * with each sub-command.
     * <p>
     * This field is initialized as an empty {@link HashMap}, and new sub-commands
     * can be added by using methods like {@link #registerSubCommand(CoreCommand...)}.
     */
    private final Map<String, CoreCommand> subCommands = new HashMap<>();

    /**
     * Represents the name of the command in the CoreCommand class.
     * It is a unique identifier for the command and is immutable once set.
     */
    @Getter private final String name;

    /**
     * Represents a reference to a parent command for the current command instance.
     * This variable is used to define the hierarchical relationship between commands,
     * where the current command is a sub-command of the specified {@link CoreCommand}.
     * It is typically utilized to manage command structures within the system.
     */
    @Getter @Setter private CoreCommand superCommand = null;

    /**
     * Represents the rank associated with the command. This variable determines
     * the access level or permissions required to execute the command.
     * <p>
     * The default value is {@code Rank.GUEST}, indicating the lowest access level.
     * Can be modified to set different rank-based restrictions on the command.
     */
    @Getter @Setter private Rank rank = Rank.GUEST;

    /**
     * Represents a textual description of the command. This typically outlines the
     * purpose or functionality of the command, providing clarity for users or developers.
     * <p>
     * The description can be used in documentation, tooltips, or other display elements
     * to give more details about the command.
     */
    @Getter @Setter private String description = "";

    /**
     * Constructs a CoreCommand instance with a specified name.
     *
     * @param name The name of the command.
     */
    protected CoreCommand(String name) {
        this.name = name;
    }

    /**
     * Constructs a CoreCommand instance with a specified name and an optional
     * list of subcommands.
     *
     * @param name        The name of the command.
     * @param subCommands An optional list of subcommands to be registered
     *                    under this command.
     */
    protected CoreCommand(final String name, CoreCommand... subCommands) {
        this.name = name;
        registerSubCommand(subCommands);
    }

    /**
     * Registers one or more subcommands to the current command. Each subcommand is added
     * to the internal subcommands map of the current command. If a subcommand is already
     * assigned to another command, an exception is thrown. The descriptions are set based
     * on the {@code CommandMeta} annotation of the subcommand class, if present. Additionally,
     * a help command is regenerated to include the new subcommands.
     *
     * @param subCommands An array of {@code CoreCommand} objects to be registered as subcommands.
     *                    Each subcommand must not already have a supercommand assigned.
     *                    Duplicate command names within the array should be avoided to prevent conflicts.
     *                    The final list of subcommands will be reflected in the regenerated help command.
     * @throws IllegalArgumentException If any of the provided subcommands already has a supercommand.
     */
    public final void registerSubCommand(CoreCommand... subCommands) {
        // Toss all the sub commands in the map
        for (CoreCommand subCommand : subCommands) {
            if (subCommand.getSuperCommand() != null)
                throw new IllegalArgumentException("The command you attempted to register already has a supercommand.");
            CommandMeta annotation = subCommand.getClass().getAnnotation(CommandMeta.class); // Get the commandMeta
            if (annotation != null) {
                subCommand.setDescription(annotation.description());
            }
            subCommand.setSuperCommand(this);
            this.subCommands.put(subCommand.getName(), subCommand);
        }
        // Add a provided help command
        regenerateHelpCommand();
    }

    /**
     * Unregisters one or more subcommands from the current command.
     * Each subcommand's association with this command is removed, and
     * its supercommand is set to null. A call to this method also
     * triggers the regeneration of the help command.
     *
     * @param subCommands An array of {@code CoreCommand} objects to be
     *                    unregistered as subcommands. Each command
     *                    specified must be a previously registered
     *                    subcommand of this command.
     */
    public final void unregisterSubCommand(CoreCommand... subCommands) {
        for (CoreCommand subCommand : subCommands) {
            //if (!subCommand.getSuperCommand().equals(this)) continue;
            this.subCommands.remove(subCommand.getName());
            subCommand.setSuperCommand(null);
        }
        regenerateHelpCommand();
    }

    /**
     * Retrieves a list of subcommands associated with this command.
     * The returned list is immutable to ensure that the subcommands cannot
     * be directly modified by external classes.
     *
     * @return an {@code ImmutableList} containing the subcommands of this command
     */
    public final ImmutableList<CoreCommand> getSubCommands() {
        return ImmutableList.copyOf(this.subCommands.values());
    }

    /**
     * Regenerates the help command for this instance of {@code CoreCommand}. If a subcommand named "help" is
     * already registered, the method does nothing. Otherwise, it dynamically creates a new "help" subcommand
     * that displays a formatted help menu outlining the main command and all its associated subcommands.
     * <p>
     * The generated "help" subcommand displays:
     * - The name and description of the main command.
     * - A list of subcommands with their respective descriptions.
     * <p>
     * The help menu is styled using {@code ChatColor} for enhanced readability and is sent to the sender when the
     * "help" subcommand is executed. The "help" subcommand is automatically associated with its parent command.
     * <p>
     * The newly created help command is configured with the description "Open the help menu."
     */
    public void regenerateHelpCommand() {
        if (subCommands.containsKey("help")) return;
        final CoreCommand superHelpCommand = this;
        CoreCommand help = new CoreCommand("help") {
            @Override
            public void handleCommandUnspecific(CommandSender sender, String[] args) {
                String name = CoreCommand.this.getFormattedName();
                StringBuilder msg = new StringBuilder(ChatColor.GREEN + MiscUtil.capitalizeFirstLetter(name) + " Commands:");
                if (!isUsingSubCommandsOnly()) {
                    msg.append("\n").append(ChatColor.GREEN).append("/").append(name.toLowerCase()).append(" ").append(ChatColor.AQUA).append("- ").append(CoreCommand.this.getDescription());
                }
                for (Map.Entry<String, CoreCommand> entry : subCommands.entrySet()) {
                    msg.append("\n").append(ChatColor.GREEN).append("/").append(entry.getValue().getFormattedName()).append(" ").append(ChatColor.AQUA).append("- ").append(entry.getValue().getDescription());
                }
                sender.sendMessage(msg.toString());
//                StringBuilder builder = new StringBuilder();
//                for (Map.Entry<String, CoreCommand> stringModuleCommandEntry : CoreCommand.this.subCommands.entrySet()) {
//                    builder.append(stringModuleCommandEntry.getKey()).append("|");
//                }
//                String s = msg;
                // Looks like this /name - [subcommand1|subcommand2|]
//                sender.sendMessage(ChatColor.GRAY + "/" + ChatColor.DARK_AQUA + superHelpCommand.getFormattedName() + ChatColor.GOLD + " - [" + s.substring(0, s.length() - 1) + "]");
            }
        };
        help.setSuperCommand(this);
        help.setDescription("Open the help menu");
        this.subCommands.put("help", help);
    }

    /**
     * Handles the execution of a command, including sub-command dispatch, permission checks,
     * and error handling for a variety of {@code CommandSender} types. This method
     * ensures appropriate execution behavior depending on the sender and command context.
     *
     * @param sender the source of the command (e.g., player, console, block command sender)
     * @param command the command being executed
     * @param s the command alias used
     * @param args the arguments passed alongside the command (sub-commands or additional parameters)
     * @return {@code true} if the command was successfully executed; {@code false} otherwise
     */
    @Override
    public final boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // Handling commands can be done by the logic below, and all errors should be thrown using an exception.
        // If you wish to override the behavior of displaying that error to the player, it is discouraged to do that in
        // your command logic, and you are encouraged to use the provided method handleCommandException.
        try {
            // STEP ONE: Handle sub-commands
            CoreCommand subCommand = null;
            if (sender instanceof Player) {
                CPlayer player = Core.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
                Rank requiredRank = Rank.GUEST;
                RankTag requiredTag = RankTag.NONE;
                if (getClass().isAnnotationPresent(CommandMeta.class)) {
                    CommandMeta annotation = getClass().getAnnotation(CommandMeta.class);
                    requiredRank = annotation.rank();
                    requiredTag = annotation.tag();
                }
                if (getClass().isAnnotationPresent(CommandPermission.class)) {
                    CommandPermission annotation = getClass().getAnnotation(CommandPermission.class);
                    requiredRank = annotation.rank();
                }
                if (player.getRank().getRankId() < requiredRank.getRankId() &&
                        (requiredTag.equals(RankTag.NONE) || !player.hasTag(requiredTag))) {
                    throw new PermissionException();
                }
            }
            // Check if we HAVE to use sub-commands (a behavior this class provides)
            if (isUsingSubCommandsOnly()) {
                // Check if there are not enough args for there to be a sub command
                if (args.length < 1) {
                    args = new String[1];
                    args[0] = "help";
                }
                // Also check if the sub command is valid by assigning and checking the value of the resolved sub command from the first argument.
                if ((subCommand = getSubCommandFor(args[0])) == null)
                    throw new ArgumentRequirementException("command.error.arguments.invalid");
            }
            if (subCommand == null && args.length > 0)
                subCommand = getSubCommandFor(args[0]); // If we're not requiring sub-commands but we can have them, let's try that
            // By now we have validated that the sub command can be executed if it MUST, now lets see if we can execute it
            // In this case, if we must execute the sub command, this check will always past. In cases where it's an option, this check will also pass.
            // That way, we can use this feature of sub commands without actually requiring it.
            if (subCommand != null) {
                String[] choppedArgs = args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
                preSubCommandDispatch(sender, choppedArgs, subCommand); // Notify the subclass that we are using a sub-command in case any staging needs to take place.
                subCommand.onCommand(sender, command, s, choppedArgs);
                try {
                    handlePostSubCommand(sender, args);
                } catch (EmptyHandlerException ignored) {
                }
                return true;
            }
            // Now that we've made it past the sub commands and permissions, STEP TWO: actually handle the command and it's args.
            try {
                if (sender instanceof Player) {
                    CPlayer player = Core.getPlayerManager().getPlayer((Player) sender);
                    if (player != null) handleCommand(player, args);
                } else if (sender instanceof ConsoleCommandSender) handleCommand((ConsoleCommandSender) sender, args);
                else if (sender instanceof BlockCommandSender) handleCommand((BlockCommandSender) sender, args);
            } catch (EmptyHandlerException e) {
                handleCommandUnspecific(sender, args); // We don't catch this because we would catch it and then immediately re-throw it so it could be caught by the below catch block (which handles the exception).
            }
        } // STEP THREE: Check for any command exceptions (intended) and any exceptions thrown in general and dispatch a call for an unhandled error to the handler.
        catch (CommandException ex) {
            handleCommandException(ex, args, sender);
        } catch (Exception e) {
            handleCommandException(new UnhandledCommandExceptionException(e), args, sender);
        }
        // STEP FOUR: Tell Bukkit we're done!
        return true;
    }

    /**
     * Handles tab completion for a command, including sub-command delegation and rank-based permission checks.
     * This method dynamically generates and returns possible tab completions based on the command structure
     * and user-provided arguments.
     *
     * @param sender the source of the command attempting to perform tab completion (e.g., player, console)
     * @param command the command being executed
     * @param alias the alias of the command, if any
     * @param args the arguments passed along with the command, used to determine context for tab completion
     * @return a list of suggested tab completions based on the command structure and user permissions.
     *         Returns an empty list if no suggestions are available or the user lacks sufficient permissions.
     */
    @Override
    public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Security for tab complete
        if (sender instanceof Player) {
            CPlayer player = Core.getPlayerManager().getPlayer(((Player) sender).getUniqueId());
            Rank requiredRank = Rank.GUEST;
            if (getClass().isAnnotationPresent(CommandMeta.class)) {
                CommandMeta annotation = getClass().getAnnotation(CommandMeta.class);
                requiredRank = annotation.rank();
            }
            if (getClass().isAnnotationPresent(CommandPermission.class)) {
                CommandPermission annotation = getClass().getAnnotation(CommandPermission.class);
                requiredRank = annotation.rank();
            }
            if (player.getRank().getRankId() < requiredRank.getRankId())
                return Collections.emptyList();
        }
        // Step one, check if we have to go a level deeper in the sub command system:
        if (args.length > 1) {
            //If so, check if there's an actual match for the sub-command to delegate to.
            CoreCommand possibleHigherLevelSubCommand;
            if ((possibleHigherLevelSubCommand = getSubCommandFor(args[0])) != null) {
                return possibleHigherLevelSubCommand.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
            }
            // NOW THINK. If there's not one, you'll reach this line, and exit this block of the if statement. The next statement is an else if, so it will skip that
            // And go to the very bottom "handleTabComplete."
        } else if (args.length == 1) { //So if we have exactly one argument, let's try and complete the sub-command for that argument
            // Grab some sub commands from the method we defined for this purpose
            List<CoreCommand> subCommandsForPartial = getSubCommandsForPartial(args[0]);
            // And if we found some
            if (subCommandsForPartial.size() != 0) {
                // Get the command names
                List<String> strings = subCommandsForPartial.stream().map(CoreCommand::getName).collect(Collectors.toList());
                List<String> strings1 = handleTabComplete(sender, command, alias, args);
                strings.addAll(strings1);
                // And return them
                return strings;
            }
            // Otherwise, head to the delegated call at the bottom.
        }
        return handleTabComplete(sender, command, alias, args);
    }

    /// Old documentation
    /// This method **should** be overridden by any sub-classes as the functionality it provides is limited.
    ///
    /// The goal of this method should always be conveying an error message to a user in a friendly manner. The [CommandException] can be extended by your [Plugin] to provide extended functionality.
    ///
    /// The `args` are the same args that would be passed to your handlers. Meaning, if this is a sub-command they will be cut to fit that sub-command, and if this is a root level command they will be all of the arguments.
    ///
    /// @param ex     The exception used to hold the error message and any other details about the failure. If there was an exception during the handling of the command this will be an [UnhandledCommandExceptionException].
    /// @param args   The arguments passed to the command.
    /// @param sender The sender of the command, cannot be directly cast to [CPlayer].

    /**
     * Handles exceptions that occur during command execution. Provides a user-friendly
     * message if the exception implements the {@code FriendlyException} interface or
     * displays a default error message for other types of exceptions.
     * <p>
     * This method also logs the stack trace for {@code UnhandledCommandExceptionException},
     * aiding debugging of commands.
     *
     * @param ex     The exception that occurred during
     */
    protected void handleCommandException(CommandException ex, String[] args, CommandSender sender) {
        //Get the friendly message if supported
        if (ex instanceof FriendlyException) {
            sender.sendMessage(Core.getLanguageFormatter().getFormat(sender, ((FriendlyException) ex).getFriendlyMessage()));
        } else {
            sender.sendMessage(ChatColor.RED + ex.getClass().getSimpleName() + ": " + ex.getMessage() + "!");
        }
        if (ex instanceof UnhandledCommandExceptionException) {
            ((UnhandledCommandExceptionException) ex).getCausingException().printStackTrace();
        }
    }

    /**
     * Prepares the environment or performs any necessary checks before dispatching
     * the execution of a subcommand.
     *
     * @param sender The source of the command, such as a player, console, or block command sender.
     * @param args The arguments passed alongside the main command, used for subcommand logic.
     * @param subCommand The subcommand to be dispatched for execution.
     */
    protected void preSubCommandDispatch(CommandSender sender, String[] args, CoreCommand subCommand) {
    }

    /**
     * Retrieves a subcommand associated with the provided string identifier.
     * The method first attempts to find an exact match (case-sensitive) for the given string.
     * If no exact match is found, it performs a case-insensitive comparison with the identifiers
     * of registered subcommands.
     *
     * @param s the string identifier of the subcommand to be retrieved. This may be
     *          either an exact match or a case-insensitive match for a registered subcommand.
     *
     * @return the corresponding {@code CoreCommand} instance if a matching subcommand is found;
     *         otherwise, returns {@code null}.
     */
    public final CoreCommand getSubCommandFor(String s) {
        // If we have an exact match, case and all, don't waste the CPU cycles on the lower for loop.
        if (subCommands.containsKey(s)) return subCommands.get(s);
        // Otherwise, loop through the sub-commands and do a case insensitive check.
        for (String s1 : subCommands.keySet()) {
            if (s1.equalsIgnoreCase(s)) return subCommands.get(s1);
        }
        // And we didn't find anything, so let's return nothing.
        return null;
    }

    /**
     * Retrieves a list of subcommands that match, either exactly or partially, a given string identifier.
     * The method first checks for an exact match. If no exact match is found, it searches for subcommands
     * whose names start with the provided string (case-insensitive).
     *
     * @param s the string identifier used to search for matching subcommands. It may represent an exact match
     *          or the beginning of a subcommand's name.
     * @return a list of {@code CoreCommand} instances that match the given string. The list contains either
     *         a single exact match or all subcommands whose names start with the string (case-insensitive).
     *         If no matches are found, an empty list is returned.
     */
    public final List<CoreCommand> getSubCommandsForPartial(String s) {
        List<CoreCommand> commands = new ArrayList<>(); // Create a place to hold our possible commands
        CoreCommand subCommand;
        if ((subCommand = getSubCommandFor(s)) != null) { // Check if we can get an exact sub-command
            commands.add(subCommand);
            return commands; // Exact sub-command is all we need.
        }
        String s2 = s.toUpperCase(); // Get the case-insensitive comparator.
        // We found one that starts with the argument.
        commands.addAll(subCommands.keySet().stream().filter(s1 -> s1.toUpperCase().startsWith(s2)).map(subCommands::get).collect(Collectors.toList()));
        return commands;
    }

    /**
     * Handles the execution of a command for a specific player. This method
     * processes the input arguments and performs the necessary actions based
     * on the command's logic. If the command is not implemented or supported,
     * an {@code EmptyHandlerException} is thrown.
     *
     * @param player The player executing the command.
     * @param args   The arguments provided with the command.
     * @throws CommandException If an error occurs during the execution of the command.
     */
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        throw new EmptyHandlerException();
    }

    /**
     * Handles the execution of a command sent by a console command sender. This method
     * processes the input arguments and allows for specific handling logic to be
     * implemented by subclasses. By default, it throws an {@code EmptyHandlerException}
     * to indicate no specific handling is provided for console commands.
     *
     * @param commandSender The sender of the command, which is a console entity.
     * @param args The arguments provided by the console for the command execution.
     * @throws CommandException If an error occurs during the execution of the command.
     */
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        throw new EmptyHandlerException();
    }

    /**
     * Handles a command sent by a BlockCommandSender with the provided arguments.
     *
     * @param commandSender the sender that issued the command
     * @param args the arguments provided with the command
     * @throws CommandException if there is an error during command execution
     */
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {
        throw new EmptyHandlerException();
    }

    /**
     * Handles an unspecified command by throwing an exception.
     *
     * @param sender The source of the command, typically the user or entity that issued the command.
     * @param args The arguments provided with the command.
     * @throws CommandException If an error occurs while processing the command.
     */
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        throw new EmptyHandlerException();
    }

    /**
     * Handles the execution of a subcommand related to a post operation.
     *
     * @param sender the source initiating the command, typically a player or console
     * @param args the array of arguments provided with the command
     * @throws CommandException if an error occurs while handling the subcommand
     */
    protected void handlePostSubCommand(CommandSender sender, String[] args) throws CommandException {
        throw new EmptyHandlerException();
    }

    /**
     * Handles tab completion for commands, providing suggestions based on the currently entered arguments.
     * Attempts to suggest online player names that match the current argument input.
     *
     * @param sender The sender of the command who is requesting tab completion.
     * @param command The command being executed for which tab completion is being handled.
     * @param alias The alias of the command used by the sender.
     * @param args The arguments currently entered by the sender.
     * @return A list of possible suggestions for tab completion. Returns an empty list if no suggestions are found or if
     *         subcommands are being used exclusively.
     */
    protected List<String> handleTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (isUsingSubCommandsOnly() || subCommands.size() > 0) return Collections.emptyList();
        List<String> ss = new ArrayList<>(); // Create a list to put possible names
        String arg = args.length > 0 ? args[args.length - 1].toLowerCase() : ""; // Get the last argument
        for (Player player : Bukkit.getOnlinePlayers()) { // Loop through all the players
            String name1 = player.getName(); // Get this players name (since we reference it twice)
            if (name1.toLowerCase().startsWith(arg))
                ss.add(name1); // And if it starts with the argument we add it to this list
        }
        return ss; //Return what we found.
    }

    /**
     * Determines if only sub-commands are being used.
     *
     * @return true if the method uses only sub-commands, false otherwise
     */
    protected boolean isUsingSubCommandsOnly() {
        return false;
    }

    /**
     * Returns the formatted name of the command.
     * If the command has a super command, the formatted name is a concatenation
     * of the super command's formatted name and the current command's name, separated by a space.
     *
     * @return the formatted name as a String, including the super command's formatted name if applicable.
     */
    protected String getFormattedName() {
        return superCommand == null ? name : superCommand.getFormattedName() + " " + name;
    }

    /**
     * Returns a string representation of the object, specifically
     * a formatted command text prefixed with "Command > ".
     *
     * @return a string representation of the object in the format "Command > formattedName".
     */
    @Override
    public String toString() {
        return "Command > " + getFormattedName();
    }
}
