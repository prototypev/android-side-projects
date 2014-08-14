package prototypev.PermissiveFov.Scenes;

import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;
import prototypev.PermissiveFov.ResourceManagers.IResourceManager;

public abstract class SceneBase extends Scene {
    protected final BaseGameActivity activity;
    protected final IResourceManager resourceManager;

    public SceneBase(BaseGameActivity activity, IResourceManager resourceManager) {
        this.activity = activity;
        this.resourceManager = resourceManager;
    }

    @Override
    public void dispose() {
        resourceManager.unload();
        detachSelf();

        super.dispose();
    }
}
