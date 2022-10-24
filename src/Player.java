import common.Hand;

public class Player {
    public final int PLAYER_ID;
    private int chips;
    private Hand hand;

    public Player(int id, int chips) {
        this.PLAYER_ID = id;
        this.chips = chips;
        this.hand = new Hand();
    }
}
