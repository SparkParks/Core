package network.palace.core.player;

import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * The {@code CPlayerParticlesManager} interface provides methods for managing particle effects
 * at a player's location or any other specified location within the game world.
 * <p>
 * This interface supports varying control over particle spawning, including:
 * <ul>
 *     <li>Spawning individual particles or a specified number of particles.</li>
 *     <li>Spawning particles at a player's location.</li>
 *     <li>Spawning particles at a custom location.</li>
 *     <li>Adding random offsets to particle positions for natural dispersion effects.</li>
 *     <li>Customizing particles with additional data, such as speed (when applicable).</li>
 * </ul>
 *
 * <p>
 * Implementers of this interface can provide specific behaviors or constraints
 * for particle spawning based on game requirements or player states.
 */
public interface CPlayerParticlesManager {

    /**
     * Sends a particle effect to the defined recipient or context.
     * The implementation determines where and to whom this particle will be displayed.
     *
     * @param particle The {@link Particle} to be sent. This represents the specific type of particle effect
     *                 that is being dispatched for visual rendering.
     */
    void send(Particle particle);

    /**
     * Sends a specified number of particles of the given type to the default target location.
     * <p>
     * This method allows spawning a specific quantity of a particle type, enabling
     * finer control over particle effects in the game.
     *
     * @param particle the {@link Particle} type to be spawned; must not be null
     * @param count the number of particles to spawn; must be a non-negative integer
     *              <ul>
     *                  <li>If <code>count</code> is 0, no particles will be spawned.</li>
     *                  <li>Higher counts will increase the density of the particle effect.</li>
     *              </ul>
     */
    void send(Particle particle, int count);

    /**
     * Sends a specified particle effect to the given location in the game world.
     * The particle will be displayed visually based on the provided information.
     *
     * <p>
     * This method is utilized to create immersive visual effects by spawning
     * particles at a specific location. Particles can represent various
     * entities, elements, or animations and are often used for decorative
     * or signaling purposes.
     * </p>
     *
     * @param location the {@link Location} where the particle will be displayed.
     *                 This defines the x, y, and z coordinates in the game world.
     * @param particle the {@link Particle} type to spawn at the specified location.
     *                 This determines the visual representation of the particle effect.
     */
    void send(Location location, Particle particle);

    /**
     * Sends a specified particle effect to a given location within the game world.
     * This method allows precise control over the number of particles to be displayed.
     * <p>
     * Useful for creating customizable visual effects at specific coordinates.
     *
     * @param location the {@link Location} where the particles will be displayed.
     *                 This defines the target position for the particle effect.
     * @param particle the {@link Particle} type to display. Determines the visual style
     *                 of the particle effect, such as smoke, flame, water, etc.
     * @param count    the number of particle instances to show. Must be a non-negative integer,
     *                 where larger values increase the particle density.
     */
    void send(Location location, Particle particle, int count);

    /**
     * Sends a particle effect to the specified {@link Location} with configurable properties.
     * This method allows the customization of the number of particles, their random spread,
     * and optional extra data such as velocity or specific characteristics depending on the particle type.
     *
     * @param location the {@link Location} where the particles will be displayed.
     *                 This determines the center position for spawning the particles.
     * @param particle the {@link Particle} type to be spawned. Determines the style or effect of the particle.
     * @param count    the number of individual particle effects to spawn at the specified location.
     * @param offsetX  the maximum range of random spread along the X-axis for the particles.
     *                 Higher values increase the spread.
     * @param offsetY  the maximum range of random spread along the Y-axis for the particles.
     *                 Higher values increase the spread.
     * @param offsetZ  the maximum range of random spread along the Z-axis for the particles.
     *                 Higher values increase the spread.
     * @param extra    additional data or value used for modifying certain particle behaviors,
     *                 such as speed for {@link Particle#REDSTONE} or particle size for others.
     *                 Its usage depends on the specific particle type.
     */
    void send(Location location, Particle particle, int count, float offsetX, float offsetY, float offsetZ, float extra);
}
