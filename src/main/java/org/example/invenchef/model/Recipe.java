package org.example.invenchef.model;

import java.util.ArrayList;

public class Recipe {

    private String title;
    private int prepTime;

    private ArrayList<Ingredients> requirements;

    public boolean canCook(Fridge f) {
        return true; // Placeholder return value
    }
}
