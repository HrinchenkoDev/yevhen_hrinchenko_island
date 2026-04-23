package ua.hrinchenko.organism.animal.herbivore;

import ua.hrinchenko.config.Config;

public class Duck extends Herbivore {
    public Duck() {
        super(
                Config.WEIGHT.get("Duck"),
                Config.MAX_PER_CELL.get("Duck"),
                Config.SPEED.get("Duck"),
                Config.FOOD_NEEDED.get("Duck")
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
