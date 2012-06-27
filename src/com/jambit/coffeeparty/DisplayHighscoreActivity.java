package com.jambit.coffeeparty;

import com.jambit.coffeeparty.db.HighscoreDBHelper;
import com.jambit.coffeeparty.db.HighscoreDataSource;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class DisplayHighscoreActivity extends ListActivity {
	HighscoreDataSource dataSource;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dataSource = new HighscoreDataSource(this);
		dataSource.openForReading();
		Cursor c = dataSource.getAllHighscores();
		String[] mapFrom = {HighscoreDBHelper.COLUMN_USERNAME, HighscoreDBHelper.COLUMN_SCORE, HighscoreDBHelper.COLUMN_TIMESTAMP};
		int[] mapTo = {R.id.highscore_nameview, R.id.highscore_scoreview, R.id.highscore_dateview};
		
		HighscoreCursorAdapter adapter = new HighscoreCursorAdapter(this, c);
		
		this.setListAdapter(adapter);
		this.getListView().setClickable(false);
	}
}
