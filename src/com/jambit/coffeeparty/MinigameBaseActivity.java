package com.jambit.coffeeparty;

import java.util.Date;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.HorizontalAlign;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;

public class MinigameBaseActivity extends BaseGameActivity {

    public static String name;
    public static String description;

    protected Camera mCamera;

    private int score = 0;

    protected BitmapTextureAtlas mFontTexture;
    protected Font mFont;

    private ChangeableText scoreText;
    private ChangeableText timeText;

    private DisplayTimer displayTimer;

    protected int cameraWidth;
    protected int cameraHeight;

    private class DisplayTimer {
        private Date startDate;
        private Date endDate;
        private boolean isCountdownTimer;
        private TimerHandler timerHandler;

        DisplayTimer(boolean shouldCountdown, float maxTimeInSec) {
            startDate = new Date();
            endDate = new Date(startDate.getTime() + (long) (maxTimeInSec * 1000));
            this.isCountdownTimer = shouldCountdown;

            MinigameBaseActivity.this.getEngine()
                                     .registerUpdateHandler(timerHandler = new TimerHandler(0.05f,
                                                                                            true,
                                                                                            new ITimerCallback() {
                                                                                                @Override
                                                                                                public void onTimePassed(TimerHandler pTimerHandler) {
                                                                                                    timeText.setText("Time: "
                                                                                                            + getTimerString());
                                                                                                    if (isDone()) {
                                                                                                        unregisterUpdateHandler();
                                                                                                        MinigameBaseActivity.this.onTimerFinished();
                                                                                                        return;
                                                                                                    }
                                                                                                }
                                                                                            }));
        }

        public String getTimerString() {
            long passedTime = (new Date().getTime() - startDate.getTime());

            if (isCountdownTimer) {
                passedTime = (endDate.getTime() - startDate.getTime()) - passedTime;
            }
            // convert to display in tenths of seconds
            passedTime = passedTime / 100;
            float passed = ((float) passedTime) / 10.0f;
            return "" + passed;
        }

        public boolean isDone() {
            return new Date().after(endDate);
        }

        public void unregisterUpdateHandler() {
            MinigameBaseActivity.this.getEngine().unregisterUpdateHandler(timerHandler);
        }

        public float getFractionOfPassedTime() {
            return ((float) (new Date().getTime() - startDate.getTime()))
                    / ((float) (endDate.getTime() - startDate.getTime()));
        }
    }

    @Override
    public Engine onLoadEngine() {
        final Display display = getWindowManager().getDefaultDisplay();
        cameraWidth = display.getWidth();
        cameraHeight = display.getHeight();
        this.mCamera = new Camera(0, 0, 800, 480);
        return new Engine(new EngineOptions(true,
                                            ScreenOrientation.LANDSCAPE,
                                            new RatioResolutionPolicy(cameraWidth, cameraHeight),
                                            this.mCamera));
    }

    @Override
    public void onLoadResources() {

        this.mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        this.mFont = new Font(this.mFontTexture,
                              Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
                              32,
                              true,
                              Color.BLACK);

        this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
        this.mEngine.getFontManager().loadFont(this.mFont);

    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene();
        scene.setBackground(new ColorBackground(0, 0, 0.8784f));

        scoreText = new ChangeableText(20f, 0, this.mFont, "            ");
        scene.attachChild(scoreText);

        timeText = new ChangeableText(cameraWidth - 180, 0, this.mFont, "Time: 0", HorizontalAlign.LEFT, 12);
        scene.attachChild(timeText);

        return scene;
    }

    @Override
    public void onLoadComplete() {
        updateScoreDisplay();
    }

    protected void addScore(int i) {
        score += i;
    }

    protected void reduceScore(int i) {
        score -= i;
        if (score < 0) {
            score = 0;
        }
    }

    protected void setScore(int i) {
        score = i;
    }

    protected void updateScoreDisplay() {
        scoreText.setText("Score: " + score);
    }

    protected void onGameFinished() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.game_result), score);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    protected boolean areCoordinatesInsideSprite(int posX, int posY, Sprite sprite) {
    	if (posX >= sprite.getX() && posX < sprite.getX() + sprite.getWidth() * sprite.getScaleX() && posY >= sprite.getY()
                && posY < sprite.getY() + sprite.getHeight() * sprite.getScaleY()) {
    		Log.d("HIT", "hit");
            return true;
        } else {
            return false;
        }
    }

    // TIMER stuff

    protected void startCountUpTimer(float maxTimeSecs) {

        if (displayTimer != null) {
            displayTimer.unregisterUpdateHandler();
        }
        displayTimer = new DisplayTimer(false, maxTimeSecs);

    }
    
    protected void startCountDownTimer(float startTimeSecs) {
        if (displayTimer != null) {
            displayTimer.unregisterUpdateHandler();
        }
        displayTimer = new DisplayTimer(true, startTimeSecs);
    }

    protected void onTimerFinished() {
        // override this if you need to do some work before the game is done
        onGameFinished();
    }

    /*
     * ! returns the fraction of time left from the current timer, use this for calculating your score
     */
    protected float getFractionOfPassedTime() {
        if (displayTimer == null) {
            return 0;
        } else {
            return displayTimer.getFractionOfPassedTime();
        }
    }

}
