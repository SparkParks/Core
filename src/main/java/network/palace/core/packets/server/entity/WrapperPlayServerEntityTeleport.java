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
 * Represents a wrapper for the server-side "Entity Teleport" packet.
 * Used to handle and manipulate packet data related to entity teleportation events
 * in Minecraft. This includes accessing and modifying the entity's position,
 * orientation, and on-ground status.
 */
public class WrapperPlayServerEntityTeleport extends AbstractPacket {

    /**
     * Represents the packet type {@code PacketType.Play.Server.ENTITY_TELEPORT}.
     * This packet type is used to identify packets sent by the server
     * when an entity is teleported in the game.
     */
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_TELEPORT;

    /**
     * WrapperPlayServerEntityTeleport is a packet wrapper designed to handle entity teleportation in Minecraft.
     * This constructor initializes a new instance of the packet wrapper and sets default modifier values.
     * The packet corresponds to the "ENTITY_TELEPORT" packet type and represents teleportation events
     * for entities to a specific position and orientation within the game world.
     *
     * The wrapper ensures proper handling of packet data and provides methods for accessing and modifying
     * attributes such as position coordinates (X, Y, Z), orientation (yaw and pitch), the on-ground status,
     * and associated entity information.
     *
     * The default packet type and underlying packet structure are initialized upon construction.
     * This ensures the packet is properly prepared before any further modifications or handling.
     *
     * The packet type is verified and validated using the superclass.
     *
     * @throws IllegalArgumentException if the provided packet handle is null or the packet type does not match
     *                                  the expected type for an ENTITY_TELEPORT packet.
     */
    public WrapperPlayServerEntityTeleport() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Retrieves the ID of the entity associated with this packet.
     *
     * @return The entity ID as an integer.
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
     * @return the entity associated with this packet, or null if the entity could not be found
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieves the entity associated with this packet in the world of the player
     * involved in the specified packet event.
     *
     * @param event the packet event containing the player and world information
     * @return the entity associated with this packet, or null if the entity could not be found
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieves the X-coordinate of the entity's position associated with the packet.
     *
     * @return The X-coordinate of the entity as a double.
     */
    public double getX() {
        return handle.getDoubles().read(0);
    }

    /**
     * Sets the X-coordinate of the entity's position associated with the packet.
     *
     * @param value The new X-coordinate of the entity as a double.
     */
    public void setX(double value) {
        handle.getDoubles().write(0, value);
    }

    /**
     * Retrieves the Y-coordinate of the entity's position associated with the packet.
     *
     * @return The Y-coordinate of the entity as a double.
     */
    public double getY() {
        return handle.getDoubles().read(1);
    }

    /**
     * Sets the Y-coordinate of the entity's position associated with the packet.
     *
     * @param value The new Y-coordinate of the entity as a double.
     */
    public void setY(double value) {
        handle.getDoubles().write(1, value);
    }

    /**
     * Retrieves the Z-coordinate of the entity's position associated with the packet.
     *
     * @return The Z-coordinate of the entity as a double.
     */
    public double getZ() {
        return handle.getDoubles().read(2);
    }

    /**
     * Sets the Z-coordinate of the entity's position associated with the packet.
     *
     * @param value The new Z-coordinate of the entity as a double.
     */
    public void setZ(double value) {
        handle.getDoubles().write(2, value);
    }

    /**
     * Retrieves the yaw (horizontal rotation) of the entity's orientation.
     *
     * The yaw is calculated based on the first byte of the packet data, scaled
     * to a range of 0 to 360 degrees.
     *
     * @return The yaw of the entity as a floating-point number in degrees.
     */
    public float getYaw() {
        return (handle.getBytes().read(0) * 360.F) / 256.0F;
    }

    /**
     * Sets the yaw (horizontal rotation) of the entity's orientation.
     *
     * The yaw value is scaled from a float representing degrees (0-360)
     * to a byte value used in the packet. This byte value corresponds to yaw
     * as a fraction of 256.
     *
     * @param value The new yaw of the entity as a floating-point number in degrees.
     */
    public void setYaw(float value) {
        handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieves the pitch (vertical rotation) of the entity's orientation.
     *
     * The pitch is calculated based on the second byte of the packet data, scaled
     * to a range of 0 to 360 degrees.
     *
     * @return The pitch of the entity as a floating-point number in degrees.
     */
    public float getPitch() {
        return (handle.getBytes().read(1) * 360.F) / 256.0F;
    }

    /**
     * Sets the pitch (vertical rotation) of the entity's orientation.
     *
     * The pitch value is scaled from a float representing degrees (0-360)
     * to a byte value used in the packet. This byte value corresponds to
     * pitch as a fraction of 256.
     *
     * @param value The new pitch of the entity as a floating-point number in degrees.
     */
    public void setPitch(float value) {
        handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieves the on-ground status of the entity associated with this packet.
     *
     * @return {@code true} if the entity is on the ground, {@code false} otherwise.
     */
    public boolean getOnGround() {
        return handle.getBooleans().read(0);
    }

    /**
     * Sets the on-ground status of the entity associated with this packet.
     *
     * @param value {@code true} if the entity is on the ground, {@code false} otherwise.
     */
    public void setOnGround(boolean value) {
        handle.getBooleans().write(0, value);
    }
}
