package ua.hrinchenko;

import ua.hrinchenko.organism.animal.Animal;
import ua.hrinchenko.organism.animal.herbivore.*;
import ua.hrinchenko.organism.animal.predator.*;
import ua.hrinchenko.organism.plant.Plant;

public class EntityFactory {

    // Метод, який отримує рядок (назву), а повертає готового звіра
    public static Animal createAnimal(String type) {
        switch (type) {
            case "Wolf":
                return new Wolf();
            case "Fox":
                return new Fox();
            case "Eagle":
                return new Eagle();
            case "Boa":
                return new Boa();
            case "Bear":
                return new Bear();
            case "Boar":
                return new Boar();
            case "Buffalo":
                return new Buffalo();
            case "Caterpillar":
                return new Caterpillar();
            case "Deer":
                return new Deer();
            case "Duck":
                return new Duck();
            case "Goat":
                return new Goat();
            case "Horse":
                return new Horse();
            case "Mouse":
                return new Mouse();
            case "Rabbit":
                return new Rabbit();
            case "Sheep":
                return new Sheep();
            default:
                throw new IllegalArgumentException("Unknown type of animal: " + type);
        }
    }

    // Окремий метод для рослин
    public static Plant createPlant() {
        return new Plant();
    }
}
