package prototypev.PermissiveFov.LevelGeneration.Generators;

import prototypev.PermissiveFov.LevelGeneration.DirectionType;
import prototypev.PermissiveFov.LevelGeneration.Entities.Cell;
import prototypev.PermissiveFov.LevelGeneration.Entities.Level;
import prototypev.PermissiveFov.LevelGeneration.Entities.Room;
import prototypev.PermissiveFov.LevelGeneration.SideType;
import prototypev.PermissiveFov.LevelGeneration.TileType;

import java.util.List;

public class LevelGenerator {
    public static Level generate(
            int width,
            int height,
            MazeGenerator mazeGenerator,
            RoomGenerator roomGenerator,
            int numRooms) {

        Room map = mazeGenerator.generate(0, 0, width, height);
        System.out.println("Generated maze:");
        System.out.println(map);

        roomGenerator.createRooms(map, numRooms);
        System.out.println("After rooms are placed:");
        System.out.println(map);

        RoomGenerator.createDoors(map);
        System.out.println("After doors after placed:");
        System.out.println(map);

        Level level = expandToTiles(map);
        System.out.println("After expanding to tiles:");
        System.out.println(level);

        return level;
    }

    private static Level expandToTiles(Room room) {
        // The size of the expanded room is 1+ twice the original size.
        // The reason for this is that there will always be a ring of rock around the
        // map, and a tile for each of the sides are shared between the map cells.
        int expandedWidth = room.width * 2 + 1;
        int expandedHeight = room.height * 2 + 1;

        Level level = new Level(expandedWidth, expandedHeight);

        // Fill tiles with corridor values for each inner room
        List<Room> rooms = room.getRooms();
        for (Room innerRoom : rooms) {
            int roomX = innerRoom.getLeft();
            int roomY = innerRoom.getTop();

            // Get the room min and max location in tile coordinates
            int minX = roomX * 2 + 1;
            int minY = roomY * 2 + 1;

            int maxX = (roomX + innerRoom.width) * 2;
            int maxY = (roomY + innerRoom.height) * 2;

            // Fill the room in tile space with an empty value
            for (int y = minY; y < maxY; y++) {
                for (int x = minX; x < maxX; x++) {
                    level.setTileTypeAt(x, y, TileType.EMPTY);
                }
            }
        }

        // Loop for each corridor cell and expand it
        List<Cell> corridorCells = room.getCorridorCells();
        for (Cell cell : corridorCells) {
            int tileX = cell.getX() * 2 + 1;
            int tileY = cell.getY() * 2 + 1;

            level.setTileTypeAt(tileX, tileY, TileType.EMPTY);

            // At every location, check each side to determine if it's empty or a door
            // and fill the adjacent tile with appropriate TileType.
            SideType northSide = cell.getSide(DirectionType.NORTH);
            if (northSide == SideType.EMPTY || northSide == SideType.DOOR) {
                level.setTileTypeAt(tileX, tileY - 1, northSide.toTileType());
            }

            SideType westSide = cell.getSide(DirectionType.WEST);
            if (westSide == SideType.EMPTY || westSide == SideType.DOOR) {
                level.setTileTypeAt(tileX - 1, tileY, westSide.toTileType());
            }

            SideType southSide = cell.getSide(DirectionType.SOUTH);
            if (southSide == SideType.EMPTY || southSide == SideType.DOOR) {
                level.setTileTypeAt(tileX, tileY + 1, southSide.toTileType());
            }

            SideType eastSide = cell.getSide(DirectionType.EAST);
            if (eastSide == SideType.EMPTY || eastSide == SideType.DOOR) {
                level.setTileTypeAt(tileX + 1, tileY, eastSide.toTileType());
            }
        }

        return level;
    }
}
