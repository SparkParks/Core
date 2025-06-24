package network.palace.core.utils;

import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for performing operations related to locations and blocks in a 3D coordinate space.
 * This class provides methods to retrieve the blocks or locations that lie between two specified locations.
 */
public class LocationUtil {

    /**
     * Retrieves all blocks located between two specified locations in a three-dimensional space.
     *
     * The method calculates the blocks in the axis-aligned cuboid defined by the two given locations,
     * including both endpoints. The locations should exist in the same world for the method to work correctly.
     *
     * @param starting the starting location, defining one corner of the cuboid
     * @param ending the ending location, defining the opposite corner of the cuboid
     * @return an immutable list containing all blocks between the starting and ending locations
     */
    public static ImmutableList<Block> getBlocksBetween(Location starting, Location ending) {
        List<Block> blocks = new ArrayList<>();

        // Get the two x's to use
        int x1 = (starting.getBlockX() < ending.getBlockX() ? ending.getBlockX() : starting.getBlockX());
        int x2 = (starting.getBlockX() > ending.getBlockX() ? ending.getBlockX() : starting.getBlockX());

        // The ys
        int y1 = (starting.getBlockY() < ending.getBlockY() ? ending.getBlockY() : starting.getBlockY());
        int y2 = (starting.getBlockY() > ending.getBlockY() ? ending.getBlockY() : starting.getBlockY());

        // The zs
        int z1 = (starting.getBlockZ() < ending.getBlockZ() ? ending.getBlockZ() : starting.getBlockZ());
        int z2 = (starting.getBlockZ() > ending.getBlockZ() ? ending.getBlockZ() : starting.getBlockZ());

        for (int x = x2; x <= x1; x++) {
            for (int z = z2; z <= z1; z++) {
                for (int y = y2; y <= y1; y++) {
                    blocks.add(starting.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return ImmutableList.copyOf(blocks);
    }

    /**
     * Retrieves all locations between two specified locations in a three-dimensional space.
     *
     * This method calculates the locations corresponding to all the blocks in the axis-aligned cuboid
     * defined by the two given locations, including both endpoints. The locations must exist
     * in the same world for the method to function correctly.
     *
     * @param starting the starting location, defining one corner of the cuboid
     * @param ending the ending location, defining the opposite corner of the cuboid
     * @return an immutable list of locations between the starting and ending locations
     */
    public static ImmutableList<Location> getLocationsBetween(Location starting, Location ending) {
        return ImmutableList.copyOf((Location[]) getBlocksBetween(starting, ending).stream().map(Block::getLocation).toArray());
    }
}
