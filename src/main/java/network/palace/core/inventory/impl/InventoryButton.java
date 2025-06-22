package network.palace.core.inventory.impl;

import lombok.Getter;
import network.palace.core.inventory.ButtonCriteria;
import network.palace.core.inventory.InventoryButtonInterface;
import network.palace.core.inventory.InventoryClick;
import network.palace.core.player.CPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a button in an inventory system, which may perform an action
 * upon being clicked and has criteria determining its visibility.
 * This class allows flexibility in defining the appearance and behavior
 * of inventory buttons through custom item stacks, click actions, and visibility criteria.
 */
public class InventoryButton implements InventoryButtonInterface {

    /**
     * Represents the visual item associated with the inventory button.
     * This item stack determines the appearance of the button
     * within the inventory UI.
     */
    @Getter private ItemStack stack;

    /**
     * Represents the action handler associated with the inventory button.
     * This field holds an implementation of the {@link InventoryClick} interface,
     * which defines the behavior that occurs when a player interacts with the button
     * in the inventory.
     * <p>
     * When the button is clicked, the specified click action is executed,
     * allowing custom behavior to be defined for individual buttons.
     */
    @Getter private InventoryClick click;

    /**
     * Represents the criteria used to determine the visibility of this inventory button.
     * The criteria define the conditions under which the button is visible to a player.
     * The logic for visibility is encapsulated within the associated {@link ButtonCriteria} implementation.
     */
    @Getter private ButtonCriteria criteria;

    /**
     * Constructs a new InventoryButton with the specified material and click action.
     * The button's visibility is determined by a default criteria.
     *
     * @param material The material representing the visual appearance of the button.
     * @param click The action to be executed when the button is clicked.
     */
    public InventoryButton(Material material, InventoryClick click) {
        this(material, click, new DefaultButtonCriteria());
    }

    /**
     * Constructs an InventoryButton with the specified material, click action,
     * and criteria for visibility.
     *
     * @param material The material representing the visual appearance of the button.
     * @param click The action handler to be executed when the button is clicked.
     * @param criteria The criteria used to determine the visibility of the button.
     */
    public InventoryButton(Material material, InventoryClick click, ButtonCriteria criteria) {
        this.stack = new ItemStack(material, 1);
        this.click = click;
        this.criteria = criteria;
    }

    /**
     * Constructs a new InventoryButton with the specified ItemStack and click action.
     * The button's visibility is determined by a default criteria.
     *
     * @param stack The ItemStack representing the visual appearance of the button.
     * @param click The action to be executed when the button is clicked.
     */
    public InventoryButton(ItemStack stack, InventoryClick click) {
        this(stack, click, new DefaultButtonCriteria());
    }

    /**
     * Constructs a new InventoryButton with the specified ItemStack, click action, and visibility criteria.
     *
     * @param stack The ItemStack representing the visual appearance of the button.
     * @param click The action handler to be executed when the button is clicked.
     * @param criteria The criteria used to determine the visibility of the button.
     */
    public InventoryButton(ItemStack stack, InventoryClick click, ButtonCriteria criteria) {
        this.stack = stack;
        this.click = click;
        this.criteria = criteria;
    }

    /**
     * Determines whether the button is visible to the given player based on predefined criteria.
     *
     * @param player The player for whom the visibility of the button is being determined.
     * @return true if the button is visible to the specified player; false otherwise.
     */
    @Override
    public boolean isVisible(CPlayer player) {
        return criteria.isVisible(player);
    }
}
