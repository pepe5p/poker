import enums.Rank;
import enums.Suite;
import exceptions.EmptyDeckException;
import exceptions.FullDeckException;

import java.util.ArrayList;
import java.util.Random;

/**
 *  Class representing deck of cards.
 */
public class Deck {

    /**
     * List of cards in deck.
     * */
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

    /**
     * Method that shuffles the deck.
     */
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

    /**
     * Method that takes first 5 cards from deck.
     * @return first 5 cards
     * @throws Exception when there is no card
     */
    public Hand getHand() throws Exception {
        return new Hand(this.getTopCards(Hand.NUMBER_OF_CARDS));
    }

    /**
     * Method that takes first cards from deck.
     * @param numberOfCards number of cards to take
     * @return first cards
     * @throws EmptyDeckException when there is no card
     */
    public ArrayList<Card> getTopCards(int numberOfCards) throws EmptyDeckException {
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
        ArrayList<Card> cards = hand.getCards();
        hand.clearCards();
        returnCards(cards);
    }

    private void returnCards(ArrayList<Card> cards) throws FullDeckException {
        if (this.cards.size() + cards.size() > 52) {
            throw new FullDeckException();
        }
        this.cards.addAll(cards);
    }
}
