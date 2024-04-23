package org.example;

import java.util.List;

public class Movie extends Production{
    String duration;
    int releaseYear;

    public Movie(String title, String type, List<String> directors, List<String> actors, List<Genre> genres, List<Rating> ratings, String plot, Double averageRating, String duration, int releaseYear) {
        super(title, type,directors,actors,genres,ratings,plot,averageRating);
        this.duration = duration;
        this.releaseYear = releaseYear;
    }

    @Override
    public void displayInfo() {
        System.out.println("Title: " + this.title + "\n");
        System.out.println("Type: " + this.type + "\n");
        int i;
        System.out.println("Directors:");
        for(i = 0; i < this.directors.size(); i++)
            System.out.println(this.directors.get(i));

        System.out.println("\nActors:");
        for(i = 0; i < this.actorList.size(); i++)
            System.out.println(this.actorList.get(i));

        System.out.println("\nGenres:");
        for(i = 0; i < this.genreList.size(); i++)
            System.out.println(this.genreList.get(i));

        System.out.println("\nRatings:");
        for(i = 0; i < this.ratingList.size(); i++) {
            Rating r = this.ratingList.get(i);
            System.out.println("Username: "  + r.username + " Rating: " + r.rating + " Comment:" + r.comments);
        }
        if(this.plot != null)
            System.out.println("\nPlot: \n" + this.plot);

        System.out.println("\nAverage rating: " + this.nota);
        System.out.println("\nDuration: " + this.duration);
        System.out.println("\nRelease year: " + this.releaseYear + "\n\n");

    }


}
