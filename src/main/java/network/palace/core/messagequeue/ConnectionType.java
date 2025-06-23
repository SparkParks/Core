package network.palace.core.messagequeue;

/**
 * Represents the type of connection for a message-oriented middleware client.
 * This enum is used to define the role of a connection within the system's messaging architecture.
 * <p>
 * The possible connection types are:
 * <p>
 * - PUBLISHING: Used for publishing messages to an exchange.
 * - CONSUMING: Used for consuming messages from a queue.
 * - OTHER: Represents any connection type that does not fall under publishing or consuming.
 */
public enum ConnectionType {
    PUBLISHING, CONSUMING, OTHER
}
