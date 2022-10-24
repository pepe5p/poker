import java.util.ArrayList;

public class Game {

    final int ANTE;
    final int SB;
    final int BB;
    Player OWNER;
    int dealerIndex;
    ArrayList<Player> activePlayers;
    ArrayList<Player> playersInQueue;
    static final int MIN_PLAYERS_NUMBER = 2;
    static final int MAX_PLAYERS_NUMBER = 4;

    Game(int ante, int small_blind) {
        this.ANTE = ante;
        this.SB = small_blind;
        this.BB = small_blind * 2;
        this.OWNER = null;
        this.dealerIndex = 0;
        this.activePlayers = new ArrayList<>(MAX_PLAYERS_NUMBER);
        this.playersInQueue = new ArrayList<>(MAX_PLAYERS_NUMBER);
    }

    Game(int ante, int small_blind, Player owner) {
        this(ante, small_blind);
        this.OWNER = owner;
        this.playersInQueue.add(owner);
    }

    private int getPot() {
        int chipsInPot = 0;
        for (Player player : this.activePlayers) {
            chipsInPot += player.getChips().chipsInPot;
        }
        return chipsInPot;
    }

    public boolean enqueuePlayers(ArrayList<Player> players) {
        for (Player player : players) {
            boolean added = this.enqueuePlayer(player);
            if (!added) return false;
        }
        return true;
    }

    public boolean enqueuePlayer(Player player) {
        if (this.activePlayers.size() + this.playersInQueue.size() >= MAX_PLAYERS_NUMBER) {
            return false;
        }
        this.playersInQueue.add(player);
        return true;
    }

    public void startGame() {
        if (this.playersInQueue.size() < MIN_PLAYERS_NUMBER) return;
        this.startRound();
    }

    public void startRound() {
        this.addQueueToGame();
        this.moveDealer();
        this.takeAnte();
        this.takeBlinds();
    }

    public void addQueueToGame() {
        this.activePlayers.addAll(this.playersInQueue);
        this.playersInQueue.clear();
    }

    private void moveDealer() {
        this.dealerIndex = this.getNextPlayerIndex(this.dealerIndex, 1);
    }

    private void takeAnte() {
        for (Player player : this.activePlayers) {
            player.putChipsToPot(this.ANTE);
        }
    }

    private void takeBlinds() {
        int smallBlindIndex = this.getNextPlayerIndex(this.dealerIndex, 1);
        int bigBlindIndex = this.getNextPlayerIndex(this.dealerIndex, 2);
        this.activePlayers.get(smallBlindIndex).putChipsToPot(this.SB);
        this.activePlayers.get(bigBlindIndex).putChipsToPot(this.BB);
    }

    private int getNextPlayerIndex(int index, int step) {
        return (index + step) % this.activePlayers.size();
    }

    public void finishRound() {
        ArrayList<ArrayList<Player>> playersRanking = this.getPlayersRanking();
        this.splitChips(playersRanking);
    }

    public ArrayList<ArrayList<Player>> getPlayersRanking() {
        ArrayList<ArrayList<Player>> playersRanking = null;
        return playersRanking;
    }

    public void splitChips(ArrayList<ArrayList<Player>> playersRanking) {
        for (ArrayList<Player> playersGroup : playersRanking) {
            int numberOfPlayersToSplit = playersGroup.size();
            for (Player player : playersGroup) {
                this.transferChips(player, numberOfPlayersToSplit);
                numberOfPlayersToSplit--;
            }
        }
    }

    private void transferChips(Player winner, int numberOfPlayersToSplit) {
//        int[] chipsToTake = this.divideChips(player.getChips().chipsInPot, numberOfPlayersToSplit);
        int chipsToTake = winner.getChips().chipsInPot / numberOfPlayersToSplit;
        for (Player player : this.activePlayers) {
            if (player == winner) continue;
            int takenChips = player.removeChipsFromPot(chipsToTake);
            winner.addChips(takenChips);
        }
        winner.removeChipsFromPot(chipsToTake);
        winner.addChips(chipsToTake);
    }

    private int[] divideChips(int chips, int numberOfPlayersToSplit) {
        int[] splittedChips = new int[numberOfPlayersToSplit];
        for (int i = 0; i < numberOfPlayersToSplit; i++) {
            int playerChips = chips / (numberOfPlayersToSplit - i);
            splittedChips[i] = playerChips;
            chips -= playerChips;
        }
        return splittedChips;
    }
}
