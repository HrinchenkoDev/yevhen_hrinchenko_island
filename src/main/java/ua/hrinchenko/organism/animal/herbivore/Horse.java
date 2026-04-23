package ua.hrinchenko.organism.animal.herbivore;

import ua.hrinchenko.config.Config;

public class Horse extends Herbivore {
    public Horse() {
        super(
                Config.WEIGHT.get("Horse"),
                Config.MAX_PER_CELL.get("Horse"),
                Config.SPEED.get("Horse"),
                Config.FOOD_NEEDED.get("Horse")
        );
    }

    @Override
    public void move() {

    }

    @Override
    public void eat() {

    }

    @Override
    public void multiply() {

    }
}