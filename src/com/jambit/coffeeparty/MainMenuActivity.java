package com.jambit.coffeeparty;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.xpath.XPathExpressionException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import com.jambit.coffeeparty.db.HighscoreDataSource;
import com.jambit.coffeeparty.model.MinigameIdentifier;
import com.jambit.coffeeparty.model.Player;

public class MainMenuActivity extends Activity {

    private static final String DEVELOPER = "Developer";
    private final static int NUM_PLAYERS_SET = 0;
    private final static int GAME_SETTINGS = 1;
    private final static int GAME_BOARD = 2;
    private static final int MINIGAME_REQUESTCODE = 3;

    private List<Player> mPlayers = new ArrayList<Player>();

    private Spinner startDirectGameSpinner;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // startDirectGameSpinner = (Spinner) findViewById(R.id.startDirectGame);
        //
        // ArrayAdapter<MinigameIdentifier> adapter = new ArrayAdapter<MinigameIdentifier>(this,
        // android.R.layout.simple_spinner_item);
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //
        // for (MinigameIdentifier minigameIdentifier : MinigameIdentifier.values()) {
        // if (minigameIdentifier != MinigameIdentifier.RANDOM_MINIGAME)
        // adapter.add(minigameIdentifier);
        // }
        //
        // startDirectGameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        //
        // @Override
        // public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // MinigameIdentifier minigameIdentifier = (MinigameIdentifier) parent.getItemAtPosition(pos);
        // onStartGameDirectly(minigameIdentifier);
        // }
        //
        // @Override
        // public void onNothingSelected(AdapterView<?> parent) {
        // }
        // });
        //
        // startDirectGameSpinner.setAdapter(adapter);
        // startDirectGameSpinner.setSelection(adapter.getPosition(MinigameIdentifier.POINTS));
    }

    public void onStartNewGame(View v) {
        Intent intent = new Intent(this, NumberOfPlayersActivity.class);
        startActivityForResult(intent, NUM_PLAYERS_SET);
    }

    public void onStartGameDirectly(MinigameIdentifier minigame) {
        if (minigame == MinigameIdentifier.POINTS || minigame == MinigameIdentifier.RANDOM_MINIGAME)
            return;

        Intent intent = new Intent(this, MinigameStartActivity.class);
        intent.putExtra(getString(R.string.minigameidkey), minigame);
        intent.putExtra(getString(R.string.playernamekey), DEVELOPER);
        startActivityForResult(intent, MINIGAME_REQUESTCODE);
    }

    private void showBoard() {
        Intent intent = new Intent(this, GameBoardActivity.class);
        startActivityForResult(intent, GAME_BOARD);
    }

    private void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, GAME_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == NUM_PLAYERS_SET) {
                for (Object player : (Object[]) data.getExtras().get("players")) {
                    Log.d("MAIN_MENU", player.toString());
                    mPlayers.add((Player) player);
                }
                showSettings();
            } else if (requestCode == GAME_SETTINGS) {
                int numRounds = data.getExtras().getInt("numRounds");
                int mapId = data.getExtras().getInt("mapId");
                InputStream mapXml = this.getResources().openRawResource(mapId);
                try {
                    ((CoffeePartyApplication) getApplication()).getGameState().startGame(mPlayers, numRounds, mapXml);
                } catch (XPathExpressionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // player data and settings entered. Proceed to board
                showBoard();
            } else if (requestCode == GAME_BOARD) {
                mPlayers.clear();
            } else if (requestCode == MINIGAME_REQUESTCODE) {
                int points = data.getExtras().getInt(getString(R.string.game_result));
                Intent resultIntent = new Intent(this, MinigameResultActivity.class);
                resultIntent.putExtra(getString(R.string.playerkey),
                                      new Player(DEVELOPER,
                                                 ((BitmapDrawable) this.getResources()
                                                                       .getDrawable(R.drawable.droid_green)).getBitmap()));
                resultIntent.putExtra(getString(R.string.pointskey), points);
                startActivity(resultIntent);
            }
        }
        else if (requestCode == GAME_BOARD) {
            mPlayers.clear();
        } 
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onShowHighscores(View v) {
        Intent intent = new Intent(this, DisplayHighscoreActivity.class);
        startActivity(intent);
    }

    public void onAddRandomPlayer() {
    	ArrayList<Player> ps = new ArrayList<Player>();
    	for (int i = 0; i < 3; i++) {
    		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.droid_red); 
        	Player p = new Player ("(Random)", bm);
        	Random r = new Random();
        	p.changeScoreBy(r.nextInt(50));
        	ps.add(p);
    	}
    	
    	HighscoreDataSource dataSource = new HighscoreDataSource(this);
    	dataSource.openForWriting();
    	HashMap<Player, Integer> hashMap = dataSource.storeHighscore(ps);
    	dataSource.close();
    	
    	for (Player p : hashMap.keySet()) {
    		Log.d("Player result", "Rank for " + p.getName() + ": " + hashMap.get(p));
    	}
    }

    public void onDebugButton(View v) {
        this.openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.debug_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        MinigameIdentifier minigameIdentifier;
        switch (item.getItemId()) {
        case R.id.addRandomPlayerButton:
            onAddRandomPlayer();
            break;
        case R.id.MiniGame_BallMaze:
            minigameIdentifier = MinigameIdentifier.MINI_GAME_BALLMAZE;
            onStartGameDirectly(minigameIdentifier);
            break;
        case R.id.MiniGame_Catch:
            minigameIdentifier = MinigameIdentifier.MINI_GAME_CATCHTHEFLY;
            onStartGameDirectly(minigameIdentifier);
            break;
        case R.id.MiniGame_FallingBeans:
            minigameIdentifier = MinigameIdentifier.MINI_GAME_FALLINGBEANS;
            onStartGameDirectly(minigameIdentifier);
            break;
        case R.id.MiniGame_WhackAMole:
            minigameIdentifier = MinigameIdentifier.MINI_GAME_WHACKAMOLE;
            onStartGameDirectly(minigameIdentifier);
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
