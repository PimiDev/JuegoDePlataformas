package com.example.practica6;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Aliado extends Entidad {

    protected Image sprite;
    protected String mensaje;

    public Aliado(double x, double y, double width, double height) {
        super(x, y, width, height);
        try {
            sprite = new Image("file:assets/images/friend.png");
        } catch (Exception e) {
            sprite = null;
        }
    }

    @Override
    public void update() {
    }

    public void revisarColision(Jugador jugador) {
        // Rectángulo del aliado
        Rectangle2D aliadoBounds = new Rectangle2D(x, y, width, height);

        // Rectángulo del jugador
        Rectangle2D jugadorBounds = new Rectangle2D(jugador.getX(), jugador.getY(), jugador.getWidth(), jugador.getHeight());

        if (aliadoBounds.intersects(jugadorBounds)) {
            System.out.println(mensaje);
        }
    }


    @Override
    public void draw(GraphicsContext gc) {
        gc.setImageSmoothing(false);
        if (sprite != null) {
            gc.drawImage(sprite, x, y, width, height);
        } else {
            gc.setFill(Color.BLUE);
            gc.fillRect(x, y, width, height);
        }
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }

    public String revisarColisionVisual(Jugador jugador) {
        Rectangle2D aliadoBounds = new Rectangle2D(x, y, width, height);
        Rectangle2D jugadorBounds = new Rectangle2D(jugador.getX(), jugador.getY(), jugador.getWidth(), jugador.getHeight());

        if (aliadoBounds.intersects(jugadorBounds)) {
            return mensaje; // devuelve el mensaje en vez de imprimirlo
        }
        return null;
    }

    public String getMensaje() {
        return mensaje;
    }
}
