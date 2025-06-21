package network.palace.core.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * The LanguageManager class is responsible for managing and retrieving language translations
 * for different locales. It loads translations from a remote JSON source and makes them
 * available for use in the application.
 */
public class LanguageManager {
    /**
     * The URL to access a public Google Sheets JSON feed.
     * This represents the endpoint for retrieving spreadsheet data in JSON format.
     * The URL points to a specific spreadsheet and sheet, and fetches the cell data.
     * It is used as a static, constant resource in the LanguageManager class.
     */
    private static final String url = "https://spreadsheets.google.com/feeds/cells/1Sy0BswfWXjGybjchn6YGj68fN19cl4P4wtrEF8EkPhw/od6/public/basic?alt=json";

    /**
     * Represents the default language code used by the system if no other language is specified.
     * This value is typically used as a fallback or to ensure consistency in areas where language settings are required.
     * The default language is set to U.S. English ("en_us").
     */
    public static final String DEFAULT_LANG = "en_us";

    /**
     * A mapping of language identifiers to their corresponding translations.
     * The outer HashMap uses a locale code (e.g., "en_US") as the key,
     * while the inner HashMap uses a key for the specific translation string (e.g., "greeting")
     * and its corresponding localized value.
     * <p>
     * This field is used to store and manage all loaded language data for different locales.
     * It works as a central repository for fetching language-specific translations based on
     * the selected locale and key.
     */
    private final HashMap<String, HashMap<String, String>> languages = new HashMap<>();

    /**
     * Constructs a new instance of the LanguageManager.
     * <p>
     * This constructor initializes the LanguageManager by scheduling an asynchronous task
     * that periodically invokes the {@code reload} method. The task runs at a fixed interval
     * starting immediately and then repeats every 6000 ticks.
     */
    public LanguageManager() {
        Core.runTaskTimerAsynchronously(Core.getInstance(), this::reload, 0L, 6000L);
    }

    /**
     * Reloads and processes language data from a JSON source specified by the URL.
     * <p>
     * This method fetches JSON data from the given URL, parses the relevant language
     * information, and organizes it into a structured format for future use. It clears
     * existing language mappings before processing the new data.
     * <p>
     * Steps:
     * 1. Retrieves a JSON object from the URL using {@link MiscUtil#readJsonFromUrl(String)}.
     * 2. Validates and ensures the presence of data in a specific structure:
     *    - Expects a "feed" node and an "entry" array within the JSON object.
     * 3. Iterates through the "entry" array to:
     *    - Parse language headers and mappings.
     *    - Extract and map language keys and values.
     *    - Construct a mapping of languages and their respective translations.
     * 4. Skips invalid or empty data entries encountered during parsing.
     * <p>
     * Data parsing adheres to the format where:
     * - Language keys are identified in the first row.
     * - Subsequent rows define mappings for language translations.
     * <p>
     * This method does not return a value but updates the internal language mappings.
     */
    public void reload() {
        JsonObject obj = MiscUtil.readJsonFromUrl(url);
        if (obj == null) return;

        JsonArray array = (JsonArray) ((JsonObject) obj.get("feed")).get("entry");
        languages.clear();
        HashMap<Integer, String> langs = new HashMap<>();
        String key = "";
        for (Object elementArray : array) {
            JsonObject object = (JsonObject) elementArray;
            JsonObject content = object.getAsJsonObject("content");
            JsonObject id = object.getAsJsonObject("title");
            String column = id.get("$t").getAsString();
            String letter = column.substring(0, 1).toLowerCase();
            int row = Integer.parseInt(column.substring(1));
            String text = content.get("$t").getAsString();
            if (text == null || text.isEmpty()) continue;
            if (row == 1) {
                if (!text.equals("node")) {
                    languages.put(text, new HashMap<>());
                    langs.put(getColumnInt(letter), text);
                }
                continue;
            }
            if (letter.equalsIgnoreCase("a")) {
                key = text;
                continue;
            }
            HashMap<String, String> map = languages.get(langs.get(getColumnInt(letter)));
            map.put(key, text);
        }
    }

    /**
     * Converts the first character of the given string to a zero-based index
     * corresponding to its position in the English alphabet (case insensitive).
     *
     * @param s the input string, where the first character will be used for conversion
     * @return the zero-based index of the first character of the string in the English alphabet,
     *         or a negative value if the character is not a valid alphabet letter
     */
    private int getColumnInt(String s) {
        return s.toLowerCase().charAt(0) - 97;
    }

    /**
     * Retrieves the format string associated with the specified key for the given command sender.
     * If the sender is a player, the player's locale is used; otherwise, the default language is applied.
     *
     * @param sender the command sender, which may be a player or another entity
     * @param key    the key representing the format to retrieve
     * @return the format string corresponding to the key and sender's locale
     */
    public final String getFormat(CommandSender sender, String key) {
        String locale = DEFAULT_LANG;
        if (sender instanceof Player) {
            CPlayer player = Core.getPlayerManager().getPlayer((Player) sender);
            locale = player.getLocale();
        }
        return getFormat(locale, key);
    }

    /**
     * Retrieves the format string associated with the specified key for the given player.
     * The player's locale is used to determine the appropriate format.
     *
     * @param player the player whose locale will be used to fetch the format
     * @param key    the key representing the format to retrieve
     * @return the format string corresponding to the key and player's locale
     */
    public String getFormat(CPlayer player, String key) {
        String locale = player.getLocale();
        return getFormat(locale, key);
    }

    /**
     * Retrieves the format string associated with the specified key for the given locale.
     * If the locale is not available, the default language is used.
     * If the key is not found in the specified locale or the default language, an empty string is returned.
     *
     * @param locale the locale identifier to use when looking for the format (e.g., "en_US")
     * @param key    the key representing the format to retrieve
     * @return the format string corresponding to the key and locale, translated with color codes,
     *         or an empty string if the key is not found in both the given locale and the default language
     */
    public String getFormat(String locale, String key) {
        HashMap<String, String> lang = languages.get(locale);
        if (lang == null) {
            lang = languages.get(DEFAULT_LANG);
        }
        String val = lang.getOrDefault(key, "");
        if (val.isEmpty()) {
            val = languages.get(DEFAULT_LANG).get(key);
        }
        return ChatColor.translateAlternateColorCodes('&', val);
    }
}
