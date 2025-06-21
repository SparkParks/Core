package network.palace.core.command;

import network.palace.core.player.Rank;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define the permissions required for a command. This is tied to a specific
 * {@link Rank}, which determines the level of privilege necessary to execute the command.
 * <p>
 * The annotation is intended to be used at the class level and is marked as deprecated,
 * indicating that its usage is discouraged and may be removed in future updates.
 */
@Deprecated
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermission {
    /**
     * Retrieves the rank associated with the annotated command or permission.
     *
     * @return the {@link Rank} that represents the level of privilege required
     *         for the annotated command or permission.
     */
    Rank rank();
}
