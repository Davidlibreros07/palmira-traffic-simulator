package org.icesi.implementacionintegradora.models;

public class Bombero extends Vehiculo {

    public Bombero(double x, double y) {
        super(x, y, 3, "BOMBERO", 2);
    }

    @Override
    protected void resolverIncidente() {
        super.resolverIncidente();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected void moverAutonomamente() {
        super.moverAutonomamente();


        if (posicionX < 400) {
            posicionX += velocidad * 0.5;
        }
    }
}
