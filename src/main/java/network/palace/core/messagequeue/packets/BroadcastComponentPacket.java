package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;

/**
 * Represents a packet used for broadcasting a serialized message component
 * across the system. This implementation extends {@code MQPacket} and can
 * be serialized to and deserialized from JSON format.
 * <p>
 * The primary purpose of this class is to transmit broadcast messages
 * containing structured text components, where the sender and message
 * content are part of the packet payload.
 * <p>
 * This packet utilizes the {@code PacketID.Global.BROADCAST_COMPONENT} ID.
 */
public class BroadcastComponentPacket extends MQPacket {
    /**
     * Represents the identifier or origin of the sender that constructed the broadcast message.
     * This field is immutable and provides context about who or what initiated the transmission
     * of a serialized message within a broadcast component packet.
     */
    @Getter private final String sender, serializedMessage;

    /**
     * Constructs a new BroadcastComponentPacket instance using the given JSON object.
     * This packet is intended to broadcast a serialized message component containing
     * information about the sender and the message itself.
     *
     * @param object a JSON object representing the packet data. It must contain the
     *               following properties:
     *               - "sender": A string identifying the sender of the broadcast message.
     *               - "serializedMessage": A string containing the serialized content of the message.
     * @throws IllegalArgumentException if the packet ID in the JSON object does not match
     *                                  the PacketID assigned to this packet type.
     */
    public BroadcastComponentPacket(JsonObject object) {
        super(PacketID.Global.BROADCAST_COMPONENT.getId(), object);
        this.sender = object.get("sender").getAsString();
        this.serializedMessage = object.get("serializedMessage").getAsString();
    }

    /**
     * Constructs a new BroadcastComponentPacket instance using the specified sender identifier
     * and serialized message content. This packet is used for broadcasting a serialized message
     * component, which includes the sender's information and the structured message payload.
     *
     * @param sender the identifier of the sender responsible for creating the broadcast message.
     *               This provides context about the origin of the broadcast.
     * @param serializedMessage the serialized content of the message to be broadcast. It is a
     *               structured string representation of the message component.
     */
    public BroadcastComponentPacket(String sender, String serializedMessage) {
        super(PacketID.Global.BROADCAST_COMPONENT.getId(), null);
        this.sender = sender;
        this.serializedMessage = serializedMessage;
    }

    /**
     * Generates a JSON representation of the BroadcastComponentPacket.
     * The resulting JSON object includes properties that describe the sender and the serialized message content,
     * in addition to the base packet properties.
     *
     * @return a JsonObject containing the packet's data, including sender information, serialized message content,
     *         and any base packet properties from the parent class.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("sender", sender);
        object.addProperty("serializedMessage", serializedMessage);
        return object;
    }
}
