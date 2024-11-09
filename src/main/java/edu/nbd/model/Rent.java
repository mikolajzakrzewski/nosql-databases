package edu.nbd.model;


import edu.nbd.exceptions.RentException;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

public class Rent {

    @BsonId
    private long id;

    @BsonProperty("client")
    private Client client;

    @BsonProperty("vehicle")
    private Vehicle vehicle;

    @BsonProperty("beginTime")
    private LocalDateTime beginTime;

    @BsonProperty("endTime")
    private LocalDateTime endTime;

    @BsonProperty("rentCost")
    private double rentCost = 0;

    @BsonCreator
    public Rent(@BsonProperty("id") long id,
                @BsonProperty("client") Client client,
                @BsonProperty("vehicle") Vehicle vehicle,
                @BsonProperty("beginTime") LocalDateTime beginTime) {
        this.id = id;
        this.client = client;
        this.vehicle = vehicle;
        this.beginTime = Objects.requireNonNullElseGet(beginTime, LocalDateTime::now).withNano(0);
    }

    public Rent() {

    }

    @BsonIgnore
    public String getRentInfo() {
        String className = "Rent";
        return className + id +
                client.getClientInfo() +
                vehicle.getVehicleInfo() +
                beginTime.toString() +
                (endTime != null ? endTime.toString() : "current");
    }

    @BsonIgnore
    public long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endRentTime) {
        if (endTime != null) {
            throw new RentException("Rent already ended.");
        }
        if (endRentTime.isAfter(beginTime)) {
            endTime = endRentTime.withNano(0);
        } else {
            throw new RentException("Given end rent time should be after the begin time.");
        }

        double rentCostEndRent = getRentDays() * vehicle.getBasePrice();

        if (rentCostEndRent != 0) {
            rentCostEndRent = client.applyDiscount(rentCostEndRent);
        }

        rentCost = rentCostEndRent;
    }

    public int getRentDays() {
        return (endTime == null) ? 0 : (int) java.time.Duration.between(beginTime, endTime).toDays() + 1;
    }

    public double getRentCost() {
        return rentCost;
    }
}