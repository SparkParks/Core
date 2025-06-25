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
package network.palace.core.packets.server.block;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import network.palace.core.packets.AbstractPacket;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Represents a wrapper for the Minecraft server packet responsible
 * for handling block actions. This packet notifies the client of
 * specific block interactions or state changes.
 *
 * This wrapper is designed to simplify accessing and modifying the
 * data contained within the packet, including block location, type,
 * action id, and action parameters. The underlying packet type is
 * {@code PacketType.Play.Server.BLOCK_ACTION}.
 */
public class WrapperPlayServerBlockAction extends AbstractPacket {

    /**
     * Specifies the packet type for handling server-side block actions in the game.
     * This constant represents the packet {@code PacketType.Play.Server.BLOCK_ACTION},
     * which is used to notify clients about interactions or updates to blocks.
     */
    public static final PacketType TYPE = PacketType.Play.Server.BLOCK_ACTION;

    /**
     * Constructs a new wrapper instance for the Minecraft server packet
     * {@code PacketType.Play.Server.BLOCK_ACTION}. This packet is used to notify
     * the client about actions or state changes occurring on specific blocks,
     * such as pistons, chests, or note blocks.
     *
     * Upon initialization, this constructor sets up the packet container with
     * the default modifications necessary for handling this packet type.
     *
     * The wrapper serves as an abstraction for simplifying interactions with the
     * raw packet data, enabling easier modification and retrieval of fields
     * like block location, type, action ID, and parameters.
     *
     * @throws IllegalArgumentException if the packet container is not correctly initialized
     *                                  or does not match the expected packet type.
     */
    public WrapperPlayServerBlockAction() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Constructs a new instance of the wrapper for the packet
     * {@code PacketType.Play.Server.BLOCK_ACTION}. This constructor initializes
     * the packet wrapper with the provided {@link PacketContainer}.
     *
     * @param packet the {@link PacketContainer} containing the raw packet data
     *               to be wrapped. Must not be null and must match the expected
     *               packet type for block action events.
     * @throws IllegalArgumentException if the packet container is null, not properly
     *                                  initialized, or does not match the expected packet type.
     */
    public WrapperPlayServerBlockAction(PacketContainer packet) {
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
     * Sets the location of the block that this packet is referring to.
     * This method takes a Block object and updates the underlying packet
     * data with the block's coordinates.
     *
     * @param block The block whose location is to be set. Must not be null.
     */
    public void setLocation(Block block) {
        Location location = block.getLocation();
        handle.getBlockPositionModifier().write(0, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    /**
     * Retrieves the action ID associated with this packet.
     * The action ID indicates the specific action or state change
     * related to the block specified in the packet.
     *
     * @return The current action ID as an integer.
     */
    public int getActionID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Sets the action ID for the block action packet. The action ID specifies the type of
     * action or state change associated with the block referred to in the packet.
     *
     * @param value The action ID to be set. Must be a valid integer corresponding to the
     *              specific block action or state change.
     */
    public void setActionID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieves the action parameter associated with this packet.
     * The action parameter provides additional data or context for the action
     * specified by the action ID, relating to the block in question.
     *
     * @return The current action parameter as an integer.
     */
    public int getActionParam() {
        return handle.getIntegers().read(1);
    }

    /**
     * Sets the action parameter for the block action packet. The action parameter
     * provides additional context or information related to the block action
     * specified by the action ID.
     *
     * @param value the action parameter to be set. Must be an integer corresponding
     *              to the specific block action or state.
     */
    public void setActionParam(int value) {
        handle.getIntegers().write(1, value);
    }

    /**
     * Retrieves the type of block associated with this packet.
     *
     * This method provides the block type information, represented by the {@link Material} enum,
     * derived from the packet's data.
     *
     * @return The {@link Material} representing the type of block involved in this packet.
     */
    public Material getBlockType() {
        return handle.getBlocks().read(0);
    }

    /**
     * Sets the type of the block associated with this packet.
     * This method updates the block type information in the packet data
     * using the {@link Material} value provided.
     *
     * @param value the {@link Material} representing the new block type to be set.
     *              Must not be null.
     */
    public void setBlockType(Material value) {
        handle.getBlocks().write(0, value);
    }
}
