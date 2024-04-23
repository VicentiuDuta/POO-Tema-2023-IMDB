package org.example;

public class Rating implements Subject{
    public String username;
    public int rating;
    public String comments;

    public Rating(String username, int rating, String comments) {
        this.username = username;
        this.rating = rating;
        this.comments = comments;
    }

    public Rating() {

    }

    @Override
    public void Subscribe(Observer a, Production b) {
        b.observers.add(a);
    }

    @Override
    public void Unsubscribe(Observer a, Production b) {
        b.observers.remove(a);
    }

    @Override
    public void Notify(Production p) {
        String notification;
        if (p instanceof Movie)
            notification = "Filmul " + p.title + " a primit o noua recenzie de la " + this.username + "->" + this.rating;
        else
            notification = "Serialul " + p.title + " a primit o noua recenzie de la " + this.username + "->" + this.rating;
        for (Observer o : p.observers)
            o.Update(notification);
    }
}
