package edu.nbd.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;

@Entity(defaultKeyspace = "nbd")
@CqlName("vehicles")
public class Bicycle extends Vehicle {

    public Bicycle(String plateNumber, int basePrice, boolean archived, String discriminator) {
        super(plateNumber, basePrice, archived, discriminator);
    }

    public Bicycle(String plateNumber, int basePrice) {
        super(plateNumber, basePrice);
        this.discriminator = "bicycle";
    }

    public Bicycle() {
    }

    @Override
    public String getVehicleInfo() {
        String className = "Bicycle";
        return className + super.getVehicleInfo();
    }
}