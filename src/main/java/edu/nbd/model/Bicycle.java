package edu.nbd.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "bicycles")
public class Bicycle extends Vehicle {

    public Bicycle(String plateNumber, int basePrice) {
        super(plateNumber, basePrice);
    }

    public Bicycle() {

    }
}