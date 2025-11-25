package com.example.practica6;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public abstract class Entidad {
    protected double x, y, width, height;

    public Entidad(double x, double y, double width, double height) {
        this.x = x; this.y = y; this.width = width; this.height = height;
    }

    public abstract void update();
    public abstract void draw(GraphicsContext gc);

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }

    // getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
