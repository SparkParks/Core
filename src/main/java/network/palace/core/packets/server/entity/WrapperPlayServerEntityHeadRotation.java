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
 * Wrapper for the Play.Server.ENTITY_HEAD_ROTATION packet.
 * This packet is responsible for handling the entity's head rotation.
 * Provides methods to retrieve and modify the entity ID and head yaw.
 */
public class WrapperPlayServerEntityHeadRotation extends AbstractPacket {

    /**
     * Represents the packet type {@code PacketType.Play.Server.ENTITY_HEAD_ROTATION}.
     * This type is used to handle packets associated with an entity's head rotation.
     */
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_HEAD_ROTATION;

    /**
     * Constructs a new wrapper for the Play.Server.ENTITY_HEAD_ROTATION packet type.
     * This wrapper is used to handle and modify packets associated with an entity's
     * head rotation.
     *
     * The packet type associated with this wrapper is {@code PacketType.Play.Server.ENTITY_HEAD_ROTATION}.
     *
     * Initializes the packet with its associated data and assigns default values
     * to the packet's modifiers.
     *
     * Throws an IllegalArgumentException if the packet type does not match
     * the expected type or if the packet container is null.
     */
    public WrapperPlayServerEntityHeadRotation() {
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
     * Retrieves the head yaw of the entity associated with this packet.
     *
     * @return The head yaw as a byte, representing the rotational angle of the entity's head.
     */
    public byte getHeadYaw() {
        return handle.getBytes().read(0);
    }

    /**
     * Sets the head yaw rotation for the entity associated with this packet.
     * The provided value represents the rotational angle in degrees, which will
     * be normalized and converted to a byte representation before being set in the packet.
     *
     * @param value the head yaw angle in degrees (0 to 360). The value will be converted
     *              internally to a byte used by the packet.
     */
    public void setHeadYaw(float value) {
        handle.getBytes().write(0, (byte) (value * 256f / 360f));
    }
}
