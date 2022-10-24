import enums.HandRanking;
import enums.Rank;
import enums.Suite;
import exceptions.ImproperHandCardsNumberException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class HandTest {

    static Hand hand;

    @BeforeAll
    public static void beforeAllTests() {
        HandTest.hand = new Hand();
    }

    @ParameterizedTest(name = "Grouping to {0}")
    @ArgumentsSource(GroupingArgsProvider.class)
    public void testGroupCards(
        String testName,
        GroupingTestData groupingTestData
    ) throws ImproperHandCardsNumberException {
        hand.setCards(groupingTestData.cards);
        hand.print();
        LinkedHashMap<Rank, Integer> groups = hand.groupCards();
        System.out.println(groups);
        assertEquals(
            groupingTestData.groups.entrySet().stream().toList(),
            groups.entrySet().stream().toList()
        );
    }

    @ParameterizedTest(name = "Evaluating {0}")
    @ArgumentsSource(EvaluatingArgsProvider.class)
    public void testEvaluate(
        String testName,
        EvaluatingTestData evaluatingTestData
    ) throws ImproperHandCardsNumberException {
        hand.setCards(evaluatingTestData.cards);
        hand.print();
        assertEquals(evaluatingTestData.handValue, hand.getValue());
    }

    private record GroupingTestData(
        ArrayList<Card> cards,
        LinkedHashMap<Rank, Integer> groups
    ) {}

    private record EvaluatingTestData(
        ArrayList<Card> cards,
        HandValue handValue
    ) {}

    private static class GroupingArgsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                Arguments.of(
                    "High Card",
                    new GroupingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SEVEN, Suite.SPADES),
                            new Card(Rank.FIVE, Suite.SPADES),
                            new Card(Rank.FOUR, Suite.SPADES),
                            new Card(Rank.THREE, Suite.SPADES),
                            new Card(Rank.DEUCE, Suite.DIAMONDS)
                        )),
                        new LinkedHashMap<>() {{
                            put(Rank.SEVEN, 1);
                            put(Rank.FIVE, 1);
                            put(Rank.FOUR, 1);
                            put(Rank.THREE, 1);
                            put(Rank.DEUCE, 1);
                        }}
                    )
                ),
                Arguments.of(
                    "Pair",
                    new GroupingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SEVEN, Suite.SPADES),
                            new Card(Rank.SEVEN, Suite.CLUBS),
                            new Card(Rank.THREE, Suite.SPADES),
                            new Card(Rank.FOUR, Suite.SPADES),
                            new Card(Rank.ACE, Suite.DIAMONDS)
                        )),
                        new LinkedHashMap<>() {{
                            put(Rank.SEVEN, 2);
                            put(Rank.ACE, 1);
                            put(Rank.FOUR, 1);
                            put(Rank.THREE, 1);
                        }}
                    )
                ),
                Arguments.of(
                    "Two Pairs",
                    new GroupingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SEVEN, Suite.SPADES),
                            new Card(Rank.SEVEN, Suite.DIAMONDS),
                            new Card(Rank.FOUR, Suite.SPADES),
                            new Card(Rank.FOUR, Suite.SPADES),
                            new Card(Rank.ACE, Suite.DIAMONDS)
                        )),
                        new LinkedHashMap<>() {{
                            put(Rank.SEVEN, 2);
                            put(Rank.FOUR, 2);
                            put(Rank.ACE, 1);
                        }}
                    )
                ),
                Arguments.of(
                    "Three of a Kind",
                    new GroupingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SEVEN, Suite.SPADES),
                            new Card(Rank.SEVEN, Suite.DIAMONDS),
                            new Card(Rank.SEVEN, Suite.CLUBS),
                            new Card(Rank.THREE, Suite.SPADES),
                            new Card(Rank.DEUCE, Suite.DIAMONDS)
                        )),
                        new LinkedHashMap<>() {{
                            put(Rank.SEVEN, 3);
                            put(Rank.THREE, 1);
                            put(Rank.DEUCE, 1);
                        }}
                    )
                ),
                Arguments.of(
                    "Full House",
                    new GroupingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SIX, Suite.SPADES),
                            new Card(Rank.SIX, Suite.CLUBS),
                            new Card(Rank.SIX, Suite.HEARTS),
                            new Card(Rank.THREE, Suite.SPADES),
                            new Card(Rank.THREE, Suite.DIAMONDS)
                        )),
                        new LinkedHashMap<>() {{
                            put(Rank.SIX, 3);
                            put(Rank.THREE, 2);
                        }}
                    )
                ),
                Arguments.of(
                    "Four of a Kind",
                    new GroupingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SIX, Suite.SPADES),
                            new Card(Rank.SIX, Suite.CLUBS),
                            new Card(Rank.SIX, Suite.HEARTS),
                            new Card(Rank.SIX, Suite.DIAMONDS),
                            new Card(Rank.ACE, Suite.DIAMONDS)
                        )),
                        new LinkedHashMap<>() {{
                            put(Rank.SIX, 4);
                            put(Rank.ACE, 1);
                        }}
                    )
                )
            );
        }
    }

    private static class EvaluatingArgsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                Arguments.of(
                    "High Card",
                    new EvaluatingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SEVEN, Suite.SPADES),
                            new Card(Rank.FIVE, Suite.SPADES),
                            new Card(Rank.FOUR, Suite.SPADES),
                            new Card(Rank.THREE, Suite.SPADES),
                            new Card(Rank.DEUCE, Suite.DIAMONDS)
                        )),
                        new HandValue(
                            HandRanking.HIGH_CARD,
                            new ArrayList<>(Arrays.asList(
                                Rank.SEVEN,
                                Rank.FIVE,
                                Rank.FOUR,
                                Rank.THREE,
                                Rank.DEUCE
                            ))
                        )
                    )
                ),
                Arguments.of(
                    "Pair",
                    new EvaluatingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SEVEN, Suite.SPADES),
                            new Card(Rank.SEVEN, Suite.CLUBS),
                            new Card(Rank.THREE, Suite.SPADES),
                            new Card(Rank.FOUR, Suite.SPADES),
                            new Card(Rank.ACE, Suite.DIAMONDS)
                        )),
                        new HandValue(
                            HandRanking.PAIR,
                            new ArrayList<>(Arrays.asList(
                                Rank.SEVEN,
                                Rank.ACE,
                                Rank.FOUR,
                                Rank.THREE
                            ))
                        )
                    )
                ),
                Arguments.of(
                    "Two Pairs",
                    new EvaluatingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SEVEN, Suite.SPADES),
                            new Card(Rank.SEVEN, Suite.DIAMONDS),
                            new Card(Rank.FOUR, Suite.SPADES),
                            new Card(Rank.FOUR, Suite.SPADES),
                            new Card(Rank.ACE, Suite.DIAMONDS)
                        )),
                        new HandValue(
                            HandRanking.TWO_PAIRS,
                            new ArrayList<>(Arrays.asList(
                                Rank.SEVEN,
                                Rank.FOUR,
                                Rank.ACE
                            ))
                        )
                    )
                ),
                Arguments.of(
                    "Three of a Kind",
                    new EvaluatingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SEVEN, Suite.SPADES),
                            new Card(Rank.SEVEN, Suite.DIAMONDS),
                            new Card(Rank.SEVEN, Suite.CLUBS),
                            new Card(Rank.THREE, Suite.SPADES),
                            new Card(Rank.DEUCE, Suite.DIAMONDS)
                        )),
                        new HandValue(
                            HandRanking.THREE_OF_A_KIND,
                            new ArrayList<>(Arrays.asList(
                                Rank.SEVEN,
                                Rank.THREE,
                                Rank.DEUCE
                            ))
                        )
                    )
                ),
                Arguments.of(
                    "Straight",
                    new EvaluatingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SIX, Suite.SPADES),
                            new Card(Rank.FIVE, Suite.SPADES),
                            new Card(Rank.FOUR, Suite.SPADES),
                            new Card(Rank.THREE, Suite.SPADES),
                            new Card(Rank.DEUCE, Suite.DIAMONDS)
                        )),
                        new HandValue(
                            HandRanking.STRAIGHT,
                            new ArrayList<>(Arrays.asList(
                                Rank.SIX,
                                Rank.FIVE,
                                Rank.FOUR,
                                Rank.THREE,
                                Rank.DEUCE
                            ))
                        )
                    )
                ),
                Arguments.of(
                    "Small Straight",
                    new EvaluatingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.ACE, Suite.SPADES),
                            new Card(Rank.FIVE, Suite.SPADES),
                            new Card(Rank.FOUR, Suite.SPADES),
                            new Card(Rank.THREE, Suite.SPADES),
                            new Card(Rank.DEUCE, Suite.DIAMONDS)
                        )),
                        new HandValue(
                            HandRanking.STRAIGHT,
                            new ArrayList<>(Arrays.asList(
                                Rank.FIVE,
                                Rank.FOUR,
                                Rank.THREE,
                                Rank.DEUCE,
                                Rank.ACE
                            ))
                        )
                    )
                ),
                Arguments.of(
                    "Flush",
                    new EvaluatingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SEVEN, Suite.SPADES),
                            new Card(Rank.FIVE, Suite.SPADES),
                            new Card(Rank.FOUR, Suite.SPADES),
                            new Card(Rank.THREE, Suite.SPADES),
                            new Card(Rank.DEUCE, Suite.SPADES)
                        )),
                        new HandValue(
                            HandRanking.FLUSH,
                            new ArrayList<>(Arrays.asList(
                                Rank.SEVEN,
                                Rank.FIVE,
                                Rank.FOUR,
                                Rank.THREE,
                                Rank.DEUCE
                            ))
                        )
                    )
                ),
                Arguments.of(
                    "Full House",
                    new EvaluatingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SIX, Suite.SPADES),
                            new Card(Rank.SIX, Suite.CLUBS),
                            new Card(Rank.SIX, Suite.HEARTS),
                            new Card(Rank.THREE, Suite.SPADES),
                            new Card(Rank.THREE, Suite.DIAMONDS)
                        )),
                        new HandValue(
                            HandRanking.FULL_HOUSE,
                            new ArrayList<>(Arrays.asList(
                                Rank.SIX,
                                Rank.THREE
                            ))
                        )
                    )
                ),
                Arguments.of(
                    "Four of a Kind",
                    new EvaluatingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SIX, Suite.SPADES),
                            new Card(Rank.SIX, Suite.CLUBS),
                            new Card(Rank.SIX, Suite.HEARTS),
                            new Card(Rank.SIX, Suite.DIAMONDS),
                            new Card(Rank.ACE, Suite.DIAMONDS)
                        )),
                        new HandValue(
                            HandRanking.FOUR_OF_A_KIND,
                            new ArrayList<>(Arrays.asList(
                                Rank.SIX,
                                Rank.ACE
                            ))
                        )
                    )
                ),
                Arguments.of(
                    "Straight Flush",
                    new EvaluatingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.SIX, Suite.SPADES),
                            new Card(Rank.FIVE, Suite.SPADES),
                            new Card(Rank.FOUR, Suite.SPADES),
                            new Card(Rank.THREE, Suite.SPADES),
                            new Card(Rank.DEUCE, Suite.SPADES)
                        )),
                        new HandValue(
                            HandRanking.STRAIGHT_FLUSH,
                            new ArrayList<>(Arrays.asList(
                                Rank.SIX,
                                Rank.FIVE,
                                Rank.FOUR,
                                Rank.THREE,
                                Rank.DEUCE
                            ))
                        )
                    )
                ),
                Arguments.of(
                    "Small Straight Flush",
                    new EvaluatingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.ACE, Suite.SPADES),
                            new Card(Rank.FIVE, Suite.SPADES),
                            new Card(Rank.FOUR, Suite.SPADES),
                            new Card(Rank.THREE, Suite.SPADES),
                            new Card(Rank.DEUCE, Suite.SPADES)
                        )),
                        new HandValue(
                            HandRanking.STRAIGHT_FLUSH,
                            new ArrayList<>(Arrays.asList(
                                Rank.FIVE,
                                Rank.FOUR,
                                Rank.THREE,
                                Rank.DEUCE,
                                Rank.ACE
                            ))
                        )
                    )
                ),
                Arguments.of(
                    "Royal Straight Flush",
                    new EvaluatingTestData(
                        new ArrayList<>(Arrays.asList(
                            new Card(Rank.ACE, Suite.SPADES),
                            new Card(Rank.KING, Suite.SPADES),
                            new Card(Rank.QUEEN, Suite.SPADES),
                            new Card(Rank.TEN, Suite.SPADES),
                            new Card(Rank.JACK, Suite.SPADES)
                        )),
                        new HandValue(
                            HandRanking.ROYAL_FLUSH,
                            new ArrayList<>(Arrays.asList(
                                Rank.ACE,
                                Rank.KING,
                                Rank.QUEEN,
                                Rank.JACK,
                                Rank.TEN
                            ))
                        )
                    )
                )
            );
        }
    }
}
