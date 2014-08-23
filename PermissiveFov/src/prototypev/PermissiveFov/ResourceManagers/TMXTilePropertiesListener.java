package prototypev.PermissiveFov.ResourceManagers;

import org.andengine.extension.tmx.*;
import prototypev.PermissiveFov.LevelGeneration.Entities.Level;
import prototypev.PermissiveFov.LevelGeneration.TileType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Event listener for TMX loader.
 */
public class TMXTilePropertiesListener implements TMXLoader.ITMXTilePropertiesListener {
    private static final String COLLIDABLE_PROPERTY_NAME = "Collidable";
    private static final String TILE_TYPE_PROPERTY_NAME = "TileType";
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
    private final Map<String, Integer> backgroundTiles = new HashMap<String, Integer>();
    private int collidableTileID;

    public Integer getBackgroundTileID(String key) {
        return backgroundTiles.get(key);
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

    public int getCollidableTileID() {
        return collidableTileID;
    }

    public int getWalkableTileID() {
        return collidableTileID + 1;
    }

    @Override
    public void onTMXTileWithPropertiesCreated(TMXTiledMap tiledMap, TMXLayer layer, TMXTile tile, TMXProperties<TMXTileProperty> properties) {
        String layerName = layer.getName();

        int tileID = tile.getGlobalTileID();
        if (layerName.equals(GameResourceManager.BACKGROUND_LAYER_NAME)) {
            if (hasTilePropertyValue(properties, TILE_TYPE_PROPERTY_NAME)) {
                String tileType = getTilePropertyValue(properties, TILE_TYPE_PROPERTY_NAME);
                backgroundTiles.put(tileType, tileID);
            }
        } else if (layerName.equals(GameResourceManager.META_LAYER_NAME)) {
            if (hasTilePropertyValue(properties, COLLIDABLE_PROPERTY_NAME)) {
                collidableTileID = tileID;
            }
        }
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
     * @param properties   The properties.
     * @param propertyName The name of the property to get.
     * @return The value of the property.
     */
    private String getTilePropertyValue(TMXProperties<TMXTileProperty> properties, String propertyName) {
        if (properties != null) {
            for (TMXTileProperty property : properties) {
                if (property.getName().equals(propertyName)) {
                    return property.getValue();
                }
            }
        }

        throw new IllegalArgumentException(String.format("No property with name: %s found!", propertyName));
    }

    /**
     * @param properties   The properties.
     * @param propertyName The name of the property to check.
     * @return true if the property exists; otherwise false.
     */
    private boolean hasTilePropertyValue(TMXProperties<TMXTileProperty> properties, String propertyName) {
        if (properties != null) {
            for (TMXTileProperty property : properties) {
                if (property.getName().equals(propertyName)) {
                    return true;
                }
            }
        }

        return false;
    }
}
