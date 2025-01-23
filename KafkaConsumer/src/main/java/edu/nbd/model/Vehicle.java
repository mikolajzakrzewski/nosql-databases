package edu.nbd.model;

import edu.nbd.exceptions.VehicleException;
import jakarta.json.bind.annotation.*;
import org.bson.codecs.pojo.annotations.*;

@JsonbTypeInfo({
        @JsonbSubtype(alias = "bicycle", type = Bicycle.class),
        @JsonbSubtype(alias = "motor_vehicle", type = MotorVehicle.class)
})
@BsonDiscriminator(key = "_type")
public abstract class Vehicle {

    @BsonId
    @JsonbProperty("plateNumber")
    private String plateNumber;

    @BsonProperty("basePrice")
    @JsonbProperty("basePrice")
    private int basePrice;

    @BsonProperty("archived")
    @JsonbProperty("archived")
    private boolean archived = false;

    @BsonProperty("rented")
    @JsonbProperty("rented")
    private int rented = 0;

    public Vehicle() {
    }

    @BsonCreator
    @JsonbCreator
    public Vehicle(@BsonProperty("plateNumber") @JsonbProperty("plateNumber") String plateNumber,
                   @BsonProperty("basePrice") @JsonbProperty("basePrice") int basePrice) {
        this.plateNumber = plateNumber;
        this.basePrice = basePrice;
        this.archived = false;
        this.rented = 0;
    }

    @BsonIgnore
    @JsonbTransient  // Exclude from serialization and deserialization
    public String getVehicleInfo() {
        return plateNumber + basePrice;
    }

    @JsonbProperty("plateNumber")  // Serialize plateNumber as JSON property
    public String getPlateNumber() {
        return plateNumber;
    }

    @JsonbProperty("plateNumber")  // Serialize plateNumber as JSON property
    public void setPlateNumber(String plateNumber) {
        if (plateNumber != null && !plateNumber.isEmpty()) {
            this.plateNumber = plateNumber;
        } else {
            throw new VehicleException("Given plate number shouldn't be empty.");
        }
    }

    @JsonbProperty("basePrice")  // Serialize basePrice as JSON property
    public int getBasePrice() {
        return basePrice;
    }

    @JsonbProperty("basePrice")  // Serialize basePrice as JSON property
    public void setBasePrice(int basePrice) {
        if (basePrice > 0) {
            this.basePrice = basePrice;
        } else {
            throw new VehicleException("Given base price should be more than zero.");
        }
    }

    @BsonIgnore
    @JsonbTransient
    public double getActualRentalPrice() {
        return basePrice;
    }

    @JsonbProperty("archived")
    public boolean isArchived() {
        return archived;
    }

    @JsonbProperty("archived")
    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    @JsonbProperty("rented")
    public int getRented() {
        return rented;
    }

    @BsonIgnore
    @JsonbTransient
    public String getInfo() {
        return getVehicleInfo();
    }

    @BsonIgnore
    @JsonbTransient
    public String getId() {
        return getPlateNumber();
    }
}