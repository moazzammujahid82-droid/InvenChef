package org.example.invenchef.model;

import java.util.ArrayList;
import java.util.List;

public class Fridge {

    private static final int CAPACITY = 50;
    private final ArrayList<Ingredients> inventory = new ArrayList<>();

    public void addItem(Ingredients item) {
        addIngredient(item);
    }

    public void addIngredient(Ingredients item) {
        if (inventory.size() >= CAPACITY) {
            throw new IllegalStateException("Fridge is full. Cannot add more items.");
        }

        inventory.add(item);
    }

    public void removeIngredient(Ingredients item) {
        inventory.remove(item);
    }

    public void removeExpired(Ingredients item) {
        removeIngredient(item);
    }

    public List<Ingredients> getAllIngredients() {
        return new ArrayList<>(inventory);
    }

    public int getExpiredCount() {
        int count = 0;
        for (Ingredients item : inventory) {
            if (item instanceof Perishable perishableItem) {
                if (perishableItem.getDaysUntilExpiry() < 0) {
                    count++;
                }
            }
        }
        return count;
    }
}
