package network.palace.core.vector;

import lombok.Getter;
import network.palace.core.Core;
import org.bukkit.Location;

/**
 * Represents a 3D vector with utility methods for creating and manipulating vector data.
 * This class wraps around Bukkit's {@link org.bukkit.util.Vector} and provides additional functionality.
 */
public class Vector {

    /**
     * The underlying Bukkit {@link org.bukkit.util.Vector} object that represents a 3D vector
     * in the Minecraft world. This field is immutable and provides access to the
     * core vector data used for calculations and operations on the vector instance.
     */
    @Getter private final org.bukkit.util.Vector vector;

    /**
     * Constructs a new Vector from the specified Location object.
     *
     * @param location the Location to construct the vector from, containing the x, y, and z coordinates.
     */
    private Vector(Location location) {
        this(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Creates a new Vector instance with the specified x, y, and z components.
     *
     * @param x the x-component of the vector
     * @param y the y-component of the vector
     * @param z the z-component of the vector
     */
    private Vector(double x, double y, double z) {
        this.vector = new org.bukkit.util.Vector(x, y, z);
    }

    /**
     * Creates a new Vector instance from the specified Location object.
     *
     * @param location the Location to construct the vector from, containing the x, y, and z coordinates
     * @return a new Vector instance representing the coordinates of the specified location
     */
    public static Vector of(Location location) {
        return new Vector(location);
    }

    /**
     * Creates a new Vector instance with the specified x, y, and z components.
     *
     * @param x the x-coordinate of the vector
     * @param y the y-coordinate of the vector
     * @param z the z-coordinate of the vector
     * @return a new Vector instance with the specified components
     */
    public static Vector of(double x, double y, double z) {
        return new Vector(x, y, z);
    }

    /**
     * Creates a new Vector instance from the specified Bukkit {@link org.bukkit.util.Vector} object.
     *
     * @param vector the Bukkit vector to use for constructing a new Vector instance, containing the x, y, and z coordinates
     * @return a new Vector instance with the same x, y, and z components as the supplied Bukkit vector
     */
    public static Vector of(org.bukkit.util.Vector vector) {
        return new Vector(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Converts the current vector to a Location instance in the default world.
     *
     * @return a Location object representing the x, y, and z coordinates of this vector in the default world
     */
    public Location getLocation() {
        return this.vector.toLocation(Core.getDefaultWorld());
    }
}
