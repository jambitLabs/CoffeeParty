package com.jambit.coffeeparty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.view.MotionEvent;

public class MinigameWhackAMole extends MinigameBaseActivity {

    private class Mole {
        private Sprite sprite;
        final float targetScale = 2.0f;

        public Mole(Sprite s) {
            s.setScale(0.0f);
            this.sprite = s;
        }

        public void hit() {
                sprite.registerEntityModifier(this.getHitModifier());
        }

        public void disappear() {
                sprite.registerEntityModifier(this.getDisappearModifier());
        }

        public void appear() {
                sprite.registerEntityModifier(this.getAppearModifier());
        }
        
        public boolean isFullyScaledUp() {
        	return sprite.getScaleX() == targetScale;
        }
        
        public boolean isFullyScaledDown() {
        	return sprite.getScaleX() == 0f;
        }

        private ScaleModifier getAppearModifier() {
            return new ScaleModifier(1, 0.0f, targetScale);
        }

        private ScaleModifier getDisappearModifier() {
            return new ScaleModifier(0.5f, targetScale, 0.0f);
        }

        private ScaleModifier getHitModifier() {
            return new ScaleModifier(0.05f, targetScale, 0.0f);
        }
    }

    private TextureRegion moleTexture;
    private TextureRegion holeSpriteTexture;

    private List<Mole> moles;

    public Scene onLoadScene() {
        final Scene scene = super.onLoadScene();
        float maxX = mCamera.getMaxX();
        float maxY = mCamera.getMaxY();

        moles = new ArrayList<Mole>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float x = maxX / 4 + i * maxX / 4;
                float y = maxY / 4 + j * maxY / 4;
                moles.add(new Mole(new Sprite(x - moleTexture.getWidth() / 2,
                                              y - moleTexture.getHeight() / 2,
                                              moleTexture)));
            }
        }

        // create holes
        for (Mole mole : moles) {
            Sprite newSprite = new Sprite(mole.sprite.getX() - holeSpriteTexture.getWidth() / 2
                    + moleTexture.getWidth() / 2, mole.sprite.getY() - holeSpriteTexture.getHeight() / 2
                    + moleTexture.getHeight() / 2, holeSpriteTexture);
            newSprite.setScale(0.65f);
            scene.attachChild(newSprite);
        }
        // add moles
        for (Mole mole : moles) {
            scene.attachChild(mole.sprite);
        }
        scene.registerUpdateHandler(new TimerHandler(0.2f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                Random r = new Random();
                Mole currentMole = moles.get(r.nextInt(moles.size()));
                if (r.nextBoolean() && currentMole.isFullyScaledDown()) {
                    currentMole.appear();
                } else if (currentMole.isFullyScaledUp()){
                	currentMole.disappear();
                }
            }
        }));
        startCountDownTimer(15);
        return scene;
    }

    @Override
    public void onLoadResources() {
        super.onLoadResources();
        BitmapTextureAtlas moleAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR);
        BitmapTextureAtlas holeAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR);
        this.moleTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(moleAtlas, this, "face_box.png", 0, 0);
        this.holeSpriteTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(holeAtlas,
                                                                                        this,
                                                                                        "hole.png",
                                                                                        0,
                                                                                        0);

        this.mEngine.getTextureManager().loadTexture(moleAtlas);
        this.mEngine.getTextureManager().loadTexture(holeAtlas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for (Mole mole : moles) {
                if (areCoordinatesInsideSprite(event.getX(), event.getY(), mole.sprite)) {
                	mole.hit();
                    addScore(1);
                    updateScoreDisplay();
                    break;
                }
            }
        }
        return super.onTouchEvent(event);
    }
}