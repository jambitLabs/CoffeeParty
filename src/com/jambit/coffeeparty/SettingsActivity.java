package com.jambit.coffeeparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SettingsActivity extends Activity {
    
    private int numRounds;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        EditText editText = (EditText) findViewById(R.id.inputRounds);
        editText.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    numRounds = Integer.parseInt(v.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
    }
    
    public void onButtonConfirmClicked(View v){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("numRounds", numRounds);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
