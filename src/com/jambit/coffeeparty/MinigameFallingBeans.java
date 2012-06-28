package com.jambit.coffeeparty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;

public class MinigameFallingBeans extends MinigameBaseActivity implements IAccelerometerListener {
    
    private static final float MAX_BEAN_VELOCITY = 250.0f;
    private static int POINTS_PER_BEAN = 1;
    
    private TextureRegion mBeanTextureRegion;
    private TextureRegion mTeabagTextureRegion;
    private TextureRegion mBackgroundTexture;
    private TextureRegion mCupTextureRegion;
    private BitmapTextureAtlas mBackgroundTextureAtlas;
    private BitmapTextureAtlas mBeanTextureAtlas;
    private BitmapTextureAtlas mTeabagTextureAtlas;
    private BitmapTextureAtlas mCupTextureAtlas;
    private Scene mScene;
    
    private List<FallingSprite> mBeans = new ArrayList<FallingSprite>();
    private Sprite mCupSprite;
    
    private final Random r = new Random(System.currentTimeMillis());
    
    @Override
    public Scene onLoadScene() {
        mScene = super.onLoadScene();
        
        Sprite backgroundSprite = new Sprite(0, 0, mBackgroundTexture);
        mScene.setBackground(new SpriteBackground(backgroundSprite));
        
        final float centerX = (mCamera.getMaxX() - this.mCupTextureRegion.getWidth()) / 2;
        final float bottomY = mCamera.getMaxY() - (this.mCupTextureRegion.getHeight() * 0.75f);
        mCupSprite = new Cup(centerX, bottomY);
        mCupSprite.setScale(0.5f);
        mScene.attachChild(mCupSprite);
        
        this.getEngine().registerUpdateHandler(new IUpdateHandler(){
            @Override
            public void onUpdate(float pSecondsElapsed) {
                for(int i=0; i < mBeans.size(); i++){
                    FallingSprite sprite = mBeans.get(i);
                    if(sprite.isOutOfBounds()){
                        mScene.detachChild(sprite);
                        mBeans.remove(sprite);
                    }
                    
                    if(sprite.isInsideCup()){
                        mScene.detachChild(sprite);
                        mBeans.remove(sprite);
                        switch(sprite.getType()){
                            case BEAN:
                                addScore(POINTS_PER_BEAN);
                                break;
                            case TEABAG:
                                reduceScore(POINTS_PER_BEAN);
                                break;
                        }
                    }
                }
            }

            @Override
            public void reset() {
                mBeans.clear();
            }
        });
        
        // create new beans and teabags periodically
        mScene.registerUpdateHandler(new TimerHandler(0.5f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                final float insertX = r.nextFloat() * mCamera.getMaxX();
                float xVelo;
                // alternate directions on the x-axis
                if(r.nextBoolean())
                    xVelo = 1 + r.nextFloat() * MAX_BEAN_VELOCITY;
                else
                    xVelo = 1 - r.nextFloat() * MAX_BEAN_VELOCITY;
                // assert a certain min speed on the y axis
                final float yVelo = MAX_BEAN_VELOCITY / 2 + r.nextFloat() * (MAX_BEAN_VELOCITY / 2);
                FallingSpriteType type;
                TextureRegion texture;
                if(r.nextBoolean()){
                    type = FallingSpriteType.BEAN;
                    texture = mBeanTextureRegion;
                }
                else{
                    type = FallingSpriteType.TEABAG;
                    texture = mTeabagTextureRegion;
                }
                FallingSprite bean = new FallingSprite(insertX, -100.0f, xVelo, yVelo, type, texture);
                mScene.attachChild(bean);
                mBeans.add(bean);
            }
        }));
        
        return mScene;
    }
    
    @Override
    public void onLoadResources() {
        super.onLoadResources();
        this.mBeanTextureAtlas = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mBeanTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBeanTextureAtlas, this, 
                "jambitbean_white.png", 0, 0);
        this.mTeabagTextureAtlas = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mTeabagTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mTeabagTextureAtlas, this, 
                "teabag.png", 0, 0);
        this.mCupTextureAtlas = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mCupTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mCupTextureAtlas, this, 
                "coffee-cup.png", 0, 0);
        this.mBackgroundTextureAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR);
        this.mBackgroundTexture = (TextureRegion) BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBackgroundTextureAtlas, this,
                "kitchen.png", 0, 0);
        this.mEngine.getTextureManager().loadTexture(mBeanTextureAtlas);
        this.mEngine.getTextureManager().loadTexture(mTeabagTextureAtlas);
        this.mEngine.getTextureManager().loadTexture(mCupTextureAtlas);
        this.mEngine.getTextureManager().loadTexture(mBackgroundTextureAtlas);
    }
    
    @Override
    public void onLoadComplete(){
        startCountDownTimer(30);
        super.onLoadComplete();
    }
    
    @Override
    public void onResumeGame(){
        this.enableAccelerometerSensor(this);
    }
    
    @Override
    public void onPauseGame() {
        this.disableAccelerometerSensor();
    }
    
    @Override
    public void onAccelerometerChanged(AccelerometerData pAccelerometerData) {
        mCupSprite.setPosition(mCupSprite.getX() + pAccelerometerData.getX() * 2.5f , mCupSprite.getY());
    }
    
    private enum FallingSpriteType{
        BEAN,
        TEABAG
    }
    
    private class FallingSprite extends Sprite {
        private final PhysicsHandler mPhysicsHandler;
        private boolean mOutOfBounds = false;
        private boolean mInsideCup = false;
        private FallingSpriteType type;
        
        public FallingSprite(final float pX, final float pY, final float xVelocity, final float yVelocity, 
                FallingSpriteType type, TextureRegion pTextureRegion) {
            super(pX, pY, pTextureRegion);
            this.type = type;
            this.mPhysicsHandler = new PhysicsHandler(this);
            this.registerUpdateHandler(this.mPhysicsHandler);
            this.mPhysicsHandler.setVelocity(xVelocity, yVelocity);
        }
        
        public boolean isOutOfBounds(){
            return this.mOutOfBounds;
        }
        
        public boolean isInsideCup(){
            // calc only if not in cup already
            if(!this.mInsideCup)
                this.mInsideCup = areCoordinatesInsideSprite(this.getX(), this.getY(), mCupSprite);
            return this.mInsideCup;
        }
        
        public FallingSpriteType getType(){
            return this.type;
        }

        @Override
        protected void onManagedUpdate(final float pSecondsElapsed) {
            if(this.mX < 0) {
                this.mPhysicsHandler.setVelocityX(MAX_BEAN_VELOCITY);
            } else if(this.mX + this.getWidth() > mCamera.getMaxX()) {
                this.mPhysicsHandler.setVelocityX(-MAX_BEAN_VELOCITY);
            }

            if(this.mY < 0) {
                this.mPhysicsHandler.setVelocityY(MAX_BEAN_VELOCITY);
            } else if(this.mY + this.getHeight() > mCamera.getMaxY()) {
                mOutOfBounds = true;
            }

            super.onManagedUpdate(pSecondsElapsed);
        }
    }
    
    private class Cup extends Sprite{

        public Cup(float pX, float pY) {
            super(pX, pY, mCupTextureRegion);
        }
        
        /*
         * Implementation prevents Cup from leaving screen on the X-axis
         * @see org.anddev.andengine.entity.Entity#onManagedUpdate(float)
         */
        @Override
        protected void onManagedUpdate(final float pSecondsElapsed) {
            if(this.mX < 0) {
                this.mX = 0;
            } else if(this.mX + this.getWidth() > mCamera.getMaxX()) {
                this.mX = mCamera.getMaxX() - this.getWidth();
            }

            super.onManagedUpdate(pSecondsElapsed);
        }
    }
}
