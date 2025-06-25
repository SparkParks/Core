/*
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package network.palace.core.packets.server.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import network.palace.core.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * Wrapper for the server-side packet ENTITY_EQUIPMENT.
 * This packet is sent to update an entity's equipped items.
 * The packet includes information such as the entity ID, the slot being updated, and the item in the slot.
 */
public class WrapperPlayServerEntityEquipment extends AbstractPacket {

    /**
     * Represents the packet type {@code PacketType.Play.Server.ENTITY_EQUIPMENT}.
     * This constant is used to identify packets sent by the server to update
     * an entity's equipped items, such as armor or held items. The packet contains
     * information about the entity ID, the equipment slot being updated,
     * and the item in the specified slot.
     */
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_EQUIPMENT;

    /**
     * Constructs a new wrapper for the Play.Server.ENTITY_EQUIPMENT packet type.
     * This wrapper is used to handle and modify packets sent by the server
     * to update an entity's equipped items.
     *
     * Initializes the packet with its associated data and assigns default
     * values to the packet's modifiers.
     *
     * The packet type associated with this wrapper is {@code PacketType.Play.Server.ENTITY_EQUIPMENT}.
     *
     * Throws an IllegalArgumentException if the packet type does not match
     * the expected type or if the packet container is null.
     */
    public WrapperPlayServerEntityEquipment() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Retrieves the ID of the entity associated with this packet.
     *
     * @return The ID of the entity as an integer.
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Sets the ID of the entity associated with this packet.
     *
     * @param value the new entity ID to be set.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieves the entity associated with this packet in the specified world.
     *
     * @param world the world in which the entity exists.
     * @return The entity associated with this packet, or null if the entity could not be found.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieves the entity associated with this packet in the world of the player
     * involved in the specified packet event.
     *
     * @param event the packet event containing the player and world information.
     * @return The entity associated with this packet, or null if the entity could not be found.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieves the item slot from the packet.
     *
     * @return The {@code ItemSlot} representing the current slot in the packet.
     */
    public ItemSlot getSlot() {
        return handle.getItemSlots().read(0);
    }

    /**
     * Sets the item slot in the packet.
     *
     * @param value the {@code ItemSlot} representing the new slot to be set in the packet.
     */
    public void setSlot(ItemSlot value) {
        handle.getItemSlots().write(0, value);
    }

    /**
     * Retrieves the item associated with this packet.
     *
     * @return The {@code ItemStack} representing the item currently set in the packet.
     */
    public ItemStack getItem() {
        return handle.getItemModifier().read(0);
    }

    /**
     * Sets the item associated with this packet.
     *
     * @param value the {@code ItemStack} representing the item to be set in the packet.
     */
    public void setItem(ItemStack value) {
        handle.getItemModifier().write(0, value);
    }
}
