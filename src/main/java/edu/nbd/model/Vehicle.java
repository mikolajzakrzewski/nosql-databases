package edu.nbd.model;

public abstract class Vehicle {

    private String plateNumber;
    private int basePrice;
    private boolean archive = false;

    public Vehicle(String plateNumber, int basePrice) {
        this.plateNumber = plateNumber;
        this.basePrice = basePrice;
    }

    public String getVehicleInfo() {
        return plateNumber + basePrice;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        if (plateNumber != null && !plateNumber.isEmpty()) {
            this.plateNumber = plateNumber;
        } else {
            throw new VehicleException("Given plate number shouldn't be empty.");
        }
    }

    public int getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(int basePrice) {
        if (basePrice > 0) {
            this.basePrice = basePrice;
        } else {
            throw new VehicleException("Given base price should be more than zero.");
        }
    }

    public double getActualRentalPrice() {
        return basePrice;
    }

    public boolean isArchived() {
        return archive;
    }

    public void setArchive(boolean isArchived) {
        this.archive = isArchived;
    }

    public String getInfo() {
        return getVehicleInfo();
    }

    public String getId() {
        return getPlateNumber();
    }
}