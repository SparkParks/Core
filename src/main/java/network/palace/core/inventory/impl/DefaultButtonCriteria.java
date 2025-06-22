package network.palace.core.inventory.impl;

import network.palace.core.inventory.ButtonCriteria;
import network.palace.core.player.CPlayer;

/**
 * Default implementation of the ButtonCriteria interface.
 * <p>
 * This class provides a simple and consistent criterion where the button
 * is always visible regardless of the player's context or state.
 */
public class DefaultButtonCriteria implements ButtonCriteria {

    /**
     * Determines whether the button is visible for the given player.
     *
     * @param player the player for whom the visibility is being checked
     * @return true if the button is visible, false otherwise
     */
    @Override
    public boolean isVisible(CPlayer player) {
        return true;
    }
}
