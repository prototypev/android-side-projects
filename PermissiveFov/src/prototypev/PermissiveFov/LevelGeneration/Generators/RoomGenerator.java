package prototypev.PermissiveFov.LevelGeneration.Generators;

import prototypev.PermissiveFov.LevelGeneration.DirectionType;
import prototypev.PermissiveFov.LevelGeneration.Entities.Cell;
import prototypev.PermissiveFov.LevelGeneration.Entities.Room;
import prototypev.PermissiveFov.LevelGeneration.SideType;
import prototypev.PermissiveFov.Randomizer;

import java.util.List;

public class RoomGenerator {
    /**
     * @param container The containing room.
     * @param room      The room to attempt to be placed in the containing room.
     * @param x         The horizontal component of the location to place the room.
     * @param y         The vertical component of the location to place the room.
     * @return The placement score. The score is inversely proportional to the chance to place the room.
     */
    public static int getRoomPlacementScore(Room container, Room room, int x, int y) {
        // Check if the room at the given point will fit inside the bounds of the container
        if (container.getLeft() > x ||
                container.getTop() > y ||
                container.getLeft() + container.width < x + room.width ||
                container.getTop() + container.height < y + room.height) {
            // Room does not fit inside container
            return Integer.MAX_VALUE;
        }

        int roomPlacementScore = 0;

        for (int j = 0; j < room.height; j++) {
            for (int i = 0; i < room.width; i++) {
                Cell cell = room.getCellAt(room.getLeft() + i, room.getTop() + j);

                // Translate the room's cell's location to its location in the container
                int translatedX = cell.getX() - room.getLeft() + x;
                int translatedY = cell.getY() - room.getTop() + y;

                // Add 1 point for each adjacent corridor to the cell
                for (DirectionType direction : DirectionType.values()) {
                    if (container.isAdjacentCellCorridor(translatedX, translatedY, direction)) {
                        roomPlacementScore++;
                    }
                }

                // Add 3 points if the cell overlaps an existing corridor
                if (container.getCellAt(translatedX, translatedY).isCorridor()) {
                    roomPlacementScore += 3;
                }

                // Add 100 points if the cell overlaps any existing room cells
                List<Room> existingRooms = container.getRooms();
                for (Room existingRoom : existingRooms) {
                    if (!existingRoom.isOutOfBounds(translatedX, translatedY)) {
                        roomPlacementScore += 100;
                    }
                }

            }
        }

        return roomPlacementScore;
    }

    /**
     * Creates rooms of random dimensions bounded by the input parameters in the specified containing room.
     *
     * @param container The containing room.
     * @param numRooms  The number of rooms to create.
     * @param minWidth  The minimum width of each room to create.
     * @param maxWidth  The maximum width of each room to create.
     * @param minHeight The minimum height of each room to create.
     * @param maxHeight The maximum height of each room to create.
     */
    public static void createRooms(Room container, int numRooms, int minWidth, int maxWidth, int minHeight, int maxHeight) {
        for (int roomCounter = 0; roomCounter < numRooms; roomCounter++) {
            int width = Randomizer.getInstance().nextInt(minWidth, maxWidth);
            int height = Randomizer.getInstance().nextInt(minHeight, maxHeight);
            Room room = Room.createWalledInRoom(0, 0, width, height);

            int bestScore = Integer.MAX_VALUE;
            int bestX = -1;
            int bestY = -1;

            // Ensure that rooms are always created adjacent to a corridor
            List<Cell> corridorCells = container.getCorridorCells();
            if (corridorCells.isEmpty()) {
                throw new IllegalStateException("Cannot place rooms if map has no corridors!");
            }

            for (Cell cell : corridorCells) {
                int currentRoomPlacementScore = getRoomPlacementScore(container, room, cell.getX(), cell.getY());
                if (currentRoomPlacementScore < bestScore) {
                    bestScore = currentRoomPlacementScore;
                    bestX = cell.getX();
                    bestY = cell.getY();
                }
            }

            if (bestX < 0 || bestY < 0) {
                throw new IllegalStateException("Room placement point should have been initialized!");
            }

            // Create room at best room placement cell
            container.addRoom(room, bestX, bestY);
        }
    }

    /**
     * Creates doors in the specified room.
     *
     * @param room The room.
     */
    public static void createDoors(Room room) {
        List<Room> rooms = room.getRooms();
        for (Room innerRoom : rooms) {
            int left = innerRoom.getLeft();
            int top = innerRoom.getTop();

            Iterable<Cell> cells = innerRoom.getCells();
            for (Cell cell : cells) {
                int x = cell.getX();
                int y = cell.getY();

                // Check if we are on the boundaries of our room and if there is a corridor in that direction
                if (y == top && room.isAdjacentCellCorridor(cell, DirectionType.NORTH)) {
                    room.setCellSide(cell, DirectionType.NORTH, SideType.DOOR);
                }

                if (x == left && room.isAdjacentCellCorridor(cell, DirectionType.WEST)) {
                    room.setCellSide(cell, DirectionType.WEST, SideType.DOOR);
                }

                if (y == top + innerRoom.height - 1 && room.isAdjacentCellCorridor(cell, DirectionType.SOUTH)) {
                    room.setCellSide(cell, DirectionType.SOUTH, SideType.DOOR);
                }

                if (x == left + innerRoom.width - 1 && room.isAdjacentCellCorridor(cell, DirectionType.EAST)) {
                    room.setCellSide(cell, DirectionType.EAST, SideType.DOOR);
                }

                // Discard any redundant doors that surround this cell's location
                removeRedundantDoors(room, cell);
            }
        }
    }

    /**
     * Removes redundant doors around the specified cell.
     *
     * @param room The room containing the cell.
     * @param cell The cell.
     */
    private static void removeRedundantDoors(Room room, Cell cell) {
        Cell cellNorth = room.getAdjacentCell(cell, DirectionType.NORTH);
        Cell cellWest = room.getAdjacentCell(cell, DirectionType.WEST);
        Cell cellSouth = room.getAdjacentCell(cell, DirectionType.SOUTH);
        Cell cellEast = room.getAdjacentCell(cell, DirectionType.EAST);

        // Remove redundant north side doors
        if (cell.getSide(DirectionType.NORTH) == SideType.DOOR) {
            if (cellNorth == null) {
                throw new IllegalStateException("The cell to the north cannot possibly be null if the north side of the current cell is a door!");
            }

            // If there is a cell to the west of the reference cell,
            // check if that cell has a door to the north
            if (cellWest != null && cellWest.getSide(DirectionType.NORTH) == SideType.DOOR) {
                // Check if cell to the north of the current cell is a corridor to the west
                if (room.isAdjacentCellCorridor(cellNorth, DirectionType.WEST)) {
                    // Remove redundant door in west cell
                    room.setCellSide(cellWest, DirectionType.NORTH, SideType.WALL);
                }
            }

            // If there is a cell to the east of the reference cell,
            // check if that cell has a door to the north
            if (cellEast != null && cellEast.getSide(DirectionType.NORTH) == SideType.DOOR) {
                // Check if cell to the north of the current cell is a corridor to the east
                if (room.isAdjacentCellCorridor(cellNorth, DirectionType.EAST)) {
                    // Remove redundant door in east cell
                    room.setCellSide(cellEast, DirectionType.NORTH, SideType.WALL);
                }
            }
        }

        // Remove redundant south side doors
        if (cell.getSide(DirectionType.SOUTH) == SideType.DOOR) {
            if (cellSouth == null) {
                throw new IllegalStateException("The cell to the south cannot possibly be null if the south side of the current cell is a door!");
            }

            // If there is a cell to the west of the reference cell,
            // check if that cell has a door to the south
            if (cellWest != null && cellWest.getSide(DirectionType.SOUTH) == SideType.DOOR) {
                // Check if cell to the south of the current cell is a corridor to the west
                if (room.isAdjacentCellCorridor(cellSouth, DirectionType.WEST)) {
                    // Remove redundant door in west cell
                    room.setCellSide(cellWest, DirectionType.SOUTH, SideType.WALL);
                }
            }

            // If there is a cell to the east of the reference cell, check if that
            // cell has a door to the south
            if (cellEast != null && cellEast.getSide(DirectionType.SOUTH) == SideType.DOOR) {
                // Check if cell to the south of the current cell is a corridor to the east
                if (room.isAdjacentCellCorridor(cellSouth, DirectionType.EAST)) {
                    // Remove redundant door in east cell
                    room.setCellSide(cellEast, DirectionType.SOUTH, SideType.WALL);
                }
            }
        }

        // Remove redundant west side doors
        if (cell.getSide(DirectionType.WEST) == SideType.DOOR) {
            if (cellWest == null) {
                throw new IllegalStateException("The cell to the west cannot possibly be null if the west side of the current cell is a door!");
            }

            // If there is a cell to the north of the reference cell,
            // check if that cell has a door to the west
            if (cellNorth != null && cellNorth.getSide(DirectionType.WEST) == SideType.DOOR) {
                // Check if cell to the west of the current cell is a corridor to the north
                if (room.isAdjacentCellCorridor(cellWest, DirectionType.NORTH)) {
                    // Remove redundant door in north cell
                    room.setCellSide(cellNorth, DirectionType.WEST, SideType.WALL);
                }
            }

            // If there is a cell to the south of the reference cell,
            // check if that cell has a door to the west
            if (cellSouth != null && cellSouth.getSide(DirectionType.WEST) == SideType.DOOR) {
                // Check if cell to the west of the current cell is a corridor to the south
                if (room.isAdjacentCellCorridor(cellWest, DirectionType.SOUTH)) {
                    // Remove redundant door in south cell
                    room.setCellSide(cellSouth, DirectionType.WEST, SideType.WALL);
                }
            }
        }

        // Remove redundant east side doors
        if (cell.getSide(DirectionType.EAST) == SideType.DOOR) {
            if (cellEast == null) {
                throw new IllegalStateException("The cell to the east cannot possibly be null if the east side of the current cell is a door!");
            }

            // If there is a cell to the north of the reference cell,
            // check if that cell has a door to the east
            if (cellNorth != null && cellNorth.getSide(DirectionType.EAST) == SideType.DOOR) {
                // Check if cell to the east of the current cell is a corridor to the north
                if (room.isAdjacentCellCorridor(cellEast, DirectionType.NORTH)) {
                    // Remove redundant door in north cell
                    room.setCellSide(cellNorth, DirectionType.EAST, SideType.WALL);
                }
            }

            // If there is a cell to the south of the reference cell, check if that
            // cell has a door to the east
            if (cellSouth != null && cellSouth.getSide(DirectionType.EAST) == SideType.DOOR) {
                // Check if cell to the east of the current cell is a corridor to the south
                if (room.isAdjacentCellCorridor(cellEast, DirectionType.SOUTH)) {
                    // Remove redundant door in south cell
                    room.setCellSide(cellSouth, DirectionType.EAST, SideType.WALL);
                }
            }
        }
    }
}
