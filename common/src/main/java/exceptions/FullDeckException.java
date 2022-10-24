package exceptions;

public class FullDeckException extends Exception {
    public FullDeckException() {
        super("Cannot return Card, because Deck is full.");
    }
}
