package network.palace.core.economy.honor;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Represents a report containing information about an individual's ranking and honor points
 * within a system. The report includes unique identifiers, names, placements, and honor values
 * relevant to the person's standing.
 * <p>
 * This class is immutable, ensuring that its state cannot be modified once initialized.
 * <p>
 * Fields:
 * - uuid: The unique identifier associated with the individual for whom the report is generated.
 * - name: The name of the individual being represented in the report.
 * - place: The position or rank achieved by the individual in the honor system.
 * - honor: The total amount of honor points scored by the individual.
 * <p>
 * It is typically used to represent top-ranking individuals in contexts where honor points
 * signify prestige or achievement.
 */
@AllArgsConstructor
public class TopHonorReport {
    /**
     * The unique identifier associated with an individual within the report.
     * This UUID serves as a distinct key to uniquely identify the person
     * whose ranking and honor points are represented in this report.
     */
    @Getter private final UUID uuid;

    /**
     * The name of the individual being represented in the report.
     * This field identifies the person associated with the honor and ranking details.
     */
    @Getter private final String name;

    /**
     * The position or rank achieved by the individual in the honor system.
     * This field represents the placement of the person within the ranking context
     * and reflects their relative standing based on honor points or achievements.
     */
    @Getter private final int place;

    /**
     * The total amount of honor points scored by the individual.
     * This field represents the accumulation of points that signify the individual's
     * standing or achievements within an honor system. It is an integral part of
     * the ranking mechanism and reflects the level of prestige associated with the person.
     */
    @Getter private final int honor;
}