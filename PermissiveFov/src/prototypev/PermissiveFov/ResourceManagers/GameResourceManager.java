package prototypev.PermissiveFov.ResourceManagers;

import org.andengine.extension.tmx.*;
import org.andengine.ui.activity.BaseGameActivity;
import prototypev.PermissiveFov.LevelGeneration.Entities.Level;
import prototypev.PermissiveFov.LevelGeneration.TileType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameResourceManager implements IResourceManager {
    private static final String MAP_FILE = "map.tmx";
    private static final String WILDCARD_STRING = "*";
    private static final int[][] adjacentTileDeltasIncludeDiagonals = new int[][]
            {
                    {-1, -1}, {0, -1}, {1, -1},
                    {-1, 0}, {1, 0},
                    {-1, 1}, {0, 1}, {1, 1}
            };
    private static final int[][] adjacentTileDeltasNoDiagonals = new int[][]
            {
                    {0, -1},
                    {-1, 0}, {1, 0},
                    {0, 1}
            };
    private final BaseGameActivity activity;
    private Map<String, Integer> backgroundTiles;
    private int collidableTileID;
    private TMXTiledMap tiledMap;

    public GameResourceManager(BaseGameActivity activity) {
        this.activity = activity;
    }

    /**
     * @param level The generated level.
     * @param x     The horizontal component of the co-ordinate.
     * @param y     The vertical component of the co-ordinate.
     * @return The background tile ID.
     */
    public int getBackgroundTileIDForWall(Level level, int x, int y) {
        String wallKey = getWallKey(level, x, y);

        if (backgroundTiles.containsKey(wallKey)) {
            return backgroundTiles.get(wallKey);
        }

        // Attempt wildcard match
        Set<String> keys = backgroundTiles.keySet();
        for (String key : keys) {
            // Skip if key does not allow wildcards
            if (key.contains(WILDCARD_STRING)) {
                // Replace WILDCARD_STRING with \d to sniff only digits
                String regex = key.replace(WILDCARD_STRING, "\\d");

                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(wallKey);
                if (matcher.matches()) {
                    return backgroundTiles.get(key);
                }
            }
        }

        // Not a special wall
        return backgroundTiles.get(TileType.WALL.getName());
    }

    public Map<String, Integer> getBackgroundTiles() {
        return backgroundTiles;
    }

    public int getCollidableTileID() {
        return collidableTileID;
    }

    /**
     * @param layerName The layer name.
     * @return The layer, if found.
     */
    public TMXLayer getLayer(String layerName) {
        ArrayList<TMXLayer> layers = tiledMap.getTMXLayers();

        for (TMXLayer layer : layers) {
            if (layer.getName().equals(layerName)) {
                return layer;
            }
        }

        throw new IllegalArgumentException(String.format("Layer: '%s' not found!", layerName));
    }

    public TMXTiledMap getTiledMap() {
        return tiledMap;
    }

    public int getWalkableTileID() {
        // Assumption: the tile IDs are sequential, and the collidable meta tile comes first.
        return collidableTileID + 1;
    }

    @Override
    public void load() throws Exception {
        TMXLoader tmxLoader = new TMXLoader(activity.getAssets(), activity.getTextureManager(), activity.getVertexBufferObjectManager());
        tiledMap = tmxLoader.loadFromAsset(MAP_FILE);

        TMXLayer metaLayer = getLayer("Meta");
        metaLayer.setVisible(false);

        backgroundTiles = readBackgroundTiles();
        collidableTileID = readCollidableTileID();
    }

    @Override
    public void unload() {
        backgroundTiles = null;
        tiledMap = null;
    }

    /**
     * @param level The generated level.
     * @param x     The horizontal component of the co-ordinate.
     * @param y     The vertical component of the co-ordinate.
     * @return The key representing the wall type at the specified co-ordinates.
     */
    private static String getWallKey(Level level, int x, int y) {
        // Get the wall type
        String binaryString = getWallType(level, x, y);

        return String.format("%s_%s", TileType.WALL.getName(), binaryString);
    }

    /**
     * @param level The generated level.
     * @param x     The horizontal component of the co-ordinate.
     * @param y     The vertical component of the co-ordinate.
     * @return The binary string representation of the wall type at the specified co-ordinates.
     */
    private static String getWallType(Level level, int x, int y) {
        int wallType = 0xFF; // Initialize to 11111111
        for (int i = 0; i < adjacentTileDeltasIncludeDiagonals.length; i++) {
            int adjX = x + adjacentTileDeltasIncludeDiagonals[i][0];
            int adjY = y + adjacentTileDeltasIncludeDiagonals[i][1];

            // Skip if out of bounds
            if (!level.isOutOfBounds(adjX, adjY)) {
                // To get the wall type, we do an XOR on the i-th most significant bit
                TileType adjTileType = level.getTileTypeAt(adjX, adjY);
                if (adjTileType == TileType.EMPTY || adjTileType == TileType.DOOR) {
                    wallType ^= 1 << (adjacentTileDeltasIncludeDiagonals.length - 1 - i);
                }
            }
        }

        return String.format("%8s", Integer.toBinaryString(wallType)).replace(' ', '0');
    }

    /**
     * @param tileID       The tile ID.
     * @param propertyName The name of the property to get.
     * @return The value of the property.
     */
    private String getTilePropertyValue(int tileID, String propertyName) {
        TMXProperties<TMXTileProperty> properties = tiledMap.getTMXTileProperties(tileID);
        if (properties != null) {
            for (TMXTileProperty property : properties) {
                if (property.getName().equals(propertyName)) {
                    return property.getValue();
                }
            }
        }

        throw new IllegalArgumentException(String.format("No property with name: %s found for tile ID: %d!", propertyName, tileID));
    }

    /**
     * @return The background tiles.
     */
    private Map<String, Integer> readBackgroundTiles() {
        Map<String, Integer> tiles = new HashMap<String, Integer>();

        TMXLayer backgroundLayer = getLayer("Background");

        boolean isScanEnded = false;

        int rows = tiledMap.getTileRows();
        int columns = tiledMap.getTileColumns();

        // Loop through the tiled map to retrieve the tile ID associations
        for (int y = 0; !isScanEnded && y < rows; y++) {
            for (int x = 0; !isScanEnded && x < columns; x++) {
                TMXTile tile = backgroundLayer.getTMXTile(x, y);
                int tileID = tile.getGlobalTileID();

                try {
                    String tileType = getTilePropertyValue(tileID, "TileType");
                    tiles.put(tileType, tileID);
                } catch (IllegalArgumentException ex) {
                    // If tile has no properties, we are done (the rest are fillers)
                    isScanEnded = true;
                }
            }
        }

        return tiles;
    }

    /**
     * @return The collidable tile ID.
     */
    private int readCollidableTileID() {
        TMXLayer metaLayer = getLayer("Meta");

        int rows = tiledMap.getTileRows();
        int columns = tiledMap.getTileColumns();

        // Loop through the tiled map to retrieve the tile ID associations
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                TMXTile tile = metaLayer.getTMXTile(x, y);
                int tileID = tile.getGlobalTileID();

                boolean isCollidable = Boolean.parseBoolean(getTilePropertyValue(tileID, "Collidable"));
                if (isCollidable) {
                    return tile.getGlobalTileID();
                }
            }
        }

        throw new IllegalArgumentException("No collidable tile found in meta layer!");
    }
}
