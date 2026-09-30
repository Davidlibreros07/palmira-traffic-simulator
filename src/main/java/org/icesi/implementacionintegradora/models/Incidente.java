package org.icesi.implementacionintegradora.models;

public class Incidente implements Comparable<Incidente> {
    private String tipo;
    private double posicionX;
    private double posicionY;
    private int prioridad;
    private boolean resuelto;
    private long tiempoCreacion;

    public Incidente(String tipo, double x, double y, int prioridad) {
        this.tipo = tipo;
        this.posicionX = x;
        this.posicionY = y;
        this.prioridad = prioridad;
        this.resuelto = false;
        this.tiempoCreacion = System.currentTimeMillis();
    }

    @Override
    public int compareTo(Incidente other) {
        int comparison = Integer.compare(this.prioridad, other.prioridad);

        if (comparison == 0) {
            comparison = Long.compare(this.tiempoCreacion, other.tiempoCreacion);
        }

        if (comparison == 0) {
            comparison = Double.compare(this.posicionX, other.posicionX);
        }

        if (comparison == 0) {
            comparison = Double.compare(this.posicionY, other.posicionY);
        }

        if (comparison == 0) {
            comparison = this.tipo.compareTo(other.tipo);
        }

        if (comparison == 0 && this != other) {
            comparison = Integer.compare(this.hashCode(), other.hashCode());
        }

        return comparison;
    }

    // Getters y Setters
    public String getTipo() { return tipo; }
    public double getPosicionX() { return posicionX; }
    public double getPosicionY() { return posicionY; }
    public int getPrioridad() { return prioridad; }
    public boolean isResuelto() { return resuelto; }


    public void setResuelto(boolean resuelto) { this.resuelto = resuelto; }

    @Override
    public String toString() {
        return String.format("%s (Prioridad: %d) en (%.0f, %.0f)",
                tipo, prioridad, posicionX, posicionY);
    }
}
