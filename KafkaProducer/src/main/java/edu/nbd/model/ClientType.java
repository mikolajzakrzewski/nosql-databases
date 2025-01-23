package edu.nbd.model;


import jakarta.json.bind.annotation.*;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator(key = "_type")
@JsonbTypeInfo({
        @JsonbSubtype(alias = "default", type = Default.class),
        @JsonbSubtype(alias = "gold", type = Gold.class)
})
public abstract class ClientType {

    @BsonProperty("maxVehicles")
    @JsonbProperty("maxVehicles")
    private int maxVehicles;

    @BsonProperty("discount")
    @JsonbProperty("discount")
    private int discount;

    @BsonCreator
    @JsonbCreator
    public ClientType(@BsonProperty("maxVehicles") @JsonbProperty("maxVehicles") int maxVehicles,
                      @BsonProperty("discount") @JsonbProperty("discount") int discount) {
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
    @JsonbTransient
    public String getTypeInfo() {
        return String.valueOf(maxVehicles) + discount;
    }

    @BsonIgnore
    @JsonbTransient
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