package network.palace.core.player;

/**
 * Represents the status of a player in the system.
 *
 * <p>This enumeration is used to describe different states that a player can
 * transition through during the lifecycle of their interaction with the system.</p>
 *
 * <p>The possible statuses include:</p>
 * <ul>
 *   <li><b>LOGIN</b> - Indicates that the player has logged into the system.</li>
 *   <li><b>JOINED</b> - Indicates that the player has joined the gameplay or a session.</li>
 *   <li><b>LEFT</b> - Indicates that the player has exited or left the session.</li>
 * </ul>
 */
public enum PlayerStatus {
    /**
     * Login player status.
     */
    LOGIN,
    /**
     * Joined player status.
     */
    JOINED,
    /**
     * Left player status.
     */
    LEFT
}
