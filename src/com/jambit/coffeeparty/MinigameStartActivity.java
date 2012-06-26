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
		((TextView) findViewById(R.id.minigameStartTitle)).setText(minigameID.toString());
		((TextView) findViewById(R.id.minigameStartDescription)).setText(minigameID.description());
		((TextView) findViewById(R.id.minigameStartPlayerName)).setText(currentPlayer.getName());
	}
	
	public void onConfirmButtonClicked(View v) {
		Intent intent = new Intent(this, minigameID.minigameClass());
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
