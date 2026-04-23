package ua.hrinchenko.organism.animal.herbivore;

import ua.hrinchenko.config.Config;

public class Mouse extends Herbivore {
    public Mouse() {
        super(
                Config.WEIGHT.get("Mouse"),
                Config.MAX_PER_CELL.get("Mouse"),
                Config.SPEED.get("Mouse"),
                Config.FOOD_NEEDED.get("Mouse")
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
