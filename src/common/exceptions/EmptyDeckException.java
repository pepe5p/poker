package common.exceptions;

public class EmptyDeckException extends Exception {
    public EmptyDeckException() {
        super("Cannot get common.Card, because common.Deck is empty.");
    }
}
