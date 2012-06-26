package com.jambit.coffeeparty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.view.Display;
import android.view.MotionEvent;

import com.jambit.coffeeparty.model.Field;
import com.jambit.coffeeparty.model.Game;
import com.jambit.coffeeparty.model.Player;

public class GameBoardActivity extends BaseGameActivity {

    private BitmapTextureAtlas bitmapTextureAtlas;
    private TextureRegion backgroundTexture;
    private TiledTextureRegion playerSpriteTexture;

    private class PlayerSprite extends AnimatedSprite {
        private final Player player;

        PlayerSprite(Player player, int x, int y) {
            super(x, y, playerSpriteTexture);
            this.player = player;
            animate(new Random().nextInt(10) + 100);
        }

        public Player getPlayer() {
            return player;
        }
    }

    private List<PlayerSprite> playerSprites = new ArrayList<PlayerSprite>();

    public void movePlayer(Player player, Field toField) {
        PlayerSprite playerSprite = getPlayerSpriteForPlayer(player);

        int fieldX = toField.getX() + new Random().nextInt(20) - 10;
        int fieldY = toField.getY() + new Random().nextInt(20) - 10;

        // playerSprite.setPosition(fieldPosition.x, fieldPosition.y);
        playerSprite.registerEntityModifier(new MoveModifier(3, playerSprite.getX(), fieldX, playerSprite.getY(),
                fieldY));
    }

    @Override
    public Engine onLoadEngine() {

        final Display display = getWindowManager().getDefaultDisplay();
        int cameraWidth = display.getWidth();
        int cameraHeight = display.getHeight();

        Camera camera = new Camera(0, 0, 800, 480);
        return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(cameraWidth,
                cameraHeight), camera));
    }

    @Override
    public void onLoadResources() {
        String boardImage = ((CoffeePartyApplication) getApplication()).getGameState().getMap().getBoardImage();
                
        this.bitmapTextureAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR);
        this.backgroundTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, this,
                boardImage, 0, 0);
        this.playerSpriteTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.bitmapTextureAtlas,
                this, "face_box_tiled.png", 132, 180, 2, 1);

        this.mEngine.getTextureManager().loadTexture(this.bitmapTextureAtlas);
    }

    @Override
    public Scene onLoadScene() {
        reset();

        this.mEngine.registerUpdateHandler(new FPSLogger());
        
        final Scene scene = new Scene();
        // scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
        scene.setBackground(new SpriteBackground(new Sprite(0, 0, backgroundTexture)));

        createPlayers(scene);
        placePlayers();

        return scene;
    }

    private void reset()
    {
        playerSprites.clear();
    }
    
    private void createPlayers(Scene scene) {
        Game gameState = ((CoffeePartyApplication) getApplication()).getGameState();

        for (Player player : gameState.getPlayers()) {
            Field fieldOfPlayer = gameState.getMap().getFieldOfPlayer(player);
            
            PlayerSprite playerSprite = new PlayerSprite(player, fieldOfPlayer.getX(), fieldOfPlayer.getY());
            playerSprites.add(playerSprite);
            scene.attachChild(playerSprite);
        }
    }

    private void placePlayers() {
        Game gameState = ((CoffeePartyApplication) getApplication()).getGameState();

        for (Player player : gameState.getPlayers()) {
            movePlayer(player, gameState.getMap().getFieldOfPlayer(player));
        }
    }

    private PlayerSprite getPlayerSpriteForPlayer(Player player) {
        for (PlayerSprite playerSprite : playerSprites) {
            if (playerSprite.getPlayer() == player) {
                return playerSprite;
            }
        }

        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN)
        {
            return false;
        }
        
        Game gameState = ((CoffeePartyApplication) getApplication()).getGameState();

        for (Player player : gameState.getPlayers()) {
            player.setPosition(player.getPosition() + 1);
            movePlayer(player, gameState.getMap().getFieldOfPlayer(player));
        }
        
        return true;
    }

    @Override
    public void onLoadComplete() {

    }
}
