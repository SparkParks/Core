package network.palace.core.pathfinding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.player.CPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

/**
 * The Point class represents a three-dimensional point in space with additional properties
 * such as yaw and pitch for direction, and a World object for context. It provides methods
 * to manipulate the point's coordinates, calculate distances, and create or clone Point objects.
 *
 * This class is intended for use in environments where spatial representation is necessary,
 * including working with locations in gaming, simulations, and related applications.
 */
@AllArgsConstructor
public class Point implements Cloneable {

    /**
     * Represents the x-coordinate of the point in a three-dimensional space.
     * This value is typically used to determine the horizontal position
     * of the point along the X-axis. The x value is stored as a double-precision
     * floating-point number to allow for high precision in calculations.
     */
    @Getter private double x;

    /**
     * Represents the y-coordinate of the point in a 3D space.
     * This value specifies the vertical position of the point.
     */
    @Getter private double y;

    /**
     * Represents the Z-coordinate of the point in a 3D space.
     * This field denotes the vertical depth or elevation component in the coordinate system.
     * The value is mutable through provided methods in the class.
     */
    @Getter private double z;

    /**
     * Represents the yaw component of the point's orientation in degrees.
     * Yaw is the rotation around the vertical axis, where 0 degrees typically
     * indicates facing north. Positive values represent clockwise rotation.
     */
    @Getter private float yaw;

    /**
     * Represents the pitch (vertical rotation) of an element, such as an entity or object, in degrees.
     * The pitch defines the up or down orientation, with a value of 0 indicating a level orientation.
     * Positive values represent looking downward, while negative values represent looking upward.
     */
    @Getter private float pitch;

    /**
     * Represents the world associated with a specific point.
     * This variable is used to indicate the world context in which
     * the point resides.
     */
    @Getter private World world;

    /**
     * Determines whether the current Point instance represents a block coordinate.
     * A Point is considered a block if its pitch and yaw are both zero, and its x, y, and z coordinates
     * are whole numbers (i.e., integers).
     *
     * @return true if the Point represents a block coordinate; otherwise, false.
     */
    public boolean isBlock() {
        return (pitch == 0.0F && yaw == 0.0F && y % 1 == 0 && z % 1 == 0 && x % 1 == 0);
    }

    /**
     * Retrieves the location represented by this Point instance.
     *
     * @return a Location object consisting of the world, x, y, z coordinates, yaw, and pitch values.
     */
    public Location getLocation() {
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Retrieves the location represented by this Point instance in the specified world.
     *
     * @param world the World object representing the world in which the location is situated
     * @return a Location object consisting of the provided world, and the x, y, z coordinates, yaw, and pitch values from this Point instance
     */
    public Location getLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Retrieves the location represented by this Point instance in the specified world.
     *
     * @param world the World object representing the world in which the location is situated
     * @return a Location object consisting of the provided world and the x, y, z coordinates, yaw, and pitch values from this Point instance
     */
    public Location in(World world) {
        return getLocation(world);
    }

    /**
     * Creates a new Point instance representing the location of the given player.
     * The Point is constructed based on the player's current location.
     *
     * @param player the player whose current location is to be converted into a Point
     * @return a Point instance representing the given player's location
     */
    public static Point of(CPlayer player) {
        return of(player.getLocation());
    }

    /**
     * Creates a new {@code Point} instance representing the location of the given entity.
     * The {@code Point} is constructed based on the entity's current location.
     *
     * @param entity the entity whose current location is to be converted into a {@code Point}
     * @return a {@code Point} instance representing the given entity's location
     */
    public static Point of(Entity entity) {
        return of(entity.getLocation());
    }

    /**
     * Creates a new {@code Point} instance with the specified coordinates and world.
     *
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param z the z-coordinate of the point
     * @param world the world in which the point is located
     * @return a new {@code Point} instance with the specified coordinates and world
     */
    public static Point of(double x, double y, double z, World world) {
        return new Point(x, y, z, 0F, 0F, world);
    }

    /**
     * Creates a new {@code Point} instance based on the given {@code Location}.
     * The {@code Point} is constructed using the x, y, z coordinates,
     * yaw, pitch, and world values from the {@code Location}.
     *
     * @param location the {@code Location} instance containing the
     *                 coordinates, yaw, pitch, and world information
     * @return a new {@code Point} instance representing the provided {@code Location}
     */
    public static Point of(Location location) {
        return new Point(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), location.getWorld());
    }

    /**
     * Creates a new {@code Point} instance representing the location of the given {@code Block}.
     * The {@code Point} is constructed using the coordinates, yaw, pitch, and world information
     * obtained from the {@code Block}'s location.
     *
     * @param block the {@code Block} whose location is to be converted into a {@code Point}
     * @return a {@code Point} instance representing the provided {@code Block}'s location
     */
    public static Point of(Block block) {
        return of(block.getLocation());
    }

    /**
     * Calculates the squared distance between the current Point instance and the specified Point.
     * This method avoids the computational overhead of calculating a square root, making it useful for comparisons where
     * the exact Euclidean distance is not required.
     *
     * @param point the Point to which the squared distance is calculated
     * @return the squared distance as a double between this Point and the given Point
     */
    public double distanceSquared(Point point) {
        double x = Math.pow((this.x - point.getX()), 2);
        double y = Math.pow((this.y - point.getY()), 2);
        double z = Math.pow((this.z - point.getZ()), 2);
        return x + y + z;
    }

    /**
     * Calculates the Euclidean distance between the current Point instance and the specified Point.
     * This method computes the square root of the squared distance for the exact distance between two points.
     *
     * @param point the Point to which the distance is calculated
     * @return the Euclidean distance as a double between this Point and the given Point
     */
    public double distance(Point point) {
        return Math.sqrt(distanceSquared(point));
    }

    /**
     * Creates and returns a deep copy of the current Point instance.
     * The new Point will have identical values for x, y, z, yaw, pitch, and world
     * but will be a completely independent object.
     *
     * @return a new Point instance that is a deep copy of this instance
     */
    public Point deepCopy() {
        return new Point(x, y, z, yaw, pitch, world);
    }

    /**
     * Adds the specified x, y, and z values to the current Point's coordinates.
     *
     * @param x the value to be added to the x-coordinate of this Point
     * @param y the value to be added to the y-coordinate of this Point
     * @param z the value to be added to the z-coordinate of this Point
     * @return the updated Point instance after adding the specified values
     */
    public Point add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    /**
     * Subtracts the specified x, y, and z values from the current Point's coordinates.
     *
     * @param x the value to be subtracted from the x-coordinate of this Point
     * @param y the value to be subtracted from the y-coordinate of this Point
     * @param z the value to be subtracted from the z-coordinate of this Point
     * @return the updated Point instance after subtracting the specified values
     */
    public Point subtract(double x, double y, double z) {
        return add(-1 * x, -1 * y, -1 * z);
    }

    /**
     * Multiplies the current Point's x, y, and z coordinates by the specified values.
     *
     * @param x the value to multiply with the x-coordinate of this Point
     * @param y the value to multiply with the y-coordinate of this Point
     * @param z the value to multiply with the z-coordinate of this Point
     * @return the updated Point instance after multiplying the specified values
     */
    public Point multiply(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    /**
     * Adds the coordinates of the specified Point instance to the current Point's coordinates.
     *
     * @param point the Point whose x, y, and z values are to be added to this Point
     * @return the updated Point instance after adding the specified Point's coordinates
     */
    public Point add(Point point) {
        return add(point.getX(), point.getY(), point.getZ());
    }

    /**
     * Subtracts the coordinates of the specified Point instance from the current Point's coordinates.
     *
     * @param point the Point whose x, y, and z values are to be subtracted from this Point
     * @return the updated Point instance after subtracting the specified Point's coordinates
     */
    public Point subtract(Point point) {
        return subtract(point.getX(), point.getY(), point.getZ());
    }

    /**
     * Sets the x-coordinate of this Point to the specified value.
     *
     * @param x the new x-coordinate value to be set
     * @return the updated Point instance with the modified x-coordinate
     */
    public Point setX(double x) {
        this.x = x;
        return this;
    }

    /**
     * Sets the y-coordinate of this Point to the specified value.
     *
     * @param y the new y-coordinate value to be set
     * @return the updated Point instance with the modified y-coordinate
     */
    public Point setY(double y) {
        this.y = y;
        return this;
    }

    /**
     * Sets the z-coordinate of this Point to the specified value.
     *
     * @param z the new z-coordinate value to be set
     * @return the updated Point instance with the modified z-coordinate
     */
    public Point setZ(double z) {
        this.z = z;
        return this;
    }

    /**
     * Sets the yaw value for this Point instance to the specified yaw value.
     *
     * @param yaw the new yaw value to be set
     * @return the updated Point instance with the modified yaw value
     */
    public Point setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    /**
     * Sets the pitch value for this Point instance to the specified value.
     *
     * @param pitch the new pitch value to be set
     * @return the updated Point instance with the modified pitch value
     */
    public Point setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    /**
     * Returns a string representation of the Point object. The returned string includes
     * the values of the x, y, z coordinates, pitch, and yaw in a structured format.
     *
     * @return a string representation of this Point object with its properties
     */
    @Override
    public String toString() {
        return "Point [" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", pitch=" + pitch +
                ", yaw=" + yaw +
                "]";
    }
}
