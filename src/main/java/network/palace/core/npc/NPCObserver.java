package network.palace.core.npc;

import network.palace.core.player.CPlayer;

/**
 * Represents an observer interface for handling interactions between a player and an NPC entity.
 * Classes implementing this interface can define custom behavior
 * when a player interacts with an NPC entity using a specific action.
 */
public interface NPCObserver {
    /**
     * Handles interactions between a player and an entity when a specific click action is performed.
     *
     * @param player the player who performed the interaction
     * @param entity the entity that was interacted with
     * @param action the click action performed by the player
     */
    void onPlayerInteract(CPlayer player, AbstractEntity entity, ClickAction action);
}
