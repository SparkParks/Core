package network.palace.core.economy;

/**
 * An abstract class representing a transaction callback mechanism.
 * This class defines a template for handling transaction results and any errors that may occur.
 *
 * Subclasses are responsible for providing an implementation of the {@code callback} method.
 * The {@code handled} method serves as a wrapper to invoke the defined {@code callback}
 * and handles any potential exceptions that may arise.
 */
public abstract class TransactionCallback {
    /**
     * Invokes the {@code callback} method with the provided transaction result and error message,
     * and handles any exceptions that may occur during the callback execution.
     *
     * @param success a boolean indicating whether the transaction was successful
     * @param error a string containing an error message if the transaction failed, or {@code null} if there was no error
     */
    protected void handled(boolean success, String error) {
        try {
            callback(success, error);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * An abstract method to be implemented for handling transaction results.
     *
     * @param success a boolean indicating whether the transaction was successful
     * @param error a string containing an error message if the transaction failed, or {@code null} if there was no error
     */
    public abstract void callback(boolean success, String error);
}
