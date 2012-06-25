package com.jambit.coffeeparty.model;

public final class Field {
    private final FieldType type;
    
    public Field(FieldType type){
        super();
        this.type = type;
    }

    public FieldType getType() {
        return type;
    }
}
