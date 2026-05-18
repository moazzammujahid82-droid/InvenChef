package org.example.invenchef.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.invenchef.model.Fridge;
import org.example.invenchef.model.Ingredients;
import org.example.invenchef.model.InventoryStore;
import org.example.invenchef.model.Perishable;
import org.example.invenchef.model.PantryStaple;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class DashboardController {

    @FXML private Label totalIngredientsLabel;
    @FXML private Label expiringSoonLabel;
    @FXML private Label expiredItemsLabel;
    @FXML private Label recipesAvailableLabel;
    @FXML private Label inventorySummaryLabel;

    @FXML private VBox expiringItemsContainer;
    @FXML private VBox recipesContainer;

    private final Fridge fridge = InventoryStore.getInstance().getFridge();

    private final Runnable changeListener = this::refreshAll;

    @FXML
    public void initialize() {
        InventoryStore.getInstance().addChangeListener(changeListener);
        refreshAll();
    }

    private void refreshAll() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::refreshAll);
            return;
        }
        updateStatCards();
        updateExpiringPanel();
        updateRecipesPanel();
    }

    private void updateStatCards() {
        List<Ingredients> all   = fridge.getAllIngredients();
        LocalDate today         = LocalDate.now();
        LocalDate sevenDaysOut  = today.plusDays(7);

        int total    = all.size();
        int expiring = 0;
        int expired  = 0;

        for (Ingredients i : all) {
            if (i instanceof Perishable p) {
                LocalDate exp = p.getExpiryDate();
                if      (exp.isBefore(today))       expired++;
                else if (exp.isBefore(sevenDaysOut)) expiring++;
            }
        }

        int recipesReady = Math.max(0, total - expired);

        totalIngredientsLabel.setText(String.valueOf(total));
        expiringSoonLabel.setText(String.valueOf(expiring));
        expiredItemsLabel.setText(String.valueOf(expired));
        recipesAvailableLabel.setText(String.valueOf(recipesReady));

        long perishCount = all.stream().filter(i -> i instanceof Perishable).count();
        long pantryCount = all.stream().filter(i -> i instanceof PantryStaple).count();
        inventorySummaryLabel.setText(
            "Total: " + total + " items  •  " +
            perishCount + " perishable  •  " +
            pantryCount + " pantry staples"
        );
    }

    private void updateExpiringPanel() {
        expiringItemsContainer.getChildren().clear();
        LocalDate today        = LocalDate.now();
        LocalDate sevenDaysOut = today.plusDays(7);

        List<Perishable> soon = new ArrayList<>();
        for (Ingredients i : fridge.getAllIngredients()) {
            if (i instanceof Perishable p && !p.getExpiryDate().isAfter(sevenDaysOut)) {
                soon.add(p);
            }
        }
        soon.sort(Comparator.comparing(Perishable::getExpiryDate));

        if (soon.isEmpty()) {
            expiringItemsContainer.getChildren().add(
                noDataLabel("No items expiring within 7 days \uD83C\uDF89"));
            return;
        }

        for (Perishable p : soon) {
            LocalDate exp      = p.getExpiryDate();
            boolean   isExpired = exp.isBefore(today);
            boolean   isToday   = exp.isEqual(today);
            String    dot       = (isExpired || isToday) ? "#d62828" : "#fca311";

            long daysLeft = today.until(exp).getDays();
            String badge  = isExpired ? "Expired"
                          : isToday   ? "Expires today"
                          : "In " + daysLeft + " day(s)";

            String sub = exp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                       + "  \u00B7  " + p.getQuantity() + " " + p.getUnit()
                       + "  \u2014  " + badge;

            expiringItemsContainer.getChildren().add(buildRow(p.getName(), sub, dot));
        }
    }

    private void updateRecipesPanel() {
        recipesContainer.getChildren().clear();
        LocalDate today = LocalDate.now();

        List<Ingredients> fresh = new ArrayList<>();
        for (Ingredients i : fridge.getAllIngredients()) {
            boolean ok = !(i instanceof Perishable p) || !p.getExpiryDate().isBefore(today);
            if (ok) fresh.add(i);
        }

        if (fresh.isEmpty()) {
            recipesContainer.getChildren().add(
                noDataLabel("Add some ingredients to get recipe ideas"));
            return;
        }

        List<String[]> suggestions = buildSuggestions(fresh);

        if (suggestions.isEmpty()) {
            recipesContainer.getChildren().add(
                noDataLabel("No matching recipes \u2014 keep stocking up!"));
            return;
        }

        for (String[] s : suggestions) {
            recipesContainer.getChildren().add(buildRow(s[0], s[1], s[2]));
        }
    }

    private List<String[]> buildSuggestions(List<Ingredients> fresh) {
        List<String[]> results = new ArrayList<>();

        Predicate<String> has = kw ->
            fresh.stream().anyMatch(i -> i.getName().toLowerCase().contains(kw.toLowerCase()));

        String[][] recipes = {
            {"\uD83C\uDF73 Omelette",       "egg",              "#40916c"},
            {"\uD83E\uDD5B Milk Shake",      "milk",             "#40916c"},
            {"\uD83C\uDF5E Butter Toast",    "butter,bread",     "#40916c"},
            {"\uD83E\uDDC6 Chickpea Curry",  "chickpea",         "#40916c"},
            {"\uD83C\uDF5A Plain Rice",      "rice",             "#40916c"},
            {"\uD83C\uDF4B Lemon Chicken",   "chicken,lemon",    "#40916c"},
            {"\uD83E\uDD57 Fresh Salad",     "tomato,onion",     "#40916c"},
            {"\uD83E\uDD63 Yogurt Bowl",     "yogurt",           "#40916c"},
            {"\uD83E\uDDC0 Cheese Toast",    "cheese,bread",     "#40916c"},
            {"\uD83C\uDF5D Pasta",           "pasta",            "#40916c"},
            {"\uD83E\uDDD2 Pancakes",        "flour,egg",        "#40916c"},
            {"\uD83C\uDF72 Dal",             "lentil",           "#40916c"},
            {"\uD83C\uDF57 Chicken Karahi",  "chicken,tomato",   "#40916c"},
            {"\uD83E\uDD51 Avocado Toast",   "avocado,bread",    "#40916c"},
            {"\uD83C\uDF6F Honey Yoghurt",   "yoghurt,honey",    "#40916c"},
        };

        for (String[] r : recipes) {
            String[] keywords = r[1].split(",");
            boolean allFound = true;
            for (String kw : keywords) {
                if (!has.test(kw.trim())) { allFound = false; break; }
            }
            if (allFound) {
                String sub = "Ingredients found: " + r[1].replace(",", ", ");
                results.add(new String[]{r[0], sub, r[2]});
            }
        }

        return results;
    }

    private HBox buildRow(String title, String subtitle, String dotColor) {
        HBox row = new HBox(12);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

        Label dot = new Label("\u25CF");
        dot.setStyle("-fx-text-fill: " + dotColor + "; -fx-font-size: 11px;");
        dot.setMinWidth(14);

        VBox text = new VBox(2);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1b1b1b;");
        Label subLbl = new Label(subtitle);
        subLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
        text.getChildren().addAll(titleLbl, subLbl);
        HBox.setHgrow(text, Priority.ALWAYS);

        row.getChildren().addAll(dot, text);
        return row;
    }

    private Label noDataLabel(String message) {
        Label l = new Label(message);
        l.setStyle("-fx-text-fill: #aaa; -fx-font-size: 13px; -fx-padding: 10 0 4 4;");
        return l;
    }
}
