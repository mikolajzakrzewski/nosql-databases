package edu.nbd.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

import java.time.LocalDateTime;

@Entity(defaultKeyspace = "rent_a_vehicle")
@CqlName("rents_by_vehicle")
public class RentsByVehicle extends Rent {
    @CqlName("rent_id")
    private long rentId;
    private String clientId;
    @CqlName("vehicle_id")
    @PartitionKey
    private String plateNumber;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private double rentCost;
    private boolean archived;

    public RentsByVehicle() {}

    public RentsByVehicle(long id, Client client, Vehicle vehicle, LocalDateTime beginTime) {
        super(id, client, vehicle, beginTime);
    }

    public RentsByVehicle(long id, String clientId, String plateNumber, LocalDateTime beginTime, LocalDateTime endTime, double rentCost, boolean archived) {
        super(id, clientId, plateNumber, beginTime, endTime, rentCost, archived);
    }

    public RentsByVehicle(long id, Client client, Vehicle vehicle, LocalDateTime beginTime, LocalDateTime endTime, double rentCost, boolean archived) {
        super(id, client, vehicle, beginTime, endTime, rentCost, archived);
    }

    public long getRentId() {
        return rentId;
    }

    public void setRentId(long rentId) {
        this.rentId = rentId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public double getRentCost() {
        return rentCost;
    }

    public void setRentCost(double rentCost) {
        this.rentCost = rentCost;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
