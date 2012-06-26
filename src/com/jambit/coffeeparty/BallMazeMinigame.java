package com.jambit.coffeeparty;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

public class BallMazeMinigame extends MinigameBaseActivity {

    private BitmapTextureAtlas bitmapTextureAtlas;
    private TextureRegion backgroundTexture;
    private TextureRegion playerSpriteTexture;

    public Scene onLoadScene() {
        final Scene scene = super.onLoadScene();

        Sprite test = new Sprite(50, 50, playerSpriteTexture);
        scene.attachChild(test);

        return scene;
    }

    @Override
    public void onLoadResources() {
        super.onLoadResources();
        this.bitmapTextureAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR);

        this.backgroundTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas,
                                                                                        this,
                                                                                        "gameboard.png",
                                                                                        0,
                                                                                        0);

        this.playerSpriteTexture = BitmapTextureAtlasTextureRegionFactory.createFromResource(bitmapTextureAtlas,
                                                                                             getBaseContext(),
                                                                                             R.drawable.droid_green,
                                                                                             0,
                                                                                             0);

        this.mEngine.getTextureManager().loadTexture(this.bitmapTextureAtlas);

    }
}
