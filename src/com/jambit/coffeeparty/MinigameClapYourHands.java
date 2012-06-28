package com.jambit.coffeeparty;

import java.util.ArrayList;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

import android.graphics.PointF;
import android.util.Log;

import com.jambit.coffeeparty.clapyourhandsgame.ClapDetector;
import com.jambit.coffeeparty.clapyourhandsgame.ClapDetector.ClapDetectorListener;

// TODO: Scores
// TODO: Mutex
// TODO: Improve Clap detection: Cleanup, Configurable, etc.
// TODO: Multiple Beats
// TODO: 

public class MinigameClapYourHands extends MinigameBaseActivity implements ClapDetectorListener {

    private class SemiBeat {
        private boolean hit = false;

        public SemiBeat(boolean hit) {
            super();
            this.hit = hit;
        }

        public void setHit(boolean hit) {
            this.hit = hit;
        }

        public boolean isHit() {
            return hit;
        }
    }

    private class Beat {
        private ArrayList<SemiBeat> semiBeats = new ArrayList<SemiBeat>();

        public Beat(String beatAsString) {
            super();
            setBeatFromString(beatAsString);
        }

        public Beat(int numSemiBeats) {
            super();
            for (int i = 0; i < numSemiBeats; i++) {
                semiBeats.add(new SemiBeat(false));
            }
        }

        public void setHit(int semiBeatIndex, boolean hit) {
            semiBeats.get(semiBeatIndex % semiBeats.size()).setHit(hit);
        }

        private void setBeatFromString(String beatAsString) {
            semiBeats.clear();
            for (int i = 0; i < beatAsString.length(); i++) {
                semiBeats.add(new SemiBeat(beatAsString.charAt(i) != ' '));
            }
        }

        public boolean isHit(int semiBeatIndex) {
            if (semiBeatIndex < 0)
                return false;
            else
                return semiBeats.get(semiBeatIndex % semiBeats.size()).isHit();
        }

        public int numSemiBeats() {
            return semiBeats.size();
        }
    }

    private enum CurrentState {
        PLAYING, RECORDING
    };

    private CurrentState state = CurrentState.PLAYING;

    private Beat currentBeat = new Beat("*   * * *   *   ");
    private Beat recordedBeat;
    float semiBeatsPerMinute = 80.0f * 4.0f;
    private int currentSemiBeatIndex = -8;

    private ClapDetector clapDetector = new ClapDetector(this);

    private TiledTextureRegion dotTexture;
    private TiledTextureRegion dot2Texture;
    private AnimatedSprite playHeadSprite;

    private Sound tickSound;

    @Override
    public Scene onLoadScene() {
        final Scene scene = super.onLoadScene();

        mEngine.registerUpdateHandler(new IUpdateHandler() {

            float lastCurrentTime = 0;
            int lastSemiBeat = -1;

            @Override
            public void reset() {
                lastCurrentTime = 0;
                lastSemiBeat = -1;
            }

            @Override
            public void onUpdate(float pSecondsElapsed) {
                float currentTime = lastCurrentTime + pSecondsElapsed;

                int newSemiBeat = (int) (currentTime / 60 * semiBeatsPerMinute);
                while (lastSemiBeat < newSemiBeat)
                {
                    lastSemiBeat++;
                    onNextSemiBeat();
                }

                lastCurrentTime = currentTime;
            }
        });

        // startCountDownTimer(15);
        return scene;
    }

    private void onNextSemiBeat() {
        currentSemiBeatIndex++;

        PointF semiBeatPosition = getPositionForSemiBeat(currentSemiBeatIndex);
        playHeadSprite.setPosition(semiBeatPosition.x, semiBeatPosition.y + 20);

        if (state == CurrentState.PLAYING) {
            if (currentBeat.isHit(currentSemiBeatIndex))
                tickSound.play();

            if (currentSemiBeatIndex == currentBeat.numSemiBeats() - 1)
                switchToRecordingState();
        } else if (state == CurrentState.RECORDING) {
            if (currentSemiBeatIndex == currentBeat.numSemiBeats() - 1)
                switchToPlayingState();
        }
    }

    private void switchToRecordingState() {
        currentSemiBeatIndex = -1;
        recordedBeat = new Beat(currentBeat.numSemiBeats());

        mEngine.getScene().setBackground(new ColorBackground(0.8784f, 0, 0f));

        state = CurrentState.RECORDING;
    }

    private void switchToPlayingState() {
        currentSemiBeatIndex = -8;

        // TODO: Create a new Beat
        recordedBeat = new Beat(currentBeat.numSemiBeats());

        mEngine.getScene().setBackground(new ColorBackground(0, 0, 0.8784f));
        drawBeat(mEngine.getScene());

        state = CurrentState.PLAYING;
    }

    private PointF getPositionForSemiBeat(int semiBeatIndex) {
        float xMargin = 100;
        float yMargin = 100;
        float xScale = (mCamera.getWidth() - 2 * xMargin) / currentBeat.numSemiBeats();
        return new PointF(xMargin + semiBeatIndex * xScale, yMargin);
    }

    private void drawBeat(Scene scene) {
        scene.detachChildren();
        for (int semiBeatIndex = 0; semiBeatIndex < currentBeat.numSemiBeats(); semiBeatIndex++) {
            if (currentBeat.isHit(semiBeatIndex))
            {
                PointF semiBeatPosition = getPositionForSemiBeat(semiBeatIndex);
                scene.attachChild(new AnimatedSprite(semiBeatPosition.x, semiBeatPosition.y, dotTexture));
            }
        }

        PointF semiBeatPosition = getPositionForSemiBeat(currentSemiBeatIndex);
        playHeadSprite = new AnimatedSprite(semiBeatPosition.x, semiBeatPosition.y + 20, dot2Texture);
        mEngine.getScene().attachChild(playHeadSprite);
    }

    @Override
    public void onLoadResources() {
        super.onLoadResources();

        BitmapTextureAtlas bitmapTextureAtlas = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR);

        this.dotTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, this,
                "dot.png", 0, 0, 2, 1);
        this.dot2Texture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(bitmapTextureAtlas, this,
                "dot2.png", 0, 0, 2, 1);

        this.mEngine.getTextureManager().loadTexture(bitmapTextureAtlas);

        try {
            this.tickSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "tick.wav");
            this.tickSound.setLooping(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPauseGame() {
        clapDetector.interrupt();
//        clapDetector.join();
        super.onPauseGame();
    }

    @Override
    public void onResumeGame() {
        clapDetector.start(); // resume?
        switchToPlayingState();
        super.onResumeGame();
    }

    @Override
    public void onClapDetected(double time) {

        int newSemiBeat = (int) (time / 60 * semiBeatsPerMinute);
        Log.d("clap", "onClapDetected at beat index " + currentSemiBeatIndex + " newSemiBeat=" + newSemiBeat);
        
        if (state == CurrentState.RECORDING) {
            if (currentSemiBeatIndex >= 0 && currentSemiBeatIndex < currentBeat.numSemiBeats()) {
                recordedBeat.setHit(currentSemiBeatIndex, true);
                PointF semiBeatPosition = getPositionForSemiBeat(currentSemiBeatIndex);
                
                AnimatedSprite clapIndicator = new AnimatedSprite(semiBeatPosition.x, semiBeatPosition.y + 40, dotTexture);
                clapIndicator.setCurrentTileIndex(currentBeat.isHit(currentSemiBeatIndex) ? 1 : 0);
                mEngine.getScene().attachChild(clapIndicator);
            }
        }
    }

}
