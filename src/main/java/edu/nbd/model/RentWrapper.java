package edu.nbd.model;

public class RentWrapper {
    private Rent rent;
    private String rentalName;

    public RentWrapper() {
    }

    public RentWrapper(Rent rent, String rentalName) {
        this.rent = rent;
        this.rentalName = rentalName;
    }

    public Rent getRent() {
        return rent;
    }

    public void setRent(Rent rent) {
        this.rent = rent;
    }

    public String getRentalName() {
        return rentalName;
    }

    public void setRentalName(String rentalName) {
        this.rentalName = rentalName;
    }
}
