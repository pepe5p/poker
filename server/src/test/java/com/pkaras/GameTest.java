package com.pkaras;

import com.pkaras.enums.GamePhase;
import com.pkaras.enums.PlayerAction;
import com.pkaras.enums.Rank;
import com.pkaras.enums.Suite;
import com.pkaras.exceptions.ImproperHandCardsNumberException;
import com.pkaras.exceptions.PokerException;
import com.pkaras.exceptions.ShuffleIncompleteDeckException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {

    static Game game;
    static SocketChannel socketChannel;

    static {
        try {
            socketChannel = SocketChannel.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void beforeEachTest() throws ShuffleIncompleteDeckException {
        GameTest.game = new Game(2137, 25, 50);
    }

    @DisplayName("Splitting Chips")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(SplittingChipsArgsProvider.class)
    void testSplittingChips(
        String testName,
        SplittingChipsTestData splittingChipsTestData
    ) {
        int numberOfPlayers = splittingChipsTestData.pot.size();
        ArrayList<Player> players = new ArrayList<>(numberOfPlayers);
        for (Chips chips : splittingChipsTestData.pot) {
            players.add(new Player("Marek", chips));
        }
        assertTrue(game.enqueuePlayers(players));
        for (Player player : game.players) {
            if (player.folded) player.folded = false;
        }

        ArrayList<ArrayList<Player>> playersRanking = new ArrayList<>();
        for (ArrayList<Integer> group : splittingChipsTestData.playersIndexRanking) {
            ArrayList<Player> playersGroup = new ArrayList<>();
            for (Integer index : group) playersGroup.add(players.get(index));
            playersRanking.add(new ArrayList<>(playersGroup));
        }
        game.splitChips(playersRanking);

        for (int i = 0; i < numberOfPlayers; i++) {
            Chips actualChips = game.players.get(i).chips;
            Chips expectedChips = splittingChipsTestData.expectedChips.get(i);
            assertEquals(expectedChips.chipsInStack, actualChips.chipsInStack);
            assertEquals(expectedChips.chipsInGame, actualChips.chipsInGame);
            assertEquals(expectedChips.chipsInPot, actualChips.chipsInPot);
        }
    }

    @DisplayName("Getting Players Ranking")
    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(PlayersRankingArgsProvider.class)
    void testGettingPlayersRanking(
        String testName,
        PlayersRankingTestData playersRankingTestData
    ) throws ImproperHandCardsNumberException {
        game.tableCards = playersRankingTestData.tableCards;
        int numberOfPlayers = playersRankingTestData.cards.size();
        ArrayList<Player> players = new ArrayList<>(numberOfPlayers);
        for (int i = 0; i < numberOfPlayers; i++) {
            Player player = new Player("Maciej", playersRankingTestData.chips.get(i));
            players.add(player);
            player.hand.setCards(playersRankingTestData.cards.get(i));
        }
        assertTrue(game.enqueuePlayers(players));
        for (Player player : game.players) {
            if (player.folded) player.folded = false;
        }

        ArrayList<ArrayList<Player>> playersRanking = new ArrayList<>(game.getPlayersRanking());

        int actualPlayersRankingSize = playersRanking.size();
        int expectedPlayersRankingSize = playersRankingTestData.playersIndexRanking.size();
        assertEquals(actualPlayersRankingSize, expectedPlayersRankingSize);
        for (int i = 0; i < actualPlayersRankingSize; i++) {
            ArrayList<Player> actualPlayersGroup = playersRanking.get(i);
            ArrayList<Integer> expectedPlayersGroup = playersRankingTestData.playersIndexRanking.get(i);
            int actualPlayersGroupSize = actualPlayersGroup.size();
            int expectedPlayersGroupSize = expectedPlayersGroup.size();
            assertEquals(actualPlayersGroupSize, expectedPlayersGroupSize);
            for (int j = 0; j < actualPlayersGroupSize; j++) {
                Player expectedPlayer = players.get(expectedPlayersGroup.get(j));
                assertEquals(actualPlayersGroup.get(j), expectedPlayer);
            }
        }
    }

    @DisplayName("Getting Players Ranking with Folded")
    @Test
    void testGettingPlayersRanking() throws ImproperHandCardsNumberException {
        Player player1 = new Player("Borubar", new Chips(0));
        Player player2 = new Player("Boruc", new Chips(0));
        player1.hand.setCards(
            new ArrayList<>(List.of(
                new Card(Rank.ACE, Suite.SPADES),
                new Card(Rank.JACK, Suite.SPADES)
            ))
        );
        player2.hand.setCards(
            new ArrayList<>(List.of(
                new Card(Rank.KING, Suite.SPADES),
                new Card(Rank.JACK, Suite.DIAMONDS)
            ))
        );

        assertTrue(game.enqueuePlayer(player1));
        assertTrue(game.enqueuePlayer(player2));

        for (Player player : game.players) {
            if (player.folded) player.folded = false;
        }

        player1.folded = true;

        ArrayList<ArrayList<Player>> expectedRanking = new ArrayList<>(List.of(
            new ArrayList<>(List.of(player2)),
            new ArrayList<>(List.of(player1))
        ));

        ArrayList<ArrayList<Player>> playersRanking = new ArrayList<>(game.getPlayersRanking());
        assertEquals(expectedRanking, playersRanking);
    }

    @DisplayName("Starting Game")
    @ParameterizedTest(name = "isOwner={0}")
    @ValueSource(booleans = {true, false})
    void testStartingGame(boolean isOwner) throws PokerException {
        Player player1 = new Player("Jan Paweł", new Chips(2137));
        Player player2 = new Player("Jan Paweł", new Chips(2137));
        game.players.add(player1);
        game.players.add(player2);
        if (isOwner) game.owner = player1;
        GameResponse gameResponse = game.performAction(PlayerAction.START_GAME, player1, "");
        if (isOwner) {
            assertEquals("game started", gameResponse.messageToGame());
            assertTrue(gameResponse.messageToPlayers().get(player1).startsWith("not your turn"));
            assertTrue(gameResponse.messageToPlayers().get(player2).startsWith("your turn"));
        }
        else {
            assertEquals("you must be owner to start", gameResponse.messageToPlayers().get(player1));
            Assertions.assertNull(gameResponse.messageToPlayers().get(player2));
            assertEquals("", gameResponse.messageToGame());
        }
    }

    @DisplayName("Simple Game Scenario")
    @Test
    void testGameScenario() throws PokerException {
        Player player1 = new Player("Jan Paweł", new Chips(2137));
        Player player2 = new Player("Jan Paweł II", new Chips(2137));
        game.players.add(player1);
        game.players.add(player2);
        game.owner = player1;

        Assertions.assertSame(GamePhase.UNSTARTED, game.gamePhase);

        GameResponse createResponse = game.performAction(PlayerAction.START_GAME, player1, "");
        Assertions.assertSame(GamePhase.PRE_FLOP, game.gamePhase);
        assertTrue(createResponse.messageToPlayers().get(player1).startsWith("not your turn"));
        assertTrue(createResponse.messageToPlayers().get(player2).startsWith("your turn"));
        assertEquals("game started", createResponse.messageToGame());

        GameResponse raiseResponse = game.performAction(PlayerAction.RAISE, player2, "1000");
        Assertions.assertSame(GamePhase.PRE_FLOP, game.gamePhase);
        assertEquals("Jan Paweł II raise to 1000\n", raiseResponse.messageToGame());
        assertTrue(raiseResponse.messageToPlayers().get(player1).startsWith("your turn"));
        assertTrue(raiseResponse.messageToPlayers().get(player2).startsWith("not your turn"));

        GameResponse callResponse = game.performAction(PlayerAction.CALL, player1, "");
        Assertions.assertSame(GamePhase.FLOP, game.gamePhase);
        assertEquals("Jan Paweł call 875\n", callResponse.messageToGame());
        assertTrue(callResponse.messageToPlayers().get(player1).startsWith("not your turn"));
        assertTrue(callResponse.messageToPlayers().get(player2).startsWith("your turn"));

        GameResponse allInResponse = game.performAction(PlayerAction.ALL_IN, player2, "");
        Assertions.assertSame(GamePhase.FLOP, game.gamePhase);
        assertEquals("Jan Paweł II all-in 2137\n", allInResponse.messageToGame());
        assertTrue(allInResponse.messageToPlayers().get(player1).startsWith("your turn"));
        assertTrue(allInResponse.messageToPlayers().get(player2).startsWith("not your turn"));

        GameResponse foldResponse = game.performAction(PlayerAction.FOLD, player1, "");
        Assertions.assertSame(GamePhase.PRE_FLOP, game.gamePhase);
        assertTrue(foldResponse.messageToGame().startsWith("Jan Paweł fold"));
        assertTrue(foldResponse.messageToGame().endsWith("[Jan Paweł II] won"));
        assertTrue(foldResponse.messageToPlayers().get(player1).startsWith("your turn"));
        assertTrue(foldResponse.messageToPlayers().get(player2).startsWith("not your turn"));
    }

    @DisplayName("All-ins Scenario")
    @Test
    void testAllInsScenario() throws PokerException {
        Player player1 = new Player("Jan Paweł", new Chips(2137));
        Player player2 = new Player("Jan Paweł II", new Chips(2137));
        game.players.add(player1);
        game.players.add(player2);
        game.owner = player1;

        Assertions.assertSame(GamePhase.UNSTARTED, game.gamePhase);
        game.performAction(PlayerAction.START_GAME, player1, "");
        Assertions.assertSame(GamePhase.PRE_FLOP, game.gamePhase);

        game.performAction(PlayerAction.ALL_IN, player2, "");
        Assertions.assertSame(GamePhase.PRE_FLOP, game.gamePhase);

        game.performAction(PlayerAction.CALL, player1, "");
        Assertions.assertSame(GamePhase.PRE_FLOP, game.gamePhase);
    }

    private record SplittingChipsTestData(
        ArrayList<Chips> pot,
        ArrayList<ArrayList<Integer>> playersIndexRanking,
        ArrayList<Chips> expectedChips
    ) {}

    private record PlayersRankingTestData(
        ArrayList<ArrayList<Card>> cards,
        ArrayList<Card> tableCards,
        ArrayList<Chips> chips,
        ArrayList<ArrayList<Integer>> playersIndexRanking
    ) {}

    private static class SplittingChipsArgsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                Arguments.of(
                    "Simple 2 Players Game",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 0, 1000),
                            new Chips(3000, 0, 2000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0)),
                            new ArrayList<>(List.of(1))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(6000, 0, 0),
                            new Chips(4000, 0, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "Simple 2 Players Game",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 0, 1000),
                            new Chips(3000, 0, 2000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(1)),
                            new ArrayList<>(List.of(0))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(4000, 0, 0),
                            new Chips(6000, 0, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "Simple 3 Players Game",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 0, 1000),
                            new Chips(6000, 0, 1500),
                            new Chips(3000, 0, 2000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0)),
                            new ArrayList<>(List.of(1)),
                            new ArrayList<>(List.of(2))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(7000, 0, 0),
                            new Chips(7000, 0, 0),
                            new Chips(3500, 0, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "Simple 3 Players Game",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 0, 1000),
                            new Chips(6000, 0, 1500),
                            new Chips(3000, 0, 2000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(2)),
                            new ArrayList<>(List.of(0)),
                            new ArrayList<>(List.of(1))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(4000, 0, 0),
                            new Chips(6000, 0, 0),
                            new Chips(7500, 0, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "2 Players Split",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 0, 1000),
                            new Chips(4000, 0, 1000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0, 1))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(5000, 0, 0),
                            new Chips(5000, 0, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "2 Players Split",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 0, 1000),
                            new Chips(4000, 0, 1000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0, 1))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(5000, 0, 0),
                            new Chips(5000, 0, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "2 Players Split",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 0, 1000),
                            new Chips(4000, 0, 2000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0, 1))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(5000, 0, 0),
                            new Chips(6000, 0, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "3 Players Split",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 0, 1000),
                            new Chips(4000, 0, 1000),
                            new Chips(4000, 0, 1000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0, 1, 2))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(5000, 0, 0),
                            new Chips(5000, 0, 0),
                            new Chips(5000, 0, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "It's complicated...",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 0, 500),
                            new Chips(4000, 0, 1000),
                            new Chips(4000, 0, 1000),
                            new Chips(4000, 0, 2000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0)),
                            new ArrayList<>(List.of(1, 2)),
                            new ArrayList<>(List.of(3))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(6000, 0, 0),
                            new Chips(4750, 0, 0),
                            new Chips(4750, 0, 0),
                            new Chips(5000, 0, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "It's complicated...",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 0, 1000),
                            new Chips(4500, 0, 500),
                            new Chips(1000, 0, 25)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(1, 0)),
                            new ArrayList<>(List.of(2))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(5013, 0, 0),
                            new Chips(5012, 0, 0),
                            new Chips(1000, 0, 0)
                        ))
                    )
                )
            );
        }
    }

    private static class PlayersRankingArgsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                Arguments.of(
                    "High Card Win 2 Players",
                    new PlayersRankingTestData(
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(
                                new Card(Rank.ACE, Suite.SPADES),
                                new Card(Rank.JACK, Suite.SPADES)
                            )),
                            new ArrayList<>(List.of(
                                new Card(Rank.KING, Suite.CLUBS),
                                new Card(Rank.JACK, Suite.CLUBS)
                            ))
                        )),
                        new ArrayList<>(List.of(
                            new Card(Rank.TEN, Suite.CLUBS),
                            new Card(Rank.FIVE, Suite.HEARTS),
                            new Card(Rank.FOUR, Suite.DIAMONDS),
                            new Card(Rank.THREE, Suite.CLUBS),
                            new Card(Rank.DEUCE, Suite.HEARTS)
                        )),
                        new ArrayList<>(List.of(
                            new Chips(0, 0, 0),
                            new Chips(0, 0, 0)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0)),
                            new ArrayList<>(List.of(1))
                        ))
                    )
                ),
                Arguments.of(
                    "High Card Split 2 Players",
                    new PlayersRankingTestData(
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(
                                new Card(Rank.ACE, Suite.SPADES),
                                new Card(Rank.JACK, Suite.SPADES)
                            )),
                            new ArrayList<>(List.of(
                                new Card(Rank.ACE, Suite.CLUBS),
                                new Card(Rank.JACK, Suite.CLUBS)
                            ))
                        )),
                        new ArrayList<>(List.of(
                            new Card(Rank.TEN, Suite.CLUBS),
                            new Card(Rank.FIVE, Suite.HEARTS),
                            new Card(Rank.FOUR, Suite.DIAMONDS),
                            new Card(Rank.THREE, Suite.CLUBS),
                            new Card(Rank.DEUCE, Suite.HEARTS)
                        )),
                        new ArrayList<>(List.of(
                            new Chips(0, 0, 1000),
                            new Chips(0, 0, 2000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0, 1))
                        ))
                    )
                ),
                Arguments.of(
                    "Pair Split 2 Players",
                    new PlayersRankingTestData(
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(
                                new Card(Rank.ACE, Suite.SPADES),
                                new Card(Rank.JACK, Suite.SPADES)
                            )),
                            new ArrayList<>(List.of(
                                new Card(Rank.ACE, Suite.CLUBS),
                                new Card(Rank.JACK, Suite.CLUBS)
                            ))
                        )),
                        new ArrayList<>(List.of(
                            new Card(Rank.ACE, Suite.CLUBS),
                            new Card(Rank.JACK, Suite.CLUBS),
                            new Card(Rank.FIVE, Suite.HEARTS),
                            new Card(Rank.FOUR, Suite.DIAMONDS),
                            new Card(Rank.THREE, Suite.DIAMONDS)
                        )),
                        new ArrayList<>(List.of(
                            new Chips(0, 0, 2000),
                            new Chips(0, 0, 1000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(1, 0))
                        ))
                    )
                ),
                Arguments.of(
                    "High Card Win 3 Players",
                    new PlayersRankingTestData(
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(
                                new Card(Rank.ACE, Suite.SPADES),
                                new Card(Rank.JACK, Suite.SPADES)
                            )),
                            new ArrayList<>(List.of(
                                new Card(Rank.KING, Suite.CLUBS),
                                new Card(Rank.JACK, Suite.CLUBS)
                            )),
                            new ArrayList<>(List.of(
                                new Card(Rank.QUEEN, Suite.DIAMONDS),
                                new Card(Rank.JACK, Suite.DIAMONDS)
                            ))
                        )),
                        new ArrayList<>(List.of(
                            new Card(Rank.TEN, Suite.DIAMONDS),
                            new Card(Rank.THREE, Suite.DIAMONDS),
                            new Card(Rank.DEUCE, Suite.CLUBS)
                        )),
                        new ArrayList<>(List.of(
                            new Chips(0, 0, 0),
                            new Chips(0, 0, 0),
                            new Chips(0, 0, 0)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0)),
                            new ArrayList<>(List.of(1)),
                            new ArrayList<>(List.of(2))
                        ))
                    )
                ),
                Arguments.of(
                    "Win and Split",
                    new PlayersRankingTestData(
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(
                                new Card(Rank.ACE, Suite.SPADES),
                                new Card(Rank.JACK, Suite.SPADES)
                            )),
                            new ArrayList<>(List.of(
                                new Card(Rank.KING, Suite.CLUBS),
                                new Card(Rank.JACK, Suite.CLUBS)
                            )),
                            new ArrayList<>(List.of(
                                new Card(Rank.KING, Suite.DIAMONDS),
                                new Card(Rank.JACK, Suite.DIAMONDS)
                            ))
                        )),
                        new ArrayList<>(List.of(
                            new Card(Rank.TEN, Suite.DIAMONDS),
                            new Card(Rank.THREE, Suite.DIAMONDS),
                            new Card(Rank.DEUCE, Suite.CLUBS)
                        )),
                        new ArrayList<>(List.of(
                            new Chips(0, 0, 0),
                            new Chips(0, 0, 1000),
                            new Chips(0, 0, 0)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0)),
                            new ArrayList<>(List.of(2, 1))
                        ))
                    )
                )
            );
        }
    }
}