package network.palace.core.player.impl.listeners;

import network.palace.core.Core;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;

/**
 * The {@code CorePlayerManagerListener} class is a listener that handles various
 * player-related events on the server. It ensures proper management of player
 * connections, permissions, and interactions, and integrates with the core
 * server functionality.
 *
 * <p>This listener performs the following key actions:
 * <ul>
 *   <li>Validates and manages player login and logout events.</li>
 *   <li>Handles cases where the server is starting or where players are not authorized
 *       to join the network.</li>
 *   <li>Manages in-game actions, such as picking up special items.</li>
 *   <li>Synchronizes player data and permissions with the server's central systems.</li>
 * </ul>
 */
public class CorePlayerManagerListener implements Listener {

    /**
     * Handles the {@link AsyncPlayerPreLoginEvent} to manage player login attempts.
     * This method ensures that players cannot log in during specific server states
     * or if their account is not authorized.
     * <p>
     * Key behaviors include:
     * <ul>
     *   <li>Rejecting login attempts if the server is still starting or the MongoDB handler is unavailable.</li>
     *   <li>Restricting login for unauthorized accounts.</li>
     *   <li>Handling player login processes while catching potential errors.</li>
     * </ul>
     *
     * @param event the {@link AsyncPlayerPreLoginEvent} instance containing information
     *              about the player's login attempt, including their unique ID, username,
     *              and login result.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (Core.isStarting()) {
            event.setKickMessage(ChatColor.AQUA + "This server is still starting up. Try again in a few seconds!");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }
        if (Core.getMongoHandler() == null) {
            event.setKickMessage(ChatColor.AQUA + "This server is still starting up. Try again in a few seconds!");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            return;
        }
        if (!Core.getMongoHandler().isPlayerOnline(event.getUniqueId())) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "Your account is not authorized on our network!");
            return;
        }
        if (event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            try {
                Core.getPlayerManager().playerLoggedIn(event.getUniqueId(), event.getName());
            } catch (Exception e) {
                event.setKickMessage(ChatColor.RED + "An error occurred while connecting you to this server. Please try again soon!");
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            }
        }
    }

    /**
     * Monitors player login attempts and removes their data from the player manager
     * if their login attempt is denied.
     *
     * <p>This method is executed at the {@link EventPriority#MONITOR} phase and
     * will not act if the event is cancelled. If the player's login result is not
     * {@code ALLOWED}, the player's data is removed from the manager.</p>
     *
     * @param event the {@link AsyncPlayerPreLoginEvent} containing details about
     *              the player's login attempt, including their unique identifier
     *              and login result status.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLoginMonitor(AsyncPlayerPreLoginEvent event) {
        if (!event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            Core.getPlayerManager().removePlayer(event.getUniqueId());
        }
    }

    /**
     * Handles the {@link PlayerLoginEvent} to determine if a player can log in or should be kicked.
     * This method performs two main checks:
     * <ul>
     *   <li>If the server is still starting, the player is kicked and shown a corresponding message.</li>
     *   <li>If the login operation is not allowed (result is not {@code ALLOWED}), the player's data is
     *   removed from the {@link Core}'s player manager.</li>
     * </ul>
     *
     * @param event The {@link PlayerLoginEvent} triggered when a player attempts to log in.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (Core.isStarting()) {
            event.setKickMessage(ChatColor.AQUA + "This server is still starting up. Try again in a few seconds!");
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
        if (!event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) {
            Core.getPlayerManager().removePlayer(event.getPlayer().getUniqueId());
        }
    }

    /**
     * Handles the event triggered when a player joins the server.
     * <p>
     * This method performs the following actions:
     * <ul>
     *     <li>Suppresses the default join message.</li>
     *     <li>Resets the player's experience points and level to zero.</li>
     *     <li>Notifies the player manager that the player has joined.</li>
     * </ul>
     *
     * @param event the {@link PlayerJoinEvent} triggered when a player joins the server.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
        Player player = event.getPlayer();
        player.setExp(0);
        player.setLevel(0);
        Core.getPlayerManager().playerJoined(player);
    }

    /**
     * Handles the event triggered when a player quits the server.
     *
     * <p>This method performs the following actions:</p>
     * <ul>
     *   <li>Notifies the {@code PlayerManager} in the {@code Core} to handle the player logout process.</li>
     *   <li>Suppresses the quit message by setting it to an empty string.</li>
     *   <li>Notifies the {@code PermissionManager} in the {@code Core} to process the player's logout.</li>
     * </ul>
     *
     * @param event the {@link PlayerQuitEvent} triggered when a player quit action occurs.
     *              Contains information about the player and their quit context.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Core.getPlayerManager().playerLoggedOut(event.getPlayer());
        event.setQuitMessage("");
        Core.getPermissionManager().logout(event.getPlayer().getUniqueId());
    }

    /**
     * Handles the player kick event, ensuring proper actions are executed when a player is removed from the server.
     *
     * <p>This method performs the following tasks:</p>
     * <ul>
     *   <li>Logs the player out from the player management system.</li>
     *   <li>Clears the leave message to suppress default messages in the server chat.</li>
     *   <li>Logs out the player's permissions using the permission manager.</li>
     * </ul>
     *
     * @param event The {@link PlayerKickEvent} containing details about the kicked player and the kick event.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        Core.getPlayerManager().playerLoggedOut(event.getPlayer());
        event.setLeaveMessage("");
        Core.getPermissionManager().logout(event.getPlayer().getUniqueId());
    }

    /**
     * Handles the event when an entity picks up an item. If the item has a "special" metadata
     * tag with a value of {@code true}, the pickup action will be canceled.
     *
     * <p>This method listens to {@link EntityPickupItemEvent} and determines the behavior based on
     * the metadata of the item involved in the event.</p>
     *
     * @param event the {@link EntityPickupItemEvent} that is triggered when an entity attempts
     *              to pick up an item in the world.
     *              <ul>
     *              <li>{@code event.getItem()} is used to access the item being picked up.</li>
     *              <li>If the item has a metadata key "special" with value {@code true},
     *              the event is cancelled to prevent the item from being picked up.</li>
     *              </ul>
     */
    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getItem().hasMetadata("special")) {
            if (event.getItem().getMetadata("special").get(0).asBoolean()) {
                event.setCancelled(true);
            }
        }
    }
}
