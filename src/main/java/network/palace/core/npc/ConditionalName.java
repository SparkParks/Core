package network.palace.core.npc;

import network.palace.core.player.CPlayer;

/**
 * Represents an abstraction for generating a custom name based on a given player's context.
 * This class is designed to be extended, where specific implementations can define how
 * the custom name is derived.
 */
public abstract class ConditionalName {

    /**
     * Generates a custom name based on the context or details of the specified player.
     *
     * @param player the player whose context or details are used to generate the custom name
     * @return a string representation of the custom name specific to the provided player
     */
    public abstract String getCustomName(CPlayer player);
}
