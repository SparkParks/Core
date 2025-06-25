/*
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package network.palace.core.packets.server.entity;

import network.palace.core.packets.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

/**
 * Represents a packet wrapper for the server-side "animation" packet.
 * This packet is used to play animations for entities in the game.
 */
public class WrapperPlayServerAnimation extends AbstractPacket {

    /**
     * Represents the packet type for the server-side "animation" packet.
     * This constant is used to identify the specific type of the animation packet
     * within the ProtocolLib packet handling framework.
     */
    public static final PacketType TYPE = PacketType.Play.Server.ANIMATION;

    /**
     * Constructs a new instance of the WrapperPlayServerAnimation.
     * This class represents a server-side "animation" packet wrapper used in the ProtocolLib framework,
     * enabling interactions with entity animations.
     */
    public WrapperPlayServerAnimation() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    /**
     * Retrieve Entity ID.
     *
     * Notes: entity's ID.
     *
     * @return The current Entity ID.
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set the Entity ID.
     *
     * Updates the entity ID associated with the packet. This is typically used to specify
     * or modify which entity the packet is referring to.
     *
     * @param value The new entity ID to set.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity referenced by this packet for the provided world.
     *
     * @param world The world in which the entity resides.
     * @return The entity associated with this packet in the specified world.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity referenced by this packet for the world associated with the given packet event.
     *
     * @param event The packet event containing the player and the world in which the entity resides.
     * @return The entity associated with this packet in the world referenced by the event.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieve Animation.
     *
     * Extracts the animation ID from the packet, which represents
     * the specific animation played for an entity.
     *
     * @return The ID of the animation.
     */
    public int getAnimation() {
        return handle.getIntegers().read(1);
    }

    /**
     * Sets the animation ID associated with the animation packet.
     * The animation ID determines which specific animation is played for an entity.
     *
     * @param value The ID of the animation to set.
     */
    public void setAnimation(int value) {
        handle.getIntegers().write(1, value);
    }
}
