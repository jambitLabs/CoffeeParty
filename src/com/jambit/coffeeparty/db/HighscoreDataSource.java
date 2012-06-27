package com.jambit.coffeeparty.db;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.util.EntityUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

public class HighscoreDataSource {
	private HighscoreDBHelper dbHelper;
	private SQLiteDatabase database;
	HighscoreDataSource(Context context) {
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
	
	public void storeHighscore (String name, int score, Date timestamp, Bitmap avatar) {
		ContentValues values = new ContentValues();
		values.put(HighscoreDBHelper.COLUMN_USERNAME, name);
		values.put(HighscoreDBHelper.COLUMN_SCORE, score);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		values.put(HighscoreDBHelper.COLUMN_TIMESTAMP, dateFormat.format(timestamp));
		values.put(HighscoreDBHelper.COLUMN_AVATAR, bitmapToByteArray(avatar));
		
		database.insert(HighscoreDBHelper.DB_NAME, null, values);
	}
	
	private byte[] bitmapToByteArray (Bitmap bmp) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}
}
