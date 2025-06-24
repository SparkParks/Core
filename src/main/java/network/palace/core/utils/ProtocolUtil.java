package network.palace.core.utils;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Utility class for determining the network protocol version used by a player.
 * This class integrates with the ViaVersion and ProtocolLib plugins to fetch
 * the protocol version based on the player's connection.
 *
 * The method in this class ensures compatibility with servers that use
 * either ViaVersion or ProtocolLib for protocol handling and players' version
 * information.
 */
public class ProtocolUtil {

    /**
     * Retrieves the protocol version for the specified player.
     * The method determines the protocol version using ViaVersion if available,
     * otherwise falls back to ProtocolLib.
     *
     * @param player the player whose protocol version is to be retrieved
     * @return the protocol version of the specified player as an integer
     */
    public static int getProtocolVersion(Player player) {
        if (Bukkit.getPluginManager().getPlugin("ViaVersion") != null) {
            us.myles.ViaVersion.api.ViaAPI api = us.myles.ViaVersion.api.Via.getAPI();
            return api.getPlayerVersion(player);
        } else {
            return ProtocolLibrary.getProtocolManager().getProtocolVersion(player);
        }
    }
}
