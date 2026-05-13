package org.example.invenchef.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class DashboardController {

    // ── Stat card labels (must match dashboard-view.fxml fx:id) ──────────────
    @FXML private Label totalIngredientsLabel;
    @FXML private Label expiringSoonLabel;
    @FXML private Label expiredItemsLabel;
    @FXML private Label recipesAvailableLabel;
    @FXML private Label inventorySummaryLabel;

    // ── Panel containers (must match dashboard-view.fxml fx:id) ──────────────
    @FXML private VBox expiringItemsContainer;   // ← was "recentActivityContainer" (mismatch fixed)
    @FXML private VBox recipesContainer;         // ← was "suggestionsContainer" (mismatch fixed)

    // ── Internal state ────────────────────────────────────────────────────────
    private int inventoryCount = 0;

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Set stat cards
        totalIngredientsLabel.setText("0");
        expiringSoonLabel.setText("0");
        expiredItemsLabel.setText("0");
        recipesAvailableLabel.setText("0");
        inventorySummaryLabel.setText("Total: 0 items across categories");

        // Populate panels with placeholder rows
        populateExpiringItems();
        populateRecipes();
    }

    // ── Panel population ──────────────────────────────────────────────────────

    private void populateExpiringItems() {
        expiringItemsContainer.getChildren().clear();

        // Sample rows — replace with real data from your Fridge/DB layer
        String[][] items = {
            {"Milk",   "Expires today",    "#d62828"},
            {"Eggs",   "Expires in 2 days","#fca311"},
            {"Butter", "Expires in 5 days","#fca311"},
        };

        for (String[] item : items) {
            expiringItemsContainer.getChildren().add(buildRow(item[0], item[1], item[2]));
        }

        if (expiringItemsContainer.getChildren().isEmpty()) {
            expiringItemsContainer.getChildren().add(noDataLabel("No items expiring soon 🎉"));
        }
    }

    private void populateRecipes() {
        recipesContainer.getChildren().clear();

        // Sample rows — replace with real recipe data
        String[][] recipes = {
            {"Omelette",        "2 ingredients available","#40916c"},
            {"Butter Chicken",  "5 ingredients available","#40916c"},
            {"Pasta Primavera", "3 ingredients available","#40916c"},
        };

        for (String[] recipe : recipes) {
            recipesContainer.getChildren().add(buildRow(recipe[0], recipe[1], recipe[2]));
        }

        if (recipesContainer.getChildren().isEmpty()) {
            recipesContainer.getChildren().add(noDataLabel("No recipes available yet"));
        }
    }

    /** Builds a simple two-label row with a coloured dot indicator. */
    private HBox buildRow(String title, String subtitle, String dotColor) {
        HBox row = new HBox(12);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setStyle("-fx-background-color: #f8f8f8; -fx-background-radius: 8;");

        Label dot = new Label("●");
        dot.setStyle("-fx-text-fill: " + dotColor + "; -fx-font-size: 10px;");

        VBox text = new VBox(2);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #222;");
        Label subtitleLbl = new Label(subtitle);
        subtitleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
        text.getChildren().addAll(titleLbl, subtitleLbl);
        HBox.setHgrow(text, Priority.ALWAYS);

        row.getChildren().addAll(dot, text);
        return row;
    }

    private Label noDataLabel(String message) {
        Label l = new Label(message);
        l.setStyle("-fx-text-fill: #aaa; -fx-font-size: 13px; -fx-padding: 10 0 0 0;");
        return l;
    }

    // ── Public API (called from other controllers if needed) ──────────────────

    public void increaseInventory() {
        inventoryCount++;
        totalIngredientsLabel.setText(String.valueOf(inventoryCount));
    }

    public void openRecipes()  { System.out.println("Opening recipes…");  }
    public void openShopping() { System.out.println("Opening shopping…"); }
    public void openPlanner()  { System.out.println("Opening planner…");  }
}
