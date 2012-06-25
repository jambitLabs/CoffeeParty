package com.jambit.coffeeparty.model;

import java.util.ArrayList;
import java.util.List;

public final class Game {

    private List<Player> players = new ArrayList<Player>();
    private List<Field> board = new ArrayList<Field>();

    public Game()
    {
        Player alex = new Player("Alex", null);
        alex.setPosition(3);
        alex.setScore(15);
        players.add(alex);
        
        Player bene = new Player("Bene", null);
        bene.setPosition(4);
        bene.setScore(10);
        players.add(bene);
        
        Player sebi = new Player("Sebi", null);
        sebi.setPosition(0);
        sebi.setScore(0);
        players.add(sebi);
        
        board.add(new Field(FieldType.SCORE));
        board.add(new Field(FieldType.SCORE));
        board.add(new Field(FieldType.SCORE));
        board.add(new Field(FieldType.SCORE));
        board.add(new Field(FieldType.SCORE));
        board.add(new Field(FieldType.SCORE));
        board.add(new Field(FieldType.SCORE));
        board.add(new Field(FieldType.SCORE));
    }
    
    public List<Field> getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }

}
