package edu.nbd.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import edu.nbd.exceptions.VehicleException;

@Entity(defaultKeyspace = "nbd")
@CqlName("vehicles")
public class Vehicle {

    @PartitionKey
    @CqlName("plate_number")
    private String plateNumber;

    @CqlName("base_price")
    private int basePrice;

    @CqlName("archived")
    private boolean archived = false;

    @CqlName("discriminator")
    protected String discriminator;

    public Vehicle(String plateNumber, int basePrice) {
        this.plateNumber = plateNumber;
        this.basePrice = basePrice;
        this.archived = false;
    }

    public Vehicle(String plateNumber, int basePrice, boolean archived, String discriminator) {
        this.plateNumber = plateNumber;
        this.basePrice = basePrice;
        this.archived = archived;
        this.discriminator = discriminator;
    }

    public Vehicle() {
    }

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

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    public String getInfo() {
        return getVehicleInfo();
    }

    public String getId() {
        return getPlateNumber();
    }
}