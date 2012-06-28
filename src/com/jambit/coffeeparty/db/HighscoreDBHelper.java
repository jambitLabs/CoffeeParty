package com.jambit.coffeeparty.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HighscoreDBHelper extends SQLiteOpenHelper{
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "highscoredb";
	public static final String TABLE_NAME = "highscores";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_USERNAME = "username";
	public static final String COLUMN_SCORE = "score";
	public static final String COLUMN_TIMESTAMP = "timestamp";
	public static final String COLUMN_AVATAR = "avatar";
	
	public static final String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_USERNAME, COLUMN_SCORE, COLUMN_TIMESTAMP, COLUMN_AVATAR};
	
	private static final String DB_CREATE = "CREATE TABLE " + TABLE_NAME + "(" +
											COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
											COLUMN_USERNAME + " TEXT NOT NULL, " +
											COLUMN_SCORE + " INTEGER, " +
											COLUMN_TIMESTAMP + " TEXT, " +
											COLUMN_AVATAR + " BLOB);";
											
	private static final String DB_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	HighscoreDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATE);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//just drop it
		db.execSQL(DB_DROP);
	}
}