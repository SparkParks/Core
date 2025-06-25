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
package network.palace.core.packets.server.playerlist;

import network.palace.core.packets.AbstractPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
 * Wrapper class for the Play.Server.PLAYER_LIST_HEADER_FOOTER packet.
 * This class is used to handle packets related to the player list's header and footer
 * in the server, allowing for the retrieval and modification of these fields.
 */
public class WrapperPlayServerPlayerListHeaderFooter extends AbstractPacket {

    /**
     * Represents the {@link PacketType} for the Play.Server.PLAYER_LIST_HEADER_FOOTER packet.
     * This constant is used to specify the type of packet that modifies the header and footer
     * of the player list displayed on the server.
     */
    public static final PacketType TYPE = PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER;

    /**
     * Constructs a new instance of the WrapperPlayServerPlayerListHeaderFooter packet wrapper.
     * This constructor initializes the packet with default values and sets up the structure
     * for managing the player list's header and footer.
     *
     * The packet is associated with the Play.Server.PLAYER_LIST_HEADER_FOOTER type, which allows
     * changes to the header and footer displayed in the player list on the server.
     *
     * This constructor provides a clean slate for manipulating the packet contents and should
     * generally be used when intending to send or modify header and footer information
     * for players.
     */
    public WrapperPlayServerPlayerListHeaderFooter() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Retrieves the current header of the player list in the associated packet.
     *
     * @return The current header, represented as a {@link WrappedChatComponent}.
     */
    public WrappedChatComponent getHeader() {
        return handle.getChatComponents().read(0);
    }

    /**
     * Sets the header of the player list in the associated packet.
     *
     * @param value the new header, represented as a {@link WrappedChatComponent}.
     */
    public void setHeader(WrappedChatComponent value) {
        handle.getChatComponents().write(0, value);
    }

    /**
     * Retrieves the current footer of the player list in the associated packet.
     *
     * @return The current footer, represented as a {@link WrappedChatComponent}.
     */
    public WrappedChatComponent getFooter() {
        return handle.getChatComponents().read(1);
    }

    /**
     * Sets the footer of the player list in the associated packet.
     *
     * @param value the new footer, represented as a {@link WrappedChatComponent}.
     */
    public void setFooter(WrappedChatComponent value) {
        handle.getChatComponents().write(1, value);
    }
}
