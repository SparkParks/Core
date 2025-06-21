package network.palace.core.inventory;

import network.palace.core.player.CPlayer;

/**
 * Represents an action handler for inventory click events.
 * This interface provides a method to handle player interactions with an inventory.
 */
public interface InventoryClick {

    /**
     * Handles the event when a player performs a click action in an inventory.
     *
     * @param player The player who performed the click action.
     * @param action The specific click action performed by the player.
     */
    void onPlayerClick(CPlayer player, ClickAction action);

}
