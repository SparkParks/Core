package network.palace.core.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.player.CPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a resource pack with multiple versions. Each version is targeted for a specific
 * protocol and can be sent to players based on their protocol compatibility.
 *
 * A resource pack consists of a name and a list of versions. Each version is associated with
 * a protocol ID, a URL for downloading the resource pack, and a hash for verification.
 * The class allows the creation of versions, and the ability to send the appropriate
 * version of the resource pack to a player based on their protocol ID.
 */
@Getter
@Setter
public class ResourcePack {

    /**
     * The name of the resource pack.
     * This variable holds the identifier or title for the resource pack,
     * used to differentiate it from other packs. It can be appended with
     * protocol-specific details when creating versioned resource packs.
     */
    private String name;

    /**
     * A list of resource pack versions associated with the current resource pack.
     * Each version contains specific details such as the protocol ID, URL, and hash,
     * and is used to deliver the appropriate resource pack version based on the player's protocol.
     *
     * This variable acts as a container for all the versions created for the resource pack.
     * It allows determining and retrieving the right version when sending the resource pack
     * to a player, ensuring compatibility with their protocol ID.
     */
    private List<Version> versions = new ArrayList<>();

    /**
     * Constructs a new ResourcePack with the specified name.
     *
     * @param name the name of the resource pack
     */
    public ResourcePack(String name) {
        this.name = name;
    }

    /**
     * Generates a versioned representation of the resource pack using the specified protocol ID, URL, and hash.
     * The version encapsulates the highest protocol ID supported by the pack, alongside its URL and integrity hash.
     *
     * @param protocolId the highest protocol ID this resource pack supports
     * @param url the URL from which the resource pack can be downloaded
     * @param hash the hash used to verify the integrity of the resource pack
     * @return a Version object representing the versioned details of the resource pack
     */
    public Version generateVersion(int protocolId, String url, String hash) {
        return new Version(protocolId, url, hash);
    }

    /**
     * Sends the appropriate version of the resource pack to the specified player based on their protocol ID.
     *
     * @param player the player object representing the recipient of the resource pack
     */
    protected void sendTo(CPlayer player) {
        int playerProtocolId = player.getProtocolId();

        int[] versionIds = new int[versions.size()];
        for (int i = 0; i < versions.size(); i++) {
            if (versionIds[i] == -1) {
                Version v = versions.get(i);
                player.getResourcePack().send(v.getUrl(), v.getHash());
                return;
            }
            versionIds[i] = versions.get(i).getProtocolId();
        }
        Arrays.sort(versionIds);

        int packId = 0;
        for (int i = 0; i < versions.size(); i++) {
            if (playerProtocolId <= versionIds[i]) {
                packId = versionIds[i];
                break;
            }
        }

        String url = "";
        String hash = "";
        for (Version version : versions) {
            if (version.getProtocolId() != packId) continue;
            url = version.getUrl();
            hash = version.getHash();
        }
        if (url.isEmpty()) return;

        player.getRegistry().addEntry("packDownloadURL", url);
        player.getResourcePack().send(url, hash);
    }

    /**
     * Represents a versioned resource pack supported by a protocol ID, URL, and hash.
     * This class encapsulates the details necessary for identifying and distributing
     * specific versions of a resource pack.
     */
    @Getter
    @AllArgsConstructor
    public class Version {

        /**
         * Represents the highest protocol ID that this resource pack is compatible with.
         * This value is used to determine compatibility between the resource pack
         * and the current game version or network protocol version.
         */
        private int protocolId; //This is the highest protocol id this pack works for

        /**
         * Represents the URL associated with the versioned resource pack.
         * This URL can be used to locate and download the resource pack.
         * It should point to the specific location where the resource pack is hosted.
         */
        private String url;

        /**
         * Represents the hash of the versioned resource pack.
         * The hash is a unique identifier typically used to verify the
         * integrity and validity of the resource pack. It ensures that
         * the resource pack is not corrupted and matches the expected content.
         */
        @Setter private String hash;

        /**
         * Retrieves the name of the resource pack version.
         * If the version is associated with a specific protocol ID (not equal to -1),
         * the method appends an underscore followed by the protocol ID to the resource pack's base name.
         * Otherwise, it simply returns the base name of the resource pack.
         *
         * @return the name of the resource pack version, with the protocol ID appended if applicable
         */
        public String getName() {
            if (protocolId != -1) {
                return ResourcePack.this.name + "_" + protocolId;
            }
            return ResourcePack.this.name;
        }
    }
}