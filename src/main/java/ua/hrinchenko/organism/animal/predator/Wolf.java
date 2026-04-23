package ua.hrinchenko.organism.animal.predator;

import ua.hrinchenko.config.Config;

public class Wolf extends Predator {
    public Wolf() {
        // Викликаємо конструктор батьківського класу Animal за допомогою super()
        // Передаємо параметри: вага (50), макс. на клітинці (30), швидкість (3), потрібно їжі (8)
        super(
                Config.WEIGHT.get("Wolf"),
                Config.MAX_PER_CELL.get("Wolf"),
                Config.SPEED.get("Wolf"),
                Config.FOOD_NEEDED.get("Wolf")
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
