package network.palace.core.packets.server.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import network.palace.core.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * Wrapper class for the Minecraft packet Play.Server.BED.
 * This packet is sent by the server when a player enters a bed.
 */
public class WrapperPlayServerBed extends AbstractPacket {
    /**
     * Represents the packet type {@code PacketType.Play.Server.BED}.
     * This type is used to identify packets sent by the server when a player enters a bed.
     */
    public static final PacketType TYPE = PacketType.Play.Server.BED;

    /**
     * Constructs a new wrapper for the Play.Server.BED packet type.
     * This wrapper is used to handle and modify packets sent by the server
     * when a player enters a bed.
     *
     * Initializes the packet with its associated data and assigns default values
     * to the packet's modifiers.
     *
     * The packet type associated with this wrapper is {@code PacketType.Play.Server.BED}.
     *
     * Throws an IllegalArgumentException if the packet type does not match
     * the expected type or if the packet container is null.
     */
    public WrapperPlayServerBed() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Constructs a new instance of the WrapperPlayServerBed wrapper.
     *
     * This constructor initializes the wrapper for the PacketType.Play.Server.BED,
     * ensuring that the provided packet container matches the expected packet type.
     *
     * @param packet The packet container containing the necessary packet data. This
     *               must not be null and must match the PacketType.Play.Server.BED.
     * @throws IllegalArgumentException if the packet container is null or the packet
     *                                  type does not match the expected type.
     */
    public WrapperPlayServerBed(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieves the ID of the entity associated with this packet.
     *
     * @return The ID of the entity as an integer.
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Sets the ID of the entity associated with this packet.
     *
     * @param value the new entity ID to be set.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieves the entity associated with this packet in the specified world.
     *
     * @param world the world in which the entity exists.
     * @return The entity associated with this packet, or null if the entity could not be found.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieves the entity associated with this packet in the world of the player
     * involved in the specified packet event.
     *
     * @param event the packet event containing the player and world information.
     * @return The entity associated with this packet, or null if the entity could not be found.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieves the block position associated with this packet.
     *
     * @return The block position as a {@code BlockPosition} object.
     */
    public BlockPosition getLocation() {
        return handle.getBlockPositionModifier().read(0);
    }

    /**
     * Sets the block position associated with this packet.
     *
     * @param value The block position to set, represented by a {@code BlockPosition} object.
     */
    public void setLocation(BlockPosition value) {
        handle.getBlockPositionModifier().write(0, value);
    }

}