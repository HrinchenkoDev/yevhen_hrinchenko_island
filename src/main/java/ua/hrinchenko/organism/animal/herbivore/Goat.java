package ua.hrinchenko.organism.animal.herbivore;

import ua.hrinchenko.config.Config;

public class Goat extends Herbivore {
    public Goat() {
        super(
                Config.WEIGHT.get("Goat"),
                Config.MAX_PER_CELL.get("Goat"),
                Config.SPEED.get("Goat"),
                Config.FOOD_NEEDED.get("Goat")
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
