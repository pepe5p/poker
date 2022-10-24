package common;

import common.exceptions.EmptyDeckException;

import java.util.ArrayList;
import java.util.Random;

public class Deck {
    public ArrayList<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>(52);
        for (Suite suite : Suite.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suite));
            }
        }
        this.shuffle();
    }

    public void shuffle() {
        int size = this.cards.size();
        Random rand = new Random();
        for (int cardIndex = 0; cardIndex < size; cardIndex++) {
            swap(cardIndex, rand.nextInt(this.cards.size()));
        }
    }

    private void swap(int card1Index, int card2Index) {
        Card card1 = this.cards.get(card1Index);
        this.cards.set(card1Index, this.cards.get(card2Index));
        this.cards.set(card2Index, card1);
    }

    public Hand getHand() throws Exception {
        return new Hand(this.getTopCards(5));
    }

    public ArrayList<Card> getTopCards(int numberOfCards) throws EmptyDeckException {
        ArrayList<Card> topCards = new ArrayList<>(numberOfCards);
        for (int i = 0; i < numberOfCards; i++) {
            topCards.add(this.getTopCard());
        }
        return topCards;
    }

    public Card getTopCard() throws EmptyDeckException{
        if (this.cards.isEmpty()) {
            throw new EmptyDeckException();
        }
        return this.cards.remove(0);
    }
}
