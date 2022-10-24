package common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Map.entry;

enum HandRankingToTests {
    HIGH_CARD(
            new HandValue(
                    HandRanking.HIGH_CARD,
                    new ArrayList<>(Arrays.asList(
                        Rank.SEVEN,
                        Rank.FIVE,
                        Rank.FOUR,
                        Rank.THREE,
                        Rank.TWO
                    ))
            ),
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.SEVEN, Suite.SPADES),
                    new Card(Rank.FIVE, Suite.SPADES),
                    new Card(Rank.FOUR, Suite.SPADES),
                    new Card(Rank.THREE, Suite.SPADES),
                    new Card(Rank.TWO, Suite.DIAMONDS)
            )),
            new ArrayList<>(Arrays.asList(
                    entry(Rank.SEVEN, 1),
                    entry(Rank.FIVE, 1),
                    entry(Rank.FOUR, 1),
                    entry(Rank.THREE, 1),
                    entry(Rank.TWO, 1)
            ))
    ),
    PAIR(
            new HandValue(
                    HandRanking.PAIR,
                    new ArrayList<>(Arrays.asList(
                            Rank.SEVEN,
                            Rank.ACE,
                            Rank.FOUR,
                            Rank.THREE
                    ))
            ),
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.SEVEN, Suite.SPADES),
                    new Card(Rank.SEVEN, Suite.CLUBS),
                    new Card(Rank.FOUR, Suite.HEARTS),
                    new Card(Rank.THREE, Suite.SPADES),
                    new Card(Rank.ACE, Suite.DIAMONDS)
            )),
            new ArrayList<>(Arrays.asList(
                    entry(Rank.SEVEN, 2),
                    entry(Rank.ACE, 1),
                    entry(Rank.FOUR, 1),
                    entry(Rank.THREE, 1)
            ))
    ),
    TWO_PAIRS(
            new HandValue(
                    HandRanking.TWO_PAIRS,
                    new ArrayList<>(Arrays.asList(
                            Rank.SEVEN,
                            Rank.FOUR,
                            Rank.ACE
                    ))
            ),
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.FOUR, Suite.HEARTS),
                    new Card(Rank.FOUR, Suite.SPADES),
                    new Card(Rank.SEVEN, Suite.SPADES),
                    new Card(Rank.SEVEN, Suite.CLUBS),
                    new Card(Rank.ACE, Suite.DIAMONDS)
            )),
            new ArrayList<>(Arrays.asList(
                    entry(Rank.SEVEN, 2),
                    entry(Rank.FOUR, 2),
                    entry(Rank.ACE, 1)
            ))
    ),
    THREE_OF_A_KIND(
            new HandValue(
                    HandRanking.THREE_OF_A_KIND,
                    new ArrayList<>(Arrays.asList(
                            Rank.SEVEN,
                            Rank.ACE,
                            Rank.FOUR
                    ))
            ),
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.SEVEN, Suite.SPADES),
                    new Card(Rank.SEVEN, Suite.CLUBS),
                    new Card(Rank.FOUR, Suite.HEARTS),
                    new Card(Rank.ACE, Suite.SPADES),
                    new Card(Rank.SEVEN, Suite.DIAMONDS)
            )),
            new ArrayList<>(Arrays.asList(
                    entry(Rank.SEVEN, 3),
                    entry(Rank.ACE, 1),
                    entry(Rank.FOUR, 1)
            ))
    ),
    FULL_HOUSE(
            new HandValue(
                    HandRanking.FULL_HOUSE,
                    new ArrayList<>(Arrays.asList(
                            Rank.SEVEN,
                            Rank.ACE
                    ))
            ),
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.SEVEN, Suite.SPADES),
                    new Card(Rank.SEVEN, Suite.CLUBS),
                    new Card(Rank.SEVEN, Suite.HEARTS),
                    new Card(Rank.ACE, Suite.SPADES),
                    new Card(Rank.ACE, Suite.DIAMONDS)
            )),
            new ArrayList<>(Arrays.asList(
                    entry(Rank.SEVEN, 3),
                    entry(Rank.ACE, 2)
            ))
    ),
    FOUR_OF_A_KIND(
            new HandValue(
                    HandRanking.FOUR_OF_A_KIND,
                    new ArrayList<>(Arrays.asList(
                            Rank.SEVEN,
                            Rank.ACE
                    ))
            ),
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.SEVEN, Suite.SPADES),
                    new Card(Rank.SEVEN, Suite.CLUBS),
                    new Card(Rank.SEVEN, Suite.HEARTS),
                    new Card(Rank.ACE, Suite.SPADES),
                    new Card(Rank.SEVEN, Suite.DIAMONDS)
            )),
            new ArrayList<>(Arrays.asList(
                    entry(Rank.SEVEN, 4),
                    entry(Rank.ACE, 1)
            ))
    ),
    STRAIGHT1(
            new HandValue(
                    HandRanking.STRAIGHT,
                    new ArrayList<>(Arrays.asList(
                            Rank.SIX,
                            Rank.FIVE,
                            Rank.FOUR,
                            Rank.THREE,
                            Rank.TWO
                    ))
            ),
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.SIX, Suite.SPADES),
                    new Card(Rank.FIVE, Suite.SPADES),
                    new Card(Rank.FOUR, Suite.SPADES),
                    new Card(Rank.THREE, Suite.SPADES),
                    new Card(Rank.TWO, Suite.DIAMONDS)
            )),
            new ArrayList<>(Arrays.asList(
                    entry(Rank.SIX, 1),
                    entry(Rank.FIVE, 1),
                    entry(Rank.FOUR, 1),
                    entry(Rank.THREE, 1),
                    entry(Rank.TWO, 1)
            ))
    ),
    STRAIGHT2(
            new HandValue(
                    HandRanking.STRAIGHT,
                    new ArrayList<>(Arrays.asList(
                            Rank.SIX,
                            Rank.FIVE,
                            Rank.FOUR,
                            Rank.THREE,
                            Rank.TWO
                    ))
            ),
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.TWO, Suite.DIAMONDS),
                    new Card(Rank.THREE, Suite.SPADES),
                    new Card(Rank.FOUR, Suite.SPADES),
                    new Card(Rank.FIVE, Suite.SPADES),
                    new Card(Rank.SIX, Suite.SPADES)
            )),
            new ArrayList<>(Arrays.asList(
                    entry(Rank.SIX, 1),
                    entry(Rank.FIVE, 1),
                    entry(Rank.FOUR, 1),
                    entry(Rank.THREE, 1),
                    entry(Rank.TWO, 1)
            ))
    ),
    STRAIGHT3(
            new HandValue(
                    HandRanking.STRAIGHT,
                    new ArrayList<>(Arrays.asList(
                            Rank.SIX,
                            Rank.FIVE,
                            Rank.FOUR,
                            Rank.THREE,
                            Rank.TWO
                    ))
            ),
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.TWO, Suite.DIAMONDS),
                    new Card(Rank.THREE, Suite.SPADES),
                    new Card(Rank.FIVE, Suite.SPADES),
                    new Card(Rank.SIX, Suite.SPADES),
                    new Card(Rank.FOUR, Suite.SPADES)
            )),
            new ArrayList<>(Arrays.asList(
                    entry(Rank.SIX, 1),
                    entry(Rank.FIVE, 1),
                    entry(Rank.FOUR, 1),
                    entry(Rank.THREE, 1),
                    entry(Rank.TWO, 1)
            ))
    ),
    MALY_STRIT_I_MAKAO(
            new HandValue(
                    HandRanking.STRAIGHT,
                    new ArrayList<>(Arrays.asList(
                            Rank.FIVE,
                            Rank.FOUR,
                            Rank.THREE,
                            Rank.TWO,
                            Rank.ACE
                    ))
            ),
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.TWO, Suite.DIAMONDS),
                    new Card(Rank.THREE, Suite.SPADES),
                    new Card(Rank.FIVE, Suite.SPADES),
                    new Card(Rank.ACE, Suite.SPADES),
                    new Card(Rank.FOUR, Suite.SPADES)
            )),
            new ArrayList<>(Arrays.asList(
                    entry(Rank.FIVE, 1),
                    entry(Rank.FOUR, 1),
                    entry(Rank.THREE, 1),
                    entry(Rank.TWO, 1),
                    entry(Rank.ACE, 1)
            ))
    ),
    FLUSH(
            new HandValue(
                    HandRanking.FLUSH,
                    new ArrayList<>(Arrays.asList(
                            Rank.JACK,
                            Rank.FIVE,
                            Rank.FOUR,
                            Rank.THREE,
                            Rank.TWO
                    ))
            ),
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.TWO, Suite.SPADES),
                    new Card(Rank.THREE, Suite.SPADES),
                    new Card(Rank.FOUR, Suite.SPADES),
                    new Card(Rank.FIVE, Suite.SPADES),
                    new Card(Rank.JACK, Suite.SPADES)
            )),
            new ArrayList<>(Arrays.asList(
                    entry(Rank.JACK, 1),
                    entry(Rank.FIVE, 1),
                    entry(Rank.FOUR, 1),
                    entry(Rank.THREE, 1),
                    entry(Rank.TWO, 1)
            ))
    ),
    STRAIGHT_FLUSH(
            new HandValue(
                    HandRanking.STRAIGHT_FLUSH,
                    new ArrayList<>(Arrays.asList(
                            Rank.SIX,
                            Rank.FIVE,
                            Rank.FOUR,
                            Rank.THREE,
                            Rank.TWO
                    ))
            ),
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.TWO, Suite.SPADES),
                    new Card(Rank.THREE, Suite.SPADES),
                    new Card(Rank.FOUR, Suite.SPADES),
                    new Card(Rank.FIVE, Suite.SPADES),
                    new Card(Rank.SIX, Suite.SPADES)
            )),
            new ArrayList<>(Arrays.asList(
                    entry(Rank.SIX, 1),
                    entry(Rank.FIVE, 1),
                    entry(Rank.FOUR, 1),
                    entry(Rank.THREE, 1),
                    entry(Rank.TWO, 1)
            ))
    ),
    ROYAL_FLUSH(
            new HandValue(
                    HandRanking.ROYAL_FLUSH,
                    new ArrayList<>(Arrays.asList(
                            Rank.ACE,
                            Rank.KING,
                            Rank.QUEEN,
                            Rank.JACK,
                            Rank.TEN
                    ))
            ),
            new ArrayList<>(Arrays.asList(
                    new Card(Rank.TEN, Suite.SPADES),
                    new Card(Rank.JACK, Suite.SPADES),
                    new Card(Rank.QUEEN, Suite.SPADES),
                    new Card(Rank.KING, Suite.SPADES),
                    new Card(Rank.ACE, Suite.SPADES)
            )),
            new ArrayList<>(Arrays.asList(
                    entry(Rank.ACE, 1),
                    entry(Rank.KING, 1),
                    entry(Rank.QUEEN, 1),
                    entry(Rank.JACK, 1),
                    entry(Rank.TEN, 1)
            ))
    );

    final HandValue handValue;
    final ArrayList<Card> cards;
    final ArrayList<Map.Entry<Rank, Integer>> groups;

    HandRankingToTests(
            final HandValue handValue,
            final ArrayList<Card> cards,
            final ArrayList<Map.Entry<Rank, Integer>> groups
    ) {
        this.handValue = handValue;
        this.cards = cards;
        this.groups = groups;
    }
}
