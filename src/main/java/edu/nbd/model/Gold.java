package edu.nbd.model;

public class Gold extends ClientType {

    private final int discountPercent;

    public Gold() {
        super(4, 0);
        this.discountPercent = 5;
    }

    @Override
    public double applyDiscount(double price) {
        return (super.applyDiscount(price) / 100 * (100 - discountPercent));
    }

    @Override
    public String getTypeInfo() {
        return super.getTypeInfo() + discountPercent + "%";
    }
}