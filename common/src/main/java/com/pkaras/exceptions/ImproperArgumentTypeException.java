package com.pkaras.exceptions;

public class ImproperArgumentTypeException extends PokerException {
    public ImproperArgumentTypeException(
        String actionName,
        String argument
    ) {
        super(
            String.format(
                "%s arguments should be a number, but `%s` was provided",
                actionName,
                argument
            )
        );
    }
}
