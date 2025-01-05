package edu.nbd.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;

@Entity(defaultKeyspace = "nbd")
@CqlName("vehicles")
public class MotorVehicle extends Vehicle {

    @CqlName("engine_displacement")
    private int engineDisplacement;

    public MotorVehicle(String plateNumber, int basePrice, int engineDisplacement) {
        super(plateNumber, basePrice);
        this.discriminator = "motor_vehicle";
        this.engineDisplacement = engineDisplacement;
    }

    public MotorVehicle(String plateNumber, int basePrice, boolean archived, int rented, String discriminator, int engineDisplacement) {
        super(plateNumber, basePrice, archived, rented, discriminator);
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

    public void setEngineDisplacement(int engineDisplacement) {
        this.engineDisplacement = engineDisplacement;
    }
}