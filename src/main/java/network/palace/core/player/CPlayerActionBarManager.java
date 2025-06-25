package network.palace.core.player;

/**
 * The {@code CPlayerActionBarManager} interface provides an abstraction for managing
 * and displaying action bar messages for players in a game.
 *
 * <p>This interface is primarily responsible for sending text-based messages
 * to the action bar area of a player's UI. Implementations of this interface
 * can define the behavior of how messages are processed and shown to the player.
 *
 * <p>Use cases:
 * <ul>
 *   <li>Displaying short, prominent messages or notifications to players.</li>
 *   <li>Providing real-time feedback or status updates during gameplay.</li>
 * </ul>
 *
 * <p>It is designed to be implemented by classes that manage player-specific
 * user interface interactions related to the action bar.
 */
public interface CPlayerActionBarManager {
    /**
     * Displays a message to the player's action bar.
     *
     * <p>This method is used to send a short, text-based message to the player's action bar
     * in the game interface. The message will be rendered prominently for the player
     * to notice and is typically used for brief notifications or real-time updates.
     *
     * @param message the text to be displayed in the player's action bar.
     *                <ul>
     *                  <li>Must not be null or empty.</li>
     *                  <li>Should be concise to ensure proper display in the action bar.</li>
     *                </ul>
     */
    void show(String message);
}
