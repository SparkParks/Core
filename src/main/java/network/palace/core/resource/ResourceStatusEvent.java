package network.palace.core.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.events.CoreEvent;
import network.palace.core.player.CPlayer;

/**
 * Represents an event that is triggered when the status of a resource pack changes
 * for a specific player. This event provides information about the player involved
 * and the current status of the resource pack.
 *
 * This class extends the {@link CoreEvent} to leverage the event handling system
 * provided for custom events. It allows event listeners to respond to changes
 * in the resource pack status of a player during gameplay.
 *
 * The resource pack statuses are defined by the {@link PackStatus} enumeration.
 * These statuses can help determine the state of the resource pack interaction, such as
 * whether it was accepted, loaded, failed, or declined by the player.
 *
 * Fields:
 * - {@code status}: Represents the current {@link PackStatus} of the resource pack.
 * - {@code player}: Represents the {@link CPlayer} associated with the event.
 */
@AllArgsConstructor
public class ResourceStatusEvent extends CoreEvent {

    /**
     * Represents the current status of the resource pack associated with the event.
     *
     * This status is derived from the {@link PackStatus} enumeration and indicates
     * the state of the resource pack interaction for the player. The status can be
     * used to determine whether the resource pack was accepted, loaded, failed, or
     * declined by the player during the event's lifecycle.
     */
    @Getter private PackStatus status;

    /**
     * Represents the {@link CPlayer} associated with this event.
     *
     * The player is the individual related to the resource pack status change.
     * This object provides detailed information about the player's identity
     * and current context within the event.
     */
    @Getter private CPlayer player;
}
