package network.palace.core.player;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * The <code>CPlayerManager</code> interface serves as a management system
 * for player-specific functionalities within the application.
 * It provides methods to handle player-related actions such as login, logout,
 * message broadcasting, and retrieval of player information.
 *
 * <p>Features include:</p>
 * <ul>
 *     <li>Tracking player login and logout events</li>
 *     <li>Managing active players</li>
 *     <li>Broadcasting messages to players</li>
 *     <li>Fetching player data based on various identifiers</li>
 *     <li>Displaying player ranks to all players</li>
 * </ul>
 */
public interface CPlayerManager {

    /**
     * Retrieves the count of currently active players in the system.
     *
     * <p>This method provides the number of players who are currently online or active,
     * useful in scenarios where monitoring or managing player activities is required.</p>
     *
     * @return the total count of active players.
     */
    int getPlayerCount();

    /**
     * Logs a player into the system using their unique identifier and name.
     *
     * <p>This method should be called when a player successfully connects
     * and is authenticated. It updates internal records to mark the player
     * as logged in and performs any necessary setup for the player's session.</p>
     *
     * @param uuid the unique identifier (UUID) of the player
     * @param name the display name of the player
     * @throws Exception if an error occurs during the login process
     */
    void playerLoggedIn(UUID uuid, String name) throws Exception;

    /**
     * Handles the event when a player joins the system. This method is responsible
     * for performing any necessary actions or updates that need to occur
     * when a new player is added.
     *
     * <p>Possible actions may include:</p>
     * <ul>
     *     <li>Registering the player in the system</li>
     *     <li>Updating active player lists</li>
     *     <li>Assigning default settings or roles for the player</li>
     * </ul>
     *
     * @param player the {@link Player} object representing the player who joined
     */
    void playerJoined(Player player);

    /**
     * Handles the event when a player logs out of the system.
     * <p>This method should be invoked to perform any necessary cleanup
     * or state updates related to the player when they log out.</p>
     *
     * @param player the {@link Player} object representing the player who logged out
     */
    void playerLoggedOut(Player player);

    /**
     * Removes a player from the system using their unique identifier (UUID).
     *
     * <p>Typically invoked when a player's data needs to be removed due to disconnection,
     * ban, or similar scenarios. This method ensures the player is no longer tracked
     * in the system's active player records.</p>
     *
     * <p>Possible actions performed by this method include:</p>
     * <ul>
     *     <li>Removing the player from internal data structures.</li>
     *     <li>Cleaning up resources associated with the player.</li>
     *     <li>Updating relevant lists or statistics affected by the player's removal.</li>
     * </ul>
     *
     * @param uuid the unique identifier (UUID) of the player to be removed.
     */
    void removePlayer(UUID uuid);

    /**
     * Broadcasts a message to all players currently active in the system.
     *
     * <p>This method sends the provided message to all players who are online. It is
     * intended to be used for system-wide announcements, notifications, or any other
     * communication where all active players need to be informed simultaneously.</p>
     *
     * @param message the message to be broadcasted to all active players. This should
     *                be a non-null and properly formatted string, as it will be sent
     *                directly to players.
     */
    void broadcastMessage(String message);

    /**
     * Retrieves the {@link CPlayer} object associated with the specified unique player identifier (UUID).
     *
     * <p>This method is used to look up a player within the system using their UUID.
     * If an associated {@link CPlayer} exists, it will be returned; otherwise, the
     * method may return <code>null</code> if the player is not found.</p>
     *
     * @param playerUUID the unique identifier (UUID) of the player to be retrieved.
     *                   This parameter must not be <code>null</code>.
     *
     * @return the {@link CPlayer} object associated with the specified UUID,
     *         or <code>null</code> if no player with the given UUID exists in the system.
     */
    CPlayer getPlayer(UUID playerUUID);

    /**
     * Retrieves a {@link CPlayer} object associated with the specified {@link Player}.
     *
     * <p>This method is used to fetch the internal representation of a player in the system.
     * The {@link CPlayer} instance contains the system-specific player details,
     * which may include additional information or functionality beyond the standard {@link Player} object.</p>
     *
     * @param player the {@link Player} object representing the player whose {@link CPlayer} instance
     *               is to be retrieved. This parameter must not be null and should represent
     *               a valid player tracked by the system.
     * @return the {@link CPlayer} instance corresponding to the given {@link Player}.
     *         If the player is not found or not currently active, the method may return null.
     */
    CPlayer getPlayer(Player player);

    /**
     * Retrieves the player object based on the provided player's name.
     *
     * <p>This method searches for a player in the system using their display name
     * and returns the associated {@link CPlayer} object if found.
     * It is useful for locating a specific player by name in scenarios
     * involving player management, communication, or data retrieval.</p>
     *
     * @param name the display name of the player to retrieve. This value should not be null
     *             and must match the name of a player currently registered in the system.
     * @return the {@link CPlayer} object representing the player with the specified name.
     *         Returns {@code null} if no player with the given name is found.
     */
    CPlayer getPlayer(String name);

    /**
     * Retrieves a list of all currently online players.
     *
     * <p>This method returns a list of {@link CPlayer} objects representing players who
     * are currently online in the system. This can be useful for tasks such as broadcasting
     * messages, analyzing real-time player activity, or managing active players.</p>
     *
     * @return a {@link List} containing {@link CPlayer} instances of all currently online players.
     *         The list will be empty if no players are online.
     */
    List<CPlayer> getOnlinePlayers();

    /**
     * Displays the rank of the specified player.
     *
     * <p>This method retrieves and showcases the rank of a player
     * within the system. It is useful for providing rank-based
     * information or status to the player or external systems.</p>
     *
     * @param player the {@link CPlayer} object representing the player whose rank is to be displayed.
     *               This parameter should not be null and must refer to a valid player
     *               currently managed within the system.
     */
    void displayRank(CPlayer player);
}
