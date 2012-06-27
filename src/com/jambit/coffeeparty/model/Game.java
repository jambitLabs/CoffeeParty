package com.jambit.coffeeparty.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

public final class Game {

    private List<Player> players = new ArrayList<Player>();
    private Player currentPlayer = null;
    private int currentPlayerIndex = 0;
    private Map mMap;
    private int mTotalRounds = 0;
    private int mRoundsPlayed = 0;
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public Map getMap(){
        return this.mMap;
    }
    
    public int getTotalRounds() {
        return mTotalRounds;
    }

    public int getRoundsPlayed() {
        return mRoundsPlayed;
    }

    public void startGame(int rounds, InputStream xml) throws XPathExpressionException{
        this.mTotalRounds = rounds;
        this.mRoundsPlayed = 0;
        this.mMap = Map.loadFromXML(xml);
        this.currentPlayer = players.get(0);
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public void nextPlayer(){
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
        
        if(currentPlayerIndex == players.size() - 1)
            mRoundsPlayed++;
    }
}
