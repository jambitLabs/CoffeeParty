package com.jambit.coffeeparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainMenuActivity extends Activity {
    
    private final static int NUM_PLAYERS_SET = 0;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void onStartNewGame(View v){
        Intent intent = new Intent(this, NumberOfPlayersActivity.class);
        startActivityForResult(intent, NUM_PLAYERS_SET);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NUM_PLAYERS_SET) {
            for(Object name : (Object[])data.getExtras().get("players"))
                Log.d("NUM_PLAYERS", "Player " + name);
            
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}