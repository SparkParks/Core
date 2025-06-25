package network.palace.core.player.impl.managers;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.events.CoreOnlineCountUpdate;
import network.palace.core.events.CorePlayerJoinedEvent;
import network.palace.core.events.CurrentPackReceivedEvent;
import network.palace.core.player.*;
import network.palace.core.player.impl.CorePlayer;
import network.palace.core.player.impl.CorePlayerDefaultScoreboard;
import network.palace.core.player.impl.listeners.CorePlayerManagerListener;
import network.palace.core.player.impl.listeners.CorePlayerStaffLoginListener;
import network.palace.core.utils.ProtocolUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * The {@code CorePlayerManager} class serves as the main player management system within the application.
 * It implements the {@link CPlayerManager} interface, managing and coordinating all operations related
 * to online players, including login, logout, scoreboard management, rank displays, skin caching, and other
 * player-related functionality.
 *
 * <p>This class integrates with various subsystems such as the permission manager, MongoDB data handler,
 * scoreboard manager, and event system, ensuring seamless operations for players in the environment. It also
 * handles core tasks like event listeners, asynchronous routines, and scheduled updates for online players.
 */
public class CorePlayerManager implements CPlayerManager {
    /**
     * The <code>defaultScoreboard</code> field represents the default scoreboard
     * assigned to players managed by the <code>CorePlayerManager</code>.
     * <p>
     * This scoreboard is typically used to display relevant player information
     * or game statistics in the default state when no custom scoreboard is set
     * for a player.
     * </p>
     * <p>
     * It can be manipulated or replaced using the
     * {@link #setDefaultScoreboard(CorePlayerDefaultScoreboard)} method to update
     * its configuration.
     * </p>
     * <ul>
     *   <li>Accessible as a getter to retrieve the current default scoreboard.</li>
     *   <li>Encapsulates the default configuration for all players.</li>
     * </ul>
     *
     * <p>
     * Utilized as part of the player management system in the context of
     * maintaining consistent player UI presentation and unified scoreboard state
     * for all players managed by the system.
     * </p>
     */
    @Getter private CorePlayerDefaultScoreboard defaultScoreboard;

    /**
     * Represents a mapping of currently online players in the system.
     *
     * <p>This map uses a {@link UUID} as the key to uniquely identify each player and
     * associates it with a {@link CPlayer} object that contains the player's data and state.
     *
     * <p>This field is immutable in reference (declared as {@code final}), ensuring that the map
     * itself cannot be reassigned during the lifetime of the application. However, the contents
     * of the map (the key-value pairs) can be modified as players log in and out of the system.
     *
     * <p>Key characteristics:
     * <ul>
     *     <li><b>UUID:</b> Universally unique identifier for a player.</li>
     *     <li><b>CPlayer:</b> The object representing player-specific information, such as
     *         statistics, ranks, and other related data.</li>
     * </ul>
     *
     * <p>Common operations associated with this field may include:
     * <ul>
     *     <li>Adding new entries when players log in.</li>
     *     <li>Removing entries when players log out.</li>
     *     <li>Retrieving player data while a player is online using their UUID as the key.</li>
     * </ul>
     *
     * <p>This field is initialized to an empty {@code HashMap} by default and is populated dynamically
     * as players interact with the system.
     */
    private final Map<UUID, CPlayer> onlinePlayers = new HashMap<>();

    /**
     * Represents the count of currently online players.
     * <p>
     * This variable keeps track of the number of players who are actively connected
     * to the server and recognized by the player management system.
     * <p>
     * <ul>
     *     <li>Initialized to 0 by default.</li>
     *     <li>May be modified as players join or leave the server.</li>
     * </ul>
     */
    @Getter private int playerCount = 0;

    /**
     * Constructs a new instance of the {@code CorePlayerManager}.
     *
     * <p>This constructor initializes the CorePlayerManager by performing the following operations:
     * <ul>
     *     <li>Registers an internal {@code CorePlayerManagerListener} to listen for player-related events.</li>
     *     <li>Registers a {@code CorePlayerStaffLoginListener} to handle staff login events.</li>
     *     <li>Initializes the {@code defaultScoreboard} with a new {@code CorePlayerDefaultScoreboard} instance.</li>
     *     <li>Schedules a recurring task using the Core scheduler to:
     *         <ul>
     *             <li>Update the player count in MongoDB based on the {@code onlinePlayers} size.</li>
     *             <li>Periodically fetch and update the global online player count, broadcasting it using the {@code CoreOnlineCountUpdate} event.</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * <p>The scheduled task alternates between updating data on its iterations to minimize redundant or excessive operations, ensuring optimal performance.
     */
    public CorePlayerManager() {
        Core.registerListener(new CorePlayerManagerListener());
        Core.registerListener(new CorePlayerStaffLoginListener());
        defaultScoreboard = new CorePlayerDefaultScoreboard();
        Core.runTaskTimer(Core.getInstance(), new Runnable() {
            boolean b = true;

            @Override
            public void run() {
                Core.getMongoHandler().setPlayerCount(Core.getInstanceName(), Core.isPlayground(), onlinePlayers.size());
                if (b) {
                    playerCount = Core.getMongoHandler().getPlayerCount();
                    new CoreOnlineCountUpdate(playerCount).call();
                }
                b = !b;
            }
        }, 20L, 100L);
    }

    /**
     * Handles the login process when a player joins the server. This method retrieves
     * relevant player data from a MongoDB database and initializes a player object
     * for use within the system. If the player is new, default values are applied
     * to their data.
     *
     * <p>The method initializes the following player details:
     * <ul>
     *     <li>Assigns a rank to the player based on retrieved data or sets it to
     *         the default {@code Rank.GUEST} for new players.</li>
     *     <li>Loads rank-specific tags if available.</li>
     *     <li>Verifies online data and associates the player with a proxy identifier.</li>
     *     <li>Stores the player object in a map for further management.</li>
     * </ul>
     *
     * <p>If certain required data is missing, such as the {@code onlineData} document
     * for an existing player, an exception will be thrown to signal the inconsistency.
     *
     * @param uuid The unique identifier (UUID) of the player logging in.
     * @param name The username of the player logging in.
     * @throws Exception If an inconsistency is found in the stored online player data
     *                   or other critical errors occur during initialization.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void playerLoggedIn(UUID uuid, String name) throws Exception {
        Document joinData = Core.getMongoHandler().getJoinData(uuid, "rank", "tags", "onlineData");
        Rank rank;
        List<RankTag> tags = new ArrayList<>();
        UUID proxy;
        Document onlineData;
        if (joinData == null) {
            // new player!
            rank = Rank.GUEST;
            proxy = UUID.randomUUID();
            onlineData = new Document();
        } else {
            rank = joinData.containsKey("rank") ? Rank.fromString(joinData.getString("rank")) : Rank.GUEST;
            if (joinData.containsKey("tags")) {
                joinData.get("tags", ArrayList.class).forEach(o -> tags.add(RankTag.fromString((String) o)));
            }
            if (!joinData.containsKey("onlineData")) {
                throw new Exception("Player isn't online!");
            } else {
                onlineData = (Document) joinData.get("onlineData");
                proxy = UUID.fromString(onlineData.getString("proxy"));
            }
        }
        CPlayer player = new CorePlayer(uuid, name, rank, tags, "en_us");
        player.getRegistry().addEntry("proxy", proxy);
        player.getRegistry().addEntry("onlineData", onlineData);
        onlinePlayers.put(uuid, player);
    }

    /**
     * Handles the event when a player joins the server.
     *
     * <p>This method performs the following operations:
     * <ul>
     *     <li>Retrieves and initializes the core player instance from the given player.</li>
     *     <li>Sets protocol version and player status to "JOINED".</li>
     *     <li>Executes asynchronous tasks, including:
     *         <ul>
     *             <li>Caching the player's skin texture data in MongoDB.</li>
     *             <li>Loading and managing achievements for the player.</li>
     *             <li>Loading the player's honor stats and displaying honor-related information.</li>
     *             <li>Handling resource pack-related data, if applicable.</li>
     *         </ul>
     *     </li>
     *     <li>Sets up permissions for the player.</li>
     *     <li>Displays the player's rank using the scoreboard.</li>
     *     <li>Configures the player’s tab header and footer.</li>
     *     <li>Displays a login title to the player, if enabled.</li>
     *     <li>Calls the {@code CorePlayerJoinedEvent} to signal the player's join event.</li>
     * </ul>
     *
     * @param player The {@code Player} instance representing the player who just joined.
     */
    @Override
    public void playerJoined(Player player) {
        // Get core player
        CPlayer corePlayer = getPlayer(player);
        if (corePlayer == null) return;
        corePlayer.setProtocolId(ProtocolUtil.getProtocolVersion(player));
        // Joined
        corePlayer.setStatus(PlayerStatus.JOINED);

        // Async Task
        Core.runTaskAsynchronously(Core.getInstance(), () -> {
            // Cache Skin
            WrappedGameProfile wrappedGameProfile = WrappedGameProfile.fromPlayer(player);
            Optional<WrappedSignedProperty> propertyOptional = wrappedGameProfile.getProperties().get("textures").stream().findFirst();
            if (propertyOptional.isPresent()) {
                WrappedSignedProperty property = propertyOptional.get();
                corePlayer.setTextureValue(property.getValue());
                corePlayer.setTextureSignature(property.getSignature());
            }
            Core.getMongoHandler().cacheSkin(corePlayer.getUniqueId(), corePlayer.getTextureValue(), corePlayer.getTextureSignature());

            // Achievements
            List<Integer> ids = Core.getMongoHandler().getAchievements(corePlayer.getUniqueId());
            corePlayer.setAchievementManager(new CorePlayerAchievementManager(corePlayer, ids));
            Core.getCraftingMenu().update(corePlayer, 2, Core.getCraftingMenu().getAchievement(corePlayer));
            corePlayer.loadHonor(Core.getMongoHandler().getHonor(corePlayer.getUniqueId()));
            corePlayer.setPreviousHonorLevel(Core.getHonorManager().getLevel(corePlayer.getHonor()).getLevel());
            corePlayer.giveAchievement(0);
            Core.getHonorManager().displayHonor(corePlayer, true);
            Object packObject = Core.getMongoHandler().getOnlineDataValue(corePlayer.getUniqueId(), "resourcePack");
            String pack = packObject == null ? "none" : (String) packObject;
            Core.runTask(() -> new CurrentPackReceivedEvent(corePlayer, pack).call());
        });

        // Setup permissions for player
        Core.getPermissionManager().login(corePlayer);
        // Display the scoreboard
        displayRank(corePlayer);
        // Tab header and footer
        corePlayer.getHeaderFooter().setHeaderFooter(Core.getInstance().getTabHeader(), Core.getInstance().getTabFooter());
        // Show the title if we're supposed to
        if (Core.getInstance().isShowTitleOnLogin()) {
            player.sendTitle(Core.getInstance().getLoginTitle(), Core.getInstance().getLoginSubTitle(),
                    Core.getInstance().getLoginTitleFadeIn(), Core.getInstance().getLoginTitleStay(), Core.getInstance().getLoginTitleFadeOut());
        }
        // Called joined event
        new CorePlayerJoinedEvent(corePlayer).call();
    }

    /**
     * Handles the event when a player logs out of the server.
     *
     * <p>This method ensures the proper removal of the player from the system to
     * maintain accurate state and prevent any potential conflicts related to the
     * player's session. It performs the following operations:
     * <ul>
     *   <li>Checks if the provided {@link Player} object is not <code>null</code>.</li>
     *   <li>Removes the player from active records using their unique identifier.</li>
     * </ul>
     *
     * @param player the {@link Player} instance representing the player who is logging out.
     *               If <code>null</code>, the method will exit without performing any actions.
     */
    @Override
    public void playerLoggedOut(Player player) {
        if (player == null) return;
        removePlayer(player.getUniqueId());
    }

    /**
     * Removes a player from the online players list and performs cleanup operations related to their data and state.
     *
     * <p>This method handles the removal of a player identified by their {@link UUID}. The following operations are performed:
     * <ul>
     *   <li>Safely ignores the operation if the provided {@code UUID} or corresponding player object is {@code null}.</li>
     *   <li>Removes any tags or references to the player from the scoreboards of other online players.</li>
     *   <li>Calls internal reset procedures for the player's managers and sets their status to {@code LEFT}.</li>
     *   <li>Removes the player from the internal {@code onlinePlayers} collection.</li>
     * </ul>
     *
     * <p>All relevant cleanup steps are executed on the main server thread to ensure thread safety.
     *
     * @param uuid the unique identifier of the player to be removed. Cannot be {@code null}.
     */
    @Override
    public void removePlayer(UUID uuid) {
        if (uuid == null) return;
        CPlayer cPlayer = getPlayer(uuid);
        if (cPlayer == null) return;
        Core.runTask(Core.getInstance(), () -> {
            for (CPlayer otherPlayer : Core.getPlayerManager().getOnlinePlayers()) {
                cPlayer.getScoreboard().removePlayerTag(otherPlayer);
                otherPlayer.getScoreboard().removePlayerTag(cPlayer);
            }
        });
        cPlayer.resetManagers();
        cPlayer.setStatus(PlayerStatus.LEFT);
        onlinePlayers.remove(cPlayer.getUniqueId());
    }

    /**
     * Broadcasts a message to all online players.
     *
     * <p>This method retrieves the list of all currently online players and sends the specified message
     * to each player individually.</p>
     *
     * @param message The message to broadcast. It must be a non-null, non-empty string that represents
     *                the content to be sent to all online players.
     */
    @Override
    public void broadcastMessage(String message) {
        getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

    /**
     * Retrieves the {@code CPlayer} instance associated with the specified {@code UUID}.
     *
     * <p>This method searches for and returns the player object from the list of online players
     * using the provided unique identifier. If no player is found with the specified {@code UUID},
     * this method will return {@code null}.
     *
     * @param playerUUID the unique identifier of the player to retrieve. This value must not be
     *                   {@code null}.
     *
     * @return the {@code CPlayer} instance representing the player associated with the given
     *         {@code UUID}, or {@code null} if the player is not found.
     */
    @Override
    public CPlayer getPlayer(UUID playerUUID) {
        return onlinePlayers.get(playerUUID);
    }

    /**
     * Retrieves the corresponding {@code CPlayer} instance for the given {@link Player}.
     *
     * <p>If the provided {@link Player} object is <code>null</code>, this method will return
     * <code>null</code>. Otherwise, it attempts to retrieve the {@code CPlayer} instance
     * using the player's unique identifier.</p>
     *
     * @param player The {@link Player} object representing the player whose {@code CPlayer}
     *               instance should be retrieved. This parameter can be {@code null}.
     * @return The {@code CPlayer} instance corresponding to the given {@link Player},
     *         or {@code null} if the {@link Player} object is {@code null}.
     */
    @Override
    public CPlayer getPlayer(Player player) {
        if (player == null) {
            return null;
        }
        return getPlayer(player.getUniqueId());
    }

    /**
     * Retrieves a {@link CPlayer} instance by the player's name.
     *
     * <p>This method attempts to locate an online player by the given name.
     * If the player is found, their corresponding {@code CPlayer} instance
     * is returned. If no player with the specified name is currently online,
     * this method will return {@code null}.
     *
     * @param name The username of the player to retrieve. Cannot be {@code null}.
     * @return The {@link CPlayer} instance associated with the given name,
     *         or {@code null} if no such player is currently online.
     */
    @Override
    public CPlayer getPlayer(String name) {
        Player p = Bukkit.getPlayer(name);
        if (p == null)
            return null;
        return getPlayer(p);
    }

    /**
     * Retrieves a list of all currently online players.
     *
     * <p>This method provides access to the collection of players who are
     * currently active on the server as {@link CPlayer} objects.</p>
     *
     * <p>The returned list is a new copy of the internal collection to ensure
     * thread safety and prevent unintended modifications to the underlying data.</p>
     *
     * @return A {@link List} of {@link CPlayer} objects representing the online players.
     */
    @Override
    public List<CPlayer> getOnlinePlayers() {
        return new ArrayList<>(onlinePlayers.values());
    }

    /**
     * Displays the rank of the given player and sets up the player's scoreboard and tags
     * accordingly. This method ensures the player's operator status aligns with their rank
     * and adjusts scoreboard data for them and other online players.
     *
     *<p>The method performs the following operations:
     * <ul>
     *   <li>Determines whether the player should have operator (op) status based on their rank
     *       and updates this status if needed.</li>
     *   <li>Configures the player's scoreboard and sets up associated player tags.</li>
     *   <li>Schedules a delayed task to add player tags to the scoreboards of currently
     *       online players, ensuring visibility and synchronization across all sessions.</li>
     * </ul>
     *
     * @param player The {@link CPlayer} instance whose rank and scoreboard
     *               are being managed. Must not be {@code null}.
     */
    @Override
    public void displayRank(CPlayer player) {
        // Set op if the player should be
        boolean op = player.getRank().isOp();
        if (player.isOp() != op) {
            player.setOp(op);
        }

        player.getScoreboard().setupPlayerTags();
        defaultScoreboard.setup(player);
        Core.runTaskLater(Core.getInstance(), () -> {
            for (CPlayer otherPlayer : Core.getPlayerManager().getOnlinePlayers()) {
                if (player.getScoreboard() != null) player.getScoreboard().addPlayerTag(otherPlayer);
                if (!player.getUniqueId().equals(otherPlayer.getUniqueId()) &&
                        otherPlayer.getScoreboard() != null) otherPlayer.getScoreboard().addPlayerTag(player);
            }
        }, 20L);
    }

    /**
     * Sets the default scoreboard for the {@code CorePlayerManager} instance.
     *
     * <p>This method updates the current default scoreboard to the provided
     * instance of {@link CorePlayerDefaultScoreboard}. Upon setting the new
     * default scoreboard, the scoreboard setup procedure is immediately
     * applied to all currently online players.
     *
     * @param defaultScoreboard The new {@code CorePlayerDefaultScoreboard} instance
     *                          to be set as the default scoreboard. It must be
     *                          non-null and properly initialized to define the
     *                          scoreboard's behavior and appearance.
     */
    public void setDefaultScoreboard(CorePlayerDefaultScoreboard defaultScoreboard) {
        this.defaultScoreboard = defaultScoreboard;
        getOnlinePlayers().forEach(defaultScoreboard::setup);
    }
}
