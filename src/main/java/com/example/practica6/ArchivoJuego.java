package com.example.practica6;

import java.io.*;

public class ArchivoJuego {
    private final String ruta;

    public ArchivoJuego(String ruta) {
        this.ruta = ruta;

        File f = new File(ruta).getParentFile();
        if (f != null && !f.exists()) f.mkdirs();
    }

    public void guardar(Progreso p) throws IOException {
        try (FileWriter fw = new FileWriter(ruta)) {
            fw.write("puntaje=" + p.puntaje + "\n");
            fw.write("jugador=" + p.nombre + "\n");

            fw.write("x=" + p.x + "\n");
            fw.write("y=" + p.y + "\n");
        }
    }

    public Progreso cargar() throws IOException {
        File file = new File(ruta);
        if (!file.exists()) return null;

        int puntaje = 0;
        String nombre = "player";
        double x = 50;
        double y = 450;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {

                if (line.startsWith("puntaje="))
                    puntaje = Integer.parseInt(line.split("=")[1].trim());

                if (line.startsWith("jugador="))
                    nombre = line.split("=")[1].trim();

                // cargar posición
                if (line.startsWith("x=")) x = Double.parseDouble(line.split("=")[1].trim());
                if (line.startsWith("y=")) y = Double.parseDouble(line.split("=")[1].trim());
            }
        }
        return new Progreso(puntaje, nombre, x, y);
    }

    public static class Progreso {
        public int puntaje;
        public String nombre;
        public double x, y;

        public Progreso(int puntaje, String nombre, double x, double y) {
            this.puntaje = puntaje;
            this.nombre = nombre;
            this.x = x;
            this.y = y;
        }
    }
}