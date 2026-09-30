package org.icesi.implementacionintegradora.controllers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import org.icesi.implementacionintegradora.models.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class VehicleController {

    private List<Vehiculo> vehicles;
    private Vehiculo lastCollisionVehicle1;
    private Vehiculo lastCollisionVehicle2;
    private Map<String, Long> collisionCooldowns;
    private final long COLLISION_COOLDOWN_MS = 2000;
    private Map<String, Image> vehicleImages;

    public VehicleController() {
        vehicles = new CopyOnWriteArrayList<>();
        collisionCooldowns = new HashMap<>();
        initializeVehicles();
        loadVehicleImages();
    }

    private void loadVehicleImages() {
        vehicleImages = new HashMap<>();
        try {

            vehicleImages.put("patrulla", new Image(getClass().getResourceAsStream("/images/patrulla.png")));
            vehicleImages.put("ambulancia", new Image(getClass().getResourceAsStream("/images/ambulancia.png")));
            vehicleImages.put("bombero", new Image(getClass().getResourceAsStream("/images/bombero.png")));


            vehicleImages.put("particular_rutaA", new Image(getClass().getResourceAsStream("/images/particular_trabajo.png")));
            vehicleImages.put("particular_rutaB", new Image(getClass().getResourceAsStream("/images/particular_perimetral.png")));

            // Imagen por defecto
            vehicleImages.put("particular_default", new Image(getClass().getResourceAsStream("/images/particular_default.png")));

            System.out.println("Imágenes de vehículos cargadas correctamente");
            System.out.println("   - 3 tipos de vehículos de emergencia");
            System.out.println("   - 2 tipos de vehículos particulares (Ruta A y B)");
        } catch (Exception e) {
            System.err.println(" Error al cargar imágenes de vehículos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeVehicles() {

        for (int i = 0; i < 3; i++) {
            Patrulla patrulla = new Patrulla(100 + i * 150, 100 + i * 100);
            vehicles.add(patrulla);
        }


        for (int i = 0; i < 2; i++) {
            Ambulancia ambulancia = new Ambulancia(200 + i * 200, 150 + i * 150);
            vehicles.add(ambulancia);
        }

        // Crear bomberos
        for (int i = 0; i < 2; i++) {
            Bombero bombero = new Bombero(300 + i * 250, 200 + i * 100);
            vehicles.add(bombero);
        }

        for (int i = 0; i < 5; i++) {
            Particular particular = new Particular(0, 0);
            vehicles.add(particular);
        }

        System.out.println("Total de vehículos creados: " + vehicles.size());
        System.out.println("   - Patrullas: 3, Ambulancias: 2, Bomberos: 2");
        System.out.println("   - Particulares: 5 (alternando entre Ruta A y Ruta B)");
    }


    private String generateCollisionKey(Vehiculo v1, Vehiculo v2) {
        // crea una clave única ordenando por hashCode para evitar duplicados (A-B vs B-A)
        int hash1 = System.identityHashCode(v1);
        int hash2 = System.identityHashCode(v2);

        if (hash1 < hash2) {
            return hash1 + "-" + hash2;
        } else {
            return hash2 + "-" + hash1;
        }
    }


    private boolean isCollisionInCooldown(Vehiculo v1, Vehiculo v2) {
        String collisionKey = generateCollisionKey(v1, v2);
        Long lastCollisionTime = collisionCooldowns.get(collisionKey);

        if (lastCollisionTime == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        long timeSinceLastCollision = currentTime - lastCollisionTime;

        return timeSinceLastCollision < COLLISION_COOLDOWN_MS;
    }


    private void registerCollision(Vehiculo v1, Vehiculo v2) {
        String collisionKey = generateCollisionKey(v1, v2);
        collisionCooldowns.put(collisionKey, System.currentTimeMillis());

        // se limpia cooldowns antiguos para evitar que el mapa crezca demasiado
        cleanupOldCooldowns();
    }


    private void cleanupOldCooldowns() {
        long currentTime = System.currentTimeMillis();
        collisionCooldowns.entrySet().removeIf(entry ->
                currentTime - entry.getValue() > COLLISION_COOLDOWN_MS * 2); // se limpian después de 4 segundos
    }

    public void startAllVehicles() {
        for (Vehiculo vehicle : vehicles) {
            if (!vehicle.isAlive()) {
                vehicle.start();
            }
        }
    }

    public void drawVehicles(GraphicsContext gc, double cameraX, double cameraY) {
        for (Vehiculo vehicle : vehicles) {
            double x = vehicle.getPosicionX() - cameraX;
            double y = vehicle.getPosicionY() - cameraY;

            // Solo dibujar si está en pantalla
            if (x >= -30 && x <= 1000 && y >= -30 && y <= 600) {

                double width = 30;
                double height = 30;

                Image vehicleImage = null;

                if (vehicle instanceof Patrulla) {
                    vehicleImage = vehicleImages.get("patrulla");
                } else if (vehicle instanceof Ambulancia) {
                    vehicleImage = vehicleImages.get("ambulancia");
                } else if (vehicle instanceof Bombero) {
                    vehicleImage = vehicleImages.get("bombero");
                } else if (vehicle instanceof Particular) {
                    Particular particular = (Particular) vehicle;


                    if (particular.getTipoRutaNumero() == 0) {
                        vehicleImage = vehicleImages.get("particular_rutaA");
                    } else {
                        vehicleImage = vehicleImages.get("particular_rutaB");
                    }


                    if (vehicleImage == null) {
                        vehicleImage = vehicleImages.get("particular_default");
                    }
                }


                if (vehicleImage != null) {
                    gc.drawImage(vehicleImage, x - width/2, y - height/2, width, height);

                    // indicador de estado para vehículos de emergencia
                    if (!(vehicle instanceof Particular) && !vehicle.isDisponible()) {
                        gc.setStroke(Color.YELLOW);
                        gc.setLineWidth(2);
                        gc.strokeOval(x - width/2 - 2, y - height/2 - 2, width + 4, height + 4);
                    }
                } else {

                    drawFallbackVehicle(gc, x, y, vehicle);
                }
            }
        }
    }

    private void drawFallbackVehicle(GraphicsContext gc, double x, double y, Vehiculo vehicle) {
        if (vehicle instanceof Patrulla) {
            gc.setFill(Color.BLUE);
            gc.fillOval(x, y, 15, 15);
            gc.setFill(Color.WHITE);
            gc.fillText("P", x + 5, y + 10);
        } else if (vehicle instanceof Ambulancia) {
            gc.setFill(Color.WHITE);
            gc.fillOval(x, y, 15, 15);
            gc.setFill(Color.RED);
            gc.fillText("A", x + 5, y + 10);
        } else if (vehicle instanceof Bombero) {
            gc.setFill(Color.RED);
            gc.fillOval(x, y, 15, 15);
            gc.setFill(Color.WHITE);
            gc.fillText("B", x + 5, y + 10);
        } else if (vehicle instanceof Particular) {
            Particular particular = (Particular) vehicle;


            if (particular.getTipoRutaNumero() == 0) {
                gc.setFill(Color.LIGHTGRAY); // Ruta A
            } else {
                gc.setFill(Color.DARKGRAY);  // Ruta B
            }

            gc.fillOval(x, y, 12, 12);
            gc.setFill(Color.BLACK);
            gc.fillText(particular.getTipoRutaNumero() == 0 ? "A" : "B", x + 4, y + 8);
        }
    }



    public int getAvailablePatrols() {
        return (int) vehicles.stream()
                .filter(v -> v instanceof Patrulla && v.isDisponible())
                .count();
    }

    public int getAvailableAmbulances() {
        return (int) vehicles.stream()
                .filter(v -> v instanceof Ambulancia && v.isDisponible())
                .count();
    }

    public int getAvailableFireTrucks() {
        return (int) vehicles.stream()
                .filter(v -> v instanceof Bombero && v.isDisponible())
                .count();
    }

    public int getParticularCount() {
        return (int) vehicles.stream()
                .filter(v -> v instanceof Particular)
                .count();
    }

    public String getParticularRoutesStats() {
        List<Vehiculo> particulares = getParticularVehicles();
        int rutaA = 0, rutaB = 0;

        for (Vehiculo v : particulares) {
            if (v instanceof Particular) {
                Particular p = (Particular) v;
                if (p.getTipoRutaNumero() == 0) {
                    rutaA++;
                } else {
                    rutaB++;
                }
            }
        }

        return String.format("A:%d B:%d", rutaA, rutaB);
    }

    public String getVelocityStats() {
        List<Vehiculo> particulares = getParticularVehicles();
        int velocidad18 = 0, otros = 0;

        for (Vehiculo v : particulares) {
            if (v instanceof Particular) {
                Particular p = (Particular) v;
                if (Math.abs(p.getVelocidad() - 1.8) <= 0.1) {
                    velocidad18++;
                } else {
                    otros++;
                }
            }
        }

        return String.format("Vel1.8:%d Otros:%d", velocidad18, otros);
    }

    public List<Vehiculo> getAvailableVehicles() {
        return vehicles.stream()
                .filter(v -> v.isDisponible() && !(v instanceof Particular))
                .toList();
    }


    public List<Vehiculo> getParticularVehicles() {
        return vehicles.stream()
                .filter(v -> v instanceof Particular)
                .toList();
    }


    public boolean checkForAccidents() {
        lastCollisionVehicle1 = null;
        lastCollisionVehicle2 = null;

        for (int i = 0; i < vehicles.size(); i++) {
            for (int j = i + 1; j < vehicles.size(); j++) {
                Vehiculo v1 = vehicles.get(i);
                Vehiculo v2 = vehicles.get(j);


                if (isCollisionInCooldown(v1, v2)) {
                    continue; // Salta esta verificación si está en cooldown
                }

                double distance = Math.sqrt(
                        Math.pow(v1.getPosicionX() - v2.getPosicionX(), 2) +
                                Math.pow(v1.getPosicionY() - v2.getPosicionY(), 2)
                );

                // CRITERIO 1: Bombero choca con Particular
                if ((v1 instanceof Bombero && v2 instanceof Particular) ||
                        (v1 instanceof Particular && v2 instanceof Bombero)) {

                    if (distance < 16 && v1.getVelocidad() > 1.0 && v2.getVelocidad() > 1.0) {
                        lastCollisionVehicle1 = v1;
                        lastCollisionVehicle2 = v2;

                        // se registra la colisión para activar cooldown
                        registerCollision(v1, v2);

                        Bombero bombero = (v1 instanceof Bombero) ? (Bombero) v1 : (Bombero) v2;
                        Particular particular = (v1 instanceof Particular) ? (Particular) v1 : (Particular) v2;

                        System.out.println(" ACCIDENTE: Colisión entre Bombero y Particular!");
                        System.out.println("Bombero vs Particular #" + particular.getParticularId() + " (Ruta " + particular.getTipoRuta() + ")");
                        System.out.println(" Cooldown activado por " + COLLISION_COOLDOWN_MS + "ms para este par de vehículos");
                        return true;
                    }
                }

                // CRITERIO 2: Dos bomberos se choquen
                if (v1 instanceof Bombero && v2 instanceof Bombero) {
                    if (distance < 18 && v1.getVelocidad() > 2.0 && v2.getVelocidad() > 2.0) {
                        lastCollisionVehicle1 = v1;
                        lastCollisionVehicle2 = v2;

                        // NUEVO: Registrar la colisión para activar cooldown
                        registerCollision(v1, v2);

                        System.out.println(" ACCIDENTE: Colisión entre dos bomberos!");
                        System.out.println(" Cooldown activado por " + COLLISION_COOLDOWN_MS + "ms para este par de vehículos");
                        return true;
                    }
                }

                // CRITERIO 3: Particular con velocidad 4 choca con Patrulla
                if ((v1 instanceof Particular && v2 instanceof Patrulla) ||
                        (v1 instanceof Patrulla && v2 instanceof Particular)) {

                    Particular particular = (v1 instanceof Particular) ? (Particular) v1 : (Particular) v2;
                    Patrulla patrulla = (v1 instanceof Patrulla) ? (Patrulla) v1 : (Patrulla) v2;

                    boolean particularVelocidadCorrecta = Math.abs(particular.getVelocidad() - 4) <= 0.1;

                    if (distance < 16 && particularVelocidadCorrecta && patrulla.getVelocidad() > 1.5) {
                        lastCollisionVehicle1 = v1;
                        lastCollisionVehicle2 = v2;


                        registerCollision(v1, v2);

                        System.out.println("ACCIDENTE: Particular (velocidad 1.8) choca con Patrulla!");
                        System.out.println("Particular #" + particular.getParticularId() + " (Ruta " + particular.getTipoRuta() + ") vs Patrulla");
                        System.out.println(" Cooldown activado por " + COLLISION_COOLDOWN_MS + "ms para este par de vehículos");
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public double[] getLastCollisionLocation() {
        if (lastCollisionVehicle1 != null && lastCollisionVehicle2 != null) {
            double avgX = (lastCollisionVehicle1.getPosicionX() + lastCollisionVehicle2.getPosicionX()) / 2;
            double avgY = (lastCollisionVehicle1.getPosicionY() + lastCollisionVehicle2.getPosicionY()) / 2;
            return new double[]{avgX, avgY};
        }
        return new double[]{400, 300};
    }

    public String getLastCollisionInfo() {
        if (lastCollisionVehicle1 != null && lastCollisionVehicle2 != null) {
            // Criterio 1: Bombero vs Particular
            if ((lastCollisionVehicle1 instanceof Bombero && lastCollisionVehicle2 instanceof Particular) ||
                    (lastCollisionVehicle1 instanceof Particular && lastCollisionVehicle2 instanceof Bombero)) {

                Bombero bombero = (lastCollisionVehicle1 instanceof Bombero) ? (Bombero) lastCollisionVehicle1 : (Bombero) lastCollisionVehicle2;
                Particular particular = (lastCollisionVehicle1 instanceof Particular) ? (Particular) lastCollisionVehicle1 : (Particular) lastCollisionVehicle2;

                return String.format("Colisión Bombero vs P#%d (Ruta%s)",
                        particular.getParticularId(), particular.getTipoRuta());
            } else if (lastCollisionVehicle1 instanceof Bombero && lastCollisionVehicle2 instanceof Bombero) {
                return "Colisión entre bomberos";
            } else if ((lastCollisionVehicle1 instanceof Particular && lastCollisionVehicle2 instanceof Patrulla) ||
                    (lastCollisionVehicle1 instanceof Patrulla && lastCollisionVehicle2 instanceof Particular)) {
                Particular particular = (lastCollisionVehicle1 instanceof Particular) ?
                        (Particular) lastCollisionVehicle1 : (Particular) lastCollisionVehicle2;
                return String.format("Colisión P#%d (Ruta%s, vel: %.1f) vs Patrulla",
                        particular.getParticularId(), particular.getTipoRuta(), particular.getVelocidad());
            }
        }
        return "Colisión desconocida";
    }

}