package com.pkaras.exceptions;

import com.pkaras.Hand;

public class ImproperHandCardsNumberException extends PokerException {
    public ImproperHandCardsNumberException() {
        super("Hand must have " + Hand.NUMBER_OF_CARDS + " Cards.");
    }
}
