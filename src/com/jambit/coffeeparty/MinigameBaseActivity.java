package com.jambit.coffeeparty;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Display;

public class MinigameBaseActivity extends BaseGameActivity {

    public static String name;
    public static String description;

    protected Camera mCamera;

    protected int score = 0;

    protected BitmapTextureAtlas mFontTexture;
    protected Font mFont;

    private ChangeableText scoreText;
    protected int cameraWidth;
    protected int cameraHeight;

    @Override
    public Engine onLoadEngine() {
        final Display display = getWindowManager().getDefaultDisplay();
        cameraWidth = display.getWidth();
        cameraHeight = display.getHeight();
        this.mCamera = new Camera(0, 0, cameraWidth, cameraHeight);
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
        
        scoreText = new ChangeableText(20f, 20f, this.mFont, "            ");
        scene.attachChild(scoreText);
        
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
        if (posX >= sprite.getX() && posX < sprite.getX() + sprite.getWidthScaled()
                && posY >= sprite.getY() && posY < sprite.getY() + sprite.getHeightScaled()) {
        	return true;
        } else {
        	return false;
        }
    }
}
