package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.player.CPlayer;

/**
 * Represents an event triggered when a player's ability to allow flight
 * is toggled. This event is called whenever the flight state of a player
 * is modified, either enabled or disabled.
 * <p>
 * This event contains information about the player whose flight state
 * was toggled and the new state of the flight ability.
 * <p>
 * The event extends CoreEvent, enabling it to utilize the event
 * handling system for registration and processing with associated
 * event handlers.
 */
@AllArgsConstructor
public class PlayerToggleAllowFlightEvent extends CoreEvent {
    /**
     * Represents the player whose flight ability state is being toggled within the event.
     * This field provides access to the player's data and attributes encapsulated
     * in the {@code CPlayer} object.
     */
    @Getter private CPlayer player;

    /**
     * Indicates the flight state of a player involved in this event.
     * Represents whether the flight ability is enabled or disabled for the player.
     * If {@code true}, the player is allowed to fly; if {@code false}, flight is disallowed.
     */
    @Getter private boolean flightState;
}
