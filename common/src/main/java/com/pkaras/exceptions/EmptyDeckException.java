package com.pkaras.exceptions;

public class EmptyDeckException extends PokerException {
    public EmptyDeckException() {
        super("Cannot get Card, because Deck is empty.");
    }
}
