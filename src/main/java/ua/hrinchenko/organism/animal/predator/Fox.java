package ua.hrinchenko.organism.animal.predator;

import ua.hrinchenko.config.Config;

public class Fox extends Predator {
    public Fox() {
        // Викликаємо конструктор батьківського класу Animal за допомогою super()
        // Передаємо параметри: вага (50), макс. на клітинці (30), швидкість (3), потрібно їжі (8)
        super(
                Config.WEIGHT.get("Fox"),
                Config.MAX_PER_CELL.get("Fox"),
                Config.SPEED.get("Fox"),
                Config.FOOD_NEEDED.get("Fox")
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
