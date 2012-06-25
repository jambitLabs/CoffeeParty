package com.jambit.coffeeparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.TextView;

public class AvatarActivity extends Activity {
    public static String PLAYERNAME_EXTRA = "playerName";
    public static String SELECTED_AVATAR_EXTRA = "selected_avatar";
    private Intent data = new Intent();
    private ImageAdapter imageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avataractivity);
        imageAdapter = new ImageAdapter(this);
        Gallery g = (Gallery) findViewById(R.id.gallery1);
        g.setAdapter(imageAdapter);
        TextView playerNameTextView = (TextView) findViewById(R.id.playerNameTextView);
        CharSequence playerName = getIntent().getCharSequenceExtra(PLAYERNAME_EXTRA);
        playerNameTextView.setText(playerName);

        g.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(@SuppressWarnings("rawtypes") AdapterView parent, View v, int position, long id) {
                data.putExtra(SELECTED_AVATAR_EXTRA, imageAdapter.getItemId(position));
            }
        });
    }

    public void onApplyButtonClicked(View v) {
        setResult(1, data);
        finish();
    }
}
