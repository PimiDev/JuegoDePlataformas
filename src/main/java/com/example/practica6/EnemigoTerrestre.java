package com.example.practica6;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public abstract class EnemigoTerrestre extends Enemigo {
    public EnemigoTerrestre(double x, double y, double width, double height, double velX) {
        super(x,y,width,height,velX);
    }

    @Override
    public void update() {
        x += velX;
        if (x < 0 || x + width > 800) velX *= -1;
    }

    public abstract void update(GraphicsContext gc);

    @Override
    public void draw(GraphicsContext gc) {

            gc.setFill(Color.RED);
            gc.fillRect(x,y,width,height);

    }
}
