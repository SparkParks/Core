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
package network.palace.core.packets.server.resourcepack;

import network.palace.core.packets.AbstractPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

/**
 * Represents a wrapper for the Minecraft Packet {@code Play.Server.RESOURCE_PACK_SEND}.
 * This packet is sent by the server to prompt the client to download and apply a resource pack.
 * It includes the URL of the resource pack and its associated hash for verification.
 */
public class WrapperPlayServerResourcePackSend extends AbstractPacket {

    /**
     * The {@code TYPE} field represents the packet type for the {@code Play.Server.RESOURCE_PACK_SEND} packet.
     * This packet is used to notify the client about a resource pack that should be downloaded and applied.
     * It includes information such as the resource pack URL and SHA-1 hash for validation.
     * This field serves as an identifier for this specific packet type within the packet wrapper.
     */
    public static final PacketType TYPE = PacketType.Play.Server.RESOURCE_PACK_SEND;

    /**
     * Constructs a new {@code WrapperPlayServerResourcePackSend} instance.
     * This wrapper specifically represents the Minecraft packet {@code Play.Server.RESOURCE_PACK_SEND},
     * which is used to prompt the client to download a resource pack from a given URL and optionally verify it
     * using a provided hash.
     *
     * This constructor initializes the packet with its default data structure and associates it with its type.
     * The structure includes modifiers that allow manipulation of the resource pack URL and hash values.
     *
     * This wrapper is typically used to interact with the resource pack functionality of the client by
     * sending the packet through the server's API.
     *
     * @throws IllegalArgumentException if the packet type validation fails during initialization.
     */
    public WrapperPlayServerResourcePackSend() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Retrieves the URL of the resource pack associated with this packet.
     * The URL points to the location where the client can download the resource pack.
     *
     * @return the URL of the resource pack as a string, or an empty string if no URL is set.
     */
    public String getUrl() {
        return handle.getStrings().read(0);
    }

    /**
     * Sets the URL for the resource pack associated with this packet.
     * The URL specifies the location where the client can download the resource pack.
     *
     * @param value the URL of the resource pack as a string
     */
    public void setUrl(String value) {
        handle.getStrings().write(0, value);
    }

    /// old documentation
    /// Retrieve Hash.
    ///
    /// Notes: a 40 character hexadecimal and lower-case SHA-1 hash of the
    /// resource pack file. (must be lower case in order to work) If it's not a
    /// 40 character hexadecimal string, the client will not use it for hash
    /// verification and likely waste bandwidth - but it will still treat it as a
    /// unique id
    ///
    /// @return The current Hash
    /**
     * Retrieves the hash associated with the resource pack in this packet.
     * The hash is used to verify the integrity of the resource pack
     * and ensure it matches the expected content.
     *
     * @return a string representing the hash of the resource pack, or an empty string if no hash is set.
     */
    public String getHash() {
        return handle.getStrings().read(1);
    }

    /**
     * Sets the hash for the resource pack associated with this packet.
     * The hash is used to verify the integrity of the resource pack and ensure it matches the expected content.
     *
     * @param value the hash of the resource pack as a string
     */
    public void setHash(String value) {
        handle.getStrings().write(1, value);
    }
}
