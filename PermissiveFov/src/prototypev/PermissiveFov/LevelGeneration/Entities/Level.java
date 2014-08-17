package prototypev.PermissiveFov.LevelGeneration.Entities;

import prototypev.PermissiveFov.LevelGeneration.TileType;

import java.util.Arrays;

public class Level {
    public final int width;
    public final int height;

    private final TileType[][] tileTypes;

    public Level(int width, int height) {
        this.width = width;
        this.height = height;

        tileTypes = new TileType[height][width];

        // Initialize all cells to WALL
        for (TileType[] row : tileTypes) {
            Arrays.fill(row, TileType.WALL);
        }
    }

    public TileType getTileTypeAt(int x, int y) {
        return tileTypes[y][x];
    }

    public void setTileTypeAt(int x, int y, TileType tileType) {
        tileTypes[y][x] = tileType;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                TileType tileType = getTileTypeAt(x, y);
                stringBuilder.append(tileType);
            }

            stringBuilder.append(System.getProperty("line.separator"));
        }

        return stringBuilder.toString();
    }
}
