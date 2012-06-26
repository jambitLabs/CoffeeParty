package com.jambit.coffeeparty;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.jambit.coffeeparty.model.*;

public class MinigameStartActivity extends Activity {
	private final int REQUEST_CODE = 123;
	
	private Player currentPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		currentPlayer = ((CoffeePartyApplication) getApplication()).getGameState().getCurrentPlayer();
		
		setContentView(R.layout.minigame_start);
		((TextView) findViewById(R.id.minigameStartTitle)).setText("Whack-a-Mole");
		((TextView) findViewById(R.id.minigameStartDescription)).setText("Try to hit as many moles as you can before the time runs out!");
		((TextView) findViewById(R.id.minigameStartPlayerName)).setText(currentPlayer.getName());
	}

	public void onConfirmButtonClicked(View v) {
		Intent intent = new Intent(this, MinigameBaseActivity.class);
		//add player to intent
		startActivityForResult(intent, REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE) {
			//store result for current player, display info for next player
		}
	}
}
