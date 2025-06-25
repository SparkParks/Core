package network.palace.core.npc.mob;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import lombok.AllArgsConstructor;

/**
 * Represents a texture associated with a mob player.
 *
 * The MobPlayerTexture class allows for storing and managing texture data,
 * including its value and signature. The stored texture information is used to
 * define and sign properties related to the appearance of player-like mobs
 * in the game.
 *
 * This class includes functionality to attach the texture data as a signed
 * property to a given WrappedGameProfile.
 */
@AllArgsConstructor
public class MobPlayerTexture {

    /**
     * Represents the base64-encoded texture data of a mob player.
     *
     * The value contains the textual representation of the texture associated
     * with the appearance of a player-like mob. This data is used in conjunction
     * with the signature to authenticate and define the mob's visual elements.
     */
    private String value;

    /**
     * Represents the base64-encoded signature associated with the texture data.
     *
     * The signature is used to verify the authenticity and validity of the texture
     * information. It is crucial in ensuring that the texture data can be trusted
     * and has not been tampered with. This field works in conjunction with the
     * value to define a signed property for the appearance of player-like mobs.
     */
    private String signature;

    /**
     * Attaches a signed "textures" property to the provided {@code WrappedGameProfile}
     * using the internally stored texture value and signature.
     *
     * This method modifies the input profile by adding a {@code WrappedSignedProperty}
     * to its properties map, where the property name is "textures", and its value and
     * signature correspond to the texture data stored in the current instance.
     *
     * @param profile the {@code WrappedGameProfile} to which the signed texture property
     *                will be added
     * @return the modified {@code WrappedGameProfile} containing the signed "textures" property
     */
    public WrappedGameProfile getWrappedSignedProperty(WrappedGameProfile profile) {
        profile.getProperties().put("textures", new WrappedSignedProperty("textures", value, signature));
        return profile;
    }
}
