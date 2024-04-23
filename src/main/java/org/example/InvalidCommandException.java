package org.example;

public class InvalidCommandException extends Exception {
    public InvalidCommandException() {
        super("There is no such option! Please try again!");
    }
}
