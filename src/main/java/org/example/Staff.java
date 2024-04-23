package org.example;

import java.util.Scanner;
import java.util.SortedSet;
import java.util.List;

public abstract class Staff<T extends Comparable<T>> extends User implements StaffInterface, ExperienceStrategy {

    public List<Request> requestList;
    public SortedSet<T> contributions;

    public Staff(Information informations, AccountType type, String username, String experience, SortedSet favorites, List notifications, List<Request> requestList, SortedSet<T> contributions) {
        super(informations, type, username, experience, favorites, notifications);
        this.requestList = requestList;
        this.contributions = contributions;
    }

    public Staff() {
        super();
    }

    @Override
    public int calculateExperience(User user, boolean isAdd) {
        if(isAdd)
            return Integer.parseInt(user.experience) + 10;
        else
            return Integer.parseInt(user.experience) - 10;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addProductionSystem(Production p) {
        int newExperience = calculateExperience(IMDB.getInstance().findUser(p.addedBy), true);
        if (IMDB.getInstance().findUser(p.addedBy).experience != null)
            IMDB.getInstance().findUser(p.addedBy).experience = String.valueOf(newExperience);

        IMDB.getInstance().getProductions().add(p);
        this.contributions.add((T) p);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addActorSystem(Actor a) {
        int newExperience = calculateExperience(IMDB.getInstance().findUser(a.addedBy), true);
        IMDB.getInstance().findUser(a.addedBy).experience = String.valueOf(newExperience);

        IMDB.getInstance().getActors().add(a);
        this.contributions.add((T) a);
    }

    @Override
    public void removeProductionSystem(String name) {
        Production p = IMDB.getInstance().findProd(name);
        if(this.contributions.contains(p)) {
            int newExperience = calculateExperience(IMDB.getInstance().findUser(p.addedBy), false);
            IMDB.getInstance().findUser(p.addedBy).experience = String.valueOf(newExperience);

            this.contributions.remove(p);
            IMDB.getInstance().getProductions().remove(p);
        }
    }

    @Override
    public void removeActorSystem(String name) {
        Actor a = IMDB.getInstance().findActor(name);
        if(this.contributions.contains(a)) {
            int newExperience = calculateExperience(IMDB.getInstance().findUser(a.addedBy), false);
            IMDB.getInstance().findUser(a.addedBy).experience = String.valueOf(newExperience);

            this.contributions.remove(a);
            IMDB.getInstance().getActors().remove(a);
        }
    }

    @Override
    public void updateProduction(Production p, Scanner myObj) {

    }

    @Override
    public void updateActor(Actor a, Scanner myObj) {

    }
}
