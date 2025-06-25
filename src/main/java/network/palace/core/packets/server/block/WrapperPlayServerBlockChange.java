/*
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
package network.palace.core.packets.server.block;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import network.palace.core.packets.AbstractPacket;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * WrapperPlayServerBlockChange is a packet wrapper for the Minecraft protocol, specifically
 * designed to handle server-side block change packets. This class allows manipulation and
 * retrieval of block data and location information related to block changes in the game world.
 *
 * This wrapper is built upon the ProtocolLib API and provides a simple and effective way of
 * interacting with block change packets in a type-safe manner.
 */
public class WrapperPlayServerBlockChange extends AbstractPacket {
    /**
     * Specifies the packet type for handling server-side block change events
     * in the game. This constant represents the packet {@code PacketType.Play.Server.BLOCK_CHANGE},
     * which is used to notify clients about updates to specific blocks in the world.
     *
     * The packet is commonly utilized to inform clients about changes to block state,
     * such as modifications to block type or data, at a particular location in the game world.
     */
    public static final PacketType TYPE = PacketType.Play.Server.BLOCK_CHANGE;

    /**
     * Constructs a new instance of the {@code WrapperPlayServerBlockChange} class.
     * This constructor initializes the packet container for the block change packet
     * and writes default values to the packet data.
     *
     * The {@code WrapperPlayServerBlockChange} class is a specialized wrapper for handling
     * block change packets sent from the server to the client. It provides methods for
     * retrieving and modifying the block's position and data in a type-safe and efficient manner.
     *
     * The block change packet is used to notify the client about modifications to a specific
     * block in the game world, including changes to the block type and related metadata.
     *
     * This constructor uses the {@code PacketType.Play.Server.BLOCK_CHANGE} type to ensure
     * the packet is of the correct type and prepares it for use without requiring a custom
     * packet to be passed as input.
     *
     * By default, the packet's modifier values are set to their default state during initialization.
     */
    public WrapperPlayServerBlockChange() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Constructs a new instance of the {@code WrapperPlayServerBlockChange} class.
     * This constructor initializes the wrapped packet container for the block change
     * packet and ensures the packet is of the correct type.
     *
     * @param packet the {@link PacketContainer} representing the raw packet data.
     *               It must not be null and must contain a valid server block change packet.
     *
     * @throws IllegalArgumentException if the provided packet is null or the packet type
     *                                  does not match the expected {@code PacketType.Play.Server.BLOCK_CHANGE}.
     */
    public WrapperPlayServerBlockChange(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieves the block position associated with this packet.
     *
     * @return The current block position as a {@link BlockPosition}.
     */
    public BlockPosition getLocation() {
        return handle.getBlockPositionModifier().read(0);
    }

    /**
     * Updates the location of the block referenced by this packet.
     * This method modifies the packet's internal data to reflect the specified block position.
     *
     * @param value the new block position as a {@link BlockPosition}. Must not be null.
     */
    public void setLocation(BlockPosition value) {
        handle.getBlockPositionModifier().write(0, value);
    }

    /**
     * Converts the {@link BlockPosition} associated with this packet into a Bukkit {@link Location}
     * within the specified {@link World}.
     *
     * @param world the world in which the resulting {@link Location} should be created. Must not be null.
     * @return the corresponding {@link Location} object in the specified world.
     */
    public Location getBukkitLocation(World world) {
        return getLocation().toVector().toLocation(world);
    }

    /**
     * Retrieves the block data associated with this packet.
     *
     * @return The {@link WrappedBlockData} object representing the block's current data.
     */
    public WrappedBlockData getBlockData() {
        return handle.getBlockData().read(0);
    }

    /**
     * Updates the block data associated with this packet.
     * This method modifies the packet's internal data to reflect the specified block state.
     *
     * @param value the new block data as a {@link WrappedBlockData}. Must not be null.
     */
    public void setBlockData(WrappedBlockData value) {
        handle.getBlockData().write(0, value);
    }
}