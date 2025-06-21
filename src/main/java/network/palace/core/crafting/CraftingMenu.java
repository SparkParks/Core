package network.palace.core.crafting;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import network.palace.core.Core;
import network.palace.core.achievements.CoreAchievement;
import network.palace.core.events.OpenCosmeticsEvent;
import network.palace.core.player.CPlayer;
import network.palace.core.player.RankTag;
import network.palace.core.utils.HeadUtil;
import network.palace.core.utils.ItemUtil;
import network.palace.core.utils.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * The CraftingMenu class is responsible for managing a custom crafting menu in the game.
 * It integrates with the server's packet handling system to override certain behaviors
 * of the crafting inventory while providing additional functionality such as pages for achievements,
 * cosmetic menus, and other custom features.
 * <p>
 * This class implements packet listeners for handling interactions with the player's inventory
 * and listening for specific events related to the crafting menu.
 * <p>
 * The main functionality includes:
 * - Displaying custom menu items in the crafting inventory.
 * - Preventing unauthorized modifications of the crafting inventory by players in certain game modes.
 * - Handling navigation through custom achievement pages in the inventory.
 * - Scheduling updates to maintain menu consistency for online players.
 * - Providing custom visual elements such as player-specific heads and information.
 * <p>
 * This class relies on external dependencies such as ProtocolLibrary, which allows manipulation
 * of server-client packets, and Core for accessing essential player and game utilities.
 * <p>
 * Note: This class assumes that the external utility classes like ItemUtil, HeadUtil, and Core
 * are implemented properly and provide the required methods for item creation and player management.
 */
public class CraftingMenu implements Listener {
    //Pages
    /**
     * Represents the ItemStack used to navigate to the next page in the crafting menu.
     * It is created as an arrow item with a green-colored "Next Page" label.
     * This field is immutable and utilized within the CraftingMenu class for pagination purposes.
     */
    private final ItemStack nextPage = ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Next Page");

    /**
     * Represents the navigation item for the "Last Page" in the crafting menu.
     * This item is displayed as an arrow with a green-colored name.
     * Used to allow players to navigate to the previous page.
     * It is a final and immutable field initialized with a specific ItemStack configuration.
     */
    private final ItemStack lastPage = ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Last Page");

    /**
     * A list of UUIDs representing players who require the crafting menu to be refreshed.
     * This list is used to track players whose menu needs to be updated upon specific actions
     * or events, ensuring that the displayed menu contents are accurate and consistent.
     */
    private final List<UUID> refresh = new ArrayList<>();

    /**
     * Handles the initialization of the CraftingMenu feature in the application. This constructor
     * sets up the necessary packet listeners and recurrent tasks required for managing player
     * interactions with crafting inventories.
     * <p>
     * The constructor listens for specific packet types to intercept and modify inventory-related packets
     * to maintain custom crafting menu behavior. These include:
     * <p>
     * - Server-bound packets for updating inventory contents and slots.
     * - Client-bound packets to manage creative inventory interactions and window click events.
     * <p>
     * It also sets up a scheduled task that periodically monitors online players' inventory views,
     * ensuring consistency in crafting menu updates and handling specific inventory types.
     * <p>
     * The functionality includes:
     * - Preventing certain clicks and interactions in the crafting menu based on player game mode.
     * - Customizing the appearance and content of items in the crafting inventory.
     * - Handling actions such as opening achievement pages or cosmetics inventory when specific slots
     *   are clicked.
     * - Synchronizing and refreshing inventory states for players during gameplay.
     * <p>
     * The constructor ensures that players in unsupported game modes (Creative, Spectator) have
     * these features disabled and their inventories remain unaffected.
     */
    public CraftingMenu() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Core.getInstance(),
                PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.SET_SLOT, PacketType.Play.Client.SET_CREATIVE_SLOT, PacketType.Play.Client.WINDOW_CLICK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
                if (player == null || player.getGamemode().equals(GameMode.CREATIVE) || player.getGamemode().equals(GameMode.SPECTATOR))
                    return;
                PacketContainer cont = event.getPacket();
                PacketType type = cont.getType();
                if (type.equals(PacketType.Play.Server.WINDOW_ITEMS)) {
                    List<ItemStack> item = cont.getItemListModifier().read(0);
                    if (item.size() != 46) return;
                    ItemStack[] array = getMenuItems(player);
                    for (int i = 0; i < array.length; i++) {
                        ItemStack itm = array[i];
                        if (itm != null && !itm.getType().equals(Material.AIR)) {
                            item.set(i, itm);
                        }
                    }
                    cont.getItemListModifier().write(0, item);
                } else if (type.equals(PacketType.Play.Server.SET_SLOT)) {
                    StructureModifier<Integer> mod = cont.getIntegers();
                    int id = mod.read(0);
                    int slot = mod.read(1);
                    ItemStack item = cont.getItemModifier().read(0);
                    if (id != 0 || slot >= 5) return;
                    ItemStack[] array = getMenuItems(player);
                    cont.getItemModifier().write(0, array[slot]);
                }
            }

            @Override
            public void onPacketReceiving(PacketEvent event) {
                CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
                if (player == null)
                    return;
                boolean survival = player.getGamemode().equals(GameMode.SURVIVAL) || player.getGamemode().equals(GameMode.ADVENTURE);
                PacketContainer cont = event.getPacket();
                PacketType type = cont.getType();
                if (type.equals(PacketType.Play.Client.SET_CREATIVE_SLOT) && !survival) {
                    ItemStack item = cont.getItemModifier().read(0);
                    if (item == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null)
                        return;
                    String name = item.getItemMeta().getDisplayName();
                    ItemStack[] array = getMenuItems(player);
                    for (ItemStack i : array) {
                        if (i != null && i.getType().equals(item.getType()) && i.getItemMeta() != null &&
                                i.getItemMeta().getDisplayName() != null && name.equals(i.getItemMeta().getDisplayName())) {
                            event.setCancelled(true);
                            break;
                        }
                    }
                } else if (type.equals(PacketType.Play.Client.WINDOW_CLICK) && !Core.isGameMode() && survival) {
                    StructureModifier<Integer> intMod = cont.getIntegers();
                    if (intMod.read(0) != 0) return;
                    int slot = intMod.read(1);
                    switch (slot) {
                        case 1:
                        case 4:
                        case 45:
                            event.setCancelled(true);
                            Core.runTask(Core.getInstance(), () -> {
                                player.updateInventory();
                                if (slot <= 4) update(player, slot, getMenuItems(player)[slot]);
                            });
                            break;
                        case 2:
                            event.setCancelled(true);
                            Core.runTask(Core.getInstance(), () -> openAchievementPage(player, 1));
                            break;
                        case 3:
                            event.setCancelled(true);
                            Core.runTask(Core.getInstance(), () -> openCosmeticsInventory(player));
                    }
                }
            }
        });
        Core.runTaskTimer(Core.getInstance(), () -> Core.getPlayerManager().getOnlinePlayers().forEach(player -> {
            Optional<InventoryView> optional = player.getOpenInventory();
            if (!optional.isPresent() || player.getGamemode().equals(GameMode.CREATIVE) || player.getGamemode().equals(GameMode.SPECTATOR)) {
                refresh.remove(player.getUniqueId());
                return;
            }
            InventoryView view = optional.get();
            if (view.getType().equals(InventoryType.CRAFTING)) {
                boolean contains = refresh.remove(player.getUniqueId());
                if (!contains) return;
//                player.updateInventory();
                update(player);
            } else if (!refresh.contains(player.getUniqueId())) {
                refresh.add(player.getUniqueId());
            }
        }), 0L, 10L);
    }

    /**
     * Handles the InventoryClickEvent to manage player interactions within the custom achievements menu.
     * Prevents unauthorized actions, navigates between achievement pages, and updates the menu view accordingly.
     *
     * @param event The InventoryClickEvent triggered when a player interacts with an inventory.
     *              This event is used to determine the actions taken within the achievements page GUI.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getWhoClicked().getUniqueId());
        if (player == null) return;
        InventoryView view = event.getView();
        if (view == null || !view.getTitle().startsWith(ChatColor.BLUE + "Achievements Page ")) return;
        event.setCancelled(true);
        String name;
        try {
            name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
        } catch (Exception ignored) {
            return;
        }
        int page = Integer.parseInt(ChatColor.stripColor(view.getTitle()).replaceAll("Achievements Page ", ""));
        switch (name) {
            case "Next Page":
                openAchievementPage(player, page + 1);
                break;
            case "Last Page":
                openAchievementPage(player, page - 1);
                break;
            default:
                openAchievementPage(player, page);
                break;
        }
    }

    /**
     * Handles the InventoryCloseEvent to manage the behavior when a player closes specific custom inventory views.
     * This method checks if the closed inventory matches certain predefined titles (e.g., Achievements Page, Cosmetics, Particles, Hats, Pets) and updates the related player data
     *  accordingly.
     *
     * @param event The InventoryCloseEvent triggered when a player closes an inventory. It provides details about the
     *              player and the inventory view that was closed.
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        if (player == null) return;
        InventoryView view = event.getView();
        String name = view.getTitle();
        if (name.startsWith(ChatColor.BLUE + "Achievements Page ") || name.startsWith(ChatColor.BLUE + "Cosmetics") ||
                name.startsWith(ChatColor.BLUE + "Particles") || name.startsWith(ChatColor.BLUE + "Hats") ||
                name.startsWith(ChatColor.BLUE + "Pets")) {
            update(player);
        }
    }

    /**
     * Updates the custom crafting menu for a given player by iterating through
     * the menu items and refreshing their states based on the player's current data.
     * If the provided player object is null, the method will return without action.
     *
     * @param player The CPlayer object representing the player whose crafting menu
     *               is being updated. This is used to fetch the appropriate menu
     *               items and apply updates to them.
     */
    public void update(CPlayer player) {
        if (player == null) return;
        ItemStack[] array = getMenuItems(player);
        for (int i = 0; i < array.length; i++) {
            update(player, i, array[i]);
        }
    }

    /**
     * Updates a specific inventory slot for a given player with a new item.
     * This method sends a server packet to update the specified slot in
     * the player's inventory using ProtocolLib. If the player object or
     * their Bukkit player instance is null, the method will return
     * without action.
     *
     * @param player The CPlayer object representing the player whose inventory
     *               slot is being updated. This is used to identify the player
     *               and send the update packet.
     * @param slot   The integer representing the inventory slot that should be
     *               updated. The slot number corresponds to the player's
     *               inventory layout.
     * @param item   The ItemStack to be placed in the specified inventory slot.
     *               This determines the new item to display in the slot.
     */
    public void update(CPlayer player, int slot, ItemStack item) {
        if (player == null || player.getBukkitPlayer() == null) return;
        PacketContainer cont = new PacketContainer(PacketType.Play.Server.SET_SLOT);
        StructureModifier<Integer> mod = cont.getIntegers();
        mod.write(0, 0);
        mod.write(1, slot);
        cont.getItemModifier().write(0, item);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.getBukkitPlayer(), cont);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves an array of menu items for the specified player.
     * The menu items include cosmetic inventory, leveling rewards, and other custom items,
     * based on the provided player's data.
     *
     * @param player The CPlayer object representing the player for whom the menu items are being generated.
     *               If the player is null, a default set of menu items will be returned.
     * @return An array of ItemStack objects representing the menu items for the player's crafting menu.
     *         The returned array will always contain exactly five items.
     */
    public ItemStack[] getMenuItems(CPlayer player) {
        if (player == null) return new ItemStack[5];
        ItemStack air = new ItemStack(Material.AIR);
        return new ItemStack[]{air, getPlayerHead(player), getAchievement(player),
                ItemUtil.create(Material.ENDER_CHEST, ChatColor.GREEN + "Cosmetics",
                        Collections.singletonList(ChatColor.GRAY + "Open Cosmetics Menu")),
                ItemUtil.create(Material.STORAGE_MINECART, ChatColor.GREEN + "Leveling Rewards",
                        Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Coming soon!"))};
    }

    /**
     * Creates and returns an ItemStack representing the player's head with customized meta
     * data including the player's rank, level, honor points, and points to the next level.
     * This method utilizes the player's data to set the display name and lore of the skull item.
     *
     * @param player The CPlayer object representing the player whose head is being created.
     *               The player's data is used to populate the metadata of the ItemStack.
     * @return An ItemStack representing the player's head with customized meta data,
     *         including display name and lore containing player-specific information.
     */
    public ItemStack getPlayerHead(CPlayer player) {
        ItemStack head = HeadUtil.getPlayerHead(player);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Player Info");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Rank: " + player.getRank().getFormattedName() + RankTag.formatChat(player.getTags()),
                ChatColor.GRAY + "Level: " + ChatColor.YELLOW + MiscUtil.formatNumber(Core.getHonorManager().getLevel(player.getHonor()).getLevel()),
                ChatColor.GRAY + "Honor Points: " + ChatColor.YELLOW + MiscUtil.formatNumber(player.getHonor()),
                ChatColor.GRAY + "Points to Next Level: " + ChatColor.YELLOW + MiscUtil.formatNumber(Core.getHonorManager().getNextLevel(player.getHonor()).getHonor() - player.getHonor())));
        head.setItemMeta(meta);
        return head;
    }

    /**
     * Retrieves an ItemStack representing the player's achievements.
     * The ItemStack contains metadata such as the number of achievements
     * the player has earned, the total number of achievements available,
     * and a description to view all achievements.
     * <p>
     * If the player or their AchievementManager is null, an ItemStack
     * of type Material.AIR is returned.
     *
     * @param player The CPlayer object representing the player whose
     *               achievements are being retrieved. The player's
     *               AchievementManager is used to determine the number
     *               of achievements earned by the player.
     * @return An ItemStack with metadata displaying the player's
     *         achievements information, or an ItemStack of
     *         Material.AIR if the player or their AchievementManager
     *         is null.
     */
    public ItemStack getAchievement(CPlayer player) {
        if (player == null || player.getAchievementManager() == null) return new ItemStack(Material.AIR);
        int earned = player.getAchievementManager().getAchievements().size();
        int total = Core.getAchievementManager().getAchievements().size();
        return ItemUtil.create(Material.EMERALD, ChatColor.GREEN + "Achievements", Arrays.asList(ChatColor.GREEN +
                        "You've earned " + ChatColor.YELLOW + earned + ChatColor.GREEN + " achievements!",
                ChatColor.GREEN + "There are " + ChatColor.YELLOW + total + ChatColor.GREEN + " total to earn",
                ChatColor.GRAY + "Click to view all of your achievements"));
    }

    /**
     * Opens the achievements page for the specified player, displaying the achievements
     * in a paginated format. This method initializes and configures an inventory GUI
     * where achievements are displayed with their corresponding status (earned or not earned).
     * Additionally, it handles logic for navigation between achievement pages.
     *
     * @param player The CPlayer object representing the player for whom the achievements page
     *               is being opened. Used to determine the player's earned achievements and to set
     *               the inventory view.
     * @param page   The integer value representing the page number of the achievements to be displayed.
     *               Each page contains a fixed number of items, and the method adjusts the inventory
     *               content based on the provided page number.
     */
    public void openAchievementPage(CPlayer player, int page) {
        List<CoreAchievement> achievements = Core.getAchievementManager().getAchievements();
        int size = achievements.size();
        if (size < 46) {
            page = 1;
        } else if (size < (45 * (page - 1) + 1)) {
            page -= 1;
        }
        List<CoreAchievement> list = achievements.subList(page > 1 ? (45 * (page - 1)) : 0, (size - (45 * (page - 1)))
                > 45 ? (45 * page) : size);
        int localSize = list.size();
        int invSize = 54;
        if (localSize < 46) {
            invSize = 9;
            while (invSize < localSize) {
                invSize += 9;
                if (invSize >= 45) {
                    break;
                }
            }
        }
        Inventory inv = Bukkit.createInventory(player.getBukkitPlayer(), invSize, ChatColor.BLUE + "Achievements Page " + page);
        int place = 0;
        for (CoreAchievement ach : list) {
            if (place >= 45) {
                break;
            }
            if (player.hasAchievement(ach.getId())) {
                inv.setItem(place, ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 5, ChatColor.GREEN +
                        ach.getDisplayName(), Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + ach.getDescription())));
            } else {
                inv.setItem(place, ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 4, ChatColor.RED +
                        ach.getDisplayName(), Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "?")));
            }
            place++;
        }
        if (page > 1) {
            inv.setItem(48, lastPage);
        }
        int maxPage = 1;
        int n = size;
        while (true) {
            if (n - 45 > 0) {
                n -= 45;
                maxPage += 1;
            } else {
                break;
            }
        }
        if (size > 45 && page < maxPage) {
            inv.setItem(50, nextPage);
        }
        player.openInventory(inv);
    }

    /**
     * Opens the cosmetics inventory for the specified player. This method triggers
     * the {@code OpenCosmeticsEvent}, which handles the logic for displaying the
     * player's cosmetics inventory.
     *
     * @param player The {@code CPlayer} object representing the player for whom
     *               the cosmetics inventory is being opened. This player object
     *               is required to determine the inventory context and initiate
     *               the corresponding event.
     */
    private void openCosmeticsInventory(CPlayer player) {
        new OpenCosmeticsEvent(player).call();
    }

    /**
     * Handles the {@code OpenCosmeticsEvent} to manage actions when a player attempts to open the
     * cosmetics inventory. If the event is cancelled, the player is notified with an error message,
     * and their inventory is forcibly closed.
     *
     * @param event The {@code OpenCosmeticsEvent} triggered when a player tries to open the cosmetics
     *              inventory. This event allows handling of custom behavior, such as cancellation
     *              and sending feedback to the player.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onOpenCosmetics(OpenCosmeticsEvent event) {
        if (event.isCancelled()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Sorry, there was an error opening the Cosmetics inventory!");
            event.getPlayer().closeInventory();
        }
    }
}
