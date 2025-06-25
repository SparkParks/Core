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

import network.palace.core.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

/**
 * Represents a wrapper for the "Play.Server.ENTITY_STATUS" packet in Minecraft.
 * This packet is used to update the status of an entity for a player.
 * Provides methods for reading and modifying the entity ID and status within the packet.
 */
public class WrapperPlayServerEntityStatus extends AbstractPacket {

    /**
     * Represents the packet type Play.Server.ENTITY_STATUS in the Minecraft protocol.
     * This packet type is utilized to update the status of an entity for a player.
     * It serves as a unique identifier for packets related to entity status updates.
     */
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_STATUS;

    /**
     * Constructs a new wrapper for the Play.Server.ENTITY_STATUS packet type.
     * This wrapper is used for handling and modifying packets that are sent by the server
     * to update the status of an entity for a player.
     *
     * Initializes the packet with its associated type and assigns default values
     * to the packet's modifiers.
     *
     * The packet type associated with this wrapper is {@code PacketType.Play.Server.ENTITY_STATUS}.
     *
     * Throws an IllegalArgumentException if the packet type does not match
     * the expected type or if the packet container is null.
     */
    public WrapperPlayServerEntityStatus() {
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
     * @param value The new entity ID to be set.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieves the entity associated with the given world.
     *
     * @param world the world in which the entity exists.
     * @return The entity associated with the specified world, or null if the entity could not be found.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieves the entity associated with the packet event.
     *
     * @param event the packet event containing the player and world information.
     * @return The entity associated with the specified packet event's world, or null if the entity could not be found.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieves the status of the entity associated with this packet.
     *
     * @return The status of the entity as a byte.
     */
    public byte getEntityStatus() {
        return handle.getBytes().read(0);
    }

    /**
     * Sets the status of the entity associated with the packet.
     *
     * @param value The new status value to be set for the entity. This value is interpreted
     *              as a byte by the protocol to represent different entity states or events.
     */
    public void setEntityStatus(int value) {
        handle.getBytes().write(0, (byte) value);
    }
}
