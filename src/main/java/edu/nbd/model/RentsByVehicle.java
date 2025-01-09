package edu.nbd.model;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

import java.time.LocalDateTime;

@Entity(defaultKeyspace = "nbd")
@CqlName("rents_by_vehicle")
public class RentsByVehicle extends Rent {

    @PartitionKey
    @CqlName("plate_number")
    private String plateNumber;

    @ClusteringColumn
    @CqlName("rent_id")
    private long rentId;

    @CqlName("personal_id")
    private String personalId;

    @CqlName("begin_time")
    private LocalDateTime beginTime;

    @CqlName("end_time")
    private LocalDateTime endTime;

    @CqlName("rent_cost")
    private double rentCost;

    @CqlName("archived")
    private boolean archived;

    public RentsByVehicle() {}

    public RentsByVehicle(long id, Client client, Vehicle vehicle, LocalDateTime beginTime) {
        super(id, client, vehicle, beginTime);
    }

    public RentsByVehicle(long id, String personalId, String plateNumber, LocalDateTime beginTime, LocalDateTime endTime, double rentCost, boolean archived) {
        super(id, personalId, plateNumber, beginTime, endTime, rentCost, archived);
    }

    public RentsByVehicle(long id, Client client, Vehicle vehicle, LocalDateTime beginTime, LocalDateTime endTime, double rentCost, boolean archived) {
        super(id, client, vehicle, beginTime, endTime, rentCost, archived);
    }

    public long getId() {
        return rentId;
    }

    public void setRentId(long rentId) {
        this.rentId = rentId;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
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
