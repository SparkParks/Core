package network.palace.core.economy.currency;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import network.palace.core.economy.TransactionCallback;

import java.util.UUID;

/**
 * Represents a transaction in the system.
 * <p>
 * A transaction includes details such as the payment ID, the player involved in the transaction,
 * the type of currency being transacted, the amount of that currency, the source of the transaction,
 * and a callback to be invoked upon completion of the transaction.
 * <p>
 * This class is immutable and thread-safe. The payment ID is automatically generated
 * and uniquely identifies each transaction.
 */
@RequiredArgsConstructor
public class Transaction {
    /**
     * A unique identifier for the transaction.
     * <p>
     * The {@code paymentId} is automatically generated using a Universally Unique Identifier (UUID),
     * ensuring that each transaction has a distinct and immutable identifier. This ID can be used to
     * track and reference individual transactions throughout the system.
     */
    @Getter private final UUID paymentId = UUID.randomUUID();

    /**
     * A unique identifier representing the player associated with the transaction.
     * <p>
     * The {@code playerId} is a Universally Unique Identifier (UUID) that uniquely identifies a player
     * within the system. It ensures that transactions are accurately linked to the corresponding player.
     * The value of this field is immutable and is required when creating a transaction.
     */
    @Getter private final UUID playerId;

    /**
     * Specifies the type of currency used in the transaction.
     * <p>
     * This field references the {@code CurrencyType} enumeration, which defines the
     * various types of currencies available in the system, such as BALANCE, TOKENS,
     * and ADVENTURE. The {@code type} field is immutable and captures the currency
     * classification relevant to the transaction.
     */
    @Getter private final CurrencyType type;

    /**
     * Represents the amount of currency involved in the transaction.
     * <p>
     * The {@code amount} specifies the quantity of the selected {@code CurrencyType}
     * that is being transacted. This value is immutable and directly influences the
     * nature and impact of the transaction within the system. It must always be
     * non-negative, as negative values are not supported for transactions.
     */
    @Getter private final int amount;

    /**
     * Describes the origin or cause associated with the transaction.
     * <p>
     * The {@code source} field specifies the context or initiating factor behind
     * the transaction. This could represent various origins, such as a specific
     * user action, an automated process, or a system event that triggered the
     * transaction. The value of this field is immutable and provides additional
     * insight into the nature of the transaction.
     */
    @Getter private final String source;

    /**
     * Represents a callback to be executed upon the completion of the transaction.
     * The {@code callback} is an instance of a {@link TransactionCallback} that facilitates
     * handling the outcome of the transaction, including success or failure states,
     * and any error messages that may be associated with a failed transaction.
     * <p>
     * This field is immutable and is provided during the creation of the transaction object.
     * The callback implementation must define the specific behavior to handle transaction results.
     */
    @Getter private final TransactionCallback callback;
}
