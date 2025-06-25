package network.palace.core.player.impl.managers;

import lombok.AllArgsConstructor;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerTitleManager;
import network.palace.core.player.PlayerStatus;
import org.bukkit.entity.Player;

/**
 * The {@code CorePlayerTitleManager} class is an implementation of the {@link CPlayerTitleManager}
 * interface responsible for managing and displaying titles and subtitles for a specific player.
 * It provides methods to show titles with various configuration options, such as custom text
 * and timing durations, as well as functionality to reset or clear titles.
 *
 * <p>This class ensures that titles are displayed only under appropriate conditions,
 * such as when the player is in the correct status and is available in the game context.
 * Interaction with the underlying {@code player} object allows for sending formatted title data.
 *
 * <p>Core functions of this class:
 * <ul>
 *   <li>Display a title with default timing parameters.</li>
 *   <li>Display a title and subtitle with default timing parameters.</li>
 *   <li>Display a title and subtitle with customizable timing parameters (fadeIn, stay, and fadeOut).</li>
 *   <li>Clear or hide the currently displayed title.</li>
 * </ul>
 *
 * <p>Each method validates whether the player is able to receive titles before performing any actions.
 */
@AllArgsConstructor
public class CorePlayerTitleManager implements CPlayerTitleManager {

    /**
     * Represents the player for whom titles and subtitles are managed within the game.
     *
     * <p>The {@code player} field is a reference to the {@link CPlayer} object associated with
     * an individual player in the game. It is utilized to perform various title-related operations,
     * such as sending formatted titles and subtitles or resetting currently displayed titles.
     *
     * <p>This player object acts as the core entity for interaction with the game mechanics,
     * including:
     * <ul>
     *   <li>Determining the player's status (e.g., joined, disconnected).</li>
     *   <li>Accessing the underlying Minecraft player instance to invoke visual features.</li>
     *   <li>Maintaining game-related context controlled by this manager.</li>
     * </ul>
     *
     * <p>In the {@link CorePlayerTitleManager}, this final field ensures that all title-related
     * actions are directed specifically toward the intended player.
     *
     * <p>Note: This field is immutable once assigned.
     */
    private final CPlayer player;

    /**
     * Displays a title to the player. This method uses a default subtitle value of an empty string.
     *
     * <p>The method ensures the player is eligible for receiving titles before attempting
     * to display the title. The title is typically shown with standard timing parameters
     * unless further overridden in other methods.
     *
     * @param title the text of the title to be displayed to the player. It should not be null.
     */
    @Override
    public void show(String title) {
        show(title, "");
    }

    /**
     * Displays a title and subtitle for the player using default timing parameters.
     * <p>
     * This method simplifies the process of showing a title and subtitle without
     * specifying additional timing configurations such as fade in, stay, and fade out durations.
     * It relies on default timing values defined in the implementation.
     * </p>
     *
     * @param title    The main title text to be displayed. Cannot be {@code null}.
     * @param subtitle The subtitle text to be displayed. Cannot be {@code null}.
     */
    @Override
    public void show(String title, String subtitle) {
        show(title, subtitle, 1, 7, 2);
    }

    /**
     * Displays a title and subtitle to the player with customizable fade-in, display duration, and fade-out times.
     *
     * <p>This method sends a title and a subtitle to the player using specified timing parameters.
     * The title will only be sent if the player's current status is {@code JOINED} and the
     * {@code BukkitPlayer} object is not {@code null}.
     * </p>
     *
     * @param title     the main title text to display to the player. This can include visual effects or
     *                  formatted strings, depending on the server's support for such features.
     * @param subtitle  the subtitle text to display beneath the main title. It is usually used to
     *                  provide additional context or details related to the title.
     * @param fadeIn    the time in ticks for the title to fade in. For example, a value of {@code 20}
     *                  represents 1 second of fade-in time (based on a typical tick rate of 20 ticks per second).
     * @param stay      the time in ticks for the title to remain fully displayed before starting to fade out.
     *                  For example, {@code 140} represents 7 seconds of display time.
     * @param fadeOut   the time in ticks for the title to fade out. Similar to {@code fadeIn}, a value of
     *                  {@code 20} represents 1 second.
     */
    @Override
    public void show(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player.getStatus() != PlayerStatus.JOINED) return;
        if (player.getBukkitPlayer() == null) return;
        player.getBukkitPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    /**
     * Hides the currently displayed title for the associated player.
     *
     * <p>This method resets the title for the player, removing any currently displayed title
     * or subtitle. Before performing this action, it validates the following conditions:</p>
     * <ul>
     *   <li>The player's status must be {@code JOINED}.</li>
     *   <li>The player must have a valid {@link Player} instance returned by {@code getBukkitPlayer()}.</li>
     * </ul>
     *
     * <p>If either of these conditions are not met, the method exits without performing any action.</p>
     *
     * <p>This functionality is typically used to clear or hide any visual title elements
     * displayed to the player in the context of a Minecraft server environment.</p>
     */
    @Override
    public void hide() {
        if (player.getStatus() != PlayerStatus.JOINED) return;
        if (player.getBukkitPlayer() == null) return;
        player.getBukkitPlayer().resetTitle();
    }
}
