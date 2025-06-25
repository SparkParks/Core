package network.palace.core.player.impl.managers;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import network.palace.core.packets.server.playerlist.WrapperPlayServerPlayerListHeaderFooter;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerHeaderFooterManager;

/**
 * <p>The {@code CorePlayerHeaderFooterManager} is an implementation of the {@link CPlayerHeaderFooterManager} interface,
 * responsible for managing and updating the header and footer displayed to a player.</p>
 *
 * <p>This class provides the functionality to:</p>
 * <ul>
 *     <li>Set the custom header text.</li>
 *     <li>Set the custom footer text.</li>
 *     <li>Simultaneously set both header and footer text.</li>
 *     <li>Hide the header and footer from visibility.</li>
 *     <li>Trigger an update for the displayed header and footer content.</li>
 * </ul>
 *
 * <p>The header represents the text displayed at the top of the player's interface, while the footer
 * is displayed at the bottom. These fields default to a single space (" ") if no text is provided
 * or if removed through the methods in this class.</p>
 *
 * <p>The class relies on sending custom packets to the client using {@code WrapperPlayServerPlayerListHeaderFooter},
 * ensuring that the display updates are synchronized with the player's user interface.</p>
 *
 * <h3>Key Features</h3>
 * <ul>
 *     <li>Internal handling of empty or null header and footer values by defaulting to a single space.</li>
 *     <li>Automated packet updates upon setting new header/footer values or hiding them.</li>
 *     <li>Seamless integration with the player's existing packet-based interface updates.</li>
 * </ul>
 *
 * <p>For proper usage, ensure that the provided {@link CPlayer} instance is properly initialized and capable
 * of sending packets to the client.</p>
 *
 * <h3>Example Use Cases</h3>
 * <ul>
 *     <li>Displaying server-specific notices in the header and footer.</li>
 *     <li>Providing dynamic or event-specific updates to a player's display.</li>
 *     <li>Clearing the header and footer to reset the player's interface for an immersive experience.</li>
 * </ul>
 *
 * <p>The internal methods of this class ensure any modifications to the header and footer are immediately
 * reflected to the player through the usage of the {@code update()} method, which sends the latest header and
 * footer content via packets to the client.</p>
 */
public class CorePlayerHeaderFooterManager implements CPlayerHeaderFooterManager {

    /**
     * <p>
     * Represents the core player associated with the {@code CorePlayerHeaderFooterManager} instance.
     * This variable is assigned during initialization and cannot be modified afterward.
     * </p>
     *
     * <p>
     * The {@code player} is primarily responsible for associating the actions and behavior
     * of header and footer management with a specific player in the system.
     * </p>
     *
     * <ul>
     * <li>Used for managing dynamic header and footer updates.</li>
     * <li>Provides the context for personalization and visibility control of player-specific UI components.</li>
     * </ul>
     */
    private final CPlayer player;

    /**
     * Represents the header text displayed for the player in a user interface,
     * such as a scoreboard or tab list.
     *
     * <p>The value of this field is a {@code String} representing the textual
     * content of the header. Modifications to this field affect the displayed
     * header for the associated player.</p>
     *
     * <p>Key details:</p>
     * <ul>
     *   <li>Default value: Empty string {@code " "}, indicating no header is set initially.</li>
     *   <li>Used typically in methods that set or update player-specific interface details.</li>
     *   <li>Can be updated dynamically to reflect relevant state or contextual changes.</li>
     * </ul>
     */
    private String header = " ";

    /**
     * <p>The <code>footer</code> variable represents the footer text displayed in the player's
     * header-footer section within the game. It is used to customize or update the content
     * shown at the bottom of the player's tab menu or interface.</p>
     *
     * <p>By default, this variable is initialized as an empty string (<code>" "</code>) and can
     * be modified through appropriate methods within the containing class to reflect
     * specific footer content for players.</p>
     *
     * <p>Usage scenarios:</p>
     * <ul>
     *   <li>Displaying server information or messages in the footer.</li>
     *   <li>Personalizing the player interface with dynamic data.</li>
     *   <li>Providing contextual game updates or announcements.</li>
     * </ul>
     */
    private String footer = " ";

    /**
     * Constructs a {@code CorePlayerHeaderFooterManager} object to manage header and footer
     * elements for a specified player.
     *
     * <p>This class is responsible for configuring and maintaining the header and footer
     * of the player's display.</p>
     *
     * @param player The {@code CPlayer} instance for which the header and footer management
     *               will be handled.
     */
    public CorePlayerHeaderFooterManager(CPlayer player) {
        this.player = player;
    }

    /**
     * Sets the footer text for the player list's header and footer display.
     * <p>
     * If the provided {@code footer} is {@code null} or an empty string,
     * a single space is set as the footer text. Otherwise, the provided
     * footer value is applied.
     * <p>
     * This method also triggers an update to reflect the changes.
     *
     * @param footer the text to set as the footer; can be a non-null string, null, or an empty string
     */
    @Override
    public void setFooter(String footer) {
        if (footer == null || footer.isEmpty()) {
            this.footer = " ";
            return;
        }
        this.footer = footer;
        update();
    }

    /**
     * Sets the header text for the player's header and footer display.
     *
     * <p>If the provided {@code header} is {@code null} or an empty string,
     * a single space is set as the header text. Otherwise, the provided
     * {@code header} value is applied.</p>
     *
     * <p>This method also triggers an update to reflect the changes.</p>
     *
     * @param header the text to set as the header; can be a non-null string, {@code null}, or an empty string
     */
    @Override
    public void setHeader(String header) {
        if (header == null || header.isEmpty()) {
            this.header = " ";
            return;
        }
        this.header = header;
        update();
    }

    /**
     * Sets the header and footer text for the player list's display.
     * <p>
     * This method configures both the header and footer text for a player's display.
     * If the provided {@code header} or {@code footer} is {@code null} or empty,
     * a single space will be applied as the default text. After setting the header and footer,
     * the {@code update} method is invoked to apply the changes.
     * </p>
     *
     * @param header the text to set as the header; if {@code null} or empty, a single space is used.
     * @param footer the text to set as the footer; if {@code null} or empty, a single space is used.
     */
    @Override
    public void setHeaderFooter(String header, String footer) {
        if (header == null || header.isEmpty()) {
            this.header = " ";
        } else {
            this.header = header;
        }
        if (footer == null || footer.isEmpty()) {
            this.footer = " ";
        } else {
            this.footer = footer;
        }
        update();
    }

    /**
     * Hides the header and footer of the player's display by setting their values to a single space.
     * <p>
     * This method is typically used to clear any existing header and footer content.
     * Once cleared, an update is triggered to ensure the changes are visible to the player.
     * </p>
     * <ul>
     *     <li>Sets the {@code header} field to a single space.</li>
     *     <li>Sets the {@code footer} field to a single space.</li>
     *     <li>Calls the {@code update()} method to reflect the changes.</li>
     * </ul>
     */
    @Override
    public void hide() {
        this.header = " ";
        this.footer = " ";
        update();
    }

    /**
     * Updates the player's header and footer display with the current values.
     *
     * <p>This method constructs a {@link WrapperPlayServerPlayerListHeaderFooter} packet to manage
     * the player's list header and footer display. The header and footer are set using the currently
     * configured values and the packet is sent to the player via the underlying network system.</p>
     *
     * <p>Steps performed in this process:
     * <ul>
     *   <li>A new {@code WrapperPlayServerPlayerListHeaderFooter} object is instantiated.</li>
     *   <li>The header and footer values are converted to {@link WrappedChatComponent} instances
     *       and set in the packet.</li>
     *   <li>The configured packet is sent to the player using {@code sendPacket}.</li>
     * </ul>
     * </p>
     *
     * <p>This method typically reflects the latest header and footer
     * configuration changes for the player's display in real-time.</p>
     */
    @Override
    public void update() {
        WrapperPlayServerPlayerListHeaderFooter packet = new WrapperPlayServerPlayerListHeaderFooter();
        packet.setHeader(WrappedChatComponent.fromText(header));
        packet.setFooter(WrappedChatComponent.fromText(footer));
        player.sendPacket(packet);
    }
}
