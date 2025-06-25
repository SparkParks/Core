package network.palace.core.player.impl.managers;

import com.comphenix.protocol.wrappers.EnumWrappers;
import network.palace.core.packets.server.chat.WrapperPlayServerChat;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerActionBarManager;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.AllArgsConstructor;

/**
 * The {@code CorePlayerActionBarManager} class is an implementation of the
 * {@link CPlayerActionBarManager} interface, designed to manage and display
 * action bar messages for a specific player in the game.
 *
 * <p>This class utilizes the underlying packet system to send messages to the
 * player's action bar. Messages are displayed prominently in the game interface,
 * often used for real-time notifications or updates intended to capture the
 * player's attention quickly.
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Provide a mechanism to send action bar messages to the associated player.</li>
 *   <li>Utilize appropriate server-side packets to ensure the message is delivered.</li>
 * </ul>
 *
 * <p>The {@code show} method, in particular, is responsible for constructing and sending
 * the necessary packet to display text on the action bar.
 *
 * <h3>Implementation Details</h3>
 * <ul>
 *   <li>The {@link WrapperPlayServerChat} is used to create and send the packet for
 *       rendering the message.</li>
 *   <li>The {@code EnumWrappers.ChatType.GAME_INFO} is specified to ensure the message
 *       appears in the action bar region.</li>
 *   <li>The {@link WrappedChatComponent#fromText(String)} method is used to create
 *       the appropriate chat component from the provided message string.</li>
 *   <li>The {@code CPlayer#sendPacket} method is invoked to send the constructed packet
 *       to the player.</li>
 * </ul>
 *
 * <p>Instances of this class are tightly coupled to the {@code CPlayer} object,
 * which represents the player for whom the action bar messages will be managed.
 *
 * <h3>Thread Safety</h3>
 * <p>This class does not guarantee thread safety and should generally be accessed
 * within the context of the player's update/interaction thread to avoid concurrency
 * issues with the underlying systems.
 */
@AllArgsConstructor
public class CorePlayerActionBarManager implements CPlayerActionBarManager {

    /**
     * Represents the player associated with this action bar manager.
     * <p>
     * The {@code player} is an instance of {@link CPlayer} that signifies the specific
     * player to whom action bar messages will be sent. This association ensures the
     * functionality provided by this class is targeted at a single player.
     * </p>
     * <p>
     * It serves as the core component enabling the transmission of real-time updates,
     * notifications, or other information through the action bar system specific to
     * this player.
     * </p>
     * <h3>Key Responsibilities</h3>
     * <ul>
     *   <li>Acts as the recipient for action bar packets created by the manager.</li>
     *   <li>Provides methods for communication with the player, such as sending packets.</li>
     *   <li>Is crucial for the execution of core features of this class, including
     *       the display of messages via the action bar.</li>
     * </ul>
     */
    private final CPlayer player;

    /**
     * Displays an action bar message to the associated player.
     *
     * <p>This method constructs and sends a packet to display a message in the action bar region
     * of the game interface. The message is sent specifically to the player represented
     * by this instance.</p>
     *
     * <p>Key operations performed in this method:</p>
     * <ul>
     *   <li>Creates a {@link WrapperPlayServerChat} packet instance to encapsulate the chat message.</li>
     *   <li>Sets the message position using {@link EnumWrappers.ChatType#GAME_INFO} to indicate
     *       the action bar region.</li>
     *   <li>Translates the provided string message into a compatible chat component using
     *       {@link WrappedChatComponent#fromText(String)}.</li>
     *   <li>Sends the generated packet to the associated player using the {@code sendPacket} method.</li>
     * </ul>
     *
     * @param message The message to be displayed in the player's action bar. Must not be null.
     *                <ul>
     *                  <li>This string represents the text to be shown in the action bar.</li>
     *                  <li>Ensure the message is appropriately formatted for the intended display.</li>
     *                </ul>
     */
    @Override
    public void show(String message) {
        WrapperPlayServerChat packet = new WrapperPlayServerChat();
        packet.setPosition(EnumWrappers.ChatType.GAME_INFO);
        packet.setMessage(WrappedChatComponent.fromText(message));
        player.sendPacket(packet);
    }
}
