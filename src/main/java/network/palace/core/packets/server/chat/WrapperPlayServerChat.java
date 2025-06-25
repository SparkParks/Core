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
package network.palace.core.packets.server.chat;

import com.comphenix.protocol.wrappers.EnumWrappers;
import network.palace.core.Core;
import network.palace.core.packets.AbstractPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
 * A packet wrapper for the Play.Server.CHAT packet type, facilitating the manipulation and
 * retrieval of server-to-client chat messages.
 * Allows setting and retrieving chat messages and their associated positions or types.
 * This class is used for server-sent chat messages and supports Minecraft versions with differing
 * chat type handling.
 */
public class WrapperPlayServerChat extends AbstractPacket {

    /**
     * Represents the specific packet type for server-sent chat messages in the Play.Server.CHAT protocol.
     * This constant is used to identify and handle packets related to chat messages sent from the server
     * to the client. It enables functionalities such as reading or modifying chat messages and their
     * attributes, including position and type.
     *
     * The PacketType ensures that the packet structure adheres to the Play.Server.CHAT protocol,
     * allowing consistent interaction with the chat system within supported Minecraft versions.
     */
    public static final PacketType TYPE = PacketType.Play.Server.CHAT;

    /**
     * Constructs a new instance of the WrapperPlayServerChat class.
     * This wrapper is designed to handle packets of the Play.Server.CHAT type,
     * allowing manipulation and retrieval of server-to-client chat messages.
     * The constructor initializes the underlying packet container and sets
     * default values for the packet fields, preparing it for use.
     *
     * The WrapperPlayServerChat is typically used in scenarios where chat messages
     * need to be customized or dynamically sent to clients, supporting various
     * Minecraft versions with differing chat type handling.
     *
     * @throws IllegalArgumentException if the underlying packet cannot be
     *                                  initialized correctly or does not match
     *                                  the specified packet type.
     */
    public WrapperPlayServerChat() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Retrieves the chat message associated with this packet.
     *
     * @return The current chat message as a {@link WrappedChatComponent}.
     */
    public WrappedChatComponent getMessage() {
        return handle.getChatComponents().read(0);
    }

    /**
     * Sets the chat message for the packet.
     *
     * @param value The chat message to set, represented as a {@link WrappedChatComponent}.
     */
    public void setMessage(WrappedChatComponent value) {
        handle.getChatComponents().write(0, value);
    }

    /**
     * Retrieves the chat type or position associated with the chat packet.
     * This is used to indicate where and how the chat message should be displayed,
     * depending on the Minecraft version being used.
     *
     * @return An {@link EnumWrappers.ChatType} representing the chat type or position.
     *         For versions greater or equal to 1.11.2, it directly retrieves the
     *         chat type from the packet. For versions below 1.11.2, the chat type
     *         is determined by the byte value in the packet.
     */
    public EnumWrappers.ChatType getPosition() {
        if (Core.getInstance().isMinecraftGreaterOrEqualTo11_2()) {
            return handle.getChatTypes().read(0);
        } else {
            return EnumWrappers.ChatType.values()[handle.getBytes().read(0).intValue()];
        }
    }

    /**
     * Sets the chat type or position for this packet. This determines how and
     * where the message should appear to the client based on the Minecraft version.
     * For Minecraft versions 1.11.2 or greater, the chat type is directly written
     * into the packet. For versions below 1.11.2, the byte equivalent of the chat
     * type's ID is used.
     *
     * @param value The {@link EnumWrappers.ChatType} to set for the chat type or
     *              position in the packet.
     */
    public void setPosition(EnumWrappers.ChatType value) {
        if (Core.getInstance().isMinecraftGreaterOrEqualTo11_2()) {
            handle.getChatTypes().write(0, value);
        } else {
            handle.getBytes().write(0, value.getId());
        }
    }
}
