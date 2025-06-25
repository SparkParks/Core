package network.palace.core.player.impl;

import lombok.RequiredArgsConstructor;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerRegistry;

import java.util.HashMap;

/**
 * The {@code CorePlayerRegistry} class provides an implementation of the {@link CPlayerRegistry}
 * interface, enabling the management of key-value pair entries associated with a specific {@code CPlayer}.
 * This registry facilitates storing, querying, updating, and removing data entries tied to the provided
 * {@code CPlayer} instance.
 *
 * <p>The core functionality of this class revolves around:</p>
 * <ul>
 *   <li>Associating data entries with the represented {@link CPlayer} instance.</li>
 *   <li>Ensuring that no duplicate keys exist within the entries.</li>
 *   <li>Handling null values by explicitly throwing {@link IllegalArgumentException} for invalid inputs.</li>
 *   <li>Providing a clean and manageable structure for modifying or clearing the registry of all entries.</li>
 * </ul>
 *
 * <p><b>Thread Safety:</b>
 * Instances of this class are not inherently thread-safe. Synchronization,
 * if required, must be handled externally by the caller.</p>
 *
 * <p><b>Usage:</b>
 * This class is designed to be instantiated by passing the required {@code CPlayer} object to the constructor,
 * which acts as the reference point for all registry operations. The internal data is stored in a {@link HashMap},
 * providing efficient key-value mapping and retrieval.</p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>Implements all key methods defined in {@link CPlayerRegistry}, including retrieval, existence checks,
 *       addition, removal, and clearing of registry entries.</li>
 *   <li>Provides detailed checks and exceptions for invalid inputs (e.g., {@code null} values).</li>
 *   <li>Uses a private {@code HashMap} to isolate and encapsulate the registry's internal state.</li>
 * </ul>
 *
 * <p><b>Design Decisions:</b>
 * <ul>
 *   <li>All operations ensure that the integrity of the registry is maintained.</li>
 *   <li>{@code player} is a final reference, ensuring the association with the {@code CPlayer} instance
 *       remains constant throughout the lifecycle of the registry.</li>
 * </ul>
 */
@RequiredArgsConstructor
public class CorePlayerRegistry implements CPlayerRegistry {
    /**
     * Represents the primary CPlayer instance associated with this registry.
     * <p>
     * This variable is final and initialized via the constructor of the containing class,
     * ensuring that the player reference remains immutable. The {@code player} is utilized
     * to maintain or access the core functionality related to a CPlayer entity.
     * <p>
     * <b>Usage Considerations:</b>
     * <ul>
     *   <li>This variable is not modifiable after the initialization of the containing class.</li>
     *   <li>Acts as a critical link between the registry and the associated CPlayer entity.</li>
     * </ul>
     */
    private final CPlayer player;
    /**
     * <p>This variable represents a map structure used for managing key-value pairs within the
     * player registry. The key is a {@link String} and the value is a generic {@link Object},
     * allowing users to store various types of data associated with specific keys.</p>
     *
     * <p>The map supports the following operations:</p>
     * <ul>
     *   <li>Adding entries with a specified key and value.</li>
     *   <li>Retrieving values based on their corresponding keys.</li>
     *   <li>Checking if a specific key exists in the map.</li>
     *   <li>Removing entries by their key.</li>
     *   <li>Clearing all entries in the map.</li>
     * </ul>
     *
     * <p>The map is private and accessed through the methods provided in the containing class.</p>
     */
    private HashMap<String, Object> map = new HashMap<>();

    /**
     * Retrieves the value associated with the specified key from the internal registry.
     * <p>
     * If the provided key is <code>null</code>, an {@link IllegalArgumentException} is thrown.
     * </p>
     *
     * @param key A non-null {@link String} representing the key whose associated value is to be retrieved.
     * @return The value associated with the specified key, or <code>null</code> if the key does not exist in the registry.
     * @throws IllegalArgumentException if the key is <code>null</code>.
     */
    @Override
    public Object getEntry(String key) {
        if (key == null) throw new IllegalArgumentException("key cannot be null!");
        return map.get(key);
    }

    /**
     * Checks if the registry contains an entry associated with the specified key.
     *
     * <p>This method determines whether the provided key exists in the internal map
     * used by the registry.</p>
     *
     * @param key the key to check for existence in the registry; must not be {@code null}
     * @return {@code true} if the key exists in the registry, {@code false} otherwise
     * @throws IllegalArgumentException if the provided key is {@code null}
     */
    @Override
    public boolean hasEntry(String key) {
        if (key == null) throw new IllegalArgumentException("key cannot be null!");
        return map.containsKey(key);
    }

    /**
     * Adds an entry to the registry with the specified key and associated object.
     * <p>
     * The key must not be null, and the associated object must also not be null.
     * If either is null, an {@link IllegalArgumentException} will be thrown.
     * </p>
     *
     * @param key the unique identifier for the entry in the registry. Must not be null.
     * @param o the object to associate with the provided key. Must not be null.
     * @throws IllegalArgumentException if the key or object is null.
     */
    @Override
    public void addEntry(String key, Object o) {
        if (key == null) throw new IllegalArgumentException("key cannot be null!");
        if (o == null) throw new IllegalArgumentException("entry object cannot be null!");
        map.put(key, o);
    }

    /**
     * Removes the entry associated with the specified key from the internal registry map.
     * <p>
     * If the specified key exists in the map, its corresponding value is removed and returned.
     * If the key does not exist, this method will return {@code null}.
     * </p>
     *
     * @param key the key whose associated entry is to be removed from the map. Must not be {@code null}.
     * @return the previous value associated with the specified key, or {@code null} if there was no mapping for the key.
     * @throws IllegalArgumentException if the provided key is {@code null}.
     */
    @Override
    public Object removeEntry(String key) {
        return map.remove(key);
    }

    /**
     * Clears all entries in the registry.
     * <p>
     * This method removes all key-value pairs stored in the internal data structure,
     * effectively resetting the registry to an empty state.
     * </p>
     * <p>
     * Use this method with caution, as all existing data held in the registry will be lost.
     * </p>
     */
    @Override
    public void clearRegistry() {
        map.clear();
    }
}
