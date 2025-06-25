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
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import network.palace.core.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * Represents a wrapper for the server-side packet that spawns a living entity in the world.
 * Used to handle, read, and modify the details of a living entity spawn packet.
 */
public class WrapperPlayServerSpawnEntityLiving extends AbstractPacket {

    /**
     * Represents the packet type {@code PacketType.Play.Server.SPAWN_ENTITY_LIVING}.
     * This type is used to identify packets sent by the server when a living entity
     * is spawned in the world.
     */
    public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY_LIVING;

    /**
     * Represents a reusable packet constructor for creating the SpawnEntityLiving
     * packet with predefined configurations or customizations.
     *
     * This static variable enables efficient packet creation by caching the
     * construction process, avoiding redundant packet instance generation.
     * It is commonly used internally for initializing or modifying packets
     * related to spawning living entities.
     *
     * The cached constructor is specific to the context of the
     * WrapperPlayServerSpawnEntityLiving class, which handles packets for spawning
     * entities such as mobs or NPCs in a Minecraft server.
     *
     * This variable is initialized and utilized when custom packet creation
     * logic is required for spawning specific entities.
     */
    private static PacketConstructor entityConstructor;

    /**
     * Constructs a new WrapperPlayServerSpawnEntityLiving instance.
     * This wrapper is used to handle the packet responsible for spawning entities
     * of type living in the Minecraft world.
     *
     * The constructor initializes a packet container with the required packet type
     * and sets its default modifier values.
     */
    public WrapperPlayServerSpawnEntityLiving() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Constructs a new WrapperPlayServerSpawnEntityLiving instance from the provided entity.
     * This constructor initializes the packet with the given entity's data, allowing it
     * to represent a packet responsible for spawning a living entity in the Minecraft world.
     *
     * @param entity the entity to be represented in the spawn packet. This must be a valid
     *               {@code Entity} instance whose data will be used to populate the packet.
     */
    public WrapperPlayServerSpawnEntityLiving(Entity entity) {
        super(fromEntity(entity), TYPE);
    }

    /**
     * Constructs a packet container to spawn a living entity based on the provided entity.
     * If the packet constructor has not been initialized, it will be created using the
     * specified entity and the packet type.
     *
     * @param entity the entity to be used for populating the packet. It must be a valid
     *               instance of {@code Entity}.
     * @return a {@code PacketContainer} initialized with the data of the specified entity.
     */
    private static PacketContainer fromEntity(Entity entity) {
        if (entityConstructor == null) {
            entityConstructor = ProtocolLibrary.getProtocolManager().createPacketConstructor(TYPE, entity);
        }
        return entityConstructor.createPacket(entity);
    }

    /**
     * Retrieves the entity ID of the living entity that is being spawned.
     *
     * @return The entity ID of the spawned living entity as an integer.
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Retrieves the entity associated with the world and this packet.
     *
     * @param world the world in which the entity resides. This must be a valid {@code World} instance.
     * @return the entity associated with the given world or {@code null} if no entity could be found.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieves the entity associated with this packet event.
     *
     * @param event the packet event containing the player and associated world information. Must be a valid {@code PacketEvent} instance.
     * @return the entity associated with the event's world or {@code null} if no entity could be found.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieves the unique identifier (UUID) of the entity associated with this packet.
     * The UUID is used to uniquely identify the entity in the game world.
     *
     * @return The unique identifier (UUID) of the entity as a {@code UUID} object.
     */
    public UUID getUniqueId() {
        return handle.getUUIDs().read(0);
    }

    /**
     * Updates the unique identifier (UUID) of the entity associated with this packet.
     * The UUID is used to uniquely identify the entity in the game world.
     *
     * @param value the new unique identifier (UUID) to assign to the entity. Must be a valid {@code UUID} object.
     */
    public void setUniqueId(UUID value) {
        handle.getUUIDs().write(0, value);
    }

    /**
     * Sets the entity ID for this packet.
     *
     * @param value the entity ID to assign. This must be a valid integer representing
     *              the unique identifier of the entity in the game.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieves the type of the entity represented by this packet.
     * This method converts the data from the underlying packet to an EntityType.
     *
     * @return The type of entity as an {@code EntityType}.
     */
    @SuppressWarnings("deprecation")
    public EntityType getType() {
        return EntityType.fromId(handle.getIntegers().read(1));
    }

    /**
     * Sets the type of entity represented by this packet.
     * This method writes the provided EntityType's type ID to the packet data.
     *
     * @param value the entity type to assign. This must be a valid {@code EntityType} instance,
     *              where {@code value.getTypeId()} represents the type ID of the entity.
     */
    @SuppressWarnings("deprecation")
    public void setType(EntityType value) {
        handle.getIntegers().write(1, (int) value.getTypeId());
    }

    /**
     * Retrieves the x-coordinate of the entity's position.
     *
     * @return The current x-coordinate of the entity as a double.
     */
    public double getX() {
        return handle.getDoubles().read(0);
    }

    /**
     * Sets the x-coordinate of the entity's position.
     *
     * @param value the new x-coordinate to assign. This should be a valid double
     *              representing the horizontal position of the entity in the game world.
     */
    public void setX(double value) {
        handle.getDoubles().write(0, value);
    }

    /**
     * Retrieves the y-coordinate of the entity's position.
     *
     * @return The current y-coordinate of the entity as a double.
     */
    public double getY() {
        return handle.getDoubles().read(1);
    }

    /**
     * Sets the y-coordinate of the entity's position.
     *
     * @param value the new y-coordinate to assign. This should be a valid double
     *              representing the vertical position of the entity in the game world.
     */
    public void setY(double value) {
        handle.getDoubles().write(1, value);
    }

    /**
     * Retrieves the z-coordinate of the entity's position.
     *
     * @return The current z-coordinate of the entity as a double.
     */
    public double getZ() {
        return handle.getDoubles().read(2);
    }

    /**
     * Sets the z-coordinate of the entity's position.
     *
     * @param value the new z-coordinate to assign. This should be a valid double
     *              representing the position of the entity along the z-axis in the game world.
     */
    public void setZ(double value) {
        handle.getDoubles().write(2, value);
    }

    /**
     * Retrieves the yaw (horizontal rotation) of the entity represented by this packet.
     * The yaw is calculated from the underlying packet data and expressed as a float value
     * in degrees, ranging from 0 to 360.
     *
     * @return The yaw of the entity as a float value in degrees.
     */
    public float getYaw() {
        return (handle.getBytes().read(0) * 360.F) / 256.0F;
    }

    /**
     * Sets the yaw (horizontal rotation) of the entity represented by this packet.
     * The yaw is stored in the packet as a byte value calculated from the given float input.
     *
     * @param value the new yaw of the entity as a float value in degrees, ranging from 0 to 360.
     *              This value will be internally converted to a byte representation.
     */
    public void setYaw(float value) {
        handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieves the pitch (vertical rotation) of the entity represented by this packet.
     * The pitch is calculated from the underlying packet data and expressed as a float value
     * in degrees, ranging from -90 to 90.
     *
     * @return The pitch of the entity as a float value in degrees.
     */
    public float getPitch() {
        return (handle.getBytes().read(1) * 360.F) / 256.0F;
    }

    /**
     * Sets the pitch (vertical rotation) of the entity represented by this packet.
     * The pitch is stored in the packet as a byte value calculated from the provided float input.
     *
     * @param value the new pitch of the entity as a float value in degrees,
     *              ranging from -90 to 90. This value will be internally converted
     *              to a byte representation.
     */
    public void setPitch(float value) {
        handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieves the pitch (vertical rotation) of the mob's head represented by this packet.
     * The head pitch is calculated from the underlying packet data and expressed as a float value
     * in degrees.
     *
     * @return The pitch of the mob's head as a float value in degrees, ranging from -90 to 90.
     */
    public float getHeadPitch() {
        return (handle.getBytes().read(2) * 360.F) / 256.0F;
    }

    /**
     * Sets the pitch (vertical rotation) of the mob's head represented by this packet.
     * The pitch value is internally converted into a byte and stored in the packet.
     *
     * @param value the new pitch of the mob's head as a float value in degrees, ranging from -90 to 90.
     *              This value will be internally scaled and converted to a byte representation.
     */
    public void setHeadPitch(float value) {
        handle.getBytes().write(2, (byte) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieves the velocity of the entity along the x-axis.
     * The velocity is derived from the underlying packet data and scaled.
     *
     * @return The current velocity along the x-axis as a double.
     */
    public double getVelocityX() {
        return handle.getIntegers().read(2) / 8000.0D;
    }

    /**
     * Set the velocity in the x axis.
     *
     * @param value - new value.
     */
    public void setVelocityX(double value) {
        handle.getIntegers().write(2, (int) (value * 8000.0D));
    }

    /**
     * Retrieves the velocity of the entity along the y-axis.
     * The velocity is derived from the underlying packet data and scaled.
     *
     * @return The current velocity along the y-axis as a double.
     */
    public double getVelocityY() {
        return handle.getIntegers().read(3) / 8000.0D;
    }

    /**
     * Sets the velocity of the entity along the y-axis.
     * This method adjusts the packet data to represent the new y-axis velocity by converting
     * the provided value to an internal representation used in the game.
     *
     * @param value the new velocity along the y-axis as a double. This value should represent
     *              the desired speed in the vertical direction, which will be internally
     *              scaled and stored in the packet.
     */
    public void setVelocityY(double value) {
        handle.getIntegers().write(3, (int) (value * 8000.0D));
    }

    /**
     * Retrieves the Z-axis velocity value.
     *
     * @return the Z-axis velocity as a double, calculated by dividing
     *         the raw integer value by 8000.0
     */
    public double getVelocityZ() {
        return handle.getIntegers().read(4) / 8000.0D;
    }

    /**
     * Sets the velocity along the Z-axis for the associated entity.
     *
     * The velocity is scaled by a factor of 8000.0 before being written
     * to the internal representation.
     *
     * @param value the velocity in the Z direction to be applied,
     *              expressed as a double
     */
    public void setVelocityZ(double value) {
        handle.getIntegers().write(4, (int) (value * 8000.0D));
    }

    /**
     * Retrieves the metadata associated with the current object.
     *
     * @return a WrappedDataWatcher instance containing the metadata.
     */
    public WrappedDataWatcher getMetadata() {
        return handle.getDataWatcherModifier().read(0);
    }

    /**
     * Sets the metadata for the associated data watcher.
     *
     * @param value the WrappedDataWatcher object containing the metadata to be set
     */
    public void setMetadata(WrappedDataWatcher value) {
        handle.getDataWatcherModifier().write(0, value);
    }
}
