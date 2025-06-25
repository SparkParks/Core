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
 * WrapperPlayServerEntityVelocity is a packet wrapper for the server-side
 * ENTITY_VELOCITY packet. It is used to manipulate and retrieve data
 * related to the velocity of entities in the game world.
 * The packet encapsulates the entity's ID and its velocities along the
 * x, y, and z axes.
 */
public class WrapperPlayServerEntityVelocity extends AbstractPacket {

    /**
     * Represents the packet type {@code PacketType.Play.Server.ENTITY_VELOCITY}.
     * This constant is used to identify and handle packets related to the server-side
     * ENTITY_VELOCITY event, which manages the velocity information of entities in the game.
     */
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_VELOCITY;

    /**
     * Wrapper class for the Minecraft packet Play.Server.ENTITY_VELOCITY.
     * This packet is sent by the server to update an entity's velocity.
     *
     * The class initializes the packet container with the required data and
     * assigns default values to its modifiers. The packet type associated
     * with this wrapper is {@code PacketType.Play.Server.ENTITY_VELOCITY}.
     *
     * Throws an IllegalArgumentException if the packet type does not match
     * the expected type or if the packet container is null.
     */
    public WrapperPlayServerEntityVelocity() {
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
     * @param world the world in which the entity exists
     * @return The entity associated with this packet, or null if the entity could not be found
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieves the entity associated with this packet in the world of the player
     * involved in the specified packet event.
     *
     * @param event the packet event containing the player and world information
     * @return The entity associated with this packet, or null if the entity could not be found
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieves the velocity of the entity along the X-axis.
     *
     * The velocity is stored as an integer in the packet and is divided by 8000.0 to
     * convert it into a value representing blocks per tick.
     *
     * @return The current X-axis velocity of the entity as a double.
     */
    public double getVelocityX() {
        return handle.getIntegers().read(1) / 8000.0D;
    }

    /**
     * Sets the velocity of the entity along the X-axis.
     *
     * The velocity is stored as an integer in the packet after being multiplied by 8000.0.
     *
     * @param value The new velocity value for the X-axis, represented as a double,
     *              where the value is in blocks per tick.
     */
    public void setVelocityX(double value) {
        handle.getIntegers().write(1, (int) (value * 8000.0D));
    }

    /**
     * Retrieves the velocity of the entity along the Y-axis.
     *
     * The velocity is stored as an integer in the packet and is divided by 8000.0
     * to convert it into a value representing blocks per tick.
     *
     * @return The current Y-axis velocity of the entity as a double.
     */
    public double getVelocityY() {
        return handle.getIntegers().read(2) / 8000.0D;
    }

    /**
     * Sets the velocity of the entity along the Y-axis.
     *
     * The velocity is stored as an integer in the packet after being multiplied by 8000.0.
     *
     * @param value The new velocity value for the Y-axis, represented as a double,
     *              where the value is in blocks per tick.
     */
    public void setVelocityY(double value) {
        handle.getIntegers().write(2, (int) (value * 8000.0D));
    }

    /**
     * Retrieves the velocity of the entity along the Z-axis.
     *
     * The velocity is stored as an integer in the packet and is divided by 8000.0
     * to convert it into a value representing blocks per tick.
     *
     * @return The current Z-axis velocity of the entity as a double.
     */
    public double getVelocityZ() {
        return handle.getIntegers().read(3) / 8000.0D;
    }

    /**
     * Sets the velocity of the entity along the Z-axis.
     *
     * The velocity is stored as an integer in the packet after being multiplied by 8000.0.
     *
     * @param value The new velocity value for the Z-axis, represented as a double,
     */
    public void setVelocityZ(double value) {
        handle.getIntegers().write(3, (int) (value * 8000.0D));
    }
}
