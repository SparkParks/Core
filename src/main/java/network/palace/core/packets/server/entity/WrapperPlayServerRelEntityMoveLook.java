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
 * Wrapper for the Play.Server.REL_ENTITY_MOVE_LOOK packet.
 * This packet is used to update the relative position, rotation, and ground state of an entity.
 * It provides methods to get and set the entity's ID, movement deltas, rotation values, and
 * whether the entity is on the ground.
 */
public class WrapperPlayServerRelEntityMoveLook extends AbstractPacket {

    /**
     * Represents the packet type {@code PacketType.Play.Server.REL_ENTITY_MOVE_LOOK}.
     * This type is used to identify packets sent by the server to update
     * an entity's position and rotation in the world.
     */
    public static final PacketType TYPE = PacketType.Play.Server.REL_ENTITY_MOVE_LOOK;

    /**
     * Wrapper class for the Minecraft packet Play.Server.REL_ENTITY_MOVE_LOOK.
     * This packet is sent by the server to update an entity's relative position and look direction.
     *
     * This class provides functionality to handle and modify the data within the REL_ENTITY_MOVE_LOOK packet.
     * It initializes the packet with default values and ensures the proper packet type is being handled.
     *
     * The packet type associated with this wrapper is {@code PacketType.Play.Server.REL_ENTITY_MOVE_LOOK}.
     *
     * Constructs the wrapper instance with a new PacketContainer of the given packet type.
     * By default, all modifiers are assigned their default values as per the Comphenix Protocol specifications.
     *
     * Throws an IllegalArgumentException during initialization if the provided packet type does not match
     * the expected {@code PacketType.Play.Server.REL_ENTITY_MOVE_LOOK} or if the packet container is null.
     */
    public WrapperPlayServerRelEntityMoveLook() {
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
     * Retrieves the movement offset in the X-axis (dx) for the entity.
     * The value is derived from the packet data and represents the
     * relative change in position scaled by 1/4096.
     *
     * @return The movement offset in the X-axis as a double.
     */
    public double getDx() {
        return handle.getIntegers().read(1) / 4096D;
    }

    /**
     * Sets the movement offset in the X-axis (dx) for the entity.
     * The value is scaled by a factor of 4096 to match the internal packet representation.
     *
     * @param value the new movement offset in the X-axis as a double.
     */
    public void setDx(double value) {
        handle.getIntegers().write(1, (int) (value * 4096));
    }

    /**
     * Retrieves the movement offset in the Y-axis (dy) for the entity.
     * The value is derived from the packet data and represents the
     * relative change in position scaled by 1/4096.
     *
     * @return The movement offset in the Y-axis as a double.
     */
    public double getDy() {
        return handle.getIntegers().read(2) / 4096D;
    }

    /**
     * Sets the movement offset in the Y-axis (dy) for the entity.
     * The value is scaled by a factor of 4096 to match the internal packet representation.
     *
     * @param value the new movement offset in the Y-axis as a double.
     */
    public void setDy(double value) {
        handle.getIntegers().write(2, (int) (value * 4096));
    }

    /**
     * Retrieves the movement offset in the Z-axis (dz) for the entity.
     * The value is derived from the packet data and represents the
     * relative change in position scaled by 1/4096.
     *
     * @return The movement offset in the Z-axis as a double.
     */
    public double getDz() {
        return handle.getIntegers().read(3) / 4096D;
    }

    /**
     * Sets the movement offset in the Z-axis (dz) for the entity.
     * The value is scaled by a factor of 4096 to match the internal packet representation.
     *
     * @param value the new movement offset in the Z-axis as a double.
     */
    public void setDz(double value) {
        handle.getIntegers().write(3, (int) (value * 4096));
    }

    /**
     * Retrieves the yaw value of the entity.
     * The yaw is calculated based on the raw byte data retrieved from the packet.
     *
     * @return The yaw value of the entity as a float, represented in degrees.
     */
    public float getYaw() {
        return (handle.getBytes().read(0) * 360.F) / 256.0F;
    }

    /**
     * Sets the yaw value for the entity.
     * The yaw value represents the direction the entity is facing, measured in degrees.
     * Internally, the value is converted and stored as a byte based on a range of 0 to 256.
     *
     * @param value the yaw value to set, as a float in degrees. The value is expected to be in the range of 0.0 to 360.0.
     */
    public void setYaw(float value) {
        handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieves the pitch value of the entity.
     * The pitch is calculated based on the raw byte data retrieved from the packet.
     *
     * @return The pitch value of the entity as a float, represented in degrees.
     */
    public float getPitch() {
        return (handle.getBytes().read(1) * 360.F) / 256.0F;
    }

    /**
     * Sets the pitch value for the entity.
     * The pitch value represents the vertical rotation of the entity, measured in degrees.
     * Internally, the value is converted and stored as a byte based on a range of 0 to 256.
     *
     * @param value the pitch value to set, as a float in degrees. The value is expected to be in the range of 0.0 to 360.0.
     */
    public void setPitch(float value) {
        handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
    }

    /**
     * Determines whether the entity is currently on the ground.
     * This represents the "on ground" flag within the packet.
     *
     * @return true if the entity is on the ground, false otherwise.
     */
    public boolean getOnGround() {
        return handle.getBooleans().read(0);
    }

    /**
     * Sets the "on ground" status of the entity.
     * This modifies the "on ground" flag in the packet, which is used to indicate
     * whether the entity is currently on the ground.
     *
     * @param value true if the entity is on the ground, false otherwise.
     */
    public void setOnGround(boolean value) {
        handle.getBooleans().write(0, value);
    }
}
