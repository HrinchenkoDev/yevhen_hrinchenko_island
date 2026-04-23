package ua.hrinchenko.organism.animal.herbivore;

import ua.hrinchenko.config.Config;

public class Rabbit extends Herbivore {

    public Rabbit() {
        super(
                Config.WEIGHT.get("Rabbit"),
                Config.MAX_PER_CELL.get("Rabbit"),
                Config.SPEED.get("Rabbit"),
                Config.FOOD_NEEDED.get("Rabbit")
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
