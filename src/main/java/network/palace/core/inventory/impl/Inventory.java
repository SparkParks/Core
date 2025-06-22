package network.palace.core.inventory.impl;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.inventory.ClickAction;
import network.palace.core.inventory.InventoryButtonInterface;
import network.palace.core.inventory.InventoryInterface;
import network.palace.core.player.CPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

/**
 * Represents an interactive inventory GUI with support for managing buttons, observers, and various GUI-related events.
 * This class provides functionality to open and close inventories for players, manage buttons within the inventory,
 * and handle player interactions with the inventory.
 * <p>
 * Implements {@link InventoryInterface} for inventory manipulation and {@link Listener} for handling Bukkit events.
 */
public class Inventory implements InventoryInterface, Listener {

    /**
     * A list of current observers (CPlayers) monitoring or interacting with this inventory.
     * Observers are typically players that have the inventory open in their client.
     * This list is updated when players open or close the inventory.
     */
    protected final List<CPlayer> observers = new LinkedList<>();

    /**
     * Represents the Bukkit inventory managed by this class.
     * This inventory corresponds to the graphical user interface
     * displayed to the player for interactions.
     * <p>
     * Used internally to handle and modify the state of the inventory,
     * such as adding or removing items or responding to player actions.
     * <p>
     * Note that modifications to this variable directly impact the
     * inventory displayed to users and should be handled carefully.
     */
    protected org.bukkit.inventory.Inventory inventory;

    /**
     * A protected final map that maintains the inventory buttons associated with specific slots in the inventory.
     * Keys represent the inventory slot indices (0-based), and values correspond to instances of {@link InventoryButtonInterface}.
     * Used to manage the placement, retrieval, and interaction of buttons within the inventory GUI.
     * This map ensures that each slot in the inventory can be mapped to a specific button implementation.
     */
    protected final Map<Integer, InventoryButtonInterface> inventoryButtons = new HashMap<>();

    /**
     * A set of slot indices within the inventory that have been marked for updates.
     * These slots represent the inventory locations that have undergone changes
     * such as button modifications or other state updates and need to be refreshed.
     * <p>
     * This field is used to ensure that only the affected slots are updated when
     * the inventory GUI is refreshed, optimizing performance by avoiding unnecessary
     * updates to unchanged slots.
     */
    protected Set<Integer> updatedSlots = new HashSet<>();

    /**
     * Represents whether a sound should be played when interacting with the inventory.
     * If set to true, a sound effect is triggered on associated inventory actions.
     * If false, no sound is played.
     */
    @Getter @Setter private boolean playInventorySound = false;

    /**
     * Constructs a new Inventory instance with the specified size and title.
     * The size of the inventory must be divisible by 9.
     * Throws an {@link IllegalArgumentException} if the size is not valid.
     *
     * @param size  The number of slots in the inventory. Must be divisible by 9.
     * @param title The title of the inventory to be displayed.
     * @throws IllegalArgumentException If the size is not divisible by 9.
     */
    public Inventory(int size, String title) {
        if (size % 9 != 0) {
            throw new IllegalArgumentException("The size of an inventory must be divisible by 9");
        }
        this.inventory = Core.createInventory(size, title);
        Core.registerListener(this);
    }

    /**
     * Called when a player opens this inventory.
     *
     * @param player The player who is opening the inventory.
     */
    public void onOpen(CPlayer player) {
    }

    /**
     * Triggered when a player closes the inventory instance.
     *
     * @param player The player who is closing the inventory.
     */
    public void onClose(CPlayer player) {
    }

    /**
     * Opens the inventory for the specified player, adding them as an observer
     * if they are not already in the list of observers. Plays an opening sound
     * for the player if {@code playInventorySound} is enabled, and opens the
     * inventory GUI for them. This method also triggers the {@code onOpen}
     * lifecycle action.
     *
     * @param player The player for whom the inventory should be opened.
     */
    @Override
    public void open(CPlayer player) {
        if (observers.contains(player)) return;
        observers.add(player);
        if (playInventorySound) player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
        player.getBukkitPlayer().openInventory(inventory);
        onOpen(player);
    }

    /**
     * Closes the inventory for the specified player, removing them from the list of observers
     * and triggering the appropriate actions such as playing a sound, closing the inventory
     * GUI, and calling the {@code onClose} lifecycle method.
     *
     * @param player The player for whom the inventory should be closed.
     */
    @Override
    public void close(CPlayer player) {
        if (!observers.contains(player)) return;
        observers.remove(player);
        if (playInventorySound) player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1.0F, 1.0F);
        player.getBukkitPlayer().closeInventory();
        onClose(player);
    }

    /**
     * Opens the inventory for all specified players. Iterates through the provided collection
     * of players and individually opens the inventory for each one. Each player's opening action
     * will also trigger the associated lifecycle events and actions managed by the internal logic.
     *
     * @param players An iterable collection of {@link CPlayer} instances for whom the inventory should be opened.
     *                Each player in this collection will have the inventory opened specifically for them.
     */
    @Override
    public final void open(Iterable<CPlayer> players) {
        players.forEach(this::open);
    }

    /**
     * Closes the inventory for each specified player in the provided collection.
     * Iterates through the collection of {@link CPlayer} instances and calls the
     * {@code close} method for each player, triggering any associated lifecycle
     * events and closing actions defined in the inventory logic.
     *
     * @param players An iterable collection of {@link CPlayer} instances for whom
     *                the inventory should be closed. Each player in this collection
     *                will have their inventory closed.
     */
    @Override
    public final void close(Iterable<CPlayer> players) {
        players.forEach(this::close);
    }

    /**
     * Adds a button to the inventory at the next available open slot. If there are
     * no open slots, the button will not be added.
     *
     * @param button The {@link InventoryButtonInterface} instance representing the
     *               button to be added to the inventory.
     */
    @Override
    public final void addButton(InventoryButtonInterface button) {
        int nextOpenSlot = getNextOpenSlot();
        if (nextOpenSlot == -1) return;
        addButton(button, nextOpenSlot);
    }

    /**
     * Adds a button to the inventory at a specified slot and marks the slot for an update.
     *
     * @param button The {@link InventoryButtonInterface} instance representing the button
     *               to be added to the inventory.
     * @param slot   The specific slot where the button should be placed within the inventory.
     *               The slot number must correspond to a valid index within the inventory's
     *               size constraints.
     */
    @Override
    public final void addButton(InventoryButtonInterface button, int slot) {
        inventoryButtons.put(slot, button);
        markForUpdate(slot);
    }

    /**
     * Moves a button in the inventory to a specified slot.
     * First removes the button from its current position, then places it into the new slot.
     *
     * @param button The {@link InventoryButtonInterface} instance representing the button
     *               to be moved within the inventory.
     * @param slot   The target slot where the button should be moved.
     *               The slot number must correspond to a valid index in the inventory's size.
     */
    @Override
    public final void moveButton(InventoryButtonInterface button, int slot) {
        removeButton(button);
        addButton(button, slot);
    }

    /**
     * Removes a button from the inventory by clearing the slot it occupies.
     * The method determines the slot associated with the specified button
     * using {@code getSlotFor} and then clears it using {@code clearSlot}.
     *
     * @param button The {@link InventoryButtonInterface} instance representing
     *               the button to be removed from the inventory.
     */
    @Override
    public final void removeButton(InventoryButtonInterface button) {
        clearSlot(getSlotFor(button));
    }

    /**
     * Replaces the button located at a specified slot in the inventory with a new button.
     * This method first clears the specified slot if it is already occupied by another button,
     * then places the new button into the cleared slot.
     *
     * @param button The {@link InventoryButtonInterface} instance representing the new button
     *               to be placed in the inventory.
     * @param slot   The specific slot where the button should be placed. The slot number must
     *               correspond to a valid index within the inventory's size constraints.
     */
    @Override
    public final void replaceButton(InventoryButtonInterface button, int slot) {
        clearSlot(slot);
        addButton(button, slot);
    }

    /**
     * Clears the specified slot in the inventory if it contains a button.
     * The method removes the button from the slot and marks the slot for an update.
     *
     * @param slot The slot number to be cleared. Must refer to a valid slot in the inventory.
     */
    @Override
    public void clearSlot(int slot) {
        if (!inventoryButtons.containsKey(slot)) return;
        inventoryButtons.remove(slot);
        markForUpdate(slot);
    }

    /**
     * Retrieves the slot number associated with the specified {@link InventoryButtonInterface}
     * within the inventory. If the button is not found in the inventory, this method returns -1.
     *
     * @param button The {@link InventoryButtonInterface} instance whose associated slot
     *               number is to be determined.
     * @return The slot number corresponding to the specified button, or -1 if the button
     *         is not present in the inventory.
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
     * Handles the event when a player leaves the game. This method is triggered
     * by the {@link PlayerQuitEvent} and ensures that the player is removed from
     * the list of observers tied to the inventory. If the leaving player is in
     * the internal observers list, they are removed to prevent further interactions.
     *
     * @param event The {@link PlayerQuitEvent} instance representing the player
     *              leaving the game. Contains information about the player who
     *              quit.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public final void onPlayerLeave(PlayerQuitEvent event) {
        CPlayer onlinePlayer = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (observers.contains(onlinePlayer)) observers.remove(onlinePlayer);
    }

    /**
     * Handles the event when a player's inventory is closed. This method verifies
     * if the inventory being closed matches the current inventory instance and ensures
     * that the player is an instance of {@link Player}. If so, the player is removed
     * from the observers list. Optionally plays a sound for the player if
     * {@code playInventorySound} is enabled. Finally, invokes the {@code onClose}
     * lifecycle action for the player.
     *
     * @param event The {@link InventoryCloseEvent} triggered when a player closes
     *              an inventory. Contains information about the inventory being
     *              closed and the player associated with the event.
     */
    @EventHandler
    public final void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (!event.getInventory().equals(inventory)) return;
        Player player = (Player) event.getPlayer();
        CPlayer onlinePlayer = Core.getPlayerManager().getPlayer(player);
        observers.remove(onlinePlayer);
        if (playInventorySound) player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1.0F, 1.0F);
        onClose(onlinePlayer);
    }

    /**
     * Handles the event when a player interacts with an inventory slot by clicking.
     * This method determines if the interaction is valid for the custom inventory
     * and processes the click action if applicable. If the clicked slot corresponds
     * to a defined button in the inventory, its associated click action is executed.
     * The event is canceled to prevent further processing by the default inventory logic.
     *
     * @param event The {@link InventoryClickEvent} instance triggered upon player interaction
     *              with an inventory slot. Contains details such as the player clicking,
     *              the inventory involved, and the type of click action performed.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!(event.getInventory().equals(inventory))) return;
        CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getWhoClicked());
        InventoryButtonInterface inventoryButton = inventoryButtons.get(event.getSlot());
        if (player == null) return;
        if (inventoryButton == null) return;
        inventoryButton.getClick().onPlayerClick(player, ClickAction.getActionTypeFor(event.getClick()));
        event.setCancelled(true);
    }

    /**
     * Handles the {@link InventoryMoveItemEvent} to manage item movement into the inventory.
     * Cancels the event if the item is being moved into the associated inventory to prevent
     * unauthorized or unintended changes.
     *
     * @param event The {@link InventoryMoveItemEvent} triggered when an item is moved between
     *              inventories. Contains information about the source, destination, and the
     *              item being moved.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public final void onPlayerInventoryMove(InventoryMoveItemEvent event) {
        if (!event.getDestination().equals(inventory)) return;
        event.setCancelled(true);
    }

    /**
     * Determines whether the specified slot in the inventory is currently filled
     * with a button. This method checks if the given slot index is present
     * in the mapping of inventory buttons.
     *
     * @param slot The index of the inventory slot to check. Must refer to a valid slot
     *             within the inventory's defined size.
     * @return {@code true} if the slot is filled with a button; {@code false} otherwise.
     */
    @Override
    public boolean isFilled(int slot) {
        return inventoryButtons.containsKey(slot);
    }

    /**
     * Retrieves the size of the inventory.
     *
     * @return the size of the inventory as an integer
     */
    @Override
    public int getSize() {
        return inventory.getSize();
    }

    /**
     * Retrieves a list of the current observers.
     *
     * @return an immutable list containing the current observers
     */
    @Override
    public final ImmutableList<CPlayer> getCurrentObservers() {
        return ImmutableList.copyOf(observers);
    }

    /**
     * Retrieves a list of inventory buttons represented by the current state.
     *
     * @return an immutable list of inventory buttons.
     */
    @Override
    public final ImmutableList<InventoryButtonInterface> getButtons() {
        return ImmutableList.copyOf(inventoryButtons.values());
    }

    /**
     * Marks the specified slot for an update. This includes adding the slot
     * to the list of updated slots and triggering the inventory update process.
     *
     * @param slot the index of the slot to mark for update
     */
    private void markForUpdate(int slot) {
        updatedSlots.add(slot);
        updateInventory();
    }

    /**
     * Updates the inventory by synchronizing the inventory buttons and the actual inventory state.
     * This method iterates through all slots of the inventory, ensuring that the inventory contents
     * match the state defined by the inventory buttons. If discrepancies exist, it updates the inventory
     * to reflect the correct state. Additionally, it refreshes the inventory view for all observers.
     * <p>
     * Behavior:
     * <p>
     * - If an inventory button is null but the corresponding inventory slot is not empty, the slot is
     *   cleared.
     *   <p>
     * - If an inventory button is not null but the corresponding inventory slot is empty, or the slot
     *   is marked as updated, the item's stack from the inventory button is set to the slot.
     *   <p>
     * - After synchronizing all slots, it refreshes the inventory display for all observers.
     * <p>
     * - Clears the set of updated slots after processing.
     */
    private void updateInventory() {
        for (int i = 0; i < inventory.getSize(); i++) {
            InventoryButtonInterface inventoryButton = inventoryButtons.get(i);
            if (inventoryButton == null && inventory.getItem(i) != null) {
                inventory.setItem(i, null);
                continue;
            }
            if ((inventory.getItem(i) == null && inventoryButton != null) || updatedSlots.contains(i)) {
                inventory.setItem(i, inventoryButton.getStack());
            }
        }
        observers.forEach(observer -> observer.getBukkitPlayer().updateInventory());
        updatedSlots = new HashSet<>();
    }

    /**
     * Determines the next available open slot in the inventory, based on the current occupied slots.
     * The method checks for the next slot index that is not already used, incrementing from 0.
     * If no slot is available within the size of the inventory, it returns -1.
     *
     * @return the next open slot index if available; returns -1 if no slot is available within the inventory size
     */
    private int getNextOpenSlot() {
        int nextSlot = 0;
        for (int integer : inventoryButtons.keySet()) {
            if (integer == nextSlot) nextSlot = integer + 1;
        }
        return nextSlot >= inventory.getSize() ? -1 : nextSlot;
    }
}
