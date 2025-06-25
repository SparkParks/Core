package network.palace.core.player;

/**
 * The {@code CPlayerScoreboardManager} interface provides a contract for managing
 * scoreboard-related functionalities for players, such as displaying and organizing
 * information in a sidebar, setting titles, managing tags, and controlling visibility.
 * <p>
 * This interface is designed to allow flexible handling of scoreboard entries
 * and player tag management.
 */
public interface CPlayerScoreboardManager {

    /**
     * Updates or sets the text of a specific scoreboard entry by its identifier.
     * <p>
     * This method allows modifying the text displayed for a specific entry in the
     * player scoreboard, identified by the given ID. The text can be customized
     * to provide relevant information or updates dynamically.
     *
     * @param id   the unique identifier of the scoreboard entry to modify
     * @param text the text to display for the specified scoreboard entry; it can include
     *             colors or special formats based on the context
     * @return the {@code CPlayerScoreboardManager} instance, enabling method chaining for
     *         further configuration or modifications
     */
    CPlayerScoreboardManager set(int id, String text);

    /**
     * Sets a blank entry in the scoreboard at a specified position.
     * <p>
     * This method updates the scoreboard by creating a blank line at the provided position.
     * It can be used to create visual spacing between entries in the scoreboard.
     *
     * @param id the position index where the blank entry should be placed. Typically,
     *           positions are sequential and define the order of scoreboard entries.
     * @return the {@code CPlayerScoreboardManager} instance, allowing for method chaining.
     */
    CPlayerScoreboardManager setBlank(int id);

    /**
     * Removes the scoreboard entry associated with the specified {@code id}.
     * <p>
     * This method is used to delete a specific entry from the scoreboard, typically
     * identified by its numerical position or unique identifier.
     *
     * @param id the identifier of the scoreboard entry to be removed
     *           <ul>
     *              <li>Must be a valid identifier that corresponds to an existing entry.</li>
     *              <li>Entries with the given {@code id} will no longer be visible or accessible.</li>
     *           </ul>
     *
     * @return the current {@code CPlayerScoreboardManager} instance
     *         <p>
     *         Allows for method chaining by maintaining the fluent interface design.
     */
    CPlayerScoreboardManager remove(int id);

    /**
     * Sets the title for the player's scoreboard.
     * <p>
     * This method allows you to define the title that will be displayed at the
     * top of the scoreboard for the associated player.
     *
     * @param title the title to be set on the player's scoreboard.
     *              It must be a non-null {@link String}, and the intended
     *              display text for the scoreboard's title.
     * @return {@code CPlayerScoreboardManager} instance for method chaining.
     */
    CPlayerScoreboardManager title(String title);

    /**
     * Initializes and configures player tags for the scoreboard system.
     * <p>
     * This method prepares and assigns the necessary tags or identifiers
     * for players to be used in the scoreboard system, ensuring proper
     * categorization and visibility. It may include operations like
     * associating ranks, setting visual identifiers, or updating metadata
     * for the player within the scoreboard context.
     * <p>
     * Key functionalities include:
     * <ul>
     *   <li>Ensuring that all required tags for players are set up correctly.</li>
     *   <li>Preparing scoreboard-related metadata for interaction with player data.</li>
     *   <li>Establishing initial player tag visibility settings.</li>
     * </ul>
     * <p>
     * Should be called before any interaction with player-related tags
     * to ensure their proper configuration in the system.
     */
    void setupPlayerTags();

    /**
     * Adds a tag to the specified player for identification, categorization, or
     * any other scoreboard-related purpose. Tags might be used to classify players,
     * display additional information, or improve gameplay mechanics.
     *
     * @param otherPlayer  The {@code CPlayer} instance representing the player
     *                     to whom the tag should be added. This player will receive
     *                     the associated tag and its relevant functionalities.
     *
     * <p>Note:</p>
     * <ul>
     *  <li>This method should be called after ensuring the player is valid and
     *      the tagging system is properly initialized.</li>
     *  <li>Repeated calls with the same {@code otherPlayer} may replace or merge
     *      existing tags based on the underlying implementation.</li>
     * </ul>
     */
    void addPlayerTag(CPlayer otherPlayer);

    /**
     * Removes a specific tag from another player's scoreboard or tag management system.
     * <p>
     * This method is responsible for detaching or removing any tag or marker associated
     * with the provided {@code CPlayer} instance. It supports use cases where dynamic
     * player tag management is necessary, such as team configurations, game modes, or
     * visibility handling within the scoreboard.
     *
     * @param otherPlayer The {@code CPlayer} instance from which the tag should be removed.
     *                     This parameter must be a valid, non-null player object currently
     *                     recognized by the scoreboard manager.
     */
    void removePlayerTag(CPlayer otherPlayer);

    /**
     * Checks whether the setup process has been completed.
     * <p>
     * This method determines if the necessary configurations or initializations
     * for the player scoreboard manager have been performed.
     * It can be used to ensure that the instance is ready to handle
     * further operations, such as managing scoreboard entries or player tags.
     *
     * @return {@code true} if the setup has been completed successfully;
     *         {@code false} otherwise.
     */
    boolean isSetup();

    /**
     * Clears all the entries and data associated with the player's scoreboard in this manager.
     * <p>
     * This method removes existing entries, resets title, and ensures that the scoreboard
     * is blank and ready for new data or updates.
     *
     * <ul>
     *     <li>It can be used to reset the scoreboard to its default state.</li>
     *     <li>Best used when a fresh start for the player's scoreboard is required.</li>
     * </ul>
     */
    void clear();

    /**
     * Toggles the visibility of player tags in the scoreboard.
     * <p>
     * This method switches the current visibility state of player tags. If
     * player tags are currently visible, they will be hidden, and if they are
     * hidden, they will be made visible.
     * <p>
     * This can be useful for dynamically changing the display of tags
     * without directly specifying their visibility.
     *
     * <p>
     * Example scenarios where this method might be used:
     * <ul>
     *     <li>Temporarily hiding tags during specific events or activities.</li>
     *     <li>Re-enabling tags after they have been hidden.</li>
     * </ul>
     */
    void toggleTags();

    /**
     * Toggles the visibility of player tags based on the provided parameter.
     * <p>
     * This method is used for managing the visibility state of player tags,
     * which are typically used for displaying relevant information next to players in-game.
     * By calling this method with a {@code true} or {@code false} argument,
     * the tags' visibility can be explicitly set on or off.
     *
     * @param hidden a boolean value specifying whether the tags should be hidden:
     *        <ul>
     *          <li>{@code true} to hide the tags</li>
     *          <li>{@code false} to make the tags visible</li>
     *        </ul>
     */
    void toggleTags(boolean hidden);

    /**
     * Retrieves the visibility status of player tags.
     * <p>
     * This method determines whether player tags are currently visible or hidden
     * in the scoreboard or related interfaces.
     *
     * @return {@code true} if player tags are visible; {@code false} otherwise.
     */
    boolean getTagsVisible();
}
