package edu.nbd.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator(key = "_type", value = "bicycle")
public class Bicycle extends Vehicle {

    @BsonCreator
    public Bicycle(@BsonProperty("plateNumber") String plateNumber,
                   @BsonProperty("basePrice") int basePrice) {
        super(plateNumber, basePrice);
    }

    @Override
    public String getVehicleInfo() {
        String className = "Bicycle";
        return className + super.getVehicleInfo();
    }

    public Bicycle() {

    }
}