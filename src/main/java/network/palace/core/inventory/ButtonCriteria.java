package network.palace.core.inventory;

import network.palace.core.player.CPlayer;

/**
 * Represents a criterion to determine the visibility of a button
 * for a specific player.
 * <p>
 * This interface provides a contract for implementing visibility
 * logic based on the provided player's context or state.
 * <p>
 * Implementations of this interface should define the logic within
 * the isVisible method to decide whether a button should be visible
 * or not for the specified player.
 */
public interface ButtonCriteria {

    /**
     * Determines whether the button is visible for the specified player.
     * The visibility logic is determined by the implementing class.
     *
     * @param player the player for whom the visibility is being checked
     * @return true if the button is visible to the specified player, false otherwise
     */
    boolean isVisible(CPlayer player);
}
