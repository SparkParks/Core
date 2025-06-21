package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.player.CPlayer;

/**
 * Represents an event triggered when a specific pack is received for a given player.
 * CurrentPackReceivedEvent extends CoreEvent, which allows it to utilize core event
 * handling mechanisms such as registration and dispatching through the CoreEvent system.
 * <p>
 * This event encapsulates the player associated with the pack being received and the
 * identifier or details of the received pack. It is used within the event-driven system
 * to notify relevant components or listeners of this occurrence.
 */
@AllArgsConstructor
public class CurrentPackReceivedEvent extends CoreEvent {
    /**
     * The player associated with the event indicating the reception of a specific pack.
     * Represents the individual to whom the pack was delivered and provides access to
     * their related data or operations via the CPlayer instance. This field is automatically
     * accessible through the generated getter method.
     */
    @Getter private CPlayer player;

    /**
     * Represents the identifier or details of the received pack associated with the event.
     * This field provides information about the specific pack received by the player
     * during a {@link CurrentPackReceivedEvent}.
     */
    @Getter private String pack;
}
