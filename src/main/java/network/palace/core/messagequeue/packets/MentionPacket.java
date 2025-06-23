package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.UUID;

/**
 * Represents a packet for handling mentions within the message queue system.
 * This packet includes a unique identifier (UUID) that associates with the mention.
 * It extends the {@code MQPacket} class and provides methods for JSON serialization
 * and deserialization to facilitate communication over the network.
 * <p>
 * Constructor Details:
 * - One constructor initializes the packet based on a {@code JsonObject}, parsing the UUID from it.
 * - Another constructor initializes the packet with a given UUID directly.
 * <p>
 * Methods:
 * - {@code getJSON()}: Serializes the {@code MentionPacket} instance into a {@code JsonObject},
 *   including the UUID and other base packet properties.
 */
public class MentionPacket extends MQPacket {
    /**
     * A unique identifier for the {@code MentionPacket}.
     * <p>
     * This field stores a {@link UUID} that uniquely represents the packet instance.
     * It is used for associating the packet with a specific mention and for ensuring its
     * global uniqueness within the message queue system. This identifier is immutable and
     * set during the creation of the {@code MentionPacket}.
     */
    @Getter private UUID uuid;

    /**
     * Constructs a {@code MentionPacket} instance by parsing the provided JSON object.
     * The JSON object should contain a UUID value to uniquely identify the mention packet.
     *
     * @param object the {@code JsonObject} containing the necessary data to initialize
     *               the {@code MentionPacket}, specifically the UUID. The object must have a
     *               "uuid" property as a string in valid UUID format.
     * @throws IllegalArgumentException if the packet ID in the JSON object does not match the expected ID
     *                                  or if the UUID is invalid.
     */
    public MentionPacket(JsonObject object) {
        super(PacketID.Global.MENTION.getId(), object);
        this.uuid = UUID.fromString(object.get("uuid").getAsString());
    }

    /**
     * Constructs a {@code MentionPacket} instance with a specified UUID.
     * The UUID uniquely identifies the specific mention associated with this packet.
     *
     * @param uuid the unique identifier for the mention packet, which ensures the
     *             global uniqueness of this packet within the message queue system.
     */
    public MentionPacket(UUID uuid) {
        super(PacketID.Global.MENTION.getId(), null);
        this.uuid = uuid;
    }

    /**
     * Serializes the instance into a {@code JsonObject}.
     * This method includes the UUID and other base properties defined in the parent class.
     * The resulting JSON object contains the unique identifier of the mention packet
     * and any additional base packet information, such as the packet ID and proxy ID.
     *
     * @return a {@code JsonObject} representing the serialized form of the {@code MentionPacket},
     *         including its UUID and inherited base properties.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("uuid", uuid.toString());
        return object;
    }
}
