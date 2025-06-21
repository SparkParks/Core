package network.palace.core.command;

import lombok.Getter;

/**
 * Represents an exception thrown when a command encounters an unhandled exception during processing.
 * <p>
 * This exception serves as a wrapper for another exception that occurs during the execution of a command.
 * It provides additional context by including the causing exception, allowing for easier debugging and handling
 * of unforeseen errors in the command execution flow.
 */
public class UnhandledCommandExceptionException extends CommandException {

    /**
     * Represents the root exception that caused the current exception to be thrown.
     * <p>
     * This field is intended to store the original exception that triggered an
     * {@link UnhandledCommandExceptionException}. It provides context and aids in
     * debugging by preserving the underlying reason for the failure, allowing for
     * further analysis or troubleshooting.
     */
    @Getter private final Exception causingException;

    /**
     * Constructs a new UnhandledCommandExceptionException with a specified underlying exception.
     * <p>
     * This constructor accepts an {@link Exception} that serves as the original cause of the
     * {@code UnhandledCommandExceptionException}. The causing exception is internally stored
     * to provide context for debugging and error analysis, and its message is included in
     * the exception description.
     *
     * @param e the original {@link Exception} that caused this exception to be thrown
     */
    public UnhandledCommandExceptionException(Exception e) {
        super("Unhandled exception " + e.getMessage());
        this.causingException = e;
    }
}
