package common;

public enum Suite {
    HEARTS(0, "♥"),
    CLUBS(1, "♣"),
    DIAMONDS(2, "♦"),
    SPADES(3, "♠");

    final int value;
    final String name;

    Suite(final int newValue, final String newName) {
        value = newValue;
        name = newName;
    }
}
