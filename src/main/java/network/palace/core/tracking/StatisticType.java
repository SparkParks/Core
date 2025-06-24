package network.palace.core.tracking;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a specific type of statistic within various game contexts.
 * Statistic types are categorized into different classes depending on their associated
 * game modes and are used to track specific types of metrics.
 *
 * Use this class to define and retrieve various statistic types for tracking player
 * or game performance metrics in specific game modes.
 *
 * Nested static classes define specific groups of statistics for each game mode:
 *
 * - The `Global` class defines statistics that are general across all games.
 * - The `Outpost` class defines statistics specific to the Outpost game mode.
 * - The `Spleef` class defines statistics specific to the Spleef game mode.
 * - The `OneShot` class defines statistics specific to the OneShot game mode.
 * - The `FreezeTag` class defines statistics specific to the Freeze Tag game mode.
 * - The `PIXIE_DUST_SHOOTOUT` class defines statistics specific to the Pixie Dust Shootout game mode.
 */
@AllArgsConstructor
public class StatisticType {

    /**
     * The `type` field represents the identifier for a specific statistic category.
     *
     * This value is used to uniquely identify and differentiate statistic types
     * across various game contexts. It serves as the primary attribute for
     * categorizing and managing statistics, ensuring that each statistic type can
     * be referenced and processed accurately within the application's tracking systems.
     */
    @Getter private String type;

    /**
     * Defines globally applicable statistics that are not specific to any single game mode.
     *
     * This class contains constants representing various statistic types
     * that are common across all game scenarios. These statistic types can
     * be used for tracking overall player performance across all game modes.
     *
     * Fields:
     * - `TIME_PLAYED`: Tracks the total time a player has spent playing.
     * - `WINS`: Tracks the number of games a player has won.
     * - `LOSSES`: Tracks the number of games a player has lost.
     */
    public static class Global {
        /**
         * Represents a statistic type that tracks the total time spent playing by a player.
         *
         * This statistic is globally applicable and is not limited to a specific game mode.
         * It is used to measure the cumulative playing time, typically represented in seconds
         * or other suitable time units. This information can be useful for analyzing
         * player engagement, activity levels, or time spent in the game.
         */
        public static final StatisticType TIME_PLAYED = new StatisticType("time_played");

        /**
         * Represents a statistic type that tracks the total number of games a player has won.
         *
         * This statistic is globally applicable and is not specific to any single game mode.
         * It is used to monitor player performance in terms of victories across all game modes.
         * Tracking wins can provide insight into a player's overall success and competitiveness.
         */
        public static final StatisticType WINS = new StatisticType("wins");

        /**
         * Represents a statistic type that tracks the total number of games a player has lost.
         *
         * This statistic is globally applicable and is not limited to any single game mode.
         * It is used to measure the cumulative number of losses a player has experienced
         * across all game modes. Tracking losses provides insight into overall player performance
         * and can help identify areas for improvement or trends in gameplay outcomes.
         */
        public static final StatisticType LOSSES = new StatisticType("losses");
    }

    /**
     * Defines statistics specific to the Outpost game mode.
     *
     * This class contains constants representing various types of statistics
     * that are tracked specifically for the Outpost game. These statistics
     * allow for detailed analysis and tracking of player performance within
     * this game mode.
     *
     * Fields:
     * - `ELIMINATIONS`: Tracks the total number of eliminations achieved by a player.
     * - `DEATHS`: Tracks the total number of deaths experienced by a player.
     * - `CONQUERED_TOWERS`: Tracks the total number of towers conquered by a player.
     * - `SECONDS_ON_TOWER`: Tracks the total time (in seconds) a player has spent
     *   on a tower, contributing to gameplay objectives.
     */
    public static class Outpost {
        /**
         * Represents a statistic type that tracks the total number of eliminations achieved by a player.
         *
         * This statistic is specific to the Outpost game mode and provides a measure of the player's
         * effectiveness in achieving eliminations during gameplay. It is a key metric for analyzing
         * player performance and success in competitive scenarios within the Outpost game.
         */
        public static final StatisticType ELIMINATIONS = new StatisticType("eliminations");

        /**
         * Represents a statistic type that tracks the total number of deaths experienced by a player.
         *
         * This statistic is specific to the Outpost game mode and provides a measure of how often
         * a player has been defeated during gameplay. It is a crucial metric for analyzing player
         * survivability and overall performance within the competitive environment of the game.
         */
        public static final StatisticType DEATHS = new StatisticType("deaths");

        /**
         * Represents a statistic type that tracks the total number of towers conquered by a player.
         *
         * This statistic is specific to the Outpost game mode and is used to measure a player's
         * success in taking control of towers during gameplay. It provides insight into a player's
         * contribution to strategic objectives and team success within the Outpost game mode.
         */
        public static final StatisticType CONQUERED_TOWERS = new StatisticType("conquered_towers");

        /**
         * Represents a statistic type that tracks the total time a player has spent on a tower.
         *
         * This statistic is specific to the Outpost game mode and is used to measure the player's
         * participation in controlling and defending towers during gameplay. The time is recorded
         * in seconds and contributes to analyzing a player's involvement in achieving objectives
         * within the Outpost game. It is a key metric for assessing player contributions to
         * strategic tower-based objectives.
         */
        public static final StatisticType SECONDS_ON_TOWER = new StatisticType("seconds_on_tower");
    }

    /**
     * The Spleef class contains predefined statistic types specific to the Spleef game.
     *
     * Spleef is a competitive multiplayer game where players attempt to remove blocks
     * from under their opponents, causing them to fall. The class defines statistics
     * that are tracked for players participating in this game.
     */
    public static class Spleef {
        /**
         * Represents a statistic type that tracks the total number of deaths experienced by a player
         * in the Spleef game mode.
         *
         * This statistic is specific to the Spleef game and measures how often a player falls or
         * is otherwise eliminated during a match. It serves as a key indicator of player performance
         * and survivability in this competitive multiplayer game. Tracking deaths helps to analyze
         * player behavior and gameplay outcomes within the Spleef mode.
         */
        public static final StatisticType DEATHS = new StatisticType("deaths");

        /**
         * Represents a statistic type that tracks the total number of blocks removed by a player
         * in the Spleef game mode.
         *
         * This statistic is specific to the Spleef game and measures the player's effectiveness
         * in breaking or removing blocks during gameplay. It serves as a key metric for analyzing
         * a player's strategic actions, gameplay style, and contribution to the match objectives.
         * Tracking the number of blocks removed helps to evaluate player performance and
         * involvement in the competitive aspects of the Spleef game.
         */
        public static final StatisticType BLOCKS_REMOVED = new StatisticType("blocks_removed");
    }

    /**
     * The OneShot class serves as a container for predefined statistic types
     * that are relevant within the context of a particular game mode or activity.
     *
     * These statistic types represent specific metrics or events that can be tracked
     * and recorded, providing insights into various aspects of gameplay performance.
     *
     * Fields:
     * - ELIMINATIONS: Represents the statistic type for tracking the number of eliminations.
     * - SHOTS_MISSED: Represents the statistic type for tracking the number of shots missed.
     */
    public static class OneShot {
        /**
         * Represents a predefined statistic type for tracking the number of eliminations
         * within a specific game mode or activity.
         *
         * This statistic type is used to record and analyze the player's performance
         * in terms of the eliminations they achieve during gameplay.
         */
        public static final StatisticType ELIMINATIONS = new StatisticType("eliminations");

        /**
         * Represents a predefined statistic type for tracking the number of shots missed
         * within a specific game mode or activity.
         *
         * This statistic type is used to record and analyze the player's performance
         * in terms of accuracy or missed opportunities during gameplay.
         */
        public static final StatisticType SHOTS_MISSED = new StatisticType("shots_missed");
    }

    /**
     * Represents specific statistic types used in the FreezeTag game mode.
     *
     * Provides predefined constants for tracking the number of players frozen and unfrozen
     * during gameplay. These constants are utilized for monitoring and analyzing player actions,
     * contributing to the overall game statistics management.
     */
    public static class FreezeTag {
        /**
         * Represents the statistic type for tracking the number of players
         * frozen in the FreezeTag game mode.
         *
         * This constant is used to monitor how many players have been frozen
         * during gameplay, aiding in game statistics and analysis.
         */
        public static final StatisticType FROZEN_PLAYERS = new StatisticType("frozen_players");

        /**
         * Represents the statistic type for tracking the number of players
         * unfrozen in the FreezeTag game mode.
         *
         * This constant is used to monitor how many players have been unfrozen
         * during gameplay, aiding in game statistics and analysis.
         */
        public static final StatisticType UNFROZEN_PLAYERS = new StatisticType("unfrozen_players");
    }

    /**
     * Represents a game-specific statistic type for the "Pixie Dust Shootout" game.
     *
     * This static nested class focuses on defining and encapsulating
     * statistics that are applicable to the "Pixie Dust Shootout" game mode,
     * aiding in tracking and evaluating player performance in the context
     * of this specific game mode.
     *
     * Key statistic included in this class:
     * - `ELIMINATIONS`: Tracks the count of eliminations achieved by a player
     *   during the game. It is represented as a specific type of statistic
     *   identifiable within the game's record-keeping systems.
     */
    public static class PIXIE_DUST_SHOOTOUT {
        /**
         * Represents the statistic type for tracking the number of eliminations
         * achieved by a player in the "Pixie Dust Shootout" game mode.
         *
         * This constant defines a specific metric used to quantify player performance
         * by counting successful eliminations during gameplay.
         */
        public static final StatisticType ELIMINATIONS = new StatisticType("eliminations");
    }
}
