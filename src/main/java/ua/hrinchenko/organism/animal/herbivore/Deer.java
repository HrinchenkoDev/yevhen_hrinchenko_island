package ua.hrinchenko.organism.animal.herbivore;

import ua.hrinchenko.config.Config;

public class Deer extends Herbivore {
    public Deer() {
        super(
                Config.WEIGHT.get("Deer"),
                Config.MAX_PER_CELL.get("Deer"),
                Config.SPEED.get("Deer"),
                Config.FOOD_NEEDED.get("Deer")
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
