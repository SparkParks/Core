package network.palace.core.player.impl.managers;

import lombok.AllArgsConstructor;
import network.palace.core.player.CPlayer;
import network.palace.core.player.CPlayerParticlesManager;
import network.palace.core.player.PlayerStatus;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * The {@code CorePlayerParticlesManager} class is an implementation of the {@link CPlayerParticlesManager} interface
 * that manages the spawning and display of particle effects for a specific player context.
 * <p>
 * This class provides various methods to send particle effects either at the player's current location,
 * a specific world location, or with customization for the number of particles, their spread, and additional data.
 * </p>
 * <p>
 * It ensures that particles are only sent when the player is actively in the game (i.e., has a status of {@link PlayerStatus#JOINED})
 * and the player has a valid corresponding Bukkit player instance.
 * </p>
 *
 * <h3>Features:</h3>
 * <ul>
 *     <li>Send a single particle type to the player's location or a specific location.</li>
 *     <li>Spawn particles with customizable count and positional offsets.</li>
 *     <li>Include additional data (extra parameter) for specific particle customization (e.g., speed or size).</li>
 * </ul>
 *
 * <h3>Behavior:</h3>
 * <ul>
 *     <li>If the player's status is not {@link PlayerStatus#JOINED}, no particles will be sent.</li>
 *     <li>If the player's Bukkit instance is invalid or null, no particles will be sent.</li>
 * </ul>
 */
@AllArgsConstructor
public class CorePlayerParticlesManager implements CPlayerParticlesManager {

    /**
     * Represents a {@link CPlayer} instance associated with this {@code CorePlayerParticlesManager}.
     * <p>
     * This variable holds the context of the player for which particle effects will be managed and displayed.
     * It is essential for determining the player's status, retrieving their current location, and accessing their Bukkit player instance.
     * </p>
     *
     * <h3>Responsibilities:</h3>
     * <ul>
     *     <li>Tracks the player to whom particle effects should be sent.</li>
     *     <li>Used to determine whether the player has a valid {@link PlayerStatus} to allow particle spawning.</li>
     *     <li>Provides access to the Bukkit API through the player's Bukkit representation.</li>
     * </ul>
     *
     * <h3>Key Notes:</h3>
     * <ul>
     *     <li>If the player's status is not {@code PlayerStatus.JOINED}, no particle effects will be sent.</li>
     *     <li>If the player's Bukkit instance is {@code null}, particle-related operations will be skipped.</li>
     * </ul>
     */
    private final CPlayer player;

    /**
     * Sends the specified {@link Particle} effect to the player with a default count of 1 particle.
     * This method delegates to {@code send(Particle, int)} with the count set to 1.
     * <p>
     * Only sends the particle effect if the player's status is {@link PlayerStatus#JOINED}
     * and the player has a valid Bukkit player instance.
     * </p>
     *
     * @param particle the {@link Particle} type to send; must not be {@code null}.
     */
    @Override
    public void send(Particle particle) {
        send(particle, 1);
    }

    /**
     * Sends a specified particle effect to the player's current location.
     *
     * <p>This method allows sending a defined particle type with a customizable count
     * directly to the player's current location as retrieved via {@code player.getLocation()}.</p>
     *
     * @param particle the {@link Particle} type to be displayed.
     *                 This defines the visual effect to be used when rendering the particle.
     * @param count the number of particles to spawn at the player's location.
     *              A higher count results in a denser display of particles.
     */
    @Override
    public void send(Particle particle, int count) {
        send(player.getLocation(), particle, count);
    }

    /**
     * Sends a particle effect to a specific location with a specified particle type and count.
     * <p>
     * This method invokes the particle effect at the given world location with the provided type and number of particles.
     * It internally defaults positional offsets and extra data to zero.
     * </p>
     *
     * @param location The location where the particles will be displayed. Must not be null.
     * @param particle The type of particle to display. Must not be null.
     * @param count The number of particles to spawn. Must be a positive integer.
     */
    @Override
    public void send(Location location, Particle particle) {
        send(location, particle, 1);
    }

    /**
     * Sends particle effects to a specified location with a given particle type and count.
     * <p>
     * This method defaults the positional offsets (spread) and additional data parameters to zero.
     * </p>
     *
     * @param location The {@link Location} where the particles will be displayed. Cannot be null.
     * @param particle The {@link Particle} type to display. Cannot be null.
     * @param count    The number of particles to spawn at the provided location.
     */
    @Override
    public void send(Location location, Particle particle, int count) {
        send(location, particle, count, 0, 0, 0, 0);
    }

    /**
     * Sends particle effects to a specified location for the player, using the specified parameters for
     * particle type, count, positional offsets, and additional customization.
     *
     * <p>
     * This method ensures that particles are only sent if the player is in a {@link PlayerStatus#JOINED} state
     * and has a valid Bukkit {@link Player} instance. If either condition is not met, no particles will be sent.
     * </p>
     *
     * @param location the {@link Location} where the particles should be displayed.
     * @param particle the {@link Particle} type to display.
     * @param count the number of particles to spawn.
     * @param offsetX the offset in the X direction for particle positions.
     * @param offsetY the offset in the Y direction for particle positions.
     * @param offsetZ the offset in the Z direction for particle positions.
     * @param extra additional particle-specific data, e.g., speed or size.
     */
    @Override
    public void send(Location location, Particle particle, int count, float offsetX, float offsetY, float offsetZ, float extra) {
        if (player.getStatus() != PlayerStatus.JOINED) return;
        if (player.getBukkitPlayer() == null) return;
        player.getBukkitPlayer().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
    }
}
