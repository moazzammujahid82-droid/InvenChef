package org.example.invenchef.model;

public abstract class Ingredients {

    private String name;
    private double quantity;
    private String unit;

    Ingredients() {
    }

    Ingredients(String name) {
        this.name = name;
    }

    Ingredients(String name, double quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    Ingredients(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public abstract boolean isUseable();

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

}