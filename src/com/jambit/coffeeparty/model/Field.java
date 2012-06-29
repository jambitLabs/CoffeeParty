package com.jambit.coffeeparty.model;

public final class Field {
    private final MinigameIdentifier type;
    private final int x;
    private final int y;
    private final String iconName;
    
    public Field(MinigameIdentifier type, int x, int y, String icon){
        super();
        this.type = type;
        this.x = x;
        this.y = y;
        this.iconName = icon;
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
    
    public String getIconName(){
        return iconName;
    }
}
