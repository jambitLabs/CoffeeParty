package com.jambit.coffeeparty.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

public final class Game {

    private List<Player> players = new ArrayList<Player>();
    private Map mMap;
    private int mTotalRounds = 0;
    private int mRoundsPlayed = 0;
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public Map getMap(){
        return this.mMap;
    }
    
    public int getmTotalRounds() {
        return mTotalRounds;
    }

    public int getmRoundsPlayed() {
        return mRoundsPlayed;
    }

    public void startGame(int rounds, InputStream xml) throws XPathExpressionException{
        this.mTotalRounds = rounds;
        this.mRoundsPlayed = 0;
        this.mMap = Map.loadFromXML(xml);
    }
    
    public Player getCurrentPlayer() {
    	//placeholder
    	return new Player("Horst", null);
    }
}
