package network.palace.core.npc.status;

/**
 * The Status class defines a collection of constant integer values that represent
 * specific in-game events or states. These constants can be used to identify and
 * handle various actions or changes related to entities in the game, such as taming
 * animals, particle effects for villagers, or interactions with fireworks.
 */
public class Status {
    /**
     * Represents the event when an entity takes damage. This constant can be used
     * to identify and handle in-game situations where an entity is hurt.
     */
    public static final int ENTITY_HURT = 2;

    /**
     * Represents the event when an entity has died. This constant can be used
     * to identify and handle situations where an entity's life state transitions
     * to dead, allowing for specific game logic to execute in response to this event.
     */
    public static final int ENTITY_DEAD = 3;

    /**
     * Represents the event when a player or entity initiates the process of taming a wolf.
     * This constant can be utilized to identify and handle scenarios where a wolf is in
     * the process of becoming tamed within the game.
     */
    public static final int WOLF_TAMING = 6;

    /**
     * Represents the event when a wolf has been successfully tamed by a player.
     * This constant can be used to identify and handle game logic triggered by
     * the completion of the wolf taming process.
     */
    public static final int WOLF_TAMED = 7;

    /**
     * Represents the event when a wolf shakes off water from its fur after being wet.
     * This constant can be used to identify and handle scenarios involving the wolf's
     * animation or interaction logic associated with this action in the game.
     */
    public static final int WOLF_SHAKING_OFF_WATER = 8;

    /**
     * Represents the event when an entity accepts food, such as during feeding or
     * taming scenarios. This constant can be used to identify and handle in-game
     * interactions where entities consume food items or accept feeding actions.
     */
    public static final int EATING_ACCEPTED = 9;

    /**
     * Represents the event when a sheep is eating grass. This constant can be
     * used to identify and handle in-game scenarios where a sheep interacts
     * with its environment by consuming grass blocks, allowing for specific
     * game logic or animations to be triggered in response to this action.
     */
    public static final int SHEEP_EATING_GRASS = 10;

    /**
     * Represents the event when an Iron Golem offers a poppy (rose) to nearby villagers or players.
     * This constant can be used to identify and handle in-game scenarios where the gifting behavior
     * of Iron Golems occurs, often signifying their benevolent interaction with villagers.
     */
    public static final int IRON_GOLEM_GIFTING_ROSE = 11;

    /**
     * Represents the particle effect ID used when a villager spawns heart particles.
     * Typically associated with actions like positive villager interactions or events
     * that cause villagers to display affection or happiness.
     */
    public static final int VILLAGER_SPAWN_HEART_PARTICLE = 12;

    /**
     * An integer constant representing the status ID for spawning angry particles around a villager.
     * This value is used to trigger the visual effect associated with a villager's angry reaction.
     */
    public static final int VILLAGER_SPAWN_ANGRY_PARTICLE = 13;

    /**
     * Represents the constant identifier for the happy particle effect spawned when a villager spawns.
     * Typically used in scenarios where visual feedback is provided to indicate a villager's positive
     * interaction or spawning event.
     */
    public static final int VILLAGER_SPAWN_HAPPY_PARTICLE = 14;

    /**
     * Represents the magic particle effect that spawns when a witch appears.
     * This constant is used to identify the specific type of particle effect
     * tied to the spawning animation or presence of a witch entity in the game.
     */
    public static final int WITCH_SPAWN_MAGIC_PARTICLE = 15;

    /**
     * Represents the status identifier for the process of a zombie converting a villager into a zombie villager.
     * This can be used to track or trigger events related to this specific transformation.
     */
    public static final int ZOMBIE_VILLAGERIZING = 16;

    /**
     * Represents the status code for a firework exploding event.
     * Used to indicate the specific occurrence of a firework explosion
     * within the system.
     */
    public static final int FIREWORK_EXPLODING = 17;
}
