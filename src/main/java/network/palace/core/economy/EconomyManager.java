package network.palace.core.economy;

import network.palace.core.Core;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.economy.currency.Transaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * The EconomyManager class is responsible for managing and processing
 * financial transactions for players. It handles the queuing, validation,
 * and asynchronous processing of these transactions, and interacts with
 * the database to update account balances.
 * <p>
 * Transactions are processed in batches at regular intervals, with a limit
 * on the number of transactions handled per task execution. Callbacks are
 * used to notify the source of a transaction's success or failure.
 */
public class EconomyManager {
    /**
     * Represents a map that stores transactions associated with unique identifiers (UUIDs).
     * Each entry in the map contains a UUID as the key, representing the unique player or
     * transaction identifier, and a {@link Transaction} object as the value, containing
     * detailed information about the transaction.
     * <p>
     * This map is used to manage and track economic transactions, linking the details of
     * each transaction to a specific UUID. The map is immutable after initialization due
     * to its designation as a {@code final} field, ensuring the reference to the map cannot
     * be reassigned.
     */
    private final Map<UUID, Transaction> transactions = new HashMap<>();

    /**
     * Constructor for EconomyManager. Initializes and schedules a repeating task that processes
     * queued transactions in batches for efficient handling.
     * <p>
     * The constructor sets up a timer task that:
     * - Processes up to a limited number of transactions per iteration.
     * - Removes transactions from the main transaction queue and processes them asynchronously.
     * - Handles any database operations necessary to update the state of individual transactions.
     * - Notifies the corresponding callback of each transaction regarding its success or failure.
     * <p>
     * Database operations and transaction handling are performed asynchronously to prevent
     * blocking the main server thread. Failed transactions are logged, and callbacks are notified
     * accordingly.
     */
    public EconomyManager() {
        Core.runTaskTimer(() -> {
            boolean started = false;
            try {
                // Process at most 20 transactions per second
                Map<UUID, Transaction> localMap = getPartOfMap(transactions, 10);
                started = true;
                // Remove payments about to be processed from main transactions map
                localMap.keySet().forEach(transactions::remove);
                // Asynchronously handle database calls for localMap payments
                Core.runTaskAsynchronously(() -> {
                    for (Map.Entry<UUID, Transaction> entry : new HashSet<>(localMap.entrySet())) {
                        Transaction transaction = entry.getValue();
                        if (transaction.getAmount() == 0) {
                            if (transaction.getCallback() != null)
                                transaction.getCallback().handled(false, "Cannot process transaction of amount 0.");
                            continue;
                        }
                        try {
                            Core.getMongoHandler().changeAmount(transaction.getPlayerId(), transaction.getAmount(), transaction.getSource(), transaction.getType(), false);
                            if (transaction.getCallback() != null) transaction.getCallback().handled(true, "");
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (transaction.getCallback() != null)
                                transaction.getCallback().handled(false, "An error occurred while contacting the database to process this transaction.");
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                if (started) {
                    transactions.values().forEach(transaction -> {
                        if (transaction.getCallback() != null)
                            transaction.getCallback().handled(false, "An error occurred while processing this transaction.");
                    });
                    transactions.clear();
                }
            }
        }, 0L, 20L);
    }

    /**
     * Retrieves a subset of the given map containing at most the specified number of entries.
     *
     * @param map the original map from which a subset will be extracted
     * @param amount the maximum number of entries to include in the resulting map
     * @return a new map containing up to the specified number of entries from the original map
     */
    private Map<UUID, Transaction> getPartOfMap(Map<UUID, Transaction> map, int amount) {
        if (map.size() <= amount) return new HashMap<>(map);
        Map<UUID, Transaction> mapPart = new HashMap<>();
        int i = 0;
        for (Map.Entry<UUID, Transaction> entry : map.entrySet()) {
            if (i >= amount) break;
            mapPart.put(entry.getKey(), entry.getValue());
            i++;
        }
        return mapPart;
    }

    /**
     * Adds a new transaction to the transaction queue for processing.
     *
     * @param uuid     the unique identifier of the player associated with this transaction
     * @param amount   the amount of currency involved in the transaction
     * @param source   the source or reason for the transaction
     * @param type     the type of currency being transacted, defined by the {@link CurrencyType} enum
     * @param callback the callback to be executed upon completion of the transaction, indicating success or failure
     */
    public void addTransaction(UUID uuid, int amount, String source, CurrencyType type, TransactionCallback callback) {
        Transaction transaction = new Transaction(uuid, type, amount, source, callback);
        transactions.put(transaction.getPaymentId(), transaction);
    }
}
