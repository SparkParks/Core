package network.palace.core.npc.mob;

import network.palace.core.npc.AbstractAnimal;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.EntityType;

import java.util.Set;

/**
 * Represents a wolf mob in the game world that inherits properties and behaviors
 * from the {@code AbstractAnimal} class. The {@code MobWolf} class defines specific
 * attributes such as its entity type and maximum health.
 *
 * The wolf entity is identified by the {@code EntityType.WOLF} and has a maximum
 * health value of 10.
 */
public class MobWolf extends AbstractAnimal {

    /**
     * Initializes a new instance of the {@code MobWolf} class with its location,
     * set of observers, and a designated title.
     *
     * @param location the location of the wolf in the game world, represented by a {@code Point} object
     * @param observers a set of {@code CPlayer} instances observing this mob
     * @param title the title of the wolf as a {@code String}
     */
    public MobWolf(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    /**
     * Retrieves the type of entity represented by this class.
     *
     * @return the EntityType corresponding to a wolf.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.WOLF;
    }

    /**
     * Retrieves the maximum health value for this wolf entity.
     *
     * @return the maximum health of the wolf, which is 10.
     */
    @Override
    public float getMaximumHealth() {
        return 10f;
    }
}
