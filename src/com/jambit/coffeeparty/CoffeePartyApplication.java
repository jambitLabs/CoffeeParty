package com.jambit.coffeeparty;

import android.app.Application;

import com.jambit.coffeeparty.model.Game;

public class CoffeePartyApplication extends Application {
    private Game gameState;

    public CoffeePartyApplication(){
        gameState = new Game();
    }
    
    public Game getGameState() {
        return gameState;
    }
}
