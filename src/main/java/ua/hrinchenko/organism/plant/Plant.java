package ua.hrinchenko.organism.plant;

import ua.hrinchenko.config.Config;

public class Plant {
    // Характеристики рослини
    private final double weight;

    public Plant() {
        // Під час створення нової рослини, вона автоматично бере свої
        // характеристики зі спільних налаштувань
        this.weight = Config.WEIGHT.get("Plant");
    }

    // Додамо методи (гетери), щоб інші класи могли дізнатися вагу рослини
    // (наприклад, коли травоїдне її їстиме)
    public double getWeight() {
        return weight;
    }
}
