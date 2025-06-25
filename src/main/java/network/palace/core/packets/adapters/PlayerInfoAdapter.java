package network.palace.core.packets.adapters;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;

import java.util.List;

/**
 * PlayerInfoAdapter is a packet adapter used to intercept and process
 * PLAYER_INFO packets sent to players. It extends the PacketAdapter class
 * to customize packet handling behavior for player information.
 *
 * This adapter primarily handles modifying or accessing details in the
 * PLAYER_INFO packet, such as player-specific data. It works in conjunction
 * with the Core plugin's player management system.
 *
 * Constructor:
 * - Initializes the adapter with Core's main plugin instance and the target
 *   packet type (PLAYER_INFO).
 *
 * Methods:
 * - onPacketSending(PacketEvent event):
 *   Intercepts PLAYER_INFO packets being sent to players.
 *   It processes the packet's data to retrieve player information,
 *   such as latency details, and integrates it with Core's player management
 *   system. The method ensures null safety checks before proceeding to read
 *   or modify the packet's data.
 */
public class PlayerInfoAdapter extends PacketAdapter {

    /**
     * Constructs a new instance of PlayerInfoAdapter.
     *
     * This constructor initializes the adapter with a reference to the main
     * instance of the Core plugin and sets the packet type to PLAYER_INFO.
     * The PLAYER_INFO packet is used to manage player-related details in
     * Minecraft, such as player profiles, latency, and status.
     *
     * The PlayerInfoAdapter is designed to intercept PLAYER_INFO packets
     * and facilitate custom handling or modification of their data, enabling
     * integration with the Core plugin's player management system.
     */
    public PlayerInfoAdapter() {
        super(Core.getInstance(), PacketType.Play.Server.PLAYER_INFO);
    }

    /**
     * Intercepts a packet that is being sent to a player.
     *
     * This method inspects the `PLAYER_INFO` packet data being sent during gameplay.
     * It reads and processes player-related information contained in the packet, including
     * details about latency and player profiles. The processed data can then be utilized
     * to update or synchronize information within the server's player management systems.
     *
     * The method performs multiple null checks to ensure robust handling of potentially
     * uninitialized packet data. Player information is read from the packet and processed
     * by interacting with the Core plugin's player manager.
     *
     * @param event The event containing the packet to be intercepted. This parameter provides
     *              access to the packet's data, which can be read or modified as necessary.
     */
    @Override
    public void onPacketSending(PacketEvent event) {
        if (event == null) return;
        if (event.getPacket() == null) return;
        if (event.getPacket().getSpecificModifier(List.class) == null) return;
        if (event.getPacket().getSpecificModifier(List.class).read(0) == null) return;
        List playerInfo = event.getPacket().getSpecificModifier(List.class).read(0);
        for (Object infoDataObj : playerInfo) {
            if (infoDataObj instanceof PlayerInfoData) {
                PlayerInfoData infoData = (PlayerInfoData) infoDataObj;
                CPlayer player = Core.getPlayerManager().getPlayer(infoData.getProfile().getName());
                if (player == null) return;
//                player.setPing(infoData.getLatency());
            }
        }
    }
}
