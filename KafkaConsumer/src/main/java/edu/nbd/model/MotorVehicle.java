package edu.nbd.model;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator(key = "_type", value = "motorVehicle")
public class MotorVehicle extends Vehicle {

    @BsonProperty("engineDisplacement")
    @JsonbProperty("engineDisplacement")
    private int engineDisplacement;

    @BsonCreator
    @JsonbCreator
    public MotorVehicle(@BsonProperty("plateNumber") @JsonbProperty("plateNumber") String plateNumber,
                        @BsonProperty("basePrice") @JsonbProperty("basePrice") int basePrice,
                        @BsonProperty("engineDisplacement") @JsonbProperty("engineDisplacement") int engineDisplacement) {
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