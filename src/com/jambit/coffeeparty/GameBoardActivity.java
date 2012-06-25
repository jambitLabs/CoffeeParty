package com.jambit.coffeeparty;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class GameBoardActivity extends BaseGameActivity {

    private BitmapTextureAtlas mBitmapTextureAtlas;
    private TextureRegion mFaceTextureRegion;

    @Override
    public Engine onLoadEngine() {
        Camera camera = new Camera(0, 0, 720, 480);
        return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(
                camera.getWidth(), camera.getHeight()), camera));
    }

    @Override
    public void onLoadResources() {
        this.mBitmapTextureAtlas = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas,
                this, "face_box.png", 0, 0);

        this.mEngine.getTextureManager().loadTexture(this.mBitmapTextureAtlas);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene();
        scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));

        scene.attachChild(new Sprite(0, 0, this.mFaceTextureRegion));
        scene.attachChild(new Sprite(0, 100, this.mFaceTextureRegion));
        scene.attachChild(new Sprite(100, 0, this.mFaceTextureRegion));
        scene.attachChild(new Sprite(100, 100, this.mFaceTextureRegion));

        return scene;
    }

    @Override
    public void onLoadComplete() {

    }
}