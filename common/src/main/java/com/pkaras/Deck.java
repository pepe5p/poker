package com.pkaras;

import com.pkaras.enums.Rank;
import com.pkaras.enums.Suite;
import com.pkaras.exceptions.EmptyDeckException;
import com.pkaras.exceptions.FullDeckException;
import com.pkaras.exceptions.ImproperHandCardsNumberException;
import com.pkaras.exceptions.ShuffleIncompleteDeckException;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 *  Class representing deck of cards.
 */
public class Deck {

    public static final int MAX_CARDS_NUMBER = 52;

    SecureRandom rand = new SecureRandom();

    /**
     * List of cards in deck.
     * */
    ArrayList<Card> cards;

    public Deck() throws ShuffleIncompleteDeckException {
        this.cards = new ArrayList<>(MAX_CARDS_NUMBER);
        for (Suite suite : Suite.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suite));
            }
        }
        this.shuffle();
    }

    /**
     * Method that shuffles the deck.
     */
    public void shuffle() throws ShuffleIncompleteDeckException {
        int size = this.cards.size();
        if (size != MAX_CARDS_NUMBER) throw new ShuffleIncompleteDeckException();
        for (int cardIndex = 0; cardIndex < size; cardIndex++) {
            swap(cardIndex, rand.nextInt(size));
        }
    }

    private void swap(int card1Index, int card2Index) {
        Card card1 = this.cards.get(card1Index);
        this.cards.set(card1Index, this.cards.get(card2Index));
        this.cards.set(card2Index, card1);
    }

    /**
     * Method that takes first NUMBER_OF_CARDS cards from deck.
     * @return first NUMBER_OF_CARDS cards
     * @throws ImproperHandCardsNumberException when NUMBER_OF_CARDS
     * @throws EmptyDeckException when there is no card
     */
    public Hand getHand() throws EmptyDeckException, ImproperHandCardsNumberException {
        return new Hand(this.getTopCards(Hand.NUMBER_OF_CARDS));
    }

    /**
     * Method that takes first cards from deck.
     * @param numberOfCards number of cards to take
     * @return first cards
     * @throws EmptyDeckException when there is no card
     */
    public List<Card> getTopCards(int numberOfCards) throws EmptyDeckException {
        ArrayList<Card> topCards = new ArrayList<>(numberOfCards);
        for (int i = 0; i < numberOfCards; i++) {
            topCards.add(this.getTopCard());
        }
        return topCards;
    }

    /**
     * Method that takes first card from deck.
     * @return first card from deck
     * @throws EmptyDeckException when there is no card
     */
    public Card getTopCard() throws EmptyDeckException{
        if (this.cards.isEmpty()) {
            throw new EmptyDeckException();
        }
        return this.cards.remove(0);
    }

    public void returnHand(Hand hand) throws FullDeckException {
        this.returnCards(new ArrayList<>(hand.removeCards()));
    }

    public void returnCards(List<Card> cards) throws FullDeckException {
        if (this.cards.size() + cards.size() > MAX_CARDS_NUMBER) {
            throw new FullDeckException();
        }
        this.cards.addAll(cards);
    }
}
