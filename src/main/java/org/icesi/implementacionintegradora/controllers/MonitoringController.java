package org.icesi.implementacionintegradora.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MonitoringController implements Initializable {

    @FXML
    private Label scoreLabel;
    @FXML
    private Label totalIncidentsLabel;
    @FXML
    private Label robberyCountLabel;
    @FXML
    private Label fireCountLabel;
    @FXML
    private Label accidentCountLabel;
    @FXML
    private Label patrolCountLabel;
    @FXML
    private Label ambulanceCountLabel;
    @FXML
    private Label firetruckCountLabel;
    @FXML
    private ProgressBar systemEfficiencyBar;

    private VehicleController vehicleController;
    private IncidentController incidentController;
    private Thread updateThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startUpdateThread();
    }

    public void initializeWithGameState() {
        GameState gameState = GameState.getInstance();
        this.vehicleController = gameState.getVehicleController();
        this.incidentController = gameState.getIncidentController();
        updateDisplay();
    }

    private void startUpdateThread() {
        updateThread = new Thread(() -> {
            while (true) {
                Platform.runLater(this::updateDisplay);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }

    private void updateDisplay() {
        if (vehicleController != null && incidentController != null) {
            GameState gameState = GameState.getInstance();

            scoreLabel.setText("Puntuación: " + gameState.getCurrentScore());
            totalIncidentsLabel.setText("Total: " + incidentController.getIncidentCount());
            robberyCountLabel.setText("Robos: " + incidentController.getIncidentCountByType("ROBO"));
            fireCountLabel.setText("Incendios: " + incidentController.getIncidentCountByType("INCENDIO"));
            accidentCountLabel.setText("Accidentes: " + incidentController.getIncidentCountByType("ACCIDENTE"));

            patrolCountLabel.setText("Patrullas: " + vehicleController.getAvailablePatrols());
            ambulanceCountLabel.setText("Ambulancias: " + vehicleController.getAvailableAmbulances());
            firetruckCountLabel.setText("Bomberos: " + vehicleController.getAvailableFireTrucks());

        }
    }

    @FXML
    private void backToGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/icesi/implementacionintegradora/game.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);
            Stage stage = (Stage) scoreLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showIncidents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/icesi/implementacionintegradora/incidents.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);

            IncidentsController controller = loader.getController();
            controller.initializeWithGameState();

            Stage stage = (Stage) scoreLabel.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
