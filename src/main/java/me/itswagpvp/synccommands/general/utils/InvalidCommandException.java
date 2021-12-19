package me.itswagpvp.synccommands.general.utils;

public class InvalidCommandException extends Exception {
    public InvalidCommandException (String message, Throwable cause) {
        super(message, cause);
    }
}
