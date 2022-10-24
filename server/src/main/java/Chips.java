public class Chips {
    int chipsInStack;
    int chipsInPot;

    public Chips(int chipsInStack) {
        this.chipsInStack = chipsInStack;
        this.chipsInPot = 0;
    }

    public Chips(int chipsInStack, int chipsInPot) {
        this.chipsInStack = chipsInStack;
        this.chipsInPot = chipsInPot;
    }
}
