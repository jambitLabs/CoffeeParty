package com.jambit.coffeeparty;

import com.jambit.coffeeparty.db.HighscoreDataSource;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;

public class DisplayHighscoreActivity extends ListActivity {
	HighscoreDataSource dataSource;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dataSource = new HighscoreDataSource(this);
		dataSource.openForReading();
		Cursor c = dataSource.getAllHighscores();
		
		HighscoreCursorAdapter adapter = new HighscoreCursorAdapter(this, c);
		
		this.setListAdapter(adapter);
		this.getListView().setClickable(false);
		this.getListView().setFocusable(false);
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dataSource.close();
	}
}
