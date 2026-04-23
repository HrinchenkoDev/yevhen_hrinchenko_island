package ua.hrinchenko.organism.animal.herbivore;

import ua.hrinchenko.organism.animal.Animal;

public abstract class Herbivore extends Animal {
    public Herbivore(double weight, int maxPerCell, int speed, double foodNeeded) {
        super(weight, maxPerCell, speed, foodNeeded);
    }
}
