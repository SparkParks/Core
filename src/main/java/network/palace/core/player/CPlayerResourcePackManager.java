package network.palace.core.player;

/**
 * The {@code CPlayerResourcePackManager} interface provides methods to manage and send resource packs
 * to a player in a Minecraft environment.
 * <p>
 * It allows for sending a resource pack via a URL and optionally passing a hash string for validation.
 * This can be used to ensure that players download and apply specific resource packs during gameplay.
 * </p>
 */
public interface CPlayerResourcePackManager {
    /**
     * Sends a resource pack to the player using the specified URL.
     * <p>
     * This method facilitates the transfer of a resource pack file to the player in
     * a Minecraft environment. The URL provided should point to the resource pack file
     * stored in a location accessible by the player.
     * </p>
     *
     * @param url the URL of the resource pack file to be sent. Must be a valid, accessible URL in a string format.
     *            <ul>
     *              <li>Must be properly formatted and accessible by the client.</li>
     *              <li>The file at the URL should match the requirements for a Minecraft resource pack.</li>
     *            </ul>
     */
    void send(String url);

    /**
     * Sends a resource pack to a player using the specified URL and an optional hash for validation.
     * <p>
     * This method facilitates the delivery of a resource pack to ensure players have the required
     * assets during gameplay. The hash can be used to verify the authenticity and integrity of the
     * resource pack.
     * </p>
     *
     * @param url  The URL to the resource pack file. This must be a valid, accessible URL.
     * @param hash An optional hash string to verify the integrity of the resource pack. This can be null
     *             or left empty if no verification is required.
     */
    void send(String url, String hash);
}
