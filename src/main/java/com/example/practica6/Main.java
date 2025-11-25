package com.example.practica6;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        StackPane rootMenu = new StackPane();
        rootMenu.setPrefSize(800, 600);
        rootMenu.setStyle("-fx-background-color: black;"); // fondo negro

        VBox menu = new VBox(25);
        menu.setAlignment(Pos.CENTER);

        // Cargar fuente Minecraft.ttf
        Font minecraftFont = Font.loadFont("file:assets/fonts/Minecraft.ttf", 50);

        // Título
        Text title = new Text("PLATAFORMERO JAVA");
        title.setFont(minecraftFont);
        title.setFill(Color.WHITE);
        title.setEffect(new DropShadow(10, Color.GRAY));

        // Botones
        Button btnStart = new Button("Iniciar Juego");
        Button btnSalir = new Button("Salir");

        // Estilizar botones con la misma fuente
        estilizarBoton(btnStart, minecraftFont);
        estilizarBoton(btnSalir, minecraftFont);

        menu.getChildren().addAll(title, btnStart, btnSalir);
        rootMenu.getChildren().add(menu);

        Scene menuScene = new Scene(rootMenu, 800, 600);

        // ---- Escena del juego ----
        Game game = new Game(800, 600);
        StackPane rootGame = new StackPane(game.getCanvas());
        Scene gameScene = new Scene(rootGame, 800, 600);
        game.setupInput(gameScene);

        // Acción botón Iniciar Juego con transición
        btnStart.setOnAction(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(600), rootMenu);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(ev -> {
                primaryStage.setScene(gameScene);
                game.start();
            });
            fadeOut.play();
        });

        // Botón salir
        btnSalir.setOnAction(e -> primaryStage.close());

        primaryStage.setTitle("Artemio's Adventure - JavaFX");
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    private void estilizarBoton(Button b, Font font) {
        b.setFont(font);
        b.setStyle("""
                -fx-background-color: #222;
                -fx-text-fill: white;
                -fx-font-size: 24px;
                -fx-padding: 10 40 10 40;
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-border-color: white;
                -fx-border-width: 2;
                """);

        b.setOnMouseEntered(ev -> b.setStyle("""
                -fx-background-color: #555;
                -fx-text-fill: cyan;
                -fx-font-size: 24px;
                -fx-padding: 10 40 10 40;
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-border-color: cyan;
                -fx-border-width: 2;
                """));

        b.setOnMouseExited(ev -> b.setStyle("""
                -fx-background-color: #222;
                -fx-text-fill: white;
                -fx-font-size: 24px;
                -fx-padding: 10 40 10 40;
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-border-color: white;
                -fx-border-width: 2;
                """));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
