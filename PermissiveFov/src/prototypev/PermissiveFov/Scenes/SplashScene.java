package prototypev.PermissiveFov.Scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.ui.activity.BaseGameActivity;
import prototypev.PermissiveFov.ResourceManagers.SplashResourceManager;

public class SplashScene extends SceneBase {
    public SplashScene(BaseGameActivity activity, SplashResourceManager resourceManager) {
        super(activity, resourceManager);

        Camera camera = activity.getEngine().getCamera();

        Sprite splash = resourceManager.getSplash();
        splash.setPosition((camera.getWidth() - splash.getWidth()) * 0.5f, (camera.getHeight() - splash.getHeight()) * 0.5f);

        attachChild(splash);
    }
}
