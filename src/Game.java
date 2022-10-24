import java.util.ArrayList;

public class Game {

    final int GAME_ID;
    final int ANTE;
    final int SB;
    final int BB;
    final Player OWNER;
    ArrayList<Player> activePlayers;
    ArrayList<Player> playersInQueue;
    final int MIN_PLAYERS_NUMBER = 2;
    final int MAX_PLAYERS_NUMBER = 4;

    Game(int id, int ante, int small_blind, Player owner) {
        this.GAME_ID = id;
        this.ANTE = ante;
        this.SB = small_blind;
        this.BB = small_blind * 2;
        this.OWNER = owner;
        this.activePlayers = new ArrayList<>(MAX_PLAYERS_NUMBER);
        this.playersInQueue = new ArrayList<>(MAX_PLAYERS_NUMBER);
        this.playersInQueue.add(owner);
    }

    private void startGame() {
        this.activePlayers.addAll(this.playersInQueue);
        this.playersInQueue.clear();
    }
}
