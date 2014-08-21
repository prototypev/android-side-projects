package prototypev.PermissiveFov;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;
import prototypev.PermissiveFov.ResourceManagers.GameResourceManager;
import prototypev.PermissiveFov.ResourceManagers.SplashResourceManager;
import prototypev.PermissiveFov.Scenes.GameScene;
import prototypev.PermissiveFov.Scenes.SplashScene;

public class MainActivity extends BaseGameActivity {
    private static final int CAMERA_HEIGHT = 480;
    private static final int CAMERA_WIDTH = 800;
    private GameResourceManager gameResourceManager;
    private SplashResourceManager splashResourceManager;

    @Override
    public EngineOptions onCreateEngineOptions() {
        Camera camera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
//        engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
        return engineOptions;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
        splashResourceManager = new SplashResourceManager(this);
        gameResourceManager = new GameResourceManager(this);

        splashResourceManager.load();

        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
        SplashScene splashScene = new SplashScene(this, splashResourceManager);

        pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
        final BaseGameActivity activity = this;

        mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);

                Scene currentScene = mEngine.getScene();

                try {
                    gameResourceManager.load();

                    GameScene gameScene = new GameScene(activity, gameResourceManager);
                    mEngine.setScene(gameScene);


                } catch (Exception e) {
                    // TODO: Need to think of error handling strategy
                    e.printStackTrace();
                }

                currentScene.dispose();
            }
        }));

        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }
}
