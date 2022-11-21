package com.pkaras.exceptions;

public class PlayerInMenuException extends PokerException {
    public PlayerInMenuException() {
        super("you are not in game");
    }
}
