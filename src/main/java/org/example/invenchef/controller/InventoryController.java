package org.example.invenchef.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.invenchef.model.Fridge;
import org.example.invenchef.model.Ingredients;
import org.example.invenchef.model.InventoryStore;
import org.example.invenchef.model.Perishable;
import org.example.invenchef.model.PantryStaple;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InventoryController {

    @FXML private TextField    ingredientNameField;
    @FXML private TextField    quantityField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> unitCombo;
    @FXML private DatePicker   expiryDatePicker;
    @FXML private Button       addIngredientBtn;
    @FXML private Button       clearFormBtn;

    @FXML private TableView<Ingredients>            inventoryTable;
    @FXML private TableColumn<Ingredients, String>  nameColumn;
    @FXML private TableColumn<Ingredients, String>  qtyColumn;
    @FXML private TableColumn<Ingredients, String>  typeColumn;
    @FXML private TableColumn<Ingredients, String>  expiryColumn;
    @FXML private TableColumn<Ingredients, Void>    deleteColumn;

    @FXML private Button allTab;
    @FXML private Button perishableTab;
    @FXML private Button pantryTab;
    @FXML private Button expiredTab;

    @FXML private Label totalLabel;
    @FXML private Label freshLabel;
    @FXML private Label expiringLabel;
    @FXML private Label expiredLabel;
    @FXML private Label pageIndicator;
    private final Fridge fridge = InventoryStore.getInstance().getFridge();

    private ObservableList<Ingredients> inventoryData;
    private String currentFilter = "ALL";
    private static final int ITEMS_PER_PAGE = 6;
    private int currentPage = 1;

    @FXML
    public void initialize() {
        categoryCombo.setItems(FXCollections.observableArrayList("Perishable", "Pantry Staple"));
        categoryCombo.setValue("Perishable");

        unitCombo.setItems(FXCollections.observableArrayList("Kilogram", "Gram", "Litre", "milliLitre"));
        unitCombo.setValue("Kilogram");

        setupTableColumns();
        setupFilterButtons();

        addIngredientBtn.setOnAction(e -> handleAddIngredient());
        clearFormBtn.setOnAction(e -> handleClearForm());

        loadInventory();
        updateInventoryStats();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cell ->
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));

        qtyColumn.setCellValueFactory(cell -> {
            Ingredients i = cell.getValue();
            return new javafx.beans.property.SimpleStringProperty(i.getQuantity() + " " + i.getUnit());
        });

        typeColumn.setCellValueFactory(cell -> {
            String type = cell.getValue() instanceof Perishable ? "Perish." : "Pantry";
            return new javafx.beans.property.SimpleStringProperty(type);
        });

        expiryColumn.setCellValueFactory(cell -> {
            Ingredients i = cell.getValue();
            if (i instanceof Perishable) {
                return new javafx.beans.property.SimpleStringProperty(
                        getExpiryStatus(((Perishable) i).getExpiryDate()));
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        deleteColumn.setCellValueFactory(param ->
            new javafx.beans.property.SimpleObjectProperty<>(null));

        deleteColumn.setCellFactory(param -> new TableCell<Ingredients, Void>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.setStyle(
                    "-fx-font-size: 12; -fx-padding: 5 10; " +
                    "-fx-background-color: transparent; -fx-cursor: hand; " +
                    "-fx-text-fill: #d62828;");
                deleteBtn.setOnAction(event -> {
                    int row = getIndex();
                    if (row >= 0 && row < getTableView().getItems().size()) {
                        handleDeleteIngredient(getTableView().getItems().get(row));
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

        inventoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void setupFilterButtons() {
        allTab.setOnAction(e        -> filterByCategory("ALL"));
        perishableTab.setOnAction(e -> filterByCategory("PERISHABLE"));
        pantryTab.setOnAction(e     -> filterByCategory("PANTRY"));
        expiredTab.setOnAction(e    -> filterByCategory("EXPIRED"));
    }

    private void handleAddIngredient() {
        String name        = ingredientNameField.getText().trim();
        String quantityStr = quantityField.getText().trim();
        String category    = categoryCombo.getValue();
        String unit        = unitCombo.getValue();
        LocalDate expiry   = expiryDatePicker.getValue();

        if (name.isEmpty() || quantityStr.isEmpty() || unit == null || unit.isBlank()) {
            showAlert("Error", "Please fill in all required fields.", Alert.AlertType.ERROR);
            return;
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            Ingredients ingredient;

            if ("Perishable".equals(category)) {
                if (expiry == null) {
                    showAlert("Error", "Perishable items require an expiry date.", Alert.AlertType.ERROR);
                    return;
                }
                ingredient = new Perishable(name, quantity, unit, expiry);
            } else {
                ingredient = new PantryStaple(name, quantity, unit);
            }

            fridge.addIngredient(ingredient);

            InventoryStore.getInstance().notifyListeners();

            handleClearForm();
            loadInventory();
            updateInventoryStats();
            showAlert("Success", name + " added successfully!", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Error", "Quantity must be a number.", Alert.AlertType.ERROR);
        } catch (IllegalStateException e) {
            showAlert("Error", "Fridge is full! Remove some items first.", Alert.AlertType.ERROR);
        }
    }

    private void handleDeleteIngredient(Ingredients ingredient) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete \"" + ingredient.getName() + "\" from inventory?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            fridge.removeIngredient(ingredient);

            InventoryStore.getInstance().notifyListeners();

            loadInventory();
            updateInventoryStats();
        }
    }

    private void handleClearForm() {
        ingredientNameField.clear();
        quantityField.clear();
        categoryCombo.setValue("Perishable");
        unitCombo.setValue("Kilogram");
        expiryDatePicker.setValue(null);
    }

    private void filterByCategory(String category) {
        currentFilter = category;
        currentPage = 1;
        loadInventory();
        updateFilterButtonStyles();
    }

    private void updateFilterButtonStyles() {
        String active   = "-fx-background-color: #40916c; -fx-text-fill: white;  -fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand;";
        String inactive = "-fx-background-color: #e9ecef; -fx-text-fill: #333;   -fx-background-radius: 8; -fx-cursor: hand;";

        allTab.setStyle(       currentFilter.equals("ALL")        ? active : inactive);
        perishableTab.setStyle(currentFilter.equals("PERISHABLE") ? active : inactive);
        pantryTab.setStyle(    currentFilter.equals("PANTRY")     ? active : inactive);
        expiredTab.setStyle(   currentFilter.equals("EXPIRED")    ? active : inactive);
    }

    private void loadInventory() {
        ObservableList<Ingredients> filtered = FXCollections.observableArrayList();

        for (Ingredients i : fridge.getAllIngredients()) {
            boolean matches = switch (currentFilter) {
                case "PERISHABLE" -> i instanceof Perishable;
                case "PANTRY"     -> i instanceof PantryStaple;
                case "EXPIRED"    -> (i instanceof Perishable)
                        && ((Perishable) i).getExpiryDate().isBefore(LocalDate.now());
                default           -> true; // "ALL"
            };
            if (matches) filtered.add(i);
        }

        inventoryData = filtered;
        inventoryTable.setItems(inventoryData);
        updatePageIndicator();
    }

    private void updateInventoryStats() {
        int total = 0, fresh = 0, expiring = 0, expired = 0;
        LocalDate today       = LocalDate.now();
        LocalDate sevenDays   = today.plusDays(7);

        for (Ingredients i : fridge.getAllIngredients()) {
            total++;
            if (i instanceof Perishable p) {
                LocalDate exp = p.getExpiryDate();
                if      (exp.isBefore(today))      expired++;
                else if (exp.isBefore(sevenDays))  expiring++;
                else                               fresh++;
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
        if      (expiryDate.isBefore(today))         return "Expired";
        else if (expiryDate.isEqual(today))           return "Today";
        else if (expiryDate.isBefore(today.plusDays(7))) return "Soon";
        else    return expiryDate.format(DateTimeFormatter.ofPattern("MMM dd"));
    }

    private void updatePageIndicator() {
        int totalPages = Math.max(1,
            (int) Math.ceil((double) inventoryData.size() / ITEMS_PER_PAGE));
        pageIndicator.setText("Page " + currentPage + " of " + totalPages);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
