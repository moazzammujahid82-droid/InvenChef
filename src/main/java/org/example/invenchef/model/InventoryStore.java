package org.example.invenchef.model;

import java.util.ArrayList;
import java.util.List;


public class InventoryStore {

    private static final InventoryStore INSTANCE = new InventoryStore();

    public static InventoryStore getInstance() {
        return INSTANCE;
    }

    private InventoryStore() {
        fridge = new Fridge();
    }

    private final Fridge fridge;

    public Fridge getFridge() {
        return fridge;
    }


    private final List<Runnable> listeners = new ArrayList<>();

    public void addChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(Runnable listener) {
        listeners.remove(listener);
    }

    public void notifyListeners() {
        for (Runnable r : listeners) {
            r.run();
        }
    }
}
