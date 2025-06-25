package network.palace.core.player.impl;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.config.LanguageManager;
import network.palace.core.economy.TransactionCallback;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.events.GameStatisticChangeEvent;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.player.*;
import network.palace.core.player.impl.managers.*;
import network.palace.core.plugin.Plugin;
import network.palace.core.tracking.GameType;
import network.palace.core.tracking.StatisticType;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Represents the core implementation of a player in the system. The {@code CorePlayer}
 * class extends the functionalities of the {@link CPlayer} class, providing additional
 * player-specific attributes and operations.
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *     <li>{@code uuid} - A unique identifier for the player.</li>
 *     <li>{@code name} - The name of the player.</li>
 *     <li>{@code protocolId} - The protocol identification used by the player.</li>
 *     <li>{@code rank} - The rank assigned to the player.</li>
 *     <li>{@code tags} - A list of rank tags associated with the player.</li>
 *     <li>{@code locale} - The locale or language preference of the player.</li>
 *     <li>{@code status} - The current status of the player.</li>
 *     <li>{@code achievementManager} - Manager for the player's achievements.</li>
 *     <li>{@code scoreboard} - The scoreboard displayed to the player.</li>
 *     <li>{@code actionBar} - The action bar currently visible to the player.</li>
 *     <li>{@code bossBar} - The boss bar currently visible to the player.</li>
 *     <li>{@code headerFooter} - The header and footer displayed to the player in the player's tab list.</li>
 *     <li>{@code title} - The title currently displayed to the player.</li>
 *     <li>{@code particles} - The particle effects associated with the player.</li>
 *     <li>{@code resourcePack} - The resource pack used by the player, if any.</li>
 *     <li>{@code registry} - Internal registry for the player data.</li>
 *     <li>{@code textureValue} - The player's texture value (e.g., skin data).</li>
 *     <li>{@code textureSignature} - The signature associated with the player's texture.</li>
 *     <li>{@code pack} - Additional pack information related to the player.</li>
 *     <li>{@code joinTime} - The time when the player joined the server.</li>
 *     <li>{@code queuedAchievements} - The achievements queued for processing.</li>
 *     <li>{@code honor} - The total honor points assigned to the player.</li>
 *     <li>{@code previousHonorLevel} - The player's previous honor level.</li>
 * </ul>
 *
 * <p><b>Methods:</b></p>
 * <ul>
 *     <li>{@code CorePlayer(UUID uuid, String name, Rank rank, List<RankTag> tags, String locale)} - Constructs a new instance of {@code CorePlayer} with the provided identifier
 * , name, rank, tags, and locale.</li>
 *     <li>{@code getName()} - Retrieves the player's name.</li>
 *     <li>{@code isOnline()} - Checks if the player is currently online.</li>
 *     <li>{@code setOp(boolean isOp)} - Grants or revokes operator status for the player.</li>
 *     <li>{@code isOp()} - Determines if the player has operator permissions.</li>
 *     <li>{@code getEntityId()} - Returns the unique entity ID for the player.</li>
 *     <li>{@code playSound(Location location, Sound sound, float volume, float pitch)} - Plays a sound at the specified location for the player.</li>
 *     <li>{@code setMaxHealth(double health)} - Sets the player's maximum health.</li>
 *     <li>{@code setHealth(double health)} - Modifies the player's current health.</li>
 *     <li>{@code getHealth()} - Gets the player's current health.</li>
 *     <li>{@code getMaxHealth()} - Retrieves the player's maximum health.</li>
 *     <li>{@code setFoodLevel(int level)} - Sets the player's food or hunger level.</li>
 *     <li>{@code getFoodLevel()} - Gets the player's current food or hunger level.</li>
 *     <li>{@code setGamemode(GameMode gamemode)} - Changes the player's game mode.</li>
 *     <li>{@code getGamemode()} - Retrieves the current game mode of the player.</li>
 *     <li>{@code teleport(Location location)} - Teleports the player to a specified location.</li>
 *     <li>{@code sendMessage(String message)} - Sends a message to the player.</li>
 *     <li>{@code sendPacket(AbstractPacket packet)} - Sends a custom packet to the player.</li>
 *     <li>{@code addAdventureCoins(int amount, String reason)} - Adds adventure coins to the player along with a reason.</li>
 *     <li>{@code addAchievement(int i)} - Awards the player a specific achievement.</li>
 *     <li>{@code getInventory()} - Retrieves the player's {@link PlayerInventory} instance.</li>
 *     <li>{@code setDisplayName(String name)} - Updates the display name of the player.</li>
 *     <li>{@code setHelmet(ItemStack itemStack)} - Sets the player's helmet slot with the provided item.</li>
 *     <li>{@code setBoots(ItemStack itemStack)} - Sets the player's boots slot with the provided item.</li>
 *     <li>{@code setVelocity(Vector vector)} - Updates the velocity vector of the player.</li>
 *     <li>{@code setAchievementManager(CPlayerAchievementManager manager)} - Assigns a new achievement manager for the player.</li>
 *     <li>{@code addPotionEffect(PotionEffect effect)} - Adds a potion effect to the player.</li>
 *     <li>{@code hasPermission(String node)} - Checks if the player has a specific permission node.</li>
 *     <li>{@code resetPlayer()} - Resets the player's internal state to its default values.</li>
 *     <li>{@code canSee(CPlayer player)} - Determines whether this player can see another player.</li>
 *     <li>{@code addStatistic(GameType gameType, StatisticType type, int amount)} - Updates the player's statistics based on game type and statistic type.</li>
 *     <li>{@code giveHonor(int amount)} - Awards honor points to the player.</li>
 *     <li>{@code removeHonor(int amount)} - Deducts honor points from the player.</li>
 *     <li>{@code getOnlineTime()} - Retrieves the total time the player has been online.</li>
 * </ul>
 *
 * <p>This class provides comprehensive functionality to manage, query, and manipulate
 * the core aspects of a player's game state, actions, and attributes.</p>
 */
public class CorePlayer implements CPlayer {

    /**
     * Represents the unique identifier for a {@link CorePlayer} instance.
     *
     * <p>This UUID is a final and immutable property that uniquely identifies a player.
     * It is typically tied to the underlying player's identity in the system and is used
     * for distinguishing, tracking, and managing individual players.</p>
     *
     * <p>Key characteristics of this property include:</p>
     * <ul>
     *     <li>It is generated and assigned when the {@link CorePlayer} instance is created.</li>
     *     <li>Once assigned, it cannot be changed.</li>
     *     <li>It is used in various methods and data structures for player-related operations.</li>
     * </ul>
     *
     * <p>Technical Context:</p>
     * <ul>
     *     <li>UUID ensures uniqueness across the platform.</li>
     *     <li>It serves as a critical key in player registries, metadata, and server communication layers.</li>
     * </ul>
     */
    @Getter private final UUID uuid;

    /**
     * Represents the name of the CorePlayer.
     * <p>
     * This field stores the display name or username of the player.
     * It is a final field, meaning the value is immutable after initialization.
     * </p>
     */
    private final String name;

    /**
     * Represents the protocol ID associated with the player.
     * <p>
     * This field is primarily used to store the protocol version of the client
     * that the player is using to connect to the server. It can be utilized for
     * compatibility checks or feature toggling based on client versions.
     * </p>
     * <p>
     * The initial value is set to <code>-1</code>, which may indicate an uninitialized
     * or unknown protocol version.
     * </p>
     * <p>
     * Example considerations for usage:
     * <ul>
     *   <li>Detecting specific client versions for feature compatibility.</li>
     *   <li>Preventing unsupported protocol versions from interacting with the server.</li>
     *   <li>Recording analytics for protocol distribution.</li>
     * </ul>
     * </p>
     */
    @Getter @Setter private int protocolId = -1;

    /**
     * Represents the rank of a {@code CorePlayer}.
     *
     * <p>The {@code rank} field defines the level, status, or privileges associated with the player.
     * The rank may contribute to determining access permissions, player roles, or special capabilities
     * within the system.
     *
     * <p>Characteristics of {@code rank} include:
     * <ul>
     *   <li>Defines the player's hierarchical position or role in the system.</li>
     *   <li>Impacts performance or restrictions based on permissions assigned to the rank.</li>
     *   <li>Can be updated or retrieved as required.</li>
     * </ul>
     *
     * <p>The {@code rank} is managed and utilized within the {@code CorePlayer} class and can be integrated
     * with other aspects of the player's attributes, such as achievements or gameplay states.
     */
    @Getter @Setter private Rank rank;

    /**
     * Represents a collection of {@link RankTag} objects associated with a player.
     * <p>
     * This list is used to store and manage the tags that define additional attributes
     * or categorizations associated with the player's rank or status. Tags can be
     * added, removed, or queried as part of the player's metadata.
     * <p>
     * <ul>
     *   <li>Each item in the list must be an instance of {@link RankTag}.</li>
     *   <li>Operations on this list should maintain the integrity of the player's tag data.</li>
     *   <li>Typically used for rank customization or representation purposes.</li>
     * </ul>
     */
    private List<RankTag> tags;

    /**
     * Represents the locale setting for the player.
     * <p>
     * The locale indicates the language or regional preference of the player, which can influence
     * display options such as in-game messages and formatting.
     * </p>
     * <p>
     * Commonly used to customize the user experience based on the player's preferred language.
     * </p>
     *
     * <ul>
     *     <li>Example of formats: <i>en_US</i>, <i>fr_FR</i>, <i>de_DE</i></li>
     *     <li>Used internally for localization and language support within the game.</li>
     * </ul>
     */
    @Getter @Setter private String locale;

    /**
     * Represents the current status of the player within the system.
     *
     * <p>This field is used to track the player's state during their interaction
     * with the platform. It helps in determining the player's lifecycle stage
     * and performing related operations accordingly.</p>
     *
     * <p>Possible values for the status include:</p>
     * <ul>
     *   <li>{@link PlayerStatus#LOGIN} - Indicates the player is in the login state.</li>
     *   <li>{@link PlayerStatus#JOINED} - Indicates the player has joined the game or session.</li>
     *   <li>{@link PlayerStatus#LEFT} - Indicates the player has exited or left the game.</li>
     * </ul>
     *
     * <p>By default, the player's status is initialized to {@link PlayerStatus#LOGIN}.</p>
     */
    @Getter @Setter private PlayerStatus status = PlayerStatus.LOGIN;

    /**
     * <p>Represents the achievement management system for the player.</p>
     *
     * <p>This field allows access to the {@link CPlayerAchievementManager}, which is responsible for
     * handling player achievements within the application. This can include tasks such as
     * checking if a player has specific achievements, granting new achievements, or managing
     * achievement-related data.</p>
     *
     * <ul>
     *   <li>Provides functionality to interact with and modify a player's achievements.</li>
     *   <li>Used to track and update the player's progression with achievements.</li>
     * </ul>
     */
    @Getter private CPlayerAchievementManager achievementManager;

    /**
     * Represents the manager handling scoreboard functionalities for the player.
     * <p>
     * The {@code scoreboard} field is responsible for managing and interacting
     * with the player's scoreboard. It leverages {@link CorePlayerScoreboardManager},
     * which provides the implementation for this functionality.
     * </p>
     *
     * <p>Key responsibilities include:</p>
     * <ul>
     *     <li>Updating and displaying custom scoreboard elements specific to the player.</li>
     *     <li>Managing any dynamic scoreboard updates, such as statistics, text, or visual effects.</li>
     *     <li>Providing integration with other player-related systems that require scoreboard updates.</li>
     * </ul>
     *
     * <p>This field is initialized with a new instance of {@code CorePlayerScoreboardManager},
     * which takes the containing {@code CorePlayer} object as a parameter to handle player-specific
     * logic.</p>
     */
    @Getter @Setter private CPlayerScoreboardManager scoreboard = new CorePlayerScoreboardManager(this);

    /**
     * Manages the action bar for the player. The action bar is a visual text element
     * displayed above the player's hotbar in the game interface.
     * <p>
     * This variable is an instance of {@code CPlayerActionBarManager}, which provides methods
     * to update, customize, and manage the content displayed in the player's action bar.
     * </p>
     * <p>
     * <strong>Purpose:</strong> This manager allows the player to receive contextual messages,
     * notifications, or gameplay-related updates directly in the action bar, ensuring a
     * non-intrusive user experience.
     * </p>
     * <p>
     * Example use cases for the action bar could include:
     * <ul>
     *   <li>Displaying game statistics, such as remaining time or score.</li>
     *   <li>Notifying the player about specific in-game events or actions.</li>
     *   <li>Showing real-time updates about the player's current status.</li>
     * </ul>
     * </p>
     *
     * <p><strong>Instance Details:</strong></p>
     * <ul>
     *   <li>Initialized on player creation, using the {@code CorePlayer} instance.</li>
     *   <li>Operates within the context of the {@link CorePlayer} class.</li>
     * </ul>
     */
    @Getter private CPlayerActionBarManager actionBar = new CorePlayerActionBarManager(this);

    /**
     * Manages the Boss Bar interface for the player.
     *
     * <p>
     * The <code>bossBar</code> variable is an instance of {@link CPlayerBossBarManager},
     * responsible for handling the player's boss bar functionality.
     * </p>
     *
     * <p>
     * This manager allows for customization and control of boss bar displays, ensuring
     * a personalized experience for the player. It may include features such as:
     * </p>
     * <ul>
     *   <li>Dynamic boss bar updates</li>
     *   <li>Custom titles and progress levels</li>
     *   <li>Integration with various game systems</li>
     *   <li>Proper management and cleanup upon player disconnection</li>
     * </ul>
     */
    @Getter private CPlayerBossBarManager bossBar = new CorePlayerBossBarManager(this);

    /**
     * Manages the header and footer display for a player's user interface in the game.
     * <p>
     * The <code>headerFooter</code> variable is an instance of <code>CPlayerHeaderFooterManager</code>,
     * which provides functionality to control and update the header and footer sections
     * typically seen on a player's screen during gameplay.
     * </p>
     * <ul>
     *      <li>Handles updates to the player's header and footer text.</li>
     *      <li>Allows customization based on the player's current state or gameplay context.</li>
     *      <li>Integrates with the overall player management system of the <code>CorePlayer</code> class.</li>
     * </ul>
     * <p>
     * This variable serves as one of the core components for managing visual elements
     * specific to headers and footers for a given player.
     * </p>
     */
    @Getter private CPlayerHeaderFooterManager headerFooter = new CorePlayerHeaderFooterManager(this);

    /**
     * Manages the player's title functionalities within the game.
     * This includes operations such as setting, updating, and retrieving
     * the current title displayed for the player.
     *
     * <p>Each player instance contains a title manager which is responsible
     * for handling all title-related data and operations.</p>
     *
     * <p>Features of this manager may include:</p>
     * <ul>
     *   <li>Customizing the player's title based on their achievements or rank.</li>
     *   <li>Displaying temporary or permanent titles during gameplay.</li>
     *   <li>Integrating with other systems to ensure a consistent user experience.</li>
     * </ul>
     *
     * <p>This field is initialized with a specific implementation of
     * {@code CPlayerTitleManager}, ensuring that title-related operations
     * are seamlessly integrated with the player's behavior and experience.</p>
     */
    @Getter private CPlayerTitleManager title = new CorePlayerTitleManager(this);

    /**
     * Manages the individual player's particle effects.
     * <p>
     * This field holds an instance of {@link CorePlayerParticlesManager},
     * which is responsible for controlling and customizing particles for
     * the player that this class represents.
     * </p>
     * <p>
     * The particle manager provides functionality such as enabling, disabling,
     * or altering particle effects and is initialized specifically
     * for the context of the current player.
     * </p>
     */
    @Getter private CPlayerParticlesManager particles = new CorePlayerParticlesManager(this);

    /**
     * <p>Represents the resource pack manager associated with a {@code CorePlayer}. This variable is responsible
     * for handling the player's resource pack-related operations and functionalities, including pack management and updates.</p>
     *
     * <p>The type of this variable is {@code CPlayerResourcePackManager}, which provides methods and behaviors
     * specifically tailored for managing resource packs.</p>
     *
     * <ul>
     *     <li>Handles resource packs for the player.</li>
     *     <li>Manages interactions such as applying or resetting resource packs.</li>
     * </ul>
     */
    @Getter private CPlayerResourcePackManager resourcePack = new CorePlayerResourcePackManager(this);

    /**
     * Represents the player registry associated with the current {@code CorePlayer} instance.
     * <p>
     * The registry manages player-related data and interactions, acting as a central module
     * for accessing and updating player-specific information.
     * </p>
     * <p>
     * An instance of {@link CorePlayerRegistry} is initialized and linked to this {@code CorePlayer}.
     * </p>
     * <ul>
     *   <li>It provides functionality to manage and track player states, achievements,
     *   and other personalized settings.</li>
     *   <li>This registry serves as an extension to the framework for handling
     *   user-specific gameplay logic.</li>
     * </ul>
     */
    @Getter private CPlayerRegistry registry = new CorePlayerRegistry(this);

    /**
     * Represents the texture value associated with a player's skin in the game.
     *
     * <p>This value typically corresponds to the encoded texture data that is used
     * for rendering the player's skin appearance. It is usually retrieved or set
     * when interacting with external systems or APIs that manage player skin data.</p>
     *
     * <p><b>Usage:</b></p>
     * <ul>
     *   <li>Can be used to retrieve the texture value for rendering purposes.</li>
     *   <li>Allows setting a new texture value to update a player's skin.</li>
     * </ul>
     *
     * <p>The value is stored as a string and is commonly accompanied by a signature
     * (e.g., {@code textureSignature}) to verify the validity of the texture data.</p>
     */
    @Getter @Setter private String textureValue = "";

    /**
     * Represents the signature used to validate the texture data associated with
     * a player's skin, such as in Minecraft or similar platforms.
     *
     * <p>
     * The texture signature is typically paired with a {@code textureValue}
     * to authenticate and ensure the data integrity of the player's skin or
     * related visual elements. The variable holds a Base64-encoded string
     * issued or verified through a trusted source, such as an external skin
     * service or API.
     * </p>
     *
     * <ul>
     *   <li>Can be used to verify the validity of a player’s visual customizations.</li>
     *   <li>Its value can be updated or retrieved dynamically using its getter and setter
     *       methods provided by Lombok annotations {@code @Getter} and {@code @Setter}.</li>
     * </ul>
     */
    @Getter @Setter private String textureSignature = "";

    /**
     * Represents the name of the current resource pack associated with the player.
     * <p>
     * This variable is stored as a {@code String} and defaults to {@code "none"}.
     * It can be modified to reflect the name of the resource pack the player is using.
     * </p>
     *
     * <p>
     * Usage:
     * <ul>
     *   <li>Set this variable to update the player's current resource pack.</li>
     *   <li>Retrieve this variable to check the name of the resource pack currently assigned to the player.</li>
     * </ul>
     * </p>
     */
    @Getter @Setter private String pack = "none";

    /**
     * The time at which the player joined the system.
     * <p>
     * This field represents the timestamp (in milliseconds since epoch) when the player
     * instantiated the corresponding {@code CorePlayer} instance.
     * </p>
     * <p>
     * This value is immutable and set automatically upon the player joining.
     * It can be used for tracking the player's session duration or for auditing purposes.
     * </p>
     */
    @Getter private final long joinTime = System.currentTimeMillis();

    /**
     * <p>A list containing the IDs of achievements that are queued to be processed for the player.</p>
     *
     * <p>This field is used to temporarily hold achievement IDs until they are properly awarded
     * or handled by the achievement management logic within the player handling system. It ensures
     * a streamlined and efficient way to manage multiple achievements for a player.</p>
     *
     * <ul>
     *   <li>Stored as a list of Integer values representing the IDs of the queued achievements.</li>
     *   <li>Immutable field, initialized to an empty {@code ArrayList}.</li>
     *   <li>Managed and accessed internally by the player's achievement-related methods.</li>
     * </ul>
     */
    private final List<Integer> queuedAchievements = new ArrayList<>();

    /**
     * <p>Represents the honor points assigned to a player in the game, which can be used
     * to reflect their achievements, status, or standing within the game environment.</p>
     *
     * <p>The honor value is an integral data type that tracks quantitative metrics
     * associated with a player's progress, accomplishments, or other achievements.</p>
     *
     * <ul>
     *     <li>The honor value can be modified using the methods provided in the player's API.</li>
     *     <li>It is a vital metric for systems relying on player reputation or ranking.</li>
     *     <li>Changes to the honor can trigger associated in-game effects or events.</li>
     * </ul>
     */
    @Getter private int honor;

    /**
     * Represents the honor level the player had prior to its most recent update.
     * <p>
     * This field is used to store the player's previous honor level, allowing
     * comparisons or tracking changes over time within the system.
     * <p>
     * Possible use cases include:
     * <ul>
     *     <li>Displaying how the player's honor level has changed.</li>
     *     <li>Reverting to a prior state if needed.</li>
     *     <li>Logging or auditing changes in honor levels.</li>
     * </ul>
     */
    @Getter @Setter private int previousHonorLevel;

    /**
     * Constructs a new {@code CorePlayer} instance representing a player in the game.
     *
     * <p>This constructor initializes the player's unique ID, name, rank, tags,
     * and locale.</p>
     *
     * @param uuid   The unique identifier of the player. This value cannot be null.
     * @param name   The name of the player. This value cannot be null or empty.
     * @param rank   The rank of the player, indicating their permissions or status. This value cannot be null.
     * @param tags   A list of {@code RankTag} objects associated with the player. This value cannot be null, but may be empty.
     * @param locale The locale of the player, representing their language or region. This value cannot be null or empty.
     */
    public CorePlayer(UUID uuid, String name, Rank rank, List<RankTag> tags, String locale) {
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
        this.tags = tags;
        this.locale = locale;
    }

    /**
     * Retrieves the name of the player based on their current status and availability.
     *
     * <p>The behavior of this method is as follows:
     * <ul>
     *   <li>If the player's status is not {@code PlayerStatus.JOINED}, the method returns the {@code name} directly.</li>
     *   <li>If the player is in {@code PlayerStatus.JOINED} but their Bukkit player object is {@code null}, the method returns the {@code name} directly.</li>
     *   <li>If the player is {@code PlayerStatus.JOINED} and their Bukkit player object is available, the method returns the name from the Bukkit player object.</li>
     * </ul>
     *
     * @return The name of the player, either the locally stored name or the name from the Bukkit player object,
     * depending on the player's status and availability.
     */
    @Override
    public String getName() {
        if (getStatus() != PlayerStatus.JOINED) return name;
        if (getBukkitPlayer() == null) return name;
        return getBukkitPlayer().getName();
    }

    /**
     * Checks whether the player is currently online.
     *
     * <p>This method determines the online status of the player based on the following conditions:
     * <ul>
     *   <li>The player's status must be {@code PlayerStatus.JOINED}.</li>
     *   <li>The Bukkit player instance must not be {@code null}.</li>
     *   <li>The Bukkit player instance's online status must be {@code true}.</li>
     * </ul>
     *
     * @return {@code true} if the player meets all conditions and is considered online;
     *         {@code false} otherwise.
     */
    @Override
    public boolean isOnline() {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && getBukkitPlayer().isOnline();
    }

    /**
     * Sets the operator (op) status of a player.
     * <p>
     * This method updates the operator status of the player only if the player's status
     * is set to {@code PlayerStatus.JOINED} and the Bukkit player object is not null.
     * By setting the operator status, the player is granted or revoked operator privileges
     * within the game.
     *
     * @param isOp <code>true</code> to grant the player operator status;
     *             <code>false</code> to remove operator status.
     */
    @Override
    public void setOp(boolean isOp) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setOp(isOp);
    }

    /**
     * Checks if the player is an operator (op) in the game.
     *
     * <p>This method determines if the player has operator privileges by verifying:
     * <ul>
     *   <li>The player's status is <code>PlayerStatus.JOINED</code></li>
     *   <li>The player's Bukkit representation is not <code>null</code></li>
     *   <li>The player is marked as an operator within the Bukkit system</li>
     * </ul>
     *
     * @return <code>true</code> if the player is an operator; <code>false</code> otherwise.
     */
    @Override
    public boolean isOp() {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && getBukkitPlayer().isOp();
    }

    /**
     * Retrieves the entity ID of the Bukkit player associated with this object.
     * <p>
     * This method returns:
     * <ul>
     *     <li>The entity ID of the associated Bukkit player if the player has joined and has a valid Bukkit player instance.</li>
     *     <li><code>0</code> if the player's status is not <code>PlayerStatus.JOINED</code> or the Bukkit player is null.</li>
     * </ul>
     *
     * @return The entity ID of the Bukkit player, or <code>0</code> if the player is not eligible.
     */
    @Override
    public int getEntityId() {
        if (getStatus() != PlayerStatus.JOINED) return 0;
        if (getBukkitPlayer() == null) return 0;
        return getBukkitPlayer().getEntityId();
    }

    /**
     * Plays a sound for the player at the specified location with the given parameters.
     * <p>
     * This method ensures the player is in the appropriate status to receive sound playback,
     * and verifies that the player and relevant parameters are valid.
     *
     * @param location the location at which the sound will be played. Must not be null.
     * @param sound the sound type to be played. Must not be null.
     * @param volume the volume of the sound. Should be a positive value to ensure it is audible.
     * @param pitch the pitch of the sound. Determines how high or low the sound is perceived.
     */
    @Override
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (location == null) return;
        if (sound == null) return;
        getBukkitPlayer().playSound(location, sound, volume, pitch);
    }

    /**
     * Sets the maximum health for the player.
     * <p>
     * This method updates the maximum health value of the player if the player's status
     * is {@code JOINED} and the Bukkit player object is not null.
     *
     * @param health the new maximum health value to be set for the player.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void setMaxHealth(double health) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setMaxHealth(health);
    }

    /**
     * Sets the health of the player in the game.
     * <p>
     * This method updates the health of the player only if the player has a status of
     * {@code PlayerStatus.JOINED} and there is a valid Bukkit player instance associated
     * with the player.
     *
     * @param health The new health value to be set for the player. The value should
     *               typically be within the range allowed by the Bukkit API for health values.
     */
    @Override
    public void setHealth(double health) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setHealth(health);
    }

    /**
     * Retrieves the current health of the player.
     * <p>
     * This method returns the player's health as a {@code double} value.
     * The returned value depends on the following conditions:
     * <ul>
     *     <li>If the player's status is not {@code PlayerStatus.JOINED}, the method returns 20.</li>
     *     <li>If the Bukkit player instance is {@code null}, the method returns 20.</li>
     *     <li>Otherwise, it fetches and returns the player's current health from the Bukkit instance.</li>
     * </ul>
     *
     * @return the player's current health as a {@code double}. Default value is 20 when not joined or the Bukkit player instance is {@code null}.
     */
    @Override
    public double getHealth() {
        if (getStatus() != PlayerStatus.JOINED) return 20;
        if (getBukkitPlayer() == null) return 20;

        return getBukkitPlayer().getHealth();
    }

    /**
     * Retrieves the maximum health of the player. This method will return
     * a default value of 20 if the player's status is not {@code PlayerStatus.JOINED}
     * or if the Bukkit player object is unavailable.
     *
     * <p>Note: This method is marked as deprecated and should be avoided in favor of newer
     * implementations.</p>
     *
     * @return the maximum health of the player:
     * <ul>
     *   <li>20 if the player is not in a joined status or the Bukkit player object is {@code null}.</li>
     *   <li>The actual maximum health provided by the Bukkit player API otherwise.</li>
     * </ul>
     */
    @Deprecated
    @Override
    public double getMaxHealth() {
        if (getStatus() != PlayerStatus.JOINED) return 20;
        if (getBukkitPlayer() == null) return 20;

        return getBukkitPlayer().getMaxHealth();
    }

    /**
     * Retrieves the current food level of the player.
     * <p>
     * The food level refers to the player's hunger level in the game.
     * If the player has not joined the game or if the Bukkit player
     * instance is null, a default value of 20 is returned.
     * </p>
     *
     * @return <ul>
     *         <li>The current food level of the player if available.</li>
     *         <li>20 if the player has not joined or the player object is null.</li>
     *         </ul>
     */
    @Override
    public int getFoodLevel() {
        if (getStatus() != PlayerStatus.JOINED) return 20;
        if (getBukkitPlayer() == null) return 20;

        return getBukkitPlayer().getFoodLevel();
    }

    /**
     * Sets the food level for the player if the player has a status of {@code JOINED}.
     * <p>
     * This method updates the food level of the Bukkit player associated
     * with this player object. If the player is not in the {@code JOINED}
     * status or there is no associated Bukkit player, the method will not perform
     * any operation.
     *
     * @param level the new food level to set. This represents the player's food
     *              saturation level in the game.
     */
    @Override
    public void setFoodLevel(int level) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;

        getBukkitPlayer().setFoodLevel(level);
    }

    /**
     * Retrieves the current fire ticks for this player.
     * <p>
     * Fire ticks represent the amount of time (in ticks) the player
     * will remain on fire. The value updates dynamically based on the
     * player's state.
     * <p>
     * If the player is not in the game or the player object is null, the method will return 0.
     *
     * @return the number of ticks the player will remain on fire, or 0 if the player is not in the
     *         "JOINED" state or if the player object is null.
     */
    @Override
    public int getFireTicks() {
        if (getStatus() != PlayerStatus.JOINED) return 0;
        if (getBukkitPlayer() == null) return 0;

        return getBukkitPlayer().getFireTicks();
    }

    /**
     * Retrieves the maximum fire ticks allowed for the player.
     * <p>
     * Fire ticks represent the duration a player can remain on fire before
     * it extinguishes naturally. This value may vary depending on the player's
     * current status or interaction with the server.
     *
     * <p>Behavior details:
     * <ul>
     *   <li>If the player's status is not {@code JOINED}, a default value of 20 is returned.</li>
     *   <li>If the Bukkit player object is unavailable, a default value of 20 is returned.</li>
     *   <li>If the player is valid and joined, the maximum fire ticks are retrieved from the Bukkit player object.</li>
     * </ul>
     *
     * @return the maximum fire ticks the player can endure, with a default value of 20 for non-joined or unavailable players.
     */
    @Override
    public int getMaxFireTicks() {
        if (getStatus() != PlayerStatus.JOINED) return 20;
        if (getBukkitPlayer() == null) return 20;

        return getBukkitPlayer().getMaxFireTicks();
    }

    /**
     * Sets the fire ticks for the player represented by this object. Fire ticks determine the duration for which
     * the player remains on fire.
     *
     * <p>Note: This method has no effect if the player is not in the {@code JOINED} status or if
     * the underlying Bukkit player object is unavailable.</p>
     *
     * @param ticks the time in ticks for which the player should be set on fire. A value of 0 extinguishes
     *              the fire, while a positive value sets the duration.
     */
    @Override
    public void setFireTicks(int ticks) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;

        getBukkitPlayer().setFireTicks(ticks);
    }

    /**
     * Retrieves the {@link AttributeInstance} associated with the specified {@link Attribute}
     * for the player if they are currently in a joined status and the player object is initialized.
     *
     * <p>
     * This method checks the following:
     * <ul>
     *   <li>If the player's status is not {@code PlayerStatus.JOINED}, it will return {@code null}.</li>
     *   <li>If the internal Bukkit player representation is {@code null}, it will return {@code null}.</li>
     * </ul>
     * If both conditions are satisfied, the player's {@link AttributeInstance} for the given attribute is returned.
     * </p>
     *
     * @param attribute The {@link Attribute} to retrieve the associated {@link AttributeInstance} for.
     *                  Must not be {@code null}.
     * @return The {@link AttributeInstance} associated with the specified {@link Attribute},
     *         or {@code null} if the player's status is not {@code PlayerStatus.JOINED}
     *         or if the internal Bukkit player is {@code null}.
     */
    @Override
    public AttributeInstance getAttribute(Attribute attribute) {
        if (getStatus() != PlayerStatus.JOINED) return null;
        if (getBukkitPlayer() == null) return null;

        return getBukkitPlayer().getAttribute(attribute);
    }

    /**
     * Retrieves the current game mode of the player.
     * <p>
     * This method checks the player's status and returns the appropriate game mode:
     * <ul>
     *   <li>If the player is not in the {@code PlayerStatus.JOINED} state, the default {@link GameMode#SURVIVAL} is returned.</li>
     *   <li>If the Bukkit player instance is null, the default {@link GameMode#SURVIVAL} is returned.</li>
     *   <li>Otherwise, the game mode of the associated Bukkit player is returned.</li>
     * </ul>
     *
     * @return the current {@link GameMode} of the player, or {@link GameMode#SURVIVAL} if the player is not in a joined state
     *         or if the Bukkit player instance is not available.
     */
    @Override
    public GameMode getGamemode() {
        if (getStatus() != PlayerStatus.JOINED) return GameMode.SURVIVAL;
        if (getBukkitPlayer() == null) return GameMode.SURVIVAL;
        return getBukkitPlayer().getGameMode();
    }

    /**
     * Sets the game mode for the player.
     * <p>
     * If the player's status is not {@code PlayerStatus.JOINED}, or the player
     * reference is {@code null}, the method will exit without performing any action.
     * If the provided {@code gamemode} is {@code null}, the default
     * game mode {@code GameMode.ADVENTURE} will be used.
     *
     * <p>
     * Valid {@link GameMode} values include:
     * <ul>
     * <li>{@code GameMode.SURVIVAL}</li>
     * <li>{@code GameMode.CREATIVE}</li>
     * <li>{@code GameMode.ADVENTURE}</li>
     * <li>{@code GameMode.SPECTATOR}</li>
     * </ul>
     *
     * <p>
     * The game mode will only be changed if the player's {@link PlayerStatus} is
     * {@code JOINED} and the {@link Player} instance is valid.
     *
     * @param gamemode The target {@code GameMode} to set for the player. If this is {@code null},
     *        the default {@code GameMode.ADVENTURE} is set.
     */
    @Override
    public void setGamemode(GameMode gamemode) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (gamemode == null) gamemode = GameMode.ADVENTURE;
        getBukkitPlayer().setGameMode(gamemode);
    }

    /**
     * Retrieves the player's current location.
     * <p>
     * If the player is not in the {@code JOINED} status or the Bukkit player reference is null,
     * this method returns a default location at coordinates (0, 64, 0) in the default world.
     * </p>
     *
     * @return the player's current location if the player is joined and the Bukkit player reference is valid;
     *         otherwise, a default location in the default world.
     */
    @Override
    public Location getLocation() {
        if (getStatus() != PlayerStatus.JOINED) return new Location(Core.getDefaultWorld(), 0, 64, 0);
        if (getBukkitPlayer() == null) return new Location(Core.getDefaultWorld(), 0, 64, 0);
        return getBukkitPlayer().getLocation();
    }

    /**
     * Retrieves the {@link World} associated with the player.
     * <p>
     * If the player's status is not {@code PlayerStatus.JOINED} or the Bukkit player instance
     * is {@code null}, the first world in the server's world list is returned. Otherwise,
     * the world of the Bukkit player is returned.
     *
     * @return The {@link World} object associated with the player. If the player is not in
     *         the proper status or the Bukkit player instance is unavailable, the first world
     *         in the server's loaded worlds list is returned.
     */
    @Override
    public World getWorld() {
        if (getStatus() != PlayerStatus.JOINED) return Bukkit.getWorlds().get(0);
        if (getBukkitPlayer() == null) return Bukkit.getWorlds().get(0);
        return getBukkitPlayer().getWorld();
    }

    /**
     * Teleports a player to the specified location if the player's status is {@code JOINED}
     * and the player exists in the game. The method performs validation checks on the
     * player's state and the provided location before attempting the teleport action.
     *
     * <p>Preconditions for successful teleportation:</p>
     * <ul>
     *   <li>The player's status must be {@code JOINED}.</li>
     *   <li>The player's Bukkit representation must not be {@code null}.</li>
     *   <li>The provided location must not be {@code null}.</li>
     *   <li>The world associated with the provided location must not be {@code null}.</li>
     * </ul>
     *
     * @param location The destination {@code Location} object where the player should be teleported.
     *                 This must not be {@code null} and must reference a valid world.
     */
    @Override
    public void teleport(Location location) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (location == null) return;
        if (location.getWorld() == null) return;
        getBukkitPlayer().teleport(location);
    }

    /**
     * Teleports the player to the specified location using the given teleportation cause.
     * This method ensures the player is in the correct status and that the required parameters
     * are not null before performing the teleportation.
     *
     * <p>Preconditions:
     * <ul>
     *   <li>Player must have a status of {@code PlayerStatus.JOINED}.</li>
     *   <li>{@code location} must not be null and must contain a {@code World}.</li>
     *   <li>{@code cause} must not be null.</li>
     *   <li>{@code getBukkitPlayer()} must not return null.</li>
     * </ul>
     * </p>
     *
     * @param location the {@link Location} where the player should be teleported.
     *                 Must include a valid world.
     * @param cause the {@link PlayerTeleportEvent.TeleportCause} that specifies the reason
     *              for the teleportation.
     */
    @Override
    public void teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (location == null || cause == null) return;
        if (location.getWorld() == null) return;
        getBukkitPlayer().teleport(location, cause);
    }

    /**
     * Teleports the current player to the location of the specified player.
     * <p>
     * This method checks the current player's status and ensures they have a valid
     * Bukkit player instance before performing the teleportation. If the specified
     * player is null or the required conditions are not met, the method will terminate
     * without performing any action.
     *
     * @param tp The target player whose location will be used for teleportation.
     *           Must not be null. Ensures the current player is teleported to this
     *           player's location if all conditions are met.
     */
    @Override
    public void teleport(CPlayer tp) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (tp == null) return;
        Location location = tp.getLocation();
        teleport(location);
    }

    /**
     * Sends a message to the player if the player has joined, the player instance is not null,
     * and the provided message is not null.
     *
     * <p>This method checks the player’s status and validity before attempting to send the message.</p>
     *
     * @param message The message to send to the player. Must not be null.
     */
    @Override
    public void sendMessage(String message) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (message == null) return;
        getBukkitPlayer().sendMessage(message);
    }

    /**
     * Retrieves a formatted message based on the provided key and the player's current locale.
     * <p>
     * This method fetches a localized and formatted message string from the language manager.
     * If the player is not in the {@code PlayerStatus.JOINED} state or the language manager
     * cannot be retrieved, an empty string is returned. If the message corresponding to
     * the key is not found, an empty string is returned, and a log message is recorded.
     *
     * @param key The key used to retrieve the localized message format.
     *            <ul>
     *                <li>Should be a valid key present in the language manager.</li>
     *                <li>Used to identify the message format in the configured locale.</li>
     *            </ul>
     * @return A formatted message string corresponding to the provided key and the player's locale.
     *         <ul>
     *             <li>Returns an empty string if the player is not in the {@code JOINED} state.</li>
     *             <li>Returns an empty string if the language manager is unavailable.</li>
     *             <li>Returns an empty string if the key does not map to any message.</li>
     *         </ul>
     */
    @Override
    public String getFormattedMessage(String key) {
        if (getStatus() != PlayerStatus.JOINED) return "";
        LanguageManager languageManager = Core.getLanguageFormatter();
        if (languageManager == null) {
            Core.logMessage("Language Formatter", "PROBLEM GETTING LANGUAGE FORMATTER for key: " + key);
            return "";
        }
        String message = languageManager.getFormat(getLocale(), key);
        if (message.isEmpty()) {
            Core.logMessage("Language Formatter", "MESSAGE NULL for key: " + key);
            return "";
        }
        return message;
    }

    /**
     * Sends a formatted message to the player based on the provided key.
     * The method first ensures that the player is in the correct status (`JOINED`)
     * and that the player's Bukkit player instance is not null. If the message
     * associated with the key is empty, it logs a warning message and stops execution.
     * Otherwise, it sends the formatted message to the player.
     *
     * <p>Steps performed by this method:
     * <ul>
     *   <li>Check if the player's status is `JOINED`. If not, the method exits.</li>
     *   <li>Ensure the Bukkit player instance is not null. If it is, the method exits.</li>
     *   <li>Retrieve the formatted message associated with the given key.</li>
     *   <li>If the message is empty, log a warning and exit.</li>
     *   <li>If a valid message exists, send it to the player.</li>
     * </ul>
     *
     * @param key the message key used to retrieve the formatted message.
     *            It identifies the specific message to be sent.
     */
    @Override
    public void sendFormatMessage(String key) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        String message = getFormattedMessage(key);
        if (message.isEmpty()) {
            Core.logMessage("Language Formatter", "MESSAGE NULL for key: " + key);
            return;
        }
        getBukkitPlayer().sendMessage(message);
    }

    /**
     * Resets the player's state to default values, effectively restoring them to a clean slate.
     * <p>
     * This method should only be executed if the player's current status is {@code PlayerStatus.JOINED}.
     * It ensures the player's inventory, attributes, and environmental conditions are reset.
     * </p>
     *
     * <p><b>Key actions performed:</b>
     * <ul>
     *   <li>Clears the player's inventory, including the cursor item, armor contents, main hand, and off-hand items.</li>
     *   <li>Restores default food and health levels, including saturation and exhaustion values.</li>
     *   <li>Resets environmental and motion-related properties, such as fall distance, fire ticks, velocity, and air levels.</li>
     *   <li>Resets experience levels, total experience, and XP bar.</li>
     *   <li>Disables flight, sneaking, and sprinting states.</li>
     *   <li>Removes all active potion effects.</li>
     *   <li>Restores default weather and time conditions.</li>
     *   <li>Updates the inventory to reflect changes.</li>
     * </ul>
     * </p>
     *
     * <p><b>Notes:</b>
     * <ul>
     *   <li>The method does nothing if the player's status is not {@code PlayerStatus.JOINED}.</li>
     *   <li>If the Bukkit player instance is not available, the method will terminate early.</li>
     * </ul>
     * </p>
     *
     * <p>This method ensures that the player is in a fully neutral state, suitable for scenarios where a fresh reset of the
     * character is required (e.g., game restarts or player respawns).</p>
     */
    @SuppressWarnings("deprecation")
    @Override
    public void resetPlayer() {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        Player player = getBukkitPlayer();
        player.setItemOnCursor(null);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.getInventory().setItemInMainHand(null);
        player.getInventory().setItemInOffHand(null);
        player.setFoodLevel(20);
        player.setExhaustion(0);
        player.setSaturation(20);
        player.setHealth(20);
        player.setFallDistance(0);
        player.setFireTicks(0);
        player.resetMaxHealth();
        player.setExp(0);
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setRemainingAir(300);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setSneaking(false);
        player.setSprinting(false);
        player.setVelocity(new Vector());
        player.setFallDistance(0f);
        player.resetPlayerTime();
        player.resetPlayerWeather();
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.updateInventory();
    }

    /**
     * Resets various managers and clears associated player interfaces
     * when the player's current status is {@code PlayerStatus.JOINED}.
     *
     * <p>This method performs the following actions:
     * <ul>
     *   <li>Removes the player's boss bar.</li>
     *   <li>Hides the player's header and footer.</li>
     *   <li>Hides any titles displayed to the player.</li>
     *   <li>Clears the player's scoreboard.</li>
     * </ul>
     *
     * <p>If the player's status is not {@code PlayerStatus.JOINED},
     * the method exits without performing any actions.
     */
    @Override
    public void resetManagers() {
        if (getStatus() != PlayerStatus.JOINED) return;
        getBossBar().remove();
        getHeaderFooter().hide();
        getTitle().hide();
        getScoreboard().clear();
    }

    /**
     * Sets the display name of the player. This method updates the player's display name
     * in the Bukkit system if the player's status is {@code JOINED} and the Bukkit player
     * object is not {@code null}.
     * <p>
     * If a {@code null} value is given for the display name, it will reset to an empty string.
     *
     * <p><b>Conditions:</b></p>
     * <ul>
     *   <li>The player's status must be {@code PlayerStatus.JOINED}.</li>
     *   <li>The Bukkit player object must not be {@code null}.</li>
     * </ul>
     *
     * @param name the new display name to set. If {@code null}, the display name will be set to an empty string.
     */
    @Override
    public void setDisplayName(String name) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (name == null) name = "";
        getBukkitPlayer().setDisplayName(name);
    }

    /**
     * Sets a specific inventory slot with the given {@link ItemStack}.
     * <p>
     * If the provided {@code stack} is {@code null}, the slot will be set to {@link Material#AIR}.
     * If the inventory does not exist, this method will take no action.
     *
     * @param slot the inventory slot to update. Must be a valid slot index within the inventory.
     * @param stack the {@link ItemStack} to place in the specified slot. Can be {@code null} to clear the slot by setting it to {@link Material#AIR}.
     */
    @Override
    public void setInventorySlot(int slot, ItemStack stack) {
        if (getInventory() == null) return;
        if (stack == null) stack = new ItemStack(Material.AIR);
        getInventory().setItem(slot, stack);
    }

    /**
     * Adds the specified array of {@link ItemStack} objects to the inventory.
     * <p>
     * This method checks if the inventory exists and the provided array of stacks is not null.
     * If both conditions are met, the items are added to the inventory.
     *
     * @param stacks an array of {@link ItemStack} objects to be added to the inventory.
     *               <ul>
     *                 <li>If {@code stacks} is null, the method returns without performing any action.</li>
     *               </ul>
     */
    @Override
    public void addToInventory(ItemStack... stacks) {
        if (getInventory() == null) return;
        if (stacks == null) return;
        getInventory().addItem(stacks);
    }

    /**
     * Checks if the specified material is present in the inventory.
     *
     * <p>This method validates the presence of the specified {@link Material}
     * in the current inventory. It returns {@code false} if the material is
     * {@code null}, the inventory is {@code null}, or the inventory does not
     * contain the material.</p>
     *
     * @param material The {@link Material} to check for in the inventory. Must not be {@code null}.
     * @return {@code true} if the material is present in the inventory, {@code false} otherwise.
     */
    @Override
    public boolean doesInventoryContain(Material material) {
        return material != null && getInventory() != null && getInventory().contains(material);
    }

    /**
     * Removes the specified material from the inventory if it exists.
     * <p>
     * This method checks if the inventory is not null before attempting
     * to remove the material. If the material is null, the method does nothing.
     * </p>
     *
     * @param material the material to be removed from the inventory.
     *                 If {@code null}, no action is performed.
     */
    @Override
    public void removeFromInventory(Material material) {
        if (getInventory() == null) return;
        if (material == null) return;
        getInventory().remove(material);
    }

    /**
     * Retrieves the helmet item currently equipped by the entity.
     * <p>
     * If the inventory is null, it returns an {@link ItemStack} of {@link Material#AIR}.
     * Otherwise, it returns the helmet item from the inventory.
     *
     * @return The current helmet {@link ItemStack} equipped by the entity, or an {@link ItemStack} of {@link Material#AIR} if the inventory is null.
     */
    @Override
    public ItemStack getHelmet() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getHelmet();
    }

    /**
     * Sets the helmet item in the inventory.
     *
     * <p>This method assigns a specific {@link ItemStack} as the helmet for the entity.
     * If the inventory is <code>null</code>, a default helmet of type {@link Material#AIR}
     * will be set instead.</p>
     *
     * @param itemStack the {@link ItemStack} to set as the helmet.
     *                  If <code>null</code>, the existing helmet will be replaced
     *                  with an empty air {@link ItemStack}.
     */
    @Override
    public void setHelmet(ItemStack itemStack) {
        if (getInventory() == null) itemStack = new ItemStack(Material.AIR);
        getInventory().setHelmet(itemStack);
    }

    /**
     * Retrieves the chestplate item from the inventory of the entity.
     * <p>
     * If the inventory is null, this method will return an {@link ItemStack}
     * of type {@link Material#AIR}.
     *
     * @return An {@link ItemStack} representing the chestplate item currently equipped,
     *         or an {@link ItemStack} of type {@link Material#AIR} if no inventory exists.
     */
    @Override
    public ItemStack getChestplate() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getChestplate();
    }

    /**
     * Sets the item equipped as the chestplate in the entity's inventory.
     *
     * <p>This method replaces the current chestplate with the specified {@link ItemStack}.
     * If the inventory associated with the entity is null, the chestplate is set to air.
     *
     * @param itemStack the {@link ItemStack} to be set as the chestplate.
     *                  If {@code null} or an invalid inventory is present,
     *                  the chestplate will be unset and replaced with air.
     */
    @Override
    public void setChestplate(ItemStack itemStack) {
        if (getInventory() == null) itemStack = new ItemStack(Material.AIR);
        getInventory().setChestplate(itemStack);
    }

    /**
     * Retrieves the leggings item from the inventory, if available.
     * <p>
     * This method checks if the inventory is not null before attempting
     * to retrieve the leggings. If the inventory is null, it returns a
     * placeholder {@link Material#AIR} ItemStack.
     *
     * @return The {@link ItemStack} representing the leggings in the inventory,
     * or an {@link ItemStack} of {@link Material#AIR} if the inventory is null.
     */
    @Override
    public ItemStack getLeggings() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getLeggings();
    }

    /**
     * Sets the leggings item for the entity's inventory.
     * <p>
     * This method updates the leggings slot in the inventory of the entity.
     * If the inventory is null, it sets the leggings to an empty item stack with {@link Material#AIR}.
     *
     * @param itemStack the {@link ItemStack} to set in the leggings slot.
     *                  If {@code null}, the slot will be set to an empty item stack.
     */
    @Override
    public void setLeggings(ItemStack itemStack) {
        if (getInventory() == null) itemStack = new ItemStack(Material.AIR);
        getInventory().setLeggings(itemStack);
    }

    /**
     * Retrieves the boots currently equipped by the entity from its inventory.
     * <p>
     * If the entity has no inventory or is not wearing any boots,
     * this method will return an {@link ItemStack} of type {@link Material#AIR}.
     * </p>
     *
     * @return An {@link ItemStack} representing the boots currently worn by the entity,
     * or an {@link ItemStack} of {@link Material#AIR} if no boots are present.
     */
    @Override
    public ItemStack getBoots() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getBoots();
    }

    /**
     * Sets the boots for the entity using the specified {@link ItemStack}.
     * <p>
     * This method updates the entity's inventory to equip the specified item as boots.
     * If the inventory is null, the boots are set to an air item, effectively unequipping any current boots.
     *
     * @param itemStack The {@link ItemStack} to set as the entity's boots.
     *                  <ul>
     *                      <li>If {@code null}, the method defaults to using {@link Material#AIR}.</li>
     *                      <li>Must be a valid item that can be used as boots.</li>
     *                  </ul>
     */
    @Override
    public void setBoots(ItemStack itemStack) {
        if (getInventory() == null) itemStack = new ItemStack(Material.AIR);
        getInventory().setBoots(itemStack);
    }

    /**
     * Retrieves the item currently held in the main hand of this entity.
     * <p>
     * If the inventory is unavailable, this method returns an {@link ItemStack} of type {@link Material#AIR}.
     * </p>
     *
     * @return The {@link ItemStack} held in the main hand, or an {@link ItemStack} of {@link Material#AIR} if no inventory is present.
     */
    @Override
    public ItemStack getMainHand() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getItemInMainHand();
    }

    /**
     * Sets the item in the main hand of the entity's inventory.
     * <p>
     * This method updates the item held in the main hand of the entity. If the inventory of
     * the entity is not initialized, it ensures the main hand is set to an air item
     * as a fallback.
     *
     * @param itemStack The {@link ItemStack} to be placed in the main hand.
     *                  <ul>
     *                      <li>If {@code null}, the main hand will be set to an air item.</li>
     *                      <li>If the inventory is not initialized, the provided {@code itemStack}
     *                      will be replaced with an air item.</li>
     *                  </ul>
     */
    @Override
    public void setMainHand(ItemStack itemStack) {
        if (getInventory() == null) itemStack = new ItemStack(Material.AIR);
        getInventory().setItemInMainHand(itemStack);
    }

    /**
     * Retrieves the item currently held in the off-hand slot of the entity's inventory.
     *
     * <p>If the inventory is null, it will return an {@link ItemStack} of {@link Material#AIR}.
     *
     * @return The {@link ItemStack} currently in the off-hand slot, or an {@link ItemStack} of {@link Material#AIR}
     *         if the inventory is null.
     */
    @Override
    public ItemStack getOffHand() {
        if (getInventory() == null) return new ItemStack(Material.AIR);
        return getInventory().getItemInOffHand();
    }

    /**
     * Sets the item in the off-hand slot of the entity's inventory.
     * <p>
     * If the inventory is null, the provided {@code itemStack} will be replaced
     * with an {@link ItemStack} of type {@link Material#AIR} to ensure no null values are set.
     *
     * @param itemStack the {@link ItemStack} to set in the off-hand slot of the inventory.
     *                  <ul>
     *                      <li>If {@code itemStack} is {@code null} or invalid, it will
     *                      default to an empty off-hand slot.</li>
     *                      <li>Must be a valid {@link ItemStack} object representing the item
     *                      to equip in the off-hand.</li>
     *                  </ul>
     */
    @Override
    public void setOffHand(ItemStack itemStack) {
        if (getInventory() == null) itemStack = new ItemStack(Material.AIR);
        getInventory().setItemInOffHand(itemStack);
    }

    /**
     * Retrieves the inventory of the player associated with this instance.
     * <p>
     * This method checks if the player has joined and if the Bukkit player instance is available.
     * If either of these conditions is not met, it will return <code>null</code>.
     * </p>
     *
     * @return the <code>PlayerInventory</code> of the player if the player is in the
     *         <code>PlayerStatus.JOINED</code> state and the Bukkit player exists;
     *         otherwise, returns <code>null</code>.
     */
    @Override
    public PlayerInventory getInventory() {
        if (getStatus() != PlayerStatus.JOINED) return null;
        if (getBukkitPlayer() == null) return null;
        return getBukkitPlayer().getInventory();
    }

    /**
     * Updates the inventory view of the player's Bukkit entity if certain conditions are met.
     * <p>
     * This method checks the player's current status and ensures it is {@code PlayerStatus.JOINED}.
     * Additionally, it verifies that the associated Bukkit player object is not {@code null}.
     * If both conditions are satisfied, the inventory of the Bukkit player is updated to reflect
     * any changes.
     * </p>
     * <p>
     * This method is called to ensure that the in-game player inventory remains synchronized and
     * correctly displayed after updates.
     * </p>
     *
     * <ul>
     *   <li>Condition 1: The player's status must be {@code PlayerStatus.JOINED}.</li>
     *   <li>Condition 2: The associated Bukkit player object must exist (not {@code null}).</li>
     * </ul>
     */
    @Override
    public void updateInventory() {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().updateInventory();
    }

    /**
     * Retrieves the {@link ItemStack} from the specified inventory slot of the player
     * if the player's status is {@code JOINED} and the Bukkit player instance is not null.
     *
     * <p>This method ensures that the player's status is verified and they have
     * a valid Bukkit player instance before attempting to retrieve the item.</p>
     *
     * @param slot the inventory slot index from which the item is to be retrieved.
     *             Must be a valid slot index for the player's inventory.
     * @return the {@link ItemStack} located in the specified slot,
     *         or {@code null} if the player's status is not {@code JOINED},
     *         the Bukkit player instance is {@code null},
     *         or if the slot contains no item.
     */
    @Override
    public ItemStack getItem(int slot) {
        if (getStatus() != PlayerStatus.JOINED) return null;
        if (getBukkitPlayer() == null) return null;
        return getBukkitPlayer().getInventory().getItem(slot);
    }

    /**
     * Retrieves the item currently held in the main hand of the player.
     * <p>
     * This method checks if the player status is {@code JOINED} and if the associated
     * Bukkit player object is not {@code null}. If either condition is not met,
     * the method returns {@code null}. Otherwise, it fetches the item from the
     * player's main hand in their inventory.
     *
     * @return the {@link ItemStack} held in the player's main hand, or {@code null} if the player
     *         is not in the {@code JOINED} state or if no Bukkit player object exists.
     */
    @Override
    public ItemStack getItemInMainHand() {
        if (getStatus() != PlayerStatus.JOINED) return null;
        if (getBukkitPlayer() == null) return null;
        return getBukkitPlayer().getInventory().getItemInMainHand();
    }

    /**
     * Retrieves the item currently held in the player's off-hand.
     * <p>
     * If the player's status is not {@code PlayerStatus.JOINED} or the Bukkit player object is null,
     * this method will return {@code null}.
     * <p>
     * <b>Note:</b> This method's functionality depends on the state of the Bukkit player object
     * and the player's current game status.
     *
     * @return The {@link ItemStack} held in the player's off-hand, or {@code null} if the player is
     * not in the appropriate state or the Bukkit player object is unavailable.
     */
    @Override
    public ItemStack getItemInOffHand() {
        if (getStatus() != PlayerStatus.JOINED) return null;
        if (getBukkitPlayer() == null) return null;
        return getBukkitPlayer().getInventory().getItemInOffHand();
    }

    /**
     * Retrieves the slot index of the item currently held by the player.
     * <p>
     * The player's current held item slot is determined based on their
     * in-game inventory. If the player is not properly initialized or
     * their status is not {@code PlayerStatus.JOINED}, the method will
     * return slot index {@code 0} as a default.
     *
     * <p><b>Conditions:</b>
     * <ul>
     *   <li>If the player's status is not {@code JOINED}, it returns {@code 0}.</li>
     *   <li>If the Bukkit player is not available, it returns {@code 0}.</li>
     *   <li>Otherwise, the method retrieves the active held item slot
     *       from the player's inventory.</li>
     * </ul>
     *
     * @return The index of the held item slot, where valid indices are
     * usually within the range of available inventory slots. Returns {@code 0}
     * if the player's status is not {@code JOINED} or if the player
     * instance is unavailable.
     */
    @Override
    public int getHeldItemSlot() {
        if (getStatus() != PlayerStatus.JOINED) return 0;
        if (getBukkitPlayer() == null) return 0;
        return getBukkitPlayer().getInventory().getHeldItemSlot();
    }

    /**
     * Sets the held item slot for the player.
     * <p>
     * This method updates the player's currently selected hotbar slot in their inventory,
     * provided the player's status is {@code PlayerStatus.JOINED} and the Bukkit player instance is not null.
     *
     * @param slot The hotbar slot to set as the selected slot.
     *             Must be a valid index within the player's inventory hotbar range.
     */
    @Override
    public void setHeldItemSlot(int slot) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().getInventory().setHeldItemSlot(slot);
    }

    /**
     * Opens the provided inventory for the player if certain conditions are met.
     * <p>
     * This method checks the following before attempting to open the inventory:
     * <ul>
     *   <li>The player's status must be {@code PlayerStatus.JOINED}.</li>
     *   <li>The Bukkit player object must not be {@code null}.</li>
     *   <li>The provided inventory must not be {@code null}.</li>
     * </ul>
     * If any of these conditions are not met, the method returns without opening the inventory.
     *
     * @param inventory the {@link Inventory} to open for the player; must not be {@code null}.
     */
    @Override
    public void openInventory(Inventory inventory) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (inventory == null) return;
        getBukkitPlayer().openInventory(inventory);
    }

    /**
     * Closes the player's inventory interface if applicable.
     * <p>
     * This method checks the player's current status and ensures the player is active
     * and properly initialized in the system before attempting to close their inventory UI.
     * </p>
     *
     * <p>This method performs the following checks before execution:</p>
     * <ul>
     *   <li>Ensures the player has a status of {@code PlayerStatus.JOINED}.</li>
     *   <li>Verifies that the associated Bukkit player instance is not {@code null}.</li>
     * </ul>
     *
     * <p>If both conditions are satisfied, the inventory UI for the player is closed
     * through the {@code closeInventory()} method of the Bukkit player instance.</p>
     */
    @Override
    public void closeInventory() {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().closeInventory();
    }

    /**
     * Checks if the player has the specified permission.
     *
     * <p>This method verifies if the player's status is {@code PlayerStatus.JOINED},
     * ensures the underlying Bukkit player object is not {@code null}, and then
     * delegates the permission check to Bukkit's permission system.</p>
     *
     * @param node the permission node to check for, represented as a {@code String}.
     *             It must not be {@code null}.
     * @return {@code true} if the player has the specified permission and meets
     *         the necessary conditions such as being in the {@code JOINED} status
     *         and having a valid Bukkit player object, otherwise {@code false}.
     */
    @Override
    public boolean hasPermission(String node) {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && getBukkitPlayer().hasPermission(node);
    }

    /**
     * Handles the respawn logic for a player in the game.
     * <p>
     * This method ensures that the player can only respawn if they have joined the game
     * and their associated Bukkit player instance is valid. It interacts with the
     * Spigot API to trigger the player's respawn event server-side.
     * </p>
     *
     * <p>The method includes the following checks:</p>
     * <ul>
     *   <li>Ensures the player status is {@code PlayerStatus.JOINED}.</li>
     *   <li>Verifies that the player has a valid {@code BukkitPlayer} reference.</li>
     *   <li>Validates the Spigot instance for the player is not {@code null}.</li>
     * </ul>
     *
     * <p>If all conditions are met, the Spigot {@code respawn} method is invoked for the player.</p>
     */
    @Override
    public void respawn() {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (getBukkitPlayer().spigot() == null) return;
        getBukkitPlayer().spigot().respawn();
    }

    /**
     * <p>
     * Displays the given player's information or state. This method may delegate the task
     * to another method for execution.
     * </p>
     * <p>
     * Note: This method is marked as deprecated and its usage is not recommended
     * in future implementations. Consider using alternative methods if available.
     * </p>
     *
     * @param player the <code>CPlayer</code> object representing the player to be displayed.
     *               Must not be <code>null</code>.
     */
    @Override
    @Deprecated
    public void showPlayer(CPlayer player) {
        showPlayer(null, player);
    }

    /**
     * Displays a specific player's entity to the current player in the game.
     * <p>
     * This method ensures that both the current player and the specified player
     * are in the proper {@link PlayerStatus#JOINED} state before performing the action.
     * The method utilizes Bukkit's player management system to show the entity.
     * </p>
     *
     * @param plugin The {@link org.bukkit.plugin.Plugin} instance managing the player visibility.
     *               If null, the core plugin instance is used as a default.
     * @param player The {@link CPlayer} instance representing the player entity to show.
     *               The method will not perform any action if this parameter is null or if the
     *               player's status is not {@link PlayerStatus#JOINED}.
     */
    @Override
    public void showPlayer(org.bukkit.plugin.Plugin plugin, CPlayer player) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (player == null) return;
        if (player.getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (plugin == null) plugin = Core.getInstance();
        getBukkitPlayer().showPlayer(plugin, player.getBukkitPlayer());
//        getBukkitPlayer().showPlayer(player.getBukkitPlayer());
    }

    /**
     * Hides the specified player from view.
     *
     * <p>This method is used to make the provided player invisible to others.
     * It delegates the functionality to another internal method with additional parameters.
     * Note that this method is marked as deprecated and may be removed in future versions.
     * It is recommended to use alternative methods if available. </p>
     *
     * @param player the {@code CPlayer} instance to hide
     */
    @Override
    @Deprecated
    public void hidePlayer(CPlayer player) {
        hidePlayer(null, player);
    }

    /**
     * Hides a specified player from the view of this player.
     * <p>
     * This method ensures that the player to be hidden and this player are both in the "JOINED" status.
     * If the plugin is null, the Core plugin instance is used by default.
     * </p>
     *
     * @param plugin the plugin instance initiating this action. A Core plugin instance is used if null.
     * @param player the player to be hidden from the view of this player. Must not be null and must have joined.
     */
    @Override
    public void hidePlayer(org.bukkit.plugin.Plugin plugin, CPlayer player) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (player == null) return;
        if (player.getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (plugin == null) plugin = Core.getInstance();
        getBukkitPlayer().hidePlayer(plugin, player.getBukkitPlayer());
//        getBukkitPlayer().hidePlayer(player.getBukkitPlayer());
    }

    /**
     * Determines if the current player can see the specified {@code CPlayer}.
     *
     * <p>This method evaluates the visibility of the specified player based
     * on their underlying Bukkit player representation.</p>
     *
     * @param player the {@code CPlayer} to check visibility for
     * @return {@code true} if the current player can see the specified {@code CPlayer},
     *         {@code false} otherwise
     */
    @Override
    public boolean canSee(CPlayer player) {
        return canSee(player.getBukkitPlayer());
    }

    /**
     * Determines whether the current player can see the specified player.
     *
     * <p>This method checks the following conditions:
     * <ul>
     *   <li>The current player's status must be {@code PlayerStatus.JOINED}.</li>
     *   <li>The current Bukkit player instance must not be {@code null}.</li>
     *   <li>The specified player must not be {@code null}.</li>
     *   <li>The Bukkit API-level condition {@code canSee(player)} must evaluate to {@code true}.</li>
     * </ul>
     *
     * @param player the player to check visibility for, must not be {@code null}.
     * @return {@code true} if the current player*/
    @Override
    public boolean canSee(Player player) {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && player != null && getBukkitPlayer().canSee(player);
    }

    /**
     * Sends a packet to the associated player if the player's status is {@code JOINED}
     * and the Bukkit player instance is available.
     * <p>
     * Ensures the packet is transmitted only when all required conditions are met:
     * <lu>
     * <li>The player's status must be set to {@code JOINED}.</li>
     * <li>The Bukkit player instance must not be {@code null}.</li>
     * <li>The provided packet must not be {@code null}.</li>
     * </lu>
     * If any of the conditions fail, the method exits without executing the packet send operation.
     *
     * @param packet The {@link AbstractPacket*/
    @Override
    public void sendPacket(AbstractPacket packet) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (packet == null) return;
        packet.sendPacket(Core.getPlayerManager().getPlayer(getBukkitPlayer()));
    }

    /**
     * Sends the player to the specified server using the BungeeCord plugin messaging channel.
     * <p>
     * This method checks if the player is in the {@code JOINED} status and if the player's Bukkit
     * entity is not null. If the conditions are met, it creates a plugin message to instruct
     * the BungeeCord proxy to connect the player to the specified server.
     *
     * @param server The name of the target server to send the player to. Must not be {@code null}.
     */
    @Override
    public void sendToServer(String server) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (server == null) return;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        getBukkitPlayer().sendPluginMessage(Core.getInstance(), "BungeeCord", out.toByteArray());
    }

    /**
     * Retrieves the Bukkit {@link Player} instance associated with this player,
     * if the player is currently in the {@link PlayerStatus#JOINED} state.
     * <p>
     * This method checks the player's status to ensure it is set to {@code JOINED}
     * before attempting to retrieve the corresponding {@link Player} object from Bukkit.
     * If the player is not in the {@code JOINED} state, the method will return {@code null}.
     * </p>
     *
     * @return The {@link Player} instance from Bukkit if the player is in the
     *         {@code JOINED} state,*/
    @Override
    public Player getBukkitPlayer() {
        if (getStatus() != PlayerStatus.JOINED) return null;
        return Bukkit.getPlayer(getUniqueId());
    }

    /**
     * Retrieves a unique identifier for the current object.
     *
     * <p>This method overrides the superclass implementation to
     * provide a unique {@link UUID} value associated with the object,
     * enabling reliable identification.</p>
     *
     * @return a {@link UUID} that uniquely identifies the object
     */
    @Override
    public UUID getUniqueId() {
        return getUuid();
    }

    /**
     * Retrieves a list of RankTag objects. If the internal tags list is null or empty,
     * it returns an empty list. Otherwise, it sorts the tags by their IDs in descending
     * order before returning a copy of the list.
     *
     * <p> The returned list is a new instance and any modifications to it will not affect
     * the internal representation of tags.</p>
     *
     * @return A <code>List</code> of <code>RankTag</code> objects, sorted in descending order by ID.
     * If no tags are available, returns an empty list.
     */
    @Override
    public List<RankTag> getTags() {
        if (tags == null || tags.isEmpty()) return new ArrayList<>();
        tags.sort((rankTag, t1) -> t1.getId() - rankTag.getId());
        return new ArrayList<>(tags);
    }

    /**
     * Adds a new {@code RankTag} to the collection if it is not already present and sorts the
     * collection in descending order by the tag IDs.
     *
     * <p>This method ensures that duplicate tags are not added to maintain the integrity
     * of the collection. The sorting is performed based on the {@code getId()} of each
     * {@code RankTag} in descending order.
     *
     * <p><strong>Note:</strong> If the provided {@code tag} is {@code null} or already exists
     * in the collection, the operation is aborted without any modification.
     *
     * @param tag the {@code RankTag} object to be added to the collection
     */
    @Override
    public void addTag(RankTag tag) {
        if (tag == null || tags.contains(tag)) return;
        tags.add(tag);
        tags.sort((rankTag, t1) -> t1.getId() - rankTag.getId());
    }

    /**
     * Removes the specified tag from the collection of tags.
     *
     * <p>This method attempts to remove the provided {@link RankTag} object
     * from the internal collection of tags. If the provided tag is {@code null},
     * the method returns {@code false}.
     *
     * @param tag the {@link RankTag} object to be removed from the collection.
     *        If {@code null}, the method will return {@code false}.
     * @return {@code true} if the tag was successfully removed from the collection,
     *         {@code false} otherwise, including when {@code tag} is null or when
     *         the tag is not present in the collection.
     */
    @Override
    public boolean removeTag(RankTag tag) {
        if (tag == null) return false;
        return tags.remove(tag);
    }

    /**
     * Checks if the specified tag is present in the collection of tags.
     *
     * <p>This method verifies whether the provided {@code tag} exists
     * in the current set of tags associated with the object.</p>
     *
     * @param tag the {@link RankTag} to check for presence in the tag collection.
     * @return {@code true} if the tag is present in the collection, otherwise {@code false}.
     */
    @Override
    public boolean hasTag(RankTag tag) {
        return getTags().contains(tag);
    }

    /**
     * Checks if the player has a specific achievement.
     * <p>
     * This method verifies if the player is in the "JOINED" status and has
     * an available Bukkit player and achievement manager. It then checks
     * the specified achievement based on its identifier.
     *
     * @param i the identifier of the achievement to check
     * @return <code>true</code> if the player has the specified achievement and meets the prerequisites;
     *         <code>false</code> otherwise
     */
    @Override
    public boolean hasAchievement(int i) {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && achievementManager != null && achievementManager.hasAchievement(i);
    }

    /**
     * Grants an achievement to the player if the player's status and other conditions allow it.
     * <p>
     * This method checks if the player has joined the game and ensures that they have an active
     * Bukkit player instance and achievement manager. If conditions are met, it directly gives
     * the achievement. If the achievement manager is unavailable, it queues the achievement for
     * later processing.
     *
     * @param i the ID of the achievement to be granted.
     */
    @Override
    public void giveAchievement(int i) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        if (achievementManager == null) {
            queuedAchievements.add(i);
            return;
        }
        achievementManager.giveAchievement(i);
    }

    /**
     * Retrieves the target block within the specified range that the player is looking at.
     * <p>
     * This method returns the block that is being targeted by the player's view, up to the specified range.
     * If no block is targeted or the player is not available, it returns <code>null</code>.
     *
     * @param range the maximum distance (in blocks) to check for a target block
     *              from the player's current position and view direction.
     * @return the {@link Block} that the player is targeting within the given range,
     *         or <code>null</code> if no target block can be found or if the player is unavailable.
     */
    @Override
    public Block getTargetBlock(int range) {
        if (getBukkitPlayer() == null) return null;
        return getBukkitPlayer().getTargetBlock(null, range);
    }

    /**
     * Retrieves the ping (latency) of the player.
     * <p>
     * This method checks if the player's status is {@code PlayerStatus.JOINED} and if the Bukkit player
     * instance is available. It then attempts to access the internal ping value from the underlying
     * Minecraft server implementation.
     * <p>
     * If any exception occurs during this process or the player is not joined, the method will return {@code 0}.
     *
     * @return the current ping (latency) of the player in milliseconds, or {@code 0} if the value cannot be retrieved.
     */
    @Override
    public int getPing() {
        if (getStatus() != PlayerStatus.JOINED) return 0;
        if (getBukkitPlayer() == null) return 0;
        try {
            Object craftPlayer = Class.forName("org.bukkit.craftbukkit." + Core.getMinecraftVersion() +
                    ".entity.CraftPlayer").cast(getBukkitPlayer());
            Method m = craftPlayer.getClass().getDeclaredMethod("getHandle");
            Object entityPlayer = m.invoke(craftPlayer);
            Field ping = entityPlayer.getClass().getDeclaredField("ping");
            ping.setAccessible(true);
            return (int) ping.get(entityPlayer);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                NoSuchFieldException e) {
            return 0;
        }
    }

    /**
     * Determines whether the player is allowed to fly in the current game state.
     *
     * <p>This method checks if the player's status is {@code PlayerStatus.JOINED}
     * and verifies the {@code BukkitPlayer} instance is not null. If both conditions
     * are true, the method returns the flight allowance of the Bukkit player.</p>
     *
     * @return <code>true</code> if the player is allowed to fly;
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean getAllowFlight() {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && getBukkitPlayer().getAllowFlight();
    }

    /**
     * Sets whether the player is allowed to fly or not.
     *
     * <p>This method will enable or disable the player's ability to fly.
     * It checks if the player's status is {@code JOINED} and if the player object in the Bukkit system is not null
     * before modifying the flight permission.</p>
     *
     * @param fly <code>true</code> to allow the player to fly; <code>false</code> to disallow flying.
     */
    @Override
    public void setAllowFlight(boolean fly) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setAllowFlight(fly);
    }

    /**
     * Determines if the player is currently flying.
     *
     * <p>This method checks the player's status and the associated Bukkit player instance to determine
     * if they are actively flying in the game.</p>
     *
     * @return <code>true</code> if the player is in the {@link PlayerStatus#JOINED} state,
     * the associated Bukkit player instance exists, and the player is flagged as flying;
     * otherwise <code>false</code>.
     */
    @Override
    public boolean isFlying() {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && getBukkitPlayer().isFlying();
    }

    /**
     * Returns the current walking speed of the player.
     *
     * <p>This method retrieves the walk speed of the player if their status is
     * {@code PlayerStatus.JOINED} and the underlying Bukkit player object
     * is not null. If either condition is not met, the method returns 0.</p>
     *
     * @return the walking speed of the player as a floating-point value. Returns 0
     *         if the player is not in the {@code JOINED} status or the Bukkit player
     *         instance is null.
     */
    @Override
    public float getWalkSpeed() {
        if (getStatus() != PlayerStatus.JOINED) return 0;
        if (getBukkitPlayer() == null) return 0;
        return getBukkitPlayer().getWalkSpeed();
    }

    /**
     * Sets the walking speed of the player.
     * <p>
     * This method modifies the walk speed of the player only if the player has joined
     * and has an associated Bukkit player object.
     * </p>
     *
     * @param speed the new walking speed for the player, which must be a value between -1.0 and 1.0.
     *              Values outside of this range may result in exceptions or unintended behavior.
     */
    @Override
    public void setWalkSpeed(float speed) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setWalkSpeed(speed);
    }

    /**
     * Retrieves the flying speed of the player in the game.
     *
     * <p>This method checks the current status of the player.
     * If the player is not in the {@code PlayerStatus.JOINED} state
     * or the Bukkit player object is unavailable, the flying speed
     * is returned as <code>0</code>. Otherwise, it fetches and
     * returns the flying speed of the player from the Bukkit API.</p>
     *
     * @return the player's current fly speed as a <code>float</code>.
     *         Returns <code>0</code> if the player is not joined or
     *         the player object is unavailable.
     */
    @Override
    public float getFlySpeed() {
        if (getStatus() != PlayerStatus.JOINED) return 0;
        if (getBukkitPlayer() == null) return 0;
        return getBukkitPlayer().getFlySpeed();
    }

    /**
     * Sets the fly speed for the player.
     * <p>
     * This method adjusts the player's ability to move while flying. The speed is represented
     * as a float, where the value must be within the boundaries allowed by the underlying
     * game mechanics. Typically, it must be between -1.0 and 1.0.
     * <p>
     * Note:
     * <ul>
     *     <li>This method only applies if the current player status is {@code PlayerStatus.JOINED}.</li>
     *     <li>The player's Bukkit instance must not be {@code null}; otherwise, no action is taken.</li>
     * </ul>
     *
     * @param speed the fly speed value to set for the player, where negative values reverse fly direction
     *              depending on game implementation, and positive values increase forward speed.
     */
    @Override
    public void setFlySpeed(float speed) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setFlySpeed(speed);
    }

    /**
     * Sets the sneaking state of the player.
     *
     * <p>This method updates the sneaking status of the player
     * in the game, if the player is currently in the {@code JOINED} state
     * and has an associated Bukkit player instance.</p>
     *
     * @param sneaking {@code true} to make the player sneak, or {@code false} to stop sneaking.
     */
    @Override
    public void setSneaking(boolean sneaking) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setSneaking(sneaking);
    }

    /**
     * Checks if the player is currently sneaking.
     * <p>
     * This method verifies the sneaking status by ensuring the player:
     * <ul>
     *   <li>is in the {@code JOINED} status</li>
     *   <li>has a valid {@code BukkitPlayer} instance</li>
     *   <li>and the {@code BukkitPlayer} is sneaking</li>
     * </ul>
     *
     * @return {@code true} if the player is sneaking; {@code false} otherwise.
     */
    @Override
    public boolean isSneaking() {
        return getStatus() == PlayerStatus.JOINED && getBukkitPlayer() != null && getBukkitPlayer().isSneaking();
    }

    /**
     * Sets the flying state for the player.
     * <p>
     * This method enables or disables the player's ability to fly. It will only
     * execute if the player's status is {@code PlayerStatus.JOINED} and the player's
     * Bukkit instance is non-null.
     *
     * @param fly A boolean value indicating the desired flying state:
     *            <ul>
     *              <li><code>true</code> to enable flying</li>
     *              <li><code>false</code> to disable flying</li>
     *            </ul>
     */
    @Override
    public void setFlying(boolean fly) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setFlying(fly);
    }

    /**
     * Kicks a player from the server with a specified reason.
     * <p>
     * This method checks if the player object associated with
     * the method is valid. If valid, it invokes the kickPlayer
     * functionality on the Bukkit API to disconnect the player from
     * the server with the provided reason.
     *
     * @param reason the reason for the player's removal from the server.
     *               <ul>
     *                 <li>Must be a non-null, non-empty string.</li>
     *                 <li>The provided reason will be displayed to the player upon disconnection.</li>
     *               </ul>
     */
    @Override
    public void kick(String reason) {
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().kickPlayer(reason);
    }

    /**
     * Executes a command as the associated Bukkit player if the player's status is {@code JOINED}.
     * <p>
     * This method checks whether the player's status is {@code JOINED} and ensures the Bukkit player
     * object is not null before executing the command.
     *
     * @param cmd the command to be executed by the Bukkit player. It should be a valid command string understood
     *            by the underlying game server.
     */
    @Override
    public void performCommand(String cmd) {
        if (getStatus() != PlayerStatus.JOINED) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().performCommand(cmd);
    }

    /**
     * Retrieves the token balance for the associated entity using the {@link CurrencyType#TOKENS}.
     * This method is deprecated and may be removed in future updates.
     *
     * <p>It fetches the token count by delegating the request to the core Mongo handler.</p>
     *
     * @return the token balance as an integer. This value is retrieved from the underlying
     * storage system and corresponds to the entity's token balance.
     */
    @Deprecated
    @Override
    public int getTokens() {
        return Core.getMongoHandler().getCurrency(getUuid(), CurrencyType.TOKENS);
    }

    /**
     * Retrieves the balance for the current user.
     * <p>
     * This method fetches the balance from the data source using the user's UUID and returns it.
     * Note that this method is marked as deprecated and may be removed in future releases.
     * It is recommended to consider alternative methods for retrieving the balance.
     *
     * @return an integer representing the user's balance
     */
    @Deprecated
    @Override
    public int getBalance() {
        return Core.getMongoHandler().getCurrency(getUuid(), CurrencyType.BALANCE);
    }

    /**
     * <p>Adds a specific amount of tokens to the system. This method has been marked as deprecated
     * and may no longer be supported in future versions.</p>
     *
     * <p>Internally, this method calls an alternate implementation
     * that requires specifying a source identifier.</p>
     *
     * @param amount the number of tokens to add. Must be a positive integer.
     */
    @Deprecated
    @Override
    public void addTokens(int amount) {
        addTokens(amount, "plugin");
    }

    /**
     * Adds a specified amount to the balance. This method is deprecated and may be removed in future versions.
     * It delegates the call to {@link #addBalance(int, String)} using a default source of "plugin".
     *
     * @param amount the amount to be added to the balance. Must be a positive integer.
     */
    @Deprecated
    @Override
    public void addBalance(int amount) {
        addBalance(amount, "plugin");
    }

    /**
     * Adds a specified amount of tokens with an associated reason.
     * This method is deprecated and may be removed in future releases.
     * Consider using an alternative method that provides enhanced functionality.
     *
     * <p>
     * This method performs the same operation as its overload, allowing
     * a specific number of tokens to be added along with a textual reason for logging or tracking purposes.
     * </p>
     *
     * @param amount the number of tokens to add. Must be a non-negative integer.
     * @param reason the reason or description for adding the tokens. Typically used for logging or auditing purposes.
     */
    @Deprecated
    @Override
    public void addTokens(int amount, String reason) {
        addTokens(amount, reason, null);
    }

    /**
     * Adds a specified amount to the balance with an associated reason.
     * <p>
     * This method allows updating the balance with contextual information
     * represented by a reason. A deprecated implementation redirects
     * the call to another method variant.
     * </p>
     *
     * @param amount the amount to be added to the balance. It must be a positive integer.
     * @param reason a string representing the reason for the balance addition.
     */
    @Deprecated
    @Override
    public void addBalance(int amount, String reason) {
        addBalance(amount, reason, null);
    }

    /**
     * Adds tokens to the user's account based on the specified amount and provides
     * an optional reason and transaction callback. Displays the transaction details
     * in the action bar.
     *
     * <p><b>Note:</b> This method is deprecated and may be removed in future releases.</p>
     *
     * @param amount   The amount of tokens to add. Positive values indicate tokens to
     *                 be added, and negative values indicate tokens to be deducted.
     *                 If the amount is 0, the method returns immediately with no effect.
     *
     * @param reason   The reason for the token transaction. This can be used for logging
     *                 or informational purposes.
     *
     * @param callback The callback to handle the result of the transaction. This allows
     *                 post-transaction functionality, such as confirming success or handling errors.
     */
    @Deprecated
    @Override
    public void addTokens(int amount, String reason, TransactionCallback callback) {
        if (amount == 0) return;
        if (amount > 0) {
            getActionBar().show(ChatColor.YELLOW + "+" + CurrencyType.TOKENS.getIcon() + Math.abs(amount));
        } else {
            getActionBar().show(ChatColor.YELLOW + "-" + CurrencyType.TOKENS.getIcon() + Math.abs(amount));
        }
        Core.getEconomy().addTransaction(uuid, amount, reason, CurrencyType.TOKENS, callback);
    }

    /**
     * <p>
     * Adds a specified amount to the user's balance and logs the transaction with a reason.
     * Also displays a notification regarding the balance change in the action bar.
     * </p>
     *
     * <p><b>Note:</b> This method is deprecated and should be replaced with the updated implementation.</p>
     *
     * @param amount   the amount to be added to the balance. A positive value increases the balance,
     *                 while a negative value decreases it.
     * @param reason   the reason for the transaction. This helps in identifying the purpose of the balance change.
     * @param callback a callback to handle the result of the transaction once it is processed.
     */
    @Deprecated
    @Override
    public void addBalance(int amount, String reason, TransactionCallback callback) {
        if (amount == 0) return;
        if (amount > 0) {
            getActionBar().show(ChatColor.GREEN + "+" + CurrencyType.BALANCE.getIcon() + Math.abs(amount));
        } else {
            getActionBar().show(ChatColor.GREEN + "-" + CurrencyType.BALANCE.getIcon() + Math.abs(amount));
        }
        Core.getEconomy().addTransaction(uuid, amount, reason, CurrencyType.BALANCE, callback);
    }

    /**
     * Sets the number of tokens for the given amount and source.
     * <p>
     * This method is deprecated and is overridden to directly set the tokens
     * using the specified amount and a default source of "plugin".
     * It internally delegates to {@link #setTokens(int, String)}.
     * </p>
     *
     * @param amount The number of tokens to be set.
     */
    @Deprecated
    @Override
    public void setTokens(int amount) {
        setTokens(amount, "plugin");
    }

    /**
     * Sets the balance to the specified amount.
     * This method is deprecated and may be removed in future releases.
     * Use alternative methods for setting the balance, if available.
     *
     * <p>
     * <b>Note:</b> This method internally calls {@code setBalance(amount, "plugin")}.
     * </p>
     *
     * @param amount the new balance to be set, specified as an integer.
     */
    @Deprecated
    @Override
    public void setBalance(int amount) {
        setBalance(amount, "plugin");
    }

    /**
     * Sets the token amount for the user asynchronously. This method updates the user's token balance
     * in the database for the specified reason and amount.
     *
     * <p><strong>Note:</strong> This method is deprecated and might be removed or replaced in future versions.</p>
     *
     * @param amount The amount of tokens to be set. This can be a positive or negative value, depending on
     *               whether tokens are being added or deducted.
     * @param reason The reason for the token amount adjustment. This is used for tracking and logging purposes.
     */
    @Deprecated
    @Override
    public void setTokens(int amount, String reason) {
        Core.runTaskAsynchronously(Core.getInstance(), () -> Core.getMongoHandler().changeAmount(getUuid(), amount, reason, CurrencyType.TOKENS, true));
    }

    /**
     * Sets the balance for a user asynchronously with the specified amount and reason.
     *
     * <p><strong>Note:</strong> This method is deprecated and may be removed in future versions.</p>
     *
     * @param amount The amount by which the balance is to be updated. Can be positive or negative.
     * @param reason The reason for the balance change. This helps in tracking and logging the transaction.
     */
    @Deprecated
    @Override
    public void setBalance(int amount, String reason) {
        Core.runTaskAsynchronously(Core.getInstance(), () -> Core.getMongoHandler().changeAmount(getUuid(), amount, reason, CurrencyType.BALANCE, true));
    }

    /**
     * Removes a specified amount of tokens from the system. This method is deprecated
     * and should not be used in future implementations. Consider using alternative methods
     * for token management.
     *
     * <p><b>Note:</b> This method overrides a parent implementation and defaults to a
     * specific token removal type ("plugin").</p>
     *
     * @param amount the number of tokens to be removed. The value should be a positive integer.
     */
    @Deprecated
    @Override
    public void removeTokens(int amount) {
        removeTokens(amount, "plugin");
    }

    /**
     * Removes a specified amount from the balance.
     * <p>
     * This method is marked as deprecated and an alternative implementation should be used.
     * It overrides an existing method in the parent class to provide additional functionality.
     * </p>
     *
     * @param amount The amount to be removed from the balance.
     */
    @Deprecated
    @Override
    public void removeBalance(int amount) {
        removeBalance(amount, "plugin");
    }

    /**
     * Removes a specified amount of tokens from the current entity with an optional reason.
     *
     * <p>This method is marked as <strong>deprecated</strong>. It is advised to use an alternative
     * method that allows specifying additional details or has an updated signature.</p>
     *
     * @param amount the number of tokens to be removed
     * @param reason a brief explanation for the removal of tokens, can be a user-defined reason
     */
    @Deprecated
    @Override
    public void removeTokens(int amount, String reason) {
        removeTokens(amount, reason, null);
    }

    /**
     * Removes a specified balance from an account or system. This method should be used with caution
     * as it is deprecated and may be removed in future versions.
     * <p>
     * It reduces the balance by a given amount and records a reason for the deduction.
     * Consider using alternative methods for balance adjustment if available.
     *
     * @param amount The amount to be removed from the balance.
     *               <ul>
     *                  <li>Must be a positive integer.</li>
     *               </ul>
     * @param reason A string describing the reason for the balance removal.
     *               <ul>
     *                  <li>Cannot be null or empty.</li>
     *               </ul>
     */
    @Deprecated
    @Override
    public void removeBalance(int amount, String reason) {
        removeBalance(amount, reason, null);
    }

    /**
     * Removes a specified amount from the user's balance and logs the transaction with a reason.
     * This method handles balance adjustments, provides feedback via visual cues, and processes
     * the transaction using the specified callback.
     *
     * <p><b>Note:</b> This method is marked as deprecated and may be removed in a future version.
     * Consider using an alternative approach for managing balances.</p>
     *
     * @param amount The amount to be removed from the balance. Positive values indicate reductions,
     *               and negative values increase the balance.
     * @param reason A string describing the reason for the transaction. This reason is logged
     *               along with the transaction.
     * @param callback An instance of {@code TransactionCallback} to handle the result or state
     *                 of the transaction after processing.
     */
    @Deprecated
    @Override
    public void removeBalance(int amount, String reason, TransactionCallback callback) {
        if (amount == 0) return;
        if (amount > 0) {
            getActionBar().show(ChatColor.GREEN + "-" + CurrencyType.BALANCE.getIcon() + Math.abs(amount));
        } else {
            getActionBar().show(ChatColor.GREEN + "+" + CurrencyType.BALANCE.getIcon() + Math.abs(amount));
        }
        Core.getEconomy().addTransaction(uuid, -amount, reason, CurrencyType.BALANCE, callback);
    }

    /**
     * Removes a specified amount of tokens from a user's balance and registers the transaction.
     * <p>
     * This method also displays the token adjustment in the user's action bar.
     * A callback can be executed upon completion of the transaction.
     * </p>
     * <p>
     * <strong>Note:</strong> This method is marked as deprecated and may be removed in future versions.
     * </p>
     *
     * @param amount   The amount of tokens to remove. Positive values decrease the balance, negative values increase it.
     * @param reason   The reason or description for the transaction. Used for record-keeping purposes.
     * @param callback The {@link TransactionCallback} to execute once the transaction is processed.
     */
    @Deprecated
    @Override
    public void removeTokens(int amount, String reason, TransactionCallback callback) {
        if (amount == 0) return;
        if (amount > 0) {
            getActionBar().show(ChatColor.YELLOW + "-" + CurrencyType.TOKENS.getIcon() + Math.abs(amount));
        } else {
            getActionBar().show(ChatColor.YELLOW + "+" + CurrencyType.TOKENS.getIcon() + Math.abs(amount));
        }
        Core.getEconomy().addTransaction(uuid, -amount, reason, CurrencyType.TOKENS, callback);
    }

    /**
     * Adds the specified amount of Adventure Coins to the player's total and logs the action.
     * <p>
     * If the amount is greater than zero, it notifies the player with a sound and messages,
     * and records the transaction in the system.
     *
     * @param amount the number of Adventure Coins to add. If the value is zero, no action is taken.
     *               Positive values will increase the player's Adventure Coins balance.
     * @param reason the reason or description for adding the Adventure Coins. This will be logged
     *               as part of the transaction record.
     */
    @Override
    public void addAdventureCoins(int amount, String reason) {
        if (amount == 0) return;
        if (amount > 0) {
            playSound(getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0f);
            sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "-------------------------");
            sendMessage(ChatColor.GREEN + "You earned " + amount + " Adventure Coins!");
            sendMessage(ChatColor.GREEN + "Giving you a total of " + (getAdventureCoins() + amount) + " Adventure Coins!");
            sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "-------------------------");
            Core.getEconomy().addTransaction(uuid, amount, reason, CurrencyType.ADVENTURE, null);
        }
    }

    /**
     * Retrieves the number of Adventure Coins for the current user.
     * <p>
     * This method queries the database to fetch the amount of Adventure Coins
     * associated with the user's unique identifier (UUID).
     *
     * @return the number of Adventure Coins the user currently possesses as an integer.
     */
    @Override
    public int getAdventureCoins() {
        return Core.getMongoHandler().getCurrency(getUuid(), CurrencyType.ADVENTURE);
    }

    /**
     * Adds a statistic for the specified game type and statistic type with the given amount.
     * <p>
     * This method updates the database with the statistic information
     * and triggers a game statistic change event.
     *
     * @param gameType       the type of the game for which the statistic is being updated
     * @param statisticType  the type of the statistic to add (e.g., score, kills, wins)
     * @param amount         the numerical value to be added to the statistic
     */
    @Override
    public void addStatistic(GameType gameType, StatisticType statisticType, int amount) {
        Core.getMongoHandler().addGameStat(gameType, statisticType, amount, this);
        new GameStatisticChangeEvent(this, gameType, statisticType, amount).call();
    }

    /**
     * Retrieves a specific game statistic for the given game type and statistic type.
     * <p>
     * This method fetches data from the underlying persistence layer to
     * obtain the requested statistic value.
     * </p>
     *
     * @param gameType the type of the game for which the statistic is being requested.
     *                 It defines the context of the statistic.
     * @param statisticType the type of the statistic to retrieve.
     *                      This specifies what kind of data is being queried.
     * @return the value of the requested game statistic as an integer.
     */
    @Override
    public int getStatistic(GameType gameType, StatisticType statisticType) {
        return Core.getMongoHandler().getGameStat(gameType, statisticType, this);
    }

    /**
     * Retrieves the inventory view currently open for the player, if any.
     * <p>
     * This method checks if the player's status is {@code JOINED}. If the player is not in the {@code JOINED}
     * status or if no inventory is open, this method will return {@code Optional.empty()}. Otherwise, it returns
     * an {@code Optional} containing the currently open {@link InventoryView}.
     * </p>
     *
     * @return an {@code Optional} containing the {@link InventoryView} currently open for the player,
     * or {@code Optional.empty()} if the player does not have an open inventory or is not in the {@code JOINED} state.
     */
    @Override
    public Optional<InventoryView> getOpenInventory() {
        if (!getStatus().equals(PlayerStatus.JOINED)) return Optional.empty();
        return Optional.ofNullable(getBukkitPlayer().getOpenInventory());
    }

    /**
     * Retrieves the vehicle that the player is currently riding, if any.
     * <p>
     * This method checks if the player's status is {@code PlayerStatus.JOINED}. If the player
     * is not in the joined state, it returns an empty {@code Optional}. Otherwise, it attempts to
     * retrieve the vehicle of the underlying Bukkit player.
     *
     * @return An {@code Optional} containing the {@code Entity} representing the player's vehicle
     *         if the player is in a vehicle, or an empty {@code Optional} if no vehicle is
     *         associated or the player is not in the joined state.
     */
    @Override
    public Optional<Entity> getVehicle() {
        if (!getStatus().equals(PlayerStatus.JOINED)) return Optional.empty();
        return Optional.ofNullable(getBukkitPlayer().getVehicle());
    }

    /**
     * Sets the player's level in the game if the player has a status of {@code JOINED}.
     * <p>
     * This method will only update the level if the player's current status is {@code JOINED}.
     * If the status is not {@code JOINED}, the method execution will immediately return without any changes.
     *
     * @param level the new level to be set for the player. This value determines the player's current level in the game.
     */
    @Override
    public void setLevel(int level) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return;
        getBukkitPlayer().setLevel(level);
    }

    /**
     * Retrieves the level of the player based on their status.
     * <p>
     * This method determines the player's level by checking their current status:
     * <ul>
     * <li>If the player's status is {@code JOINED}, their level is retrieved from the Bukkit player instance.</li>
     * <li>Otherwise, it defaults to 1.</li>
     * </ul>
     *
     * @return the player's level if their status is {@code JOINED}, or 1 if the status is not {@code JOINED}.
     */
    @Override
    public int getLevel() {
        return getStatus().equals(PlayerStatus.JOINED) ? getBukkitPlayer().getLevel() : 1;
    }

    /**
     * Retrieves the velocity of a player.
     *
     * <p>This method checks the player's status and ensures the player is active
     * and valid before attempting to retrieve the velocity. If the player is not
     * joined or their underlying Bukkit player instance is null, it will return
     * a default velocity (a zero vector).</p>
     *
     * @return A {@link Vector} representing the player's velocity:
     * <ul>
     * <li>A zero vector if the player's status is not {@code PlayerStatus.JOINED}.</li>
     * <li>A zero vector if the player's Bukkit player instance is null.</li>
     * <li>The velocity retrieved from the player's Bukkit instance otherwise.</li>
     * </ul>
     */
    @Override
    public Vector getVelocity() {
        if (!getStatus().equals(PlayerStatus.JOINED)) return new Vector();
        if (getBukkitPlayer() == null) return new Vector();
        return getBukkitPlayer().getVelocity();
    }

    /**
     * Sets the velocity of the player using the provided vector.
     * <p>
     * This method will only set the velocity if the player's status
     * is {@code PlayerStatus.JOINED} and the corresponding Bukkit player
     * instance is not null.
     *
     * @param vector the velocity vector to be applied to the player.
     */
    @Override
    public void setVelocity(Vector vector) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setVelocity(vector);
    }

    /**
     * Sets the experience points (exp) of the player to the specified value if the player
     * is in the "JOINED" status and the Bukkit player instance is not null.
     *
     * <p>This method updates the experience points of the associated Bukkit player object.</p>
     *
     * @param exp the new experience value to set for the player. It should be a float
     *            value representing the player's experience progress between levels.
     */
    @Override
    public void setExp(float exp) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().setExp(exp);
    }

    /**
     * Retrieves the experience points of the current player.
     * <p>
     * This method checks whether the player's status is <code>PlayerStatus.JOINED</code> and
     * if the corresponding Bukkit player instance exists. If these requirements are not met,
     * the method returns <code>0</code>. Otherwise, it returns the experience points
     * of the associated Bukkit player.
     *
     * @return the current experience points of the player as a float, or <code>0</code>
     * if the player status is not <code>JOINED</code> or the Bukkit player instance is null.
     */
    @Override
    public float getExp() {
        if (!getStatus().equals(PlayerStatus.JOINED)) return 0;
        if (getBukkitPlayer() == null) return 0;
        return getBukkitPlayer().getExp();
    }

    /**
     * Assigns honor to a user or entity within the application.
     * <p>
     * This method calls an internal implementation to provide a specified amount of honor,
     * while setting a default context identifier as "plugin".
     * </p>
     *
     * @param amount The amount of honor to be assigned. Must be a positive integer.
     */
    @Override
    public void giveHonor(int amount) {
        giveHonor(amount, "plugin");
    }

    /**
     * Awards honor points to a recipient with an optional reason provided.
     * <p>
     * This method assigns a specified amount of honor points and includes a reason
     * for tracking or documentation purposes. By default, it internally calls another
     * variant of the {@code giveHonor} method with a null value for additional data.
     *
     * @param amount the number of honor points to be awarded. Must be a positive integer.
     * @param reason the reason or description for awarding the honor points. Cannot be null or empty.
     */
    @Override
    public void giveHonor(int amount, String reason) {
        giveHonor(amount, reason, null);
    }

    /**
     * Modifies the user's honor points by the specified amount, displays an appropriate message,
     * and logs the transaction.
     *
     * <p>The method handles both positive and negative honor changes, displaying a message
     * to the user indicating the change in honor.</p>
     *
     * @param amount The amount of honor to be modified. Positive values add honor, and negative
     *               values subtract honor. If the amount is zero, no changes are made.
     * @param reason A brief description or reason for the honor modification. It is used to log
     *               the transaction for future reference.
     * @param callback A {@link TransactionCallback} instance that can be used to handle the result
     *                 of the honor transaction.
     */
    @Override
    public void giveHonor(int amount, String reason, TransactionCallback callback) {
        if (amount == 0) return;
        if (amount > 0) {
            getActionBar().show(ChatColor.LIGHT_PURPLE + "+" + Math.abs(amount) + " Honor");
        } else {
            getActionBar().show(ChatColor.LIGHT_PURPLE + "-" + Math.abs(amount) + " Honor");
        }
        Core.getHonorManager().addTransaction(uuid, amount, reason, null);
    }

    /**
     * Removes a specified amount of honor from the player.
     * <p>
     * This method adjusts the player's honor level by decreasing it. The change is logged
     * under the specified context for tracking purposes.
     *
     * @param amount The amount of honor to remove. Must be a non-negative integer.
     */
    @Override
    public void removeHonor(int amount) {
        removeHonor(amount, "plugin");
    }

    /**
     * Removes the specified amount of honor for a given reason.
     * <p>
     * This method is an overridden implementation that delegates the removal process
     * to another method while allowing an optional parameter.
     * </p>
     *
     * @param amount the quantity of honor to be removed; must be a non-negative integer.
     * @param reason the reason for the removal of honor; cannot be null or empty.
     */
    @Override
    public void removeHonor(int amount, String reason) {
        removeHonor(amount, reason, null);
    }

    /**
     * Removes a given amount of honor from an entity and optionally logs the transaction.
     * The change in honor is displayed to the user via an action bar.
     *
     * <p>This method handles both positive and negative changes to honor. When the amount
     * of honor to remove is zero, the method returns immediately without further action.</p>
     *
     * @param amount  The amount of honor to remove. Can be negative for adding honor.
     * @param reason  A textual explanation for the transaction, used for logging.
     * @param callback  A callback function that is triggered upon completion of the transaction.
     */
    @Override
    public void removeHonor(int amount, String reason, TransactionCallback callback) {
        if (amount == 0) return;
        if (amount < 0) {
            getActionBar().show(ChatColor.LIGHT_PURPLE + "+" + Math.abs(amount) + " Honor");
        } else {
            getActionBar().show(ChatColor.LIGHT_PURPLE + "-" + Math.abs(amount) + " Honor");
        }
        Core.getHonorManager().addTransaction(uuid, amount, reason, null);
    }

    /**
     * Sets the honor value for the current object. This method adjusts
     * the internal honor state using the specified amount and a default
     * source identifier.
     *
     * <p><strong>Usage:</strong> This method is typically called to update
     * the honor value associated with an object when changes occur
     * within the system.</p>
     *
     * @param amount the integer value to set the honor to.
     */
    @Override
    public void setHonor(int amount) {
        setHonor(amount, "plugin");
    }

    /**
     * Sets the honor value for a player and updates it asynchronously in the database.
     *
     * <p>This method is responsible for setting the {@code honor} attribute of the player and
     * initiating an asynchronous task to persist the changes in the database. The reason for the
     * change can also be specified.</p>
     *
     * @param amount the new honor value to be set.
     * @param reason the reason for modifying the honor value.
     */
    @Override
    public void setHonor(int amount, String reason) {
        this.honor = amount;
        Core.runTaskAsynchronously(Core.getInstance(), () -> Core.getMongoHandler().setHonor(getUuid(), amount, reason));
    }

    /**
     * Loads the given honor value into the system.
     *
     * <p>This method sets the internal honor field to the provided value,
     * allowing it to be used for further operations or processing.</p>
     *
     * @param honor the integer value representing the honor to be loaded
     */
    @Override
    public void loadHonor(int honor) {
        this.honor = honor;
    }

    /**
     * Sends the given map view to the player's client if the player has joined the game and is valid.
     * <p>
     * This method ensures that the provided map is only sent under the following conditions:
     * <ul>
     *   <li>The {@code view} parameter is not {@code null}.</li>
     *   <li>The player's current status is {@code PlayerStatus.JOINED}.</li>
     *   <li>The player's Bukkit instance is not {@code null}.</li>
     * </ul>
     * If any of these conditions are not met, the method will simply return without performing any action.
     *
     * @param view The {@link MapView} object representing the map to be sent to the player. Must not be {@code null}.
     */
    @Override
    public void sendMap(MapView view) {
        if (view == null) return;
        if (!getStatus().equals(PlayerStatus.JOINED)) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().sendMap(view);
    }

    /**
     * Removes the specified potion effect from the player.
     *
     * <p>This method will only execute if the player has a status of
     * {@code PlayerStatus.JOINED} and the internal reference to the
     * Bukkit player is not null. If the conditions are not met, the
     * potion effect removal is skipped.
     *
     * @param type the type of potion effect to be removed from the player.
     *             Must not be null.
     */
    @Override
    public void removePotionEffect(PotionEffectType type) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return;
        if (getBukkitPlayer() == null) return;
        getBukkitPlayer().removePotionEffect(type);
    }

    /**
     * Adds a potion effect to the player if their status is set to {@code PlayerStatus.JOINED}
     * and the effect can be successfully applied to the Bukkit player instance.
     *
     * <p>This method verifies the player's current status before attempting to add
     * the specified potion effect. If the player is eligible, the effect is delegated
     * to the underlying Bukkit player for application.</p>
     *
     * @param effect The {@link PotionEffect} to be applied to the player. This specifies the
     *               type of effect, its duration, amplifier, and other properties.
     * @return {@code true} if the potion effect was successfully added; {@code false} otherwise.
     *         This may return {@code false} if the player's status is not {@code PlayerStatus.JOINED}
     *         or if the Bukkit player fails to accept the effect.
     */
    @Override
    public boolean addPotionEffect(PotionEffect effect) {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer().addPotionEffect(effect);
    }

    /**
     * Adds a potion effect to the player if certain conditions are met.
     * <p>
     * This method attempts to apply the given {@link PotionEffect} to the player. The application
     * will succeed only if the player's status is {@code PlayerStatus.JOINED}.
     *
     * @param effect The {@link PotionEffect} to apply to the player.
     * @param force Whether to forcefully apply the effect even if an existing effect of the same type is present.
     *              If {@code true}, the effect will be applied regardless of existing effects. If {@code false}, the
     *              method will not apply the new effect if it conflicts with an existing one.
     *
     * @return {@code true} if the specified potion effect was successfully added to the player; {@code false} otherwise.
     */
    @Override
    public boolean addPotionEffect(PotionEffect effect, boolean force) {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer().addPotionEffect(effect, force);
    }

    /**
     * Checks if the player currently has the specified potion effect.
     *
     * <p>This method verifies if the player is in the correct status (<code>PlayerStatus.JOINED</code>)
     * and then checks if the player possesses the given potion effect.</p>
     *
     * @param type the {@link PotionEffectType} to check for. It specifies the type of potion effect to verify.
     *             Must not be <code>null</code>.
     * @return <code>true</code> if the player has the specified potion effect and the status is <code>PlayerStatus.JOINED</code>;
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean hasPotionEffect(PotionEffectType type) {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer().hasPotionEffect(type);
    }

    /**
     * Adds a collection of potion effects to the player.
     * <p>
     * This method applies the specified potion effects to the player,
     * provided the player's status is <code>JOINED</code>. The effects
     * will not be applied if the player's status is not <code>JOINED</code>.
     * </p>
     *
     * @param effects a collection of {@link PotionEffect} objects to be applied to the player.
     *                Each potion effect specifies the type, duration, and amplifier of the effect.
     * @return <code>true</code> if all the potion effects were successfully added to the player and the player's status is <code>JOINED</code>;
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean addPotionEffects(Collection<PotionEffect> effects) {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer().addPotionEffects(effects);
    }

    /**
     * Retrieves a collection of active potion effects currently applied to the player.
     * <p>
     * This method checks the player's status to ensure the player is in the "JOINED" state
     * and whether the Bukkit player instance is available. If these conditions are not met,
     * an empty collection is returned. Otherwise, it delegates to the underlying Bukkit player
     * to retrieve the active potion effects.
     *
     * @return A {@link Collection} of {@link PotionEffect} objects representing the
     * active potion effects applied to the player. Returns an empty collection if the
     * player is not in the "JOINED" state or if the Bukkit player instance is null.
     */
    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        if (!getStatus().equals(PlayerStatus.JOINED)) return Collections.emptyList();
        if (getBukkitPlayer() == null) return Collections.emptyList();
        return getBukkitPlayer().getActivePotionEffects();
    }

    /**
     * Retrieves the active potion effect of the specified type for the player.
     * <p>
     * If the player's current status is not {@code PlayerStatus.JOINED} or the
     * underlying Bukkit player object is null, this method will return {@code null}.
     * Otherwise, it will retrieve the potion effect of the given type currently
     * applied to the player.
     *
     * @param type the {@link PotionEffectType} to search for. Must not be {@code null}.
     *             Represents the type of potion effect to retrieve.
     * @return the {@link PotionEffect} of the specified {@code type} if it exists and the player
     *         meets the required conditions; otherwise, returns {@code null}.
     */
    @Override
    public PotionEffect getPotionEffect(PotionEffectType type) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return null;
        if (getBukkitPlayer() == null) return null;
        return getBukkitPlayer().getPotionEffect(type);
    }

    /**
     * Checks if the player is currently inside a vehicle.
     *
     * <p>This method verifies if the player's status is {@code PlayerStatus.JOINED},
     * and that the underlying Bukkit player object is not null, followed by checking
     * if the player is inside a vehicle in the game.
     *
     * @return {@code true} if the player is inside a vehicle, {@code false} otherwise.
     */
    @Override
    public boolean isInsideVehicle() {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer() != null && getBukkitPlayer().isInsideVehicle();
    }

    /**
     * Ejects the player from the vehicle they are currently riding, if applicable.
     *
     * <p>This method checks whether the player is in the proper status (i.e., {@code JOINED})
     * and whether a valid Bukkit player instance is available before attempting to eject
     * them from the vehicle.
     *
     * @return {@code true} if the player was successfully ejected from the vehicle;
     *         {@code false} otherwise.
     */
    @Override
    public boolean eject() {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer() != null && getBukkitPlayer().eject();
    }

    /**
     * Retrieves the window ID of the active container for the player.
     * <p>
     * This method uses reflection to access underlying NMS (Net Minecraft Server)
     * classes and determine the active container's window ID for the player in
     * the current Minecraft version.
     * </p>
     *
     * @return The window ID of the player's active container. If an error occurs
     *         during reflection or NMS class access, the method will return 0.
     */
    @Override
    public int getWindowId() {
        try {
            Object craftPlayer = Class.forName("org.bukkit.craftbukkit." + Core.getMinecraftVersion() +
                    ".entity.CraftPlayer").cast(getBukkitPlayer());
            Method m = craftPlayer.getClass().getDeclaredMethod("getHandle");
            Object entityPlayer = m.invoke(craftPlayer);
            Object entityHuman = Class.forName("net.minecraft.server." + Core.getMinecraftVersion() +
                    ".EntityHuman").cast(entityPlayer);
            Field field = entityHuman.getClass().getField("activeContainer");
            field.setAccessible(true);
            Object container = field.get(entityHuman);
            Field windowIdField = container.getClass().getField("windowId");
            return (int) windowIdField.get(container);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                NoSuchFieldException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Determines if the player is currently inside a vehicle in the game.
     * <p>
     * This method checks the player's status to ensure they have joined the game
     * and verifies that the associated Bukkit player instance is not null and is inside a vehicle.
     *
     * @return <code>true</code> if the player is in a joined state, the Bukkit player instance is not null,
     * and the player is inside a vehicle; <code>false</code> otherwise.
     */
    @Override
    public boolean isInVehicle() {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer() != null && getBukkitPlayer().isInsideVehicle();
    }

    /**
     * Attempts to make the player leave the vehicle they are currently in.
     * <p>
     * This method checks if the player's current status is {@code JOINED} and
     * ensures that a valid Bukkit player instance exists. If both conditions
     * are met, it will invoke the {@code leaveVehicle()} method on the Bukkit
     * player object to remove the player from any vehicle they are occupying.
     * </p>
     *
     * @return <code>true</code> if the player successfully leaves the vehicle;
     *         <code>false</code> otherwise. The result could be influenced by
     *         the player's current status, the existence of a valid Bukkit player
     *         instance, or the success of the underlying Bukkit API call.
     */
    @Override
    public boolean leaveVehicle() {
        return getStatus().equals(PlayerStatus.JOINED) && getBukkitPlayer() != null && getBukkitPlayer().leaveVehicle();
    }

    /**
     * Sets metadata for the player if the player has a status of "JOINED".
     *
     * <p>This method updates the metadata associated with this player using the provided
     * metadata name and value. The metadata will only be set if the current player
     * status equals {@code PlayerStatus.JOINED}.
     *
     * @param name the name of the metadata key to be set. It must not be {@code null}.
     * @param metadata the {@link MetadataValue} that will be associated with the specified
     *                 metadata key. It must not be {@code null}.
     */
    @Override
    public void setMetadata(String name, MetadataValue metadata) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return;
        getBukkitPlayer().setMetadata(name, metadata);
    }

    /**
     * Retrieves metadata for a provided name if the player status equals {@code PlayerStatus.JOINED}.
     *
     * <p>This method fetches the metadata values associated with the given name for the
     * Bukkit player. If the player's status is not {@code PlayerStatus.JOINED}, an empty list
     * is returned.</p>
     *
     * @param name the name key used to retrieve metadata values
     * @return a list of {@code MetadataValue} objects associated with the given name for the player,
     *         or an empty list if the player is not in the {@code PlayerStatus.JOINED} status
     */
    @Override
    public List<MetadataValue> getMetadata(String name) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return new ArrayList<>();
        return getBukkitPlayer().getMetadata(name);
    }

    /**
     * Removes a metadata entry from the player associated with the specified plugin.
     * <p>
     * This operation will only be executed if the player's status is {@code PlayerStatus.JOINED}.
     * </p>
     *
     * @param name   the name of the metadata entry to be removed.
     * @param plugin the plugin that owns the metadata entry.
     */
    @Override
    public void removeMetadata(String name, Plugin plugin) {
        if (!getStatus().equals(PlayerStatus.JOINED)) return;
        getBukkitPlayer().removeMetadata(name, plugin);
    }

    /**
     * Calculates the total amount of time the user has been online.
     * <p>
     * This method determines the duration in milliseconds since the user joined
     * by subtracting the recorded join time from the current system time.
     * </p>
     *
     * @return the online time in milliseconds as a <code>long</code> value.
     */
    @Override
    public long getOnlineTime() {
        return System.currentTimeMillis() - joinTime;
    }

    /**
     * Sets the achievement manager for the player and processes any queued achievements.
     * <p>
     * Assigns the provided {@link CPlayerAchievementManager} instance to the {@code achievementManager} field.
     * Any achievements that were queued before the achievement manager was set are granted to the player using the
     * newly set achievement manager. After processing, the queue of achievements is cleared.
     *
     * @param manager The {@link CPlayerAchievementManager} instance to set as the player's achievement manager.
     */
    @Override
    public void setAchievementManager(CPlayerAchievementManager manager) {
        this.achievementManager = manager;
        for (Integer i : new ArrayList<>(queuedAchievements)) {
            achievementManager.giveAchievement(i);
        }
        queuedAchievements.clear();
    }
}
