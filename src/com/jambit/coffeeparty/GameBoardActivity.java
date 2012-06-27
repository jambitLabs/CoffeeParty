package com.jambit.coffeeparty;

import java.util.ArrayList;
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
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import com.jambit.coffeeparty.model.Field;
import com.jambit.coffeeparty.model.Game;
import com.jambit.coffeeparty.model.Map;
import com.jambit.coffeeparty.model.MinigameIdentifier;
import com.jambit.coffeeparty.model.Player;

public class GameBoardActivity extends BaseGameActivity {

    private final static int DICE_ROLLED = 110;
    private final static int MINIGAME_FINISHED = 111;
    private final static int RESULT_DISPLAYED = 112;
    private final static int END_OF_GAME = 113;
    
    private final static int MAX_SCORE_FIELD_POINTS = 10;
    
    private boolean mDiceEnabled = false;
    private List<MinigameIdentifier> mMinigames = new ArrayList<MinigameIdentifier>();
    
    private BitmapTextureAtlas bitmapTextureAtlas;
    private TextureRegion backgroundTexture;
    private TiledTextureRegion playerSpriteTexture;

    private Font playerNameFont;

    private class PlayerSprite extends Entity {
        private final Player player;

        private AnimatedSprite figure;
        private Text figureNameText;

        PlayerSprite(Player player, int x, int y) {
            super(x, y);
            this.player = player;

            this.figure = new AnimatedSprite(-playerSpriteTexture.getWidth() / 2, -playerSpriteTexture.getHeight() / 2,
                    playerSpriteTexture);
            this.figure.animate(new Random().nextInt(10) + 100);
            this.attachChild(figure);

            this.figureNameText = new Text(0, -20, playerNameFont, player.getName());
            this.attachChild(figureNameText);
        }

        public Player getPlayer() {
            return player;
        }
    }
    
    @Override
    protected void onCreate(final Bundle pSavedInstanceState) {
        // find all "real" minigames
        for(MinigameIdentifier id : MinigameIdentifier.values()){
            if(id != MinigameIdentifier.POINTS && id != MinigameIdentifier.RANDOM_MINIGAME)
                mMinigames.add(id);
        }
        super.onCreate(pSavedInstanceState);
    }

    private List<PlayerSprite> playerSprites = new ArrayList<PlayerSprite>();

    private void movePlayer(Player player, int oldPosition, int newPosition) {
        Map gameMap = getGame().getMap();
        PlayerSprite playerSprite = getPlayerSpriteForPlayer(player);
        
        if (oldPosition == newPosition) {
            Field field = gameMap.getFieldForPosition(newPosition);
            playerSprite.setPosition(field.getX(), field.getY());
        }
        else
        {
            int wayPoints = newPosition - oldPosition + 1;
            Path path = new Path(wayPoints);
            
            for (int position = oldPosition; position <= newPosition; position++)
            {
                Field field = gameMap.getFieldForPosition(position);
                path.to(field.getX(), field.getY());
            }
            
            playerSprite.registerEntityModifier(new PathModifier(wayPoints * 1.0f, path, null, new IPathModifierListener() {
                @Override
                public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
                }

                @Override
                public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
                }

                @Override
                public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
                }

                @Override
                public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {
                    doFieldAction();
                }
            }));
        }
    }
    
    private void doFieldAction(){
        Random r = new Random(System.currentTimeMillis());
        Intent minigameIntent = new Intent(this, MinigameStartActivity.class);
        MinigameIdentifier currentFieldType = getGame().getMap().getFieldOfPlayer(getGame().getCurrentPlayer()).getType();
        switch(currentFieldType){
            case POINTS:
                Player currentPlayer = getGame().getCurrentPlayer();
                boolean negativePoints = r.nextBoolean();
                int points = r.nextInt(MAX_SCORE_FIELD_POINTS + 1);
                if(negativePoints)
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
        return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(cameraWidth,
                cameraHeight), camera));
    }

    @Override
    public void onLoadResources() {
        String boardImage = getGame().getMap().getBoardImage();
                
        this.bitmapTextureAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR);
        this.backgroundTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.bitmapTextureAtlas, this,
                boardImage, 0, 0);
        this.playerSpriteTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.bitmapTextureAtlas,
                this, "face_box_tiled.png", 132, 180, 2, 1);

        this.mEngine.getTextureManager().loadTexture(this.bitmapTextureAtlas);

        BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.playerNameFont = new Font(fontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 20, true,
                Color.BLACK);

        this.mEngine.getTextureManager().loadTexture(fontTexture);
        this.mEngine.getFontManager().loadFont(this.playerNameFont);
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
           
        mDiceEnabled = true;
        return scene;
    }

    private void reset()
    {
        playerSprites.clear();
    }
    
    private Game getGame()
    {
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

    private void placePlayers() {
        Game gameState = getGame();

        for (Player player : gameState.getPlayers()) {
            movePlayer(player, 0, 0);
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
        if (event.getAction() == MotionEvent.ACTION_UP && mDiceEnabled)
        {
            mDiceEnabled = false;
            Intent intent = new Intent(this, DiceRollActivity.class);
            startActivityForResult(intent, DICE_ROLLED);
            return true;
        }
        return false;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Game gameState = getGame();
        Player currentPlayer = gameState.getCurrentPlayer();
        
        if(requestCode == DICE_ROLLED){
            int diceResult = data.getExtras().getInt(getString(R.string.dice_result));
            int oldPosition = currentPlayer.getPosition();
            currentPlayer.setPosition(currentPlayer.getPosition() + diceResult);
            int newPosition = currentPlayer.getPosition();
            movePlayer(currentPlayer, oldPosition, newPosition);
        }
        else if(requestCode == MINIGAME_FINISHED){
            // TODO: maybe disable return button in minigames
            int points = 0;
            if(data != null){
                points = data.getExtras().getInt(getString(R.string.game_result));
                currentPlayer.changeScoreBy(points);
            }
            else
                Log.d("GAME_BOARD", "Return button pressed during minigame? Zero points for player " + currentPlayer.getName());
            
            Log.i("GAME_BOARD", "New score for player " + currentPlayer.getName() + ": " + currentPlayer.getScore());
            Intent resultIntent = new Intent(this, MinigameResultActivity.class);
            resultIntent.putExtra(getString(R.string.playerkey), currentPlayer);
            resultIntent.putExtra(getString(R.string.pointskey), points);
            startActivityForResult(resultIntent, RESULT_DISPLAYED);
        }
        else if(requestCode == RESULT_DISPLAYED){
            if(gameState.getRoundsPlayed() < gameState.getTotalRounds()){
                gameState.nextPlayer();
                mDiceEnabled = true;
            }
            else{
                Log.d("GAME_BOARD", "End of game");
                Intent resultIntent = new Intent(this, FinalResultsActivity.class);
//                resultIntent.putExtra(getString(R.string.playerkey), currentPlayer);
                startActivityForResult(resultIntent, END_OF_GAME);
            }
        }
        else if(requestCode == END_OF_GAME){
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onLoadComplete() {

    }
}
