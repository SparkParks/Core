package network.palace.core.inventory;

import org.bukkit.event.inventory.ClickType;

/**
 * Represents the type of action triggered by a player's click in an inventory interface.
 * This enum is used to categorize different click types (e.g., left, middle, right click)
 * and to map them to a corresponding ClickAction instance.
 */
public enum ClickAction {

    /**
     * Represents the action triggered by a left-click in an inventory interface.
     * This action type is often associated with primary interactions, such as selecting
     * or interacting with an item in a straightforward manner.
     */
    LEFT,

    /**
     * Represents the action triggered by a middle click in an inventory interface.
     * Typically associated with interactions that differ from primary (left-click)
     * and secondary (right-click) actions, the middle click may be used for additional
     * functionalities depending on the context of the inventory system.
     */
    MIDDLE,

    /**
     * Represents the action triggered by a right-click in an inventory interface.
     * Often associated with secondary interactions, such as context menus or
     * additional options when interacting with an item.
     */
    RIGHT;

    /**
     * Maps a given {@link ClickType} to its corresponding {@link ClickAction}.
     * This method is used to interpret the type of player's click and translate it
     * into a specific action that can be processed by an inventory system.
     *
     * @param click the type of click input to be mapped, represented by {@link ClickType}
     * @return the mapped {@link ClickAction} corresponding to the provided click type;
     *         defaults to {@link ClickAction#LEFT} if no specific mapping is found
     */
    public static ClickAction getActionTypeFor(ClickType click) {
        switch (click) {
            case RIGHT:
            case SHIFT_RIGHT:
                return ClickAction.RIGHT;
            case MIDDLE:
                return ClickAction.MIDDLE;
            default:
                return ClickAction.LEFT;
        }
    }
}
