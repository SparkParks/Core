package network.palace.core.npc.mob;

import com.comphenix.protocol.wrappers.Vector3F;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.npc.AbstractGearMob;
import network.palace.core.npc.ProtocolLibSerializers;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.EntityType;

import java.util.Set;

/**
 * Represents a custom armor stand entity with additional interactive features
 * and pose customization. This class extends {@link AbstractGearMob} to inherit
 * gear-related features and adds specific properties unique to armor stands,
 * such as size, arms, base plates, and positioning.
 */
public class MobArmorStand extends AbstractGearMob {

    /**
     * Indicates whether the armor stand is in its small size variant.
     * When set to true, the armor stand will appear smaller than its default size,
     * affecting its visual representation and bounding box.
     */
    @Getter @Setter private boolean isSmall = false;

    /**
     * Indicates whether the armor stand has arms enabled.
     * When set to true, the armor stand will visually display arms,
     * allowing items to be held. If false, the arms will be hidden.
     * This property affects the visual representation of the armor stand.
     */
    @Getter @Setter private boolean arms = true;

    /**
     * Indicates whether the armor stand has a visible base plate.
     * When set to true, the base plate will be displayed. If set to false,
     * the base plate will be hidden, altering the visual appearance of the armor stand.
     */
    @Getter @Setter private boolean basePlate = true;

    /**
     * Indicates whether the armor stand is in marker mode.
     * When set to true, the armor stand functions as a "marker" entity, which is invisible,
     * immovable, and does not have a collision box. This property is typically used for
     * entities that act as decorative or positioning aids in custom setups without interfering
     * with physical gameplay mechanics.
     */
    @Getter @Setter private boolean isMarker = false;

    /**
     * Represents the orientation of the head for the MobArmorStand entity.
     * The headPose defines the rotational angles of the head in 3D space, typically expressed in degrees or radians.
     * It is represented as a 3D vector where each component corresponds to a specific rotational axis (pitch, yaw, roll).
     */
    @Getter @Setter private Vector3F headPose, bodyPost, leftArmPose, rightArmPose, leftLegPose, rightLegPose;

    /**
     * Constructor for the MobArmorStand class.
     *
     * @param location the location where the armor stand is placed
     * @param observers the set of players that can observe this armor stand
     * @param title the title or name of the armor stand
     */
    public MobArmorStand(Point location, Set<CPlayer> observers, String title) {
        super(location, observers, title);
    }

    /**
     * Updates the data watcher object of the armor stand to reflect its
     * current metadata state and pose.
     *
     * This method performs the following operations:
     * 1. Updates a metadata value at a predefined index to represent the
     *    current properties of the armor stand, such as whether it is small,
     *    has arms, lacks a base plate, or is a marker. These attributes are
     *    packed into a single byte using specific bit flags.
     * 2. Updates the pose of various parts of the armor stand, including
     *    the head, body, arms, and legs, by calling the {@link #updatePose}
     *    method with the respective indices and pose values.
     * 3. Calls the parent class's implementation of {@code onDataWatcherUpdate}
     *    to ensure that any additional behavior defined in the superclass
     *    is also executed.
     *
     * This method ensures synchronization between the server state and
     * the visual representation of the armor stand for observing clients.
     *
     * Fields referenced:
     * - isSmall: A boolean indicating whether the armor stand is small.
     * - arms: A boolean indicating whether the armor stand has arms.
     * - basePlate: A boolean indicating whether the armor stand has a base plate.
     * - isMarker: A boolean indicating whether the armor stand is a marker.
     * - headPose: A vector representing the pose of the armor stand's head.
     * - bodyPost: A vector representing the pose of the armor stand's body.
     * - leftArmPose: A vector representing the pose of the armor stand's left arm.
     * - rightArmPose: A vector representing the pose of the armor stand's right arm.
     * - leftLegPose: A vector representing the pose of the armor stand's left leg.
     * - rightLegPose: A vector representing the pose of the armor stand's right leg.
     */
    @Override
    protected void onDataWatcherUpdate() {
        int metadataIndex = 11;
        byte value = 0;
        if (isSmall) value |= 0x01;
        if (arms) value |= 0x04;
        if (!basePlate) value |= 0x08;
        if (isMarker) value |= 0x10;
        getDataWatcher().setObject(ProtocolLibSerializers.getByte(metadataIndex), value);
        updatePose(12, headPose);
        updatePose(13, bodyPost);
        updatePose(14, leftArmPose);
        updatePose(15, rightArmPose);
        updatePose(16, leftLegPose);
        updatePose(17, rightLegPose);
        super.onDataWatcherUpdate();
    }

    /**
     * Updates the pose of a specific part of the armor stand using the given index and pose data.
     *
     * This method updates the data watcher object to reflect the changes in the pose
     * for a particular part of the armor stand. The input pose is modified to
     * convert its values from radians to degrees before being applied.
     *
     * @param index the index of the part of the armor stand whose pose is being updated
     * @param pose the new pose represented as a {@code Vector3F}, which defines the
     *             orientation in 3D space; must not be {@code null}
     */
    public void updatePose(int index, Vector3F pose) {
        if (pose != null) {
            getDataWatcher().setObject(ProtocolLibSerializers.getVector3F(index), radToDegress(pose));
        }
    }

    /**
     * Converts a given {@code Vector3F} where each component represents an angle in radians
     * to a {@code Vector3F} where each component represents an angle in degrees.
     *
     * @param angle the {@code Vector3F} containing the angles in radians to be converted,
     *              represented as X, Y, and Z components
     * @return a new {@code Vector3F} containing the converted angles in degrees
     */
    public Vector3F radToDegress(Vector3F angle) {
        return new Vector3F((float) (angle.getX() * 180 / Math.PI), (float) (angle.getY() * 180 / Math.PI), (float) (angle.getZ() * 180 / Math.PI));
    }

    /**
     * Retrieves the entity type associated with this mob.
     *
     * @return the {@link EntityType} representing the type of this mob, which is an armor stand.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.ARMOR_STAND;
    }

    /**
     * Retrieves the maximum health value for this MobArmorStand entity.
     *
     * @return the maximum health of the MobArmorStand, represented as a float value;
     *         in this case, the default value is 2.
     */
    @Override
    public float getMaximumHealth() {
        return 2;
    }
}
