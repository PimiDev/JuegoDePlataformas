package com.example.practica6;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Item extends Entidad {
    private boolean subiendo = true;
    private Image sprite;
    private boolean recogido = false;
    double yTemp;
    int frameCounter = 0;
    public Item(double x, double y, double width, double height) {
        super(x, y, width, height);
        yTemp = y;
        try {
            sprite = new Image("file:assets/images/resistor.png");
        } catch (Exception e) { sprite = null; }
    }

    @Override
    public void update() {
        frameCounter++;
        y = yTemp + Math.sin(frameCounter * 0.03) * 5;  // sube y baja 5px
    }


    @Override
    public void draw(GraphicsContext gc) {
        if (!recogido) {
            if (sprite != null) {
                gc.drawImage(sprite, x, y, width, height);
            } else {
                gc.setFill(Color.BLUE);
                gc.fillRect(x, y, width, height);
            }
        }
    }

    public boolean isRecogido() {
        return recogido;
    }
    public void recoger() {
        this.recogido = true;
    }
}
