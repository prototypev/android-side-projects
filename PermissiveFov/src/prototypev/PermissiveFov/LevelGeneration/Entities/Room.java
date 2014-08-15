package prototypev.PermissiveFov.LevelGeneration.Entities;

import prototypev.PermissiveFov.LevelGeneration.DirectionType;
import prototypev.PermissiveFov.LevelGeneration.SideType;

import java.util.ArrayList;
import java.util.List;

public class Room {
    public final int width;
    public final int height;

    private final Cell[][] cells;

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

        cells = new Cell[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell(left + x, top + y);
            }
        }
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

        for (int y = 0; y < height; y++) {
            room.cells[y][0].setSide(DirectionType.WEST, SideType.WALL);
            room.cells[y][width - 1].setSide(DirectionType.EAST, SideType.WALL);
        }

        for (int x = 0; x < width; x++) {
            room.cells[0][x].setSide(DirectionType.NORTH, SideType.WALL);
            room.cells[height - 1][x].setSide(DirectionType.SOUTH, SideType.WALL);
        }

        return room;
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

        for (Cell[] row : room.cells) {
            for (Cell cell : row) {
                for (DirectionType direction : DirectionType.values()) {
                    cell.setSide(direction, SideType.EMPTY);
                }
            }
        }

        return room;
    }

    /**
     * @return The top bounds.
     */
    public int getTop() {
        return cells[0][0].getY();
    }

    /**
     * @return The left bounds.
     */
    public int getLeft() {
        return cells[0][0].getX();
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
            for (Cell[] row : cells) {
                for (Cell cell : row) {
                    int newX = cell.getX() + deltaX;
                    cell.setX(newX);

                    int newY = cell.getY() + deltaY;
                    cell.setY(newY);
                }
            }
        }
    }

    /**
     * @return The list of cells that have been visited.
     */
    public List<Cell> getVisitedCells() {
        List<Cell> visitedCells = new ArrayList<Cell>();

        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.isVisited()) {
                    visitedCells.add(cell);
                }
            }
        }

        return visitedCells;
    }

    /**
     * @return true if all cells in this room have been visited; otherwise false.
     */
    public boolean isAllCellsVisited() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (!cell.isVisited()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @return The list of cells that are dead-ends.
     * Cells with 3 wall sides will be dead-end cells.
     */
    public List<Cell> getDeadEndCells() {
        List<Cell> deadEndCells = new ArrayList<Cell>();

        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.isDeadEnd()) {
                    deadEndCells.add(cell);
                }
            }
        }

        return deadEndCells;
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

        return cells[y - top][x - left];
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
     * Sets the side of the specified cell.
     *
     * @param x         The horizontal component.
     * @param y         The vertical component.
     * @param direction The direction to set.
     * @param sideType  The side type to set.
     * @return The adjacent cell in the specified direction.
     */
    public Cell setCellSide(int x, int y, DirectionType direction, SideType sideType) {
        Cell cell = getCellAt(x, y);
        cell.setSide(direction, sideType);

        Cell adjacentCell = getAdjacentCell(x, y, direction);
        adjacentCell.setSide(direction.getOpposite(), sideType);

        return adjacentCell;
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

        for (Cell[] row : cells) {
            StringBuilder rowStringBuilder1 = new StringBuilder(totalCharsPerRow);
            StringBuilder rowStringBuilder2 = new StringBuilder(totalCharsPerRow);
            StringBuilder rowStringBuilder3 = new StringBuilder(totalCharsPerRow);

            for (Cell cell : row) {
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
}
