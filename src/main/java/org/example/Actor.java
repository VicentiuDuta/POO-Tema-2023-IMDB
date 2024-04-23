package org.example;


import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Actor implements Comparable{
    public String name;
    public List<Map<String, String>> performances;
    public String biography;
    public String addedBy;


    public Actor () {

    }

    public Actor(String name, List<Map<String, String>> productions, String biography) {
        this.name = name;
        this.performances = productions;
        this.biography = biography;
    }


    @Override
    public int compareTo(@NotNull Object o) {
        Actor x = (Actor) o;
        return this.name.compareTo(x.name);
    }

   public void displayInfo() {
        System.out.println("name : " + this.name);
        System.out.println("performances: ");
        for(int i = 0; i < this.performances.size(); i++) {
            Map<String, String> map = this.performances.get(i);

            for (Map.Entry<String, String> entry : map.entrySet()) {
                String name = entry.getKey();
                String type = entry.getValue();

                System.out.println("name:  " + name + " type: " + type);
            }
        }
        System.out.println("biography: " + this.biography + "\n");
   }


}
