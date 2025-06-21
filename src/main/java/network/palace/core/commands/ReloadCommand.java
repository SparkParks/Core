package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Represents the ReloadCommand, enabling developers to reload individual Core plugins
 * or specific components of the Core system such as Mongo handlers and resource managers.
 * This command provides functionality to dynamically unload, reload, and reinitialize
 * JavaPlugin instances within a running server.
 * <p>
 * Key functionality includes:
 * - Reloading specific components of the Core system (e.g., MongoHandlers, LanguageFormatters).
 * - Reloading individual Core plugins by disabling the plugin, reloading its JAR, and re-enabling it.
 * - Handling scenarios where plugins do not exist, are not Core plugins, or do not support reloading.
 * <p>
 * Note: This command is restricted to users with developer-level permissions.
 */
@CommandMeta(aliases = {"rl"}, description = "Reload individual Core plugins.", rank = Rank.DEVELOPER)
public class ReloadCommand extends CoreCommand {

    /**
     * Constructs a new ReloadCommand instance with the command name set to "reload".
     * This command is typically used to trigger a reload operation, such as reloading
     * configuration files or refreshing application states. The specifics of the reload
     * behavior are defined in the command handling logic that extends this class.
     */
    public ReloadCommand() {
        super("reload");
    }

    /**
     * Handles the execution of the unspecified "reload" command, allowing for reloading specific plugins
     * or the Core system within the application. This method processes the command input, validates arguments,
     * and performs actions such as reloading plugins or system components.
     *
     * @param sender the sender of the command, which could be a player, console, or other command source
     * @param args the arguments passed with the command, where the first argument typically specifies
     *             the plugin or Core system to be reloaded and an optional second argument specifies the
     *             file name of the new plugin jar
     * @throws CommandException if an error occurs during command execution or processing
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/reload [Plugin Name] <New Jar Name>");
            return;
        }
        String p = args[0];
        if (p.equalsIgnoreCase("core")) {
            Core.getMongoHandler().connect();
            Core.getResourceManager().reload();
            Core.getLanguageFormatter().reload();
            Core.getAchievementManager().reload();
            Core.getHonorManager().provideMappings(Core.getMongoHandler().getHonorMappings());
            sender.sendMessage(ChatColor.GREEN + "Core reloaded!");
            return;
        }
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin(p);
        if (plugin == null) {
            sender.sendMessage(ChatColor.RED + "That plugin doesn't exist!");
            return;
        }
        if (!(plugin instanceof network.palace.core.plugin.Plugin)) {
            sender.sendMessage(ChatColor.RED + "Only Core plugins can be reloaded!");
            return;
        }
        network.palace.core.plugin.Plugin cp = (network.palace.core.plugin.Plugin) plugin;
        if (!cp.getInfo().canReload()) {
            sender.sendMessage(ChatColor.RED + "This plugin doesn't support reloading!");
            return;
        }
        final String name;
        if (args.length == 2) {
            name = args[1];
        } else {
            name = plugin.getName();
        }
        Bukkit.getScheduler().cancelTasks(plugin);
        JavaPluginLoader jpl = (JavaPluginLoader) plugin.getPluginLoader();
        jpl.disablePlugin(plugin);
        SimplePluginManager spm = (SimplePluginManager) Bukkit.getPluginManager();
        try {
            Field f = SimplePluginManager.class.getDeclaredField("plugins");
            f.setAccessible(true);
            List<Plugin> plugins = (List<Plugin>) f.get(spm);
            plugins.remove(plugin);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        System.gc();
        File f = new File("plugins/" + name + ".jar");
        if (!f.exists()) {
            sender.sendMessage(ChatColor.RED + name + ".jar doesn't exist!");
            return;
        }
        Plugin np;
        try {
            np = Bukkit.getPluginManager().loadPlugin(f);
        } catch (InvalidPluginException | InvalidDescriptionException e) {
            sender.sendMessage(ChatColor.RED + "There was an error loading the plugin!");
            e.printStackTrace();
            return;
        }
        Bukkit.getPluginManager().enablePlugin(np);
        sender.sendMessage(ChatColor.GREEN + np.getName() + " has been reloaded!");
    }
}
