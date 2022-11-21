package com.pkaras.exceptions;

import com.pkaras.Deck;

public class ShuffleIncompleteDeckException extends PokerException {
    public ShuffleIncompleteDeckException() {
        super("deck must have " + Deck.MAX_CARDS_NUMBER + " cards to be shuffled");
    }
}
