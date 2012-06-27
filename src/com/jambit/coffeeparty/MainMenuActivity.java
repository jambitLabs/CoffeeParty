package com.jambit.coffeeparty;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jambit.coffeeparty.model.MinigameIdentifier;
import com.jambit.coffeeparty.model.Player;

public class MainMenuActivity extends Activity {

    private final static int NUM_PLAYERS_SET = 0;
    private final static int GAME_SETTINGS = 1;
    private final static int GAME_BOARD = 2;
    
    private List<Player> mPlayers = new ArrayList<Player>();

    private Spinner startDirectGameSpinner;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        startDirectGameSpinner = (Spinner) findViewById(R.id.startDirectGame);

        ArrayAdapter<MinigameIdentifier> adapter = new ArrayAdapter<MinigameIdentifier>(this,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (MinigameIdentifier minigameIdentifier : MinigameIdentifier.values()) {
            adapter.add(minigameIdentifier);
        }

        startDirectGameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                MinigameIdentifier minigameIdentifier = (MinigameIdentifier) parent.getItemAtPosition(pos);
                onStartGameDirectly(minigameIdentifier);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        startDirectGameSpinner.setAdapter(adapter);
        startDirectGameSpinner.setSelection(adapter.getPosition(MinigameIdentifier.POINTS));
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
        intent.putExtra(getString(R.string.playernamekey), "Developer");
        startActivity(intent);
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

        if (requestCode == NUM_PLAYERS_SET) {
            for(Object player : (Object[])data.getExtras().get("players")){
                Log.d("MAIN_MENU", player.toString());
                mPlayers.add((Player)player);
            }
            showSettings();
        }
        else if(requestCode == GAME_SETTINGS){
            if(data != null){
                int numRounds = data.getExtras().getInt("numRounds");
                int mapId = data.getExtras().getInt("mapId");
                InputStream mapXml = this.getResources().openRawResource(mapId);
                try {
                    ((CoffeePartyApplication)getApplication()).getGameState().startGame(mPlayers, numRounds, mapXml);
                } catch (XPathExpressionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // player data and settings entered. Proceed to board
                showBoard();
            }
        }
        else if (requestCode == GAME_BOARD) {
            // Nothing to do now
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
}
