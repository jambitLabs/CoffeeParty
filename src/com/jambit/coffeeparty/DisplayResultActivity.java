package com.jambit.coffeeparty;

import com.jambit.coffeeparty.model.Player;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DisplayResultActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.minigame_result);
		Player thePlayer = (Player) getIntent().getExtras().get(getString(R.string.playerkey));
		int theScore = getIntent().getExtras().getInt(getString(R.string.scorekey));
		
		((TextView) findViewById(R.id.minigameResultPlayer)).setText(thePlayer.getName());
		((TextView) findViewById(R.id.minigameResultScore)).setText(""+theScore);
	}
	
	
	@SuppressWarnings("unused")
	private void onConfirmButton(View v) {
		finish();
	}
}
