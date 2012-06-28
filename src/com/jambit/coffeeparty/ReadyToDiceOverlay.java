package com.jambit.coffeeparty;

import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

public class ReadyToDiceOverlay extends Entity {

    private static final String GET_READY = "Get ready :";
    private Sprite diceButtonSprite;
    private Sprite overlaySprite;
    private float pX = 0;
    private float pY = 0;

    private TextureRegion diceButtonRegion;
    private BitmapTextureAtlas diceButtonTextureAtlas;

    private TextureRegion overlayRegion;
    private BitmapTextureAtlas overlayTextureAtlas;

    private BaseGameActivity context;
    private Font playerNameFont;
    private Scene mainScene;
    private ChangeableText playerNameText;

    public ReadyToDiceOverlay(BaseGameActivity context, Scene mainScene) {
        super();
        this.context = context;
        this.mainScene = mainScene;

        overlayTextureAtlas = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR);

        this.overlayRegion = (TextureRegion) BitmapTextureAtlasTextureRegionFactory.createFromAsset(overlayTextureAtlas,
                                                                                                    context,
                                                                                                    "toast_frame.png",
                                                                                                    0,
                                                                                                    0);
        context.getEngine().getTextureManager().loadTexture(overlayTextureAtlas);

        overlaySprite = new Sprite(pX, pY, overlayRegion) {
            public boolean onAreaTouched(org.anddev.andengine.input.touch.TouchEvent pSceneTouchEvent,
                                         float pTouchAreaLocalX,
                                         float pTouchAreaLocalY) {
                startRollDiceActivity();
                return true;
            };
        };
        overlaySprite.setScaleCenter(0, 0);
        overlaySprite.setScale(4.0f, 2.7f);
        overlaySprite.setAlpha(0.8f);
        this.attachChild(overlaySprite);

        diceButtonTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR);

        this.diceButtonRegion = (TextureRegion) BitmapTextureAtlasTextureRegionFactory.createFromAsset(diceButtonTextureAtlas,
                                                                                                       context,
                                                                                                       "wuerfelmitbohnen.png",
                                                                                                       0,
                                                                                                       0);
        context.getEngine().getTextureManager().loadTexture(diceButtonTextureAtlas);

        diceButtonSprite = new Sprite(pX + 150, pY + 80, diceButtonRegion);

        diceButtonSprite.setScaleCenter(0, 0);
        diceButtonSprite.setScale(0.25f);

        mainScene.setTouchAreaBindingEnabled(true);
        this.attachChild(diceButtonSprite);
        mainScene.registerTouchArea(overlaySprite);

        BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.playerNameFont = new Font(fontTexture,
                                       Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
                                       20,
                                       true,
                                       Color.BLACK);

        context.getEngine().getTextureManager().loadTexture(fontTexture);
        context.getEngine().getFontManager().loadFont(this.playerNameFont);

        playerNameText = new ChangeableText(pX + 80, pY + 50, playerNameFont, GET_READY + "           ");
        this.attachChild(playerNameText);

    }

    public void setPlayerNameText(String playerName) {
        String newText = GET_READY + playerName;
        this.playerNameText.setText(newText);
    }

    public void startRollDiceActivity() {
        Log.i("readyToRoll", "Overlay touched...");
        Intent intent = new Intent(context, DiceRollActivity.class);
        context.startActivityForResult(intent, GameBoardActivity.DICE_ROLLED);
        this.setVisible(false);
    }

}
