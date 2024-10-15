package edu.nbd.model;

import edu.nbd.exceptions.VehicleException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Vehicle {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    @NotEmpty
    private String plateNumber;

    @Column
    @NotNull
    @Min(1)
    private int basePrice;

    @Column
    @NotNull
    private boolean archived = false;

    @Version
    private long version;

    public Vehicle() {

    }

    public Vehicle(String plateNumber, int basePrice) {
        this.plateNumber = plateNumber;
        this.basePrice = basePrice;
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

    public String getInfo() {
        return getVehicleInfo();
    }

    public UUID getId() {
        return this.id;
    }
}