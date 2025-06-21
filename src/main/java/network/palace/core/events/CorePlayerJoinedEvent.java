package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.player.CPlayer;

/**
 * Represents an event that is triggered when a player joins.
 * This event extends the {@link CoreEvent} and provides information about the joined player.
 * <p>
 * The event can be used to handle and process custom logic when a player joins.
 * It provides access to a {@link CPlayer} instance representing the player who has joined.
 */
@AllArgsConstructor
public class CorePlayerJoinedEvent extends CoreEvent {
    /**
     * Represents the player associated with the event.
     * This {@link CPlayer} instance provides access to information and functionality
     * related to the player who has joined.
     */
    @Getter private CPlayer player;
}
