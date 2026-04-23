package ua.hrinchenko;

import ua.hrinchenko.config.Config;
import ua.hrinchenko.map.GameField;

public class Main {
    public static void main(String[] args) {
        System.out.println("\uD83C\uDF31 Preparation of the island...");
        // 1. Завантажуємо налаштування
        Config.load("C:\\Users\\digdo\\Documents\\Java Projects\\yevhen_hrinchenko_island\\src\\main\\resources\\config.xml");

        // 2. Створюємо карту острова (беремо розміри з конфігурації)
        System.out.println("🌍 Creating an island " + Config.ISLAND_WIDTH + "x" + Config.ISLAND_HEIGHT + "...");
        GameField field = new GameField(Config.ISLAND_WIDTH, Config.ISLAND_HEIGHT);

        // 3. Створюємо саму симуляцію і передаємо їй наш острів
        Simulation simulation = new Simulation(field);

        // 4. Заселяємо острів тваринами та рослинами
        System.out.println("🐺 Settlement of the island...");
        simulation.populateIsland();

        // 5. ЗАПУСК! (вмикається багатопотоковість)
        simulation.start();
    }
}
