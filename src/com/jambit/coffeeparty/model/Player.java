package com.jambit.coffeeparty.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public final class Player implements Parcelable{
    private final String mName;
    private final Bitmap mAvatar;
    private int mScore;
    private int mPosition;
    
    public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }
        
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
    
    public Player(String name, Bitmap avatar) {
        super();
        this.mName = name;
        this.mAvatar = avatar;
        this.mScore = 0;
    }
    
    private Player(Parcel in){
        mName = in.readString();
        mAvatar = in.readParcelable(getClass().getClassLoader());
        mScore = in.readInt();
        mPosition = in.readInt();
    }
    
    public int getScore() {
        return mScore;
    }
    public void changeScoreBy(int points) {
        this.mScore += points;
    }
    
    public int getPosition() {
        return mPosition;
    }
    public void setPosition(int position) {
        this.mPosition = position;
    }
    
    public String getName() {
        return mName;
    }
    
    public Bitmap getAvatarId() {
        return mAvatar;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeParcelable(mAvatar, 0);
        dest.writeInt(mScore);
        dest.writeInt(mPosition);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mName == null) ? 0 : mName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Player))
            return false;
        Player other = (Player) obj;
        if (mName == null) {
            if (other.mName != null)
                return false;
        } else if (!mName.equals(other.mName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("Player [name=%s, score=%s, position=%s]", mName, mScore, mPosition);
    }
}
