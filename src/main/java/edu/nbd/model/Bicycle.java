package edu.nbd.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Bicycle extends Vehicle {

    @BsonCreator
    public Bicycle(@BsonProperty("plateNumber") String plateNumber,
                   @BsonProperty("basePrice") int basePrice) {
        super(plateNumber, basePrice);
    }

    public Bicycle() {

    }
}