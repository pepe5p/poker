package com.pkaras.enums;

public enum Suite {
    HEARTS(0, "H", "\u001B[31m"),
    CLUBS(1, "C", "\u001B[35m"),
    DIAMONDS(2, "D", "\u001B[31m"),
    SPADES(3, "S", "\u001B[35m");

    public final int value;
    public final String suiteName;
    public final String color;

    Suite(final int value, final String suiteName, final String color) {
        this.value = value;
        this.suiteName = suiteName;
        this.color = color;
    }
}
