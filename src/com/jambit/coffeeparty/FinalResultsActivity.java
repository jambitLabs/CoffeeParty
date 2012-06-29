package com.jambit.coffeeparty;

import android.app.ListActivity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jambit.coffeeparty.model.Game;
import com.jambit.coffeeparty.model.Player;

public class FinalResultsActivity extends ListActivity {
    
    private MediaPlayer mPlayer;
    
    @Override
    protected void onCreate(final Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        setContentView(R.layout.final_results);
        
        Game game = ((CoffeePartyApplication)getApplication()).getGameState();
        
        ArrayAdapter<Player> adapter = new ArrayAdapter<Player>(this, R.layout.ranking_entry, game.getPlayers()){
            
            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEnabled(int position) {
                return false;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.ranking_entry, null);
                }
                Player p = getItem(position);
                if (p != null) {
                        ImageView img = (ImageView) v.findViewById(R.id.avataricon);
                        TextView tt = (TextView) v.findViewById(R.id.playername_toptext);
                        TextView bt = (TextView) v.findViewById(R.id.playerscore_bottomtext);
                        TextView pr = (TextView) v.findViewById(R.id.player_ranking);
                        
                        if (tt != null)
                              tt.setText("Player: " + p.getName());  
                        
                        if(bt != null)
                              bt.setText("Score: " + p.getScore() + ", Global Rank: " + p.getRank());
                        
                        if(img != null)
                            img.setImageBitmap(p.getAvatar());
                        
                        if(pr != null)
                            pr.setText(new Integer(position + 1).toString());
                }
                return v;
            }
        };
        setListAdapter(adapter);
        
        mPlayer = MediaPlayer.create(this, R.raw.results);
        mPlayer.setLooping(true);
        mPlayer.start();
    }
    
    @Override
    public void onPause(){
        if(mPlayer != null)
            mPlayer.release();
        super.onPause();
    }
    
    @Override
    public void onStop(){
        if(mPlayer != null)
            mPlayer.release();
        super.onStop();
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
