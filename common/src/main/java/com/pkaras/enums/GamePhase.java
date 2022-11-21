package com.pkaras.enums;

public enum GamePhase {
    UNSTARTED(0, "unstarted", 0),
    PRE_FLOP(1, "pre-flop", 2),
    FLOP(2, "flop", 0),
    TURN(3, "turn", 0),
    RIVER(4, "river", 0);

    public final int value;
    public final String phaseName;
    public final Integer initialLastIndexOfRound;

    GamePhase(int value, String phaseName, Integer initialLastIndexOfRound) {
        this.value = value;
        this.phaseName = phaseName;
        this.initialLastIndexOfRound = initialLastIndexOfRound;
    }
}
