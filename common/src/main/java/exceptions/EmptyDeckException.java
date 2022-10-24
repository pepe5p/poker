package exceptions;

public class EmptyDeckException extends Exception {
    public EmptyDeckException() {
        super("Cannot get Card, because Deck is empty.");
    }
}
