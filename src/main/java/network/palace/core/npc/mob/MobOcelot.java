package network.palace.core.npc.mob;

import network.palace.core.npc.AbstractAnimal;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.EntityType;

import java.util.Set;

/**
 * Represents the in-game ocelot entity, extending the base functionality
 * provided by {@code AbstractAnimal}. Ocelots are a passive animal
 * typically found in jungle biomes within the game.
 *
 * This class defines specific behavior and properties for ocelots.
 */
public class MobOcelot extends AbstractAnimal {

    /**
     * Constructs a new instance of the MobOcelot class, initializing it with
     * the specified location, a set of observers, and a title.
     *
     * @param location the initial location of the ocelot in the game world
     * @param observers a set of players observing this ocelot
     * @param title the title or custom name assigned to the ocelot
     */
    public MobOcelot(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    /**
     * Retrieves the type of entity represented by this class.
     *
     * @return the EntityType corresponding to an ocelot.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.OCELOT;
    }

    /**
     * Retrieves the maximum health value for this ocelot entity.
     *
     * @return the maximum health of the ocelot, which is 1.
     */
    @Override
    public float getMaximumHealth() {
        return 1f;
    }
}
