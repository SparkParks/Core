package network.palace.core.player.impl.managers;

import lombok.AllArgsConstructor;
import network.palace.core.Core;
import network.palace.core.achievements.CoreAchievement;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerAchievementManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.util.List;

/**
 * The {@code CorePlayerAchievementManager} class provides a concrete implementation of the {@link CPlayerAchievementManager} interface.
 * <p>
 * This class is responsible for managing the achievements of a specific player, allowing retrieval of earned achievements,
 * checking if a specific achievement has been obtained, and awarding new achievements. It integrates with the Core system
 * to communicate with achievement definitions, notifications, and persistence layers.
 * </p>
 * <p>
 * Key functionality includes:
 * <ul>
 *     <li>Maintaining a list of achievements represented by unique integer IDs.</li>
 *     <li>Checking if a player has earned a specific achievement.</li>
 *     <li>Awarding achievements and notifying the player with messages and sound effects.</li>
 *     <li>Persisting the awarded achievements asynchronously.</li>
 * </ul>
 * </p>
 */
@AllArgsConstructor
public class CorePlayerAchievementManager implements CPlayerAchievementManager {

    /**
     * Represents the {@link CPlayer} instance associated with this {@code CorePlayerAchievementManager}.
     * <p>
     * This variable is used to interact with the player to whom achievements are being managed. Key responsibilities
     * include:
     * <ul>
     *     <li>Sending achievement notifications to the player.</li>
     *     <li>Playing sound effects to signal achievement rewards.</li>
     *     <li>Adding rewards such as tokens upon earning new achievements.</li>
     *     <li>Fetching information pertaining to the player's identity, location, and UUID for further processing.</li>
     * </ul>
     * </p>
     * <p>
     * Being declared as {@code final}, this variable cannot be re-assigned to another {@link CPlayer} instance
     * after initialization, ensuring consistency in the context of achievement management for a specific player.
     * </p>
     */
    private final CPlayer player;
    /**
     * Represents a list of achievements earned by a player.
     * <p>
     * This variable holds the achievements as a {@link List} of integer IDs.
     * Each ID corresponds to a unique achievement in the system. The list:
     * <ul>
     *     <li>Stores the achievements currently obtained by the player.</li>
     *     <li>Is used to check if specific achievements have been earned.</li>
     *     <li>Facilitates the addition of new achievements to the player's collection.</li>
     *     <li>Enables persistence and notification features when new achievements are awarded.</li>
     * </ul>
     * </p>
     */
    private List<Integer> achievements;

    /**
     * Retrieves the list of achievements earned by the player.
     * <p>
     * Each achievement is represented by a unique integer ID.
     * </p>
     *
     * @return a {@code List<Integer>} containing the IDs of all achievements earned by the player.
     */
    @Override
    public List<Integer> getAchievements() {
        return achievements;
    }

    /**
     * Checks whether the player has obtained a specific achievement.
     * <p>
     * This method verifies if the provided achievement ID exists in the player's list of achievements.
     * </p>
     *
     * @param i the unique integer ID of the achievement to check
     * @return {@code true} if the player has obtained the achievement with the specified ID,
     *         {@code false} otherwise
     */
    @Override
    public boolean hasAchievement(int i) {
        return achievements.contains(i);
    }

    /**
     * Awards a specific achievement to the player if they have not already earned it.
     * <p>
     * This method checks if the player has already obtained the specified achievement. If not,
     * it is added to the player's achievement list, and the system performs the following actions:
     * <ul>
     *     <li>Retrieves the achievement details using the achievement ID.</li>
     *     <li>Sends a congratulatory message to the player, displaying the achievement's name
     *     and description.</li>
     *     <li>Plays a notification sound at the player's current location.</li>
     *     <li>Updates the achievement in the backend storage asynchronously.</li>
     *     <li>Awards tokens to the player as a reward for achieving the milestone.</li>
     * </ul>
     * If the achievement ID is invalid or does not exist in the achievement system, the method will simply return without any further actions.
     *
     * @param i The unique identifier of the achievement to be awarded. This ID corresponds to
     *          the {@code id} field of a {@code CoreAchievement}.
     */
    @Override
    public void giveAchievement(int i) {
        if (hasAchievement(i)) {
            return;
        }
        achievements.add(i);
        CoreAchievement ach = Core.getAchievementManager().getAchievement(i);
        if (ach == null) {
            return;
        }
        player.sendMessage(ChatColor.GREEN + "--------------" + ChatColor.GOLD + "" + ChatColor.BOLD + "Achievement" +
                ChatColor.GREEN + "--------------\n" + ChatColor.AQUA + ach.getDisplayName() + "\n" + ChatColor.GRAY +
                "" + ChatColor.ITALIC + ach.getDescription() + ChatColor.GREEN + "\n----------------------------------------");
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 100f, 0.75f);
//        Core.runTaskAsynchronously(() -> Core.getSqlUtil().addAchievement(player, i));
        Core.runTaskAsynchronously(Core.getInstance(), () -> Core.getMongoHandler().addAchievement(player.getUniqueId(), i));
        player.addTokens(5, "Achievement ID " + i);
        //TODO Make honor
    }
}
