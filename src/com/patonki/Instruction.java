package com.patonki;

public class Instruction {
    private final String message;
    private final boolean justText;

    public Instruction(String message, boolean justText) {
        this.message = message;
        this.justText = justText;
    }

    public String getMessage() {
        return message;
    }

    public boolean isJustText() {
        return justText;
    }
}
