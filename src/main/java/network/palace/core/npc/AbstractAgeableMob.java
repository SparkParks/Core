package network.palace.core.npc;

import lombok.Setter;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;

import java.util.Set;

/**
 * Represents an abstract class for ageable mobs. This is a subclass of {@link AbstractMob} and
 * provides functionality for determining whether the mob is a baby or an adult.
 *
 * Subclasses of this class can implement specific ageable entity behavior.
 */
public abstract class AbstractAgeableMob extends AbstractMob {

    /**
     * Indicates whether the entity is a baby.
     *
     * If set to {@code true}, the entity is considered a baby and will appear
     * smaller in size and may exhibit behavior typical of young entities.
     * If set to {@code false}, the entity is considered an adult.
     */
    @Setter private boolean baby = false;

    /**
     * Constructs an {@code AbstractAgeableMob}, which is an abstract class representing
     * an ageable entity in the world. This entity can be observed by a set of players
     * and has a specific title.
     *
     * @param location the spawn location of the entity in the game world
     * @param observers the set of players observing the entity
     * @param title the title or name identifier of the entity
     */
    public AbstractAgeableMob(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    /**
     * Updates the state of this entity in terms of its age (baby or adult)
     * and synchronizes this information with the entity's data watcher.
     *
     * This method is invoked when the entity's data watcher needs to be updated.
     * It sets the data watcher entry for the "baby" state of the entity at a specific
     * index using a boolean serializer. The value is determined by the `baby` field
     * in this entity. Additionally, the method calls the parent implementation of
     * `onDataWatcherUpdate` to ensure other data watcher updates are processed.
     *
     * Behavior:
     * - Defines the index for tracking the "baby" state of the entity using the data watcher.
     * - Updates the data watcher with the current `baby` property value, which determines
     *   whether the entity is a baby or an adult.
     * - Delegates additional data watcher updates to the parent class implementation.
     */
    @Override
    protected void onDataWatcherUpdate() {
        int babyIndex = 12;
        getDataWatcher().setObject(ProtocolLibSerializers.getBoolean(babyIndex), baby);
        super.onDataWatcherUpdate();
    }
}
