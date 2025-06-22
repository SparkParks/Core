package network.palace.core.inventory.impl;

import com.google.common.collect.ImmutableList;
import network.palace.core.Core;
import network.palace.core.inventory.InventoryButtonInterface;
import network.palace.core.inventory.InventoryInterface;
import network.palace.core.player.CPlayer;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The DynamicInventory class provides a dynamic way to manage inventory GUI in a game,
 * supporting operations like adding, removing, and moving buttons, tracking observers,
 * and synchronizing inventory contents with players.
 * This class implements the InventoryInterface and Listener interfaces.
 */
public class DynamicInventory implements InventoryInterface, Listener {
    /**
     * A list of {@link CPlayer} objects who are currently observing this inventory.
     * Observers are players who have the inventory open.
     * This is maintained to track interactions and manage the inventory lifecycle
     * for different players.
     */
    protected final List<CPlayer> observers = new LinkedList<>();

    /**
     * Represents the size of the dynamic inventory.
     * This value defines the total number of slots in the inventory.
     * It must be a multiple of 9, as per the underlying inventory system's requirements.
     * <p>
     * This value is immutable and is set when the inventory is instantiated.
     */
    protected final int size;

    /**
     * Represents the title of the dynamic inventory.
     * This title is used to set the display name for the inventory.
     * It can include placeholders, such as %s, which may be replaced with dynamic values (e.g., player names).
     * Immutable once set, ensuring consistency across instances of the inventory.
     */
    protected final String title;

    /**
     * A mapping of inventory slot indices to {@link InventoryButtonInterface} instances.
     * This map holds the buttons added to the inventory GUI, where the key represents
     * the slot index, and the value represents the associated button.
     * <p>
     * The {@link InventoryButtonInterface} objects stored in this map define the appearance
     * and behavior of buttons displayed in each respective slot of the inventory. Buttons
     * can be added, removed, moved, or replaced in specific slots of the inventory.
     * <p>
     * Modifications to this map affect the state and structure of the inventory GUI,
     * enabling dynamic updates to the displayed buttons.
     */
    protected final Map<Integer, InventoryButtonInterface> inventoryButtons = new HashMap<>();

    /**
     * Constructs a DynamicInventory with the specified size and title.
     *
     * @param size  The size of the inventory. The size must be divisible by 9.
     * @param title The title of the inventory.
     * @throws IllegalArgumentException If the size is not divisible by 9.
     */
    public DynamicInventory(int size, String title) {
        if (size % 9 != 0) {
            throw new IllegalArgumentException("The size of an inventory must be divisible by 9");
        }
        this.size = size;
        this.title = title;
        Core.registerListener(this);
    }

    /**
     * Opens the dynamic inventory for the specified player. If the player is not
     * already an observer of this inventory, they are added to the observer list
     * and the inventory is displayed to them.
     *
     * @param player The player for whom the inventory will be opened.
     */
    @Override
    public void open(CPlayer player) {
        if (observers.contains(player)) return;
        observers.add(player);
        org.bukkit.inventory.Inventory inv = getInventory(player);
        player.openInventory(inv);
    }

    /**
     * Retrieves the inventory for the specified player. The inventory is dynamically
     * constructed with items visible to the player and formatted with the player's name.
     *
     * @param player The player for whom the inventory is being generated.
     * @return The dynamically constructed inventory for the specified player.
     */
    public org.bukkit.inventory.Inventory getInventory(CPlayer player) {
        String title = String.format(this.title, player.getName());
        org.bukkit.inventory.Inventory inv = Core.createInventory(size, title);
        for (Map.Entry<Integer, InventoryButtonInterface> entry : inventoryButtons.entrySet()) {
            if (!entry.getValue().isVisible(player)) {
                continue;
            }
            inv.setItem(entry.getKey(), entry.getValue().getStack());
        }
        return inv;
    }

    /**
     * Closes the dynamic inventory for the specified player. If the player is
     * listed as an observer of this inventory, they are removed from the observer
     * list and their inventory is closed.
     *
     * @param player The player for whom the inventory will be closed.
     *               Must not be null.
     */
    @Override
    public void close(CPlayer player) {
        if (!observers.contains(player)) return;
        observers.remove(player);
        player.getBukkitPlayer().closeInventory();
    }

    /**
     * Opens the dynamic inventory for the specified players. For each player in
     * the provided collection, the inventory is opened, and the player is managed
     * appropriately within the inventory system.
     *
     * @param players The collection of players for whom the inventory will be opened.
     *                Each player in the iterable will have the inventory displayed.
     */
    @Override
    public void open(Iterable<CPlayer> players) {
        players.forEach(this::open);
    }

    /**
     * Closes the dynamic inventory for the specified collection of players.
     * For each player in the provided iterable, the inventory is closed.
     *
     * @param players The collection of players for whom the inventory will be closed.
     *                Each player in the iterable will have their inventory closed.
     */
    @Override
    public void close(Iterable<CPlayer> players) {
        players.forEach(this::close);
    }

    /**
     * Adds the specified button to the next available slot in the inventory.
     * If no slots are available, the method will return without making any changes.
     *
     * @param button The {@link InventoryButtonInterface} to be added to the inventory.
     */
    @Override
    public final void addButton(InventoryButtonInterface button) {
        int nextOpenSlot = getNextOpenSlot();
        if (nextOpenSlot == -1) return;
        addButton(button, nextOpenSlot);
    }

    /**
     * Adds the specified button to the inventory at the given slot.
     * The button will be mapped to the provided slot, replacing any
     * existing button assigned to that slot.
     *
     * @param button The {@link InventoryButtonInterface} to be added to the inventory.
     *               Represents the button functionality and appearance.
     * @param slot   The slot in the inventory where the button will be placed.
     *               Must be a valid slot index within the inventory's size.
     */
    @Override
    public final void addButton(InventoryButtonInterface button, int slot) {
        inventoryButtons.put(slot, button);
    }

    /**
     * Moves the specified button to a new slot in the inventory.
     * The button is first removed from its current slot and then added to the specified slot.
     *
     * @param button The {@link InventoryButtonInterface} to be moved. Represents the button
     *               functionality and appearance.
     * @param slot   The target slot in the inventory where the button will be placed.
     *               Must be a valid slot index within the inventory's size.
     */
    @Override
    public final void moveButton(InventoryButtonInterface button, int slot) {
        removeButton(button);
        addButton(button, slot);
    }

    /**
     * Removes the specified button from the inventory. The button is cleared
     * from the slot it occupies, if present.
     *
     * @param button The {@link InventoryButtonInterface} to be removed from
     *               the inventory. Represents the button functionality and
     *               appearance.
     */
    @Override
    public final void removeButton(InventoryButtonInterface button) {
        clearSlot(getSlotFor(button));
    }

    /**
     * Replaces the button at the specified slot with the given button.
     * This method first clears the slot, if occupied, and then adds
     * the new button to the specified slot.
     *
     * @param button The {@link InventoryButtonInterface} to be placed in the inventory.
     *               Represents the button functionality and appearance.
     * @param slot   The slot in the inventory where the button will be replaced.
     *               Must be a valid slot index within the inventory's size.
     */
    @Override
    public final void replaceButton(InventoryButtonInterface button, int slot) {
        clearSlot(slot);
        addButton(button, slot);
    }

    /**
     * Removes the button mapped to the specified inventory slot, if present.
     * If the slot does not contain a button, no action is taken.
     *
     * @param slot The slot index in the inventory to be cleared.
     *             Must be a valid slot index within the inventory's size.
     */
    @Override
    public void clearSlot(int slot) {
        if (!inventoryButtons.containsKey(slot)) return;
        inventoryButtons.remove(slot);
    }

    /**
     * Retrieves the inventory slot index associated with the given button.
     * Searches the internal mapping of inventory slots to buttons and returns
     * the slot index corresponding to the specified button. If the button is
     * not present in the inventory, returns -1.
     *
     * @param button The {@link InventoryButtonInterface} whose associated slot
     *               is to be retrieved. Represents a button in the inventory.
     * @return The slot index associated with the specified button. If the button
     *         is not found, returns -1.
     */
    @Override
    public final int getSlotFor(InventoryButtonInterface button) {
        for (Map.Entry<Integer, InventoryButtonInterface> integerInventoryButtonEntry : inventoryButtons.entrySet()) {
            if (integerInventoryButtonEntry.getValue().equals(button)) {
                return integerInventoryButtonEntry.getKey();
            }
        }
        return -1;
    }

    /**
     * Checks if a specific slot in the inventory is filled with a button.
     *
     * @param slot The slot index in the inventory to check.
     *             Must be a valid slot index within the inventory's size.
     * @return {@code true} if the specified slot is filled with a button,
     *         {@code false} otherwise.
     */
    @Override
    public boolean isFilled(int slot) {
        return inventoryButtons.containsKey(slot);
    }

    /**
     * Retrieves the size of the inventory.
     *
     * @return The size of the inventory as an integer.
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Retrieves a list of the current observers of the dynamic inventory.
     * Observers are players who are currently viewing this inventory.
     *
     * @return An immutable list of {@link CPlayer} representing the current observers.
     */
    @Override
    public final ImmutableList<CPlayer> getCurrentObservers() {
        return ImmutableList.copyOf(observers);
    }

    /**
     * Retrieves an immutable list of all the buttons currently present in the inventory.
     *
     * @return An {@link ImmutableList} of {@link InventoryButtonInterface} representing
     *         all the buttons in the inventory.
     */
    @Override
    public final ImmutableList<InventoryButtonInterface> getButtons() {
        return ImmutableList.copyOf(inventoryButtons.values());
    }

    /**
     * Determines the next available open slot in the inventory. The method iterates
     * over the current inventory's mapping of buttons to slots and returns the first
     * slot index that is not occupied. If there are no open slots available, it
     * returns -1.
     *
     * @return The next unoccupied slot index as an integer. If no slots are available,
     *         returns -1.
     */
    private int getNextOpenSlot() {
        int nextSlot = 0;
        for (int integer : inventoryButtons.keySet()) {
            if (integer == nextSlot) nextSlot = integer + 1;
        }
        return nextSlot >= inventoryButtons.size() ? -1 : nextSlot;
    }
}
