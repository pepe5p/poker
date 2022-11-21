package com.pkaras;

import com.pkaras.enums.GamePhase;
import com.pkaras.enums.PlayerAction;
import com.pkaras.exceptions.*;

import java.util.*;

public class Game {
    static final int NUMBER_OF_CARDS_ON_TABLE = 5;
    static final int MIN_PLAYERS_NUMBER = 2;
    static final int MAX_PLAYERS_NUMBER = 9;
    final int id;
    final int ante;
    final int sb;
    final int bb;
    Player owner;
    boolean gameStopped = false;
    int dealerIndex = 0;
    int smallBlindIndex = 0;
    int bigBlindIndex = 0;
    int currentTurnIndex = 0;
    int turnCount = 0;
    int lastRaise = 0;
    GamePhase gamePhase = GamePhase.UNSTARTED;
    static final GamePhase[] gamePhases = GamePhase.values();
    Deck deck;
    ArrayList<Card> tableCards = new ArrayList<>(NUMBER_OF_CARDS_ON_TABLE);
    ArrayList<Player> players = new ArrayList<>(MAX_PLAYERS_NUMBER);
    ArrayList<Player> scheduledRemoves = new ArrayList<>(MIN_PLAYERS_NUMBER);

    public Game(int id, int ante, int smallBlind) throws ShuffleIncompleteDeckException {
        this(id, ante, smallBlind, null);
    }

    public Game(int id, int ante, int smallBlind, Player owner) throws ShuffleIncompleteDeckException {
        this.id = id;
        this.ante = ante;
        this.sb = smallBlind;
        this.bb = smallBlind * 2;
        this.owner = owner;
        this.deck = new Deck();
    }

    private int getNextActivePlayerIndex(int index) {
        int activePlayerIndex = getNextPlayerIndex(index);
        Player player = players.get(activePlayerIndex);
        while (player.folded || player.allIn) {
            activePlayerIndex = getNextPlayerIndex(activePlayerIndex);
            player = players.get(activePlayerIndex);
        }
        return activePlayerIndex;
    }

    private int getNextPlayerIndex(int index) {
        return (index + 1) % this.players.size();
    }

    public boolean enqueuePlayers(List<Player> players) {
        for (Player player : players) {
            boolean added = this.enqueuePlayer(player);
            if (!added) return false;
        }
        return true;
    }

    public boolean enqueuePlayer(Player player) {
        if (players.isEmpty()) owner = player;
        if (players.contains(player)) {
            scheduledRemoves.remove(player);
            return true;
        }
        if (players.size() >= MAX_PLAYERS_NUMBER) return false;
        fold(player);
        return players.add(player);
    }

    public void removePlayer(Player player) {
        if (gamePhase == GamePhase.UNSTARTED) {
            players.remove(player);
            return;
        }
        this.scheduledRemoves.add(player);
        try {
            this.scheduleMove(player, PlayerAction.FOLD, null);
        }
        catch (PokerException e) {
            Console.print(e.getMessage());
        }
    }

    GameResponse performAction(
        PlayerAction action,
        Player player,
        String allArguments
    ) throws PokerException {
        GameResponse gameResponse;
        switch (action) {
            case START_GAME -> gameResponse = startGame(player);
            case CALL, RAISE, FOLD, ALL_IN -> gameResponse = scheduleMove(player, action, allArguments);
            case CLEAR_ACTION -> gameResponse = clearAction(player);
            case CHECK_STATUS -> gameResponse = checkGameStatus(player);
            default -> throw new UnknownGameActionException();
        }
        return prepareResponse(gameResponse);
    }

    GameResponse startGame(Player player) throws PokerException {
        if (this.owner != player) {
            return new GameResponse(
                Map.of(player, "you must be owner to start"),
                ""
            );
        }
        if (gamePhase != GamePhase.UNSTARTED) {
            return new GameResponse(
                Map.of(player, "game has already started"),
                ""
            );
        }
        if (this.players.size() < MIN_PLAYERS_NUMBER) {
            return new GameResponse(
                null,
                "game must have at least " + MIN_PLAYERS_NUMBER + "players"
            );
        }
        dealerIndex = -1;
        String gameResponse = finishPhase();
        return new GameResponse(generateMessageToPlayers(), gameResponse);
    }

    GameResponse scheduleMove(Player player, PlayerAction action, String allArguments) throws PokerException {
        if (player.folded) {
            return new GameResponse(
                Map.of(player, "you already folded"),
                ""
            );
        }
        if (player.allIn) {
            return new GameResponse(
                Map.of(player, "you are already all-in"),
                ""
            );
        }
        String actionScheduleString = player.setScheduledAction(action, allArguments);
        if (players.get(currentTurnIndex).scheduledAction == null) {
            return new GameResponse(
                Map.of(player, actionScheduleString),
                ""
            );
        }
        return makeMoves();
    }

    GameResponse clearAction(Player player) {
        player.scheduledAction = null;
        return new GameResponse(
            Map.of(player, "action cleared"),
            ""
        );
    }

    GameResponse checkGameStatus(Player player) {
        return new GameResponse(
            Map.of(player, gameStatusResponse(player)),
            ""
        );
    }

    private HashMap<Player, String> generateMessageToPlayers() {
        HashMap<Player, String> messageToPlayers = new HashMap<>(players.size());
        for (Player player : players) messageToPlayers.put(player, gameStatusResponse(player));
        return messageToPlayers;
    }

    String gameStatusResponse(Player player) {
        if (gamePhase == GamePhase.UNSTARTED) return "game has not started yet";
        StringBuilder messageToPlayer = new StringBuilder();
        String turnResponse = players.get(currentTurnIndex) == player ? "your turn" : "not your turn";
        messageToPlayer
            .append(turnResponse)
            .append("\n");
        for (Card card : tableCards) messageToPlayer.append(card.toString());
        if (!tableCards.isEmpty()) messageToPlayer.append(" on table\n");
        messageToPlayer
            .append(playersSummary())
            .append(getPot())
            .append(" in pot / ")
            .append(lastRaise - (int) player.chips.chipsInGame)
            .append(" to call\n")
            .append(player.hand.toString());
        return messageToPlayer.toString();
    }

    private String playersSummary() {
        StringBuilder summaryBuilder = new StringBuilder();
        for (Player gamePlayer : players) {
            if (gamePlayer == players.get(dealerIndex)) summaryBuilder.append("DE ");
            else if (gamePlayer == players.get(smallBlindIndex)) summaryBuilder.append("SB ");
            else if (gamePlayer == players.get(bigBlindIndex)) summaryBuilder.append("BB ");
            else summaryBuilder.append("   ");
            if (gamePlayer == players.get(currentTurnIndex)) summaryBuilder.append("* ");
            else if (gamePlayer.folded) summaryBuilder.append("F ");
            else if (gamePlayer.allIn) summaryBuilder.append("A ");
            else summaryBuilder.append("  ");
            summaryBuilder.append(gamePlayer.toString());
        }
        return summaryBuilder.toString();
    }

    private int getPot() {
        int chipsInPot = 0;
        for (Player player : this.players) {
            chipsInPot += player.chips.chipsInGame;
            chipsInPot += player.chips.chipsInPot;
        }
        return chipsInPot;
    }

    GameResponse makeMoves() throws PokerException {
        StringBuilder messageToGameBuilder = new StringBuilder();
        Player player = players.get(currentTurnIndex);
        boolean areMinTwoActivePlayers = checkAreMinTwoActivePlayers();
        boolean shouldPhaseFinish = false;
        String moveValidationResponse = "";
        while (player.scheduledAction != null  && areMinTwoActivePlayers && !shouldPhaseFinish) {
            moveValidationResponse = validateMove(player);
            if (!moveValidationResponse.isEmpty()) break;
            String moveResponse = makeMove(player);
            turnCount--;
            messageToGameBuilder.append(moveResponse).append("\n");
            areMinTwoActivePlayers = checkAreMinTwoActivePlayers();
            shouldPhaseFinish = checkIfPhaseShouldBeFinished();
            if (areMinTwoActivePlayers && !shouldPhaseFinish) {
                currentTurnIndex = getNextActivePlayerIndex(currentTurnIndex);
                player = players.get(currentTurnIndex);
            }
        }
        if (!moveValidationResponse.isEmpty()) {
            player.scheduledAction = null;
            return new GameResponse(
                Map.of(player, moveValidationResponse),
                messageToGameBuilder.toString()
            );
        }
        if (!areMinTwoActivePlayers || everyoneAllIn()) messageToGameBuilder.append(forceFinishRound());
        else if (shouldPhaseFinish) messageToGameBuilder.append(finishPhase());
        return new GameResponse(
            generateMessageToPlayers(),
            messageToGameBuilder.toString()
        );
    }

    private boolean checkAreMinTwoActivePlayers() {
        int activePlayers = 0;
        for (Player player : players) if (!player.folded) activePlayers++;
        return activePlayers > 1;
    }

    private boolean checkIfPhaseShouldBeFinished() {
        if (turnCount > 0) return false;
        for (Player player : players) {
            if (player.folded || player.allIn) continue;
            if (player.chips.chipsInGame != lastRaise) return false;
        }
        return true;
    }

    private boolean everyoneAllIn() {
        int notAllIns = 0;
        for (Player player : players) {
            if (player.allIn || player.folded) continue;
            notAllIns++;
            if (player.chips.chipsInGame < lastRaise) return false;
        }
        return notAllIns < 2;
    }

    @SuppressWarnings("java:S108")
    private String forceFinishRound() throws PokerException {
        switch (gamePhase) {
            case PRE_FLOP -> showCards(5);
            case FLOP -> showCards(2);
            case TURN -> showCards(1);
            default -> {}
        }
        gamePhase = GamePhase.RIVER;
        return finishPhase();
    }

    private String validateMove(Player player) throws UnknownGameActionException {
        String moveValidationResponse;
        switch (player.scheduledAction.action()) {
            case CALL, FOLD, ALL_IN -> moveValidationResponse = "";
            case RAISE -> moveValidationResponse = validateRaise(player, player.scheduledAction.value());
            default -> throw new UnknownGameActionException();
        }
        return moveValidationResponse;
    }

    private String validateRaise(Player player, Integer value) {
        if (value > player.chips.chipsInStack + player.chips.chipsInGame) return "you do not have enough chips";
        if (value < lastRaise * 2) return "too small raise";
        return "";
    }

    String makeMove(Player player) throws UnknownGameActionException {
        String moveResponse;
        switch (player.scheduledAction.action()) {
            case CALL -> moveResponse = performCall(player);
            case RAISE -> moveResponse = performRaise(player, player.scheduledAction.value());
            case FOLD -> moveResponse = performFold(player);
            case ALL_IN -> moveResponse = performAllIn(player);
            default -> throw new UnknownGameActionException();
        }
        player.scheduledAction = null;
        return moveResponse;
    }

    private String performCall(Player player) {
        int chipsToCall = lastRaise - (int) player.chips.chipsInGame;
        int takenChips = player.chips.transferChipsIntoGame(chipsToCall);
        if (takenChips < chipsToCall || player.chips.chipsInStack == 0) {
            allIn(player);
            return String.join(
                " ",
                player.name,
                PlayerAction.ALL_IN.actionName,
                String.valueOf((int) player.chips.chipsInGame)
            );
        }
        return String.join(
            " ",
            player.name,
            PlayerAction.CALL.actionName,
            String.valueOf(takenChips)
        );
    }

    private String performRaise(Player player, Integer value) {
        lastRaise = value;
        player.chips.transferChipsIntoGame(value - player.chips.chipsInGame);
        if (player.chips.chipsInStack == 0) {
            allIn(player);
            return String.join(
                " ",
                player.name,
                PlayerAction.ALL_IN.actionName,
                String.valueOf((int) player.chips.chipsInGame)
            );
        }
        return String.join(
            " ",
            player.name,
            PlayerAction.RAISE.actionName,
            "to",
            String.valueOf(value)
        );
    }

    private String performFold(Player player) {
        fold(player);
        return String.join(
            " ",
            player.name,
            PlayerAction.FOLD.actionName
        );
    }

    void fold(Player player) {
        player.folded = true;
    }

    private String performAllIn(Player player) {
        player.chips.transferChipsIntoGame(player.chips.chipsInStack);
        int chips = (int) player.chips.chipsInGame + (int) player.chips.chipsInPot;
        boolean isRaise = lastRaise < chips;
        lastRaise = isRaise ? chips : lastRaise;
        allIn(player);
        return String.join(
            " ",
            player.name,
            PlayerAction.ALL_IN.actionName,
            String.valueOf(chips)
        );
    }

    void allIn(Player player) {
        player.allIn = true;
    }

    private GameResponse prepareResponse(GameResponse gameResponse) {
        if (gameResponse.messageToPlayers() == null) {
            Player player = players.get(currentTurnIndex);
            return new GameResponse(
                Map.of(player, "your turn"),
                gameResponse.messageToGame()
            );
        }
        return gameResponse;
    }

    String finishPhase() throws PokerException {
        String response = "";
        for (Player player : players) {
            player.chips.transferChipsToPot();
            player.scheduledAction = null;
        }
        turnCount = getNumberOfActivePlayers();
        switch (gamePhase) {
            case UNSTARTED -> response = setUpRound();
            case PRE_FLOP -> showCards(3);
            case FLOP, TURN -> showCards(1);
            case RIVER -> response = finishRound();
            default -> throw new UnknownGamePhaseException();
        }
        gamePhase = gamePhases[(gamePhase.value + 1) % gamePhases.length];
        currentTurnIndex = gamePhase.initialLastIndexOfRound == 0 ? smallBlindIndex : getNextActivePlayerIndex(bigBlindIndex);
        return response;
    }

    private int getNumberOfActivePlayers() {
        int numberOfActivePlayers = 0;
        for (Player player : players) {
            if (!player.allIn && !player.folded) numberOfActivePlayers++;
        }
        return numberOfActivePlayers;
    }

    public String setUpRound() throws ImproperHandCardsNumberException, EmptyDeckException {
        for (Player player : players) {
            player.hand = deck.getHand();
            player.allIn = false;
            player.folded = player.chips.chipsInStack == 0;
        }
        moveDealer();
        takeAnte();
        takeBlinds();
        lastRaise = bb + ante;
        return "game started";
    }

    private void moveDealer() {
        dealerIndex = getNextActivePlayerIndex(dealerIndex);
        smallBlindIndex = getNextActivePlayerIndex(dealerIndex);
        bigBlindIndex = getNextActivePlayerIndex(smallBlindIndex);
    }

    private void takeAnte() {
        for (Player player : this.players) player.chips.transferChipsIntoGame(this.ante);
    }

    private void takeBlinds() {
        performRaise(players.get(smallBlindIndex), sb + ante);
        performRaise(players.get(bigBlindIndex), bb + ante);
    }

    private void showCards(int numberOfCards) throws EmptyDeckException {
        ArrayList<Card> cards = new ArrayList<>(deck.getTopCards(numberOfCards));
        tableCards.addAll(cards);
        lastRaise = 0;
        for (Player player : players) player.hand.evaluateHand(tableCards);
    }

    public String finishRound() throws PokerException {
        ArrayList<ArrayList<Player>> playersRanking = new ArrayList<>(getPlayersRanking());
        String roundSummary = roundSummary(playersRanking.get(0));
        splitChips(playersRanking);
        for (Player player : players) deck.returnHand(player.hand);
        deck.returnCards(tableCards);
        tableCards.clear();
        deck.shuffle();
        for (Player player : scheduledRemoves) players.remove(player);
        if (gameStopped) return "game stopped";
        setUpRound();
        gamePhase = GamePhase.UNSTARTED;
        return roundSummary;
    }

    private String roundSummary(ArrayList<Player> winners) {
        if (gamePhase != GamePhase.RIVER) return "game has not finished yet";
        StringBuilder summaryBuilder = new StringBuilder();
        for (Card card : tableCards) summaryBuilder.append(card.toString("\u001B[34m"));
        if (!tableCards.isEmpty()) summaryBuilder.append(" on table\n");
        summaryBuilder.append(playersHandsSummary());
        if (winners.size() == 1) summaryBuilder.append(winners.get(0).nameString()).append(" won");
        else {
            summaryBuilder.append("splitted among ");
            for (Player player : winners) summaryBuilder.append(player.nameString()).append(" ");
        }
        return summaryBuilder.toString();
    }

    private String playersHandsSummary() {
        boolean everyoneFolded = everyoneFolded();
        StringBuilder summaryBuilder = new StringBuilder();
        for (Player player : players) {
            if (player == players.get(dealerIndex)) summaryBuilder.append("DE   ");
            else if (player == players.get(smallBlindIndex)) summaryBuilder.append("SB   ");
            else if (player == players.get(bigBlindIndex)) summaryBuilder.append("BB   ");
            else summaryBuilder.append("     ");
            summaryBuilder.append(player.nameString());
            if (!player.folded && everyoneFolded) summaryBuilder.append(" did not show hand");
            else if (!player.folded) summaryBuilder.append(" ").append(player.hand.toString("\u001B[34m"));
            else summaryBuilder.append(" folded");
            summaryBuilder.append("\n");
        }
        return summaryBuilder.toString();
    }

    private boolean everyoneFolded() {
        int notFolded = 0;
        for (Player player : players) if (!player.folded) notFolded++;
        return notFolded < 2;
    }

    public List<ArrayList<Player>> getPlayersRanking() {
        ArrayList<Player> playersSortedByHandValue = this.getPlayersSortedByHandValue();
        ArrayList<ArrayList<Player>> groupedPlayers = this.groupPlayers(playersSortedByHandValue);
        this.sortPlayersInExAequoGroups(groupedPlayers);
        return groupedPlayers;
    }

    private ArrayList<Player> getPlayersSortedByHandValue() {
        ArrayList<Player> playersSortedByHandValue = new ArrayList<>(this.players.size());
        this.players.stream().sorted((p1, p2) -> {
                if (p1.folded) return 1;
                if (p2.folded) return -1;
                if (p1.hand.getHandValue().isLesserThan(p2.hand.getHandValue())) return 1;
                if (p1.hand.getHandValue().isGreaterThan(p2.hand.getHandValue())) return -1;
                return 0;
            })
            .forEachOrdered(playersSortedByHandValue::add);
        return playersSortedByHandValue;
    }

    private ArrayList<ArrayList<Player>> groupPlayers(ArrayList<Player> playersSortedByHandValue) {
        ArrayList<ArrayList<Player>> groupedPlayers = new ArrayList<>();
        Player previous = null;
        int previousIndex = -1;
        for (Player player : playersSortedByHandValue) {
            if (
                previous != null &&
                previous.hand.getHandValue().equals(player.hand.getHandValue())
            ) {
                groupedPlayers.get(previousIndex).add(player);
                continue;
            }
            ArrayList<Player> newGroup = new ArrayList<>(List.of(player));
            groupedPlayers.add(newGroup);
            previous = player;
            previousIndex = groupedPlayers.indexOf(newGroup);
        }
        return groupedPlayers;
    }

    private void sortPlayersInExAequoGroups(ArrayList<ArrayList<Player>> groupedPlayers) {
        for (ArrayList<Player> playersGroup : groupedPlayers) {
            playersGroup.sort(Comparator.comparingDouble(p -> p.chips.chipsInPot));
        }
    }

    public void splitChips(List<ArrayList<Player>> playersRanking) {
        for (ArrayList<Player> playersGroup : playersRanking) {
            int numberOfPlayersToSplit = playersGroup.size();
            for (Player player : playersGroup) {
                this.transferChips(player, numberOfPlayersToSplit);
                numberOfPlayersToSplit--;
            }
            int numberOfPlayersToSplitMiedziaki = playersGroup.size();
            int miedziaki = (int) Math.round(this.extractMiedziaki(playersGroup));
            int[] chipsToTake = this.divideChips(miedziaki, numberOfPlayersToSplitMiedziaki);
            for (int i = 0; i < numberOfPlayersToSplitMiedziaki; i++) {
                playersGroup.get(i).chips.addChipsToStack(chipsToTake[i]);
            }
        }
    }

    private void transferChips(Player winner, int numberOfPlayersToSplit) {
        double chipsToTake = winner.chips.chipsInPot / numberOfPlayersToSplit;
        for (Player player : this.players) {
            if (player == winner) continue;
            double takenChips = player.chips.removeChipsFromPot(winner.chips.chipsInPot, numberOfPlayersToSplit);
            winner.chips.addChipsToStack(takenChips);
        }
        winner.chips.returnChipsToStack(chipsToTake);
    }

    private double extractMiedziaki(ArrayList<Player> playersGroup) {
        double miedziaki = 0;
        for (Player player : playersGroup) {
            double playerChips = player.chips.chipsInStack;
            double dif = playerChips - (int) playerChips;
            player.chips.chipsInStack -= dif;
            miedziaki += dif;
        }
        return miedziaki;
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
