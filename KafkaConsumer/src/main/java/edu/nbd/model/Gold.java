package edu.nbd.model;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator(key = "_type", value = "gold")
public class Gold extends ClientType {

    @BsonProperty("discountPercent")
    @JsonbProperty("discountPercent")
    private int discountPercent;

    public Gold() {
        super(4, 10);
        this.discountPercent = 10;
    }

    @BsonIgnore
    @JsonbTransient
    @Override
    public String getTypeInfo() {
        return super.getTypeInfo() + discountPercent + "%";
    }

    @BsonIgnore
    @JsonbTransient
    @Override
    public String getInfo() {
        return getTypeInfo();
    }

    @Override
    public double applyDiscount(double price) {
        return (super.applyDiscount(price) / 100 * (100 - discountPercent));
    }
}