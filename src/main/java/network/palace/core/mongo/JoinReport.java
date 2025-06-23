package network.palace.core.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.player.Rank;

import java.util.UUID;

/**
 * Represents a report of a player's join event, including their unique identifier
 * and rank in the system.
 * <p>
 * The JoinReport class encapsulates information about a player's session at the
 * time of their joining. This information primarily includes:
 * - A unique identifier (UUID) to distinguish the player.
 * - The rank assigned to the player in the system, represented by the {@link Rank} enum.
 * <p>
 * Instances of JoinReport are designed to be immutable and thread-safe.
 */
@AllArgsConstructor
public class JoinReport {
    /**
     * The universally unique identifier (UUID) associated with a player.
     * This UUID is used to distinctly identify the player across the system.
     * <p>
     * It is immutable and ensures that each player is assigned a unique
     * identifier upon joining.
     */
    @Getter private final UUID uuid;

    /**
     * Represents the rank assigned to a player in the system.
     * <p>
     * The {@code rank} field corresponds to a player's status and permissions within
     * the system, as defined in the {@link Rank} enumeration. Each rank defines attributes
     * such as display name, chat color, operational status, and permissions.
     * <p>
     * It provides contextual information about the player's role, privileges, and
     * hierarchical position in the system.
     * <p>
     * This field is immutable and cannot be changed once the {@code JoinReport} object
     * is created.
     */
    @Getter private final Rank rank;
}
