package network.palace.core.packets.server.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.reflect.IntEnum;
import network.palace.core.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.UUID;

/**
 * WrapperPlayServerSpawnEntity is a packet wrapper for the server's spawn entity event.
 * It provides utilities to read and write data to the packet and manage different object types.
 */
public class WrapperPlayServerSpawnEntity extends AbstractPacket {
    /**
     * Represents the packet type used for spawning entities in the server play environment.
     * This variable defines the specific packet type associated with the spawning of an entity.
     */
    public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY;

    /**
     * A static field defining the PacketConstructor for spawning entities.
     * This constructor is responsible for generating a specific PacketContainer
     * that can be used to spawn an entity in the world with specified parameters.
     * It utilizes the entity, type, and other related data to create the necessary
     * packet for server-client communication regarding entity spawning.
     */
    private static PacketConstructor entityConstructor;

    /**
     * Represents the various object types defined as integer constants.
     * These constants are used to identify specific types of entities and objects
     * for server-side operations such as spawning and interaction within the game.
     *
     * Each integer value corresponds to a unique object type, such as boats, item stacks,
     * projectiles, or other game-related entities.
     *
     * This class provides a centralized enumeration of these object types
     * and includes additional methods to retrieve the singleton instance.
     */
    public static class ObjectTypes extends IntEnum {
        /**
         * Represents the object type "BOAT" within the game.
         *
         * This constant is used to identify objects categorized as boats during
         * server-side operations like spawning, tracking, or interaction. It
         * distinguishes boats from other entities or objects with unique integer
         * identifiers.
         *
         * The value assigned to this constant is 1, which is used as the
         * unique identifier for boat objects.
         */
        public static final int BOAT = 1;

        /**
         * Represents a constant for the "Item Stack" object type in the game.
         * This type is used to denote a stack of items in the game world.
         *
         * The value of this constant is 2, which maps to the identifier for the
         * item stack type in the system. It is commonly used to specify or
         * differentiate between entity and object types.
         */
        public static final int ITEM_STACK = 2;

        /**
         * Represents the ID for an "Area Effect Cloud" object type.
         * This constant is used to identify and distinguish the
         * "Area Effect Cloud" entity within the ObjectTypes enumeration.
         *
         * An "Area Effect Cloud" is typically created by certain
         * entities or items, and it applies effects, such as potions,
         * to entities within its radius over time.
         */
        public static final int AREA_EFFECT_CLOUD = 3;

        /**
         * Represents the object type for a minecart in the Minecraft protocol.
         * This constant is used to identify entities of type minecart in various
         * network operations and interactions.
         *
         * The integer value of this constant corresponds to the internal
         * representation of the minecart entity type.
         */
        public static final int MINECART = 10;

        /**
         * Represents the object type identifier for activated TNT in the game.
         * This object type is used to identify and differentiate activated TNT entities.
         *
         * The value of this constant is 50, which uniquely corresponds to activated TNT.
         */
        public static final int ACTIVATED_TNT = 50;

        /**
         * Represents the unique ID for the Ender Crystal object type in Minecraft.
         *
         * This constant is used to identify the Ender Crystal entity type within the
         * context of the game's object type enumeration. Ender Crystals are commonly
         * associated with the End dimension and are often used in mechanics related
         * to the Ender Dragon fight or as decorative entities.
         */
        public static final int ENDER_CRYSTAL = 51;

        /**
         * Represents the object type identifier for a tipped arrow projectile.
         * This is used to define the unique numerical ID associated with entities
         * categorized as tipped arrow projectiles within Minecraft.
         *
         * The constant value of this ID is 60.
         */
        public static final int TIPPED_ARROW_PROJECTILE = 60;

        /**
         * Represents the integer value corresponding to a snowball projectile in the game.
         * This constant can be used to identify snowball projectiles when working with objects
         * or entities that have associated type IDs.
         */
        public static final int SNOWBALL_PROJECTILE = 61;

        /**
         * A constant representing the unique identifier for the "Egg Projectile" object type in Minecraft.
         *
         * This object type corresponds to entities created when a player or dispenser throws an egg.
         * The integer value 62 is assigned as its unique identifier within the context of object types.
         */
        public static final int EGG_PROJECTILE = 62;

        /**
         * Represents the unique identifier for a Ghast fireball entity type in the game.
         * This identifier corresponds to the Ghast fireball projectile, which is an explosive
         * entity fired by Ghasts in the Minecraft world.
         *
         * It is used to distinguish this specific entity type in various contexts, such as
         * packet handling, game logic, or entity management systems.
         *
         * Value: 63
         */
        public static final int GHAST_FIREBALL = 63;

        /**
         * Represents the object type associated with a blaze's fireball projectile in the game.
         *
         * This static final field is used to identify the unique entity/object type
         * corresponding to a blaze's fireball. It is typically used in contexts where
         * distinguishing between various object or entity types (e.g., projectiles, entities)
         * is necessary, such as event handling, packet processing, or game logic.
         *
         * The value of this constant (64) is specific to the blaze fireball projectile
         * and correlates with its representation in the game protocol or object registry.
         */
        public static final int BLAZE_FIREBALL = 64;

        /**
         * Represents the type identifier for a thrown Ender Pearl entity in the game.
         *
         * This constant can be used to access or reference the specific object type corresponding
         * to thrown Ender Pearls when interacting with server or entity-related functionality.
         *
         * Value: 65
         */
        public static final int THROWN_ENDERPEARL = 65;

        /**
         * Represents the identifier for the wither skull projectile object type.
         * This constant is used to specify or retrieve the object type corresponding
         * to wither skull projectiles in Minecraft's protocol.
         *
         * The value of this constant is defined as {@code 66}.
         *
         * This type is commonly associated with the behavior and entity representation
         * of wither skull projectiles in the game.
         */
        public static final int WITHER_SKULL_PROJECTILE = 66;

        /**
         * Represents the ID associated with a Shulker Bullet entity in Minecraft.
         *
         * This constant can be used as a reference to identify or work with Shulker Bullet entities
         * within the game or server's context. Shulker Bullets are projectiles fired by Shulkers
         * that track their target and can cause damage upon impact.
         *
         * Value: 67
         */
        public static final int SHULKER_BULLET = 67;

        /**
         * Represents a falling block entity type.
         * This constant is used to identify a specific object type in the Minecraft protocol.
         * Falling block entities are used in the game to represent blocks that are in a falling state,
         * such as sand or gravel when they are affected by gravity.
         */
        public static final int FALLING_BLOCK = 70;

        /**
         * Represents the object type ID for an item frame in the game.
         * This constant is used to identify entities of the item frame type.
         * It is a part of the enumeration of object types in the ObjectTypes class.
         *
         * Value: 71
         */
        public static final int ITEM_FRAME = 71;

        /**
         * Represents the object type ID for an Eye of Ender entity in Minecraft.
         *
         * This constant is used within the ObjectTypes class to identify the
         * specific type of an Eye of Ender object in the protocol handling system.
         *
         * Value: 72
         */
        public static final int EYE_OF_ENDER = 72;

        /**
         * Represents the identifier for a thrown potion entity in the game.
         * This constant is used to uniquely identify the entity type for thrown potions.
         * Thrown potions are throwable items that apply effects on impact, such as
         * splash potions or lingering potions.
         */
        public static final int THROWN_POTION = 73;

        /**
         * Represents the type ID for a thrown experience bottle entity in the game.
         * This ID is used for identifying and differentiating this specific type of entity
         * in various contexts such as packets or game logic.
         */
        public static final int THROWN_EXP_BOTTLE = 75;

        /**
         * Represents the object type ID for a firework rocket entity.
         * This constant is used to identify firework rocket entities within the protocol operations.
         * Firework rockets are typically used in the game to create visual displays or to propel
         * elytra-equipped players when activated.
         */
        public static final int FIREWORK_ROCKET = 76;

        /**
         * Represents the ID for the leash knot entity type.
         *
         * This constant is used to uniquely identify leash knot entities within the
         * game. Leash knots are small objects used to tether mobs via leads, typically
         * attached to fences.
         */
        public static final int LEASH_KNOT = 77;

        /**
         * Represents the object ID for an armor stand in the game.
         * This constant is used to identify entities of the type "Armor Stand."
         *
         * This value is part of the ObjectTypes class constants and is commonly used
         * within systems where entities need to be referenced by their object type ID.
         */
        public static final int ARMORSTAND = 78;

        /**
         * Represents the type ID for a fishing float entity in the game.
         *
         * The fishing float is an entity type used to represent the bobber in fishing mechanics,
         * which appears when a player uses a fishing rod.
         */
        public static final int FISHING_FLOAT = 90;

        /**
         * Represents the unique identifier for the Spectral Arrow entity type in Minecraft.
         *
         * This constant is part of the ObjectTypes class and serves as an identifier
         * for the spectral arrow entity within the game. Spectral arrows are unique
         * projectiles that outline hit entities with a glowing effect, making them
         * visible through walls and other obstacles.
         */
        public static final int SPECTRAL_ARROW = 91;

        /**
         * A constant representing the unique identifier for a Dragon Fireball projectile entity type.
         * This value is used to refer to dragon fireball entities in the context of entity handling,
         * communication, or tracking within specific frameworks or systems.
         */
        public static final int DRAGON_FIREBALL = 93;

        /**
         * A singleton instance of the {@code ObjectTypes} class.
         *
         * This instance serves as the centralized access point for the {@code ObjectTypes} enumeration,
         * which represents various object types identified by integer constants. These constants are
         * used to manage the representation of specific entities and objects in server-side operations
         * for use cases such as spawning, tracking, and interaction.
         *
         * Access the singleton instance using the {@code getInstance()} method.
         */
        private static ObjectTypes INSTANCE = new ObjectTypes();

        /**
         * Retrieves the singleton instance of the ObjectTypes class.
         * This method provides access to the single, shared instance of ObjectTypes.
         *
         * @return the singleton instance of ObjectTypes.
         */
        public static ObjectTypes getInstance() {
            return INSTANCE;
        }
    }

    /**
     * Constructs a new instance of the {@code WrapperPlayServerSpawnEntity} class,
     * which serves as a wrapper for the server-side packet responsible for spawning entities in the game world.
     *
     * This constructor initializes a new packet container of the appropriate type for handling
     * entity spawn packets and sets its default values.
     *
     * The constructed packet wrapper ensures that the core functionality for interacting with
     * entity spawn data (e.g., position, velocity, entity type, etc.) is correctly initialized
     * for further modifications or retrieval operations.
     */
    public WrapperPlayServerSpawnEntity() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Constructs a new instance of the {@code WrapperPlayServerSpawnEntity} class.
     * This constructor initializes the packet wrapper with the provided packet,
     * ensuring it represents a valid server-side packet for spawning entities.
     *
     * @param packet the packet container representing the raw entity spawn packet.
     *               This must not be null and must contain the correct type of packet.
     */
    public WrapperPlayServerSpawnEntity(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Constructs a new instance of the {@code WrapperPlayServerSpawnEntity} class,
     * initializing the wrapper with an entity, type, and object data.
     *
     * This constructor creates a packet using the provided entity, type, and object data
     * to represent the server-side packet responsible for spawning the given entity in
     * the game world.
     *
     * @param entity the {@link Entity} to be spawned. This must not be null.
     * @param type the integer value representing the type of the entity. This is typically
     *             defined by the game/API and identifies the entity's category.
     * @param objectData additional data related to the object's specific type, used to
     *                   provide contextual information about the entity being spawned.
     */
    public WrapperPlayServerSpawnEntity(Entity entity, int type, int objectData) {
        super(fromEntity(entity, type, objectData), TYPE);
    }

    /**
     * Creates a packet container for spawning an entity using the provided entity,
     * type, and object data.
     *
     * @param entity the {@link Entity} to be spawned. This must not be null.
     * @param type the integer value representing the type of the entity. This is
     *             typically defined by the API and identifies the entity's category.
     * @param objectData additional data related to the object's specific type, used
     *                   to provide contextual information about the entity being spawned.
     * @return a {@link PacketContainer} that represents the packet for spawning the
     *         specified entity with the given type and object data.
     */
    private static PacketContainer fromEntity(Entity entity, int type,
                                              int objectData) {
        if (entityConstructor == null)
            entityConstructor =
                    ProtocolLibrary.getProtocolManager()
                            .createPacketConstructor(TYPE, entity, type,
                                    objectData);
        return entityConstructor.createPacket(entity, type, objectData);
    }

    /**
     * Retrieves the entity ID associated with the spawned entity.
     *
     * @return the integer ID of the entity being spawned.
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Retrieves an Entity instance corresponding to the given world.
     *
     * @param world the World object in which the entity exists. Must not be null.
     * @return the Entity instance associated with the provided World, or null if no entity is found.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieves an Entity instance associated with the packet event.
     *
     * @param event the PacketEvent containing details about the packet and the associated player.
     *              The player's world is used to locate the entity. Must not be null.
     * @return the Entity instance found in the player's world, or null if no entity is found.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Sets the entity ID associated with the spawned entity.
     *
     * @param value the integer ID of the entity to be set. This ID is used to uniquely identify
     *              the entity for the associated server-side spawn packet.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieves the unique identifier (UUID) associated with the entity being spawned.
     *
     * @return the UUID of the entity, representing its unique identifier within the game world.
     */
    public UUID getUniqueId() {
        return handle.getUUIDs().read(0);
    }

    /**
     * Sets the unique identifier (UUID) associated with the entity being spawned.
     *
     * @param value the UUID representing the unique identifier of the entity.
     *              This ensures the entity can be uniquely tracked within the game world.
     */
    public void setUniqueId(UUID value) {
        handle.getUUIDs().write(0, value);
    }

    /**
     * Retrieves the X-coordinate of the spawned entity's position.
     *
     * @return the current X-coordinate as a double.
     */
    public double getX() {
        return handle.getDoubles().read(0);
    }

    /**
     * Sets the X-coordinate for the spawned entity's position.
     *
     * @param value the new X-coordinate as a double.
     */
    public void setX(double value) {
        handle.getDoubles().write(0, value);
    }

    /**
     * Retrieves the Y-coordinate of the spawned entity's position.
     *
     * @return the current Y-coordinate as a double.
     */
    public double getY() {
        return handle.getDoubles().read(1);
    }

    /**
     * Sets the Y-coordinate for the spawned entity's position.
     *
     * @param value the new Y-coordinate as a double.
     */
    public void setY(double value) {
        handle.getDoubles().write(1, value);
    }

    /**
     * Retrieves the Z-coordinate of the spawned entity's position.
     *
     * @return the current Z-coordinate as a double.
     */
    public double getZ() {
        return handle.getDoubles().read(2);
    }

    /**
     * Sets the Z-coordinate for the spawned entity's position.
     *
     * @param value the new Z-coordinate as a double.
     */
    public void setZ(double value) {
        handle.getDoubles().write(2, value);
    }

    /**
     * Retrieves the optional horizontal speed along the X-axis for the
     * spawned entity.
     *
     * @return the optional speed along the X-axis as a double. The value
     *         is determined by dividing the stored integer by 8000.0. This
     *         value may not always be applicable depending on the entity type
     *         and object data.
     */
    public double getOptionalSpeedX() {
        return handle.getIntegers().read(1) / 8000.0D;
    }

    /**
     * Sets the optional horizontal speed along the X-axis for the spawned entity.
     *
     * The input value represents the speed, which is internally converted to an integer
     * by multiplying it by 8000.0 before being written to the packet data.
     *
     * @param value the optional speed along the X-axis as a double.
     *              This value may not always be applicable depending on the entity type
     *              and object data.
     */
    public void setOptionalSpeedX(double value) {
        handle.getIntegers().write(1, (int) (value * 8000.0D));
    }

    /**
     * Retrieves the optional Y-axis speed from the handle's integer data.
     *
     * @return the optional Y-axis speed, calculated as the value at index 2 in the integer list divided by 8000.0
     */
    public double getOptionalSpeedY() {
        return handle.getIntegers().read(2) / 8000.0D;
    }

    /**
     * Sets the optional Y-axis speed for the entity by converting the given value
     * to an appropriate integer scale and writing it to the specified field.
     *
     * @param value the speed value for the Y-axis, where the value is scaled by
     *              multiplying with 8000.0 before being written
     */
    public void setOptionalSpeedY(double value) {
        handle.getIntegers().write(2, (int) (value * 8000.0D));
    }

    /**
     * Retrieves the optional speed in the Z direction.
     *
     * @return The optional speed along the Z axis as a double, calculated by dividing the raw integer value by 8000.0.
     */
    public double getOptionalSpeedZ() {
        return handle.getIntegers().read(3) / 8000.0D;
    }

    /**
     * Sets the optional speed in the Z direction by converting the given value
     * into an integer representation and writing it to the specified index.
     *
     * @param value the speed value in the Z direction, which will be scaled
     *              and converted for internal use
     */
    public void setOptionalSpeedZ(double value) {
        handle.getIntegers().write(3, (int) (value * 8000.0D));
    }

    /**
     * Retrieves the pitch value based on internally stored data.
     * The pitch is calculated by scaling an integer value to a float within the range of 0 to 360 degrees.
     *
     * @return the pitch value as a float, scaled within the range of 0 to 360 degrees.
     */
    public float getPitch() {
        return (handle.getIntegers().read(4) * 360.F) / 256.0F;
    }

    /**
     * Sets the pitch of an object. The pitch represents the up or down angle of an object
     * relative to its standard orientation.
     *
     * @param value the pitch value to set, where the value represents the angle in degrees.
     *              A positive value tilts the object upwards, and a negative value tilts it downwards.
     */
    public void setPitch(float value) {
        handle.getIntegers().write(4, (int) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieves the yaw angle from the data source. The yaw is calculated
     * by converting the raw integer data into a float representing an
     * angular value between 0 and 360 degrees.
     *
     * @return the yaw angle as a float, scaled between 0.0 and 360.0 degrees.
     */
    public float getYaw() {
        return (handle.getIntegers().read(5) * 360.F) / 256.0F;
    }

    /**
     * Sets the yaw value by converting the specified float value to an integer
     * in the protocol format. The yaw value represents the horizontal rotation.
     *
     * @param value the yaw angle in degrees, which will be converted and written
     *              into the internal data structure
     */
    public void setYaw(float value) {
        handle.getIntegers().write(5, (int) (value * 256.0F / 360.0F));
    }

    /**
     * Retrieves the type value from the handle's integer list at a specific index.
     *
     * @return the type value as an integer retrieved from the handle's integer list.
     */
    public int getType() {
        return handle.getIntegers().read(6);
    }

    /**
     * Sets the type value in the handle's integer list at a specific index.
     *
     * @param value the integer value to set as the type
     */
    public void setType(int value) {
        handle.getIntegers().write(6, value);
    }

    /// old documentation
    /// Retrieve object data.
    ///
    /// The content depends on the object type:
    /// <table border="1" cellpadding="4">
    /// <tr>
    /// <th>Object Type:</th>
    /// <th>Name:</th>
    /// <th>Description</th>
    /// </tr>
    /// <tr>
    /// <td>ITEM_FRAME</td>
    /// <td>Orientation</td>
    /// <td>0-3: South, West, North, East</td>
    /// </tr>
    /// <tr>
    /// <td>FALLING_BLOCK</td>
    /// <td>Block Type</td>
    /// <td>BlockID | (Metadata << 0xC)</td>
    /// </tr>
    /// <tr>
    /// <td>Projectiles</td>
    /// <td>Entity ID</td>
    /// <td>The entity ID of the thrower</td>
    /// </tr>
    /// <tr>
    /// <td>Splash Potions</td>
    /// <td>Data Value</td>
    /// <td>Potion data value.</td>
    /// </tr>
    /// </table>
    ///
    /// @return The current object Data

    /**
     * Retrieves the integer data located at the specified position within the internal data structure.
     *
     * @return the integer value from the internal structure at the specified index.
     */
    public int getObjectData() {
        return handle.getIntegers().read(7);
    }

    /**
     * Sets the object data by writing the given integer value to a specified index.
     *
     * @param value the integer value to be written to the object data at index 7
     */
    public void setObjectData(int value) {
        handle.getIntegers().write(7, value);
    }
}