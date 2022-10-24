package enums;

public enum Suite {
    HEARTS(0, "♥"),
    CLUBS(1, "♣"),
    DIAMONDS(2, "♦"),
    SPADES(3, "♠");

    public final int value;
    public final String name;

    Suite(final int newValue, final String newName) {
        value = newValue;
        name = newName;
    }
}
