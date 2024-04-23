package org.example;

import java.time.LocalDateTime;
import java.util.List;

public class Request implements Subject{
    private RequestTypes requestType;
    private LocalDateTime creationDate;
    private String subject;
    private String description;
    private String username;
    private String to;

    public Request() {
    }

    public Request(RequestTypes requestType, LocalDateTime creationDate, String subject, String description, String username, String to) {
        this.requestType = requestType;
        this.creationDate = creationDate;
        this.subject = subject;
        this.description = description;
        this.username = username;
        this.to = to;
    }

    public RequestTypes getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestTypes requestType) {
        this.requestType = requestType;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public static class RequestsHolder{
        public static List<Request> requestList;

        public RequestsHolder(List<Request> requestList) {
            RequestsHolder.requestList = requestList;
        }

        public static void addRequest (Request r) {
            requestList.add(r);
        }


        public static void removeRequest (Request r) {

            requestList.remove(r);
        }
    }

    @Override
    public void Subscribe(Observer a, Production b) {

    }

    @Override
    public void Unsubscribe(Observer a, Production b) {

    }

    @Override
    public void Notify(Production p) {

    }

    public void Notify(int added_solved) {
        switch (added_solved) {
            case 0: {
                if(this.to.equals( "ADMIN") == false) {
                    String notification = "Ai primit o noua cerere de la utilizatorul " + this.username + " de tipul " + this.requestType;
                    if (this.requestType == RequestTypes.ACTOR_ISSUE || this.requestType == RequestTypes.MOVIE_ISSUE)
                        notification += " pentru " + this.subject;
                    IMDB.getInstance().findUser(this.to).notifications.add(notification);
                } else {
                    String notification = "Ai primit o noua cerere de la utilizatorul " + this.username + " de tipul " + this.requestType;
                    if (this.requestType == RequestTypes.ACTOR_ISSUE || this.requestType == RequestTypes.MOVIE_ISSUE)
                        notification += " pentru " + this.subject;
                    for (User u : IMDB.getInstance().getUserList())
                        if (u instanceof Admin)
                            u.notifications.add(notification);
                }
                break;
            }
            case 1: {
                if (this.requestType == RequestTypes.MOVIE_ISSUE || this.requestType == RequestTypes.ACTOR_ISSUE || this.requestType == RequestTypes.OTHERS) {
                    String notification = "Cererea ta de tipul " + this.requestType;
                    if (this.requestType == RequestTypes.ACTOR_ISSUE || this.requestType == RequestTypes.MOVIE_ISSUE)
                        notification += " pentru " + this.subject;
                    notification += " a fost rezolvata "  + " de catre " + this.to;
                    IMDB.getInstance().findUser(this.username).notifications.add(notification);
                }
                break;
            }
            case 2: {
                String notification = "Cererea ta de tipul " + this.requestType + " a fost respeinsa!";
                IMDB.getInstance().findUser(this.username).notifications.add(notification);
                break;
            }
            case 3: {
                if(this.to.equals( "ADMIN") == false) {
                    String notification = "Cererea de la utilizatorul " + this.username + " de tipul " + this.requestType;
                    if (this.requestType == RequestTypes.ACTOR_ISSUE || this.requestType == RequestTypes.MOVIE_ISSUE)
                        notification += " pentru " + this.subject;
                    notification += " a fost stearsa.";
                    IMDB.getInstance().findUser(this.to).notifications.add(notification);
                } else {
                    String notification = "Cererea de la utilizatorul " + this.username + " de tipul " + this.requestType;
                    if (this.requestType == RequestTypes.ACTOR_ISSUE || this.requestType == RequestTypes.MOVIE_ISSUE)
                        notification += " pentru " + this.subject;
                    notification += " a fost stearsa.";
                    for (User u : IMDB.getInstance().getUserList())
                        if (u instanceof Admin)
                            u.notifications.add(notification);
                }
                break;
            }
        }

    }
}
