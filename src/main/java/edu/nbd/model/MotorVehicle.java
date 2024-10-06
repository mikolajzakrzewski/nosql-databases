package edu.nbd.model;

public class MotorVehicle extends Vehicle {

    private final int engineDisplacement;

    public MotorVehicle(String plateNumber, int basePrice, int engineDisplacement) {
        super(plateNumber, basePrice);
        this.engineDisplacement = engineDisplacement;
    }

    @Override
    public double getActualRentalPrice() {
        double engineDisplacementFactor;

        if (engineDisplacement < 1000) {
            engineDisplacementFactor = 1.0;
        } else if (engineDisplacement <= 2000) {
            double k = engineDisplacement - 1000;
            engineDisplacementFactor = 1 + k / 2000.0;
        } else {
            engineDisplacementFactor = 1.5;
        }

        double actualRentalPrice = getBasePrice() * engineDisplacementFactor;
        actualRentalPrice = Math.round(actualRentalPrice * 100.0) / 100.0;
        return actualRentalPrice;
    }

    @Override
    public String getVehicleInfo() {
        return super.getVehicleInfo() + engineDisplacement;
    }

    public int getEngineDisplacement() {
        return engineDisplacement;
    }
}