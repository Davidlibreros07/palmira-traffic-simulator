package org.icesi.implementacionintegradora.controllers;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.icesi.implementacionintegradora.models.BinarySearchTree;
import org.icesi.implementacionintegradora.models.Incidente;


import java.util.List;
import java.util.Random;

public class IncidentController {

    private BinarySearchTree<Incidente> activeIncidents;
    private Random random;
    private long lastIncidentTime;
    private final long INCIDENT_INTERVAL = 30000; // 30 segundos
    private final int MAX_ACTIVE_INCIDENTS = 3; // Máximo 3 incidentes activos a la vez

    public IncidentController() {
        activeIncidents = new BinarySearchTree<>();
        random = new Random();
        lastIncidentTime = System.currentTimeMillis();
    }

    public void generateRandomIncidents() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastIncident = currentTime - lastIncidentTime;

        List<Incidente> active = getActiveIncidents();

        if (timeSinceLastIncident > INCIDENT_INTERVAL && active.size() < MAX_ACTIVE_INCIDENTS) {
            if (random.nextBoolean()) {
                generateRobbery();
            } else {
                generateFire();
            }
            lastIncidentTime = currentTime;
        }
    }


    public void generateAccident(double x, double y) {

        int prioridad = random.nextInt(2) + 1; // Prioridad 1 o 2 (muy alta)

        Incidente accident = new Incidente(
                "ACCIDENTE",
                x + (random.nextDouble() - 0.5) * 10, // Variación pequeña de mas o menos 5 píxeles
                y + (random.nextDouble() - 0.5) * 10,
                prioridad
        );

        activeIncidents.insert(accident);

        System.out.println(" ACCIDENTE CREADO:");
        System.out.println("   Ubicación: (" + accident.getPosicionX() + ", " + accident.getPosicionY() + ")");
        System.out.println("   Prioridad: " + accident.getPrioridad());
    }

    private void generateRobbery() {
        Incidente robbery = new Incidente(
                "ROBO",
                random.nextInt(800) + 100,
                random.nextInt(400) + 100,
                random.nextInt(3) + 4
        );
        activeIncidents.insert(robbery);
    }

    private void generateFire() {
        Incidente fire = new Incidente(
                "INCENDIO",
                random.nextInt(800) + 100,
                random.nextInt(400) + 100,
                random.nextInt(2) + 1
        );
        activeIncidents.insert(fire);
    }

    public void drawIncidents(GraphicsContext gc, double cameraX, double cameraY) {
        List<Incidente> incidents = activeIncidents.inOrderTraversal();

        for (Incidente incident : incidents) {
            if (!incident.isResuelto()) {
                double x = incident.getPosicionX() - cameraX;
                double y = incident.getPosicionY() - cameraY;

                if (x >= -20 && x <= 1000 && y >= -20 && y <= 600) {
                    switch (incident.getTipo()) {
                        case "ROBO":
                            gc.setFill(Color.ORANGE);
                            gc.fillRect(x, y, 20, 20);
                            gc.setFill(Color.BLACK);
                            gc.fillText("R", x + 7, y + 13);
                            break;
                        case "INCENDIO":
                            gc.setFill(Color.RED);
                            gc.fillRect(x, y, 20, 20);
                            gc.setFill(Color.WHITE);
                            gc.fillText("F", x + 7, y + 13);
                            break;
                        case "ACCIDENTE":

                            gc.setFill(Color.YELLOW);
                            gc.fillRect(x, y, 20, 20);
                            gc.setFill(Color.BLACK);
                            gc.fillText("A", x + 7, y + 13);

                            // Agrega un efecto que titila para accidentes
                            long time = System.currentTimeMillis();
                            if ((time / 500) % 2 == 0) { // Parpadeo cada 500ms
                                gc.setStroke(Color.RED);
                                gc.setLineWidth(3);
                                gc.strokeRect(x - 3, y - 3, 26, 26);
                            }
                            break;
                    }

                    // Borde según prioridad
                    gc.setStroke(incident.getPrioridad() <= 3 ? Color.RED : Color.ORANGE);
                    gc.setLineWidth(2);
                    gc.strokeRect(x - 2, y - 2, 24, 24);
                }
            }
        }
    }


    public List<Incidente> getActiveIncidents() {
        List<Incidente> allIncidents = activeIncidents.inOrderTraversal();
        return allIncidents.stream()
                .filter(i -> !i.isResuelto())
                .toList();
    }

    public int getIncidentCount() {
        return getActiveIncidents().size();
    }

    public int getIncidentCountByType(String type) {
        return (int) getActiveIncidents().stream()
                .filter(i -> i.getTipo().equals(type))
                .count();
    }

}

