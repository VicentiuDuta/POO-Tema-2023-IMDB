package org.example;

public interface Subject {
    public void Subscribe(Observer a, Production b);
    public void Unsubscribe(Observer a, Production b);
    public void Notify(Production p);

}
