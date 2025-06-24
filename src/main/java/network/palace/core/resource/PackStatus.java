package network.palace.core.resource;

/**
 * Represents the possible statuses for a resource pack during its lifecycle.
 * This enum is primarily utilized to track and manage the state of a resource pack
 * as it is interacted with by players or handled by the system.
 *
 * Pack statuses include the following:
 * - ACCEPTED: Indicates that the player has accepted the resource pack.
 * - LOADED: Indicates that the resource pack has been successfully loaded by the player.
 * - FAILED: Indicates that the resource pack failed to load or download.
 * - DECLINED: Indicates that the player declined the resource pack.
 */
public enum PackStatus {

    /**
     * Indicates that the player has accepted the resource pack.
     * This status reflects a positive acknowledgment from the player,
     * signifying their agreement to download and use the specified resource pack.
     */
    ACCEPTED,

    /**
     * Indicates that the resource pack has been successfully loaded by the player.
     * This status signifies that the resource pack was accepted, downloaded, and applied
     * without issues, making it ready for use in the game environment.
     */
    LOADED,

    /**
     * Indicates that the resource pack failed to load or download.
     * This status is used to signify an unsuccessful attempt to apply the resource pack,
     * either due to download errors, hash mismatches, or compatibility issues.
     */
    FAILED,

    /**
     * Indicates that the player declined the resource pack.
     * This status is used to represent a negative action where the player explicitly
     * rejects using the specified resource pack, preventing it from being downloaded or applied.
     */
    DECLINED
}
