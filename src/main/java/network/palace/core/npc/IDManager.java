package network.palace.core.npc;

import network.palace.core.Core;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * The IDManager class is responsible for generating unique ID values that do not
 * conflict with existing IDs in a collection of entities across different worlds.
 * It ensures ID uniqueness by verifying against a list of entities retrieved
 * from various worlds.
 */
public class IDManager {

    /**
     * Represents the current ID value used for generating unique IDs within the IDManager.
     *
     * This variable is initialized to 2000 and is incremented each time a new unique ID is generated
     * by the {@code getNextID} method. It ensures that each ID is unique by performing checks against
     * existing IDs within the entities across multiple worlds.
     *
     * The value of this variable plays a critical role in maintaining a continuous,
     * non-conflicting sequence of IDs for entities handled by the system.
     */
    private int CURRENT = 2000;

    /**
     * Generates and returns the next unique ID.
     *
     * This method increments the current ID value and ensures that the resulting ID
     * is unique by performing a check against existing IDs using the {@code isDuplicate} method.
     * If a conflict is detected, the ID is further incremented until a unique value is found.
     *
     * @return the next unique integer ID.
     */
    public int getNextID() {
        do {
            CURRENT++;
        } while (isDuplicate(CURRENT));
        return CURRENT;
    }

    /**
     * Checks whether the given ID already exists among the entities in all worlds.
     *
     * This method iterates through all worlds and their entities to determine
     * if any entity matches the specified ID, allowing the system to verify
     * ID uniqueness.
     *
     * @param id the ID to check for duplication
     * @return true if the ID exists among the entities, false otherwise
     */
    private boolean isDuplicate(int id) {
        for (World world : Core.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getEntityId() == id) return true;
            }
        }
        return false;
    }
}
