package com.jambit.coffeeparty.model;

public final class Field {
    private final MinigameIdentifier type;
    private final int x;
    private final int y;
    
    public Field(MinigameIdentifier type, int x, int y){
        super();
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public MinigameIdentifier getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
