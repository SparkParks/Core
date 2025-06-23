package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;

/**
 * Represents a packet for handling changes in bot rank within the Discord bot system.
 * This packet allows sending and receiving information about a user's rank, username, Discord ID,
 * and associated tags in the context of the message queue system.
 *
 * <ul>
 *   <li>Contains data such as the rank, username, Discord ID, and tags associated with the user.</li>
 *   <li>Can be initialized with a JSON object or explicitly passed parameters.</li>
 *   <li>Provides functionality to convert the packet back to a JSON object for transmission.</li>
 * </ul>
 */
public class BotRankChangePacket extends MQPacket {
    /**
     * Represents the rank information associated with a user within the context of
     * Discord bot rank changes. The rank defines the user's role or permission level.
     * <p>
     * This value is immutable and is intended to reflect the user's current rank
     * within the bot's hierarchy.
     */
    @Getter
    private final String rank, username, discordId, tags;

    /**
     * Constructs a new BotRankChangePacket instance using the provided JSON object.
     * This constructor is used to initialize the packet with the rank, username, Discord ID,
     * and tags from the JSON data. It verifies the packet ID for compatibility and deserializes
     * the necessary properties from the JSON object.
     *
     * @param object a JsonObject containing the serialized data for the packet.
     *               <p>
     *               Expected properties:
     *               <p>
     *               - "rank": the rank of the user as a string.
     *               <p>
     *               - "username": the username of the user as a string.
     *               <p>
     *               - "user": the Discord ID of the user as a string.
     *               <p>
     *               - "tags": the tags associated with the user as a string.
     */
    public BotRankChangePacket(JsonObject object) {
        super(PacketID.Global.DISCORD_BOT_RANKS.getId(), object);
        this.rank = object.get("rank").getAsString();
        this.username = object.get("username").getAsString();
        this.discordId = object.get("user").getAsString();
        this.tags = object.get("tags").getAsString();
    }

    /**
     * Constructs a new instance of BotRankChangePacket with the specified rank, username, Discord ID, and tags.
     *
     * @param rank      the rank of the user as a string, representing the user's role or permission level.
     * @param username  the username of the user, identifying them within the system.
     * @param discordId the Discord ID of the user, used for associating with their Discord account.
     * @param tags      additional tags or metadata associated with the user, provided as a string.
     */
    public BotRankChangePacket(String rank, String username, String discordId, String tags) {
        super(PacketID.Global.DISCORD_BOT_RANKS.getId(), null);
        this.rank = rank;
        this.username = username;
        this.discordId = discordId;
        this.tags = tags;
    }

    /**
     * Constructs a JSON representation of the `BotRankChangePacket` instance.
     * This includes the base JSON structure provided by the superclass along
     * with additional fields specific to the rank change packet, such as:
     * <p>
     * - The user's rank.
     * <p>
     * - The user's username.
     * <p>
     * - The user's Discord ID.
     * <p>
     * - Any associated tags for the user.
     *
     * @return a JsonObject containing the serialized representation of the rank change packet.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        object.addProperty("rank", rank);
        object.addProperty("username", username);
        object.addProperty("user", discordId);
        object.addProperty("tags", tags);
        return object;
    }
}
