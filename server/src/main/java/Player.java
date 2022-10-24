public class Player {
    private final Chips chips;
    private final Hand hand;

    public Player(int chips) {
        this.chips = new Chips(chips);
        this.hand = new Hand();
    }

    public Player(Chips chips) {
        this.chips = chips;
        this.hand = new Hand();
    }

    public Chips getChips() {
        return this.chips;
    }

    public Hand getHand() {
        return hand;
    }

    public void addChips(int chips) {
        this.chips.chipsInStack += chips;
    }

    public void putChipsToPot(int chips) {
        if (this.chips.chipsInStack >= chips) {
            this.chips.chipsInStack -= chips;
            this.chips.chipsInPot += chips;
        }
        else {
            this.chips.chipsInPot += this.chips.chipsInStack;
            this.chips.chipsInStack = 0;
        }
    }

    public int removeChipsFromPot(int chips) {
        int removedChips = chips;
        if (this.chips.chipsInPot >= chips) {
            this.chips.chipsInPot -= chips;
        }
        else {
            removedChips = this.chips.chipsInPot;
            this.chips.chipsInPot = 0;
        }
        return removedChips;
    }
}
