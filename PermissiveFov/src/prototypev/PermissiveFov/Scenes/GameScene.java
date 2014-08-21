package prototypev.PermissiveFov.Scenes;

import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.ui.activity.BaseGameActivity;
import prototypev.PermissiveFov.LevelGeneration.Entities.Level;
import prototypev.PermissiveFov.LevelGeneration.Generators.LevelGenerator;
import prototypev.PermissiveFov.LevelGeneration.Generators.MazeGenerator;
import prototypev.PermissiveFov.LevelGeneration.Generators.RoomGenerator;
import prototypev.PermissiveFov.LevelGeneration.TileType;
import prototypev.PermissiveFov.ResourceManagers.GameResourceManager;

import java.util.Map;

public class GameScene extends SceneBase {
    private int spawnX;
    private int spawnY;

    public GameScene(BaseGameActivity activity, GameResourceManager resourceManager) {
        super(activity, resourceManager);

        TMXTiledMap tiledMap = resourceManager.getTiledMap();
        Level level = generateLevel(tiledMap);

        updateTiledMap(tiledMap, level, resourceManager);

        generateSpawnPoint(level);
    }

    /**
     * Generates a random level and tile it.
     *
     * @param tiledMap The tiled map.
     */
    private static Level generateLevel(TMXTiledMap tiledMap) {
        // Generate the random level
        MazeGenerator mazeGenerator = new MazeGenerator(30, 70);
        RoomGenerator roomGenerator = new RoomGenerator(2, 3, 2, 3);
        return LevelGenerator.generate(tiledMap.getTileColumns() / 2, tiledMap.getTileRows() / 2, mazeGenerator, roomGenerator, 5);
    }

    /**
     * Sets the appropriate tiles in the tiled map based on what was generated.
     *
     * @param tiledMap        The tiled map.
     * @param level           The generated level.
     * @param resourceManager The resource manager.
     */
    private static void updateTiledMap(TMXTiledMap tiledMap, Level level, GameResourceManager resourceManager) {
        TMXLayer backgroundLayer = resourceManager.getLayer("Background");
        TMXLayer metaLayer = resourceManager.getLayer("Meta");

        Map<String, Integer> backgroundTiles = resourceManager.getBackgroundTiles();
        int collidableTileID = resourceManager.getCollidableTileID();
        int walkableTileID = resourceManager.getWalkableTileID();

        for (int y = 0; y < level.height; y++) {
            for (int x = 0; x < level.width; x++) {
                TMXTile backgroundTile = backgroundLayer.getTMXTile(x, y);
                TMXTile metaTile = metaLayer.getTMXTile(x, y);

                TileType tileType = level.getTileTypeAt(x, y);
                int backgroundTileID;

                if (tileType == TileType.WALL) {
                    // Mark meta layer tile as collidable if it's a wall
                    metaTile.setGlobalTileID(tiledMap, collidableTileID);

                    backgroundTileID = resourceManager.getBackgroundTileIDForWall(level, x, y);
                } else {
                    // Non-wall, mark meta tile as walkable
                    metaTile.setGlobalTileID(tiledMap, walkableTileID);

                    backgroundTileID = backgroundTiles.get(tileType.getName());
                }

                // Set the background tile accordingly
                backgroundTile.setGlobalTileID(tiledMap, backgroundTileID);
            }
        }
    }

    /**
     * Generates the player's spawn point.
     *
     * @param level The generated level.
     */
    private void generateSpawnPoint(Level level) {
        // TODO: For now, just place the player on the first empty tile.
        for (spawnY = 0; spawnY < level.height; spawnY++) {
            for (spawnX = 0; spawnX < level.width; spawnX++) {
                if (level.getTileTypeAt(spawnX, spawnY) == TileType.EMPTY) {
                    break;
                }
            }
        }
    }
}
