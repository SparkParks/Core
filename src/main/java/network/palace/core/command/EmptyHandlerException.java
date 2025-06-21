package network.palace.core.command;

/**
 * Represents an exception that is thrown when there is an invalid or empty handler
 * during the execution or processing of a command.
 * <p>
 * This exception serves as a specific type of {@link CommandException} to indicate
 * issues related to the absence or invalidity of a handler required to process a command.
 * <p>
 * Additionally, this exception implements {@link FriendlyException}, allowing for
 * a user-friendly message about the error to be retrieved and displayed.
 */
public final class EmptyHandlerException extends CommandException implements FriendlyException {

    /**
     * Constructs a new {@code EmptyHandlerException} with a default error message.
     * <p>
     * This exception is thrown to indicate that a command handler is invalid or empty,
     * preventing further execution or processing. It is used as a specific type of
     * {@code CommandException} to signify such errors related to command handlers.
     */
    public EmptyHandlerException() {
        super("command.error.handler.invalid");
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
