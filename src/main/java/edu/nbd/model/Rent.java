package edu.nbd.model;


import edu.nbd.exceptions.RentException;

import java.time.LocalDateTime;
import java.util.Objects;

public class Rent {

    private long id;

    private String clientId;

    private Client client;

    private Vehicle vehicle;

    private String plateNumber;

    private LocalDateTime beginTime;

    private LocalDateTime endTime;

    private double rentCost;

    private boolean archived;

    public Rent(long id, Client client, Vehicle vehicle, LocalDateTime beginTime) {
        this.id = id;
        this.client = client;
        this.vehicle = vehicle;
        this.beginTime = Objects.requireNonNullElseGet(beginTime, LocalDateTime::now).withNano(0);
        this.endTime = null;
        this.archived = false;
        this.rentCost = 0;
        this.clientId = client.getPersonalID();
        this.plateNumber = vehicle.getPlateNumber();
    }

    public Rent(long id, Client client, Vehicle vehicle, LocalDateTime beginTime, LocalDateTime endTime, double rentCost, boolean archived) {
        this.id = id;
        this.client = client;
        this.vehicle = vehicle;
        this.beginTime = Objects.requireNonNullElseGet(beginTime, LocalDateTime::now).withNano(0);
        this.endTime = Objects.requireNonNullElseGet(endTime, LocalDateTime::now).withNano(0);
        this.archived = archived;
        this.rentCost = rentCost;
        this.clientId = client.getPersonalID();
        this.plateNumber = vehicle.getPlateNumber();
    }

    public Rent(long id, String clientId, String plateNumber, LocalDateTime beginTime, LocalDateTime endTime, double rentCost, boolean archived) {
        this.id = id;
        this.clientId = clientId;
        this.plateNumber = plateNumber;
        this.beginTime = Objects.requireNonNullElseGet(beginTime, LocalDateTime::now).withNano(0);
        this.endTime = Objects.requireNonNullElseGet(endTime, LocalDateTime::now).withNano(0);
        this.archived = archived;
        this.rentCost = rentCost;
    }

    public Rent() {

    }

    public String getRentInfo() {
        String className = "Rent";
        return className + id +
                client.getClientInfo() +
                vehicle.getVehicleInfo() +
                beginTime.toString() +
                (endTime != null ? endTime.toString() : "current");
    }

    public long getRentId() {
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

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
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

    public void setId(long id) {
        this.id = id;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public void setRentCost(double rentCost) {
        this.rentCost = rentCost;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }


}