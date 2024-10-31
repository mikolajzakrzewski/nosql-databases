package edu.nbd.model;

import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator(key = "_type", value = "gold")
public class Gold extends ClientType {

    @BsonProperty("discountPercent")
    private int discountPercent;

    public Gold() {
        super(4, 10);
        this.discountPercent = 10;
    }

    @Override
    public String getTypeInfo() {
        return super.getTypeInfo() + discountPercent + "%";
    }

    @Override
    public String getInfo() {
        return getTypeInfo();
    }

    @Override
    public double applyDiscount(double price) {
        return (super.applyDiscount(price) / 100 * (100 - discountPercent));
    }
}