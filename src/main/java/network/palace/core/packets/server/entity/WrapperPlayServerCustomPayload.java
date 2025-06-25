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
package network.palace.core.packets.server.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.palace.core.packets.AbstractPacket;

/**
 * A wrapper class for the Minecraft server packet Play.Server.CUSTOM_PAYLOAD.
 * This packet is used to send custom plugin messages between the client and server via specified channels.
 * It encapsulates the functionality for managing the custom channel and payload data.
 */
public class WrapperPlayServerCustomPayload extends AbstractPacket {

    /**
     * Represents the packet type {@code PacketType.Play.Server.CUSTOM_PAYLOAD}.
     * This constant is used to identify the specific packet type that handles
     * custom plugin messages sent between the server and client via specified channels.
     */
    public static final PacketType TYPE = PacketType.Play.Server.CUSTOM_PAYLOAD;

    /**
     * Constructs a new wrapper for the Play.Server.CUSTOM_PAYLOAD packet type.
     * This packet type is used to send custom plugin messages between the server
     * and client via specified channels.
     *
     * Initializes the packet with its associated data and assigns default values
     * to the packet's modifiers.
     *
     * The packet type associated with this wrapper is {@code PacketType.Play.Server.CUSTOM_PAYLOAD}.
     *
     * Throws an IllegalArgumentException if the packet type does not match the
     * expected type or if the packet container is null.
     */
    public WrapperPlayServerCustomPayload() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Constructs a new wrapper for the Play.Server.CUSTOM_PAYLOAD packet type.
     * This packet type is used to send custom plugin messages between the server
     * and client via specified channels.
     *
     * Initializes the packet with its associated data and assigns default values
     * to the packet's modifiers.
     *
     * The packet type associated with this wrapper is {@code PacketType.Play.Server.CUSTOM_PAYLOAD}.
     *
     * @param packet The packet container containing the necessary packet data. This
     *               must not be null and must match the PacketType.Play.Server.CUSTOM_PAYLOAD.
     * @throws IllegalArgumentException if the packet container is null or the packet
     *                                  type does not match the expected type.
     */
    public WrapperPlayServerCustomPayload(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieves the channel associated with the custom payload packet.
     * This channel is used for communication between the server and client
     * for plugin messages.
     *
     * @return The name of the channel as a string.
     */
    public String getChannel() {
        return handle.getStrings().read(0);
    }

    /**
     * Sets the channel for the custom payload packet. The channel is used to
     * specify the communication path between the server and client for plugin messages.
     *
     * @param value The name of the channel as a string.
     */
    public void setChannel(String value) {
        handle.getStrings().write(0, value);
    }

    /**
     * Retrieves the contents buffer of the custom payload packet.
     * The contents are represented as a ByteBuf, allowing direct manipulation
     * of the packet's data buffer used for custom plugin messaging between
     * the server and client.
     *
     * @return The contents of the custom payload packet as a {@code ByteBuf}.
     */
    public ByteBuf getContentsBuffer() {
        return (ByteBuf) handle.getModifier().withType(ByteBuf.class).read(0);
    }

    /**
     * Retrieves the contents of the custom payload packet.
     * The contents are extracted from the internal buffer and returned
     * as a byte array.
     *
     * @return A byte array containing the contents of the custom payload packet.
     */
    public byte[] getContents() {
        ByteBuf buffer = getContentsBuffer();
        byte[] array = new byte[buffer.readableBytes()];
        buffer.readBytes(array);
        return array;
    }

    /**
     * Updates the contents buffer of the custom payload packet. The contents are
     * represented as a {@code ByteBuf}, enabling direct manipulation of the packet's
     * data buffer used for custom plugin messaging between the server and client.
     *
     * @param contents the new contents to set for the custom payload packet as a {@code ByteBuf}.
     */
    public void setContentsBuffer(ByteBuf contents) {
        if (MinecraftReflection.is(MinecraftReflection.getPacketDataSerializerClass(), contents)) {
            handle.getModifier().withType(ByteBuf.class).write(0, contents);
        } else {
            Object serializer = MinecraftReflection.getPacketDataSerializer(contents);
            handle.getModifier().withType(ByteBuf.class).write(0, serializer);
        }
    }

    /**
     * Sets the contents of the custom payload packet.
     * The contents are provided as a byte array and are converted into a buffer
     * that is used to update the packet's internal data.
     *
     * @param content the new contents to set for the custom payload packet as a byte array.
     */
    public void setContents(byte[] content) {
        setContentsBuffer(Unpooled.copiedBuffer(content));
    }
}