package network.palace.core.utils;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

/**
 * Utility class for creating and manipulating player skulls or heads in the game.
 * Provides methods to generate player heads based on skin textures and custom display names.
 */
public class HeadUtil {

    /**
     * Generates a player head ItemStack based on the texture value associated with the given player.
     *
     * @param player the CPlayer object representing the player whose head texture is to be used
     * @return an ItemStack representing the player's head based on their texture
     */
    public static ItemStack getPlayerHead(CPlayer player) {
        return getPlayerHead(player.getTextureValue());
    }

    /**
     * Generates a player head ItemStack based on the provided texture hash.
     *
     * @param hash the texture hash used to create the player's head
     * @return an ItemStack representing the player's head
     */
    public static ItemStack getPlayerHead(String hash) {
        return getPlayerHead(hash, "Head");
    }

    /**
     * Generates a player head ItemStack based on the provided texture hash and custom display name.
     *
     * @param hash the texture hash used to define the player's head skin
     * @param display the display name to be set for the player's head
     * @return an ItemStack representing the player's head with the specified display name
     */
    public static ItemStack getPlayerHead(String hash, String display) {
        ItemStack head = getHead(hash);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(display + ChatColor.RESET);
        head.setItemMeta(meta);
        return head;
    }

    /**
     * Generates an ItemStack representing a player head with a skin texture based on the provided texture hash.
     * The texture is applied using NBT data, and a unique UUID is assigned to the head.
     *
     * @param hash the texture hash used to define the skin of the player head
     * @return an ItemStack object representing the player head with the specified texture
     */
    private static ItemStack getHead(String hash) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        item = MinecraftReflection.getBukkitItemStack(item);
        NbtCompound nbt = NbtFactory.asCompound(NbtFactory.fromItemTag(item));
        NbtCompound texture = NbtFactory.ofCompound("");
        texture.put("Value", hash);
        NbtList<NbtCompound> textures = NbtFactory.ofList("textures", texture);
        NbtCompound properties = NbtFactory.ofCompound("Properties");
        properties.put(textures);
        NbtCompound skullOwner = NbtFactory.ofCompound("SkullOwner");
        skullOwner.put("Id", UUID.randomUUID().toString());
        skullOwner.put(properties);
        nbt.put(skullOwner);
        return item;
    }
}
