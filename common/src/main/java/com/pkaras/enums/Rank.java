package com.pkaras.enums;

public enum Rank {
    DEUCE(0, "2"),
    THREE(1, "3"),
    FOUR(2, "4"),
    FIVE(3, "5"),
    SIX(4, "6"),
    SEVEN(5, "7"),
    EIGHT(6, "8"),
    NINE(7, "9"),
    TEN(8, "10"),
    JACK(9, "J"),
    QUEEN(10, "Q"),
    KING(11, "K"),
    ACE(12, "A");

    public final int value;

    public final String rankName;

    Rank(final int newValue, final String newName) {
        value = newValue;
        rankName = newName;
    }
}
