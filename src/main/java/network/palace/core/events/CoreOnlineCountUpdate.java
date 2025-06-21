package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents an event that encapsulates an update to the count of online users in the system.
 * This event extends {@link CoreEvent}, which provides the foundational structure
 * for handling and processing events within the application.
 * <p>
 * The primary purpose of this event is to notify listeners or handlers when the online
 * user count is updated, allowing them to react accordingly.
 * <p>
 * This event includes:
 * - The updated online user count.
 * <p>
 * The event can be dispatched and processed using the core event processing system.
 */
@AllArgsConstructor
public class CoreOnlineCountUpdate extends CoreEvent {
    /**
     * Represents the updated count of online users in the system.
     * This value is used to indicate the current number of users
     * actively online during the event's occurrence.
     */
    @Getter private int count;
}
