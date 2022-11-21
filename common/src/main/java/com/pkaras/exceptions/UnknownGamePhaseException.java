package com.pkaras.exceptions;

public class UnknownGamePhaseException extends PokerException {
    public UnknownGamePhaseException() {
        super("unknown game phase");
    }
}
