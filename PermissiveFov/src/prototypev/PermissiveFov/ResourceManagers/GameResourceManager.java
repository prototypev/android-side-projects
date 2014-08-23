package prototypev.PermissiveFov.ResourceManagers;

import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.ui.activity.BaseGameActivity;
import prototypev.PermissiveFov.LevelGeneration.Entities.Level;

import java.util.ArrayList;

public class GameResourceManager implements IResourceManager {
    public static final String BACKGROUND_LAYER_NAME = "Background";
    public static final String META_LAYER_NAME = "Meta";
    private static final String MAP_FILE = "map.tmx";
    private final BaseGameActivity activity;
    private TMXTilePropertiesListener tilePropertiesListener;
    private TMXTiledMap tiledMap;

    public GameResourceManager(BaseGameActivity activity) {
        this.activity = activity;
    }

    public int getBackgroundTileID(String key) {
        return tilePropertiesListener.getBackgroundTileID(key);
    }

    /**
     * @param level The generated level.
     * @param x     The horizontal component of the co-ordinate.
     * @param y     The vertical component of the co-ordinate.
     * @return The background tile ID.
     */
    public int getBackgroundTileIDForWall(Level level, int x, int y) {
        return tilePropertiesListener.getBackgroundTileIDForWall(level, x, y);
    }

    public int getCollidableTileID() {
        return tilePropertiesListener.getCollidableTileID();
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
        return tilePropertiesListener.getWalkableTileID();
    }

    @Override
    public void load() throws Exception {
        tilePropertiesListener = new TMXTilePropertiesListener();
        TMXLoader tmxLoader = new TMXLoader(activity.getAssets(), activity.getTextureManager(), activity.getVertexBufferObjectManager(), tilePropertiesListener);

        tiledMap = tmxLoader.loadFromAsset(MAP_FILE);

        TMXLayer metaLayer = getLayer(META_LAYER_NAME);
        metaLayer.setVisible(false);
    }

    @Override
    public void unload() {
        tilePropertiesListener = null;
        tiledMap = null;
    }


}
