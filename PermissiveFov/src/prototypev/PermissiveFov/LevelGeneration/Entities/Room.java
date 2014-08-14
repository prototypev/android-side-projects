package prototypev.PermissiveFov.LevelGeneration.Entities;

import prototypev.PermissiveFov.LevelGeneration.DirectionType;
import prototypev.PermissiveFov.LevelGeneration.SideType;
import prototypev.PermissiveFov.Randomizer;

import java.util.ArrayList;
import java.util.List;

public class Room {
    public final int top;
    public final int left;
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

        this.top = top;
        this.left = left;
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
        Room room = createFilledRoom(top, left, width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = room.getCellAt(x, y);

                cell.setSide(DirectionType.NORTH, y == 0 ? SideType.WALL : SideType.EMPTY);
                cell.setSide(DirectionType.WEST, x == 0 ? SideType.WALL : SideType.EMPTY);
                cell.setSide(DirectionType.SOUTH, y == height - 1 ? SideType.WALL : SideType.EMPTY);
                cell.setSide(DirectionType.EAST, x == width - 1 ? SideType.WALL : SideType.EMPTY);
            }
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

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = room.getCellAt(x, y);

                for (DirectionType direction : DirectionType.values()) {
                    cell.setSide(direction, SideType.EMPTY);
                }
            }
        }

        return room;
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
     * @return A cell at random.
     */
    public Cell getRandomCell() {
        int x = Randomizer.getInstance().nextInt(width);
        int y = Randomizer.getInstance().nextInt(height);

        return getCellAt(x, y);
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

        return cells[y][x];
    }

    /**
     * @param x The horizontal component.
     * @param y The vertical component.
     * @return true if the specified co-ordinates is outside the bounds of this room; otherwise false.
     */
    public boolean isOutOfBounds(int x, int y) {
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

        switch (direction) {
            case NORTH:
                return y > 0;

            case WEST:
                return x > 0;

            case SOUTH:
                return y < height - 1;

            case EAST:
                return x < width - 1;

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

        StringBuilder stringBuilder = new StringBuilder(height * totalCharsPerRow);

        for (int y = 0; y < height; y++) {
            StringBuilder rowStringBuilder1 = new StringBuilder(totalCharsPerRow);
            StringBuilder rowStringBuilder2 = new StringBuilder(totalCharsPerRow);
            StringBuilder rowStringBuilder3 = new StringBuilder(totalCharsPerRow);

            for (int x = 0; x < width; x++) {
                Cell cell = getCellAt(x, y);

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

            stringBuilder.append(rowStringBuilder1).append(System.getProperty("line.separator"))
                    .append(rowStringBuilder2).append(System.getProperty("line.separator"))
                    .append(rowStringBuilder3).append(System.getProperty("line.separator"));
        }

        return stringBuilder.toString();
    }
}
