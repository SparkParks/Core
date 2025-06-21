package network.palace.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.player.CPlayer;
import network.palace.core.tracking.GameType;
import network.palace.core.tracking.StatisticType;

/**
 * Represents an event triggered when a player's game statistics change.
 * This class extends CoreEvent, inheriting base event functionality such as
 * event handling and dispatching.
 * <p>
 * GameStatisticChangeEvent provides information about the specific statistic
 * that has changed, including the player involved, the type of game, the
 * type of statistic, and the amount of change.
 */
@AllArgsConstructor
public class GameStatisticChangeEvent extends CoreEvent {
    /**
     * The player involved in the game statistic change event.
     * Represents the CPlayer instance associated with the event,
     * providing access to the player's details and attributes.
     */
    @Getter private CPlayer player;

    /**
     * Represents the type of game associated with the statistic change event.
     * <p>
     * This field specifies the {@link GameType} for which the statistic change has occurred,
     * providing context about the game mode or activity related to the event.
     * It is used for identifying and categorizing game-specific events.
     */
    @Getter private GameType gameType;

    /**
     * Represents the type of statistic associated with the game statistic change event.
     * This field contains an instance of {@link StatisticType}, which categorizes
     * the specific metric being updated (e.g., Time Played, Wins, Deaths).
     * <p>
     * It provides context about the nature of the statistic being changed during
     * the event and is crucial for distinguishing between various statistical updates
     * in gameplay scenarios.
     */
    @Getter private StatisticType statisticType;

    /**
     * Represents the amount of change in a game statistic during an event.
     * This field specifies the numerical value indicating the modification
     * of a specific statistic (increase or decrease) associated with the event.
     * The amount is integral to understanding the scale or magnitude of the
     * statistic change in gameplay scenarios.
     */
    @Getter private int amount;
}
