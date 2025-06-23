package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;

/**
 * Represents a packet used to send messages to players based on their rank.
 * Extends the {@link MQPacket} class to support message-based communication within a system.
 * This class enables the delivery of messages to users filtered by specific ranks, with additional options
 * for exact rank matching and whether the message is a component-based message.
 */
public class MessageByRankPacket extends MQPacket {
    /**
     * The message intended for specific users based on rank and filtering criteria.
     *
     * <p>This field contains a string that represents the content of the message. It
     * is utilized by instances of the {@code MessageByRankPacket} class to facilitate
     * communication with users or groups of users in the system based on their rank.
     * The value of this variable is immutable and is set during the initialization
     * of the {@code MessageByRankPacket}.
     */
    @Getter private final String message;

    /**
     * The rank associated with a message in the {@code MessageByRankPacket}.
     * This variable determines the specific rank to which the message applies.
     * The {@link Rank} class contains the various rank definitions and their attributes.
     * <p>
     * A {@code Rank} includes properties such as:
     * - Name of the rank.
     * - Chat & scoreboard formatting details.
     * - Operational permissions and rank hierarchy information.
     * <p>
     * This variable is immutable and cannot be modified after initial assignment.
     */
    @Getter private final Rank rank;

    /**
     * Represents the rank tag associated with this message packet.
     * The {@code tag} determines additional context or classification
     * for the rank involved in the message transmission. This may
     * include information such as rank display formatting or
     * identification for processing.
     */
    @Getter private final RankTag tag;

    /**
     * Indicates whether the rank filtering in the {@code MessageByRankPacket} is performed as an exact match.
     * <p>
     * This boolean field determines if the rank specified within the message packet
     * must match precisely (true) or if a broader, non-exact match is acceptable (false).
     * It is immutable and set during the object construction.
     */
    @Getter private final boolean exact, componentMessage;

    /**
     * Constructs a {@code MessageByRankPacket} object using the provided {@link JsonObject}.
     * This constructor initializes the packet with specific attributes related to messages,
     * rank filtering, and additional options like exact match and component-based messages.
     *
     * @param object the {@link JsonObject} containing the packet data, including:
     *               - "message": a {@code String} representing the message content.
     *               - "rank": a {@code String} identifying the target rank.
     *               - "tag": an optional {@code String} specifying the rank tag, if present.
     *               - "exact": a {@code boolean} indicating whether to match the rank precisely.
     *               - "componentMessage": a {@code boolean} determining if the message is component-based.
     * @throws IllegalArgumentException if the packet ID in the supplied {@link JsonObject} does not match the expected ID.
     */
    public MessageByRankPacket(JsonObject object) {
        super(PacketID.Global.MESSAGEBYRANK.getId(), object);
        this.message = object.get("message").getAsString();
        this.rank = Rank.fromString(object.get("rank").getAsString());
        this.tag = object.has("tag") ? RankTag.fromString(object.get("tag").getAsString()) : null;
        this.exact = object.get("exact").getAsBoolean();
        this.componentMessage = object.get("componentMessage").getAsBoolean();
    }

    /**
     * Constructs a {@code MessageByRankPacket} object with the given parameters.
     * This constructor initializes the packet for sending messages to specific ranks
     * with configurable filters and options such as exact rank match and component-based messages.
     *
     * @param message the message content to be sent.
     * @param rank the target rank to which the message will be sent.
     * @param tag an optional rank tag to further filter the target recipients.
     * @param exact a boolean indicating whether the rank should be matched exactly.
     * @param componentMessage a boolean indicating if the message is component-based.
     */
    public MessageByRankPacket(String message, Rank rank, RankTag tag, boolean exact, boolean componentMessage) {
        super(PacketID.Global.MESSAGEBYRANK.getId(), null);
        this.message = message;
        this.rank = rank;
        this.tag = tag;
        this.exact = exact;
        this.componentMessage = componentMessage;
    }

    /**
     * Serializes the current {@code MessageByRankPacket} instance into a {@link JsonObject}.
     * This method constructs a JSON representation of the packet, including its message content,
     * rank information, tag (if available), and flags for exact matching and component-based messages.
     * <p>
     * The method relies on the base information from {@code getBaseJSON()} and adds specific properties:
     * - "message": The content of the message as a {@code String}.
     * - "rank": The database name of the rank as a {@code String}.
     * - "tag": The database name of the tag as a {@code String} (if available).
     * - "exact": A {@code boolean} indicating if the rank should be matched exactly.
     * - "componentMessage": A {@code boolean} specifying if the message is component-based.
     * <p>
     * If an error occurs during serialization, the exception is printed to the standard error stream,
     * and {@code null} is returned.
     *
     * @return a {@code JsonObject} representing the serialized packet, containing its properties
     *         and base JSON attributes. Returns {@code null} if an exception is encountered.
     */
    @Override
    public JsonObject getJSON() {
        try {
            JsonObject object = getBaseJSON();
            object.addProperty("message", message);
            object.addProperty("rank", rank.getDBName());
            if (tag != null) object.addProperty("tag", tag.getDBName());
            object.addProperty("exact", exact);
            object.addProperty("componentMessage", componentMessage);
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
