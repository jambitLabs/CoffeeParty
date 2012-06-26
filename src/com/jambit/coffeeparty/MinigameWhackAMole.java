package com.jambit.coffeeparty;

import java.util.Random;

import org.anddev.andengine.entity.modifier.MoveByModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.HorizontalAlign;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class MinigameWhackAMole extends MinigameBaseActivity {
    private BitmapTextureAtlas bitmapTextureAtlas;
    private TextureRegion backgroundTexture;
    private TiledTextureRegion playerSpriteTexture;

	public Scene onLoadScene() {
		final Scene scene = super.onLoadScene();
		 
		AnimatedSprite test = new AnimatedSprite(30, 80, playerSpriteTexture);
		test.registerEntityModifier(new ScaleModifier(2, 0, 3));
		scene.attachChild(test);
		
		return scene;
	}
	
	@Override
	public void onLoadResources() {
		super.onLoadResources();
		this.bitmapTextureAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR);
        this.backgroundTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, this,
                "gameboard.png", 0, 0);
        this.playerSpriteTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.bitmapTextureAtlas,
                this, "face_box_tiled.png", 132, 180, 2, 1);

        this.mEngine.getTextureManager().loadTexture(this.bitmapTextureAtlas);

	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Random r = new Random();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			AnimatedSprite test = new AnimatedSprite(r.nextInt(600), r.nextInt(800), playerSpriteTexture);
			test.registerEntityModifier(new ScaleModifier(2, 0, 3));
			mEngine.getScene().attachChild(test);
		}
		return super.onTouchEvent(event);
	}
}