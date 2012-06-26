package com.jambit.coffeeparty;

public enum MinigameIdentifier {
	MINI_GAME_IDENTIFIER_WHACKAMOLE("Hit as many moles as you can before time runs out!", MinigameWhackAMole.class){
	    @Override
	    public String toString(){
	        return "Whack-A-Mole";
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
}
