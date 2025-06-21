package network.palace.core.events;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.messagequeue.packets.MQPacket;

/**
 * Represents an event triggered by the receipt of an incoming message packet.
 * This event extends {@link CoreEvent}, which provides the foundational structure
 * for handling and processing events within the application.
 * <p>
 * The primary purpose of this event is to notify listeners or handlers whenever
 * an incoming message packet is received, allowing components to respond accordingly.
 * <p>
 * Features of this event include:
 * - The unique identifier associated with the received packet.
 * - The JSON object representing the contents of the packet.
 * <p>
 * This event can be dispatched and processed using the core event handling mechanisms.
 */
@AllArgsConstructor
public class IncomingMessageEvent extends CoreEvent {
    /**
     * Represents the unique identifier associated with an incoming message packet.
     * This identifier serves as a reference for tracking and handling the specific
     * message packet that triggered the event.
     * <p>
     * The `id` is immutable and uniquely defines the context of the incoming message,
     * ensuring that event processing remains consistent and reliable for messaging
     * operations within the system.
     */
    @Getter private final int id;

    /**
     * Represents the content of an incoming message packet encapsulated as a JSON object.
     * <p>
     * This field provides direct access to the actual packet data, enabling event listeners
     * or handlers to parse and utilize the packet's contents. The packet is delivered in
     * JSON format, allowing for structured and efficient data handling.
     * <p>
     * The `packet` is immutable and is expected to contain key information relevant
     * to the event context, such as message details, metadata, or other information
     * required for processing the incoming message event.
     */
    @Getter private final JsonObject packet;
}
