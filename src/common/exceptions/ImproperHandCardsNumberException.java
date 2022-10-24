package common.exceptions;

public class ImproperHandCardsNumberException extends Exception {
    public ImproperHandCardsNumberException() {
        super("common.Hand must have 5 Cards.");
    }
}
