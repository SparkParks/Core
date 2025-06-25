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
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

/**
 * Represents a wrapper for the Play.Client.USE_ENTITY packet.
 * This packet is sent when a player interacts with an entity in the game world.
 */
public class WrapperPlayClientUseEntity extends AbstractPacket {

    /**
     * Defines the packet type for the Play.Client.USE_ENTITY packet.
     * This packet is used when a player interacts with an entity in the game world.
     */
    public static final PacketType TYPE = PacketType.Play.Client.USE_ENTITY;

    /**
     * Constructs a new {@code WrapperPlayClientUseEntity} packet wrapper.
     * This wrapper is used to represent the Play.Client.USE_ENTITY packet.
     * The packet is sent to the server when a player interacts with an entity in the game world.
     * Initializes the packet's data with default values.
     */
    public WrapperPlayClientUseEntity() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Constructs a new {@code WrapperPlayClientUseEntity} packet wrapper.
     * This wrapper is designed to handle packets of the type `Play.Client.USE_ENTITY`.
     * The packet is sent to the server when a player interacts with an entity in the game world.
     *
     * @param packet the raw packet data to wrap. This must not be null and must
     *               represent a valid packet of type `Play.Client.USE_ENTITY`.
     * @throws IllegalArgumentException if the packet is null or if the packet type does not match.
     */
    public WrapperPlayClientUseEntity(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieves the target entity ID from the packet data.
     *
     * @return The ID of the target entity.
     */
    public int getTargetID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Retrieves the target entity associated with this packet in the specified world.
     *
     * @param world - the world in which the entity resides.
     * @return The targeted entity, or null if the entity could not be found.
     */
    public Entity getTarget(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieves the target entity associated with this packet in the world of the player
     * involved in the provided packet event.
     *
     * @param event the packet event containing the player whose world will be used
     *              to locate the target entity.
     * @return The targeted entity, or null if the entity could not be found.
     */
    public Entity getTarget(PacketEvent event) {
        return getTarget(event.getPlayer().getWorld());
    }

    /**
     * Sets the target entity ID in the packet data.
     *
     * @param value The ID of the target entity to set.
     */
    public void setTargetID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieves the type of entity use action specified in the packet.
     *
     * @return The {@link EntityUseAction} representing the type of interaction with the entity.
     */
    public EntityUseAction getType() {
        return handle.getEntityUseActions().read(0);
    }

    /**
     * Sets the type of entity use action in the packet.
     *
     * @param value The {@link EntityUseAction} representing the type of interaction with the entity.
     */
    public void setType(EntityUseAction value) {
        handle.getEntityUseActions().write(0, value);
    }

    /**
     * Retrieves the target vector from the packet data.
     *
     * @return The {@link Vector} representing the target position associated with this packet.
     */
    public Vector getTargetVector() {
        return handle.getVectors().read(0);
    }

    /**
     * Sets the target vector in the packet data. This represents the specific location
     * or point associated with the entity being interacted with in this packet.
     *
     * @param value The {@link Vector} representing the new target position to be set.
     */
    public void setTargetVector(Vector value) {
        handle.getVectors().write(0, value);
    }
}
