package network.palace.core.player.impl.managers;

import lombok.Getter;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerScoreboardManager;
import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code CorePlayerScoreboardManager} class manages the player scoreboards,
 * allowing for efficient customization and control of sidebar text, titles,
 * and player tags. This implementation supports operations such as
 * dynamically setting scoreboard lines, titles, managing player tags,
 * and toggling the visibility of name tags.
 * <p>
 * This class relies on the Bukkit API's {@code Scoreboard} and {@code Team}
 * interfaces to manage scoreboard functionalities. It primarily operates on
 * a specific player's scoreboard and allows modifications to appear live
 * during the game.
 * </p>
 * <p>
 * Key Features:
 * <ul>
 *   <li>Manage sidebar scoreboard titles and content.</li>
 *   <li>Handle player tag functionality with rank and additional tags.</li>
 *   <li>Support toggling name tag visibility.</li>
 *   <li>Prevent scoreboard collisions between players by implementing unique teams and objectives.</li>
 * </ul>
 */
public class CorePlayerScoreboardManager implements CPlayerScoreboardManager {

    /**
     * <p>The maximum allowable length for a string to be displayed on the player's sidebar scoreboard.</p>
     *
     * <p>This constant is used to enforce a limit on the length of text that can be displayed
     * in various scoreboard elements, such as titles or entries. Ensuring that string values
     * do not exceed this limit helps maintain visual consistency and prevent truncation or rendering issues.</p>
     *
     * <ul>
     *     <li>Type: {@code int} (integer)</li>
     *     <li>Value: {@code 64}</li>
     *     <li>Accessibility: {@code private static final}</li>
     * </ul>
     *
     * <p>This variable is used internally in the class to validate or truncate strings where necessary.</p>
     */
    private static final int MAX_STRING_LENGTH = 64;

    /**
     * <p>Represents the {@link CPlayer} instance associated with this
     * {@code CorePlayerScoreboardManager}.</p>
     *
     * <ul>
     *   <li>The {@code player} variable is a reference to the player whose
     *   scoreboard-related operations are managed by this class.</li>
     *   <li>This object is immutable and cannot be reassigned after it is initialized.</li>
     * </ul>
     *
     * <p>It acts as the core player context for managing scoreboard customization,
     * including sidebar updates, title settings, tag visibility,
     * and other related player-specific scoreboard tasks.</p>
     */
    private final CPlayer player;

    /**
     * Holds a mapping of sidebar IDs to their respective string values in the player's scoreboard.
     * <p>
     * <ul>
     *   <li>The key (<code>Integer</code>) represents the unique ID for the line position on the scoreboard sidebar.</li>
     *   <li>The value (<code>String</code>) represents the text content to be displayed on the respective line.</li>
     * </ul>
     * <p>
     * Used internally to manage and update sidebar contents efficiently.
     */
    private Map<Integer, String> lines = new HashMap<>();

    /**
     * Represents the private instance of the {@link Scoreboard} used to manage
     * and display custom scoreboard functionalities for the player.
     *
     * <p>This field is responsible for managing the player's scoreboard data, including:
     * <ul>
     *   <li>Displaying sidebar content (e.g., scores and text).</li>
     *   <li>Maintaining the player's tags and team display.</li>
     *   <li>Tracking and updating dynamic information on the scoreboard.</li>
     *   <li>Enabling compatibility with custom implementations specific to player interactions.</li>
     * </ul>
     *
     * <p>It is utilized and modified through the methods provided in the
     * {@link CorePlayerScoreboardManager}, including those for setting values,
     * creating blank entries, handling player tags, and clearing the scoreboard.
     *
     * <p>The {@code setup} methods ensure the proper initialization and configuration
     * of this scoreboard for the player.
     */
    private Scoreboard scoreboard;

    /**
     * Represents the underlying {@link Objective} used for managing
     * and displaying the player's scoreboard.
     *
     * <p>This variable is primarily responsible for handling the display logic and
     * organization of sidebar elements, including titles, lines, and other related
     * components managed within the player's scoreboard.</p>
     *
     * <p>Key responsibilities include:</p>
     * <ul>
     *   <li>Tracking and updating sidebar scoreboard objectives.</li>
     *   <li>Facilitating changes to text, titles, and other display settings.</li>
     *   <li>Enabling interaction with the scoreboard API for displaying game data.</li>
     * </ul>
     *
     * <p>The {@code scoreboardObjective} is initialized and manipulated internally by
     * the {@code CorePlayerScoreboardManager} class to provide players with
     * up-to-date and visually organized scoreboard information.</p>
     */
    private Objective scoreboardObjective;

    /**
     * Represents the title of the sidebar scoreboard for a player.
     *
     * <p>This variable stores the current title that is displayed on the player's
     * scoreboard. It is used to set or retrieve the visual header text of the
     * scoreboard. The title can be updated dynamically based on the context
     * of the game or application.</p>
     *
     * <p>Its value is initialized as an empty string, indicating no title is
     * set by default. Changes to this variable will reflect on the player's
     * scoreboard once applied through the corresponding methods in the
     * manager class.</p>
     */
    private String title = "";

    /**
     * Indicates whether the scoreboard setup has been completed for the player.
     *
     * <p>This variable is used to track whether the necessary initialization
     * or configuration steps for the player's scoreboard have been performed.</p>
     *
     * <ul>
     *   <li>If <code>true</code>, the scoreboard setup is finalized and ready for use.</li>
     *   <li>If <code>false</code>, the scoreboard setup has not been completed, and additional
     *       steps may be required to initialize it.</li>
     * </ul>
     *
     * <p>Typically modified during the <code>setup</code> process or other configuration
     * methods within the class.</p>
     */
    @Getter private boolean isSetup = false;

    /**
     * <p>Indicates whether tags, such as player tags, are currently visible on the scoreboard.</p>
     *
     * <p>This boolean flag controls the visibility of tags. When set to <code>true</code>,
     * the tags are displayed for players; otherwise, they are hidden if set to <code>false</code>.</p>
     *
     * <p>The state of this field can be toggled via the corresponding methods in the class,
     * and it plays a role in managing the player's scoreboard presentation.</p>
     */
    private boolean tagsVisible = true;

    /**
     * Constructs a new instance of {@code CorePlayerScoreboardManager} to manage
     * the scoreboard and related functionalities for a specified player.
     *
     * <p>This class assists in setting up and managing the scoreboard for the
     * provided {@link CPlayer}, including lines, titles, and associated player tags.
     *
     * @param player The {@link CPlayer} instance for which the scoreboard is
     *               being managed. This player will be associated with the
     *               scoreboard configurations handled by this manager.
     */
    public CorePlayerScoreboardManager(CPlayer player) {
        this.player = player;
    }

    /**
     * Updates or adds a line on the player's scoreboard at the specified position (id).
     * Trims the text to a maximum length and ensures the text remains valid.
     * Resolves conflicts by removing duplicate texts and adjusts the associated score.
     *
     * @param id the line position on the scoreboard to update
     * @param text the text content to display at the specified position
     * @return the current {@code CPlayerScoreboardManager} instance for method chaining
     */
    @Override
    public CPlayerScoreboardManager set(int id, String text) {
        text = text.substring(0, Math.min(text.length(), MAX_STRING_LENGTH));
        while (text.endsWith("\u00A7")) text = text.substring(0, text.length() - 1);
        if (lines.containsKey(id)) {
            if (lines.get(id).equals(text) || (ChatColor.stripColor(lines.get(id)).trim().equals("") && ChatColor.stripColor(text).trim().equals(""))) {
                return this;
            } else {
                remove(id);
            }
        }
        lines.values().removeIf(text::equals);
        lines.put(id, text);
        if (scoreboardObjective == null) return this;
        if (scoreboardObjective.getScore(text) == null) return this;
        scoreboardObjective.getScore(text).setScore(id);
        return this;
    }

    /**
     * Sets a blank entry in the player's scoreboard at the specified ID.
     * <p>
     * This method will replace the existing entry at the given ID with a blank
     * line, ensuring that the scoreboard maintains its intended structure. The
     * number of blanks is determined based on the ID provided.
     *
     * @param id the ID of the scoreboard line to be set as blank
     *           <ul>
     *             <li>Must be a valid ID within the scoreboard range.</li>
     *           </ul>
     * @return the updated {@code CPlayerScoreboardManager} instance
     *         to allow for method chaining
     */
    @Override
    public CPlayerScoreboardManager setBlank(int id) {
        return set(id, getBlanks(id));
    }

    /**
     * Removes the entry associated with the given {@code id} from the scoreboard and its internal data structure.
     * <p>
     * If the entry exists for the specified {@code id}, it will also reset its score on the scoreboard.
     * After removal, the state is returned for method chaining.
     *
     * @param id the identifier of the entry to be removed
     *            <ul>
     *              <li>Must correspond to an existing entry in the scoreboard.</li>
     *              <li>If the {@code id} does not exist, no processing will occur.</li>
     *            </ul>
     *
     * @return the current instance of {@code CPlayerScoreboardManager} for chaining further modifications
     */
    @Override
    public CPlayerScoreboardManager remove(int id) {
        if (lines.containsKey(id)) {
            scoreboard.resetScores(lines.get(id));
        }
        lines.remove(id);
        return this;
    }

    /**
     * Updates the title on the player's scoreboard. This adjusts the display name of the scoreboard objective
     * and applies the updated scoreboard to the associated player.
     *
     * <p>
     * If the new title is equal to the current title, no action will be performed. If the title or scoreboard
     * configuration is invalid, the method will safely return without updating.
     * </p>
     *
     * @param title The title to set for the scoreboard. It must be a non-null {@link String}.
     * @return The current instance of {@code CPlayerScoreboardManager}, allowing for method chaining.
     */
    @Override
    public CPlayerScoreboardManager title(String title) {
        if (this.title != null && this.title.equals(title)) return this;
        if (scoreboard == null) setup();
        if (title == null) return this;

        this.title = title;
        if (scoreboardObjective == null) setup();
        try {
            if (scoreboardObjective != null) scoreboardObjective.setDisplayName(title);
        } catch (IllegalStateException | IllegalArgumentException ignored) {
        }
        if (scoreboard != null && player != null && player.getBukkitPlayer() != null)
            player.getBukkitPlayer().setScoreboard(scoreboard);
        return this;
    }

    /**
     * Initializes the scoreboard for the player and sets up necessary configurations.
     *
     * <p>This method creates a new {@link Scoreboard} associated with the player, assigns
     * it to their Bukkit {@link Player} instance, and configures a sidebar {@link Objective}
     * for display. If the scoreboard manager or player instance is unavailable, the method
     * exits early without proceeding further. Upon successful setup, the `isSetup` flag is
     * marked as {@code true}.
     *
     * <p>Key operations performed by this method include:
     * <ul>
     *   <li>Creating and storing an empty mapping for scoreboard lines.</li>
     *   <li>Obtaining a new scoreboard instance from the {@link Bukkit} scoreboard manager.</li>
     *   <li>Associating the new scoreboard with the player's {@link Player} instance.</li>
     *   <li>Registering a new {@link Objective} for the scoreboard, displayed in the sidebar slot.</li>
     * </ul>
     *
     * <p>Note that this setup process must be called to enable scoreboard-related features
     * to function correctly for the player.
     */
    private void setup() {
        lines = new HashMap<>();
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        if (scoreboard == null) return;
        if (player.getBukkitPlayer() == null) return;
        player.getBukkitPlayer().setScoreboard(scoreboard);
        scoreboardObjective = scoreboard.registerNewObjective(player.getName(), "dummy");
        scoreboardObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        isSetup = true;
    }

    /**
     * Generates a string consisting of blank spaces to be used in the scoreboard.
     * <p>
     * The resulting string is made up of {@link ChatColor#WHITE} placeholders repeated
     * based on the specified ID. This can be used to ensure proper alignment or
     * spacing in the scoreboard display.
     *
     * @param id the number of blanks to generate
     *           <ul>
     *             <li>Must be a non-negative integer.</li>
     *             <li>The number of blanks generated will be {@code id + 1}.</li>
     *           </ul>
     *
     * @return a {@code String} consisting of {@code id + 1} blank placeholders.
     */
    private String getBlanks(int id) {
        String[] blank = new String[id + 1];
        Arrays.fill(blank, ChatColor.WHITE.toString());
        return String.join("", blank);
    }

    /**
     * Initializes and sets up player tags for the associated scoreboard, ensuring that
     * teams for each {@link Rank} are properly configured.
     *
     * <p>If the scoreboard is not already initialized, this method will invoke the
     * {@code setup()} method to create and configure the scoreboard.</p>
     *
     * <p>For each rank in {@link Rank}, this method:</p>
     * <ul>
     *   <li>Checks if a team already exists for the rank based on its prefix and short name.</li>
     *   <li>If no team exists, it registers a new team with a unique name derived from the
     *       rank's prefix and short name.</li>
     *   <li>Configures the team with the following properties:
     *     <ul>
     *       <li>Sets the team's prefix to the formatted name of the rank followed by a space.</li>
     *       <li>Sets the team's color to the {@link net.minecraft.chat.ChatColor} of the rank.</li>
     *       <li>Disables collision for all players in the team by setting the collision rule
     *           to {@code NEVER}.</li>
     *       <li>Sets an empty suffix for the team.</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <p>This method ensures that all ranks have corresponding teams on the scoreboard with the
     * proper configuration necessary for rank-specific prefixes, colors, and collision handling.</p>
     *
     * <p>If modifications are made to the ranks or their properties, this method should be
     * re-invoked to reflect the changes on the scoreboard.</p>
     */
    @Override
    public void setupPlayerTags() {
        if (scoreboard == null) setup();
        for (Rank rank : Rank.values()) {
            if (scoreboard.getTeam(rank.getScoreboardPrefix() + rank.getShortName()) != null) continue;
            Team team = scoreboard.registerNewTeam(rank.getScoreboardPrefix() + rank.getShortName());
            team.setPrefix(rank.getFormattedName() + " ");
            team.setColor(rank.getTagColor());
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setSuffix("");
        }
    }

    /**
     * Adds a tag to the specified player's scoreboard, ensuring its proper placement
     * and formatting based on the player's rank and associated tags.
     *
     * <p>This method retrieves or creates a scoreboard team for the given player based
     * on their rank and tags. If the team does not already exist, it initializes a new
     * team with the appropriate prefix, suffix, and display options, then assigns the
     * player to that team. If the player is already assigned to the correct team, no
     * further action is performed.
     *
     * <p>Key functionalities include:
     * <ul>
     *   <li>Setting up the scoreboard if it is not already initialized.</li>
     *   <li>Determining the team name dynamically from the player's rank and tags.</li>
     *   <li>Formatting the team's prefix, suffix, and collision rules for the scoreboard display.</li>
     * </ul>
     *
     * <p>The method performs safely by handling null cases for the player, rank, or tags.
     *
     * @param otherPlayer the {@link CPlayer} whose tag is to be added to the scoreboard
     *                    <ul>
     *                      <li>Must not be null. If null, the method returns without action.</li>
     *                      <li>The player's rank and tags must also be non-null to proceed with
     *                      adding their tag to the scoreboard.</li>
     *                    </ul>
     */
    @Override
    public void addPlayerTag(CPlayer otherPlayer) {
        if (scoreboard == null) setup();
        if (otherPlayer == null || otherPlayer.getRank() == null || otherPlayer.getTags() == null) return;
        String teamName = teamName(otherPlayer.getRank(), otherPlayer.getTags());
        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.setPrefix(otherPlayer.getRank().getFormattedName() + " ");
            team.setSuffix(RankTag.formatScoreboardSuffix(otherPlayer.getTags()));
            team.setColor(otherPlayer.getRank().getTagColor());
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.addEntry(otherPlayer.getName());
        } else if (!team.hasEntry(otherPlayer.getName())) {
            team.addEntry(otherPlayer.getName());
        }
    }

    /**
     * Removes a player's tag from the scoreboard team associated with their rank and tags.
     * <p>
     * This method ensures that the player is properly removed from the team if they have an associated
     * rank and team on the scoreboard. If the {@code scoreboard} instance is not initialized, it is
     * set up before proceeding with the removal process.
     * <p>
     * The removal process involves:
     * <ul>
     *   <li>Checking if the {@code otherPlayer} or their rank is null to avoid invalid operations.</li>
     *   <li>Determining the appropriate team name based on the player's rank and tags.</li>
     *   <li>Removing the player's name from the corresponding team on the scoreboard,
     *   if such a team exists.</li>
     * </ul>
     *
     * @param tag {@code otherPlayer} the player whose tag is to be removed from the scoreboard team.
     *            The player must have a rank and tags associated with them to perform this operation.
     */
    @Override
    public void removePlayerTag(CPlayer otherPlayer) {
        if (scoreboard == null) setup();
        if (otherPlayer == null || otherPlayer.getRank() == null) return;
        String teamName = teamName(otherPlayer.getRank(), otherPlayer.getTags());
        Team team = scoreboard.getTeam(teamName);

        if (team == null) return;
        team.removeEntry(otherPlayer.getName());
    }

    /**
     * Constructs a unique team name based on the provided rank and a sorted list of rank tags.
     * <p>
     * The method combines the scoreboard team name of the rank with the short scoreboard tag
     * of the first rank tag from the sorted list, if available. If the list of tags is empty,
     * only the scoreboard team name is returned.
     * </p>
     *
     * @param rank the {@code Rank} object representing the current rank, containing a base scoreboard team name.
     * @param tags a {@code List} of {@code RankTag} objects, which will be sorted in descending order by their ID.
     *             The first element (if the list is not empty) provides a short scoreboard tag that is appended
     *             to the rank's team name.
     * @return a {@code String} representing the constructed team name, which is a combination of
     *         the rank's scoreboard team name and the highest-ranked tag's short scoreboard tag
     *         (if applicable).
     */
    private String teamName(Rank rank, List<RankTag> tags) {
        tags.sort((rankTag, t1) -> t1.getId() - rankTag.getId());
        return rank.getScoreboardTeamName() + (tags.isEmpty() ? "" : tags.get(0).getShortScoreboardTag());
    }

    /**
     * Clears the current state of the player's scoreboard by resetting
     * its configuration and reinitializing it using the {@code setup()} method.
     *
     * <p>This method is typically used to restore the scoreboard to a clean state,
     * ensuring all lines, objectives, and associated metadata are effectively
     * reinitialized. Any preexisting scoreboard state is discarded, and a fresh
     * configuration is created for the player.
     *
     * <p>Key operations performed by this method include:
     * <ul>
     *   <li>Invokes {@code setup()} to reinitialize the scoreboard.</li>
     *   <li>Resets all player-related scoreboard configurations.</li>
     *   <li>Ensures preparedness for new scoreboard modifications.</li>
     * </ul>
     *
     * <p>Calling this method is non-destructive to the player instance itself
     * but will erase all existing scoreboard data, such as lines and objectives,
     * for the associated player.
     */
    @Override
    public void clear() {
        setup();
    }

    /**
     * Toggles the visibility of name tags for all teams within the scoreboard.
     * <p>
     * This method flips the state of the {@code tagsVisible} boolean. If {@code tagsVisible} is set to {@code true},
     * name tags for all teams on the scoreboard will become always visible. Conversely, if {@code tagsVisible} is set to
     * {@code false}, name tags for all teams will be hidden.
     * </p>
     *
     * <p>
     * The following key operations are performed:
     * <ul>
     *     <li>If the scoreboard or its teams are {@code null}, the method exits without applying any changes.</li>
     *     <li>If {@code tagsVisible} is {@code true}, the method iterates through all teams in the scoreboard and sets
     *         their name tag visibility option to {@code Team.OptionStatus.ALWAYS}.</li>
     *     <li>If {@code tagsVisible} is {@code false}, the method iterates through all teams in the scoreboard and sets
     *         their name tag visibility option to {@code Team.OptionStatus.NEVER}.</li>
     * </ul>
     * </p>
     */
    @Override
    public void toggleTags() {
        tagsVisible = !tagsVisible;
        if (scoreboard == null || scoreboard.getTeams() == null) return;
        if (tagsVisible) {
            for (Team team : scoreboard.getTeams()) {
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            }
        } else {
            for (Team team : scoreboard.getTeams()) {
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            }
        }
    }

    /**
     * Toggles the visibility of player tags on the scoreboard based on the provided state.
     *
     * <p>This method adjusts the visibility of tags by comparing the current visibility state
     * with the desired state. If the desired state matches the current state, no further action
     * is taken. Otherwise, it toggles the visibility using the internal {@code toggleTags()} method.</p>
     *
     * <p>This allows for dynamic management of scoreboard tags, enabling or disabling their visibility
     * based on gameplay requirements or player preferences.</p>
     *
     * @param hidden a {@code boolean} indicating whether tags should be hidden
     *               <ul>
     *                 <li>{@code true} to hide the tags.</li>
     *                 <li>{@code false} to show the tags.</li>
     *               </ul>
     */
    @Override
    public void toggleTags(boolean hidden) {
        if (tagsVisible == !hidden) return;
        toggleTags();
    }

    /**
     * Determines whether tags are currently visible on the scoreboard.
     *
     * <p>This method checks the state of the {@code tagsVisible} field to
     * ascertain if the tags associated with the scoreboard are currently
     * being displayed.</p>
     *
     * @return {@code true} if tags are visible on the scoreboard; {@code false} otherwise.
     */
    @Override
    public boolean getTagsVisible() {
        return tagsVisible;
    }
}