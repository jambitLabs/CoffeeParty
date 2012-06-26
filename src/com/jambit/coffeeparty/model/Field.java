package com.jambit.coffeeparty.model;

public final class Field {
    private final FieldType type;
    private final int x;
    private final int y;
    
    public Field(FieldType type, int x, int y){
        super();
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public FieldType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
