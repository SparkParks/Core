package network.palace.core.plugin;

import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.command.CoreCommand;
import network.palace.core.command.CoreCommandMap;
import network.palace.core.errors.ErrorLog;
import network.palace.core.errors.RollbarHandler;
import network.palace.core.library.LibraryHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The Plugin class serves as the base class for all plugins within the Core framework.
 * It extends from JavaPlugin and provides essential methods and utilities to assist in
 * plugin initialization, command registration, listener management, and task scheduling.
 *
 * The Plugin class ensures that required dependencies are loaded and validated during startup.
 * Additionally, it incorporates methods for scheduling synchronous and repeating tasks,
 * as well as facilities for facilitating interaction with the Core plugin system.
 */
public class Plugin extends JavaPlugin {

    /**
     * Holds metadata about the plugin, such as its name, version, dependencies, and other relevant information.
     * This information is typically defined via the {@link PluginInfo} annotation at the class level.
     *
     * The {@link PluginInfo} object includes details such as:
     * - The name of the plugin.
     * - The current version of the plugin.
     * - Dependencies on other plugins and soft dependencies.
     * - Whether the plugin can be reloaded by the Core system.
     * - The target Spigot API version.
     *
     * This field is initialized and provides access to plugin-specific details for use during
     * the plugin's lifecycle operations, such as enabling or disabling.
     *
     * @see PluginInfo
     */
    @Getter private PluginInfo info;

    /**
     * Represents the error logging configuration and behavior for the plugin.
     * This annotation provides the settings for managing error logs and integrates
     * with external services for logging purposes (e.g., Rollbar).
     *
     * The {@code errorLog} field retrieves and applies the error logging settings
     * defined by the {@link ErrorLog} annotation on the plugin class. These settings
     * include the access token for authentication, the environment type, and whether
     * logging is enabled.
     */
    @Getter private ErrorLog errorLog;

    /**
     * Represents the central command map of the plugin, used to manage and handle commands
     * registered to the plugin. This provides functionality for managing {@link CoreCommand}
     * instances and ensuring command execution is properly mapped and routed.
     */
    @Getter private CoreCommandMap commandMap;

    /**
     * The RollbarHandler instance responsible for managing error reporting
     * and logging through the Rollbar service. This allows the plugin
     * to send error and event data to the Rollbar platform for monitoring
     * and debugging purposes.
     */
    @Getter private RollbarHandler rollbarHandler;

    /**
     * This method is called when the plugin is enabled. It performs the following tasks:
     * - Loads required libraries using the LibraryHandler.
     * - Verifies if the Core plugin is enabled. If not, halts execution.
     * - Retrieves and validates the plugin's {@link PluginInfo} annotation for configuration.
     * - (Commented out code) Includes optional initialization for error logging via a RollbarHandler.
     * - Initializes the command map by creating a new instance of {@link CoreCommandMap}.
     * - Invokes the {@link #onPluginEnable()} method to handle additional startup tasks defined in the subclass.
     * - Logs a message indicating successful plugin enablement using the Core logging system.
     *
     * This method is designed to ensure proper initialization and error handling during the startup phase.
     * If any exception occurs during startup, it will be printed to the standard error stream.
     */
    @Override
    public final void onEnable() {
        try {
            // Start library downloading and loading
            LibraryHandler.loadLibraries(this);
            // Check if Core is enabled if not can't work
            if (!Core.getInstance().isEnabled()) return;
            // Get plugin info
            info = getClass().getAnnotation(PluginInfo.class);
            if (info == null) {
                throw new IllegalStateException("You must annotate your class with the @PluginInfo annotation!");
            }
            // Check rollbar info
//            errorLog = getClass().getAnnotation(ErrorLog.class);
//            if (errorLog != null) {
//                if (!errorLog.enabled()) return;
//                rollbarHandler = new RollbarHandler(errorLog.accessToken(), errorLog.environment());
//                rollbarHandler.watch();
//            }
            // Start command map
            commandMap = new CoreCommandMap(this);
            // Plugin enabled finally
            onPluginEnable();
            // Log enabled
            Core.logMessage(getInfo().name(), ChatColor.DARK_GREEN + "Plugin Enabled");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called when the plugin is disabled. It performs necessary cleanup
     * tasks and shutdown operations for the plugin.
     *
     * The method attempts to invoke the {@link #onPluginDisable()} method, which handles
     * additional shutdown logic specific to the subclass. If any exception is thrown
     * during the execution of that method, it will be printed to the error stream.
     *
     * After the plugin-specific shutdown logic, if the {@link #getInfo()} method
     * returns a non-null {@link PluginInfo} instance, a message
     * is logged to indicate that the plugin has been disabled.
     */
    @Override
    public final void onDisable() {
        try {
            onPluginDisable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getInfo() == null) return;
        Core.logMessage(getInfo().name(), ChatColor.DARK_RED + "Plugin Disabled");
    }

    /**
     * Called when the plugin is enabled.
     *
     * This method serves as a placeholder for additional startup logic and is intended to be
     * overridden by subclasses to define specific behavior when the plugin is enabled.
     * If not overridden, it logs a message indicating that no specific enable
     * behavior has been executed.
     *
     * @throws Exception if an error occurs during the enable phase.
     */
    protected void onPluginEnable() throws Exception {
        Core.logMessage(getInfo().name(), ChatColor.RED + "Did not run any code on enable!");
    }

    /**
     * Handles custom behavior and cleanup tasks when the plugin is being disabled.
     *
     * This method is invoked by the {@code onDisable()} method in the plugin lifecycle.
     * It is intended to be overridden by subclasses to define specific shutdown logic
     * that should be executed when the plugin is disabled.
     *
     * Subclasses may use this method to release resources, save plugin state,
     * cancel tasks, or perform other necessary operations to prepare
     * for plugin shutdown. If an exception is thrown during execution,
     * it is logged by the {@code onDisable()} method.
     *
     * @throws Exception if any error occurs during the shutdown operations
     */
    protected void onPluginDisable() throws Exception {
    }

    /**
     * Registers a new command with the plugin's command map.
     *
     * @param command the command to be registered
     */
    public void registerCommand(CoreCommand command) {
        getCommandMap().registerCommand(command);
    }

    /**
     * Registers an event listener with the plugin's event system.
     *
     * This method registers the specified listener to handle events
     * and associates the listener with the current plugin.
     *
     * @param listener the listener to be registered
     */
    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    /**
     * Schedules a repeating task that starts after the specified delay and runs at a fixed interval.
     *
     * @param runnable the task to be executed
     * @param delay    the delay in ticks before the task starts
     * @param period   the interval in ticks between successive executions of the task
     * @return the task ID of the scheduled repeating task
     */
    public int runTaskTimer(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(this, runnable, delay, period).getTaskId();
    }

    /**
     * Schedules a repeating task to be executed by Bukkit, where the task starts after a specific delay
     * and continues to run at a fixed interval.
     *
     * @param runnable the task to be executed, represented as a {@link BukkitRunnable}
     * @param delay the delay in ticks before the task starts executing
     * @param period the interval in ticks between successive executions of the task
     * @return the unique task ID of the scheduled repeating task
     */
    public int runTaskTimerBukkit(BukkitRunnable runnable, long delay, long period) {
        return runnable.runTaskTimer(this, delay, period).getTaskId();
    }

    /**
     * Schedules a task to be executed after a specified delay.
     *
     * @param runnable the task to be executed
     * @param delay    the delay in ticks before executing the task
     * @return the unique task ID of the scheduled task
     */
    public int runTaskLater(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(this, runnable, delay).getTaskId();
    }

    /**
     * Schedules a task to be executed synchronously after a specified delay.
     *
     * @param runnable the task to execute
     * @param delay    the delay in ticks before the task is executed
     * @return the unique task ID of the scheduled task
     */
    public int scheduleSyncDelayedTask(Runnable runnable, long delay) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(this, runnable, delay);
    }

    /**
     * Schedules a synchronous repeating task to be executed at a fixed interval
     * after an initial delay.
     *
     * @param runnable the task to execute
     * @param delay the delay in ticks before the task first runs
     * @param period the interval in ticks between successive executions of the task
     * @return the unique task ID of the scheduled repeating task
     */
    public int scheduleSyncRepeatingTask(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(this, runnable, delay, period);
    }
}
