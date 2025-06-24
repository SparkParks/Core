package network.palace.core.tracking;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing types of games and their associated attributes.
 * Each game type is identified by a unique ID, a display name, and a database name.
 * This allows for easy categorization and integration with game-related systems.
 *
 * Attributes:
 * - `id`: A unique identifier for the game type.
 * - `name`: The display name of the game type.
 * - `dbName`: The name used in the database to represent the game type.
 */
@AllArgsConstructor
public enum GameType {

    /**
     * Represents the Deathrun game type.
     *
     * Deathrun is a type of game where players navigate through challenging obstacles,
     * aiming to complete the course as quickly as possible while avoiding traps.
     * This constant holds information essential for identifying and interacting
     * with the game's related systems, including:
     * - A unique identifier (`1`) for internal differentiation.
     * - The display name (`Deathrun`) for user-facing contexts.
     * - The database name (`deathrun`) for backend integration.
     */
    DEATHRUN(1, "Deathrun", "deathrun"), PIXIE_DUST_SHOOTOUT(2, "Pixie Dust Shootout", "pixie");

    /**
     * A unique identifier for the game type.
     *
     * This identifier distinguishes each game type within the system
     * and is utilized for internal differentiation and interaction
     * with game-related functionalities.
     */
    @Getter int id;

    /**
     * The display name of the game type.
     *
     * This name is intended for user-facing contexts and provides a readable identifier
     * that represents the game type in a descriptive and accessible manner.
     */
    @Getter String name;

    /**
     * The name used in the database to represent the game type.
     *
     * This value ensures seamless backend integration and data consistency
     * when interacting with the database for game type management and retrieval.
     */
    @Getter String dbName;
}
