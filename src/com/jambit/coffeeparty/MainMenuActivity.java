package com.jambit.coffeeparty;

import com.jambit.coffeeparty.model.Player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

    public void showBoard() {
        Intent intent = new Intent(this, GameBoardActivity.class);
        startActivityForResult(intent, GAME_BOARD);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == NUM_PLAYERS_SET) {
            for(Object player : (Object[])data.getExtras().get("players")){
                Log.d("MAIN_MENU", player.toString());
                ((CoffeePartyApplication)getApplication()).getGameState().getPlayers().add((Player)player);
            }
            // player data entered and added to the game. Proceed to board
            showBoard();
            
        } else if (requestCode == GAME_BOARD) {
            // Nothing to do now
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
