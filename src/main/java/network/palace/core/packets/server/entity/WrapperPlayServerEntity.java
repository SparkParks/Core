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
 * Represents a wrapper for the Play.Server.ENTITY packet in Minecraft.
 * This class provides methods to manipulate and access packet data such as
 * the associated entity's ID and entity object within the context of a server world or event.
 */
public class WrapperPlayServerEntity extends AbstractPacket {

    /**
     * Represents the packet type {@code PacketType.Play.Server.ENTITY}.
     * This constant is used to identify packets related to entity-based events
     * in the Play.Server protocol.
     */
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY;

    /**
     * Constructs a new wrapper for the Play.Server.ENTITY packet type.
     * This wrapper is used to handle and modify packets related to entities
     * in the server.
     *
     * Initializes the packet with its associated data and assigns default values
     * to the packet's modifiers.
     *
     * The packet type associated with this wrapper is {@code PacketType.Play.Server.ENTITY}.
     *
     * Throws an IllegalArgumentException if the packet type does not match
     * the expected type or if the packet container is null.
     */
    public WrapperPlayServerEntity() {
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
}
