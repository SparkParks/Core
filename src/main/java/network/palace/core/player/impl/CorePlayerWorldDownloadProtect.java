package network.palace.core.player.impl;

import network.palace.core.Core;
import network.palace.core.messagequeue.packets.KickPlayerPacket;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 * The {@code CorePlayerWorldDownloadProtect} class is responsible for handling
 * plugin messages related to the World Downloader mod, typically aiming to prevent
 * unauthorized world downloads from a Minecraft server.
 *
 * <p>This class listens for plugin messages sent by clients and enforces restrictions
 * or applies penalties based on server policies when unauthorized attempts to use
 * a World Downloader are detected.</p>
 *
 * <h2>Key Responsibilities:</h2>
 * <ul>
 *   <li>Intercepts plugin messages sent over specific channels.</li>
 *   <li>Checks if the player has permission to bypass World Downloader restrictions.</li>
 *   <li>Manages player data and registry to determine their bypass status.</li>
 *   <li>Temporarily bans players attempting to use prohibited features of the
 *       World Downloader mod.</li>
 *   <li>Sends messages to relevant systems to notify staff and execute actions such as
 *       kicking the offending player.</li>
 * </ul>
 *
 * <h2>Process Overview:</h2>
 * <ul>
 *   <li>When a message is received on the {@code WDL|INIT} channel, the system
 *       verifies whether the player is permitted to use the World Downloader mod.</li>
 *   <li>If the player is unauthorized, a temporary ban is issued for a predefined
 *       duration (3 days by default).</li>
 *   <li>The system sends a notification to staff members and kicks the player from
 *       the server with a descriptive ban message.</li>
 *   <li>Player data related to World Downloader permissions is fetched and stored
 *       using the MongoDB player registry database.</li>
 * </ul>
 *
 * <h2>Plugin Message Listener:</h2>
 * <p>The class implements the {@code PluginMessageListener} interface, which
 * enables it to listen for and process plugin messages from the server. Specifically, it
 * targets the {@code WDL|INIT} channel to detect initialization attempts by the
 * World Downloader mod.</p>
 */
public class CorePlayerWorldDownloadProtect implements PluginMessageListener {

    /**
     * Handles the reception of a plugin message on the specified channel.
     * This method processes messages related to the "WDL|INIT" channel and applies
     * specific behavior based on the player's registry and permissions to determine
     * if they are allowed to use a World Downloader. If the player is not authorized, they
     * will be temporarily banned and notified.
     *
     * <p>Key functionality includes:
     * <ul>
     *     <li>Determining if the player has a "wdl_bypass" registry entry to allow
     *         or deny the use of World Downloader.</li>
     *     <li>Fetching and updating the player's registry from a MongoDB document
     *         if necessary.</li>
     *     <li>Banning and notifying players attempting unauthorized use of World Downloader.</li>
     * </ul>
     *
     * @param channel the channel on which the plugin message was sent. Typically "WDL|INIT".
     * @param pl the player who sent or triggered the plugin message.
     * @param data the raw data sent with the plugin message.
     */
    @Override
    public void onPluginMessageReceived(String channel, Player pl, byte[] data) {
        CPlayer player = Core.getPlayerManager().getPlayer(pl);
        if (player == null) return;
        if (channel.equals("WDL|INIT")) {
            if (!player.getRegistry().hasEntry("wdl_bypass")) {
                if (player.getRank().getRankId() >= Rank.TRAINEE.getRankId()) {
                    Document doc = Core.getMongoHandler().getPlayer(player.getUniqueId(), new Document("wdl_bypass", true));
                    if (doc != null && doc.containsKey("wdl_bypass")) {
                        boolean bypass = doc.getBoolean("wdl_bypass");
                        player.getRegistry().addEntry("wdl_bypass", bypass);
                    } else {
                        player.getRegistry().addEntry("wdl_bypass", false);
                    }
                } else {
                    player.getRegistry().addEntry("wdl_bypass", false);
                }
            }

            if ((boolean) player.getRegistry().getEntry("wdl_bypass")) return;

            long expires = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000);
            Core.getMongoHandler().banPlayer(player.getUniqueId(), "Attempting to use a World Downloader",
                    expires, false, "Core-WDLProtect");
            try {
                Core.getMessageHandler().sendMessage(new KickPlayerPacket(player.getUniqueId(),
                        ChatColor.RED + "You are temporarily banned from this server!\n\n" +
                                ChatColor.YELLOW + "Reason: " + ChatColor.WHITE + "Attempting to use a World Downloader" + "\n\n" +
                                ChatColor.YELLOW + "Expires: " + ChatColor.WHITE + "3 Days\n\n" +
                                ChatColor.YELLOW + "Appeal at " + ChatColor.AQUA + "" + ChatColor.UNDERLINE + "https://palnet.us/appeal",
                        false), Core.getMessageHandler().ALL_PROXIES);
                Core.getMessageHandler().sendStaffMessage(ChatColor.GREEN + player.getName() + ChatColor.RED + " was banned by " +
                        ChatColor.GREEN + Core.getInstanceName() + ChatColor.RED + " Reason: " + ChatColor.GREEN + "Attempting to use a World Downloader" +
                        ChatColor.RED + " Expires: " + ChatColor.GREEN + "3 Days");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
