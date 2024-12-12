package edu.nbd.model;

public class Bicycle extends Vehicle {

    public Bicycle(String plateNumber, int basePrice) {
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