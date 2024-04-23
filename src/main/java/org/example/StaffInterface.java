package org.example;

import java.util.Scanner;

public interface StaffInterface {
    void addProductionSystem(Production p);
    void addActorSystem(Actor a);
    void removeProductionSystem(String name);
    void removeActorSystem(String name);
    void updateProduction(Production p, Scanner myObj);
    void updateActor(Actor a, Scanner myObj);
}
