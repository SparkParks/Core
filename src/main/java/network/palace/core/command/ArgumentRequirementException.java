package network.palace.core.command;

/**
 * The ArgumentRequirementException is a specific type of {@link CommandException} that is thrown
 * when an argument requirement for a command is not met.
 * This exception implements {@link FriendlyException}, allowing for the retrieval of a friendly message
 * to provide user-friendly feedback in the event of an error.
 */
public final class ArgumentRequirementException extends CommandException implements FriendlyException {

    /**
     * Constructs a new {@code ArgumentRequirementException} with the specified detail message.
     * This exception is thrown when a command argument requirement is not satisfied.
     *
     * @param message the detail message, which provides additional context about the specific
     *                argument requirement that was violated
     */
    public ArgumentRequirementException(String message) {
        super(message);
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
