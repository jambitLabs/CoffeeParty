package com.jambit.coffeeparty.db;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jambit.coffeeparty.model.Player;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

public class HighscoreDataSource {
	private HighscoreDBHelper dbHelper;
	private SQLiteDatabase database;
	public HighscoreDataSource(Context context) {
		dbHelper = new HighscoreDBHelper(context);
	}
	
	public void openForReading() {
		database = dbHelper.getReadableDatabase();
	}
	public void openForWriting() {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public int storeHighscore (Player p) {
		
		Date timestamp = new Date();
		ContentValues values = new ContentValues();
		values.put(HighscoreDBHelper.COLUMN_USERNAME, p.getName());
		values.put(HighscoreDBHelper.COLUMN_SCORE, p.getScore());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		values.put(HighscoreDBHelper.COLUMN_TIMESTAMP, dateFormat.format(timestamp));
		values.put(HighscoreDBHelper.COLUMN_AVATAR, bitmapToByteArray(p.getAvatar()));
		
		database.insert(HighscoreDBHelper.TABLE_NAME, null, values);
		
		
		Cursor cursor = getAllHighscores();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			if (cursor.getString(cursor.getColumnIndex(HighscoreDBHelper.COLUMN_USERNAME)).equals(p.getName()) &&
					cursor.getString(cursor.getColumnIndex(HighscoreDBHelper.COLUMN_TIMESTAMP)).equals(dateFormat.format(timestamp))) {
				return cursor.getPosition() + 1;
			}
			cursor.moveToNext();
		}
		
		return -1;
	}
	
	public Cursor getAllHighscores() {
		return database.query(HighscoreDBHelper.TABLE_NAME, HighscoreDBHelper.ALL_COLUMNS, 
				null, null, null, null, HighscoreDBHelper.COLUMN_SCORE + " DESC", "10");
	}
	
	private byte[] bitmapToByteArray (Bitmap bmp) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}
}
