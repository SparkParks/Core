package network.palace.core.events;

import network.palace.core.Core;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents a base event type that other custom events can extend from.
 * CoreEvent provides functionality for being called and handling event processing
 * through the use of a HandlerList.
 * <p>
 * This class simplifies the event handling mechanism and is intended to be
 * extended by concrete event implementations.
 */
public class CoreEvent extends Event {

    /**
     * The handler list for this event type.
     * It allows registration and management of event handlers associated with
     * this particular type of event. Provides methods to retrieve the handlers and
     * dispatch events to them during event processing.
     */
    private static final HandlerList handlers = new HandlerList();

    /**
     * Retrieves the handler list associated with this event type.
     * The handler list manages the event handlers registered for this event.
     *
     * @return the handler list for this event type
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Retrieves the handler list associated with the CoreEvent type.
     * The handler list is used to manage and process event handlers for this event type.
     *
     * @return the handler list for CoreEvent
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Calls the event using the core event system for processing.
     * This method dispatches the current event instance to all registered handlers
     * in the event processing system.
     * <p>
     * It ensures that the event is handled based on the registered event handlers
     * and their priority levels. The calling system processes the event
     * synchronously or asynchronously depending on the specific implementation
     * of the event dispatcher.
     */
    public void call() {
        Core.callEvent(this);
    }
}
