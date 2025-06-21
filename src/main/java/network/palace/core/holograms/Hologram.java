package network.palace.core.holograms;

import network.palace.core.npc.ConditionalName;
import network.palace.core.npc.mob.MobArmorStand;
import network.palace.core.pathfinding.Point;

/**
 * The Hologram class is responsible for managing the creation, display, and
 * modification of holographic entities in the game world.
 * A hologram is represented by a MobArmorStand with specific settings to make it
 * serve as a visual display tool rather than a conventional entity.
 */
public class Hologram {
    /**
     * A constant used to adjust the vertical position of the hologram's ArmorStand relative to the provided point.
     * It ensures that the ArmorStand is displayed at an appropriate height, typically aligning it to the center
     * or base of the hologram text.
     * <p>
     * This value is subtracted from the y-coordinate of the coordinates passed to the `adjust` method,
     * effectively lowering the hologram's position in the game world.
     */
    private static final double adjustHeight = 1.6888;

    /**
     * The `armorStand` field represents an instance of the `MobArmorStand` class, which is used as the primary
     * visual and interactive component of the hologram in the game world. This ArmorStand is configured with
     * certain properties, such as being invisible, without a base plate, and having its name always visible,
     * to fulfill its role as a holographic display tool rather than a typical in-game entity.
     * <p>
     * The `armorStand` handles the following responsibilities:
     * <p>
     * - Managing its configuration (e.g., visibility, arms, base plate, gravity).
     * <p>
     * - Displaying custom text or conditional names when updated.
     * <p>
     * - Spawning, despawning, and moving within the game world based on the hologram's requirements.
     */
    private MobArmorStand armorStand;

    /**
     * Represents the text displayed by the hologram. This variable holds the content
     * that is shown above the hologram's MobArmorStand in the game world.
     * <p>
     * The value of this variable can be modified through the `setText` method,
     * which updates the ArmorStand's custom name to reflect the new text. When
     * the hologram is spawned, this text is displayed as its primary visual element.
     * <p>
     * By default, this field is initialized as an empty string.
     */
    private String text = "";

    /**
     * Constructor for creating a new Hologram.
     * This constructor initializes and adjusts the position of the Hologram,
     * and sets properties such as visibility, arms, base plate, gravity, and
     * custom name visibility using a MobArmorStand.
     *
     * @param point the initial position of the Hologram in the world.
     *              This point is adjusted internally in the constructor.
     * @param text  the custom text to be displayed by the Hologram.
     */
    public Hologram(Point point, String text) {
        adjust(point);
        armorStand = new MobArmorStand(point, null, text);
        armorStand.setVisible(false);
        armorStand.setArms(false);
        armorStand.setBasePlate(false);
        armorStand.setGravity(false);
        armorStand.setCustomNameVisible(true);
    }

    /**
     * Spawns the armor stand associated with this hologram in the world.
     * <p>
     * This method is used to initialize and display the hologram by triggering the
     * spawn logic of the underlying MobArmorStand instance. The armor stand is
     * configured with specific properties, such as visibility and custom name
     * options, and made visible to its targets when this method is called.
     * <p>
     * If the armor stand is already spawned, calling this method will have no effect.
     */
    public void create() {
        armorStand.spawn();
    }

    /**
     * Removes the hologram's associated armor stand from the world.
     * <p>
     * This method despawns the underlying {@code MobArmorStand} instance that
     * represents the visual element of the hologram. It ensures that any resources
     * related to the armor stand are released and any active listeners are also
     * removed. If the armor stand is not currently spawned, calling this method
     * has no effect.
     */
    public void destroy() {
        armorStand.despawn();
    }

    /**
     * Moves the hologram to a new position in the world.
     * This method adjusts the given position and moves the associated armor stand
     * to the updated position if it is currently spawned.
     *
     * @param point the target position to which the hologram should be moved.
     */
    public void move(Point point) {
        adjust(point);
        if (armorStand.isSpawned())
            armorStand.move(point);
    }

    /**
     * Updates the text displayed by the hologram and synchronizes it with the associated
     * armor stand. If the armor stand is already spawned in the world, it updates
     * its custom name immediately.
     *
     * @param text the new text to be displayed by the hologram
     */
    public void setText(String text) {
        this.text = text;
        armorStand.setCustomName(text);
        if (armorStand.isSpawned())
            armorStand.update(false);
    }

    /**
     * Retrieves the text displayed by the hologram.
     *
     * @return the current text being displayed by the hologram
     */
    public String getText() {
        return text;
    }

    /**
     * Checks if the associated armor stand has a conditional name set.
     *
     * @return true if the armor stand has a conditional name, false otherwise.
     */
    public boolean hasConditionalName() {
        return armorStand.hasConditionalName();
    }

    /**
     * Sets a conditional name for the hologram's associated armor stand.
     * This method allows specifying a {@code ConditionalName} object which can
     * dynamically determine the custom display name of the armor stand.
     *
     * @param conditionalName the {@code ConditionalName} object that determines
     *                        the conditional custom name for the armor stand
     */
    public void setConditionalName(ConditionalName conditionalName) {
        armorStand.setConditionalName(conditionalName);
    }

    /**
     * Adjusts the position of the provided point by modifying its y-coordinate.
     * This method shifts the point downward based on the value of the adjustHeight field.
     *
     * @param p the point to be adjusted, representing the position in the 3D world.
     */
    private void adjust(Point p) {
        p.add(0.0, -adjustHeight, 0.0);
    }
}
