package ua.hrinchenko.organism.animal.predator;

import ua.hrinchenko.config.Config;

public class Bear extends Predator {
    public Bear() {
        // Викликаємо конструктор батьківського класу Animal за допомогою super()
        // Передаємо параметри: вага (50), макс. на клітинці (30), швидкість (3), потрібно їжі (8)
        super(
                Config.WEIGHT.get("Bear"),
                Config.MAX_PER_CELL.get("Bear"),
                Config.SPEED.get("Bear"),
                Config.FOOD_NEEDED.get("Bear")
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
