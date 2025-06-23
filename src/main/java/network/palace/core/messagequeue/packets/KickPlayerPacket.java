package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.UUID;

/**
 * The KickPlayerPacket class is used to create and manage packets for kicking a player from a network or system.
 * This packet contains information about the player being kicked, the reason for the kick,
 * and whether the kick reason is presented as a component message.
 * <p>
 * This class extends the MQPacket class and implements the necessary structures
 * for serializing and deserializing the packet data.
 */
@Getter
public class KickPlayerPacket extends MQPacket {
    /**
     * The unique identifier (UUID) of the player to be kicked.
     * This UUID is used to identify the player targeted by the kick action within the system.
     * It serves as a reference for locating the player across distributed network components.
     * <p>
     * The UUID is immutable and is assigned either by parsing it from
     * the JSON object provided during deserialization or directly through the constructor.
     */
    private final UUID uuid;

    /**
     * The reason for kicking the player. This variable contains a textual description
     * or explanation that specifies why the player is being removed from the system or network.
     * It provides context for the action and may be displayed to the affected user or logged
     * for administrative purposes.
     * <p>
     * This value is immutable and is initialized either through the constructor or deserialized
     * from the associated JSON object.
     */
    private final String reason;

    /**
     * Indicates whether the reason for kicking a player is represented as a component message.
     * A component message typically allows for richer formatting, such as colors or interactive elements,
     * as opposed to plain text. This flag determines how the reason should be processed or displayed.
     */
    private final boolean componentMessage;

    /**
     * Constructs a new KickPlayerPacket instance using the provided JSON object.
     * This constructor initializes the packet with information about the player to be kicked,
     * the reason for the kick, and whether the reason is represented as a component message.
     *
     * @param object The JSON object containing the data for the packet.
     *               It must include the following properties:
     *               - "uuid" (String): The unique identifier of the player to be kicked.
     *               - "reason" (String): The reason for kicking the player.
     *               - "componentMessage" (boolean): A flag indicating if the reason is a component message.
     * @throws IllegalArgumentException If the packet ID in the JSON object does not match the expected packet ID.
     * @throws NullPointerException If the required fields "uuid", "reason", or "componentMessage" are missing in the JSON object.
     */
    public KickPlayerPacket(JsonObject object) {
        super(PacketID.Global.KICK_PLAYER.getId(), object);
        this.uuid = UUID.fromString(object.get("uuid").getAsString());
        this.reason = object.get("reason").getAsString();
        this.componentMessage = object.get("componentMessage").getAsBoolean();
    }

    /**
     * Constructs a new KickPlayerPacket with the specified parameters.
     * This packet is used to kick a player identified by a unique UUID
     * with a specified reason. The reason can optionally be represented as
     * a component message.
     *
     * @param uuid The unique identifier (UUID) of the player to be kicked.
     * @param reason The reason for kicking the player.
     * @param componentMessage A boolean flag indicating whether the reason
     *                         is represented as a component message.
     */
    public KickPlayerPacket(UUID uuid, String reason, boolean componentMessage) {
        super(PacketID.Global.KICK_PLAYER.getId(), null);
        this.uuid = uuid;
        this.reason = reason;
        this.componentMessage = componentMessage;
    }

    /**
     * Generates a JSON representation of this KickPlayerPacket instance.
     * The JSON object includes the following properties:
     * <ul>
     *     <li>"id": The unique identifier of the packet.</li>
     *     <li>"proxyID": The UUID of the proxy sending the packet, if applicable.</li>
     *     <li>"uuid": The UUID of the player to be kicked.</li>
     *     <li>"reason": The reason for kicking the player.</li>
     *     <li>"componentMessage": Indicates whether the reason is represented as a component message.</li>
     * </ul>
     *
     * @return A JsonObject representing the KickPlayerPacket instance.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("reason", reason);
        object.addProperty("componentMessage", componentMessage);
        return object;
    }
}