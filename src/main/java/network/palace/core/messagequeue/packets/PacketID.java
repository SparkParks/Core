package network.palace.core.messagequeue.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The PacketID class defines and encapsulates various global packet identifiers
 * used within a message queue system. Each identifier corresponds to a specific
 * type of action or operation that can be communicated within the system.
 * <p>
 * This class provides an enumeration called {@code Global}, which contains
 * a set of predefined constants with their unique integer identifiers. These
 * identifiers are used in message routing, handling, and processing within
 * the system.
 * <p>
 * The {@code Global} enumeration can be utilized to reference specific packet
 * types in a standardized way, making it easier to manage and interpret
 * packets in a distributed environment.
 */
public class PacketID {

    /**
     * The {@code Global} enumeration defines a set of constant packet identifiers
     * used for different types of operations in a networked message queue system.
     * Each constant is associated with a unique integer ID to facilitate routing
     * and recognition of messages across the system.
     * <p>
     * This enumeration is utilized to standardize the process of identifying and
     * handling messages, ensuring consistency and clarity in the communication
     * between different components of the system.
     * <p>
     * Each constant represents a specific type of action or event that can be
     * invoked or communicated, such as broadcasting messages, managing servers,
     * handling user-specific actions (e.g., bans, kicks, mutes), or coordinating
     * queue operations.
     * <p>
     * Fields:
     * - {@code id}: The integer value uniquely identifying the packet or operation type.
     */
    @AllArgsConstructor
    enum Global {
        BROADCAST(1), MESSAGEBYRANK(2), PROXYRELOAD(3), DM(4), MESSAGE(5), COMPONENTMESSAGE(6),
        CLEARCHAT(7), CREATESERVER(8), DELETESERVER(9), MENTION(10), IGNORE_LIST(11), CHAT(12),
        CHAT_ANALYSIS(13), CHAT_ANALYSIS_RESPONSE(14), SEND_PLAYER(15), CHANGE_CHANNEL(16), CHAT_MUTED(17),
        MENTIONBYRANK(18), KICK_PLAYER(19), KICK_IP(20), MUTE_PLAYER(21), BAN_PROVIDER(22), FRIEND_JOIN(23),
        PARK_STORAGE_LOCK(24), REFRESH_WARPS(25), MULTI_SHOW_START(26), MULTI_SHOW_STOP(27), CREATE_QUEUE(28),
        REMOVE_QUEUE(29), UPDATE_QUEUE(30), PLAYER_QUEUE(31), BROADCAST_COMPONENT(32), EMPTY_SERVER(33),
        RANK_CHANGE(34), LOG_STATISTIC(35), DISCORD_BOT_RANKS(1);

        /**
         * Represents the unique integer identifier assigned to a specific packet
         * or operation type within the {@code Global} enumeration.
         * <p>
         * This field is used to uniquely identify and standardize different types
         * of actions or events in the associated networked message queue system.
         * Each enumeration constant in {@code Global} is linked to a distinct
         * {@code id}, ensuring clarity and consistency in message identification
         * and processing.
         * <p>
         * The value of this field is immutable and defined at the time of creation
         * for each constant in the enumeration.
         */
        @Getter private final int id;
    }
}
