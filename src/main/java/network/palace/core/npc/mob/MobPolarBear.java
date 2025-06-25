package network.palace.core.npc.mob;

import network.palace.core.npc.AbstractAnimal;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.EntityType;

import java.util.Set;

/**
 * Represents a Polar Bear entity in the game world, extending the functionalities of
 * the {@code AbstractAnimal} class. This class defines specific attributes and behaviors
 * for the Polar Bear, such as its entity type and maximum health.
 *
 * The {@code MobPolarBear} is initialized with its location, a set of observing players,
 * and a title or name for the entity. This class uses predefined values for the Polar Bear,
 * including its type and health attributes, offering a concrete implementation of
 * the abstract methods defined in its parent class.
 */
public class MobPolarBear extends AbstractAnimal {
    /**
     * Constructs a {@code MobPolarBear} instance, representing a Polar Bear entity in the game world.
     *
     * @param point The location of the Polar Bear in the game world.
     * @param observers A set of players who are observing this entity.
     * @param title The title or name assigned to the Polar Bear entity.
     */
    public MobPolarBear(Point point, Set<CPlayer> observers, String title) {
        super(point, observers, title);
    }

    /**
     * Retrieves the type of entity represented by this class.
     *
     * @return the EntityType corresponding to a polar bear.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.POLAR_BEAR;
    }

    /**
     * Retrieves the maximum health value for the polar bear entity.
     *
     * @return the maximum health of the polar bear, which is 30.
     */
    @Override
    public float getMaximumHealth() {
        return 30;
    }
}
