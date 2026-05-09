package org.example.invenchef.model;

import java.time.LocalDate;

public class Perishable extends Ingredients {

    private final LocalDate expiryDate;

    public Perishable(String name, double quantity, String unit, LocalDate expiryDate) {
        super(name, quantity, unit);
        this.expiryDate = expiryDate;
    }

    public boolean isUseable() {
        return true; // Placeholder return value
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public long getDaysUntilExpiry() {
        return LocalDate.now().until(expiryDate).getDays();
    }

}
