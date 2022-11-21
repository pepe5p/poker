package com.pkaras.exceptions;

public class UnknownGameActionException extends PokerException {
    public UnknownGameActionException() {
        super("game cannot recognize this command");
    }
}
