package network.palace.core.messagequeue.packets;

import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;

/**
 * Represents a packet used to mention users by rank. This class extends {@link MQPacket}
 * and provides functionality for constructing and handling packets that target a specific rank or rank tag.
 * <p>
 * The packet contains information about the rank, an associated rank tag, and whether the match
 * should be exact. This allows for flexible and detailed targeting of rank-based mentions.
 * <p>
 * Fields:
 * - {@code rank}: The rank to be mentioned. Can be null.
 * - {@code tag}: An associated rank tag for more specific targeting. Can be null.
 * - {@code exact}: A boolean indicating whether the match should be exact.
 * <p>
 * This packet can be initialized using either a JSON object as input or explicit rank, tag,
 * and exact parameters. It can generate a corresponding JSON representation for serialization.
 * <p>
 * Constructor Details:
 * - {@link #MentionByRankPacket(JsonObject)}: Initializes the packet using a JSON object.
 *   Extracts the rank, tag, and exact values from the provided JSON if present.
 * - {@link #MentionByRankPacket(Rank, RankTag, boolean)}: Initializes the packet with explicitly
 *   defined rank, tag, and exact values.
 * <p>
 * Methods:
 * - {@link #getJSON()}: Creates a JSON representation of the packet, including the rank, tag,
 *   and exact fields if they are present.
 */
public class MentionByRankPacket extends MQPacket {
    /**
     * Represents the rank associated with the mention packet.
     * The {@code rank} determines the target rank for this packet's operation.
     * <p>
     * The rank can be any valid instance of the {@link Rank} enumeration, which defines
     * various hierarchical roles such as "Owner", "Manager", "Guest", etc.
     * <p>
     * This field may be null if the packet does not target a specific rank.
     */
    @Getter private final Rank rank;

    /**
     * Represents the rank tag associated with the mention packet.
     * The {@code tag} determines a specific subset or category of ranks
     * to be targeted within the scope of this packet's operation.
     * <p>
     * The {@code RankTag} enum defines various possible tags, such as
     * sponsor tiers, team roles, or other groupings. This field allows
     * for more granular targeting compared to the general {@code rank}.
     * <p>
     * This field may be null if the packet is not targeting a specific rank tag.
     */
    @Getter private final RankTag tag;

    /**
     * Indicates whether the rank-based mention operation should match exactly.
     * <p>
     * The {@code exact} flag determines if an operation requiring rank-based
     * mentions (e.g., sending messages or notifications) should strictly match
     * the specified rank and tag. If {@code true}, only the exact rank specified
     * will be considered valid. Otherwise, broader or less strict matching may be
     * acceptable.
     * <p>
     * This field is immutable and cannot be changed after the object is created.
     */
    @Getter private final boolean exact;

    /**
     * Constructs a MentionByRankPacket object with the specified JSON payload.
     * This packet is used to represent a mention command targeting specific ranks
     * with an optional tag and an exact match condition.
     *
     * @param object the JSON object containing packet data. The JSON should include
     *               the following keys: "rank" (optional, defines the rank to be mentioned),
     *               "tag" (optional, specifies the rank tag), and "exact" (mandatory, a boolean
     *               indicating if the match must be exact).
     *               If "rank" is present, it will be converted to a Rank object. If "tag"
     *               is provided, it will be converted to a RankTag object.
     */
    public MentionByRankPacket(JsonObject object) {
        super(PacketID.Global.MENTIONBYRANK.getId(), object);
        this.rank = object.has("rank") ? Rank.fromString(object.get("rank").getAsString()) : null;
        this.tag = object.has("tag") ? RankTag.fromString(object.get("tag").getAsString()) : null;
        this.exact = object.get("exact").getAsBoolean();
    }

    /**
     * Constructs a MentionByRankPacket object with the specified rank, rank tag, and exact match condition.
     * This packet is used to represent a mention command targeting specific ranks with optional tags
     * and the ability to specify whether the match must be exact.
     *
     * @param rank  the rank to be mentioned. It indicates the specific target rank.
     * @param tag   the rank tag associated with the mention. This further refines the targeting criteria.
     * @param exact a boolean flag indicating whether the rank and tag match must be exact.
     */
    public MentionByRankPacket(Rank rank, RankTag tag, boolean exact) {
        super(PacketID.Global.MENTIONBYRANK.getId(), null);
        this.rank = rank;
        this.tag = tag;
        this.exact = exact;
    }

    /**
     * Constructs a JsonObject representing the current state of the MentionByRankPacket.
     * The JSON object includes the base properties of the packet, as well as specific fields:
     * - "rank": the database name of the rank associated with the packet, if the rank is not null.
     * - "tag": the database name of the tag associated with the packet, if the tag is not null.
     * - "exact": a boolean indicating if the match must be exact.
     * <p>
     * The method uses the base JSON constructed by the superclass and appends rank-specific,
     * tag-specific, and exact match details if applicable.
     *
     * @return a JsonObject containing the serialized representation of this MentionByRankPacket.
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        if (rank != null) object.addProperty("rank", rank.getDBName());
        if (tag != null) object.addProperty("tag", tag.getDBName());
        object.addProperty("exact", exact);
        return object;
    }
}
