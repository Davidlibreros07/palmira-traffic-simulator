package org.icesi.implementacionintegradora.models;

public class Patrulla extends Vehiculo {

    public Patrulla(double x, double y) {
        super(x, y, 3.5, "PATRULLA", 5);
    }

    @Override
    protected void moverAutonomamente() {
        super.moverAutonomamente();


        if (random.nextInt(100) < 5) { // 5% de probabilidad de cambiar dirección
            indiceRuta = random.nextInt(rutaX.length);
        }
    }
}
