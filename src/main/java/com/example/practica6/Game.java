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

    private String mensajePimi = "";
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
        fondo = new Image("file:assets/images/fondo.png");


        archivoJuego = new ArchivoJuego("datos/progreso.txt");
        entidades = new ArrayList<>();
        plataformas = new ArrayList<>();


        jugador = new Jugador(50, 450, 48, 72);
        entidades.add(jugador);

        // ---------- NIVEL COMPLETO (sin inventar ninguna función nueva) ---------

// Aliado al inicio
        entidades.add(new Aliado(100, 450, 48, 72, "file:assets/images/pimi.png"));

// -------------------- PLATAFORMAS --------------------
        plataformas.add(new Plataforma(0, 540, 2000, 60));       // suelo largo

        plataformas.add(new Plataforma(200, 430, 150, 25));
        plataformas.add(new Plataforma(420, 380, 150, 25));
        plataformas.add(new Plataforma(650, 340, 180, 25));

        plataformas.add(new Plataforma(900, 500, 180, 40));      // zona de enemigos
        plataformas.add(new Plataforma(900, 420, 120, 40));
        plataformas.add(new Plataforma(900, 350, 120, 40));
        plataformas.add(new Plataforma(900, 280, 120, 40));      // torre

        plataformas.add(new Plataforma(1150, 260, 200, 40));     // plataforma final
        plataformas.add(new Plataforma(1500, 540, 200, 60));     // meta


// -------------------- ENEMIGOS --------------------
        entidades.add(new CarroBomba(300, 500, 40, 40, 1.5, 200, 500));
        entidades.add(new CarroBomba(950, 450, 40, 40, 2.0, 900, 1200));
        entidades.add(new CarroBomba(1250, 220, 40, 40, 1.2, 1200, 1600));
        // enemigo final


// -------------------- ITEMS --------------------
        entidades.add(new Item(230, 390, 40, 40));
        entidades.add(new Item(470, 340, 40, 40));
        entidades.add(new Item(690, 300, 40, 40));   // escalera de items

        entidades.add(new Item(930, 240, 40, 40));   // cima torre
        entidades.add(new Item(1180, 220, 40, 40));  // final



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
                    mensajePimi = "Y la verdad es que no soy \n tan fuerte como" +
                            " lo pensaba\n oye podrias avisarme si ves a derek?\nesta MUY enojado conmigo!";
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
        gc.setFill(Color.web("#87CEEB")); // azul cielo

        gc.fillRect(0, 0, width, height);




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
            gc.fillText(mensajePimi, 20, 90);
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
