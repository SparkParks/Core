package network.palace.core.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

/**
 * Utility class providing miscellaneous helper methods for common operations such as string manipulation,
 * numeric checks, JSON handling, and more.
 */
public class MiscUtil {
    /**
     * A static map that defines directional yaw values for each cardinal direction.
     *
     * This map associates cardinal directions represented by {@link BlockFace} with
     * corresponding yaw angles (in degrees). Yaw angles are used to represent the
     * rotational orientation of an object in a virtual 3D space.
     *
     * Mappings:
     * - {@code BlockFace.NORTH} -> 180F
     * - {@code BlockFace.EAST}  -> -90F
     * - {@code BlockFace.SOUTH} -> 0F
     * - {@code BlockFace.WEST}  -> 90F
     *
     * This structure is useful for operations needing a quick directional-to-yaw
     * angle mapping, such as handling rotations or orientations in game worlds.
     */
    public static final HashMap<BlockFace, Float> DIRECTIONAL_YAW = new HashMap<BlockFace, Float>() {{
        put(BlockFace.NORTH, 180F);
        put(BlockFace.EAST, -90F);
        put(BlockFace.SOUTH, 0F);
        put(BlockFace.WEST, 90F);
    }};

    /**
     * Checks if the given string can be parsed as an integer.
     *
     * @param toCheck the string to check for integer compatibility
     * @return true if the string can be parsed as an integer; false otherwise
     */
    public static boolean checkIfInt(String toCheck) {
        try {
            Integer.parseInt(toCheck);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the given string can be parsed as a float.
     *
     * @param toCheck the string to check for float compatibility
     * @return true if the string can be parsed as a float; false otherwise
     */
    public static boolean checkIfFloat(String toCheck) {
        try {
            Float.parseFloat(toCheck);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the given string can be parsed as a double.
     *
     * @param toCheck the string to check for double compatibility
     * @return true if the string can be parsed as a double; false otherwise
     */
    public static boolean checkIfDouble(String toCheck) {
        try {
            Double.parseDouble(toCheck);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Capitalizes the first letter of the given string.
     *
     * @param input the string whose first letter needs to be capitalized
     * @return a new string with the first letter capitalized. If the input is null or empty, behavior may be undefined.
     */
    public static String capitalizeFirstLetter(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * Formats the given integer into a locale-specific string representation.
     * The formatting follows U.S. locale standards, including the use of commas
     * as thousands separators.
     *
     * @param i the integer to format
     * @return a string representation of the formatted integer
     */
    public static String formatNumber(int i) {
        return NumberFormat.getNumberInstance(Locale.US).format(i);
    }

    /**
     * Checks whether a specified element exists within an array.
     *
     * @param <T> the type of elements in the array
     * @param ts the array to check for the presence of the element; can be null
     * @param t the element to search for in the array; can be null
     * @return true if the element is found in the array; false otherwise
     */
    public static <T> boolean contains(T[] ts, T t) {
        if (t == null || ts == null) return false;
        for (T t1 : ts) {
            if (t1 == null) continue;
            if (t1.equals(t)) return true;
        }
        return false;
    }

    /**
     * Reads a JSON object from the specified URL.
     *
     * This method performs a network request to fetch the JSON data from the given URL,
     * parses the JSON, and returns it as a JsonObject. If an error occurs during the
     * operation, such as a network issue or invalid JSON, the method will
     * return null and print the stack trace of the exception.
     *
     * @param url the URL pointing to the JSON resource to be read
     * @return a JsonObject representing the JSON data retrieved from the given URL,
     *         or null if an error occurs during the retrieval or parsing process
     */
    public static JsonObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JsonParser parser = new JsonParser();
            return (JsonObject) parser.parse(jsonText);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads all characters from the given Reader and returns them as a single String.
     *
     * @param rd the Reader from which characters are to be read
     * @return a String containing all the characters read from the Reader
     * @throws IOException if an I/O error occurs while reading from the Reader
     */
    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     * Constructs a Location object based on the data provided in the given JsonObject.
     * The JsonObject should contain the following keys with appropriate data types:
     * "world" (String), "x" (double), "y" (double), "z" (double), "yaw" (float), and "pitch" (float).
     *
     * If the JsonObject is null, the method will return null.
     *
     * @param object the JsonObject containing location data; can be null
     * @return a Location object representing the specified coordinates and world,
     *         or null if the input JsonObject is null
     */
    public static Location getLocation(JsonObject object) {
        if (object == null) return null;
        return new Location(Bukkit.getWorld(object.get("world").getAsString()), object.get("x").getAsDouble(),
                object.get("y").getAsDouble(), object.get("z").getAsDouble(), object.get("yaw").getAsFloat(), object.get("pitch").getAsFloat());
    }

    /**
     * Converts a Location object into a JsonObject representation. The resulting
     * JsonObject contains the following keys: "x", "y", "z", "yaw", "pitch", and "world".
     * If the provided Location object is null, an empty JsonObject is returned.
     *
     * @param location the Location object to be converted to a JsonObject; can be null
     * @return a JsonObject containing the data from the Location object, or an empty JsonObject if the input is null
     */
    public static JsonObject getJson(Location location) {
        JsonObject object = new JsonObject();
        if (location != null) {
            object.addProperty("x", location.getX());
            object.addProperty("y", location.getY());
            object.addProperty("z", location.getZ());
            object.addProperty("yaw", location.getYaw());
            object.addProperty("pitch", location.getPitch());
            object.addProperty("world", location.getWorld().getName());
        }
        return object;
    }
}
