package network.palace.core.messagequeue.packets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.*;

/**
 * Represents a packet for sending a serialized component message, typically used within a message queue system.
 * This packet can include a serialized message and an optional list of players to whom the message is targeted.
 * It extends from the {@link MQPacket} class.
 */
public class ComponentMessagePacket extends MQPacket {
    /**
     * The serialized form of a component message represented as a {@code String}.
     * This variable typically stores a serialized representation of a message that
     * can be used for communication across components or systems, such as in message
     * queues. The contents are expected to be in a format suitable for reconstruction
     * or deserialization at the receiving end.
     */
    @Getter private final String serializedMessage;

    /**
     * A list of UUIDs representing the players targeted by the message in a message queue system.
     * This variable may be null if no specific players are targeted.
     * <p>
     * Typically used in scenarios where a message or component is directed to a particular group
     * of players identified by their unique UUIDs.
     */
    @Getter private final List<UUID> players;

    /**
     * Constructs a new ComponentMessagePacket using the provided JSON object.
     * Initializes the packet with a serialized message and an optional list of targeted players.
     *
     * @param object the JSON object containing the packet data. The "serializedMessage"
     *               property must be present as a string, and optionally, the "players"
     *               property can be present as a JSON array of UUIDs to specify targeted players.
     *               If no players are specified, it defaults to {@code null}.
     * @throws IllegalArgumentException if the provided JSON object contains an "id" field that does not
     *                                  match the expected packet ID.
     */
    public ComponentMessagePacket(JsonObject object) {
        super(PacketID.Global.COMPONENTMESSAGE.getId(), object);
        this.serializedMessage = object.get("serializedMessage").getAsString();
        if (object.has("players")) {
            this.players = new ArrayList<>();
            JsonArray players = object.get("players").getAsJsonArray();
            for (JsonElement e : players) {
                this.players.add(UUID.fromString(e.getAsString()));
            }
        } else {
            this.players = null;
        }
    }

    /**
     * Constructs a new {@code ComponentMessagePacket} with the specified components and an optional target player UUID.
     * This packet encapsulates a serialized representation of the provided components and targets a specific
     * player if a UUID is provided; otherwise, it applies globally.
     *
     * @param components an array of {@link BaseComponent} objects representing the message content.
     *                   These components will be serialized into a JSON string to be sent as the packet's payload.
     * @param uuid       the {@code UUID} of a target player to whom this message packet will be directed.
     *                   If {@code null}, the packet will not target any specific player.
     */
    public ComponentMessagePacket(BaseComponent[] components, UUID uuid) {
        super(PacketID.Global.COMPONENTMESSAGE.getId(), null);
        this.serializedMessage = ComponentSerializer.toString(components);
        if (uuid != null) {
            this.players = new ArrayList<>(Collections.singletonList(uuid));
        } else {
            this.players = null;
        }
    }

    /**
     * Constructs a new {@code ComponentMessagePacket} with a serialized message
     * and a list of target players.
     *
     * @param serializedMessage the serialized message content as a string. This represents
     *                          the message to be sent within this packet.
     * @param players           an optional varargs parameter of {@code UUID}s, representing
     *                          the unique identifiers of targeted players. If no players
     *                          are specified, the packet will be broadcast to all recipients.
     */
    public ComponentMessagePacket(String serializedMessage, UUID... players) {
        super(PacketID.Global.COMPONENTMESSAGE.getId(), null);
        this.serializedMessage = serializedMessage;
        this.players = new ArrayList<>(Arrays.asList(players));
    }

    /**
     * Constructs and returns a JSON representation of the current {@code ComponentMessagePacket}.
     * The JSON object includes a base structure with the packet ID and proxy ID,
     * along with additional properties specific to this packet type:
     * - "serializedMessage": The serialized message as a string
     * - "players": An optional JSON array containing the UUIDs of the targeted players
     *
     * @return a {@code JsonObject} containing the serialized data of this packet,
     *         including its base properties and any additional packet-specific properties.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("serializedMessage", serializedMessage);
        if (players != null) {
            Gson gson = new Gson();
            object.add("players", gson.toJsonTree(players).getAsJsonArray());
        }
        return object;
    }
}