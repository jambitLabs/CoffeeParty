package com.jambit.coffeeparty;

import java.util.Random;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.modifier.ease.EaseSineInOut;

import android.view.MotionEvent;

public class CatchGameActivity extends MinigameBaseActivity {

    private BitmapTextureAtlas bitmapTextureAtlas;
    private TextureRegion theThingToCatchTexture;

    private Sprite thingToCatchSprite;

    private long lastMovement = System.currentTimeMillis();

    private void catchTheThing() {
        long now = System.currentTimeMillis();
        long timeToCatch = now - lastMovement;

        long score = (1000 - timeToCatch) / 100;
        if (score > 0) {
            addScore((int) score);
        }

        jumpToNewLocation();
    }

    private void jumpToNewLocation() {
        int newX = new Random().nextInt((int) (mCamera.getWidth() - thingToCatchSprite.getWidth()));
        int newY = new Random().nextInt((int) (mCamera.getHeight() - thingToCatchSprite.getHeight()));

        thingToCatchSprite.registerEntityModifier(new MoveModifier(0.1f, thingToCatchSprite.getX(), newX,
                thingToCatchSprite.getY(), newY, EaseSineInOut.getInstance()));

        lastMovement = System.currentTimeMillis();
    }

    @Override
    public void onLoadResources() {
        super.onLoadResources();
        
        bitmapTextureAtlas = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR);
        theThingToCatchTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this,
                "face_box.png", 0, 0);
        mEngine.getTextureManager().loadTexture(bitmapTextureAtlas);
    }

    @Override
    public Scene onLoadScene() {
        final Scene scene = super.onLoadScene();

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

        scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));

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