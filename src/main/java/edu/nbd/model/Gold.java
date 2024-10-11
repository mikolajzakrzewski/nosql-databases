package edu.nbd.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("Gold")
public class Gold extends ClientType {

    @Column
    @NotNull
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