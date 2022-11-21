package com.pkaras.exceptions;

public class ImproperArgumentsNumberException extends PokerException {
    public ImproperArgumentsNumberException(
        String actionName,
        int expectedNumber,
        int actualNumber
    ) {
        super(
            String.format(
                "action `%s` should have %d arguments, but %d were provided",
                actionName,
                expectedNumber,
                actualNumber
            )
        );
    }
}
