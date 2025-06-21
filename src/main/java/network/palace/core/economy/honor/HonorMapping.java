package network.palace.core.economy.honor;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a mapping between an honor level and the associated honor value.
 * This class is used to link a specific honor level with its corresponding
 * honor points in the system.
 */
@AllArgsConstructor
public class HonorMapping {
    /**
     * Represents the honor level associated with a specific honor mapping.
     * The level determines the rank or position in the honor system.
     */
    @Getter private int level;

    /**
     * Represents the honor points associated with a specific entity or mapping.
     * It is used to quantify a measure of honor within the system.
     */
    @Getter private int honor;
}
