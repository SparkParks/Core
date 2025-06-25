package network.palace.core.player.impl.managers;

import lombok.AllArgsConstructor;
import network.palace.core.packets.server.resourcepack.WrapperPlayServerResourcePackSend;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerResourcePackManager;

/**
 * The {@code CorePlayerResourcePackManager} class provides an implementation of the
 * {@link CPlayerResourcePackManager} interface, allowing for the management and delivery
 * of resource packs to a Minecraft player.
 *
 * <p>
 * This class handles the creation and sending of packets related to resource packs. It
 * permits the submission of a resource pack URL, along with an optional hash string to
 * validate the resource pack's integrity. This ensures that the player receives and applies
 * the desired resources during gameplay.
 * </p>
 *
 * <p>
 * The {@link CorePlayerResourcePackManager} relies on a {@link CPlayer}, which acts as
 * the recipient of the resource pack packets. The provided methods internally configure
 * the packet and communicate it to the player.
 * </p>
 *
 * <ul>
 *   <li><b>send(String url)</b>: Sends a resource pack to the player using the specified URL.</li>
 *   <li><b>send(String url, String hash)</b>: Sends a resource pack to the player using a URL and an optional integrity hash.</li>
 * </ul>
 */
@AllArgsConstructor
public class CorePlayerResourcePackManager implements CPlayerResourcePackManager {

    /**
     * Represents the {@link CPlayer} instance associated with this resource pack manager.
     *
     * <p>
     * This variable is used to identify and interact with the player in the context
     * of sending resource pack packets. It serves as the primary recipient for the
     * resource pack delivery, allowing the {@link CorePlayerResourcePackManager} to
     * communicate directly with the associated player.
     * </p>
     *
     * <p>
     * The {@code player} variable is immutable and must be provided during the initialization
     * of the {@link CorePlayerResourcePackManager} class.
     * </p>
     */
    private final CPlayer player;

    /**
     * Sends a resource pack to the player using the provided URL, with a default hash value.
     * This method is a convenience overload that invokes {@link #send(String, String)} with
     * the hash value set to "null".
     *
     * <p>
     * The method ensures the delivery of a resource pack to the player by creating and sending
     * an appropriate network packet containing the resource pack URL.
     * </p>
     *
     * @param url The URL of the resource pack to be sent. This URL must point to a valid
     *            resource pack location that the client can access and download.
     */
    @Override
    public void send(String url) {
        send(url, "null");
    }

    /**
     * Sends a resource pack packet to the associated player.
     *
     * <p>This method configures and transmits a resource pack packet to the client,
     * containing the specified URL and hash. The provided URL specifies the location
     * of the resource pack, while the hash is used to verify the integrity of the resource pack.
     * If the hash is an empty string or consists only of whitespace, it defaults to "null".</p>
     *
     * @param url  the URL of the resource pack to be downloaded. Cannot be null.
     *             <ul>
     *               <li>This URL must point to a valid resource pack file hosted on a public server.</li>
     *               <li>Ensure the URL is accessible and correctly formatted for proper delivery.</li>
     *             </ul>
     * @param hash the SHA-1 hash of the resource pack, used to verify its integrity.
     *             <ul>
     *               <li>If the hash is provided as an empty string, it will default to the string "null".</li>
     *               <li>To optimize bandwidth and ensure integrity verification, use a valid 40-character
     *                   hexadecimal string in lower case.</li>
     *             </ul>
     */
    @Override
    public void send(String url, String hash) {
        WrapperPlayServerResourcePackSend packet = new WrapperPlayServerResourcePackSend();
        packet.setUrl(url);
        packet.setHash(hash.trim().equals("") ? "null" : hash);
        player.sendPacket(packet);
    }
}
