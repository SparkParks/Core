package network.palace.core.npc.mob;

import lombok.Setter;
import network.palace.core.npc.AbstractMob;
import network.palace.core.npc.ProtocolLibSerializers;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.EntityType;

import java.util.Set;

/**
 * Represents a Creeper mob entity in the game.
 * Extends the functionality of the AbstractMob class to define
 * Creeper-specific attributes and behavior.
 *
 * Fields:
 * - isCharged: Indicates whether the Creeper is in a charged state.
 * - isIgnited: Indicates whether the Creeper is ignited and ready to explode.
 *
 * Constructor:
 * - The MobCreeper constructor initializes the Creeper at a specified location, with a set of observers and a title.
 *
 * Key Methods:
 * - {@code protected void onDataWatcherUpdate()}: Synchronizes the Creeper's charged and ignited states
 *   with its internal DataWatcher, which then propagates the updated state to observing clients.
 * - {@code protected EntityType getEntityType()}: Specifies the Creeper's entity type as {@code EntityType.CREEPER}.
 * - {@code public float getMaximumHealth()}: Returns the maximum health of the Creeper as 20.0f.
 *
 * This implementation ensures that the Creeper's state changes, such as charging and ignition,
 * are accurately reflected in both the server and client representations.
 */
public class MobCreeper extends AbstractMob {

    /**
     * Indicates whether the Creeper is in a charged state.
     *
     * A charged Creeper is a unique state of the Creeper entity that occurs when it is struck by lightning.
     * In this state, the Creeper becomes significantly more powerful, dealing increased damage upon explosion.
     * This variable tracks the charged status of the Creeper and is synchronized with the entity's internal
     * DataWatcher to ensure consistency between server and client representations of the Creeper.
     *
     * By default, the Creeper is not charged ({@code false}).
     */
    @Setter private boolean isCharged = false;

    /**
     * Indicates whether the Creeper is ignited and ready to explode.
     *
     * Ignition represents the state where the Creeper has been triggered
     * to detonate, typically through external actions such as a player
     * using flint and steel or other in-game interactions. When ignited,
     * the Creeper will enter its countdown animation leading to an explosion.
     *
     * This variable tracks the ignited status of the Creeper and is
     * synchronized with the entity's internal DataWatcher to ensure
     * consistency between server and client representations.
     *
     * By default, the Creeper is not ignited ({@code false}).
     */
    @Setter private boolean isIgnited = false;

    /**
     * Constructs a new MobCreeper instance with the specified location, observers, and title.
     *
     * @param location the location of the MobCreeper
     * @param observers the set of players observing this MobCreeper
     * @param title the title associated with this MobCreeper
     */
    public MobCreeper(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    /**
     * Updates the {@code WrappedDataWatcher} for this {@code MobCreeper} instance to reflect
     * its current state, specifically whether it is charged or ignited. This method ensures
     * that the relevant data attributes are synchronized and shared with clients observing
     * this entity.
     *
     * The following states are updated in the data watcher:
     * 1. Charged state: The method uses {@code ProtocolLibSerializers.getBoolean} with index 13
     *    to indicate whether the creeper is charged.
     * 2. Ignited state: Similarly, the method uses {@code ProtocolLibSerializers.getBoolean}
     *    with index 14 to indicate whether the creeper is ignited.
     *
     * After updating the specific states, this method invokes {@code super.onDataWatcherUpdate()}
     * to handle any additional updates defined by the superclass.
     */
    @Override
    protected void onDataWatcherUpdate() {
        int chargedIndex = 13;
        getDataWatcher().setObject(ProtocolLibSerializers.getBoolean(chargedIndex), isCharged);
        int ignitedIndex = 14;
        getDataWatcher().setObject(ProtocolLibSerializers.getBoolean(ignitedIndex), isIgnited);
        super.onDataWatcherUpdate();
    }

    /**
     * Retrieves the type of entity represented by this class.
     *
     * @return the EntityType corresponding to a creeper.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.CREEPER;
    }

    /**
     * Retrieves the maximum health value for the creeper entity.
     *
     * @return the maximum health of the creeper, which is 20.
     */
    @Override
    public float getMaximumHealth() {
        return 20f;
    }
}