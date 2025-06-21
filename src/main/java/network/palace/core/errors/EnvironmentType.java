package network.palace.core.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents various types of application environments.
 * This enum provides a predefined set of constants for identifying
 * the target environment where the application is operating,
 * such as production, staging, or local development.
 * <p>
 * Each environment type is associated with a string that serves
 * as its identifier. It provides functionality to convert a string
 * name to its corresponding {@code EnvironmentType}.
 * <p>
 * Enum Constants:
 * - PRODUCTION: Represents the production environment.
 * - STAGING: Represents the staging environment.
 * - LOCAL: Represents the local development environment.
 */
@AllArgsConstructor
public enum EnvironmentType {
    /**
     * Represents the production environment.
     * This constant is used to denote the live, user-facing environment
     * where the application operates under normal conditions.
     */
    PRODUCTION("production"), STAGING("staging"), LOCAL("local");

    /**
     * The string representation of the environment type.
     * This field associates each {@code EnvironmentType} enum constant
     * with a unique string identifier, such as "production", "staging", or "local".
     * It is primarily used for mapping and identifying environment types
     * by their string equivalents.
     */
    @Getter private String type;

    /**
     * Converts a given string to its corresponding {@code EnvironmentType}.
     * The comparison is case-insensitive. If no matching environment type is found,
     * the {@code LOCAL} type is returned as the default.
     *
     * @param name the name of the environment type as a string
     * @return the {@code EnvironmentType} that corresponds to the given string,
     * or {@code LOCAL} if no match is found
     */
    public static EnvironmentType fromString(String name) {
        for (EnvironmentType type : values()) {
            if (type.getType().equalsIgnoreCase(name)) return type;
        }
        return LOCAL;
    }
}
