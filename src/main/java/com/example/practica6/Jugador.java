package com.example.practica6;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

public class Jugador extends Entidad {
    private double velY = 0;
    private boolean enSuelo = false;
    private int puntaje = 0;
    private boolean vivo = true;
    private Image sprite;
    private boolean isQuieto = true;
    private boolean isMoviendoseIzquierda = false;
    private boolean isMoviendoseDerecha = false;
    private boolean puedeMoverse = true;
    private MediaPlayer sonidoMuerte;


    private Image[] idleFrames;
    private Image[] runFrames;
    private int frameIndex = 0;
    private int frameCounter = 0; //velocidad

    public Jugador(double x, double y, double width, double height) {
        super(x,y,width,height);
        idleFrames = new Image[]{
                new Image("file:assets/images/artemioIDLE.png", 0, 0, true, false),
                new Image("file:assets/images/artemioIDLE2.png", 0, 0, true, false)
        };

        runFrames = new Image[]{
                new Image("file:assets/images/artemioRUN.png", 0, 0, true, false),
                new Image("file:assets/images/artemioRUN2.png", 0, 0, true, false),
                new Image("file:assets/images/artemioRUN3.png", 0, 0, true, false)
        };
    }

    public void moverIzquierda() {
        if (!puedeMoverse) return;
        if (isQuieto) frameIndex = 0;

        x -= 5;
        if (x < 0) x = 0;
        isQuieto = false;
        isMoviendoseIzquierda = true;
        isMoviendoseDerecha = false;

    }
    public void moverDerecha() {
        if (!puedeMoverse) return;
        if (isQuieto) frameIndex = 0;
        x += 5;

        isQuieto = false;
        isMoviendoseDerecha = true;
        isMoviendoseIzquierda = false;
    }

    public void respawn() {
        x = 50;
        y = 450;
        vivo = true;
        velY = 0;
        puedeMoverse = true;
    }

    public boolean saltar() {
        if (!puedeMoverse) return false;
        if (enSuelo) {
            velY = -12;
            enSuelo = false;
            return true; // SOLO aquí saltó
        }
        return false; // si estaba en el aire → no suena
    }

    public void morir() {
        // si ya está muerto, no hacemos nada (evita reproducir sonido varias veces)
        if (!vivo) return;

        vivo = false;
        puedeMoverse = false;
        velY = 0;

        try {
            Media m = new Media(new java.io.File("assets/sounds/gameover.mp3").toURI().toString());
            sonidoMuerte = new MediaPlayer(m);
            sonidoMuerte.play();
        } catch (Exception e) {
            System.out.println("No se pudo cargar sonido de muerte");
        }
    }

    public void applyGravity() {
        velY += 0.5;
        y += velY;
        if (y > 1000) {
            // usar morir() para asegurar bloqueo y sonido
            morir();
        }
    }

    public void quedarseQuieto() {
        if (!isQuieto) {
            frameIndex = 0;
        }
        isQuieto = true;
        isMoviendoseDerecha = false;
        isMoviendoseIzquierda = false;
    }


    public void landOn(Plataforma p) {
        // simple landing: place on top
        y = p.getY() - height;
        velY = 0;
        enSuelo = true;
    }

    @Override
    public void update() {
        frameCounter++;
        if (frameCounter > 20)  { // Cambia frame cada 10 updates
            frameCounter = 0; frameIndex++; }
        if (isQuieto) {
            if (frameIndex >= idleFrames.length) frameIndex = 0;
        } else {
            if (frameIndex >= runFrames.length) frameIndex = 0; }
    }
    @Override
    public void draw(GraphicsContext gc) {
        gc.setImageSmoothing(false);

        Image frame;

        if (isQuieto) {
            frame = idleFrames[frameIndex];
        } else {
            frame = runFrames[frameIndex];
        }

        if (isMoviendoseIzquierda) {
            // DIBUJO ESPEJO
            gc.drawImage(
                    frame,
                    x + width,  // mover origen a la derecha
                    y,
                    -width,     // escala negativa → espejo
                    height
            );
        } else {
            gc.drawImage(frame, x, y, width, height);
        }
    }

    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int p) { this.puntaje = p; }
    public void addPuntaje(int v) { this.puntaje += v; }

    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double nx) { this.x = nx; }
    public void setY(double ny) { this.y = ny; }


    public void setEnSuelo(boolean v) { this.enSuelo = v; }
    public boolean isEnSuelo() { return enSuelo; }

    public void setVivo(boolean v) { this.vivo = v; }
    public boolean isVivo() { return vivo; }

    public void setVelY(double v) { this.velY = v; }

    // Nuevo getter para saber si puede moverse
    public boolean puedeMoverse() { return puedeMoverse; }
}