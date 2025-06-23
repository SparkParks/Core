package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;

/**
 * Represents a packet that indicates an empty server in the system.
 * <p>
 * This packet is sent with information about a specific server but signifies
 * that the server contains no active users or is flagged as empty.
 * <p>
 * It extends the {@code MQPacket} class and uses a predefined packet ID
 * from {@code PacketID.Global.EMPTY_SERVER}.
 * <p>
 * The class provides JSON serialization functionality, ensuring
 * that the server name is included in the serialized format.
 * <p>
 * Constructors:
 * <ul>
 *     <li>{@code EmptyServerPacket(JsonObject object)}: Initializes the object
 *     using data from a given JSON representation, extracting the server name.
 *     </li>
 *     <li>{@code EmptyServerPacket(String server)}: Initializes the object directly
 *     using a server name.
 *     </li>
 * </ul>
 * Overrides:
 * <ul>
 *     <li>{@code getJSON()}: Constructs and returns a JSON representation of the
 *     packet, including its base properties and the server name.
 *     </li>
 * </ul>
 */
@Getter
public class EmptyServerPacket extends MQPacket {
    /**
     * The name of the server associated with this packet.
     * <p>
     * This field holds the identifier of the server that the packet concerns. It is
     * used to specify the target server for which the data in the packet is relevant.
     * This value is immutable and is initialized through the constructor.
     * <p>
     * In the context of the {@code EmptyServerPacket} class, this variable provides
     * the name of the server that is being represented as empty.
     */
    private final String server;

    /**
     * Constructs an instance of {@code EmptyServerPacket} using the provided JSON object.
     * This constructor extracts the server name from the given JSON representation
     * and initializes the packet with it. Additionally, it assigns a predefined packet ID
     * specific to the "Empty Server" packet.
     *
     * @param object a {@code JsonObject} representing the data for this packet.
     *               This object must include the "server" field, which specifies
     *               the name of the server associated with the packet.
     *               If the "id" in the {@code JsonObject} does not match the expected ID,
     *               an {@code IllegalArgumentException} is thrown.
     */
    public EmptyServerPacket(JsonObject object) {
        super(PacketID.Global.EMPTY_SERVER.getId(), object);
        this.server = object.get("server").getAsString();
    }

    /**
     * Constructs an instance of {@code EmptyServerPacket} with the specified server name.
     * This packet is associated with a server that is marked as empty and includes
     * a predefined packet ID for "Empty Server".
     *
     * @param server the name of the server that the packet represents. This value
     *               specifies the server identified as empty. It must not be null.
     */
    public EmptyServerPacket(String server) {
        super(PacketID.Global.EMPTY_SERVER.getId(), null);
        this.server = server;
    }

    /**
     * Converts the {@code EmptyServerPacket} instance into a JSON representation.
     * The generated JSON object includes the base packet information and the
     * server name associated with this packet.
     *
     * @return a {@code JsonObject} representing the serialized form of the
     *         {@code EmptyServerPacket}, containing the base packet data and
     *         the "server" property.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("server", server);
        return object;
    }
}