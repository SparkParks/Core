/*
 * This file is part of PacketWrapper.
 * Copyright (C) 2012-2015 Kristian S. Strangeland
 * Copyright (C) 2015 dmulloy2
 *
 * PacketWrapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PacketWrapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PacketWrapper.  If not, see <http://www.gnu.org/licenses/>.
 */
package network.palace.core.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;
import lombok.Getter;
import network.palace.core.player.CPlayer;

import java.lang.reflect.InvocationTargetException;

/**
 * Represents an abstract base class for working with strongly typed packet wrappers.
 * Provides functionality for verifying raw packet data and sending packets to players.
 */
public abstract class AbstractPacket {

    /**
     * Represents the handle to the raw packet data associated with this packet wrapper.
     * Provides direct access to the underlying {@link PacketContainer} for advanced manipulations.
     * The handle ensures the packet is strongly typed and valid for its intended usage.
     * This field is initialized during the construction of the packet wrapper.
     */
    @Getter protected PacketContainer handle;

    /**
     * Constructs an instance of an abstract packet wrapper, validating the packet type and its data.
     * Used to ensure that the provided packet is of the correct type before initializing the wrapper.
     *
     * @param handle the handle to the raw packet data, represented as a {@link PacketContainer}.
     *               This must not be null and must contain a valid packet of the specified type.
     * @param type   the type of the packet, represented as a {@link PacketType}.
     *               This is used to verify the packet's type against the provided packet container.
     *
     * @throws IllegalArgumentException if the handle is null or if the packet type does not match
     *                                  the type specified.
     */
    protected AbstractPacket(PacketContainer handle, PacketType type) {
        // Make sure we're given a valid packet
        if (handle == null) {
            throw new IllegalArgumentException("Packet handle cannot be NULL.");
        }
        if (!Objects.equal(handle.getType(), type)) {
            throw new IllegalArgumentException(handle.getHandle() + " is not a packet of type " + type);
        }
        this.handle = handle;
    }

    /**
     * Sends the packet to a specified player.
     * This method requires the player and their associated Bukkit player object to be non-null.
     * If either is null, the method will exit without performing any operation.
     * It uses the ProtocolLibrary to send the packet and throws a runtime exception
     * if an error occurs during the sending process.
     *
     * @param player the {@code CPlayer} object representing the player to whom the packet
     *               should be sent. The associated Bukkit player must also be non-null.
     *               If the player or their Bukkit player is null, this method will do nothing.
     * @throws RuntimeException if the packet cannot be sent due to an {@link InvocationTargetException}.
     */
    public void sendPacket(CPlayer player) {
        if (player == null) return;
        if (player.getBukkitPlayer() == null) return;
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.getBukkitPlayer(), getHandle());
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet.", e);
        }
    }
}
