package com.jambit.coffeeparty;

import com.jambit.coffeeparty.db.HighscoreDBHelper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HighscoreCursorAdapter extends CursorAdapter {
	public HighscoreCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView name = (TextView)view.findViewById(R.id.highscore_nameview);
		name.setText(cursor.getString(cursor.getColumnIndex(HighscoreDBHelper.COLUMN_USERNAME)));
		
		TextView score = (TextView)view.findViewById(R.id.highscore_scoreview);
		score.setText(cursor.getString(cursor.getColumnIndex(HighscoreDBHelper.COLUMN_SCORE)));
		
		TextView date = (TextView)view.findViewById(R.id.highscore_dateview);
		date.setText(cursor.getString(cursor.getColumnIndex(HighscoreDBHelper.COLUMN_TIMESTAMP)));
		
		TextView rank = (TextView)view.findViewById(R.id.highscore_rankview);
		rank.setText("" + (cursor.getPosition() + 1));
		
		byte[] byteArray = cursor.getBlob(cursor.getColumnIndex(HighscoreDBHelper.COLUMN_AVATAR));
		Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		ImageView imageView = (ImageView)view.findViewById(R.id.highscore_imageview);
		imageView.setImageBitmap(bitmap);	
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.highscore_list_item, parent, false);
		bindView(v, context, cursor);
		return v;
	}
	
}
