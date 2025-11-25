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

    private Stage stage;
    private Font minecraftFont;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        minecraftFont = Font.loadFont("file:assets/fonts/Minecraft.ttf", 50);

        mostrarMenu();
    }

    public void mostrarMenu() {
        StackPane rootMenu = new StackPane();
        rootMenu.setPrefSize(800, 600);
        rootMenu.setStyle("-fx-background-color: black;");

        VBox menu = new VBox(25);
        menu.setAlignment(Pos.CENTER);

        // Título
        Text title = new Text("PLATAFORMERO JAVA");
        title.setFont(minecraftFont);
        title.setFill(Color.WHITE);
        title.setEffect(new DropShadow(10, Color.GRAY));

        // Botones
        Button btnNuevo = new Button("Nuevo Juego");
        Button btnContinuar = new Button("Continuar");
        Button btnSalir = new Button("Salir");

        estilizarBoton(btnNuevo, minecraftFont);
        estilizarBoton(btnContinuar, minecraftFont);
        estilizarBoton(btnSalir, minecraftFont);

        menu.getChildren().addAll(title, btnNuevo, btnContinuar, btnSalir);
        rootMenu.getChildren().add(menu);

        Scene menuScene = new Scene(rootMenu, 800, 600);

        // Configurar acciones de los botones con transición fade
        btnNuevo.setOnAction(e -> iniciarJuego(false, rootMenu));
        btnContinuar.setOnAction(e -> iniciarJuego(true, rootMenu));
        btnSalir.setOnAction(e -> stage.close());

        stage.setTitle("Menú Principal - Minecraft Style");
        stage.setScene(menuScene);
        stage.show();
    }

    private void iniciarJuego(boolean cargarProgreso, StackPane rootMenu) {
        Game game = new Game(this, cargarProgreso);
        StackPane rootGame = new StackPane(game);
        Scene gameScene = new Scene(rootGame, 800, 600);
        game.setupInput(gameScene);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(600), rootMenu);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(ev -> stage.setScene(gameScene));
        fadeOut.play();
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

    public void regresarMenu() {
        mostrarMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}