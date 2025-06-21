package network.palace.core.command;

/**
 * Represents an exception that is thrown when a user attempts to execute a command
 * without having the necessary permissions.
 * <p>
 * This exception serves as a specific type of {@link CommandException}, typically used
 * when access control or permission validation fails during the execution of a command.
 * <p>
 * By implementing the {@link FriendlyException} interface, this exception provides
 * a user-friendly error message that can be displayed to end-users, making it
 * suitable for applications that require clear and concise error reporting.
 */
public final class PermissionException extends CommandException implements FriendlyException {

    /**
     * Constructs a new {@code PermissionException} with a predefined error message
     * indicating that a command execution failed due to insufficient permissions.
     * <p>
     * This exception is typically thrown when a user attempts to execute a command
     * they do not have the required rank or permission to access.
     */
    public PermissionException() {
        super("command.error.permissions");
    }

    /**
     * Retrieves a user-friendly error message for the associated exception.
     * This method is intended to provide a clear and human-readable message
     * that can be presented directly to end-users, suitable for applications
     * requiring informative error reporting.
     *
     * @return a user-friendly message describing the exception
     */
    @Override
    public String getFriendlyMessage() {
        return getMessage();
    }
}
