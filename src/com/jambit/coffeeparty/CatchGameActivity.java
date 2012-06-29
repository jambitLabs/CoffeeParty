package com.jambit.coffeeparty;

import java.util.LinkedList;
import java.util.Random;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.modifier.ease.EaseSineInOut;

import android.view.Display;
import android.view.MotionEvent;

public class CatchGameActivity extends MinigameBaseActivity {

    private TextureRegion theThingToCatchTexture;
    private TextureRegion backgroundTexture;
    private TiledTextureRegion meadowTexture;

    private Sprite thingToCatchSprite;
    private LinkedList<AnimatedSprite> meadows = new LinkedList<AnimatedSprite>();
    
    private long startTime = System.currentTimeMillis();
    private long lastMovement = System.currentTimeMillis();
    
    private static Random random = new Random();

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
        int newX = random.nextInt((int) (mCamera.getWidth() - thingToCatchSprite.getWidth()));
        int newY = random.nextInt((int) (mCamera.getHeight() - thingToCatchSprite.getHeight()));

        thingToCatchSprite.registerEntityModifier(new MoveModifier(0.1f, thingToCatchSprite.getX(), newX,
                thingToCatchSprite.getY(), newY, EaseSineInOut.getInstance()));

        lastMovement = System.currentTimeMillis();
    }

    @Override
    public void onLoadResources() {
        super.onLoadResources();
        
        BitmapTextureAtlas bitmapTextureAtlas;
        
        bitmapTextureAtlas = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR);
        theThingToCatchTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this,
                "huml.png", 0, 0);
        mEngine.getTextureManager().loadTexture(bitmapTextureAtlas);

        bitmapTextureAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR);
        backgroundTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas, this,
                "cloud.png", 0, 0);
        mEngine.getTextureManager().loadTexture(bitmapTextureAtlas);

        bitmapTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR);
        meadowTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, this,
                "meadow.png", 0, 0, 1, 8);
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
                
                if (now - startTime > 60 * 1000)
                {
                    onGameFinished();
                } else
                {
                    long timeSinceCatch = now - lastMovement;

                    if (timeSinceCatch > 1000) {
                        jumpToNewLocation();
                        updateScoreDisplay();
                    }
                }
            }
        });
        
        this.mEngine.registerUpdateHandler(new TimerHandler(4.0f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {

                while (meadows.size() > 5) {
                    mEngine.getScene().detachChild(meadows.poll());
                }

                AnimatedSprite meadowSprite = new AnimatedSprite(0, 0, meadowTexture);
                meadowSprite.setSize(mCamera.getWidth(), mCamera.getHeight() / 5.0f);
                meadowSprite.setCurrentTileIndex(random.nextInt(meadowTexture.getTileCount()));

                final Path path = new Path(2)
                        .to(-meadowSprite.getWidth(),mCamera.getHeight() - meadowSprite.getHeight() + 40)
                        .to(meadowSprite.getWidth() + mCamera.getWidth(), mCamera.getHeight() - meadowSprite.getHeight() + 40);
                meadowSprite.registerEntityModifier(new PathModifier(random.nextInt(10) + 10, path));

                meadows.add(meadowSprite);
                scene.attachChild(meadowSprite);
            }
        }));

        scene.setBackground(new SpriteBackground(new Sprite(0, 0, backgroundTexture)));

        thingToCatchSprite = new Sprite(100, 100, theThingToCatchTexture);
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

        final Display display = getWindowManager().getDefaultDisplay();        
        float touchX = event.getX() * mCamera.getWidth() / display.getWidth();
        float touchY = event.getY() * mCamera.getHeight() / display.getHeight();

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