package org.example;

public class InformationIncompleteException extends Exception
{
    public InformationIncompleteException() {
        super("Invalid credentials/name");
    }
}

