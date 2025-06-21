package network.palace.core.commands;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.IncompleteAnnotationException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The PluginsCommand class handles the `plugins` command for listing all plugins on the server,
 * including both core and third-party plugins.
 * <p>
 * The output includes plugin names, their versions, and their enabled/disabled status,
 * formatted for both in-game players and console users. Details about the Spigot server version
 * and the core plugin are also displayed.
 * <p>
 * This command can be executed by users with the required rank or higher.
 */
@CommandMeta(aliases = {"about", "pl", "ver", "version", "help", "?"}, description = "Lists the plugins for the server.", rank = Rank.DEVELOPER)
public class PluginsCommand extends CoreCommand {

    /**
     * Represents a status message or value indicating how many versions
     * a system or application is behind compared to the latest available version.
     * <p>
     * The variable is initialized with a default message of "Loading version..."
     * until the actual value is determined asynchronously or otherwise updated.
     * <p>
     * This field is primarily used in the context of validating or informing about
     * version discrepancies within the plugin system.
     */
    private String versionsBehind = "Loading version...";

    /**
     * Constructs a new PluginsCommand instance.
     * <p>
     * Initializes the command with the name "plugins". Additionally, an asynchronous task
     * is scheduled to execute the {@code obtainVersion} method using the {@code Core.runTaskAsynchronously}
     * utility, ensuring non-blocking behavior.
     */
    public PluginsCommand() {
        super("plugins");
        Core.runTaskAsynchronously(Core.getInstance(), this::obtainVersion);
    }

    /**
     * Handles the execution of a command when no specific sub-command is provided.
     * <p>
     * This method gathers and organizes plugin information, differentiates between core
     * plugins and third-party plugins, sorts them alphabetically, and displays the
     * relevant information to the sender. The response is tailored depending on
     * whether the sender is a player or not.
     *
     * @param sender the entity that executed the command. This can be either a player or the console.
     * @param args the arguments passed with the command. May be used for additional command handling logic.
     * @throws CommandException if an issue occurs during command execution.
     */
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        // Check if sender is a player
        boolean isPlayer = sender instanceof Player;
        // Lists
        List<PluginInfo> pluginsList = new ArrayList<>();
        List<PluginInfo> thirdPartyList = new ArrayList<>();
        // Loop through plugins and add
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin instanceof network.palace.core.plugin.Plugin) {
                network.palace.core.plugin.Plugin corePlugin = (network.palace.core.plugin.Plugin) plugin;
                String version;
                try {
                    version = corePlugin.getInfo().version();
                } catch (IncompleteAnnotationException ignored) {
                    version = corePlugin.getDescription().getVersion();
                }
                pluginsList.add(new PluginInfo(corePlugin.getInfo().name(), version, corePlugin.isEnabled()));
            } else if (!(plugin instanceof Core)) {
                thirdPartyList.add(new PluginInfo(plugin.getName(), plugin.getDescription().getVersion(), plugin.isEnabled()));
            }
        }
        // Sort
        pluginsList.sort(Comparator.comparing(PluginInfo::getName));
        thirdPartyList.sort(Comparator.comparing(PluginInfo::getName));
        // Plugins info and colors
        FormattedMessage pluginsFM = createPluginListMessage(isPlayer, pluginsList);
        // Third party plugins info and colors
        FormattedMessage thirdPartyFM = createPluginListMessage(isPlayer, thirdPartyList);
        // Boilerplate text
        String boilerPlate = BoilerplateUtil.getBoilerplateText(ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH.toString());
        // Send messages
        sender.sendMessage(boilerPlate);
        sender.sendMessage(ChatColor.GOLD + "Running " + ChatColor.GREEN + "Core " + ChatColor.GOLD + "version " + ChatColor.DARK_GREEN + Core.getVersion());
        sender.sendMessage("");
        if (isPlayer) {
            MinecraftVersion current = new MinecraftVersion(Bukkit.getServer());
            String currentVersion = ChatColor.DARK_GREEN + current.getVersion();
            String spigotVersion = ChatColor.GOLD + "Spigot version: " + ChatColor.GREEN + Bukkit.getVersion();
            String apiVersion = ChatColor.GOLD + "API Version: " + ChatColor.GREEN + Bukkit.getBukkitVersion();
            String versionInfo = ChatColor.GOLD + "Version Info: " + ChatColor.GREEN + versionsBehind;
            FormattedMessage spigot = new FormattedMessage("Running ").color(ChatColor.GOLD).then("Spigot ").color(ChatColor.GREEN).then("version ").color(ChatColor.GOLD).then(currentVersion).tooltip(currentVersion, spigotVersion, apiVersion, versionInfo);
            spigot.send((Player) sender);
        } else {
            sender.sendMessage(ChatColor.GOLD + "Running " + ChatColor.GREEN + "Spigot " + ChatColor.GOLD + "version " + ChatColor.DARK_GREEN + Bukkit.getVersion() + " (API version " + Bukkit.getBukkitVersion() + ") " + ChatColor.YELLOW + "(" + versionsBehind + ")");
        }
        sender.sendMessage("");
        if (isPlayer) {
            pluginsFM.send((Player) sender);
        } else {
            sender.sendMessage(pluginsFM.toFriendlyString());
        }
        sender.sendMessage("");
        if (isPlayer) {
            thirdPartyFM.send((Player) sender);
        } else {
            sender.sendMessage(thirdPartyFM.toFriendlyString());
        }
        sender.sendMessage(boilerPlate);
    }

    /**
     * Represents information about a specific plugin, including its name,
     * version, and whether it is currently enabled.
     * <p>
     * This class is intended to encapsulate basic metadata for plugins,
     * allowing for easy retrieval and management of plugin details.
     */
    public static class PluginInfo {
        /**
         * The name of the plugin.
         * <p>
         * This variable represents the unique identifier or title of the plugin
         * and is intended to distinguish it from other plugins. It is a
         * final, immutable string that is set at the time of object creation
         * and cannot be changed afterward.
         */
        @Getter private final String name;

        /**
         * Represents the version of a plugin.
         * <p>
         * This variable holds the version string associated with a plugin instance.
         * It is intended to identify the specific release or iteration of the plugin
         * and is immutable once the object is constructed. If no valid version value
         * is provided during initialization, it will default to "Unknown".
         */
        @Getter private final String version;

        /**
         * Indicates whether the plugin is currently enabled.
         * <p>
         * This variable represents the active state of the plugin. If it is set to
         * {@code true}, the plugin is enabled and operational; otherwise, if set
         * to {@code false}, the plugin is disabled and non-functional. This value
         * is immutable and reflects the plugin's state at the time of object
         * creation.
         */
        @Getter private final boolean enabled;

        /**
         * Constructs a new PluginInfo instance with the provided name, version, and enabled status.
         *
         * @param name    The name of the plugin. This is a final, immutable value that identifies the plugin.
         * @param version The version of the plugin. If the provided value is null or empty, the version defaults to "Unknown".
         * @param enabled A boolean indicating whether the plugin is enabled (true) or disabled (false).
         */
        public PluginInfo(String name, String version, boolean enabled) {
            this.name = name;
            this.version = ((version == null || version.trim().isEmpty()) ? "Unknown" : version);
            this.enabled = enabled;
        }

    }

    /**
     * Analyzes and determines the version metadata of the Bukkit server implementation in use.
     * Updates the `versionsBehind` field based on the comparison of the current server version
     * against the latest public version details of Spigot or CraftBukkit repositories.
     * <p>
     * The method performs the following checks:
     * 1. Retrieves the server's version string via the Bukkit API.
     * 2. If the version string starts with "git-Spigot-", it calculates the distance
     *    between the current CraftBukkit and Spigot versions to their respective latest versions.
     *    - If the calculated distances are valid, it sets `versionsBehind` to either "Latest"
     *      (if current versions are up-to-date) or "{distance} behind" (if outdated).
     *    - If any error occurs during the calculations, it sets `versionsBehind` to
     *      "Error obtaining version information".
     * 3. If the version string starts with "git-Bukkit-", it calculates the distance
     *    for the CraftBukkit version to the latest.
     *    - The handling of distances and error cases follows the same logic as for Spigot-based versions.
     * 4. If the version string does not match recognized patterns, it sets `versionsBehind`
     *    to "Unknown".
     * <p>
     * This method relies on the `getDistance` method to query public versioning details
     * from Spigot's repository and determine the number of commits behind the current version.
     */
    private void obtainVersion() {
        String version = Bukkit.getVersion();
        if (version == null) version = "Custom";
        if (version.startsWith("git-Spigot-")) {
            String[] parts = version.substring("git-Spigot-".length()).split("-");
            int cbVersions = getDistance("craftbukkit", parts[1].substring(0, parts[1].indexOf(' ')));
            int spigotVersions = getDistance("spigot", parts[0]);
            if (cbVersions == -1 || spigotVersions == -1) {
                versionsBehind = "Error obtaining version information";
            } else {
                if (cbVersions == 0 && spigotVersions == 0) {
                    versionsBehind = "Latest";
                } else {
                    versionsBehind = (cbVersions + spigotVersions) + " behind";
                }
            }
        } else if (version.startsWith("git-Bukkit-")) {
            version = version.substring("git-Bukkit-".length());
            int cbVersions = getDistance("craftbukkit", version.substring(0, version.indexOf(' ')));
            if (cbVersions == -1) {
                versionsBehind = "Error obtaining version information";
            } else {
                if (cbVersions == 0) {
                    versionsBehind = "Latest";
                } else {
                    versionsBehind = cbVersions + " behind";
                }
            }
        } else {
            versionsBehind = "Unknown";
        }
    }

    /**
     * Calculates the distance (number of commits) behind a specific commit hash
     * in a given Spigot repository using the public API.
     *
     * @param repo the name of the repository to query (e.g., "CraftBukkit" or "Spigot").
     * @param hash the commit hash to calculate the distance from.
     * @return the number of commits behind the specified hash if successful, or -1 in case of an error.
     */
    private static int getDistance(String repo, String hash) {
        try {
            try (BufferedReader reader = Resources.asCharSource(new URL("https://hub.spigotmc.org/stash/rest/api/1.0/projects/SPIGOT/repos/"
                    + repo + "/commits?since=" + URLEncoder.encode(hash, "UTF-8") + "&withCounts=true"), Charsets.UTF_8
            ).openBufferedStream()) {
                JSONObject obj = (JSONObject) new JSONParser().parse(reader);
                return ((Number) obj.get("totalCount")).intValue();
            } catch (ParseException ex) {
                ex.printStackTrace();
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Creates a formatted message representing a list of plugins with their statuses and versions.
     * <p>
     * The method iterates through a list of {@code PluginInfo} objects, formatting each plugin's
     * name, status (enabled or disabled), and version information. Additional details are
     * conditionally included depending on whether the requester is a player or not.
     *
     * @param isPlayer a boolean indicating whether the message is being created for a player or not.
     *                 If true, the message includes a tooltip with the plugin's version. If false,
     *                 the version is displayed directly in the message.
     * @param thirdPartyList a {@code List} of {@code PluginInfo} objects, each representing a third-party plugin
     *                       with its associated details such as name, version, and enabled status.
     * @return a {@code FormattedMessage} containing the formatted list of plugins with their respective details.
     */
    private FormattedMessage createPluginListMessage(boolean isPlayer, List<PluginInfo> thirdPartyList) {
        FormattedMessage msg = new FormattedMessage("");
        for (int i = 0; i < thirdPartyList.size(); i++) {
            PluginInfo info = thirdPartyList.get(i);
            msg.then(info.getName()).color(info.isEnabled() ? ChatColor.GREEN : ChatColor.RED);
            if (isPlayer) {
                msg.tooltip(ChatColor.GOLD + "Version: " + ChatColor.GREEN + info.getVersion());
            } else {
                msg.then(" (" + info.getVersion() + ")").color(ChatColor.YELLOW);
            }
            if (i != thirdPartyList.size() - 1) {
                msg.then(", ").color(ChatColor.GOLD);
            }
        }
        return msg;
    }
}
