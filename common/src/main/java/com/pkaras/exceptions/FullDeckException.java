package com.pkaras.exceptions;

public class FullDeckException extends PokerException {
    public FullDeckException() {
        super("Cannot return Card, because Deck is full.");
    }
}
