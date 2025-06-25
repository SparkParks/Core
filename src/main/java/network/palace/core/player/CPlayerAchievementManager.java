package network.palace.core.player;

import java.util.List;

/**
 * The {@code CPlayerAchievementManager} interface provides methods to manage a player's achievements.
 * <p>
 * Implementations of this interface allow for querying and modifying a player's achievements, including:
 * <ul>
 *     <li>Retrieving a list of earned achievements.</li>
 *     <li>Checking if a specific achievement has been obtained.</li>
 *     <li>Granting an achievement to the player.</li>
 * </ul>
 * </p>
 */
public interface CPlayerAchievementManager {

    /**
     * Retrieves a list of achievements represented by their unique integer IDs.
     *
     * <p>
     * Each integer in the returned list corresponds to an achievement
     * that the player has earned or unlocked within the system.
     * </p>
     *
     * @return a {@link List} of {@link Integer} objects where each integer represents a unique achievement ID.
     */
    List<Integer> getAchievements();

    /**
     * Checks whether the player has obtained a specific achievement.
     *
     * <p>This method checks if an achievement with the given identifier exists
     * in the player's collection of achievements.</p>
     *
     * @param id the unique identifier of the achievement to be checked
     *           <ul>
     *               <li>It must be a valid integer representing an achievement ID.</li>
     *           </ul>
     * @return {@code true} if the achievement with the specified ID exists in the player's achievements;
     *         {@code false} otherwise
     */
    boolean hasAchievement(int id);

    /**
     * Grants an achievement to the player associated with the given achievement ID.
     * <p>
     * This method awards an achievement to a player if the achievement ID exists
     * and the player does not already have the specified achievement.
     * </p>
     *
     * @param id the unique identifier of the achievement to be awarded to the player.
     *           <ul>
     *           <li>The ID must correspond to a valid and defined achievement.</li>
     *           <li>If the player already has the achievement, the operation is redundant.</li>
     *           </ul>
     */
    void giveAchievement(int id);
}
