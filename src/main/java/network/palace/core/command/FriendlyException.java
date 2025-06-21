package network.palace.core.command;

/// Old documentation
/// Implement this on any exception that extends [CommandException] and the return value of the [#getFriendlyMessage()]
/// method will be displayed instead of a verbose message for the exception in the default handler.

/**
 * The FriendlyException interface defines a contract for exceptions that are capable
 * of providing a user-friendly error message. This is particularly useful in scenarios
 * where exceptions need to convey meaningful and human-readable messages to end-users,
 * rather than generic or technical error details.
 * <p>
 * Classes implementing this interface must provide an implementation for the
 * `getFriendlyMessage` method, which retrieves the formatted message.
 */
public interface FriendlyException {
    /// Old documentation
    /// Grabs a friendly version of the message to be displayed during an exception.
    /// @return A message to be displayed to the user during failure by default.

    /**
     * Retrieves a user-friendly error message for the associated exception.
     * This method is intended to provide a clear and human-readable message
     * that can be presented directly to end-users, suitable for applications
     * requiring informative error reporting.
     *
     * @return a user-friendly message describing the exception
     */
    String getFriendlyMessage();
}
