package edu.nbd.model;

import edu.nbd.exceptions.RentException;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Rent {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn
    private Client client;

    @ManyToOne
    @JoinColumn
    private Vehicle vehicle;

    @Column
    private LocalDateTime beginTime;

    @Column
    private LocalDateTime endTime = null;

    @Column
    private double rentCost = 0;

    public Rent(Client client, Vehicle vehicle, LocalDateTime beginTime) {
        this.client = client;
        this.vehicle = vehicle;
        this.beginTime = Objects.requireNonNullElseGet(beginTime, LocalDateTime::now);
    }

    public Rent() {

    }

    public String getRentInfo() {
        return id.toString() +
                client.getClientInfo() +
                vehicle.getVehicleInfo() +
                beginTime.toString() +
                (endTime != null ? endTime.toString() : "");
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

    public void endRent(LocalDateTime endRentTime) {
        if (endTime == null && (endRentTime.isAfter(beginTime) || endRentTime.isEqual(beginTime))) {
            endTime = endRentTime;
        } else if (endTime == null) {
            endTime = beginTime;
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