package network.palace.core.pathfinding;

import lombok.Getter;

/**
 * The PathfindingTile class represents a tile in a grid or map used for pathfinding algorithms.
 * Each tile contains its coordinates represented by a {@code Point}, reference to a parent tile,
 * and scores used in pathfinding calculations such as the F, G, and H scores.
 */
public class PathfindingTile {

    /**
     * Represents the movement cost for straight (non-diagonal) movement
     * between adjacent tiles in a pathfinding algorithm.
     * This constant is used to calculate the G-score for straight movements
     * in the grid or map. The value adheres to typical conventions,
     * where the cost of straight movement is less than diagonal movement.
     */
    private final int STRAIGHT_SCORE = 10;

    /**
     * Represents the movement cost for diagonal movement between adjacent tiles
     * in a pathfinding algorithm. This constant is used to calculate the G-score
     * for diagonal movements in the grid or map. Its value commonly reflects
     * the higher cost of diagonal movement compared to straight movement in most
     * pathfinding calculations.
     */
    private final int DIAGONAL_SCORE = 14;

    /**
     * Represents the coordinates of the tile in a three-dimensional space.
     * The {@code Point} object defines the position of the tile using
     * x, y, and z values, as well as orientation with yaw and pitch angles.
     * This information is used to perform spatial operations and calculations
     * within the context of pathfinding algorithms.
     */
    @Getter private Point point;

    /**
     * Represents the parent tile of the current {@code PathfindingTile}.
     * This reference is used to trace the path back from the current tile
     * to the starting point in pathfinding algorithms, enabling reconstruction
     * of the optimal path.
     */
    @Getter private PathfindingTile parent;

    /**
     * Represents the f-score (or estimated total cost) of a tile in a pathfinding algorithm.
     * The f-score is typically calculated as the sum of the g-score (cost from the start node)
     * and the h-score (heuristic estimated cost to the end node).
     *
     * This value is used to prioritize exploration of tiles during pathfinding.
     */
    @Getter private int fScore;

    /**
     * Represents the cost of the cheapest path from the starting point to this tile during pathfinding.
     * Used in algorithms like A* to keep track of the accumulated movement cost.
     */
    @Getter private int gScore;

    /**
     * Represents the heuristic score (hScore) of a tile, which is used in pathfinding algorithms
     * such as A* to estimate the cost to reach the target destination from this tile.
     * The hScore is typically calculated based on the distance or cost between the current tile
     * and the destination tile, providing a heuristic estimation of the remaining traversal cost.
     */
    @Getter private int hScore;

    /**
     * Indicates whether the movement to this tile is ordinal (diagonal).
     * Ordinal movement typically involves moving to adjacent tiles
     * that are diagonally situated, as opposed to straight horizontal
     * or vertical movement.
     */
    private boolean ordinalMovement;

    /**
     * Constructs a new PathfindingTile with the specified point and parent tile.
     *
     * @param point the point representing the location of this tile
     * @param tile the parent tile of this tile in the pathfinding process
     */
    public PathfindingTile(Point point, PathfindingTile tile) {
        this.point = point;
        this.parent = tile;
    }

    /**
     * Updates the scores (gScore, hScore, and fScore) for the current PathfindingTile
     * based on the given starting and ending tiles in the pathfinding process.
     *
     * @param start the starting tile of the pathfinding process
     * @param end the destination tile in the pathfinding process
     */
    public void updateScores(PathfindingTile start, PathfindingTile end) {
        PathfindingTile current = getParent();
        int combinedScore = 0;

        while(!current.equals(start)) {
            combinedScore += current.getGScore();
            current = current.getParent();
        }
        ordinalMovement = isOrdinalMovement(parent, this);
        if (ordinalMovement) {
            gScore = combinedScore + STRAIGHT_SCORE;
        } else {
            gScore = combinedScore + DIAGONAL_SCORE;
        }

        hScore = ((int) Math.ceil(end.getPoint().distanceSquared(point)));
        fScore = hScore + gScore;
    }

    /**
     * Determines whether the movement from the starting tile to the destination tile
     * is an ordinal movement. Ordinal movement occurs when the distance between the
     * tiles is less than the square root of 2, indicating adjacent tiles.
     *
     * @param start the starting tile in the pathfinding process
     * @param destination the destination tile in the pathfinding process
     * @return true if the movement is ordinal; false otherwise
     */
    private boolean isOrdinalMovement(PathfindingTile start, PathfindingTile destination) {
        Point startPoint = start.getPoint();
        Point endPoint = destination.getPoint();

        return startPoint.distanceSquared(endPoint) < 2;
    }

    /**
     * Recursively generates a unique identifier (UID) based on the coordinates
     * of the given point and its parent PathfindingTile, if provided.
     *
     * @param point the point representing the current tile's location in space
     * @param parent the parent PathfindingTile in the pathfinding hierarchy; can be null
     * @return a double representing the generated unique identifier for the tile
     */
    public static double getUid(Point point, PathfindingTile parent) {
        return (point.getX() + point.getY() + point.getZ()) * (parent == null ? 1 : getUid(parent.getPoint(), parent.getParent()));
    }
}
