package network.palace.core.npc;

import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;

import java.util.Set;

/**
 * Represents an abstract implementation of an ageable animal entity in the game world.
 * This class extends the functionalities of {@code AbstractAgeableMob}, allowing specific
 * animals to inherit common behaviors such as mating animations.
 *
 * Subclasses of {@code AbstractAnimal} should implement their specific characteristics
 * and behaviors based on different types of animals.
 */
public abstract class AbstractAnimal extends AbstractAgeableMob {

    /**
     * Constructs a new AbstractAnimal entity with the specified location, observers, and title.
     *
     * @param location the location of the animal in the game world
     * @param observers the set of players observing this animal
     * @param title the title or name of the animal
     */
    public AbstractAnimal(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    /**
     * Triggers the mate animation for the animal entity.
     * This method broadcasts a predefined status code (18) to all observing players,
     * which will visually display the mating animation for this entity in the game world.
     */
    public void playMateAnimation() {
        playStatus(18);
    }

    /**
     * Triggers the mate animation for the animal entity, targeting a specific set of players.
     * This method sends a predefined status code (18) to the specified players,
     * allowing them to visualize the mating animation for this entity in the game world.
     *
     * @param players the set of players to whom the mating animation will be displayed
     */
    public void playMateAnimation(Set<CPlayer> players) {
        playStatus(players, 18);
    }
}
