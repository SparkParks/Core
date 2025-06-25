package network.palace.core.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.events.CorePlayerJoinedEvent;
import network.palace.core.npc.mob.MobPlayer;
import network.palace.core.packets.server.entity.WrapperPlayServerPlayerInfo;
import network.palace.core.packets.server.scoreboard.WrapperPlayServerScoreboardTeam;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * The {@code SoftNPCManager} class is responsible for managing non-player characters (NPCs) and their interactions
 * with players in the game environment. It implements the {@code Listener} interface to handle various events
 * triggered during player or NPC activity. It provides mechanisms for:
 * - Tracking and updating the visibility, position, and state of NPC entities in relation to players.
 * - Managing hidden player mobs and ensuring their proper representation using a scoreboard team.
 * - Controlling the display of NPCs in the player's tab list and removing them if necessary.
 * - Handling player-specific events such as movement, teleportation, world changes, and login/logout to update NPCs accordingly.
 *
 * This class operates as a core system for NPC management and ensures that the appropriate updates are applied
 * dynamically based on player actions to maintain a consistent environment.
 *
 * Core functionalities include:
 * - Automatic entity updates using scheduled tasks.
 * - Event-driven NPC state management (movement, visibility, spawning/despawning).
 * - Delegating actions to support systems like "hidden teams" and tab list management.
 */
public final class SoftNPCManager implements Listener {
    /**
     * Represents the name of a predefined team containing hidden players or entities.
     *
     * This constant is used within the context of managing hidden player mobs or entities
     * that should not be visible or interactable to other players within the system.
     *
     * The value "hidden_players" is used to identify and categorize such entities, ensuring
     * they are handled separately in logic such as rendering, interactions, or other game mechanics.
     *
     * It is a static, unmodifiable constant, providing a consistent reference throughout the
     * {@code SoftNPCManager} class.
     */
    private static final String HIDDEN_TEAM = "hidden_players";

    /**
     * Represents the maximum distance, in blocks, at which non-player characters (NPCs)
     * are displayed to players.
     *
     * This value defines the radius around a player within which NPCs are rendered.
     * If an NPC is located outside this distance from a player's position, the NPC
     * will not be visible to that player. This feature is used for optimizing server
     * performance by limiting the rendering of NPCs to those within close proximity
     * to players.
     *
     * A higher value increases the range of NPC visibility, which can improve the
     * user experience by displaying more NPCs in the environment, but may result in
     * greater resource utilization. Conversely, a lower value reduces resource usage
     * at the cost of limiting NPC visibility.
     *
     * This constant is immutable and shared across all instances of the class.
     */
    private static final int RENDER_DISTANCE = 60;

    /**
     * Represents the minimum distance required for a teleport action
     * to be processed for an entity or a player within the system.
     *
     * This value is used to ensure that teleportation events are only
     * handled when the distance between the starting point and the
     * destination exceeds this threshold. Setting a minimum distance
     * prevents the system from responding to trivial movements that
     * may otherwise be considered as teleportation, optimizing event
     * handling.
     *
     * Units are measured in blocks.
     */
    private static final int TELEPORT_MIN_DISTANCE = 15;

    /**
     * Manages an instance of the {@link IDManager} class, which is responsible for generating
     * and managing unique ID values across entities in multiple worlds.
     *
     * The {@code IDManager} ensures ID uniqueness by verifying against existing IDs
     * in all current entities, making it critical for maintaining a non-conflicting
     * ID sequence within the system. This field is used to provide access to the
     * {@code IDManager} functionality within the class.
     */
    @Getter private IDManager iDManager;

    /**
     * A set of weak references to AbstractEntity objects. This collection is used to manage
     * instances of AbstractEntity without preventing them from being garbage collected.
     *
     * The weak references allow entities to be tracked while avoiding strong references
     * that could lead to memory leaks. Once an entity is no longer referenced elsewhere,
     * it will be eligible for garbage collection and automatically removed from this set.
     *
     * This is particularly useful in scenarios where entities may be created and discarded
     * frequently, ensuring efficient memory management for tracked entity objects.
     */
    @Getter private final Set<WeakReference<AbstractEntity>> entityRefs = new HashSet<>();

    /**
     * A list that keeps track of the identifiers (names) of player-controlled mobs
     * which are hidden from view or interaction within the system.
     *
     * This list is used to manage and track player-specific mobs that should be
     * excluded from visibility or operations, ensuring they remain concealed according
     * to the game's logic or specific mechanics.
     */
    private List<String> hiddenPlayerMobs = new ArrayList<>();

    /**
     * A map that associates a UUID with a list of MobPlayer instances representing mobs
     * that need to be removed from the tab list for specific players. The UUID corresponds
     * to a player's unique identifier.
     *
     * This field is used to manage visibility of certain mob entities in the player's
     * tab list, ensuring they are hidden when necessary. The logic for adding or removing
     * entries in this map is handled internally within the associated methods of the
     * SoftNPCManager class.
     */
    private HashMap<UUID, List<MobPlayer>> removeFromTabList = new HashMap<>();

    /**
     * Constructs a new instance of SoftNPCManager.
     *
     * This constructor performs the following operations:
     * 1. Initializes the ID manager (`iDManager`) for handling unique IDs.
     * 2. Registers the instance of this class as an event listener in the Core system.
     * 3. Schedules a repeating task to maintain proper synchronization and removal
     *    of MobPlayer entities from player tab lists under specific conditions.
     *
     * The scheduled task iterates through a cloned version of the `removeFromTabList` map,
     * ensuring thread safety by clearing the original map before processing. For each player
     * and associated MobPlayer entities, the following conditions are checked:
     *
     * - If the player is offline or their online time is less than 3000 milliseconds,
     *   the entities are preserved for further processing in subsequent task iterations.
     * - Otherwise, it removes the MobPlayer's names from the player's tab list
     *   using the appropriate PlayerInfo packets.
     *
     * The task is executed with an initial delay of 20 ticks and repeats every 10 ticks.
     * This ensures the regular monitoring and management of MobPlayer entities for optimization
     * and maintaining server performance.
     */
    public SoftNPCManager() {
        iDManager = new IDManager();
        Core.registerListener(this);
        Core.runTaskTimer(Core.getInstance(), () -> {
            HashMap<UUID, List<MobPlayer>> localMap = (HashMap<UUID, List<MobPlayer>>) removeFromTabList.clone();
            removeFromTabList.clear();
            for (Map.Entry<UUID, List<MobPlayer>> entry : localMap.entrySet()) {
                UUID uuid = entry.getKey();
                List<MobPlayer> mobs = entry.getValue();
                CPlayer player = Core.getPlayerManager().getPlayer(uuid);
                if (player == null) continue;
                if (player.getOnlineTime() < 3000) {
                    removeFromTabList.put(uuid, mobs);
                    continue;
                }

                for (MobPlayer mob : mobs) {
                    if (mob == null) continue;
                    WrapperPlayServerPlayerInfo hideName = new WrapperPlayServerPlayerInfo();
                    hideName.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    hideName.setData(Collections.singletonList(new PlayerInfoData(new WrappedGameProfile(mob.getUuid(), mob.getCustomName()), 0, EnumWrappers.NativeGameMode.ADVENTURE, null)));
                    hideName.sendPacket(player);
                }
            }
        }, 20L, 10L);
    }

    /**
     * Ensures that all elements within the entityRefs collection remain valid by removing
     * any entries that are no longer accessible.
     *
     * This method iterates through the entityRefs collection, which is assumed to store references
     * to objects, and removes elements where the associated reference has been garbage collected
     * (e.g., instances where {@code get()} returns {@code null}). This process is essential to
     * maintain the integrity of the collection by preventing stale or invalid references from
     * persisting.
     */
    private void ensureAllValid() {
        entityRefs.removeIf(mob -> mob.get() == null);
    }

    /**
     * Handles the player's movement by monitoring changes in their location.
     * Updates the player's position based on the movement event, ensuring that the player's yaw value
     * is normalized within a 0 to 360-degree range.
     *
     * @param event The player movement event to be handled, which provides details about
     *              the source and destination location of the player's movement.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) return;
        Location from = event.getFrom().clone();
        Location to = event.getTo().clone();
        to.setYaw(to.getYaw() % 360);
        updatePosition(player, from, to);
    }

    /**
     * Monitors and handles player teleportation events. This method is triggered whenever a player
     * teleports, updating the player's position based on their teleportation source and destination.
     *
     * @param event The {@link PlayerTeleportEvent} that contains information about the teleportation,
     *              including the source (from) location and destination (to) location.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) return;
        Location from = event.getFrom().clone();
        Location to = event.getTo().clone();
        updatePosition(player, from, to);
    }

    /**
     * Updates the position and view of the specified player by monitoring changes
     * in their location and orientation.
     *
     * @param player The player whose position is being updated.
     * @param from   The initial location of the player.
     * @param to     The new location of the player.
     */
    private void updatePosition(CPlayer player, Location from, Location to) {
        boolean changedView = from.getYaw() != to.getYaw() || from.getPitch() != to.getPitch();
        boolean changedPosition = from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ();
        updateMobs(player, to, changedPosition, changedView);
    }

    /**
     * Handles the event when a player joins the game. This method performs the following actions:
     * 1. Ensures that all relevant elements remain valid within the system.
     * 2. Retrieves the joined player and manages hidden player mobs.
     * 3. Creates a scoreboard team for hidden players and configures visibility settings.
     * 4. Updates the player's view of mobs based on visibility rules.
     *
     * @param event The {@link CorePlayerJoinedEvent} containing information about the player who joined.
     */
    @EventHandler
    public void onPlayerJoin(CorePlayerJoinedEvent event) {
        ensureAllValid();
        CPlayer player = event.getPlayer();
        //Create team for hidden players
        WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam();
        wrapper.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_CREATED);
        wrapper.setName(HIDDEN_TEAM);
        wrapper.setNameTagVisibility("never");
        wrapper.setPlayers(hiddenPlayerMobs);
        wrapper.sendPacket(player);
        updateMobs(player, null, true, false);
    }

    /**
     * Handles the event when a player quits the game.
     *
     * This method ensures that all entities within the entity reference collection
     * remain valid by performing cleanup operations. Additionally, it handles the
     * player's logout process by updating their interaction with entities, such as
     * removing visibility or viewers as necessary.
     *
     * @param event The {@link PlayerQuitEvent} representing the player quitting the server,
     *              containing information about the player who has left.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ensureAllValid();
        playerLogout(Core.getPlayerManager().getPlayer(event.getPlayer()));
    }

    /**
     * Handles the event triggered when a player is kicked from the server.
     *
     * This method ensures that all elements within the `entityRefs` collection
     * are valid by removing any invalid or garbage-collected entries prior
     * to handling the kick. Additionally, it processes the player's logout,
     * performing cleanup operations such as updating entity visibility and
     * removing the player as a viewer of any relevant entities.
     *
     * @param event The {@link PlayerKickEvent} containing information about the
     *              kicked player and the reason for their removal from the server.
     */
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        ensureAllValid();
        playerLogout(Core.getPlayerManager().getPlayer(event.getPlayer()));
    }

    /**
     * Handles the event triggered when a player respawns in the game.
     * This method ensures the validity of the entity references and updates the mobs
     * associated with the player based on the player's respawn location.
     *
     * @param event The {@link PlayerRespawnEvent} containing information about the player
     *              who has respawned, including their new spawn location.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ensureAllValid();
        updateMobs(Core.getPlayerManager().getPlayer(event.getPlayer()), null, true, false);
    }

    /**
     * Handles the event triggered when a player changes worlds.
     *
     * This method ensures the validity of all entities stored in the collection,
     * retrieves the player who changed worlds, and updates the visibility or
     * spawning state of associated entities based on the world transition. If an
     * entity needs to be visible to the player in the new world, it will be
     * spawned; otherwise, it will be despawned.
     *
     * @param event The {@link PlayerChangedWorldEvent} that contains information
     *              about the player who changed worlds, including the old and new
     *              worlds.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        ensureAllValid();
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        for (WeakReference<AbstractEntity> mobRef : entityRefs) {
            AbstractEntity mobNPC = mobRef.get();
            if (mobNPC == null || mobNPC.getVisibleTo().size() != 0 && MiscUtil.contains(mobNPC.getTargets(), event.getPlayer()))
                continue;
            if (mobNPC.getLocation().getWorld() == null) {
                if (event.getPlayer().getWorld().equals(mobNPC.getLocation().getWorld())) {
                    mobNPC.forceSpawn(player);
                } else {
                    mobNPC.forceDespawn(player);
                }
            }
        }
    }

    /**
     * Updates the state of mobs (Non-Player Characters) visible to a specific player,
     * based on their location and viewing conditions. This includes spawning or despawning
     * mobs within a defined render distance and managing their visibility on the tab list.
     *
     * @param player  The player whose view of the mobs is being updated.
     * @param loc     The location used as the reference point for visibility checks.
     *                If null, the player's current location is used.
     * @param spawn   Determines whether mobs should be spawned or despawned based on distance and visibility.
     * @param tabList Indicates whether mobs of type PLAYER should be managed for appearance
     *                on the tab list.
     */
    private void updateMobs(CPlayer player, Location loc, boolean spawn, boolean tabList) {
        if (loc == null) {
            loc = player.getLocation();
        }
        for (WeakReference<AbstractEntity> mobRef : entityRefs) {
            final AbstractEntity npcMob = mobRef.get();
            if (npcMob == null) continue;
            if (npcMob.isSpawned() && npcMob.canSee(player) && npcMob.sameWorld(player)) {
                if (spawn) {
                    boolean viewer = npcMob.isViewer(player);
                    double distance = loc.distance(npcMob.getLocation().getLocation());
                    if (distance <= RENDER_DISTANCE && !viewer) {
                        npcMob.forceSpawn(player);
                    } else if (distance > RENDER_DISTANCE && viewer) {
                        npcMob.forceDespawn(player);
                    }
                }
                if (!tabList || !npcMob.getEntityType().equals(EntityType.PLAYER) || !npcMob.isViewer(player))
                    continue;
                MobPlayer mobPlayer = (MobPlayer) npcMob;

                if (!mobPlayer.needsRemoveFromTabList(player)) continue;

                Vector mobLoc = mobPlayer.getLocation().getLocation().toVector();

                Location copy = loc.clone();
                copy.setDirection(mobLoc.subtract(copy.toVector()));
                float yaw = copy.getYaw();
                float playerYaw = loc.getYaw();
                if (yaw < 0) yaw += 360;
                if (playerYaw < 0) playerYaw += 360;
                float difference = Math.abs(playerYaw - yaw);
                if (difference <= 60) {
                    removeFromTabList(player, mobPlayer);
                }
            }
        }
    }

    /**
     * Handles the logout process for the specified player, ensuring that
     * they are properly removed as a viewer from any entities they were observing.
     *
     * This method iterates through the entity references, identifying entities
     * that are currently spawned and being viewed by the player. It then
     * removes the player as a viewer from those entities to maintain proper
     * synchronization and cleanup.
     *
     * @param player The player who is logging out and needs to be removed as a viewer
     *               from relevant entities.
     */
    private void playerLogout(CPlayer player) {
        for (WeakReference<AbstractEntity> mobRef : entityRefs) {
            final AbstractEntity npcMob = mobRef.get();
            if (npcMob != null && npcMob.isSpawned() && npcMob.isViewer(player)) {
                npcMob.removeViewer(player);
            }
        }
    }

    /**
     * Tracks a hidden player mob by adding it to the hidden player mob list
     * and associating it with a specific scoreboard team for visibility control.
     *
     * This method performs the following operations:
     * 1. Adds the custom name of the MobPlayer to the list of hidden player mobs.
     * 2. Configures a scoreboard team with the name defined in HIDDEN_TEAM and
     *    assigns the hidden player mob to this team.
     * 3. Sends the configured scoreboard team packet to all target players of
     *    the specified MobPlayer to manage visibility.
     *
     * @param mob The MobPlayer instance representing the hidden player mob
     *            to be tracked. Its custom name is used to manage visibility
     *            and scoreboard team assignment.
     */
    public void trackHiddenPlayerMob(MobPlayer mob) {
        hiddenPlayerMobs.add(mob.getCustomName());

        WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam();
        wrapper.setName(HIDDEN_TEAM);
        wrapper.setMode(WrapperPlayServerScoreboardTeam.Mode.PLAYERS_ADDED);
        wrapper.setPlayers(Collections.singletonList(mob.getCustomName()));

        Arrays.asList(mob.getTargets()).forEach(wrapper::sendPacket);
    }

    /**
     * Removes a hidden player mob from the visibility management system.
     *
     * This method performs the following operations:
     * 1. Removes the custom name of the specified `MobPlayer` from the list of hidden player mobs.
     * 2. Configures a scoreboard team packet to remove the hidden player mob from the team specified
     *    by `HIDDEN_TEAM`.
     * 3. Sends the configured packet to all target players of the specified `MobPlayer` to update
     *    their visibility of the mob.
     *
     * @param mob The `MobPlayer` instance representing the hidden player mob to be untracked.
     *            Its custom name is used to manage removal from the hidden players' list and
     *            scoreboard team.
     */
    public void untrackHiddenPlayerMob(MobPlayer mob) {
        hiddenPlayerMobs.remove(mob.getCustomName());

        WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam();
        wrapper.setName(HIDDEN_TEAM);
        wrapper.setMode(WrapperPlayServerScoreboardTeam.Mode.PLAYERS_REMOVED);
        wrapper.setPlayers(Collections.singletonList(mob.getCustomName()));

        Arrays.asList(mob.getTargets()).forEach(wrapper::sendPacket);
    }

    /**
     * Removes the specified MobPlayer from the tab list associated with the given CPlayer.
     * Updates the state of the MobPlayer and maintains an internal map of removed MobPlayers.
     *
     * @param player the CPlayer whose tab list is being updated
     * @param mob    the MobPlayer to be removed from the tab list
     */
    public void removeFromTabList(CPlayer player, MobPlayer mob) {
        mob.removedFromTabList(player.getUniqueId());
        List<MobPlayer> list;
        if (removeFromTabList.containsKey(player.getUniqueId())) {
            list = removeFromTabList.get(player.getUniqueId());
        } else {
            list = new ArrayList<>();
        }
        list.add(mob);
        removeFromTabList.put(player.getUniqueId(), list);
    }
}
