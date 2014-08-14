package prototypev.PermissiveFov.LevelGeneration.Entities;

import prototypev.PermissiveFov.LevelGeneration.DirectionType;
import prototypev.PermissiveFov.LevelGeneration.SideType;

import java.util.HashMap;
import java.util.Map;

public class Cell {
    private final Map<DirectionType, SideType> sides = new HashMap<DirectionType, SideType>();
    private int x;
    private int y;
    private boolean isVisited;

    /**
     * Creates a new Cell at the specified co-ordinates.
     *
     * @param x The horizontal component.
     * @param y The vertical component.
     */
    public Cell(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Invalid co-ordinates. x and y must be >= 0");
        }

        this.x = x;
        this.y = y;

        // Initialize a cell as a solid wall (i.e. walls on all 4 sides)
        sides.put(DirectionType.NORTH, SideType.WALL);
        sides.put(DirectionType.WEST, SideType.WALL);
        sides.put(DirectionType.SOUTH, SideType.WALL);
        sides.put(DirectionType.EAST, SideType.WALL);
    }

    /**
     * Creates a new Cell at (0, 0)
     */
    public Cell() {
        this(0, 0);
    }

    /**
     * @return The horizontal component.
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the horizontal component.
     *
     * @param x The value.
     */
    void setX(int x) {
        this.x = x;
    }

    /**
     * @return The vertical component.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the vertical component.
     *
     * @param y The value.
     */
    void setY(int y) {
        this.y = y;
    }

    /**
     * @return true if a cell is a dead-end; otherwise false.
     * Cells with 3 wall sides will be dead-end cells.
     */
    public boolean isDeadEnd() {
        return getWallCount() == 3;
    }

    /**
     * If the cell is a dead-end, determine the direction where the corridor direction is.
     *
     * @return The corridor direction.
     */
    public DirectionType getDeadEndCorridorDirection() {
        if (!isDeadEnd()) {
            throw new IllegalStateException(String.format("Cannot get dead end corridor direction for non dead end cell (%d, %d)!", x, y));
        }

        for (Map.Entry<DirectionType, SideType> entry : sides.entrySet()) {
            if (entry.getValue() == SideType.EMPTY) {
                return entry.getKey();
            }
        }

        // Should not reach here
        throw new IllegalStateException("A dead end cell must have 1 side empty!");
    }

    /**
     * @return The number of walls surrounding this cell.
     */
    public int getWallCount() {
        int wallCount = 0;

        for (Map.Entry<DirectionType, SideType> entry : sides.entrySet()) {
            if (entry.getValue() == SideType.WALL) {
                wallCount++;
            }
        }

        return wallCount;
    }

    /**
     * @param direction The direction of the side to get.
     * @return The type of the side in the specified direction.
     */
    public SideType getSide(DirectionType direction) {
        return sides.get(direction);
    }

    /**
     * Sets the side type in the specified direction.
     *
     * @param direction The direction of the side to set.
     * @param sideType  The type of the side to set.
     */
    public void setSide(DirectionType direction, SideType sideType) {
        sides.put(direction, sideType);
    }

    /**
     * @return true if this cell has been visited; otherwise false.
     */
    public boolean isVisited() {
        return isVisited;
    }

    /**
     * Indicates whether this cell is visited or not.
     *
     * @param isVisited A value indicating if this cell is visited.
     */
    public void setVisited(boolean isVisited) {
        this.isVisited = isVisited;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Cell) {
            Cell cell = (Cell) o;
            return cell.x == this.x && cell.y == this.y;
        }

        return false;
    }
}
