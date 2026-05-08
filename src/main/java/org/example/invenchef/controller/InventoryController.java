package org.example.invenchef.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.invenchef.model.Fridge;
import org.example.invenchef.model.Ingredients;
import org.example.invenchef.model.Perishable;
import org.example.invenchef.model.PantryStaple;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InventoryController {

    // Add Ingredient Section
    @FXML
    private TextField ingredientNameField;
    @FXML
    private TextField quantityField;
    @FXML
    private ComboBox<String> categoryCombo;
    @FXML
    private DatePicker expiryDatePicker;
    @FXML
    private Button addIngredientBtn;
    @FXML
    private Button clearFormBtn;

    // Current Inventory Section
    @FXML
    private TableView<Ingredients> inventoryTable;
    @FXML
    private TableColumn<Ingredients, String> nameColumn;
    @FXML
    private TableColumn<Ingredients, String> qtyColumn;
    @FXML
    private TableColumn<Ingredients, String> typeColumn;
    @FXML
    private TableColumn<Ingredients, String> expiryColumn;
    @FXML
    private TableColumn<Ingredients, Void> deleteColumn;

    // Filter Tabs
    @FXML
    private Button allTab;
    @FXML
    private Button perishableTab;
    @FXML
    private Button pantryTab;
    @FXML
    private Button expiredTab;

    // Stats Section
    @FXML
    private Label totalLabel;
    @FXML
    private Label freshLabel;
    @FXML
    private Label expiringLabel;
    @FXML
    private Label expiredLabel;

    @FXML
    private Label pageIndicator;

    // Backend
    private Fridge fridge;
    private ObservableList<Ingredients> inventoryData;
    private String currentFilter = "ALL";
    private int currentPage = 1;
    private static final int ITEMS_PER_PAGE = 6;

    @FXML
    public void initialize() {
        // Initialize backend
        fridge = new Fridge();

        // Setup category combo
        categoryCombo.setItems(FXCollections.observableArrayList(
                "Perishable",
                "Pantry Staple"
        ));
        categoryCombo.setValue("Perishable");

        // Setup table columns
        setupTableColumns();

        // Setup filter buttons
        setupFilterButtons();

        // Setup event handlers
        addIngredientBtn.setOnAction(e -> handleAddIngredient());
        clearFormBtn.setOnAction(e -> handleClearForm());

        // Load initial data
        loadInventory();

        // Update stats
        updateInventoryStats();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> {
            Ingredients ingredient = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(ingredient.getName());
        });

        qtyColumn.setCellValueFactory(cellData -> {
            Ingredients ingredient = cellData.getValue();
            String quantity = ingredient.getQuantity() + " " + ingredient.getUnit();
            return new javafx.beans.property.SimpleStringProperty(quantity);
        });

        typeColumn.setCellValueFactory(cellData -> {
            Ingredients ingredient = cellData.getValue();
            String type = ingredient instanceof Perishable ? "Perish." : "Pantry";
            return new javafx.beans.property.SimpleStringProperty(type);
        });

        expiryColumn.setCellValueFactory(cellData -> {
            Ingredients ingredient = cellData.getValue();
            if (ingredient instanceof Perishable) {
                Perishable p = (Perishable) ingredient;
                LocalDate expiry = p.getExpiryDate();
                String status = getExpiryStatus(expiry);
                return new javafx.beans.property.SimpleStringProperty(status);
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        deleteColumn.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<Void>(null));
        deleteColumn.setCellFactory(param -> new TableCell<Ingredients, Void>() {
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.setStyle(
                        "-fx-font-size: 12; " +
                        "-fx-padding: 5 10; " +
                        "-fx-background-color: transparent; " +
                        "-fx-cursor: hand;"
                );
                deleteBtn.setOnAction(event -> {
                    int row = getIndex();
                    if (row >= 0 && row < getTableView().getItems().size()) {
                        Ingredients ingredient = getTableView().getItems().get(row);
                        handleDeleteIngredient(ingredient);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
                setStyle("-fx-alignment: CENTER;");
            }
        });

        inventoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupFilterButtons() {
        allTab.setOnAction(e -> filterByCategory("ALL"));
        perishableTab.setOnAction(e -> filterByCategory("PERISHABLE"));
        pantryTab.setOnAction(e -> filterByCategory("PANTRY"));
        expiredTab.setOnAction(e -> filterByCategory("EXPIRED"));
    }

    private void handleAddIngredient() {
        String name = ingredientNameField.getText().trim();
        String quantityStr = quantityField.getText().trim();
        String category = categoryCombo.getValue();
        LocalDate expiryDate = expiryDatePicker.getValue();

        // Validation
        if (name.isEmpty() || quantityStr.isEmpty()) {
            showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
            return;
        }

        try {
            double quantity = Double.parseDouble(quantityStr.split(" ")[0]);
            String unit = quantityStr.contains(" ") ? 
                    quantityStr.substring(quantityStr.indexOf(" ") + 1) : 
                    "units";

            Ingredients ingredient;
            if (category.equals("Perishable")) {
                if (expiryDate == null) {
                    showAlert("Error", "Perishable items require an expiry date", Alert.AlertType.ERROR);
                    return;
                }
                ingredient = new Perishable(name, quantity, unit, expiryDate);
            } else {
                ingredient = new PantryStaple(name, quantity, unit);
            }

            fridge.addIngredient(ingredient);
            handleClearForm();
            loadInventory();
            updateInventoryStats();
            showAlert("Success", "Ingredient added successfully!", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid quantity format", Alert.AlertType.ERROR);
        } catch (IllegalStateException e) {
            showAlert("Error", "Fridge is full! Remove some items.", Alert.AlertType.ERROR);
        }
    }

    private void handleDeleteIngredient(Ingredients ingredient) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Ingredient");
        alert.setContentText("Are you sure you want to delete " + ingredient.getName() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            fridge.removeIngredient(ingredient);
            loadInventory();
            updateInventoryStats();
        }
    }

    private void handleClearForm() {
        ingredientNameField.clear();
        quantityField.clear();
        categoryCombo.setValue("Perishable");
        expiryDatePicker.setValue(null);
    }

    private void filterByCategory(String category) {
        currentFilter = category;
        currentPage = 1;
        loadInventory();
        updateFilterButtonStyles();
    }

    private void updateFilterButtonStyles() {
        String activeStyle = "-fx-background-color: #4a7c5e; -fx-text-fill: white;";
        String inactiveStyle = "-fx-background-color: #e0e0e0; -fx-text-fill: #333;";

        allTab.setStyle(currentFilter.equals("ALL") ? activeStyle : inactiveStyle);
        perishableTab.setStyle(currentFilter.equals("PERISHABLE") ? activeStyle : inactiveStyle);
        pantryTab.setStyle(currentFilter.equals("PANTRY") ? activeStyle : inactiveStyle);
        expiredTab.setStyle(currentFilter.equals("EXPIRED") ? activeStyle : inactiveStyle);
    }

    private void loadInventory() {
        ObservableList<Ingredients> filteredData = FXCollections.observableArrayList();

        for (Ingredients ingredient : fridge.getAllIngredients()) {
            boolean matches = false;

            switch (currentFilter) {
                case "ALL":
                    matches = true;
                    break;
                case "PERISHABLE":
                    matches = ingredient instanceof Perishable;
                    break;
                case "PANTRY":
                    matches = ingredient instanceof PantryStaple;
                    break;
                case "EXPIRED":
                    if (ingredient instanceof Perishable) {
                        Perishable p = (Perishable) ingredient;
                        matches = p.getExpiryDate().isBefore(LocalDate.now());
                    }
                    break;
            }

            if (matches) {
                filteredData.add(ingredient);
            }
        }

        inventoryData = filteredData;
        inventoryTable.setItems(inventoryData);
        updatePageIndicator();
    }

    private void updateInventoryStats() {
        int total = fridge.getAllIngredients().size();
        int fresh = 0;
        int expiring = 0;
        int expired = 0;

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);

        for (Ingredients ingredient : fridge.getAllIngredients()) {
            if (ingredient instanceof Perishable) {
                Perishable p = (Perishable) ingredient;
                LocalDate expiry = p.getExpiryDate();

                if (expiry.isBefore(today)) {
                    expired++;
                } else if (expiry.isBefore(sevenDaysLater) || expiry.isEqual(today)) {
                    expiring++;
                } else {
                    fresh++;
                }
            } else {
                fresh++;
            }
        }

        totalLabel.setText(String.valueOf(total));
        freshLabel.setText(String.valueOf(fresh));
        expiringLabel.setText(String.valueOf(expiring));
        expiredLabel.setText(String.valueOf(expired));
    }

    private String getExpiryStatus(LocalDate expiryDate) {
        LocalDate today = LocalDate.now();

        if (expiryDate.isBefore(today)) {
            return "Expired";
        } else if (expiryDate.isEqual(today)) {
            return "Today";
        } else if (expiryDate.isBefore(today.plusDays(7))) {
            return "Soon";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
            return expiryDate.format(formatter);
        }
    }

    private void updatePageIndicator() {
        int totalPages = (int) Math.ceil((double) inventoryData.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        pageIndicator.setText("Screen " + currentPage + " of " + totalPages);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
