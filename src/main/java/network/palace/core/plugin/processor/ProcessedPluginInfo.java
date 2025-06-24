package network.palace.core.plugin.processor;

import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Representer;

import java.util.*;

/**
 * Represents processed information about a plugin.
 * This class encapsulates the metadata and configuration details of a plugin,
 * including name, author, version, dependencies, and main class information.
 */
public class ProcessedPluginInfo {

    /**
     * Represents the name of the plugin.
     * This is the identifier used to denote the plugin and is typically unique.
     */
    private String name;

    /**
     * Represents the author of the plugin.
     *
     * This field stores the name of the individual or organization
     * that created or is responsible for the plugin. It is primarily
     * used for metadata purposes, such as generating or displaying
     * plugin information.
     */
    private String author;

    /**
     * Represents the version of the plugin.
     *
     * The version is a string value that identifies the current version of the
     * plugin. It is used for both documentation and compatibility purposes.
     * This value typically follows semantic versioning (e.g., "1.0.0") or
     * another versioning scheme defined by the plugin author.
     */
    private String version;

    /**
     * Represents the API version that the plugin is compatible with.
     *
     * This variable declares the specific version of the plugin API
     * used by a plugin. It is utilized to ensure compatibility between
     * the plugin and the runtime environment's API standards.
     *
     * Typically, this value is set during the processing of plugin
     * metadata (e.g., in a `plugin.yml` file) and can influence
     * how the plugin interacts with the provided API.
     */
    private String apiVersion;

    /**
     * Represents the list of required plugin dependencies for a given plugin.
     *
     * This array contains the names of other plugins that the current plugin depends on to function properly.
     * If any of the plugins listed in this array are not present or loaded, the plugin will not start.
     *
     * This field is primarily utilized during the processing of plugin metadata to populate
     * the `depend` field in a `plugin.yml` file.
     */
    private String[] depend;

    /**
     * Represents an array of plugin names that this plugin can optionally depend on.
     *
     * The `softdepend` field specifies optional dependencies that, if present, should
     * be loaded before this plugin. Unlike hard dependencies defined in the `depend`
     * field, soft dependencies do not prevent this plugin from loading if they are
     * not present but can affect its functionality.
     *
     * This field is commonly used to declare integration points with other plugins
     * without strictly requiring their presence.
     */
    private String[] softdepend;

    /**
     * Represents the fully qualified class name of the main plugin class.
     *
     * This field is used to identify the primary entry point for the plugin,
     * which must extend the `JavaPlugin` class. The value stored in this
     * variable is expected to be a fully qualified Java class name (e.g.,
     * `com.example.MyPlugin`).
     *
     * The `main` field is critical for generating the `plugin.yml` file
     * as it specifies the plugin's main class, allowing the Bukkit/Spigot
     * server to load and run the plugin.
     */
    private String main;

    /**
     * Constructs a new instance of ProcessedPluginInfo with the specified plugin information.
     *
     * @param name        The name of the plugin.
     * @param author      The author of the plugin.
     * @param version     The version of the plugin.
     * @param apiVersion  The API version the plugin is compatible with.
     * @param depend      An array of mandatory dependencies for the plugin.
     * @param softdepend  An array of optional dependencies for the plugin.
     * @param main        The fully qualified name of the main plugin class.
     */
    public ProcessedPluginInfo(String name, String author, String version, String apiVersion, String[] depend, String[] softdepend, String main) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.apiVersion = apiVersion;
        this.depend = depend;
        this.softdepend = softdepend;
        this.main = main;
    }

    /**
     * Creates and configures a customized {@link Representer} for YAML serialization of
     * {@link ProcessedPluginInfo} instances. The method sets up specific behaviors to skip
     * empty properties and maintain the insertion order of properties during serialization.
     *
     * @return a configured instance of {@link Representer} designed to handle specific
     *         serialization requirements for {@link ProcessedPluginInfo}.
     */
    public Representer getRepresenter() {
        Representer representer = new SkipEmptyRepresenter();
        representer.addClassTag(ProcessedPluginInfo.class, Tag.MAP);
        representer.setPropertyUtils(new UnsortedPropertyUtils());
        return representer;
    }

    /**
     * Converts the current instance of the ProcessedPluginInfo class into a YAML-compatible map.
     * The resulting map provides key-value pairs representing the fields of the plugin,
     * such as name, author, version, API version, dependencies, and main class. Fields with
     * empty or null values may be omitted in the resulting map.
     *
     * @return a map containing the serialized representation of the plugin's information.
     */
    public Map<String, Object> toYamlMap() {
        return new LinkedHashMap<String, Object>() {{
            put("name", name);
            put("author", author);
            put("version", version);
            if (!apiVersion.isEmpty()) put("api-version", apiVersion);
            put("depend", depend);
            put("softdepend", softdepend);
            put("main", main);
        }};
    }

    /**
     * Utility subclass of {@link PropertyUtils} that overrides the default property set creation behavior
     * to preserve the insertion order of properties.
     *
     * This class is designed to ensure that properties are handled in the order they are defined,
     * which is particularly useful in scenarios where maintaining the property insertion order
     * during serialization or processing is critical.
     */
    private class UnsortedPropertyUtils extends PropertyUtils {
        @Override
        protected Set<Property> createPropertySet(Class<?> type, BeanAccess bAccess) {
            return new LinkedHashSet<>(getPropertiesMap(type, BeanAccess.FIELD).values());
        }
    }

    /**
     * A custom implementation of the {@link Representer} class designed to skip
     * empty properties during YAML serialization. This class ensures that properties
     * with null or empty values (such as empty collections or maps) are excluded
     * from the output.
     *
     * This implementation overrides the {@code representJavaBeanProperty} method
     * to determine whether a property should be included in the serialized output
     * based on its value. If a property has a null value, an empty collection, or
     * an empty map, it is skipped.
     *
     * The supported scenarios for exclusion include:
     * - Properties with a null value.
     * - Properties with empty collections (e.g., lists or sets).
     * - Properties with empty maps.
     *
     * This class can be used to streamline the YAML representation by omitting
     * unnecessary or redundant information, resulting in a cleaner and more concise
     * output.
     */
    private class SkipEmptyRepresenter extends Representer {

        /**
         * Overrides the representation of a JavaBean property during YAML serialization.
         * This method determines whether a property should be included in the serialized
         * output based on its value. Properties with null, empty collections, or empty
         * maps are excluded.
         *
         * @param javaBean the JavaBean instance being serialized
         * @param property the property of the JavaBean being processed
         * @param propertyValue the value of the property to be represented
         * @param customTag an optional custom tag for the property (can be null)
         * @return the NodeTuple representing the property, or null if the property is
         *         null, an empty collection, or an empty map
         */
        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
            NodeTuple tuple = super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            Node valueNode = tuple.getValueNode();
            if (Tag.NULL.equals(valueNode.getTag())) return null;
            if (valueNode instanceof CollectionNode) {
                if (Tag.SEQ.equals(valueNode.getTag())) {
                    SequenceNode seq = (SequenceNode) valueNode;
                    if (seq.getValue().isEmpty()) return null;
                }
                if (Tag.MAP.equals(valueNode.getTag())) {
                    MappingNode seq = (MappingNode) valueNode;
                    if (seq.getValue().isEmpty()) return null;
                }
            }
            return tuple;
        }
    }

    /**
     * Returns a string representation of the ProcessedPluginInfo object.
     * The representation includes the name, author, version, API version,
     * dependencies, soft dependencies, and main class of the plugin.
     *
     * @return a string containing the concatenation of the plugin's information
     *         including name, author, version, API version, dependencies,
     *         soft dependencies, and main class.
     */
    @Override
    public String toString() {
        return name + " " + author + " " + version + " " + apiVersion + " " + Arrays.toString(depend) + " " + Arrays.toString(softdepend) + " " + main;
    }
}
