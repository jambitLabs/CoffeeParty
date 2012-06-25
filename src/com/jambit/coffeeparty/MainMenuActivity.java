package com.jambit.coffeeparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainMenuActivity extends Activity {

    private final static int NUM_PLAYERS_SET = 0;
    private final static int SET_AVATAR = 1;
    private final static int GAME_BOARD = 3;

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
            for(Object name : (Object[])data.getExtras().get("players"))
                Log.d("MAIN_MENU", "Player " + name);
            
            Intent avatarIntent = new Intent(this, AvatarActivity.class);
            avatarIntent.putExtra(AvatarActivity.PLAYERNAME_EXTRA, "TestPlayer");
            startActivityForResult(avatarIntent, SET_AVATAR);
        } else if (requestCode == SET_AVATAR) {
            showBoard();
        } else if (requestCode == GAME_BOARD) {
            // Nothing to do now
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
