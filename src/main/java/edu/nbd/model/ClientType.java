package edu.nbd.model;

public abstract class ClientType {

    private int maxVehicles;

    private int discount;

    public ClientType(int maxVehicles, int discount) {
        this.maxVehicles = maxVehicles;
        this.discount = discount;
    }

    public int getMaxVehicles() {
        return maxVehicles;
    }

    public int getDiscount() {
        return discount;
    }

    public String getTypeInfo() {
        return String.valueOf(maxVehicles) + discount;
    }

    public String getInfo() {
        return getTypeInfo();
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
}