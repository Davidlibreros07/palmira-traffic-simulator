package org.icesi.implementacionintegradora.controllers;

/**
 * Clase Singleton para mantener el estado global del juego
 * y compartir controladores entre escenas
 */
public class GameState {
    private static GameState instance;

    private VehicleController vehicleController;
    private IncidentController incidentController;
    private ThreadController threadController;
    private int currentScore = 0;

    // Constructor privado (patrón Singleton)
    private GameState() {
        vehicleController = new VehicleController();
        incidentController = new IncidentController();
    }

    // Metodo para obtener la única instancia
    public static synchronized GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    // Inicializar ThreadController (solo se hace una vez)
    public void initThreadController(GameController gameController) {
        if (threadController == null) {
            threadController = new ThreadController(vehicleController, incidentController, gameController);
            threadController.startAllThreads();
        }
    }


    public VehicleController getVehicleController() {
        return vehicleController;
    }

    public IncidentController getIncidentController() {
        return incidentController;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void addScore(int points) {
        currentScore += points;
    }
}