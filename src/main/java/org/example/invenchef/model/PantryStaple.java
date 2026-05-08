package org.example.invenchef.model;

public class PantryStaple extends Ingredients {

    private boolean sealed;

    public PantryStaple(String name, double quantity, String unit) {
        this(name, quantity, unit, true);
    }

    public PantryStaple(String name, double quantity, String unit, boolean sealed) {
        super(name, quantity, unit);
        this.sealed = sealed;
    }

    public boolean isUseable() {
        return sealed;
    }

}
