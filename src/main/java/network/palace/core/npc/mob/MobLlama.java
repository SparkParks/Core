package network.palace.core.npc.mob;

import network.palace.core.npc.AbstractAnimal;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.EntityType;

import java.util.Set;

/**
 * Represents a specific implementation of an animal entity, the llama, in the game world.
 * This class extends {@code AbstractAnimal} and defines the unique characteristics and
 * behaviors of the llama entity.
 */
public class MobLlama extends AbstractAnimal {
    /**
     * Constructs a new {@code MobLlama} object.
     *
     * @param point The location of the llama in the game world, represented as a {@code Point}.
     * @param observers A set of {@code CPlayer} instances observing this llama.
     * @param title A string representing the title or name assigned to this llama.
     */
    public MobLlama(Point point, Set<CPlayer> observers, String title) {
        super(point, observers, title);
    }

    /**
     * Retrieves the type of entity represented by this class.
     *
     * @return the EntityType corresponding to a llama.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.LLAMA;
    }

    /**
     * Retrieves the maximum health value for the llama entity.
     *
     * @return the maximum health of the llama, which is 30.
     */
    @Override
    public float getMaximumHealth() {
        return 30;
    }
}
