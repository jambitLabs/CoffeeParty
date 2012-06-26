package com.jambit.coffeeparty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SettingsActivity extends Activity {
    
    private int mNumRounds;
    private int mMapId;
    
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
                    mNumRounds = Integer.parseInt(v.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
        
        Spinner mapChoices = (Spinner) findViewById(R.id.mapChoices);
        ArrayAdapter<ResourceMapping> adapter = new ArrayAdapter<ResourceMapping>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // iterate over raw resources and add all files with prefix map_
        java.lang.reflect.Field[] mapResources = R.raw.class.getFields();
        for(java.lang.reflect.Field f : mapResources){
            if(f.getName().startsWith("map_")){
                ResourceMapping res;
                try {
                    res = new ResourceMapping(f.getName(), f.getInt(null));
                    adapter.add(res);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        mapChoices.setAdapter(adapter);
        
        mapChoices.setOnItemSelectedListener(new OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                ResourceMapping res = (ResourceMapping)parent.getItemAtPosition(pos);
                mMapId = res.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMapId = R.raw.map_settlers;
            }
        });
    }
    
    public void onButtonConfirmClicked(View v){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("numRounds", mNumRounds);
        resultIntent.putExtra("mapId", mMapId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
    
    private class ResourceMapping{
        private final String name;
        private final int id;
        
        private ResourceMapping(String name, int id){
            this.name = name;
            this.id = id;
        }
        
        public int getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
}
