package com.jambit.coffeeparty;

import java.util.Collections;

import android.app.ListActivity;
import android.content.Context;
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
    
    @Override
    protected void onCreate(final Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        setContentView(R.layout.final_results);
        
        Game game = ((CoffeePartyApplication)getApplication()).getGameState();
        // determine order of players: highest score first (reverse natural ordering)
        Collections.sort(game.getPlayers());
        Collections.reverse(game.getPlayers());
        
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
                              bt.setText("Score: " + p.getScore());
                        
                        if(img != null)
                            img.setImageBitmap(p.getAvatar());
                        
                        if(pr != null)
                            pr.setText(new Integer(position + 1).toString());
                }
                return v;
            }
        };
        
        setListAdapter(adapter);
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
