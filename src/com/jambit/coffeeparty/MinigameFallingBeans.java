package com.jambit.coffeeparty;

import java.util.ArrayList;
import java.util.List;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class MinigameFallingBeans extends MinigameBaseActivity {
    
    private static final float BEAN_VELOCITY = 125.0f;
    
    private TextureRegion mBeanTextureRegion;
    private BitmapTextureAtlas mBeanTextureAtlas;
    private Scene mScene;
    
    private List<Bean> mBeans = new ArrayList<Bean>();
    
    @Override
    public Scene onLoadScene() {
        mScene = super.onLoadScene();
        
        this.getEngine().registerUpdateHandler(new IUpdateHandler(){
            
            private float lastCreation;
            
            @Override
            public void onUpdate(float pSecondsElapsed) {
                for(Bean b : mBeans){
                    if(b.isOutOfBounds())
                        mScene.detachChild(b);
                }
                if(pSecondsElapsed - lastCreation > 1){
                    final float centerX = (mCamera.getMaxX() - mBeanTextureRegion.getWidth()) / 2;
                    Bean bean = new Bean(centerX, 0.0f, mBeanTextureRegion);
                    mScene.attachChild(bean);
                    mBeans.add(bean);
                    lastCreation = pSecondsElapsed;
                }
            }

            @Override
            public void reset() {
                mBeans.clear();
            }
        });
        
        startCountDownTimer(30);
        return mScene;
    }
    
    @Override
    public void onLoadResources() {
        super.onLoadResources();
        this.mBeanTextureAtlas = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mBeanTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBeanTextureAtlas, this, "jambitbean.png", 0, 0);
        this.mEngine.getTextureManager().loadTexture(mBeanTextureAtlas);
    }
    
    private class Bean extends Sprite {
        private final PhysicsHandler mPhysicsHandler;
        private boolean mOutOfBounds = false;
        
        public Bean(final float pX, final float pY, final TextureRegion pTextureRegion) {
            super(pX, pY, pTextureRegion);
            this.mPhysicsHandler = new PhysicsHandler(this);
            this.registerUpdateHandler(this.mPhysicsHandler);
            this.mPhysicsHandler.setVelocity(BEAN_VELOCITY, BEAN_VELOCITY);
        }
        
        public boolean isOutOfBounds(){
            return mOutOfBounds;
        }

        @Override
        protected void onManagedUpdate(final float pSecondsElapsed) {
            if(this.mX < 0) {
                this.mPhysicsHandler.setVelocityX(BEAN_VELOCITY);
            } else if(this.mX + this.getWidth() > mCamera.getMaxX()) {
                this.mPhysicsHandler.setVelocityX(-BEAN_VELOCITY);
            }

            if(this.mY < 0) {
                this.mPhysicsHandler.setVelocityY(BEAN_VELOCITY);
            } else if(this.mY + this.getHeight() > mCamera.getMaxY()) {
                mOutOfBounds = true;
            }

            super.onManagedUpdate(pSecondsElapsed);
        }
    }
}
