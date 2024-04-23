package org.example;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.example.Request;

public class Admin<T extends Comparable<T>> extends Staff implements ExperienceStrategy, Observer {
    public static List<Production> adminProductions;
    public static List<Actor> adminActors;

    public Admin() {
        super();
    }

    public static void addUser(Scanner myObj) throws InformationIncompleteException{
        System.out.println("Enter the name of the new user:");
        String name = myObj.nextLine();
        if(name == null || name.equals("\n") || name.equals(""))
            throw new InformationIncompleteException();

        String username = name.toLowerCase().replace(" ", "_") + "_" + new Random().nextInt(1000);
        System.out.println("Enter the email of the new user:");
        String email = myObj.nextLine();
        if(email == null || email.equals("\n") || email.equals(""))
            throw new InformationIncompleteException();

        String password = name.replace('e', '3')
                              .replace('a', '@')
                              .replace('i', '!')
                              .replace('o', '0')
                              .replace('s', '$')
                              .replace(' ', '_');

        password += new Random().nextInt(1000);

        System.out.println("The password for the new user is: " + password);


        System.out.println("Enter the country of the new user:");
        String country = myObj.nextLine();

        System.out.println("Enter the birth date of the new user:");
        String birthDate = myObj.nextLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(birthDate, formatter);

        System.out.println("Enter the age of the new user:");
        Long age = myObj.nextLong();
        myObj.nextLine();

        System.out.println("Enter the gender of the new user:");
        String gender = myObj.nextLine();
        char genderChar;
        if(gender.equalsIgnoreCase("female"))
                genderChar = 'F';
        else if(gender.equalsIgnoreCase("male"))
                genderChar = 'M';
        else
                genderChar = 'N';

        Information.InformationBuilder builder = new Information.InformationBuilder();
        builder.Name(name)
                .country(country)
                .age(age)
                .Credentials(new Credentials(email, password))
                .gender(genderChar)
                .birthDate(date);

        Information info = builder.build();
        System.out.println("What type of account would you like to create? Regular/Contributor/Admin");
        String type = myObj.nextLine();

        if(type.equals("Regular")) {
            User newUser = UserFactory.factory(AccountType.Regular);
            newUser.informations = info;
            newUser.type = AccountType.Regular;
            newUser.username = username;
            newUser.experience = "0";
            newUser.favorites = new TreeSet<>((t, t1) -> {
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
            newUser.notifications = new ArrayList<>();
            IMDB.getInstance().getUserList().add(newUser);
            System.out.println("Regular account created successfully!");
        }

        else if(type.equals("Contributor")) {
            User newUser = UserFactory.factory(AccountType.Contributor);
            newUser.informations = info;
            newUser.type = AccountType.Contributor;
            newUser.username = username;
            newUser.experience = "0";
            newUser.favorites = new TreeSet<>((t, t1) -> {
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
            newUser.notifications = new ArrayList<>();
            ((Contributor)newUser).contributions = new TreeSet<>((t, t1) -> {
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
            ((Contributor)newUser).requestList = new ArrayList<>();
            IMDB.getInstance().getUserList().add(newUser);
            System.out.println("Contributor account created successfully!");
        }

        else if(type.equals("Admin")) {
            User newUser = UserFactory.factory(AccountType.Admin);
            newUser.informations = info;
            newUser.type = AccountType.Admin;
            newUser.username = username;
            newUser.experience = "0";
            newUser.favorites = new TreeSet<>((t, t1) -> {
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
            newUser.notifications = new ArrayList<>();
            ((Admin)newUser).contributions = new TreeSet<>((t, t1) -> {
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
            ((Admin)newUser).requestList = new ArrayList<>();
            IMDB.getInstance().getUserList().add(newUser);
            System.out.println("Admin account created successfully!");
        }
        else
            System.out.println("Invalid type of account!");

    }

    public static void deleteUser(User user) {
        //Daca este contributor, atunci toate productiile adaugate de el vor fi trecute in lista productiilor
        //aflate in responsabilitatea adminilor
        if(user.type.equals(AccountType.Contributor)) {
            Contributor<?> c = (Contributor<?>) user;
            int i;
            Production p;
            Actor a;
            for(Object element : c.contributions){
                if(element instanceof Production) {
                    p = (Production) element;
                    //c.contributions.remove(element);
                    Admin.adminProductions.add(p);
                }
                else if(element instanceof Actor) {
                    a = (Actor) element;
                    //c.contributions.remove(a);
                    Admin.adminActors.add(a);
                }
            }
        }
        //Toate cererile depuse de contributor vor fi adaugate in lista de cereri a adminilor:
        if(user instanceof Contributor<?>)
            for(Object r : ((Contributor<?>) user).requestList)
                Request.RequestsHolder.requestList.add((Request) r);

        //Stergem cererile din lista de cereri a adminilor
        int i;
        for(i = 0; i < Request.RequestsHolder.requestList.size(); i++)
            if(Request.RequestsHolder.requestList.get(i).getUsername().equals(user.username))
                Request.RequestsHolder.requestList.remove(i);
        //Stergem cererile din listele de cereri ale  membrilor stafului
        IMDB imdb = IMDB.getInstance();
        for(i = 0; i < imdb.getUserList().size(); i++) {
            if(imdb.getUserList().get(i) instanceof Staff) {
                Staff<?> aux = (Staff<?>) imdb.getUserList().get(i);
                int j;
                for(j = 0; j < aux.requestList.size(); j++)
                    if(aux.requestList.get(j).getUsername().equals(user.username))
                        aux.requestList.remove(j);
            }
        }
        //Stergem ratingurile utilizatorului
       int j;
        for(i = 0; i < imdb.getProductions().size(); i++) {
            Production x = imdb.getProductions().get(i);
            if(x.ratingList != null || !x.ratingList.isEmpty())
            for(j = 0; j < x.ratingList.size(); j++)
                if(x.ratingList.get(j).username.equals(user.username)) {
                    x.ratingList.remove(j);
                    x.updateRating();
                }
        }
        //stergem user-ul din lista de useri
        IMDB.getInstance().getUserList().remove(user);

    }

    @Override
    public void removeProductionSystem(String name) {
        Production p = IMDB.getInstance().findProd(name);
        if(p != null) {
            if(this.contributions.contains(p)) {
                this.contributions.remove(p);
                IMDB.getInstance().getProductions().remove(p);
            }
            Admin.adminProductions.remove(p);
        }
    }

    @Override
    public void addProductionSystem(Production p) {
        super.addProductionSystem(p);
    }

    @Override
    public void removeActorSystem(String name) {
        Actor a = IMDB.getInstance().findActor(name);
        if(a != null) {
            if(this.contributions.contains(a)) {
                this.contributions.remove(a);
                IMDB.getInstance().getActors().remove(a);
            }
            Admin.adminActors.remove(a);
        }
    }

    @Override
    public void updateProduction(Production p, Scanner myObj) {
            System.out.println("What would you like to modify?");
            if (p instanceof Movie) {
                System.out.println("1) Title");
                System.out.println("2) Plot");
                System.out.println("3) Add a genre");
                System.out.println("4) Remove a genre");
                System.out.println("5) Duration");
                System.out.println("6) Remove a rating");
                System.out.println("7) Release year");
                System.out.println("8) Add / remove a director");
                System.out.println("9) Add / remove an actor");
                int command = myObj.nextInt();
                myObj.nextLine();

                if (command == 1) {
                    System.out.println("Enter the new title:");
                    String newTitle = myObj.nextLine();
                    p.title = newTitle;
                    System.out.println("Title successfully changed!");
                } else if (command == 2) {
                    System.out.println("Enter the new plot:");
                    String newPlot = myObj.nextLine();
                    p.plot = newPlot;
                    System.out.println("Plot successfully changed!");
                } else if (command == 3) {
                    System.out.println("Enter the genre you want to add: " + Arrays.toString(Genre.values()));
                    String genre = myObj.nextLine();

                    if (!p.genreList.contains(Genre.valueOf(genre))) {
                        p.genreList.add(Genre.valueOf(genre));
                        System.out.println("Genre successfully added!");
                    } else
                        System.out.println("This genre is already in the list!");
                }
                else if (command == 4) {
                    System.out.println("Enter the genre you want to remove: " + Arrays.toString(Genre.values()));
                    String genre = myObj.nextLine();

                    if (p.genreList.contains(Genre.valueOf(genre))) {
                        p.genreList.remove(Genre.valueOf(genre));
                        System.out.println("Genre successfully removed!");
                    } else
                        System.out.println("This genre is not in the list!");
                }
                else if (command == 5) {
                    System.out.println("Enter the new duration of the movie:");
                    String newDuration = myObj.nextLine();
                    ((Movie) p).duration = newDuration;
                    System.out.println("Duration updated successfully!");
                }
                else if(command == 6) {
                    if(p.ratingList.isEmpty() == false) {
                        System.out.println("Enter the index of the rating you want to remove:");
                        for(int i = 0; i < p.ratingList.size(); i++)
                            System.out.println(i + ") " + p.ratingList.get(i).username + " " + p.ratingList.get(i).rating);
                        int index = myObj.nextInt();
                        myObj.nextLine();

                        p.ratingList.remove(index);
                        System.out.println("Rating successfully removed!");
                        p.updateRating();
                    }
                }
                else if(command == 7) {
                    System.out.println("Enter the new release year of the movie:");
                    int newReleaseYear = myObj.nextInt();
                    ((Movie) p).releaseYear = newReleaseYear;
                    System.out.println("Release year updated successfully!");
                }
                else if(command == 8) {
                    System.out.println("Enter the name of the director you want to add / remove:");
                    String newDirector = myObj.nextLine();

                    if(p.directors.contains(newDirector) == false) {
                        p.directors.add(newDirector);
                        System.out.println("Director successfully added!");
                    }
                    else {
                        p.directors.remove(newDirector);
                        System.out.println("Director successfully removed!");
                    }
                }

                else if(command == 9) {
                    System.out.println("Enter the name of the actor you want to add / remove:");
                    String newActor = myObj.nextLine();

                    if(p.actorList.contains(newActor) == false) {
                        p.actorList.add(newActor);
                        System.out.println("Actor successfully added!");
                    }
                    else {
                        p.actorList.remove(newActor);
                        System.out.println("Actor successfully removed!");
                    }
                }
                else
                    System.out.println("Invalid command! Please try again!");
            }
            else if(p instanceof Series) {
                System.out.println("1) Title");
                System.out.println("2) Plot");
                System.out.println("3) Add a genre");
                System.out.println("4) Remove a genre");
                System.out.println("5) Add/Update a season");
                System.out.println("6) Remove a rating");
                System.out.println("7) Add / remove a director");
                System.out.println("8) Add / remove an actor");
                int command = myObj.nextInt();
                myObj.nextLine();

                if(command == 1) {
                    System.out.println("Enter the new title:");
                    String newTitle = myObj.nextLine();
                    p.title = newTitle;
                    System.out.println("Title successfully changed!");
                }
                else if(command == 2) {
                    System.out.println("Enter the new plot:");
                    String newPlot = myObj.nextLine();
                    p.plot = newPlot;
                    System.out.println("Plot successfully changed!");
                }
                else if(command == 3) {
                    System.out.println("Enter the genre you want to add: " + Arrays.toString(Genre.values()));
                    String genre = myObj.nextLine();

                    if(!p.genreList.contains(Genre.valueOf(genre))) {
                        p.genreList.add(Genre.valueOf(genre));
                        System.out.println("Genre successfully added!");
                    }
                    else
                        System.out.println("This genre is already in the list!");
                }
                else if(command == 4) {
                    System.out.println("Enter the genre you want to remove: " + Arrays.toString(Genre.values()));
                    String genre = myObj.nextLine();

                    if(p.genreList.contains(Genre.valueOf(genre))) {
                        p.genreList.remove(Genre.valueOf(genre));
                        System.out.println("Genre successfully removed!");
                    }
                    else
                        System.out.println("This genre is not in the list!");

                }
                else if(command == 5) {
                    System.out.println("Choose an option");
                    System.out.println("1) Add a season");
                    System.out.println("2) Add an episode");
                    System.out.println("3) Change the duration of an episode");
                    int option = myObj.nextInt();
                    myObj.nextLine();

                    if(option == 1) {
                        System.out.println("Enter the season name:");
                        String seasonName = myObj.nextLine();

                        System.out.println("Enter the number of episodes:");
                        int nrEpisodes = myObj.nextInt();
                        myObj.nextLine();

                        List<Episode> episodes = new ArrayList<Episode>();
                        for(int i = 0; i < nrEpisodes; i++) {
                            System.out.println("Enter the episode name:");
                            String episodeName = myObj.nextLine();

                            System.out.println("Enter the episode duration:");
                            String episodeDuration = myObj.nextLine();

                            Episode e = new Episode(episodeName, episodeDuration);
                            episodes.add(e);
                        }

                        ((Series) p).seasons.put(seasonName, episodes);
                        ((Series) p).numSeasons++;
                        System.out.println("Season successfully added!");
                    }
                    else if(option == 2) {
                        System.out.println("Enter the season name:");
                        String seasonName = myObj.nextLine();

                        if(((Series) p).seasons.containsKey(seasonName)) {
                            System.out.println("Enter the episode name:");
                            String episodeName = myObj.nextLine();

                            System.out.println("Enter the episode duration:");
                            String episodeDuration = myObj.nextLine();

                            Episode e = new Episode(episodeName, episodeDuration);
                            ((Series) p).seasons.get(seasonName).add(e);
                            System.out.println("Episode successfully added!");
                        }
                        else
                            System.out.println("There is no such season!");
                    }
                    else if(option == 3) {
                        System.out.println("Enter the season name:");
                        String seasonName = myObj.nextLine();

                        if(((Series) p).seasons.containsKey(seasonName)) {
                            System.out.println("Enter the episode name:");
                            String episodeName = myObj.nextLine();

                            System.out.println("Enter the new duration:");
                            String newDuration = myObj.nextLine();

                            for(int i = 0; i < ((Series) p).seasons.get(seasonName).size(); i++) {
                                if(((Series) p).seasons.get(seasonName).get(i).episodeName.equals(episodeName)) {
                                    ((Series) p).seasons.get(seasonName).get(i).duration = newDuration;
                                    System.out.println("Duration successfully changed!");
                                    break;
                                }
                            }
                        }
                        else
                            System.out.println("There is no such season!");
                    }
                    else
                        System.out.println("Invalid command! Please try again!");
                }
                else if(command == 6) {
                    if(p.ratingList.isEmpty() == false) {
                        System.out.println("Enter the index of the rating you want to remove:");
                        for(int i = 0; i < p.ratingList.size(); i++)
                            System.out.println(i + ") " + p.ratingList.get(i).username + " " + p.ratingList.get(i).rating);
                        int index = myObj.nextInt();
                        myObj.nextLine();

                        p.ratingList.remove(index);
                        System.out.println("Rating successfully removed!");
                        p.updateRating();
                    }
                    else
                        System.out.println("There are no ratings!");
                }
                else if(command == 7) {
                    System.out.println("Enter the name of the director you want to add / remove:");
                    String newDirector = myObj.nextLine();

                    if(p.directors.contains(newDirector) == false) {
                        p.directors.add(newDirector);
                        System.out.println("Director successfully added!");
                    }
                    else {
                        p.directors.remove(newDirector);
                        System.out.println("Director successfully removed!");
                    }
                }

                else if(command == 8) {
                    System.out.println("Enter the name of the actor you want to add / remove:");
                    String newActor = myObj.nextLine();

                    if(p.actorList.contains(newActor) == false) {
                        p.actorList.add(newActor);
                        System.out.println("Actor successfully added!");
                    }
                    else {
                        p.actorList.remove(newActor);
                        System.out.println("Actor successfully removed!");
                    }
                }
                else
                    System.out.println("Invalid command! Please try again!");
            }

    }

    public int calculateExperience(User user, boolean isAdd) {
        if (user.experience != null) {
            if (isAdd)
                return Integer.parseInt(user.experience) + 5;
            else
                return Integer.parseInt(user.experience) - 5;
        }
        return 0;
    }

    public void removeRequest(Request r) {
        int newExperience = calculateExperience(IMDB.getInstance().findUser(r.getUsername()), true);
        IMDB.getInstance().findUser(r.getUsername()).experience = String.valueOf(newExperience);

        this.requestList.remove(r);
        IMDB.getInstance().getRequestList().remove(r);
    }

    public void removeRequestRequestsHolder (Request r) {
        int newExperience = calculateExperience(IMDB.getInstance().findUser(r.getUsername()), true);
        IMDB.getInstance().findUser(r.getUsername()).experience = String.valueOf(newExperience);

        Request.RequestsHolder.requestList.remove(r);
        IMDB.getInstance().getRequestList().remove(r);
    }

    @Override
    public void updateActor(Actor a, Scanner myObj) {
        System.out.println("What would you like to modify?");
        System.out.println("1) Actor name");
        System.out.println("2) Biography");
        System.out.println("3) Add a performance");
        System.out.println("4) Remove a performance");

        int CMD = myObj.nextInt();
        myObj.nextLine();

        if (CMD == 1) {
            System.out.println("Enter the new name of the actor");
            String newName = myObj.nextLine();

            if (a != null) {
                a.name = newName;
                System.out.println("Name successfully changed!");
            } else
                System.out.println("There is no such actor!");
        } else if (CMD == 2) {
            System.out.println("Enter the new biography of the actor");
            String newBio = myObj.nextLine();

            if (a != null) {
                a.biography = newBio;
                System.out.println("Biography successfully changed!");
            } else
                System.out.println("There is no such actor!");
        } else if (CMD == 3) {

            if (a != null) {
                System.out.println("Enter the title of the production in which he performed");
                String name = myObj.nextLine();

                Production p = IMDB.getInstance().findProd(name);
                if (p != null) {
                    Map<String, String> newPerformance = new HashMap<String, String>();
                    newPerformance.put(p.title, p.type);
                    a.performances.add(newPerformance);
                    if (p.actorList.contains(a.name) == false)
                        p.actorList.add(a.name);
                    else
                        System.out.println("Actor is already in the production's cast!");
                } else
                    System.out.println("There is no such production!");
            } else
                System.out.println("There is no such actor!");
        }
        else if(CMD == 4) {
            if(a != null) {
                System.out.println("Enter the title of the production in which he performed");
                String name = myObj.nextLine();

                Production p = IMDB.getInstance().findProd(name);
                if (p != null) {
                    Map<String, String> newPerformance = new HashMap<String, String>();
                    newPerformance.put(p.title, p.type);
                    a.performances.remove(newPerformance);
                    if (p.actorList.contains(a.name) == true) {
                        p.actorList.remove(a.name);
                        System.out.println("Actor successfully removed from the production's cast!");
                    }
                    else
                        System.out.println("Actor is not in the production's cast!");
                } else
                    System.out.println("There is no such production!");
            }
            else
                System.out.println("There is no such actor!");
        }
        else
            System.out.println("Invalid command! Please try again!");
        }


    @Override
    public void Update(String notification) {
        this.notifications.add(notification);
    }
}
