package network.palace.core.npc.mob;

import network.palace.core.npc.AbstractAnimal;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.EntityType;

import java.util.Set;

/**
 * Represents a chicken entity in the game world.
 * This class extends {@code AbstractAnimal} to provide specific attributes
 * and behaviors associated with chickens, such as their maximum health and type.
 */
public class MobChicken extends AbstractAnimal {

    /**
     * Constructs a new MobChicken instance with the specified location, observers, and title.
     *
     * @param location the point in the game world where the chicken is spawned
     * @param observers a set of players observing this chicken
     * @param title the title or name associated with this MobChicken instance
     */
    public MobChicken(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    /**
     * Retrieves the type of entity represented by this class.
     *
     * @return the EntityType corresponding to a chicken.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.CHICKEN;
    }

    /**
     * Retrieves the maximum health value for the chicken entity.
     *
     * @return the maximum health of the chicken, which is 4.
     */
    @Override
    public float getMaximumHealth() {
        return 4f;
    }
}
