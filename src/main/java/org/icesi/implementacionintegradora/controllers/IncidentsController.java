package org.icesi.implementacionintegradora.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.icesi.implementacionintegradora.models.Incidente;
import org.icesi.implementacionintegradora.models.Vehiculo;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class IncidentsController implements Initializable {

    @FXML
    private TableView<Incidente> incidentsTable;
    @FXML
    private TableColumn<Incidente, String> typeColumn;
    @FXML
    private TableColumn<Incidente, Integer> priorityColumn;
    @FXML
    private TableColumn<Incidente, String> locationColumn;
    @FXML
    private TableColumn<Incidente, String> statusColumn;

    private VehicleController vehicleController;
    private IncidentController incidentController;
    private ObservableList<Incidente> incidentData;
    private Thread updateThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        startUpdateThread();
        setupKeyboardControls();
    }

    public void initializeWithGameState() {
        GameState gameState = GameState.getInstance();
        this.vehicleController = gameState.getVehicleController();
        this.incidentController = gameState.getIncidentController();
        updateIncidentsList();
    }

    private void setupTable() {
        // Reemplazamos PropertyValueFactory con lambdas para evitar problemas de reflexión
        typeColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipo()));
        priorityColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getPrioridad()).asObject());
        locationColumn.setCellValueFactory(cellData -> {
            Incidente incident = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    String.format("(%.0f, %.0f)", incident.getPosicionX(), incident.getPosicionY())
            );
        });
        statusColumn.setCellValueFactory(cellData -> {
            Incidente incident = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    incident.isResuelto() ? "Resuelto" : "Activo"
            );
        });

        incidentData = FXCollections.observableArrayList();
        incidentsTable.setItems(incidentData);
    }

    // Lo usamos para configurar controles de teclado, para resolver los incidentes
    private void setupKeyboardControls() {
        incidentsTable.setOnKeyPressed(event -> {
            Incidente selectedIncident = incidentsTable.getSelectionModel().getSelectedItem();

            if (selectedIncident != null && !selectedIncident.isResuelto()) {
                switch (event.getCode()) {
                    case DIGIT1:
                        assignVehicleByType(selectedIncident, "Patrulla");
                        break;
                    case DIGIT2:
                        assignVehicleByType(selectedIncident, "Ambulancia");
                        break;
                    case DIGIT3:
                        assignVehicleByType(selectedIncident, "Bombero");
                        break;
                }
            }
        });

        // Asegura que la tabla pueda recibir eventos de teclado
        incidentsTable.setFocusTraversable(true);
    }

    private void assignVehicleByType(Incidente incident, String vehicleType) {
        if (vehicleController == null) return;

        // Buscar vehículo disponible del tipo que se necesita para el incidente
        Vehiculo availableVehicle = null;

        for (Vehiculo vehicle : vehicleController.getAvailableVehicles()) {
            String currentType = vehicle.getClass().getSimpleName();
            if (currentType.equals(vehicleType)) {
                availableVehicle = vehicle;
                break;
            }
        }

        if (availableVehicle != null) {
            // Verificar la compatibilidad del incidente-vehículo
            boolean canHandle = false;
            switch (incident.getTipo()) {
                case "ROBO":
                    canHandle = vehicleType.equals("Patrulla");
                    break;
                case "INCENDIO":
                    canHandle = vehicleType.equals("Bombero");
                    break;
                case "ACCIDENTE":
                    canHandle = vehicleType.equals("Ambulancia") || vehicleType.equals("Patrulla");
                    break;
            }

            if (canHandle) {

                availableVehicle.assignToIncident(incident);


                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Asignación Exitosa");
                alert.setHeaderText("Vehículo Asignado con Teclado");
                alert.setContentText(String.format(" %s asignado al %s\nUbicación: (%.0f, %.0f)\nPuntos: +10",
                        vehicleType,
                        incident.getTipo(),
                        incident.getPosicionX(),
                        incident.getPosicionY()));
                alert.showAndWait();


                GameState.getInstance().addScore(10);

                updateIncidentsList();
            } else {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Vehículo Incompatible");
                alert.setHeaderText("No puede manejar este incidente");
                alert.setContentText(String.format(" %s no puede atender %s\n\nUsa:\n• Tecla 1 (Patrulla) para ROBOS\n• Tecla 2 (Ambulancia) para ACCIDENTES\n• Tecla 3 (Bombero) para INCENDIOS",
                        vehicleType, incident.getTipo()));
                alert.showAndWait();
            }
        } else {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sin Vehículos Disponibles");
            alert.setHeaderText("No hay " + vehicleType + " disponible");
            alert.setContentText(" Todos los vehículos de este tipo están ocupados.\nEspera a que terminen sus misiones.");
            alert.showAndWait();
        }
    }

    private void startUpdateThread() {
        updateThread = new Thread(() -> {
            while (true) {
                Platform.runLater(() -> {
                    updateIncidentsList();
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }

    private void updateIncidentsList() {
        if (incidentController != null) {
            incidentData.clear();
            incidentData.addAll(incidentController.getActiveIncidents());
        }
    }

    @FXML
    private void backToGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/icesi/implementacionintegradora/game.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);
            Stage stage = (Stage) incidentsTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showMonitoring() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/icesi/implementacionintegradora/monitoring.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);

            MonitoringController controller = loader.getController();
            controller.initializeWithGameState();

            Stage stage = (Stage) incidentsTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}