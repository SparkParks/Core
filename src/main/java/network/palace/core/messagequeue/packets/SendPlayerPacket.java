package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;

/**
 * The SendPlayerPacket class represents a packet used to request the transfer of a player
 * from one server to another within a distributed system. This packet is sent through a
 * message queue and includes details necessary to identify the target player and the
 * destination server.
 *<p>
 * This class extends the {@code MQPacket} class, inheriting its properties for packet
 * serialization and deserialization.
 */
public class SendPlayerPacket extends MQPacket {
    // Players can be targeded by username, UUID, 'Server:Hub1', or 'all'

    /**
     * Represents the identifier of the target player to be transferred.
     * This field specifies the recipient of the {@code SendPlayerPacket},
     * which can take various forms depending on the targeting method:
     * <p>
     * 1. A username (String) to address a specific player.
     * 2. A UUID (String) to uniquely identify a player.
     * 3. A server-specific destination in the format "Server:HubX" to route all players from a specific server.
     * 4. The value "all" to indicate a broadcast to all players.
     * <p>
     * The {@code targetPlayer} field is immutable and is initialized during the construction
     * of the packet. It is used primarily to determine the intended recipient(s) for the
     * packet's actions in a distributed system, ensuring that the operation is executed
     * on the correct entities.
     */
    @Getter private final String targetPlayer, targetServer;

    /**
     * Constructs a {@code SendPlayerPacket} instance using the provided {@code JsonObject}.
     * This constructor initializes the packet with the relevant data for targeting a player
     * and a server where the player should be transferred. It retrieves the values for
     * {@code targetPlayer} and {@code targetServer} from the given {@code JsonObject}.
     *
     * @param object a {@code JsonObject} containing the details for the packet. This
     *               must include properties named {@code targetPlayer} and {@code targetServer},
     *               which specify the identifier of the player to be targeted and the
     *               name of the destination server, respectively.
     */
    public SendPlayerPacket(JsonObject object) {
        super(PacketID.Global.SEND_PLAYER.getId(), object);
        this.targetPlayer = object.get("targetPlayer").getAsString();
        this.targetServer = object.get("targetServer").getAsString();
    }

    /**
     * Constructs a {@code SendPlayerPacket} instance using the provided target player
     * and target server identifiers. This packet is used to request the transfer of
     * a player to a specific server within the networked system.
     *
     * @param targetPlayer the identifier of the target player to be transferred. This can
     *                     be a username, a UUID, or a special keyword like "all" to indicate
     *                     multiple players.
     * @param targetServer the identifier of the server where the player should be transferred.
     */
    public SendPlayerPacket(String targetPlayer, String targetServer) {
        super(PacketID.Global.SEND_PLAYER.getId(), null);
        this.targetPlayer = targetPlayer;
        this.targetServer = targetServer;
    }

    /**
     * Retrieves the JSON representation of the packet, including the target player
     * and the target server as additional properties. This method invokes the base
     * packet's JSON construction and appends specific information related to the
     * {@code SendPlayerPacket}.
     *
     * @return a {@code JsonObject} representing the packet, containing the base properties
     *         as well as the "targetPlayer" and "targetServer" fields.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("targetPlayer", targetPlayer);
        object.addProperty("targetServer", targetServer);
        return object;
    }
}
