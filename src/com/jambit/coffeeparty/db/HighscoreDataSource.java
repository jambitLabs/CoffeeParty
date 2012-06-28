package com.jambit.coffeeparty.db;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.jambit.coffeeparty.model.Player;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

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
	
	public HashMap<Player, Integer> storeHighscore(List<Player> playersList) {
		Player[] players = (Player[]) playersList.toArray();
		float[] rowIds = new float[players.length];
		HashMap<Player, Integer> toReturn = new HashMap<Player, Integer>();
		
		for (int i = 0; i < players.length; i++) {
			rowIds[i] = this.storeHighscore(players[i]);
		}
		//now, iterate again to get the rank
		for (int i = 0; i < players.length; i++) {
			toReturn.put(players[i], this.getRankForId(rowIds[i]));
		}

		return toReturn;
	}
	
	private int getRankForId(float id) {
		Cursor cursor = getAllScores();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			if (cursor.getInt(cursor.getColumnIndex(HighscoreDBHelper.COLUMN_ID)) == id) {
				int toReturn = cursor.getPosition() + 1;
				cursor.close();
				return toReturn;
			}
			cursor.moveToNext();
		}
		cursor.close();
		return -1;
	}
	
	public int addPlayerToScores(Player p) {
		float id = storeHighscore(p);
		return getRankForId(id);
	}
	
	private float storeHighscore (Player p) {
		
		Date timestamp = new Date();
		ContentValues values = new ContentValues();
		values.put(HighscoreDBHelper.COLUMN_USERNAME, p.getName());
		values.put(HighscoreDBHelper.COLUMN_SCORE, p.getScore());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		values.put(HighscoreDBHelper.COLUMN_TIMESTAMP, dateFormat.format(timestamp));
		values.put(HighscoreDBHelper.COLUMN_AVATAR, bitmapToByteArray(p.getAvatar()));
		
		return database.insert(HighscoreDBHelper.TABLE_NAME, null, values);
	}
	
	public Cursor getAllHighscores() {
		return database.query(HighscoreDBHelper.TABLE_NAME, HighscoreDBHelper.ALL_COLUMNS, 
				null, null, null, null, HighscoreDBHelper.COLUMN_SCORE + " DESC", "10");
	}
	
	private Cursor getAllScores() {
		return database.query(HighscoreDBHelper.TABLE_NAME, HighscoreDBHelper.ALL_COLUMNS, 
				null, null, null, null, HighscoreDBHelper.COLUMN_SCORE + " DESC");
	}
	
	private byte[] bitmapToByteArray (Bitmap bmp) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}
}
