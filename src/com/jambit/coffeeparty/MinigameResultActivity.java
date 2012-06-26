package com.jambit.coffeeparty;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MinigameResultActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.minigame_result);
		((TextView) findViewById(R.id.minigameResultPlayer)).setText("Theplayer");
		((TextView) findViewById(R.id.minigameResultScore)).setText("+25");
	}
	
	private void onConfirmButton(View v) {
		//TODO: change score in model!
		finish();
	}

}
