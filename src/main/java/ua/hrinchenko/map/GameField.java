package ua.hrinchenko.map;

public class GameField {

    private final int width; // Ширина острова (наприклад, 100)
    private final int height; // Висота острова (наприклад, 20)

    // Двовимірний масив об'єктів нашого класу Location
    private final Location[][] locations;

    public GameField(int width, int height) {
        this.width = width;
        this.height = height;
        // Створюємо масив потрібного розміру
        this.locations = new Location[height][width];

        // ВАЖЛИВО: спочатку масив порожній (null).
        // Його потрібно заповнити об'єктами Location.
        initialize();
    }

    private void initialize() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Створюємо кожну окрему клітинку острова
                locations[y][x] = new Location();
            }
        }
    }

    // Метод, щоб отримати конкретну клітинку за координатами
    public Location getLocation(int x, int y) {
        // Додамо перевірку, щоб не вийти за межі острова
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return locations[y][x];
        }
        return null;
    }

    // Гетери для розмірів, вони знадобляться для циклів симуляції
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
