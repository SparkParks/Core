package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.economy.currency.CurrencyType;

import java.util.UUID;

/**
 * Represents an event that is triggered when there is an update to a player's economy.
 * This event extends {@link CoreEvent}, which serves as the base structure for event handling
 * within the system.
 * <p>
 * The primary purpose of this event is to notify listeners or handlers whenever a player's
 * economy details, such as currency type and amount, are updated. This allows other parts
 * of the application to respond to these changes accordingly.
 * <p>
 * Features of this event include:
 * - The player associated with the economy update, identified by a {@link UUID}.
 * - The amount by which the player's economy has changed.
 * - The specific {@link CurrencyType} associated with the update.
 * <p>
 * This event can be dispatched and processed using the core event handling mechanisms.
 */
@AllArgsConstructor
public class EconomyUpdateEvent extends CoreEvent {
    /**
     * Represents the unique identifier (UUID) associated with a player or entity
     * involved in the economy update event.
     * <p>
     * This UUID serves as a globally unique reference, enabling the identification
     * and differentiation of players or entities within the system. It is especially
     * useful in ensuring consistency when handling events or interactions related
     * to player economy changes.
     */
    @Getter private UUID uuid;

    /**
     * Represents the amount associated with the economy update event.
     * <p>
     * This field specifies the numerical value indicating the change in a player's economy.
     * It can represent either an increase or decrease in the player's balance
     * and is associated with a specific {@link CurrencyType}.
     */
    @Getter private int amount;

    /**
     * Represents the type of currency involved in the economy update event.
     * <p>
     * This field is an instance of {@link CurrencyType}, which determines the specific
     * currency (such as BALANCE, TOKENS, or ADVENTURE) associated with the event.
     * It provides context for the economy update, specifying the relevant currency
     * impacted by the change.
     */
    @Getter private CurrencyType currency;
}
