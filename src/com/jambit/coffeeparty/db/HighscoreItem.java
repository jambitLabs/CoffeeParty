package com.jambit.coffeeparty.db;

import android.graphics.Bitmap;

public class HighscoreItem {
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getDateString() {
		return dateString;
	}
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}
	public Bitmap getAvatar() {
		return avatar;
	}
	public void setAvatar(Bitmap avatar) {
		this.avatar = avatar;
	}
	private String name;
	private int score;
	private String dateString;
	private Bitmap avatar;
}
