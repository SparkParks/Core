package network.palace.core.messagequeue.packets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Represents a packet containing a message and a list of players,
 * used for message communication in a distributed system.
 * <p>
 * This packet can either be constructed by providing a JSON object
 * with message and player data or by specifying the message and players directly.
 */
public class MessagePacket extends MQPacket {
    /**
     * The message contained in this packet.
     * <p>
     * This field stores a string message intended for communication within
     * a distributed system. It is immutable and must be initialized during
     * the construction of the packet. The message is designed to be serialized
     * and deserialized as part of the packet's JSON representation for
     * transmission through the system.
     * <p>
     * The value of this field represents the textual content conveyed
     * by the packet, such as notifications, chat messages, or other
     * forms of communication.
     */
    @Getter private final String message;

    /**
     * A list of players associated with this packet, represented by their unique identifiers (UUID).
     * <p>
     * This field stores a collection of {@link UUID} objects that uniquely identify the players
     * related to the packet. It is immutable and must be initialized at the time of object creation.
     * The list of players can be used for various purposes, such as targeting specific players
     * for messages or operations within a distributed system.
     */
    @Getter private final List<UUID> players;

    /**
     * Constructs a new MessagePacket instance using the provided JSON object.
     * The JSON object must contain a "message" field, which is assigned to the message field,
     * and a "players" field, which is expected to be an array of strings representing UUIDs.
     * These UUIDs are converted into a list of UUIDs and assigned to the players field.
     *
     * @param object the JSON object containing the message and the list of player UUIDs
     * @throws IllegalArgumentException if the packet ID in the provided JSON object does not match the expected ID
     * @throws NullPointerException if the "message" field or the "players" field in the JSON object is missing or null
     */
    public MessagePacket(JsonObject object) {
        super(PacketID.Global.MESSAGE.getId(), object);
        this.message = object.get("message").getAsString();
        this.players = new ArrayList<>();
        JsonArray array = object.get("players").getAsJsonArray();
        for (JsonElement e : array) {
            players.add(UUID.fromString(e.getAsString()));
        }
    }

    /**
     * Constructs a new {@code MessagePacket} with the specified message and list of player UUIDs.
     *
     * @param message the message string to be delivered with the packet. This message represents
     *                the content being sent to the specified players.
     * @param players the list of {@code UUID} representing the players to whom the message
     *                will be delivered. Each UUID in the list uniquely identifies a player.
     */
    public MessagePacket(String message, List<UUID> players) {
        super(PacketID.Global.MESSAGE.getId(), null);
        this.message = message;
        this.players = players;
    }

    /**
     * Constructs a new {@code MessagePacket} with the specified message and an array of player UUIDs.
     *
     * @param message the string message to be sent, representing the content of the packet.
     * @param players the array of {@code UUID} objects representing the players to whom the message
     *                will be delivered. Each UUID uniquely identifies a player.
     */
    public MessagePacket(String message, UUID... players) {
        super(PacketID.Global.MESSAGE.getId(), null);
        this.message = message;
        this.players = new ArrayList<>(Arrays.asList(players));
    }

    /**
     * Converts the current {@code MessagePacket} instance into a {@code JsonObject}
     * for serialization and transmission.
     * <p>
     * The resulting JSON object includes the base packet data, such as the ID and
     * optional proxy ID, along with the specific data for this packet:
     * - A "message" property containing the message string.
     * - A "players" array property containing a list of player UUID strings.
     *
     * @return a {@code JsonObject} representing the serialized form of the
     *         {@code MessagePacket}, which includes the message, player UUIDs, and
     *         inherited base properties.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("message", message);

        JsonArray players = new JsonArray();
        this.players.forEach(p -> players.add(p.toString()));
        object.add("players", players);

        return object;
    }
}
