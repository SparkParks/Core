package network.palace.core.npc;

import lombok.Setter;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.packets.server.entity.WrapperPlayServerSpawnEntityLiving;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;

import java.util.Set;

/**
 * Represents an abstract implementation of a mob entity, extending from {@link AbstractEntity}.
 * This class provides the core properties and behaviors shared by all mobs in the system,
 * including health management, spawning mechanics, and data watcher updates.
 * Subclasses are expected to define specific mob attributes and behavior.
 *
 * Fields:
 * - health: The current health of the mob. Defaults to 0 and is clamped to the mob's maximum health.
 *
 * Abstract Methods:
 * - {@code float getMaximumHealth()}: Must be implemented by subclasses to return the maximum health
 *   value for the specific mob.
 *
 * Constructor Parameters:
 * - location: The initial spawn location of the mob in the game world.
 * - observers: A set of players observing this mob.
 * - title: The name or identifier of the mob.
 *
 * Key Methods:
 * - {@code Float getHealth()}: Retrieves the current health of the mob. If the health is 0, it defaults
 *   to the maximum health. The returned value is clamped to the mob's maximum health if needed.
 *
 * - {@code AbstractPacket getSpawnPacket()}: Creates a packet with the necessary information to spawn
 *   the mob in the game world. Includes attributes like position, rotation, and metadata.
 *
 * - {@code void onDataWatcherUpdate()}: Updates the mob's data watcher with its current health state.
 *   Synchronizes this information with any observing players.
 *
 * Developers extending this class should implement {@code getMaximumHealth()} to define the mob's
 * maximum health and may override other methods to customize behaviors such as spawning or data
 * watcher updates.
 */
public abstract class AbstractMob extends AbstractEntity {
    /**
     * Represents the current health of the mob.
     *
     * The health value is initialized to 0 by default and may be dynamically
     * updated throughout the mob's lifecycle. It typically indicates the
     * mob's remaining vitality in the game world. The value is clamped
     * to the mob's maximum health as defined by the implementing subclass.
     * If set to 0, the health is treated as a placeholder and defaults to
     * `getMaximumHealth()` when retrieved.
     *
     * Modifying this field may trigger updates to the data watcher mechanism,
     * ensuring that changes to the mob's health are synchronized with
     * observing players.
     */
    @Setter private float health = 0;

    /**
     * Retrieves the maximum health of this abstract entity.
     *
     * @return the maximum health value as a float, representing the upper limit
     *         of this entity's health capacity.
     */
    public abstract float getMaximumHealth();

    /**
     * Constructs an instance of AbstractMob with a specified location, a set of observing players,
     * and a title for the entity.
     *
     * @param location the initial location of the AbstractMob, represented as a Point object.
     * @param observers a set of CPlayer objects representing the players observing this AbstractMob.
     * @param title the title or name associated with this AbstractMob.
     */
    public AbstractMob(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    /**
     * Retrieves the current health of this entity.
     * If the current health value is zero, this method returns the entity's maximum health.
     * Otherwise, it returns the lesser of the maximum health and the current health value.
     *
     * @return the current health of the entity as a Float, or the maximum health if the current health is zero.
     */
    public final Float getHealth() {
        return health == 0 ? getMaximumHealth() : Math.min(getMaximumHealth(), health);
    }

    /**
     * Constructs and returns the spawn packet for this entity.
     * The spawn packet contains information such as the entity's ID, location,
     * orientation, metadata, and type.
     *
     * @return an {@code AbstractPacket} representing the spawn packet for this entity.
     */
    @Override
    protected AbstractPacket getSpawnPacket() {
        WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();
        packet.setEntityID(entityId);
        packet.setX(location.getX());
        packet.setY(location.getY());
        packet.setZ(location.getZ());
        packet.setYaw(location.getYaw());
        packet.setPitch(location.getPitch());
        packet.setHeadPitch(location.getYaw());
        updateDataWatcher();
        packet.setMetadata(dataWatcher);
        packet.setType(getEntityType());
        return packet;
    }

    /**
     * Updates the entity's internal DataWatcher to reflect the current health value.
     * This method sets the health value in the DataWatcher object, allowing it to
     * synchronize and propagate to observing clients.
     *
     * The health value is retrieved using {@link #getHealth()} and assigned to the
     * DataWatcher at a predefined index using the {@code ProtocolLibSerializers.getFloat} method.
     * The index used for the health value is defined within the method.
     *
     * This method is typically called whenever the entity's state changes to ensure
     * consistency between the server and client representations of the entity.
     */
    @Override
    protected void onDataWatcherUpdate() {
        int healthIndex = 7;
        getDataWatcher().setObject(ProtocolLibSerializers.getFloat(healthIndex), getHealth());
    }
}
