package prototypev.PermissiveFov.ResourceManagers;

import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.ui.activity.BaseGameActivity;

public class GameResourceManager implements IResourceManager {
    private static final String MAP_FILE = "map.tmx";

    private final BaseGameActivity activity;
    private TMXTiledMap tiledMap;

    public GameResourceManager(BaseGameActivity activity) {
        this.activity = activity;
    }

    @Override
    public void load() throws Exception {
        TMXLoader tmxLoader = new TMXLoader(activity.getAssets(), activity.getTextureManager(), activity.getVertexBufferObjectManager());
        tiledMap = tmxLoader.loadFromAsset(MAP_FILE);
    }

    @Override
    public void unload() {
        // TODO
    }

    public TMXTiledMap getTiledMap() {
        return tiledMap;
    }
}
