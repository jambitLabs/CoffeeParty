package com.jambit.coffeeparty;

import java.io.InputStream;

import javax.xml.xpath.XPathExpressionException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jambit.coffeeparty.model.MinigameIdentifier;
import com.jambit.coffeeparty.model.Player;

public class MainMenuActivity extends Activity {

    private final static int NUM_PLAYERS_SET = 0;
    private final static int GAME_SETTINGS = 1;
    private final static int GAME_BOARD = 2;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onStartNewGame(View v) {
        Intent intent = new Intent(this, NumberOfPlayersActivity.class);
        startActivityForResult(intent, NUM_PLAYERS_SET);
    }
    
    public void onStartMinigame(View v) {
    	Intent intent = new Intent(this, MinigameStartActivity.class);
    	intent.putExtra(getString(R.string.minigameidkey), MinigameIdentifier.MINI_GAME_CATCHTHEFLY);
        startActivity(intent);
    }

    private void showBoard() {
        Intent intent = new Intent(this, GameBoardActivity.class);
        startActivityForResult(intent, GAME_BOARD);
    }
    
    private void showSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, GAME_SETTINGS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == NUM_PLAYERS_SET) {
            for(Object player : (Object[])data.getExtras().get("players")){
                Log.d("MAIN_MENU", player.toString());
                ((CoffeePartyApplication)getApplication()).getGameState().getPlayers().add((Player)player);
            }
            showSettings();
        }
        else if(requestCode == GAME_SETTINGS){
            int numRounds = data.getExtras().getInt("numRounds");
            int mapId = data.getExtras().getInt("mapId");
            InputStream mapXml = this.getResources().openRawResource(mapId);
            try {
                ((CoffeePartyApplication)getApplication()).getGameState().startGame(numRounds, mapXml);
            } catch (XPathExpressionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // player data and settings entered. Proceed to board
            showBoard();
        }
        else if (requestCode == GAME_BOARD) {
            // Nothing to do now
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
}
