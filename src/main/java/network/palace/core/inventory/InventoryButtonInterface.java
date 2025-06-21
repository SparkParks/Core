package network.palace.core.inventory;

import network.palace.core.player.CPlayer;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an interface for a button in an inventory system.
 * This interface defines methods to retrieve the item stack displayed on the button,
 * handle click actions, and determine the button's visibility for a specific player.
 */
public interface InventoryButtonInterface {

    /**
     * Retrieves the ItemStack associated with this inventory button.
     *
     * @return the ItemStack representing this button, which will be displayed
     *         in the inventory.
     */
    ItemStack getStack();

    /**
     * Retrieves the {@link InventoryClick} associated with this inventory button.
     * This object is responsible for handling click actions performed by a player
     * on the button in an inventory interface.
     *
     * @return the {@link InventoryClick}, defining the behavior of the button
     *         when clicked by a player.
     */
    InventoryClick getClick();

    /**
     * Determines whether the inventory button is visible for a specific player.
     *
     * @param player the player for whom the visibility of the button is being checked
     * @return true if the button is visible to the specified player, false otherwise
     */
    boolean isVisible(CPlayer player);

}
