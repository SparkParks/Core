package network.palace.core.packets.server.scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.IntEnum;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import network.palace.core.packets.AbstractPacket;
import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.List;

/**
 * Represents a wrapper for the packet {@code Play.Server.SCOREBOARD_TEAM}.
 * This class is used to handle scoreboard team-related packets, allowing
 * the modification and retrieval of specific properties for a team such
 * as team name, display name, prefixes, suffixes, visibility, collision
 * rules, and more. It supports operations for creating, updating, and
 * removing scoreboard teams or manipulating their player memberships.
 */
public class WrapperPlayServerScoreboardTeam extends AbstractPacket {
    /**
     * Represents the packet type associated with the SCOREBOARD_TEAM functionality in the Play.Server context.
     * This defines the type of the packet being handled by the WrapperPlayServerScoreboardTeam class.
     */
    public static final PacketType TYPE =
            PacketType.Play.Server.SCOREBOARD_TEAM;

    /**
     * Constructs a new instance of the WrapperPlayServerScoreboardTeam class.
     * This wrapper is designed for handling the Minecraft scoreboard team packet, allowing for creation,
     * modification, and deletion of teams, along with configuration of team properties such as visibility settings
     * and player assignments.
     *
     * The constructor initializes a new packet container with the specified packet type and writes default values
     * to ensure proper setup before further configuration.
     *
     * Typically used to manage teams within the scoreboard system for use cases such as customizing player name displays,
     * visibility rules, or collision behaviors.
     *
     * @throws IllegalArgumentException if the packet type is invalid or if initialization fails.
     */
    public WrapperPlayServerScoreboardTeam() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Constructs a new instance of the WrapperPlayServerScoreboardTeam class.
     * This wrapper is designed for handling the Minecraft scoreboard team packet, allowing for
     * creation, modification, and deletion of teams, along with configuration of team
     * properties such as visibility settings and player assignments.
     *
     * @param packet the handle to the raw packet data, represented as a PacketContainer. This must not
     *               be null and must contain a valid packet of the specified type. It is used for initializing
     *               the scoreboard team wrapper with the provided packet data.
     */
    public WrapperPlayServerScoreboardTeam(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * The Mode class is a static inner class within the WrapperPlayServerScoreboardTeam class
     * that extends IntEnum. It represents the different operational modes for handling
     * scoreboard teams in Minecraft packets. These modes dictate the specific operations
     * to be performed on a team, such as creating, removing, updating, adding, or removing players.
     *
     * Modes:
     * - TEAM_CREATED (0): Indicates that a new team is being created.
     * - TEAM_REMOVED (1): Indicates that an existing team is being removed.
     * - TEAM_UPDATED (2): Indicates that the information about a team is being updated.
     * - PLAYERS_ADDED (3): Indicates that players are being added to a team.
     * - PLAYERS_REMOVED (4): Indicates that players are being removed from a team.
     *
     * This class also includes a singleton instance for efficient access.
     */
    public static class Mode extends IntEnum {
        public static final int TEAM_CREATED = 0;
        public static final int TEAM_REMOVED = 1;
        public static final int TEAM_UPDATED = 2;
        public static final int PLAYERS_ADDED = 3;
        public static final int PLAYERS_REMOVED = 4;

        private static final Mode INSTANCE = new Mode();

        public static Mode getInstance() {
            return INSTANCE;
        }
    }

    /**
     * Retrieves the name of the scoreboard team.
     *
     * @return The team name as a string.
     */
    public String getName() {
        return handle.getStrings().read(0);
    }

    /**
     * Sets the name of the scoreboard team.
     *
     * @param value The new name to assign to the scoreboard team. The name must be a string
     *              with a maximum length of 16 characters. It serves as the unique identifier
     *              for the team within the scoreboard system.
     */
    public void setName(String value) {
        handle.getStrings().write(0, value);
    }

    /**
     * Retrieves the display name of the scoreboard team.
     *
     * @return The display name of the team, represented as a {@code WrappedChatComponent}.
     */
    public WrappedChatComponent getDisplayName() {
        return handle.getChatComponents().read(0);
    }

    /**
     * Sets the display name of the scoreboard team.
     *
     * @param value The new display name to assign to the team,
     *              represented as a {@code WrappedChatComponent}.
     *              This value is displayed in the scoreboard
     *              and used for identifying the team visually.
     */
    public void setDisplayName(WrappedChatComponent value) {
        handle.getChatComponents().write(0, value);
    }

    /**
     * Retrieves the prefix of the scoreboard team.
     *
     * @return The prefix of the team, represented as a {@code WrappedChatComponent}.
     */
    public WrappedChatComponent getPrefix() {
        return handle.getChatComponents().read(1);
    }

    /**
     * Sets the prefix of the scoreboard team.
     *
     * @param value The new prefix to assign to the scoreboard team,
     *              represented as a {@code WrappedChatComponent}.
     *              This value is displayed before the player's name in the team.
     */
    public void setPrefix(WrappedChatComponent value) {
        handle.getChatComponents().write(1, value);
    }

    /**
     * Retrieves the suffix of the scoreboard team.
     *
     * @return The suffix of the team, represented as a {@code WrappedChatComponent}.
     */
    public WrappedChatComponent getSuffix() {
        return handle.getChatComponents().read(2);
    }

    /**
     * Sets the suffix of the scoreboard team.
     *
     * @param value The new suffix to assign to the scoreboard team,
     *              represented as a {@code WrappedChatComponent}.
     *              This value is displayed after the player's name in the team.
     */
    public void setSuffix(WrappedChatComponent value) {
        handle.getChatComponents().write(2, value);
    }

    /**
     * Retrieves the visibility setting for name tags of the scoreboard team.
     * The visibility setting determines how name tags are displayed for players
     * on the team, such as always visible, never visible, or dependent on team rules.
     *
     * @return The name tag visibility as a string. Possible values include "always",
     *         "never", "hideForOtherTeams", and "hideForOwnTeam".
     */
    public String getNameTagVisibility() {
        return handle.getStrings().read(1);
    }

    /**
     * Sets the visibility setting for name tags of the scoreboard team.
     * The visibility determines how player name tags are displayed within
     * the team. This can be used to configure whether name tags are always
     * visible, hidden, or shown conditionally based on the team rules.
     *
     * @param value The visibility setting for name tags. Acceptable values include:
     *              - "always": Name tags are always visible.
     *              - "never": Name tags are never visible.
     *              - "hideForOtherTeams": Name tags are hidden from other teams.
     *              - "hideForOwnTeam": Name tags are hidden from the team itself.
     */
    public void setNameTagVisibility(String value) {
        handle.getStrings().write(1, value);
    }

    /**
     * Retrieves the color assigned to the scoreboard team.
     *
     * @return The {@code ChatColor} representing the team's color.
     */
    public ChatColor getColor() {
        return handle.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).read(0);
    }

    /**
     * Sets the color assigned to the scoreboard team. The color is used to visually represent the team
     * and is typically displayed in various in-game elements such as player names, prefixes, or suffixes.
     *
     * @param value The {@code ChatColor} to set for the team. Represents the desired color for the team.
     */
    public void setColor(ChatColor value) {
        handle.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, value);
    }

    /**
     * Retrieves the collision rule for the scoreboard team.
     * The collision rule determines how entities within the team interact in terms of collision.
     *
     * @return The collision rule as a string. Possible values may include "always", "never", "pushOtherTeams", or "pushOwnTeam".
     */
    public String getCollisionRule() {
        return handle.getStrings().read(2);
    }

    /**
     * Sets the collision rule for the scoreboard team.
     * The collision rule specifies how team members interact in terms of collision behavior.
     *
     * @param value The collision rule to set. Valid values may include:
     *              - "always": Collision is always allowed.
     *              - "never": Collision is never allowed.
     *              - "pushOtherTeams": Collision is allowed with members of other teams, but not within the same team.
     *              - "pushOwnTeam": Collision is allowed within the same team, but not with members of other teams.
     */
    public void setCollisionRule(String value) {
        handle.getStrings().write(2, value);
    }

    /**
     * Retrieves the list of players associated with the scoreboard team.
     *
     * @return A list of player names as strings. Each string represents the name of a player
     *         currently assigned to the team.
     */
    public List<String> getPlayers() {
        return (List<String>) handle.getSpecificModifier(Collection.class).read(0);
    }

    /**
     * Sets the list of players associated with the scoreboard team.
     * This method is used to define or update the players that belong to the team.
     *
     * @param value A list of player names as strings. Each string represents the name
     *              of a player to be assigned to the team. This list replaces any
     *              previously set players in the team.
     */
    public void setPlayers(List<String> value) {
        handle.getSpecificModifier(Collection.class).write(0, value);
    }

    /**
     * Retrieves the mode value associated with the scoreboard team.
     * The mode indicates the current operation or state related to the team.
     *
     * @return The mode value as an integer. Typical values may
     *         correspond to creation, removal, or update operations.
     */
    public int getMode() {
        return handle.getIntegers().read(0);
    }

    /**
     * Sets the mode value for the scoreboard team. The mode determines the current
     * operation or state of the team, such as creation, removal, or updating.
     *
     * @param value The mode value to set. Typical values may correspond to operations
     *              like team creation, removal, or updates. The valid range and specific
     *              meaning of the values should align with the underlying implementation
     *              of the scoreboard system.
     */
    public void setMode(int value) {
        handle.getIntegers().write(0, value);
    }

    /// old documentation
    /// Retrieve pack option data. Pack data is calculated as follows:
    /// <pre>
    /// <code>
    /// int data = 0;
    /// if (team.allowFriendlyFire()) {
    ///     data |= 1;
    /// }
    /// if (team.canSeeFriendlyInvisibles()) {
    ///     data |= 2;
    /// }
    /// </code>
    /// </pre>
    ///
    /// @return The current pack option data

    /**
     * Retrieves the pack option data for the scoreboard team.
     *
     * The pack option data typically represents a set of flags that control various
     * properties or behaviors of the scoreboard team. These flags might influence
     * settings such as visibility, friendly fire, or other team-related features.
     *
     * @return The pack option data as an integer, representing the encoded flags or options.
     */
    public int getPackOptionData() {
        return handle.getIntegers().read(1);
    }

    /**
     * Sets the pack option data for the scoreboard team.
     *
     * The pack option data typically represents a set of flags or settings
     * that control various properties or behaviors of the team, such as
     * visibility, friendly fire, or other team-related features.
     *
     * @param value The pack option data to set, represented as an integer.
     *              This value encodes the specific flags or settings for the team.
     */
    public void setPackOptionData(int value) {
        handle.getIntegers().write(1, value);
    }
}