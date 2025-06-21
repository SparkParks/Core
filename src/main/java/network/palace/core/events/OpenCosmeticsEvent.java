package network.palace.core.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import network.palace.core.player.CPlayer;

/**
 * Represents an event triggered when a player's cosmetics menu is opened.
 * This event is specific to a player and can be cancelled if necessary,
 * preventing further processing or actions associated with opening the cosmetics menu.
 * <p>
 * The event contains a reference to the player for whom the event is triggered,
 * and provides functionality for cancelling the event behavior.
 * <p>
 * This event extends the CoreEvent class, which provides the base functionality
 * for event handling within the system.
 */
@Getter
@RequiredArgsConstructor
public class OpenCosmeticsEvent extends CoreEvent {
    /**
     * The player for whom the cosmetics menu open event is triggered.
     * Represents the specific player associated with this event.
     * This field is immutable as it is declared final.
     */
    private final CPlayer player;

    /**
     * Indicates whether the event has been cancelled.
     * <p>
     * If set to {@code true}, the event is considered cancelled, and any associated
     * actions or further processing tied to the event may be halted depending on the
     * system's event handling logic.
     * <p>
     * This field can be modified to cancel or restore the event's execution
     * during its lifecycle.
     */
    @Setter private boolean cancelled = true;
}
