package network.palace.core.errors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to configure error logging behavior and settings.
 * This annotation is typically used to define the parameters required for
 * sending error or log information (e.g., to an external service such as Rollbar).
 * It provides customizable options such as the access token,
 * environment type, and whether logging is enabled.
 * <p>
 * Attributes:
 * - `accessToken` specifies the token used for authenticating access to the logging service.
 * - `environment` indicates the application environment (e.g., production, staging, local).
 * - `enabled` determines whether error logging is currently enabled; defaults to `true`.
 * <p>
 * This annotation is retained at runtime and is applicable to types only.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ErrorLog {
    /**
     * Retrieves the access token used for authenticating with the logging service.
     *
     * @return a {@code String} representing the access token for authentication
     */
    String accessToken();

    /**
     * Retrieves the type of application environment being used.
     * This method returns a value from the {@code EnvironmentType} enum,
     * which represents predefined constants such as production, staging, or local environments.
     *
     * @return an {@code EnvironmentType} representing the current application environment
     */
    EnvironmentType environment();

    /**
     * Indicates whether error logging is enabled.
     *
     * @return {@code true} if error logging is enabled; {@code false} otherwise
     */
    boolean enabled() default true;
}
