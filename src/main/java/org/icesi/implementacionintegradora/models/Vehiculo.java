package org.icesi.implementacionintegradora.models;

import java.util.Random;

public abstract class Vehiculo extends Thread {

    protected double posicionX;
    protected double posicionY;
    protected double velocidad;
    protected String tipoVehiculo;
    protected boolean disponible;
    protected int prioridad;
    protected Incidente incidenteAsignado;
    protected Random random;
    protected boolean activo;

    // rutas predefinidas
    protected double[] rutaX = {100, 300, 500, 700, 900, 100};
    protected double[] rutaY = {300, 300, 300, 300, 300, 300};
    protected int indiceRuta = 0;

    public Vehiculo(double x, double y, double velocidad, String tipo, int prioridad) {
        this.posicionX = x;
        this.posicionY = y;
        this.velocidad = velocidad;
        this.tipoVehiculo = tipo;
        this.prioridad = prioridad;
        this.disponible = true;
        this.random = new Random();
        this.activo = true;
    }

    @Override
    public void run() {
        while (activo && !isInterrupted()) {
            try {
                if (incidenteAsignado != null && !incidenteAsignado.isResuelto()) {
                    moverHaciaIncidente();
                } else {
                    moverAutonomamente();
                }

                Thread.sleep((long) (1000 / velocidad));
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    protected void moverAutonomamente() {
        // seguir la ruta predefinida
        double targetX = rutaX[indiceRuta];
        double targetY = rutaY[indiceRuta];

        double dx = targetX - posicionX;
        double dy = targetY - posicionY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < 10) {
            indiceRuta = (indiceRuta + 1) % rutaX.length;
        } else {
            posicionX += (dx / distance) * velocidad;
            posicionY += (dy / distance) * velocidad;
        }


        posicionX = Math.max(50, Math.min(950, posicionX));
        posicionY = Math.max(50, Math.min(550, posicionY));
    }

    protected void moverHaciaIncidente() {

        if (incidenteAsignado == null) return;

        double dx = incidenteAsignado.getPosicionX() - posicionX;
        double dy = incidenteAsignado.getPosicionY() - posicionY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < 15) {

            resolverIncidente();
        } else {
            posicionX += (dx / distance) * velocidad * 1.5;
            posicionY += (dy / distance) * velocidad * 1.5;
        }


    }

    protected void resolverIncidente() {
        if (incidenteAsignado != null) {
            incidenteAsignado.setResuelto(true);
            incidenteAsignado = null;
            disponible = true;
        }
    }

    public void assignToIncident(Incidente incident) {
        this.incidenteAsignado = incident;
        this.disponible = false;
    }

    public double getPosicionX() { return posicionX; }
    public double getPosicionY() { return posicionY; }
    public double getVelocidad() { return velocidad; }
    public boolean isDisponible() { return disponible; }

}
