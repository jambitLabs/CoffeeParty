package com.jambit.coffeeparty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.jambit.coffeeparty.model.Field;
import com.jambit.coffeeparty.model.Game;
import com.jambit.coffeeparty.model.Player;

public class GameBoardActivity extends BaseGameActivity {

    private static int BOARDWIDTH = 720;
    private static int BOARDHEIGHT = 480;

    private BitmapTextureAtlas bitmapTextureAtlas;
    private TextureRegion fieldSpriteTexture;
    private TiledTextureRegion playerSpriteTexture;

    private class FieldSprite extends Sprite {
        private final Field field;

        FieldSprite(Field field, int x, int y) {
            super(x, y, fieldSpriteTexture);
            this.field = field;
        }

        public Field getField() {
            return field;
        }
    }

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

    // TODO: This is complicated. Better use maps...
    private List<FieldSprite> fieldSprites = new ArrayList<FieldSprite>();
    private List<PlayerSprite> playerSprites = new ArrayList<PlayerSprite>();

    public void movePlayer(Player player, Field toField) {
        FieldSprite fieldSprite = getFieldSpriteForField(toField);
        PlayerSprite playerSprite = getPlayerSpriteForPlayer(player);

        playerSprite.setPosition(fieldSprite.getX() + 5, fieldSprite.getY() + 5);
    }

    @Override
    public Engine onLoadEngine() {
        Camera camera = new Camera(0, 0, BOARDWIDTH, BOARDHEIGHT);
        return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(BOARDWIDTH,
                BOARDHEIGHT), camera));
    }

    @Override
    public void onLoadResources() {
        this.bitmapTextureAtlas = new BitmapTextureAtlas(512, 256, TextureOptions.BILINEAR);
        this.fieldSpriteTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, this,
                "face_box.png", 0, 0);
        this.playerSpriteTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.bitmapTextureAtlas,
                this, "face_box_tiled.png", 132, 180, 2, 1);

        this.mEngine.getTextureManager().loadTexture(this.bitmapTextureAtlas);
    }

    @Override
    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene();
        scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));

        createFields(scene);
        createPlayers(scene);
        placePlayers();

        return scene;
    }

    private void createFields(Scene scene) {
        Game gameState = ((CoffeePartyApplication) getApplication()).getGameState();

        int fieldsPerLine = 6;

        int lineStartX = 10;
        int lineStartY = 10;
        int fieldWithinLine = 0;
        for (Field field : gameState.getBoard()) {
            FieldSprite fieldSprite = new FieldSprite(field, lineStartX, lineStartY);
            fieldSprites.add(fieldSprite);
            scene.attachChild(fieldSprite);

            lineStartX += 100;
            if (++fieldWithinLine > fieldsPerLine) {
                lineStartX = 10;
                lineStartY += 100;
                fieldWithinLine = 0;
            }
        }
    }

    private void createPlayers(Scene scene) {
        Game gameState = ((CoffeePartyApplication) getApplication()).getGameState();

        for (Player player : gameState.getPlayers()) {
            PlayerSprite playerSprite = new PlayerSprite(player, 0, 0);
            playerSprites.add(playerSprite);
            scene.attachChild(playerSprite);
        }
    }

    private void placePlayers() {
        Game gameState = ((CoffeePartyApplication) getApplication()).getGameState();

        for (Player player : gameState.getPlayers()) {
            movePlayer(player, getFieldForPosition(player.getPosition()));
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

    private FieldSprite getFieldSpriteForField(Field field) {
        for (FieldSprite fieldSprite : fieldSprites) {
            if (fieldSprite.getField() == field) {
                return fieldSprite;
            }
        }

        return null;
    }

    private Field getFieldForPosition(int position) {
        if (position >= fieldSprites.size()) {
            return null;
        }

        return fieldSprites.get(position).getField();
    }

    @Override
    public void onLoadComplete() {

    }
}