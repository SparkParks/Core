package network.palace.core.command;

import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to provide metadata for a command class. This metadata can include
 * descriptions, usage instructions, aliases, access ranks, and specific tags.
 * It is intended to be used at the class level for defining command-related details.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandMeta {
    /**
     * Specifies a description for the command. This description can provide details
     * about the functionality or purpose of the command.
     *
     * @return the description of the command as a String
     */
    String description() default "";

    /**
     * Specifies alternative names or shortcuts for a command.
     *
     * @return an array of strings representing the aliases for the command
     */
    String[] aliases() default {};

    /**
     * Specifies the usage information for a command. This can include instructions
     * or details on how the command should be executed.
     *
     * @return the usage information for the command as a String
     */
    String usage() default "";

    /**
     * Defines the access rank required for the annotated command. The rank determines
     * the level of privilege or authority needed to execute the command.
     *
     * @return the required {@link Rank} for the command, defaulting to {@code Rank.GUEST}
     */
    Rank rank() default Rank.GUEST;


    /**
     * Specifies a rank tag associated with the command. Rank tags provide additional
     * categorization or identification for commands, often tied to specific roles or attributes.
     *
     * @return the associated {@link RankTag} value, defaulting to {@code RankTag.NONE}
     */
    RankTag tag() default RankTag.NONE;
}
