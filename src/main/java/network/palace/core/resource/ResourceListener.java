package network.palace.core.resource;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

/**
 * The ResourceListener class is responsible for handling packet events related to
 * resource pack status updates. It extends the {@link PacketAdapter} class and
 * listens for specific types of packets that indicate the status of resource pack
 * downloads or interactions.
 *
 * This class processes resource pack status updates and triggers appropriate
 * actions based on the status. Actions include notifying the system about the
 * status of the resource pack and notifying the player in case of errors or specific outcomes.
 */
public class ResourceListener extends PacketAdapter {

    /**
     * Constructs a new ResourceListener instance to listen for specified packet types.
     * The listener is registered with the provided plugin and configured to handle
     * events related to the specified packet types.
     *
     * @param plugin the plugin instance that this listener is associated with
     * @param types the array of packet types that this listener will process
     */
    public ResourceListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    /**
     * Handles packet receiving events related to resource pack status updates.
     * Processes the player's resource pack interaction status (e.g., accepted, declined, loaded, failed)
     * and triggers appropriate actions based on the status.
     *
     * @param event the {@link PacketEvent} containing information about the received packet.
     *              This includes the player associated with the event and the packet details.
     */
    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPlayer() == null) return;
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        try {
            PacketContainer packet = event.getPacket();
            EnumWrappers.ResourcePackStatus status = packet.getResourcePackStatus().read(0);
            switch (status) {
                case SUCCESSFULLY_LOADED:
                    new ResourceStatusEvent(PackStatus.LOADED, player).call();
                    Core.getResourceManager().downloadingResult(player.getUniqueId(), PackStatus.LOADED);
                    return;
                case DECLINED:
                    new ResourceStatusEvent(PackStatus.DECLINED, player).call();
                    Core.getResourceManager().downloadingResult(player.getUniqueId(), PackStatus.DECLINED);
                    return;
                case FAILED_DOWNLOAD:
                    new ResourceStatusEvent(PackStatus.FAILED, player).call();
                    Core.getResourceManager().downloadingResult(player.getUniqueId(), PackStatus.FAILED);
                    return;
                case ACCEPTED:
                    new ResourceStatusEvent(PackStatus.ACCEPTED, player).call();
                    return;
                default:
                    Core.getResourceManager().downloadingResult(player.getUniqueId(), null);
                    player.sendMessage(ChatColor.RED + "There seems to be an error, please report this to a Staff Member! (Error Code 100)");
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "There seems to be an error, please report this to a Staff Member! (Error Code 101)");
            e.printStackTrace();
        }
    }
}
