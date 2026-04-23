package ua.hrinchenko.config;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Config {

    // ==========================================
    // 1. БАЗОВІ НАЛАШТУВАННЯ СИМУЛЯЦІЇ
    // ==========================================
    public static int ISLAND_WIDTH;
    public static int ISLAND_HEIGHT;
    public static int TICK_DURATION_MS; // Тривалість такту (1 секунда)
    public static int MAX_TICKS; // Умова зупинки симуляції (максимальна кількість кроків)

    // ==========================================
    // 2. ХАРАКТЕРИСТИКИ СУТНОСТЕЙ (Словники)
    // ==========================================
    public static final Map<String, Double> WEIGHT = new HashMap<>();
    public static final Map<String, Integer> MAX_PER_CELL = new HashMap<>();
    public static final Map<String, Integer> SPEED = new HashMap<>();
    public static final Map<String, Double> FOOD_NEEDED = new HashMap<>();

    // Стартова популяція на всьому острові при запуску
    public static final Map<String, Integer> STARTING_POPULATION = new HashMap<>();

    // Максимальна кількість дитинчат, яка може народитися у пари за один такт
    public static final Map<String, Integer> MAX_OFFSPRING = new HashMap<>();

    // Таблиця вірогідностей поїдання (Хто їсть -> (Кого їсть -> Вірогідність у %))
    public static final Map<String, Map<String, Integer>> EATING_PROBABILITIES = new HashMap<>();

    // Головний метод для завантаження конфігурації
    public static void load(String filePath) {
        try {
            File inputFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            // 1. Читаємо налаштування симуляції
            Node simNode = doc.getElementsByTagName("simulation").item(0);
            if (simNode.getNodeType() == Node.ELEMENT_NODE) {
                Element simElement = (Element) simNode;
                ISLAND_WIDTH = Integer.parseInt(simElement.getElementsByTagName("islandWidth").item(0).getTextContent());
                ISLAND_HEIGHT = Integer.parseInt(simElement.getElementsByTagName("islandHeight").item(0).getTextContent());
                TICK_DURATION_MS = Integer.parseInt(simElement.getElementsByTagName("tickDurationMs").item(0).getTextContent());
                MAX_TICKS = Integer.parseInt(simElement.getElementsByTagName("maxTicks").item(0).getTextContent());
            }

            // 2. Читаємо характеристики тварин
            NodeList entityList = doc.getElementsByTagName("entity");
            for (int i = 0; i < entityList.getLength(); i++) {
                Node node = entityList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String type = element.getAttribute("type");

                    WEIGHT.put(type, Double.parseDouble(element.getElementsByTagName("weight").item(0).getTextContent()));
                    MAX_PER_CELL.put(type, Integer.parseInt(element.getElementsByTagName("maxPerCell").item(0).getTextContent()));
                    SPEED.put(type, Integer.parseInt(element.getElementsByTagName("speed").item(0).getTextContent()));
                    FOOD_NEEDED.put(type, Double.parseDouble(element.getElementsByTagName("foodNeeded").item(0).getTextContent()));
                    STARTING_POPULATION.put(type, Integer.parseInt(element.getElementsByTagName("startingPopulation").item(0).getTextContent()));
                    MAX_OFFSPRING.put(type, Integer.parseInt(element.getElementsByTagName("maxOffspring").item(0).getTextContent()));

                    // 3. Читаємо ймовірності поїдання
                    Map<String, Integer> menu = new HashMap<>();
                    NodeList victims = element.getElementsByTagName("victim");
                    for (int j = 0; j < victims.getLength(); j++) {
                        Element victimElement = (Element) victims.item(j);
                        menu.put(victimElement.getAttribute("type"), Integer.parseInt(victimElement.getAttribute("probability")));
                    }
                    EATING_PROBABILITIES.put(type, menu);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading configuration file: " + e.getMessage());
        }
    }
}
