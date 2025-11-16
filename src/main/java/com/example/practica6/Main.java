package com.example.practica6;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {

        VBox menu = new VBox(20);
        menu.setStyle("-fx-alignment: center; -fx-background-color: black;");

        Button btnStart = new Button("Iniciar Juego");
        btnStart.setStyle("-fx-font-size: 24px; -fx-padding: 10;");

        Button btnSalir = new Button("Salir");
        btnSalir.setStyle("-fx-font-size: 18px; -fx-padding: 8;");

        menu.getChildren().addAll(btnStart, btnSalir);

        Scene menuScene = new Scene(new StackPane(menu), 800, 600);

        Game game = new Game(800, 600);
        StackPane root = new StackPane(game.getCanvas());
        Scene gameScene = new Scene(root, 800, 600);
        game.setupInput(gameScene);

        btnStart.setOnAction(e -> {
            primaryStage.setScene(gameScene);
            game.start();
        });

        btnSalir.setOnAction(e -> primaryStage.close());

        primaryStage.setTitle("Juego Plataforma 2D - JavaFX");
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
