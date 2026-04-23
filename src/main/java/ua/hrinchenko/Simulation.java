package ua.hrinchenko;

import ua.hrinchenko.config.Config;
import ua.hrinchenko.map.GameField;
import ua.hrinchenko.map.Location;
import ua.hrinchenko.organism.animal.Animal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Simulation {
    private final GameField field;

    // Пул для запуску завдань за розкладом (3 потоки: рослини, тварини, статистика)
    private final ScheduledExecutorService scheduledPool;

    // Пул для виконання важких паралельних завдань (наприклад, 4 потоки-робітники)
    private final ExecutorService animalWorkerPool;

    // Лічильник тактів (днів)
    private int currentTick = 1;

    public Simulation(GameField field) {
        this.field = field;
        this.scheduledPool = Executors.newScheduledThreadPool(3);
        this.animalWorkerPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    // Головний метод для початкового заселення
    public void populateIsland() {
        // Проходимося по кожному запису в нашому словнику стартових популяцій
        for (Map.Entry<String, Integer> entry : Config.STARTING_POPULATION.entrySet()) {
            String type = entry.getKey(); // Наприклад, "Wolf"
            int maxStartingCount = entry.getValue();

            int actualCount;

            // Логіка для рослин (їх має бути достатньо на старті, щоб травоїдні не померли одразу)
            if (type.equals("Plant")) {
                // Від половини максимуму до максимуму
                int minPlants = maxStartingCount / 2;
                actualCount = ThreadLocalRandom.current().nextInt(minPlants, maxStartingCount + 1);
            }
            // Логіка для тварин
            else {
                // Перевіряємо, щоб максимум був хоча б 2 (інакше random видасть помилку)
                if (maxStartingCount >= 2) {
                    // Генеруємо від 2 до вказаного максимуму
                    actualCount = ThreadLocalRandom.current().nextInt(2, maxStartingCount + 1);
                } else {
                    actualCount = maxStartingCount;
                }
            }

            // Створюємо і розміщуємо потрібну кількість сутностей
            for (int i = 0; i < actualCount; i++) {
                placeRandomly(type);
            }
        }
    }

    // Метод, який шукає випадкову вільну клітинку для однієї сутності
    private void placeRandomly(String type) {
        boolean placed = false;
        int maxPerCell = Config.MAX_PER_CELL.get(type);

        // Шукаємо місце, поки не знайдемо (while)
        while (!placed) {
            // Генеруємо випадкові координати X та Y
            int x = ThreadLocalRandom.current().nextInt(field.getWidth());
            int y = ThreadLocalRandom.current().nextInt(field.getHeight());
            Location loc = field.getLocation(x, y);

            // Перевіряємо, чи є місце на клітинці (не перевищено ліміт)
            if (type.equals("Plant")) {
                if (loc.getPlants().size() < maxPerCell) {
                    loc.getPlants().add(EntityFactory.createPlant());
                    placed = true;
                }
            } else {
                if (loc.getAnimalsByType(type).size() < maxPerCell) {
                    loc.addAnimal(type, EntityFactory.createAnimal(type));
                    placed = true;
                }
            }
        }
    }

    public void start() {
        System.out.println("\uD83D\uDE80 The simulation begins...");

//        // 1. Завдання росту рослин (кожен такт)
//        scheduledPool.scheduleAtFixedRate(() -> {
//            growPlants();
//        }, 0, Config.TICK_DURATION_MS, TimeUnit.MILLISECONDS);
//
//        // 2. Життєвий цикл тварин (кожен такт)
//        scheduledPool.scheduleAtFixedRate(() -> {
//            runAnimalLifecycle();
//        }, 0, Config.TICK_DURATION_MS, TimeUnit.MILLISECONDS);
//
//        // 3. Виведення статистики (кожен такт, але з невеликою затримкою, щоб тварини встигли походити)
//        scheduledPool.scheduleAtFixedRate(() -> {
//            printStatistics();
//            currentTick++;
//
//            // Умова зупинки симуляції (якщо досягли ліміту тактів)
//            if (currentTick >= Config.MAX_TICKS) {
//                stop();
//            }
//        }, 100, Config.TICK_DURATION_MS, TimeUnit.MILLISECONDS);

        // Запускаємо лише ОДНЕ періодичне завдання, яке диригує всіма іншими по черзі
        scheduledPool.scheduleAtFixedRate(() -> {
            try {
                growPlants();          // 1. Спочатку ростуть рослини
                runAnimalLifecycle();  // 2. Потім тварини рухаються, їдять, розмножуються
                printStatistics();     // 3. Тільки після цього рахуємо статистику

                currentTick++;

                // Перевірка умови зупинки
                if (currentTick >= Config.MAX_TICKS) {
                    stop();
                }
            } catch (Exception e) {
                // Тепер, якщо станеться якась помилка, ми її точно побачимо в консолі!
                System.err.println("Critical error in simulation cycle on clock " + currentTick + ":");
                e.printStackTrace();
                stop();
            }
        }, 0, Config.TICK_DURATION_MS, TimeUnit.MILLISECONDS);
    }

    // Метод для зупинки симуляції
    public void stop() {
        System.out.println("\uD83C\uDFC1 Simulation completed!");
        scheduledPool.shutdown();
        animalWorkerPool.shutdown();
        System.exit(0);
    }

    private void runAnimalLifecycle() {
        // 1. Пересування (робимо в основному потоці завдання, щоб уникнути конфліктів на мапі)
        moveAllAnimals();

        // 2. Створюємо список задач для пулу робітників
        // Кожна задача оброблятиме харчування, голод і розмноження на ОДНІЙ конкретній клітинці
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int y = 0; y < field.getHeight(); y++) {
            for (int x = 0; x < field.getWidth(); x++) {
                final Location loc = field.getLocation(x, y);

                // Описуємо, що має зробити потік-робітник з цією клітинкою
                tasks.add(() -> {
                    processLocation(loc);
                    return null;
                });
            }
        }

        try {
            // Віддаємо всі задачі пулу робітників і ЧЕКАЄМО, поки вони всі закінчать
            animalWorkerPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            System.err.println("Multithreading error: " + e.getMessage());
        }
    }

    // Метод, який змушує всіх тварин на острові зробити хід
    public void moveAllAnimals() {

        // 1. ПІДГОТОВКА: Скидаємо прапорці. На початку такту ніхто ще не ходив.
        for (int y = 0; y < field.getHeight(); y++) {
            for (int x = 0; x < field.getWidth(); x++) {
                for (List<Animal> list : field.getLocation(x, y).getAnimals().values()) {
                    list.forEach(a -> {
                        a.setMoved(false);
                        a.setReproduced(false);
                    });
                }
            }
        }

        // 2. ПЕРЕСУВАННЯ
        for (int y = 0; y < field.getHeight(); y++) {
            for (int x = 0; x < field.getWidth(); x++) {
                Location currentLoc = field.getLocation(x, y);

                // Отримуємо список усіх видів тварин, які є на цій клітинці (наприклад, ["Wolf", "Rabbit"])
                // Робимо КОПІЮ ключів (new ArrayList), щоб безпечно працювати
                List<String> animalTypes = new ArrayList<>(currentLoc.getAnimals().keySet());

                for (String type : animalTypes) {
                    // Робимо КОПІЮ списку конкретних тварин (наприклад, усіх вовків на цій клітинці),
                    // щоб уникнути помилки ConcurrentModificationException при видаленні
                    List<Animal> animalsToMove = new ArrayList<>(currentLoc.getAnimalsByType(type));

                    for (Animal animal : animalsToMove) {
                        // Якщо тварина ще не ходила
                        if (!animal.hasMoved() && animal.isAlive()) {

                            // Запитуємо у тварини її нові координати
                            int[] newCoords = animal.chooseDirection(x, y, field.getWidth(), field.getHeight());

                            // Якщо координати змінилися (тварина вирішила піти, а не залишитися на місці)
                            if (newCoords[0] != x || newCoords[1] != y) {
                                Location newLoc = field.getLocation(newCoords[0], newCoords[1]);

                                // Перевіряємо, чи є місце на новій клітинці для цього виду
                                if (newLoc.getAnimalsByType(type).size() < Config.MAX_PER_CELL.get(type)) {

                                    // ПЕРЕЇЗД: забираємо зі старої клітинки, додаємо на нову
                                    currentLoc.removeAnimal(type, animal);
                                    newLoc.addAnimal(type, animal);

                                    // Ставимо прапорець, щоб вона більше не ходила в цьому такті
                                    animal.setMoved(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Метод для харчування всіх тварин
    public void feedAllAnimals() {
        for (int y = 0; y < field.getHeight(); y++) {
            for (int x = 0; x < field.getWidth(); x++) {
                Location loc = field.getLocation(x, y);

                for (List<Animal> animalList : loc.getAnimals().values()) {
                    for (Animal animal : animalList) {
                        if (animal.isAlive()) {
                            animal.eat(loc);
                        }
                    }
                }
            }
        }
    }

    // Метод, який змушує всіх тварин на острові зголодніти
    public void applyHunger() {
        for (int y = 0; y < field.getHeight(); y++) {
            for (int x = 0; x < field.getWidth(); x++) {
                Location loc = field.getLocation(x, y);

                for (List<Animal> animalList : loc.getAnimals().values()) {
                    for (Animal animal : animalList) {
                        animal.growHungry();
                    }
                }
            }
        }
    }

    public void reproduceAllAnimals() {
        for (int y = 0; y < field.getHeight(); y++) {
            for (int x = 0; x < field.getWidth(); x++) {
                Location loc = field.getLocation(x, y);

                // Робимо копію списку видів, щоб уникнути помилок при додаванні нових тварин
                List<String> types = new ArrayList<>(loc.getAnimals().keySet());
                for (String type : types) {
                    // Копіюємо список тварин, щоб ітерація була стабільною
                    List<Animal> animals = new ArrayList<>(loc.getAnimalsByType(type));
                    for (Animal animal : animals) {
                        animal.multiply(loc);
                    }
                }
            }
        }
    }

    // Метод, який виконується паралельно для кожної клітинки
    private void processLocation(Location loc) {
        // Оскільки ми працюємо з однією клітинкою, нам потрібно безпечно перебирати тварин.
        // Беремо копії списків, як ми робили раніше.
        List<String> types = new ArrayList<>(loc.getAnimals().keySet());

        for (String type : types) {
            List<Animal> animals = new ArrayList<>(loc.getAnimalsByType(type));
            for (Animal animal : animals) {
                if (animal.isAlive()) {
                    animal.eat(loc);
                    animal.multiply(loc);
                    animal.growHungry();
                }
            }
        }

        // 3. ПРИБИРАННЯ ТРУПІВ (видаляємо мертвих тварин зі списків клітинки)
        for (String type : types) {
            loc.getAnimalsByType(type).removeIf(animal -> !animal.isAlive());
        }
    }

    // Простий метод росту рослин
    private void growPlants() {
        int maxPlants = Config.MAX_PER_CELL.get("Plant");

        for (int y = 0; y < field.getHeight(); y++) {
            for (int x = 0; x < field.getWidth(); x++) {
                Location loc = field.getLocation(x, y);

                int newPlants = ThreadLocalRandom.current().nextInt(20) + 5;

                // Якщо є місце, додаємо трохи нових рослин
                if (loc.getPlants().size() < maxPlants) {
                    // Наприклад, виростає випадково від 0 до 5 рослин
                    for (int i = 0; i < newPlants && loc.getPlants().size() < maxPlants; i++) {
                        loc.getPlants().add(EntityFactory.createPlant());
                    }
                }
            }
        }
    }

    // Метод для збору та виведення статистики
    private void printStatistics() {
        // Словник для підрахунку кількості кожної сутності (Назва -> Кількість)
        Map<String, Integer> statistics = new HashMap<>();
        int totalAnimals = 0;
        int totalPlants = 0;

        // Проходимо по всьому острову
        for (int y = 0; y < field.getHeight(); y++) {
            for (int x = 0; x < field.getWidth(); x++) {
                Location loc = field.getLocation(x, y);

                // Рахуємо тварин
                for (Map.Entry<String, List<Animal>> entry : loc.getAnimals().entrySet()) {
                    String type = entry.getKey();
                    // Рахуємо тільки живих
                    int aliveCount = (int) entry.getValue().stream().filter(Animal::isAlive).count();

                    if (aliveCount > 0) {
                        statistics.put(type, statistics.getOrDefault(type, 0) + aliveCount);
                        totalAnimals += aliveCount;
                    }
                }

                // Рахуємо рослини
                int plantCount = loc.getPlants().size();
                if (plantCount > 0) {
                    statistics.put("Plant", statistics.getOrDefault("Plant", 0) + plantCount);
                    totalPlants += plantCount;
                }
            }
        }

        // === ВИВЕДЕННЯ В КОНСОЛЬ ===
        System.out.println("\n==================================================");
        System.out.println("🌍 SIMULATION TIME: " + currentTick);
        System.out.println("==================================================");
        System.out.println("Total animals: " + totalAnimals + " | Total plants: " + totalPlants);
        System.out.println("--------------------------------------------------");

        // Виводимо детальну статистику по кожному виду
        for (Map.Entry<String, Integer> entry : statistics.entrySet()) {
            System.out.printf("%-15s : %d шт.\n", entry.getKey(), entry.getValue());
        }

        // Якщо всі тварини вимерли - зупиняємо симуляцію
        if (totalAnimals == 0) {
            System.out.println("💀 All animals on the island are extinct. Simulation complete.");
            stop();
        }
    }
}
