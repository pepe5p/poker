package com.pkaras.enums;

public enum Suite {
    HEARTS(0, "♥", "\u001B[31m"),
    CLUBS(1, "♣", "\u001B[35m"),
    DIAMONDS(2, "♦", "\u001B[31m"),
    SPADES(3, "♠", "\u001B[35m");

    public final int value;
    public final String suiteName;
    public final String color;

    Suite(final int value, final String name, final String color) {
        this.value = value;
        this.suiteName = name;
        this.color = color;
    }
}
