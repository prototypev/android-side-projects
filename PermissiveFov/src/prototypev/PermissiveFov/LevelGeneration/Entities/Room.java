package prototypev.PermissiveFov.LevelGeneration.Entities;

import prototypev.PermissiveFov.LevelGeneration.DirectionType;
import prototypev.PermissiveFov.LevelGeneration.SideType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Room {
    public final int height;
    public final int width;
    private final List<Cell> cells;
    private final List<Room> rooms = new LinkedList<Room>();

    /**
     * Creates a new Room with the specified bounds.
     *
     * @param top    The top bound.
     * @param left   The left bound.
     * @param width  The number of cells spanning the room horizontally.
     * @param height The number of cells spanning the room vertically.
     */
    private Room(int top, int left, int width, int height) {
        if (top < 0 || left < 0 || width < 1 || height < 1) {
            throw new IllegalArgumentException("Invalid bounds specified. top and left must be >= 0. width and height must be > 0");
        }

        this.width = width;
        this.height = height;

        cells = new ArrayList<Cell>(height * width);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells.add(new Cell(left + x, top + y));
            }
        }
    }

    /**
     * Creates an empty room.
     *
     * @param top    The top bound.
     * @param left   The left bound.
     * @param width  The number of cells spanning the room horizontally.
     * @param height The number of cells spanning the room vertically.
     * @return The room.
     */
    public static Room createEmptyRoom(int top, int left, int width, int height) {
        Room room = createFilledRoom(top, left, width, height);

        for (Cell cell : room.cells) {
            for (DirectionType direction : DirectionType.values()) {
                cell.setSide(direction, SideType.EMPTY);
            }
        }

        return room;
    }

    /**
     * Creates a room filled with walls.
     *
     * @param top    The top bound.
     * @param left   The left bound.
     * @param width  The number of cells spanning the room horizontally.
     * @param height The number of cells spanning the room vertically.
     * @return The room.
     */
    public static Room createFilledRoom(int top, int left, int width, int height) {
        return new Room(top, left, width, height);
    }

    /**
     * Creates an empty room with walls only on the boundaries.
     *
     * @param top    The top bound.
     * @param left   The left bound.
     * @param width  The number of cells spanning the room horizontally.
     * @param height The number of cells spanning the room vertically.
     * @return The room.
     */
    public static Room createWalledInRoom(int top, int left, int width, int height) {
        Room room = createEmptyRoom(top, left, width, height);

        int right = left + width;
        int bottom = top + height;

        for (int y = top; y < bottom; y++) {
            room.setCellSide(left, y, DirectionType.WEST, SideType.WALL);
            room.setCellSide(right - 1, y, DirectionType.EAST, SideType.WALL);
        }

        for (int x = left; x < right; x++) {
            room.setCellSide(x, top, DirectionType.NORTH, SideType.WALL);
            room.setCellSide(x, bottom - 1, DirectionType.SOUTH, SideType.WALL);
        }

        return room;
    }

    /**
     * Adds a room at the specified co-ordinates.
     *
     * @param room The room to add.
     * @param x    The horizontal component.
     * @param y    The vertical component.
     */
    public void addRoom(Room room, int x, int y) {
        // Check if the room at the given point will fit inside the bounds of the container
        if (getLeft() > x ||
                getTop() > y ||
                getLeft() + width < x + room.width ||
                getTop() + height < y + room.height) {
            // Room does not fit inside container
            throw new IllegalArgumentException(String.format("Room at (%d, %d) will not fit!", x, y));
        }

        // Offset the room origin to the new location
        room.moveTo(x, y);

        for (Cell cell : room.cells) {
            int cellX = cell.getX();
            int cellY = cell.getY();

            // Set the room's cell to be the same as the container's cell
            replaceCellAt(cell, cellX, cellY);

            // Create room walls on map
            if (cellY == y && hasAdjacentCell(cell, DirectionType.NORTH)) {
                setCellSide(cell, DirectionType.NORTH, SideType.WALL);
            }

            if (cellX == x && hasAdjacentCell(cell, DirectionType.WEST)) {
                setCellSide(cell, DirectionType.WEST, SideType.WALL);
            }

            if (cellY == y + room.height - 1 && hasAdjacentCell(cell, DirectionType.SOUTH)) {
                setCellSide(cell, DirectionType.SOUTH, SideType.WALL);
            }

            if (cellX == x + room.width - 1 && hasAdjacentCell(cell, DirectionType.EAST)) {
                setCellSide(cell, DirectionType.EAST, SideType.WALL);
            }
        }

        rooms.add(room);
    }

    /**
     * @param x         The horizontal component.
     * @param y         The vertical component.
     * @param direction The direction to check.
     * @return The cell adjacent from the specified co-ordinates in the specified direction.
     * If no adjacent cells are available, returns null.
     */
    public Cell getAdjacentCell(int x, int y, DirectionType direction) {
        if (!hasAdjacentCell(x, y, direction)) {
            return null;
        }

        switch (direction) {
            case NORTH:
                return getCellAt(x, y - 1);

            case WEST:
                return getCellAt(x - 1, y);

            case SOUTH:
                return getCellAt(x, y + 1);

            case EAST:
                return getCellAt(x + 1, y);

            default:
                // This should not happen
                throw new IllegalStateException(String.format("Direction %s is invalid.", direction.getName()));
        }
    }

    /**
     * @param cell The cell to check.
     * @return The cell adjacent from the specified cell in the specified direction.
     * If no adjacent cells are available, returns null.
     */
    public Cell getAdjacentCell(Cell cell, DirectionType direction) {
        return getAdjacentCell(cell.getX(), cell.getY(), direction);
    }

    /**
     * @param x The horizontal component.
     * @param y The vertical component.
     * @return The cell at the specified co-ordinates.
     */
    public Cell getCellAt(int x, int y) {
        if (isOutOfBounds(x, y)) {
            throw new IllegalStateException(String.format("(%d, %d) is out of bounds!", x, y));
        }

        int left = getLeft();
        int top = getTop();
        int index = (y - top) * width + x - left;
        return cells.get(index);
    }

    /**
     * @return The list of all cells in this room.
     */
    public Iterable<Cell> getCells() {
        return cells;
    }

    /**
     * @return The list of cells that are corridors.
     * A corridor is defined as a cell with at least 1 side empty.
     */
    public List<Cell> getCorridorCells() {
        List<Cell> corridorCells = new ArrayList<Cell>();

        for (Cell cell : cells) {
            if (cell.isCorridor()) {
                corridorCells.add(cell);
            }
        }

        return corridorCells;
    }

    /**
     * @return The list of cells that are dead-ends.
     * Cells with 3 wall sides will be dead-end cells.
     */
    public List<Cell> getDeadEndCells() {
        List<Cell> deadEndCells = new ArrayList<Cell>();

        for (Cell cell : cells) {
            if (cell.isDeadEnd()) {
                deadEndCells.add(cell);
            }
        }

        return deadEndCells;
    }

    /**
     * @return The left bounds.
     */
    public int getLeft() {
        return cells.get(0).getX();
    }

    /**
     * @return The inner rooms that are contained within this room.
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * @return The top bounds.
     */
    public int getTop() {
        return cells.get(0).getY();
    }

    /**
     * @return The list of cells that have been visited.
     */
    public List<Cell> getVisitedCells() {
        List<Cell> visitedCells = new ArrayList<Cell>();

        for (Cell cell : cells) {
            if (cell.isVisited()) {
                visitedCells.add(cell);
            }
        }

        return visitedCells;
    }

    /**
     * @param x         The horizontal component.
     * @param y         The vertical component.
     * @param direction The direction to check.
     * @return true if there is an adjacent cell in the specified direction from the specified co-ordinates; otherwise false.
     */
    public boolean hasAdjacentCell(int x, int y, DirectionType direction) {
        if (isOutOfBounds(x, y)) {
            throw new IllegalStateException(String.format("(%d, %d) is out of bounds!", x, y));
        }

        int left = getLeft();
        int top = getTop();

        switch (direction) {
            case NORTH:
                return y > top;

            case WEST:
                return x > left;

            case SOUTH:
                return y < top + height - 1;

            case EAST:
                return x < left + width - 1;

            default:
                // This should not happen
                throw new IllegalStateException(String.format("Direction %s is invalid.", direction.getName()));
        }
    }

    /**
     * @param cell      The cell to check.
     * @param direction The direction to check.
     * @return true if there is an adjacent cell in the specified direction from the specified cell; otherwise false.
     */
    public boolean hasAdjacentCell(Cell cell, DirectionType direction) {
        return hasAdjacentCell(cell.getX(), cell.getY(), direction);
    }

    /**
     * @param x         The horizontal component.
     * @param y         The vertical component.
     * @param direction The direction to check.
     * @return true if the adjacent cell is a corridor; otherwise false.
     * A corridor is defined as a cell with at least 1 side empty.
     */
    public boolean isAdjacentCellCorridor(int x, int y, DirectionType direction) {
        if (!hasAdjacentCell(x, y, direction)) {
            // If there is no adjacent cell in the given direction, then just return false
            return false;
        }

        Cell adjacentCell = getAdjacentCell(x, y, direction);
        return adjacentCell.isCorridor();
    }

    /**
     * @param cell      The cell to check.
     * @param direction The direction to check.
     * @return true if the adjacent cell is a corridor; otherwise false.
     * A corridor is defined as a cell with at least 1 side empty.
     */
    public boolean isAdjacentCellCorridor(Cell cell, DirectionType direction) {
        return isAdjacentCellCorridor(cell.getX(), cell.getY(), direction);
    }

    /**
     * @return true if all cells in this room have been visited; otherwise false.
     */
    public boolean isAllCellsVisited() {
        for (Cell cell : cells) {
            if (!cell.isVisited()) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param x The horizontal component.
     * @param y The vertical component.
     * @return true if the specified co-ordinates is outside the bounds of this room; otherwise false.
     */
    public boolean isOutOfBounds(int x, int y) {
        int left = getLeft();
        int top = getTop();

        return x < left || y < top || y >= top + height || x >= left + width;
    }

    /**
     * Moves the room to the specified co-ordinates.
     *
     * @param x The horizontal component.
     * @param y The vertical component.
     */
    public void moveTo(int x, int y) {
        int deltaX = x - getLeft();
        int deltaY = y - getTop();

        if (deltaX != 0 || deltaY != 0) {
            for (Cell cell : cells) {
                int newX = cell.getX() + deltaX;
                cell.setX(newX);

                int newY = cell.getY() + deltaY;
                cell.setY(newY);
            }
        }
    }

    /**
     * Sets the side of the specified cell.
     *
     * @param x         The horizontal component.
     * @param y         The vertical component.
     * @param direction The direction to set.
     * @param sideType  The side type to set.
     */
    public void setCellSide(int x, int y, DirectionType direction, SideType sideType) {
        Cell cell = getCellAt(x, y);
        cell.setSide(direction, sideType);

        if (hasAdjacentCell(cell, direction)) {
            Cell adjacentCell = getAdjacentCell(cell, direction);
            adjacentCell.setSide(direction.getOpposite(), sideType);
        }
    }

    /**
     * Sets the side of the specified cell.
     *
     * @param cell      The cell.
     * @param direction The direction to set.
     * @param sideType  The side type to set.
     */
    public void setCellSide(Cell cell, DirectionType direction, SideType sideType) {
        setCellSide(cell.getX(), cell.getY(), direction, sideType);
    }

    @Override
    public String toString() {
        int totalCharsPerRow = width * 3 + 1;

        String lineSeparator = System.getProperty("line.separator");

        StringBuilder stringBuilder = new StringBuilder("Origin = (")
                .append(getLeft())
                .append(", ")
                .append(getTop())
                .append(')')
                .append(lineSeparator);

        for (int y = 0; y < height; y++) {
            StringBuilder rowStringBuilder1 = new StringBuilder(totalCharsPerRow);
            StringBuilder rowStringBuilder2 = new StringBuilder(totalCharsPerRow);
            StringBuilder rowStringBuilder3 = new StringBuilder(totalCharsPerRow);

            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                Cell cell = cells.get(index);

                SideType northSide = cell.getSide(DirectionType.NORTH);
                SideType westSide = cell.getSide(DirectionType.WEST);
                SideType southSide = cell.getSide(DirectionType.SOUTH);
                SideType eastSide = cell.getSide(DirectionType.EAST);

                rowStringBuilder1.append(northSide == SideType.EMPTY && westSide == SideType.EMPTY ? SideType.EMPTY : SideType.WALL)
                        .append(northSide)
                        .append(northSide == SideType.EMPTY && eastSide == SideType.EMPTY ? SideType.EMPTY : SideType.WALL);

                rowStringBuilder2.append(westSide)
                        .append(cell.getWallCount() == DirectionType.size ? SideType.WALL : '.')
                        .append(eastSide);

                rowStringBuilder3.append(southSide == SideType.EMPTY && westSide == SideType.EMPTY ? SideType.EMPTY : SideType.WALL)
                        .append(southSide)
                        .append(southSide == SideType.EMPTY && eastSide == SideType.EMPTY ? SideType.EMPTY : SideType.WALL);
            }

            stringBuilder.append(rowStringBuilder1).append(lineSeparator)
                    .append(rowStringBuilder2).append(lineSeparator)
                    .append(rowStringBuilder3).append(lineSeparator);
        }

        return stringBuilder.toString();
    }

    /**
     * @param cell The new cell replacing the old cell.
     * @param x    The horizontal component.
     * @param y    The vertical component.
     */
    private void replaceCellAt(Cell cell, int x, int y) {
        if (isOutOfBounds(x, y)) {
            throw new IllegalStateException(String.format("(%d, %d) is out of bounds!", x, y));
        }

        int left = getLeft();
        int top = getTop();
        int index = (y - top) * width + x - left;

        cells.set(index, cell);
    }
}
