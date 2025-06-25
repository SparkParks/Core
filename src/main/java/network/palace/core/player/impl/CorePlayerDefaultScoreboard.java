package network.palace.core.player.impl;

import network.palace.core.Core;
import network.palace.core.events.CoreOnlineCountUpdate;
import network.palace.core.events.EconomyUpdateEvent;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerScoreboardManager;
import network.palace.core.player.PlayerStatus;
import network.palace.core.player.RankTag;
import network.palace.core.utils.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * The {@code CorePlayerDefaultScoreboard} class implements functionality related to the default player scoreboard
 * in the game, providing automated setup and updates for player scoreboards and managing player-specific settings.
 * This class listens for events to dynamically update the contents of the scoreboard based on server and player data.
 * <p>
 * Key features of the default scoreboard include:
 * <ul>
 *     <li>Dynamic content for player-specific balances, tokens, and ranks.</li>
 *     <li>Real-time updates for the number of online players.</li>
 *     <li>Support for managing groups of players to disable scoreboard updates for specific individuals.</li>
 * </ul>
 * <p>
 * This class heavily depends on server configurations and events to maintain consistent functionality
 * across the player base. If the default scoreboard feature is disabled in the server configuration,
 * the functionality provided by this class will not be active.
 *
 * <h3>Event Handling</h3>
 * <ul>
 *     <li>{@link #onOnlineCountUpdate(CoreOnlineCountUpdate)} - Updates the scoreboard for online player counts when the count changes.</li>
 *     <li>{@link #onEconomyUpdate(EconomyUpdateEvent)} - Updates player-specific balance or tokens on economy-related events.</li>
 * </ul>
 *
 * <h3>Scoreboard Contents</h3>
 * <p>
 * The default scoreboard contains the following components:
 * <ul>
 *     <li>Title - A visually prominent title displaying server branding.</li>
 *     <li>Balance - The player's in-game currency balance.</li>
 *     <li>Tokens - The player's current token count.</li>
 *     <li>Rank - Display of the player's rank and any associated tags.</li>
 *     <li>Server Information - The current server type and number of online players.</li>
 *     <li>Store Link - A link promoting the server's store.</li>
 * </ul>
 */
public class CorePlayerDefaultScoreboard implements Listener {
    /**
     * Indicates whether the default scoreboard is enabled by default for the player.
     * <p>
     * This variable is used to determine if the default scoreboard should be displayed
     * to all players unless explicitly disabled for specific users.
     * </p>
     * <p>
     * It is primarily utilized in methods that manage scoreboard visibility and behavior,
     * such as enabling or disabling the scoreboard for a particular player or globally.
     * </p>
     */
    private final boolean defaultScoreboardEnabled;
    /**
     * A list of UUIDs representing players for whom the default scoreboard is disabled.
     *
     * <p>This list is used to track players who have opted out of or are otherwise excluded
     * from using the default scoreboard within the system. Players' UUIDs are added to this list
     * when the default scoreboard is explicitly disabled for them and removed when it is re-enabled.
     *
     * <p>The primary purpose of this list is to support methods that manage the scoreboard preferences
     * of individual players, allowing for customization of their gameplay experience.
     *
     * <ul>
     *   <li>Players in this list will not receive updates or displays for the default scoreboard.</li>
     *   <li>Management of these preferences is handled through related methods in the class, such as
     *       {@code disableDefaultScoreboard(UUID)} and {@code enableDefaultScoreboard(UUID)}.</li>
     * </ul>
     */
    /* An ArrayList tracking the players not using the default scoreboard */
    private final List<UUID> disabledFor = new ArrayList<>();

    /**
     * Constructor for the CorePlayerDefaultScoreboard class.
     *
     * <p>This constructor initializes the default scoreboard system by loading its enabled/disabled
     * state from the core configuration. If the default sidebar is not enabled, initialization
     * is terminated. Otherwise, the class registers itself as an event listener to handle relevant
     * events for scoreboard updates.</p>
     *
     * <ul>
     *   <li>Reads the "isDefaultSidebarEnabled" setting from the core configuration to determine
     *   if the default scoreboard functionality is enabled.</li>
     *   <li>If the functionality is enabled, registers this class as an event listener for handling
     *   scoreboard-related events.</li>
     *   <li>If the functionality is disabled, the class does not activate any further setup logic.</li>
     * </ul>
     */
    public CorePlayerDefaultScoreboard() {
        defaultScoreboardEnabled = Core.getCoreConfig().getBoolean("isDefaultSidebarEnabled", true);
        if (!isDefaultSidebarEnabled()) return;
        Core.registerListener(this);
    }

    /**
     * Sets up the player's scoreboard with relevant information and styling for the default scoreboard layout.
     * <p>
     * This method configures the player's scoreboard by resetting specific rows, adding titles,
     * placeholders, and dynamically generated content such as the player's rank, tags, balance,
     * tokens, online player count, and server name. Additional data such as balance and tokens
     * are loaded asynchronously.
     * </p>
     *
     * <p><strong>Note:</strong> This method will not execute if the default sidebar is disabled
     * globally or for the specified player.</p>
     *
     * @param player The {@link CPlayer} instance for which the scoreboard is being set up.
     *               <ul>
     *                 <li><strong>player.getScoreboard(): </strong> Used to manage the player's scoreboard rows and data.</li>
     *                 <li><strong>player.getTags(): </strong> Retrieves the custom rank tags for formatting additional scoreboard rows.</li>
     *                 <li><strong>player.getRank(): </strong> Provides rank information (e.g., name, color) to display on the scoreboard.</li>
     *               </ul>
     */
    public void setup(CPlayer player) {
        if (!isDefaultSidebarEnabled() || disabledFor.contains(player.getUniqueId())) return;
        CPlayerScoreboardManager scoreboard = player.getScoreboard();
        for (int i = 15; i > 11; i--) {
            scoreboard.remove(i);
        }
        // Title
        scoreboard.title(ChatColor.GOLD + "" + ChatColor.BOLD + "The " + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Palace " + ChatColor.GOLD + "" + ChatColor.BOLD + "Network");
        List<RankTag> tags = player.getTags();
        int tagOffset = tags.size() > 0 ? tags.size() + 1 : 0;
        // Blank space
        scoreboard.setBlank(10 + tagOffset);
        // Balance temp
        scoreboard.set(9 + tagOffset, ChatColor.GREEN + "$ Loading...");
        // Blank space
        scoreboard.setBlank(8 + tagOffset);
        // Tokens temp
        scoreboard.set(7 + tagOffset, ChatColor.GREEN + "\u272a Loading...");
        // Blank space
        scoreboard.setBlank(6 + tagOffset);
        // Rank
        scoreboard.set(5 + tagOffset, ChatColor.GREEN + "Rank: " + player.getRank().getTagColor() + player.getRank().getName());
        if (tags.size() > 0) {
            scoreboard.setBlank(4 + tagOffset);
            for (int i = tags.size(); i > 0; i--) {
                RankTag tag = tags.get(tags.size() - i);
                scoreboard.set(4 + i, tag.getColor() + "" + ChatColor.ITALIC + "" + tag.getName());
            }
        }
        // Blank space
        scoreboard.setBlank(4);
        // Players number
        scoreboard.set(3, ChatColor.GREEN + "Online Players: " + Core.getPlayerManager().getPlayerCount());
        // Server name
        scoreboard.set(2, ChatColor.GREEN + "Server: " + Core.getServerType());
        // Blank
        scoreboard.setBlank(1);
        // Store link #Sellout
        scoreboard.set(0, ChatColor.YELLOW + "store.palace.network");
        // Load balance async
        loadBalance(player, scoreboard, 9 + tagOffset);
        // Load tokens async
        loadTokens(player, scoreboard, 7 + tagOffset);
    }

    /**
     * Responds to updates to the online player count by adjusting scoreboard statistics for all
     * eligible players. This method listens for the {@link CoreOnlineCountUpdate} event and updates
     * each player's scoreboard with the latest online player count if they meet the necessary criteria.
     * <p>
     * The updated online count is displayed on the third line of the scoreboard, formatted as:
     * <code>Online Players: [formatted count]</code>.
     * <p>
     * The following conditions are checked for each player before updating their scoreboard:
     * <ul>
     *     <li>The player must have the {@link PlayerStatus#JOINED} status.</li>
     *     <li>The player's scoreboard must be properly set up.</li>
     *     <li>The player must not have their default scoreboard functionality disabled.</li>
     * </ul>
     *
     * @param event The {@link CoreOnlineCountUpdate} event containing the updated online player count.
     */
    @EventHandler
    public void onOnlineCountUpdate(CoreOnlineCountUpdate event) {
        for (CPlayer player : Core.getPlayerManager().getOnlinePlayers()) {
            if (player.getStatus() != PlayerStatus.JOINED ||
                    !player.getScoreboard().isSetup() ||
                    disabledFor.contains(player.getUniqueId())) continue;
            player.getScoreboard().set(3, ChatColor.GREEN + "Online Players: " + MiscUtil.formatNumber(event.getCount()));
        }
    }

    /**
     * Handles updates to a player's economy when the {@link EconomyUpdateEvent} is triggered.
     * <p>
     * This method listens for changes in a player's economic data, such as balance or tokens,
     * and updates the player's scoreboard accordingly if the default scoreboard is enabled.
     * If the player is disabled for the default scoreboard, this method exits early without
     * making any changes.
     * <p>
     * Depending on the type of currency involved in the event, the corresponding economy panel
     * (e.g., balance or tokens) on the player's scoreboard is updated to reflect the new value.
     * This behavior allows real-time updates of economy-related metrics in the player's user interface.
     * </p>
     *
     * <ul>
     *   <li>If the currency type is {@code BALANCE}, the player's balance panel on the scoreboard
     *   is updated with the new amount.</li>
     *   <li>If the currency type is {@code TOKENS}, the player's tokens panel on the scoreboard
     *   is updated with the new amount.</li>
     * </ul>
     *
     * @param event the {@link EconomyUpdateEvent} that contains details about the player,
     *              the updated economy amount, and the type of currency involved in the update.
     *              This value cannot be null.
     */
    @EventHandler
    public void onEconomyUpdate(EconomyUpdateEvent event) {
        int amount = event.getAmount();
        CPlayer player = Core.getPlayerManager().getPlayer(event.getUuid());
        if (player == null || disabledFor.contains(player.getUniqueId())) return;
        List<RankTag> tags = player.getTags();
        int tagOffset = tags.size() > 0 ? tags.size() + 1 : 0;
        switch (event.getCurrency()) {
            case BALANCE:
                setBalance(9 + tagOffset, player.getScoreboard(), amount);
                break;
            case TOKENS:
                setTokens(7 + tagOffset, player.getScoreboard(), amount);
                break;
        }
    }

    /**
     * Asynchronously loads the token count of a player and updates the given scoreboard at the specified position.
     *
     * <p>This method fetches the token count of the provided player in an asynchronous task,
     * preventing potential blocking of the main thread. Once retrieved, it schedules a synchronous
     * task to update the player's tokens on the given scoreboard.</p>
     *
     * <ul>
     *   <li>Fetches the player's token count asynchronously.</li>
     *   <li>Updates the scoreboard synchronously to ensure thread safety.</li>
     * </ul>
     *
     * @param player The {@link CPlayer} instance representing the player whose tokens are being loaded.
     * @param scoreboard The {@link CPlayerScoreboardManager} instance responsible for managing the player's scoreboard.
     * @param position The integer position in the scoreboard where the token count should be displayed.
     */
    public void loadTokens(CPlayer player, CPlayerScoreboardManager scoreboard, int position) {
        Core.runTaskAsynchronously(Core.getInstance(), () -> {
            int tokens = player.getTokens();
            Core.callSyncMethod(Core.getInstance(), (Callable<Object>) () -> {
                setTokens(position, scoreboard, tokens);
                return true;
            });
        });
    }

    /**
     * Updates the balance information for a specified player on their scoreboard.
     *
     * <p>This method retrieves the player's current balance asynchronously and updates
     * the specified position on their scoreboard in a synchronized task.</p>
     *
     * @param player The {@code CPlayer} whose balance is to be retrieved and displayed.
     * @param scoreboard The {@code CPlayerScoreboardManager} instance managing the player's scoreboard.
     * @param position The position on the scoreboard where the balance should be displayed.
     */
    public void loadBalance(CPlayer player, CPlayerScoreboardManager scoreboard, int position) {
        Core.runTaskAsynchronously(Core.getInstance(), () -> {
            int balance = player.getBalance();
            Core.callSyncMethod(Core.getInstance(), (Callable<Object>) () -> {
                setBalance(position, scoreboard, balance);
                return true;
            });
        });
    }

    /**
     * Updates the token count displayed at a specific position on the player's scoreboard.
     *
     * <p>If the token count exceeds the maximum integer value allowed (minus one), the scoreboard
     * displays the maximum value followed by a "+" symbol. Otherwise, the exact token count is displayed.</p>
     *
     * @param position The integer position on the scoreboard where the token count should be displayed.
     * @param scoreboard The {@link CPlayerScoreboardManager} instance managing the player's scoreboard display.
     * @param tokens The token count to display on the scoreboard.
     */
    private void setTokens(int position, CPlayerScoreboardManager scoreboard, int tokens) {
        if (tokens > (Integer.MAX_VALUE - 1)) {
            scoreboard.set(position, ChatColor.GREEN + "\u272a " + (Integer.MAX_VALUE - 1) + "+");
        } else {
            scoreboard.set(position, ChatColor.GREEN + "\u272a " + tokens);
        }
    }

    /**
     * Sets the balance value for a specific position on the player's scoreboard.
     *
     * <p>This method updates the player's scoreboard with a formatted balance. If the balance
     * exceeds the maximum allowable integer value, it displays a capped value with a "+" symbol
     * to indicate the overflow. Otherwise, the exact balance is displayed.</p>
     *
     * <ul>
     *   <li>Formats the balance to include a dollar sign and applies green coloration.</li>
     *   <li>Caps the displayed balance at {@link Integer#MAX_VALUE - 1} if the provided balance
     *       exceeds this limit.</li>
     * </ul>
     *
     * @param position The integer position on the scoreboard where the balance should be displayed.
     * @param scoreboard The {@code CPlayerScoreboardManager} instance managing the player's scoreboard.
     * @param balance The integer value representing the player's current balance.
     */
    private void setBalance(int position, CPlayerScoreboardManager scoreboard, int balance) {
        if (balance > (Integer.MAX_VALUE - 1)) {
            scoreboard.set(position, ChatColor.GREEN + "$ " + (Integer.MAX_VALUE - 1) + "+");
        } else {
            scoreboard.set(position, ChatColor.GREEN + "$ " + balance);
        }
    }

    /**
     * Disables the default scoreboard functionality for a specified player.
     *
     * <p>This method prevents the player identified by the provided {@link UUID}
     * from being included in the default scoreboard system. Once disabled,
     * the player's scoreboard will not receive updates related to the default
     * scoreboard functionality unless re-enabled.</p>
     *
     * <ul>
     *   <li>The player's {@link UUID} is added to an internal tracking list of disabled players.</li>
     *   <li>Only players on this list will have their default scoreboard functionality disabled.</li>
     * </ul>
     *
     * @param uuid The {@link UUID} of the player for whom the default scoreboard should be disabled.
     *             Must not be null.
     */
    public void disableDefaultScoreboard(UUID uuid) {
        disabledFor.add(uuid);
    }

    /**
     * Enables the default scoreboard for the specified player by removing their {@code UUID}
     * from the list of players for whom the default scoreboard is disabled.
     *
     * <p>The default scoreboard functionality allows a player to view dynamic, real-time
     * statistics and updates in the game. By calling this method, the player will start
     * receiving these updates unless globally disabled.</p>
     *
     * <ul>
     *   <li>If the player's {@code UUID} exists in the {@code disabledFor} list, it will be removed,
     *   enabling the default scoreboard for that player.</li>
     *   <li>If the {@code UUID} is not in the {@code disabledFor} list, calling this method has
     *   no additional effect.</li>
     * </ul>
     *
     * @param uuid The {@link UUID} of the player for whom the default scoreboard should be enabled.
     *             <ul>
     *               <li>This value identifies a unique player in the game.</li>
     *               <li>When removed from the {@code disabledFor} list, the player will have their
     *               default scoreboard functionality restored.</li>
     *             </ul>
     */
    public void enableDefaultScoreboard(UUID uuid) {
        disabledFor.remove(uuid);
    }

    /**
     * Checks whether the default scoreboard functionality is disabled for the specified player.
     *
     * <p>This method determines whether a given player's UUID is present in the set of players
     * who have the default scoreboard feature disabled.</p>
     *
     * @param uuid The {@link UUID} of the player whose default scoreboard status is being checked.
     *             This value is used to identify whether the player is excluded from receiving
     *             the default scoreboard updates.
     * @return {@code true} if the default scoreboard is disabled for the player,
     *         {@code false} otherwise.
     */
    public boolean isDefaultScoreboardDisabled(UUID uuid) {
        return disabledFor.contains(uuid);
    }

    /**
     * Checks whether the default sidebar functionality is enabled.
     *
     * <p>This method determines the enabled state of the default scoreboard sidebar based
     * on the configuration or state stored within the system. The sidebar displays various
     * gameplay-related statistics and information to the user, and its functionality can be
     * toggled globally or for specific players as required.</p>
     *
     * <ul>
     *   <li>If enabled, the default sidebar will display relevant information.</li>
     *   <li>If disabled, the default sidebar feature will be turned off.</li>
     * </ul>
     *
     * @return {@code true} if the default sidebar is enabled; {@code false} otherwise.
     */
    private boolean isDefaultSidebarEnabled() {
        return defaultScoreboardEnabled;
    }
}
