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
 * Represents the core structure for a command system, allowing for a hierarchical setup of commands
 * with main commands, subcommands, and associated handling logic. This class is designed to be extended
 * to define custom commands, handling subcommand registration, execution, tab-completion, and error handling.
 * <p>
 * Key features include:
 * <ul>
 *     <li>Registration and unregistration of subcommands</li>
 *     <li>Dynamic help command generation</li>
 *     <li>Command execution for different command sender types (e.g., players, console, block entities)</li>
 *     <li>Flexible subcommand retrieval based on full or partial identifiers</li>
 *     <li>Error handling and logging for exceptions during command execution</li>
 * </ul>
 * <p>
 * The {@code CoreCommand} class allows commands to be organized hierarchically, where parent commands
 * can have their own subcommands. The structure ensures a robust command system while maintaining
 * configuration flexibility via annotations or runtime logic.
 * <p>
 * This class is primarily intended for integration with the Minecraft server API and provides
 * functionality for managing command input and delegating logic specific to the command context.
 */
public abstract class CoreCommand implements CommandExecutor, TabCompleter {

    /**
     * A map that holds the sub-commands available for a specific context or
     * command environment.
     *
     * <p>
     * The key in the map is a {@link String} representing the name of the sub-command.
     * The value is a {@link CoreCommand} instance that encapsulates the logic and
     * behavior of the corresponding sub-command.
     * </p>
     *
     * <p>
     * This structure allows efficient retrieval of sub-command implementations
     * based on their name, enabling dynamic and extensible command handling.
     * </p>
     *
     * <p>
     * Example use cases:
     * <ul>
     *   <li>Registering a set of supported sub-commands for a main command.</li>
     *   <li>Parsing user input to locate and execute the appropriate sub-command.</li>
     *   <li>Providing extensibility by allowing new sub-commands to be added dynamically.</li>
     * </ul>
     * </p>
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
     * Constructs a new CoreCommand with the specified name.
     * <p>
     * This constructor initializes the core command with a unique identifier name.
     *
     * @param name The name of the command, used to uniquely identify this command.
     */
    protected CoreCommand(String name) {
        this.name = name;
    }

    /**
     * Constructs a new {@code CoreCommand} instance with the specified name and optional subcommands.
     *
     * <p>This constructor initializes the command with a given name and allows attaching
     * one or more subcommands to the current command. Subcommands, if any, are registered
     * during the initialization process.</p>
     *
     * @param name the name of the command; must not be {@code null}.
     * @param subCommands optional varargs parameter representing one or more subcommands
     *                    to be associated with this command; can be empty.
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
     * Regenerates the "help" command for the current command if it is not already present.
     *
     * <p>This method checks whether a "help" subcommand is already registered in the subcommands map.
     * If not, it creates a new "help" command, registers it as a subcommand, and configures its
     * functionality. The purpose of the "help" command is to provide users with a list of available
     * subcommands, their descriptions, and usage information.
     *
     * <p>The newly created "help" command:
     * <ul>
     *   <li>Displays the command's main name and a brief description if not exclusively using subcommands.</li>
     *   <li>Lists all available subcommands along with their descriptions.</li>
     *   <li>Sends the generated help message to the command sender.</li>
     * </ul>
     *
     * <p>This method also sets the description of the "help" command to "Open the help menu".
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
     * Handles exceptions thrown during the execution of commands and notifies the command sender.
     * <p>
     * This method processes exceptions in the following manner:
     * <ul>
     *     <li>If the exception is an instance of {@code FriendlyException}, it retrieves a user-friendly message
     *     and sends it to the command sender.</li>
     *     <li>If the exception is any other type, it sends the exception's class name and message to the sender,
     *     formatted in red.</li>
     *     <li>If the exception is an instance of {@code UnhandledCommandExceptionException}, it prints the
     *     stack trace of the underlying cause to the console for debugging purposes.</li>
     * </ul>
     *
     * @param ex The {@link CommandException} that was thrown during the execution of a command.
     *           This is the exception to be handled.
     * @param args The array of arguments that were passed to the command which caused the exception.
     *             Can provide context for the exception handling.
     * @param sender The {@link CommandSender} who executed the command.
     *               This is the recipient of any messages generated by the exception handling.
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
     * Prepares the dispatcher for handling a sub-command associated with this command.
     * <p>
     * This method is invoked before dispatching a specific sub-command,
     * allowing for any necessary pre-processing or setup.
     * </p>
     *
     * @param sender The {@link CommandSender} initiating the command. This could represent a player, console, or other source.
     * @param args An array of {@code String} arguments passed to the command, typically capturing any parameters or sub-command names.
     * @param subCommand The {@link CoreCommand} representing the sub-command to be dispatched. Contains the logic associated with the specific sub-command.
     */
    protected void preSubCommandDispatch(CommandSender sender, String[] args, CoreCommand subCommand) {
    }

    /**
     * Retrieves the sub-command associated with the given string.
     * <p>
     * This method first checks for an exact match (case-sensitive) within the sub-commands.
     * If no exact match is found, it performs a case-insensitive lookup to find a matching sub-command.
     * If no matching sub-command is found, this method returns {@code null}.
     *
     * @param s the name of the sub-command to retrieve. This can be in any letter case.
     *          If the string matches exactly or in a case-insensitive manner with a sub-command,
     *          the corresponding {@link CoreCommand} will be returned.
     *
     * @return the {@link CoreCommand} corresponding to the given string if found, or {@code null}
     *         if no matching sub-command exists.
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
     * Retrieves a list of sub-commands that match a given partial input string.
     * <p>
     * This method attempts to find either an exact match for the provided partial input
     * string or any sub-commands whose names start with the partial input string,
     * ignoring case sensitivity.
     *
     * @param s The partial input string used to match sub-command names.
     *          Cannot be {@code null}.
     *
     * @return A {@code List} of {@code CoreCommand} objects that match the provided
     *         partial input string. If an exact match is found, only the exact match
     *         is returned. Otherwise, all sub-commands with names starting with the
     *         input string are returned. If no matches are found, the returned list
     *         will be empty.
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
     * Handles the execution of a specific command for a player.
     * <p>
     * This method is invoked when a command matching this handler is executed by a {@link CPlayer}.
     * The method processes the command arguments and performs the necessary functionality or validation.
     * Subclasses may override this method to implement specific command handling logic.
     * <p>
     * By default, this method throws an {@link EmptyHandlerException}, indicating that no specific handling
     * logic is implemented for this command.
     *
     * @param player The {@link CPlayer} executing the command. This represents the player initiating the command.
     * @param args   An array of {@link String} representing the arguments provided with the command.
     *               Arguments may include additional parameters or sub-command identifiers.
     *
     * @throws CommandException If any error occurs during command execution, such as validation failure or
     *                          insufficient permissions. Subclasses should handle this exception as needed.
     */
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        throw new EmptyHandlerException();
    }

    /**
     * Handles the execution of a command sent from the console.
     * <p>
     * This method is responsible for processing a command that originates
     * from a {@link ConsoleCommandSender}, including arguments parsing and
     * sub-command delegation if applicable. By default, this implementation
     * throws an {@link EmptyHandlerException} indicating that no specific
     * handling is implemented.
     * </p>
     *
     * @param commandSender the {@link ConsoleCommandSender} who executed the command.
     *                      This represents the console from which the command originates.
     * @param args          an array of {@code String} representing the arguments
     *                      passed with the command. The first element typically
     *                      represents the sub-command, if any.
     * @throws CommandException if there is an issue processing the command, or
     *                          if the handling for the command is not implemented.
     */
    protected void handleCommand(ConsoleCommandSender commandSender, String[] args) throws CommandException {
        throw new EmptyHandlerException();
    }

    /**
     * Handles a command execution specifically for {@link BlockCommandSender}.
     * <p>
     * This method processes the command input provided by a {@code BlockCommandSender} and
     * performs any logic or operations defined in its implementation.
     * If no operations are implemented, an {@code EmptyHandlerException} is thrown.
     * </p>
     *
     * @param commandSender The {@link BlockCommandSender} instance that invoked the command.
     *                      Represents a block entity capable of issuing commands (e.g., command block).
     * @param args          An array of {@link String} arguments accompanying the command.
     *                      These could represent parameters or subcommand flags.
     *
     * @throws CommandException If the execution fails or encounters an invalid condition.
     */
    protected void handleCommand(BlockCommandSender commandSender, String[] args) throws CommandException {
        throw new EmptyHandlerException();
    }

    /**
     * Handles a command in a general, unspecific context when no type-specific handling
     * is available for the provided {@link CommandSender}.
     *
     * <p>This method is intended to be used as a fallback for handling commands that do not
     * have specialized implementations based on the specific type of {@code CommandSender}.
     * For instance, it is invoked if there is no explicit method to handle commands issued by
     * players, console senders, or block command senders.</p>
     *
     * <p>By default, this implementation throws an {@link EmptyHandlerException}, signaling that
     * the command has no defined handling for this context.</p>
     *
     * @param sender The {@link CommandSender} executing the command. This may include players,
     *               console senders, or block command senders, among others.
     * @param args   The arguments passed to the command, represented as an array of {@link String}.
     *               These arguments provide additional context or data for the command processing.
     * @throws CommandException If an issue occurs during the handling of the command, general or
     *                          specific exceptions related to the command logic may be thrown.
     */
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        throw new EmptyHandlerException();
    }

    /**
     * Handles any necessary logic after the execution of a subcommand.
     *
     * <p>This method may be used for tasks such as cleanup, logging, or other operations
     * that should occur immediately after a subcommand is executed. If no specific behavior
     * is defined, it will throw an {@link EmptyHandlerException} to signify that the
     * post-subcommand handler has not been implemented.</p>
     *
     * @param sender The {@link CommandSender} who executed the command. This represents
     *               the entity (e.g., player, console) responsible for issuing the command.
     * @param args   An array of {@link String} arguments that were passed to the executed
     *               subcommand. This array may contain additional parameters or context
     *               used to influence the behavior of the method.
     * @throws CommandException If the post-subcommand handling encounters an error or
     *                          if the handler has not been implemented.
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
