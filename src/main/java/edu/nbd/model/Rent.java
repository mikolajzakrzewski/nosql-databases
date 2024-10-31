package edu.nbd.model;


import edu.nbd.exceptions.RentException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Rent {

    private UUID id;

    private Client client;

    private Vehicle vehicle;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private double rentCost = 0;

    public Rent(Client client, Vehicle vehicle, LocalDateTime beginTime) {
        this.client = client;
        this.vehicle = vehicle;
        this.beginTime = Objects.requireNonNullElseGet(beginTime, LocalDateTime::now).withNano(0);
    }

    public Rent() {

    }

    public String getRentInfo() {
        return id.toString() +
                client.getClientInfo() +
                vehicle.getVehicleInfo() +
                beginTime.toString() +
                (endTime != null ? endTime.toString() : "current");
    }

    public UUID getId() {
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