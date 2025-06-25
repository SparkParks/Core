package network.palace.core.npc.entity;

import lombok.Getter;
import network.palace.core.npc.AbstractEntity;
import network.palace.core.packets.AbstractPacket;
import network.palace.core.packets.server.entity.WrapperPlayServerSpawnEntity;
import network.palace.core.pathfinding.Point;
import network.palace.core.player.CPlayer;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a falling block entity within the game world.
 *
 * This entity is visually represented as a block falling due to gravity. It is defined
 * by the block type ID and associated block data. Observers can interact or render the entity,
 * making it a part of the game mechanics and user experience.
 */
public class EntityFallingBlock extends AbstractEntity {
    /**
     * Represents the block type ID of the falling block entity.
     *
     * The typeId identifies the specific type of block being portrayed as the falling
     * block within the game world. It is used to render and interact with the block entity,
     * and is consistent with the block types defined in the game's block ID system.
     * This value is immutable and determined at the creation of the entity.
     */
    @Getter private final int typeId;

    /**
     * Represents additional block data for the falling block entity.
     *
     * This data provides supplementary information about the specific state
     * of the block, such as orientation, color, or other properties specific
     * to the block type. The value is immutable and is combined with the block
     * type ID to fully define the block's appearance and behavior in the game world.
     */
    @Getter private final byte data;

    /**
     * Creates a new instance of a falling block entity in the game world.
     *
     * This constructor initializes the entity with a specific location, observers, title,
     * block type ID, and block data. The falling block entity represents a block that
     * is subject to gravity and can be visually represented and interacted with in the game.
     *
     * @param location The location where the falling block entity exists in the game world.
     * @param observers A set of players who will observe updates related to this entity.
     * @param title The name or title associated with this entity.
     * @param typeId The type ID of the block being represented by this entity.
     * @param data Additional block data describing the specific state or properties of the block.
     */
    public EntityFallingBlock(Point location, Set<CPlayer> observers, String title, int typeId, byte data) {
        super(location, observers, title);
        this.typeId = typeId;
        this.data = data;
    }

    /**
     * Retrieves the type of entity represented by this class.
     *
     * This method identifies the entity as a falling block, which is a specific
     * type of entity in the game that represents a block subjected to gravity.
     *
     * @return The {@code EntityType} corresponding to a falling block.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.FALLING_BLOCK;
    }

    /**
     * Constructs and returns the packet required to spawn the falling block entity in the game world.
     *
     * This method initializes a {@code WrapperPlayServerSpawnEntity} packet with the necessary
     * data such as entity ID, unique UUID, coordinates, and block-specific type and data.
     * The resulting packet represents the visual and functional spawning of a falling block entity in the game.
     *
     * @return The constructed spawn packet for the falling block entity, encapsulated as an {@code AbstractPacket}.
     */
    @Override
    protected AbstractPacket getSpawnPacket() {
        WrapperPlayServerSpawnEntity wrapper = new WrapperPlayServerSpawnEntity();
        wrapper.setType(WrapperPlayServerSpawnEntity.ObjectTypes.FALLING_BLOCK);
        wrapper.setEntityID(entityId);
        wrapper.setUniqueId(UUID.randomUUID());
        wrapper.setX(location.getX());
        wrapper.setY(location.getY());
        wrapper.setZ(location.getZ());
        wrapper.setObjectData(typeId | data << 12);
        return wrapper;
    }
}
