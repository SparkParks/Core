package network.palace.core.npc.mob;

import com.comphenix.protocol.wrappers.*;
import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.npc.AbstractGearMob;
import network.palace.core.npc.ProtocolLibSerializers;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.packets.server.entity.*;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Stairs;

import java.util.*;

/**
 * Represents a MobPlayer entity which extends AbstractGearMob.
 * This class provides the functionality to customize the appearance and behavior of a player-like entity within a game.
 * It includes properties for enabling or disabling various player cosmetics and features such as cape, jacket, sleeves,
 * pant legs, and hat, as well as actions like fishing, sitting, and sleeping.
 */
public class MobPlayer extends AbstractGearMob {

    /**
     * Indicates whether the cape feature is enabled for the player entity.
     *
     * This variable determines if the player's cape is visible or active in the game world.
     * When set to {@code true}, the cape is displayed; when {@code false}, the cape is hidden.
     */
    @Getter @Setter private boolean capeEnabled = true;

    /**
     * Indicates whether the jacket feature is enabled for the mob player.
     *
     * This variable determines the visibility and properties of the jacket
     * appearance for the mob player entity. When set to {@code true}, the jacket
     * is enabled and will be shown; otherwise, it is disabled and will not be displayed.
     */
    @Getter @Setter private boolean jacketEnabled = true;

    /**
     * Indicates whether the left sleeve of the player's clothing is enabled.
     *
     * This variable is used to manage the visibility or availability of the left sleeve
     * in the player's appearance configuration. When set to {@code true}, the left sleeve
     * is enabled and visible. When set to {@code false}, the left sleeve is disabled
     * and not part of the player's appearance.
     */
    @Getter @Setter private boolean leftSleeveEnabled = true;

    /**
     * Determines whether the right sleeve of the player's skin is enabled.
     *
     * This variable is used to control the visibility of the right sleeve
     * portion of the player's custom skin or appearance. When set to {@code true},
     * the right sleeve is rendered as part of the player model. If set to {@code false},
     * the right sleeve will not be displayed.
     */
    @Getter @Setter private boolean rightSleeveEnabled = true;

    /**
     * Indicates whether the left pant leg appearance of the {@code MobPlayer} entity is enabled.
     *
     * This variable controls the visibility or state of the left pant leg aspect of the {@code MobPlayer}.
     * When set to {@code true}, the left pant leg feature is active or visible; otherwise, it is
     * disabled or invisible.
     *
     * This could impact the visual representation of the {@code MobPlayer} entity for observers within
     * the game, allowing the dynamic toggling of specific appearance features.
     */
    @Getter @Setter private boolean leftPantLegEnabled = true;

    /**
     * Determines whether the right pant leg of the MobPlayer entity is enabled.
     *
     * This variable controls the visibility or functionality of the right pant leg
     * in the game. When set to {@code true}, the right pant leg is enabled, and when
     * set to {@code false}, it is disabled. This state may influence the appearance
     * or behavior of the MobPlayer entity.
     */
    @Getter @Setter private boolean rightPantLegEnabled = true;

    /**
     * Indicates whether the hat is enabled for the mob player.
     *
     * This variable determines if the hat is visible as part of the mob player's appearance.
     * When set to {@code true}, the hat is enabled and displayed; when set to {@code false},
     * the hat is disabled and not displayed.
     */
    @Getter @Setter private boolean hatEnabled = true;

    /**
     * Represents the texture information of a MobPlayer entity.
     *
     * This variable stores the player's texture data, which includes details
     * such as the skin or visual appearance. The texture data is managed via
     * the {@link MobPlayerTexture} class and is used to ensure the correct
     * rendering of the MobPlayer's skin in the game world.
     *
     * The texture information typically includes a value and a signature
     * necessary for verifying and applying the associated texture.
     */
    private MobPlayerTexture textureInfo;

    /**
     * Stores a list of UUIDs representing players that need to be removed from the tab list.
     *
     * This list is used to keep track of player identifiers for whom the corresponding
     * tab list entry should be removed, typically as part of the NPC's interaction logic
     * or lifecycle management. Modifications to this list should reflect changes
     * in the observer or player state regarding their visibility in the tab list.
     */
    private List<UUID> needTabListRemoved = new ArrayList<>();

    /**
     * Represents the location of the player's bed in the game world.
     *
     * This variable holds a reference to a {@code Location} object that indicates
     * where the bed associated with the player is placed. The bed location is used
     * to determine the player's respawn point after death or when sleeping in the game.
     *
     * The value may be {@code null} if the player does not currently have a bed
     * assigned or has not set a respawn point.
     */
    @Getter private Location bed = null;

    /**
     * Represents the location of the fishing bobber associated with the mob player.
     *
     * This variable holds the in-game coordinates of the fishing line's bobber
     * while the mob player is in a fishing state. It is updated when the player
     * starts or stops fishing. If the player is not fishing, this value is set
     * to {@code null}.
     */
    private Location bobber = null;

    /**
     * Represents the unique identifier for the fishing bobber entity associated with the player.
     *
     * This variable is used to track and manage interactions with the fishing bobber in the game.
     * The value stored represents the entity ID assigned to the bobber by the game server. It is
     * used in various logic related to fishing mechanics, including spawning, updating, and
     * despawning the bobber entity.
     */
    private int bobberEntityId;

    /**
     * Indicates whether the player entity is currently sitting.
     *
     * This variable determines the sitting state of a player entity
     * and affects its behavior or appearance in the game world.
     * When set to {@code true}, the player is sitting; otherwise,
     * the player is not sitting. The sitting state may be used
     * for specific animations or interactions in the environment.
     */
    private boolean sitting;

    /**
     * Represents the player's designated seating entity while in a sitting position.
     * The `seat` field is an instance of {@link MobArmorStand}, used to visually anchor
     * or simulate the player's position while sitting. This allows for interaction with
     * the game's visual and collision systems to provide an immersive experience.
     *
     * The `seat` entity is typically spawned and managed during sitting-related behaviors
     * and despawned when the player exits the sitting state.
     *
     * Related fields:
     * - {@code sitting}: A boolean indicating whether the player is currently sitting.
     *
     * Related methods:
     * - {@code setSitting(boolean sitting)}: Manages the player's sitting state and
     *   updates the `seat` entity accordingly.
     * - {@code isSitting()}: Checks whether the player is currently sitting.
     */
    private MobArmorStand seat;

    /**
     * Indicates whether the MobPlayer entity is currently sneaking (crouching).
     *
     * This variable is used to track the sneaking state of the MobPlayer, which may affect its
     * interaction capabilities, visibility, and movement behavior within the game. When set
     * to {@code true}, the MobPlayer is considered to be sneaking; when set to {@code false},
     * the MobPlayer is standing normally.
     */
    private boolean sneaking;

    /**
     * Constructs a new MobPlayer instance.
     *
     * @param location The initial location of the MobPlayer.
     * @param observers A set of CPlayer observers watching this MobPlayer.
     * @param title The title to display for this MobPlayer. If null, a default private username will be used.
     * @param textureInfo The texture information for this MobPlayer.
     */
    public MobPlayer(Point location, Set<CPlayer> observers, String title, MobPlayerTexture textureInfo) {
        super(location, observers, title);
        String tempUUID = UUID.randomUUID().toString();
        StringBuilder uuidBuilder = new StringBuilder(UUID.randomUUID().toString());
        uuidBuilder.setCharAt(14, '2');
        this.uuid = UUID.fromString(uuidBuilder.toString());
        this.textureInfo = textureInfo;
        if (title == null) {
            setCustomName(ChatColor.DARK_GRAY + getPrivateUsername());
        }
    }

    /**
     * Spawns the MobPlayer entity in the game world, making it visible and interactive
     * to its observers. This method overrides the spawn behavior inherited from the
     * superclass and adds functionality specific to the MobPlayer entity.
     *
     * Upon spawning, the MobPlayer's head yaw rotation is set based on its current
     * location's yaw value. This ensures that the entity's visual orientation matches
     * its actual direction in the game world. The head yaw is updated by invoking
     * the {@link #setHeadYaw(Location)} method with the location obtained from
     * {@link #getLocation()}.
     *
     * Note: This method calls the superclass {@link AbstractGearMob#spawn()}
     * to handle common spawning behaviors before applying MobPlayer-specific logic.
     */
    @Override
    public void spawn() {
        super.spawn();

        setHeadYaw(getLocation().getLocation());
    }

    /**
     * Despawns the MobPlayer entity, making it no longer visible or interactive to any players.
     * This method overrides the superclass despawn behavior to handle MobPlayer-specific logic.
     *
     * The despawning process includes:
     * 1. Checking if the entity has a conditional name using {@link #hasConditionalName()}.
     *    If true, removes the entity from the tab list and sends a player removal packet
     *    to all target players obtained from {@link #getTargets()}.
     * 2. If there is no conditional name, a similar process is executed without considering
     *    a per-target custom name.
     * 3. All viewers of the entity are removed using {@link #removeViewer(CPlayer)} for each
     *    target player.
     * 4. If the entity is engaged in fishing (checked via {@link #isFishing()}), the associated
     *    state is cleared by invoking {@link #setFishing(Location)} with a null argument.
     *
     * Note: This method calls {@code super.despawn()} to execute any despawn logic defined in
     * the superclass before applying MobPlayer-specific behavior.
     */
    @Override
    public void despawn() {
        super.despawn();

        if (hasConditionalName()) {
            for (CPlayer target : getTargets()) {
                WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
                playerInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                playerInfo.setData(Collections.singletonList(new PlayerInfoData(new WrappedGameProfile(getUuid(), getCustomName(target)), 0, EnumWrappers.NativeGameMode.ADVENTURE, null)));
                playerInfo.sendPacket(target);
                removeViewer(target);
            }
        } else {
            WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
            playerInfo.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            playerInfo.setData(Collections.singletonList(new PlayerInfoData(new WrappedGameProfile(getUuid(), getCustomName()), 0, EnumWrappers.NativeGameMode.ADVENTURE, null)));
            Arrays.asList(getTargets()).forEach(p -> {
                playerInfo.sendPacket(p);
                removeViewer(p);
            });
        }
        if (isFishing()) setFishing(null);
    }

    /**
     * Constructs and returns a spawn packet for the entity. This packet is used to
     * spawn a named entity in the game world with the specified properties such as
     * entity ID, UUID, location coordinates, rotation, and metadata.
     *
     * @return The constructed {@link AbstractPacket} that contains all necessary
     *         information to spawn the entity in the game world.
     */
    @Override
    protected AbstractPacket getSpawnPacket() {
        WrapperPlayServerNamedEntitySpawn packet = new WrapperPlayServerNamedEntitySpawn();
        packet.setEntityID(entityId);
        packet.setPlayerUUID(uuid);
        packet.setX(location.getX());
        packet.setY(location.getY());
        packet.setZ(location.getZ());
        packet.setYaw(location.getYaw());
        packet.setPitch(location.getPitch());
        updateDataWatcher();
        packet.setMetadata(dataWatcher);
        return packet;
    }

    /**
     * Adds a specified player as a viewer to the MobPlayer entity and updates the internal list
     * to ensure this player is removed from the tab list when necessary.
     *
     * @param player The player to be added as a viewer. This operation also flags the player's
     *               unique identifier for tab list removal.
     */
    @Override
    protected void addViewer(CPlayer player) {
        super.addViewer(player);
        needTabListRemoved.add(player.getUniqueId());
    }

    /**
     * Removes the specified player from the list of viewers for this MobPlayer entity.
     * This method overrides the behavior defined in the superclass to include additional
     * logic for handling the removal of the player's unique identifier from the
     * `needTabListRemoved` set, ensuring the player is no longer included in the tab list
     * of the game.
     *
     * @param player The player to be removed as a viewer of this MobPlayer entity.
     *               This operation also removes the player's unique identifier
     *               from the `needTabListRemoved` tracking set.
     */
    @Override
    protected void removeViewer(CPlayer player) {
        super.removeViewer(player);
        needTabListRemoved.remove(player.getUniqueId());
    }

    /**
     * Forces the MobPlayer entity to spawn for the specified player, making it
     * visible and interactive to them. This method overrides the behavior of
     * the superclass to include additional logic specific to MobPlayer entities,
     * such as handling player info packets, spawn packets, and optional updates.
     *
     * @param player The player for whom the MobPlayer entity will be spawned.
     * @param update If true, triggers an update process for the MobPlayer entity after spawning.
     */
    @Override
    public void forceSpawn(CPlayer player, boolean update) {
        if (isViewer(player)) {
            return;
        }
        addViewer(player);
        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        playerInfo.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        WrappedGameProfile profile = textureInfo.getWrappedSignedProperty(new WrappedGameProfile(getUuid(), getCustomName(player)));
        playerInfo.setData(Collections.singletonList(new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.ADVENTURE,
                WrappedChatComponent.fromText(getTabListName()))));
        playerInfo.sendPacket(player);

        getSpawnPacket().sendPacket(player);

        if (bed != null || bobber != null) {
            Core.runTaskLater(Core.getInstance(), () -> {
                if (bed != null) {
                    WrapperPlayServerBed bedPacket = getBedPacket();
                    viewers.forEach(bedPacket::sendPacket);
                }
                if (bobber != null) {
                    WrapperPlayServerSpawnEntity bobberPacket = getFishingPacket();
                    viewers.forEach(bobberPacket::sendPacket);
                }
            }, 10L);
        }

        if (update) update(new CPlayer[]{player}, true);

        sendYaw(new CPlayer[]{player});
    }

    /**
     * Retrieves the entity type of this MobPlayer instance.
     *
     * @return The {@code EntityType} representing the type of entity,
     *         which is {@code EntityType.PLAYER} for this MobPlayer.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.PLAYER;
    }

    /**
     * Retrieves the maximum health value for this MobPlayer entity.
     *
     * @return The maximum health value, represented as a float. In this implementation, it always returns 20.
     */
    @Override
    public float getMaximumHealth() {
        return 20;
    }

    /**
     * Updates the data watcher object for this MobPlayer entity. This method is invoked
     * whenever the state of attributes represented by the data watcher needs to be refreshed.
     *
     * The method handles the configuration of the skin parts parameter, represented by
     * a byte value, which determines the appearance attributes for the MobPlayer. The
     * byte is constructed based on several boolean flags representing enabled cosmetic
     * options, such as cape, jacket, hat, and sleeve or pant leg visibility.
     *
     * Specifically, the flags are mapped to bits in the byte value as follows:
     * - Bit 0 (0x01): Cape enabled
     * - Bit 1 (0x02): Jacket enabled
     * - Bit 2 (0x04): Left sleeve enabled
     * - Bit 3 (0x08): Right sleeve enabled
     * - Bit 4 (0x10): Left pant leg enabled
     * - Bit 5 (0x20): Right pant leg enabled
     * - Bit 6 (0x40): Hat enabled
     *
     * Once the byte value is constructed from these enabled attributes, it is set into
     * the data watcher using the API provided by the `ProtocolLibSerializers` class and
     * the associated data watcher key.
     *
     * Finally, the superclass's `onDataWatcherUpdate` method is called to ensure any
     * additional behavior defined by the superclass is executed.
     */
    @Override
    protected void onDataWatcherUpdate() {
        int skinPartsIndex = 13;
        byte value = 0;
        if (capeEnabled) value |= 0x01;
        if (jacketEnabled) value |= 0x02;
        if (leftSleeveEnabled) value |= 0x04;
        if (rightSleeveEnabled) value |= 0x08;
        if (leftPantLegEnabled) value |= 0x10;
        if (rightPantLegEnabled) value |= 0x20;
        if (hatEnabled) value |= 0x40;
        getDataWatcher().setObject(ProtocolLibSerializers.getByte(skinPartsIndex), value);
        super.onDataWatcherUpdate();
    }

    /**
     * Generates a private username for the MobPlayer using the initial segment of
     * the player's UUID. The username is derived by taking the first eight characters
     * of the UUID string representation.
     *
     * @return A substring of the UUID representing the private username, consisting
     *         of the first eight characters of the UUID.
     */
    private String getPrivateUsername() {
        return getUuid().toString().substring(0, 8);
    }

    /**
     * Retrieves the tab list name for the MobPlayer. This name is constructed
     * by combining a dark gray "[NPC]" prefix with the private username of the MobPlayer.
     *
     * @return A string representing the tab list name, consisting of a dark gray "[NPC]"
     *         prefix followed by the MobPlayer's private username.
     */
    private String getTabListName() {
        return ChatColor.DARK_GRAY + "[NPC] " + getPrivateUsername();
    }

    /**
     * Toggles the visibility of the custom name for this MobPlayer entity. When the custom name visibility
     * is enabled, the entity is untracked from the hidden player mob list. When it is disabled, the entity
     * is tracked into the hidden player mob list instead.
     *
     * @param b A boolean value determining whether the custom name should be visible. If true, the custom
     *          name will be visible to players; if false, it will not be visible.
     */
    @Override
    public void setCustomNameVisible(boolean b) {
        if (b) {
            Core.getSoftNPCManager().untrackHiddenPlayerMob(this);
        } else {
            Core.getSoftNPCManager().trackHiddenPlayerMob(this);
        }
    }

    /**
     * Removes the specified UUID from the `needTabListRemoved` tracking set,
     * ensuring the corresponding player or entity is no longer marked for
     * removal from the tab list.
     *
     * @param uuid The unique identifier of the player or entity to be removed
     *             from the `needTabListRemoved` set.
     */
    public void removedFromTabList(UUID uuid) {
        needTabListRemoved.remove(uuid);
    }

    /**
     * Determines if the specified player needs to be removed from the tab list.
     *
     * @param player The player whose unique identifier is checked against the `needTabListRemoved` set.
     * @return True if the player's unique identifier is present in the `needTabListRemoved` set, indicating
     *         they need to be removed from the tab list; otherwise, false.
     */
    public boolean needsRemoveFromTabList(CPlayer player) {
        return needTabListRemoved.contains(player.getUniqueId());
    }

    /**
     * Retrieves the number of elements that need to be removed from the tab list.
     *
     * @return the count of elements in the needTabListRemoved collection
     */
    public int getNeedsRemoved() {
        return needTabListRemoved.size();
    }

    /**
     * Sets the sleeping state by assigning the provided bed location
     * and sending a bed packet to all viewers.
     *
     * @param bed the location of the bed where the entity is sleeping
     */
    public void setSleeping(Location bed) {
        this.bed = bed;
        WrapperPlayServerBed bedPacket = getBedPacket();
        viewers.forEach(bedPacket::sendPacket);
    }

    /**
     * Determines whether the entity is currently sleeping.
     *
     * @return true if the entity is sleeping (i.e., if the bed is not null), false otherwise
     */
    public boolean isSleeping() {
        return bed != null;
    }

    /**
     * Constructs and returns a WrapperPlayServerBed packet containing the entity's ID
     * and the bed location.
     *
     * The method initializes a new WrapperPlayServerBed object, sets its entity ID,
     * and assigns a location based on the bed's position. If the bed is null, the
     * location is set to null.
     *
     * @return an instance of WrapperPlayServerBed with the entity ID and bed location set.
     */
    private WrapperPlayServerBed getBedPacket() {
        WrapperPlayServerBed bedPacket = new WrapperPlayServerBed();
        bedPacket.setEntityID(getEntityId());
        if (bed == null) {
            bedPacket.setLocation(null);
        } else {
            bedPacket.setLocation(new BlockPosition(bed.getBlockX(), bed.getBlockY(), bed.getBlockZ()));
        }
        return bedPacket;
    }

    /**
     * Sets the fishing action for this entity by specifying the bobber's location.
     * If the provided bobber location is null, the fishing action is stopped and
     * the bobber entity is destroyed. If a location is provided, a new bobber entity
     * is spawned at the specified location, and the entity's main hand is set to
     * wield a fishing rod.
     *
     * @param bobber The location of the bobber to start fishing. If null, the
     *               current fishing action is stopped, and the entity's main hand
     *               is reset to empty.
     */
    public void setFishing(Location bobber) {
        this.bobber = bobber;
        if (bobber == null) {
            setMainHand(new ItemStack(Material.AIR));
            if (bobberEntityId != 0) {
                WrapperPlayServerEntityDestroy wrapper = new WrapperPlayServerEntityDestroy();
                wrapper.setEntityIds(new int[]{bobberEntityId});
                Arrays.asList(getTargets()).forEach(wrapper::sendPacket);
            }
        } else {
            setMainHand(new ItemStack(Material.FISHING_ROD));
            this.bobberEntityId = Core.getSoftNPCManager().getIDManager().getNextID();
            WrapperPlayServerSpawnEntity bobberPacket = getFishingPacket();
            viewers.forEach(bobberPacket::sendPacket);
        }
    }

    /**
     * Determines whether the user is currently fishing.
     *
     * @return true if the bobber is not null, indicating the user is fishing; false otherwise.
     */
    public boolean isFishing() {
        return bobber != null;
    }

    /**
     * Creates and retrieves a packet for spawning a fishing bobber entity.
     *
     * @return a {@code WrapperPlayServerSpawnEntity} object representing the fishing bobber entity,
     *         or {@code null} if the bobber is not initialized.
     */
    private WrapperPlayServerSpawnEntity getFishingPacket() {
        if (bobber == null) return null;
        WrapperPlayServerSpawnEntity wrapper = new WrapperPlayServerSpawnEntity();
        wrapper.setType(WrapperPlayServerSpawnEntity.ObjectTypes.FISHING_FLOAT);
        wrapper.setEntityID(bobberEntityId);
        wrapper.setUniqueId(UUID.randomUUID());
        wrapper.setX(bobber.getX());
        wrapper.setY(bobber.getY());
        wrapper.setZ(bobber.getZ());
        wrapper.setObjectData(getEntityId());
        return wrapper;
    }

    /**
     * Sets the sitting state for the entity. When set to true, the entity will attempt to
     * sit on a valid block or stairs at its current location. If no valid block is found,
     * the sitting state is reverted to false. If set to false, the entity will stop sitting.
     *
     * @param sitting the desired sitting state of the entity, where true makes the entity sit
     *                and false makes the entity stand up.
     */
    public void setSitting(boolean sitting) {
        if (this.sitting == sitting) return;
        this.sitting = sitting;
        if (sitting) {
            Block block = getLocation().getLocation().getBlock();
            System.out.println(block == null);
            System.out.println(block.getType().name());
            System.out.println(block.getLocation().toString());
            if (block == null || block.getType().equals(Material.AIR)) {
                this.sitting = false;
                return;
            }
            Stairs stairs = null;
            if (block.getState().getData() instanceof Stairs) {
                stairs = (Stairs) block.getState().getData();
            }
            Location location = block.getLocation();
            location.add(0.5, -1.18888, 0.5);
            if (stairs != null && MiscUtil.DIRECTIONAL_YAW.containsKey(stairs.getDescendingDirection())) {
                location.setYaw(MiscUtil.DIRECTIONAL_YAW.get(stairs.getDescendingDirection()));
            }

            seat = new MobArmorStand(Point.of(location), null, "");
//            seat.setVisible(false);
            seat.setGravity(false);
            seat.setCustomNameVisible(false);
            seat.spawn();
            seat.addPassenger(this);
        } else if (seat != null) {
            seat.despawn();
            seat = null;
        }
    }

    /**
     * Determines if the object is currently in a sitting state.
     *
     * @return true if the object is sitting, false otherwise
     */
    public boolean isSitting() {
        return this.sitting;
    }

    /**
     * Sets the crouched state for the object and updates relevant targets.
     *
     * @param crouched a boolean indicating the crouched state to be set.
     *                 If true, the object is set to a crouched state;
     *                 otherwise, it is set to a non-crouched state.
     */
    @Override
    public void setCrouched(boolean crouched) {
        super.setCrouched(crouched);
        update(getTargets(), false);
    }
}
