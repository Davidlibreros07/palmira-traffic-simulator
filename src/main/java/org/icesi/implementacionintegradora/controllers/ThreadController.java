package org.icesi.implementacionintegradora.controllers;

import javafx.application.Platform;

public class ThreadController {

    private VehicleController vehicleController;
    private IncidentController incidentController;
    private GameController gameController;
    private Thread incidentGeneratorThread;
    private Thread accidentDetectorThread;

    public ThreadController(VehicleController vehicleController,
                            IncidentController incidentController,
                            GameController gameController) {
        this.vehicleController = vehicleController;
        this.incidentController = incidentController;
        this.gameController = gameController;
    }

    public void startAllThreads() {
        startVehicleThreads();
        startIncidentGeneratorThread();
        startAccidentDetectorThread();
    }

    private void startVehicleThreads() {
        vehicleController.startAllVehicles();
    }

    private void startIncidentGeneratorThread() {
        incidentGeneratorThread = new Thread(() -> {
            while (true) {
                try {
                    incidentController.generateRandomIncidents();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        incidentGeneratorThread.setDaemon(true);
        incidentGeneratorThread.start();
    }


    private void startAccidentDetectorThread() {
        accidentDetectorThread = new Thread(() -> {
            while (true) {
                try {
                    if (vehicleController.checkForAccidents()) {

                        double[] collisionLocation = vehicleController.getLastCollisionLocation();
                        String collisionInfo = vehicleController.getLastCollisionInfo();

                        Platform.runLater(() -> {

                            incidentController.generateAccident(collisionLocation[0], collisionLocation[1]);


                            System.out.println(" ACCIDENTE GENERADO: " + collisionInfo);
                            System.out.println(" Ubicación: (" + collisionLocation[0] + ", " + collisionLocation[1] + ")");
                        });

                        Thread.sleep(3000); // Espera 3 segundos antes de detectar otro accidente
                    }
                    Thread.sleep(1500); // Verificar cada 1.5 segundos (más frecuente)
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        accidentDetectorThread.setDaemon(true);
        accidentDetectorThread.start();
    }

}