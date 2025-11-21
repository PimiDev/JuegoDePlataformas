package com.example.practica6;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Plataforma {


    private double x, y, width, height;
    public Plataforma(double x, double y, double width, double height) {
        this.x = x; this.y = y; this.width = width; this.height = height;



    }
    public Rectangle2D getBounds() { return new Rectangle2D(x,y,width,height); }
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.fillRect(x,y,width,height);
    }
    public double getY() { return y; }
}
