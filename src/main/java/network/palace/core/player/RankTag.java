package network.palace.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.List;

/**
 * <p>
 * The {@code RankTag} is an enumeration that defines various rank tags
 * associated with user roles, sponsor tiers, and special identifications in the application.
 * Each rank tag has a name, a tag for display, a short scoreboard representation, a color, and an ID.
 * </p>
 *
 * <p>
 * This enum is designed to handle rank representations in various formats,
 * such as chat formatting or scoreboard suffixes, based on their attributes.
 * It includes utility methods for processing and displaying multiple ranks effectively.
 * </p>
 *
 * <h3>Enum Constants:</h3>
 * <ul>
 *   <li><b>JRDEV</b>: Represents a Junior Developer in the Developer Team.</li>
 *   <li><b>DESIGNER</b>: Represents a Resource Pack Designer in the Media Team.</li>
 *   <li><b>GUIDE</b>: Represents a member of the Guide Team.</li>
 *   <li><b>SPONSOR_OBSIDIAN</b>, <b>SPONSOR_EMERALD</b>, <b>SPONSOR_DIAMOND</b>, <b>SPONSOR_LAPIS</b>, <b>SPONSOR_GOLD</b>, <b>SPONSOR_IRON</b>: Represent different tier levels of
 *  sponsors with unique identifiers and colors.</li>
 *   <li><b>CREATOR</b>: Represents a Creator user role.</li>
 *   <li><b>NONE</b>: Represents the absence of any tag.</li>
 * </ul>
 *
 * <h3>Key Functionalities:</h3>
 * <ul>
 *   <li>Retrieve properties like name, color, tag, and ID.</li>
 *   <li>Generate standardized formats for chat, scoreboard suffixes, and sidebar displays.</li>
 *   <li>Search for a specific rank tag by its string representation.</li>
 * </ul>
 *
 * <h3>Methods:</h3>
 * <ul>
 *   <li><b>getTag:</b> Formats the full tag with proper color and styling for display.</li>
 *   <li><b>getShortTag:</b> Provides the short representation of the tag.</li>
 *   <li><b>fromString:</b> Finds a rank tag from its string representation. Returns {@code null} if not found.</li>
 *   <li><b>getDBName:</b> Returns a lowercase representation of the tag name, often used for database purposes.</li>
 *   <li><b>getScoreboardTag:</b> Generates a formatted scoreboard tag for display.</li>
 *   <li><b>formatChat:</b> Combines multiple rank tags into a formatted string for chat representation.</li>
 *   <li><b>formatScoreboardSuffix:</b> Creates a formatted tag for scoreboard suffix based on the highest-ranked tag.</li>
 *   <li><b>formatScoreboardSidebar:</b> Formats and concatenates rank tags for display in the scoreboard sidebar.</li>
 * </ul>
 *
 * <h3>Customization:</h3>
 * <p>
 * Each rank tag is associated with customizable properties, such as {@link ChatColor} for coloring
 * and unique identifiers to define their order or precedence.
 * </p>
 */
@AllArgsConstructor
public enum RankTag {

    /**
     * Represents the "Jr. Developer Program" rank within the ranking system.
     *
     * <p>This rank is distinguished by the following attributes:</p>
     * <ul>
     *   <li>Name: "Jr. Developer Program".</li>
     *   <li>Tag: "J".</li>
     *   <li>Short Scoreboard Tag: "j".</li>
     *   <li>Color: {@link ChatColor#DARK_RED}.</li>
     *   <li>ID: 9.</li>
     * </ul>
     *
     * <p>The "JRDEV" rank is specifically used for junior developers associated with the program,
     * offering a unique tag representation and visuals for identification in the system.</p>
     */
    JRDEV("Jr. Developer Program", "J", "j", ChatColor.DARK_RED, 9),

    /**
     * Represents the "DESIGNER" rank tag within the {@code RankTag} enumeration.
     *
     * <p>This rank tag is associated with the title "Resource Pack Designer" and is stylized in blue using {@link ChatColor#BLUE}.
     * It is often used to identify individuals specializing in resource pack design within the system.</p>
     *
     * <p>Key features of this rank tag include:</p>
     * <ul>
     *   <li>Name: "Resource Pack Designer" - A descriptive identifier for the rank.</li>
     *   <li>Tag: "D" - The full tag representation used in tag-related contexts.</li>
     *   <li>Short Scoreboard Tag: "d" - A compact version of the tag used in concise UI displays or scoreboards.</li>
     *   <li>Color: {@link ChatColor#BLUE} - The specific color associated with this rank for visual distinction.</li>
     *   <li>ID: 8 - A unique numeric identifier for order or comparison purposes.</li>
     * </ul>
     *
     * <p>Functions leveraging this rank provide a range of visual and structured representations, including formatted tags
     * for user interfaces and scoreboards. This rank is especially significant in scenarios emphasizing resource pack design roles.</p>
     */
    DESIGNER("Resource Pack Designer", "D", "d", ChatColor.BLUE, 8),

    /**
     * Represents the "Guide" rank tag within the system.
     *
     * <p>The "Guide" rank is associated with members of the Guide Team.
     * It is defined by specific characteristics such as its name, tag,
     * short scoreboard tag, color, and identifier.</p>
     *
     * <p>Attributes of the Guide rank:</p>
     * <ul>
     *   <li><b>Name:</b> "Guide Team" - The descriptive name representing this rank.</li>
     *   <li><b>Tag:</b> "G" - The primary tag used for system representation.</li>
     *   <li><b>Short Tag:</b> "g" - A compact version of the tag for use in minimalistic displays.</li>
     *   <li><b>Color:</b> {@link ChatColor#DARK_GREEN} - The assigned color associated with the rank for visual representation in text formatting.</li>
     *   <li><b>ID:</b> 7 - A unique numeric identifier for internal ranking and sorting purposes.</li>
     * </ul>
     *
     * <p>This rank is typically used to provide members of the Guide Team with
     * distinct and recognizable identification across platforms, enabling clearer
     * role differentiation and improved user experience.</p>
     */
    GUIDE("Guide Team", "G", "g", ChatColor.DARK_GREEN, 7),

    /**
     * Represents the "Obsidian Tier Sponsor" rank in the system.
     *
     * <p>The {@code SPONSOR_OBSIDIAN} rank is associated with a high-level sponsorship tier, offering
     * a distinct identification within the hierarchy of available ranks. This rank is recognized
     * visually by a specific color and tag representation and is assigned a unique identifier.</p>
     *
     * <p>Key Features:</p>
     * <ul>
     *   <li>Name: "Obsidian Tier Sponsor" - The display name of this rank.</li>
     *   <li>Short Tag: "s_o" - A compact representation for scoreboard or UI purposes.</li>
     *   <li>Color: {@link ChatColor#DARK_PURPLE} - Used for text representation in interfaces.</li>
     *   <li>ID: 6 - Unique identifier for ordering or categorizing ranks.</li>
     * </ul>
     */
    SPONSOR_OBSIDIAN("Obsidian Tier Sponsor", "S", "s_o", ChatColor.DARK_PURPLE, 6),

    /**
     * Represents the "Emerald Tier Sponsor" rank within the ranking system.
     *
     * <p>This rank is associated with a green color theme and holds a higher-tier
     * sponsorship level designation. It provides an identifiable tag "S" for shorthand
     * uses and "s_e" for a short-form scoreboard representation. With an ID value of 5,
     * this rank is positioned among high-tier sponsorship levels within the system.</p>
     *
     * <ul>
     *   <li><b>Name</b>: Emerald Tier Sponsor</li>
     *   <li><b>Tag</b>: "S"</li>
     *   <li><b>Short Tag</b>: "s_e"</li>
     *   <li><b>Color</b>: {@link ChatColor#GREEN}</li>
     *   <li><b>ID</b>: 5</li>
     * </ul>
     */
    SPONSOR_EMERALD("Emerald Tier Sponsor", "S", "s_e", ChatColor.GREEN, 5),

    /**
     * Represents the "Diamond Tier Sponsor" rank within the application.
     *
     * <p>The {@code SPONSOR_DIAMOND} rank is associated with the highest level of sponsorship status,
     * offering premium recognition and benefits. It features a distinct color and formatting for display
     * in chat, scoreboards, and other rank-based UI components.</p>
     *
     * <ul>
     *   <li><b>Name:</b> "Diamond Tier Sponsor" - Descriptive full name for the rank.</li>
     *   <li><b>Tag:</b> "S" - Abbreviated tag for concise rank representation.</li>
     *   <li><b>Short Scoreboard Tag:</b> "s_d" - A short-form version of the rank tag used in limited-space contexts such as scoreboards.</li>
     *   <li><b>Color:</b> {@link ChatColor#AQUA} - The visual color styling applied to the rank when displayed.</li>
     *   <li><b>ID:</b> 4 - Unique identifier for the rank, used to determine its position relative to other ranks.</li>
     * </ul>
     */
    SPONSOR_DIAMOND("Diamond Tier Sponsor", "S", "s_d", ChatColor.AQUA, 4),

    /**
     * Represents the "Lapis Tier Sponsor" rank within the enumeration of rank tiers.
     *
     * <p>This rank is characterized by the following attributes:</p>
     * <ul>
     *   <li><b>Name:</b> "Lapis Tier Sponsor"</li>
     *   <li><b>Short Scoreboard Tag:</b> "s_l"</li>
     *   <li><b>Color:</b> {@link ChatColor#BLUE} - Indicates the use of a blue coloring scheme for display purposes.</li>
     *   <li><b>ID:</b> 3 - Used to determine the hierarchy or sorting of rank tiers within the system.</li>
     * </ul>
     *
     * <p>The "Lapis Tier Sponsor" rank is generally associated with a lower-mid tier sponsorship level within the hierarchy of sponsorship ranks. It is visually recognized by its
     *  blue-styled formatting and concise tag representation. This rank can be used in various contexts, including UI displays, scoreboards, and formatted messages.</p>
     */
    SPONSOR_LAPIS("Lapis Tier Sponsor", "S", "s_l", ChatColor.BLUE, 3),

    /**
     * Represents the "Gold Tier Sponsor" rank within the application.
     *
     * <p>The {@code SPONSOR_GOLD} rank signifies a gold-level sponsorship, typically associated with
     * premium contributions or support. The rank includes the following specific attributes:
     * </p>
     *
     * <ul>
     *   <li><b>Name:</b> "Gold Tier Sponsor" - A descriptive name for the rank.</li>
     *   <li><b>Tag:</b> "S" - A concise tag representation.</li>
     *   <li><b>Short Scoreboard Tag:</b> "s_g" - A shortened tag for use in scoreboard displays.</li>
     *   <li><b>Color:</b> {@link ChatColor#YELLOW} - A visual color representation for the rank.</li>
     *   <li><b>ID:</b> 2 - An identifier for ordering and comparison.</li>
     * </ul>
     *
     * <p>This rank is designed for use in contexts where user roles, permissions, or hierarchy
     * are displayed, such as in chat, scoreboards, or UI elements.</p>
     */
    SPONSOR_GOLD("Gold Tier Sponsor", "S", "s_g", ChatColor.YELLOW, 2),

    /**
     * Represents the "Iron Tier Sponsor" rank within the system hierarchy.
     *
     * <p>The {@code SPONSOR_IRON} rank is associated with specific properties that define
     * its visual representation and unique identity. This rank typically signifies lightweight
     * sponsorship support and is styled accordingly for use in UI, chat, and other relevant contexts.
     * </p>
     *
     * <p>Key Properties:</p>
     * <ul>
     *   <li><b>Name:</b> "Iron Tier Sponsor" - The full descriptive name of the rank.</li>
     *   <li><b>Tag:</b> "S" - The short form of the rank tag, used for compact representations.</li>
     *   <li><b>Short Scoreboard Tag:</b> "s_i" - A concise identifier for scoreboard usage.</li>
     *   <li><b>Color:</b> {@link ChatColor#GRAY} - The color code used to visually represent the rank.</li>
     *   <li><b>ID:</b> 1 - The unique identifier for this rank within the enum definition.</li>
     * </ul>
     *
     * <p>This rank is used across various contexts, such as chat styling, scoreboard displays,
     * and database storage, providing clear distinction and uniformity in its presentation.</p>
     */
    SPONSOR_IRON("Iron Tier Sponsor", "S", "s_i", ChatColor.GRAY, 1),

    /**
     * Represents the "Creator" rank within the {@code RankTag} enumeration.
     *
     * <p>The "Creator" rank is identified by the following attributes:</p>
     * <ul>
     *   <li><strong>Name:</strong> "Creator"</li>
     *   <li><strong>Short Scoreboard Tag:</strong> "C"</li>
     *   <li><strong>Database-Friendly Tag:</strong> "c"</li>
     *   <li><strong>Color:</strong> {@link ChatColor#BLUE}</li>
     *   <li><strong>ID:</strong> {@code 0}</li>
     * </ul>
     *
     * <p>It is commonly used to represent a user with the "Creator" rank in various
     * game or application contexts, leveraging its attributes for display and functional purposes.</p>
     */
    CREATOR("Creator", "C", "c", ChatColor.BLUE, 0),

    /**
     * Represents a blank or default rank tag with no specific characteristics.
     *
     * <p>The {@code NONE} rank tag is defined as a placeholder or neutral tag, lacking any distinct name,
     * tag, or associated identifier. It serves as a default value, often used when no rank is applicable
     * or defined for a user.</p>
     *
     * <ul>
     *   <li><b>Name:</b> An empty string, indicating no descriptive name is assigned.</li>
     *   <li><b>Tag:</b> An empty string, representing the absence of a formatted tag.</li>
     *   <li><b>Short Scoreboard Tag:</b> An empty string, indicating no compact representation for UI or scoreboard purposes.</li>
     *   <li><b>Color:</b> {@link ChatColor#RESET}, providing a reset or default color for text formatting.</li>
     *   <li><b>ID:</b> {@code -1}, serving as a unique identifier indicating the absence of a valid rank.</li>
     * </ul>
     *
     * <p>The {@code NONE} rank tag is particularly useful in scenarios where functionality requires a fallback
     * or neutral value. Examples include representing users with no rank or initializing values prior to
     * assignment.</p>
     */
    NONE("", "", "", ChatColor.RESET, -1);

    /**
     * The name of the rank tag.
     * <p>
     * This field represents the name associated with a specific rank tag within the application.
     * It serves as a unique identifier or display name for distinguishing different rank tags.
     * <p>
     * <ul>
     *     <li>Each rank tag has a unique name assigned to it.</li>
     *     <li>It is used in methods for operations like retrieving and formatting tags.</li>
     * </ul>
     * <p>
     * This field is used across various methods for functionalities including database interactions,
     * scoreboard formatting, and general tag identification.
     */
    @Getter private String name;

    /**
     * <p>Represents the textual identifier or label associated with a specific rank.</p>
     *
     * <p>This field stores the rank's tag information, which is used for displaying or processing
     * rank-related details in various contexts such as user interfaces, scoreboards, or database entries.</p>
     *
     * <ul>
     *   <li>Accessible through getter methods for retrieval in external operations.</li>
     *   <li>Serves as a key property to distinguish ranks programmatically.</li>
     * </ul>
     */
    private String tag;

    /**
     * <p>Represents the abbreviated version of a rank tag suitable for use in
     * compact display contexts, such as a scoreboard.</p>
     *
     * <p>This string is typically a shorter, more concise alternative to
     * the full scoreboard tag and is designed to ensure clarity in limited
     * space environments.</p>
     */
    @Getter private String shortScoreboardTag;

    /**
     * Represents the color associated with the {@code RankTag}.
     * <p>
     * This variable holds the {@link ChatColor} that is used to visually represent
     * the rank in text-based formats such as chat and scoreboard displays.
     * <p>
     * <b>Key Points:</b>
     * <ul>
     *   <li>Defines the color to be used for the specific rank.</li>
     *   <li>Is used as part of the visual distinction of each rank.</li>
     *   <li>Directly influences how ranks appear in client-side UI elements.</li>
     * </ul>
     */
    @Getter private ChatColor color;

    /**
     * <p>
     * Represents the unique identifier for a specific rank tag.
     * This field serves as a primary key or reference to differentiate
     * between various rank tags within the application context.
     * </p>
     *
     * <ul>
     * <li>Each rank tag is assigned a unique identifier.</li>
     * <li>Used for database storage, retrieval, and operations involving rank tags.</li>
     * </ul>
     */
    @Getter private int id;

    /**
     * Retrieves the short-form tag representation of the rank.
     *
     * <p>
     * This method provides a compact version of the rank's tag,
     * which can be used in contexts where brevity is required,
     * such as UI displays or summary information.
     * </p>
     *
     * @return A {@link String} containing the short-form tag associated with the rank.
     */
    public String getShortTag() {
        return tag;
    }

    /**
     * Constructs and returns the formatted tag representation associated with this rank.
     * <p>
     * The tag is styled using a combination of the rank's color and bold formatting,
     * encapsulated within square brackets. This provides a clear visual distinction for the rank.
     *
     * @return A formatted String representing the rank's tag.
     *         <ul>
     *           <li>The tag is styled with {@code ChatColor.WHITE} before and after the rank's representation.</li>
     *           <li>The rank's name is styled using the specific {@code color} field and bold formatting {@code ChatColor.BOLD}.</li>
     *           <li>The final format will appear as "[<color><bold>tagName</bold><white>]".</li>
     *         </ul>
     */
    public String getTag() {
        return ChatColor.WHITE + "[" + color + ChatColor.BOLD + tag + ChatColor.WHITE + "] ";
    }

    /**
     * Parses a string representation to match an existing {@code RankTag} enum constant.
     *
     * <p>This method attempts to find and return a {@code RankTag} instance that matches the
     * provided string, disregarding case sensitivity. If the provided string is {@code null},
     * empty, or does not correspond to any valid {@code RankTag}, the method returns {@code null}.
     * </p>
     *
     * @param name The string representation of the {@code RankTag} to be matched.
     *             <ul>
     *               <li>If {@code null} or empty: no match will be performed, and {@code null} will be returned.</li>
     *               <li>If matching a valid {@code RankTag} name disregarding case sensitivity: the corresponding {@code RankTag} will be returned.</li>
     *               <li>If no match is found: {@code null} will be returned.</li>
     *             </ul>
     *
     * @return The {@code RankTag} instance that matches the given string.
     *         <ul>
     *           <li>If a match is found, this method returns the corresponding {@code RankTag}.</li>
     *           <li>If no matching {@code RankTag} is found, or the input is {@code null} or empty, {@code null} is returned.</li>
     *         </ul>
     */
    public static RankTag fromString(String name) {
        if (name == null || name.isEmpty()) return null;

        for (RankTag tag : RankTag.values()) {
            if (!tag.equals(NONE) && tag.getDBName().equalsIgnoreCase(name)) return tag;
        }
        return null;
    }

    /**
     * Retrieves the database-friendly name of the rank.
     *
     * <p>
     * This method converts the rank's name to lowercase, ensuring consistency
     * and compatibility with database storage or comparison operations.
     * </p>
     *
     * @return A {@link String} containing the lowercased name of the rank.
     */
    public String getDBName() {
        return name().toLowerCase();
    }

    /**
     * Retrieves the formatted scoreboard tag representation for this rank.
     *
     * <p>
     * This method constructs a stylized representation of the rank's tag, intended for use
     * in scoreboard displays. The tag is surrounded by square brackets and formatted using
     * {@code ChatColor.WHITE} for the brackets and the rank's specific {@code color} field
     * for the tag itself.
     * </p>
     *
     * <ul>
     *   <li>The outer brackets are rendered in white using {@code ChatColor.WHITE}.</li>
     *   <li>The internal tag is colored using the rank's designated {@code color} field.</li>
     * </ul>
     *
     * @return A {@link String} containing the formatted scoreboard tag. The format will appear as:
     *         <code>[{@code <color>} tagName {@code <ChatColor.WHITE>}]</code>.
     */
    public String getScoreboardTag() {
        return " " + ChatColor.WHITE + "[" + color + tag + ChatColor.WHITE + "]";
    }

    /**
     * Formats a list of {@link RankTag} objects into a single concatenated string representation.
     *
     * <p>
     * The method arranges the provided list of {@link RankTag} objects in descending order
     * based on their {@code id} field and then concatenates their formatted tags
     * (retrieved using the {@link RankTag#getTag()} method) into a single string.
     * </p>
     *
     * @param tags A {@link List} of {@link RankTag} objects to be formatted.
     *             <ul>
     *                 <li>The list should contain valid {@link RankTag} objects.</li>
     *                 <li>Each {@link RankTag} contributes its formatted tag to the result.</li>
     *                 <li>The list is sorted by the {@code id} field of the {@link RankTag} objects before formatting.</li>
     *             </ul>
     *
     * @return A {@link String} containing the concatenated formatted tags in descending {@code id} order.
     *         <p>The tags are combined into a single string with no separators.</p>
     */
    public static String formatChat(List<RankTag> tags) {
        tags.sort((rankTag, t1) -> t1.id - rankTag.id);
        StringBuilder s = new StringBuilder();
        for (RankTag tag : tags) {
            s.append(tag.getTag());
        }
        return s.toString();
    }

    /**
     * Formats a list of {@link RankTag} objects into a scoreboard suffix string.
     *
     * <p>
     * The method selects the {@link RankTag} with the highest {@code id}
     * from the provided list, applies formatting to it, and returns the formatted
     * string. The formatting includes:
     * </p>
     *
     * <ul>
     *   <li>Encapsulation of the rank tag within square brackets {@code []}.</li>
     *   <li>Usage of {@link ChatColor#WHITE} for the brackets and surrounding text.</li>
     *   <li>The usage of the rank tag's specific {@code color}, retrieved via {@link RankTag#getColor()}.</li>
     *   <li>Bold formatting is applied to the tag using {@link ChatColor#BOLD}.</li>
     * </ul>
     *
     * <p>If the list of {@link RankTag} objects is empty, an empty string is returned.</p>
     *
     * @param tags A {@link List} of {@link RankTag} objects to format.
     *             <ul>
     *               <li>The list may contain one or more {@link RankTag} instances.</li>
     *               <li>If the list is empty, no formatting is performed, and an empty string is returned.</li>
     *               <li>The list is sorted in descending order based on the {@code id} field of each rank.</li>
     *             </ul>
     *
     * @return A formatted {@link String} representing the suffix for the scoreboard.
     *         <ul>
     *           <li>If the list contains at least one {@link RankTag}, the rank with the highest {@code id} is formatted and returned.</li>
     *           <li>If the list is empty, an empty string is returned.</li>
     *         </ul>
     */
    public static String formatScoreboardSuffix(List<RankTag> tags) {
        if (tags.isEmpty()) return "";
        tags.sort((rankTag, t1) -> t1.id - rankTag.id);
        RankTag tag = tags.get(0);
        return ChatColor.WHITE + " [" + tag.getColor() + ChatColor.BOLD + tag.getShortTag() + ChatColor.WHITE + "]";
    }

    /**
     * Formats a list of {@link RankTag} objects into a single string representation suitable for a scoreboard sidebar.
     *
     * <p>
     * This method sorts the provided list of {@link RankTag} objects in descending order based on their {@code id} field.
     * It then concatenates their short-form tags, obtained via {@link RankTag#getShortTag()}, into a single string.
     * Each tag is prefixed by its associated color, retrieved via {@link RankTag#getColor()}, and appended in sorted order.
     * </p>
     *
     * <ul>
     * <li>The sorted order ensures that higher-ranked tags (based on {@code id}) appear first.</li>
     * <li>Tags are separated by a single space in the resulting string.</li>
     * </ul>
     *
     * @param tags A {@link List} of {@link RankTag} objects to be processed.
     *             <ul>
     *               <li>Each {@link RankTag} in the list must have valid {@code id}, {@code color}, and {@code shortTag} fields.</li>
     *               <li>The list should not be {@code null}; however, it can be empty, resulting in an empty string.</li>
     *             </ul>
     *
     * @return A {@link String} containing the formatted tags in descending {@code id} order.
     *         <p>The tags are concatenated into a single string, separated by spaces, and each prefixed by its color.</p>
     */
    public static String formatScoreboardSidebar(List<RankTag> tags) {
        tags.sort((rankTag, t1) -> t1.id - rankTag.id);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            RankTag tag = tags.get(i);
            s.append(tag.getColor()).append(tag.getShortTag());
            if (i <= (tags.size() - 1)) {
                s.append(" ");
            }
        }
        return s.toString();
    }
}
