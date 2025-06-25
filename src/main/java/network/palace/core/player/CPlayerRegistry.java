package network.palace.core.player;

/**
 * The {@code CPlayerRegistry} interface provides a contract to manage key-value pair entries
 * where keys are {@link String} and values are {@link Object}.
 * This registry allows querying, adding, removing, and clearing of entries.
 * <p>
 * Implementations of this interface are expected to ensure proper management of the lifecycle of entries,
 * including checking for null values and potential key collisions.
 */
public interface CPlayerRegistry {

    /**
     * Retrieves an entry from the registry based on the specified key.
     * <p>
     * This method searches for the given key in the registry and returns the
     * associated value if it exists. If no entry is found for the key, {@code null} is returned.
     *
     * @param key the key associated with the desired entry; must not be {@code null}.
     * @return the value associated with the specified key, or {@code null} if no entry exists for the key.
     */
    Object getEntry(String key);

    /**
     * Checks if an entry with the specified key exists in the registry.
     *
     * <p>This method allows querying the existence of a key within the registry
     * without retrieving its associated value.
     *
     * @param key the key to search for in the registry; must not be {@code null}.
     * @return {@code true} if the registry contains an entry with the specified key,
     *         {@code false} otherwise.
     */
    boolean hasEntry(String key);

    /**
     * Adds a key-value pair entry into the registry. The key must be unique within the registry
     * and cannot be null. The value can be any object, but null values are not allowed.
     * If an entry with the given key already exists, it should be updated with the new value.
     * <p>
     * Implementations must handle potential conflicts and edge cases, such as duplicate keys
     * and invalid inputs.
     *
     * @param key a unique identifier to associate with the given value. This must not be null.
     * @param o   the object to associate with the given key. This must not be null.
     */
    void addEntry(String key, Object o);

    /**
     * Removes the entry associated with the specified key from the registry.
     * If the key exists, the corresponding value will be removed and returned.
     * If the key does not exist, null will be returned.
     *
     * <p>
     * This operation allows precise removal of an entry and is a safe operation,
     * typically used when managing dynamically added key-value pair entries.
     *
     * @param key the {@link String} key of the entry to be removed. Cannot be null.
     *            <ul>
     *                <li>If the key is null, a {@code NullPointerException} may be thrown by implementations
     *                    or the implementation may handle it with a no-op.</li>
     *                <li>If the key is not found in the registry, the method will return {@code null}.</li>
     *            </ul>
     * @return the {@code Object} value associated with the specified key if the key exists.
     *         <ul>
     *             <li>Returns {@code null} if the key does not exist in the registry.</li>
     *             <li>The removed value can be further inspected or reused by the caller.</li>
     *         </ul>
     */
    Object removeEntry(String key);

    /**
     * Clears all entries in the registry, removing all key-value pairs currently stored.
     * <p>
     * After this method is invoked, the registry will be empty, and querying for any key will return no result.
     * Implementations should ensure that resources associated with the removed entries are properly cleaned up,
     * if applicable, to prevent resource leaks.
     * <p>
     * Notes:
     * <ul>
     *   <li>Any subsequent operations on the empty registry should conform to the defined behavior of an empty state.</li>
     *   <li>This method does not throw exceptions even if the registry is already empty.</li>
     * </ul>
     */
    void clearRegistry();
}
