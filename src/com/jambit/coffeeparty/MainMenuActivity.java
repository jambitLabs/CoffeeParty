package com.jambit.coffeeparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainMenuActivity extends Activity {

    private final static int NUM_PLAYERS_SET = 0;
    private final static int SET_AVATAR = 1;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == NUM_PLAYERS_SET) {
            int numPlayers = data.getIntExtra("numPlayers", 2);
            Log.d("mainMenu", "Num Players set to " + numPlayers);
            Intent avatarIntent = new Intent(this, AvatarActivity.class);
            avatarIntent.putExtra(AvatarActivity.PLAYERNAME_EXTRA, "TestPlayer");
            startActivityForResult(avatarIntent, SET_AVATAR);
        } else if (requestCode == SET_AVATAR) {
            Log.i("mainMenu", "AvatarActivity returned: " + data.getLongExtra(AvatarActivity.SELECTED_AVATAR_EXTRA, 0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}