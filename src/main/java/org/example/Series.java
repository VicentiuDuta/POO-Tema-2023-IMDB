package org.example;

import kotlin.Pair;

import java.util.List;
import java.util.Map;

public class Series extends Production {
    int releaseYear;
    int numSeasons;
    public Map<String, List<Episode>> seasons;


    public Series(String title, String type, List<String> directors, List<String> actorList, List<Genre> genreList, List<Rating> ratingList, String plot, Double nota, int releaseYear, int numSeasons, Map<String, List<Episode>> seasons) {
        super(title, type, directors, actorList, genreList, ratingList, plot, nota);
        this.releaseYear = releaseYear;
        this.numSeasons = numSeasons;
        this.seasons = seasons;
    }

   public Series() {

   }

    @Override
    public void displayInfo() {
        System.out.println("Title: " + this.title);
        System.out.println("Type: " + this.type);
        int i;
        System.out.println("\nDirectors:");
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
            System.out.println("Username: " + r.username + " Rating:" + r.rating + " Comment:" + r.comments);
        }
        if(this.plot != null)
            System.out.println("\nPlot: " + this.plot);

        System.out.println("\nAverage rating: " + this.nota);
        System.out.println("\nRelease year: " + this.releaseYear);
        System.out.println("\nNumber of Seasons: " + this.numSeasons);
        System.out.println("Seasons: ");
        for(Map.Entry<String, List<Episode>> entry : this.seasons.entrySet()) {
            System.out.println("Season: " + entry.getKey());

            List<Episode> episodes = entry.getValue();
            for (Episode episode : episodes) {
                System.out.println("Episode Name: " + episode.episodeName);
                System.out.println("Duration: " + episode.duration + "\n");
            }
        }
    }

}
