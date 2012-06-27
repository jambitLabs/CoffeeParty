package com.jambit.coffeeparty;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import com.jambit.coffeeparty.model.Player;

public class DisplayResultActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.minigame_result);
		Player thePlayer = (Player) getIntent().getExtras().get(getString(R.string.playerkey));
		int pointsGained = getIntent().getExtras().getInt(getString(R.string.pointskey));
		
		((TextView) findViewById(R.id.minigameResultPlayer)).setText(thePlayer.getName());
		((TextView) findViewById(R.id.minigameResultPoints)).setText("Points: " + pointsGained);
		((TextView) findViewById(R.id.minigameResultScore)).setText("New Score: " + thePlayer.getScore());
		((TextView) findViewById(R.id.minigameResultScore)).setPadding(50, 0, 0, 0);
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return false;
    }
}
