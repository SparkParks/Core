package network.palace.core.npc;

import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;

import java.util.Set;

/**
 * Represents an abstract class for tameable mobs. This class is a subclass of {@link AbstractAgeableMob}
 * and provides functionality specific to entities that can be tamed by players.
 *
 * Subclasses of this class can implement specific behaviors for tameable entities,
 * such as sitting, being owned by a player, and displaying particle effects.
 */
public abstract class AbstractTameableMob extends AbstractAgeableMob {

    /**
     * Indicates whether this mob entity is tamed.
     *
     * The tame state determines whether the entity has been domesticated,
     * typically as a result of player interaction or custom logic specific to the subclass.
     *
     * If {@code true}, the entity is considered tamed and may exhibit
     * behaviors aligned with a tamed state, such as following its owner
     * or responding to specific commands. Otherwise, the entity behaves as a
     * wild mob.
     *
     * This field can be updated dynamically, often reflecting changes in the
     * entity's ownership status or interaction with players.
     */
    private boolean tame = false;

    /**
     * Indicates whether the tameable mob is in a sitting state.
     *
     * When the mob is sitting (i.e., {@code true}), the entity remains stationary
     * and does not follow its owner. This state is often toggled by player interaction
     * or subclass-specific logic. If {@code false}, the mob is not sitting and may
     * exhibit behaviors such as following its owner or moving freely.
     *
     * This field is typically used in conjunction with the tamed state, as only
     * tameable mobs can obey sit commands. The sitting state may also influence
     * other visual or behavioral aspects of the mob.
     */
    private boolean sitting = false;

    /**
     * Represents the name of the current owner of the tameable mob.
     *
     * This field is used to associate a tamed entity with a specific player or owner.
     * The value of this variable is expected to match the name of the player who
     * tamed the mob or otherwise became its owner.
     *
     * By default, the owner name is initialized to "Notch" if it is not explicitly set*/
    private String ownerName = "Notch";

    /**
     * Constructs a new AbstractTameableMob.
     *
     * @param location the location of the tameable mob.
     * @param observers the set of players observing the mob.
     * @param title the title of the mob.
     * @param tame indicates whether the mob is tamed.
     * @param sitting indicates whether the mob is sitting.
     * @param ownerName the name of the owner of the mob.
     */
    public AbstractTameableMob(Point location, Set<CPlayer> observers, String title, boolean tame, boolean sitting, String ownerName) {
        super(location, observers, title);

        this.tame = tame;
        this.sitting = sitting;
        this.ownerName = ownerName;
    }

    /**
     * Updates the entity's data watcher with the current states of the tameable mob.
     *
     * This method synchronizes the tameable mob's "sitting" and "tame" states, as well as its owner name,
     * with the data watcher, ensuring these states are communicated to observers as needed.
     *
     * Functionality:
     * - Tracks the binary representation of the "sitting" and "tame" states in a single byte value.
     *   - If the mob is sitting, the least significant bit (0x01) is set.
     *   - If the mob is tamed, the third least significant bit (0x04) is set.
     * - Updates the data watcher with the calculated byte value at index 13 using a byte serializer.
     * - Ensures that the owner name is not null by defaulting to "Notch" if no owner name is set.
     * - Updates the data watcher with the owner name at index 14 using a string serializer.
     * - Calls the parent implementation of `onDataWatcherUpdate` to perform additional updates
     *   defined in the superclass.
     *
     * This method is typically invoked when the state of the tameable mob changes and the new
     * state needs to be sent to observers.
     */
    @Override
    protected void onDataWatcherUpdate() {
        int metadataIndex = 13;
        byte value = 0;
        if (sitting) value |= 0x01;
        if (tame) value |= 0x04;
        getDataWatcher().setObject(ProtocolLibSerializers.getByte(metadataIndex), value);
        if (ownerName == null) ownerName = "Notch";
        getDataWatcher().setObject(14, ownerName);
        super.onDataWatcherUpdate();
    }

    /**
     * Displays heart particle effects to indicate a positive status, such as a tameable mob being
     * successfully tamed or expressing affection.
     *
     * This method utilizes the `playStatus` mechanism with a specific status code (6) to broadcast
     * the heart particle effect to relevant targets. The exact behavior of the particle effect rendering
     * is handled by the underlying `playStatus` implementation, which interacts with packets to
     * communicate the visual effect.
     *
     * The method is typically invoked in scenarios where a tameable mob needs to signify affection
     * or a successful interaction with its owner or player.
     */
    public void playHeartParticles() {
        playStatus(6);
    }

    /**
     * Displays smoke particle effects to indicate a neutral or negative status associated
     * with the tameable mob, such as an unsuccessful interaction or action.
     *
     * This method utilizes the `playStatus` mechanism with a predefined status code (7)
     * to broadcast the smoke particle effect to relevant targets. The exact behavior of
     * the particle effect rendering is internally handled by the `playStatus` method,
     * which communicates the visual effect through status packets.
     *
     * The method is typically invoked in scenarios where a visual representation of an
     * unsuccessful action or neutral status is required for the tameable mob.
     */
    public void playSmokeParticles() {
        playStatus(7);
    }

    /**
     * Displays heart particle effects to the specified set of players, typically indicating a
     * positive status such as affection or successful interaction with the tameable mob.
     *
     * This method utilizes the `playStatus` mechanism with a status code of 6 to broadcast
     * the heart particle effect to the provided players. The exact rendering of the particles
     * is handled by the underlying system within the `playStatus` implementation.
     *
     * @param players the set of players to whom the heart particle effects will be displayed.
     */
    public void playHeartParticles(Set<CPlayer> players) {
        playStatus(players, 6);
    }

    /**
     * Displays smoke particle effects to the specified set of players, typically indicating
     * a neutral or negative status such as an unsuccessful interaction or action with the tameable mob.
     *
     * This method utilizes the `playStatus` mechanism with a predefined status code of 7 to
     * broadcast the smoke particle effect to the provided players. The actual rendering and
     * delivery of the particle effect are managed by the underlying implementation of `playStatus`.
     *
     * @param players the set of players to whom the smoke particle effects will be displayed.
     */
    public void playSmokeParticles(Set<CPlayer> players) {
        playStatus(players, 7);
    }
}
