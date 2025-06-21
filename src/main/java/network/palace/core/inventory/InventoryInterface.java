package network.palace.core.inventory;

import network.palace.core.player.CPlayer;

import java.util.List;

/**
 * Interface defining the behaviors and operations for managing an inventory system.
 * It provides methods to manage inventory buttons, assign them to specific slots,
 * and handle player interactions with the inventory system.
 */
public interface InventoryInterface {

    /**
     * Opens the inventory interface for the specified player.
     *
     * @param player The player for whom the inventory interface will be opened.
     */
    void open(CPlayer player);

    /**
     * Closes the inventory interface for the specified player.
     *
     * @param player The player for whom the inventory interface will be closed.
     */
    void close(CPlayer player);

    /**
     * Opens the inventory interface for the specified players.
     *
     * @param players An iterable collection of players for whom the inventory interface will be opened.
     */
    void open(Iterable<CPlayer> players);

    /**
     * Closes the inventory interface for the specified players.
     *
     * @param players An iterable collection of players for whom the inventory interface will be closed.
     */
    void close(Iterable<CPlayer> players);

    /**
     * Adds a button to the inventory interface. This button will represent a specific
     * item or action available in the inventory system.
     *
     * @param button The button to be added to the inventory interface. The button must
     *               implement the {@link InventoryButtonInterface}, providing the necessary
     *               properties and behaviors for inventory interaction.
     */
    void addButton(InventoryButtonInterface button);

    /**
     * Adds a button to a specific slot in the inventory interface. This method places
     * the provided button at the designated slot within the inventory system.
     *
     * @param button The button to be added to the inventory interface. The button must
     *               implement the {@link InventoryButtonInterface}, providing the necessary
     *               properties and behaviors for inventory interaction.
     * @param slot   The specific slot in the inventory where the button will be placed.
     *               The slot must be within the bounds of the inventory size.
     */
    void addButton(InventoryButtonInterface button, int slot);

    /**
     * Moves the specified inventory button to a new slot within the inventory interface.
     * This method updates the position of the button to the given slot while maintaining
     * its current functionality and visibility properties.
     *
     * @param button The inventory button to be moved. The button must implement the
     *               {@link InventoryButtonInterface}, providing the necessary properties
     *               and behaviors for interaction within the inventory.
     * @param slot   The target slot to which the button will be moved. The slot must be
     *               within the bounds of the inventory size.
     */
    void moveButton(InventoryButtonInterface button, int slot);

    /**
     * Removes the specified button from the inventory interface.
     *
     * @param button The button to be removed from the inventory interface. The button must
     *               implement the {@link InventoryButtonInterface}, ensuring it carries
     *               the necessary properties and behaviors for inventory interaction.
     */
    void removeButton(InventoryButtonInterface button);

    /**
     * Replaces the existing button in the specified slot of the inventory interface
     * with the provided button. This method allows updating or modifying the contents
     * of a specific slot within the inventory system.
     *
     * @param button The new button to place in the specified slot. The button must
     *               implement the {@link InventoryButtonInterface}, providing the
     *               necessary properties and behaviors for inventory interaction.
     * @param slot   The target slot in the inventory where the existing button will
     *               be replaced. The slot must be within the bounds of the
     *               inventory size.
     */
    void replaceButton(InventoryButtonInterface button, int slot);

    /**
     * Retrieves the slot number where the specified inventory button is located.
     *
     * @param button The inventory button whose slot number is to be retrieved.
     *               The button must implement the {@link InventoryButtonInterface}.
     * @return The slot number associated with the specified button, or -1 if the button
     *         is not present in the inventory.
     */
    int getSlotFor(InventoryButtonInterface button);

    /**
     * Clears the specified slot in the inventory interface. This action removes
     * any content or button assigned to the given slot, effectively resetting it
     * to an empty state.
     *
     * @param slot The slot number to be cleared. The slot must be within the
     *             bounds of the inventory size.
     */
    void clearSlot(int slot);

    /**
     * Determines whether the specified slot in the inventory is filled.
     *
     * @param slot The slot number to be checked. The slot must be within the
     *             bounds of the inventory size.
     * @return true if the specified slot is filled, false otherwise.
     */
    boolean isFilled(int slot);

    /**
     * Retrieves the size of the inventory.
     *
     * @return the number of slots in the inventory.
     */
    int getSize();

    /**
     * Retrieves a list of current observers of the inventory interface. Observers are typically
     * players who are viewing the inventory at the given time.
     *
     * @return A list of CPlayer objects representing the current observers of the inventory interface.
     */
    List<CPlayer> getCurrentObservers();

    /**
     * Retrieves the list of buttons currently present in the inventory interface.
     * These buttons represent various actions or items available for interaction
     * within the inventory system.
     *
     * @return a list of {@link InventoryButtonInterface} instances representing
     *         the buttons in the inventory interface.
     */
    List<InventoryButtonInterface> getButtons();

}
