package network.palace.core.player.impl.listeners;

import network.palace.core.Core;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

/**
 * The {@code CorePlayerStaffLoginListener} class serves as an event listener that listens to
 * various {@link PlayerEvent} or related events in order to manage
 * the behavior of players who are marked as "disabled" in the system.
 * <p>
 * This class enforces restrictions on certain actions performed by players whose {@code UUID}
 * is included in the {@code disabledPlayers} collection managed by the {@code Core} instance.
 * If a player is identified as disabled, the associated events can either be cancelled
 * or have their consequences mitigated.
 * <p>
 * The event handling logic uses low priority to ensure it intervenes before other handlers
 * with higher priorities.
 *
 * <h2>Event Handlers</h2>
 * <p>
 * Each event listed below is monitored to restrict the interaction of disabled players:
 * <ul>
 *   <li>{@link PlayerQuitEvent}: Triggered when a player leaves the server.</li>
 *   <li>{@link PlayerKickEvent}: Triggered when a player is forcibly removed from the server.</li>
 *   <li>{@link PlayerBedEnterEvent}: Triggered when a player enters a bed.</li>
 *   <li>{@link PlayerBucketEmptyEvent}: Triggered when a player empties a bucket.</li>
 *   <li>{@link PlayerBucketFillEvent}: Triggered when a player fills a bucket.</li>
 *   <li>{@link PlayerDropItemEvent}: Triggered when a player drops an item.</li>
 *   <li>{@link PlayerEditBookEvent}: Triggered when a player edits a book.</li>
 *   <li>{@link PlayerFishEvent}: Triggered when a player attempts to fish.</li>
 *   <li>{@link PlayerInteractEntityEvent}: Triggered when a player interacts with an entity.</li>
 *   <li>{@link PlayerInteractEvent}: Triggered when a player interacts with an object or block.</li>
 *   <li>{@link PlayerItemConsumeEvent}: Triggered when a player consumes an item.</li>
 *   <li>{@link PlayerItemDamageEvent}: Triggered when an item held by a player takes damage.</li>
 *   <li>{@link PlayerItemHeldEvent}: Triggered when a player changes the held item.</li>
 *   <li>{@link PlayerMoveEvent}: Triggered when a player changes their location.</li>
 *   <li>{@link PlayerPickupArrowEvent}: Triggered when a player picks up an arrow.</li>
 *   <li>{@link PlayerPickupItemEvent}: Triggered when a player picks up an item.</li>
 *   <li>{@link PlayerPortalEvent}: Triggered when a player enters a portal.</li>
 *   <li>{@link PlayerShearEntityEvent}: Triggered when a player shears an entity.</li>
 *   <li>{@link PlayerUnleashEntityEvent}: Triggered when a player unleashes an entity.</li>
 *   <li>{@link PlayerVelocityEvent}: Triggered when a player's velocity changes due to external impacts.</li>
 *   <li>{@link InventoryClickEvent}: Triggered when a player interacts with an inventory.</li>
 *   <li>{@link InventoryOpenEvent}: Triggered when a player opens an inventory.</li>
 *   <li>{@link InventoryCreativeEvent}: Triggered when a player opens their creative inventory.</li>
 * </ul>
 *
 * <h2>Methods</h2>
 * <ul>
 *   <li>{@code removePlayer(Player player)}: Removes a player's UUID from the disabled players list, thereby
 *       lifting restrictions for the respective player.</li>
 *   <li>{@code onPlayerEvent(Player player, Cancellable cancellable)}: Checks if the player is disabled
 *       and cancels the event, if applicable.</li>
 * </ul>
 *
 * <h3>Behavior</h3>
 * <p>
 * Disabled players are restricted from performing the above actions. The applicable event is cancelled
 * if their UUID is included in the {@code disabledPlayers} list. When a player leaves the server, they
 * are automatically removed from this list to ensure proper cleanup.
 *
 * <h3>Prioritization</h3>
 * <p>
 * The priority for all event handling is set to {@link EventPriority#LOWEST}, ensuring
 * the logic executes before other handlers to assert desired restrictions early.
 */
public class CorePlayerStaffLoginListener implements Listener {

    /**
     * Handles the event triggered when a player quits the server.
     * <p>
     * This method ensures that necessary cleanup actions are performed
     * for the player that is leaving the server.
     * </p>
     *
     * @param event The {@link PlayerQuitEvent} instance containing information
     *              about the player who has quit the server.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    /**
     * Handles the {@link PlayerKickEvent} by removing the kicked player from the disabled players list.
     * <p>
     * This method is triggered when a player is kicked from the server, ensuring their data is properly
     * managed by invoking the {@code removePlayer(Player player)} method to clean up any related state.
     * </p>
     *
     * @param event The {@link PlayerKickEvent} that contains details about the player kick, such as the
     *              player being kicked and the reason for the kick.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {
        removePlayer(event.getPlayer());
    }

    /**
     * Removes the specified player from the list of disabled players if present.
     * <p>
     * This method checks if the player's unique identifier is in the disabled players list.
     * If the identifier is found, it is removed from the list.
     *
     * @param player The {@code Player} object representing the player to be removed
     *               from the disabled players list.
     */
    private void removePlayer(Player player) {
        if (Core.getInstance().getDisabledPlayers().contains(player.getUniqueId())) {
            Core.getInstance().getDisabledPlayers().remove(player.getUniqueId());
        }
    }

    /**
     * Handles the event triggered when a player attempts to enter a bed.
     * <p>
     * This method is called when a player interacts with a bed to sleep. If the player
     * is in the disabled players list, the action will be cancelled to prevent them
     * from entering the bed.
     * </p>
     *
     * @param event The {@link PlayerBedEnterEvent} instance containing details about the bed
     *              interaction, such as the player attempting to enter the bed and the bed's location.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the {@link PlayerBucketEmptyEvent} when a player uses a bucket to empty its contents.
     * <p>
     * This method is triggered when a player attempts to empty the contents of a bucket into the world.
     * It ensures proper handling of the event, potentially canceling it if the related conditions
     * specified in {@code onPlayerEvent(Player player, Cancellable cancellable)} are met.
     * </p>
     *
     * @param event The {@link PlayerBucketEmptyEvent} instance containing information about the player
     *              and the bucket action being performed in the world.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the {@link PlayerBucketFillEvent} when a player attempts to fill a bucket.
     * <p>
     * This method is triggered during the lowest event priority, ensuring it processes
     * the event before higher-priority event handlers. It invokes the {@code onPlayerEvent(Player, Cancellable)}
     * method to check if the player's actions should be canceled based on specific conditions.
     * </p>
     *
     * @param event The {@link PlayerBucketFillEvent} that contains details about the player and
     *              the action of filling a bucket, such as the bucket being filled and the block interacted with.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the {@link PlayerDropItemEvent} triggered when a player drops an item.
     * <p>
     * This method ensures that actions are properly handled or restricted based on the player's state.
     * It invokes {@link #onPlayerEvent(Player, Cancellable)} to check if the player is in the
     * disabled players list and cancels the event if necessary.
     * </p>
     *
     * @param event The {@link PlayerDropItemEvent} instance containing details about the item
     *              being dropped and the player initiating the action.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the {@link PlayerEditBookEvent} that is triggered when a player edits a book in their inventory.
     * <p>
     * This method ensures that specific event-related procedures are executed when a player modifies a book.
     * By invoking {@link #onPlayerEvent(Player, Cancellable)}, it checks if the player is in a disabled
     * state (e.g., due to server-specific restrictions) and cancels the editing action if necessary.
     * </p>
     *
     * @param event The {@link PlayerEditBookEvent} instance containing details about the player and the edited book.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the {@link PlayerFishEvent} triggered when a player attempts to fish.
     * <p>
     * This method processes the fishing event and invokes {@link #onPlayerEvent(Player, Cancellable)}
     * to determine if the player's action should be canceled based on specific conditions.
     * It operates with the lowest priority to handle the event before any higher-priority
     * event handlers.
     * </p>
     *
     * @param event The {@link PlayerFishEvent} instance containing details about the player,
     *              the fishing action, and the potential catch (if applicable).
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerFish(PlayerFishEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the {@link PlayerInteractEntityEvent} triggered when a player interacts with an entity.
     * <p>
     * This method is invoked at the lowest event priority and delegates interaction handling
     * to {@link #onPlayerEvent(Player, Cancellable)}. It ensures that actions are appropriately
     * managed, such as preventing interactions if the player is in a disabled state or
     * under certain conditions specific to the server.
     * </p>
     *
     * @param event The {@link PlayerInteractEntityEvent} containing details about the interaction,
     *              such as the player initiating the action and the entity being interacted with.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the {@link PlayerInteractEvent} triggered when a player interacts with the game world.
     * <p>
     * This method processes the interaction event at the lowest priority, allowing for custom actions
     * or restrictions, such as canceling the event based on certain conditions. This is achieved by
     * invoking {@link #onPlayerEvent(Player, Cancellable)}, which performs checks for player-specific
     * restrictions and cancels the event if necessary.
     * </p>
     *
     * @param event The {@link PlayerInteractEvent} instance containing details about the interaction,
     *              such as the player performing the action, the type of interaction, and the block
     *              or item involved.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the {@link PlayerItemConsumeEvent} that is triggered when a player consumes an item.
     * <p>
     * This method processes the event with the lowest priority to ensure its execution before
     * higher-priority event handlers. It invokes the {@code onPlayerEvent(Player, Cancellable)} method,
     * which checks if the player is in the disabled players list and cancels the event if necessary.
     * </p>
     *
     * @param event The {@link PlayerItemConsumeEvent} instance containing information about the item
     *              being consumed and the player performing the action.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the {@link PlayerItemDamageEvent} triggered when a player's item takes damage.
     * <p>
     * This method is invoked at the lowest event priority to handle item damage events,
     * such as when an item in the player's inventory experiences durability loss.
     * It utilizes {@link #onPlayerEvent(Player, Cancellable)} to determine if the
     * player's action should be canceled based on specific conditions (e.g., if the player
     * is restricted by the server's rules).
     * </p>
     *
     * @param event The {@link PlayerItemDamageEvent} instance containing information about
     *              the player whose item is being damaged, the affected item, and the
     *              amount of durability lost.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the event that is triggered when a player changes the item they are holding.
     * <p>
     * This method is executed with the {@link EventPriority#LOWEST} to ensure it runs before
     * any other event handlers of higher priority.
     * </p>
     * <p>
     * The event provides details about the player and the item change action that occurred.
     * </p>
     *
     * @param event The {@link PlayerItemHeldEvent} that contains information about the player item switch action.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the {@link PlayerMoveEvent} that is triggered whenever a player moves.
     * <p>
     * This method processes player movement at the lowest priority and delegates
     * further handling to the {@code onPlayerEvent} method.
     *
     * @param event The {@link PlayerMoveEvent} triggered when a player moves.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the event when a player picks up an arrow in the game.
     * This method reacts to the {@link PlayerPickupArrowEvent} and processes the event at
     * the lowest priority, allowing other listeners with higher priority to handle it first.
     *
     * @param event The {@link PlayerPickupArrowEvent} triggered when a player collects an arrow.
     *              <p>
     *              This event provides access to the player who picked up the arrow, the
     *              arrow entity being picked up, and allows potential modification or cancellation
     *              of the event.
     *              </p>
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPickupArrow(PlayerPickupArrowEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the event triggered when a player attempts to pick up an item.
     * <p>
     * This method intercepts the {@link PlayerPickupItemEvent} at the lowest priority
     * level to perform additional processing before normal event handling occurs.
     * It delegates further handling of the event to the {@code onPlayerEvent} method.
     *
     * <p>Usage scenarios for this method may include:
     * <ul>
     *     <li>Custom logging of item pickup events</li>
     *     <li>Implementing restrictions or conditions under which items can be picked up</li>
     *     <li>Triggering additional gameplay mechanics based on item pickups</li>
     * </ul>
     *
     * @param event The {@link PlayerPickupItemEvent} triggered when a player tries to pick up an item.
     *              This event provides access to the player attempting the action
     *              and the item entity involved in the pickup.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
//        if (event.getEntity() instanceof Player) {
        onPlayerEvent(event.getPlayer(), event);
//        }
    }

    /**
     * Handles the {@link PlayerPortalEvent}, enabling custom behavior when a player uses a portal.
     *
     * <p>This method is triggered at the lowest priority when a player interacts with a portal. It processes
     * the event and delegates the functionality to {@code onPlayerEvent} for further handling.
     *
     * @param event The {@link PlayerPortalEvent} instance containing details of the portal interaction,
     *              such as the player involved and the resulting teleportation behavior.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPortal(PlayerPortalEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the event triggered when a player shears an entity.
     * <p>
     * This method is executed with the lowest event priority, ensuring
     * it processes the event early before other handlers with a higher
     * priority.
     *
     * @param event the {@link PlayerShearEntityEvent} containing details about
     *              the player and the sheared entity.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the event triggered when a player unleashes an entity.
     * <p>
     * This method is called with the lowest priority to ensure it processes
     * before other handlers that may listen for the same event.
     *
     * @param event The {@link PlayerUnleashEntityEvent} instance containing
     *              details about the player and the entity being unleashed.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerUnleashEntity(PlayerUnleashEntityEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the {@link PlayerVelocityEvent}, invoked when a player's velocity is being changed.
     * This method processes the event at the {@link EventPriority#LOWEST}.
     * <p>
     * This can be used to monitor or modify the player's velocity before other event listeners process it.
     * </p>
     *
     * @param event the {@link PlayerVelocityEvent} triggered when a player's velocity is altered.
     *              <ul>
     *                  <li>{@link PlayerVelocityEvent#getPlayer()} - Retrieves the player whose velocity is being changed.</li>
     *                  <li>{@link PlayerVelocityEvent#getVelocity()} - Gets the new velocity being applied to the player.</li>
     *              </ul>
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        onPlayerEvent(event.getPlayer(), event);
    }

    /**
     * Handles the {@link InventoryClickEvent} triggered when a player interacts with an inventory.
     * This method delegates the event handling to {@code onPlayerEvent}.
     *
     * <p>This event is called with the lowest priority, allowing other handlers to process
     * it subsequently unless canceled. It's essential to ensure proper event flow and not
     * disrupt other plugins unless absolutely necessary.</p>
     *
     * @param event the {@link InventoryClickEvent} that contains details about the inventory interaction,
     *              such as the player who clicked, the inventory involved, and the clicked item.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        onPlayerEvent((Player) event.getWhoClicked(), event);
    }

    /**
     * Handles the inventory open event for a player. This method is triggered when a player opens an inventory
     * in the game. It processes the event at the lowest priority level to ensure that it executes before
     * other event handlers for the same event.
     *
     * <p>The method calls the {@code onPlayerEvent} function to handle additional logic for the player
     * related to the inventory open event.</p>
     *
     * @param event The {@link InventoryOpenEvent} that contains information about the inventory opening action,
     *              including the player who triggered the event and the inventory involved.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        onPlayerEvent((Player) event.getPlayer(), event);
    }

    /**
     * Handles the InventoryCreativeEvent that is triggered when a player in creative mode interacts
     * with their inventory. This method processes the event and passes it along for further handling.
     *
     * <p>This method ensures that actions performed in creative inventory are appropriately managed
     * and invokes the necessary logic to handle the player-based event.</p>
     *
     * @param event the <code>InventoryCreativeEvent</code> representing the player's interaction
     *              with their inventory while in creative mode.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryCreative(InventoryCreativeEvent event) {
        onPlayerEvent((Player) event.getWhoClicked(), event);
    }

    /**
     * Handles the event triggered by a player action and checks if the player's unique identifier
     * is present in the list of disabled players. If the player is disabled, the event will be cancelled.
     *
     * <p>
     * This method ensures that disabled players cannot perform specific actions by interacting
     * with the system through event cancellation.
     * </p>
     *
     * @param player The player object associated with the current event.
     * @param cancellable The event's cancellable state object to manage event cancellation logic.
     */
    private void onPlayerEvent(Player player, Cancellable cancellable) {
        if (Core.getInstance().getDisabledPlayers().contains(player.getUniqueId())) {
            cancellable.setCancelled(true);
        }
    }
}
