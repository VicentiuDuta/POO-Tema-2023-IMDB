package org.example;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Production implements Comparable{
    public String title;
    public String type;
    public List<String> directors;
    public List<String> actorList;
    public List<Genre> genreList;
    public List<Rating> ratingList;
    public String plot;
    public Double nota;
    public String addedBy;
    public List<Observer> observers;


    public Production(String title, String type, List<String> directors, List<String> actorList, List<Genre> genreList, List<Rating> ratingList, String plot, Double nota) {
        this.title = title;
        this.type = type;
        this.directors = directors;
        this.actorList = actorList;
        this.genreList = genreList;
        this.ratingList = ratingList;
        this.plot = plot;
        this.nota = nota;
        this.observers = new ArrayList<Observer>();
    }

    public Production() {

    }

    public abstract void displayInfo();

    public void updateRating() {
        int suma = 0;
        for(Rating r : this.ratingList)
            suma += r.rating;

        this.nota = (double) suma / this.ratingList.size();
    }


    @Override
    public int compareTo(@NotNull Object o) {
        Production x = (Production) o;
        return x.title.compareTo(((Production) o).title);
    }
}
