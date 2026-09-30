package org.icesi.implementacionintegradora;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.icesi.implementacionintegradora.controllers.GameState;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        GameState.getInstance();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/icesi/implementacionintegradora/game.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 800);

        primaryStage.setTitle("Simulador de Gestión de Tráfico - Palmira");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}