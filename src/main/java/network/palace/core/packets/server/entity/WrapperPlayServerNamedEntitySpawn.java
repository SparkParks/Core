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
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import network.palace.core.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Represents a wrapper for the Play.Server.NAMED_ENTITY_SPAWN packet type.
 * This packet is used to spawn a named entity, typically a player, within the game world.
 * It contains information such as the entity ID, position, rotation, UUID, and metadata.
 * Provides methods to manipulate and access the underlying data of the packet.
 */
public class WrapperPlayServerNamedEntitySpawn extends AbstractPacket {

    /**
     * Represents the packet type {@code PacketType.Play.Server.NAMED_ENTITY_SPAWN}.
     * This type is used to identify packets sent by the server when a named entity
     * is spawned in the game world.
     *
     * This constant is utilized within the {@code WrapperPlayServerNamedEntitySpawn}
     * class to define the specific packet type it wraps and operates on. The packet
     * is primarily used for operations related to named entities, enabling
     * modification and handling of their properties during server-client interactions.
     */
    public static final PacketType TYPE = PacketType.Play.Server.NAMED_ENTITY_SPAWN;

    /**
     * Constructs a wrapper for the Minecraft packet Play.Server.NAMED_ENTITY_SPAWN.
     * This packet is sent by the server to spawn a named entity, such as a player,
     * in the world.
     *
     * Initializes the packet with its associated data and assigns default values
     * to the packet's modifiers. The packet type associated with this wrapper is
     * {@code PacketType.Play.Server.NAMED_ENTITY_SPAWN}.
     *
     * Throws an IllegalArgumentException if the packet type does not match
     * the expected type or if the packet container is null.
     */
    public WrapperPlayServerNamedEntitySpawn() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Constructs a WrapperPlayServerNamedEntitySpawn wrapper for the Minecraft packet
     * Play.Server.NAMED_ENTITY_SPAWN. This packet is sent by the server to spawn a
     * named entity, such as a player, in the world.
     *
     * This constructor initializes the wrapper by validating the provided packet.
     * It ensures that the packet container matches the expected packet type
     * {@code PacketType.Play.Server.NAMED_ENTITY_SPAWN}.
     *
     * @param packet The packet container containing the relevant data for the
     *               named entity spawn packet. This must not be null and must
     *               match the PacketType.Play.Server.NAMED_ENTITY_SPAWN.
     *
     * @throws IllegalArgumentException if the packet is null or does not match
     *                                  the expected packet type.
     */
    public WrapperPlayServerNamedEntitySpawn(PacketContainer packet) {
        super(packet, TYPE);
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
     * @return the entity associated with this packet, or null if the entity could not be found.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieves the entity associated with this packet in the world of the player
     * involved in the specified packet event.
     *
     * @param event the packet event containing the player and world information.
     * @return the entity associated with this packet, or null if the entity could not be found.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieves the UUID of the player associated with this packet.
     *
     * @return The UUID of the player as a {@code UUID} object.
     */
    public UUID getPlayerUUID() {
        return handle.getUUIDs().read(0);
    }

    /**
     * Sets the UUID of the player associated with this packet.
     *
     * @param value the new UUID to be set for the player.
     */
    public void setPlayerUUID(UUID value) {
        handle.getUUIDs().write(0, value);
    }

    /**
     * Retrieves the position of the entity represented by this packet.
     * The position is returned as a {@link Vector} containing the
     * x, y, and z coordinates.
     *
     * @return A {@code Vector} representing the x, y, and z coordinates
     *         of the entity's position.
     */
    public Vector getPosition() {
        return new Vector(getX(), getY(), getZ());
    }

    /**
     * Sets the position of the entity using the given vector.
     * Updates the x, y, and z coordinates of the entity to match
     * the values provided in the {@code position} parameter.
     *
     * @param position A {@code Vector} object containing the x, y, and z
     *                 coordinates to set for the entity's position.
     */
    public void setPosition(Vector position) {
        setX(position.getX());
        setY(position.getY());
        setZ(position.getZ());
    }

    /**
     * Retrieves the X coordinate of the entity associated with this packet.
     *
     * @return The X coordinate as a double.
     */
    public double getX() {
        return handle.getDoubles().read(0);
    }

    /**
     * Sets the X coordinate of the spawned entity for this packet.
     *
     * @param value The new X coordinate to be set, as a double.
     */
    public void setX(double value) {
        handle.getDoubles().write(0, value);
    }

    /**
     * Retrieves the Y coordinate of the entity associated with this packet.
     *
     * @return The Y coordinate as a double.
     */
    public double getY() {
        return handle.getDoubles().read(1);
    }

    /**
     * Sets the Y coordinate for the entity associated with this packet.
     *
     * @param value The new Y coordinate to be set, as a double.
     */
    public void setY(double value) {
        handle.getDoubles().write(1, value);
    }

    /**
     * Retrieves the Z coordinate of the entity associated with this packet.
     *
     * @return The Z coordinate as a double.
     */
    public double getZ() {
        return handle.getDoubles().read(2);
    }

    /**
     * Sets the Z coordinate for the entity associated with this packet.
     *
     * @param value the new Z coordinate to be set, as a double.
     */
    public void setZ(double value) {
        handle.getDoubles().write(2, value);
    }

    /**
     * Retrieves the yaw angle of the spawned entity. The yaw represents the
     * horizontal rotation of the entity in degrees.
     *
     * @return The yaw of the entity as a float, where the value is derived from
     *         the entity's orientation, scaled from a range of 0-255 to 0-360 degrees.
     */
    public float getYaw() {
        return (handle.getBytes().read(0) * 360.F) / 256.0F;
    }

    /**
     * Sets the yaw angle of the entity in the packet. The yaw represents the entity's horizontal rotation
     * in degrees, and it is internally converted to a byte value ranging from 0 to 255 for the packet.
     *
     * @param value The new yaw value to be set, as a float. The value is expected to be in degrees (0-360),
     *              and it is scaled to fit within the range of 0-255 for internal representation.
     */
    public void setYaw(float value) {
        handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieves the pitch of the spawned entity. The pitch represents the vertical rotation of the entity in degrees.
     * The value is derived from the internal byte representation and scaled to fit within a range of 0-360 degrees.
     *
     * @return The pitch of the entity as a float, scaled to a range from 0 to 360 degrees.
     */
    public float getPitch() {
        return (handle.getBytes().read(1) * 360.F) / 256.0F;
    }

    /**
     * Sets the pitch angle of the entity in the packet. The pitch represents the vertical rotation
     * of the entity in degrees. The value is internally converted to a byte ranging from 0 to 255
     * for the packet.
     *
     * @param value The new pitch value to be set, as a float. The value is expected to be in degrees (0-360),
     *              and it is scaled to fit within the range of 0-255 for internal representation.
     */
    public void setPitch(float value) {
        handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieves the metadata associated with the entity represented in the packet.
     *
     * @return A WrappedDataWatcher object, which contains the metadata information for the entity.
     */
    public WrappedDataWatcher getMetadata() {
        return handle.getDataWatcherModifier().read(0);
    }

    /**
     * Updates the metadata associated with the entity represented by this packet.
     * The metadata is used to modify and store various attributes or properties
     * of the entity, including custom data such as names, status effects, or
     * other client-side visual or functional properties.
     *
     * @param value A {@code WrappedDataWatcher} object that contains the metadata
     *              information to be set for the entity.
     */
    public void setMetadata(WrappedDataWatcher value) {
        handle.getDataWatcherModifier().write(0, value);
    }
}