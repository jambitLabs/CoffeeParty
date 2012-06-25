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

import com.jambit.coffeeparty.model.Field;
import com.jambit.coffeeparty.model.Game;

public class GameBoardActivity extends BaseGameActivity {

    private static int BOARDWIDTH = 720;
    private static int BOARDHEIGHT = 480;

    private BitmapTextureAtlas bitmapTextureAtlas;
    private TextureRegion fieldSpriteTexture;

    private class FieldSprite extends Sprite {
        private final Field field;

        FieldSprite(Field field, int x, int y) {
            super(x, y, fieldSpriteTexture);
            this.field = field;
        }

        public Field getField() {
            return field;
        }
    }

    @Override
    public Engine onLoadEngine() {
        Camera camera = new Camera(0, 0, BOARDWIDTH, BOARDHEIGHT);
        return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(BOARDWIDTH,
                BOARDHEIGHT), camera));
    }

    @Override
    public void onLoadResources() {
        this.bitmapTextureAtlas = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.fieldSpriteTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas,
                this, "face_box.png", 0, 0);

        this.mEngine.getTextureManager().loadTexture(this.bitmapTextureAtlas);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene();
        scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));

        createFields(scene);

        return scene;
    }

    private void createFields(Scene scene) {
        Game gameState = ((CoffeePartyApplication) getApplication()).getGameState();

        int fieldsPerLine = 6;
        
        int lineStartX = 10;
        int lineStartY = 10;
        int fieldWithinLine = 0;
        for (Field field : gameState.getBoard()) {
            scene.attachChild(new FieldSprite(field, lineStartX, lineStartY));
            lineStartX += 100;
            if (++fieldWithinLine > fieldsPerLine) {
                lineStartX = 10;
                lineStartY += 100;
                fieldWithinLine = 0;
            }
        }
    }

    @Override
    public void onLoadComplete() {

    }
}