package network.palace.core.menu;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.player.CPlayer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a menu button that can be interacted with in a menu system.
 * The button is associated with a slot, an item, and a set of actions triggered by different click types.
 */
@AllArgsConstructor
public class MenuButton {

    /**
     * The slot index where this menu button is placed in the menu system.
     * Used to determine the button's placement within the menu layout.
     */
    @Getter private final int slot;

    /**
     * The ItemStack associated with this menu button, representing the visual item displayed
     * in the menu system.
     * This item can represent the function or purpose of the button within the menu.
     * It is immutable and cloned when duplicating the button to ensure uniqueness.
     */
    @Getter private final ItemStack itemStack;

    /**
     * A map of actions associated with different types of player clicks.
     * Each entry in the map maps a specific {@link ClickType} to a {@link Consumer}
     * that defines the behavior to execute when a player performs that click action
     * on this menu button.
     * <p>
     * This allows defining custom functionalities for various types of clicks, such as
     * left-click, right-click, or shift click, providing flexibility in behavior for
     * the menu button.
     * <p>
     * The key in the map is the {@link ClickType}, representing the type of interaction,
     * and the value is the {@link Consumer}, which takes a {@link CPlayer} parameter
     * to execute the respective action.
     * <p>
     * The actions map is immutable, ensuring that it cannot be modified to preserve
     * the integrity of the button's click-handling logic.
     */
    @Getter private final Map<ClickType, Consumer<CPlayer>> actions;

    /**
     * Constructs a new {@code MenuButton} with the specified slot and item stack,
     * while initializing it with an empty actions map.
     *
     * @param slot      the slot index where this menu button will be placed in the menu system
     * @param itemStack the {@code ItemStack} representing the visual item displayed in the menu
     */
    public MenuButton(int slot, ItemStack itemStack) {
        this(slot, itemStack, ImmutableMap.of());
    }

    /**
     * Creates a duplicate of this {@code MenuButton}.
     * The duplicate will have the same slot and a cloned {@code ItemStack},
     * ensuring that the duplicated button is independent in terms of the item representation.
     *
     * @return a new {@code MenuButton} instance with the same properties as the original
     */
    public MenuButton duplicate() {
        return new MenuButton(slot, itemStack.clone());
    }
}
