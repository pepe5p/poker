package common;

public enum Rank {
    TWO(0, "2"),
    THREE(1, "3"),
    FOUR(2, "4"),
    FIVE(3, "5"),
    SIX(4, "6"),
    SEVEN(5, "7"),
    EIGHT(6, "8"),
    NINE(7, "9"),
    TEN(8, "T"),
    JACK(9, "J"),
    QUEEN(10, "Q"),
    KING(11, "K"),
    ACE(12, "A");

    final int value;

    final String name;

    Rank(final int newValue, final String newName) {
        value = newValue;
        name = newName;
    }

    public int getValue() {
        return value;
    }
}
