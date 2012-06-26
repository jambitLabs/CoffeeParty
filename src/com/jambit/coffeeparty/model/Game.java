package com.jambit.coffeeparty.model;

import java.util.ArrayList;
import java.util.List;

public final class Game {

    private List<Player> players = new ArrayList<Player>();
    private List<Field> board = new ArrayList<Field>();
    private int mTotalRounds = 0;
    private int mRoundsPlayed = 0;

    public List<Field> getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }
    
    public int getmTotalRounds() {
        return mTotalRounds;
    }

    public int getmRoundsPlayed() {
        return mRoundsPlayed;
    }

    public void startGame(int rounds, List<Field> board){
        this.mTotalRounds = rounds;
        this.mRoundsPlayed = 0;
        this.board = board;
    }
}
