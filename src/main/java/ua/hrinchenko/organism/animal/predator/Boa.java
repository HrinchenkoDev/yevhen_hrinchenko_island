package ua.hrinchenko.organism.animal.predator;

import ua.hrinchenko.config.Config;

public class Boa extends Predator {
    public Boa() {
        // Викликаємо конструктор батьківського класу Animal за допомогою super()
        // Передаємо параметри: вага (50), макс. на клітинці (30), швидкість (3), потрібно їжі (8)
        super(
                Config.WEIGHT.get("Boa"),
                Config.MAX_PER_CELL.get("Boa"),
                Config.SPEED.get("Boa"),
                Config.FOOD_NEEDED.get("Boa")
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
