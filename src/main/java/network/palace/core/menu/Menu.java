package network.palace.core.menu;

import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.List;
import java.util.Optional;

/**
 * Represents a customizable menu that can be displayed to a player within an inventory.
 * The menu is interactive and supports binding actions to specific button clicks.
 */
public class Menu implements Listener {

    /**
     * Represents the size of the menu, defining the number of slots available.
     * The value is set during the construction of the menu and cannot be modified.
     */
    @Getter private final int size;

    /**
     * A collection of {@link MenuButton} instances representing the interactive buttons
     * within the menu. Each button is associated with a specific slot, an item representation,
     * and potential click actions. This list constitutes the core interactive elements of the menu,
     * allowing users to perform defined actions when interacting with the menu's visual interface.
     * <p>
     * The order and position of {@link MenuButton} elements correlate with the slots specified
     * by the buttons. Modifications to this list impact the menu's configuration and behavior.
     * <p>
     * This field is immutable once initialized and should be managed through appropriate
     * methods provided by the {@code Menu} class.
     */
    private final List<MenuButton> menuButtons;

    /**
     * Represents the inventory associated with the menu.
     * This is a private and final field that holds
     * the underlying Bukkit or server platform's inventory instance
     * used for rendering and managing the items in the menu.
     * It is initialized when the menu is created and cannot be modified afterwards.
     */
    private final Inventory inventory;

    /**
     * The title of the menu displayed to the player. This string is used to
     * represent the name or header of the menu when it is opened in the
     * player's inventory interface.
     */
    private final String title;

    /**
     * Represents the player associated with this menu.
     * This is the player for whom the menu is opened or interacted with.
     * It is initialized during the creation of the menu and remains constant throughout the lifecycle of the menu instance.
     */
    private final CPlayer player;

    /**
     * Represents an optional {@link Runnable} action to be executed when the menu is closed.
     * The absence of a value indicates that no action should occur upon menu closure.
     * <p>
     * This field can be set using the {@code setOnClose} method to define custom behavior
     * when the menu is closed, allowing users to provide dynamic or specific close handling logic.
     */
    private Optional<Runnable> onClose = Optional.empty();

    /**
     * Constructs a new Menu instance with the specified size, title, player, and menu buttons.
     *
     * @param size the size of the inventory for this menu
     * @param title the title of the menu
     * @param player the player associated with this menu
     * @param buttons the list of buttons to be included in the menu
     */
    public Menu(int size, String title, CPlayer player, List<MenuButton> buttons) {
        this.size = size;
        this.inventory = Core.createInventory(size, title);
        this.title = title;
        this.player = player;
        this.menuButtons = buttons;
    }

    /**
     * Retrieves the {@link MenuButton} associated with the specified inventory slot, if it exists.
     *
     * @param slot the inventory slot to search for a matching MenuButton
     * @return an {@link Optional} containing the found {@link MenuButton} if present,
     *         or an empty {@link Optional} if no button exists in the specified slot
     */
    public Optional<MenuButton> getButton(int slot) {
        return menuButtons.stream().filter(b -> b.getSlot() == slot).findFirst();
    }

    /**
     * Opens the menu for the associated player, initializes the inventory with the configured menu buttons,
     * and registers necessary listeners for menu interactions.
     * <p>
     * The method performs the following operations:
     * - Clears the current inventory content.
     * - Populates the inventory with each {@code MenuButton} from the {@code menuButtons} list at their respective slots.
     *   Throws an {@code IllegalArgumentException} if a button's slot exceeds the inventory size.
     * - Opens the inventory for the associated player.
     * - Registers this instance as an event listener to handle menu interactions.
     * <p>
     * This method ensures that the menu is displayed correctly and is reactive to user actions.
     * <p>
     * Exceptions:
     * - Throws {@link IllegalArgumentException} if any menu button's slot is out of the bounds of the inventory size.
     */
    public void open() {
        Core.runTask(Core.getInstance(), () -> {
            inventory.clear();
            menuButtons.forEach(button -> {
                if (button.getSlot() >= inventory.getSize()) {
                    throw new IllegalArgumentException("Button " + button.getItemStack().toString() + " in slot " +
                            button.getSlot() + " exceeds inventory " + title + " size " + inventory.getSize());
                }
                inventory.setItem(button.getSlot(), button.getItemStack());
            });
            player.openInventory(inventory);
            Core.registerListener(this);
        });
    }

    /**
     * Removes the {@link MenuButton} associated with the specified inventory slot from the menu.
     * If a button exists in the given slot, it is removed from the {@code menuButtons} list,
     * and the corresponding item in the inventory is cleared.
     *
     * @param slot the inventory slot from which the button should be removed
     */
    public void removeButton(int slot) {
        menuButtons.removeIf(b -> b.getSlot() == slot);
        inventory.setItem(22, null);
    }

    /**
     * Sets or replaces a {@link MenuButton} in the menu at the specified slot.
     * If a {@link MenuButton} with the same slot already exists, it is removed
     * before adding the new button. The inventory is updated to reflect the change
     * by placing the {@link org.bukkit.inventory.ItemStack} associated with the button
     * at the corresponding slot.
     *
     * @param button the {@link MenuButton} to set in the menu
     */
    public void setButton(MenuButton button) {
        menuButtons.removeIf(b -> b.getSlot() == button.getSlot());
        menuButtons.add(button);
        inventory.setItem(button.getSlot(), button.getItemStack());
    }

    /**
     * Handles the inventory click event for a menu. This method ensures that clicks within the menu
     * are processed appropriately, including canceling unauthorized actions and executing defined
     * button actions. The method also monitors and logs events that exceed a specified execution time threshold.
     *
     * @param event the {@link InventoryClickEvent} triggered when a player interacts with an inventory
     */
    @EventHandler
    public void click(InventoryClickEvent event) {
        long t = System.currentTimeMillis();
        InventoryView view = event.getView();
        if (isSameInventory(view)) {
            event.setCancelled(true);
            menuButtons.stream().filter(button -> button.getSlot() == event.getRawSlot() && button.getActions().containsKey(event.getClick()))
                    .findFirst().map(menuButton -> menuButton.getActions().get(event.getClick()))
                    .ifPresent(action -> action.accept(Core.getPlayerManager().getPlayer((Player) event.getWhoClicked())));

            long t2 = System.currentTimeMillis();
            long diff = t2 - t;
            if (diff >= 500) {
                for (CPlayer cp : Core.getPlayerManager().getOnlinePlayers()) {
                    if (cp == null)
                        continue;
                    if (cp.getRank().getRankId() >= Rank.DEVELOPER.getRankId()) {
                        cp.sendMessage(ChatColor.RED + "Click event took " + diff + "ms! " + ChatColor.GREEN +
                                event.getWhoClicked().getName() + " " + ChatColor.stripColor(view.getTitle()) + " ");
                    }
                }
            }
        }
    }

    /**
     * Sets a {@link Runnable} task to be executed when the menu is closed.
     *
     * @param onClose the {@link Runnable} task to be run upon closing the menu.
     *                If null is provided, no action will be executed on menu closure.
     */
    public void setOnClose(Runnable onClose) {
        this.onClose = Optional.ofNullable(onClose);
    }

    /**
     * Handles the {@link InventoryCloseEvent} for the menu. This method ensures that
     * appropriate clean-up actions are performed when the menu is closed, including
     * unregistering event listeners and executing any optional {@link Runnable} tasks
     * defined for the menu closure.
     *
     * @param event the {@link InventoryCloseEvent} triggered when a player closes the inventory
     */
    @EventHandler
    public void close(InventoryCloseEvent event) {
        if (isSameInventory(event.getView())) {
            HandlerList.unregisterAll(this);
            onClose.ifPresent(Runnable::run);
        }
    }

    /**
     * Determines if the provided inventory view corresponds to the same inventory as this menu.
     * This method checks if the view is not null, verifies that the inventory is initialized,
     * and matches the title and at least one viewer's unique ID.
     *
     * @param view the {@link InventoryView} to compare with this menu's inventory
     * @return {@code true} if the provided view matches the menu's inventory, otherwise {@code false}
     */
    private boolean isSameInventory(InventoryView view) {
        if (view == null) {
            return false;
        }

        if (this.inventory == null) {
            return false;
        }

        return view.getTitle().equals(title) && view.getTopInventory().getViewers().stream().anyMatch(p -> p.getUniqueId().equals(player.getUniqueId()));
    }
}
