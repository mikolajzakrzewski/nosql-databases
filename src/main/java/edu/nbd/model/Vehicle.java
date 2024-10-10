package edu.nbd.model;

import com.sun.istack.NotNull;
import edu.nbd.exceptions.VehicleException;
import jakarta.persistence.*;
import java.util.UUID;

import java.rmi.AccessException;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    @NotNull
    private String plateNumber;

    @Column
    private int basePrice;

    @Column
    private boolean archive;

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
        return archive;
    }

    public void setArchive(boolean isArchived) {
        this.archive = isArchived;
    }

    public String getInfo() {
        return getVehicleInfo();
    }

    public String getId() {
        return getPlateNumber();
    }
}