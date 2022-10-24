import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {

    static Game game;

    @BeforeEach
    public void beforeEachTest() {
        GameTest.game = new Game(25, 50);
    }

    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(SplittingChipsArgsProvider.class)
    public void testFinishingRound(
        String testName,
        SplittingChipsTestData splittingChipsTestData
    ) {
        int numberOfPlayers = splittingChipsTestData.pot.size();
        ArrayList<Player> players = new ArrayList<>(numberOfPlayers);
        for (Chips chips : splittingChipsTestData.pot) {
            players.add(new Player(chips));
        }
        boolean added = game.enqueuePlayers(players);
        assertTrue(added);
        game.addQueueToGame();

        ArrayList<ArrayList<Player>> playersRanking = new ArrayList<>();
        for (ArrayList<Integer> group : splittingChipsTestData.playersIndexRanking) {
            ArrayList<Player> playersGroup = new ArrayList<>();
            for (Integer index : group) playersGroup.add(players.get(index));
            playersRanking.add(new ArrayList<>(playersGroup));
        }
        game.splitChips(playersRanking);

        for (int i = 0; i < numberOfPlayers; i++) {
            Chips actualChips = game.activePlayers.get(i).getChips();
            Chips expectedChips = splittingChipsTestData.expectedChips.get(i);
            System.out.format(
                "Actual: %s | Expected: %s\n",
                actualChips.chipsInStack,
                expectedChips.chipsInStack
            );
            assertEquals(expectedChips.chipsInStack, actualChips.chipsInStack);
            assertEquals(expectedChips.chipsInPot, actualChips.chipsInPot);
        }
    }

    private record SplittingChipsTestData(
        ArrayList<Chips> pot,
        ArrayList<ArrayList<Integer>> playersIndexRanking,
        ArrayList<Chips> expectedChips
    ) {}

    private static class SplittingChipsArgsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                Arguments.of(
                    "Simple 2 Players Game",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 1000),
                            new Chips(3000, 2000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0)),
                            new ArrayList<>(List.of(1))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(6000, 0),
                            new Chips(4000, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "Simple 2 Players Game",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 1000),
                            new Chips(3000, 2000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(1)),
                            new ArrayList<>(List.of(0))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(4000, 0),
                            new Chips(6000, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "Simple 3 Players Game",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 1000),
                            new Chips(6000, 1500),
                            new Chips(3000, 2000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0)),
                            new ArrayList<>(List.of(1)),
                            new ArrayList<>(List.of(2))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(7000, 0),
                            new Chips(7000, 0),
                            new Chips(3500, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "Simple 3 Players Game",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 1000),
                            new Chips(6000, 1500),
                            new Chips(3000, 2000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(2)),
                            new ArrayList<>(List.of(0)),
                            new ArrayList<>(List.of(1))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(4000, 0),
                            new Chips(6000, 0),
                            new Chips(7500, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "2 Players Split",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 1000),
                            new Chips(4000, 1000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0, 1))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(5000, 0),
                            new Chips(5000, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "2 Players Split",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 1000),
                            new Chips(4000, 2000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0, 1))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(5000, 0),
                            new Chips(6000, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "3 Players Split",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 1000),
                            new Chips(4000, 1000),
                            new Chips(4000, 1000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0, 1, 2))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(4000, 0),
                            new Chips(4000, 0),
                            new Chips(4000, 0)
                        ))
                    )
                ),
                Arguments.of(
                    "It's complicated...",
                    new SplittingChipsTestData(
                        new ArrayList<>(List.of(
                            new Chips(4000, 500),
                            new Chips(4000, 1000),
                            new Chips(4000, 1000),
                            new Chips(4000, 2000)
                        )),
                        new ArrayList<>(List.of(
                            new ArrayList<>(List.of(0)),
                            new ArrayList<>(List.of(1, 2)),
                            new ArrayList<>(List.of(3))
                        )),
                        new ArrayList<>(List.of(
                            new Chips(6000, 0),
                            new Chips(4750, 0),
                            new Chips(4750, 0),
                            new Chips(5000, 0)
                        ))
                    )
                )
            );
        }
    }
}