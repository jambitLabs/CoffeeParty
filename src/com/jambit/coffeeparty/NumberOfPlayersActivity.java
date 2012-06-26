package com.jambit.coffeeparty;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jambit.coffeeparty.model.Player;

public class NumberOfPlayersActivity extends Activity {
    
    private final static int AVATAR_SET = 1;
    
    private List<Player> mPlayers;
    private int mNumPlayers;
    private int mCurrentPlayer = 1;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.numplayers);
        mPlayers = new ArrayList<Player>();
    }
    
    public void onButton2Clicked(View v){
        mNumPlayers = 2;
        createAlertDialog(mCurrentPlayer, this).show();
    }
    
    public void onButton3Clicked(View v){
        mNumPlayers = 3;
        createAlertDialog(mCurrentPlayer, this).show();
    }
    
    public void onButton4Clicked(View v){
        mNumPlayers = 4;
        createAlertDialog(mCurrentPlayer, this).show();
    }
    
    private void returnPlayerData(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("players", mPlayers.toArray());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
    
    private AlertDialog createAlertDialog(final int playerIndex, final Context packageContext){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setSingleLine();
        builder.setMessage("Enter name of player " + playerIndex)
            .setView(input)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = input.getText().toString();
                    dialog.dismiss();
                    Intent avatarIntent = new Intent(packageContext, AvatarActivity.class);
                    avatarIntent.putExtra(AvatarActivity.PLAYERNAME_EXTRA, name);
                    startActivityForResult(avatarIntent, AVATAR_SET);
                }
            });
        return builder.create();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AVATAR_SET) {
            String name = data.getExtras().get(AvatarActivity.PLAYERNAME_EXTRA).toString();
            Bitmap avatar = (Bitmap)data.getExtras().get(AvatarActivity.SELECTED_AVATAR_EXTRA);
            Player player = new Player(name, avatar);
            mPlayers.add(player);
            
            if(mCurrentPlayer < mNumPlayers)
                createAlertDialog(++mCurrentPlayer, this).show();
            else
                returnPlayerData();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
