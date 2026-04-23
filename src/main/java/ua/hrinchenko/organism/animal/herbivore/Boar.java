package ua.hrinchenko.organism.animal.herbivore;

import ua.hrinchenko.config.Config;

public class Boar extends Herbivore {
    public Boar() {
        super(
                Config.WEIGHT.get("Boar"),
                Config.MAX_PER_CELL.get("Boar"),
                Config.SPEED.get("Boar"),
                Config.FOOD_NEEDED.get("Boar")
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
