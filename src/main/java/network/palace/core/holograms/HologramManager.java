package network.palace.core.holograms;

import java.util.ArrayList;
import java.util.List;

/**
 * The HologramManager class serves as a manager for handling the collection,
 * creation, display, and removal of holograms. A hologram is a visual entity
 * represented by custom displays (usually text or other visuals) in the game world.
 * <p>
 * The class maintains an internal list of holograms and provides functionality
 * to manage these holograms dynamically at runtime.
 * <p>
 * Responsibilities include:
 * <p>
 * - Creating and storing holograms in a managed list.
 * <p>
 * - Spawning and despawning holograms in the game world.
 * <p>
 * - Providing mechanisms to modify or retrieve information about existing holograms.
 * <p>
 * This class is designed to encapsulate all hologram-related operations, allowing
 * developers to efficiently manage groups of holograms without having to handle
 * individual entities directly.
 */
public class HologramManager {

    /**
     * A list that holds all instances of holograms managed by the HologramManager.
     * This list is used to keep track of active holograms in the game world,
     * allowing for their creation, removal, and modification.
     * Each hologram in the list represents a visual entity in the game, typically
     * as text or other visuals displayed at a specific location.
     */
    private List<Hologram> holograms = new ArrayList<>();
}
