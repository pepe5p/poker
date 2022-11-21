package com.pkaras;

import com.pkaras.enums.HandRanking;
import com.pkaras.enums.Rank;
import com.pkaras.enums.Suite;
import com.pkaras.exceptions.ImproperHandCardsNumberException;

import java.util.*;

public class Hand {

    public static final int NUMBER_OF_CARDS = 2;
    ArrayList<Card> cards;
    HandValue handValue;

    public Hand() {
        this.cards = new ArrayList<>(NUMBER_OF_CARDS);
        evaluateHand();
    }
    
    public Hand(List<Card> cards) throws ImproperHandCardsNumberException {
        this.cards = new ArrayList<>(NUMBER_OF_CARDS);
        this.setCards(cards);
        evaluateHand();
    }

    public HandValue getHandValue() {
        return handValue;
    }

    public void refreshHandValue() {
        handValue = new HandValue(HandRanking.EMPTY, new ArrayList<>(0));
    }

    public void setCards(List<Card> cards) throws ImproperHandCardsNumberException {
        if (cards.size() != NUMBER_OF_CARDS) {
            throw new ImproperHandCardsNumberException();
        }
        this.cards = new ArrayList<>(cards);
        evaluateHand();
    }

    public List<Card> removeCards() {
        ArrayList<Card> handCards = new ArrayList<>(NUMBER_OF_CARDS);
        handCards.addAll(this.cards);
        this.cards.clear();
        refreshHandValue();
        return handCards;
    }

    void evaluateHand() {
        evaluateHand(new ArrayList<>(0));
    }

    public void evaluateHand(List<Card> tableCards) {
        if (cards.isEmpty() && tableCards.isEmpty()) {
            handValue = new HandValue(HandRanking.EMPTY, new ArrayList<>(0));
            return;
        }
        ArrayList<Card> allCards = new ArrayList<>(tableCards.size() + cards.size());
        allCards.addAll(tableCards);
        allCards.addAll(cards);
        LinkedHashMap<Rank, Integer> groups = new LinkedHashMap<>(groupCards(allCards));
        ArrayList<Rank> flushGroupsKeys = groupIfFlush(allCards);
        ArrayList<Rank> straightGroupsKeys = groupIfStraight(allCards);
        handValue = chooseHandValue(groups, flushGroupsKeys, straightGroupsKeys);
    }

    public Map<Rank, Integer> groupCards(List<Card> allCards) {
        LinkedHashMap<Rank, Integer> groups = new LinkedHashMap<>();
        int number;
        for (Card card : allCards) {
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

    private ArrayList<Rank> groupIfFlush(ArrayList<Card> allCards) {
        ArrayList<Card> sortedCards = new ArrayList<>(allCards.size());
        allCards.stream().sorted((c1, c2) -> {
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

    private ArrayList<Rank> groupIfStraight(ArrayList<Card> allCards) {
        ArrayList<Card> withoutDuplicates = new ArrayList<>(dropDuplicates(allCards));
        ArrayList<Card> sortedCards = new ArrayList<>(withoutDuplicates.size());
        withoutDuplicates.stream().sorted((c1, c2) -> Integer.compare(c2.rank().value, c1.rank().value))
        .forEachOrdered(sortedCards::add);

        if (sortedCards.get(0).rank() == Rank.ACE) sortedCards.add(new Card(Rank.ACE, Suite.CLUBS));

        ArrayList<Rank> straightGroupsKeys = new ArrayList<>(5);
        Rank rank = sortedCards.get(0).rank();
        for (Card card : sortedCards) {
            if (rank.value != card.rank().value + 1 && (card.rank() != Rank.ACE || rank != Rank.DEUCE)) {
                straightGroupsKeys.clear();
            }
            rank = card.rank();
            straightGroupsKeys.add(rank);
            if (straightGroupsKeys.size() == 5) return straightGroupsKeys;
        }
        straightGroupsKeys.clear();
        return straightGroupsKeys;
    }

    private ArrayList<Card> dropDuplicates(ArrayList<Card> sortedCards) {
        Set<Card> withoutDuplicates = new HashSet<>(sortedCards);
        return new ArrayList<>(withoutDuplicates);
    }

    private HandValue chooseHandValue(
        LinkedHashMap<Rank, Integer> groups,
        ArrayList<Rank> flushGroupsKeys,
        ArrayList<Rank> straightGroupsKeys
    ) {
        boolean flush = !flushGroupsKeys.isEmpty();
        boolean straight = !straightGroupsKeys.isEmpty();

        ArrayList<Map.Entry<Rank, Integer>> groupList = new ArrayList<>(groups.entrySet());
        int firstGroupQuantity = groupList.get(0).getValue();
        int secondGroupQuantity = groupList.size() == 1 ? 0 : groupList.get(1).getValue();

        if (flush && straight && straightGroupsKeys.get(0) == Rank.ACE) {
            return new HandValue(
                HandRanking.ROYAL_FLUSH,
                new ArrayList<>(straightGroupsKeys)
            );
        }
        if (flush && straight) {
            return new HandValue(
                HandRanking.STRAIGHT_FLUSH,
                new ArrayList<>(straightGroupsKeys)
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

    @Override
    public String toString() {
        return toString("\u001B[33m");
    }

    public String toString(String actualColor) {
        StringBuilder handString = new StringBuilder();
        for (Card card : cards) handString.append(card.toString(actualColor));
        if (cards.isEmpty()) handString.append("[No Cards] -");
        handString.append(" ").append(handValue.handRanking().handRankingName);
        for (int i = 0; i < handValue.handRanking().numberOfCardsToShow; i++) {
            if (i == 0) handString.append(" on ");
            else handString.append(", ");
            handString.append(handValue.rankList().get(i).rankName);
        }
        return handString.toString();
    }
}
