package com.example.practica6;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class CarroBomba extends EnemigoTerrestre {
    private Image[] Frames;
    private int frameIndex = 0;
    private int frameCounter = 0; //velocidad

    public CarroBomba(double x, double y, double width, double height, double velX) {
        super(x, y, width, height, velX);

        Frames = new Image[]{
                new Image("file:assets/images/car1.png", 0, 0, true, false),
                new Image("file:assets/images/car2.png", 0, 0, true, false)
        };

    }

    @Override
    public void update() {
        frameCounter++;

        // Cambiar sprite cada 20 frames
        if (frameCounter >=60) {
            frameCounter = 0;
            frameIndex++;

            if (frameIndex >= Frames.length) {
                frameIndex = 0;   // Reiniciar ciclo
            }
        }

        // Movimiento
        x += velX;
        if (x < 0 || x + width > 800) velX *= -1;
    }

    @Override
    public void update(GraphicsContext gc) {

    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setImageSmoothing(false);
        gc.drawImage(Frames[frameIndex], x, y, width, height);
    }


}
