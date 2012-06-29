package com.jambit.coffeeparty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.particle.ParticleSystem;
import org.anddev.andengine.entity.particle.emitter.PointParticleEmitter;
import org.anddev.andengine.entity.particle.initializer.GravityInitializer;
import org.anddev.andengine.entity.particle.initializer.VelocityInitializer;
import org.anddev.andengine.entity.particle.modifier.AlphaModifier;
import org.anddev.andengine.entity.particle.modifier.ExpireModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;

public class MinigameWhackAMole extends MinigameBaseActivity {

	private SoundPool soundPool;
	private int soundIDWhack1;
	private int soundIDWhack2;
	private int soundIDWhack3;
	private int soundIDWhack4;
	
	Random r;
    
    private class Mole {
        private Sprite sprite;
        final float targetScale = 0.4f;

        public Mole(Sprite s) {
            s.setScale(0.0f);
            this.sprite = s;
        }

        public void hit() {
        	sprite.clearEntityModifiers();
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
    private TextureRegion backgroundTexture;
    private TextureRegion particleTexture;
    
    private ParticleSystem particleSystem;
    
    private List<Mole> moles;

    public Scene onLoadScene() {
        final Scene scene = super.onLoadScene();
        Sprite bgSprite = new Sprite (0, 0, backgroundTexture);
        scene.setBackground(new SpriteBackground(bgSprite));
        
        
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
        scene.registerUpdateHandler(new TimerHandler(0.3f, true, new ITimerCallback() {
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
        BitmapTextureAtlas bgAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR);
        
        this.moleTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(moleAtlas, this, "jambitbean_big.png", 0, 0);
        this.holeSpriteTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(holeAtlas,
                                                                                        this,
                                                                                        "hole.png",
                                                                                        0,
                                                                                        0);
        this.backgroundTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bgAtlas, this, "mole_hills.jpg", 0, 0);
        this.particleTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bgAtlas, this, "jambitbean.png", 0, 0);

        this.mEngine.getTextureManager().loadTexture(moleAtlas);
        this.mEngine.getTextureManager().loadTexture(holeAtlas);
        this.mEngine.getTextureManager().loadTexture(bgAtlas);
        
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundIDWhack1 = soundPool.load(this, R.raw.sharppunch, 1);
        soundIDWhack2 = soundPool.load(this, R.raw.strongpunch, 1);
        soundIDWhack3 = soundPool.load(this, R.raw.whack, 1);
        soundIDWhack4 = soundPool.load(this, R.raw.woodwhack, 1);
        r = new Random();
    }
    
    private int getRandomSoundID () {
    	int i = r.nextInt(4);
    	switch (i){
    	case 0:
    		return soundIDWhack1;
    	case 1:
    		return soundIDWhack2;
    	case 2:
    		return soundIDWhack3;
    	case 3:
    		return soundIDWhack4;
    	default:
    		return soundIDWhack1;
    	}
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for (Mole mole : moles) {
                if (areCoordinatesInsideSprite(event.getX(), event.getY(), mole.sprite)) {
                	mole.hit();
                    addScore(1);
                    updateScoreDisplay();
                    soundPool.play(this.getRandomSoundID(), 1f, 1f, 1, 0, 1f);
                    showParticles(event.getX(), event.getY());
                    break;
                }
            }
        }
        return super.onTouchEvent(event);
    }
    
    private ParticleSystem createParticleSystem (float particlesXSpawn, float particlesYSpawn, final TextureRegion particleTextureRegion) {

        //Max & min rate are the maximum particles per second and the minimum particles per second.
        final float maxRate = 100;
        final float minRate = 80;

        //This variable determines the maximum particles in the particle system.
        final int maxParticles = 500;

        //Particle emitter which will set all of the particles at a ertain point when they are initialized.
        final PointParticleEmitter pointParticleEmtitter = new PointParticleEmitter(particlesXSpawn, particlesYSpawn);

        //Creating the particle system.
        final ParticleSystem particleSystem = new ParticleSystem(pointParticleEmtitter, maxRate, minRate, maxParticles, particleTextureRegion);

        //And now, lets create the initiallizers and modifiers.
        //Velocity initiallizer - will pick a random velocity from -20 to 20 on the x & y axes. Play around with this value.
        particleSystem.addParticleInitializer(new VelocityInitializer(-1000, 1000, -1000, 1000));

        //Acceleration initializer - gives all the particles the earth gravity (so they accelerate down).
        particleSystem.addParticleInitializer(new GravityInitializer());

        //And now, adding an alpha modifier, so particles slowly fade out. This makes a particle go from alpha = 1 to alpha = 0 in 3 seconds, starting exactly when the particle is spawned.
        particleSystem.addParticleModifier(new AlphaModifier(1, 0, 0, 0.5f));

        //Lastly, expire modifier. Make particles die after 3 seconds - their alpha reached 0.
        particleSystem.addParticleModifier(new ExpireModifier(0.25f));

        return particleSystem;

    }
    
    private void showParticles(float x, float y) {
    	final ParticleSystem particleSystem = createParticleSystem(x, y, particleTexture);
    	
    	final Scene scene = mEngine.getScene();
    	
    	if (particleSystem.hasParent()) {
    		particleSystem.detachSelf();
    	}
    	particleSystem.setPosition(x, y);
    	
    	scene.attachChild(particleSystem);
        
        scene.registerUpdateHandler(new TimerHandler(0.25f, new ITimerCallback() {
        	@Override
        	public void onTimePassed(TimerHandler pTimerHandler) {
        		particleSystem.setParticlesSpawnEnabled(false);
        		scene.unregisterUpdateHandler(pTimerHandler);
        	}
        }));

        scene.registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
        	@Override
        	public void onTimePassed(TimerHandler pTimerHandler) {
        		scene.detachChild(particleSystem);
        		scene.unregisterUpdateHandler(pTimerHandler);
        	}
        }));
    }
}