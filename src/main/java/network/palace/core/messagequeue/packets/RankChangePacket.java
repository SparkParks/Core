package network.palace.core.messagequeue.packets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.player.Rank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The {@code RankChangePacket} class represents a specific type of message queue
 * packet used to communicate changes to a user's rank within a system. It is derived
 * from the {@code MQPacket} class and contains additional attributes specific
 * to rank change messages.
 * <p>
 * Attributes defined in this class include:
 * - {@code uuid}: The unique identifier of the user whose rank is being updated.
 * - {@code rank}: The new rank being assigned to the user.
 * - {@code tags}: A list of tags associated with the rank change operation, which
 *   may include metadata or additional information about the rank update.
 * - {@code source}: The source or origin of the rank change operation, which indicates
 *   where or why the change was triggered.
 * <p>
 * This class supports two constructors:
 * 1. A constructor that initializes an instance based on a JSON object, typically
 *    used for deserialization purposes when receiving a packet.
 * 2. A constructor that accepts the attributes directly, used when creating a new
 *    instance programmatically to transmit rank change information.
 * <p>
 * The {@code getJSON()} method provides a JSON representation of the packet for
 * serialization and transmission.
 */
@Getter
public class RankChangePacket extends MQPacket {
    /**
     * Represents the unique identifier associated with the user whose rank is being
     * updated by this {@code RankChangePacket}.
     * <p>
     * This field serves as a key to uniquely identify the user within the system.
     * It is utilized to track and apply the rank change to the correct user, ensuring
     * accuracy in processing rank-related updates.
     * <p>
     * The value of this field is immutable and is set during the construction of
     * the {@code RankChangePacket} instance, either from a {@code JsonObject} or
     * through direct parameterization. Once initialized, it cannot be changed.
     * <p>
     * The format of the {@code uuid} complies with the universally unique identifier
     * (UUID) standard, ensuring global uniqueness and consistency across operations.
     */
    private final UUID uuid;

    /**
     * Represents the specific rank associated with a user or entity.
     * The rank determines the user's role and permissions within the system.
     * This property is immutable and cannot be changed after initialization.
     * <p>
     * The rank is derived from the {@link Rank} enumeration, which defines
     * various user roles, associated names, visual formatting, and permission levels.
     */
    private final Rank rank;

    /**
     * A list of tag strings associated with the rank change operation.
     * <p>
     * This field stores metadata or descriptors used to classify or further define
     * the rank change event. Tags may include identifiers, contextual labels, or
     * any supplementary information necessary for the proper categorization and
     * handling of the rank change operation.
     * <p>
     * This field is initialized during the construction of the {@code RankChangePacket}
     * instance and remains immutable throughout the packet's lifecycle.
     */
    private final List<String> tags;

    /**
     * Represents the source responsible for initiating or triggering the rank change
     * encapsulated by this packet.
     * <p>
     * This field is used to store information about the context or origin of the rank
     * change operation, such as the system, module, or component that initiated the
     * rank modification. It is particularly useful for tracking the provenance of the
     * action in environments involving multiple sources of rank changes.
     * <p>
     * This field is immutable and must be initialized when creating an instance of
     * the {@code RankChangePacket} class.
     */
    private final String source;

    /**
     * Constructs a new RankChangePacket with the specified JSON data.
     * This packet is used to represent a change in rank for a specific user,
     * including additional metadata such as tags and the source of the change.
     *
     * @param object the {@code JsonObject} containing data for initializing the packet.
     *               It must include:
     *               - {@code "uuid"}: A string representing the UUID of the user.
     *               - {@code "rank"}: A string indicating the updated rank.
     *               - {@code "tags"}: A JSON array of strings representing tags associated with the user.
     *               - {@code "source"}: A string specifying the source of the rank update.
     * @throws IllegalArgumentException if the packet ID in the {@code JsonObject} does not match
     *                                  the expected ID (specified by the super constructor).
     */
    public RankChangePacket(JsonObject object) {
        super(PacketID.Global.RANK_CHANGE.getId(), object);
        this.uuid = UUID.fromString(object.get("uuid").getAsString());
        this.rank = Rank.fromString(object.get("rank").getAsString());
        this.tags = new ArrayList<>();
        for (JsonElement e : object.get("tags").getAsJsonArray()) {
            tags.add(e.getAsString());
        }
        this.source = object.get("source").getAsString();
    }

    /**
     * Creates a new instance of the {@code RankChangePacket} to represent a change
     * in the rank of a specific user, encapsulating information about the user's
     * identifier, updated rank, associated tags, and the source of the change.
     *
     * @param uuid the unique identifier of the user whose rank is being updated.
     * @param rank the new rank assigned to the user.
     * @param tags a list of tags associated with the user.
     * @param source the source or origin of the rank change.
     */
    public RankChangePacket(UUID uuid, Rank rank, List<String> tags, String source) {
        super(PacketID.Global.RANK_CHANGE.getId(), null);
        this.uuid = uuid;
        this.rank = rank;
        this.tags = tags;
        this.source = source;
    }

    /**
     * Constructs and returns a {@code JsonObject} representation of the current {@code RankChangePacket}.
     * This representation includes:
     * - Base properties derived from the {@code getBaseJSON()} method, such as packet ID and proxy ID (if applicable).
     * - The unique identifier of the user ({@code uuid}).
     * - The database-compatible name of the rank ({@code rank}).
     * - A JSON array of tags associated with the user ({@code tags}).
     * - The source of the rank change ({@code source}).
     *
     * @return a {@code JsonObject} containing the serialized data of the rank change packet,
     *         including user details, rank information, metadata, and other associated properties.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("rank", rank.getDBName());
        Gson gson = new Gson();
        object.add("tags", gson.toJsonTree(this.tags).getAsJsonArray());
        object.addProperty("source", source);
        return object;
    }
}