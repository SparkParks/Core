package network.palace.core.player.impl.managers;

import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerBossBarManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

/**
 * The {@code CorePlayerBossBarManager} class provides an implementation of the {@link CPlayerBossBarManager} interface to
 * manage and display a boss bar for a player in a Minecraft environment.
 *
 * <p>The boss bar can have its text, progress, color, and style dynamically updated. It also allows for showing,
 * hiding, and fully removing the boss bar as needed. This class ensures the boss bar is properly created, initialized,
 * and associated with the given player.</p>
 *
 * <p>The {@code CorePlayerBossBarManager} is dependent on:</p>
 * <ul>
 *     <li>{@link CPlayer} - the player to associate with the boss bar.</li>
 *     <li>BossBar - the Spigot API class used to represent the boss bar in the game.</li>
 *     <li>{@link BarColor} - the color of the boss bar.</li>
 *     <li>{@link BarStyle} - the style applied to the boss bar.</li>
 * </ul>
 *
 * <p>Key responsibilities of this class include:</p>
 * <ul>
 *     <li>Creating and associating a boss bar with the {@link CPlayer} if it does not already exist.</li>
 *     <li>Dynamically updating the boss bar's title, progress, color, and style.</li>
 *     <li>Providing methods to show, hide, and remove the boss bar as needed.</li>
 * </ul>
 *
 * <p>Creation of a boss bar occurs lazily - the boss bar is only created when it is first required by one of the
 * methods that modify or interact with it.</p>
 *
 * <p>The following actions are provided:</p>
 * <ul>
 *     <li>{@code setText} - Updates the text shown in the boss bar.</li>
 *     <li>{@code setProgress} - Sets the progress percentage of the boss bar (0.0 to 1.0 range).</li>
 *     <li>{@code setColor} - Changes the color of the boss bar.</li>
 *     <li>{@code setStyle} - Updates the style of the boss bar.</li>
 *     <li>{@code setTextAndProgress} - Updates both the text and progress simultaneously.</li>
 *     <li>{@code setEverything} - Updates text, progress, color, and style simultaneously.</li>
 *     <li>{@code show} - Makes the boss bar visible to the player.</li>
 *     <li>{@code hide} - Hides the boss bar from the player.</li>
 *     <li>{@code remove} - Completely removes the boss bar and cleans up resources.</li>
 * </ul>
 *
 * <p>Note that invalid progress values (less than 0.0 or greater than 1.0) will be clamped to valid bounds. If no
 * boss bar exists when a method requiring it is invoked, one will be automatically created and associated with the
 * {@link CPlayer}.</p>
 *
 * <p>Internal validation ensures that modifications or display changes to the boss bar occur only if the object exists,
 * preventing unintended behavior in cases where the boss bar has not yet been initialized or has been removed.</p>
 */
public class CorePlayerBossBarManager implements CPlayerBossBarManager {

    /**
     * Represents the player associated with this CorePlayerBossBarManager instance.
     * <p>
     * This variable holds a reference to the {@code CPlayer} object, which is used to
     * manage and manipulate the boss bar for the specified player. It is a final field
     * that ensures the player reference remains constant throughout the lifecycle of the
     * {@code CorePlayerBossBarManager} instance.
     * </p>
     *
     * <p>
     * Primary use cases include:
     * <lu>
     * <li>Associating the player's boss bar with this manager to update or modify its text, progress, color, and style.</li>
     * <li>Facilitating operations such as showing, hiding, or removing the boss bar associated with the player.</li>
     * </lu>
     * </p>
     */
    private final CPlayer player;

    /**
     * The `bossBar` variable represents an instance of the {@link BossBar} object associated
     * with a specific player. This variable is used to display customizable, interactive
     * boss bars in the Minecraft client, providing visual feedback such as progress, text,
     * and other styles to players during gameplay.
     *
     * <p><b>Usage Details:</b></p>
     * <ul>
     *   <li>The `bossBar` is initialized as null and is lazily created when the respective
     *   player requires a boss bar using utility methods.</li>
     *   <li>It can be modified through various management methods (e.g., setting text,
     *   progress, color, and style).</li>
     *   <li>The `bossBar` can be shown, hidden, updated, or removed dynamically
     *   during gameplay, adapting to player or server events.</li>
     * </ul>
     *
     * <p>This instance is managed internally within the {@link CorePlayerBossBarManager},
     * ensuring proper creation, customization, and disposal when required.</p>
     */
    private BossBar bossBar = null;

    /**
     * Constructs a new {@code CorePlayerBossBarManager} instance for a specific player.
     * <p>
     * This manager is responsible for handling the boss bar functionality for the given {@code CPlayer}.
     *
     * @param player the {@code CPlayer} instance for which the boss bar manager is created.
     *               This parameter cannot be {@code null} and represents the player associated
     *               with this boss bar manager.
     */
    public CorePlayerBossBarManager(CPlayer player) {
        this.player = player;
    }

    /**
     * Sets the text (title) of the boss bar.
     * <p>
     * The method ensures that the boss bar is created if it does not already exist,
     * updates the title of the boss bar, and makes the boss bar visible to the player.
     * </p>
     *
     * @param title the text to set as the title of the boss bar
     */
    @Override
    public void setText(String title) {
        BossBar bossBar = createIfDoesNotExist();
        if (bossBar == null) return;
        bossBar.setTitle(title);
        show();
    }

    /**
     * Updates the progress of the boss bar and ensures it is displayed. The progress value is clamped between 0.0 and 1.0.
     * If the boss bar does not exist, it will be created and initialized before setting the progress.
     *
     * <p>The method performs the following steps:</p>
     * <ul>
     * <li>Creates the boss bar if it does not already exist.</li>
     * <li>Clamps the provided progress value between 0.0 and 1.0.</li>
     * <li>Sets the clamped progress value to the boss bar.</li>
     * <li>Ensures the boss bar is visible.</li>
     * </ul>
     *
     * @param progress The progress value to set on the boss bar, ranging from 0.0 (empty) to 1.0 (full).
     */
    @Override
    public void setProgress(double progress) {
        BossBar bossBar = createIfDoesNotExist();
        if (bossBar == null) return;
        if (progress < 0.0) progress = 0.0;
        if (progress > 1.0) progress = 1.0;
        bossBar.setProgress(progress);
        show();
    }

    /**
     * Updates the color of the boss bar associated with the player.
     * <p>
     * If the boss bar does not already exist, it will be created. The updated
     * color will then be applied to the boss bar.
     * <p>
     * The method also ensures that the boss bar is displayed after the color
     * is set.
     *
     * @param color the {@link BarColor} to set for the boss bar. It defines
     *              the visual color of the boss bar.
     */
    @Override
    public void setColor(BarColor color) {
        BossBar bossBar = createIfDoesNotExist();
        if (bossBar == null) return;
        bossBar.setColor(color);
        show();
    }

    /**
     * Sets the style of the boss bar displayed to the player.
     * <p>
     * This method modifies the style of the boss bar by using the provided {@code style}.
     * If the boss bar does not already exist, it will be created. The new style will then
     * be applied to the boss bar, and the boss bar will be displayed to the player.
     *
     * @param style the {@link BarStyle} to set for the boss bar, defining the visual
     *              appearance and behavior of the bar (e.g., solid, segmented).
     */
    @Override
    public void setStyle(BarStyle style) {
        BossBar bossBar = createIfDoesNotExist();
        if (bossBar == null) return;
        bossBar.setStyle(style);
        show();
    }

    /**
     * Sets both the text (title) and progress of the boss bar, ensuring it is visible to the player.
     * <p>
     * This method updates the title of the boss bar using the provided text and the progress value.
     * It ensures that the boss bar is created if it does not already exist. The progress value is
     * clamped between 0.0 (empty) and 1.0 (full). After setting the text and progress, the boss bar
     * is displayed to the player.
     * </p>
     *
     * <p>The following steps are performed:</p>
     * <ul>
     * <li>Sets the text of the boss bar using the provided {@code text}.</li>
     * <li>Updates the progress of the boss bar using the provided {@code progress} value.</li>
     * <li>Ensures the boss bar is displayed to the player.</li>
     * </ul>
     *
     * @param text the text to set as the title of the boss bar.
     *             This is displayed to the player as the bar's label.
     * @param progress the progress value to set on the boss bar, ranging from 0.0 (empty) to 1.0 (full).
     */
    @Override
    public void setTextAndProgress(String text, double progress) {
        setText(text);
        setProgress(progress);
        show();
    }

    /**
     * Sets all properties of the player's boss bar, including its text, progress, color, and style, and ensures it is displayed.
     * <p>
     * This method performs the following actions:
     * <ul>
     * <li>Sets the text and progress of the boss bar.</li>
     * <li>Applies the specified color to the boss bar.</li>
     * <li>Applies the specified style to the boss bar.</li>
     * <li>Ensures that the boss bar is displayed to the player.</li>
     * </ul>
     *
     * @param text     the text to display as the title of the boss bar.
     * @param progress the progress value of the boss bar, where 0.0 represents empty and 1.0 represents full.
     * @param color    the {@link BarColor} defining the color of the boss bar.
     * @param style    the {@link BarStyle} defining the visual style of the boss bar, such as solid or segmented lines.
     */
    @Override
    public void setEverything(String text, double progress, BarColor color, BarStyle style) {
        setTextAndProgress(text, progress);
        setColor(color);
        setStyle(style);
        show();
    }

    /**
     * Displays the boss bar to the player.
     *
     * <p>This method ensures the boss bar is visible to the player. If the boss bar
     * does not already exist, it will be created using the {@code createIfDoesNotExist} method.</p>
     *
     * <p>Behavior:</p>
     * <ul>
     *   <li>If the boss bar is not yet created, it will call {@code createIfDoesNotExist()} to create it.</li>
     *   <li>If the boss bar creation fails (returns {@code null}), the method will exit early.</li>
     *   <li>Sets the visibility of the boss bar to {@code true}, making it display to the player.</li>
     * </ul>
     */
    @Override
    public void show() {
        BossBar bossBar = createIfDoesNotExist();
        if (bossBar == null) return;
        bossBar.setVisible(true);
    }

    /**
     * Hides the boss bar from the player.
     * <p>
     * This method ensures that if a boss bar is currently associated with the player,
     * it will be set to invisible. If no boss bar is present, the method does nothing.
     * </p>
     *
     * <p>Behavior:</p>
     * <ul>
     *   <li>Checks if the boss bar exists.</li>
     *   <li>If the boss bar is present, it is set to not visible.</li>
     *   <li>If the boss bar does not exist, the method exits without further action.</li>
     * </ul>
     */
    @Override
    public void hide() {
        if (bossBar == null) return;
        bossBar.setVisible(false);
    }

    /**
     * Removes the player-specific boss bar from the game UI.
     * <p>
     * This method ensures that any existing boss bar associated with the player
     * is safely removed. It performs the following actions:
     * </p>
     * <ul>
     *   <li>Immediately returns if the boss bar is {@code null}, indicating
     *       that no boss bar currently exists for the player.</li>
     *   <li>Calls the {@link #hide()} method to make the boss bar invisible
     *       before removal.</li>
     *   <li>Clears all references and settings associated with the boss bar
     *       by invoking the {@code removeAll()} method on the boss bar instance.</li>
     *   <li>Sets the {@code bossBar} field to {@code null} to signify that no boss bar
     *       is currently active for the player.</li>
     * </ul>
     * <p>
     * This method is typically used when cleaning up resources or when
     * the boss bar is no longer needed for display purposes.
     * </p>
     */
    @Override
    public void remove() {
        if (bossBar == null) return;
        hide();
        bossBar.removeAll();
        this.bossBar = null;
    }

    /**
     * Creates and initializes a {@link BossBar} instance if it does not already exist.
     *
     * <p>The method checks whether the {@code bossBar} field is null, and if it is, creates a new
     * {@link BossBar} with the title "Boss Bar", a {@link BarColor} of {@code PINK}, and a
     * {@link BarStyle} of {@code SOLID}. The boss bar is then linked to the player associated with this
     * manager, set to full progress, and made visible to the player.</p>
     *
     * <ul>
     * <li>If the {@code bossBar} field is non-null, the existing instance is returned.</li>
     * <li>Otherwise, a new {@link BossBar} is created, initialized, and returned.</li>
     * </ul>
     *
     * @return the existing {@link BossBar} instance if already initialized, or a newly created and
     *         configured {@link BossBar} instance if it did not exist previously.
     */
    private BossBar createIfDoesNotExist() {
        if (bossBar != null) {
            return bossBar;
        }
        BossBar bossBar = Bukkit.createBossBar("Boss Bar", BarColor.PINK, BarStyle.SOLID);
        bossBar.addPlayer(player.getBukkitPlayer());
        this.bossBar = bossBar;
        setProgress(1.0);
        show();
        return bossBar;
    }
}
