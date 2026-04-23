package ua.hrinchenko.organism.animal.herbivore;

import ua.hrinchenko.config.Config;

public class Caterpillar extends Herbivore {
    public Caterpillar() {
        super(
                Config.WEIGHT.get("Caterpillar"),
                Config.MAX_PER_CELL.get("Caterpillar"),
                Config.SPEED.get("Caterpillar"),
                Config.FOOD_NEEDED.get("Caterpillar")
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
