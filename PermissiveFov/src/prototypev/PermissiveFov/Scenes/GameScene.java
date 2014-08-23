package prototypev.PermissiveFov.Scenes;

import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;
import prototypev.PermissiveFov.LevelGeneration.Entities.Level;
import prototypev.PermissiveFov.LevelGeneration.Generators.LevelGenerator;
import prototypev.PermissiveFov.LevelGeneration.Generators.MazeGenerator;
import prototypev.PermissiveFov.LevelGeneration.Generators.RoomGenerator;
import prototypev.PermissiveFov.LevelGeneration.TileType;
import prototypev.PermissiveFov.ResourceManagers.GameResourceManager;

public class GameScene extends SceneBase {
    private int spawnX;
    private int spawnY;

    public GameScene(BaseGameActivity activity, GameResourceManager resourceManager) {
        super(activity, resourceManager);

        TMXTiledMap tiledMap = resourceManager.getTiledMap();
        Level level = generateLevel(tiledMap);

        updateTiledMap(tiledMap, level, resourceManager);

        generateSpawnPoint(level);

        TMXLayer backgroundLayer = resourceManager.getLayer("Background");
        attachChild(backgroundLayer);
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
        final int tileHeight = tiledMap.getTileHeight();
        final int tileWidth = tiledMap.getTileWidth();

        TMXLayer backgroundLayer = resourceManager.getLayer(GameResourceManager.BACKGROUND_LAYER_NAME);
        TMXLayer metaLayer = resourceManager.getLayer(GameResourceManager.META_LAYER_NAME);

        int collidableTileID = resourceManager.getCollidableTileID();
        int walkableTileID = resourceManager.getWalkableTileID();

        for (int y = 0; y < level.height; y++) {
            for (int x = 0; x < level.width; x++) {
                TMXTile backgroundTile = backgroundLayer.getTMXTile(x, y);
                TMXTile metaTile = metaLayer.getTMXTile(x, y);

                TileType tileType = level.getTileTypeAt(x, y);

                int metaTileID;
                int backgroundTileID;

                if (tileType == TileType.WALL) {
                    // Mark meta layer tile as collidable if it's a wall
                    metaTileID = collidableTileID;

                    // Set the tile in the background layer accordingly
                    backgroundTileID = resourceManager.getBackgroundTileIDForWall(level, x, y);
                } else {
                    // Non-wall, mark meta tile as walkable
                    metaTileID = walkableTileID;

                    // Set the tile in the background layer accordingly
                    backgroundTileID = resourceManager.getBackgroundTileID(tileType.getName());
                }

                int index = y * level.width + x;

                metaTile.setGlobalTileID(tiledMap, metaTileID);
                metaLayer.setIndex(index);
                metaLayer.drawWithoutChecks(metaTile.getTextureRegion(), metaTile.getTileX(), metaTile.getTileY(), tileWidth, tileHeight, Color.WHITE_ABGR_PACKED_FLOAT);

                backgroundTile.setGlobalTileID(tiledMap, backgroundTileID);
                backgroundLayer.setIndex(index);
                backgroundLayer.drawWithoutChecks(backgroundTile.getTextureRegion(), backgroundTile.getTileX(), backgroundTile.getTileY(), tileWidth, tileHeight, Color.WHITE_ABGR_PACKED_FLOAT);
            }
        }

        metaLayer.submit();
        backgroundLayer.submit();
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
