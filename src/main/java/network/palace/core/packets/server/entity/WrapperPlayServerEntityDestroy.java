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
import network.palace.core.packets.AbstractPacket;

/**
 * Wrapper class for the Minecraft packet Play.Server.ENTITY_DESTROY.
 * This packet is sent by the server to indicate the destruction of one or more entities.
 */
public class WrapperPlayServerEntityDestroy extends AbstractPacket {

    /**
     * Represents the packet type {@code PacketType.Play.Server.ENTITY_DESTROY}.
     * This type is used to identify packets sent by the server when one or more entities
     * are destroyed. It is a constant identifier for the destruction packet in Minecraft's
     * Play.Server protocol.
     */
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_DESTROY;

    /**
     * Constructs a new wrapper for the Play.Server.ENTITY_DESTROY packet type.
     * This wrapper is used to handle and modify packets sent by the server
     * when one or more entities are destroyed.
     *
     * Initializes the packet with its associated data and assigns default values
     * to the packet's modifiers.
     *
     * The packet type associated with this wrapper is {@code PacketType.Play.Server.ENTITY_DESTROY}.
     *
     * Throws an IllegalArgumentException if the packet type does not match
     * the expected type or if the packet container is null.
     */
    public WrapperPlayServerEntityDestroy() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Retrieves the number of entity IDs present in the packet.
     *
     * @return The count of entity IDs.
     */
    public int getCount() {
        return handle.getIntegerArrays().read(0).length;
    }

    /**
     * Retrieves the entity IDs contained in the packet.
     *
     * @return An array of integers representing the IDs of the entities being referenced or destroyed.
     */
    public int[] getEntityIDs() {
        return handle.getIntegerArrays().read(0);
    }

    /**
     * Sets the entity IDs associated with this packet. These IDs represent
     * the entities being referenced or destroyed in the Minecraft server packet.
     *
     * @param value An array of integers representing the entity IDs to set.
     */
    public void setEntityIds(int[] value) {
        handle.getIntegerArrays().write(0, value);
    }
}
