package network.palace.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.Core;
import org.bukkit.ChatColor;

import java.util.Map;

/**
 * The {@code Rank} enum represents different roles or levels within a system,
 * each associated with specific attributes such as name, colors, permissions,
 * and ranking order.
 *
 * <p>Ranks are used to differentiate between various user roles and their
 * associated privileges, as well as providing a structured hierarchy.
 *
 * <p>The {@code Rank} enum provides methods for:
 * <ul>
 *   <li>Formatting rank names for display.
 *   <li>Retrieving permissions associated with a specific rank.
 *   <li>Obtaining database-friendly names for persistence.
 *   <li>Return styling or prefix/suffix names for scoreboard systems.
 * </ul>
 *
 * <p>Each rank is configured with:
 * <ul>
 *   <li>A {@code name} representing the rank's human-readable identifier.
 *   <li>A {@code scoreboardName} used for scoreboard displays.
 *   <li>A {@code tagColor} used for UI representation of the tag.
 *   <li>A {@code chatColor} used for chat-related customizations.
 *   <li>A boolean flag {@code isOp} to indicate if the rank has elevated privileges.
 *   <li>A rank identifier {@code rankId} to designate the order or hierarchy of the rank.
 * </ul>
 *
 * <p>Usage includes methodologies for comparing ranks, managing scoreboard formatting,
 * and data persistence for the rank system.
 *
 * <p>Key methods include:
 * <ul>
 *   <li>{@link #fromString(String)}: Resolves a string to its matching {@code Rank}.
 *   <li>{@link #getFormattedName()}: Retrieves the rank's name with formatting.
 *   <li>{@link #getPermissions()}: Fetches all permissions granted to the rank.
 *   <li>{@link #getScoreboardTeamName()}: Derives a formatted team name for scoreboards.
 * </ul>
 *
 * <p>Legacy methods remain for compatibility, such as {@code getSqlName()} and
 * {@code getNameWithBrackets()}, but are marked as deprecated.
 */
@AllArgsConstructor
public enum Rank {
    /**
     * Represents the "Owner" rank within the system.
     * <p>
     * The {@code OWNER} rank is associated with the highest level of permissions
     * within the system hierarchy, signified by a unique formatted name, tag color,
     * chat color, and additional related attributes.
     * </p>
     *
     * <ul>
     *   <li><b>Name:</b> "Owner" - the readable name of this rank.</li>
     *   <li><b>Tag Color:</b> {@link ChatColor#RED} - color displayed for the rank's tag.</li>
     *   <li><b>Chat Color:</b> {@link ChatColor#RED} - primary color for text in chat by this rank.</li>
     *   <li><b>Highlight Color:</b> {@link ChatColor#YELLOW} - optional highlight color for emphasis.</li>
     *   <li><b>Operator Status:</b> {@code true} - indicates that this rank has operator-level permissions.</li>
     *   <li><b>Rank ID:</b> 13 - the internal identifier for this rank.</li>
     * </ul>
     */
    OWNER("Owner", ChatColor.RED + "Owner ", ChatColor.RED, ChatColor.YELLOW, true, 13),

    /**
     * Represents the "EXEC" rank within the {@code Rank} enum, signifying the Executive rank.
     * <p>
     * This rank is defined with the following properties:
     * </p>
     * <ul>
     *   <li><b>Name:</b> "Executive"</li>
     *   <li><b>Scoreboard Name:</b> "Director "</li>
     *   <li><b>Tag Color:</b> {@link ChatColor#RED}</li>
     *   <li><b>Chat Color:</b> {@link ChatColor#YELLOW}</li>
     *   <li><b>Operator Privileges:</b> {@code true}, indicating that this rank has administrator permissions.</li>
     *   <li><b>Rank ID:</b> 13</li>
     * </ul>
     */
    EXEC("Executive", ChatColor.RED + "Director ", ChatColor.RED, ChatColor.YELLOW, true, 13),

    /**
     * Represents the "Manager" rank with specific attributes such as name, colors, and permissions.
     *
     * <p>The {@code MANAGER} rank is a part of the enumeration of ranks and is identified by the
     * following characteristics:
     * <ul>
     *   <li><b>Name:</b> "Manager" - The display name associated with this rank.</li>
     *   <li><b>Tag Color:</b> {@code ChatColor.GOLD} - The primary color used for formatting the rank.</li>
     *   <li><b>Chat Formatting:</b> {@code ChatColor.GOLD + "Manager "} - The prefix displayed in chat, combining color and rank name.</li>
     *   <li><b>Chat Color:</b> {@code ChatColor.YELLOW} - The color applied to messages sent by users with this rank.</li>
     *   <li><b>Operator Status:</b> {@code true} - Indicates that this rank has operator-level permissions.</li>
     *   <li><b>Rank ID:</b> {@code 13} - A unique identifier for the rank in the system.</li>
     * </ul>
     *
     * <p>As a management role, this rank typically has elevated permissions and responsibilities within the system.
     */
    MANAGER("Manager", ChatColor.GOLD + "Manager ", ChatColor.GOLD, ChatColor.YELLOW, true, 13),

    /**
     * Represents the "Lead" rank within the ranking system.
     * <p>
     * The "Lead" rank is associated with specific visual and functional properties that define its role
     * in the system. These include a designated color scheme, a display name, and an operational status.
     *
     * <p><b>Key Properties:</b></p>
     * <ul>
     *   <li><b>Rank Name:</b> "Lead".</li>
     *   <li><b>Formatted Name:</b> Includes a green tag color (as indicated by {@code ChatColor.GREEN})
     *       with the prefix "Lead " in styled text.</li>
     *   <li><b>Highlight Colors:</b> Combines {@code ChatColor.DARK_GREEN} and {@code ChatColor.GREEN}
     *       for aesthetic consistency.</li>
     *   <li><b>Operational Status:</b> This rank is marked as operational, indicated by
     *       the {@code isOp} flag set to {@code true}.</li>
     *   <li><b>Rank ID:</b> An internal identifier value of 13.</li>
     * </ul>
     */
    LEAD("Lead", ChatColor.GREEN + "Lead ", ChatColor.DARK_GREEN, ChatColor.GREEN, true, 13),

    /**
     * Represents the "Retired Owner" rank within the system.
     *
     * <p>The RETIRED rank signifies a previous owner who is no longer in an active ownership role,
     * but retains a unique recognition within the rank hierarchy. This rank uses a distinctive
     * color scheme and specific attributes that set it apart from other ranks.</p>
     *
     * <ul>
     *   <li><b>Rank Name:</b> "Retired Owner"</li>
     *   <li><b>Tag Color:</b> {@code ChatColor.DARK_PURPLE}</li>
     *   <li><b>Chat Color:</b> {@code ChatColor.DARK_PURPLE}</li>
     *   <li><b>Scoreboard Name:</b> "Retired Owner "</li>
     *   <li><b>Operational Privileges:</b> {@code true} (indicating privileged access)</li>
     *   <li><b>Rank ID:</b> 13</li>
     * </ul>
     *
     * <p>This rank embodies both legacy status and exclusive recognition, granting limited operational privileges
     * to its members. The specific use cases for this rank may vary depending on the system's implementation
     * and permissions assigned to it.</p>
     */
    RETIRED("Retired Owner", ChatColor.DARK_PURPLE + "Retired Owner ", ChatColor.DARK_PURPLE, ChatColor.DARK_PURPLE, true, 13),

    /**
     * Represents the "Developer" rank within the system.
     *
     * <p>The "Developer" rank holds elevated privileges and is associated
     * with a specific set of attributes for display, permission management,
     * and functionality within the application. These include:
     * <ul>
     *   <li><b>Name:</b> The formal display name of the rank, set as "Developer."</li>
     *   <li><b>Tag Color:</b> The color used to visually represent the rank tag, defined as {@link ChatColor#BLUE}.</li>
     *   <li><b>Chat Color:</b> The color used for chat messages associated with the rank, defined as {@link ChatColor#AQUA}.</li>
     *   <li><b>Operator Status:</b> Indicates that this rank has operator-level permissions, set to {@code true}.</li>
     *   <li><b>Rank ID:</b> A unique identifier for the rank, set to {@code 13} for internal usage.</li>
     * </ul>
     *
     * <p>The "Developer" rank is used for individuals with extended access
     * rights, typically for development and administrative purposes.
     */
    DEVELOPER("Developer", ChatColor.BLUE + "Developer ", ChatColor.BLUE, ChatColor.AQUA, true, 13),

    /**
     * Represents the "Coordinator" rank within the system with predefined attributes such as name, colors,
     * permission levels, and rank ID.
     *
     * <p><b>Main Attributes:</b>
     * <ul>
     *   <li><b>Rank Name:</b> "Coordinator"</li>
     *   <li><b>Tag Color:</b> {@code ChatColor.BLUE}</li>
     *   <li><b>Chat Color:</b> {@code ChatColor.AQUA}</li>
     *   <li><b>Is an Operator:</b> {@code true}</li>
     *   <li><b>Rank ID:</b> {@code 12}</li>
     * </ul>
     *
     * <p>The "Coordinator" rank is assigned distinct visual and functional properties. This rank is
     * visually represented in scoreboard name formatting and tag colors, while also holding permissions
     * defined within the system. Its attributes support both internal operations (e.g., database-friendly
     * identifiers) as well as user-facing components (e.g., formatted display names, chat integration).
     */
    COORDINATOR("Coordinator", ChatColor.BLUE + "Coordinator ", ChatColor.BLUE, ChatColor.AQUA, true, 12),

    /**
     * Represents the "Imagineer" rank within the system.
     * <p>
     * This particular rank is characterized by its distinct formatting, permissions, and display attributes.
     * Below are the attributes specific to this rank:
     * </p>
     * <ul>
     *   <li><b>Rank Name:</b> Imagineer</li>
     *   <li><b>Tag Color:</b> Aqua</li>
     *   <li><b>Chat Color:</b> Aqua</li>
     *   <li><b>Scoreboard Name:</b> Imagineer</li>
     *   <li><b>Has Operator Permissions:</b> Yes</li>
     *   <li><b>Rank ID:</b> 11</li>
     * </ul>
     */
    BUILDER("Imagineer", ChatColor.AQUA + "Imagineer ", ChatColor.AQUA, ChatColor.AQUA, true, 11),

    /**
     * Represents the "Imagineer" rank in the system with specific attributes including
     * the display name, chat color, and other rank-related properties.
     *
     * <p>The "Imagineer" rank is visually represented with the {@link ChatColor#AQUA} color
     * for its name prefix, main tag, and chat color. This rank supports administrative
     * operations as indicated by {@code isOp = true}, and it is identified internally by
     * the rank ID of 11.</p>
     *
     * <p>Settings for the "Imagineer" rank include:</p>
     * <ul>
     *   <li><b>Name:</b> "Imagineer"</li>
     *   <li><b>Tag Color:</b> {@link ChatColor#AQUA}</li>
     *   <li><b>Chat Color:</b> {@link ChatColor#AQUA}</li>
     *   <li><b>Prefix:</b> {@code ChatColor.AQUA + "Imagineer "}</li>
     *   <li><b>Administrative Permissions:</b> Enabled ({@code true})</li>
     *   <li><b>Rank ID:</b> 11</li>
     * </ul>
     */
    IMAGINEER("Imagineer", ChatColor.AQUA + "Imagineer ", ChatColor.AQUA, ChatColor.AQUA, true, 11),

    /**
     * Represents the "MEDIA" rank within the application or system.
     *
     * <p>The MEDIA rank is associated with cast members, providing them with a
     * distinct identity and formatting in various contexts such as chat, scoreboards, and permissions.
     * It is identified by specific attributes including a name, chat formatting color,
     * and identifier in the ranking hierarchy.
     *
     * <ul>
     *   <li><b>Name:</b> "Cast Member"</li>
     *   <li><b>Tag Color:</b> {@link ChatColor#AQUA}</li>
     *   <li><b>Chat Prefix:</b> {@code ChatColor.AQUA + "CM "}</li>
     *   <li><b>Op Status:</b> {@code true}</li>
     *   <li><b>Rank Identifier:</b> {@code 11}</li>
     * </ul>
     *
     * <p>The "MEDIA" rank is visually distinguished by its aqua-colored tag
     * and prefix in chats, allowing users to easily identify individuals holding this rank.
     *
     * <p>This rank is designed to represent individuals in media-related roles or positions
     * and has specific permissions and characteristics within the system.
     */
    MEDIA("Cast Member", ChatColor.AQUA + "CM ", ChatColor.AQUA, ChatColor.AQUA, true, 11),

    /**
     * Represents the "Cast Member" rank in the system.
     * <p>
     * This rank is associated with specific visual and functional properties
     * used throughout the application for categorization, display, and
     * permission handling.
     * </p>
     *
     * <ul>
     *   <li><b>Display Name:</b> Cast Member</li>
     *   <li><b>Tag Color:</b> {@link ChatColor#AQUA}</li>
     *   <li><b>Chat Color:</b> {@link ChatColor#AQUA}</li>
     *   <li><b>Scoreboard Name:</b> "CM "</li>
     *   <li><b>Operator Status:</b> True</li>
     *   <li><b>Rank ID:</b> 11</li>
     * </ul>
     */
    CM("Cast Member", ChatColor.AQUA + "CM ", ChatColor.AQUA, ChatColor.AQUA, true, 11),

    /**
     * Represents the "Trainee Tech" rank within the ranking system.
     * <p>
     * This rank is typically assigned to technical trainees, signifying
     * an entry-level position within the technical department.
     * </p>
     *
     * <ul>
     *   <li><b>Name:</b> Trainee</li>
     *   <li><b>Scoreboard Name:</b> "Trainee " (formatted with color)</li>
     *   <li><b>Tag Color:</b> <code>ChatColor.AQUA</code></li>
     *   <li><b>Chat Color:</b> <code>ChatColor.AQUA</code></li>
     *   <li><b>Operator Status:</b> <code>false</code> (does not grant operator permissions)</li>
     *   <li><b>Rank ID:</b> 10</li>
     * </ul>
     */
    TRAINEETECH("Trainee", ChatColor.AQUA + "Trainee ", ChatColor.AQUA, ChatColor.AQUA, false, 10),

    /**
     * Represents the "Trainee Build" rank within the system.
     * <p>
     * The {@code TRAINEEBUILD} rank is a specific designation associated with trainees involved
     * in building-related activities. This rank is formatted using a distinct aqua-colored
     * prefix and tag for easy identification in various contexts, such as chat and scoreboards.
     * </p>
     * <ul>
     *   <li><b>Display Name:</b> "Trainee"</li>
     *   <li><b>Scoreboard Name:</b> "Trainee "</li>
     *   <li><b>Tag Color:</b> Aqua</li>
     *   <li><b>Chat Color:</b> Aqua</li>
     *   <li><b>Operator Status:</b> {@code false}</li>
     *   <li><b>Rank ID:</b> {@code 10}</li>
     * </ul>
     * This rank is primarily intended for users with trainee-level building roles.
     */
    TRAINEEBUILD("Trainee", ChatColor.AQUA + "Trainee ", ChatColor.AQUA, ChatColor.AQUA, false, 10),

    /**
     * Represents the "Trainee" rank in the system.
     *
     * <p>Key attributes of this rank include:</p>
     * <ul>
     *   <li>Name: "Trainee" – the display name of the rank.</li>
     *   <li>Tag Color: {@link ChatColor#AQUA}, providing a consistent visual style.</li>
     *   <li>Chat Color: {@link ChatColor#AQUA}, indicating the text color used for chat messages.</li>
     *   <li>Operational Flag: {@code false}, signifying whether the rank has administrative permissions.</li>
     *   <li>Rank ID: {@code 9}, defining the unique identifier for this rank.</li>
     * </ul>
     *
     * <p>This rank is designed to represent entry-level participants or new members
     * in a hierarchy, with limited permissions and a unique visual appearance to
     * distinguish it from other ranks.</p>
     */
    TRAINEE("Trainee", ChatColor.AQUA + "Trainee ", ChatColor.AQUA, ChatColor.AQUA, false, 9),

    /**
     * Represents the "Character" rank in the system.
     *
     * <p>This rank is associated with the following attributes:
     * <ul>
     *   <li><b>Rank Name:</b> "Character"</li>
     *   <li><b>Tag Color:</b> Dark Purple, as represented by the appropriate color code</li>
     *   <li><b>Chat Color:</b> Dark Purple, applied to text in communications</li>
     *   <li><b>Operational Status:</b> Non-operator rank (<code>false</code>)</li>
     *   <li><b>Rank ID:</b> 8</li>
     * </ul>
     *
     * <p>The "Character" rank represents a specific role or tier within the system and has its
     * own distinctive visual and functional properties, including a unique identifier and color formatting.
     */
    CHARACTER("Character", ChatColor.DARK_PURPLE + "Character ", ChatColor.DARK_PURPLE, ChatColor.DARK_PURPLE, false, 8),

    /**
     * Represents the "Influencer" rank within the system.
     * <p>
     * This rank designation is intended for individuals who are recognized as influencers.
     * It includes the corresponding display name, tag color, chat color, rank permissions,
     * and an associated rank identifier.
     * </p>
     *
     * <p><b>Attributes:</b></p>
     * <ul>
     *   <li><b>Name:</b> "Influencer" - The human-readable name of the rank.</li>
     *   <li><b>Tag Color:</b> {@code ChatColor.DARK_PURPLE} - The color applied to
     *   the rank’s tag in various interfaces.</li>
     *   <li><b>Chat Prefix:</b> {@code ChatColor.DARK_PURPLE + "Influencer "} - The prefix
     *   displayed before any chat message sent by users with this rank.</li>
     *   <li><b>Chat Color:</b> {@code ChatColor.WHITE} - The color used for the text
     *   of chat messages sent by users with this rank.</li>
     *   <li><b>Operator Permissions:</b> {@code false} - Indicates whether users
     *   holding this rank should have operator-level permissions.</li>
     *   <li><b>Rank ID:</b> {@code 7} - The unique identifier associated with this rank
     *   within the system.</li>
     * </ul>
     */
    INFLUENCER("Influencer", ChatColor.DARK_PURPLE + "Influencer ", ChatColor.DARK_PURPLE, ChatColor.WHITE, false, 7),

    /**
     * Represents the "VIP" rank within the application.
     *
     * <p>This rank is associated with a special name tag, text color, and associated attributes.
     * It is designed to represent VIP-level privileges and status within the system. This rank
     * does not have operator permissions and is assigned a unique rank ID for internal handling.</p>
     *
     * <p><b>Attributes:</b></p>
     * <ul>
     *   <li><b>Display Name:</b> "VIP"</li>
     *   <li><b>Tag Color:</b> Dark Purple</li>
     *   <li><b>Chat Color:</b> Dark Purple with white text</li>
     *   <li><b>Operator Permissions:</b> Not enabled</li>
     *   <li><b>Rank ID:</b> 7</li>
     * </ul>
     *
     * <p>This rank is visually identified using the tag color and integrated with the system's
     * scoreboard capabilities through its scoreboard name (based on its attributes). It can also
     * be formatted for UI displays and database-friendly operations.</p>
     */
    VIP("VIP", ChatColor.DARK_PURPLE + "VIP ", ChatColor.DARK_PURPLE, ChatColor.WHITE, false, 7),

    /**
     * Represents the "Shareholder" rank in the system.
     *
     * <p>This rank is defined with specific attributes including display name, tag color,
     * chat color, operational permissions, and rank identification number. The rank is
     * primarily identified by its name, "Shareholder," which is displayed with a distinct
     * {@link ChatColor#LIGHT_PURPLE} color theme.
     *
     * <p><b>Attributes:</b>
     * <ul>
     *   <li><b>Name:</b> "Shareholder" - The descriptive title of the rank.</li>
     *   <li><b>Tag Color:</b> {@link ChatColor#LIGHT_PURPLE} - The color used for displaying the rank in different contexts.</li>
     *   <li><b>Chat Color:</b> {@link ChatColor#WHITE} - The color used for the rank's associated text in chat.</li>
     *   <li><b>Operational Privileges:</b> <code>false</code> - Indicates that this rank does not have operational permissions enabled.</li>
     *   <li><b>Rank ID:</b> 6 - A unique identifier for the rank.</li>
     * </ul>
     *
     * <p>The "Shareholder" rank is designed for individuals who hold a unique position or role in
     * the system. While it does not grant operational privileges, it has a distinct presence
     * through its styling and rank ID.
     */
    SHAREHOLDER("Shareholder", ChatColor.LIGHT_PURPLE + "Shareholder ", ChatColor.LIGHT_PURPLE, ChatColor.WHITE, false, 6),

    /**
     * Represents the "Club" rank within the application, identified as "Club 33."
     * <p>
     * This rank holds a unique identifier and appears under the "C33" tag colored
     * in dark red for emphasis. Additionally, the rank name is displayed using
     * dark red and white as its primary and secondary colors, respectively. It is
     * configured as a non-operator ({@code isOp == false}) with a rank ID of 5.
     * </p>
     * <p>
     * This rank is typically utilized for a specific category of privileged users,
     * potentially related to a closed and exclusive group.
     * </p>
     * <ul>
     * <li><b>Name:</b> "Club 33"</li>
     * <li><b>Tag:</b> "C33 "</li>
     * <li><b>Primary Color:</b> {@code ChatColor.DARK_RED}</li>
     * <li><b>Secondary Color:</b> {@code ChatColor.WHITE}</li>
     * <li><b>Operator Privileges:</b> {@code false}</li>
     * <li><b>Rank ID:</b> 5</li>
     * </ul>
     */
    CLUB("Club 33", ChatColor.DARK_RED + "C33 ", ChatColor.DARK_RED, ChatColor.WHITE, false, 5),

    /**
     * The {@code DVC} constant represents a specific rank in the system.
     * <p>
     * This rank features a distinct visual and functional representation with the following properties:
     * </p>
     * <ul>
     *   <li><b>Name:</b> "DVC"</li>
     *   <li><b>Tag Color:</b> {@code ChatColor.GOLD}</li>
     *   <li><b>Prefix:</b> {@code ChatColor.GOLD + "DVC "}</li>
     *   <li><b>Chat Color:</b> {@code ChatColor.WHITE}</li>
     *   <li><b>Operator Status:</b> Non-operator ({@code false})</li>
     *   <li><b>Rank ID:</b> 4</li>
     * </ul>
     * <p>
     * These attributes define the visual and access-related configurations associated with the {@code DVC} rank.
     * </p>
     */
    DVC("DVC", ChatColor.GOLD + "DVC ", ChatColor.GOLD, ChatColor.WHITE, false, 4),

    /**
     * Represents the "Premier Passport" rank within the system, indicating a special
     * membership tier with distinct characteristics and privileges.
     *
     * <p>The "Premier Passport" rank is identified by the following attributes:
     * <ul>
     *     <li><b>Name:</b> "Premier Passport".</li>
     *     <li><b>Display Name:</b> Appears as "Premier " with a yellow tag color for emphasis.</li>
     *     <li><b>Tag Color:</b> Yellow, used to visually signify the rank in user interfaces.</li>
     *     <li><b>Chat Color:</b> White, utilized for messaging purposes.</li>
     *     <li><b>Operator Status:</b> {@code false}, indicating that this rank does not possess
     *     operator-level permissions.</li>
     *     <li><b>Rank ID:</b> 3, serving as the internal identifier for this rank.</li>
     * </ul>
     *
     * <p>This rank's formatting and privileges are handled explicitly within the functionality
     * of the associated {@link Rank} class. The tag color and name formatting ensure
     * distinct representation across various contexts, such as scoreboards and chat.
     */
    PASSPORT("Premier Passport", ChatColor.YELLOW + "Premier ", ChatColor.YELLOW, ChatColor.WHITE, false, 3),

    /**
     * Represents the "Passholder" rank within the system.
     *
     * <p>This rank is associated with specific attributes such as a display name,
     * tag color, and chat color, which govern its representation across the application.
     * The "Passholder" rank is defined with a priority level and does not grant
     * operator permissions.
     * </p>
     *
     * <ul>
     *   <li><b>Display Name:</b> "Passholder" - The name assigned to this rank.</li>
     *   <li><b>Tag Color:</b> ChatColor.DARK_AQUA - The color used for the tag representation of the rank.</li>
     *   <li><b>Chat Color:</b> ChatColor.WHITE - The color assigned to text messages from this rank in chat.</li>
     *   <li><b>Operator Permissions:</b> {@code false} - This rank does not grant operator privileges.</li>
     *   <li><b>Rank Priority:</b> {@code 2} - Represents the rank's priority level relative to other ranks.</li>
     * </ul>
     *
     * <p>The "Passholder" rank is a mid-tier rank intended for individuals who hold a pass,
     * allowing them access to certain privileges or roles within the system. Its characteristics
     * emphasize its role without granting elevated permissions like higher-tier ranks.</p>
     */
    PASSHOLDER("Passholder", ChatColor.DARK_AQUA + "Passholder ", ChatColor.DARK_AQUA, ChatColor.WHITE, false, 2),

    /**
     * Represents the "Guest" rank in the system, typically assigned to users without
     * any specialized permissions or elevated privileges.
     *
     * <p>The {@code GUEST} rank is characterized by the following attributes:
     * <ul>
     *   <li><b>Name:</b> "Guest"</li>
     *   <li><b>Tag Color:</b> Gray</li>
     *   <li><b>Chat Color:</b> Gray</li>
     *   <li><b>Operational Status:</b> Non-operator</li>
     *   <li><b>Rank ID:</b> 1 (default and minimum rank ID)</li>
     * </ul>
     *
     * <p>Users with the {@code GUEST} rank have limited or no special functionality
     * within the system, often serving as the default rank for unauthenticated or
     * basic-level users.
     */
    GUEST("Guest", ChatColor.GRAY + "", ChatColor.GRAY, ChatColor.GRAY, false, 1);

    /**
     * <p>Defines the English alphabet as an array of lowercase characters from 'a' to 'z'.</p>
     *
     * <p>This array can be used in various circumstances where an ordered list of alphabetic characters
     * is necessary, such as for indexing, character processing, or validation purposes.</p>
     *
     * <p>Key characteristics:</p>
     * <ul>
     *   <li>Immutable: The array is declared as {@code final}, ensuring the array reference cannot be
     *       reassigned.</li>
     *   <li>Shared across all instances: Being declared as {@code static}, the array is available at the
     *       class level and shared among all instances.</li>
     *   <li>Contains 26 characters: Covers only lowercase English alphabet letters.</li>
     * </ul>
     */
    private static final char[] alphabet = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    /**
     * <p>The name of the rank.</p>
     *
     * <p>This field represents the basic name identifier associated with a specific rank.
     * It is typically used to refer to, categorize, or identify a rank in various contexts
     * such as permissions, scoreboard display, or rank-based logic.</p>
     *
     * <p><b>Note:</b> This may correspond to the internal or external representation of the rank's name.</p>
     */
    @Getter private String name;

    /**
     * Represents the scoreboard name associated with a rank.
     * <p>
     * This value is used for displaying or managing rank-related information
     * within a scoreboard context.
     *
     * <p><b>Key Details:</b></p>
     * <ul>
     *     <li>Serves as the unique name for identifying ranks on a scoreboard.</li>
     *     <li>Typically formatted for presentation within in-game interfaces.</li>
     *     <li>Can be accessed or modified to reflect rank-specific representations.</li>
     * </ul>
     */
    @Getter private String scoreboardName;

    /**
     * <p>The color associated with the tag of the rank.</p>
     *
     * <p>This color is used for visually distinguishing players by their rank
     * in various displays such as chat messages or player lists.</p>
     *
     * <p>The {@code tagColor} is typically represented using {@link ChatColor},
     * which provides a predefined set of colors and formatting options.</p>
     */
    @Getter private ChatColor tagColor;

    /**
     * Represents the color associated with the chat for a specific rank.
     * <p>
     * This property determines how messages appear in chat,
     * providing a distinct color for the corresponding rank.
     */
    @Getter private ChatColor chatColor;

    /**
     * Indicates whether the rank represented by this instance has operator permissions.
     *
     * <p>This field is a boolean flag used to determine if the associated rank has special
     * administrative or elevated privileges, typically related to operational or management
     * capabilities.
     *
     * <p>It is primarily used to manage and enforce permissions within the context where
     * this rank is applied. Ranks with this flag set to {@code true} are expected to have
     * additional control, authority, or permissions compared to other ranks.
     *
     * <ul>
     *   <li><b>true</b>: The rank has operator permissions.</li>
     *   <li><b>false</b>: The rank does not have operator permissions.</li>
     * </ul>
     */
    @Getter private boolean isOp;

    /**
     * <p>The unique identifier for the rank.</p>
     *
     * <p>This field holds an integer value that is used to represent and differentiate
     * specific ranks within the system. It is typically used for comparison, persistence,
     * or for associating specific attributes or behaviors with a rank.</p>
     */
    @Getter private int rankId;

    /**
     * Converts the provided string into a {@code Rank} object.
     * <p>
     * This method attempts to match the input string against the database-friendly
     * names of each rank. The comparison is case-insensitive and ignores spaces.
     * If no match is found or if the input is {@code null}, the {@code GUEST} rank is returned.
     *
     * @param name the name of the rank as a {@code String}, which may represent a rank in the system
     *             either in a case-insensitive manner or with spaces included
     * @return the corresponding {@code Rank} object if the name matches a valid rank;
     *         otherwise, the {@code GUEST} rank if no match is found
     */
    public static Rank fromString(String name) {
        if (name == null) return GUEST;
        if (name.equalsIgnoreCase("admin")) return LEAD;
        String rankName = name.replaceAll(" ", "");

        for (Rank rank : Rank.values()) {
            if (rank.getDBName().equalsIgnoreCase(rankName)) return rank;
        }
        return GUEST;
    }

    /**
     * Retrieves the SQL-compatible name of the rank.
     * <p>
     * This method returns a database-friendly representation of the rank's name.
     * It is marked as {@code @Deprecated} and should be replaced with {@link #getDBName()}.
     *
     * @return the SQL-compatible rank name as a {@code String}
     *         derived from the database-friendly format.
     *         This value is generated using {@link #getDBName()}.
     */
    @Deprecated
    public String getSqlName() {
        return getDBName();
    }

    /**
     * Retrieves the database-friendly name of a rank.
     * <p>
     * This method converts the rank's name to a lowercase representation and removes
     * spaces for compatibility with database naming conventions. Specific ranks, such as
     * {@code TRAINEEBUILD} and {@code TRAINEETECH}, are handled as exceptions to this general
     * format, returning their lowercase enum name without any additional transformations.
     *
     * @return the database-compatible name of the rank as a {@code String}.
     */
    public String getDBName() {
        String s;
        switch (this) {
            case TRAINEEBUILD:
            case TRAINEETECH:
                s = name().toLowerCase();
                break;
            default:
                s = name.toLowerCase().replaceAll(" ", "");
        }
        return s;
    }

    /**
     * Retrieves the rank's name encapsulated with brackets.
     * <p>
     * This method delegates to {@code getFormattedName()} to obtain the rank's formatted
     * name and may include additional formatting depending on the underlying implementation.
     *
     * <p><b>Note:</b> This method is deprecated and may be removed in future releases.
     *
     * @return the formatted name of the rank enclosed in brackets, or additional formatting as applicable
     */
    @Deprecated
    public String getNameWithBrackets() {
        return getFormattedName();
    }

    /**
     * Retrieves the formatted name of a rank.
     * <p>
     * This method combines the rank's tag color and name to generate a formatted
     * string representing the rank. If the rank's name is "Premier Passport," it
     * returns a special case formatted as the tag color followed by "Premier."
     * For all other ranks, it returns the tag color concatenated with the rank's name.
     *
     * @return A {@code String} representing the formatted name of the rank, combining
     *         the tag color and rank name or a special case handling for "Premier Passport".
     */
    public String getFormattedName() {
        if (getName() == "Premier Passport") {
            return getTagColor() + "Premier";
        }
        return getTagColor() + getName();
    }

    /**
     * Retrieves the formatted name of the rank for use in a scoreboard.
     * <p>
     * This method combines the rank's tag color with its scoreboard name to produce
     * a formatted string that reflects both its visual and functional representation
     * in the context of a scoreboard.
     * </p>
     *
     * @return the formatted scoreboard name as a {@code String}, which consists
     *         of the rank's tag color followed by its scoreboard name.
     */
    public String getFormattedScoreboardName() {
        return getTagColor() + getScoreboardName();
    }

    /**
     * Retrieves the permissions associated with the current rank.
     * <p>
     * This method provides a map of permission keys to their boolean states,
     * indicating whether each specific permission is granted or denied for the rank.
     * </p>
     *
     * @return a {@code Map<String, Boolean>} where the keys are permission names
     *         and the values indicate whether the permission is granted ({@code true}) or denied ({@code false}).
     */
    public Map<String, Boolean> getPermissions() {
        return Core.getPermissionManager().getPermissions(this);
    }

    /**
     * Retrieves the name of the rank for use in a scoreboard context.
     * <p>
     * This method determines the appropriate scoreboard name for the rank. It uses the
     * ordinal position of the rank to locate a corresponding character from the
     * {@code alphabet}. If the rank's name is "Premier Passport," it appends "Premier"
     * to the character; otherwise, it appends the rank's regular name. If the ordinal
     * position is out of bounds, it returns an empty string.
     *
     * <ul>
     *   <li>If the ordinal position is invalid, the method returns an empty string.</li>
     *   <li>If the rank's name is "Premier Passport," the scoreboard name is
     *   constructed with "Premier" instead of the name.</li>
     *   <li>For other ranks, the character from the {@code alphabet}[ordinal] is
     *   combined with the rank's regular name to form the scoreboard name.</li>
     * </ul>
     *
     * @return the scoreboard name as a {@code String}. If the ordinal is invalid, returns an empty string.
     *         If the rank is "Premier Passport," returns a special name ending with "Premier."
     */
    public String getScoreboardName() {
        int pos = ordinal();
        if (pos < 0 || pos >= alphabet.length) return "";
        if (getName() == "Premier Passport") {
            return String.valueOf(alphabet[pos] + "Premier");
        }
        return String.valueOf(alphabet[pos] + getName());
    }

    /**
     * Constructs the full name of the scoreboard team for the rank.
     * <p>
     * This method combines a prefix determined by the rank's position in the alphabet and a
     * truncated version of the rank's database-friendly name. The truncation ensures that the
     * resulting name does not exceed a certain length while preserving its relevance.
     * </p>
     * <p>
     * For example, the prefix is derived using {@link #getScoreboardPrefix()}, and the
     * database-friendly name is retrieved using {@link #getDBName()} and truncated
     * to a maximum of 10 characters.
     * </p>
     *
     * @return the scoreboard team name as a {@code String}, comprising the prefix
     *         followed by a truncated version of the rank's database-friendly name.
     */
    public String getScoreboardTeamName() {
        return getScoreboardPrefix() + getDBName().substring(0, Math.min(getDBName().length(), 10));
    }

    /**
     * Retrieves the prefix used for scoreboard representation of the rank.
     * <p>
     * This method determines the prefix based on the rank's position in an internal array of
     * alphabetical characters. If the position is invalid (out of bounds), it returns an empty string.
     * </p>
     *
     * @return A {@code String} representing the scoreboard prefix for the rank. This is derived
     *         from its ordinal position mapped to the array of alphabetical characters, or an
     *         empty string if the position is invalid.
     */
    public String getScoreboardPrefix() {
        int pos = ordinal();
        if (pos < 0 || pos >= alphabet.length) return "";
        return String.valueOf(alphabet[pos]);
    }

    /**
     * Retrieves the shortened version of the rank's name.
     *
     * <p>This method returns the first three characters of the rank's name
     * as a {@code String}. It uses the full name obtained via {@code getName()}
     * and extracts a substring from the beginning of the name (index 0) up to
     * but not including index 3. If the rank's name has fewer than three
     * characters, it may throw an exception or behave unexpectedly depending
     * on the implementation of {@code getName()}.
     *
     * @return a {@code String} containing the first three characters of the rank's name.
     */
    public String getShortName() {
        return getName().substring(0, 3);
    }
}
