package network.palace.core.npc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.npc.status.Status;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.packets.server.entity.*;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * Represents an abstract entity in the system, providing a foundation for defining
 * custom entities with various properties and behaviors.
 *
 * This abstract class manages the visibility, location, interactions, and other
 * characteristics of an entity, while offering utility methods to handle its state
 * and interactions with players or observers.
 *
 * Subclasses must implement the appropriate details for specific entity types.
 */
public abstract class AbstractEntity implements Observable<NPCObserver> {
    /**
     * Represents the location of the entity in the virtual environment.
     * It is used to determine the current position of the entity.
     * The location data is managed and updated as the entity moves or interacts
     * in the environment.
     */
    @Getter protected Point location;

    /**
     * Represents a set of players (CPlayer) that an entity is explicitly marked as visible to.
     *
     * This variable is used to manage and track which players are able to view a specific
     * entity, independent of world or other visibility mechanics.
     *
     * It is primarily utilized within the context of entity rendering updates, and can be
     * modified through various entity management methods, such as adding or removing visibility
     * for specific players.
     *
     * This field is immutable in its declaration, but its content is mutable, allowing
     * dynamic updates to the Set based on game logic.
     */
    protected final Set<CPlayer> visibleTo;

    /**
     * A protected final set containing NPCObserver instances associated with this entity.
     * Observers are objects that listen for interactions with the entity, such as player interactions.
     *
     * This set is used to register, unregister, and manage observers for this entity.
     * It ensures that all registered observers are notified of relevant events.
     *
     * Modifications to this set should only be made using the provided methods in the class.
     */
    protected final Set<NPCObserver> observers;

    /**
     * Represents a set of players who are considered viewers of this entity.
     * These are the players to whom the entity is rendered or visible.
     *
     * This variable is protected and marked as final, indicating that its
     * content can be modified but the reference cannot be reassigned to a new set.
     *
     * The viewers are managed internally by various methods, enabling fine-grained
     * control over visibility and interaction with the entity for specific players.
     */
    protected final Set<CPlayer> viewers;

    /**
     * Represents a wrapped data watcher instance used to track and handle entity metadata changes in the game.
     * The {@code dataWatcher} is utilized internally by the entity to synchronize visual and behavioral
     * attributes (e.g., pose, status effects, animations) with its observers or viewers.
     *
     * This field is marked as {@code final} to indicate that the wrapped data watcher instance
     * is immutable after creation. It is maintained and updated during the entity's lifecycle.
     */
    @Getter protected final WrappedDataWatcher dataWatcher;

    /**
     * Represents the last known {@code WrappedDataWatcher} state of the entity.
     * This object is used to store and manage metadata values associated
     * with the entity, such as health, display name, or other entity-specific attributes.
     * It is primarily utilized for synchronization and updates between
     * the server and client, ensuring that the clients view the correct
     * entity state.
     *
     * This field is protected to allow subclasses of {@code AbstractEntity}
     * to access and manipulate the data as needed during entity state changes.
     */
    protected WrappedDataWatcher lastDataWatcher;
    //    private List<WrappedWatchableObject> lastDataWatcherObjects;

    /**
     * Indicates whether the entity has been spawned in the game world.
     *
     * This field is used to track the spawn state of the entity. It is set
     * to {@code true} when the entity has been successfully spawned and
     * is present in the game world. Otherwise, it remains {@code false}.
     *
     * This flag is essential for managing the entity's lifecycle, including
     * operations like updating, despawning, and tracking its state for
     * observers or interacting players.
     */
    @Getter protected boolean spawned;

    /**
     * Represents a unique identifier for the entity within the game world.
     * This ID is used to distinguish the entity from other entities and ensure
     * proper interactions and tracking during gameplay.
     *
     * The value is generated uniquely to avoid duplicates, and it is immutable
     * once assigned to an entity. It is critical for functionalities such as
     * spawning, despawning, and network packet communication related to this entity.
     */
    @Getter protected final int entityId;

    /**
     * Represents the yaw rotation of the entity's head.
     * In degrees, this value determines the horizontal facing direction
     * of the entity's head, independent of its body rotation.
     */
    @Getter private int headYaw;

    /**
     * Represents a protected instance of {@code AbstractEntity.InteractWatcher} associated with this entity.
     *
     * This field is an implementation of a packet adapter designed to listen for player interactions
     * with the entity. It intercepts entity interaction packets, processes them to determine the type
     * of interaction (e.g., right-click, left-click), and notifies all registered {@code NPCObserver}
     * instances about the interaction.
     *
     * The {@code listener} is instantiated and linked to the current instance of {@code AbstractEntity}.
     * It ensures that any interaction targeting the specific entityID of this entity is captured and handled.
     *
     * Developers can extend or modify the behavior of this watcher by customizing the interaction handling
     * logic in the {@code InteractWatcher} class.
     */
    protected AbstractEntity.InteractWatcher listener;

    /**
     * Represents the list of passengers currently associated with the entity.
     * Passengers are other entities or objects riding or attached to this entity.
     *
     * This array is initialized to an empty state by default and can be modified
     * during the lifecycle of the entity to add or remove passengers as needed.
     */
    private int[] passengers = new int[0];

    /**
     * Indicates whether this abstract entity is on fire.
     *
     * When set to {@code true}, the entity will visually appear as burning,
     * which may affect gameplay mechanics, such as taking damage over time
     * or interacting with other entities and environmental elements.
     *
     * This property can be toggled on or off as needed to reflect the current
     * state of the entity in the game world.
     */
    @Getter @Setter private boolean onFire;

    /**
     * Indicates whether the entity is currently in a crouching state.
     * The crouching state may affect the entity's behavior or appearance,
     * such as its hitbox size or movement speed.
     *
     * This property is typically used to reflect or manipulate the crouching
     * status of the entity within the game world.
     */
    @Getter @Setter private boolean crouched;

    /**
     * Represents the sprinting state of an entity.
     *
     * When the value is {@code true}, the entity is considered to be sprinting.
     * When {@code false}, the entity is not sprinting.
     */
    @Getter @Setter private boolean sprinting;

    /**
     * Indicates whether the entity is visible in the game world.
     * The visibility of the entity can be toggled to control
     * whether it is rendered or interactable by players.
     *
     * When set to {@code true}, the entity is visible; otherwise, it is invisible.
     */
    @Getter @Setter private boolean visible = true;

    /**
     * Indicates whether the custom name of the entity is visible to players.
     * When set to {@code true}, the custom name of the entity will be displayed.
     * When set to {@code false}, the custom name will remain hidden.
     */
    @Getter @Setter private boolean customNameVisible;

    /**
     * Represents the gravity state of an entity.
     * This property determines whether the entity is affected by gravity
     * in the game world. When set to {@code true}, the entity will
     * behave normally under gravity's effects. If set to {@code false},
     * the entity will not be influenced by gravitational forces,
     * allowing it to float or remain stationary in the air.
     */
    @Getter @Setter private boolean gravity = true;

    /**
     * Represents an optional, context-dependent name for an entity.
     * The conditional name allows the entity to dynamically reflect different names
     * for different players based on game logic or specific conditions.
     *
     * This variable is utilized to dynamically provide a customized name for the entity
     * when retrieving the name through context-specific logic implemented in {@link ConditionalName}.
     * If set, this takes precedence over static naming conventions.
     *
     * In cases where the entity has no assigned conditional name,
     * fallback to other name-retrieval logic might occur.
     *
     * @see ConditionalName
     * @see AbstractEntity#setConditionalName(ConditionalName)
     * @see AbstractEntity#hasConditionalName()
     */
    protected ConditionalName conditionalName;

    /**
     * Represents a customizable name for the entity.
     * This field stores a specific name that can be assigned to the entity, allowing it
     * to have a unique identifier or display name different from its default behavior
     * or entity type. It may be displayed to players when interacting with or observing
     * the entity, depending on its visibility settings.
     */
    @Setter protected String customName;

    /**
     * The universally unique identifier (UUID) associated with this entity.
     * This unique identifier is used to distinguish the entity instance
     * across systems and ensure that it remains identifiable, even if other
     * properties of the entity change over time.
     *
     * This field is protected, allowing subclasses to access or modify it
     * when necessary. The getters and setters for this field are automatically
     * generated to provide controlled access to the UUID value.
     *
     * @see UUID
     */
    @Getter @Setter protected UUID uuid;

    /**
     * Creates a new AbstractEntity.
     *
     * @param location the initial location of the entity, cannot be null
     * @param observers the set of players who can observe this entity, can be null
     * @param title the custom name of the entity, can be null
     */
    public AbstractEntity(Point location, Set<CPlayer> observers, String title) {
        this.location = location.deepCopy();
        this.visibleTo = new HashSet<>();
        if (observers != null) this.visibleTo.addAll(observers);
        this.dataWatcher = new WrappedDataWatcher();
        this.observers = new HashSet<>();
        this.viewers = new HashSet<>();
        this.spawned = false;
        if (title == null) {
            this.customName = "";
        } else {
            this.customName = title;
        }
        this.entityId = Core.getSoftNPCManager().getIDManager().getNextID();
    }

    {
        Core.getSoftNPCManager().getEntityRefs().add(new WeakReference<>(this));
    }

    /**
     * Handles updates related to the entity's state or behavior.
     * This method is invoked during the entity's update lifecycle and
     * can be utilized to implement custom update logic, including
     * tasks such as synchronizing entity properties, managing interactions,
     * or updating visual and behavioral aspects of the entity.
     *
     * Subclasses should override this method to provide specific
     * update functionality as required.
     */
    protected void onUpdate() {
    }

    /**
     * Handles updates to the data watcher associated with the entity.
     *
     * This method is called whenever the data watcher for the entity
     * requires synchronization or changes. Subclasses can override this method
     * to perform specific updates or adjustments to the entity's data watcher,
     * ensuring that the entity's state is correctly represented to observers.
     *
     * Common use cases may include updating attributes like health, status effects,
     * or visual properties.
     */
    protected void onDataWatcherUpdate() {
    }

    /**
     * Retrieves the type of the entity represented by this class.
     * This method is abstract and must be implemented by subclasses
     * to return the appropriate {@code EntityType} corresponding to
     * the specific entity.
     *
     * @return the {@code EntityType} of the entity
     */
    protected abstract EntityType getEntityType();

    /**
     * Creates a new instance of {@link InteractWatcher} for observing and handling
     * interactions with this entity.
     *
     * The created InteractWatcher will monitor interaction packets and allow custom
     * logic to be executed when players interact with the entity. It is used to manage
     * player interactions such as clicks on the entity and invokes appropriate
     * interaction handlers for observers.
     *
     * @return a new instance of {@code InteractWatcher} linked to this entity
     */
    private InteractWatcher createNewInteractWatcher() {
        return listener = new InteractWatcher(this);
    }

    /**
     * Registers a new observer for this entity. Observers are notified
     * of specific interactions or updates related to the entity.
     *
     * @param observer the observer to register, cannot be null
     */
    @Override
    public final void registerObservable(NPCObserver observer) {
        this.observers.add(observer);
    }

    /**
     * Unregisters a previously registered observer for this entity.
     * Observers will no longer receive updates or interactions related to the entity.
     *
     * @param observer the observer to unregister, cannot be null
     */
    @Override
    public final void unregisterObservable(NPCObserver observer) {
        this.observers.remove(observer);
    }

    /**
     * Retrieves an immutable set of NPC observers currently observing this entity.
     * The observers are entities or players that are monitoring updates or interactions related to this entity.
     *
     * @return an immutable set of NPC observers
     */
    @Override
    public final ImmutableSet<NPCObserver> getObservers() {
        return ImmutableSet.copyOf(observers);
    }

    /**
     * Adds the specified player to the list of players who can see this entity.
     * If the entity is already spawned, it forces the entity to be visible to the player immediately.
     *
     * @param player the player to add to the visibility list, cannot be null
     */
    public final void addVisibleTo(CPlayer player) {
        this.visibleTo.add(player);
        if (this.isSpawned()) forceSpawn(player);
    }

    /**
     * Removes the specified player from the list of players who can see this entity.
     * If the entity is currently spawned, it forces the entity to be despawned for the player.
     *
     * @param player the player to remove from the visibility list, cannot be null
     */
    public final void removeVisibleTo(CPlayer player) {
        this.visibleTo.remove(player);
        if (this.isSpawned()) forceDespawn(player);
    }

    /**
     * Adds the specified player to the list of viewers for this entity.
     *
     * @param player the player to be added as a viewer, should not be null
     */
    protected void addViewer(CPlayer player) {
        this.viewers.add(player);
    }

    /**
     * Removes the specified player from the list of viewers of this entity.
     * The player will no longer be able to view or interact with the entity
     * as a viewer.
     *
     * @param player the player to be removed from the list of viewers, should not be null
     */
    protected void removeViewer(CPlayer player) {
        this.viewers.remove(player);
    }

    /**
     * Determines whether the specified player is a viewer of this entity.
     *
     * @param player the player to check, must not be null
     * @return {@code true} if the player is a viewer of this entity;
     *         {@code false} otherwise
     */
    protected boolean isViewer(CPlayer player) {
        return this.viewers.contains(player);
    }

    /**
     * Makes the entity globally visible to all eligible players by clearing
     * the list of specific players who were previously allowed to see it.
     *
     * This method updates the entity's visibility state, effectively
     * removing any restrictions on which players can view the entity.
     * Once invoked, the entity becomes visible to all players in the same world
     * and within render distance, provided no additional visibility rules are applied elsewhere.
     */
    public final void makeGlobal() {
        this.visibleTo.clear();
    }

    /**
     * Retrieves an array of target players that this entity is visible to.
     * The method ensures that only players located in the same world as the entity
     * are included in the result. If the entity's visibility list is empty, all
     * online players are considered.
     *
     * @return an array of {@code CPlayer} instances representing the target players
     *         this entity is visible to. If no players meet the visibility criteria,
     *         an empty array is returned.
     */
    protected final CPlayer[] getTargets() {
        CPlayer[] cPlayers = (this.visibleTo.size() == 0 ? Core.getPlayerManager().getOnlinePlayers() : this.visibleTo).toArray(new CPlayer[this.visibleTo.size()]);
        CPlayer[] players = new CPlayer[cPlayers.length];
        int x = 0;

        for (CPlayer player : cPlayers) {
            UUID uid = player.getLocation().getWorld().getUID();
            UUID uid1 = this.location.getWorld() != null ? location.getWorld().getUID() : null;

            if (this.location.getWorld() != null && !uid.equals(uid1)) continue;
            players[x] = player;
            x++;
        }
        return x == 0 ? new CPlayer[]{} : Arrays.copyOfRange(players, 0, x);
    }

    /**
     * Spawns the entity, making it visible and interactive for observers.
     *
     * This method ensures that the entity is only spawned once by checking the
     * {@code spawned} field. If the entity has already been spawned, the method
     * exits without making any changes. Otherwise, it performs the following actions:
     *
     * 1. Registers a packet listener using the {@link ProtocolManager}, enabling
     *    interaction tracking via a newly created {@link InteractWatcher}.
     * 2. Marks the entity as spawned by setting the {@code spawned} field to {@code true}.
     * 3. Retrieves the list of target players who can see the entity using {@link #getTargets()}.
     * 4. Forces the entity to be visible to these players using {@link #forceSpawn(CPlayer, boolean)}.
     * 5. Updates the state of the entity for these players by invoking {@link #update(CPlayer[], boolean)}.
     */
    public void spawn() {
        if (spawned) return;
        ProtocolLibrary.getProtocolManager().addPacketListener(createNewInteractWatcher());
        spawned = true;
        CPlayer[] targets = getTargets();
        Arrays.asList(targets).forEach(t -> this.forceSpawn(t, false));
        update(targets, true);
    }

    /**
     * Constructs and returns a new {@code WrapperPlayServerEntityDestroy} packet
     * for despawning the entity. The packet includes the entity's unique identifier
     * to ensure the correct entity is targeted for destruction.
     *
     * @return a {@code WrapperPlayServerEntityDestroy} instance configured
     *         to despawn the entity with the specified entity ID.
     */
    private WrapperPlayServerEntityDestroy getDespawnPacket() {
        WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
        packet.setEntityIds(new int[]{entityId});
        return packet;
    }

    /**
     * Despawns the entity, making it no longer visible or interactive to any players.
     *
     * This method will execute only if the entity is currently spawned, as determined
     * by the {@code spawned} field. If the entity is not spawned, the method immediately
     * exits without performing any action.
     *
     * The despawn process involves:
     * 1. Removing the packet listener tied to the entity, which was previously registered
     *    via the {@link ProtocolLibrary}'s ProtocolManager.
     * 2. Nullifying the {@code listener} field to release the reference.
     * 3. Setting the {@code spawned} field to {@code false} to mark the entity as no longer spawned.
     * 4. Iterating over the list of target players retrieved via {@link #getTargets()} and calling
     *    {@link #forceDespawn(CPlayer)} for each player to ensure the entity is forcibly despawned.
     */
    public void despawn() {
        if (!spawned) return;
        ProtocolLibrary.getProtocolManager().removePacketListener(listener);
        listener = null;
        spawned = false;
        Arrays.asList(getTargets()).forEach(this::forceDespawn);
    }

    /**
     * Forces the despawn of a specified player by sending a despawn packet
     * and removing the player from the viewer list.
     *
     * @param player the player to be forcibly despawned if they are a viewer
     */
    public final void forceDespawn(CPlayer player) {
        if (!isViewer(player)) {
            return;
        }
        getDespawnPacket().sendPacket(player);
        removeViewer(player);
    }

    /**
     * Forces the spawning of a player in the game world.
     *
     * @param player The player to be forcibly spawned.
     */
    public final void forceSpawn(CPlayer player) {
        forceSpawn(player, true);
    }

    /**
     * Forces the spawning of an object or entity for a specified player.
     * If the player is not already a viewer, it sends the spawn packet
     * and adds the player as a viewer. Optionally updates the entity
     * state for the player.
     *
     * @param player the player for whom the entity is being spawned
     * @param update whether to update the entity state after spawning
     */
    public void forceSpawn(CPlayer player, boolean update) {
        if (!isViewer(player)) {
            getSpawnPacket().sendPacket(player);
            addViewer(player);
        }

        if (update) update(new CPlayer[]{player}, true);
    }

    /**
     * Retrieves the spawn packet associated with the entity.
     *
     * @return an AbstractPacket representing the spawn packet for the entity.
     */
    protected abstract AbstractPacket getSpawnPacket();

    /**
     * Creates and returns a WrapperPlayServerEntityStatus packet with the specified status.
     *
     * @param status the status code to be set for the entity.
     * @return a WrapperPlayServerEntityStatus packet with the specified entity ID and status.
     */
    private WrapperPlayServerEntityStatus getStatusPacket(int status) {
        WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
        packet.setEntityID(entityId);
        packet.setEntityStatus(status);
        return packet;
    }

    /**
     * Updates the play status for a set of players by sending the appropriate status packet.
     *
     * @param players A set of players to whom the status update will be sent.
     * @param status  The status value to be sent to the players.
     */
    protected final void playStatus(Set<CPlayer> players, int status) {
        players.forEach(getStatusPacket(status)::sendPacket);
    }

    /**
     * Sends a status packet to all targets, indicating the provided status.
     *
     * @param status the status code to broadcast to the targets
     */
    protected final void playStatus(int status) {
        Arrays.asList(getTargets()).forEach(getStatusPacket(status)::sendPacket);
    }

    /**
     * Triggers the display of the hurt animation for the entity.
     * This method plays the animation associated with the entity being hurt
     * by changing its status to the ENTITY_HURT state.
     */
    public final void playHurtAnimation() {
        playStatus(Status.ENTITY_HURT);
    }

    /**
     * Triggers the dead animation for the entity.
     * This method sets the status of the entity to reflect
     * that it is in a "dead" state and plays the corresponding animation.
     */
    public final void playDeadAnimation() {
        playStatus(Status.ENTITY_DEAD);
    }

    /**
     * Plays the hurt animation for a set of players by updating their status to represent that they have been hurt.
     *
     * @param players a set of {@code CPlayer} objects representing the players who should play the hurt animation
     */
    public final void playHurtAnimation(Set<CPlayer> players) {
        playStatus(players, Status.ENTITY_HURT);
    }

    /**
     * Triggers and executes the 'dead' animation for a given set of players.
     *
     * @param players the set of players for whom the dead animation should be played
     */
    public final void playDeadAnimation(Set<CPlayer> players) {
        playStatus(players, Status.ENTITY_DEAD);
    }

    /**
     * Updates the state or properties associated with the current instance.
     * This method provides a shorthand way to invoke an update operation
     * with a default configuration or behavior.
     *
     * The method internally delegates the operation to another overloaded
     * {@code update} method with a predefined argument.
     */
    public final void update() {
        update(false);
    }

    /**
     * Updates the state based on the current targets and a spawn flag.
     *
     * @param spawn a boolean flag indicating whether a spawn action should occur
     */
    public final void update(boolean spawn) {
        update(getTargets(), spawn);
    }

    /**
     * Updates the state of an entity and sends corresponding packet updates to the specified targets.
     *
     * @param targets an array of CPlayer instances to which the entity update packets will be sent
     * @param spawn a boolean flag indicating whether the entity should be spawned if not already spawned
     */
    public final void update(CPlayer[] targets, boolean spawn) {
        try {
            if (!spawned) spawn();
            updateDataWatcher();
            List<WrappedWatchableObject> watchableObjects = new ArrayList<>();

            if (spawn) {
                watchableObjects.addAll(dataWatcher.getWatchableObjects());
            } else {
                if (lastDataWatcher == null) {
                    watchableObjects.addAll(dataWatcher.getWatchableObjects());
                } else {
                    for (WrappedWatchableObject watchableObject : dataWatcher.getWatchableObjects()) {
                        Object object = lastDataWatcher.getObject(watchableObject.getIndex());
                        if (object == null || !object.equals(watchableObject.getValue())) {
                            watchableObjects.add(watchableObject);
                        }
                    }
                }
            }

            if (hasConditionalName()) {
                for (CPlayer target : targets) {
                    WrapperPlayServerEntityMetadata localPacket = new WrapperPlayServerEntityMetadata();
                    for (WrappedWatchableObject watchableObject : watchableObjects) {
                        if (watchableObject.getIndex() == 2) {
                            watchableObject.setValue(conditionalName.getCustomName(target));
                        }
                    }
                    localPacket.setMetadata(watchableObjects);
                    localPacket.setEntityID(entityId);
                    localPacket.sendPacket(target);
                }
            } else {
                WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
                packet.setMetadata(watchableObjects);
                packet.setEntityID(entityId);
                Arrays.asList(targets).forEach(packet::sendPacket);
                lastDataWatcher = deepClone(dataWatcher);
            }

            if (passengers.length > 0) {
                WrapperPlayServerMount packet = new WrapperPlayServerMount();
                packet.setEntityID(entityId);
                packet.setPassengerIds(passengers);
                for (CPlayer target : targets) {
                    packet.sendPacket(target);
                }
            }

            onUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a deep clone of the given WrappedDataWatcher instance.
     * This method ensures all watchable objects are copied into a new WrappedDataWatcher.
     *
     * @param dataWatcher the WrappedDataWatcher instance to be cloned
     * @return a new instance of WrappedDataWatcher containing a deep copy of the data from the provided instance
     */
    private WrappedDataWatcher deepClone(WrappedDataWatcher dataWatcher) {
        WrappedDataWatcher clone = new WrappedDataWatcher();
        if (MinecraftReflection.watcherObjectExists()) {
            dataWatcher.getWatchableObjects().forEach(object -> clone.setObject(object.getWatcherObject(), object));
        } else {
            dataWatcher.getWatchableObjects().forEach(object -> clone.setObject(object.getIndex(), object));
        }
        return clone;
    }

    /**
     * Moves an object to the specified location represented by the given point.
     *
     * @param point the target location to which the object should be moved.
     */
    public final void move(Point point) {
        move(point, false);
    }

    /**
     * Moves an entity to a specified location, either absolutely or relatively, and sends the corresponding update packets to the targets.
     *
     * @param point The target point to move to. If the movement is relative, this point represents the offsets from the current location.
     * @param relative A boolean indicating whether the movement is relative to the current position (true) or an absolute position (false).
     * @throws IllegalStateException If the entity has not been spawned yet.
     */
    public final void move(Point point, boolean relative) {
        if (!spawned) throw new IllegalStateException("You cannot teleport something that hasn't spawned yet!");
        final Point oldLocation = this.location;
        final Point newLocation = relative ? this.location.deepCopy().add(point) : point;
        AbstractPacket packet;
        if (oldLocation.distanceSquared(newLocation) <= 16) {
            WrapperPlayServerRelEntityMoveLook moveLookPacket = new WrapperPlayServerRelEntityMoveLook();
            moveLookPacket.setEntityID(entityId);
            moveLookPacket.setDx(newLocation.getX() - oldLocation.getX());
            moveLookPacket.setDy(newLocation.getY() - oldLocation.getY());
            moveLookPacket.setDz(newLocation.getZ() - oldLocation.getZ());
            moveLookPacket.setPitch(newLocation.getPitch());
            moveLookPacket.setYaw(newLocation.getYaw());
            moveLookPacket.setOnGround(true);
            packet = moveLookPacket;
        } else {
            WrapperPlayServerEntityTeleport packet1 = new WrapperPlayServerEntityTeleport();
            packet1.setEntityID(entityId);
            packet1.setX(newLocation.getX());
            packet1.setY(newLocation.getY());
            packet1.setZ(newLocation.getZ());
            packet1.setPitch(newLocation.getPitch());
            packet1.setYaw(newLocation.getYaw());
            packet = packet1;
        }
        Arrays.asList(getTargets()).forEach(packet::sendPacket);
        this.location = newLocation;
    }

    /**
     * Adds velocity to an entity by sending a velocity packet to all targets.
     *
     * @param vector the vector defining the velocity to be applied. It includes X, Y, and Z components.
     */
    public final void addVelocity(Vector vector) {
        WrapperPlayServerEntityVelocity packet = new WrapperPlayServerEntityVelocity();
        packet.setEntityID(entityId);
        packet.setVelocityX(vector.getX());
        packet.setVelocityY(vector.getY());
        packet.setVelocityZ(vector.getZ());
        Arrays.asList(getTargets()).forEach(packet::sendPacket);
    }

    /**
     * Updates the internal data watcher with the entity's current state.
     * This method synchronizes entity properties such as custom name, visibility,
     * gravity, and status flags (e.g., on fire, crouching, sprinting) to the data watcher.
     *
     * Key updates:
     * - The `customNameVisible` property determines if the custom name tag is shown.
     * - The `customName` property, if non-null, is added to the data watcher,
     *   truncated to a maximum length of 64 characters.
     * - The `gravity` property controls the no-gravity flag.
     * - Status flags such as on fire, crouching, sprinting, and visibility are
     *   packed into a byte and set in the data watcher.
     *
     * The method ensures that outdated or null values are removed from the data watcher.
     * After changes are made to the data watcher, the `onDataWatcherUpdate` method
     * is called to signal that an update occurred.
     */
    protected final void updateDataWatcher() {
        int metadataIndex = 0;
        int customNameIndex = 2;
        int showNameTagIndex = 3;
        int noGravityIndex = 5;
        if (customNameVisible) {
            dataWatcher.setObject(ProtocolLibSerializers.getBoolean(showNameTagIndex), true);
        } else if (dataWatcher.getObject(showNameTagIndex) != null) {
            dataWatcher.remove(showNameTagIndex);
        }
        if (customName != null) {
            dataWatcher.setObject(ProtocolLibSerializers.getString(customNameIndex), customName.substring(0, Math.min(customName.length(), 64)));
        } else if (dataWatcher.getObject(customNameIndex) != null) {
            dataWatcher.remove(customNameIndex);
        }
        if (!gravity) {
            dataWatcher.setObject(ProtocolLibSerializers.getBoolean(noGravityIndex), true);
        } else if (dataWatcher.getObject(noGravityIndex) != null) {
            dataWatcher.remove(noGravityIndex);
        }
        byte zeroByte = 0;
        if (onFire) zeroByte |= 0x01;
        if (crouched) zeroByte |= 0x02;
        if (sprinting) zeroByte |= 0x08;
        if (!visible) zeroByte |= 0x20;
        dataWatcher.setObject(ProtocolLibSerializers.getByte(metadataIndex), zeroByte);
        onDataWatcherUpdate();
    }

    /**
     * Sets the head yaw rotation of the entity based on the given location.
     *
     * @param location the Location object whose yaw value will be used
     *                 to set the head rotation of the entity
     * @throws IllegalStateException if the entity is not spawned
     */
    public final void setHeadYaw(Location location) {
        if (!spawned)
            throw new IllegalStateException("You cannot modify the rotation of the head of a non-spawned entity!");
        headYaw = (int) location.getYaw();
        sendYaw(getTargets());
    }

    /**
     * Sends the yaw rotation packet to a list of players.
     *
     * @param players an array of CPlayer objects to whom the head yaw rotation packet will be sent
     */
    public final void sendYaw(CPlayer[] players) {
        WrapperPlayServerEntityHeadRotation packet = new WrapperPlayServerEntityHeadRotation();
        packet.setEntityID(entityId);
        packet.setHeadYaw(headYaw);
        Arrays.asList(players).forEach(packet::sendPacket);
        Entity e;
    }

    /**
     * Retrieves the set of players to whom this object is visible.
     *
     * @return an immutable set of CPlayer objects representing the players that can see this object
     */
    public final ImmutableSet<CPlayer> getVisibleTo() {
        return ImmutableSet.copyOf(visibleTo);
    }

    /**
     * Determines whether the specified player is visible.
     *
     * @param player the player to check for visibility
     * @return true if the player is visible, false otherwise
     */
    public boolean canSee(CPlayer player) {
        if (visibleTo.isEmpty()) {
            return true;
        }
        return visibleTo.contains(player);
    }

    /**
     * Checks if the world of this object's location is the same as the world of the specified player's location.
     *
     * @param player the player whose world is to be compared
     * @return true if the world of this object's location matches the player's world, false otherwise
     */
    public boolean sameWorld(CPlayer player) {
        return location.getWorld().equals(player.getWorld());
    }

    /**
     * Determines if there are any passengers.
     *
     * @return true if there are passengers, false otherwise
     */
    public boolean hasPassengers() {
        return passengers.length > 0;
    }

    /**
     * Retrieves the number of passengers.
     *
     * @return the total count of passengers.
     */
    public int getPassengerCount() {
        return passengers.length;
    }

    /**
     * Adds a passenger to the existing list of passengers by including the entity's ID.
     *
     * @param entity an AbstractEntity object representing the passenger to be added.
     *               The passenger's unique identifier is retrieved via the entity's ID.
     */
    public void addPassenger(AbstractEntity entity) {
        int[] newArray = new int[passengers.length + 1];
        int i = 0;
        for (int p : passengers) {
            newArray[i++] = p;
        }
        newArray[i] = entity.getEntityId();
        passengers = newArray;
        update(false);
    }

    /**
     * Removes a passenger identified by the given entity from the passengers array.
     *
     * @param entity the entity whose ID is to be removed from the passengers array
     */
    public void removePassenger(AbstractEntity entity) {
        if (passengers.length < 1) return;
        int[] newArray = new int[passengers.length - 1];
        int i = 0;
        for (int p : passengers) {
            if (p == entity.getEntityId()) continue;
            if (i >= newArray.length) return;
            newArray[i++] = p;
        }
        passengers = newArray;
        update(false);
    }

    /**
     * Retrieves a custom name. If no specific context is provided, it may return a default or pre-determined value.
     *
     * @return the custom name as a String, potentially modified or defaulted based on internal logic.
     */
    public String getCustomName() {
        return getCustomName(null);
    }

    /**
     * Retrieves the custom name for the specified player. If the player is null,
     * the conditional name is null, or the conditional name for the player is null,
     * it will return the default custom name.
     *
     * @param player the player for whom the custom name is to be retrieved
     * @return the custom name for the player or the default custom name if conditions are not met
     */
    public String getCustomName(CPlayer player) {
        if (player == null || this.conditionalName == null || conditionalName.getCustomName(player) == null) {
            return customName;
        } else {
            return conditionalName.getCustomName(player);
        }
    }

    /**
     * Sets the conditional name for this object.
     *
     * @param conditionalName the ConditionalName object to set
     */
    public void setConditionalName(ConditionalName conditionalName) {
        this.conditionalName = conditionalName;
    }

    /**
     * Checks if the object has a conditional name set.
     *
     * @return true if the conditionalName field is not null, false otherwise.
     */
    public boolean hasConditionalName() {
        return this.conditionalName != null;
    }

    /**
     * InteractWatcher is a specialized PacketAdapter that listens for specific
     * interaction packets related to a specified AbstractEntity. It intercepts
     * client packets to detect interactions with the targeted entity and notifies
     * all observers of the entity about the interaction event.
     *
     * Upon packet reception, this class checks if the packet's target matches
     * the specified entity. If the interaction is valid, it determines the type
     * of interaction (e.g., left-click, right-click) and notifies all relevant
     * NPCObserver instances associated with the watched entity. The packet is then
     * canceled to prevent further processing by the server.
     *
     * This class is primarily used to handle interactions with non-player
     * characters (NPCs) and abstract entities in a controlled manner while ensuring
     * interaction events are propagated to custom observers.
     */
    protected static class InteractWatcher extends PacketAdapter {

        private final AbstractEntity watchingFor;

        public InteractWatcher(AbstractEntity watchingFor) {
            super(Core.getInstance(), PacketType.Play.Client.USE_ENTITY);
            this.watchingFor = watchingFor;
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());
            if (packet.getTargetID() != watchingFor.getEntityId()) return;
            CPlayer onlinePlayer = Core.getPlayerManager().getPlayer(event.getPlayer());
            ClickAction clickAction = ClickAction.from(packet.getType().name());
            if (clickAction != null) {
                for (NPCObserver npcObserver : watchingFor.getObservers()) {
                    try {
                        npcObserver.onPlayerInteract(onlinePlayer, watchingFor, clickAction);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            event.setCancelled(true);
        }
    }
}
