package prototypev.PermissiveFov.ResourceManagers;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

public class SplashResourceManager implements IResourceManager {
    private static final String SPLASH_FILE = "gfx/Splash.png";

    private final BaseGameActivity activity;

    private BitmapTextureAtlas splashTextureAtlas;
    private ITextureRegion splashRegion;
    private Sprite splash;

    public SplashResourceManager(BaseGameActivity activity) {
        this.activity = activity;
    }

    @Override
    public void load() throws Exception {
        splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 480, 320, TextureOptions.DEFAULT);
        splashRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, SPLASH_FILE, 0, 0);
        splashTextureAtlas.load();

        splash = new Sprite(0, 0, splashRegion, activity.getVertexBufferObjectManager());
    }

    @Override
    public void unload() {
        splashTextureAtlas.unload();
        splashRegion = null;

        splash.detachSelf();
        splash.dispose();
    }

    public Sprite getSplash() {
        return splash;
    }
}
