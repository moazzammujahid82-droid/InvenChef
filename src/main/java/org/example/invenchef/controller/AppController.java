package org.example.invenchef.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import java.io.IOException;
import java.util.Objects;

public class AppController {

    @FXML
    private Button navDashBoard;
    @FXML
    private Button navPlanner;
    @FXML
    private Button navSettings;
    @FXML
    private StackPane contentArea;
    @FXML
    private Button navInventory;
    @FXML
    private Button navRecipes;

    private final String IDLE_COLOR = "#2D6A4F";
    private final String HOVER_COLOR = "#1A3C2F";

    @FXML
    public void initialize() {
        setupHoverEffect(navInventory);
        setupHoverEffect(navRecipes);
        setupHoverEffect(navDashBoard);
        setupHoverEffect(navPlanner);
        setupHoverEffect(navSettings);
    }

    @FXML
    private void showInventory() {
        loadView("/org/example/invenchef/view/inventory-view.fxml");
    }

    @FXML
    private void showRecipes() {
        loadView("/org/example/invenchef/view/recipes.fxml");
    }

    @FXML
    private void showDashBoard(){
        loadView("/org/example/invenchef/view/dashboard.fxml");
    }

    @FXML
    private void showPlanner(){
        loadView("/org/example/invenchef/view/planner.fxml");
    }

    @FXML
    private void showSettings(){
        loadView("/org/example/invenchef/view/settings.fxml");
    }




    private void loadView(String fxmlPath) {
        if (contentArea == null) {
            return;
        }

        try {
            Parent view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath), "Missing FXML: " + fxmlPath));
            contentArea.getChildren().setAll(view);
        } catch (IOException | NullPointerException e) {
            System.err.println("Error loading FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void setupHoverEffect(Button btn) {
        if (btn == null) {
            return;
        }

        btn.setStyle("-fx-background-color: " + IDLE_COLOR + "; -fx-text-fill: white;");

        btn.setOnMouseEntered(e ->
                btn.setStyle("-fx-background-color: " + HOVER_COLOR + "; -fx-text-fill: white; -fx-cursor: hand;")
        );

        btn.setOnMouseExited(e ->
                btn.setStyle("-fx-background-color: " + IDLE_COLOR + "; -fx-text-fill: white;")
        );
    }
}
