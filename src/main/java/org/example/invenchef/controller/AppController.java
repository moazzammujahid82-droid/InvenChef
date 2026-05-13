package org.example.invenchef.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppController {

    private static final Logger LOGGER = Logger.getLogger(AppController.class.getName());

    @FXML private StackPane contentArea;

    @FXML private Label pageTitle;

    @FXML private Button navDashboard;
    @FXML private Button navInventory;
    @FXML private Button navRecipes;
    @FXML private Button navPlanner;
    @FXML private Button navSettings;

    private static final String ACTIVE_STYLE =
            "-fx-background-color: #40916c; -fx-text-fill: white; " +
            "-fx-font-size: 15px; -fx-padding: 0 0 0 14; " +
            "-fx-alignment: CENTER-LEFT; -fx-background-radius: 6; -fx-cursor: hand;";

    private static final String INACTIVE_STYLE =
            "-fx-background-color: transparent; -fx-text-fill: white; " +
            "-fx-font-size: 15px; -fx-padding: 0 0 0 14; " +
            "-fx-alignment: CENTER-LEFT; -fx-background-radius: 6; -fx-cursor: hand;";

    private static final String INACTIVE_SETTINGS_STYLE =
            "-fx-background-color: transparent; -fx-text-fill: #b7e4c7; " +
            "-fx-font-size: 14px; -fx-padding: 0 0 0 14; " +
            "-fx-alignment: CENTER-LEFT; -fx-background-radius: 6; -fx-cursor: hand;";

    @FXML
    public void initialize() {
        showDashBoard();
    }

    @FXML
    public void showDashBoard() {
        pageTitle.setText("Dashboard");
        setActiveButton(navDashboard);
        loadView("/org/example/invenchef/view/dashboard-view.fxml");
    }

    @FXML
    public void showInventory() {
        pageTitle.setText("Inventory");
        setActiveButton(navInventory);
        loadView("/org/example/invenchef/view/inventory-view.fxml");
    }

    @FXML
    public void showRecipes() {
        pageTitle.setText("Recipes");
        setActiveButton(navRecipes);
        showPlaceholder("🍳  Recipes screen coming soon");
    }

    @FXML
    public void showPlanner() {
        pageTitle.setText("Meal Planner");
        setActiveButton(navPlanner);
        showPlaceholder("📅  Meal Planner coming soon");
    }

    @FXML
    public void showSettings() {
        pageTitle.setText("Settings");
        setActiveButton(navSettings);
        loadView("/org/example/invenchef/view/settings-view.fxml");
    }


    private void loadView(String resourcePath) {
        if (contentArea == null) {
            LOGGER.warning("contentArea is null — check fx:id in app-view.fxml");
            return;
        }
        try {
            URL resource = getClass().getResource(resourcePath);
            if (resource == null) {
                LOGGER.severe("FXML not found: " + resourcePath);
                showPlaceholder("View missing: " + resourcePath);
                return;
            }
            Parent node = FXMLLoader.load(resource);
            contentArea.getChildren().setAll(node);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading FXML: " + resourcePath, e);
            showPlaceholder("Error loading view");
        }
    }

    private void showPlaceholder(String message) {
        if (contentArea == null) return;
        Label l = new Label(message);
        l.setStyle("-fx-font-size: 20px; -fx-text-fill: #555;");
        StackPane placeholder = new StackPane(l);
        placeholder.setStyle("-fx-padding: 40;");
        contentArea.getChildren().setAll(placeholder);
    }

    private void setActiveButton(Button active) {
        Button[] navButtons = {navDashboard, navInventory, navRecipes, navPlanner};
        for (Button btn : navButtons) {
            btn.setStyle(btn == active ? ACTIVE_STYLE : INACTIVE_STYLE);
        }
        navSettings.setStyle(
            navSettings == active ? ACTIVE_STYLE : INACTIVE_SETTINGS_STYLE
        );
    }
}
