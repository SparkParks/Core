package network.palace.core.packets.server.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import network.palace.core.packets.AbstractPacket;

import java.util.List;

/**
 * Wrapper class for the Minecraft packet Play.Server.PLAYER_INFO.
 * This packet is sent by the server to update player information in the client’s player list.
 * It includes data about the player and the type of action being performed (e.g., adding, updating, or removing players).
 */
public class WrapperPlayServerPlayerInfo extends AbstractPacket {

    /**
     * Represents the packet type {@code PacketType.Play.Server.PLAYER_INFO}.
     * This type is used to identify packets sent by the server to update player information
     * in the client's player list. It includes actions such as adding, updating, or removing players.
     */
    public static final PacketType TYPE = PacketType.Play.Server.PLAYER_INFO;

    /**
     * Constructs a new wrapper for the Play.Server.PLAYER_INFO packet type.
     * This wrapper provides functionality to handle and modify the data sent by the server
     * to update player information in the client's player list.
     *
     * Initializes the packet with its associated data and sets default values
     * for the packet's modifiers.
     *
     * The packet type associated with this wrapper is {@code PacketType.Play.Server.PLAYER_INFO}.
     *
     * Throws an IllegalArgumentException if the packet container is null or if the
     * packet type does not match the expected type.
     */
    public WrapperPlayServerPlayerInfo() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Constructs a new instance of the WrapperPlayServerPlayerInfo wrapper.
     *
     * This constructor initializes the wrapper for the PacketType.Play.Server.PLAYER_INFO,
     * ensuring that the provided packet container matches the expected packet type.
     *
     * @param packet The packet container containing the necessary packet data. This
     *               must not be null and must match the PacketType.Play.Server.PLAYER_INFO.
     * @throws IllegalArgumentException if the packet container is null or the packet
     *                                  type does not match the expected type.
     */
    public WrapperPlayServerPlayerInfo(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieves the player info action associated with this packet.
     * The action represents the type of operation being performed, such as adding,
     * updating, or removing players from the player list.
     *
     * @return An instance of {@code EnumWrappers.PlayerInfoAction} representing the player info action.
     */
    public EnumWrappers.PlayerInfoAction getAction() {
        return handle.getPlayerInfoAction().read(0);
    }

    /**
     * Sets the player info action associated with this packet.
     * The action represents the type of operation being performed,
     * such as adding, updating, or removing players from the player list.
     *
     * @param value An instance of {@code EnumWrappers.PlayerInfoAction} representing
     *              the action to set for the player info packet.
     */
    public void setAction(EnumWrappers.PlayerInfoAction value) {
        handle.getPlayerInfoAction().write(0, value);
    }

    /**
     * Retrieves the list of player information data associated with this packet.
     * This data provides detailed information about players, such as their
     * profile, actions, and properties, to update or manage entries in the
     * player's list.
     *
     * @return A list of {@code PlayerInfoData} objects containing player-related
     * information retrieved from the packet.
     */
    public List<PlayerInfoData> getData() {
        return handle.getPlayerInfoDataLists().read(0);
    }

    /**
     * Sets the list of player information data for this packet.
     * This data contains detailed information about players, allowing
     * updates or modifications to be made to the player's list in the server's packets.
     *
     * @param value A list of {@code PlayerInfoData} objects containing player-related
     *              information to update or manage entries in the player's list.
     */
    public void setData(List<PlayerInfoData> value) {
        handle.getPlayerInfoDataLists().write(0, value);
    }
}
