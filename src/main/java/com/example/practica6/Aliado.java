package com.example.practica6;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Aliado extends Entidad {

    private Image sprite;
    private double velY = 0;
    private boolean enSuelo = false;
    public Aliado(double x, double y, double width, double height , String direccion) {
        super(x, y, width, height);
        try {
            sprite = new Image(direccion);
        } catch (Exception e) { sprite = null; }
    }

    @Override
    public void update() {

    }

    public void landOn(Plataforma p) {
        // simple landing: place on top
        y = p.getY() - height;
        velY = 0;
        enSuelo = true;
    }
    public void applyGravity() {
        velY += 0.5;
        y += velY;
    }
    @Override
    public void draw(GraphicsContext gc) {
        gc.setImageSmoothing(false);

        if (sprite != null) {
            gc.drawImage(sprite, x, y, width, height);
        } else {
            gc.setFill(Color.BLUE);
            gc.fillRect(x,y,width,height);
        }
    }
}
