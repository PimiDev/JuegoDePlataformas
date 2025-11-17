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

    private AnimationTimer loop;

    private MediaPlayer musica;
    private double cameraX = 0;


    public Game(int width, int height) {
        this.width = width;
        this.height = height;
        this.canvas = new Canvas(width, height);
        this.gc = canvas.getGraphicsContext2D();
        init();
    }

    public Canvas getCanvas() { return canvas; }

    private void init() {
        archivoJuego = new ArchivoJuego("datos/progreso.txt");
        entidades = new ArrayList<>();
        plataformas = new ArrayList<>();

        jugador = new Jugador(50, 450, 48, 72);
        entidades.add(jugador);

        // Plataformas (suelo + dos plataformas elevadas)
        plataformas.add(new Plataforma(0, 540, 2000, 60)); // suelo
        plataformas.add(new Plataforma(200, 420, 120, 20));
        plataformas.add(new Plataforma(450, 350, 150, 20));

        // Enemigos
        EnemigoTerrestre et = new EnemigoTerrestre(300, 500, 40, 40, 1.5);
        EnemigoVolador ev = new EnemigoVolador(600, 200, 40, 40, 1.2);

        //Item
        Item item = new Item(100,350,40,40);

        //amigos
        Aliado aliado = new Aliado(100,450,48,72);


        entidades.add(aliado);
        entidades.add(et);
        entidades.add(ev);
        entidades.add(item);
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
    }

    private void iniciarMusica() {
        try {
            String path = "assets/music/fondo.mp3";    // tu ruta
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
        scene.addEventHandler(KeyEvent.KEY_RELEASED, e -> keys.remove(e.getCode()));
    }

    public void start() { loop.start(); }

    private void actualizar(double delta) {
        // input
        if (keys.contains(KeyCode.LEFT)) jugador.moverIzquierda();
        if (keys.contains(KeyCode.RIGHT)) jugador.moverDerecha();
        if (keys.contains(KeyCode.SPACE)) jugador.saltar();

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
                }
            }
            if (en instanceof Aliado aliado) {
                aliado.applyGravity();
                if(jugador.getBounds().intersects(aliado.getBounds())) {
                    System.out.println("me tocaste");
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
        gc.setFill(Color.web("#1e1e1e"));
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
        gc.setFont(Font.font(18));
        gc.fillText("Puntaje: " + jugador.getPuntaje(), 20, 30);
        gc.fillText("Presiona 'S' para guardar", 20, 55);

        if (!jugador.isVivo()) {
            gc.setFill(Color.color(0,0,0,0.6));
            gc.fillRect(0, 0, width, height);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font(36));
            gc.fillText("¡Has perdido!", width/2 - 100, height/2);
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
