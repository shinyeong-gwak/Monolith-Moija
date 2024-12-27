package com.example.monolithmoija.dto;

public enum Type {
    ENTER,READ,TALK,QUIT;
    @Override
    public String toString() {
        return this.name();
    }
}
