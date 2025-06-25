package network.palace.core.npc;

import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;

import java.util.Set;

/**
 * Represents an abstract implementation of ambient entities, intended to define the
 * behavior and characteristics common to all non-hostile, passive ambient mobs in the system.
 *
 * This class inherits from the {@link AbstractMob} class and introduces no additional
 * public methods or properties beyond what is defined by its superclass. The primary
 * purpose of this class is to serve as a base for specific ambient entity types.
 *
 * Developers are expected to extend this class to create specific implementations
 * of ambient entities. When subclassing, implement the required methods from
 * {@link AbstractMob}, such as {@code getEntityType()} and {@code getMaximumHealth()},
 * to define the entity-specific behavior.
 *
 * Constructor Parameters:
 * - location: Represents the initial spawn location of the entity.
 * - observers: A set of players who can observe the entity.
 * - title: The title or name of the entity.
 */
public abstract class AbstractAmbient extends AbstractMob {

    /**
     * Constructs an instance of {@code AbstractAmbient}. This constructor initializes
     * the ambient mob with a specific location, a set of observing players, and a title.
     *
     * @param location the initial spawn location of the ambient entity
     * @param observers the set of players who can observe this entity
     * @param title the title or name of the ambient entity
     */
    public AbstractAmbient(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }
}
