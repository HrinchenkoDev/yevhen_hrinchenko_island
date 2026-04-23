package ua.hrinchenko.organism.animal.herbivore;

import ua.hrinchenko.config.Config;

public class Sheep extends Herbivore {
    public Sheep() {
        super(
                Config.WEIGHT.get("Sheep"),
                Config.MAX_PER_CELL.get("Sheep"),
                Config.SPEED.get("Sheep"),
                Config.FOOD_NEEDED.get("Sheep")
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
