package network.palace.core.pathfinding;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.material.Door;
import org.bukkit.material.Gate;

import java.util.*;

/**
 * The Pathfinder class represents a pathfinding system that calculates paths
 * from a starting point to an end point within a specified environment. It uses
 * a set of PathfindingTile objects to evaluate possible paths, implementing
 * algorithms to determine the best route based on movement rules and terrain data.
 *
 * This class supports defining start and end points, as well as determining adjacent
 * tiles and evaluating the traversability of specific blocks. It ensures paths
 * abide by movement constraints, such as walkable terrain and obstacles.
 */
public class Pathfinder {

    /**
     * A map that stores tiles used for pathfinding, where the key is a unique
     * identifier (UID) represented as a {@code Double}, and the value is a
     * {@link PathfindingTile}.
     *
     * The UID for each {@link PathfindingTile} is calculated based on its
     * {@link Point} coordinates and its parent tile, enabling unique
     * identification of each tile in the pathfinding process. This map is used
     * to manage and retrieve tiles efficiently during pathfinding operations.
     */
    @Getter private final Map<Double, PathfindingTile> tiles = new HashMap<>();

    /**
     * Represents the end position in the pathfinding process.
     * This variable indicates the target {@link Point} that the pathfinding algorithm
     * aims to reach.
     *
     * It is initialized through the constructor of the Pathfinder class and is used
     * in multiple calculations to determine the most efficient path from the start
     * position to this destination.
     *
     * The value is immutable to ensure consistency during the pathfinding process.
     */
    private Point endPos;

    /**
     * Represents the starting point tile in the pathfinding process.
     * This tile serves as the initial node from where the pathfinding algorithm begins.
     * It is an instance of {@link PathfindingTile}, which holds positional and scoring
     * information for the pathfinding logic.
     */
    private PathfindingTile start;

    /**
     * Represents the target tile for the pathfinding operation.
     * This field stores the end point of the path that is being calculated by the pathfinding algorithm.
     * It is used as a reference to determine the goal for the algorithm, calculate heuristic values,
     * and construct the final path.
     */
    private PathfindingTile end;

    /**
     * Constructs a Pathfinder instance for finding a path between two points.
     *
     * @param startPos the starting position for the pathfinding
     * @param endPos the ending position for the pathfinding
     */
    public Pathfinder(Point startPos, Point endPos) {
        this.endPos = endPos;
        this.start = tileFrom(startPos);
        this.end = tileFrom(endPos);
    }

    /**
     * Solves a pathfinding problem by computing a path from the starting point to the end point.
     * This method uses A* pathfinding, adding adjacent tiles to the open set, scoring them,
     * and ultimately constructing a path based on the lowest-cost traversal.
     *
     * @param range the maximum number of nodes to evaluate before halting the search. A value of -1 indicates
     *              no limit on the range.
     * @return a list of {@code PathfindingTile} objects representing the calculated path from the start to the end
     *         position. Returns {@code null} if no path is found or if the range is exceeded before reaching the endpoint.
     */
    public List<PathfindingTile> solvePath(int range) {
        tiles.clear();
        Set<PathfindingTile> closedSet = new LinkedHashSet<>();
        Set<PathfindingTile> openSet = new LinkedHashSet<>();
        closedSet.add(start);
        PathfindingTile current = start;
        while (!current.getPoint().equals(endPos)) {
            List<PathfindingTile> tilesAdjacent = getTilesAdjacent(current);
            tilesAdjacent.removeAll(closedSet);
            openSet.addAll(tilesAdjacent);
            if (tilesAdjacent.size() == 0 && closedSet.size() == 0) return null;
            PathfindingTile chosen = null;
            for (PathfindingTile pathTile : openSet) {
                if (chosen == null) {
                    chosen = pathTile;
                    continue;
                }
                if (pathTile.getFScore() < chosen.getFScore()) chosen = pathTile;
            }
            if (chosen == null) return null;
            current = chosen;
            openSet.remove(current);
            closedSet.add(current);
            if (range != -1 && closedSet.size() == range) break;
        }
        List<PathfindingTile> pathTiles = new ArrayList<>(closedSet);
        Collections.reverse(pathTiles);
        return pathTiles;
    }

    /**
     * Retrieves a list of tiles adjacent to the specified tile. Adjacent tiles are determined
     * based on their proximity in three dimensions and whether they are walkable according
     * to the pathfinding constraints.
     *
     * @param tile the tile for which adjacent tiles are to be retrieved
     * @return a list of {@code PathfindingTile} objects that are adjacent to the specified tile
     *         and meet the walkable criteria
     */
    private List<PathfindingTile> getTilesAdjacent(PathfindingTile tile) {
        List<PathfindingTile> pathTiles = new ArrayList<>();
        Point center = tile.getPoint();
        double centerX = center.getX(), centerY = center.getY(), centerZ = center.getZ();
        for (int x = -1; x < 1; x++) {
            for (int y = -1; y < 1; y++) {
                for (int z = -1; z < 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    Point current = Point.of(centerX + x, centerY + y, centerZ + z, endPos.getWorld());
                    if (!canWalk(current)) continue;
                    PathfindingTile pathTile = tileFrom(current, tile);
                    pathTile.updateScores(start, end);
                    pathTiles.add(pathTile);
                }
            }
        }
        return pathTiles;
    }

    /**
     * Determines whether an entity can walk to the specified point based on the block on which
     * the entity would be walking and the blocks the entity would pass through. The method checks
     * the walkability of the block at the specified point as well as the blocks directly above it.
     *
     * @param current the {@code Point} representing the current location to check for walkability
     * @return {@code true} if the specified location is walkable; {@code false} otherwise
     */
    private boolean canWalk(Point current) {
        Block walkingOn = current.getLocation(endPos.getWorld()).getBlock();
        if (!canWalkOn(walkingOn)) return false;
        Block walkingThrough1 = walkingOn.getRelative(0, 1, 0), walkingThrough2 = walkingThrough1.getRelative(0, 1, 0);
        return canWalkThrough(walkingThrough1) && canWalkThrough(walkingThrough2);
    }

    /**
     * Determines whether an entity can walk through the specified block based on
     * its type and state. Blocks such as AIR, WATER, and open doors or gates
     * are considered passable.
     *
     * @param b the {@code Block} to check for walkability
     * @return {@code true} if the block can be walked through; {@code false} otherwise
     */
    private static boolean canWalkThrough(Block b) {
        switch (b.getType()) {
            case AIR:
            case LAVA:
            case WATER:
            case PORTAL:
                return true;
            case IRON_DOOR:
            case DARK_OAK_DOOR:
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case WOODEN_DOOR:
            case SPRUCE_DOOR:
                Door door = (Door) b.getState();
                return door.isOpen();
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case FENCE_GATE:
            case SPRUCE_FENCE_GATE:
                Gate gate = (Gate) b.getState();
                return gate.isOpen();
            default:
                return false;
        }
    }

    /**
     * Determines whether an entity can walk on the specified block based on its type.
     * This method evaluates specific block types that are deemed unwalkable,
     * as well as checks if the block is passable through the {@code canWalkThrough} method.
     *
     * @param b the {@code Block} to check for walkability
     * @return {@code true} if the block can be walked on; {@code false} otherwise
     */
    private boolean canWalkOn(Block b) {
        if (canWalkThrough(b)) return false;

        switch (b.getType()) {
            case LADDER:
            case WHEAT:
            case LONG_GRASS:
            case RAILS:
            case ACTIVATOR_RAIL:
            case DETECTOR_RAIL:
            case POWERED_RAIL:
            case CAULDRON:
            case YELLOW_FLOWER:
            case FLOWER_POT:
            case RED_ROSE:
            case CAKE_BLOCK:
            case CARPET:
                return false;
            default:
                return true;
        }
    }

    /**
     * Retrieves or creates a {@code PathfindingTile} based on the given {@code Point} and parent tile.
     * If a tile with the same unique identifier already exists, it will be returned. Otherwise,
     * a new {@code PathfindingTile} will be created, stored, and then returned.
     *
     * @param point the {@code Point} representing the location of the tile
     * @param parent the parent {@code PathfindingTile} used for pathfinding traversal
     * @return the corresponding or newly created {@code PathfindingTile} for the given point and parent
     */
    private PathfindingTile tileFrom(Point point, PathfindingTile parent) {
        Double uidFor = PathfindingTile.getUid(point, parent);
        if (tiles.containsKey(uidFor)) return tiles.get(uidFor);
        PathfindingTile pathTile = new PathfindingTile(point, parent);
        tiles.put(uidFor, pathTile);
        return pathTile;
    }

    /**
     * Retrieves or creates a {@code PathfindingTile} based on the provided {@code Point}.
     * This is a simplified version of {@code tileFrom(Point, PathfindingTile)} that assumes no parent tile.
     *
     * @param point the {@code Point} representing the location of the tile
     * @return the corresponding or newly created {@code PathfindingTile} for the given point
     */
    private PathfindingTile tileFrom(Point point) {
        return tileFrom(point, null);
    }
}
