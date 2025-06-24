package network.palace.core.utils;

import com.comphenix.protocol.reflect.MethodUtils;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.io.NbtTextSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.palace.core.Core;
import network.palace.core.packets.server.entity.WrapperPlayServerCustomPayload;
import network.palace.core.player.CPlayer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for item-related operations in a Bukkit-based Minecraft plugin.
 *
 * This class provides static methods to manage and manipulate items, including
 * creating custom items, managing NBT tags, adding visual effects, and managing
 * inventory-related JSON serialization and deserialization. It also provides event
 * handling for inventory interactions and item drops.
 *
 * Fields:
 * - UNABLE_TO_MOVE: A flag to mark an item as unable to be moved in an inventory.
 * - UNABLE_TO_DROP: A flag to mark an item as unable to be dropped by a player.
 */
@SuppressWarnings({"Duplicates", "deprecation"})
public class ItemUtil implements Listener {

    /**
     * A constant string representing a tag used to mark an item as unable to be moved.
     *
     * This constant can be utilized in methods or logic that manage or enforce
     * restrictions on the transfer or movement of item stacks within inventory
     * or game contexts.
     */
    private static final String UNABLE_TO_MOVE = "unableToMove";

    /**
     * Represents the NBT tag used to indicate that an item is restricted from being dropped.
     *
     * This constant is utilized within methods that modify item stack properties to enforce
     * non-droppable behavior. Its presence in an item's NBT data ensures that the item cannot
     * be dropped by a player under normal circumstances.
     */
    private static final String UNABLE_TO_DROP = "unableToDrop";

    /**
     * Opens a book for the specified player by temporarily replacing the item in the main hand.
     *
     * @param player the player for whom the book will be opened
     * @param book   the book item stack to be opened
     */
    public static void openBook(CPlayer player, ItemStack book) {
        PlayerInventory i = player.getInventory();
        int slot = i.getHeldItemSlot();
        ItemStack save = i.getItemInMainHand();
        i.setItem(slot, book);

        ByteBuf buf = Unpooled.buffer(256);
        buf.setByte(0, (byte) 0);
        buf.writerIndex(1);

        WrapperPlayServerCustomPayload packet = new WrapperPlayServerCustomPayload();
        packet.setChannel("MC|BOpen");
        packet.setContentsBuffer(buf);

        Core.runTaskLater(Core.getInstance(), () -> {
            player.sendPacket(packet);
            i.setItem(slot, save);
        }, 2L);
    }

    /**
     * Modifies the provided ItemStack to make it unable to be moved
     * (e.g., through inventory interactions) by applying a specific NBT tag.
     *
     * @param stack the ItemStack to modify
     * @return the modified ItemStack with the "unable to move" restriction applied
     */
    public static ItemStack makeUnableToMove(ItemStack stack) {
        return setNBTForItemstack(stack, UNABLE_TO_MOVE);
    }

    /**
     * Modifies the provided ItemStack to make it unable to be dropped by applying a specific NBT tag.
     *
     * @param stack the ItemStack to modify
     * @return the modified ItemStack with the "unable to drop" restriction applied
     */
    public static ItemStack makeUnableToDrop(ItemStack stack) {
        return setNBTForItemstack(stack, UNABLE_TO_DROP);
    }

    /**
     * Modifies the provided ItemStack to remove its damage bar by making it unbreakable and
     * hiding the unbreakable attribute from the item description.
     *
     * @param stack the ItemStack to modify
     * @return the modified ItemStack with the damage bar removed
     */
    public static ItemStack removeDamageBar(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Modifies the provided ItemStack to hide its attribute descriptions, such as
     * attack speed and damage, by applying the `HIDE_ATTRIBUTES` ItemFlag.
     *
     * @param stack the ItemStack to modify
     * @return the modified ItemStack with hidden attributes
     */
    public static ItemStack hideAttributes(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Modifies the provided ItemStack to make it unbreakable and hides the unbreakable attribute
     * from the item description.
     *
     * @param stack the ItemStack to modify
     * @return the modified ItemStack with unbreakable attributes applied
     */
    public static ItemStack unbreakable(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Adds a visual glow effect to the specified ItemStack by applying an enchantment
     * and hiding the enchantment display in the item's metadata.
     *
     * @param stack the ItemStack to which the glow effect is added
     * @return the modified ItemStack with the glow effect applied
     */
    public static ItemStack addGlow(ItemStack stack) {
        stack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Handles the InventoryClickEvent to prevent movement of items that have specific NBT tags.
     * This method cancels the event if the clicked item has the "unable to move" NBT tag applied to it.
     *
     * @param event the InventoryClickEvent triggered when a player interacts with an inventory
     */
    @EventHandler(priority = EventPriority.LOWEST)
    protected void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (hasNBT(event.getCurrentItem(), UNABLE_TO_MOVE)) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles the PlayerDropItemEvent to prevent players from dropping items that have
     * specific NBT tags. Cancels the event if the item being dropped has the "unable to drop" NBT tag.
     *
     * @param event the PlayerDropItemEvent triggered when a player attempts to drop an item
     */
    @EventHandler(priority = EventPriority.LOWEST)
    protected void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop() == null) return;
        if (event.getItemDrop().getItemStack() == null) return;
        if (hasNBT(event.getItemDrop().getItemStack(), UNABLE_TO_DROP)) {
            event.setCancelled(true);
        }
    }

    /**
     * Checks if the given ItemStack contains a specific NBT tag with the value set to 1.
     *
     * @param stack the ItemStack to check
     * @param tag the name of the NBT tag to look for
     * @return true if the ItemStack contains the specified NBT tag with a value of 1, false otherwise
     */
    public static boolean hasNBT(ItemStack stack, String tag) {
        if (stack.getType().equals(Material.AIR)) return false;
        ItemStack craftStack = stack;
        if (!MinecraftReflection.isCraftItemStack(stack)) {
            craftStack = MinecraftReflection.getBukkitItemStack(stack);
        }
        if (craftStack.getType().equals(Material.AIR)) return false;
        try {
            NbtCompound nbt = NbtFactory.asCompound(NbtFactory.fromItemTag(craftStack));
            return nbt.containsKey(tag) && nbt.getInteger(tag) == 1;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Sets the specified NBT tag on the provided {@link ItemStack}.
     * Converts the item to a CraftItemStack if necessary, modifies its NBT data,
     * and applies the changes to the item.
     *
     * @param stack the {@link ItemStack} to modify
     * @param tag the name of the NBT tag to set
     * @return the modified {@link ItemStack} with the specified NBT tag applied
     */
    public static ItemStack setNBTForItemstack(ItemStack stack, String tag) {
        ItemStack craftStack = stack;
        if (!MinecraftReflection.isCraftItemStack(stack)) {
            craftStack = MinecraftReflection.getBukkitItemStack(stack);
        }
        NbtCompound nbt = NbtFactory.asCompound(NbtFactory.fromItemTag(craftStack));
        nbt.put(tag, 1);
        NbtFactory.setItemTag(craftStack, nbt);
        return craftStack;
    }

    /**
     * Generates a friendly, human-readable NBT (Named Binary Tag) representation
     * for the given ItemStack.
     *
     * @param stack the ItemStack whose NBT data is to be retrieved and formatted.
     * @return a string representation of the NBT data for the given ItemStack.
     *         Returns an empty string if an error occurs during processing.
     */
    public static String getFriendlyNBT(ItemStack stack) {
        Object minecraftItemstack = MinecraftReflection.getMinecraftItemStack(stack);
        Class nbtCompoundClass = MinecraftReflection.getNBTCompoundClass();
        String nbt = "";
        try {
            nbt = MethodUtils.invokeMethod(minecraftItemstack, "save", nbtCompoundClass.newInstance()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nbt;
    }

    /**
     * Creates an ItemStack of the specified material with a default quantity.
     *
     * @param type The material type of the item to create. Must not be null.
     * @return A new ItemStack of the specified material with a quantity of 1.
     */
    public static ItemStack create(Material type) {
        return create(type, 1);
    }

    /**
     * Creates a new ItemStack instance with the specified material and amount.
     *
     * @param type the material to be used for the ItemStack
     * @param amount the number of items in the stack
     * @return a new ItemStack instance with the specified material and amount
     */
    public static ItemStack create(Material type, int amount) {
        return new ItemStack(type, amount);
    }

    /**
     * Creates an ItemStack with the specified material type, amount, and damage value.
     *
     * @param type the material type of the item
     * @param amount the amount of items in the stack
     * @param damage the damage value to set for the item
     * @return the created ItemStack with the specified properties
     */
    public static ItemStack create(Material type, int amount, int damage) {
        ItemStack item = create(type, amount);
        item.setDurability((short) damage);
//        ItemMeta meta = item.getItemMeta();
//        ((Damageable) meta).setDamage(damage);
//        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates an {@code ItemStack} with the specified material type, display name, and an empty list of lore.
     *
     * @param type the material type of the {@code ItemStack}
     * @param name the display name to be applied to the {@code ItemStack}
     * @return a newly created {@code ItemStack} with the specified type and name
     */
    public static ItemStack create(Material type, String name) {
        return create(type, name, new ArrayList<>());
    }

    /**
     * Creates an ItemStack with the specified material type, name, and damage value.
     *
     * @param type the material type of the ItemStack
     * @param name the display name of the ItemStack
     * @param damage the damage value to set for the ItemStack
     * @return the created ItemStack with the specified properties
     */
    public static ItemStack create(Material type, String name, int damage) {
        ItemStack item = create(type, name);
        item.setDurability((short) damage);
//        ItemMeta meta = item.getItemMeta();
//        ((Damageable) meta).setDamage(damage);
//        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates an ItemStack with the specified material type, display name, and lore.
     *
     * @param type the material type of the ItemStack
     * @param name the display name of the ItemStack
     * @param lore the lore to be added to the ItemStack
     * @return the created ItemStack with the specified properties
     */
    public static ItemStack create(Material type, String name, List<String> lore) {
        return create(type, 1, name, lore);
    }

    /**
     * Creates an ItemStack with the specified type, amount, display name, and lore,
     * while adding specific item flags to hide attributes and destruction markers.
     *
     * @param type the material type of the item
     * @param amount the quantity of the item
     * @param name the display name of the item
     * @param lore the lore to be assigned to the item
     * @return an ItemStack with the specified properties
     */
    public static ItemStack create(Material type, int amount, String name, List<String> lore) {
        ItemStack item = create(type, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates an ItemStack with the specified material, amount, damage value, name, and lore.
     *
     * @param type   the material type of the item
     * @param amount the quantity of items in the stack
     * @param damage the damage value or durability of the item
     * @param name   the display name for the item
     * @param lore   the lore (description) lines for the item
     * @return the newly created ItemStack with the specified attributes
     */
    public static ItemStack create(Material type, int amount, int damage, String name, List<String> lore) {
        ItemStack item = create(type, amount);
        item.setDurability((short) damage);
        ItemMeta meta = item.getItemMeta();
//        ((Damageable) meta).setDamage(damage);
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates a leather armor ItemStack with the specified type, name, and color.
     *
     * @param type the material type of the leather armor (LEATHER_HELMET, LEATHER_CHESTPLATE,
     *             LEATHER_LEGGINGS, or LEATHER_BOOTS)
     * @param name the display name of the armor
     * @param r the red component of the armor color (0-255)
     * @param g the green component of the armor color (0-255)
     * @param b the blue component of the armor color (0-255)
     * @return a leather armor ItemStack with the specified properties
     * @throws IllegalArgumentException if the provided material is not a leather armor type
     */
    public static ItemStack coloredArmor(Material type, String name, int r, int g, int b) {
        if (!type.equals(Material.LEATHER_HELMET) && !type.equals(Material.LEATHER_CHESTPLATE) &&
                !type.equals(Material.LEATHER_LEGGINGS) && !type.equals(Material.LEATHER_BOOTS)) {
            throw new IllegalArgumentException("You must provide a leather armor type for this method");
        }
        ItemStack item = create(type, name);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(r, g, b));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates an ItemStack object with the specified owner and default properties.
     *
     * @param owner the name of the owner for the created ItemStack
     * @return a new ItemStack object associated with the provided owner
     */
    public static ItemStack create(String owner) {
        return create(owner, owner, new ArrayList<>());
    }

    /**
     * Creates an ItemStack with the specified owner and display name.
     *
     * @param owner the owner of the ItemStack
     * @param displayName the display name of the ItemStack
     * @return the created ItemStack with the specified owner and display name
     */
    public static ItemStack create(String owner, String displayName) {
        return create(owner, displayName, new ArrayList<>());
    }

    /**
     * Creates a custom ItemStack configured as a player head with the specified owner,
     * display name, and lore.
     *
     * @param owner the owner of the skull, specified as a player's username
     * @param displayName the custom display name to set for the ItemStack
     * @param lore the list of strings representing the lore to be added to the ItemStack
     * @return the customized ItemStack representing a player head
     */
    @SuppressWarnings("deprecation")
    public static ItemStack create(String owner, String displayName, List<String> lore) {
        ItemStack item = create(Material.SKULL_ITEM, 1, 3);
        SkullMeta sm = (SkullMeta) item.getItemMeta();
        sm.setOwner(owner);
        sm.setDisplayName(displayName);
        sm.setLore(lore);
        item.setItemMeta(sm);
        return item;
    }

    /*
    OLD METHODS START
     */

    /**
     * Creates an ItemStack with the specified material type, amount, and data value.
     *
     * @param type the material type of the item stack
     * @param amount the quantity of items in the stack
     * @param data the data value of the item stack
     * @return an ItemStack with the given properties
     */
    public static ItemStack create(Material type, int amount, byte data) {
        return new ItemStack(type, amount, data);
    }

    /**
     * Creates a new {@code ItemStack} with the specified material type, name, and data value.
     * This method also initializes the item with an empty list of additional metadata or properties.
     *
     * @param type the material type of the item
     * @param name the display name of the item
     * @param data the data value of the item (used for durability or additional item variations)
     * @return a newly created {@code ItemStack} with the specified attributes
     */
    public static ItemStack create(Material type, String name, byte data) {
        return create(type, name, data, new ArrayList<>());
    }

    /**
     * Creates an ItemStack with the specified material type, display name, data value, and lore.
     *
     * @param type the material type of the ItemStack
     * @param name the display name of the ItemStack
     * @param data the data value of the ItemStack
     * @param lore the lore (additional descriptive text) for the ItemStack
     * @return the created ItemStack with the specified properties
     */
    public static ItemStack create(Material type, String name, byte data, List<String> lore) {
        return create(type, 1, data, name, lore);
    }

    /**
     * Creates an ItemStack with the specified type, amount, data, display name, and lore.
     *
     * @param type  the material type of the item
     * @param amount  the quantity of the item
     * @param data  the data value of the item
     * @param name  the display name of the item
     * @param lore  the lore (description) of the item
     * @return a customized ItemStack with the specified properties
     */
    public static ItemStack create(Material type, int amount, byte data, String name, List<String> lore) {
        ItemStack item = create(type, amount, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Converts an {@link ItemStack} object into a {@link JsonObject} containing its properties.
     *
     * @param i the {@link ItemStack} to be converted into JSON format. Can be null.
     * @return a {@link JsonObject} representing the {@link ItemStack}'s properties.
     *         Returns an empty JSON object if the input {@link ItemStack} is null.
     */
    public static JsonObject getJsonFromItem(ItemStack i) {
        JsonObject o = new JsonObject();
        if (i == null) {
            return o;
        }
        o.addProperty("t", i.getTypeId());
        o.addProperty("a", i.getAmount());
        o.addProperty("da", i.getData().getData());
        o.addProperty("du", i.getDurability());
        String t = new NbtTextSerializer().serialize(NbtFactory.fromItemTag(i));
        if (!t.equals("")) {
            o.addProperty("ta", t);
        }
        return o;
    }

    /**
     * Converts a JSON string into an ItemStack.
     * The JSON string should contain specific keys representing item attributes such as type, amount, durability, data, and tags.
     * If the JSON string does not specify the required item type, an ItemStack of Material.AIR is returned.
     *
     * @param json the JSON string representing the item configuration
     * @return an ItemStack object created based on the JSON data, or Material.AIR if the type is not specified or an error occurs during processing
     */
    public static ItemStack getItemFromJson(String json) {
        JsonObject o = new JsonParser().parse(json).getAsJsonObject();
        if (!o.has("t")) {
            return new ItemStack(Material.AIR);
        }
        ItemStack i;
        try {
            i = MinecraftReflection.getBukkitItemStack(new ItemStack(o.get("t").getAsInt()));
            i.setData(new MaterialData(o.get("t").getAsInt(), (byte) o.get("da").getAsInt()));
            i.setAmount(o.get("a").getAsInt());
            i.setDurability(o.get("du").getAsShort());
            if (o.has("ta")) {
                try {
                    NbtFactory.setItemTag(i, new NbtTextSerializer().deserializeCompound(o.get("ta").getAsString()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ignored) {
            return new ItemStack(Material.AIR);
        }
        return i;
    }

    /**
     * Converts the contents of the provided inventory into a JSON array.
     *
     * @param inv the Inventory object whose contents are to be converted to JSON
     * @return a JsonArray representing the inventory contents
     */
    public static JsonArray getJsonFromInventory(Inventory inv) {
        return getJsonFromArray(inv.getContents());
    }

    /**
     * Converts an array of ItemStack objects into a JsonArray.
     *
     * @param arr the array of ItemStack objects to be converted
     * @return a JsonArray representation of the given ItemStack array
     */
    public static JsonArray getJsonFromArray(ItemStack[] arr) {
        JsonArray a = new JsonArray();
        for (ItemStack i : arr) {
            a.add(getJsonFromItem(i));
        }
        return a;
    }

    /**
     * Parses a JSON string representing an inventory and converts it into an array of ItemStack objects.
     * The JSON string is expected to be an array of serialized item data.
     *
     * @param json the JSON string representing the inventory, where each element describes an item
     * @return an array of ItemStack objects parsed from the JSON string.
     *         Returns an empty array if the input is not a valid JSON array.
     */
    public static ItemStack[] getInventoryFromJson(String json) {
        JsonElement e = new JsonParser().parse(json);
        if (!e.isJsonArray()) {
            return new ItemStack[0];
        }
        JsonArray ja = e.getAsJsonArray();
        ItemStack[] a = new ItemStack[ja.size()];
        int i = 0;
        for (JsonElement e2 : ja) {
            JsonObject o = e2.getAsJsonObject();
            a[i] = getItemFromJson(o.toString());
            i++;
        }
        return a;
    }

    /*
    OLD METHODS END
     */

    /*
    NEW METHODS START
     */

    /**
     * Converts the provided ItemStack into a JsonObject representation containing
     * key properties and data from the item.
     *
     * @param i the ItemStack to be converted. Can be null or an ItemStack of type AIR,
     *          in which case an empty JsonObject is returned.
     * @return a JsonObject representation of the provided ItemStack, including properties
     *         such as type, data, amount, and tag (if present).
     */
    public static JsonObject getJsonFromItemNew(ItemStack i) {
        JsonObject o = new JsonObject();
        if (i == null || i.getType().equals(Material.AIR)) {
            return o;
        }
        o.addProperty("type", i.getData().getItemType().name());
        o.addProperty("data", i.getDurability());
        o.addProperty("amount", i.getAmount());
        try {
            String nbtTag = new NbtTextSerializer().serialize(NbtFactory.fromItemTag(i));
            if (!nbtTag.isEmpty()) {
                o.addProperty("tag", nbtTag);
            }
        } catch (Exception ignored) {
        }
        return o;
    }

    /**
     * Converts a JSON string into an ItemStack instance. The JSON data should include the
     * item's type, amount, and optional metadata such as durability and NBT tags.
     *
     * @param json the JSON string containing item properties; must define "type" as a
     *             primary attribute. Additional attributes may include "data", "amount",
     *             and "tag".
     * @return an ItemStack object created from the JSON data, or an ItemStack of type
     *         Material.AIR if the input is invalid or an error occurs during parsing.
     */
    public static ItemStack getItemFromJsonNew(String json) {
        JsonObject o = new JsonParser().parse(json).getAsJsonObject();
        if (!o.has("type")) {
            return new ItemStack(Material.AIR);
        }
        ItemStack i;
        try {
            i = MinecraftReflection.getBukkitItemStack(new ItemStack(Material.matchMaterial(o.get("type").getAsString())));
            i.setDurability((short) o.get("data").getAsInt());
            i.setAmount(o.get("amount").getAsInt());
            if (o.has("tag") && !o.get("tag").getAsString().isEmpty()) {
                try {
                    NbtFactory.setItemTag(i, new NbtTextSerializer().deserializeCompound(o.get("tag").getAsString()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack(Material.AIR);
        }
        return i;
    }

    /**
     * Converts the contents of the given inventory into a JSON array.
     *
     * @param inv the inventory whose contents need to be converted to JSON
     * @return a JsonArray representing the inventory contents
     */
    public static JsonArray getJsonFromInventoryNew(Inventory inv) {
        return getJsonFromArrayNew(inv.getContents());
    }

    /**
     * Converts an array of ItemStack objects into a JsonArray.
     *
     * @param arr the array of ItemStack objects to be converted
     * @return a JsonArray representing the converted ItemStack objects
     */
    public static JsonArray getJsonFromArrayNew(ItemStack[] arr) {
        JsonArray a = new JsonArray();
        for (ItemStack i : arr) {
            a.add(getJsonFromItemNew(i));
        }
        return a;
    }

    /**
     * Converts a JSON string representation of an inventory into an array of ItemStack objects.
     *
     * @param json the JSON string representing the inventory, expected to be a JSON array
     * @return an array of ItemStack objects parsed from the JSON array; returns an empty array if the input is not valid
     */
    public static ItemStack[] getInventoryFromJsonNew(String json) {
        JsonElement e = new JsonParser().parse(json);
        if (!e.isJsonArray()) {
            return new ItemStack[0];
        }
        JsonArray ja = e.getAsJsonArray();
        ItemStack[] a = new ItemStack[ja.size()];
        int i = 0;
        for (JsonElement e2 : ja) {
            JsonObject o = e2.getAsJsonObject();
            a[i] = getItemFromJsonNew(o.toString());
            i++;
        }
        return a;
    }

    /*
    NEW METHODS END
     */
}