package edu.nbd.model;


import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator(key = "_type")
public abstract class ClientType {

    @BsonProperty("maxVehicles")
    private int maxVehicles;

    @BsonProperty("discount")
    private int discount;

    @BsonCreator
    public ClientType(@BsonProperty("maxVehicles") int maxVehicles,
                      @BsonProperty("discount") int discount) {
        this.maxVehicles = maxVehicles;
        this.discount = discount;
    }

    public int getMaxVehicles() {
        return maxVehicles;
    }

    public int getDiscount() {
        return discount;
    }

    @BsonIgnore
    public String getTypeInfo() {
        return String.valueOf(maxVehicles) + discount;
    }

    @BsonIgnore
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