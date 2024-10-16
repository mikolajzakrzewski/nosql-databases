package edu.nbd.managers;

import edu.nbd.model.Client;
import edu.nbd.model.Rent;
import edu.nbd.repositories.RentRepository;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RentManager implements Serializable {
    private RentRepository rentRepository;

    public RentManager(RentRepository rentRepository) {
        if (rentRepository == null) {
            throw new NullPointerException("RentRepository is null");
        } else {
            this.rentRepository = rentRepository;
        }
    }

    public Rent registerRent(Rent rent) {
        Client client = rent.getClient();
        if (client == null) {
            throw new NullPointerException("Client is null");
        } else if (client.isArchived()) {
            throw new IllegalArgumentException("Client is archived");
        } else if (client.getMaxVehicles() <= rentRepository.countActiveRentsByClient(client)) {
            throw new IllegalArgumentException("Client has reached the maximum number of vehicles");
        }

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
