package edu.nbd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "gold_type")
public class Gold extends ClientType {

    @Column
    private int discountPercent;

    public Gold() {
        super(4, 10);
        this.discountPercent = 10;
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