package network.palace.core.config;

import lombok.NonNull;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Represents a YAML configuration file for a Spigot or Bukkit plugin.
 * This class provides methods to manage, load, save, and retrieve
 * configuration data from the YAML file.
 * <p>
 * The configuration file is initialized when the object is created. If the
 * specific configuration file does not exist, it attempts to create and
 * populate a default file using a resource found in the plugin's JAR.
 */
public final class YAMLConfigurationFile {

    /**
     * Represents the file system path to the YAML configuration file.
     * This is used to locate and reference the configuration file on the disk.
     * The path is immutable and set during the creation of the containing object.
     */
    private final String path;

    /**
     * Represents a file object associated with the YAML configuration.
     * This file is used to store or load configuration data as necessary.
     * The file is immutable and must be specified when the YAMLConfigurationFile object is created.
     */
    private final File file;

    /**
     * The plugin instance associated with this YAML configuration file.
     * It is used to access and interact with the Bukkit plugin APIs for functionalities
     * such as accessing the plugin's directory and logging.
     * This variable is final and initialized during the creation of the configuration file instance,
     * ensuring that it is always linked to the specific plugin using this configuration system.
     */
    private final JavaPlugin plugin;

    /**
     * Represents the configuration file associated with the {@code YAMLConfigurationFile}.
     * This file is used to store and manage configuration settings in YAML format.
     * It serves as the basis for reading, writing, and reloading configuration data.
     */
    private File configFile;

    /**
     * Represents the configuration object for a YAML file.
     * This field is used to manage and interact with the file's configuration data.
     * It provides a structured way to read, write, and manipulate the settings contained
     * within the YAML configuration file.
     * <p>
     * This object is typically loaded and maintained by the class to ensure efficient
     * access and modification.
     */
    private FileConfiguration fileConfiguration;

    /**
     * Instantiates a new YAML configuration file.
     *
     * @param plugin   the plugin associated with this configuration file
     * @param fileName the name of the configuration file
     */
    public YAMLConfigurationFile(@NonNull JavaPlugin plugin, @NonNull String fileName) {
        this(plugin, "", fileName);
    }

    /**
     * Instantiates a new YAML configuration file.
     *
     * @param plugin   the plugin associated with this configuration file
     * @param path     the relative path where the configuration file is located
     * @param fileName the name of the configuration file
     */
    public YAMLConfigurationFile(@NonNull JavaPlugin plugin, String path, @NonNull String fileName) {
        this(plugin, path, new File(plugin.getDataFolder(), fileName));
    }

    /**
     * Instantiates a new YAML configuration file.
     *
     * @param plugin the plugin associated with this configuration file; must be enabled
     * @param path the relative path where the configuration file is located
     * @param file the file object representing the configuration file
     * @throws IllegalArgumentException if the plugin is not enabled
     * @throws IllegalStateException if the plugin's data folder is not accessible
     */
    public YAMLConfigurationFile(@NonNull JavaPlugin plugin, String path, @NonNull File file) {
        if (!plugin.isEnabled()) {
            throw new IllegalArgumentException("Plugin must be enabled");
        }
        this.path = path;
        this.plugin = plugin;
        this.file = file;
        File dataFolder = plugin.getDataFolder();
        if (dataFolder == null) {
            throw new IllegalStateException();
        }
        this.configFile = file;
        saveDefaultConfig();
    }

    /**
     * Reloads the configuration file into memory.
     *
     * This method reloads the contents of the associated YAML configuration file from the disk.
     * It sets the in-memory configuration to match the current state of the file on disk.
     * Additionally, if a default configuration file exists, it sets the default values
     * in the in-memory configuration to those defined in the default file.
     * <p>
     * This is useful to ensure that the configuration reflects any changes made
     * directly to the file system or to reset and apply default values if needed.
     */
    public void reloadConfig() {
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        // Look for defaults in the jar
        if (file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            fileConfiguration.setDefaults(defConfig);
        }
    }

    /**
     * Retrieves the current configuration file object.
     * <p>
     * If the configuration file is not already loaded, this method will reload it
     * prior to returning the object.
     *
     * @return the FileConfiguration representing the YAML configuration file
     */
    public FileConfiguration getConfig() {
        if (fileConfiguration == null) reloadConfig();
        return fileConfiguration;
    }

    /**
     * Retrieves the default configuration associated with this YAML configuration file.
     * <p>
     * If the configuration file is not already loaded, it reloads the configuration from disk.
     * The defaults typically represent the base configuration values provided in the associated
     * default configuration file or resource.
     *
     * @return the default {@link Configuration} of the current configuration file
     */
    public Configuration getDefaults() {
        if (fileConfiguration == null) reloadConfig();
        return fileConfiguration.getDefaults();
    }

    /**
     * Saves the current in-memory configuration to the associated YAML configuration file.
     * <p>
     * If the configuration file or its associated FileConfiguration object is null,
     * the method will return without performing any operation.
     * <p>
     * If the operation fails due to an IOException, an error will be logged using
     * the plugin's logger with a detailed message including the file path.
     */
    public void saveConfig() {
        if (fileConfiguration == null || configFile == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    /**
     * Saves the default configuration file if it does not already exist.
     * <p>
     * This method checks if the associated configuration file exists on the file system.
     * If the file does not exist, it copies the default configuration file from the
     * plugin's resources to the specified location without overwriting existing files.
     * <p>
     * The method ensures that the default configuration is available for use when the
     * custom configuration file is not present.
     */
    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            plugin.saveResource(path + file.getName(), false);
        }
    }
}
