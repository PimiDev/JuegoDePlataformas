package com.example.practica6;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Derek extends Aliado{

    public Derek(double x, double y, double width, double height) {
        super(x, y, width, height);

        try {
            sprite = new Image("file:assets/images/derek.png");
        } catch (Exception e) { sprite = null; }

        mensaje = "No has visto a pimi? ME LAS VA A PAGAR!!!";

    }

    @Override
    public void update() {

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
