package network.palace.core.npc.mob;

import network.palace.core.npc.AbstractAmbient;
import network.palace.core.npc.ProtocolLibSerializers;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.EntityType;

import java.util.Set;

/**
 * Represents a bat entity in the game world. The MobBat class extends the AbstractAmbient class
 * and defines specific behaviors and characteristics of a bat entity.
 *
 * This class provides functionality to manage the bat's awake state, in addition to the methods
 * required by the AbstractAmbient superclass.
 */
public class MobBat extends AbstractAmbient {
    /**
     * Indicates whether the bat entity is currently awake.
     *
     * This variable is used to manage the behavior of the bat, such as its activity state.
     * When set to {@code true}, the bat is considered awake, and when set to {@code false},
     * the bat is considered asleep or inactive. The awake state may influence the bat's interactions
     * and actions within the game world.
     */
    private boolean awake = true;

    /**
     * Constructs a new MobBat instance with the specified location, observers, and title.
     *
     * @param location the location of the bat in the game world
     * @param observers a set of players observing the bat entity
     * @param title the title or display name of the bat
     */
    public MobBat(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    /**
     * Retrieves the type of entity represented by this class.
     *
     * @return the EntityType corresponding to a bat.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.BAT;
    }

    /**
     * Retrieves the maximum health value for the bat entity.
     *
     * @return the maximum health of the bat, which is 6.
     */
    @Override
    public float getMaximumHealth() {
        return 6f;
    }

    /**
     * Updates the internal DataWatcher of the bat entity to reflect its awake state.
     *
     * This method sets the awake state in the DataWatcher using the index provided by
     * the {@code ProtocolLibSerializers.getBoolean} method. The awake state is represented
     * as a boolean value, and it determines whether the bat is awake or not. The updated
     * state is synchronized with the client, ensuring consistency between server and client
     * representations of the entity.
     *
     * After modifying the DataWatcher, this method delegates further processing to the
     * superclass implementation of {@code onDataWatcherUpdate}.
     */
    @Override
    protected void onDataWatcherUpdate() {
        getDataWatcher().setObject(ProtocolLibSerializers.getBoolean(13), awake);
        super.onDataWatcherUpdate();
    }

    /**
     * Sets the awake state of the bat entity.
     *
     * This method updates the internal awake state of the bat, which influences its behavior
     * in the game. The awake state is also synchronized with the entity's internal
     * DataWatcher using the appropriate serializer, and the changes are propagated
     * to relevant observers.
     *
     * @param b the new awake state of the bat, where {@code true} indicates that the bat
     *          is awake and {@code false} indicates that it is asleep.
     */
    public void setAwake(boolean b) {
        this.awake = b;
        getDataWatcher().setObject(ProtocolLibSerializers.getBoolean(13), b);
        updateDataWatcher();
    }

    /**
     * Indicates whether the bat entity is currently awake.
     *
     * @return true if the bat is awake, false if it is asleep.
     */
    public boolean isAwake() {
        return awake;
    }
}
