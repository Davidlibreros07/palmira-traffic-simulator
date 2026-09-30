package org.icesi.implementacionintegradora.models;

public class Ambulancia extends Vehiculo {

    public Ambulancia(double x, double y) {
        super(x, y, 3.5, "AMBULANCIA", 1);
    }

    @Override
    protected void moverHaciaIncidente() {
        if (incidenteAsignado == null) return;

        double dx = incidenteAsignado.getPosicionX() - posicionX;
        double dy = incidenteAsignado.getPosicionY() - posicionY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < 15) {
            resolverIncidente();
        } else {
            posicionX += (dx / distance) * velocidad * 2.0;
            posicionY += (dy / distance) * velocidad * 2.0;
        }
    }

    @Override
    protected void resolverIncidente() {
        super.resolverIncidente();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
