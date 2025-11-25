package com.example.practica6;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Pimi extends Aliado{



    public Pimi(double x, double y, double width, double height) {
        super(x, y, width, height);

        try {
            sprite = new Image("file:assets/images/pimi.png");
        } catch (Exception e) { sprite = null; }

        mensaje = "Y la verdad es que no soy \n tan fuerte como" +
                " lo pensaba\n oye podrias avisarme si ves a derek?\nesta MUY enojado conmigo!";

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
