package edu.nbd.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class MotorVehicle extends Vehicle {

    @BsonProperty("engineDisplacement")
    private int engineDisplacement;

    @BsonCreator
    public MotorVehicle(@BsonProperty("plateNumber") String plateNumber,
                        @BsonProperty("basePrice") int basePrice,
                        @BsonProperty("engineDisplacement") int engineDisplacement) {
        super(plateNumber, basePrice);
        this.engineDisplacement = engineDisplacement;
    }

    public MotorVehicle() {
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
        String className = "MotorVehicle";
        return className + super.getVehicleInfo() + engineDisplacement;
    }

    public int getEngineDisplacement() {
        return engineDisplacement;
    }
}