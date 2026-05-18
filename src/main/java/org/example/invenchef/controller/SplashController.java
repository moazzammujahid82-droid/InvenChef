package org.example.invenchef.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashController {

    @FXML private ProgressBar loadingBar;
    @FXML private Label       loadingLabel;

    @FXML
    public void initialize() {
        startLoadingAnimation();
    }

    // Animates progress bar 0 → 100% over 3 seconds, then goes to Login
    private void startLoadingAnimation() {
        Timeline timeline = new Timeline(

            new KeyFrame(Duration.seconds(1),
                e -> loadingLabel.setText("Initializing..."),
                new KeyValue(loadingBar.progressProperty(), 0.33)
            ),

            new KeyFrame(Duration.seconds(2),
                e -> loadingLabel.setText("Loading data..."),
                new KeyValue(loadingBar.progressProperty(), 0.66)
            ),

            new KeyFrame(Duration.seconds(3),
                e -> {
                    loadingLabel.setText("Ready!");
                    loadingBar.setProgress(1.0);
                    switchToLogin();
                },
                new KeyValue(loadingBar.progressProperty(), 1.0)
            )
        );

        timeline.play();
    }

    private void switchToLogin() {
        try {
            // Path matches Login.fxml location inside resources
            Parent root = FXMLLoader.load(
                getClass().getResource(
                    "/org/example/invenchef/view/Login.fxml"
                )
            );

            Stage stage = (Stage) loadingBar.getScene().getWindow();
            stage.setScene(new Scene(root, 950, 550));
            stage.setTitle("InvenChef — Login");
            stage.show();

        } catch (Exception e) {
            System.err.println("[SplashController] Could not load Login.fxml");
            e.printStackTrace();
        }
    }
}
