package network.palace.core.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The PluginInfo annotation is used to provide metadata about a plugin.
 * This metadata includes details such as the plugin's name, version,
 * dependencies, and reload capabilities. It is intended to be applied
 * to plugin classes.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginInfo {

    /**
     * Returns the name of the plugin.
     *
     * @return the plugin name
     */
    String name();

    /**
     * Returns the version of the plugin.
     *
     * @return the plugin version
     */
    String version();

    /**
     * Returns an array of plugin names that this plugin depends on.
     * These dependencies are required for this plugin to function correctly.
     *
     * @return an array of required plugin dependencies
     */
    String[] depend();

    /**
     * Returns an array of plugin names that this plugin has soft dependencies on.
     * These dependencies are not mandatory for the plugin to function but will
     * enhance functionality if they are present.
     *
     * @return an array of soft dependency plugin names
     */
    String[] softdepend() default {};

    /**
     * Indicates whether the plugin supports reloading without requiring a full
     * application restart. If set to {@code true}, the plugin can handle reload
     * events and reinitialize its state accordingly.
     *
     * @return {@code true} if the plugin supports reloading; {@code false} otherwise
     */
    boolean canReload() default false;

    /**
     * Returns the Minecraft Server API version the plugin is compatible with. This can be used to ensure
     * compatibility with specific versions of the application or platform.
     *
     * @return the compatible API version as a string, or an empty string
     * if no specific API version is declared.
     */
    String apiversion() default "";
}
