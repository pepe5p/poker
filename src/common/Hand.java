package common;

import common.exceptions.ImproperHandCardsNumberException;

import java.io.PrintStream;
import java.util.*;

public class Hand {

    final static int NUMBER_OF_CARDS = 5;
    private ArrayList<Card> cards;

    public Hand() {
        this.cards = new ArrayList<>(NUMBER_OF_CARDS);
    }
    
    public Hand(ArrayList<Card> cards) throws ImproperHandCardsNumberException {
        this.cards = new ArrayList<>(NUMBER_OF_CARDS);
        this.setCards(cards);
    }

    public void print() {
        for (Card card : this.cards) {
            System.out.print(card.toString());
        }
        HandValue value = this.getValue();
        PrintStream out = System.out;

        out.format(
            " %s",
            value.handRanking().name
        );
        for (int i = 0; i < value.handRanking().numberOfCardsToShow; i++) {
            if (i == 0) out.print(" on ");
            else out.print(", ");
            out.print(value.rankList().get(i).name);
        }
        out.println();
    }

    public void setCards(ArrayList<Card> cards) throws ImproperHandCardsNumberException {
        if (cards.size() != NUMBER_OF_CARDS) {
            throw new ImproperHandCardsNumberException();
        }
        this.cards = cards;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public HandValue getValue() {
        LinkedHashMap<Rank, Integer> groups = this.groupCards();
        ArrayList<Rank> flushGroupsKeys = this.groupIfFlush();
        ArrayList<Rank> straightGroupsKeys = this.groupIfStraight();
        return this.getHandValue(groups, flushGroupsKeys, straightGroupsKeys);
    }

    public LinkedHashMap<Rank, Integer> groupCards() {
        LinkedHashMap<Rank, Integer> groups = new LinkedHashMap<>();
        int number;
        for (Card card : this.cards) {
            Rank rank = card.rank();
            if (!groups.containsKey(rank)) {
                groups.put(rank, 1);
                continue;
            }
            number = groups.get(rank);
            groups.replace(rank, number + 1);
        }
        return this.sortGroupsByQuantity(groups);
    }

    private LinkedHashMap<Rank, Integer> sortGroupsByQuantity(LinkedHashMap<Rank, Integer> groups) {
        LinkedHashMap<Rank, Integer> sortedGroups = new LinkedHashMap<>();
        groups.entrySet().stream().sorted((me1, me2) -> {
            if (me1.getValue() < me2.getValue()) return 1;
            if (me1.getValue() > me2.getValue()) return -1;
            return -Integer.compare(me1.getKey().value, me2.getKey().value);
        })
        .forEachOrdered(x -> sortedGroups.put(x.getKey(), x.getValue()));
        return sortedGroups;
    }

    private ArrayList<Rank> groupIfFlush() {
        ArrayList<Card> sortedCards = new ArrayList<>(NUMBER_OF_CARDS);
        this.cards.stream().sorted((c1, c2) -> {
            if (c1.suite().value < c2.suite().value) return 1;
            if (c1.suite().value > c2.suite().value) return -1;
            return -Integer.compare(c1.rank().value, c2.rank().value);
        })
        .forEachOrdered(sortedCards::add);

        ArrayList<Rank> flushGroupsKeys = new ArrayList<>(5);
        Suite suite = sortedCards.get(0).suite();
        for (Card card : sortedCards) {
            if (card.suite().value != suite.value) {
                flushGroupsKeys.clear();
                suite = card.suite();
            }
            flushGroupsKeys.add(card.rank());
            if (flushGroupsKeys.size() == 5) return flushGroupsKeys;
        }
        flushGroupsKeys.clear();
        return flushGroupsKeys;
    }

    private ArrayList<Rank> groupIfStraight() {
        ArrayList<Card> sortedCards = new ArrayList<>(NUMBER_OF_CARDS);
        this.cards.stream().sorted((c1, c2) -> {
            return Integer.compare(c2.rank().value, c1.rank().value);
        })
        .forEachOrdered(sortedCards::add);

        if (sortedCards.get(0).rank() == Rank.ACE) sortedCards.add(new Card(Rank.ACE, Suite.CLUBS));

        ArrayList<Rank> straightGroupsKeys = new ArrayList<>(5);
        Rank rank = sortedCards.get(0).rank();
        for (Card card : sortedCards) {
            if (rank.value != card.rank().value + 1 && card.rank() != Rank.ACE && rank != Rank.TWO) {
                straightGroupsKeys.clear();
            }
            rank = card.rank();
            straightGroupsKeys.add(rank);
            if (straightGroupsKeys.size() == 5) return straightGroupsKeys;
        }
        straightGroupsKeys.clear();
        return straightGroupsKeys;
    }

    private HandValue getHandValue(
            LinkedHashMap<Rank, Integer> groups,
            ArrayList<Rank> flushGroupsKeys,
            ArrayList<Rank> straightGroupsKeys
    ) {
        boolean flush = flushGroupsKeys.size() != 0;
        boolean straight = straightGroupsKeys.size() != 0;

        ArrayList<Map.Entry<Rank, Integer>> group_list = new ArrayList<>(groups.entrySet());
        int firstGroupQuantity = group_list.get(0).getValue();
        int secondGroupQuantity = group_list.get(1).getValue();

        if (flush && straight && flushGroupsKeys.get(0) == Rank.ACE) {
            return new HandValue(
                HandRanking.ROYAL_FLUSH,
                new ArrayList<>(flushGroupsKeys)
            );
        }
        if (flush && straight) {
            return new HandValue(
                HandRanking.STRAIGHT_FLUSH,
                new ArrayList<>(flushGroupsKeys)
            );
        }
        if (firstGroupQuantity == 4) {
            return new HandValue(
                HandRanking.FOUR_OF_A_KIND,
                new ArrayList<>(groups.keySet())
            );
        }
        if (firstGroupQuantity == 3 && secondGroupQuantity == 2) {
            return new HandValue(
                HandRanking.FULL_HOUSE,
                new ArrayList<>(groups.keySet())
            );
        }
        if (flush) {
            return new HandValue(
                HandRanking.FLUSH,
                new ArrayList<>(flushGroupsKeys)
            );
        }
        if (straight) {
            return new HandValue(
                HandRanking.STRAIGHT,
                new ArrayList<>(straightGroupsKeys)
            );
        }
        if (firstGroupQuantity == 3) {
            return new HandValue(
                HandRanking.THREE_OF_A_KIND,
                new ArrayList<>(groups.keySet())
            );
        }
        if (firstGroupQuantity == 2 && secondGroupQuantity == 2) {
            return new HandValue(
                HandRanking.TWO_PAIRS,
                new ArrayList<>(groups.keySet())
            );
        }
        if (firstGroupQuantity == 2) {
            return new HandValue(
                HandRanking.PAIR,
                new ArrayList<>(groups.keySet())
            );
        }
        return new HandValue(
            HandRanking.HIGH_CARD,
            new ArrayList<>(groups.keySet())
        );
    }
}
