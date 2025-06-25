package network.palace.core.packets.adapters;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;

/**
 * The SettingsAdapter class is responsible for handling incoming settings packets from the client.
 * It listens to packets of type PacketType.Play.Client.SETTINGS and processes the client's language/locale setting.
 */
public class SettingsAdapter extends PacketAdapter {

    /**
     * Initializes a new instance of the SettingsAdapter class.
     * This adapter listens for incoming settings packets from the client.
     * Specifically, it handles packets of type PacketType.Play.Client.SETTINGS
     * to process client-specific settings, such as language or locale configurations.
     */
    public SettingsAdapter() {
        super(Core.getInstance(), PacketType.Play.Client.SETTINGS);
    }

    /**
     * Handles the reception of a packet by extracting and updating the client's locale setting.
     * This method is triggered when a settings packet is received from the client.
     *
     * @param event the packet event containing the packet and player information.
     *              This parameter must not be null and must contain a valid player and packet.
     */
    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event == null) return;
        if (event.getPlayer() == null) return;
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) return;
        if (event.getPacket() == null) return;
        if (event.getPacket().getStrings() == null) return;
        if (event.getPacket().getStrings().read(0) == null) return;
        player.setLocale(event.getPacket().getStrings().read(0));
    }
}
