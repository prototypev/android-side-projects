package prototypev.PermissiveFov.Scenes;

import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.ui.activity.BaseGameActivity;
import prototypev.PermissiveFov.LevelGeneration.Entities.Level;
import prototypev.PermissiveFov.LevelGeneration.Generators.LevelGenerator;
import prototypev.PermissiveFov.LevelGeneration.Generators.MazeGenerator;
import prototypev.PermissiveFov.LevelGeneration.Generators.RoomGenerator;
import prototypev.PermissiveFov.ResourceManagers.GameResourceManager;

public class GameScene extends SceneBase {
    public GameScene(BaseGameActivity activity, GameResourceManager resourceManager) {
        super(activity, resourceManager);

        // Generate the random level
        MazeGenerator mazeGenerator = new MazeGenerator(30, 70);
        RoomGenerator roomGenerator = new RoomGenerator(2, 3, 2, 3);
        Level level = LevelGenerator.generate(15, 15, mazeGenerator, roomGenerator, 5);

        // Set the appropriate tiles in the tiled map based on what was generated
        TMXTiledMap tiledMap = resourceManager.getTiledMap();
    }

}
