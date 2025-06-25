package network.palace.core.player;

/**
 * Represents a manager for handling the display of player headers and footers.
 * This interface allows customization of headers and footers displayed to the player
 * and provides methods to manage their visibility and update their content.
 *
 * <p>Core features include:</p>
 * <ul>
 *     <li>Setting custom header text.</li>
 *     <li>Setting custom footer text.</li>
 *     <li>Simultaneously setting both header and footer text.</li>
 *     <li>Hiding the header and footer from visibility.</li>
 *     <li>Triggering an update for the displayed header and footer content.</li>
 * </ul>
 */
public interface CPlayerHeaderFooterManager {
    /**
     * Sets the footer text to be displayed to the player. The footer is shown
     * at the bottom of any applicable user interface or HUD element where headers
     * and footers are supported.
     *
     * <p>The footer can include custom text formatting and may be updated dynamically
     * as needed. Setting the footer to <code>null</code> may result in clearing the
     * currently displayed footer, depending on the implementation.</p>
     *
     * @param footer the text to set as the footer; may include formatting codes or be an empty
     *               string to clear any existing footer. If <code>null</code>, the footer might
     *               be removed or defaulted to an empty state as per the implementation.
     */
    void setFooter(String footer);

    /**
     * Sets the header text to be displayed to the player.
     *
     * <p>This method allows the customization of the header section that appears
     * at the top of the player's display interface.</p>
     *
     * @param header The text to be displayed as the header. This can include
     *               formatting codes or special characters as supported by the
     *               client's implementation.
     */
    void setHeader(String header);

    /**
     * Sets both the header and footer text to be displayed to the player.
     *
     * <p>This method allows the customization of both header and footer content
     * simultaneously and ensures that the new content is displayed to the player.</p>
     *
     * <ul>
     *     <li>The header is displayed at the top of the interface.</li>
     *     <li>The footer is displayed at the bottom of the interface.</li>
     * </ul>
     *
     * @param header the text to be displayed as the header; can include additional formatting or placeholders.
     * @param footer the text to be displayed as the footer; can include additional formatting or placeholders.
     */
    void setHeaderFooter(String header, String footer);

    /**
     * Hides the player's header and footer from visibility.
     *
     * <p>This method removes any displayed header and footer content
     * that the player may currently see. Typically used when you want
     * to temporarily or permanently clear the player's display of this
     * information.</p>
     *
     * <p>Use cases include:</p>
     * <ul>
     *     <li>Clearing the interface for a more immersive experience.</li>
     *     <li>Resetting the player's display before updating or reconfiguring the header/footer.</li>
     *     <li>Hiding unnecessary information when not relevant.</li>
     * </ul>
     */
    void hide();

    /**
     * Updates the currently displayed header and footer content for the player.
     * <p>
     * This method triggers a refresh of the player's header and footer display
     * with the latest values set through the {@code setHeader}, {@code setFooter},
     * or {@code setHeaderFooter} methods.
     * </p>
     *
     * <p>Key points:</p>
     * <ul>
     *     <li>Ensures that any changes made to the header or footer are applied immediately.</li>
     *     <li>Should be called after modifying header or footer text to reflect updated content.</li>
     *     <li>Can be used to manually force the reapplication of header and footer display.</li>
     * </ul>
     */
    void update();
}
