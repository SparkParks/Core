package network.palace.core.economy;

import network.palace.core.Core;
import network.palace.core.economy.currency.Transaction;
import network.palace.core.economy.honor.HonorMapping;
import network.palace.core.player.CPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.util.*;

/**
 * Manages the honor transactions and mappings for players. This class handles
 * synchronization of transactions, mapping honor levels to players, and
 * tracking progress to the next honor level. It also provides functionality
 * to display honor levels to players.
 */
public class HonorManager {
    /**
     * A map that tracks transactions associated with players.
     * The key represents the unique identifier (UUID) of a player,
     * and the value is the corresponding {@link Transaction}
     * containing all details related to the transaction.
     * <p>
     * This map is used internally by the {@code HonorManager}
     * to manage and process player transactions efficiently.
     * <p>
     * It utilizes a {@link HashMap} for fast retrieval and storage of transaction data.
     */
    private final Map<UUID, Transaction> transactions = new HashMap<>();

    /**
     * A set of mappings used to map honor levels to specific honor values.
     * This field represents the collection of {@link HonorMapping} objects
     * managed by the {@code HonorManager} class.
     */
    private final Set<HonorMapping> mappings = new HashSet<>();

    /**
     * A sorted set that holds the honor level mappings in ascending order.
     * This field is used to store honor thresholds or levels, which can
     * be utilized for determining a player's current level or progression.
     * The use of a TreeSet ensures the mappings are always stored
     * in sorted order, facilitating efficient range queries and level calculation.
     */
    private final TreeSet<Integer> honorMappings = new TreeSet<>();

    /**
     * Represents the highest honor level recorded in the system.
     * This variable is used to track the maximum honor level and
     * may be updated during the lifecycle of the HonorManager as new levels are introduced or calculated.
     */
    private int highest = 0;

    /**
     * Constructor for the HonorManager class.
     * <p>
     * This constructor initializes a task that runs periodically to process
     * transactions for honor updates. Transactions are processed in smaller
     * batches to ensure efficient handling and to avoid overloading the system.
     * <p>
     * The process is divided into the following steps:
     * 1. Retrieve a subset of transactions from the main transactions map.
     * 2. Remove the retrieved transactions from the main map.
     * 3. Process each transaction asynchronously, ensuring database updates
     *    are made to reflect changes in honor levels.
     * <p>
     * Error handling mechanisms are in place to catch exceptions during the
     * transaction processing and to notify the corresponding callbacks if
     * transactions fail.
     */
    public HonorManager() {
        Core.runTaskTimer(() -> {
            boolean started = false;
            try {
                // Process at most 20 transactions per second
                Map<UUID, Transaction> localMap = getPartOfMap(transactions, 10);
                started = true;
                // Remove payments about to be processed from main transactions map
                localMap.keySet().forEach(transactions::remove);
                // Asynchronously handle database calls for localMap payments
                Core.runTaskAsynchronously(() -> {
                    for (Map.Entry<UUID, Transaction> entry : new HashSet<>(localMap.entrySet())) {
                        Transaction transaction = entry.getValue();
                        if (transaction.getAmount() == 0) {
                            if (transaction.getCallback() != null)
                                transaction.getCallback().handled(false, "Cannot process transaction of amount 0.");
                            continue;
                        }
                        try {
                            Core.getMongoHandler().addHonor(transaction.getPlayerId(), transaction.getAmount(), transaction.getSource());
                            if (transaction.getCallback() != null) transaction.getCallback().handled(true, "");
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (transaction.getCallback() != null)
                                transaction.getCallback().handled(false, "An error occurred while contacting the database to process this transaction.");
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                if (started) {
                    transactions.values().forEach(transaction -> {
                        if (transaction.getCallback() != null)
                            transaction.getCallback().handled(false, "An error occurred while processing this transaction.");
                    });
                    transactions.clear();
                }
            }
        }, 0L, 20L);
    }

    /**
     * Retrieves a subset of a given map, containing a specified maximum number of entries.
     * If the map's size is less than or equal to the requested amount, the entire map is returned.
     *
     * @param map    the original map from which a subset is to be extracted
     * @param amount the maximum number of entries to include in the resulting map
     * @return a new map containing up to the specified number of entries from the original map
     */
    private Map<UUID, Transaction> getPartOfMap(Map<UUID, Transaction> map, int amount) {
        if (map.size() <= amount) return new HashMap<>(map);
        Map<UUID, Transaction> mapPart = new HashMap<>();
        int i = 0;
        for (Map.Entry<UUID, Transaction> entry : map.entrySet()) {
            if (i >= amount) break;
            mapPart.put(entry.getKey(), entry.getValue());
            i++;
        }
        return mapPart;
    }

    /**
     * Adds a transaction to the system for processing. A new transaction is created
     * with the given parameters and stored in the internal transaction map.
     *
     * @param uuid      the unique identifier of the player making the transaction
     * @param amount    the amount associated with the transaction
     * @param source    the source or description of the transaction
     * @param callback  the callback to handle the result of the transaction processing
     */
    public void addTransaction(UUID uuid, int amount, String source, TransactionCallback callback) {
        Transaction transaction = new Transaction(uuid, null, amount, source, callback);
        transactions.put(transaction.getPaymentId(), transaction);
    }

    /**
     * Updates the current honor mappings with the provided list of mappings.
     * Clears any existing mappings and recalculates the highest level and
     * honor mappings. Also updates honor for all online players.
     *
     * @param mappings a list of HonorMapping objects representing the updated honor mappings
     */
    public void provideMappings(List<HonorMapping> mappings) {
        this.mappings.clear();
        honorMappings.clear();
        mappings.forEach(m -> {
            if (m.getLevel() > highest) {
                highest = m.getLevel();
            }
        });
        this.mappings.addAll(mappings);
        this.mappings.stream().map(HonorMapping::getHonor).forEach(honorMappings::add);
        if (Core.getPlayerManager().getOnlinePlayers().isEmpty()) {
            return;
        }
        for (CPlayer p : Core.getPlayerManager().getOnlinePlayers()) {
            displayHonor(p);
        }
    }

    /**
     * Retrieves the honor mapping for a given player based on their current honor.
     * This method calculates the player's level and experience points (XP) relative to their honor.
     *
     * @param player the player whose honor mapping is to be determined
     * @return an HonorMapping object containing the player's level and XP
     */
    public HonorMapping getMapped(CPlayer player) {
        if (player.getHonor() == 0) {
            return new HonorMapping(1, 0);
        }
        Integer i = honorMappings.floor(player.getHonor());
        if (i == null) return new HonorMapping(1, 0);
        Optional<HonorMapping> mapping = getMapped(i);
        if (!mapping.isPresent()) {
            return new HonorMapping(1, 0);
        }
        int xp = player.getHonor() - mapping.get().getHonor();
        return new HonorMapping(mapping.get().getLevel(), xp);
    }

    /**
     * Retrieves the honor mapping corresponding to a specific honor value.
     * Determines the level and honor mapping for the given honor amount.
     *
     * @param honor the honor value for which the corresponding level and mapping are to be determined
     * @return an HonorMapping object representing the level and honor for the given input; defaults to level 1
     *         and honor 0 if no specific mapping exists
     */
    public HonorMapping getLevel(int honor) {
        if (honor == 0) {
            return new HonorMapping(1, 0);
        }
        Integer i = honorMappings.floor(honor);
        if (i == null) return new HonorMapping(1, 0);
        Optional<HonorMapping> mapping = getMapped(i);
        return mapping.orElseGet(() -> new HonorMapping(1, 0));
    }

    /**
     * Determines the next honor level and its associated honor requirement for a given honor value.
     * This method calculates the next level based on the provided honor amount.
     * If no next level exists, it defaults to level 1 and honor 0.
     *
     * @param honor the current honor value for which the next level is to be determined
     * @return an HonorMapping object representing the next level and honor requirement
     */
    public HonorMapping getNextLevel(int honor) {
        if (honor == 0) {
            return new HonorMapping(1, 0);
        }
        Integer i = honorMappings.floor(honor);
        if (i == null) return new HonorMapping(1, 0);
        Optional<HonorMapping> mapping = getMapped(i);
        if (!mapping.isPresent()) {
            return new HonorMapping(1, 0);
        }
        int xp = honor - mapping.get().getHonor();
        Integer nextLevelHonor = honorMappings.ceiling(xp == 0 ? honor + 1 : honor);
        if (nextLevelHonor == null) {
            return new HonorMapping(1, 0);
        }
        Optional<HonorMapping> next = getMapped(nextLevelHonor);
        return next.orElseGet(() -> new HonorMapping(1, 0));
    }

    /**
     * Calculates the player's progress towards the next honor level based on their current honor value.
     * This is determined by the relative position of the player's honor within the range defined
     * by their current level's honor requirement and the next level's honor requirement.
     *
     * @param honor the current honor value of the player
     * @return a float representing the player's progress to the next level, where 0.0f indicates no progress
     *         and 1.0f indicates that the next level is fully reached
     */
    public float progressToNextLevel(int honor) {
        if (honor == 0) {
            return 0.0f;
        }
        Integer i = honorMappings.floor(honor);
        if (i == null) return 1.0f;
        Optional<HonorMapping> mapping = getMapped(i);
        if (!mapping.isPresent()) {
            return 1.0f;
        }
        int xp = honor - mapping.get().getHonor();
        Integer nextLevelHonor = honorMappings.ceiling(xp == 0 ? honor + 1 : honor);
        if (nextLevelHonor == null) {
            return 1.0f;
        }
        Optional<HonorMapping> next = getMapped(nextLevelHonor);
        if (!next.isPresent()) {
            return 1.0f;
        }
        int diff = next.get().getHonor() - mapping.get().getHonor();
        if (diff == 0) return 1.0f;
        return (float) ((double) xp / (double) diff);
    }

    /**
     * Displays the honor level and progress of the given player.
     * This method invokes an internal implementation to handle the display,
     * considering default behavior.
     *
     * @param player the player whose honor level and progress are being displayed
     */
    public void displayHonor(CPlayer player) {
        displayHonor(player, false);
    }

    /**
     * Displays the honor level and progress of the given player.
     * Updates the player's level and experience based on the honor mappings.
     * Handles level changes and provides appropriate feedback to the player.
     *
     * @param player the player whose honor level and progress are being displayed
     * @param first  a flag indicating whether this is the first time the honor is being displayed,
     *               affecting how level changes are processed
     */
    public void displayHonor(CPlayer player, boolean first) {
        HonorMapping mapping = getMapped(player);
        if (mapping == null) {
            if (!Core.isGameMode()) {
                player.setLevel(1);
                player.setExp(0.0f);
            }
            return;
        }
        if (player.getPreviousHonorLevel() != mapping.getLevel() && !first && mapping.getLevel() > 1) {
            // Level change
            if (player.getPreviousHonorLevel() < mapping.getLevel()) {
                // Level increase (most common)
                player.sendMessage("\n");
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "LEVEL UP: " + ChatColor.YELLOW +
                        "You are now " + getColorFromLevel(mapping.getLevel()) + "" + ChatColor.BOLD + "Level " + mapping.getLevel());
                player.sendMessage("\n");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
        }
        player.setPreviousHonorLevel(mapping.getLevel());
        float progress = progressToNextLevel(player.getHonor());
        if (Core.isGameMode()) {
            return;
        }
        player.setLevel(mapping.getLevel());
        player.setExp(progress);
        Core.getCraftingMenu().update(player, 1, Core.getCraftingMenu().getPlayerHead(player));
    }

    /**
     * Determines the color associated with the given level.
     * The color corresponds to predefined ranges for level thresholds.
     *
     * @param level the player's level used to determine the corresponding color
     * @return the ChatColor associated with the given level
     */
    private ChatColor getColorFromLevel(int level) {
        if (level < 10) {
            return ChatColor.GREEN;
        } else if (level < 20) {
            return ChatColor.DARK_GREEN;
        } else if (level < 30) {
            return ChatColor.YELLOW;
        } else if (level < 40) {
            return ChatColor.RED;
        } else if (level < 50) {
            return ChatColor.AQUA;
        } else if (level < 60) {
            return ChatColor.BLUE;
        } else if (level < 70) {
            return ChatColor.DARK_BLUE;
        } else if (level < 80) {
            return ChatColor.LIGHT_PURPLE;
        } else if (level < 90) {
            return ChatColor.DARK_PURPLE;
        } else {
            return ChatColor.GOLD;
        }
    }

    /**
     * Retrieves the honor mapping corresponding to the specified honor value.
     * Searches through the existing mappings to find a match and returns it if found.
     *
     * @param honor the honor value to search for in the honor mappings
     * @return an {@code Optional} containing the corresponding {@code HonorMapping} if a match exists,
     *         or an empty {@code Optional} if no match is found
     */
    private Optional<HonorMapping> getMapped(int honor) {
        return mappings.stream().filter(mapping -> mapping.getHonor() == honor).findFirst();
    }

    /**
     * Retrieves the highest honor level from the current configuration.
     *
     * @return the highest honor level as an integer
     */
    public int getTopLevel() {
        return highest;
    }
}
