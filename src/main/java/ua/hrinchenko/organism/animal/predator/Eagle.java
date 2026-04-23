package ua.hrinchenko.organism.animal.predator;

import ua.hrinchenko.config.Config;

public class Eagle extends Predator {
    public Eagle() {
        // Викликаємо конструктор батьківського класу Animal за допомогою super()
        // Передаємо параметри: вага (50), макс. на клітинці (30), швидкість (3), потрібно їжі (8)
        super(
                Config.WEIGHT.get("Eagle"),
                Config.MAX_PER_CELL.get("Eagle"),
                Config.SPEED.get("Eagle"),
                Config.FOOD_NEEDED.get("Eagle")
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
