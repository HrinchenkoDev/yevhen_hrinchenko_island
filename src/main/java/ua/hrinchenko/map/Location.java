package ua.hrinchenko.map;

import ua.hrinchenko.organism.animal.Animal;
import ua.hrinchenko.organism.plant.Plant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Location {
    // Словник усіх тварин на цій клітинці.
    // Ключ (String) - це назва виду (наприклад, "Wolf", "Rabbit").
    // Значення (List<Animal>) - це список усіх конкретних тварин цього виду.
    private Map<String, List<Animal>> animals;

    // Рослини можна тримати в окремому списку, бо їх вид лише один
    private List<Plant> plants;

    public Location() {
        this.animals = new HashMap<>();
        this.plants = new ArrayList<>();
    }

    // Метод для додавання тварини на клітинку
    public void addAnimal(String type, Animal animal) {
        // Якщо такого виду ще немає на клітинці, створюємо для нього новий список
        animals.putIfAbsent(type, new ArrayList<>());

        // Додаємо тварину до відповідного списку
        animals.get(type).add(animal);
    }

    // Метод для видалення тварини з клітинки (коли вона йде або помирає)
    public void removeAnimal(String type, Animal animal) {
        if (animals.containsKey(type)) {
            animals.get(type).remove(animal);
        }
    }

    // Метод для отримання списку тварин певного виду
    public List<Animal> getAnimalsByType(String type) {
        return animals.getOrDefault(type, new ArrayList<>());
    }

    public List<Plant> getPlants() {
        return plants;
    }

    // Метод для отримання всього словника тварин
    public Map<String, List<Animal>> getAnimals() {
        return animals;
    }
}
