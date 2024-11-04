package edu.nbd.model;

import edu.nbd.exceptions.VehicleException;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

public abstract class Vehicle {

    @BsonId
    private String id;

    @BsonProperty("plateNumber")
    private String plateNumber;

    @BsonProperty("basePrice")
    private int basePrice;

    @BsonProperty("archived")
    private boolean archived = false;

    public Vehicle() {
    }

    @BsonCreator
    public Vehicle(@BsonProperty("plateNumber") String plateNumber,
                   @BsonProperty("basePrice") int basePrice) {
        this.plateNumber = plateNumber;
        this.basePrice = basePrice;
    }

    @BsonIgnore
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
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    @BsonIgnore
    public String getInfo() {
        return getVehicleInfo();
    }

    @BsonIgnore
    public String getId() {
        return this.id;
    }
}