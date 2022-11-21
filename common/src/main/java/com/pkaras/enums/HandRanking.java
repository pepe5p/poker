package com.pkaras.enums;

public enum HandRanking {
    EMPTY(0, "Empty Hand", 0),
    HIGH_CARD(1, "High Card", 1),
    PAIR(2, "Pair", 1),
    TWO_PAIRS(3, "Two Pairs", 2),
    THREE_OF_A_KIND(4, "Three of a Kind", 1),
    STRAIGHT(5, "Straight", 1),
    FLUSH(6, "Flush", 1),
    FULL_HOUSE(7, "Full House", 2),
    FOUR_OF_A_KIND(8, "Four of a Kind", 1),
    STRAIGHT_FLUSH(9, "Straight Flush", 1),
    ROYAL_FLUSH(10, "Royal Flush", 0);

    public final int value;
    public final String handRankingName;
    public final int numberOfCardsToShow;

    HandRanking(
            final int value,
            final String handRankingName,
            final int numberOfCardsToShow
    ) {
        this.value = value;
        this.handRankingName = handRankingName;
        this.numberOfCardsToShow = numberOfCardsToShow;
    }
}
