package network.palace.core.command;

import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/// Old documentation
/// This is a command map for the plugin, and will most likely be modified to provide some level of more interesting functionality.

/**
 * CoreCommandMap is responsible for managing and registering custom commands
 * within a Bukkit/Spigot plugin ecosystem. It serves as an abstraction layer
 * for registering commands, handling aliases, descriptions, and usages,
 * and managing their presence in the Bukkit command map.
 */
public final class CoreCommandMap {

    /**
     * Represents a mapping of top-level command names to their corresponding {@link CoreCommand} instances.
     * This map is used to manage and store command registrations within the CoreCommandMap.
     * <p>
     * The keys in the map represent the names of the commands, while the values are the corresponding
     * {@link CoreCommand} objects that handle the functionality of each command.
     * <p>
     * This map is immutable upon declaration and can be updated using methods provided by the containing
     * CoreCommandMap class.
     */
    @Getter
    private final Map<String, CoreCommand> topLevelCommands = new HashMap<>();

    /**
     * Represents the instance of the JavaPlugin that is associated with this command map.
     * The plugin serves as the main plugin reference and is used to manage the lifecycle
     * and registration of commands within the Bukkit/Spigot server environment.
     * <p>
     * It is a final field, ensuring that the same plugin instance is consistently used
     * throughout the lifespan of this command map.
     */
    private final JavaPlugin plugin;

    /**
     * Constructor for the CoreCommandMap class. This initializes an instance of the
     * CoreCommandMap with the specified plugin.
     *
     * @param plugin The instance of the JavaPlugin that this command map is associated with.
     */
    public CoreCommandMap(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers a command with the Bukkit server, ensuring it is properly set as an executor,
     * tab completer, and meta information such as aliases, description, and usage are applied.
     * The command is also logged and stored in the internal command map.
     *
     * @param command The CoreCommand instance to be registered with the server.
     */
    public void registerCommand(CoreCommand command) {
        // Check if we have the command registered using the same name
        if (topLevelCommands.containsKey(command.getName())) return; // Return if so
        PluginCommand command1 = getCommand(command.getName(), plugin); // Create a command for force registration
        command1.setExecutor(command); //Set the executor
        command1.setTabCompleter(command); //Tab completer
        CommandMeta annotation = command.getClass().getAnnotation(CommandMeta.class); // Get the commandMeta
        if (annotation != null) {
            command1.setAliases(Arrays.asList(annotation.aliases()));
            command1.setDescription(annotation.description());
            command1.setUsage(annotation.usage());
            command.setDescription(annotation.description());
        }
        // Remove old commands before register
        List<String> tempList = new ArrayList<>(Collections.singletonList(command.getName()));
        if (annotation != null) {
            tempList.addAll(Arrays.asList(annotation.aliases()));
        }
        tempList.forEach(this::removeKnownCommands);
        getCommandMap().register(plugin.getDescription().getName(), command1); // Register it with Bukkit
        String pluginName = "Unknown";
        if (plugin instanceof Core) {
            pluginName = "Core";
        } else if (plugin instanceof Plugin) {
            pluginName = ((Plugin) plugin).getInfo().name();
        }
        Core.logMessage(pluginName, "Registered " + command.toString());
        this.topLevelCommands.put(command.getName(), command); // Put it in the hash map now that we've registered it.
    }

    /**
     * Creates a new instance of a Bukkit {@link PluginCommand} using reflection. This method
     * allows the instantiation of a PluginCommand with a specified name and associated plugin.
     *
     * @param name The name of the command to create.
     * @param plugin The plugin that the command is associated with.
     * @return A new instance of {@link PluginCommand} or null if an exception occurs.
     */
    private PluginCommand getCommand(String name, org.bukkit.plugin.Plugin plugin) {
        PluginCommand command = null;
        try {
            Constructor<PluginCommand> commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, org.bukkit.plugin.Plugin.class);
            commandConstructor.setAccessible(true);
            command = commandConstructor.newInstance(name, plugin);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return command;
    }

    /**
     * Retrieves the CommandMap instance from the Bukkit server using reflection.
     * The CommandMap provides access to the server's command registration and handling system.
     *
     * @return The CommandMap instance associated with the Bukkit server, or null if an exception occurs during retrieval.
     */
    private CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            Server server = Bukkit.getServer();
            Field commandMapField = server.getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(server);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return commandMap;
    }

    /**
     * Retrieves the known commands from the provided CommandMap instance using reflection.
     * This method accesses the "knownCommands" field of the CommandMap, allowing access
     * to the mapping of command names to their respective Command objects.
     *
     * @param commandMap The CommandMap instance from which the known commands are to be retrieved.
     *                   This should be an instance of SimpleCommandMap or a compatible subclass.
     * @return A map of command names to their respective Command objects. If an exception occurs,
     *         an empty map is returned.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Command> getKnownCommands(CommandMap commandMap) {
        Map<String, Command> knownCommands = new HashMap<>();
        try {
            Field knownCommandsField = ((SimpleCommandMap) commandMap).getClass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return knownCommands;
    }

    /**
     * Removes a known command from the internal command map. This allows for the
     * unregistering of a command name from the server's command system.
     *
     * @param commandName The name of the command to be removed from the known commands map.
     */
    private void removeKnownCommand(String commandName) {
        Map<String, Command> knownCommands = getKnownCommands(getCommandMap());
        knownCommands.remove(commandName);
    }

    /**
     * Removes multiple known command mappings based on the provided command name.
     * The method removes the command with three variations of its name: prefixed with
     * "minecraft:", prefixed with "bukkit:", and the name as-is. This ensures the
     * complete unregistration of the command across those namespaces.
     *
     * @param commandName The base command name to be removed. The method will
     *                    attempt to remove the command with the provided name and
     *                    common namespace prefixes.
     */
    public void removeKnownCommands(String commandName) {
        removeKnownCommand("minecraft:" + commandName);
        removeKnownCommand("bukkit:" + commandName);
        removeKnownCommand(commandName);
    }

    /**
     * Retrieves a CoreCommand instance by its name from the top-level commands map.
     *
     * @param name The case-sensitive name of the command to retrieve.
     * @return The CoreCommand instance associated with the given name, or null if no matching command is found.
     */
    public CoreCommand getCommandByName(String name) {
        return topLevelCommands.get(name);
    }
}
