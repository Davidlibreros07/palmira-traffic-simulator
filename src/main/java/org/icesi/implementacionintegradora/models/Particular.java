package org.icesi.implementacionintegradora.models;


public class Particular extends Vehiculo {


    private double[] rutaParticularX;
    private double[] rutaParticularY;
    private int tipoRuta;
    private int particularId;
    private static int contadorParticulares = 0;

    public Particular(double x, double y) {
        super(x, y, 1.5, "PARTICULAR", 10);

        // Asignar ID único
        particularId = contadorParticulares++;


        asignarRutaFija();
    }

    private void asignarRutaFija() {
        // Solo hay 2 rutas posibles: Ruta A (0) y Ruta B (1)
        // Los particulares alternan entre estas dos rutas
        if (particularId % 2 == 0) {

            tipoRuta = 0;
            rutaParticularY = new double[]{100, 200, 300, 400, 500};
            rutaParticularX = new double[]{725, 725, 725, 725, 725};
            velocidad = 4;
        } else {

            tipoRuta = 1;
            rutaParticularY = new double[]{100, 200, 300, 400, 500};
            rutaParticularX = new double[]{250, 250, 250, 250, 250};
            velocidad = 3;
        }

        // se inicializan los vehiculos en el primer punto de la ruta
        indiceRuta = 0;
        posicionX = rutaParticularX[0];
        posicionY = rutaParticularY[0];

        System.out.println(" Particular #" + particularId + " creado en Ruta " + (tipoRuta == 0 ? "A" : "B") + " - Velocidad: " + velocidad);
    }

    @Override
    protected void moverAutonomamente() {

        double targetX = rutaParticularX[indiceRuta];
        double targetY = rutaParticularY[indiceRuta];

        double dx = targetX - posicionX;
        double dy = targetY - posicionY;
        double distance = Math.sqrt(dx * dx + dy * dy);


        if (distance < 10) {
            indiceRuta = (indiceRuta + 1) % rutaParticularX.length;
        } else {

            double moveX = (dx / distance) * velocidad;
            double moveY = (dy / distance) * velocidad;

            posicionX += moveX;
            posicionY += moveY;
        }


        posicionX = Math.max(50, Math.min(950, posicionX));
        posicionY = Math.max(50, Math.min(550, posicionY));
    }

    @Override
    protected void moverHaciaIncidente() {
        // Los vehículos particulares NO responden a incidentes
        moverAutonomamente();
    }

    @Override
    protected void resolverIncidente() {
        // Los vehículos particulares NO resuelven incidentes
    }

    @Override
    public void assignToIncident(Incidente incident) {
        // Los vehículos particulares NO pueden ser asignados a incidentes
    }


    public String getTipoRuta() {
        return tipoRuta == 0 ? "RutaA" : "RutaB";
    }

    public int getParticularId() {
        return particularId;
    }


    public int getTipoRutaNumero() {
        return tipoRuta;
    }

}