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
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import network.palace.core.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

/**
 * A wrapper class for the Play.Server.ENTITY_METADATA packet in Minecraft.
 * This packet is typically used to update the metadata of an entity, such as
 * its properties, state, or other associated data.
 * The class provides methods to manipulate and retrieve the entity metadata,
 * as well as the entity ID associated with the packet.
 */
public class WrapperPlayServerEntityMetadata extends AbstractPacket {

    /**
     * Represents the packet type {@code PacketType.Play.Server.ENTITY_METADATA}.
     * This constant is used to identify packets that update the metadata of an entity,
     * including its state, properties, or associated data.
     */
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_METADATA;

    /**
     * Constructs a new wrapper instance for the Play.Server.ENTITY_METADATA packet type.
     * This wrapper is used to handle and modify packets sent by the server
     * to update the metadata of an entity. Metadata includes properties, states,
     * or other associated data of the entity.
     *
     * Initializes the packet with its associated data and assigns default values
     * to the packet's modifiers.
     *
     * The packet type associated with this wrapper is {@code PacketType.Play.Server.ENTITY_METADATA}.
     *
     * Throws an IllegalArgumentException if the packet type does not match the
     * expected type or if the packet container is null.
     */
    public WrapperPlayServerEntityMetadata() {
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
     * @param world the world in which the entity exists. Must not be null.
     * @return The entity associated with this packet, or null if the entity could not be found.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieves the entity associated with this packet in the world of the player
     * involved in the specified packet event.
     *
     * @param event the packet event containing the player and world information. Must not be null.
     * @return The entity associated with this packet, or null if the entity could not be found.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieves the metadata associated with an entity.
     * Metadata contains various properties and states of the entity.
     *
     * @return A list of {@code WrappedWatchableObject} representing the entity's metadata.
     */
    public List<WrappedWatchableObject> getMetadata() {
        return handle.getWatchableCollectionModifier().read(0);
    }

    /**
     * Sets the metadata associated with an entity.
     * Metadata contains various properties and states of the entity.
     *
     * @param value a list of {@code WrappedWatchableObject} representing
     *              the metadata to be associated with the entity.
     *              Must not be null.
     */
    public void setMetadata(List<WrappedWatchableObject> value) {
        handle.getWatchableCollectionModifier().write(0, value);
    }
}
