package com.example.practica6;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;



public class Game {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final int width;
    private final int height;

    private Jugador jugador;
    private List<Entidad> entidades;
    private List<Plataforma> plataformas;
    private Set<KeyCode> keys = new HashSet<>();
    private ArchivoJuego archivoJuego;
    private Image fondo;
    private AnimationTimer loop;

    private MediaPlayer musica;
    private double cameraX = 0;

    private String mensajeAleado = "";
    private int mensajeTimer = 0;

    private MediaPlayer sonidoItem;
    private MediaPlayer sonidoSalto;


    public Game(int width, int height) {
        this.width = width;
        this.height = height;
        this.canvas = new Canvas(width, height);
        this.gc = canvas.getGraphicsContext2D();
        init();
    }

    public Canvas getCanvas() { return canvas; }

    private void init() {
        fondo = new Image("file:assets/images/fondo2.jpg");


        archivoJuego = new ArchivoJuego("datos/progreso.txt");
        entidades = new ArrayList<>();
        plataformas = new ArrayList<>();


        jugador = new Jugador(50, 450, 48, 72);
        entidades.add(jugador);


// Aliado al inicio
        entidades.add(new Pimi(100, 450, 48, 72));

// -------------------- PLATAFORMAS --------------------
        plataformas.add(new Plataforma(0, 540, 2000, 60));       // suelo largo

        plataformas.add(new Plataforma(200, 430, 150, 25));
        plataformas.add(new Plataforma(420, 380, 150, 25));
        plataformas.add(new Plataforma(650, 340, 180, 25));
        plataformas.add(new Plataforma(1500, 430, 180, 25));

        entidades.add(new CarroBomba(830, 500, 40, 40, 2, 830, 2000));
        entidades.add(new CarroBomba(900, 500, 40, 40, 2, 900, 2000));
        entidades.add(new CarroBomba(970, 500, 40, 40, 2, 970, 2000));
        entidades.add(new CarroBomba(1040, 500, 40, 40, 2, 1040, 2000));
        entidades.add(new CarroBomba(1110, 500, 40, 40, 2, 1110, 2000));

        entidades.add(new Item(230, 390, 40, 40));
        entidades.add(new Item(470, 340, 40, 40));
        entidades.add(new Item(690, 300, 40, 40));   // escalera de items

        entidades.add(new Item(1550, 370, 40, 40));

        // Setup loop
        loop = new AnimationTimer() {
            private long last = 0;
            @Override
            public void handle(long now) {
                if (last == 0) last = now;
                double delta = (now - last) / 1e9;
                actualizar(delta);
                dibujar();
                last = now;
            }
        };

        // try to load previous progress
        try {
            ArchivoJuego.Progreso p = archivoJuego.cargar();
            if (p != null) {
                jugador.setPuntaje(p.puntaje);
            }
        } catch (Exception e) {
            // ignore
        }

        iniciarMusica();

        try {
            Media mItem = new Media(new java.io.File("assets/sounds/item.mp3").toURI().toString());
            sonidoItem = new MediaPlayer(mItem);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el sonido del item");
        }

        try {
            Media mSalto = new Media(new java.io.File("assets/sounds/jump.mp3").toURI().toString());
            sonidoSalto = new MediaPlayer(mSalto);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el sonido de salto");
        }



    }

    private void iniciarMusica() {
        try {
            String path = "assets/music/fuerteNoSoy.mp3";    // tu ruta
            Media m = new Media(new java.io.File(path).toURI().toString());
            musica = new MediaPlayer(m);
            musica.setCycleCount(MediaPlayer.INDEFINITE); // loop infinito
            musica.setVolume(10);
            musica.play();
        } catch (Exception e) {
            System.out.println("No se pudo cargar la música: " + e.getMessage());
        }
    }


    public void setupInput(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            keys.add(e.getCode());
            if (e.getCode() == KeyCode.S) {
                guardar();
            }
        });
        scene.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            keys.remove(e.getCode());

            // si soltaste una de movimiento
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) {

                // PERO SIGUES PRESIONANDO OTRA → NO QUIETO
                if (keys.contains(KeyCode.LEFT)) return;
                if (keys.contains(KeyCode.RIGHT)) return;

                // si no hay movimiento → sí quieto
                jugador.quedarseQuieto();
            }
        });


    }

    public void start() { loop.start(); }

    private void actualizar(double delta) {
        // input
        if (keys.contains(KeyCode.LEFT)) jugador.moverIzquierda();
        if (keys.contains(KeyCode.RIGHT)) jugador.moverDerecha();
        if (keys.contains(KeyCode.SPACE)){
            if (jugador.saltar()) {     // si el salto realmente ocurrió
                if (sonidoSalto != null) {
                    sonidoSalto.stop();
                    sonidoSalto.play();
                }
            }
        }

        // update entities
        for (Entidad en : entidades) en.update();

        // gravedad & plataformas collision for player
        jugador.applyGravity();


        boolean onPlatform = false;
        for (Plataforma p : plataformas) {
            if (jugador.getBounds().intersects(p.getBounds())) {
                jugador.landOn(p);
                onPlatform = true;
            }
        }
        if (!onPlatform) jugador.setEnSuelo(false);

        // collisions with enemies
        for (Entidad en : entidades) {
            if (en instanceof Enemigo) {
                if (jugador.getBounds().intersects(en.getBounds())) {
                    jugador.setVivo(false);
                }
            }
            if (en instanceof Item item){
                if (!item.isRecogido() && jugador.getBounds().intersects(en.getBounds())) {

                    item.recoger();
                    jugador.addPuntaje(1);

                    // 🔊 Reproducir sonido
                    if (sonidoItem != null) {
                        sonidoItem.stop();  // reinicia si ya se había reproducido
                        sonidoItem.play();
                    }
                }
            }

            if (en instanceof Aliado aliado) {
                aliado.applyGravity();
                if (jugador.getBounds().intersects(aliado.getBounds())) {
                    mensajeAleado = aliado.getMensaje();
                    mensajeTimer = 600; // 3 segundos aprox (60 fps)
                }

                for(Plataforma p : plataformas) {
                    if (aliado.getBounds().intersects(p.getBounds())) {
                        aliado.landOn(p);
                    }
                }
            }
        }
        cameraX = jugador.getX() - width / 2;
        if (cameraX < 0) cameraX = 0;

        // remove dead or collected items if any (not implemented but placeholder)
    }

    private void dibujar() {
        // clear pantalla
        // DIBUJAR FONDO ANTES DE CUALQUIER COSA
        //gc.drawImage(fondo, 0, 0, width, height);
        // Limpiar pantalla y dibujar fondo sin suavizado
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        // FONDO SCROLL
        if (fondo != null) {
            gc.setImageSmoothing(false);

            double offsetX = cameraX * 0.5; // la velocidad del fondo, 0.5 = más lento que jugador (parallax)
            double fondoWidth = fondo.getWidth();

            // Dibuja el fondo varias veces para cubrir la pantalla
            for (double x = -offsetX % fondoWidth; x < width; x += fondoWidth) {
                gc.drawImage(fondo, x, 0);
            }
        }




        //activar camara
        gc.save();
        gc.translate(-cameraX, 0);   // mueve todo el mundo

        // dibujar plataformas
        gc.setFill(Color.SADDLEBROWN);
        for (Plataforma p : plataformas) {
            p.draw(gc);
        }

        // dibujar entidades
        for (Entidad e : entidades) {
            e.draw(gc);
        }

        // DESACTIVAR CÁMARA (HUD queda fijo)
        gc.restore();

        // HUD (no se mueve con la cámara)
        gc.setFill(Color.WHITE);
        Font pixelFont = Font.loadFont("file:assets/fonts/Minecraft.ttf", 24);
        gc.setFont(pixelFont); // ← ESTA ES LA PARTE QUE FALTABA

        gc.fillText("RESISTENCIAS: " + jugador.getPuntaje(), 20, 30);
        gc.fillText("Presiona 'S' para guardar", 20, 55);

        if (!jugador.isVivo()) {
            gc.setFill(Color.color(0,0,0,0.6));
            gc.fillRect(0, 0, width, height);

            gc.setFont(pixelFont);
            gc.setFill(Color.WHITE);
            gc.fillText("¡Has perdido!", width/2 - 100, height/2);
        }


        // Mostrar mensaje sobre el HUD
        if (mensajeTimer > 0) {
            gc.setFill(Color.BLACK);
            gc.setFont(pixelFont);
            gc.fillText(mensajeAleado, 20, 90);
            mensajeTimer--;
        }



    }

    private void guardar() {
        try {
            archivoJuego.guardar(new ArchivoJuego.Progreso(jugador.getPuntaje(), "player"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
