package ua.hrinchenko.organism.animal.predator;

import ua.hrinchenko.organism.animal.Animal;

public abstract class Predator extends Animal {
    public Predator(double weight, int maxPerCell, int speed, double foodNeeded) {
        super(weight, maxPerCell, speed, foodNeeded);
    }
}
