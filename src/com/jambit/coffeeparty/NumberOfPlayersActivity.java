package com.jambit.coffeeparty;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class NumberOfPlayersActivity extends Activity {
    
    private List<String> playerNames;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numplayers);
        playerNames = new ArrayList<String>(4);
    }
    
    public void onButton2Clicked(View v){
        createAlertDialog(1, 2).show();
    }
    
    public void onButton3Clicked(View v){
        createAlertDialog(1, 3).show();
    }
    
    public void onButton4Clicked(View v){
        createAlertDialog(1, 4).show();
    }
    
    private void returnPlayerData(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("players", playerNames.toArray());

        // set the result code and data
        setResult(1, resultIntent);
        finish();
    }
    
    private AlertDialog createAlertDialog(final int player, final int total){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        builder.setMessage("Enter name of player " + player)
            .setView(input)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = input.getText().toString();
                    playerNames.add(name);
                    dialog.dismiss();
                    if(player < total)
                        createAlertDialog(player + 1, total).show();
                    else
                        returnPlayerData();
                }
            });
        return builder.create();
    }
}
