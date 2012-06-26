package com.jambit.coffeeparty.model;

import com.jambit.coffeeparty.CatchGameActivity;
import com.jambit.coffeeparty.MinigameWhackAMole;

public enum MinigameIdentifier {
    
	MINI_GAME_WHACKAMOLE("Hit as many moles as you can before time runs out!", MinigameWhackAMole.class){
	    @Override
	    public String toString(){
	        return "Whack-A-Mole";
	    }
	},
	
    MINI_GAME_CATCHTHEFLY("Try to catch the fly as fast as you can!", CatchGameActivity.class){
        @Override
        public String toString(){
            return "Catch-The-Fly";
        }
    },
    
	RANDOM_MINIGAME("Random minigame", null){
        @Override
        public String toString() {
            return "Random";
        }
	},
	
	SCORE("Score field. You win or lose points at random", null){
        @Override
        public String toString() {
            return "Score";
        }
	};
	
	private MinigameIdentifier(String descr, Class<?> clazz){
	    this.description = descr;
	    this.clazz = clazz;
	}
	
	private final String description;
	private final Class<?> clazz;
	
	public String description(){
	    return description;
	}
	
	public Class<?> minigameClass(){
	    return clazz;
	}
	
	public abstract String toString();
}
