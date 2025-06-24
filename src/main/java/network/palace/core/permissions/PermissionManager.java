package network.palace.core.permissions;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionDefault;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The PermissionManager class is responsible for managing permissions for different ranks
 * and players in the application. It allows initialization, assignment, and modification
 * of permissions, as well as handling permission attachments for specific players.
 */
public class PermissionManager {

    /**
     * A mapping of ranks to their respective permissions. Each rank is associated with a
     * map of permission nodes and their corresponding boolean values, indicating whether
     * the permission is granted (true) or denied (false).
     *
     * This structure operates as the core of permission management, enabling retrieval,
     * modification, and initialization of rank-based permissions across the system.
     */
    private Map<Rank, Map<String, Boolean>> permissions = new HashMap<>();

    /**
     * A map that associates unique player identifiers (UUIDs) with their respective
     * permission attachments. This is used to manage and store permissions assigned
     * to individual players in the system.
     *
     * The key represents the UUID of a player, and the value is the corresponding
     * PermissionAttachment for that player, which allows for the dynamic management
     * of permissions during runtime.
     */
    public Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    /**
     * Holds the PermissionAttachment object for the console, allowing permissions to
     * be dynamically managed for the ConsoleCommandSender.
     *
     * This variable is used in conjunction with the initialization process in
     * the {@code initialize()} method, where it is assigned an attachment associated
     * with the console sender. The permissions granted to the console are configured
     * through modifications to this attachment.
     *
     * By maintaining this object, the PermissionManager class ensures that console
     * permissions can be explicitly defined and cleared as needed.
     */
    private PermissionAttachment consoleAttachment = null;

    /**
     * Constructs a new instance of the PermissionManager class and initializes
     * the internal permissions structure. This method sets up default
     * permissions, configures console attachments, and organizes the rank
     * hierarchy to prepare the permission system for usage.
     */
    public PermissionManager() {
        initialize();
    }

    /**
     * Initializes the permissions system by preparing permissions for all ranks,
     * attaching permissions to the console, and configuring permission inheritance.
     * This method ensures that both console and player permissions are properly set up
     * and synchronized based on the defined rank structure.
     *
     * Responsibilities include:
     * - Clearing and refreshing the current permissions map.
     * - Setting up rank-specific permissions and inheritance.
     * - Configuring console sender permissions.
     * - Updating online players' permissions to align with their ranks.
     *
     * The initialization process loops through all defined ranks in reverse order
     * to handle inheritance of permissions correctly, from higher to lower ranks.
     */
    private void initialize() {
        permissions.clear();
        Rank[] ranks = Rank.values();

        ConsoleCommandSender console = Bukkit.getConsoleSender();
        if (consoleAttachment != null) console.removeAttachment(consoleAttachment);
        consoleAttachment = console.addAttachment(Core.getInstance());
        for (Rank rank : ranks) {
            consoleAttachment.setPermission("palace.core.rank." + rank.getDBName(), true);
        }

        Collection<CPlayer> players = Core.getPlayerManager().getOnlinePlayers();
        Rank previous = null;
        for (int i = ranks.length - 1; i >= 0; i--) {
            Rank r = ranks[i];
            Permission permission = Bukkit.getPluginManager().getPermission("palace.core.rank." + r.getDBName());
            if (permission == null) {
                permission = new Permission("palace.core.rank." + r.getDBName(), PermissionDefault.OP);
                Bukkit.getPluginManager().addPermission(permission);
            }
            Map<String, Boolean> perms = Core.getMongoHandler().getPermissions(r);
            if (previous != null) {
                for (Map.Entry<String, Boolean> perm : getPermissions(previous).entrySet()) {
                    if (perms.containsKey(perm.getKey())) {
                        if (!perms.get(perm.getKey()).equals(perm.getValue())) {
                            continue;
                        }
                    }
                    perms.put(perm.getKey(), perm.getValue());
                }
            }
            permissions.put(r, perms);
            if (!players.isEmpty()) {
                for (CPlayer p : players) {
                    if (p.getRank().equals(r)) {
                        setPermissions(p.getBukkitPlayer(), perms);
                    }
                }
            }
            previous = r;
        }
    }

    /**
     * Logs in a player by assigning appropriate permissions based on their rank.
     * This method retrieves the player's rank, fetches the corresponding
     * permissions, and applies them to the player's account.
     *
     * @param player the CPlayer object representing the player to be logged in.
     *               It contains information about the player's rank and related data.
     */
    public void login(CPlayer player) {
        setPermissions(player.getBukkitPlayer(), getPermissions(player.getRank()));
    }

    /**
     * Logs out a player by removing their associated attachments from the system.
     * This method cleans up any permissions or data linked to the specified player.
     *
     * @param uuid the unique identifier of the player to be logged out
     */
    public void logout(UUID uuid) {
        attachments.remove(uuid);
    }

    /**
     * Assigns or updates a player's permissions based on the provided map of permission nodes.
     * This method ensures that the player's existing permissions are cleared before applying
     * the new set of permissions. If the player does not already have an attachment, a new one
     * is created and stored for future reference.
     *
     * @param player the player whose permissions need to be set or updated
     * @param perms  a map containing permission nodes as keys and their granted status
     *               (true or false) as values
     */
    private void setPermissions(Player player, Map<String, Boolean> perms) {
        PermissionAttachment attachment;
        if (attachments.containsKey(player.getUniqueId())) {
            attachment = attachments.get(player.getUniqueId());
        } else {
            attachment = player.addAttachment(Core.getInstance());
            attachments.put(player.getUniqueId(), attachment);
        }
        for (Map.Entry<String, Boolean> entry : attachment.getPermissions().entrySet()) {
            attachment.unsetPermission(entry.getKey());
        }
        for (Map.Entry<String, Boolean> entry : perms.entrySet()) {
            attachment.setPermission(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Retrieves the permissions mapped to the given rank.
     * This method fetches the permissions associated with the specified rank
     * and returns them in a map format, where keys represent the permission nodes
     * and values indicate whether the permission is granted (true) or denied (false).
     * If no permissions are found for the rank, an empty map is returned.
     *
     * @param rank the rank for which the permissions should be retrieved
     * @return a map containing permission nodes as keys and their corresponding
     *         granted statuses as values; returns an empty map if no permissions
     *         exist for the provided rank
     */
    public Map<String, Boolean> getPermissions(Rank rank) {
        Map<String, Boolean> map = permissions.get(rank);
        return map == null ? new HashMap<>() : map;
    }

    /**
     * Refreshes the state of the permissions system.
     *
     * This method reinitializes the entire permissions infrastructure
     * by invoking the {@code initialize} method. It ensures that all
     * ranks, permissions, and inheritance configurations are reset
     * and updated. This operation can be used to apply changes
     * to permissions dynamically or to restore the system to a clean state.
     */
    public void refresh() {
        initialize();
    }

    /**
     * Sets a specific permission node with a given value for a specified rank.
     * This method updates the current permissions of the specified rank by
     * adding or modifying the given permission node and its value.
     *
     * @param rank  the rank for which the permission is being set
     * @param node  the permission node to be added or updated
     * @param value the value of the permission node; true to grant the
     *              permission, false to revoke it
     */
    public void setPermission(Rank rank, String node, boolean value) {
        Map<String, Boolean> currentPermissions = new HashMap<>(permissions.get(rank));
        currentPermissions.put(node, value);
        permissions.put(rank, currentPermissions);
    }

    /**
     * Removes a specific permission node from the permissions associated with a given rank.
     * This method updates the permission map of the specified rank by eliminating the specified node.
     *
     * @param rank the rank from which the permission node should be removed
     * @param node the permission node to be removed
     */
    public void unsetPermission(Rank rank, String node) {
        Map<String, Boolean> currentPermissions = new HashMap<>(this.permissions.get(rank));
        currentPermissions.remove(node);
        permissions.put(rank, currentPermissions);
    }
}
