package edu.nbd.model;

public class MotorVehicle extends Vehicle {

    private final int engineDisplacement;

    public MotorVehicle(String plateNumberMotorVehicle, int basePriceMotorVehicle, int engineDisplacementMotorVehicle) {
        super(plateNumberMotorVehicle, basePriceMotorVehicle);
        this.engineDisplacement = engineDisplacementMotorVehicle;
    }

    @Override
    public double getActualRentalPrice() {
        double engineDisplacementFactorMotorVehicle;

        if (engineDisplacement < 1000) {
            engineDisplacementFactorMotorVehicle = 1.0;
        } else if (engineDisplacement <= 2000) {
            double k = engineDisplacement - 1000;
            engineDisplacementFactorMotorVehicle = 1 + k / 2000.0;
        } else {
            engineDisplacementFactorMotorVehicle = 1.5;
        }

        double actualRentalPriceMotorVehicle = getBasePrice() * engineDisplacementFactorMotorVehicle;
        actualRentalPriceMotorVehicle = Math.round(actualRentalPriceMotorVehicle * 100.0) / 100.0;
        return actualRentalPriceMotorVehicle;
    }

    @Override
    public String getVehicleInfo() {
        return super.getVehicleInfo() + engineDisplacement;
    }

    public int getEngineDisplacement() {
        return engineDisplacement;
    }
}