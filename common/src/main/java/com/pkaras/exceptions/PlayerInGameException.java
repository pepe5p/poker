package com.pkaras.exceptions;

public class PlayerInGameException extends PokerException {
    public PlayerInGameException() {
        super("you are currently in game");
    }
}
