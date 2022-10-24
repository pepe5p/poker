package exceptions;

public class ImproperHandCardsNumberException extends Exception {

    private static final class Hand {
        private final static int NUMBER_OF_CARDS = 5;
    }
    public ImproperHandCardsNumberException() {
        super("Hand must have " + Hand.NUMBER_OF_CARDS + " Cards.");
    }
}
