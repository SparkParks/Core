package network.palace.core.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a generic exception that occurs during the execution or processing of a command.
 * This class serves as a base exception for more specific command-related exceptions.
 * <p>
 * It is typically used to provide a unified structure for handling command errors in the system.
 * Subclasses can extend this exception to define more specific command-related error cases.
 */
@AllArgsConstructor
public class CommandException extends Exception {
    /**
     * The detailed message or description associated with the exception.
     * <p>
     * This message typically provides context or specific information
     * about the error that occurred during command execution or processing.
     * <p>
     * It is primarily used to aid developers and users in understanding
     * the cause of the exception or its associated details.
     */
    @Getter private final String message;
}
