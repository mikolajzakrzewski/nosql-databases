package edu.nbd.model;

public abstract class ClientType {

    private final int maxVehicles;
    private final int discount;

    public ClientType(int maxVehicles, int discount) {
        this.maxVehicles = maxVehicles;
        this.discount = discount;
    }

    public int getMaxVehicles() {
        return maxVehicles;
    }

    public double applyDiscount(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Given price cannot be lower than zero.");
        }

        if (price <= discount) {
            return 0;
        }

        return price - discount;
    }

    public String getTypeInfo() {
        return String.valueOf(maxVehicles) + discount;
    }
}