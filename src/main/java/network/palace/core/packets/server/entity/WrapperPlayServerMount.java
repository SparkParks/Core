/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package network.palace.core.packets.server.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import network.palace.core.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a wrapper for the Play.Server.MOUNT packet used in Minecraft.
 * This packet is responsible for managing the mounting and dismounting of entities along with their passengers.
 */
public class WrapperPlayServerMount extends AbstractPacket {

    /**
     * Represents the packet type {@code PacketType.Play.Server.MOUNT}.
     * This type is associated with packets sent by the server for managing
     * entities that are mounting or dismounting other entities, as well as
     * their passengers.
     */
    public static final PacketType TYPE = PacketType.Play.Server.MOUNT;

    /**
     * Constructs a new wrapper for the Play.Server.MOUNT packet type.
     * This packet is used by the server to manage the mounting and dismounting
     * of entities, including their passengers.
     *
     * Initializes the packet with its associated data and assigns default values
     * to the packet's modifiers.
     *
     * The packet type associated with this wrapper is {@code PacketType.Play.Server.MOUNT}.
     *
     * Throws an IllegalArgumentException if the packet type does not match
     * the expected type or if the packet container is null.
     */
    public WrapperPlayServerMount() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Constructs a new instance of the WrapperPlayServerMount wrapper.
     *
     * This constructor initializes the wrapper for the PacketType.Play.Server.MOUNT,
     * ensuring that the provided packet container matches the expected packet type.
     *
     * @param packet The packet container containing the necessary packet data. This
     *               must not be null and must match the PacketType.Play.Server.MOUNT.
     * @throws IllegalArgumentException if the packet container is null or the packet
     *                                  type does not match the expected type.
     */
    public WrapperPlayServerMount(PacketContainer packet) {
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
     * Sets the ID of the entity associated with this packet.
     *
     * @param value the new entity ID to be set.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieves the IDs of the passengers associated with the entity in this packet.
     *
     * @return An array of integers representing the IDs of the passengers.
     */
    public int[] getPassengerIds() {
        return handle.getIntegerArrays().read(0);
    }

    /**
     * Sets the IDs of the passengers associated with the entity in this packet.
     *
     * @param value An array of integers representing the IDs of the passengers to be set.
     */
    public void setPassengerIds(int[] value) {
        handle.getIntegerArrays().write(0, value);
    }

    /**
     * Retrieves a list of passengers associated with the entity involved in the packet event.
     *
     * @param event the packet event containing the player and world information.
     * @return A list of {@code Entity} objects representing the passengers, or an empty list
     *         if no passengers are found.
     */
    public List<Entity> getPassengers(PacketEvent event) {
        return getPassengers(event.getPlayer().getWorld());
    }

    /**
     * Retrieves a list of passengers associated with the given world.
     * This method identifies the passengers by their entity IDs, using
     * the ProtocolManager to fetch the corresponding entities from the provided world.
     *
     * @param world the world in which the passengers' entities exist.
     * @return A list of {@code Entity} objects representing the passengers,
     *         or an empty list if no valid entities are found.
     */
    public List<Entity> getPassengers(World world) {
        int[] ids = getPassengerIds();
        List<Entity> passengers = new ArrayList<>();
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        for (int id : ids) {
            Entity entity = manager.getEntityFromID(world, id);
            if (entity != null) {
                passengers.add(entity);
            }
        }

        return passengers;
    }

    /**
     * Sets the passengers associated with the entity in this packet.
     * This method updates the passenger IDs by extracting them from the provided list
     * of entity objects.
     *
     * @param value A list of {@code Entity} objects representing the passengers to be set.
     */
    public void setPassengers(List<Entity> value) {
        int[] array = new int[value.size()];
        for (int i = 0; i < value.size(); i++) {
            array[i] = value.get(i).getEntityId();
        }

        setPassengerIds(array);
    }
}