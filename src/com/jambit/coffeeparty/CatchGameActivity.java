package com.jambit.coffeeparty;

import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.modifier.ease.EaseSineInOut;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Display;
import android.view.MotionEvent;

public class CatchGameActivity extends BaseGameActivity {

    int cameraWidth;
    int cameraHeight;

    private BitmapTextureAtlas bitmapTextureAtlas;
    private TextureRegion theThingToCatchTexture;

    private Font scoreDisplayFont;
    private BitmapTextureAtlas scoreDisplayFontTexture;

    private Sprite thingToCatchSprite;
    private ChangeableText scoreDisplay;

    private long lastMovement = System.currentTimeMillis();
    private int earnedScores = 0;

    private void catchTheThing() {
        long now = System.currentTimeMillis();
        long timeToCatch = now - lastMovement;

        long score = (1000 - timeToCatch) / 100;
        if (score > 0) {
            earnedScores += score;
        }

        jumpToNewLocation();
    }

    private void jumpToNewLocation() {
        int newX = new Random().nextInt((int) (cameraWidth - thingToCatchSprite.getWidth()));
        int newY = new Random().nextInt((int) (cameraHeight - thingToCatchSprite.getHeight()));

        thingToCatchSprite.registerEntityModifier(new MoveModifier(0.1f, thingToCatchSprite.getX(), newX,
                thingToCatchSprite.getY(), newY, EaseSineInOut.getInstance()));

        lastMovement = System.currentTimeMillis();
    }

    private void updateScoreDisplay() {
        this.scoreDisplay.setText("Score: " + earnedScores);
    }

    @Override
    public Engine onLoadEngine() {

        final Display display = getWindowManager().getDefaultDisplay();
        cameraWidth = display.getWidth();
        cameraHeight = display.getHeight();

        Camera camera = new Camera(0, 0, cameraWidth, cameraHeight);
        return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(cameraWidth,
                cameraHeight), camera));
    }

    @Override
    public void onLoadResources() {
        bitmapTextureAtlas = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR);
        theThingToCatchTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this,
                "face_box.png", 0, 0);
        mEngine.getTextureManager().loadTexture(bitmapTextureAtlas);

        scoreDisplayFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        scoreDisplayFont = new Font(scoreDisplayFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32,
                true, Color.BLACK);
        mEngine.getTextureManager().loadTexture(scoreDisplayFontTexture);
        mEngine.getFontManager().loadFont(scoreDisplayFont);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        this.mEngine.registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void reset() {
            }

            @Override
            public void onUpdate(float pSecondsElapsed) {
                long now = System.currentTimeMillis();
                long timeSinceCatch = now - lastMovement;

                if (timeSinceCatch > 1000) {
                    jumpToNewLocation();
                    updateScoreDisplay();
                } else {
                }
            }
        });

        final Scene scene = new Scene();
        scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));

        this.scoreDisplay = new ChangeableText(0, 0, scoreDisplayFont, "                     ");
        scene.attachChild(scoreDisplay);

        thingToCatchSprite = new Sprite(100, 100, theThingToCatchTexture);
        thingToCatchSprite.setSize(50, 50);
        scene.attachChild(thingToCatchSprite);

        jumpToNewLocation();
        updateScoreDisplay();

        return scene;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return false;
        }

        float touchX = event.getX();
        float touchY = event.getY();

        if (touchX >= thingToCatchSprite.getX() && touchX < thingToCatchSprite.getX() + thingToCatchSprite.getWidth()
                && touchY >= thingToCatchSprite.getY()
                && touchY < thingToCatchSprite.getY() + thingToCatchSprite.getHeight()) {
            catchTheThing();
            jumpToNewLocation();
            updateScoreDisplay();

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onLoadComplete() {

    }
}