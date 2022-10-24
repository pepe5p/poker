package common;

import common.exceptions.ImproperHandCardsNumberException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHand {

    static Hand hand;

    @BeforeAll
    public static void beforeAllTests() {
        TestHand.hand = new Hand();
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "HIGH_CARD",
            "PAIR",
            "TWO_PAIRS",
            "THREE_OF_A_KIND",
            "FLUSH",
            "FULL_HOUSE",
            "FOUR_OF_A_KIND",
        }
    )
    public void testGroupCards(String name) throws ImproperHandCardsNumberException {
        HandRankingToTests handRankingToTests = HandRankingToTests.valueOf(name);
        hand.setCards(handRankingToTests.cards);
        hand.print();
        LinkedHashMap<Rank, Integer> groups = hand.groupCards();
        System.out.println(groups);
        assertEquals(
            handRankingToTests.groups.stream().toList(),
            groups.entrySet().stream().toList()
        );
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "HIGH_CARD",
            "PAIR",
            "TWO_PAIRS",
            "THREE_OF_A_KIND",
            "FULL_HOUSE",
            "FOUR_OF_A_KIND",
            "STRAIGHT1",
            "STRAIGHT2",
            "STRAIGHT3",
            "MALY_STRIT_I_MAKAO",
            "FLUSH",
            "STRAIGHT_FLUSH",
            "ROYAL_FLUSH",
        }
    )
    public void testEvaluate(String name) throws ImproperHandCardsNumberException {
        HandRankingToTests handRankingToTests = HandRankingToTests.valueOf(name);
        hand.setCards(handRankingToTests.cards);
        hand.print();
        assertEquals(handRankingToTests.handValue, hand.getValue());
    }
}
