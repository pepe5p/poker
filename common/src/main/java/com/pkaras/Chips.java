package com.pkaras;


public class Chips {
    double chipsInStack;
    double chipsInGame;
    double chipsInPot;

    public Chips(double chipsInStack) {
        this.chipsInStack = chipsInStack;
        this.chipsInGame = 0;
        this.chipsInPot = 0;
    }

    public Chips(double chipsInStack, double chipsInGame, double chipsInPot) {
        this.chipsInStack = chipsInStack;
        this.chipsInGame = chipsInGame;
        this.chipsInPot = chipsInPot;
    }

    public void addChipsToStack(double chips) {
        this.chipsInStack += chips;
    }

    public int transferChipsIntoGame(double chips) {
        if (this.chipsInStack >= chips) {
            this.chipsInStack -= chips;
            this.chipsInGame += chips;
            return (int) chips;
        }
        else {
            int takenChips = (int) this.chipsInStack;
            this.chipsInGame += this.chipsInStack;
            this.chipsInStack = 0;
            return takenChips;
        }
    }

    public void returnChipsToStack(double chips) {
        double removedChips = this.removeChipsFromPot(chips);
        this.chipsInStack += removedChips;
    }

    void transferChipsToPot() {
        chipsInPot = chipsInGame;
        chipsInGame = 0;
    }

    public double removeChipsFromPot(double chips) {
        double removedChips = chips;
        if (this.chipsInPot >= chips) {
            this.chipsInPot -= chips;
        }
        else {
            removedChips = this.chipsInPot;
            this.chipsInPot = 0;
        }
        return removedChips;
    }

    public double removeChipsFromPot(double chips, int numberOfPlayersToSplit) {
        double removedChips = chips / numberOfPlayersToSplit;
        if (this.chipsInPot < chips) {
            removedChips = this.chipsInPot / numberOfPlayersToSplit;
        }
        this.chipsInPot -= removedChips;
        return removedChips;
    }

    @Override
    public String toString() {
        return String.format(
            "%d in stack / %d in pot",
            (int) this.chipsInStack,
            (int) this.chipsInGame + (int) this.chipsInPot
        );
    }
}
