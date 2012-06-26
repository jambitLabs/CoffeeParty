package com.jambit.coffeeparty;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.jambit.coffeeparty.model.*;

public class MinigameStartActivity extends Activity {
	private final int REQUEST_CODE_GAME = 123;
	
	private Player currentPlayer;
	private MinigameIdentifier minigameID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		minigameID = (MinigameIdentifier) getIntent().getExtras().get(getString(R.string.minigameidkey));
		currentPlayer = ((CoffeePartyApplication) getApplication()).getGameState().getCurrentPlayer();
		
		setContentView(R.layout.minigame_start);
		((TextView) findViewById(R.id.minigameStartTitle)).setText(nameForMinigame(minigameID));
		((TextView) findViewById(R.id.minigameStartDescription)).setText(descriptionForMinigame(minigameID));
		((TextView) findViewById(R.id.minigameStartPlayerName)).setText(currentPlayer.getName());
	}
	
	private String nameForMinigame(MinigameIdentifier id) {
		switch (id) {
			case MINI_GAME_IDENTIFIER_WHACKAMOLE:
				return "Whack-A-Mole";
			default:
				return "(Game name not found)";
		}
	}
	
	private String descriptionForMinigame(MinigameIdentifier id) {
		switch (id) {
			case MINI_GAME_IDENTIFIER_WHACKAMOLE:
				return "Hit as many moles as you can before time runs out!";
			default:
				return "(Game description not found)";
		}
	}

	private Class classForMinigame(MinigameIdentifier id) {
		switch (id) {
			case MINI_GAME_IDENTIFIER_WHACKAMOLE:
				return MinigameWhackAMole.class;
			default:
				return null;
		}
	}
	
	public void onConfirmButtonClicked(View v) {
		Intent intent = new Intent(this, classForMinigame(minigameID));
		startActivityForResult(intent, REQUEST_CODE_GAME);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_GAME) {
			//just pass on the result
	        setResult(RESULT_OK, data);
	        finish();
		}
	}
}
