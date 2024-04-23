package org.example;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.SortedSet;

public class Regular<T extends Comparable<T>> extends User implements RequestsManager, Observer {
    public Regular(Information informations, AccountType type, String username, String experience, SortedSet<T> favorites, List<String> notifications) {
        super(informations, type, username, experience, favorites, notifications);
    }

    public Regular() {
        super();
    }

    @Override
    public void createRequest(String requestType, String requestDescription, String username) {
        RequestTypes requestT = RequestTypes.OTHERS;
        String subject = null;
        String to = null;
        if (requestType.compareToIgnoreCase("DELETE_ACCOUNT") == 0) {
            requestT = RequestTypes.DELETE_ACCOUNT;
            subject = "Delete an account";
            to = "ADMIN";
        }
        else if (requestType.compareToIgnoreCase("ACTOR_ISSUE") == 0) {
            requestT = RequestTypes.ACTOR_ISSUE;
            Actor x = null;
            for (Actor a : IMDB.getInstance().getActors()) {
                if (requestDescription.contains(a.name)) {
                    x = a;
                    break;
                }
            }
            if (x != null) {
                subject = x.name;
                to = x.addedBy;
            }
            else {
                subject = "NO ACTOR FOUND";
                to = null;
            }
        }
        else if (requestType.compareToIgnoreCase("MOVIE_ISSUE") == 0) {
            requestT = RequestTypes.MOVIE_ISSUE;
            Production x = null;
            for (Production p : IMDB.getInstance().getProductions()) {
                if (requestDescription.contains(p.title)) {
                    x = p;
                    break;
                }
            }
            if (x != null) {
                subject = x.title;
                to = x.addedBy;
            }
            else {
                subject = "NO MOVIE FOUND";
                to = null;
            }
        }
        else if (requestType.compareToIgnoreCase("OTHERS") == 0) {
            subject = "OTHERS";
            to = "ADMIN";
        }
        Request r = new Request(requestT, LocalDateTime.now(), subject, requestDescription, username, to);
        r.Notify(0);
        switch (requestT) {
            case DELETE_ACCOUNT, OTHERS: {
                Request.RequestsHolder.addRequest(r);
                break;
            }
            case ACTOR_ISSUE, MOVIE_ISSUE: {
                User uTo = IMDB.getInstance().findUser(to);
                if(uTo != null) {
                    ((Staff<?>) uTo).requestList.add(r);
                }
                break;
            }
            default: break;
        }
        IMDB.getInstance().getRequestList().add(r);
    }

    @Override
    public void removeRequest(Request r) {

    }

    public void addRating(Rating r, Production p) {
        p.ratingList.add(r);
        p.updateRating();
    }

    public void Update(String notification) {
        this.notifications.add(notification);
    }
}
