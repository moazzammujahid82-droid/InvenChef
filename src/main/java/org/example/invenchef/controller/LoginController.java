package org.example.invenchef.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox      rememberMe;
    @FXML private Label         errorLabel;
    @FXML private Button        loginBtn;
    @FXML private Button        registerBtn;

    @FXML
    public void initialize() {
        // Press Enter in password field = same as clicking Sign In
        passwordField.setOnAction(event -> handleLogin());

        // Hover effect for Sign In button
        loginBtn.setOnMouseEntered(e ->
            loginBtn.setStyle(
                "-fx-background-color: #2E7D52; -fx-text-fill: white; " +
                "-fx-font-size: 14; -fx-font-weight: bold; " +
                "-fx-background-radius: 8; -fx-cursor: hand;"
            )
        );
        loginBtn.setOnMouseExited(e ->
            loginBtn.setStyle(
                "-fx-background-color: #1B5E3B; -fx-text-fill: white; " +
                "-fx-font-size: 14; -fx-font-weight: bold; " +
                "-fx-background-radius: 8; -fx-cursor: hand;"
            )
        );
    }

    // ── Login handler ─────────────────────────────────────────────────────────
    @FXML
    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty()) {
            showError("Please enter your email.");
            return;
        }
        if (!email.contains("@")) {
            showError("Please enter a valid email address.");
            return;
        }
        if (password.isEmpty()) {
            showError("Please enter your password.");
            return;
        }
        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        if (authenticate(email, password)) {
            switchToMainApp();
        } else {
            showError("Incorrect email or password. Please try again.");
            passwordField.clear();
        }
    }

    // ── Authentication ────────────────────────────────────────────────────────
    // TODO: replace with a real database/user-store check later.
    // For now: any email + password ≥ 6 characters is accepted.
    private boolean authenticate(String email, String password) {
        return password.length() >= 6;
    }

    // ── Register handler ──────────────────────────────────────────────────────
    @FXML
    private void handleRegister() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Create Account");
        alert.setHeaderText("Registration");
        alert.setContentText(
            "Registration screen coming soon!\n\n" +
            "For now use any email and a 6+ character password to sign in."
        );
        alert.showAndWait();
    }

    // ── Navigate to main app (app-view.fxml is the shell with sidebar) ────────
    private void switchToMainApp() {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource(
                    "/org/example/invenchef/view/app-view.fxml"
                )
            );

            Stage stage = (Stage) loginBtn.getScene().getWindow();
            // Resize to the main app dimensions and centre on screen
            stage.setScene(new Scene(root, 1100, 700));
            stage.setTitle("InvenChef");
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            showError("Could not load the main screen. Please try again.");
            e.printStackTrace();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void showError(String message) {
        errorLabel.setText("⚠  " + message);
        errorLabel.setVisible(true);
    }
}
