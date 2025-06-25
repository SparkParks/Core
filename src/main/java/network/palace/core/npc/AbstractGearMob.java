package network.palace.core.npc;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import network.palace.core.packets.server.entity.WrapperPlayServerEntityEquipment;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Represents an abstract implementation of a gear-based mob entity.
 * This class extends {@link AbstractMob} to include equipment functionality
 * such as items held in the main hand, off hand, and wearable items like armor.
 * Intended to be subclassed for specific gear-based mob implementations.
 */
public abstract class AbstractGearMob extends AbstractMob {

    /**
     * Represents the item currently held in the main hand of the mob entity.
     * This item is typically used for attacks or interactions and can be
     * updated dynamically during the mob's lifecycle.
     *
     * In the context of {@code AbstractGearMob}, this field is synced with
     * observers to update the mob's equipment slot visually.
     */
    private ItemStack mainHand;

    /**
     * Represents the item currently held in the mob's off-hand.
     * This item is typically used for secondary actions or interactions
     * and can be updated dynamically during the mob's lifecycle.
     *
     * Within the context of {@code AbstractGearMob}, this field
     * is synchronized with observers to visually update the mob's
     * off-hand equipment slot.
     */
    private ItemStack offHand;

    /**
     * Represents the helmet item equipped by the gear-based mob entity.
     * This item occupies the head equipment slot and can be dynamically
     * updated during the mob's lifecycle.
     *
     * In the context of {@code AbstractGearMob}, the helmet is synchronized
     * with observers to visually reflect the mob's equipped headgear.
     */
    private ItemStack helmet;

    /**
     * Represents the chestplate item equipped by the mob.
     * This item can be updated to modify the visual representation or functionality
     * of the mob's gear in the game world.
     */
    private ItemStack chestplate;

    /**
     * Represents the leggings gear equipped by this mob.
     * This item determines the appearance and potentially the
     * protective or other special attributes of the leggings
     * that the mob is wearing in-game.
     *
     * The {@code leggings} field is part of the mob's overall
     * gear configuration, which typically includes other items
     * such as helmet, chestplate, boots, and hand-held items.
     *
     * This field can be updated using the {@code setLeggings} method.
     */
    private ItemStack leggings;

    /**
     * Represents the boots equipped by the {@code AbstractGearMob} entity.
     * This field is used to define or update the boots item worn by the mob.
     * The boots can affect the mob's appearance, attributes, or other mechanics,
     * depending on their properties or enchantments.
     */
    private ItemStack boots;

    /**
     * Constructs an AbstractGearMob instance with a specified location, set of observers,
     * and title. Inherits from AbstractMob.
     *
     * @param location the location of the gear mob
     * @param observers the set of observers associated with the gear mob
     * @param title the title of the gear mob
     */
    public AbstractGearMob(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    /**
     * Sets the main hand item of the gear mob.
     *
     * @param stack the ItemStack to set as the main hand item
     */
    public void setMainHand(ItemStack stack) {
        mainHand = stack;
        updateSlot(ItemSlot.MAINHAND, mainHand);
    }

    /**
     * Sets the off-hand item for the gear mob.
     *
     * @param stack the ItemStack to set as the off-hand item
     */
    public void setOffHand(ItemStack stack) {
        offHand = stack;
        updateSlot(ItemSlot.OFFHAND, offHand);
    }

    /**
     * Sets the helmet item for the gear mob.
     *
     * @param stack the ItemStack to set as the helmet
     */
    public void setHelmet(ItemStack stack) {
        helmet = stack;
        updateSlot(ItemSlot.HEAD, helmet);
    }

    /**
     * Sets the chestplate item for the gear mob.
     *
     * @param stack the ItemStack to set as the chestplate
     */
    public void setChestplate(ItemStack stack) {
        chestplate = stack;
        updateSlot(ItemSlot.CHEST, chestplate);
    }

    /**
     * Sets the leggings item for the gear mob.
     *
     * @param stack the ItemStack to set as the leggings
     */
    public void setLeggings(ItemStack stack) {
        leggings = stack;
        updateSlot(ItemSlot.LEGS, leggings);

    }

    /**
     * Sets the boots item for the gear mob.
     *
     * @param stack the ItemStack to set as the boots
     */
    public void setBoots(ItemStack stack) {
        boots = stack;
        updateSlot(ItemSlot.FEET, boots);
    }

    /**
     * Performs an update of this mob's equipment by synchronizing its item slots
     * with the associated item stacks. This ensures that the visual representation
     * of the mob's gear is updated for all observers.
     *
     * The method updates the following slots:
     * - Main Hand
     * - Off Hand
     * - Head
     * - Chest
     * - Legs
     * - Feet
     *
     * This method overrides {@link AbstractMob#onUpdate()} to include additional
     * functionality specific to gear synchronization. The parent class's update
     * logic is invoked as part of this process to ensure inherited behavior is preserved.
     */
    @Override
    protected void onUpdate() {
        super.onUpdate();
        updateSlot(ItemSlot.MAINHAND, mainHand);
        updateSlot(ItemSlot.OFFHAND, offHand);
        updateSlot(ItemSlot.HEAD, helmet);
        updateSlot(ItemSlot.CHEST, chestplate);
        updateSlot(ItemSlot.LEGS, leggings);
        updateSlot(ItemSlot.FEET, boots);
    }

    /**
     * Updates the specified equipment slot of the entity with the provided item stack.
     * Sends a packet to all observing players to synchronize the equipment update.
     *
     * @param slot the equipment slot to be updated
     * @param itemStack the item stack to set in the specified slot; if null, the update is skipped
     */
    public void updateSlot(ItemSlot slot, ItemStack itemStack) {
        if (itemStack == null) return;
        WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment();
        wrapper.setEntityID(getEntityId());
        wrapper.setSlot(slot);
        wrapper.setItem(itemStack);
        for (CPlayer player : viewers) {
            wrapper.sendPacket(player);
        }
    }
}
