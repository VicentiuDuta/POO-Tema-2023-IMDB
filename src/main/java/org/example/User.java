package org.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class User<T extends Comparable<T>> implements Observer {
    public Information informations;
    public AccountType type;
    public String username;
    public String experience;

    public SortedSet<T> favorites;

    public List<String> notifications;

    public User() {
        this.favorites = new TreeSet<>(new Comparator<T>() {
            @Override
            public int compare(T t, T t1) {
                if(t instanceof Production && t1 instanceof Production)
                    return ((Production) t).title.compareTo(((Production) t1).title);
                else if(t instanceof Production && t1 instanceof Actor)
                    return ((Production) t).title.compareTo(((Actor) t1).name);
                else if(t instanceof Actor && t1 instanceof Production)
                    return ((Actor) t).name.compareTo(((Production) t1).title);
                else if(t instanceof Actor && t1 instanceof Actor)
                    return ((Actor) t).name.compareTo(((Actor) t1).name);
                return 0;
            }
        });
    }

    public User(Information informations, AccountType type, String username, String experience, SortedSet<T> favorites, List<String> notifications) {
        this.informations = informations;
        this.type = type;
        this.username = username;
        this.experience = experience;
        this.favorites = favorites;
        this.notifications = notifications;
    }

    public void Update(String notification) {
        this.notifications.add(notification);
    }

    public void addFavorite(T element) {
        this.favorites.add(element);
    }

    public void deleteFavorite(T element) {
        this.favorites.remove(element);
    }
    public static class Information {
        private final Credentials credentials;
        private final String name;
        private final String country;
        private final Long age;
        private final char gender;
        private final LocalDate birthDate;

        private Information(InformationBuilder informationBuilder) {
            this.credentials = informationBuilder.credentials;
            this.name = informationBuilder.name;
            this.country = informationBuilder.country;
            this.age = informationBuilder.age;
            this.gender = informationBuilder.gender;
            this.birthDate = informationBuilder.birthDate;
        }

        public Credentials getCredentials() {
            return credentials;
        }

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        public Long getAge() {
            return age;
        }

        public char getGender() {
            return gender;
        }



        public LocalDate getBirthDate() {
            return birthDate;
        }

        public static class InformationBuilder {
            private Credentials credentials;
            private String name;
            private String country;
            private Long age;
            private char gender;
            private LocalDate birthDate;

            public InformationBuilder Credentials(Credentials credentials) {
                this.credentials = credentials;
                return this;
            }

            public InformationBuilder Name(String name) {
                this.name = name;
                return this;
            }

            public InformationBuilder country(String country) {
                this.country = country;
                return this;
            }

            public InformationBuilder age(Long age) {
                this.age = age;
                return this;
            }

            public InformationBuilder gender(char gender) {
                this.gender = gender;
                return this;
            }

            public InformationBuilder birthDate(LocalDate birthDate) {
                this.birthDate = birthDate;
                return this;
            }

            public Information build() throws InformationIncompleteException {
                return new Information(this);
            }
        }
    }


}
