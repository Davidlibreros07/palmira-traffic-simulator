package org.icesi.implementacionintegradora.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML private Canvas mapCanvas;
    @FXML private Label scoreLabel;
    @FXML private Label vehicleCountLabel;

    private VehicleController vehicleController;
    private IncidentController incidentController;

    private double cameraX = 0;
    private double cameraY = 0;
    private final double CAMERA_SPEED = 5.0;

    private Image mapImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            mapImage = new Image(getClass().getResourceAsStream("/images/mapa.png"));
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen del mapa, usando mapa por defecto");
        }

        // Usar el singleton GameState
        GameState gameState = GameState.getInstance();
        vehicleController = gameState.getVehicleController();
        incidentController = gameState.getIncidentController();

        // Inicializar ThreadController solo una vez
        gameState.initThreadController(this);

        setupCanvas();
        startGameLoop();

        // Focus para capturar eventos de teclado
        mapCanvas.setFocusTraversable(true);
        mapCanvas.requestFocus();
    }

    private void setupCanvas() {
        mapCanvas.setOnKeyPressed(this::handleKeyPress);
        drawMap();
    }

    private void startGameLoop() {
        Thread gameLoop = new Thread(() -> {
            while (true) {
                Platform.runLater(this::updateGame);
                try {
                    Thread.sleep(50); // 20 FPS
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        gameLoop.setDaemon(true);
        gameLoop.start();
    }

    private void updateGame() {
        drawMap();
        updateUI();
    }

    private void drawMap() {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());

        // Dibujar mapa base
        if (mapImage != null) {
            gc.drawImage(mapImage, -cameraX, -cameraY, 1000, 600);
        } else {
            // Mapa por defecto si no se carga la imagen
            drawDefaultMap(gc);
        }

        // Dibujar vehículos
        vehicleController.drawVehicles(gc, cameraX, cameraY);

        // Dibujar incidentes
        incidentController.drawIncidents(gc, cameraX, cameraY);
    }

    private void drawDefaultMap(GraphicsContext gc) {
        // Fondo verde (zona residencial)
        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(-cameraX, -cameraY, 1000, 600);

        // Calles principales (grises)
        gc.setFill(Color.GRAY);
        gc.fillRect(-cameraX + 100, -cameraY, 800, 50);
        gc.fillRect(-cameraX + 100, -cameraY + 200, 800, 50);
        gc.fillRect(-cameraX + 100, -cameraY + 400, 800, 50);
        gc.fillRect(-cameraX, -cameraY + 100, 50, 400);
        gc.fillRect(-cameraX + 300, -cameraY + 100, 50, 400);
        gc.fillRect(-cameraX + 600, -cameraY + 100, 50, 400);

        // Zona comercial (azul claro)
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(-cameraX + 400, -cameraY + 100, 200, 100);

        // Bordes de zonas
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(-cameraX + 400, -cameraY + 100, 200, 100);
    }

    private void updateUI() {
        GameState gameState = GameState.getInstance();
        scoreLabel.setText("Puntuación: " + gameState.getCurrentScore());

        // Mostrar información más detallada de vehículos particulares
        String routeStats = vehicleController.getParticularRoutesStats();
        String velocityStats = vehicleController.getVelocityStats();
        vehicleCountLabel.setText(String.format("Emergencia - P: %d, A: %d, B: %d | Civiles: %d (%s) [%s]",
                vehicleController.getAvailablePatrols(),
                vehicleController.getAvailableAmbulances(),
                vehicleController.getAvailableFireTrucks(),
                vehicleController.getParticularCount(),
                routeStats,
                velocityStats));
    }

    @FXML
    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case W:
                cameraY -= CAMERA_SPEED;
                break;
            case S:
                cameraY += CAMERA_SPEED;
                break;
            case A:
                cameraX -= CAMERA_SPEED;
                break;
            case D:
                cameraX += CAMERA_SPEED;
                break;
        }

        // Limitar cámara
        cameraX = Math.max(-200, Math.min(200, cameraX));
        cameraY = Math.max(-200, Math.min(200, cameraY));

        mapCanvas.requestFocus();
    }

    @FXML
    private void showMonitoring() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/icesi/implementacionintegradora/monitoring.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);

            MonitoringController controller = loader.getController();
            controller.initializeWithGameState();

            Stage stage = (Stage) mapCanvas.getScene().getWindow();
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

            Stage stage = (Stage) mapCanvas.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showManual() {
        try {
            // Crear nueva ventana para el manual
            Stage manualStage = new Stage();
            manualStage.setTitle("Manual de Usuario - SGMMS");
            manualStage.initModality(Modality.APPLICATION_MODAL);


            Image manualImage = new Image(getClass().getResourceAsStream("/images/manual_usuario.png"));


            ImageView imageView = new ImageView(manualImage);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(800);
            imageView.setFitHeight(600);


            ScrollPane scrollPane = new ScrollPane(imageView);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPannable(true);

            // Crear la escena
            Scene manualScene = new Scene(scrollPane, 820, 620);
            manualStage.setScene(manualScene);


            manualStage.setResizable(true);
            manualStage.centerOnScreen();


            manualStage.show();

            System.out.println(" Manual de usuario mostrado correctamente");

        } catch (Exception e) {
            System.err.println(" Error al cargar el manual de usuario: " + e.getMessage());
            e.printStackTrace();


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Manual de Usuario");
            alert.setHeaderText("Error al cargar imagen del manual");
            alert.setContentText("No se pudo cargar la imagen del manual de usuario. Verifique que el archivo 'manual_usuario.png' esté en la carpeta /resources/images/");
            alert.showAndWait();
        }
    }

    
}


