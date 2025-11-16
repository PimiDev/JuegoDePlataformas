package com.example.practica6;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class EnemigoTerrestre extends Enemigo {
    private Image sprite;
    public EnemigoTerrestre(double x, double y, double width, double height, double velX) {
        super(x,y,width,height,velX);
        try { sprite = new Image("file:assets/images/enemy_ground.png"); } catch (Exception e) { sprite = null; }
    }

    @Override
    public void update() {
        x += velX;
        if (x < 0 || x + width > 800) velX *= -1;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (sprite != null) gc.drawImage(sprite, x, y, width, height);
        else {
            gc.setFill(Color.RED);
            gc.fillRect(x,y,width,height);
        }
    }
}
