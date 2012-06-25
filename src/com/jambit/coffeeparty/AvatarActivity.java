package com.jambit.coffeeparty;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Gallery;
import android.widget.TextView;

public class AvatarActivity extends Activity {
    public static String PLAYERNAME_EXTRA = "playerName";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avataractivity);
        Gallery g = (Gallery) findViewById(R.id.gallery1);
        g.setAdapter(new ImageAdapter(this));
        TextView playerNameTextView = (TextView) findViewById(R.id.playerNameTextView);
        CharSequence playerName = getIntent().getCharSequenceExtra(PLAYERNAME_EXTRA);
        playerNameTextView.setText(playerName);
    }
}
