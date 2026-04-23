package ua.hrinchenko.organism.animal.herbivore;

import ua.hrinchenko.config.Config;

public class Buffalo extends Herbivore {
    public Buffalo() {
        super(
                Config.WEIGHT.get("Buffalo"),
                Config.MAX_PER_CELL.get("Buffalo"),
                Config.SPEED.get("Buffalo"),
                Config.FOOD_NEEDED.get("Buffalo")
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
