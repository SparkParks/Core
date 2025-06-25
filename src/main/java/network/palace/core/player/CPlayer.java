package network.palace.core.player;

import network.palace.core.economy.TransactionCallback;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.plugin.Plugin;
import network.palace.core.tracking.GameType;
import network.palace.core.tracking.StatisticType;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a player in the game world. Provides a wide range of methods
 * to manipulate player data, control player interactions, manage inventory,
 * and handle various in-game attributes.
 *
 * The CPlayer class enables developers to work with both player-specific
 * features (such as health, food level, equipment, gamemode, etc.) and
 * interactions (like teleportation, messaging, visibility, etc.).
 *
 * Key functionalities include:
 * - Managing player health, food level, fire ticks, and attributes.
 * - Handling inventory items, equipment slots, and updates.
 * - Controlling player permissions, gamemodes, teleportation, and visibility.
 * - Interacting with action bars, boss bars, and achievement systems.
 * - Sending formatted messages and personalizing player display names.
 *
 * This class facilitates player-related tasks within plugins and game management systems.
 */
public interface CPlayer {

    /**
     * Retrieves the name associated with this instance.
     *
     * @return the name as a String
     */
    String getName();

    /**
     * Retrieves the protocol identifier associated with this instance.
     *
     * @return the protocol identifier as an integer
     */
    int getProtocolId();

    /**
     * Sets the protocol identifier.
     *
     * @param id the protocol identifier to be set
     */
    void setProtocolId(int id);

    /**
     * Checks if the system, service, or user is currently online.
     *
     * @return true if online, false otherwise
     */
    boolean isOnline();

    /**
     * Sets the operational state.
     *
     * @param isOp a boolean value indicating the operational state to be set;
     *             true to enable operation, false to disable it
     */
    void setOp(boolean isOp);

    /**
     * Determines whether the current object or entity has operational privileges or permissions.
     *
     * @return true if the object or entity is considered an operator or has operational privileges; false otherwise
     */
    boolean isOp();

    /**
     * Retrieves the unique identifier of the entity.
     *
     * <p>
     * This method is used to obtain the ID of an entity, which serves
     * as a distinct numerical identifier for it. The ID is typically
     * utilized for database operations, entity comparisons, or lookup purposes.
     * </p>
     *
     * @return an integer representing the unique identifier of the entity.
     */
    int getEntityId();

    /**
     * Plays a sound at the specified location with the given sound attributes.
     *
     * <p>This method is used to generate an auditory effect in the system by playing
     * a specific {@code Sound} at a particular {@code Location}, with controlled
     * {@code volume} and {@code pitch} to customize the output.
     *
     * @param location the {@link Location} at which the sound will be played.
     *                 This determines the spatial position of the sound in the environment.
     * @param sound    the {@link Sound} to be played. Specifies the type of sound
     *                 that will be emitted.
     * @param volume   the volume level of the sound, where higher values denote
     *                 louder sounds. Must be a non-negative value.
     * @param pitch    the pitch of the sound, which controls the tonal frequency.
     *                 A value of 1.0 represents the normal pitch.
     */
    void playSound(Location location, Sound sound, float volume, float pitch);

    /**
     * Sets the maximum health value for an entity.
     *
     * <p>This method defines the upper limit for the entity's health.
     * The specified value must be a positive number. If the entity's
     * current health exceeds this value, it may need to be adjusted
     * separately to comply with the new maximum.</p>
     *
     * @param health The maximum health value to be set. Must be greater than 0.
     */
    void setMaxHealth(double health);

    /**
     * Sets the health value for an entity.
     * <p>
     * This method allows you to update the health level of an entity, ensuring it
     * does not exceed the allowable range defined by the system.
     * </p>
     *
     * @param health the new health value to be assigned. This value should be a
     *               positive number and typically within a defined range, depending
     *               on the specific implementation.
     */
    void setHealth(double health);

    /**
     * Retrieves the current health value of the entity.
     *
     * <p>This method is designed to provide the health status of the entity
     * as a numeric value. The health value can range depending on the
     * implementation and specific use case of the entity.</p>
     *
     * @return a <code>double</code> representing the entity's current health.
     *         The value may vary according to the entity's condition,
     *         damage received, or other influencing factors.
     */
    double getHealth();

    /**
     * Retrieves the maximum health value of an entity or object.
     * <p>
     * This method is annotated as {@code Deprecated} and may be removed in future versions.
     * It is advisable to check for alternative methods to obtain the maximum health value.
     * </p>
     *
     * @return The maximum health as a {@code double} value, representing the highest health level possible.
     */
    @Deprecated
    double getMaxHealth();

    /**
     * Retrieves the current food level value.
     * <p>
     * The food level represents the amount of nourishment or energy available.
     * It usually ranges from a specific minimum to maximum value depending on the implementation.
     * </p>
     *
     * @return an integer representing the current food level.
     */
    int getFoodLevel();

    /**
     * Sets the food level for an entity or character.
     * <p>
     * The food level represents the amount of food or hunger satisfaction
     * an entity has, typically measured as an integer value. This value
     * may affect gameplay mechanics such as regeneration or exhaustion.
     *
     * @param level The new food level to set. <br>
     *              <ul>
     *                  <li>Should be a non-negative integer.</li>
     *                  <li>Typically within the specific range of gameplay limits.</li>
     *                  <li>Values outside valid bounds might be clamped or cause unpredictable behavior.</li>
     *              </ul>
     */
    void setFoodLevel(int level);

    /**
     * Retrieves the number of ticks remaining before fire on the entity is extinguished.
     *
     * <p>The fire ticks indicate the duration in game ticks that the entity will
     * remain on fire. A value of 0 typically signifies that the entity is no longer
     * burning.
     *
     * @return the number of ticks the entity will remain on fire, or 0 if not on fire
     */
    int getFireTicks();

    /**
     * Retrieves the maximum number of ticks that an entity can remain on fire.
     * <p>
     * The value returned by this method can be used to determine the total
     * duration an entity may stay in its burning state.
     *
     * @return the maximum number of fire ticks for an entity.
     */
    int getMaxFireTicks();

    /**
     * Sets the number of ticks for which the entity will remain on fire.
     *
     * <p>
     * The fire ticks determine how long the entity will continue to take fire damage
     * and produce burning effects. A value of 0 will extinguish the fire effect,
     * while a positive value will set the entity on fire for the specified number of ticks.
     * </p>
     *
     * @param ticks the number of ticks the entity will stay on fire.
     *              <ul>
     *                  <li>A value of 0 will extinguish the fire.</li>
     *                  <li>A positive value will set the entity on fire for a specific duration.</li>
     *              </ul>
     */
    void setFireTicks(int ticks);

    /**
     * Retrieves the {@link AttributeInstance} associated with the specified {@link Attribute}.
     *
     * <p>This method returns the instance of the attribute provided, enabling
     * interactions or modifications to its properties. If the specified attribute
     * is not present, this method may return {@code null}.
     *
     * @param attribute the {@link Attribute} object representing the specific attribute to retrieve.
     *                   Must not be {@code null}.
     * @return the {@link AttributeInstance} associated with the given {@link Attribute},
     *         or {@code null} if the attribute is not applicable or does not exist.
     */
    AttributeInstance getAttribute(Attribute attribute);

    /**
     * Retrieves the current game mode.
     *
     * <p>This method returns the {@code GameMode} currently assigned,
     * indicating the mode in which the game is being played.</p>
     *
     * @return the current {@code GameMode}
     */
    GameMode getGamemode();

    /**
     * Sets the game mode for a player or system.
     *
     * <p>This method allows you to set the game mode dynamically by passing
     * a valid {@code GameMode} instance. It ensures that the correct
     * game mode is applied as per the requirements.
     *
     * @param gamemode the {@code GameMode} to set. This specifies the desired mode
     *                 that determines the game rules and mechanics.
     *                 <ul>
     *                   <li>Examples of {@code GameMode} types might include SURVIVAL, CREATIVE, ADVENTURE, etc.</li>
     *                   <li>Must not be null.</li>
     *                 </ul>
     */
    void setGamemode(GameMode gamemode);

    /**
     * Retrieves the location information associated with the current context.
     *
     * <p>This method provides details about the geographical or logical location
     * that may be relevant to the associated entity or functionality within the application.</p>
     *
     * @return a {@code Location} object representing the location information.
     *         The object may contain data such as coordinates, address details, or any
     *         specific attributes related to the location.
     */
    Location getLocation();

    /**
     * Retrieves the current World instance.
     * <p>
     * This method provides access to the World object which may contain
     * information and functionalities related to the environment,
     * entities, and overall state.
     * </p>
     *
     * @return the World instance representing the current state of the environment.
     */
    World getWorld();

    /**
     * Teleports an entity or player to the specified location.
     *
     * <p>This method moves the caller to the given {@code location}, which includes
     * coordinates and possibly additional details such as dimension or world.</p>
     *
     * @param location The destination {@link Location} object specifying where the
     *                 entity or player should be teleported.
     */
    void teleport(Location location);

    /**
     * Teleports a player to the specified location using the specified cause.
     *
     * <p>This method allows moving the player to a given destination based on
     * the provided location and teleportation cause. It is important to ensure
     * the location is valid and the cause aligns with the desired teleportation
     * context.</p>
     *
     * @param location The destination location where the player will be teleported.
     *                 It must not be null and should represent a valid in-game position.
     * @param cause    The reason or cause for the teleportation. This parameter
     *                 dictates why the teleportation occurred and must be a valid
     *                 {@link PlayerTeleportEvent.TeleportCause}.
     */
    void teleport(Location location, PlayerTeleportEvent.TeleportCause cause);

    /**
     * Teleports the specified player to a designated location.
     *
     * <p>This method is used to instantly relocate a player in the game.
     * Ensure that the player's target location is valid before invoking this
     * method to prevent unexpected behavior.</p>
     *
     * @param tp the {@link CPlayer} object representing the player to be teleported
     */
    void teleport(CPlayer tp);

    /**
     * Sends a message to the designated recipient or system.
     * <p>
     * This method takes a string message and performs the operation
     * necessary to transmit it. The specific behavior of this method
     * depends on the implementation details of the system using it.
     * </p>
     *
     * @param message The message to be sent. It should be a non-null
     *                and non-empty string representing the content
     *                to transmit.
     */
    void sendMessage(String message);

    /**
     * Retrieves a formatted message based on the provided key.
     * <p>
     * This method looks up a message associated with the given key, applies any
     * necessary formatting, and then returns the resulting message as a string.
     * </p>
     *
     * @param key The key used to identify and retrieve the corresponding message.
     *            It must not be null or empty.
     * @return A formatted string representing the message associated with the given key.
     *         If no message is found for the provided key, it may return a default or empty string.
     */
    String getFormattedMessage(String key);

    /**
     * Sends a formatted message based on the provided key.
     * The key is used to look up a predefined template or format,
     * allowing dynamic generation of messages.
     *
     * @param key The unique identifier used to retrieve and format the message.
     */
    void sendFormatMessage(String key);

    /**
     * Resets the player to its initial state.
     * <p>
     * This method is intended to clear the current state of the player object and
     * return it to its default state as defined by the implementation.
     * </p>
     *
     * <p>Typical operations performed by this method may include:</p>
     * <ul>
     *   <li>Resetting the player's position.</li>
     *   <li>Clearing the player's current score or statistics.</li>
     *   <li>Reinitializing specific attributes to default values.</li>
     *   <li>Stopping active processes or tasks associated with the player.</li>
     * </ul>
     *
     * <p>It's important to invoke this method whenever the player needs
     * reinitialization, such as starting a new game session, to ensure a consistent state.</p>
     *
     * <p>Ensure any dependencies or states altered by this method are compatible
     * with other parts of the system to avoid unintended behaviors.</p>
     */
    void resetPlayer();

    /**
     * Resets the state of all manager components within the system to their default configuration.
     * <p>
     * This method is typically used to reinitialize manager instances, clearing any
     * cached data, temporary states, or other runtime changes. It ensures that all
     * managers are restored to their initial state as defined at system startup or
     * during initialization.
     * <p>
     * <b>Note:</b> This method does not persist any changes or perform backups,
     * so any unsaved data may be lost. Use it with caution during runtime operations.
     * <p>
     * Potential effects of calling this method include but are not limited to:
     * <ul>
     *   <li>Clearing in-memory caches or buffers managed by the respective managers.</li>
     *   <li>Resetting internal counters, flags, or timestamps.</li>
     *   <li>Releasing or reinitializing resources managed by managers (e.g., connections, threads).</li>
     * </ul>
     */
    void resetManagers();

    /**
     * Sets the display name for this object.
     * <p>
     * The display name is a user-defined name that represents the object
     * in a more readable or user-friendly format.
     *
     * @param name the display name to set; must not be {@code null} or empty
     */
    void setDisplayName(String name);

    /**
     * Retrieves the inventory associated with the player.
     *
     * <p>This method provides access to the player's inventory, which includes
     * all items and equipment currently held by the player within the game.
     *
     * @return the {@code PlayerInventory} object representing the player's inventory,
     *         allowing for inspection, modification, and management of the items
     *         and equipment within it.
     */
    PlayerInventory getInventory();

    /**
     * Updates the inventory by performing necessary operations such as checking
     * current stock, applying updates, and synchronizing changes.
     * <p>
     * This method ensures that the inventory data remains accurate and consistent.
     * It is commonly used when there are changes in stock levels due to
     * purchases, returns, restocking, or other inventory-related events.
     * </p>
     * <p>
     * Note: This method does not accept any parameters or return any values.
     * The inventory update process may rely on predefined configurations
     * or external data sources.
     * </p>
     * <h2>Key Points:</h2>
     * <ul>
     * <li>Validates existing inventory records before applying updates.</li>
     * <li>Handles synchronization of the inventory system to ensure consistency.</li>
     * <li>Logs relevant changes, if applicable, for tracking purposes.</li>
     * </ul>
     * <p>
     * If errors occur during the update process, appropriate mechanisms should be
     * in place to handle exceptions without disrupting system stability.
     * </p>
     */
    void updateInventory();

    /**
     * Retrieves the item stored in the specified inventory slot.
     *
     * <p>This method fetches the {@link ItemStack} object located at the provided
     * slot index in the inventory. If the slot index is empty or does not exist,
     * the method may return null or an empty {@code ItemStack} depending on the
     * implementation.</p>
     *
     * @param slot The index of the inventory slot to retrieve the item from.
     *             Must be a non-negative integer within the valid range of the inventory size.
     *
     * @return The {@link ItemStack} contained in the specified slot.
     *         Returns {@code null} or an equivalent empty {@code ItemStack}
     *         if the slot is empty or invalid.
     */
    ItemStack getItem(int slot);

    /**
     * Retrieves the item currently held in the main hand of an entity.
     * <p>
     * This method returns an {@link ItemStack} representing the item equipped
     * in the main hand. In case the main hand is empty, the returned {@link ItemStack}
     * will indicate no item present.
     *
     * <p>
     * Use this method to examine or manipulate the item being held by an entity
     * in their main hand slot.
     *
     * @return the {@link ItemStack} currently present in the main hand, or an empty {@link ItemStack} if no item is held
     */
    ItemStack getItemInMainHand();

    /**
     * Retrieves the item currently held in the entity's off-hand slot.
     *
     * <p>The off-hand slot is typically used for secondary items such as shields, torches, or
     * other complementary tools or weapons. This method will return an {@code ItemStack}
     * representing the item in the off-hand slot, or an empty {@code ItemStack} if no item
     * is present.</p>
     *
     * @return an {@code ItemStack} representing the item in the off-hand slot, or an empty
     *         {@code ItemStack} if the off-hand slot is empty.
     */
    ItemStack getItemInOffHand();

    /**
     * Retrieves the inventory slot number currently designated for the held item.
     *
     * <p>This method is used to determine which slot in the inventory the player
     * or entity has selected as the active, held item slot. The value returned
     * corresponds to the slot index.</p>
     *
     * @return The integer index of the current held item slot, where the index typically starts at 0
     *         for the first slot and increments for subsequent slots.
     */
    int getHeldItemSlot();

    /**
     * Sets the currently held item slot for the entity.
     * <p>
     * This method updates the active inventory slot that the entity is using.
     * Entities such as players or mobs can use this method to switch
     * their active held item.
     *
     * @param slot The inventory slot index to set as the held item. Must be a valid slot index.
     */
    void setHeldItemSlot(int slot);

    /**
     * Opens the specified inventory for interaction or manipulation.
     *
     * <p>This method enables access to an inventory, allowing the user or system to
     * view, modify, or interact with its contents as needed.</p>
     *
     * @param inventory the inventory to be opened. This parameter must not be null
     *                  and should represent a valid inventory object that can be accessed.
     */
    void openInventory(Inventory inventory);

    /**
     * Closes the inventory system and performs any necessary cleanup operations.
     *
     * <p>This method is typically used to ensure all resources associated with the inventory,
     * such as database connections or file streams, are properly released.
     *
     * <p><strong>Behavior:</strong>
     * <ul>
     *   <li>The inventory will no longer be available for operations after this method is called.</li>
     *   <li>Any pending changes to the inventory should be completed or saved before invoking this method.</li>
     *   <li>Calling this method multiple times may result in no operation or idempotent behavior, depending on the implementation.</li>
     * </ul>
     *
     * <p><strong>Implementation Notes:</strong>
     * <ul>
     *   <li>Ensure proper error handling during cleanup to avoid leaving the application in an inconsistent state.</li>
     *   <li>Consider logging the closure action for debugging or auditing purposes.</li>
     * </ul>
     */
    void closeInventory();

    /**
     * Checks if the permission node provided is granted.
     *
     * <p>This method evaluates whether the specified permission node is available
     * for the current context or user.</p>
     *
     * @param node the permission node to check. It should be a non-null
     *        and non-empty string that represents the specific permission path.
     * @return {@code true} if the permission node is granted; {@code false} otherwise.
     */
    boolean hasPermission(String node);

    /**
     * Sets an item stack in the specified inventory slot.
     *
     * <p>This method is used to update the contents of a specific slot in an inventory
     * with the provided {@code ItemStack}. The slot index must be within the valid
     * range of the inventory slots.</p>
     *
     * @param slot the inventory slot index to be updated. Must be a non-negative integer
     *             and within the bounds of the inventory size.
     * @param stack the {@link ItemStack} to be placed in the specified slot. Can be null
     *              or empty to signify clearing the slot.
     */
    void setInventorySlot(int slot, ItemStack stack);

    /**
     * Adds one or more {@code ItemStack} objects to the inventory.
     * <p>
     * This method allows adding multiple items to the inventory in a single call.
     * Each {@code ItemStack} represents a stack of items that should be added.
     *
     * @param itemStacks the array of {@code ItemStack} objects to be added to the inventory.
     *                   <ul>
     *                     <li>Each {@code ItemStack} contains details about the item type, quantity, and other metadata.</li>
     *                     <li>Cannot be {@code null}; an empty array will result in no change to the inventory.</li>
     *                   </ul>
     */
    void addToInventory(ItemStack... itemStacks);

    /**
     * Checks whether the inventory contains the specified material.
     *
     * <p>This method determines if the given material is present within the inventory.
     * The inventory is searched to match the specified material, and the method
     * returns a boolean indicating the presence or absence of the material.</p>
     *
     * @param material the material to check for in the inventory.
     *                 This parameter should not be null.
     *
     * @return {@code true} if the inventory contains the specified material;
     *         {@code false} otherwise.
     */
    boolean doesInventoryContain(Material material);

    /**
     * Removes the specified material from the inventory.
     *
     * <p>This method deducts the given material from the inventory.
     * It assumes that the material exists in the inventory.
     *
     * @param material The {@link Material} object to be removed from the inventory.
     */
    void removeFromInventory(Material material);

    /**
     * Retrieves the helmet item currently equipped by an entity.
     *
     * <p>This method returns an {@link ItemStack} representing the helmet
     * being worn. If there is no helmet equipped, this will typically
     * return an {@link ItemStack} with a null or empty state, depending on
     * the implementation.</p>
     *
     * @return an {@link ItemStack} representing the equipped helmet, or an empty/null {@link ItemStack} if no helmet is equipped.
     */
    ItemStack getHelmet();

    /**
     * Sets the helmet of the entity or player to the provided {@code itemStack}.
     *
     * <p>This method updates the entity's or player's helmet to the specified
     * {@code itemStack}. If {@code itemStack} is null, it removes the current helmet
     * equipped by the entity or player. Ensure the provided {@code itemStack} is a valid
     * item for a helmet to prevent unexpected behavior.</p>
     *
     * @param itemStack the {@link ItemStack} to be used as the helmet. Can be null to remove the current helmet.
     */
    void setHelmet(ItemStack itemStack);

    /**
     * Retrieves the chestplate item currently equipped.
     * <p>
     * This method returns the {@code ItemStack} representing the chestplate
     * that is currently equipped. If no chestplate is equipped, this method
     * may return {@code null} or an empty {@code ItemStack}, depending on the implementation.
     *
     * @return the {@code ItemStack} representing the equipped chestplate,
     *         or {@code null}/empty {@code ItemStack} if none is equipped
     */
    ItemStack getChestplate();

    /**
     * Sets the chestplate item for the entity or player.
     *
     * <p>This method assigns the specified {@link ItemStack} to the chestplate slot,
     * allowing the entity or player to equip the provided chestplate item.</p>
     *
     * @param itemStack the {@link ItemStack} to set as the chestplate.
     *                  It should represent a valid item to be used as a chestplate.
     *                  Passing null may unequip the current chestplate.
     */
    void setChestplate(ItemStack itemStack);

    /**
     * Retrieves the leggings item currently associated with the entity.
     *
     * <p>This method provides access to the item worn in the leggings slot,
     * typically part of the entity's armor set.</p>
     *
     * @return an {@link ItemStack} representing the leggings item. If the entity
     *         does not currently have leggings equipped, this will return an
     *         empty {@link ItemStack}.
     */
    ItemStack getLeggings();

    /**
     * Sets the leggings for the entity or player.
     *
     * <p>This method allows you to set a specific {@link ItemStack} as the leggings
     * equipped by the entity or player.</p>
     *
     * @param itemStack the {@link ItemStack} representing the leggings to be set.
     *                  <ul>
     *                      <li>If the {@code itemStack} is {@code null}, the leggings will be removed.</li>
     *                      <li>An {@code ItemStack} with appropriate type must be provided to set the leggings correctly.</li>
     *                  </ul>
     */
    void setLeggings(ItemStack itemStack);

    /**
     * Retrieves the boots currently equipped.
     * <p>
     * This method returns an {@link ItemStack} object representing the boots
     * that are currently equipped. If no boots are equipped, this method
     * may return {@code null} or an empty {@code ItemStack}, depending on
     * the implementation.
     *
     * @return an {@link ItemStack} representing the equipped boots, or
     *         {@code null} if no boots are equipped.
     */
    ItemStack getBoots();

    /**
     * Sets the boots for the entity or object with the specified {@code ItemStack}.
     * <p>
     * This method assigns the given {@code ItemStack} as the entity's boots, replacing any
     * previously equipped boots. The {@code ItemStack} should represent a valid pair of boots to be worn.
     * </p>
     *
     * @param itemStack the {@link ItemStack} to be set as the boots.
     *                  <ul>
     *                    <li>If {@code null}, the entity will have no boots equipped.</li>
     *                    <li>If invalid or incompatible, an exception or undefined behavior may occur depending on implementation.</li>
     *                  </ul>
     */
    void setBoots(ItemStack itemStack);

    /**
     * Retrieves the item currently held in the main hand of the entity or player.
     *
     * <p>The main hand is typically considered the primary hand used for
     * actions such as interacting with objects or attacking.
     *
     * @return An {@code ItemStack} object representing the item held in the main hand.
     *         If the main hand is empty, this will return an {@code ItemStack} with
     *         an empty state.
     */
    ItemStack getMainHand();

    /**
     * Sets the main hand item to the specified {@link ItemStack}.
     *
     * <p>This method updates the main hand slot with the provided {@link ItemStack} instance.
     *
     * <p><b>Note:</b> Passing a null or invalid {@link ItemStack} may result in undesirable behavior.
     * Ensure that the {@link ItemStack} is valid and properly initialized before invoking this method.
     *
     * @param itemStack the {@link ItemStack} to set as the main hand item
     */
    void setMainHand(ItemStack itemStack);

    /**
     * Retrieves the item currently held in the off-hand slot of the player or entity.
     * <p>
     * The off-hand slot is typically used as a secondary holding for items or weapons,
     * allowing for dual-wield functionalities or supplementary item usage.
     * </p>
     *
     * @return An {@code ItemStack} representing the item in the off-hand slot.
     *         If the off-hand slot is empty, returns an empty {@code ItemStack}.
     */
    ItemStack getOffHand();

    /**
     * Sets the item in the entity's off-hand slot.
     *
     * <p>This method allows updating the item held in the entity's off-hand,
     * which is the hand opposite to the main-hand (usually the left hand for most entities).</p>
     *
     * @param itemStack the {@link ItemStack} to set in the off-hand.
     *                  <ul>
     *                      <li>If the parameter is {@code null}, the off-hand will be cleared.</li>
     *                      <li>If the parameter is a valid {@link ItemStack}, it will be assigned to the off-hand.</li>
     *                  </ul>
     */
    void setOffHand(ItemStack itemStack);

    /**
     * Triggers the respawn mechanism for an entity or object within the system.
     * <p>
     * This method typically resets the associated object or entity to its initial
     * state, restoring it to its default position, attributes, or other relevant
     * conditions as defined in implementation.
     * </p>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Removes any transient states or modifications.</li>
     *   <li>Reinitializes the entity to behave as if it was newly created.</li>
     *   <li>Commonly used in gaming or simulation environments where entities
     *       need to restart their activity.</li>
     * </ul>
     *
     * <p>Implementation of this method may vary depending on the specific system's
     * rules for how and when objects should respawn.</p>
     */
    void respawn();

    /**
     * Displays information about the specified player.
     *
     * <p>This method is used to show relevant details of a player,
     * such as their name, attributes, or any other significant data
     * associated with a <code>CPlayer</code> object.
     *
     * @param player the <code>CPlayer</code> object representing
     *        the player whose details are to be displayed. This
     *        parameter must not be <code>null</code>.
     */
    void showPlayer(CPlayer player);

    /**
     * Displays information about a given player using the provided plugin.
     *
     * <p>
     * This method processes and shows data related to the specified player using
     * the supplied plugin instance. The specific behavior and output depend on
     * the implementation within the method and the features supported by the plugin.
     * </p>
     *
     * @param plugin The Bukkit plugin instance that facilitates player-related operations.
     *               Must not be null.
     * @param player The target {@code CPlayer} whose information is to be shown.
     *               Must not be null.
     */
    void showPlayer(org.bukkit.plugin.Plugin plugin, CPlayer player);

    /**
     * Hides the specified player, making them invisible or untrackable in the game environment.
     *
     * <p>This method is typically used to remove the player's visibility from other players or
     * entities. It can be useful for implementing features like stealth, spectators, or
     * temporary invulnerability. The effect depends on the game logic tied to hidden players.</p>
     *
     * @param player The player object representing the entity to be hidden.
     *               Must not be null and should represent a valid, active in-game player.
     */
    void hidePlayer(CPlayer player);

    /**
     * Hides a player from being visible to others within the game.
     * It essentially makes the specified player invisible to those
     * in the game world, they cannot be seen or interacted with by others.
     *
     * <p>Usage of this method is typically for special scenarios like
     * spectator mode, admin purposes, or temporary invisibility effects.</p>
     *
     * @param plugin The plugin instance that is invoking this method.
     *               It is used to track and manage the action context.
     *               Must not be null.
     * @param player The player to be hidden. This player will become invisible
     *               to other players in the game. Represents the targeted
     *               player object and must not be null.
     */
    void hidePlayer(org.bukkit.plugin.Plugin plugin, CPlayer player);

    /**
     * Determines whether the current entity or user has visibility or the ability to see the specified player.
     *
     * <p>This method evaluates visibility conditions, which may include factors such as distance, line of sight,
     * environmental factors, or other criteria specific to the application's logic.</p>
     *
     * @param player the {@code CPlayer} instance representing the player whose visibility is being checked
     * @return {@code true} if the entity can see the given player; {@code false} otherwise
     */
    boolean canSee(CPlayer player);

    /**
     * Determines if the current entity can see the specified player.
     *
     * <p>This method evaluates whether the current entity has an unobstructed view
     * of the given player's position, considering environmental factors and visibility constraints.
     *
     * @param player the player whose visibility is being checked. This parameter must not be null.
     * @return {@code true} if the entity can see the player; {@code false} otherwise.
     */
    boolean canSee(Player player);

    /**
     * Retrieves the achievement manager associated with the player.
     *
     * <p>The achievement manager is responsible for handling
     * and tracking the player's achievements within the game.</p>
     *
     * @return an instance of {@code CPlayerAchievementManager} that manages
     *         the player's achievements.
     */
    CPlayerAchievementManager getAchievementManager();

    /**
     * Retrieves the action bar manager associated with the player.
     * <p>
     * This method provides access to the {@code CPlayerActionBarManager},
     * which manages the action bar functionality for a player.
     * This can include updating, clearing, or modifying the action bar content.
     *
     * @return an instance of {@code CPlayerActionBarManager} that handles
     *         action bar operations for the player.
     */
    CPlayerActionBarManager getActionBar();

    /**
     * Retrieves the CPlayerBossBarManager associated with the current instance.
     * <p>
     * This method provides access to the manager responsible for handling player-specific
     * boss bar functionality and operations.
     *
     * @return the {@code CPlayerBossBarManager} instance managing boss bar operations.
     */
    CPlayerBossBarManager getBossBar();

    /**
     * Retrieves the instance of {@code CPlayerHeaderFooterManager}, which is responsible
     * for managing the header and footer display for a player.
     *
     * <p>This method provides access to the manager that controls and updates
     * the visual elements such as headers and footers associated with the player.
     *
     * @return an instance of {@code CPlayerHeaderFooterManager} that handles
     *         the player's header and footer management.
     */
    CPlayerHeaderFooterManager getHeaderFooter();

    /**
     * Retrieves the {@code CPlayerParticlesManager} instance associated with the entity.
     * <p>
     * This method is typically used to fetch the manager responsible
     * for handling and managing particle effects tied to the player.
     * </p>
     *
     * @return the {@code CPlayerParticlesManager} instance managing the particle
     *         effects for the player.
     */
    CPlayerParticlesManager getParticles();

    /**
     * Retrieves the resource pack manager associated with the player.
     * <p>
     * This method provides access to the player's resource pack manager instance,
     * which can be used to handle resource pack operations such as loading or managing
     * resource packs specific to the player.
     *
     * @return The {@code CPlayerResourcePackManager} instance responsible for managing the resource packs of the player.
     */
    CPlayerResourcePackManager getResourcePack();

    /**
     * Retrieves the current instance of the {@code CPlayerScoreboardManager}, which is responsible
     * for managing scoreboards for players.
     *
     * <p>
     * This method allows access to the scoreboard manager, which can be used to manage
     * and interact with the scoreboards associated with players. The specific implementation
     * of the {@code CPlayerScoreboardManager} determines the features and functionalities
     * available for scoreboard operations.
     *
     * @return an instance of {@code CPlayerScoreboardManager} that manages player scoreboards.
     */
    CPlayerScoreboardManager getScoreboard();

    /**
     * Retrieves the instance of the <code>CPlayerRegistry</code>.
     * <p>
     * This method returns the registry that holds and manages player-related data
     * within the system. It may provide access to all players, register new players,
     * or perform other operations as defined in the registry's implementation.
     * </p>
     *
     * @return the <code>CPlayerRegistry</code> instance responsible for managing player data.
     */
    CPlayerRegistry getRegistry();

    /**
     * Sets the scoreboard for the specified {@code CPlayerScoreboardManager}.
     *
     * <p>This method associates a scoreboard with the given manager. The scoreboard
     * helps manage and display player-specific data such as scores, teams, or statistics.
     * Ensure that the provided {@code CPlayerScoreboardManager} is properly initialized
     * before calling this method to avoid unexpected behavior.</p>
     *
     * @param manager the {@code CPlayerScoreboardManager} instance that will have its scoreboard set.
     */
    void setScoreboard(CPlayerScoreboardManager manager);

    /**
     * Retrieves the title manager associated with a player.
     *
     * <p>
     * This method returns an instance of {@code CPlayerTitleManager}, which provides
     * functionality to manage and manipulate titles for a specific player.
     * </p>
     *
     * @return an instance of {@code CPlayerTitleManager} managing the player's titles.
     */
    CPlayerTitleManager getTitle();

    /**
     * Sends the given packet to its intended recipient.
     *
     * <p>This method is responsible for transmitting the provided packet
     * using the underlying network or transport layer. It assumes the
     * packet encapsulates all necessary data for successful delivery.</p>
     *
     * @param packet the {@link AbstractPacket} instance to be sent. Must not be null.
     *               <ul>
     *                 <li>This packet should adhere to the expected protocol requirements.</li>
     *                 <li>Ensure the packet contains all fields necessary for proper handling.</li>
     *               </ul>
     *               Passing a null value may result in a {@code NullPointerException}.
     */
    void sendPacket(AbstractPacket packet);

    /**
     * Sends data to the specified server.
     *
     * <p>This method initiates a connection and transmits data
     * to the designated server identified by its address or name.</p>
     *
     * @param server The address or name of the server to which
     *               the data should be sent. Must not be null or empty.
     */
    void sendToServer(String server);

    /**
     * Sets the locale for the application or system.
     * <p>
     * This method configures the locale setting to match the specified locale string.
     * It is used to provide localization and internationalization support,
     * such as date formats, currency formats, or language preferences.
     * </p>
     *
     * @param locale A string representing the locale to be set.
     *               The format should typically follow the pattern
     *               <code>language_COUNTRY</code> (e.g., "en_US" for English (United States)
     *               or "fr_FR" for French (France)).
     */
    void setLocale(String locale);

    /**
     * Retrieves the current locale setting used by the application or system.
     *
     * <p>This method provides the locale as a string representation, which
     * can be used for tasks such as formatting dates, numbers, or providing
     * localized content.</p>
     *
     * @return a <code>String</code> representing the current locale setting.
     *         The format typically follows the pattern "language[_COUNTRY]"
     *         (e.g., "en_US" for English (United States)).
     */
    String getLocale();

    /**
     * Retrieves the Bukkit {@link Player} instance associated with this object.
     * <p>
     * This method is typically used to access the server-side representation of a player
     * in a Bukkit-based Minecraft server.
     *
     * @return the {@link Player} instance corresponding to this object, or {@code null}
     *         if no such player is currently associated or available.
     */
    Player getBukkitPlayer();

    /**
     * Retrieves a unique identifier (UUID).
     *
     * <p>This method generates or provides a universally unique identifier (UUID) that
     * can be used to distinguish objects, entities, or processes uniquely.
     *
     * @return a {@link UUID} object representing the unique identifier.
     */
    UUID getUniqueId();

    /**
     * Retrieves the unique identifier (UUID).
     *
     * <p>This method returns a UUID object that represents a globally unique identifier.
     *
     * @return a {@code UUID} object, which represents a universally unique identifier.
     */
    UUID getUuid();

    /**
     * Sets the status of the player.
     * <p>
     * This method updates the player's current status using the provided
     * {@link PlayerStatus} instance. The behavior of the player may depend
     * on the status being set.
     * </p>
     *
     * @param status the {@link PlayerStatus} that represents the new status
     *               of the player. Must not be <code>null</code>.
     */
    void setStatus(PlayerStatus status);

    /**
     * Retrieves the status of a player.
     *
     * <p>
     * This method returns the current status of a player, encapsulated in a
     * {@code PlayerStatus} object. The status may include information such as
     * the player's current state, health, achievements, or other related details
     * depending on the implementation of {@code PlayerStatus}.
     * </p>
     *
     * @return a {@code PlayerStatus} object representing the player's current status.
     */
    PlayerStatus getStatus();

    /**
     * Retrieves the rank of an entity or object.
     *
     * <p>This method is used to obtain the rank information,
     * which may be associated with a hierarchical or ordered system.
     *
     * @return the {@code Rank} representing the rank of the entity or object.
     */
    Rank getRank();

    /**
     * Sets the rank of an entity.
     *
     * <p>This method assigns a new rank to an entity. The rank describes the
     * hierarchical or categorical position of the entity within a system.</p>
     *
     * @param rank the rank to be assigned. It must be a valid {@code Rank} object.
     */
    void setRank(Rank rank);

    /**
     * Retrieves a list of tags associated with the current entity.
     *
     * <p>
     * The method returns a collection of {@code RankTag} objects. Each tag
     * represents a specific category, label, or marker relevant to the entity.
     * This can be used for classification or contextual purposes.
     * </p>
     *
     * @return a {@code List} of {@code RankTag} objects, representing the tags associated with the entity.
     */
    List<RankTag> getTags();

    /**
     * <p>
     * Adds a new tag to the system. The provided tag will be processed
     * and linked as part of the relevant entity or data structure.
     * This method ensures proper handling and integration of the supplied tag.
     * </p>
     *
     * @param tag The {@link RankTag} object to be added. It represents the tag data that
     *            is intended to be associated with the system. Ensure the tag is not
     *            <code>null</code> and adheres to the necessary format or constraints.
     */
    void addTag(RankTag tag);

    /**
     * Removes the specified tag from the collection of tags.
     * <p>
     * This method attempts to locate the given tag and remove it.
     * If the tag is successfully removed, the method will return <code>true</code>;
     * otherwise, it will return <code>false</code> if the tag does not exist
     * in the collection.
     *
     * @param tag the {@link RankTag} object to be removed
     * @return <code>true</code> if the tag was successfully removed;
     *         <code>false</code> otherwise
     */
    boolean removeTag(RankTag tag);

    /**
     * Checks if the specified tag is associated with the current object.
     *
     * <p>This method determines whether the provided {@link RankTag} exists within
     * the context of the current object. It can be used to verify whether a desired
     * tag is present.
     *
     * @param tag the {@link RankTag} object to check for, must not be null
     *
     * @return <code>true</code> if the specified tag is associated with the object;
     *         <code>false</code> otherwise
     */
    boolean hasTag(RankTag tag);

    /**
     * Sets the texture value for the corresponding object or component.
     * This method allows for configuring how a texture is represented or applied.
     *
     * <p>Texture values are typically used to define properties related to appearance,
     * such as patterns or material characteristics.</p>
     *
     * @param textureValue the texture value to be set.
     *                     <ul>
     *                        <li>Should be a non-null and non-empty string.</li>
     *                        <li>Represents the texture identifier or configuration.</li>
     *                     </ul>
     */
    void setTextureValue(String textureValue);

    /**
     * Retrieves the texture value as a string.
     *
     * <p>
     * This method provides the texture value associated with the object,
     * which may be used for rendering or other purposes that require texture data.
     * </p>
     *
     * @return a {@code String} representing the texture value. This value may be empty or null
     *         depending on the state of the object.
     */
    String getTextureValue();

    /**
     * Sets the texture signature for the object.
     * The texture signature is a unique identifier that represents
     * the visual characteristics or pattern of the texture being applied.
     *
     * <p>This method is used to configure or update the object's
     * texture properties with a specific signature. The provided
     * {@code textureSignature} must conform to the expected format
     * to ensure proper functionality.</p>
     *
     * @param textureSignature A {@code String} representing the
     *                         unique identifier of the texture to be applied.
     *                         This value must not be {@code null} or empty.
     */
    void setTextureSignature(String textureSignature);

    /**
     * Retrieves the texture signature for a specific texture or surface.
     * <p>
     * The texture signature is a unique identifier that represents the pattern,
     * properties, or characteristics of a given texture. It may be used for
     * comparison or identification purposes.
     *
     * @return a {@code String} representing the unique texture signature.
     */
    String getTextureSignature();

    /**
     * Sets the pack associated with this object.
     * <p>
     * This method allows assigning a specific pack to the object,
     * which can be used for categorization or other functional purposes.
     *
     * @param pack The name of the pack to set. It should be a non-null
     *             and valid string identifier representing the pack.
     */
    void setPack(String pack);

    /**
     * Retrieves the package details or identifier associated with this object.
     * <p>
     * The method returns a {@code String} that represents the package information.
     * This could be used to determine the grouping, categorization, or any
     * metadata associated with the object.
     *
     * @return A {@code String} containing the package details or relevant identifier.
     */
    String getPack();

    /**
     * Checks whether the given identifier corresponds to an existing achievement.
     *
     * <p>This method evaluates if an achievement exists for the supplied identifier.
     *
     * @param i the identifier of the achievement to check
     * @return {@code true} if the achievement exists, {@code false} otherwise
     */
    boolean hasAchievement(int i);

    /**
     * Assigns an achievement to a user based on the given achievement identifier.
     *
     * <p>This method is used to award an achievement to a user. The achievement
     * is identified by the supplied integer ID, which corresponds to a specific
     * predefined achievement in the system.</p>
     *
     * @param i The unique identifier for the achievement to be granted.
     */
    void giveAchievement(int i);

    /**
     * Retrieves the target block within the specified range.
     * <p>
     * This method identifies and returns a block that the player or entity is targeting
     * within the given range. It considers the player's or entity's direction
     * and range to determine the targeted block.
     *
     * @param range the maximum distance in blocks to search for the target block.
     *              Must be a positive integer.
     * <p>
     *              <ul>
     *                  <li>If the value is too high, no block may be found within the range.</li>
     *                  <li>A range of 0 or negative values is considered invalid.</li>
     *              </ul>
     * @return the {@code Block} object that is being targeted, or {@code null} if no block is found
     *         within the specified range or the input is invalid.
     */
    Block getTargetBlock(int range);

    /**
     * Retrieves the current ping value, which typically represents the
     * round-trip time for a signal to travel to a server and back.
     *
     * <p>This method can be useful for monitoring network latency
     * or connection quality in applications that rely on real-time
     * communication or data exchange.</p>
     *
     * @return an integer representing the ping value in milliseconds.
     */
    int getPing();

//    /**
//     * Set the player's ping to the server
//     *
//     * @param ping the current ping
//     */
//    void setPing(int ping);

    /**
     * Checks whether flight is allowed for the entity.
     *
     * <p>This method determines if the entity has permission or capability to fly.
     * It may depend on various conditions or configurations set in the environment.</p>
     *
     * @return <code>true</code> if the entity is allowed to fly; <code>false</code> otherwise.
     */
    boolean getAllowFlight();

    /**
     * Sets whether or not flight mode is permitted for the player.
     *
     * <p>This method enables or disables the player's ability to fly within the game. When
     * set to <code>true</code>, the player will be allowed to fly, whereas setting it to
     * <code>false</code> will disable flying.</p>
     *
     * @param fly <code>true</code> to allow the player to fly, <code>false</code> to disallow.
     */
    void setAllowFlight(boolean fly);

    /**
     * Determines whether the object is currently flying.
     *
     * <p>This method checks the state of the object and returns whether it is airborne.
     * Flying status is determined by the internal state and may depend on various factors
     * such as environment, conditions, and object capabilities.
     *
     * @return <code>true</code> if the object is currently flying, <code>false</code> otherwise.
     */
    boolean isFlying();

    /**
     * Retrieves the walking speed value.
     * <p>
     * This method returns the speed at which the entity walks. It is typically
     * represented as a floating-point value. The returned value can be used
     * to determine or adjust the walking pace of an entity.
     * </p>
     *
     * @return the walking speed as a float.
     */
    float getWalkSpeed();

    /**
     * Sets the walking speed for an entity.
     *
     * <p>This method allows you to define the speed at which an entity
     * moves while walking. A higher value indicates a faster walking
     * pace, while a lower value slows the movement.</p>
     *
     * @param speed The walking speed to be set. A positive float value
     *              representing the speed at which the entity should move.
     *              <ul>
     *                  <li>A value of 0 results in no movement.</li>
     *                  <li>Negative values are not recommended as they
     *                      may lead to undefined behavior.</li>
     *              </ul>
     */
    void setWalkSpeed(float speed);

    /**
     * Retrieves the flying speed of the entity.
     *
     * <p>This method returns the speed at which the entity can fly. The speed is
     * returned as a floating-point value and typically represents the flight speed
     * in units per second or in the relevant metrics used by the system.</p>
     *
     * @return the flying speed of the entity as a <code>float</code>.
     */
    float getFlySpeed();

    /**
     * Sets the flying speed of an entity.
     * <p>
     * This method allows assigning a specified flying speed to an entity.
     * The speed value should be a positive float representing the desired movement
     * speed in flight mode.
     * </p>
     *
     * @param speed the flying speed to set, where a higher value indicates faster movement
     *              and should be greater than or equal to 0.
     */
    void setFlySpeed(float speed);

    /**
     * Sets the sneaking state for the entity.
     * <p>
     * This determines whether the entity is currently sneaking or not.
     * A sneaking entity typically uses a slower movement speed and
     * may avoid detection by certain mechanisms.
     * </p>
     *
     * @param sneaking <code>true</code> to make the entity start sneaking,
     *                 <code>false</code> to make it stop sneaking.
     */
    void setSneaking(boolean sneaking);

    /**
     * Determines whether the entity is currently sneaking.
     * <p>
     * Sneaking is typically used to indicate that an entity is trying to move
     * stealthily or is in a crouched state.
     * <p>
     * This method returns a boolean value representing the sneak status.
     *
     * @return <code>true</code> if the entity is sneaking, <code>false</code> otherwise.
     */
    boolean isSneaking();

    /**
     * Sets the flying state of the object.
     * <p>
     * This method updates the flying status of the object
     * to either enabled or disabled based on the input parameter.
     *
     * @param fly A boolean value representing the desired flying state:
     *            <ul>
     *                <li><code>true</code> to enable flying.</li>
     *                <li><code>false</code> to disable flying.</li>
     *            </ul>
     */
    void setFlying(boolean fly);

    /**
     * Executes the action of kicking based on the provided reason.
     *
     * <p>This method ensures that the appropriate mechanisms are triggered
     * to process the kick action. The <code>reason</code> parameter specifies
     * the justification or message associated with the kick.</p>
     *
     * @param reason The reason or message explaining why the kick action is being performed.
     *               It must be a non-null, non-empty string.
     */
    void kick(String reason);

    /**
     * Executes the specified command string.
     *
     * <p>This method takes a command in the form of a string and performs the
     * necessary actions associated with it. The specific functionality of the
     * command is determined by the implementation.
     *
     * <p>Note that the command string must not be null and should adhere to
     * the expected format or syntax required by the implementation.
     *
     * @param cmd the command to be executed; must be a valid, non-null string
     */
    void performCommand(String cmd);

    /**
     * Retrieves the number of tokens available.
     * This method is marked as deprecated and should be used with caution,
     * as it may be removed in future versions or replaced with an alternative.
     *
     * <p>Consider using updated methods or APIs that provide a more robust and
     * future-proof implementation for your specific use cases.</p>
     *
     * @return the number of tokens as an integer.
     */
    @Deprecated
    int getTokens();

    /**
     * Retrieves the balance of an account or entity. This method is marked as deprecated
     * and may be removed in future versions. It is recommended to use an alternative
     * method for obtaining balance details.
     *
     * <p>Note: Usage of this method is discouraged as it may no longer be supported.</p>
     *
     * @return the current balance as an integer.
     */
    @Deprecated
    int getBalance();

    /**
     * Adds a specified amount of tokens.
     * <p>
     * This method is marked as deprecated and should not be used in new implementations.
     * Consider using an alternative method if available.
     *
     * @param amount the number of tokens to add. Must be a non-negative integer.
     */
    @Deprecated
    void addTokens(int amount);

    /**
     * Adds the specified amount to the current balance.
     * <p>
     * This method modifies the balance by adding the provided <code>amount</code>.
     * However, it is marked as deprecated and should not be used in new implementations.
     * Consider using alternative methods for modifying the balance if available.
     *
     * @param amount the amount of balance to be added. It must be a non-negative integer.
     */
    @Deprecated
    void addBalance(int amount);

    /**
     * Adds a specified number of tokens to the system for a given reason.
     * <p>
     * This method is marked as deprecated and may be removed in future releases.
     * Consider alternative methods for managing tokens if available.
     *
     * @param amount the number of tokens to be added. Must be a positive integer.
     * @param reason the reason for adding the tokens. This should provide
     *               context or justification for the addition of tokens.
     */
    @Deprecated
    void addTokens(int amount, String reason);

    /**
     * Add balance to the player
     *
     * @param amount the amount to add
     * @param reason the reason for the transaction
     */
    @Deprecated
    void addBalance(int amount, String reason);

    /**
     * <p>Adds a specified number of tokens for a given reason and executes a callback after the transaction.</p>
     *
     * <p><b>Note:</b> This method is deprecated and may be removed or replaced in future updates.</p>
     *
     * @param amount   The number of tokens to be added. Must be a positive integer.
     * @param reason   A descriptive reason for adding tokens. Cannot be null or empty.
     * @param callback The callback to execute after the transaction is processed. Cannot be null.
     */
    @Deprecated
    void addTokens(int amount, String reason, TransactionCallback callback);

    /**
     * <p>Adds the specified amount to the balance and associates it with a reason.
     * This method is deprecated and may be removed in future versions.</p>
     *
     * <p>Additionally, this method invokes the provided callback after the
     * transaction is processed.</p>
     *
     * @param amount the amount to be added to the balance. Must be a positive integer.
     * @param reason the reason or description for adding the balance. Cannot be null.
     * @param callback the callback to be executed after the transaction completes.
     *                 Cannot be null.
     */
    @Deprecated
    void addBalance(int amount, String reason, TransactionCallback callback);

    /**
     * Sets the number of tokens to the specified amount.
     * <p>
     * This method is marked as deprecated as it may be replaced with an updated implementation in the future.
     * </p>
     *
     * @deprecated This method is deprecated and may be removed or replaced in future versions.
     *
     * @param amount the number of tokens to set
     */
    @Deprecated
    void setTokens(int amount);

    /**
     * <p>
     * Sets the balance to the specified amount.
     * </p>
     *
     * <p>
     * <strong>Note:</strong> This method is deprecated and may be removed in a future release.
     * </p>
     *
     * @param amount the new balance to set
     */
    @Deprecated
    void setBalance(int amount);

    /**
     * <p>Sets the number of tokens with an associated reason. This method is marked as deprecated
     * and may be removed in future versions.</p>
     *
     * <p><strong>Note:</strong> Consider using an alternative method to manage tokens
     * as this method is deprecated.</p>
     *
     * @param amount the number of tokens to set. Must be a positive integer.
     * @param reason the reason for setting the tokens. Must be a non-null and non-empty string.
     */
    @Deprecated
    void setTokens(int amount, String reason);

    /**
     * <p>Sets the balance to the specified amount with an associated reason.</p>
     *
     * <p><b>Note:</b> This method is marked as deprecated and should not be used in new implementations.</p>
     *
     * @param amount The new balance amount to set.
     * @param reason The reason for setting the balance.
     */
    @Deprecated
    void setBalance(int amount, String reason);

    /**
     * <p>Removes a specified number of tokens. This method is deprecated and may be removed in future versions.</p>
     *
     * @param amount the number of tokens to be removed. Must be a non-negative integer.
     */
    @Deprecated
    void removeTokens(int amount);

    /**
     * <p>
     * Removes a specified amount from the balance. This method is marked as
     * deprecated and may be removed in future versions. It is recommended
     * to use an alternative method for managing balances.
     * </p>
     *
     * @param amount The amount to be removed from the balance. Must be
     *               greater than or equal to zero.
     */
    @Deprecated
    void removeBalance(int amount);

    /**
     * Removes a specified number of tokens for a given reason.
     * <p>
     * This method is marked as deprecated and should not be used in new implementations.
     * Consider using an updated replacement method if available.
     *
     * @param amount the number of tokens to be removed. It must be a positive integer.
     * @param reason the reason for removing the tokens. This should provide a valid description for auditing or logging purposes.
     */
    @Deprecated
    void removeTokens(int amount, String reason);

    /**
     * Removes the specified amount from the balance with an associated reason.
     * <p>
     * This method is marked as deprecated and may be removed in future versions.
     * </p>
     *
     * @param amount The amount to be removed from the balance. Must be a positive integer.
     * @param reason A string indicating the reason for the balance deduction.
     */
    @Deprecated
    void removeBalance(int amount, String reason);

    /**
     * <p>Removes the specified balance from the account.</p>
     * <p>This method is <strong>deprecated</strong> and should not be used in new code.</p>
     *
     * @param amount the amount of balance to be removed. Must be a non-negative integer.
     * @param reason the reason for removing the balance. Provides additional context for the operation.
     * @param callback the callback to handle the result of the balance removal operation, which may include success or failure details.
     */
    @Deprecated
    void removeBalance(int amount, String reason, TransactionCallback callback);

    /**
     * Removes a specified amount of tokens from the current account for a given reason.
     * This method is deprecated and should be used with caution, as it may no longer
     * be supported in future versions.
     *
     * <p><b>Note:</b> The removal of tokens is executed with an associated callback
     * to handle the transaction result.</p>
     *
     * @param amount the number of tokens to be removed; must be a positive integer.
     * @param reason the reason for the token removal; cannot be <code>null</code> or empty.
     * @param callback an instance of {@link TransactionCallback} that will handle the
     *        transaction completion or failure process; cannot be <code>null</code>.
     */
    @Deprecated
    void removeTokens(int amount, String reason, TransactionCallback callback);

    /**
     * Adds the specified amount of adventure coins to the user's balance.
     * <p>
     * This method updates the user's adventure coin balance by adding the provided
     * amount. The reason for the addition is logged for record-keeping purposes.
     * </p>
     *
     * @param amount The number of adventure coins to add. Must be a positive integer.
     * @param reason The description or reason for adding the adventure coins. This is used
     *               for tracking and audit purposes.
     */
    void addAdventureCoins(int amount, String reason);

    /**
     * Retrieves the current total of adventure coins for the user or character.
     * Adventure coins typically serve as a special currency in the application
     * to unlock exclusive items, features, or experiences.
     *
     * <p>Use this method to query the total number of adventure coins
     * available for the relevant entity.
     *
     * @return an integer representing the total number of adventure coins available.
     */
    int getAdventureCoins();


    /**
     * Adds a statistic entry to the system for a specific game and statistic type.
     *
     * <p>This method updates or tracks statistics such as scores, counts, or other
     * measurable attributes for a provided game and statistic category.
     * The amount represents the numeric value to be added.
     *
     * @param gameType the type of the game or category this statistic belongs to
     * @param statisticType the specific type of statistic being added
     *                      (e.g., score, win count, etc.)
     * @param amount the numeric value of the statistic to be added
     */
    void addStatistic(GameType gameType, StatisticType statisticType, int amount);

    /**
     * Retrieves a specific statistic value based on the provided game type and statistic type.
     *
     * <p>This method calculates or fetches the value of a particular statistic pertaining to a
     * given game type. The result may depend on the implementation of the game and statistic logic.</p>
     *
     * @param gameType the type of the game for which the statistic is being requested.
     *        <ul>
     *          <li>Must not be null.</li>
     *        </ul>
     * @param statisticType the type of statistic to retrieve.
     *        <ul>
     *          <li>Must not be null.</li>
     *        </ul>
     *
     * @return an integer representing the value of the requested statistic.
     *         <ul>
     *           <li>Returns 0 or a default value if no data is available for the specified inputs.</li>
     *           <li>The exact meaning of the value will depend on the {@code statisticType}.</li>
     *         </ul>
     */
    int getStatistic(GameType gameType, StatisticType statisticType);

    /**
     * Retrieves the currently open inventory view, if one exists.
     * <p>
     * This method checks if the current player or entity has an inventory view
     * open and returns it wrapped in an {@code Optional}. If no inventory
     * view is open, an empty {@code Optional} is returned.
     * </p>
     *
     * @return an {@code Optional} containing the open {@code InventoryView} if
     *         an inventory is currently open, or an empty {@code Optional} if
     *         no inventory is open.
     */
    Optional<InventoryView> getOpenInventory();

    /**
     * Retrieves the vehicle entity associated with this context, if available.
     *
     * <p>
     * This method attempts to return an {@code Optional} containing the vehicle entity.
     * If no vehicle is associated, it returns an empty {@code Optional}.
     * </p>
     *
     * @return an {@code Optional} containing the vehicle entity if present; otherwise, an empty {@code Optional}.
     */
    Optional<Entity> getVehicle();

    /**
     * Determines whether the entity is currently inside a vehicle.
     *
     * <p>This method checks the state of the entity and returns <code>true</code>
     * if the entity is inside a vehicle, otherwise returns <code>false</code>.
     *
     * @return <code>true</code> if the entity is currently in a vehicle;
     *         <code>false</code> otherwise.
     */
    boolean isInVehicle();

    /**
     * Allows a vehicle to leave a parking lot or a designated area.
     *
     * <p>This method is responsible for performing the necessary operations
     * required when a vehicle exits, such as updating capacity or status.
     *
     * @return <code>true</code> if the vehicle successfully leaves,
     * <code>false</code> otherwise.
     */
    boolean leaveVehicle();

    /**
     * Sets the specified metadata value for the given name. This can be used to associate
     * a custom key-value pair with an object for additional context or storage purposes.
     *
     * <p>Metadata values are used to add supplemental information that is not directly
     * stored within the base object structure.</p>
     *
     * @param name The key or name associated with the metadata. This serves as an identifier
     *             to allow retrieval and management of the metadata.
     * @param metadata The value associated with the provided key name. This holds the
     *                 custom data or information being stored.
     */
    void setMetadata(String name, MetadataValue metadata);

    /**
     * Retrieves the metadata values associated with the specified name.
     *
     * <p>This method returns a list of {@link MetadataValue} objects that are tied to the provided
     * name. The metadata can contain additional information or attributes relevant to the name,
     * depending on the implementation.
     *
     * @param name the name for which metadata is to be retrieved. It must not be null or empty.
     *
     * @return a {@link List} of {@link MetadataValue} objects associated with the specified name.
     *         If no metadata is found, an empty list is returned.
     */
    List<MetadataValue> getMetadata(String name);

    /**
     * Removes metadata associated with a specific name and plugin.
     *
     * <p>This method is used to delete metadata that has been previously stored or associated
     * using a given name and plugin instance. If no metadata exists for the specified name
     * and plugin, the method will perform no action.</p>
     *
     * @param name the name of the metadata to be removed. Must not be {@code null}.
     * @param plugin the plugin instance associated with the metadata. Must not be {@code null}.
     */
    void removeMetadata(String name, Plugin plugin);

    /**
     * Retrieves the join time of a user or entity.
     * <p>
     * The join time is typically represented as a timestamp in milliseconds
     * since the epoch (January 1, 1970, 00:00:00 GMT).
     * </p>
     *
     * @return the join time as a long value, representing the timestamp in milliseconds.
     */
    long getJoinTime();

    /**
     * Retrieves the online time value, which represents the total time an
     * entity or system has been online or active.
     *
     * <p>This method returns the online duration in milliseconds.
     * The value may depend on the specific implementation and may
     * represent system uptime, session duration, or another metric of activity.
     *
     * @return the total online time in milliseconds.
     */
    long getOnlineTime();

    /**
     * Sets the achievement manager for a player.
     * <p>
     * This method assigns a {@code CPlayerAchievementManager} instance to handle
     * the management of player achievements. The manager is responsible for tracking,
     * updating, and maintaining the player's achievement data.
     *
     * @param manager the {@code CPlayerAchievementManager} instance to be set as the
     *                achievement manager for the player.
     */
    void setAchievementManager(CPlayerAchievementManager manager);

    /**
     * Sets the level for this object.
     * <p>
     * This method allows the user to specify a level for the current instance.
     * The level value is typically used to define or configure the behavior
     * of the object based on its current state.
     *
     * @param level An integer representing the level to be set.
     *              <ul>
     *                <li>The value should be a valid, non-negative integer.</li>
     *                <li>Values outside of the acceptable range may result in undefined behavior.</li>
     *              </ul>
     */
    void setLevel(int level);

    /**
     * Retrieves the current level associated with an instance or process.
     *
     * <p>This method returns an integer value representing the level, which might be used
     * to denote progress, depth, or hierarchy within a certain context.</p>
     *
     * @return an integer representing the current level of the instance or process.
     */
    int getLevel();

    /**
     * Calculates and retrieves the velocity represented as a {@link Vector}.
     * <p>
     * The velocity is typically derived from an entity's movement attributes
     * and represents the speed and direction of motion.
     * </p>
     *
     * @return a {@link Vector} object that contains the components of velocity.
     *         <lu>
     *         <li>The X component represents the velocity along the X-axis.</li>
     *         <li>The Y component represents the velocity along the Y-axis.</li>
     *         <li>The Z component represents the velocity along the Z-axis (if applicable).</li>
     *         </lu>
     */
    Vector getVelocity();

    /**
     * Sets the velocity of an object.
     *
     * <p>This method updates the velocity of the object represented by this class
     * with the provided {@link Vector}. The velocity is typically used in calculations
     * involving movement, physics, or similar behaviors.
     *
     * @param vector the vector representing the new velocity to be applied
     *               <ul>
     *                 <li><strong>x:</strong> the horizontal velocity component</li>
     *                 <li><strong>y:</strong> the vertical velocity component</li>
     *                 <li><strong>z:</strong> the depth velocity component</li>
     *               </ul>
     */
    void setVelocity(Vector vector);

    /**
     * Sets the experience value for an entity or object.
     * <p>
     * This value is typically used to represent growth, skill level, or any other
     * measure of accomplishment within a system.
     *
     * @param exp the experience value to set; should be a non-negative floating-point number.
     */
    void setExp(float exp);

    /**
     * Retrieves the experience value associated with an entity or object.
     *
     * <p>
     * The returned experience value may be used for various purposes such as leveling up,
     * enhancing attributes, or other mechanics dependent on experience points.
     * </p>
     *
     * @return a floating-point value representing the experience amount.
     */
    float getExp();

    /**
     * Retrieves the honor value of the current entity.
     * <p>
     * This method returns an integer that represents the honor
     * associated with the entity. Honor could be utilized for
     * ranking, reputation, or other scoring mechanisms within the application.
     *
     * @return an integer representing the honor of the entity
     */
    int getHonor();

    /**
     * Assigns honor to an entity based on the specified amount.
     *
     * <p>This method updates the honor attribute of the target entity by
     * the specified amount. The honor system may be used to recognize
     * achievements or contributions in a given context.</p>
     *
     * @param amount the integer value of the honor to be assigned. Must be
     *               a non-negative value. If the value is negative, the
     *               method behavior is undefined.
     */
    void giveHonor(int amount);

    /**
     * Grants honor to a recipient based on the specified amount and reason.
     *
     * <p>This method is used to allocate an honor point or reward to an entity
     * by specifying the number of honor points and a description for the
     * awarding. The reason parameter helps in documenting or logging the purpose
     * of granting the honor.</p>
     *
     * @param amount the number of honor points to be granted, must be a positive integer.
     * @param reason a brief explanation for granting the honor, cannot be null or empty.
     */
    void giveHonor(int amount, String reason);

    /**
     * Grants an honor or reward to an individual or entity based on specified parameters.
     *
     * <p>
     * This method processes the awarding of honor by deducting or allocating
     * points or credits and subsequently triggers a callback to notify the outcome,
     * success, or failure of the operation.
     * </p>
     *
     * @param amount the numerical value of honor or reward to be given.
     *               Must be a positive integer.
     * @param reason a descriptive message or reason explaining why the honor is awarded.
     *               Cannot be null or empty.
     * @param callback an implementation of {@link TransactionCallback} that is
     *                 triggered once the honor allocation process is completed.
     *                 It provides the success or failure result of the operation.
     */
    void giveHonor(int amount, String reason, TransactionCallback callback);

    /**
     * Removes the specified amount of honor points from a particular entity or account.
     * <p>
     * This method should be used where honor points need to be deducted due to specific
     * actions or penalties. Ensure that the logic for evaluating whether the deduction
     * is permissible is handled before invoking this method.
     *
     * @param amount the number of honor points to be removed. Must be a positive integer.
     *               If the amount exceeds the current honor points, behavior may depend on
     *               implementation (e.g., clamp at zero or throw an error).
     */
    void removeHonor(int amount);

    /**
     * Removes a specified amount of honor from a user's current total for a given reason.
     *
     * <p>This method updates the user's honor score by deducting the provided amount.
     * The reason for the deduction should be specified to ensure transparency and tracking.
     *
     * @param amount the amount of honor to be removed; must be a positive integer.
     * @param reason a brief explanation or justification for the honor deduction;
     *               must not be null or empty.
     */
    void removeHonor(int amount, String reason);

    /**
     * Removes a specified amount of honor from a user's account for a given reason.
     * This operation is typically used in scenarios where honor needs to be deducted
     * based on user behavior or other predefined rules.
     *
     * <p>The method performs the following:
     * <ul>
     *   <li>Deducts a specified amount of honor.</li>
     *   <li>Records the reason for the deduction.</li>
     *   <li>Executes a callback after the deduction process is completed.</li>
     * </ul>
     *
     * @param amount The amount of honor to be removed. Must be a positive integer.
     * @param reason The reason for removing the honor. This will typically be used for record-keeping purposes.
     * @param callback The transaction callback to be executed after the honor removal process is complete.
     */
    void removeHonor(int amount, String reason, TransactionCallback callback);

    /**
     * Sets the honor value of an entity or player.
     * <p>
     * This method allows updating the current honor value based on the provided amount.
     * The honor typically represents a value associated with reputation, respect,
     * or any game-specific scoring logic.
     * </p>
     *
     * @param amount the new honor value to be set. This value should be an integer
     *               representing the desired honor score to be assigned.
     */
    void setHonor(int amount);

    /**
     * Sets the honor level for a character or player with a specified amount and reason.
     *
     * <p>This method updates the honor value to the provided amount and logs the reason for the change.
     * It is typically used to modify the honor status based on in-game actions or events.</p>
     *
     * @param amount the new honor value to be set. It should be a non-negative integer.
     * @param reason a brief description or explanation for the change in honor. This helps in tracking
     *               the purpose of the modification.
     */
    void setHonor(int amount, String reason);

    /**
     * Loads the specified honor value into the system or current instance context.
     *
     * <p>This method integrates the provided honor level for further processing or computations
     * within the application logic. Ensure that the input value aligns with the expected range
     * or criteria as defined by the system requirements.</p>
     *
     * @param honor The integer value representing the honor level to be loaded.
     */
    void loadHonor(int honor);

    /**
     * Retrieves the previous honor level of a user.
     * <p>
     * This method returns the honor level the user had before
     * the current level. It is useful for tracking user progress
     * or changes in their status.
     *
     * @return an integer representing the user's previous honor level.
     */
    int getPreviousHonorLevel();

    /**
     * Sets the previous honor level for a user or entity.
     *
     * <p>
     * This method updates the honor level to reflect a previously recorded
     * level. It may be used to restore or modify the honor level in a
     * specific context.
     * </p>
     *
     * @param level the integer value representing the previous honor level.
     *              <ul>
     *                <li>Must be a non-negative value.</li>
     *                <li>Values outside expected bounds may cause undefined behavior.</li>
     *              </ul>
     */
    void setPreviousHonorLevel(int level);

    /**
     * Sends the provided map view for processing or display.
     *
     * <p>This method is used to pass a {@link MapView} object to be processed or rendered.
     * The specific implementation will determine how the map view is utilized.</p>
     *
     * @param view the {@link MapView} instance that needs to be sent. Must not be null.
     */
    void sendMap(MapView view);

    /**
     * Removes the specified potion effect from an entity, if the entity currently has it.
     *
     * <p>This method allows the removal of an active potion effect applied to
     * an entity. It targets the effect type passed as an argument.
     *
     * @param type the {@link PotionEffectType} of the effect to be removed
     *             <ul>
     *                 <li>Must be a valid potion effect type.</li>
     *                 <li>If the entity does not have the specified effect, this call has no effect.</li>
     *             </ul>
     */
    void removePotionEffect(PotionEffectType type);

    /**
     * Adds a potion effect to the entity.
     * <p>
     * If the entity already has a potion effect of the same type,
     * this method will overwrite it only if the new effect has a greater
     * duration or a higher amplifier. If the new effect is the same or weaker,
     * the effect will not be applied.
     * </p>
     *
     * @param effect the {@link PotionEffect} to be added to the entity.
     *               It must not be null.
     *               <ul>
     *                 <li><b>Type:</b> Specifies the type of the potion effect (e.g., speed, strength).</li>
     *                 <li><b>Duration:</b> The duration for which the effect will last (in ticks).</li>
     *                 <li><b>Amplifier:</b> The strength level of the effect.</li>
     *               </ul>
     * @return {@code true} if the potion effect was successfully added;
     *         {@code false} if the effect was not added because the
     *         existing effect was of equal or greater strength.
     */
    boolean addPotionEffect(PotionEffect effect);

    /**
     * Adds a potion effect to an entity. This method allows applying a new potion effect
     * or updating an existing one on the entity.
     *
     * <p>If the potion effect already exists:</p>
     * <ul>
     *     <li>If <code>force</code> is <code>true</code>, the existing effect will be overridden by the new one.</li>
     *     <li>If <code>force</code> is <code>false</code>, the effect will only be updated if the new effect has a
     *     higher duration or strength.</li>
     * </ul>
     *
     * @param effect the {@link PotionEffect} to be added or updated on the entity.
     *               Must not be <code>null</code>.
     * @param force a boolean indicating whether the effect should be forcibly applied,
     *              overriding an existing effect if present.
     * @return <code>true</code> if the effect was successfully added or updated;
     *         <code>false</code> otherwise (e.g., if the effect was weaker and <code>force</code> was <code>false</code>).
     */
    boolean addPotionEffect(PotionEffect effect, boolean force);

    /**
     * Checks if the entity has a certain potion effect currently active.
     *
     * <p>This method determines whether the specified {@link PotionEffectType}
     * is currently applied to the entity. If the specified effect is active,
     * the method will return <code>true</code>, otherwise <code>false</code>.
     *
     * @param type the {@link PotionEffectType} to check for; cannot be <code>null</code>.
     *             Examples include poison, speed, or strength effects.
     * @return <code>true</code> if the entity has the specified potion effect,
     *         <code>false</code> otherwise.
     */
    boolean hasPotionEffect(PotionEffectType type);

    /**
     * Adds a collection of potion effects to the entity. If the entity already has any of
     * the specified effects, the stronger effect (determined by amplifier and duration)
     * will take precedence.
     *
     * <p>This method does not ensure that any pre-existing effects are removed. It only
     * adds the provided potion effects to the entity.</p>
     *
     * @param effects A {@link Collection} of {@link PotionEffect} objects to be applied
     * to the entity. Each {@link PotionEffect} includes information such as the potion
     * type, duration, amplifier, and additional properties.
     *
     * @return {@code true} if at least one of the provided potion effects was successfully
     * added to the entity; {@code false} otherwise.
     */
    boolean addPotionEffects(Collection<PotionEffect> effects);

    /**
     * Retrieves a collection of all the active potion effects currently applied.
     * <p>
     * Active potion effects include any effects that are influencing the
     * entity's attributes or behavior.
     *
     * @return a {@link Collection} of {@link PotionEffect} objects representing
     *         the active potion effects on the entity. The returned collection is
     *         unmodifiable.
     */
    Collection<PotionEffect> getActivePotionEffects();

    /**
     * Retrieves the {@link PotionEffect} associated with the specified {@link PotionEffectType}.
     *
     * <p>This method returns the active potion effect applied to an entity or object
     * for the given type, if present. If no effect of the specified type is active,
     * it may return null.</p>
     *
     * @param type The {@link PotionEffectType} to retrieve. This defines the type of potion
     *             effect, such as speed, strength, or regeneration.
     *             Must not be null.
     * @return The {@link PotionEffect} of the specified type, or <code>null</code>
     *         if no such effect is currently active.
     */
    PotionEffect getPotionEffect(PotionEffectType type);

    /**
     * Determines if the current entity or user is inside a vehicle.
     *
     * <p>This method checks whether the entity or user is currently occupying
     * a vehicle, such as a car, a boat, or any vehicle-like structure
     * within the context of its respective environment.</p>
     *
     * @return <code>true</code> if the entity or user is inside a vehicle;
     *         <code>false</code> otherwise.
     */
    boolean isInsideVehicle();

    /**
     * Ejects an object or entity from its current state or position.
     * The exact behavior depends on the implementing class.
     *
     * <p>This method typically performs an operation to remove or
     * expel something and returns whether the ejection was successful.
     *
     * @return <code>true</code> if the ejection was successful;
     *         <code>false</code> otherwise.
     */
    boolean eject();

    /**
     * Retrieves the unique identifier associated with the current window.
     *
     * <p>
     * This method returns an integer representing the ID of the window.
     * It can be used to distinguish between different windows within the application.
     * </p>
     *
     * @return the integer ID of the window.
     */
    int getWindowId();
}
