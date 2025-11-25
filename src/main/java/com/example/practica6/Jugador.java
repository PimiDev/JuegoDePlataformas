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

    private Image[] idleFrames;
    private Image[] runFrames;
    private Image deadFrame;   // NUEVO: frame de muerte
    private int frameIndex = 0;
    private int frameCounter = 0; // velocidad de animación

    private MediaPlayer sonidoMuerte;

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

        deadFrame = new Image("file:assets/images/artemioDEAD.png", 0, 0, true, false); // NUEVO
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

    public void quedarseQuieto() {
        if (!isQuieto) frameIndex = 0;
        isQuieto = true;
        isMoviendoseDerecha = false;
        isMoviendoseIzquierda = false;
    }

    public void applyGravity() {
        velY += 0.5;
        y += velY;
        if (y > 1000) morir();
    }

    public void landOn(Plataforma p) {
        y = p.getY() - height;
        velY = 0;
        enSuelo = true;
    }

    public boolean saltar() {
        if (!puedeMoverse || !enSuelo) return false;
        velY = -12;
        enSuelo = false;
        return true;
    }

    public void morir() {
        if (!vivo) return;

        vivo = false;
        puedeMoverse = false;
        velY = 0;

        // reproducir sonido de muerte
        try {
            Media m = new Media(new java.io.File("assets/sounds/gameover.mp3").toURI().toString());
            sonidoMuerte = new MediaPlayer(m);
            sonidoMuerte.play();
        } catch (Exception e) {
            System.out.println("No se pudo cargar sonido de muerte");
        }
    }

    @Override
    public void update() {
        if (vivo) {
            frameCounter++;
            if (frameCounter > 20) {
                frameCounter = 0;
                frameIndex++;
            }
            if (isQuieto) {
                if (frameIndex >= idleFrames.length) frameIndex = 0;
            } else {
                if (frameIndex >= runFrames.length) frameIndex = 0;
            }
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setImageSmoothing(false);
        Image frame;

        if (!vivo) {
            frame = deadFrame; // NUEVO: mostrar muerto
        } else if (isQuieto) {
            frame = idleFrames[frameIndex];
        } else {
            frame = runFrames[frameIndex];
        }

        if (isMoviendoseIzquierda) {
            gc.drawImage(frame, x + width, y, -width, height);
        } else {
            gc.drawImage(frame, x, y, width, height);
        }
    }

    // --- Getters y setters ---
    public boolean isVivo() { return vivo; }
    public boolean puedeMoverse() { return puedeMoverse; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public int getPuntaje() { return puntaje; }
    public void addPuntaje(int v) { puntaje += v; }
    public void setX(double nx) { x = nx; }
    public void setY(double ny) { y = ny; }
    public void setPuntaje(int p) { puntaje = p; }
    public void setEnSuelo(boolean v) { enSuelo = v; }
}