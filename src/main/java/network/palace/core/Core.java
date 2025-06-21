package network.palace.core;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.utility.MinecraftVersion;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.achievements.AchievementManager;
import network.palace.core.command.CoreCommand;
import network.palace.core.command.CoreCommandMap;
import network.palace.core.commands.*;
import network.palace.core.commands.disabled.MeCommand;
import network.palace.core.commands.disabled.PrefixCommandListener;
import network.palace.core.commands.disabled.StopCommand;
import network.palace.core.config.LanguageManager;
import network.palace.core.config.YAMLConfigurationFile;
import network.palace.core.crafting.CraftingMenu;
import network.palace.core.economy.EconomyManager;
import network.palace.core.economy.HonorManager;
import network.palace.core.errors.RollbarHandler;
import network.palace.core.library.LibraryHandler;
import network.palace.core.messagequeue.MessageHandler;
import network.palace.core.mongo.MongoHandler;
import network.palace.core.npc.SoftNPCManager;
import network.palace.core.packets.adapters.PlayerInfoAdapter;
import network.palace.core.packets.adapters.SettingsAdapter;
import network.palace.core.permissions.PermissionManager;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerManager;
import network.palace.core.player.impl.CorePlayerWorldDownloadProtect;
import network.palace.core.player.impl.managers.CorePlayerManager;
import network.palace.core.plugin.PluginInfo;
import network.palace.core.resource.ResourceManager;
import network.palace.core.utils.Callback;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.SqlUtil;
import network.palace.core.utils.StatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * This will manage all Modules and also the Core Managers.
 * <p>
 * You can access instances of other modules by depending on Core in your pom.xml, and then executing Core.get
 */

/**
 * Represents the main Core system responsible for managing the lifecycle and configuration
 * of the application. This class provides initialization, enabling, disabling, and various
 * utility methods to manage the core functionality of the server.
 * <p>
 * The Core class handles interactions with key managers, configurations, command registration,
 * event listeners, and integration with external services like databases and plugins.
 * It serves as the central point for coordinating the application's operations.
 */
@PluginInfo(name = "Core", version = "2.8.4", depend = {"ProtocolLib"}, softdepend = {"ViaVersion"})
public class Core extends JavaPlugin {
    /**
     * The {@code coreClassLoader} is a private instance of {@link URLClassLoader}
     * used to manage the loading of core classes and resources required by the application.
     * It provides functionality to load classes and fetch resources specified by URLs defined as part of the class loader's configuration.
     */
    @Getter private URLClassLoader coreClassLoader;

    /**
     * The singleton instance of the Core class.
     * This instance is used to access the primary functionality of the Core system.
     * It holds references to various managers and utilities to coordinate server operations.
     */
    @Getter private static Core instance;

    /**
     * Indicates whether the core system is currently in the starting phase.
     * This variable is used to track the initialization state of the system.
     * By default, it is set to true when the system begins its startup process.
     */
    private boolean starting = true;

    /**
     * Represents the timestamp when this instance of the Core class was initialized.
     * This value is assigned at the time of object creation and remains constant
     * throughout the lifecycle of the instance.
     * <p>
     * The timestamp is stored as the number of milliseconds since the Unix epoch
     * (January 1, 1970, 00:00:00 GMT).
     */
    @Getter private final long startTime = System.currentTimeMillis();

    /**
     * Represents the playground mode status for the core.
     * This field is static, meaning it is shared across all instances of the class,
     * and is not instance-specific. The field is set to false by default.
     * <p>
     * When true, the core operates in playground mode, which may enable experimental
     * or testing functionalities. When false, these functionalities are disabled.
     */
    @Getter private static boolean playground = false;

    /**
     * Represents the core configuration file for the application.
     * This variable is used to load, manage, and access information
     * stored in a YAML-based configuration file.
     */
    private YAMLConfigurationFile configFile;

    /**
     * Specifies the type of the server.
     * <p>
     * This variable is used to define the current role or type of the server, such as "Hub".
     * The value of this variable can influence the behavior or configuration of the server instance
     * depending on its designated type.
     */
    private String serverType = "Hub";

    /**
     * Represents the name of the instance being managed or processed.
     * This field is used to identify the specific instance within the context
     * of the application or server.
     */
    private String instanceName = "";

    /**
     * Flag indicating whether the application is running in debug mode.
     * When enabled, additional logging or debugging utilities may be used
     * to facilitate development and issue tracking.
     */
    private boolean debug = false;

    /**
     * A flag indicating whether both the dashboard and SQL functionalities are disabled.
     * When set to true, these features will not be available or initialized within the system.
     * Defaults to false.
     */
    private boolean dashboardAndSqlDisabled = false;

    /**
     * Represents the version of the currently running Minecraft server.
     * Uses the Bukkit API to retrieve the server version.
     */
    @Getter private static String minecraftVersion = Bukkit.getBukkitVersion();

    /**
     * Represents whether the server is in game mode.
     * Game mode allows the server to skip certain startup processes to enable faster initialization.
     */
    private boolean gameMode = false;

    /**
     * Indicates whether the title should be displayed to players during the login process.
     * <p>
     * The value is set to `false` by default, meaning the title will not appear on login unless explicitly enabled.
     */
    @Getter private boolean showTitleOnLogin = false;

    /**
     * Represents the title message displayed to players upon login.
     * <p>
     * This variable is used to set a custom title that will be shown
     * to players when they log into the server. By default, it is an empty string.
     */
    @Getter private String loginTitle = "";

    /**
     * Represents the subtitle text shown to users during the login process.
     * The value is initialized as an empty string and can be customized based
     * on the application's requirements to provide additional context or
     * instructions at the login screen.
     */
    @Getter private String loginSubTitle = "";

    /**
     * Represents the duration of the fade-in effect for the login title in ticks.
     * A tick is approximately 1/20th of a second in Minecraft.
     * This value determines the speed at which the login title appears.
     */
    @Getter private int loginTitleFadeIn = 10;

    /**
     * Defines the duration (in ticks) for which the title displayed upon player login remains visible.
     * This is part of the title display properties used when players join the server.
     */
    @Getter private int loginTitleStay = 10;

    /**
     * The duration in ticks for which the title fades out when displayed during player login.
     * This fade-out duration helps create a smooth transition effect when a title disappears.
     * <p>
     * Default value is set to 10 ticks.
     */
    @Getter private int loginTitleFadeOut = 10;

    /**
     * Represents the header text that is displayed in the server's tab list.
     * The string includes formatting codes for color and style, which are compatible with Minecraft's chat system.
     * By default, it advertises the network name and its family-oriented focus.
     */
    @Getter @Setter private String tabHeader = ChatColor.GOLD + "Palace Network - A Family of Servers";

    /**
     * Represents the footer text displayed in the player's tab list on the server.
     * The text combines color-coded information indicating the server type and context.
     * By default, it specifies that the player is on the "Hub" server, with aesthetically styled colors.
     */
    @Getter @Setter private String tabFooter = ChatColor.LIGHT_PURPLE + "You're on the " + ChatColor.GREEN + "Hub " +
            ChatColor.LIGHT_PURPLE + "server";

    /**
     * A static instance of the {@link MessageHandler} class that provides functionality
     * for managing and handling messages, such as broadcasting messages to players
     * or logging specific text output in the system.
     * <p>
     * This field is a central message handling utility used across the core system,
     * ensuring consistent management of messages throughout the application.
     * It is statically accessible for ease of use within the core context.
     */
    @Getter private static MessageHandler messageHandler;

    /**
     * Utility field for interacting with the SQL database.
     * Provides access to methods for executing and managing SQL operations.
     */
    private SqlUtil sqlUtil;

    /**
     * A handler for managing interactions with a MongoDB database.
     * Provides utility methods and functionalities to perform various database operations.
     */
    private MongoHandler mongoHandler;

    /**
     * Manages the language-specific functionalities and translations within the application.
     * This field provides a centralized way to access and operate on language-related operations.
     */
    private LanguageManager languageManager;

    /**
     * Manages permissions within the Core class.
     * This variable handles permission-related operations
     * such as managing roles, permissions, and validations
     * for players or entities within the application.
     */
    private PermissionManager permissionManager;

    /**
     * Manages the economy system within the application.
     * Responsible for handling financial operations such as currency transactions and balances.
     * Typically utilized in scenarios where virtual monetary interactions are required.
     */
    private EconomyManager economyManager;

    /**
     * This is responsible for helping players getting the resource pack
     */
    private ResourceManager resourceManager;

    /**
     * Manages achievements within the system.
     * Provides functionality to create, track, and manage player achievements.
     */
    private AchievementManager achievementManager;

    /**
     * Manages the behavior and lifecycle of SoftNPCs (non-player characters) within the system.
     * <p>
     * The SoftNPCManager is responsible for creating, updating, and managing the state of
     * NPCs, including their attributes, interactions, and associated tasks.
     * This variable operates within the Core framework and integrates with other
     * managers or utilities as needed.
     */
    private SoftNPCManager softNPCManager;

    /**
     * Manages player-specific data and operations within the application.
     * This includes handling player-related functionalities such as tracking,
     * permissions, statistics, and other player-associated features.
     * <p>
     * It serves as the primary interface for accessing and manipulating player-related
     * data and provides an organized structure for player management.
     */
    private CPlayerManager playerManager;

    /**
     * The {@code commandMap} is responsible for managing and storing the mappings of commands
     * within the core system. It facilitates the registration, execution, and organization
     * of commands used by the application. This variable plays an integral role in handling
     * command-related functionalities within the core.
     */
    private CoreCommandMap commandMap;

    /**
     * Manages the honor-related functionality within the Core class.
     * Responsible for handling operations and data associated with player honors.
     */
    private HonorManager honorManager;

    /**
     * Instance of the crafting menu used for managing and interacting with
     * crafting-related functionality in the application.
     */
    private CraftingMenu craftingMenu;

    /**
     * Provides access to utility methods and functionalities related to game statistics.
     * The StatUtil instance is initialized and managed by the Core class.
     * It can be used to handle various statistical operations within the system.
     */
    @Getter private StatUtil statUtil;

    /**
     * The RollbarHandler is responsible for managing error reporting and
     * exception logging to the Rollbar service. It facilitates seamless
     * integration with Rollbar, allowing the application to send runtime
     * errors and critical logs for monitoring and diagnostics.
     */
    @Getter private RollbarHandler rollbarHandler;

    /**
     * A list of UUIDs representing players who are disabled or restricted from certain actions
     * or features within the application. This list is immutable and persists throughout
     * the application's lifecycle.
     */
    @Getter private final List<UUID> disabledPlayers = new ArrayList<>();

    /**
     * A boolean flag indicating whether the current Minecraft version is greater than or equal to 1.11.2.
     * This is determined by comparing the minor version of the current Minecraft runtime
     * environment with the threshold value of 12 (corresponding to 1.11.2).
     */
    @Getter private final boolean isMinecraftGreaterOrEqualTo11_2 = MinecraftVersion.getCurrentVersion().getMinor() >= 12;

    /**
     * This method is called when the core plugin is loaded.
     * It initializes the class loader for the core plugin by casting
     * the current class's class loader to a {@code URLClassLoader}.
     */
    @Override
    public void onLoad() {
        this.coreClassLoader = (URLClassLoader) getClass().getClassLoader();
    }

    /**
     * Handles the enabling of the plugin during the server startup or reload.
     * This method is responsible for initializing core components, configurations,
     * managing player data, and preparing the plugin for operation.
     * <p>
     * The following actions are performed in this method:
     * <p>
     * - Kicks all currently online players to ensure a clean reload.
     * - Determines the current Minecraft server version.
     * - Loads required external libraries and dependencies.
     * - Initializes configuration files and retrieves key settings from the config,
     *   including server type, instance name, debug mode, and various gameplay options.
     * - Configures player tab header and footer, as well as login titles if enabled.
     * - Sets up packet listeners for handling specific protocol events
     *   (e.g., player settings, recipe interactions).
     * - Registers outgoing and incoming plugin channels for specific integrations.
     * - Initializes utilities such as SQL, MongoDB handlers, and managers for
     *   permissions, player data, economy, achievements, and more.
     * - Configures core commands and command mapping.
     * - Sets up message handling for communication within the network.
     * - Registers event listeners and plugin commands.
     * - Ensures players are kept off the server until all setup tasks are completed.
     * - Logs the enabling process and sends notifications to staff members via the network.
     */
    @Override
    public final void onEnable() {
        instance = this;
        // Kick all players on plugin/server reload
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(ChatColor.RED + "Server is reloading!");
        }
        // get the minecraft version
        minecraftVersion = Bukkit.getServer().getClass().getPackage().getName();
        // get the substring of the minecraft version
        minecraftVersion = minecraftVersion.substring(minecraftVersion.lastIndexOf(".") + 1);
        // Load needed libraries for Core
        LibraryHandler.loadLibraries(this);
        // Configurations
        configFile = new YAMLConfigurationFile(this, "config.yml");
        // get the playground boolean from the config
        playground = Core.getCoreConfig().getBoolean("playground");
        // Get info from config
        serverType = getCoreConfig().getString("server-type", "Unknown");
        // get the instance name from the config
        instanceName = getCoreConfig().getString("instance-name", "ServerName");
        // get the boolean of debug from the config
        debug = getCoreConfig().getBoolean("debug", false);
        // get the boolean of dashboard/sql services from the config
        dashboardAndSqlDisabled = getCoreConfig().getBoolean("dashboardAndSqlDisabled", false);
        // get the boolean of the gamemode from the config
        gameMode = getCoreConfig().getBoolean("isGameMode", false);
        // if the tab sectioon of the config is not null, set the tab header and footer
        if (getCoreConfig().getConfigurationSection("tab") != null) {
            // set the tab header from the header section of the config
            setTabHeader(ChatColor.translateAlternateColorCodes('&', getCoreConfig().getString("tab.header")));
            // set the tab footer from the footer section of the config
            setTabFooter(ChatColor.translateAlternateColorCodes('&', getCoreConfig().getString("tab.footer")));
        }
        // get the boolean of showing the title on player login to the server from the config
        showTitleOnLogin = getCoreConfig().getBoolean("showTitle", false);
        // if the title shows on player login, get the title, subtitle, and time it fades in/out and stays
        if (showTitleOnLogin) {
            // get the login title from the config
            loginTitle = getCoreConfig().getString("loginTitle", "");
            // get the sub title from the config
            loginSubTitle = getCoreConfig().getString("loginSubTitle", "");

            // get the fade in time from the config
            loginTitleFadeIn = getCoreConfig().getInt("logFadeIn", 20);
            // get the stay time from the config
            loginTitleStay = getCoreConfig().getInt("loginStay", 100);
            // get the fade out time from the config
            loginTitleFadeOut = getCoreConfig().getInt("loginFadeOut", 20);
        }
        // Settings adapter for player locales
        addPacketListener(new SettingsAdapter());
        // Player info adapter for player ping
        addPacketListener(new PlayerInfoAdapter());
        // add a new Recipes packet adapter that sets sending said packet to canceled
        addPacketListener(new PacketAdapter(this, PacketType.Play.Server.RECIPES) {
            /**
             * Handles the sending of a specific packet event during gameplay.
             * This method is triggered when a packet of the specified type is being sent
             * and provides an opportunity to intercept or modify its behavior.
             * In this implementation, the packet is canceled to prevent it from being sent.
             *
             * @param event the packet event being sent, providing information about the packet and context
             */
            @Override
            public void onPacketSending(PacketEvent event) {
                event.setCancelled(true);
            }
        });
        // Register the bungeecord outgoing plugin channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        // Register the bungeecord incoming plugin channel
        getServer().getMessenger().registerIncomingPluginChannel(this, "WDL|INIT", new CorePlayerWorldDownloadProtect());
        // initialize the sql utilities
        sqlUtil = new SqlUtil();
        // try to initialize the stat utilities, catch an IOException if it fails.
        try {
            statUtil = new StatUtil();
        } catch (IOException e) {
            // DO NOT EVER PRINT THE FULL STACK TRACE!!!!!! NEEDS TO BE MORE ROBUST!! PRINTING A STACK TRACE
            // NEEDS TO BE SAVED FOR DEBUGGING PURPOSES
            e.printStackTrace();
        }
        // initialize the mongo systems
        mongoHandler = new MongoHandler();
        // set up the language manager
        languageManager = new LanguageManager();
        // set up the player manager
        playerManager = new CorePlayerManager();
        // set up the permission manager
        permissionManager = new PermissionManager();
        // set up the resource pack manager
        resourceManager = new ResourceManager();
        // set up the economy manager
        economyManager = new EconomyManager();
        // set up the achievement manager
        achievementManager = new AchievementManager();
        // set up the npc manager
        softNPCManager = new SoftNPCManager();
        // Setup the honor manager
        honorManager = new HonorManager();
        // get the honor mappings from mongo
        honorManager.provideMappings(mongoHandler.getHonorMappings());
        // Core command map
        commandMap = new CoreCommandMap(this);
        // try to start the rabbit mq message handler, catch two different errors that may happen
        try {
            messageHandler = new MessageHandler();
            messageHandler.initialize();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            Core.logMessage("MessageHandler", "Error initializing message queue connection!");
        }
        // Crafting Menu
        craftingMenu = new CraftingMenu();
        // Register Listeners
        registerListeners();
        // Register Commands
        registerCommands();
        // register disabled commands
        registerDisabledCommands();
        // Log that core is enabled
        logMessage("Core", ChatColor.DARK_GREEN + "Enabled");
        // run a background task to set the server as online in the mongo database
        runTask(this, () -> mongoHandler.setServerOnline(getInstanceName(), getServerType(), playground, true));

        // Always keep players off the server until it's been finished loading for 1 second
        // This prevents issues with not loading player data when they join before plugins are loaded
        runTaskLater(this, () -> {
            setStarting(false);
            try {
                getMongoHandler().setServerOnline(instanceName, serverType, playground, true);
                Core.getMessageHandler().sendStaffMessage(ChatColor.AQUA + "Network: " + ChatColor.YELLOW + getInstanceName() + " (MC)" + ChatColor.GREEN + " is now online");
            } catch (Exception e) {
                e.printStackTrace();
                Core.logMessage("Core", "Error announcing server start-up to message queue");
            }
        }, 20);
    }

    /**
     * Registers various listeners required for the application. This method sets up
     * the necessary event handling by associating specific listeners to the system
     * for proper functionality.
     * <p>
     * The listeners being registered include:
     * - An item utility listener.
     * - A prefix command listener.
     * - A crafting menu listener.
     * <p>
     * This method ensures that all relevant components are wired correctly
     * to respond to their respective events.
     */
    private void registerListeners() {
        registerListener(new ItemUtil());
        registerListener(new PrefixCommandListener());
        registerListener(craftingMenu);
    }

    /**
     * Registers commands that are disabled and should not be available for use.
     * This method adds instances of specific command classes to the command registry.
     * The commands being disabled are instantiated within this method.
     */
    private void registerDisabledCommands() {
        registerCommand(new MeCommand());
        registerCommand(new StopCommand());
    }

    /**
     * Registers a collection of command implementations with the system.
     * This method initializes and registers various commands designed for
     * the application. Additionally, it dynamically checks for the presence
     * of a specific plugin ("ParkManager") to conditionally register
     * the TeleportCommand.
     * <p>
     * The commands include core functionalities such as:
     * - Managing achievements, balance, and player stats.
     * - Interactions such as messaging, teleporting, and toggling tags.
     * - Administrative operations like reload and shutdown.
     * <p>
     * If the "ParkManager" plugin is not detected in the system,
     * the TeleportCommand is registered as part of the initialization process.
     */
    private void registerCommands() {
        registerCommand(new AchievementCommand());
        registerCommand(new BalanceCommand());
        registerCommand(new FlyCommand());
        registerCommand(new HelpopCommand());
        registerCommand(new HonorCommand());
        registerCommand(new ListCommand());
        registerCommand(new LockArmorStandCommand());
        registerCommand(new MsgCommand());
        registerCommand(new MyHonorCommand());
        registerCommand(new OnlineCommand());
        registerCommand(new PermissionCommand());
        registerCommand(new PingCommand());
        registerCommand(new PluginsCommand());
        registerCommand(new ReloadCommand());
        registerCommand(new ShutdownCommand());
        registerCommand(new SpawnCommand());
        registerCommand(new TagToggleCommand());
        registerCommand(new TokenCommand());
        registerCommand(new TopHonorCommand());
        // New Commands
        registerCommand(new DevCommand());
        registerCommand(new BankCommand());
        runTask(this, () -> {
            boolean park = false;
            for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
                if (p.getName().equals("ParkManager")) {
                    park = true;
                }
            }
            if (!park) registerCommand(new TeleportCommand());
        });
    }

    /**
     * Register a core command.
     *
     * @param command the command
     */
    public final void registerCommand(CoreCommand command) {
        commandMap.registerCommand(command);
    }

    /**
     * Method executed when the server is disabled.
     * <p>
     * This method is responsible for performing necessary cleanup tasks
     * and notifying relevant systems of the server shutdown. It updates the server's
     * status in the database, sends a staff notification about the shutdown, and logs the shutdown event.
     * <p>
     * Actions performed:
     * - Updates the server's online status in the database to reflect it being offline.
     * - Notifies staff members about the server shutdown through a message handler.
     * - Logs a shutdown message for administrative purposes.
     * <p>
     * Handles exceptions that might occur during the shutdown process to ensure the application
     * can complete the disable sequence gracefully.
     */
    @Override
    public final void onDisable() {
        try {
            // try to set the server as offline in mongo
            getMongoHandler().setServerOnline(instanceName, serverType, playground, false);
            // send a staff message informing staff the server is shutting down
            Core.getMessageHandler().sendStaffMessage(ChatColor.AQUA + "Network: " + ChatColor.YELLOW + getInstanceName() + " (MC)" + ChatColor.RED + " is safely shutting down");
        } catch (Exception e) {
            // catch an exception that may come up and send the error message
            e.printStackTrace();
            Core.logMessage("Core", "Error announcing server shutdown to message queue");
        }
        // send a console message that core is disabled
        logMessage("Core", ChatColor.DARK_RED + "Disabled");
    }

    /**
     * Checks if the application is currently running in debug mode.
     *
     * @return true if the application is in debug mode; false otherwise.
     */
    public static boolean isDebug() {
        return getInstance().debug;
    }

    /**
     * Determines whether both the dashboard and SQL are disabled.
     *
     * @return true if both the dashboard and SQL functionalities are disabled, false otherwise.
     */
    public static boolean isDashboardAndSqlDisabled() {
        return getInstance().dashboardAndSqlDisabled;
    }

    /**
     * Determines whether the system or process is currently in the starting state.
     *
     * @return true if the system or process is starting; false otherwise
     */
    public static boolean isStarting() {
        return getInstance().starting;
    }

    /**
     * Sets the server's starting status and logs a message indicating whether the server is joinable.
     *
     * @param isStarting a boolean indicating the starting status. If true,
     *                   it signifies the server is not joinable. If false,
     *                   it signifies the server is joinable.
     */
    public static void setStarting(boolean isStarting) {
        // if the {@code isStarting} parameter is set to false, send a message saying the server is joinable
        // Note: why in the world would the server be joinable if the parameter is set to false????
        if (!isStarting) logMessage("Core", ChatColor.DARK_GREEN + "Server Joinable!");
        // else wise, the server is not joinable
        else logMessage("Core", ChatColor.DARK_RED + "Server Not Joinable!");
        getInstance().starting = isStarting;
    }

    /**
     * Retrieves the type of server associated with the instance.
     *
     * @return a string representing the server type.
     */
    public static String getServerType() {
        return getInstance().serverType;
    }

    /**
     * Retrieves the name of the current instance.
     *
     * @return the name of the instance as a String
     */
    public static String getInstanceName() {
        return getInstance().instanceName;
    }

    /**
     * Is this instance running in game-mode?
     * <p>
     * GameMode allows the server to skip the startup phase so it can start faster
     *
     * @return the game-mode status
     */
    public static boolean isGameMode() {
        return getInstance().gameMode;
    }

    /**
     * Retrieves the version of the current instance's description.
     *
     * @return a string representing the version information.
     */
    public static String getVersion() {
        return getInstance().getDescription().getVersion();
    }

    /**
     * Retrieves the CommandMap associated with the current instance.
     *
     * @return the CoreCommandMap object associated with the instance
     */
    public static CoreCommandMap getCommandMap() {
        return getInstance().commandMap;
    }

    /**
     * Retrieves the player manager instance.
     *
     * @return the instance of CPlayerManager associated with the current context.
     */
    public static CPlayerManager getPlayerManager() {
        return getInstance().playerManager;
    }

    /**
     * Retrieves the HonorManager instance associated with the current instance.
     *
     * @return the HonorManager instance
     */
    public static HonorManager getHonorManager() {
        return getInstance().honorManager;
    }

    /**
     * Retrieves the crafting menu associated with the current instance.
     *
     * @return the instance of CraftingMenu associated with the current instance.
     */
    public static CraftingMenu getCraftingMenu() {
        return getInstance().craftingMenu;
    }

    /**
     * Retrieves the language formatter from the language manager instance.
     *
     * @return the LanguageManager instance used for formatting languages
     */
    public static LanguageManager getLanguageFormatter() {
        return getInstance().languageManager;
    }

    /**
     * Retrieves the PermissionManager instance.
     *
     * @return the PermissionManager instance associated with the current context.
     */
    public static PermissionManager getPermissionManager() {
        return getInstance().permissionManager;
    }

    /**
     * Retrieves the instance of the EconomyManager.
     *
     * @return the EconomyManager instance associated with the current application context
     */
    public static EconomyManager getEconomy() {
        return getInstance().economyManager;
    }

    /**
     * Retrieves the resource manager instance from the singleton instance.
     *
     * @return the ResourceManager instance managed by the singleton.
     */
    public static ResourceManager getResourceManager() {
        return getInstance().resourceManager;
    }

    /**
     * Retrieves the instance of the AchievementManager.
     *
     * @return the AchievementManager instance.
     */
    public static AchievementManager getAchievementManager() {
        return getInstance().achievementManager;
    }

    /**
     * Retrieves the SoftNPCManager instance associated with the current application instance.
     *
     * @return the SoftNPCManager instance managed by the singleton application instance.
     */
    public static SoftNPCManager getSoftNPCManager() {
        return getInstance().softNPCManager;
    }

    /**
     * Retrieves the SqlUtil instance from the current instance of the class.
     *
     * @return the SqlUtil instance associated with the current instance
     */
    public static SqlUtil getSqlUtil() {
        return getInstance().sqlUtil;
    }

    /**
     * Retrieves the MongoHandler instance from the current Singleton instance.
     *
     * @return the MongoHandler instance being used by the Singleton.
     */
    public static MongoHandler getMongoHandler() {
        return getInstance().mongoHandler;
    }

    /**
     * Retrieves the core configuration file.
     *
     * @return A FileConfiguration object representing the core configuration.
     */
    public static FileConfiguration getCoreConfig() {
        return getInstance().configFile.getConfig();
    }

    /**
     * Create a new inventory
     *
     * @param size  the size of the inventory
     * @param title the name of the inventory
     * @return the new inventory
     */
    public static Inventory createInventory(int size, String title) {
        return Bukkit.createInventory(null, size, title);
    }

    /**
     * Gets Bukkit world from name.
     *
     * @param name the name of the world
     * @return the world
     */
    public static World getWorld(String name) {
        return Bukkit.getWorld(name);
    }

    /**
     * The main world
     *
     * @return the default world
     */
    public static World getDefaultWorld() {
        return Bukkit.getWorlds().get(0);
    }

    /**
     * Gets all worlds on the server.
     *
     * @return the current registered worlds
     */
    public static List<World> getWorlds() {
        return Bukkit.getWorlds();
    }

    /**
     * Shutdown the server.
     */
    public static void shutdown() {
        Bukkit.shutdown();
    }

    /**
     * Registers the given listener to handle events within the application.
     *
     * @param listener the listener instance to register for event handling
     */
    public static void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, getInstance());
    }

    /**
     * Calls the specified event using the Bukkit plugin manager.
     *
     * @param event the event to be called. This must not be null and must extend the {@link Event} class.
     */
    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Cancel task.
     *
     * @param taskId the task id
     */
    public static void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    /**
     * Executes a given callable synchronously on the server's main thread.
     * This method ensures that the code within the callable is executed in a thread-safe manner on the main server thread.
     *
     * @param <T> the result type of the callable task
     * @param plugin the plugin requesting the execution
     * @param callable the callable task to be executed synchronously
     * @return a Future representing the result of the callable execution, allowing retrieval of the result or checking the status of the task
     */
    public static <T> Future<T> callSyncMethod(Plugin plugin, Callable<T> callable) {
        return Bukkit.getScheduler().callSyncMethod(plugin, callable);
    }

    /**
     * Executes the given task asynchronously using the Bukkit scheduler.
     *
     * @param plugin the plugin instance scheduling the task
     * @param task the task to be executed asynchronously
     * @return the task ID associated with the scheduled task
     */
    public static int runTaskAsynchronously(Plugin plugin, Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, task).getTaskId();
    }

    /**
     * Schedules a task to be run asynchronously after a specified delay.
     * This method is useful for executing non-blocking operations off the main server thread.
     *
     * @param plugin the plugin requesting the task to be scheduled
     * @param task the Runnable task to be executed asynchronously
     * @param delay the number of server ticks to wait before running the task
     * @return the task ID of the scheduled task
     */
    public static int runTaskLaterAsynchronously(Plugin plugin, Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay).getTaskId();
    }

    /**
     * Schedules a task to be run later on the primary server thread.
     *
     * @param plugin the plugin instance requesting the task to be run
     * @param task the task to be executed after the specified delay
     * @param delay the delay in ticks before the task is executed
     * @return the ID of the scheduled task
     */
    public static int runTaskLater(Plugin plugin, Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLater(plugin, task, delay).getTaskId();
    }

    /**
     * Schedules a repeating task to run asynchronously with the specified initial delay and period.
     *
     * @param plugin The plugin instance requesting the scheduling of the task. Must not be null.
     * @param task The task to be executed. Must implement Runnable and must not be null.
     * @param delay The delay in server ticks before the task is executed for the first time.
     * @param period The interval in server ticks between consecutive executions of the task.
     * @return The task ID of the scheduled task.
     */
    public static int runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period).getTaskId();
    }

    /**
     * Schedules a repeating task to run at a fixed interval after an initial delay.
     *
     * @param plugin The plugin for which the task is being scheduled. Must not be null.
     * @param task The runnable task to execute. Must not be null.
     * @param delay The delay in ticks before executing the task for the first time.
     * @param period The interval in ticks between consecutive executions of the task.
     * @return The task ID assigned to the scheduled task.
     */
    public static int runTaskTimer(Plugin plugin, Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period).getTaskId();
    }

    /**
     * Executes a task asynchronously within the context of the provided plugin.
     *
     * @param plugin the plugin that owns this task and its lifecycle
     * @param task the task to execute
     * @return the ID of the scheduled task
     */
    public static int runTask(Plugin plugin, Runnable task) {
        return Bukkit.getScheduler().runTask(plugin, task).getTaskId();
    }

    /*
    OLD METHODS END
     */

    /**
     * Call sync method
     *
     * @param callable the callable
     * @return future
     */
    public static <T> Future<T> callSyncMethod(Callable<T> callable) {
        return Bukkit.getScheduler().callSyncMethod(getInstance(), callable);
    }

    /**
     * Executes the given task asynchronously using the Bukkit scheduler.
     * <p>
     * This method schedules the provided {@link Runnable} task to be executed asynchronously,
     * meaning it will not block the main thread. The task will be processed by the server's
     * asynchronous task scheduler.
     *
     * @param task the {@link Runnable} task to be executed asynchronously
     * @return the task ID of the scheduled asynchronous task
     */
    public static int runTaskAsynchronously(Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(getInstance(), task).getTaskId();
    }

    /**
     * Schedules a task to be run asynchronously after a specified delay.
     *
     * @param task  The runnable task to execute after the delay.
     * @param delay The delay, in server ticks, before executing the task.
     * @return The unique task ID for the scheduled task.
     */
    public static int runTaskLaterAsynchronously(Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(getInstance(), task, delay).getTaskId();
    }

    /**
     * Schedules a specified task to run after a specified delay.
     *
     * @param task the {@link Runnable} task to be executed after the delay
     * @param delay the delay in ticks before the task is executed
     * @return the task ID for the scheduled task
     */
    public static int runTaskLater(Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLater(getInstance(), task, delay).getTaskId();
    }

    /**
     * Schedules a runnable task to run asynchronously at a fixed rate. The task will first be executed
     * after the specified delay, and subsequent executions will occur repeatedly with the specified period.
     *
     * @param task The runnable task to be executed.
     * @param delay The delay in server ticks before the first execution.
     * @param period The period in server ticks between successive executions.
     * @return The task ID of the scheduled task.
     */
    public static int runTaskTimerAsynchronously(Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(getInstance(), task, delay, period).getTaskId();
    }

    /**
     * Schedules a repeating task to be executed at a fixed rate.
     *
     * @param task The task to be executed.
     * @param delay The delay in ticks before executing the task for the first time.
     * @param period The interval in ticks between consecutive task executions.
     * @return The task ID of the scheduled task.
     */
    public static int runTaskTimer(Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(getInstance(), task, delay, period).getTaskId();
    }

    /**
     * Schedules a Runnable task to be executed by the server's task scheduler.
     *
     * @param task the Runnable task to be executed
     * @return the task ID of the scheduled task
     */
    public static int runTask(Runnable task) {
        return Bukkit.getScheduler().runTask(getInstance(), task).getTaskId();
    }

    /*
    OLD METHODS START
     */

    /**
     * Logs a message with a formatted structure including the sender's name.
     *
     * @param name the name of the sender that will appear in the log
     * @param message the message content to be logged
     */
    public static void logMessage(String name, String message) {
        logInfo(ChatColor.GOLD + name + ChatColor.DARK_GRAY + " > " + message);
    }

    /**
     * Logs a debug message if debugging is enabled.
     *
     * @param s the message to be logged
     */
    public static void debugLog(String s) {
        if (isDebug()) {
            logMessage("CORE-DEBUG", s);
        }
    }

    /**
     * Logs an informational message to the console sender.
     *
     * @param message the informational message to be logged
     */
    public static void logInfo(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    /**
     * Adds a packet listener to the ProtocolManager, which allows custom handling of packets.
     *
     * @param listener the PacketListener to be added for handling packets
     */
    public static void addPacketListener(PacketListener listener) {
        ProtocolLibrary.getProtocolManager().addPacketListener(listener);
    }

    /**
     * Sends all online players to a specified server asynchronously and invokes a callback upon completion.
     *
     * @param server   The name of the server to which all online players should be sent.
     * @param callback The callback method to be executed upon completion of sending players.
     */
    public static void sendAllPlayers(String server, Callback callback) {
        runTaskAsynchronously(getInstance(), () -> {
            for (CPlayer player : Core.getPlayerManager().getOnlinePlayers()) {
                player.sendToServer(server);
            }
            callback.finished();
        });
    }
}
