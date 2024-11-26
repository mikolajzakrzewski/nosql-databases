package edu.nbd.managers;

import edu.nbd.model.Rent;
import edu.nbd.repositories.IRepository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class RentManager implements Serializable {
    private final IRepository<Rent> rentRepository;

    public RentManager(IRepository<Rent> rentRepository) {
        Objects.requireNonNull(rentRepository, "RentRepository is null");

        this.rentRepository = rentRepository;
    }

    public Rent registerRent(Rent rent) {
        Rent newRent = rentRepository.findById(rent.getId());
        if (newRent != null) {
            rentRepository.update(rent);
        } else {
            rentRepository.add(rent);
        }
        return rent;
    }

    public void unregisterRent(Rent rent) {
        if (rent != null) {
            rent.setEndTime(LocalDateTime.now());
            rentRepository.update(rent);
        }
    }
}
