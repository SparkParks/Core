package network.palace.core.achievements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a core achievement within the system.
 * <p>
 * This class models an achievement with an integer identifier, a display name, and a description.
 * Each achievement is uniquely identified by its ID and is used within the {@code AchievementManager}
 * to manage achievement data and handle user progress.
 * <p>
 * Fields:
 * <p>
 * - id: A unique identifier for the achievement.
 * <p>
 * - displayName: The name displayed to users for this achievement.
 * <p>
 * - description: A brief description or details about the achievement.
 */
@AllArgsConstructor
public class CoreAchievement {
    /**
     * Represents the unique identifier for an achievement.
     * <p>
     * This field stores an integer value that uniquely identifies a specific achievement.
     * It is used to differentiate one achievement from another within the system and
     * plays a key role in managing achievements in the {@code AchievementManager}.
     */
    @Getter private int id;

    /**
     * Represents the display name of an achievement.
     * <p>
     * This field stores a string value used to display the name of the achievement
     * to the users in a human-readable format. It serves as a key attribute for
     * providing a description or label to identify the achievement within the system.
     */
    @Getter @Setter private String displayName;

    /**
     * Represents a brief description or details about an achievement.
     * <p>
     * This field stores a string value that provides additional context or explanation
     * regarding the specific achievement it pertains to. The description is utilized to
     * give users a better understanding of the achievement's purpose or requirements
     * within the system.
     */
    @Getter @Setter private String description;
}