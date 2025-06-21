package network.palace.core.achievements;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.mongo.MongoHandler;
import network.palace.core.utils.MiscUtil;

import java.util.*;

/**
 * Manages achievements for the system, allowing for retrieval, initialization,
 * and updating of achievements. Provides functionality to reload achievements
 * from a defined external source and tracks achievements earned by users.
 */
public class AchievementManager {
    /**
     * URL pointing to the public JSON feed of a Google Spreadsheet.
     * This feed is used as the external data source for loading and reloading achievements
     * into the AchievementManager. The spreadsheet must have public visibility in order
     * for the data to be accessed.
     */
    private static final String url = "https://spreadsheets.google.com/feeds/cells/14OHnSeMJVmtFnE7xIdCzMaE0GPOR3Sh4SyE-ZR3hQ7o/od6/public/basic?alt=json";

    /**
     * A mapping of achievement IDs to their corresponding {@link CoreAchievement} objects.
     * This map is used to store and manage the collection of achievements within the system.
     * Each achievement is uniquely identified by its integer ID.
     */
    private final Map<Integer, CoreAchievement> achievements = new HashMap<>();

    /**
     * A map that associates a UUID with a list of integers representing
     * the IDs of achievements that have been earned by the respective user.
     * <p>
     * The UUID serves as a unique identifier for a user, and the list of integers
     * tracks the achievements that the user has obtained.
     * <p>
     * This map is used internally by the AchievementManager to manage and store
     * achievement progress for each user.
     */
    private final Map<UUID, List<Integer>> earned = new HashMap<>();

    /**
     * Constructs an instance of the AchievementManager.
     * This constructor initializes the AchievementManager by invoking the init method,
     * setting up necessary asynchronous tasks for reloading achievements and persisting earned achievements.
     */
    public AchievementManager() {
        init();
    }

    /**
     * Initializes the AchievementManager by scheduling asynchronous tasks for achievement management.
     * <p>
     * This method sets up two periodic tasks:
     * 1. A task to reload achievements at a fixed interval.
     * 2. A task to persist earned achievements to the database at a fixed interval.
     * <p>
     * The reload task ensures that achievements are kept updated, while the persistence task ensures
     * that any earned achievements are consistently saved to the database without blocking the main thread.
     */
    private void init() {
        Core.runTaskTimerAsynchronously(Core.getInstance(), this::reload, 0L, 6000L);

        Core.runTaskTimerAsynchronously(Core.getInstance(), () -> {
            MongoHandler handler = Core.getMongoHandler();
            new HashSet<>(earned.entrySet()).forEach(entry -> entry.getValue().forEach(i -> {
                handler.addAchievement(entry.getKey(), i);
            }));
        }, 0L, 100L);
    }

    /**
     * Reloads the achievements data from a configured URL and updates the internal achievement collection.
     * <p>
     * The method fetches JSON data from the specified URL and extracts achievement information.
     * It processes the data into a structured format by reading and parsing the JSON content.
     * <p>
     * The achievements are categorized based on specific prefixes of a column value. The parsed data
     * is used to update or create a new {@link CoreAchievement} instance. The method clears the current
     * achievements collection before reloading the new data.
     * <p>
     * After the achievements are updated, it updates the online players' crafting menus to reflect the
     * new achievements.
     * <p>
     * Note:
     * - The JSON structure is expected to include a "feed" element containing entries with
     *   "content" and "title" values.
     * - Each entry represents an achievement-related piece of information.
     */
    public void reload() {
        JsonObject obj = MiscUtil.readJsonFromUrl(url);
        if (obj == null) return;

        JsonArray array = (JsonArray) ((JsonObject) obj.get("feed")).get("entry");
        achievements.clear();
        CoreAchievement lastAchievement = null;
        for (JsonElement elementArray : array) {
            JsonObject object = (JsonObject) elementArray;
            JsonObject content = object.getAsJsonObject("content");
            JsonObject id = object.getAsJsonObject("title");
            String column = id.get("$t").getAsString();
            switch (column.substring(0, 1).toLowerCase()) {
                case "a":
                    lastAchievement = new CoreAchievement(Integer.parseInt(content.get("$t").getAsString()), null, null);
                    break;
                case "b":
                    lastAchievement.setDisplayName(content.get("$t").getAsString());
                    break;
                case "c":
                    lastAchievement.setDescription(content.get("$t").getAsString());
                    achievements.put(lastAchievement.getId(), lastAchievement);
                    break;
            }
        }
        Core.getPlayerManager().getOnlinePlayers().forEach(player -> Core.getCraftingMenu().update(player));
    }

    /**
     * Retrieves a specific achievement by its unique identifier.
     *
     * @param id the unique identifier of the achievement to be retrieved
     * @return the CoreAchievement instance corresponding to the given id, or null if no achievement with the specified id exists
     */
    public CoreAchievement getAchievement(int id) {
        return achievements.get(id);
    }

    /**
     * Retrieves an immutable list of all achievements managed by the AchievementManager.
     *
     * @return an {@link ImmutableList} containing all {@link CoreAchievement} instances currently managed.
     */
    public ImmutableList<CoreAchievement> getAchievements() {
        return ImmutableList.copyOf(achievements.values());
    }
}