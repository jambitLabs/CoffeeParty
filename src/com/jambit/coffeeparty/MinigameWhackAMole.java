package com.jambit.coffeeparty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.DelayModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.util.modifier.IModifier;

import android.view.MotionEvent;

public class MinigameWhackAMole extends MinigameBaseActivity {

    private class Mole {
        public Sprite sprite;
        public boolean isActive = false;
        final float targetScale = 2.0f;

        public Mole(Sprite s) {
            s.setScale(0.0f);
            this.sprite = s;
        }

        public boolean hit() {
            if (isActive) {
                isActive = false;
                sprite.registerEntityModifier(this.getHitModifier());
                addScore(1);
                updateScoreDisplay();
                return true;
            } else {
                return false;
            }
        }

        public boolean disappear() {
            if (isActive) {
                isActive = false;
                sprite.registerEntityModifier(this.getDisappearModifier());
                return true;
            } else {
                return false;
            }
        }

        public boolean appear() {
            if (isActive) {
                return false;
            } else {
                isActive = true;
                sprite.registerEntityModifier(this.getAppearModifier());
                sprite.registerEntityModifier(new DelayModifier(1f, new IEntityModifierListener() {

                    @Override
                    public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {

                    }

                    @Override
                    public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                        Mole.this.disappear();
                    }
                }));

                return true;
            }
        }

        private ScaleModifier getAppearModifier() {
            return new ScaleModifier(1, 0, targetScale);
        }

        private ScaleModifier getDisappearModifier() {
            return new ScaleModifier(1, targetScale, 0);
        }

        private ScaleModifier getHitModifier() {
            return new ScaleModifier(0.25f, targetScale, 0);
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
                if (r.nextBoolean()) {
                    Mole currentMole = moles.get(r.nextInt(moles.size()));
                    if (!currentMole.isActive) {
                        currentMole.appear();
                    }
                } else {
                    // moles.get(r.nextInt(moles.size())).hit();
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
        Random r = new Random();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for (Mole mole : moles) {
                if (areCoordinatesInsideSprite(event.getX(), event.getY(), mole.sprite)) {
                    if (mole.isActive) {
                        mole.hit();
                    }
                    break;
                }

            }
        }
        return super.onTouchEvent(event);
    }
}