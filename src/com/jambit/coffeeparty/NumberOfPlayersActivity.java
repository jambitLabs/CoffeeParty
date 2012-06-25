package com.jambit.coffeeparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class NumberOfPlayersActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numplayers);
    }
    
    public void onButton2Clicked(View v){
        returnNumPlayers(2);
    }
    
    public void onButton3Clicked(View v){
        returnNumPlayers(3);
    }
    
    public void onButton4Clicked(View v){
        returnNumPlayers(4);
    }
    
    private void returnNumPlayers(int num){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("numPlayers", num);

        // set the result code and data
        setResult(1, resultIntent);
        finish();
    }
}
