package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * The {@code MQPacket} class serves as an abstract representation of a packet
 * used in message queue operations. It encapsulates common attributes and
 * methods required for serialization and deserialization of packet data in
 * the form of JSON. This class is intended to be extended by specific packet
 * types, which implement their own data and functionality.
 * <p>
 * Key functionalities provided by this class include:
 * - Initialization of the packet with an identifier.
 * - Serialization of packet data into bytes suitable for transmission.
 * - A base JSON representation including the packet ID and optional proxy ID.
 */
@RequiredArgsConstructor
public abstract class MQPacket {
    /**
     * Represents the unique identifier (ID) for the {@code MQPacket}.
     * <p>
     * This field serves as a key attribute in distinguishing different types
     * of packets in the message queue system. The ID is used for identification
     * and routing purposes within the system, ensuring that the correct packet
     * handlers can process the corresponding packet types.
     * <p>
     * The ID is initialized when the packet is constructed and is immutable
     * throughout the lifetime of the packet.
     */
    @Getter private int id;

    /**
     * Represents the unique identifier of the proxy responsible for sending the message
     * encapsulated within this {@code MQPacket}.
     * <p>
     * This field is an optional attribute used to track which proxy instance
     * initiated the sending of the packet in distributed systems or multi-proxy
     * environments. It is primarily utilized for handling routing or source-specific
     * logic in the message queue system to ensure efficient distribution and accurate
     * processing of packets.
     * <p>
     * The value is a {@link UUID} that uniquely identifies the proxy. If the packet has not
     * been associated with a proxy, this field will remain {@code null}.
     * <p>
     * Accessibility: This field is protected and can only be accessed or modified
     * within the {@code MQPacket} class or its subclasses.
     */
    @Getter protected UUID sendingProxy = null;

    /**
     * Constructs an {@code MQPacket} instance with the given packet ID and an optional JSON object.
     * This constructor initializes the packet's ID and validates that it matches the ID in the provided
     * JSON object, if one is supplied.
     *
     * @param id the unique identifier for the packet. This ID is used to distinguish different packet types
     *           in the message queue system.
     * @param object a {@code JsonObject} containing the packet's data. If this object is provided and has an "id"
     *               property, its value must match the {@code id} parameter. If the values do not match,
     *               an {@code IllegalArgumentException} is thrown. This parameter can be {@code null}.
     * @throws IllegalArgumentException if the "id" property in the provided {@code JsonObject} does not match the {@code id} parameter.
     */
    protected MQPacket(int id, JsonObject object) {
        this.id = id;
        if (object != null && object.get("id").getAsInt() != id)
            throw new IllegalArgumentException("Packet id does not match!");
    }

    /**
     * Constructs an {@code MQPacket} instance using the provided JSON object.
     * This constructor initializes the packet with the data contained in the given
     * {@code JsonObject}.
     *
     * @param obj the {@code JsonObject} containing the data for the packet. The object
     *            may include additional properties that define or configure the
     *            packet's behavior. This parameter can be {@code null}.
     */
    public MQPacket(JsonObject obj) {
    }

    /**
     * Returns a {@code JsonObject} representation of the packet, including all relevant
     * properties and data. This method is intended to be implemented by subclasses to
     * provide their specific serialization logic.
     *
     * @return a {@code JsonObject} containing the serialized data of the packet.
     */
    public abstract JsonObject getJSON();

    /**
     * Constructs and returns a base {@code JsonObject} representation of the packet.
     * The returned object includes base properties such as the packet ID and, if applicable,
     * the ID of the proxy sending the packet.
     *
     * @return a {@code JsonObject} containing the base properties of the packet, which may
     *         include "id" and "proxyID" fields depending on the packet's properties.
     */
    protected JsonObject getBaseJSON() {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        if (sendingProxy != null) object.addProperty("proxyID", sendingProxy.toString());
        return object;
    }

    /**
     * Converts the current packet to a byte array representation.
     * The method serializes the {@code JsonObject} representation of the packet into a UTF-8 encoded byte array.
     * If the {@code JsonObject} representation is null, an empty byte array is returned.
     *
     * @return a byte array containing the UTF-8 encoded serialized data of the packet. If the packet has
     *         no valid JSON representation, an empty byte array is returned.
     */
    public byte[] toBytes() {
        JsonObject obj = getJSON();
        if (obj != null) return obj.toString().getBytes(StandardCharsets.UTF_8);
        else return new byte[0];
    }
}
