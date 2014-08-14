package prototypev.PermissiveFov.LevelGeneration.Generators;

import prototypev.PermissiveFov.LevelGeneration.DirectionType;
import prototypev.PermissiveFov.LevelGeneration.Entities.Cell;
import prototypev.PermissiveFov.LevelGeneration.Entities.Room;
import prototypev.PermissiveFov.LevelGeneration.SideType;
import prototypev.PermissiveFov.Randomizer;

import java.util.Iterator;
import java.util.List;

public class MazeGenerator {
    private final int randomness;
    private final int sparseness;

    /**
     * Creates a new MazeGenerator.
     *
     * @param randomness A value between 0 - 100 indicating the degree of randomness.
     * @param sparseness A value between 0 - 100 indicating the degree of sparseness.
     */
    public MazeGenerator(int randomness, int sparseness) {
        if (randomness < 0 || randomness > 100) {
            throw new IllegalArgumentException("randomness must be between 0 and 100!");
        }

        if (sparseness < 0 || sparseness > 100) {
            throw new IllegalArgumentException("sparseness must be between 0 and 100!");
        }

        this.randomness = randomness;
        this.sparseness = sparseness;
    }

    /**
     * @param top    The top bounds of the room.
     * @param left   The left bounds of the room.
     * @param width  The width of the room.
     * @param height The height of the room,
     * @return The generated room.
     */
    public Room generate(int top, int left, int width, int height) {
        Room room = Room.createFilledRoom(top, left, width, height);
        createDenseMaze(room);
        makeSparse(room);

        return room;
    }

    /**
     * Removes dead-ends from the specified room.
     *
     * @param room                   The containing room.
     * @param deadEndRemovalModifier A value between 0 - 100 indicating how frequent dead-ends should be removed.
     */
    public void removeDeadEnds(Room room, int deadEndRemovalModifier) {
        if (deadEndRemovalModifier < 0 || deadEndRemovalModifier > 100) {
            throw new IllegalArgumentException("deadEndRemovalModifier must be between 0 and 100!");
        }

        DirectionPicker directionPicker = new DirectionPicker(DirectionType.NORTH, 100);

        List<Cell> deadEndCells = room.getDeadEndCells();
        for (Cell cell : deadEndCells) {
            if (Randomizer.getInstance().nextInt(1, 99) < deadEndRemovalModifier) {
                // If this dead-end cell should be removed, pick a direction (excluding the direction it came from)
                // and create a corridor in that direction
                Cell currentCell = cell;
                do {
                    // Reset the direction picker not to select the dead-end corridor direction
                    DirectionType direction = currentCell.getDeadEndCorridorDirection();
                    directionPicker.reset(direction);
                    direction = directionPicker.getNextDirection();

                    while (!room.hasAdjacentCell(currentCell.x, currentCell.y, direction)) {
                        direction = directionPicker.getNextDirection();
                    }

                    // Create a corridor in the selected direction
                    currentCell = room.setCellSide(currentCell.x, currentCell.y, direction, SideType.EMPTY);
                } while (currentCell.isDeadEnd()); // Stop when intersecting an existing corridor.
            }
        }
    }

    /**
     * Creates a dense maze in the specified room.
     *
     * @param room The containing room.
     */
    private void createDenseMaze(Room room) {
        // Pick a random cell in the grid and mark it visited.
        Cell currentCell = room.getRandomCell();
        currentCell.setVisited(true);

        DirectionType previousDirection = DirectionType.NORTH;
        DirectionPicker directionPicker = new DirectionPicker(previousDirection, randomness);

        // Repeat until all cells have been visited
        while (!room.isAllCellsVisited()) {
            // From the current cell, pick a random direction
            DirectionType direction = directionPicker.getNextDirection();

            // If there is no cell adjacent to the current cell in that direction,
            // or if the adjacent cell in that direction has been visited
            while (!room.hasAdjacentCell(currentCell.x, currentCell.y, direction) ||
                    room.getAdjacentCell(currentCell.x, currentCell.y, direction).isVisited()) {
                // Then the current direction is invalid, and we must pick a different random direction.
                if (directionPicker.hasNextDirection()) {
                    direction = directionPicker.getNextDirection();
                } else {
                    // If all directions are invalid, pick a different random previously visited cell
                    currentCell = getRandomVisitedCellExcluding(room, currentCell.x, currentCell.y);

                    directionPicker.reset(previousDirection);
                    direction = directionPicker.getNextDirection();
                }
            }

            // Create a corridor from the current cell in the chosen direction,
            // and make the adjacent cell the current cell
            currentCell = room.setCellSide(currentCell.x, currentCell.y, direction, SideType.EMPTY);

            // Mark the adjacent cell as visited
            currentCell.setVisited(true);

            previousDirection = direction;
            directionPicker.reset(previousDirection);
        }
    }

    /**
     * Removes dead-end cells from a room based on the spareness level.
     *
     * @param room The containing room.
     */
    private void makeSparse(Room room) {
        // Calculate the number of cells to remove as a percentage of the total number of cells in the map
        int numDeadCellsToRemove = (int) Math.ceil(room.height * room.width * sparseness / 100f);

        Iterator<Cell> iterator = room.getDeadEndCells().iterator();
        for (int i = 0; i < numDeadCellsToRemove; i++) {
            Cell cell;
            if (iterator.hasNext()) {
                cell = iterator.next();
            } else {
                // If we have reached the end of the existing list of dead end cells, restart again
                // since we have not reached the number of dead-end cells to remove.
                iterator = room.getDeadEndCells().iterator();
                if (iterator.hasNext()) {
                    cell = iterator.next();
                } else {
                    // No more dead end cells exist so break out of loop
                    break;
                }
            }

            // If it is the last dead end in the current iteration, check to make sure that this last cell is a dead end
            if (iterator.hasNext() || cell.isDeadEnd()) {
                room.setCellSide(cell.x, cell.y, cell.getDeadEndCorridorDirection(), SideType.WALL);
            }
        }
    }

    /**
     * @param room The containing room.
     * @param x    The horizontal component of the cell to exclude.
     * @param y    The vertical component of the cell to exclude.
     * @return A random visited cell.
     */
    private Cell getRandomVisitedCellExcluding(Room room, int x, int y) {
        if (room.isOutOfBounds(x, y)) {
            throw new IllegalStateException(String.format("(%d, %d) is out of bounds!", x, y));
        }

        List<Cell> visitedCells = room.getVisitedCells();
        if (visitedCells.size() == 0) {
            throw new IllegalStateException("There are no visited cells to return.");
        }

        // Do not include starting point
        Iterator<Cell> iterator = visitedCells.iterator();
        while (iterator.hasNext()) {
            Cell cell = iterator.next();
            if (cell.x == x && cell.y == y) {
                iterator.remove();
                break;
            }
        }

        int index = Randomizer.getInstance().nextInt(visitedCells.size());
        Cell pickedCell = visitedCells.get(index);

        return pickedCell;
    }
}
