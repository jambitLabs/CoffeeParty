package com.jambit.coffeeparty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import com.jambit.coffeeparty.db.HighscoreDataSource;
import com.jambit.coffeeparty.model.Field;
import com.jambit.coffeeparty.model.Game;
import com.jambit.coffeeparty.model.Map;
import com.jambit.coffeeparty.model.MinigameIdentifier;
import com.jambit.coffeeparty.model.Player;

public class GameBoardActivity extends BaseGameActivity {

    public final static int DICE_ROLLED = 110;
    private final static int MINIGAME_FINISHED = 111;
    private final static int RESULT_DISPLAYED = 112;
    private final static int END_OF_GAME = 113;

    private final static int MAX_SCORE_FIELD_POINTS = 10;

    private List<MinigameIdentifier> mMinigames = new ArrayList<MinigameIdentifier>();

    private TextureRegion backgroundTexture;
    private TextureRegion jambitBeanTexture;
    private TiledTextureRegion playerSpriteTexture;

    private Font playerNameFont;

    private SoundPool soundPool;
    private int footStepsSoundID;

    private final Random r = new Random(System.currentTimeMillis());

    private class PlayerSprite extends Entity {
        private final Player player;

        private AnimatedSprite figure;
        private Text figureNameText;
        private ChangeableText playerPointsText;
        private Sprite bean;

        PlayerSprite(Player player, int x, int y) {
            super(x, y);
            this.player = player;

            this.figure = new AnimatedSprite(-playerSpriteTexture.getWidth() / 2,
                                             -playerSpriteTexture.getHeight() / 2,
                                             playerSpriteTexture);
            this.figure.animate(r.nextInt(10) + 100);
            this.attachChild(figure);

            this.figureNameText = new Text(0, -20, playerNameFont, player.getName());
            this.attachChild(figureNameText);

            this.playerPointsText = new ChangeableText(25, 0, playerNameFont, this.player.getScore() + "       ");
            this.attachChild(playerPointsText);

            this.bean = new Sprite(5, 5, jambitBeanTexture);
            this.bean.setSize(16, 16);
            this.attachChild(bean);
        }

        public Player getPlayer() {
            return player;
        }

        public void updatePoints() {
            playerPointsText.setText(Integer.toString(this.player.getScore()));
        }
    }

    @Override
    protected void onCreate(final Bundle pSavedInstanceState) {
        // find all "real" minigames
        for (MinigameIdentifier id : MinigameIdentifier.values()) {
            if (id != MinigameIdentifier.POINTS && id != MinigameIdentifier.RANDOM_MINIGAME)
                mMinigames.add(id);
        }
        super.onCreate(pSavedInstanceState);
    }

    private List<PlayerSprite> playerSprites = new ArrayList<PlayerSprite>();
    private List<TextureRegion> fieldTextures = new ArrayList<TextureRegion>();
    private Scene mainScene;
    private ReadyToDiceOverlay readyToDiceOverlay;
    private int streamID;

    private void movePlayer(Player player, int oldPosition, int newPosition) {
        Map gameMap = getGame().getMap();
        PlayerSprite playerSprite = getPlayerSpriteForPlayer(player);

        if (oldPosition == newPosition) {
            Field field = gameMap.getFieldForPosition(newPosition);
            playerSprite.setPosition(field.getX(), field.getY());
        } else {
            int wayPoints = newPosition - oldPosition + 1;
            Path path = new Path(wayPoints);
            startFootSteps();
            for (int position = oldPosition; position <= newPosition; position++) {

                Field field = gameMap.getFieldForPosition(position);
                path.to(field.getX(), field.getY());
            }
            stopFootSteps();

            playerSprite.registerEntityModifier(new PathModifier(wayPoints * 1.0f,
                                                                 path,
                                                                 null,
                                                                 new IPathModifierListener() {
                                                                     @Override
                                                                     public void onPathStarted(final PathModifier pPathModifier,
                                                                                               final IEntity pEntity) {
                                                                     }

                                                                     @Override
                                                                     public void onPathWaypointStarted(final PathModifier pPathModifier,
                                                                                                       final IEntity pEntity,
                                                                                                       final int pWaypointIndex) {
                                                                     }

                                                                     @Override
                                                                     public void onPathWaypointFinished(final PathModifier pPathModifier,
                                                                                                        final IEntity pEntity,
                                                                                                        final int pWaypointIndex) {
                                                                     }

                                                                     @Override
                                                                     public void onPathFinished(final PathModifier pPathModifier,
                                                                                                final IEntity pEntity) {
                                                                         doFieldAction();
                                                                     }
                                                                 }));
        }
    }

    private void startFootSteps() {
        AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        /* Play the sound with the correct volume */
        streamID = soundPool.play(footStepsSoundID, volume * 0.1f, volume * 0.1f, 1, 0, 1f);

    }

    private void stopFootSteps() {
        soundPool.stop(streamID);
    }

    private void doFieldAction() {
        Intent minigameIntent = new Intent(this, MinigameStartActivity.class);
        MinigameIdentifier currentFieldType = getGame().getMap()
                                                       .getFieldOfPlayer(getGame().getCurrentPlayer())
                                                       .getType();
        switch (currentFieldType) {
        case POINTS:
            Player currentPlayer = getGame().getCurrentPlayer();
            boolean negativePoints = r.nextBoolean();
            int points = r.nextInt(MAX_SCORE_FIELD_POINTS + 1);
            if (negativePoints)
                points = 0 - points;
            currentPlayer.changeScoreBy(points);
            Intent resultIntent = new Intent(this, MinigameResultActivity.class);
            resultIntent.putExtra(getString(R.string.playerkey), currentPlayer);
            resultIntent.putExtra(getString(R.string.pointskey), points);
            startActivityForResult(resultIntent, RESULT_DISPLAYED);
            break;
        case RANDOM_MINIGAME:
            int gameIndex = r.nextInt(mMinigames.size());
            minigameIntent.putExtra(getString(R.string.minigameidkey), mMinigames.get(gameIndex));
            startActivityForResult(minigameIntent, MINIGAME_FINISHED);
            break;
        default:
            minigameIntent.putExtra(getString(R.string.minigameidkey), currentFieldType);
            startActivityForResult(minigameIntent, MINIGAME_FINISHED);
            break;
        }
    }

    @Override
    public Engine onLoadEngine() {

        final Display display = getWindowManager().getDefaultDisplay();
        int cameraWidth = display.getWidth();
        int cameraHeight = display.getHeight();

        Camera camera = new Camera(0, 0, 800, 480);
        return new Engine(new EngineOptions(true,
                                            ScreenOrientation.LANDSCAPE,
                                            new RatioResolutionPolicy(cameraWidth, cameraHeight),
                                            camera));
    }

    @Override
    public void onLoadResources() {
        String boardImage = getGame().getMap().getBoardImage();

        BitmapTextureAtlas backgroundTextureAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR);
        BitmapTextureAtlas playerTextureAtlas = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR);
        BitmapTextureAtlas jambitTextureAtlas = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR);

        this.backgroundTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundTextureAtlas,
                                                                                        this,
                                                                                        boardImage,
                                                                                        0,
                                                                                        0);
        this.playerSpriteTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(playerTextureAtlas,
                                                                                               this,
                                                                                               "face_box_tiled.png",
                                                                                               0,
                                                                                               0,
                                                                                               2,
                                                                                               1);
        this.jambitBeanTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(jambitTextureAtlas,
                                                                                        this,
                                                                                        "jambitbean.png",
                                                                                        0,
                                                                                        0);

        this.mEngine.getTextureManager().loadTexture(backgroundTextureAtlas);
        this.mEngine.getTextureManager().loadTexture(playerTextureAtlas);
        this.mEngine.getTextureManager().loadTexture(jambitTextureAtlas);

        BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.playerNameFont = new Font(fontTexture,
                                       Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
                                       20,
                                       true,
                                       Color.BLACK);

        this.mEngine.getTextureManager().loadTexture(fontTexture);
        this.mEngine.getFontManager().loadFont(this.playerNameFont);
        
        Map map = getGame().getMap();
        for(Field field : map.getBoard()){
            BitmapTextureAtlas iconAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            TextureRegion iconTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(iconAtlas, this, 
                    field.getIconName(), field.getX() + map.getFieldIconOffsetX(), field.getY() + map.getFieldIconOffsetY());
            this.mEngine.getTextureManager().loadTexture(iconAtlas);
            fieldTextures.add(iconTexture);
        }

        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);

        footStepsSoundID = soundPool.load(this, R.raw.footsteps, 1);

    }

    @Override
    public Scene onLoadScene() {
        reset();

        this.mEngine.registerUpdateHandler(new FPSLogger());

        mainScene = new Scene();
        // scene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
        mainScene.setBackground(new SpriteBackground(new Sprite(0, 0, backgroundTexture)));

        createPlayers(mainScene);
        
        for(TextureRegion iconTexture : fieldTextures){
            Sprite iconSprite = new Sprite(iconTexture.getTexturePositionX(), iconTexture.getTexturePositionY(), iconTexture);
            mainScene.attachChild(iconSprite);
        }

        mainScene.setTouchAreaBindingEnabled(true);
        readyToDiceOverlay = new ReadyToDiceOverlay(this, mainScene);
        readyToDiceOverlay.setPosition(-50, 280);
        mainScene.attachChild(readyToDiceOverlay);

        return mainScene;
    }

    private void reset() {
        playerSprites.clear();
    }

    private Game getGame() {
        return ((CoffeePartyApplication) getApplication()).getGameState();
    }

    private void createPlayers(Scene scene) {
        Game gameState = getGame();

        for (Player player : gameState.getPlayers()) {
            Field fieldOfPlayer = gameState.getMap().getFieldOfPlayer(player);

            PlayerSprite playerSprite = new PlayerSprite(player, fieldOfPlayer.getX(), fieldOfPlayer.getY());
            playerSprites.add(playerSprite);
            scene.attachChild(playerSprite);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Game gameState = getGame();
        Player currentPlayer = gameState.getCurrentPlayer();

        if (requestCode == DICE_ROLLED) {
        	int diceResult;
        	if (resultCode != Activity.RESULT_OK) {
        		diceResult = 1; //in case a player presses the back button
        	} else {
        		diceResult = data.getExtras().getInt(getString(R.string.dice_result));	
        	}
            int oldPosition = currentPlayer.getPosition();
            currentPlayer.setPosition(currentPlayer.getPosition() + diceResult);
            int newPosition = currentPlayer.getPosition();
            movePlayer(currentPlayer, oldPosition, newPosition);
        } else if (requestCode == MINIGAME_FINISHED) {
            int points = 0;
            if (data != null) {
                points = data.getExtras().getInt(getString(R.string.game_result));
                currentPlayer.changeScoreBy(points);
            } else
                Log.d("GAME_BOARD",
                      "Return button pressed during minigame? Zero points for player " + currentPlayer.getName());

            Log.i("GAME_BOARD", "New score for player " + currentPlayer.getName() + ": " + currentPlayer.getScore());
            Intent resultIntent = new Intent(this, MinigameResultActivity.class);
            resultIntent.putExtra(getString(R.string.playerkey), currentPlayer);
            resultIntent.putExtra(getString(R.string.pointskey), points);
            startActivityForResult(resultIntent, RESULT_DISPLAYED);
        } else if (requestCode == RESULT_DISPLAYED) {
            if (gameState.getRoundsPlayed() < gameState.getTotalRounds()) {
                gameState.nextPlayer();
                showReadyToDiceOverlay();
            } else {
                Log.d("GAME_BOARD", "End of game");
                // already sort here so the returned rank is not changed
                
                Game game = getGame();
                Collections.sort(game.getPlayers());
                Collections.reverse(game.getPlayers());
                
                HighscoreDataSource dataSource = new HighscoreDataSource(this);
                dataSource.openForWriting();
                for (Player p : game.getPlayers()) {
                	int rank = dataSource.addPlayerToScores(p);
                	p.setRank(rank);
                }
                dataSource.close();
                Intent resultIntent = new Intent(this, FinalResultsActivity.class);
                startActivityForResult(resultIntent, END_OF_GAME);
            }
        } else if (requestCode == END_OF_GAME) {
            setResult(RESULT_OK);
            finish();
        }

        getPlayerSpriteForPlayer(currentPlayer).updatePoints();
    }
    
    private HashMap<Player, Integer> storeResultsInDatabaseForRanks() {
    	HighscoreDataSource dataSource = new HighscoreDataSource(this);
    	dataSource.openForWriting();
    	HashMap<Player, Integer> hashMap = dataSource.storeHighscore(getGame().getPlayers());
    	dataSource.close();
    	return hashMap;
    }

    @Override
    public void onLoadComplete() {
        showReadyToDiceOverlay();
    }

    private void showReadyToDiceOverlay() {
        String name = getGame().getCurrentPlayer().getName();
        readyToDiceOverlay.setPlayerNameText(name);
        readyToDiceOverlay.setVisible(true);
    }
}
