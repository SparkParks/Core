package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;

/**
 * Represents a broadcast packet used to send messages globally within the system.
 * A BroadcastPacket contains information about the sender and the message being broadcasted.
 * <p>
 * This packet extends from {@link MQPacket} and is identified using the Broadcast ID from {@link PacketID.Global}.
 */
public class BroadcastPacket extends MQPacket {
    /**
     * The identifier of the sender who originated the broadcast.
     * This value represents the individual or system responsible for sending
     * the broadcasted message.
     * <p>
     * In a typical use case, `sender` holds the name or unique string that
     * identifies who or what issued the message, enabling tracking of the
     * message's source.
     * <p>
     * This field is immutable and cannot be modified after object creation.
     */
    @Getter private final String sender, message;

    /**
     * Creates a new instance of {@code BroadcastPacket} using the JSON object provided.
     * The packet is initialized with the sender's identifier and the message content
     * extracted from the given JSON object.
     *
     * @param object the JSON object containing the data necessary to create a broadcast packet.
     *               It must include a "sender" key with a string value representing the source of
     *               the broadcast and a "message" key with a string value containing the message
     *               to be broadcasted.
     * @throws IllegalArgumentException if the packet ID present in the JSON object does not match
     *                                  the expected ID for a broadcast packet.
     */
    public BroadcastPacket(JsonObject object) {
        super(PacketID.Global.BROADCAST.getId(), object);
        this.sender = object.get("sender").getAsString();
        this.message = object.get("message").getAsString();
    }

    /**
     * Constructs a new {@code BroadcastPacket} with the specified sender and message.
     * This packet represents a global broadcast message within the system.
     *
     * @param sender the identifier of the entity (user or system) originating the broadcast
     *               message. This is used to track the source of the broadcast.
     * @param message the content of the broadcast message to be sent globally.
     */
    public BroadcastPacket(String sender, String message) {
        super(PacketID.Global.BROADCAST.getId(), null);
        this.sender = sender;
        this.message = message;
    }

    /**
     * Converts the BroadcastPacket instance into a JSON representation.
     * The resulting JSON object contains packet-specific properties such as
     * the sender's identifier and the broadcast message, as well as base packet data
     * inherited from the parent class.
     *
     * @return a JsonObject representation of the BroadcastPacket, including the sender's identifier,
     *         the broadcast message, and base packet properties like the packet ID and proxy ID (if applicable).
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("sender", sender);
        object.addProperty("message", message);
        return object;
    }
}
