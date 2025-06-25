package network.palace.core.npc;

/**
 * Represents the types of click actions that can occur during player interaction.
 *
 * Enum Values:
 * - RIGHT_CLICK: Indicates a right-click action, typically associated with interacting with an object.
 * - LEFT_CLICK: Indicates a left-click action, often associated with attacking or mining.
 *
 * This enum provides a mapping from generic action strings to specific click actions
 * using the {@code from} method.
 */
public enum ClickAction {

    /**
     * Represents a right-click action performed during a player interaction.
     * This enum value is typically used to identify actions related to interacting
     * or using objects within the game environment, such as opening doors, accessing
     * inventories, or performing specific contextual interactions.
     */
    RIGHT_CLICK,

    /**
     * Represents a left-click action performed during a player interaction.
     * This enum value is typically used to identify actions related to
     * attacking or mining within the game environment. It signifies a primary
     * interaction often associated with offensive or destructive behaviors.
     */
    LEFT_CLICK;

    /**
     * Converts a string representation of a click action to its corresponding {@code ClickAction} enum value.
     *
     * @param action the string representation of the action, expected to be "INTERACT" or "ATTACK".
     * @return the corresponding {@code ClickAction} enum value based on the provided string.
     *         Returns {@code null} if the input does not match a recognized action.
     */
    public static ClickAction from(String action) {
        switch (action) {
            case "INTERACT":
                return RIGHT_CLICK;
            case "ATTACK":
                return LEFT_CLICK;
        }
        return null;
    }
}
