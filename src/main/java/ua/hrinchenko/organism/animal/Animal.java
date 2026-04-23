package ua.hrinchenko.organism.animal;

import ua.hrinchenko.EntityFactory;
import ua.hrinchenko.config.Config;
import ua.hrinchenko.map.Location;
import ua.hrinchenko.organism.plant.Plant;

import java.beans.Introspector;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Animal {
    // Базові характеристики виду
    private double weight;
    private int maxPerCell;
    private int speed;
    private double foodNeeded;

    // Поточний стан конкретної особини
    private double currentSatiety; // Поточний рівень ситості
    private boolean isAlive = true; // Чи жива тварина

    // Прапорець: чи ходила тварина в поточному такті симуляції
    protected boolean hasMoved = false;
    protected boolean hasReproduced = false;

    public Animal(double weight, int maxPerCell, int speed, double foodNeeded) {
        this.weight = weight;
        this.maxPerCell = maxPerCell;
        this.speed = speed;
        this.foodNeeded = foodNeeded;
        this.currentSatiety = foodNeeded; // Нехай на початку симуляції тварина буде сита
    }

    // --- ПОВЕДІНКА (Методи) ---
    // Абстрактний метод пересування.
    public abstract void move();

    public abstract void eat();

    public abstract void multiply();

    // Додамо зручний метод, щоб тварина сама знала свою назву (наприклад, "Wolf")
    public String getType() {
        return this.getClass().getSimpleName();
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void die() {
        this.isAlive = false;
    }

    // Геттер та сеттер для прапорця
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean hasReproduced() {
        return hasReproduced;
    }

    public void setReproduced(boolean hasReproduced) {
        this.hasReproduced = hasReproduced;
    }

    // Метод: обрати напрямок пересування
    // Повертає масив з двох чисел: [новий X, новий Y]
    public int[] chooseDirection(int currentX, int currentY, int islandWidth, int islandHeight) {
        // Визначаємо, скільки кроків зробить тварина (від 0 до speed)
        int steps = ThreadLocalRandom.current().nextInt(speed + 1);

        if (steps == 0) {
            return new int[]{currentX, currentY}; // Залишається на місці
        }

        int newX = currentX;
        int newY = currentY;

        // Робимо кожен крок випадковим чином
        for (int i = 0; i < steps; i++) {
            int direction = ThreadLocalRandom.current().nextInt(4); // 0, 1, 2 або 3

            if (direction == 0 && newY > 0) {
                newY--; // Крок вгору
            } else if (direction == 1 && newX < islandWidth - 1) {
                newX++; // Крок вправо
            } else if (direction == 2 && newY < islandHeight - 1) {
                newY++; // Крок вниз
            } else if (direction == 3 && newX > 0) {
                newX--; // Крок вліво
            }
        }

        return new int[]{newX, newY};
    }

    // Метод, який імітує витрату енергії (голод)
    public void growHungry() {
        // Якщо тварина вже мертва, вона не може зголодніти ще більше
        if (!isAlive) {
            return;
        }

        if (this.foodNeeded == 0) return; // НОВИЙ РЯДОК (Гусінь не помирає від голоду)

        // Віднімаємо 15% від максимальної потреби в їжі
        this.currentSatiety -= (this.foodNeeded * 0.15);

        // Перевіряємо, чи не вмерла тварина від голоду
        if (this.currentSatiety <= 0) {
            this.die();
        }
    }

    // універсальний метод eat()
    public void eat(Location loc) {
        // Якщо тварина вже сита або мертва, вона не їсть
        if (!isAlive || currentSatiety > foodNeeded * 0.7) {
            return;
        }

        // 1. Дістаємо меню для цього виду тварини з нашого Config
        Map<String, Integer> menu = Config.EATING_PROBABILITIES.get(this.getType());

        // Якщо меню немає (наприклад, це рослина, хоча метод у тваринах), виходимо
        if (menu == null || menu.isEmpty()) {
            return;
        }

        // 2. Шукаємо жертву на клітинці згідно з меню
        for (Map.Entry<String, Integer> entry : menu.entrySet()) {
            String victimType = entry.getKey();
            Integer probability = entry.getValue();

            // Сценарій А: Тварина їсть РОСЛИНИ
            if (victimType.equals("Plant")) {
                List<Plant> plants = loc.getPlants();
                if (!plants.isEmpty()) {
                    // З'їдаємо одну рослину
                    plants.remove(0);
                    // Збільшуємо ситість
                    this.currentSatiety = Math.min(this.currentSatiety + Config.WEIGHT.get("Plant"), this.foodNeeded);
                    return; // Поїли - завершуємо метод
                }
            }
            // Сценарій Б: Тварина їсть ІНШИХ ТВАРИН
            else {
                List<Animal> potentialVictims = loc.getAnimalsByType(victimType);

                for (Animal victim : potentialVictims) {
                    // Перевіряємо, чи жертва ще жива (може її вже з'їв хтось інший у цьому такті)
                    // Якщо випало число менше за нашу вірогідність - полювання успішне!
                    if (victim.isAlive() && ThreadLocalRandom.current().nextInt(100) < probability) {
                        victim.die();
                        this.currentSatiety = Math.min(this.currentSatiety + Config.WEIGHT.get(victimType), this.foodNeeded);
                        return;
                    }
                }
            }
        }
    }

    // Додамо метод multiply, який шукатиме партнера на тій самій локації.
    public void multiply(Location loc) {
        if (!isAlive || hasReproduced) return;

        String type = this.getType();
        List<Animal> partners = loc.getAnimalsByType(type);

        // Для розмноження потрібно мінімум 2 особини одного виду
        // Також перевіряємо, чи є місце для нових мешканців
        int maxCount = Config.MAX_PER_CELL.get(type);

        if (partners.size() >= 2 && partners.size() < maxCount) {
            // Шукаємо партнера (будь-яку іншу живу особину того ж виду)
            for (Animal partner : partners) {
                if (partner != this && partner.isAlive() && !partner.hasReproduced()) {

                    // Ставимо обом прапорець, що вони вже спарувалися!
                    this.setReproduced(true);
                    partner.setReproduced(true);

                    // Визначаємо кількість дитинчат (від 1 до maxOffspring)
                    int maxBabies = Config.MAX_OFFSPRING.get(type);
                    if (maxBabies > 0) {
                        int babiesCount = ThreadLocalRandom.current().nextInt(maxBabies) + 1;

                        // Створюємо нових тварин через Фабрику
                        for (int i = 0; i < babiesCount && loc.getAnimalsByType(type).size() < maxCount; i++) {
                            loc.addAnimal(type, EntityFactory.createAnimal(type));
                        }
                    }
                    break; // Пара знайшлася, діти народилися, виходимо
                }
            }
        }
    }
}