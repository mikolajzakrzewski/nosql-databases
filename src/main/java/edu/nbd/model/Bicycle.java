package edu.nbd.model;

import jakarta.persistence.Entity;

@Entity
public class Bicycle extends Vehicle {

    public Bicycle(String plateNumber, int basePrice) {
        super(plateNumber, basePrice);
    }

    public Bicycle() {

    }
}