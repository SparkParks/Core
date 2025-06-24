package network.palace.core.resource;

import com.comphenix.protocol.PacketType;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;

import java.util.*;

/**
 * The ResourceManager class manages resource packs within the application.
 * It provides methods to initialize and reload resource packs, send resource packs to players,
 * and handle the player's response to resource pack download attempts.
 */
public class ResourceManager {

    /**
     * A map that holds the available resource packs, accessed by their unique names.
     * Each entry associates a pack name with its corresponding {@link ResourcePack} object.
     * This map serves as the primary storage for all resource packs managed by the {@code ResourceManager}.
     */
    private final Map<String, ResourcePack> packs = new HashMap<>();

    /**
     * A flag indicating whether the current resource operation is the first one.
     * This boolean is set to {@code true} by default and can be used internally
     * to manage initialization logic or track the first-time execution within the
     * resource management process.
     */
    private boolean first = true;

    /**
     * A map responsible for tracking the current resource pack download status for each player.
     * The key represents the unique identifier (UUID) of the player, while the value represents
     * the status or identifier of the resource pack being downloaded.
     *
     * This field is primarily used to manage and monitor ongoing downloads of resource packs,
     * enabling the system to maintain the state of the associated download process for individual players.
     *
     * The `downloading` map is immutable and initialized as an empty HashMap.
     */
    private final Map<UUID, String> downloading = new HashMap<>();

    /**
     * Constructs a new ResourceManager instance.
     *
     * The constructor initializes the resource manager by invoking the `initialize` method,
     * which sets up the necessary configurations and loads resource pack data.
     * This method ensures that the `ResourceManager` is prepared to handle resource packs,
     * manage downloading results, and coordinate interactions with players.
     *
     * This class is responsible for managing resource packs within the system,
     * including loading packs, retrieving specific packs or lists of packs,
     * sending resource packs to players, setting current resource packs for players,
     * and reloading the resource pack configurations.
     */
    public ResourceManager() {
        initialize();
    }

    /**
     * Initializes the resource manager by clearing the current resource packs and loading
     * the latest set of resource packs from the database.
     *
     * This method performs the following actions:
     * 1. Clears the `packs` map.
     * 2. If the dashboard and SQL functionalities are disabled, the method exits early.
     * 3. Retrieves the list of resource packs from the MongoDB handler and populates
     *    the `packs` map with these resources.
     * 4. Adds a packet listener for monitoring resource pack status if this is the first
     *    initialization. This ensures that the listener is only added once.
     *
     * This method is typically invoked during the resource manager's initialization
     * process to ensure that it has the latest configuration and is ready to manage
     * resource packs effectively.
     */
    private void initialize() {
        packs.clear();
        if (Core.isDashboardAndSqlDisabled()) return;
        List<ResourcePack> list = Core.getMongoHandler().getResourcePacks();
        for (ResourcePack pack : list) {
            packs.put(pack.getName(), pack);
        }
        if (first) {
            Core.addPacketListener(new ResourceListener(Core.getInstance(), PacketType.Play.Client.RESOURCE_PACK_STATUS));
            first = false;
        }
    }

    /**
     * Processes the result of a resource pack download attempt for a specific player.
     *
     * This method manages the state and actions based on the outcome of the resource pack download.
     * Depending on the player's status and the associated resource pack, the appropriate actions
     * are taken, such as updating the player's current resource pack or handling declined or failed
     * downloads.
     *
     * @param uuid   the unique identifier of the player whose download result is being processed
     * @param status the status of the resource pack download, indicating the outcome (can be null)
     */
    public void downloadingResult(UUID uuid, PackStatus status) {
        String pack = downloading.remove(uuid);
        if (pack == null) {
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        player.getTitle().show("", "");
        if (status != null) {
            switch (status) {
                case LOADED: {
                    if (pack.equalsIgnoreCase("blank")) {
                        pack = "none";
                    }
                    setCurrentPack(player, pack);
                    break;
                }
                case FAILED:
                case DECLINED: {
                    if (pack.equalsIgnoreCase("blank")) {
                        setCurrentPack(player, "none");
                    }
                    break;
                }
            }
        }
    }

    /**
     * Retrieves a list of all resource packs currently managed by the resource manager.
     *
     * This method returns a collection of {@link ResourcePack} objects that represent
     * the resource packs loaded into the system at the time of invocation. The returned
     * list is a new copy and will not reflect subsequent changes made to the internal
     * storage of resource packs.
     *
     * @return a list of {@link ResourcePack} objects available in the resource manager
     */
    public List<ResourcePack> getPacks() {
        return new ArrayList<>(packs.values());
    }

    /**
     * Retrieves a specific resource pack by its name.
     *
     * This method searches the internal resource pack storage and returns the
     * {@link ResourcePack} object associated with the specified name. If no
     * resource pack with the given name exists, this method returns null.
     *
     * @param name the name of the resource pack to retrieve
     * @return the {@link ResourcePack} object corresponding to the specified name,
     *         or null if no match is found
     */
    public ResourcePack getPack(String name) {
        return packs.get(name);
    }

    /**
     * Sends a resource pack to the specified player.
     *
     * If the player's online time is less than 2000 ticks, the resource pack
     * will be sent after a delay to ensure proper handling. Otherwise, the
     * resource pack is sent immediately without delay.
     *
     * @param player the player to whom the resource pack will be sent
     * @param pack   the resource pack to be sent to the player
     */
    public void sendPack(CPlayer player, ResourcePack pack) {
        if (player.getOnlineTime() < 2000) {
            Core.runTaskLater(Core.getInstance(), () -> sendPackNoDelay(player, pack), 80L);
            return;
        }
        sendPackNoDelay(player, pack);
    }

    /**
     * Sends a resource pack to the specified player instantly without any delay.
     *
     * This method displays a message to the player, providing feedback about the
     * resource pack being sent, shows a title to inform them that the process
     * has started, and schedules the resource pack delivery after a very short delay.
     *
     * @param player the player to whom the resource pack will be sent
     * @param pack   the resource pack to be sent to the player
     */
    private void sendPackNoDelay(CPlayer player, ResourcePack pack) {
        player.sendMessage(ChatColor.GREEN + "Attempting to send you the " + ChatColor.YELLOW + pack.getName() +
                ChatColor.GREEN + " Resource Pack!");
        player.getTitle().show("Sending Resource Pack", "This might take up to 30 seconds...", 0, 120, 0);
        downloading.put(player.getUniqueId(), pack.getName());
        Core.runTaskLater(Core.getInstance(), () -> pack.sendTo(player), 2L);
    }

    /**
     * Sets the current resource pack for the specified player.
     *
     * This method updates the player's current resource pack to the specified pack name.
     * It also stores this information in the online data using the MongoDB handler.
     * If the provided player object is null, the method does nothing.
     *
     * @param player the player whose resource pack is to be updated
     * @param pack   the name of the resource pack to set for the player
     */
    public void setCurrentPack(CPlayer player, String pack) {
        if (player == null) return;
        player.setPack(pack);
        Core.getMongoHandler().setOnlineDataValue(player.getUniqueId(), "resourcePack", pack);
    }

    /**
     * Sends a resource pack to the specified player based on the given resource pack name.
     *
     * This method retrieves the resource pack by its name and attempts to send it to the player.
     * If the resource pack is not found, an error message is sent to the player, prompting
     * them to contact a staff member for assistance. The method ensures proper handling of
     * scenarios where the requested resource pack does not exist.
     *
     * @param player the player to whom the resource pack will be sent
     * @param name   the name of the resource pack to be sent
     */
    public void sendPack(CPlayer player, String name) {
        ResourcePack pack = getPack(name);
        if (pack == null) {
            player.sendMessage(ChatColor.RED + "We tried to send you a Resource Pack, but it was not found!");
            player.sendMessage(ChatColor.RED + "Please contact a Staff Member about this. (Error Code 101)");
            return;
        }
        sendPack(player, pack);
    }

    /**
     * Reloads the resource packs managed by the resource manager.
     *
     * This method clears the existing resource packs and reinitializes the resource
     * manager by loading the latest resource pack configurations. It ensures that
     * the resource manager is updated and prepared to handle resource packs with
     * fresh data.
     *
     * The following actions are performed by this method:
     * 1. Clears the `packs` map, removing all currently loaded resource packs.
     * 2. Invokes the `initialize` method to repopulate the resource pack data and
     *    refresh configurations.
     */
    public void reload() {
        packs.clear();
        initialize();
    }
}
