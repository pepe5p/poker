package common;

public enum HandRanking {
    HIGH_CARD(0, "High Card", 1),
    PAIR(1, "Pair", 1),
    TWO_PAIRS(2, "Two Pairs", 2),
    THREE_OF_A_KIND(3, "Three of a Kind", 1),
    STRAIGHT(4, "Straight", 1),
    FLUSH(5, "Flush", 1),
    FULL_HOUSE(6, "Full House", 2),
    FOUR_OF_A_KIND(7, "Four of a Kind", 1),
    STRAIGHT_FLUSH(8, "Straight Flush", 1),
    ROYAL_FLUSH(9, "Royal Flush", 1);

    final int value;
    final String name;
    final int numberOfCardsToShow;

    HandRanking(
            final int value,
            final String name,
            final int numberOfCardsToShow
    ) {
        this.value = value;
        this.name = name;
        this.numberOfCardsToShow = numberOfCardsToShow;
    }
}
