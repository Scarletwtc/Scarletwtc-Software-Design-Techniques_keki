package com.keki.singleton;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    private static InventoryManager instance;
    private Map<String, Integer> ingredients;

    private InventoryManager() {
        ingredients = new HashMap<>();
        ingredients.put("Flour", 10);
        ingredients.put("Sugar", 10);
        ingredients.put("Milk", 10);
        ingredients.put("Egg", 12);
        ingredients.put("BakingSoda", 10);
        ingredients.put("Coffee", 10);
    }

    public static InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    public int checkStock(String item) {
        return ingredients.getOrDefault(item, 0);
    }

    public void updateStock(String item, int quantity) {
        ingredients.put(item, ingredients.getOrDefault(item, 0) + quantity);
    }
}
