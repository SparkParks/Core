package network.palace.core.player;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

/**
 * Manages player-specific boss bars, including their appearance, state, and behavior.
 *
 * <p>This interface provides methods to manipulate and control boss bars as seen
 * by individual players. It allows customization of text, progress, color, style,
 * and visibility. Additionally, it supports managing the lifecycle of boss bars by
 * showing, hiding, or removing them completely.</p>
 *
 * <p>Common use cases for this interface include:
 * <ul>
 *   <li>Displaying progress for actions or events.</li>
 *   <li>Communicating status updates or messages to players.</li>
 *   <li>Providing visual indicators during gameplay, such as objectives or threats.</li>
 * </ul>
 */
public interface CPlayerBossBarManager {

    /**
     * Updates the title or text displayed on the boss bar.
     *
     * <p>This method allows customization of the boss bar's text display by setting
     * it to the provided title parameter. The text is typically used to convey
     * information or status updates associated with the boss bar.</p>
     *
     * @param title the new text to display on the boss bar.
     *              <ul>
     *                  <li>Must not be {@code null}.</li>
     *                  <li>Can include Minecraft formatting codes for colors and styles.</li>
     *                  <li>Empty strings will clear the current text displayed on the boss bar.</li>
     *              </ul>
     */
    void setText(String title);

    /**
     * Sets the progress value for the player boss bar.
     *
     * <p>
     * The progress value must be a double between 0.0 and 1.0,
     * where 0.0 represents an empty bar and 1.0 represents a
     * full bar. Values outside this range may result in undefined behavior.
     * </p>
     *
     * @param progress the progress value, ranging from 0.0 to 1.0
     */
    void setProgress(double progress);

    /**
     * Sets the color of the boss bar.
     *
     * <p>This method specifies the visual color of the boss bar when it is displayed
     * to players. The color helps in visually distinguishing boss bars with different purposes
     * or states.</p>
     *
     * @param color the {@link BarColor} to set for the boss bar. Valid values include:
     * <ul>
     *     <li>{@code BarColor.PINK}</li>
     *     <li>{@code BarColor.BLUE}</li>
     *     <li>{@code BarColor.RED}</li>
     *     <li>{@code BarColor.GREEN}</li>
     *     <li>{@code BarColor.YELLOW}</li>
     *     <li>{@code BarColor.PURPLE}</li>
     *     <li>{@code BarColor.WHITE}</li>
     * </ul>
     * This parameter cannot be null.
     */
    void setColor(BarColor color);

    /**
     * Sets the style of the boss bar.
     *
     * <p>The style determines the visual appearance of the boss bar, such as whether
     * it is displayed as a solid bar or has segments. This method updates the current
     * style of the boss bar to the specified one.</p>
     *
     * @param style the {@link BarStyle} to apply to the boss bar
     *              <ul>
     *                  <li><b>BarStyle.SOLID</b> - Displays the bar as a continuous line.</li>
     *                  <li><b>BarStyle.SEGMENTED_X (e.g., SEGMENTED_6)</b> - Displays the bar divided into segments.</li>
     *              </ul>
     */
    void setStyle(BarStyle style);

    /**
     * Updates the boss bar with the specified title and progress value.
     *
     * <p>This method updates the text displayed on the boss bar as well as the progress indicator.
     * The progress value should be a double between 0.0 and 1.0, where 0.0 represents an empty bar
     * and 1.0 represents a fully filled bar.</p>
     *
     * @param title   The text to display on the boss bar. This is typically used to convey a message or status.
     * @param progress The progress value, a double ranging from 0.0 to 1.0, representing the fill level of the bar.
     *                 Values outside this range may result in unintended behavior.
     */
    void setTextAndProgress(String title, double progress);

    /**
     * Sets all the properties of the boss bar at once, including the title, progress, color, and style.
     *
     * <p>This method updates the following properties:</p>
     * <ul>
     *   <li>The title of the boss bar.</li>
     *   <li>The progress of the boss bar as a percentage, where <code>0.0</code> represents empty
     *       and <code>1.0</code> represents full.</li>
     *   <li>The color of the boss bar.</li>
     *   <li>The style or pattern of the boss bar.</li>
     * </ul>
     *
     * @param title   the text to display as the title of the boss bar; cannot be <code>null</code>
     * @param progress the progress value as a double; must be between <code>0.0</code> and <code>1.0</code> inclusive
     * @param color   the visual color of the boss bar; cannot be <code>null</code>
     * @param style   the pattern style of the boss bar; cannot be <code>null</code>
     */
    void setEverything(String title, double progress, BarColor color, BarStyle style);

    /**
     * Displays the boss bar to the associated player(s).
     *
     * <p>This method makes the boss bar visible to the players it is associated with,
     * allowing them to see the current state, including any text, progress, color,
     * or other settings applied to the bar.
     *
     * <p>Common use cases for this method include:
     * <ul>
     *   <li>Providing feedback or status updates to players during gameplay.</li>
     *   <li>Indicating progress of specific actions or events.</li>
     *   <li>Highlighting important gameplay objectives.</li>
     * </ul>
     *
     * <p>Ensure that the boss bar is appropriately set up (e.g., with text, color,
     * or progress) prior to calling this method to provide relevant information to
     * the players.
     */
    void show();

    /**
     * Hides the boss bar for the player.
     * <p>
     * This method is typically used to make the boss bar invisible to the player
     * while retaining its state. The boss bar can be reshown by invoking the
     * {@link #show()} method.
     * </p>
     * <p>
     * It does not remove the boss bar from memory; for permanent removal, use
     * the {@link #remove()} method instead.
     * </p>
     */
    void hide();

    /**
     * Removes the associated boss bar and clears all related data from the player.
     *
     * <p>This method is intended to clean up resources or reset the state associated
     * with the boss bar for a player. Once invoked, any previously shown boss bars
     * managed by this instance will be completely removed.</p>
     *
     * <p>Usage considerations:</p>
     * <ul>
     *   <li>Ensure this method is called to prevent lingering boss bars when they
     *       are no longer needed.</li>
     *   <li>This method is irreversible; re-adding a boss bar will require creating
     *       a new instance or reinitializing the necessary properties.</li>
     * </ul>
     */
    void remove();
}
