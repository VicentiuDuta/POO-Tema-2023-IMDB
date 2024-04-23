package org.example;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kotlin.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.script.ScriptEngine;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.Scanner;

public class IMDB implements ExperienceStrategy{
    private List<User> userList;
    private List<Actor> actors;

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<Request> requestList) {
        this.requestList = requestList;
    }

    public List<Production> getProductions() {
        return productions;
    }

    public void setProductions(List<Production> productions) {
        this.productions = productions;
    }

    private List<Request> requestList;
    private List<Production> productions;
    private static IMDB instance = null;

    private IMDB() {

    }

    public static IMDB getInstance() {
        if (instance == null)
            instance = new IMDB();
        return instance;
    }

    @Override
    public int calculateExperience(User user, boolean isAdd) {
        if(isAdd)
            return Integer.parseInt(user.experience) + 1;
        else
            return Integer.parseInt(user.experience) - 1;

    }

    public User findUser(String username) {
        for(int i = 0; i < IMDB.getInstance().userList.size(); i++)
            if(IMDB.getInstance().userList.get(i).username.equals(username))
                return IMDB.getInstance().userList.get(i);
        return null;
    }

    public Production findProd(String name) {
        int i;
        for (i = 0; i < IMDB.getInstance().productions.size(); i++)
            if (IMDB.getInstance().productions.get(i).title.equals(name))
                return IMDB.getInstance().productions.get(i);
        return null;
    }

    public Actor findActor(String name) {
        int i;
        for (i = 0; i < IMDB.getInstance().actors.size(); i++)
            if (IMDB.getInstance().actors.get(i).name.equals(name))
                return IMDB.getInstance().actors.get(i);
        return null;
    }
    public void readActors() throws FileNotFoundException {
        JSONParser jsonParser = new JSONParser();
        IMDB.getInstance().actors = new ArrayList<>();

        try(FileReader reader = new FileReader("/home/vicduti1948/Desktop/POO-assignment/POO-Tema-2023-checker/POO-Tema-2023-checker/src/main/resources/input/actors.json")) {
            Object obj = jsonParser.parse(reader);

            JSONArray actorsList = (JSONArray) obj;

            for(Object actorObj : actorsList) {
                JSONObject actorJson = (JSONObject) actorObj;

                String name = (String) actorJson.get("name");
                //System.out.println(name);

                JSONArray performancesList = (JSONArray) actorJson.get("performances");
                List<Map<String, String>> performances = new ArrayList<>();
                for(Object performanceObj : performancesList) {
                    JSONObject performanceJson = (JSONObject) performanceObj;

                    String title = (String) performanceJson.get("title");
                   // System.out.println(title);
                    String type = (String) performanceJson.get("type");
                    //System.out.println(type);

                    Map<String, String> perf = new HashMap<>();
                    perf.put(title, type);
                    performances.add(perf);
                }

                String biography = (String) actorJson.get("biography");
                //System.out.println(biography);
                Actor actor = new Actor(name,performances,biography);
                IMDB.getInstance().actors.add(actor);
            }
        } catch (IOException e) {
            System.out.println("Eroare citire actori");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public void readProductions() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        IMDB.getInstance().productions = new ArrayList<>();

        try (FileReader reader = new FileReader("/home/vicduti1948/Desktop/POO-assignment/POO-Tema-2023-checker/POO-Tema-2023-checker/src/main/resources/input/production.json")) {
            Object obj = jsonParser.parse(reader);

            JSONArray productionList = (JSONArray) obj;
            for(Object prodObj : productionList) {
                JSONObject prodJson = (JSONObject) prodObj;

                String title = (String) prodJson.get("title");
                String type = (String) prodJson.get("type");

                List<String> directors = new ArrayList<>();
                 JSONArray directorsList = (JSONArray) prodJson.get("directors");
                    for(Object directorObj : directorsList)
                    directors.add((String) directorObj);

                List<String> actors = new ArrayList<>();
                JSONArray actorsList = (JSONArray) prodJson.get("actors");
                for(Object actorObj : actorsList)
                    actors.add((String) actorObj);

                List<Genre> genres = new ArrayList<>();
                JSONArray genresList = (JSONArray) prodJson.get("genres");
                for(Object genreObj : genresList)
                    genres.add(Genre.valueOf((String) genreObj));

                List<Rating> ratings = new ArrayList<>();
                JSONArray ratingsList = (JSONArray) prodJson.get("ratings");
                for(Object ratingObj : ratingsList) {
                    JSONObject ratingJson = (JSONObject) ratingObj;
                    String username = (String) ratingJson.get("username");
                    Long rating = (Long) ratingJson.get("rating");
                    String comment = (String) ratingJson.get("comment");
                    Rating r = new Rating(username, rating.intValue(), comment);
                    ratings.add(r);
                }

                String plot = (String) prodJson.get("plot");
                Double averageRating = (Double) prodJson.get("averageRating");

                if(type.equals("Movie")) {
                    Long releaseYear = null;
                    if(prodJson.containsKey("releaseYear"))
                        releaseYear = (Long) prodJson.get("releaseYear");
                    int releaseYear_int = 0;
                    if(releaseYear != null)
                        releaseYear_int = releaseYear.intValue();
                    String duration = (String) prodJson.get("duration");
                    Movie x = new Movie(title,type,directors,actors,genres,ratings,plot,averageRating,duration, releaseYear_int);
                    IMDB.getInstance().productions.add(x);
                    //Sortarea recenziilor dupa experienta userilor:


                }

                else {
                    Long numSeasons = (Long) prodJson.get("numSeasons");
                    int releaseYear = ((Long) prodJson.get("releaseYear")).intValue();
                    Map<String, List<Episode>> seasons = parseSeasons((JSONObject) prodJson.get("seasons"));

                    Series series = new Series(title, type, directors, actors, genres, ratings, plot, averageRating, releaseYear, numSeasons.intValue(), seasons);
                    IMDB.getInstance().productions.add(series);
                }


                }
                Admin.adminActors = new ArrayList<>();
                //Daca vreun actor care a contribuit la o productie nu apare in lista de actori:
                for(Production p : IMDB.getInstance().getProductions())
                    for(String actorName : p.actorList) {
                        Actor a = findActor(actorName);
                        if(a == null) {
                            List<Map<String, String>> map = new ArrayList<>();
                            Map<String, String> m = new HashMap<>();
                            m.put(p.title, p.type);

                            a = new Actor(actorName, map, null);
                            IMDB.getInstance().actors.add(a);
                            Admin.adminActors.add(a);
                        }
                    }


            }
        }
    public void readUsers() throws FileNotFoundException {
        JSONParser jsonParser = new JSONParser();
        IMDB.getInstance().userList = new ArrayList<>();

        try(FileReader reader = new FileReader("/home/vicduti1948/Desktop/POO-assignment/POO-Tema-2023-checker/POO-Tema-2023-checker/src/main/resources/input/accounts.json")) {
            Object obj = jsonParser.parse(reader);

            JSONArray userList = (JSONArray) obj;
            for(Object userObj : userList) {
                JSONObject userJson = (JSONObject) userObj;

                String username = (String) userJson.get("username");
                String experience = (String) userJson.get("experience");

                JSONObject informationJson = (JSONObject) userJson.get("information");
                JSONObject credentialsJson = (JSONObject) informationJson.get("credentials");

                String email = (String) credentialsJson.get("email");
                String password = (String) credentialsJson.get("password");
                Credentials credentials = new Credentials(email, password);

                User.Information.InformationBuilder informationBuilder = new User.Information.InformationBuilder();
                informationBuilder.Name((String) informationJson.get("name"))
                        .country((String) informationJson.get("country"))
                        .age((Long) informationJson.get("age"))
                        .Credentials(credentials);

                String gender = (String) informationJson.get("gender");
                char gender_char;
                if (gender.equals("Female"))
                    gender_char = 'F';
                else
                    gender_char = 'M';

                informationBuilder.gender(gender_char);

                String birthDateString = (String) informationJson.get("birthDate");
                LocalDate birthDate = LocalDate.parse(birthDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                informationBuilder.birthDate(birthDate);

                User.Information information = informationBuilder.build();

                String userType = (String) userJson.get("userType");

                SortedSet favorites = new TreeSet<>((t, t1) -> {
                    if (t instanceof Production && t1 instanceof Production)
                        return ((Production) t).title.compareTo(((Production) t1).title);
                    else if (t instanceof Production && t1 instanceof Actor)
                        return ((Production) t).title.compareTo(((Actor) t1).name);
                    else if (t instanceof Actor && t1 instanceof Production)
                        return ((Actor) t).name.compareTo(((Production) t1).title);
                    else if (t instanceof Actor && t1 instanceof Actor)
                        return ((Actor) t).name.compareTo(((Actor) t1).name);
                    return 0;
                });

                SortedSet contributions = new TreeSet<>((t, t1) -> {
                    if (t instanceof Production && t1 instanceof Production)
                        return ((Production) t).title.compareTo(((Production) t1).title);
                    else if (t instanceof Production && t1 instanceof Actor)
                        return ((Production) t).title.compareTo(((Actor) t1).name);
                    else if (t instanceof Actor && t1 instanceof Production)
                        return ((Actor) t).name.compareTo(((Production) t1).title);
                    else if (t instanceof Actor && t1 instanceof Actor)
                        return ((Actor) t).name.compareTo(((Actor) t1).name);
                    return 0;
                });

                List<String> notifications = null;

                switch (AccountType.valueOf(userType)) {
                    case Regular: {
                        if (userJson.containsKey("favoriteProductions")) {
                            JSONArray favProdArray = (JSONArray) userJson.get("favoriteProductions");
                            List<String> favoriteProductions = new ArrayList<>();
                            for (Object productionObj : favProdArray)
                                favoriteProductions.add((String) productionObj);

                            for (int i = 0; i < favoriteProductions.size(); i++)
                                favorites.add(IMDB.getInstance().findProd(favoriteProductions.get(i)));
                        }

                        if (userJson.containsKey("favoriteActors")) {
                            JSONArray favActorsArray = (JSONArray) userJson.get("favoriteActors");
                            List<String> favoriteActors = new ArrayList<>();
                            for (Object ActorObj : favActorsArray)
                                favoriteActors.add((String) ActorObj);

                            for (int i = 0; i < favoriteActors.size(); i++)
                                favorites.add(IMDB.getInstance().findActor(favoriteActors.get(i)));

                        }

                        notifications = new ArrayList<>();
                        if (userJson.containsKey("notifications")) {
                            JSONArray notificationsArray = (JSONArray) userJson.get("notifications");
                            for (Object notificationObj : notificationsArray)
                                notifications.add((String) notificationObj);

                        }

                        User newUser = UserFactory.factory(AccountType.Regular);
                        newUser.username = username;
                        newUser.type = AccountType.Regular;
                        newUser.notifications = notifications;
                        newUser.informations = information;
                        newUser.favorites = favorites;
                        newUser.experience = experience;
                        IMDB.getInstance().userList.add(newUser);

                        break;
                    }

                    case Admin: {
                        if (userJson.containsKey("productionsContribution")) {
                            JSONArray favProdArray = (JSONArray) userJson.get("productionsContribution");
                            List<String> productionsContribution = new ArrayList<>();
                            for (Object productionObj : favProdArray)
                                productionsContribution.add((String) productionObj);

                            for (int i = 0; i < productionsContribution.size(); i++)
                                contributions.add(IMDB.getInstance().findProd(productionsContribution.get(i)));
                        }

                        if (userJson.containsKey("actorsContribution")) {
                            JSONArray contribActorsArray = (JSONArray) userJson.get("actorsContribution");
                            List<String> contribActors = new ArrayList<>();
                            for (Object ActorObj : contribActorsArray)
                                contribActors.add((String) ActorObj);

                            for (int i = 0; i < contribActors.size(); i++)
                                contributions.add(IMDB.getInstance().findActor(contribActors.get(i)));

                        }
                        User newUser = UserFactory.factory(AccountType.Admin);
                        newUser.username = username;
                        newUser.type = AccountType.Admin;
                        newUser.notifications = new ArrayList();
                        newUser.informations = information;
                        newUser.favorites = favorites;
                        newUser.experience = experience;
                        ((Admin) newUser).contributions = contributions;
                        ((Admin) newUser).requestList = new ArrayList<>();
                        IMDB.getInstance().userList.add(newUser);
                        break;
                    }

                    case Contributor: {
                        if (userJson.containsKey("favoriteProductions")) {
                            JSONArray favProdArray = (JSONArray) userJson.get("favoriteProductions");
                            List<String> favoriteProductions = new ArrayList<>();
                            for (Object productionObj : favProdArray)
                                favoriteProductions.add((String) productionObj);

                            for (int i = 0; i < favoriteProductions.size(); i++)
                                favorites.add(IMDB.getInstance().findProd(favoriteProductions.get(i)));
                        }

                        if (userJson.containsKey("favoriteActors")) {
                            JSONArray favActorsArray = (JSONArray) userJson.get("favoriteActors");
                            List<String> favoriteActors = new ArrayList<>();
                            for (Object ActorObj : favActorsArray)
                                favoriteActors.add((String) ActorObj);

                            for (int i = 0; i < favoriteActors.size(); i++)
                                favorites.add(IMDB.getInstance().findActor(favoriteActors.get(i)));
                        }

                        if (userJson.containsKey("productionsContribution")) {
                            JSONArray favProdArray = (JSONArray) userJson.get("productionsContribution");
                            List<String> productionsContribution = new ArrayList<>();
                            for (Object productionObj : favProdArray)
                                productionsContribution.add((String) productionObj);

                            for (int i = 0; i < productionsContribution.size(); i++)
                                contributions.add(IMDB.getInstance().findProd(productionsContribution.get(i)));
                        }

                        if (userJson.containsKey("actorsContribution")) {
                            JSONArray contribActorsArray = (JSONArray) userJson.get("actorsContribution");
                            List<String> contribActors = new ArrayList<>();
                            for (Object ActorObj : contribActorsArray)
                                contribActors.add((String) ActorObj);

                            for (int i = 0; i < contribActors.size(); i++)
                                contributions.add(IMDB.getInstance().findActor(contribActors.get(i)));

                        }
                        notifications = new ArrayList<>();
                        if (userJson.containsKey("notifications")) {

                            JSONArray notificationsArray = (JSONArray) userJson.get("notifications");
                            for (Object notificationObj : notificationsArray)
                                notifications.add((String) notificationObj);
                        }

                        User newUser = UserFactory.factory(AccountType.Contributor);
                        newUser.username = username;
                        newUser.type = AccountType.Contributor;
                        newUser.notifications = notifications;
                        newUser.informations = information;
                        newUser.favorites = favorites;
                        newUser.experience = experience;
                        ((Contributor) newUser).contributions = contributions;
                        ((Contributor) newUser).requestList = new ArrayList<>();
                        IMDB.getInstance().userList.add(newUser);
                        break;
                    }
                }
            }
            for(User u : IMDB.getInstance().userList) {
                if(u instanceof Staff) {
                    for(Object contribution : ((Staff<?>) u).contributions) {
                        if(contribution instanceof Production)
                            ((Production) contribution).addedBy = u.username;
                        if(contribution instanceof Actor)
                            ((Actor) contribution).addedBy = u.username;
                    }
                }
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        } catch (InformationIncompleteException e) {
            throw new RuntimeException(e);
        }
    }

    public void readRequests() throws FileNotFoundException {
        JSONParser jsonParser = new JSONParser();
        IMDB.getInstance().requestList = new ArrayList<>();
        Request.RequestsHolder.requestList = new ArrayList<>();

        try(FileReader reader = new FileReader("/home/vicduti1948/Desktop/POO-assignment/POO-Tema-2023-checker/POO-Tema-2023-checker/src/main/resources/input/requests.json")) {
            Object obj = jsonParser.parse(reader);
            JSONArray requestList = (JSONArray) obj;

            for(Object requestObj : requestList) {
                JSONObject requestJson = (JSONObject) requestObj;

                String type = (String) requestJson.get("type");

                String createdDate_str = (String) requestJson.get("createdDate");
                LocalDateTime createdDate = LocalDateTime.parse(createdDate_str, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                String username = (String) requestJson.get("username");
                switch(type) {
                    case "DELETE_ACCOUNT" : {
                        String to = "ADMIN";
                        String description = (String) requestJson.get("description");
                        Request r = new Request(RequestTypes.DELETE_ACCOUNT, createdDate, null, description, username, to);
                        IMDB.getInstance().requestList.add(r);
                        Request.RequestsHolder.addRequest(r);
                        break;
                    }
                    case "OTHERS" : {
                        String to = "ADMIN";
                        String description = (String) requestJson.get("description");
                        Request r = new Request(RequestTypes.OTHERS, createdDate, null, description, username, to);
                        IMDB.getInstance().requestList.add(r);
                        Request.RequestsHolder.addRequest(r);
                        break;
                    }
                    case "ACTOR_ISSUE" : {
                        String actorName = (String) requestJson.get("actorName");
                        String to = (String) requestJson.get("to");
                        String description = (String) requestJson.get("description");
                        Request r = new Request(RequestTypes.ACTOR_ISSUE,createdDate,actorName,description,username,to);
                        IMDB.getInstance().requestList.add(r);

                        User reciever = IMDB.getInstance().findUser(to);
                        if(((Staff)reciever).requestList == null)
                            ((Staff)reciever).requestList = new ArrayList<>();
                        ((Staff)reciever).requestList.add(r);
                        break;
                    }
                    case "MOVIE_ISSUE" : {
                        String movieTitle = (String) requestJson.get("movieTitle");
                        String to = (String) requestJson.get("to");
                        String description = (String) requestJson.get("description");
                        Request r = new Request(RequestTypes.MOVIE_ISSUE,createdDate,movieTitle,description,username,to);
                        IMDB.getInstance().requestList.add(r);

                        User reciever = IMDB.getInstance().findUser(to);
                        if(((Staff)reciever).requestList == null)
                            ((Staff)reciever).requestList = new ArrayList<>();
                        ((Staff)reciever).requestList.add(r);
                        break;
                    }

                }
            }

//            for(int i = 0; i < IMDB.getInstance().requestList.size(); i++) {
//                Request r = IMDB.getInstance().requestList.get(i);
//                System.out.println(r.getRequestType());
//                System.out.println(r.getCreationDate());
//                System.out.println(r.getUsername());
//                System.out.println(r.getSubject());
//                System.out.println(r.getTo());
//                System.out.println(r.getDescription() + "\n\n");
//            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public Map<String, List<Episode>> parseSeasons(JSONObject seasonsObj) {
        Map<String, List<Episode>> seasonsMap = new LinkedHashMap<>();

        for (Object seasonKeyObj : seasonsObj.keySet()) {
            String seasonKey = (String) seasonKeyObj;
            JSONArray episodesArray = (JSONArray) seasonsObj.get(seasonKey);

            List<Episode> episodes = new ArrayList<>();
            for (Object episodeObj : episodesArray) {
                JSONObject episodeJson = (JSONObject) episodeObj;
                String episodeName = (String) episodeJson.get("episodeName");
                String duration = (String) episodeJson.get("duration");

                Episode episode = new Episode(episodeName, duration);
                episodes.add(episode);
            }

            seasonsMap.put(seasonKey, episodes);
        }

        return seasonsMap;
    }
    public User logIn(Scanner myObj) {
        System.out.println("Welcome back! Enter your credentials!");

        User user = null;
        while(user == null) {
            System.out.println("email: ");
            String email = myObj.nextLine();

            System.out.println("password: ");
            String password = myObj.nextLine();

            for (User u : IMDB.getInstance().userList)
                if (u.informations.getCredentials().getEmail().equals(email))
                    if (u.informations.getCredentials().getPassword().equals(password))
                        user = u;

            if(user == null)
                System.out.println("Incorrect email/password. Please try again!");
        }

        return user;

    }

    public void updateNotifications () {
        for (Production p : IMDB.getInstance().getProductions()) {
            for (Rating r : p.ratingList) {
                User u = IMDB.getInstance().findUser(r.username);
                r.Subscribe(u, p);
            }
            p.observers.add(IMDB.getInstance().findUser(p.addedBy));
        }
    }




            public void run() throws IOException, ParseException, InvalidCommandException, InformationIncompleteException {
                IMDB.getInstance().readActors();
                IMDB.getInstance().readProductions();
                IMDB.getInstance().readUsers();
                IMDB.getInstance().readRequests();
                IMDB.getInstance().updateNotifications();

                Admin.adminProductions = new ArrayList<>();
                Admin.adminActors = new ArrayList<>();
                //Sortarea recenziilor productiilor dupa rating-ul userilor
                for (Production x : IMDB.getInstance().productions)
                    x.ratingList.sort(new Comparator<Rating>() {
                        @Override
                        public int compare(Rating rating, Rating t1) {
                            User r1 = findUser(rating.username);
                            String e1 = "0";
                            if (r1 != null && r1.experience != null)
                                e1 = r1.experience;
                            Integer exp1 = Integer.parseInt(e1);

                            User r2 = findUser(t1.username);
                            String e2 = "0";
                            if (r2 != null && r2.experience != null)
                                e2 = r2.experience;
                            Integer exp2 = Integer.parseInt(e2);
                            return exp2-exp1;

                        }
                    });

                Scanner myObj = new Scanner(System.in);
                int control = 0;
                while (control == 0) {
                    User u = IMDB.getInstance().logIn(myObj);
                    if (u != null) {
                        System.out.println("Welcome back user " + u.username);
                        System.out.println("Username: " + u.username);
                        System.out.println("User experience: " + u.experience);

                        System.out.println("Choose action: ");

                        int comanda = 0;
                        try {

                            switch (u.type) {
                                case Regular: {
                                    int control2 = 1;
                                    while (control2 == 1) {
                                        System.out.println("1) View productions details");
                                        System.out.println("2) View actors details");
                                        System.out.println("3) View notifications");
                                        System.out.println("4) Search for actor/movie/series");
                                        System.out.println("5) Add/Delete to/from favorites");
                                        System.out.println("6) Show favorites");
                                        System.out.println("7) Create/Delete request");
                                        System.out.println("8) Create/Delete rating for a production");
                                        System.out.println("9) Log out");
                                        System.out.println("10) Exit the application");

                                        comanda = myObj.nextInt();
                                        if (comanda < 1 || comanda > 10)
                                            throw new InvalidCommandException();

                                        if (comanda == 1) {
                                            System.out.println("Filters: ");
                                            System.out.println("1) Filter by genre");
                                            System.out.println("2) Filter by number of ratings");
                                            System.out.println("3) None");

                                            int comanda2 = myObj.nextInt();
                                            if (comanda2 < 1 || comanda2 > 3)
                                                throw new InvalidCommandException();

                                            else if (comanda2 == 3)
                                                for (Production prod : IMDB.getInstance().productions)
                                                    prod.displayInfo();

                                            else if (comanda2 == 2) {
                                                IMDB.getInstance().productions.sort((production, t1) -> t1.ratingList.size() - production.ratingList.size());
                                                for (Production prod : IMDB.getInstance().productions)
                                                    prod.displayInfo();
                                            } else {
                                                System.out.println("Enter the genre you want to filter by: " + Arrays.toString(Genre.values()));
                                                myObj.nextLine();
                                                String genre = myObj.nextLine();
                                                Genre.valueOf(genre);
                                                for (Production prod : IMDB.getInstance().productions)
                                                    if (prod.genreList.contains(Genre.valueOf(genre)))
                                                        prod.displayInfo();
                                            }

                                        } else if (comanda == 2) {
                                            System.out.println("Do you want the actors to be sorted alphabetically? Yes / No");
                                            myObj.nextLine();
                                            String comanda2 = myObj.nextLine();
                                            if (comanda2.equals("No") || comanda2.equals("no"))
                                                for (Actor actor : IMDB.getInstance().actors)
                                                    actor.displayInfo();
                                            else if (comanda2.equals("Yes") || comanda2.equals("yes")) {
                                                IMDB.getInstance().actors.sort((actor, t1) -> actor.name.compareTo(t1.name));
                                                for (Actor actor : IMDB.getInstance().actors)
                                                    actor.displayInfo();
                                            } else
                                                throw new InvalidCommandException();
                                        } else if (comanda == 3) {
                                            if (u.notifications != null )
                                                if(u.notifications.isEmpty() == false) {
                                                    System.out.println("Notifications: ");
                                                    for (Object obj : u.notifications)
                                                        System.out.println((String) obj);
                                                    System.out.println("Do you want to clear your notifications?");
                                                    myObj.nextLine();
                                                    String clearcmd = myObj.nextLine();
                                                    if (clearcmd.equalsIgnoreCase("yes")) {
                                                        u.notifications.clear();
                                                        System.out.println("You have reached the end of your notification list!");
                                                        System.out.println("You have successfully cleared your notifications!");
                                                    }
                                                    else
                                                        System.out.println("You have reached the end of your notification list!");
                                                }
                                                else
                                                    System.out.println("You don't have any notifications!");


                                             else {
                                                System.out.println("You don't have any notifications!");
                                            }
                                        } else if (comanda == 4) {
                                            System.out.println("Enter the type : actor / movie / series");
                                            myObj.nextLine();
                                            String comanda2 = myObj.nextLine();
                                            if (!Objects.equals(comanda2, "actor") && !Objects.equals(comanda2, "movie") && !Objects.equals(comanda2, "series") && !Objects.equals(comanda2, "Actor") && !Objects.equals(comanda2, "Movie") && !Objects.equals(comanda2, "Series"))
                                                throw new InvalidCommandException();

                                            System.out.println("Enter the name of the " + comanda2);
                                            String comanda3 = myObj.nextLine();

                                            if (comanda2.equals("actor") || comanda2.equals("Actor")) {
                                                Actor a = findActor(comanda3);
                                                if (a != null)
                                                    a.displayInfo();
                                                else
                                                    System.out.println("There is no such actor!");
                                            } else if (comanda2.equals("movie") || comanda2.equals("Movie")) {
                                                Production p = findProd(comanda3);
                                                if (p != null && p instanceof Movie)
                                                    p.displayInfo();
                                                else
                                                    System.out.println("There is no such movie!");
                                            } else if (comanda2.equals("series") || comanda2.equals("Series")) {
                                                Production p = findProd(comanda3);
                                                if (p != null && p instanceof Series)
                                                    p.displayInfo();
                                                else
                                                    System.out.println("There is no such series!");
                                            } else
                                                throw new InvalidCommandException();

                                        } else if (comanda == 5) {
                                            System.out.println("Enter the type: actor / movie / series");
                                            myObj.nextLine();
                                            String comanda2 = myObj.nextLine();
                                            System.out.println("Do you want to add to favorites or remove from favorites? Type add / remove");
                                            String cmd = myObj.nextLine();
                                            if (Objects.equals(cmd, "add") || Objects.equals(cmd, "Add")) {
                                                if (!Objects.equals(comanda2, "actor") && !Objects.equals(comanda2, "movie") && !Objects.equals(comanda2, "series") && !Objects.equals(comanda2, "Actor") && !Objects.equals(comanda2, "Movie") && !Objects.equals(comanda2, "Series"))
                                                    throw new InvalidCommandException();

                                                System.out.println("Enter the name of the " + comanda2);
                                                String comanda3 = myObj.nextLine();
                                                if (comanda2.equals("actor") || comanda2.equals("Actor")) {
                                                    Actor a = findActor(comanda3);
                                                    if (a != null && !u.favorites.contains(a)) {
                                                        //u.favorites.add(a);
                                                        u.addFavorite(a);
                                                        System.out.println("Actor successfully added to favorites!");
                                                    } else if (a == null)
                                                        System.out.println("Actor " + comanda3 + " couldn't be found!");
                                                    else
                                                        System.out.println("Actor " + comanda3 + " is already your favorite!");

                                                } else if (comanda2.equals("series") || comanda2.equals("Series")) {
                                                    Production p = findProd(comanda3);
                                                    if (p != null && !u.favorites.contains(p) && p instanceof Series) {
                                                        //u.favorites.add(p);
                                                        u.addFavorite(p);
                                                        System.out.println("Series successfully added to favorites!");
                                                    } else if (p == null)
                                                        System.out.println("Series " + comanda3 + " couldn't be found!");
                                                    else if (p instanceof Series == false)
                                                        System.out.println(("There is no such series!"));
                                                    else
                                                        System.out.println("Series " + comanda3 + " is already your favorite!");

                                                } else {
                                                    Production p = findProd(comanda3);
                                                    if (p != null && !u.favorites.contains(p) && p instanceof Movie) {
                                                        //u.favorites.add(p);
                                                        u.addFavorite(p);
                                                        System.out.println("Movie successfully added to favorites!");
                                                    } else if (p == null)
                                                        System.out.println("Movie " + comanda3 + " couldn't be found!");
                                                    else if (p instanceof Series == false)
                                                        System.out.println(("There is no such movie!"));
                                                    else
                                                        System.out.println("Movie " + comanda3 + " is already your favorite!");
                                                }
                                            } else if (Objects.equals(cmd, "remove") || Objects.equals(cmd, "Remove")) {
                                                if (!Objects.equals(comanda2, "actor") && !Objects.equals(comanda2, "movie") && !Objects.equals(comanda2, "series") && !Objects.equals(comanda2, "Actor") && !Objects.equals(comanda2, "Movie") && !Objects.equals(comanda2, "Series"))
                                                    throw new InvalidCommandException();

                                                System.out.println("Enter the name of the " + comanda2);
                                                String comanda3 = myObj.nextLine();
                                                if (comanda2.equals("actor") || comanda2.equals("Actor")) {
                                                    Actor a = findActor(comanda3);
                                                    if (a != null && u.favorites.contains(a)) {
                                                        u.deleteFavorite(a);
                                                        System.out.println("Actor successfully removed from favorites!");
                                                    } else if (a == null)
                                                        System.out.println("There is no such actor!");
                                                    else if (!u.favorites.contains(a))
                                                        System.out.println("Actor " + a.name + " is not in your favorites list!");
                                                } else if (comanda2.equals("movie") || comanda2.equals("Movie")) {
                                                    Production p = findProd(comanda3);
                                                    if (p != null && u.favorites.contains(p) && p instanceof Movie) {
                                                        u.deleteFavorite(p);
                                                        System.out.println("Movie successfully removed from favorites");
                                                    } else if (p == null)
                                                        System.out.println("Movie " + p.title + " couldn't be found!");
                                                    else if (p instanceof Movie == false)
                                                        System.out.println("There is no such movie!");
                                                    else
                                                        System.out.println("Movie " + p.title + " is not in your favorites list!");

                                                } else if (comanda2.equals("series") || comanda2.equals("Series")) {
                                                    Production p = findProd(comanda3);
                                                    if (p != null && u.favorites.contains(p) && p instanceof Movie) {
                                                        u.deleteFavorite(p);
                                                        System.out.println("Series successfully removed from favorites");
                                                    } else if (p == null)
                                                        System.out.println("Series " + p.title + " couldn't be found!");
                                                    else if (p instanceof Series == false)
                                                        System.out.println("There is no such series!");
                                                    else
                                                        System.out.println("Series " + p.title + " is not in your favorites list!");

                                                }


                                            } else
                                                throw new InvalidCommandException();

                                        } else if (comanda == 6) {
                                            if (!u.favorites.isEmpty()) {
                                                System.out.println("These are your favorites!");
                                                int i;
                                                for (Object obj : u.favorites) {
                                                    if (obj instanceof Production)
                                                        ((Production) obj).displayInfo();
                                                    else if (obj instanceof Actor)
                                                        ((Actor) obj).displayInfo();
                                                }
                                            } else
                                                System.out.println("You don't have any favorites yet!");
                                        } else if (comanda == 7) {
                                            myObj.nextLine();
                                            System.out.println("Do you want to create or delete a request? add / remove");
                                            String cmd = myObj.nextLine();
                                            if (cmd.compareToIgnoreCase("add") == 0) {
                                                System.out.println("Enter the request type: DELETE_ACCOUNT / ACTOR_ISSUE / MOVIE_ISSUE / OTHERS");
                                                String requestType = myObj.nextLine();

                                                System.out.println("Describe your request: ");
                                                String requestDescription = myObj.nextLine();

                                                ((Regular<?>) u).createRequest(requestType, requestDescription, u.username);

                                                System.out.println("Request created successfully!");

                                            } else if (cmd.compareToIgnoreCase("remove") == 0) {
                                                int i, contor = 0;
                                                for (i = 0; i < IMDB.getInstance().getRequestList().size(); i++)
                                                    if (IMDB.getInstance().getRequestList().get(i).getUsername().equals(u.username))
                                                        contor++;

                                                if (contor == 0)
                                                    System.out.println("You don't have any pending requests!");
                                                else {
                                                    System.out.println("Type the number of the request you want to remove: ");
                                                    for (i = 0; i < IMDB.getInstance().getRequestList().size(); i++)
                                                        if (IMDB.getInstance().getRequestList().get(i).getUsername().equals(u.username)) {
                                                            Request r = IMDB.getInstance().getRequestList().get(i);
                                                            System.out.println("Request nr. " + i + " RequestType: " + r.getRequestType() + " Description: " + r.getDescription());
                                                        }

                                                    String requestNo = myObj.nextLine();
                                                    int requestNr = Integer.parseInt(requestNo);
                                                    if (IMDB.getInstance().getRequestList().get(requestNr).getUsername().equals(u.username)) {
                                                        Request r = IMDB.getInstance().getRequestList().get(requestNr);
                                                        r.Notify(3);
                                                        IMDB.getInstance().getRequestList().remove(requestNr);
                                                        switch (r.getRequestType()) {
                                                            case ACTOR_ISSUE, MOVIE_ISSUE: {
                                                                User user = findUser(r.getTo());
                                                                if (user != null)
                                                                    ((Staff<?>) user).requestList.remove(r);
                                                                break;
                                                            }
                                                            case OTHERS, DELETE_ACCOUNT: {
                                                                Request.RequestsHolder.removeRequest(r);
                                                                break;
                                                            }
                                                            default:
                                                                break;
                                                        }
                                                        System.out.println("Your request has been deleted successfully!");
                                                    } else
                                                        System.out.println("The request number is wrong! Please try again!");

                                                }

                                            } else
                                                throw new InvalidCommandException();

                                        } else if (comanda == 8) {
                                            myObj.nextLine();
                                            System.out.println("Do you want to create or delete a rating? add / remove");
                                            String cmd = myObj.nextLine();

                                            if (cmd.compareToIgnoreCase("add") == 0) {
                                                System.out.println("Enter the name of the production: ");
                                                String command = myObj.nextLine();
                                                Production p = findProd(command);
                                                if (p == null)
                                                    System.out.println("There is no such production! Please try again!");
                                                else {
                                                    int i, ok = 1;
                                                    for (i = 0; i < p.ratingList.size(); i++)
                                                        if (p.ratingList.get(i).username.equals(u.username))
                                                            ok = 0;

                                                    if (ok == 0)
                                                        System.out.println("You have already rated this production! If you want to change your rating, you need to delete your first rating!");

                                                    else {
                                                        System.out.println("Enter a rating between 1 and 10!");
                                                        String rtg = myObj.nextLine();
                                                        int rating = Integer.parseInt(rtg);

                                                        System.out.println("Describe your review: ");
                                                        String comments = myObj.nextLine();

                                                        Rating rating1 = new Rating(u.username, rating, comments);
                                                        ((Regular<?>) u).addRating(rating1, p);

                                                        int newExperience = calculateExperience(u, true);
                                                        u.experience = String.valueOf(newExperience);

                                                        rating1.Notify(p);
                                                        rating1.Subscribe(u, p);

                                                        System.out.println("Your rating has been added successfully!");

                                                    }

                                                }

                                            } else if (cmd.compareToIgnoreCase("remove") == 0) {
                                                System.out.println("Enter the name of the production: ");
                                                String prod = myObj.nextLine();

                                                Production p = findProd(prod);
                                                if (p == null)
                                                    System.out.println("There is no such production! Please try again!");
                                                else {
                                                    int ok = 1;
                                                    for (Rating r : p.ratingList)
                                                        if (r.username.equals(u.username))
                                                            ok = 0;

                                                    if (ok == 1)
                                                        System.out.println("You haven't rated this production yet!");
                                                    else {
                                                        Rating x = null;
                                                        for (Rating r : p.ratingList)
                                                            if (r.username.equals(u.username)) {
                                                                x = r;
                                                                break;
                                                            }
                                                        if (x != null) {
                                                            p.ratingList.remove(x);
                                                            p.updateRating();
                                                            x.Unsubscribe(u, p);
                                                            System.out.println("Your rating has been deleted successfully!");
                                                            int newExperience = calculateExperience(u, false);
                                                            u.experience = String.valueOf(newExperience);
                                                        }
                                                    }
                                                }
                                            } else
                                                throw new InvalidCommandException();
                                        } else if (comanda == 9) {
                                            //System.exit(0);
                                            control2 = 0;
                                            myObj.nextLine();
                                        }
                                        else if (comanda == 10) {
                                            control = 1;
                                            control2 = 0;
                                            System.out.println("Thanks for using our IMDB!");
                                        }
                                    }
                                    break;
                                }

                                case Contributor: {
                                    int control2 = 1;
                                    while (control2 == 1) {
                                        System.out.println("1) View productions details");
                                        System.out.println("2) View actors details");
                                        System.out.println("3) View notifications");
                                        System.out.println("4) Search for actor/movie/series");
                                        System.out.println("5) Add/Delete to/from favorites");
                                        System.out.println("6) Show favorites");
                                        System.out.println("7) Create/Delete request");
                                        System.out.println("8) Add/Remove a production/actor from system");
                                        System.out.println("9) View and solve your requests");
                                        System.out.println("10) Update actor/production");
                                        System.out.println("11) Log out");
                                        System.out.println("12) Exit the application");

                                        comanda = myObj.nextInt();
                                        if (comanda < 1 || comanda > 12)
                                            throw new InvalidCommandException();

                                        if (comanda == 1) {
                                            System.out.println("Filters: ");
                                            System.out.println("1) Filter by genre");
                                            System.out.println("2) Filter by number of ratings");
                                            System.out.println("3) None");

                                            int comanda2 = myObj.nextInt();
                                            if (comanda2 < 1 || comanda2 > 3)
                                                throw new InvalidCommandException();

                                            else if (comanda2 == 3)
                                                for (Production prod : IMDB.getInstance().productions)
                                                    prod.displayInfo();

                                            else if (comanda2 == 2) {
                                                IMDB.getInstance().productions.sort((production, t1) -> t1.ratingList.size() - production.ratingList.size());
                                                for (Production prod : IMDB.getInstance().productions)
                                                    prod.displayInfo();
                                            } else {
                                                System.out.println("Enter the genre you want to filter by: " + Arrays.toString(Genre.values()));
                                                myObj.nextLine();
                                                String genre = myObj.nextLine();
                                                Genre.valueOf(genre);
                                                for (Production prod : IMDB.getInstance().productions)
                                                    if (prod.genreList.contains(Genre.valueOf(genre)))
                                                        prod.displayInfo();
                                            }
                                        } else if (comanda == 2) {
                                            System.out.println("Do you want the actors to be sorted alphabetically? Yes / No");
                                            myObj.nextLine();
                                            String comanda2 = myObj.nextLine();
                                            if (comanda2.equals("No") || comanda2.equals("no"))
                                                for (Actor actor : IMDB.getInstance().actors)
                                                    actor.displayInfo();
                                            else if (comanda2.equals("Yes") || comanda2.equals("yes")) {
                                                IMDB.getInstance().actors.sort((actor, t1) -> actor.name.compareTo(t1.name));
                                                for (Actor actor : IMDB.getInstance().actors)
                                                    actor.displayInfo();
                                            } else
                                                throw new InvalidCommandException();
                                        } else if (comanda == 3) {
                                            if (u.notifications != null)
                                                if( u.notifications.isEmpty() == false) {
                                                    System.out.println("These are your notifications:");
                                                    for (Object obj : u.notifications)
                                                        System.out.println((String) obj);
                                                    myObj.nextLine();
                                                    System.out.println("Do you want to clear your notifications?");
                                                    String clearcmd = myObj.nextLine();
                                                    if (clearcmd.equalsIgnoreCase("yes")) {
                                                        u.notifications.clear();
                                                        System.out.println("You have reached the end of your notification list!");
                                                        System.out.println("You have successfully cleared your notifications!");
                                                    }
                                                    else
                                                        System.out.println("You have reached the end of your notification list!");
                                                }
                                                else
                                                System.out.println("You don't have any notifications!");
                                                else {
                                                System.out.println("You don't have any notifications!");
                                                }
                                        } else if (comanda == 4) {
                                            myObj.nextLine();
                                            System.out.println("Enter the type : actor / movie / series");
                                            String comanda2 = myObj.nextLine();
                                            if (!Objects.equals(comanda2, "actor") && !Objects.equals(comanda2, "movie") && !Objects.equals(comanda2, "series") && !Objects.equals(comanda2, "Actor") && !Objects.equals(comanda2, "Movie") && !Objects.equals(comanda2, "Series"))
                                                throw new InvalidCommandException();

                                            System.out.println("Enter the name of the " + comanda2);
                                            String comanda3 = myObj.nextLine();

                                            if (comanda2.equals("actor") || comanda2.equals("Actor")) {
                                                Actor a = findActor(comanda3);
                                                if (a != null)
                                                    a.displayInfo();
                                                else
                                                    System.out.println("There is no such actor!");
                                            } else if (comanda2.equals("movie") || comanda2.equals("Movie")) {
                                                Production p = findProd(comanda3);
                                                if (p != null && p instanceof Movie)
                                                    p.displayInfo();
                                                else
                                                    System.out.println("There is no such movie!");
                                            } else if (comanda2.equals("series") || comanda2.equals("Series")) {
                                                Production p = findProd(comanda3);
                                                if (p != null && p instanceof Series)
                                                    p.displayInfo();
                                                else
                                                    System.out.println("There is no such series!");
                                            } else
                                                throw new InvalidCommandException();
                                        } else if (comanda == 5) {
                                            System.out.println("Enter the type: actor / movie / series");
                                            myObj.nextLine();
                                            String comanda2 = myObj.nextLine();
                                            System.out.println("Do you want to add to favorites or remove from favorites? Type add / remove");
                                            String cmd = myObj.nextLine();
                                            if (Objects.equals(cmd, "add") || Objects.equals(cmd, "Add")) {
                                                if (!Objects.equals(comanda2, "actor") && !Objects.equals(comanda2, "movie") && !Objects.equals(comanda2, "series") && !Objects.equals(comanda2, "Actor") && !Objects.equals(comanda2, "Movie") && !Objects.equals(comanda2, "Series"))
                                                    throw new InvalidCommandException();

                                                System.out.println("Enter the name of the " + comanda2);
                                                String comanda3 = myObj.nextLine();
                                                if (comanda2.equals("actor") || comanda2.equals("Actor")) {
                                                    Actor a = findActor(comanda3);
                                                    if (a != null && !u.favorites.contains(a)) {
                                                        //u.favorites.add(a);
                                                        u.addFavorite(a);
                                                        System.out.println("Actor successfully added to favorites!");
                                                    } else if (a == null)
                                                        System.out.println("Actor " + comanda3 + " couldn't be found!");
                                                    else
                                                        System.out.println("Actor " + comanda3 + " is already your favorite!");

                                                } else if (comanda2.equals("series") || comanda2.equals("Series")) {
                                                    Production p = findProd(comanda3);
                                                    if (p != null && !u.favorites.contains(p) && p instanceof Series) {
                                                        //u.favorites.add(p);
                                                        u.addFavorite(p);
                                                        System.out.println("Series successfully added to favorites!");
                                                    } else if (p == null)
                                                        System.out.println("Series " + comanda3 + " couldn't be found!");
                                                    else if (p instanceof Series == false)
                                                        System.out.println(("There is no such series!"));
                                                    else
                                                        System.out.println("Series " + comanda3 + " is already your favorite!");

                                                } else {
                                                    Production p = findProd(comanda3);
                                                    if (p != null && !u.favorites.contains(p) && p instanceof Movie) {
                                                        //u.favorites.add(p);
                                                        u.addFavorite(p);
                                                        System.out.println("Movie successfully added to favorites!");
                                                    } else if (p == null)
                                                        System.out.println("Movie " + comanda3 + " couldn't be found!");
                                                    else if (p instanceof Series == false)
                                                        System.out.println(("There is no such movie!"));
                                                    else
                                                        System.out.println("Movie " + comanda3 + " is already your favorite!");
                                                }
                                            } else if (Objects.equals(cmd, "remove") || Objects.equals(cmd, "Remove")) {
                                                if (!Objects.equals(comanda2, "actor") && !Objects.equals(comanda2, "movie") && !Objects.equals(comanda2, "series") && !Objects.equals(comanda2, "Actor") && !Objects.equals(comanda2, "Movie") && !Objects.equals(comanda2, "Series"))
                                                    throw new InvalidCommandException();

                                                System.out.println("Enter the name of the " + comanda2);
                                                String comanda3 = myObj.nextLine();
                                                if (comanda2.equals("actor") || comanda2.equals("Actor")) {
                                                    Actor a = findActor(comanda3);
                                                    if (a != null && u.favorites.contains(a)) {
                                                        u.deleteFavorite(a);
                                                        System.out.println("Actor successfully removed from favorites!");
                                                    } else if (a == null)
                                                        System.out.println("There is no such actor!");
                                                    else if (!u.favorites.contains(a))
                                                        System.out.println("Actor " + a.name + " is not in your favorites list!");
                                                } else if (comanda2.equals("movie") || comanda2.equals("Movie")) {
                                                    Production p = findProd(comanda3);
                                                    if (p != null && u.favorites.contains(p) && p instanceof Movie) {
                                                        u.deleteFavorite(p);
                                                        System.out.println("Movie successfully removed from favorites");
                                                    } else if (p == null)
                                                        System.out.println("Movie " + p.title + " couldn't be found!");
                                                    else if (p instanceof Movie == false)
                                                        System.out.println("There is no such movie!");
                                                    else
                                                        System.out.println("Movie " + p.title + " is not in your favorites list!");

                                                } else if (comanda2.equals("series") || comanda2.equals("Series")) {
                                                    Production p = findProd(comanda3);
                                                    if (p != null && u.favorites.contains(p) && p instanceof Movie) {
                                                        u.deleteFavorite(p);
                                                        System.out.println("Series successfully removed from favorites");
                                                    } else if (p == null)
                                                        System.out.println("Series " + p.title + " couldn't be found!");
                                                    else if (p instanceof Series == false)
                                                        System.out.println("There is no such series!");
                                                    else
                                                        System.out.println("Series " + p.title + " is not in your favorites list!");

                                                }


                                            } else
                                                throw new InvalidCommandException();
                                        } else if (comanda == 6) {
                                            if (!u.favorites.isEmpty()) {
                                                System.out.println("These are your favorites!");
                                                int i;
                                                for (Object obj : u.favorites) {
                                                    if (obj instanceof Production)
                                                        ((Production) obj).displayInfo();
                                                    else if (obj instanceof Actor)
                                                        ((Actor) obj).displayInfo();
                                                }
                                            } else
                                                System.out.println("You don't have any favorites yet!");
                                        } else if (comanda == 7) {
                                            myObj.nextLine();
                                            System.out.println("Do you want to create or delete a request? add / remove");
                                            String cmd = myObj.nextLine();
                                            if (cmd.compareToIgnoreCase("add") == 0) {
                                                System.out.println("Enter the request type: DELETE_ACCOUNT / ACTOR_ISSUE / MOVIE_ISSUE / OTHERS");
                                                String requestType = myObj.nextLine();

                                                if (requestType.compareToIgnoreCase("DELETE_ACCOUNT") == 0) {
                                                    System.out.println("Describe your request: ");
                                                    String description = myObj.nextLine();

                                                    Request r = new Request(RequestTypes.DELETE_ACCOUNT, LocalDateTime.now(), " ", description, u.username, "ADMIN");
                                                    Request.RequestsHolder.addRequest(r);
                                                    IMDB.getInstance().requestList.add(r);
                                                } else if (requestType.compareToIgnoreCase("OTHERS") == 0) {
                                                    System.out.println("Describe your request: ");
                                                    String description = myObj.nextLine();

                                                    Request r = new Request(RequestTypes.OTHERS, LocalDateTime.now(), " ", description, u.username, "ADMIN");
                                                    Request.RequestsHolder.addRequest(r);
                                                    IMDB.getInstance().requestList.add(r);
                                                } else if (requestType.compareToIgnoreCase("MOVIE_ISSUE") == 0) {
                                                    System.out.println("Enter the production name: ");
                                                    String prodName = myObj.nextLine();

                                                    Production p = IMDB.getInstance().findProd(prodName);
                                                    if (p != null) {
                                                        System.out.println("Describe your request: ");
                                                        String description = myObj.nextLine();

                                                        Request r = new Request(RequestTypes.MOVIE_ISSUE, LocalDateTime.now(), p.title, description, u.username, p.addedBy);

                                                        User uTo = findUser(p.addedBy);
                                                        if (uTo.username.compareTo(u.username) != 0) {
                                                            ((Staff<?>) uTo).requestList.add(r);
                                                            System.out.println("Request created successfully");
                                                            IMDB.getInstance().requestList.add(r);
                                                            r.Notify(0);
                                                        } else
                                                            System.out.println("You can't add a request for a production added by yourself!");
                                                    } else
                                                        System.out.println("There is no such production!");
                                                } else if (requestType.compareToIgnoreCase("ACTOR_ISSUE") == 0) {
                                                    System.out.println("Enter the actor name: ");
                                                    String actorName = myObj.nextLine();

                                                    Actor a = findActor(actorName);
                                                    if (a != null) {
                                                        System.out.println("Describe your request: ");
                                                        String description = myObj.nextLine();

                                                        Request r = new Request(RequestTypes.ACTOR_ISSUE, LocalDateTime.now(), a.name, description, u.username, a.addedBy);

                                                        User uTo = findUser(a.addedBy);
                                                        if (uTo.username.compareTo(u.username) != 0) {
                                                            ((Staff<?>) uTo).requestList.add(r);
                                                            IMDB.getInstance().requestList.add(r);
                                                            System.out.println("Request created successfully!");
                                                            r.Notify(0);
                                                        } else
                                                            System.out.println("You can't add a request for an actor added by yourself!");
                                                    } else
                                                        System.out.println("There is no such actor!");
                                                }
                                            }
                                            else if(cmd.compareToIgnoreCase("remove") == 0) {
                                                int i, contor = 0;
                                                for (i = 0; i < IMDB.getInstance().getRequestList().size(); i++)
                                                    if (IMDB.getInstance().getRequestList().get(i).getUsername().equals(u.username))
                                                        contor++;

                                                if (contor == 0)
                                                    System.out.println("You don't have any pending requests!");
                                                else {
                                                    System.out.println("Type the number of the request you want to remove: ");
                                                    for (i = 0; i < IMDB.getInstance().getRequestList().size(); i++)
                                                        if (IMDB.getInstance().getRequestList().get(i).getUsername().equals(u.username)) {
                                                            Request r = IMDB.getInstance().getRequestList().get(i);
                                                            System.out.println("Request nr. " + i + " RequestType: " + r.getRequestType() + " Description: " + r.getDescription());
                                                        }

                                                    String requestNo = myObj.nextLine();
                                                    int requestNr = Integer.parseInt(requestNo);
                                                    if (IMDB.getInstance().getRequestList().get(requestNr).getUsername().equals(u.username)) {
                                                        Request r = IMDB.getInstance().getRequestList().get(requestNr);
                                                        r.Notify(3);
                                                        IMDB.getInstance().getRequestList().remove(requestNr);
                                                        switch (r.getRequestType()) {
                                                            case ACTOR_ISSUE, MOVIE_ISSUE: {
                                                                User user = findUser(r.getTo());
                                                                if (user != null)
                                                                    ((Staff<?>) user).requestList.remove(r);
                                                                break;
                                                            }
                                                            case OTHERS, DELETE_ACCOUNT: {
                                                                Request.RequestsHolder.removeRequest(r);
                                                                break;
                                                            }
                                                            default:
                                                                break;
                                                        }
                                                        System.out.println("Your request has been deleted successfully!");
                                                    } else
                                                        System.out.println("The request number is wrong! Please try again!");

                                                }
                                            }
                                            else
                                                throw new InvalidCommandException();

                                        } else if (comanda == 8) {
                                            myObj.nextLine();
                                            System.out.println("Enter the type: Actor / Production");
                                            String type = myObj.nextLine();

                                            System.out.println("Do you want to add or remove?");
                                            String action = myObj.nextLine();

                                            if(action.compareToIgnoreCase("add") == 0) {
                                                if(type.compareToIgnoreCase("Actor") == 0) {
                                                    System.out.println("Enter the name of the new actor:");
                                                    String name = myObj.nextLine();

                                                    System.out.println("Enter his biography:");
                                                    String biography = myObj.nextLine();

                                                    System.out.println("How many performances does he have?");
                                                    int numPerformances = myObj.nextInt();
                                                    myObj.nextLine();
                                                    List<Map<String, String>> performances = new ArrayList<>();

                                                    for(int i = 0; i < numPerformances; i++) {
                                                        Map<String, String> performance = new HashMap<String, String>();
                                                        System.out.println("Enter the name of the performance:");
                                                        String perfName = myObj.nextLine();
                                                        Production p = findProd(perfName);

                                                        if(p != null) {
                                                            performance.put(p.title, p.type);
                                                            performances.add(performance);
                                                            p.actorList.add(name);
                                                        }
                                                        else
                                                            System.out.println("There is no such production! Please try again!");
                                                    }
                                                    Actor newActor = new Actor(name, performances, biography);
//                                                    IMDB.getInstance().actors.add(newActor);
//                                                    ((Contributor)u).contributions.add(newActor);
                                                    newActor.addedBy = u.username;
                                                    ((Contributor)u).addActorSystem(newActor);
                                                    System.out.println("Actor successfully added to system!");
                                                }
                                                else if(type.compareToIgnoreCase("Production") == 0) {
                                                    System.out.println("Enter the title of the production");
                                                    String title = myObj.nextLine();

                                                    System.out.println("Enter the type of the production");
                                                    String prodType = myObj.nextLine();

                                                    System.out.println("Enter the number of directors:");
                                                    int nrDirs = myObj.nextInt();
                                                    myObj.nextLine();
                                                    List<String> directors = new ArrayList<>();

                                                    for(int i = 0;  i < nrDirs; i++){
                                                        System.out.println("Enter the name of the director:");
                                                        String dirName = myObj.nextLine();
                                                        directors.add(dirName);
                                                    }

                                                    System.out.println("Enter the number of the actors:");
                                                    int nrActors = myObj.nextInt();
                                                    myObj.nextLine();
                                                    List<String> actors = new ArrayList<>();

                                                    for(int i = 0; i < nrActors; i++) {
                                                        System.out.println("Enter the name of the actor:");
                                                        String actorName = myObj.nextLine();
                                                        actors.add(actorName);

                                                        Actor a = findActor(actorName);
                                                        Map<String, String> performance = new HashMap<>();
                                                        performance.put(title, prodType);
                                                        a.performances.add(performance);
                                                    }

                                                    System.out.println("Enter the number of genres:");
                                                    int nrGenres = myObj.nextInt();
                                                    List<Genre> genres = new ArrayList<>();
                                                    myObj.nextLine();

                                                    for(int i = 0; i < nrGenres; i++) {
                                                        System.out.println("Enter the genre: " + Arrays.toString(Genre.values()));
                                                        String genre = myObj.nextLine();
                                                        genres.add(Genre.valueOf(genre));
                                                    }

                                                    System.out.println("Enter the plot of the production:");
                                                    String plot = myObj.nextLine();

                                                    if(prodType.compareToIgnoreCase("movie") == 0) {
                                                        System.out.println("Enter the duration of the movie:");
                                                        String duration = myObj.nextLine();

                                                        System.out.println("Enter the release year:");
                                                        int releaseYear = myObj.nextInt();
                                                        myObj.nextLine();

                                                        Movie newMovie = new Movie(title, prodType, directors, actors, genres, new ArrayList<Rating>(), plot, 0.0, duration, releaseYear);
//                                                        IMDB.getInstance().productions.add(newMovie);
//                                                        ((Contributor)u).contributions.add(newMovie);
                                                        newMovie.addedBy = u.username;
                                                        ((Contributor)u).addProductionSystem(newMovie);
                                                        newMovie.observers.add(u);
                                                        System.out.println("Movie successfully added to system!");
                                                    }

                                                    else if(prodType.compareToIgnoreCase("series") == 0) {
                                                        System.out.println("Enter the release year of the series: ");
                                                        int releaseYear = myObj.nextInt();

                                                        System.out.println("Enter the number of seasons:");
                                                        int numSeasons = myObj.nextInt();
                                                        myObj.nextLine();

                                                        Map<String, List<Episode>> seasons = new HashMap<>();
                                                        for(int i = 0; i < numSeasons; i++) {
                                                            System.out.println("Enter the name of the seasons nr " + i);
                                                            String seasonName = myObj.nextLine();

                                                            System.out.println("Enter the number of episodes for " + seasonName);
                                                            int numEpisodes = myObj.nextInt();
                                                            myObj.nextLine();
                                                            List<Episode> episodes = new ArrayList<>();

                                                            for(int j = 0; j < numEpisodes; j++){
                                                                System.out.println("Enter the name of the episode nr " + j);
                                                                String episodeName = myObj.nextLine();
                                                                System.out.println("Enter the duration of the episode nr " + j);
                                                                String duration = myObj.nextLine();

                                                                Episode episode = new Episode(episodeName, duration);
                                                                episodes.add(episode);
                                                            }

                                                            seasons.put(seasonName, episodes);
                                                        }

                                                        Series s = new Series(title, prodType, directors, actors, genres, new ArrayList<Rating>(), plot, 0.0, releaseYear, numSeasons, seasons);
                                                        s.addedBy = u.username;
//                                                        IMDB.getInstance().productions.add(s);
//                                                        ((Contributor)u).contributions.add(s);
                                                        s.addedBy = u.username;
                                                        ((Contributor)u).addProductionSystem(s);
                                                        s.observers.add(u);
                                                        System.out.println("Series successfully added to system!");
                                                    }

                                                }
                                            }
                                            else if(action.compareToIgnoreCase("remove") == 0) {
                                                if(type.compareToIgnoreCase("Actor") == 0) {
                                                    System.out.println("Enter the name of the actor you want to be removed:");
                                                    String actorName = myObj.nextLine();

                                                    Actor a = findActor(actorName);
                                                    if(((Contributor)u).contributions.contains(a) == true && a != null) {
//                                                        ((Contributor)u).contributions.remove(a);
//                                                        IMDB.getInstance().actors.remove(a);
                                                        ((Staff)u).removeActorSystem(actorName);
                                                        System.out.println("Actor successfully removed from system!");
                                                    }
                                                    else
                                                        System.out.println("There is no such actor in your contributions!");
                                                }
                                                else if(type.compareToIgnoreCase("Production") == 0) {
                                                    System.out.println("Enter the name of the production you want to be removed:");
                                                    String prodName = myObj.nextLine();

                                                    Production p = findProd(prodName);
                                                    if(((Contributor)u).contributions.contains(p) == true && p != null) {
//                                                        ((Contributor)u).contributions.remove(p);
//                                                        IMDB.getInstance().productions.remove(p);
                                                        ((Contributor)u).removeProductionSystem(prodName);
                                                        System.out.println("Production successfully removed from system!");
                                                    }
                                                    else
                                                        System.out.println("There is no such production in your contributions!");
                                                }
                                                else
                                                    throw new InvalidCommandException();
                                            }
                                            else
                                                throw new InvalidCommandException();
                                        } else if (comanda == 9) {
                                            System.out.println("1) View requests");
                                            System.out.println("2) Solve a request");
                                            myObj.nextLine();

                                            int cmd = myObj.nextInt();
                                            if (cmd == 1) {
                                                if (((Staff<?>) u).requestList.isEmpty() == false) {
                                                    System.out.println("These are your pending requests: ");
                                                    for (Request r : ((Staff<?>) u).requestList)
                                                        System.out.println(r.getRequestType() + "\n" + r.getDescription() + "\n");
                                                }
                                                else
                                                    System.out.println("You don't have any pending requests!");
                                            }

                                            else if(cmd == 2) {
                                                if(!((Staff<?>) u).requestList.isEmpty()) {
                                                    System.out.println("These are your pending requests: ");
                                                    for(int i = 0; i < ((Staff<?>) u).requestList.size(); i++) {
                                                        Request r = ((Staff<?>) u).requestList.get(i);
                                                        System.out.println(i + ")  " + r.getRequestType() + " " + r.getDescription());
                                                    }

                                                    System.out.println("Enter the number of the request you want to solve: ");
                                                    int requestNr = myObj.nextInt();
                                                    Request r = ((Staff<?>) u).requestList.get(requestNr);
                                                    if(r != null) {
                                                        myObj.nextLine();
                                                        if(r.getRequestType().equals(RequestTypes.MOVIE_ISSUE)) {
                                                            System.out.println("What would you like to modify? ");
                                                            System.out.println("1) Title");
                                                            System.out.println("2) Plot");
                                                            System.out.println("3) Genres");
                                                            System.out.println("4) Duration (for movies)");
                                                            int CMD = myObj.nextInt();
                                                            myObj.nextLine();

                                                            if(CMD == 1) {
                                                                Production p = findProd(r.getSubject());
                                                                System.out.println("Enter the new title:");
                                                                String newTitle = myObj.nextLine();
                                                                p.title = newTitle;
                                                                System.out.println("Title successfully changed!");
                                                                r.Notify(1);
                                                                ((Contributor)u).removeRequest(r);
                                                            }
                                                            else if(CMD == 2) {
                                                                Production p = findProd(r.getSubject());
                                                                System.out.println("Enter the new plot:");
                                                                String newPlot = myObj.nextLine();
                                                                p.plot = newPlot;
                                                                System.out.println("Plot successfully changed!");
                                                                r.Notify(1);
                                                                ((Contributor)u).removeRequest(r);
                                                            }
                                                            else if(CMD == 3) {
                                                                Production p = findProd(r.getSubject());
                                                                System.out.println("Enter the genre you want to add: ");
                                                                String genre = myObj.nextLine();

                                                                if(p.genreList.contains(Genre.valueOf(genre)) == false) {
                                                                    p.genreList.add(Genre.valueOf(genre));
                                                                    r.Notify(1);
                                                                    ((Contributor) u).removeRequest(r);
                                                                    System.out.println("Genre successfully added!");
                                                                }
                                                                else {
                                                                    System.out.println("This genre is already in the list!");
                                                                    r.Notify(2);
                                                                    ((Contributor) u).removeRequest(r);
                                                                }

                                                            }
                                                            else if(CMD == 4) {
                                                                Production p = findProd(r.getSubject());
                                                                if(p instanceof Movie) {
                                                                    System.out.println("Enter the new duration of the movie:");
                                                                    String newDuration = myObj.nextLine();
                                                                    ((Movie) p).duration = newDuration;
                                                                    System.out.println("Duration updated successfully!");
                                                                    r.Notify(1);
                                                                    ((Contributor)u).removeRequest(r);
                                                                }
                                                                else {
                                                                    System.out.println("This production is not a movie!");
                                                                    r.Notify(2);
                                                                    ((Contributor) u).removeRequest(r);
                                                                }
                                                            }

                                                            else
                                                                throw new InvalidCommandException();

                                                        }
                                                        else if(r.getRequestType().equals(RequestTypes.ACTOR_ISSUE)) {
                                                            System.out.println("What would you like to modify?");
                                                            System.out.println("1) Actor name");
                                                            System.out.println("2) Biography");
                                                            System.out.println("3) Add a performance");

                                                            int CMD = myObj.nextInt();
                                                            myObj.nextLine();

                                                            if(CMD == 1) {
                                                                System.out.println("Enter the new name of the actor");
                                                                String newName = myObj.nextLine();

                                                                Actor a = findActor(r.getSubject());
                                                                if(a != null) {
                                                                    a.name = newName;
                                                                    System.out.println("Name successfully changed!");
                                                                    r.Notify(1);
                                                                    ((Contributor)u).removeRequest(r);
                                                                }
                                                                else {
                                                                    System.out.println("There is no such actor!");
                                                                    r.Notify(2);
                                                                    ((Contributor) u).removeRequest(r);
                                                                }
                                                            }
                                                            else if(CMD == 2) {
                                                                System.out.println("Enter the new biography of the actor");
                                                                String newBio = myObj.nextLine();

                                                                Actor a = findActor(r.getSubject());
                                                                if(a != null) {
                                                                    a.biography = newBio;
                                                                    System.out.println("Biography successfully changed!");
                                                                    r.Notify(1);
                                                                    ((Contributor)u).removeRequest(r);
                                                                }
                                                                else {
                                                                    System.out.println("There is no such actor!");
                                                                    r.Notify(2);
                                                                    ((Contributor) u).removeRequest(r);
                                                                }
                                                            }
                                                            else if(CMD == 3) {
                                                                Actor a = findActor(r.getSubject());
                                                                if(a != null) {
                                                                    System.out.println("Enter the title of the production in which he performed");
                                                                    String name = myObj.nextLine();

                                                                    Production p = findProd(name);
                                                                    if (p != null) {
                                                                        Map<String, String> newPerformance = new HashMap<String, String>();
                                                                        newPerformance.put(p.title, p.type);
                                                                        a.performances.add(newPerformance);
                                                                        if (p.actorList.contains(a.name) == false) {
                                                                            p.actorList.add(a.name);
                                                                            System.out.println("Performance successfully added!");
                                                                            r.Notify(1);
                                                                            ((Contributor) u).removeRequest(r);
                                                                        }
                                                                        else {
                                                                            System.out.println("Actor is already in the production's cast!");
                                                                            r.Notify(2);
                                                                            ((Contributor) u).removeRequest(r);
                                                                        }
                                                                    }
                                                                    else {
                                                                        System.out.println("There is no such production!");
                                                                        r.Notify(2);
                                                                        ((Contributor) u).removeRequest(r);
                                                                    }
                                                                }
                                                                else {
                                                                    System.out.println("There is no such actor!");
                                                                    r.Notify(2);
                                                                    ((Contributor) u).removeRequest(r);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                else
                                                    System.out.println("You don't have any pending requests!");
                                            }
                                            else
                                                throw new InvalidCommandException();

                                        } else if (comanda == 10) {
                                            System.out.println("What would you want to update? Actor / Production");
                                            myObj.nextLine();

                                            String command = myObj.nextLine();
                                            if(command.compareToIgnoreCase("actor") == 0) {
                                                System.out.println("Type the name of the actor you want to update:");
                                                String actorName = myObj.nextLine();

                                                Actor a = findActor(actorName);
                                                if(a != null && ((Contributor)u).contributions.contains(a))
                                                    ((Contributor)u).updateActor(a, myObj);
                                                else
                                                    System.out.println("There is no such actor in your contributions list");
                                            }
                                            else if(command.compareToIgnoreCase("production") == 0) {
                                                System.out.println("Type the title of the production you want to update:");
                                                String prodTitle = myObj.nextLine();

                                                Production p = findProd(prodTitle);
                                                if(p != null && ((Contributor) u).contributions.contains(p)) {
                                                    ((Contributor)u).updateProduction(p, myObj);
                                                }
                                                else
                                                    System.out.println("This production is not in your contributions list");
                                            }
                                            else
                                                throw new InvalidCommandException();
                                        } else if (comanda == 11) {
                                            control2 = 0;
                                            myObj.nextLine();
                                        } else if (comanda == 12) {
                                            control = 1;
                                            control2 = 0;
                                            System.out.println("Thanks for using our IMDB!");
                                        }
                                    }
                                    break;
                                }
                                case Admin: {
                                    int control2 = 1;
                                    while(control2 == 1) {
                                        System.out.println("1) View productions details");
                                        System.out.println("2) View actors details");
                                        System.out.println("3) View notifications");
                                        System.out.println("4) Search for actor/movie/series");
                                        System.out.println("5) Add/Delete to/from favorites");
                                        System.out.println("6) Show favorites");
                                        System.out.println("7) Add/Delete production/actor from system");
                                        System.out.println("8) View and solve requests");
                                        System.out.println("9) Update actor/production");
                                        System.out.println("10) Add/Delete user from system");
                                        System.out.println("11) Log out");
                                        System.out.println("12) Exit the application");

                                        comanda = myObj.nextInt();
                                        if (comanda < 1 || comanda > 12)
                                            throw new InvalidCommandException();

                                        if (comanda == 1) {
                                            System.out.println("Filters: ");
                                            System.out.println("1) Filter by genre");
                                            System.out.println("2) Filter by number of ratings");
                                            System.out.println("3) None");

                                            int comanda2 = myObj.nextInt();
                                            if (comanda2 < 1 || comanda2 > 3)
                                                throw new InvalidCommandException();

                                            else if (comanda2 == 3)
                                                for (Production prod : IMDB.getInstance().productions)
                                                    prod.displayInfo();

                                            else if (comanda2 == 2) {
                                                IMDB.getInstance().productions.sort((production, t1) -> t1.ratingList.size() - production.ratingList.size());
                                                for (Production prod : IMDB.getInstance().productions)
                                                    prod.displayInfo();
                                            } else {
                                                System.out.println("Enter the genre you want to filter by: " + Arrays.toString(Genre.values()));
                                                myObj.nextLine();
                                                String genre = myObj.nextLine();
                                                Genre.valueOf(genre);
                                                for (Production prod : IMDB.getInstance().productions)
                                                    if (prod.genreList.contains(Genre.valueOf(genre)))
                                                        prod.displayInfo();
                                            }
                                        }

                                        else if(comanda == 2) {
                                            System.out.println("Do you want the actors to be sorted alphabetically? Yes / No");
                                            myObj.nextLine();
                                            String comanda2 = myObj.nextLine();
                                            if (comanda2.equals("No") || comanda2.equals("no"))
                                                for (Actor actor : IMDB.getInstance().actors)
                                                    actor.displayInfo();
                                            else if (comanda2.equals("Yes") || comanda2.equals("yes")) {
                                                IMDB.getInstance().actors.sort((actor, t1) -> actor.name.compareTo(t1.name));
                                                for (Actor actor : IMDB.getInstance().actors)
                                                    actor.displayInfo();
                                            } else
                                                throw new InvalidCommandException();
                                        }

                                        else if(comanda == 3) {
                                            if (u.notifications != null) {
                                                if(u.notifications.isEmpty() == false) {
                                                    System.out.println("Notifications: ");
                                                    for (Object obj : u.notifications)
                                                        System.out.println((String) obj);
                                                    System.out.println("Do you want to clear your notifications?");
                                                    myObj.nextLine();
                                                    String clearcmd = myObj.nextLine();
                                                    if (clearcmd.equalsIgnoreCase("yes")) {
                                                        u.notifications.clear();
                                                        System.out.println("You have reached the end of your notification list!");
                                                        System.out.println("You have successfully cleared your notifications!");
                                                    }
                                                    else
                                                        System.out.println("You have reached the end of your notification list!");
                                                }
                                                else
                                                    System.out.println("You don't have any notifications!");

                                            } else
                                                System.out.println("You don't have any notifications!");

                                        }
                                        else if(comanda == 4) {
                                            System.out.println("Enter the type : actor / movie / series");
                                            myObj.nextLine();
                                            String comanda2 = myObj.nextLine();
                                            if (!Objects.equals(comanda2, "actor") && !Objects.equals(comanda2, "movie") && !Objects.equals(comanda2, "series") && !Objects.equals(comanda2, "Actor") && !Objects.equals(comanda2, "Movie") && !Objects.equals(comanda2, "Series"))
                                                throw new InvalidCommandException();

                                            System.out.println("Enter the name of the " + comanda2);
                                            String comanda3 = myObj.nextLine();

                                            if (comanda2.equals("actor") || comanda2.equals("Actor")) {
                                                Actor a = findActor(comanda3);
                                                if (a != null)
                                                    a.displayInfo();
                                                else
                                                    System.out.println("There is no such actor!");
                                            } else if (comanda2.equals("movie") || comanda2.equals("Movie")) {
                                                Production p = findProd(comanda3);
                                                if (p != null && p instanceof Movie)
                                                    p.displayInfo();
                                                else
                                                    System.out.println("There is no such movie!");
                                            } else if (comanda2.equals("series") || comanda2.equals("Series")) {
                                                Production p = findProd(comanda3);
                                                if (p != null && p instanceof Series)
                                                    p.displayInfo();
                                                else
                                                    System.out.println("There is no such series!");
                                            } else
                                                throw new InvalidCommandException();

                                        }
                                        else if(comanda == 5) {
                                            System.out.println("Enter the type: actor / movie / series");
                                            myObj.nextLine();
                                            String comanda2 = myObj.nextLine();
                                            System.out.println("Do you want to add to favorites or remove from favorites? Type add / remove");
                                            String cmd = myObj.nextLine();
                                            if (Objects.equals(cmd, "add") || Objects.equals(cmd, "Add")) {
                                                if (!Objects.equals(comanda2, "actor") && !Objects.equals(comanda2, "movie") && !Objects.equals(comanda2, "series") && !Objects.equals(comanda2, "Actor") && !Objects.equals(comanda2, "Movie") && !Objects.equals(comanda2, "Series"))
                                                    throw new InvalidCommandException();

                                                System.out.println("Enter the name of the " + comanda2);
                                                String comanda3 = myObj.nextLine();
                                                if (comanda2.equals("actor") || comanda2.equals("Actor")) {
                                                    Actor a = findActor(comanda3);
                                                    if (a != null && !u.favorites.contains(a)) {
                                                        //u.favorites.add(a);
                                                        u.addFavorite(a);
                                                        System.out.println("Actor successfully added to favorites!");
                                                    } else if (a == null)
                                                        System.out.println("Actor " + comanda3 + " couldn't be found!");
                                                    else
                                                        System.out.println("Actor " + comanda3 + " is already your favorite!");

                                                } else if (comanda2.equals("series") || comanda2.equals("Series")) {
                                                    Production p = findProd(comanda3);
                                                    if (p != null && !u.favorites.contains(p) && p instanceof Series) {
                                                        //u.favorites.add(p);
                                                        u.addFavorite(p);
                                                        System.out.println("Series successfully added to favorites!");
                                                    } else if (p == null)
                                                        System.out.println("Series " + comanda3 + " couldn't be found!");
                                                    else if (p instanceof Series == false)
                                                        System.out.println(("There is no such series!"));
                                                    else
                                                        System.out.println("Series " + comanda3 + " is already your favorite!");

                                                } else {
                                                    Production p = findProd(comanda3);
                                                    if (p != null && !u.favorites.contains(p) && p instanceof Movie) {
                                                        //u.favorites.add(p);
                                                        u.addFavorite(p);
                                                        System.out.println("Movie successfully added to favorites!");
                                                    } else if (p == null)
                                                        System.out.println("Movie " + comanda3 + " couldn't be found!");
                                                    else if (p instanceof Series == false)
                                                        System.out.println(("There is no such movie!"));
                                                    else
                                                        System.out.println("Movie " + comanda3 + " is already your favorite!");
                                                }
                                            } else if (Objects.equals(cmd, "remove") || Objects.equals(cmd, "Remove")) {
                                                if (!Objects.equals(comanda2, "actor") && !Objects.equals(comanda2, "movie") && !Objects.equals(comanda2, "series") && !Objects.equals(comanda2, "Actor") && !Objects.equals(comanda2, "Movie") && !Objects.equals(comanda2, "Series"))
                                                    throw new InvalidCommandException();

                                                System.out.println("Enter the name of the " + comanda2);
                                                String comanda3 = myObj.nextLine();
                                                if (comanda2.equals("actor") || comanda2.equals("Actor")) {
                                                    Actor a = findActor(comanda3);
                                                    if (a != null && u.favorites.contains(a)) {
                                                        u.deleteFavorite(a);
                                                        System.out.println("Actor successfully removed from favorites!");
                                                    } else if (a == null)
                                                        System.out.println("There is no such actor!");
                                                    else if (!u.favorites.contains(a))
                                                        System.out.println("Actor " + a.name + " is not in your favorites list!");
                                                } else if (comanda2.equals("movie") || comanda2.equals("Movie")) {
                                                    Production p = findProd(comanda3);
                                                    if (p != null && u.favorites.contains(p) && p instanceof Movie) {
                                                        u.deleteFavorite(p);
                                                        System.out.println("Movie successfully removed from favorites");
                                                    } else if (p == null)
                                                        System.out.println("Movie " + p.title + " couldn't be found!");
                                                    else if (p instanceof Movie == false)
                                                        System.out.println("There is no such movie!");
                                                    else
                                                        System.out.println("Movie " + p.title + " is not in your favorites list!");

                                                } else if (comanda2.equals("series") || comanda2.equals("Series")) {
                                                    Production p = findProd(comanda3);
                                                    if (p != null && u.favorites.contains(p) && p instanceof Movie) {
                                                        u.deleteFavorite(p);
                                                        System.out.println("Series successfully removed from favorites");
                                                    } else if (p == null)
                                                        System.out.println("Series " + p.title + " couldn't be found!");
                                                    else if (p instanceof Series == false)
                                                        System.out.println("There is no such series!");
                                                    else
                                                        System.out.println("Series " + p.title + " is not in your favorites list!");

                                                }


                                            } else
                                                throw new InvalidCommandException();
                                        }

                                        else if(comanda == 6) {
                                            if (!u.favorites.isEmpty()) {
                                                System.out.println("These are your favorites!");
                                                int i;
                                                for (Object obj : u.favorites) {
                                                    if (obj instanceof Production)
                                                        ((Production) obj).displayInfo();
                                                    else if (obj instanceof Actor)
                                                        ((Actor) obj).displayInfo();
                                                }
                                            } else
                                                System.out.println("You don't have any favorites yet!");
                                        }

                                        else if(comanda == 7) {
                                            myObj.nextLine();
                                            System.out.println("Enter the type: Actor / Production");
                                            String type = myObj.nextLine();

                                            System.out.println("Do you want to add or remove?");
                                            String action = myObj.nextLine();

                                            if(action.compareToIgnoreCase("add") == 0) {
                                                if(type.compareToIgnoreCase("Actor") == 0) {
                                                    System.out.println("Enter the name of the new actor:");
                                                    String name = myObj.nextLine();

                                                    System.out.println("Enter his biography:");
                                                    String biography = myObj.nextLine();

                                                    System.out.println("How many performances does he have?");
                                                    int numPerformances = myObj.nextInt();
                                                    myObj.nextLine();
                                                    List<Map<String, String>> performances = new ArrayList<>();

                                                    for(int i = 0; i < numPerformances; i++) {
                                                        Map<String, String> performance = new HashMap<String, String>();
                                                        System.out.println("Enter the name of the performance:");
                                                        String perfName = myObj.nextLine();
                                                        Production p = findProd(perfName);

                                                        if(p != null) {
                                                            performance.put(p.title, p.type);
                                                            performances.add(performance);
                                                            p.actorList.add(name);
                                                        }
                                                        else
                                                            System.out.println("There is no such production! Please try again!");
                                                    }
                                                    Actor newActor = new Actor(name, performances, biography);
//                                                    IMDB.getInstance().actors.add(newActor);
//                                                    ((Contributor)u).contributions.add(newActor);
                                                    newActor.addedBy = u.username;
                                                    ((Admin)u).addActorSystem(newActor);
                                                    System.out.println("Actor successfully added to system!");
                                                }
                                                else if(type.compareToIgnoreCase("Production") == 0) {
                                                    System.out.println("Enter the title of the production");
                                                    String title = myObj.nextLine();

                                                    System.out.println("Enter the type of the production");
                                                    String prodType = myObj.nextLine();

                                                    System.out.println("Enter the number of directors:");
                                                    int nrDirs = myObj.nextInt();
                                                    myObj.nextLine();
                                                    List<String> directors = new ArrayList<>();

                                                    for(int i = 0;  i < nrDirs; i++){
                                                        System.out.println("Enter the name of the director:");
                                                        String dirName = myObj.nextLine();
                                                        directors.add(dirName);
                                                    }

                                                    System.out.println("Enter the number of the actors:");
                                                    int nrActors = myObj.nextInt();
                                                    myObj.nextLine();
                                                    List<String> actors = new ArrayList<>();

                                                    for(int i = 0; i < nrActors; i++) {
                                                        System.out.println("Enter the name of the actor:");
                                                        String actorName = myObj.nextLine();
                                                        actors.add(actorName);

                                                        Actor a = findActor(actorName);
                                                        Map<String, String> performance = new HashMap<>();
                                                        performance.put(title, prodType);
                                                        a.performances.add(performance);
                                                    }

                                                    System.out.println("Enter the number of genres:");
                                                    int nrGenres = myObj.nextInt();
                                                    List<Genre> genres = new ArrayList<>();
                                                    myObj.nextLine();

                                                    for(int i = 0; i < nrGenres; i++) {
                                                        System.out.println("Enter the genre: " + Arrays.toString(Genre.values()));
                                                        String genre = myObj.nextLine();
                                                        genres.add(Genre.valueOf(genre));
                                                    }

                                                    System.out.println("Enter the plot of the production:");
                                                    String plot = myObj.nextLine();

                                                    if(prodType.compareToIgnoreCase("movie") == 0) {
                                                        System.out.println("Enter the duration of the movie:");
                                                        String duration = myObj.nextLine();

                                                        System.out.println("Enter the release year:");
                                                        int releaseYear = myObj.nextInt();
                                                        myObj.nextLine();

                                                        Movie newMovie = new Movie(title, prodType, directors, actors, genres, new ArrayList<Rating>(), plot, 0.0, duration, releaseYear);
//                                                        IMDB.getInstance().productions.add(newMovie);
//                                                        ((Contributor)u).contributions.add(newMovie);
                                                        newMovie.addedBy = u.username;
                                                        ((Admin)u).addProductionSystem(newMovie);
                                                        System.out.println("Movie successfully added to system!");
                                                    }

                                                    else if(prodType.compareToIgnoreCase("series") == 0) {
                                                        System.out.println("Enter the release year of the series: ");
                                                        int releaseYear = myObj.nextInt();

                                                        System.out.println("Enter the number of seasons:");
                                                        int numSeasons = myObj.nextInt();
                                                        myObj.nextLine();

                                                        Map<String, List<Episode>> seasons = new HashMap<>();
                                                        for(int i = 0; i < numSeasons; i++) {
                                                            System.out.println("Enter the name of the seasons nr " + i);
                                                            String seasonName = myObj.nextLine();

                                                            System.out.println("Enter the number of episodes for " + seasonName);
                                                            int numEpisodes = myObj.nextInt();
                                                            myObj.nextLine();
                                                            List<Episode> episodes = new ArrayList<>();

                                                            for(int j = 0; j < numEpisodes; j++){
                                                                System.out.println("Enter the name of the episode nr " + j);
                                                                String episodeName = myObj.nextLine();
                                                                System.out.println("Enter the duration of the episode nr " + j);
                                                                String duration = myObj.nextLine();

                                                                Episode episode = new Episode(episodeName, duration);
                                                                episodes.add(episode);
                                                            }

                                                            seasons.put(seasonName, episodes);
                                                        }

                                                        Series s = new Series(title, prodType, directors, actors, genres, new ArrayList<Rating>(), plot, 0.0, releaseYear, numSeasons, seasons);
//                                                        IMDB.getInstance().productions.add(s);
//                                                        ((Contributor)u).contributions.add(s);
                                                        s.addedBy = u.username;
                                                        ((Admin)u).addProductionSystem(s);
                                                        System.out.println("Series successfully added to system!");
                                                    }

                                                }
                                            }
                                            else if(action.compareToIgnoreCase("remove") == 0) {
                                                if(type.compareToIgnoreCase("Actor") == 0) {
                                                    System.out.println("Enter the name of the actor you want to be removed:");
                                                    String actorName = myObj.nextLine();

                                                    Actor a = findActor(actorName);
                                                    int ok = 0;
                                                    if(((Admin)u).contributions.contains(a) == true && a != null) {
                                                        ((Admin)u).removeActorSystem(actorName);
                                                        ok = 1;
                                                    }
                                                    if(Admin.adminActors.contains(a) == true && a != null) {
                                                        Admin.adminActors.remove(a);
                                                        ok = 1;
                                                    }

                                                    if(ok == 1)
                                                        System.out.println("Actor successfully removed from system!");
                                                    else
                                                        System.out.println("There is no such actor in your contributions / admin list!");
                                                }
                                                else if(type.compareToIgnoreCase("Production") == 0) {
                                                    System.out.println("Enter the name of the production you want to be removed:");
                                                    String prodName = myObj.nextLine();
                                                    Production p = findProd(prodName);

                                                    int ok = 0;
                                                    if(((Admin)u).contributions.contains(p) == true && p != null) {
                                                        ((Admin)u).removeProductionSystem(prodName);
                                                        ok = 1;
                                                    }
                                                    if(Admin.adminProductions.contains(p) == true && p != null) {
                                                        Admin.adminProductions.remove(p);
                                                        ok = 1;
                                                    }

                                                    if(ok == 1)
                                                        System.out.println("Production successfully removed from system!");
                                                    else
                                                        System.out.println("There is no such production in your contributions / admin list!");
                                                }
                                            }
                                        }
                                        else if(comanda == 8) {
                                            System.out.println("1) View requests");
                                            System.out.println("2) Solve a request");
                                            myObj.nextLine();

                                            int cmd = myObj.nextInt();
                                            myObj.nextLine();
                                            if(cmd == 1) {
                                                int ok = 0;
                                                if (((Staff<?>) u).requestList.isEmpty() == false) {
                                                    ok = 1;
                                                    System.out.println("These are your pending requests: ");
                                                    for (Request r : ((Staff<?>) u).requestList)
                                                        System.out.println(r.getRequestType() + "\n" + r.getDescription() + "\n");
                                                }
                                                if(Request.RequestsHolder.requestList.isEmpty() == false) {
                                                    ok = 1;
                                                    for(Request r : Request.RequestsHolder.requestList)
                                                        System.out.println(r.getRequestType() + "\n" + r.getDescription() + "\n");
                                                }
                                                if(ok == 0)
                                                    System.out.println("You don't have any pending requests!");
                                            }
                                            else if(cmd == 2) {
                                                System.out.println("Do you want to solve a request from your request list or from the admin request list?");
                                                System.out.println("1) My request list");
                                                System.out.println("2) Admin request list");

                                                int cmd2 = myObj.nextInt();
                                                myObj.nextLine();

                                                if(cmd2 == 1) {
                                                    System.out.println("These are your pending requests: ");
                                                    if(!((Staff<?>) u).requestList.isEmpty()) {
                                                        for(int i = 0; i < ((Staff<?>) u).requestList.size(); i++) {
                                                            Request r = ((Staff<?>) u).requestList.get(i);
                                                            System.out.println(i + ")  " + r.getRequestType() + " " + r.getDescription());
                                                        }

                                                        System.out.println("Enter the number of the request you want to solve: ");
                                                        int requestNr = myObj.nextInt();
                                                        myObj.nextLine();
                                                        Request r = ((Staff<?>) u).requestList.get(requestNr);
                                                        if(r != null) {
                                                            if(r.getRequestType().equals(RequestTypes.MOVIE_ISSUE)) {
                                                                System.out.println("What would you like to modify? ");
                                                                System.out.println("1) Title");
                                                                System.out.println("2) Plot");
                                                                System.out.println("3) Genres");
                                                                System.out.println("4) Duration (for movies)");
                                                                int CMD = myObj.nextInt();
                                                                myObj.nextLine();

                                                                if(CMD == 1) {
                                                                    Production p = findProd(r.getSubject());
                                                                    System.out.println("Enter the new title:");
                                                                    String newTitle = myObj.nextLine();
                                                                    p.title = newTitle;
                                                                    System.out.println("Title successfully changed!");
                                                                    r.Notify(1);
                                                                    ((Admin)u).removeRequest(r);
                                                                }
                                                                else if(CMD == 2) {
                                                                    Production p = findProd(r.getSubject());
                                                                    System.out.println("Enter the new plot:");
                                                                    String newPlot = myObj.nextLine();
                                                                    p.plot = newPlot;
                                                                    System.out.println("Plot successfully changed!");
                                                                    r.Notify(1);
                                                                    ((Admin)u).removeRequest(r);
                                                                }
                                                                else if(CMD == 3) {
                                                                    Production p = findProd(r.getSubject());
                                                                    System.out.println("Enter the genre you want to add: ");
                                                                    String genre = myObj.nextLine();

                                                                    if(p.genreList.contains(Genre.valueOf(genre)) == false) {
                                                                        p.genreList.add(Genre.valueOf(genre));
                                                                        System.out.println("Genre successfully added!");
                                                                        r.Notify(1);
                                                                        ((Admin) u).removeRequest(r);
                                                                    }
                                                                    else {
                                                                        System.out.println("This genre is already in the list!");
                                                                        r.Notify(2);
                                                                        ((Admin) u).removeRequest(r);
                                                                    }
                                                                }
                                                                else if(CMD == 4) {
                                                                    Production p = findProd(r.getSubject());
                                                                    if(p instanceof Movie) {
                                                                        System.out.println("Enter the new duration of the movie:");
                                                                        String newDuration = myObj.nextLine();
                                                                        ((Movie) p).duration = newDuration;
                                                                        System.out.println("Duration updated successfully!");
                                                                        r.Notify(1);
                                                                        ((Admin) u).removeRequest(r);
                                                                    }
                                                                    else {
                                                                        System.out.println("This production is not a movie!");
                                                                        r.Notify(2);
                                                                        ((Admin) u).removeRequest(r);
                                                                    }
                                                                }

                                                                else
                                                                    throw new InvalidCommandException();

                                                            }
                                                            else if(r.getRequestType().equals(RequestTypes.ACTOR_ISSUE)) {
                                                                System.out.println("What would you like to modify?");
                                                                System.out.println("1) Actor name");
                                                                System.out.println("2) Biography");
                                                                System.out.println("3) Add a performance");

                                                                int CMD = myObj.nextInt();
                                                                myObj.nextLine();

                                                                if(CMD == 1) {
                                                                    System.out.println("Enter the new name of the actor");
                                                                    String newName = myObj.nextLine();

                                                                    Actor a = findActor(r.getSubject());
                                                                    if(a != null) {
                                                                        a.name = newName;
                                                                        System.out.println("Name successfully changed!");
                                                                        r.Notify(1);
                                                                        ((Admin)u).removeRequest(r);
                                                                    }
                                                                    else {
                                                                        System.out.println("There is no such actor!");
                                                                        r.Notify(2);
                                                                        ((Admin) u).removeRequest(r);
                                                                    }
                                                                }
                                                                else if(CMD == 2) {
                                                                    System.out.println("Enter the new biography of the actor");
                                                                    String newBio = myObj.nextLine();

                                                                    Actor a = findActor(r.getSubject());
                                                                    if(a != null) {
                                                                        a.biography = newBio;
                                                                        System.out.println("Biography successfully changed!");
                                                                        r.Notify(1);
                                                                        ((Admin)u).removeRequest(r);
                                                                    }
                                                                    else {
                                                                        System.out.println("There is no such actor!");
                                                                        r.Notify(2);
                                                                        ((Admin) u).removeRequest(r);
                                                                    }
                                                                }
                                                                else if(CMD == 3) {
                                                                    Actor a = findActor(r.getSubject());
                                                                    if(a != null) {
                                                                        System.out.println("Enter the title of the production in which he performed");
                                                                        String name = myObj.nextLine();

                                                                        Production p = findProd(name);
                                                                        if (p != null) {
                                                                            Map<String, String> newPerformance = new HashMap<String, String>();
                                                                            newPerformance.put(p.title, p.type);
                                                                            a.performances.add(newPerformance);
                                                                            if (p.actorList.contains(a.name) == false) {
                                                                                p.actorList.add(a.name);
                                                                                System.out.println("Actor added successfully!");
                                                                                r.Notify(1);
                                                                                ((Admin) u).removeRequest(r);
                                                                            }
                                                                            else {
                                                                                System.out.println("Actor is already in the production's cast!");
                                                                                r.Notify(2);
                                                                                ((Admin) u).removeRequest(r);
                                                                            }
                                                                        }
                                                                        else {
                                                                            System.out.println("There is no such production!");
                                                                            r.Notify(2);
                                                                            ((Admin) u).removeRequest(r);
                                                                        }
                                                                    }
                                                                    else {
                                                                        System.out.println("There is no such actor!");
                                                                        r.Notify(2);
                                                                        ((Admin) u).removeRequest(r);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        else
                                                            System.out.println("The request number is not valid! Please try again!");

                                                    }
                                                        else
                                                            System.out.println("You don't have any pending requests!");
                                                }
                                                else if(cmd2 == 2) {
                                                    int i = 0;
                                                    if(Request.RequestsHolder.requestList.isEmpty() == false) {
                                                        System.out.println("These are the admin pending requests:");
                                                        for(i = 0; i < Request.RequestsHolder.requestList.size(); i++) {
                                                            Request r = Request.RequestsHolder.requestList.get(i);
                                                            System.out.println(i + ")  " + r.getRequestType() + " " + r.getDescription());
                                                        }

                                                        System.out.println("Enter the number of the request you want to solve: ");
                                                        int requestNr = myObj.nextInt();
                                                        myObj.nextLine();
                                                        if(requestNr > Request.RequestsHolder.requestList.size() - 1)
                                                            throw new InvalidCommandException();

                                                        Request r = Request.RequestsHolder.requestList.get(requestNr);
                                                        if(r != null) {
                                                            if(r.getRequestType().equals(RequestTypes.MOVIE_ISSUE)) {
                                                                System.out.println("What would you like to modify? ");
                                                                System.out.println("1) Title");
                                                                System.out.println("2) Plot");
                                                                System.out.println("3) Genres");
                                                                System.out.println("4) Duration (for movies)");
                                                                int CMD = myObj.nextInt();
                                                                myObj.nextLine();

                                                                if(CMD == 1) {
                                                                    Production p = findProd(r.getSubject());
                                                                    String newTitle = myObj.nextLine();
                                                                    p.title = newTitle;
                                                                    System.out.println("Title successfully changed!");
                                                                    r.Notify(1);
                                                                    ((Admin)u).removeRequestRequestsHolder(r);
                                                                }
                                                                else if(CMD == 2) {
                                                                    Production p = findProd(r.getSubject());
                                                                    String newPlot = myObj.nextLine();
                                                                    p.plot = newPlot;
                                                                    System.out.println("Plot successfully changed!");
                                                                    r.Notify(1);
                                                                    ((Admin)u).removeRequestRequestsHolder(r);
                                                                }
                                                                else if(CMD == 3) {
                                                                    Production p = findProd(r.getSubject());
                                                                    System.out.println("Enter the genre you want to add: ");
                                                                    String genre = myObj.nextLine();

                                                                    if(p.genreList.contains(Genre.valueOf(genre)) == false) {
                                                                        p.genreList.add(Genre.valueOf(genre));
                                                                        System.out.println("Genre successfully added!");
                                                                        r.Notify(1);
                                                                        ((Admin) u).removeRequestRequestsHolder(r);
                                                                    }
                                                                    else {
                                                                        System.out.println("This genre is already in the list!");
                                                                        r.Notify(2);
                                                                        ((Admin) u).removeRequestRequestsHolder(r);
                                                                    }
                                                                }
                                                                else if(CMD == 4) {
                                                                    Production p = findProd(r.getSubject());
                                                                    if(p instanceof Movie) {
                                                                        System.out.println("Enter the new duration of the movie:");
                                                                        String newDuration = myObj.nextLine();
                                                                        ((Movie) p).duration = newDuration;
                                                                        System.out.println("Duration updated successfully!");
                                                                        r.Notify(1);
                                                                        ((Admin)u).removeRequestRequestsHolder(r);
                                                                    }
                                                                    else {
                                                                        System.out.println("This production is not a movie!");
                                                                        r.Notify(2);
                                                                        ((Admin) u).removeRequestRequestsHolder(r);
                                                                    }
                                                                }

                                                                else
                                                                    throw new InvalidCommandException();
                                                            }
                                                            else if(r.getRequestType().equals(RequestTypes.ACTOR_ISSUE)) {
                                                                System.out.println("What would you like to modify?");
                                                                System.out.println("1) Actor name");
                                                                System.out.println("2) Biography");
                                                                System.out.println("3) Add a performance");
                                                                int CMD = myObj.nextInt();
                                                                myObj.nextLine();

                                                                if(CMD == 1) {
                                                                    System.out.println("Enter the new name of the actor");
                                                                    String newName = myObj.nextLine();

                                                                    Actor a = findActor(r.getSubject());
                                                                    if(a != null) {
                                                                        a.name = newName;
                                                                        System.out.println("Name successfully changed!");
                                                                        r.Notify(1);
                                                                        ((Admin)u).removeRequestRequestsHolder(r);
                                                                    }
                                                                    else {
                                                                        System.out.println("There is no such actor!");
                                                                        r.Notify(2);
                                                                        ((Admin) u).removeRequestRequestsHolder(r);
                                                                    }
                                                                }
                                                                else if(CMD == 2) {
                                                                    System.out.println("Enter the new biography of the actor");
                                                                    String newBio = myObj.nextLine();

                                                                    Actor a = findActor(r.getSubject());
                                                                    if(a != null) {
                                                                        a.biography = newBio;
                                                                        System.out.println("Biography successfully changed!");
                                                                        r.Notify(1);
                                                                        ((Admin)u).removeRequestRequestsHolder(r);
                                                                    }
                                                                    else {
                                                                        System.out.println("There is no such actor!");
                                                                        r.Notify(2);
                                                                        ((Admin) u).removeRequestRequestsHolder(r);
                                                                    }
                                                                }
                                                                else if(CMD == 3) {
                                                                    Actor a = findActor(r.getSubject());
                                                                    if(a != null) {
                                                                        System.out.println("Enter the title of the production in which he performed");
                                                                        String name = myObj.nextLine();

                                                                        Production p = findProd(name);
                                                                        if (p != null) {
                                                                            Map<String, String> newPerformance = new HashMap<String, String>();
                                                                            newPerformance.put(p.title, p.type);
                                                                            a.performances.add(newPerformance);
                                                                            if (p.actorList.contains(a.name) == false) {
                                                                                p.actorList.add(a.name);
                                                                                System.out.println("Actor added successfully!");
                                                                                r.Notify(1);
                                                                                ((Admin) u).removeRequestRequestsHolder(r);
                                                                            }
                                                                            else {
                                                                                System.out.println("Actor is already in the production's cast!");
                                                                                r.Notify(2);
                                                                                ((Admin) u).removeRequestRequestsHolder(r);
                                                                            }
                                                                        }
                                                                        else {
                                                                            System.out.println("There is no such production!");
                                                                            r.Notify(2);
                                                                            ((Admin) u).removeRequestRequestsHolder(r);
                                                                        }
                                                                    }
                                                                    else {
                                                                        System.out.println("There is no such actor!");
                                                                        r.Notify(2);
                                                                        ((Admin) u).removeRequestRequestsHolder(r);
                                                                    }
                                                                }
                                                            }
                                                            else if(r.getRequestType().equals(RequestTypes.DELETE_ACCOUNT)) {
                                                                User user = findUser(r.getUsername());
                                                                Admin.deleteUser(user);
                                                                System.out.println("User successfully deleted!");
                                                            }
                                                            else if(r.getRequestType().equals(RequestTypes.OTHERS)){
                                                                System.out.println(r.getDescription());
                                                                System.out.println("Have you solved the request? Yes / No");
                                                                String answer = myObj.nextLine();
                                                                if(answer.compareToIgnoreCase("yes") == 0) {
                                                                    r.Notify(1);
                                                                    ((Admin) u).removeRequestRequestsHolder(r);
                                                                }
                                                                else
                                                                    System.out.println("The request will be kept in the list!");
                                                            }
                                                        }
                                                        else
                                                            System.out.println("The request number is not valid! Please try again!");

                                                    }
                                                    else
                                                        System.out.println("There are no pending admin requests!");
                                                }
                                            }
                                        }
                                        else if(comanda == 9) {
                                            System.out.println("What would you want to update? Actor / Production");
                                            myObj.nextLine();

                                            String command = myObj.nextLine();
                                            if(command.compareToIgnoreCase("actor") == 0) {
                                                System.out.println("Type the name of the actor you want to update:");
                                                String actorName = myObj.nextLine();

                                                Actor a = findActor(actorName);
                                                if(a != null)
                                                    if(((Admin)u).contributions.contains(a) || Admin.adminActors.contains(a))
                                                        ((Admin)u).updateActor(a, myObj);
                                                    else
                                                        System.out.println("There is no such actor in your contributions list");
                                                else
                                                    System.out.println("There is no such actor!");
                                            }
                                            else if(command.compareToIgnoreCase("production") == 0) {
                                                System.out.println("Type the title of the production you want to update:");
                                                String prodTitle = myObj.nextLine();

                                                Production p = findProd(prodTitle);
                                                if(p != null)
                                                    if(((Admin)u).contributions.contains(p) || Admin.adminProductions.contains(p))
                                                        ((Admin)u).updateProduction(p, myObj);
                                                    else
                                                        System.out.println("This production is not in your contributions list");
                                                else
                                                    System.out.println("There is no such production!");
                                            }
                                            else
                                                throw new InvalidCommandException();
                                        }

                                        else if(comanda == 10) {
                                            myObj.nextLine();
                                            System.out.println("Enter the action: add / delete");
                                            String action = myObj.nextLine();

                                            if(action.compareToIgnoreCase("delete") == 0) {
                                                System.out.println("Enter the username:");
                                                String username = myObj.nextLine();
                                                User user = findUser(username);
                                                if(user != null) {
                                                    Admin.deleteUser(user);
                                                    System.out.println("User successfully deleted!");
                                                }
                                                else
                                                    System.out.println("There is no such user!");
                                            }
                                            else if(action.compareToIgnoreCase("add") == 0) {
                                                Admin.addUser(myObj);
                                            }
                                        }
                                        else if(comanda == 11) {
                                            control2 = 0;
                                            myObj.nextLine();
                                        }
                                        else if(comanda == 12) {
                                            control = 1;
                                            control2 = 0;
                                            System.out.println("Thanks for using our IMDB!");
                                        }
                                        }
                                    }
                                }
                        } catch (InvalidCommandException e) {
                            System.out.println(e.toString());
                        }
                        catch (InformationIncompleteException e) {
                            System.out.println(e.toString());
                        }
                    }
                }

        }

    public static void main(String[] args) throws IOException, ParseException, InvalidCommandException, InformationIncompleteException {
        System.out.println(System.getProperty("user.dir"));
        IMDB.getInstance().run();
    }
    }