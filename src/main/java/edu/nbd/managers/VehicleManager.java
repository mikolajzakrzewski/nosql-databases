package edu.nbd.managers;

import edu.nbd.model.Vehicle;
import edu.nbd.repositories.IRepository;

import java.io.Serializable;
import java.util.Objects;

public class VehicleManager implements Serializable {
    private final IRepository<Vehicle> vehicleRepository;

    public VehicleManager(IRepository<Vehicle> vehicleRepository) {
        Objects.requireNonNull(vehicleRepository, "VehicleRepository is null");

        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle registerVehicle(Vehicle vehicle) {
        Vehicle newVehicle = vehicleRepository.findById(vehicle.getId());
        if (newVehicle != null) {
            vehicle.setArchived(false);
            vehicleRepository.update(vehicle);
        } else {
            vehicleRepository.add(vehicle);
        }
        return vehicle;
    }

    public void unregisterVehicle(Vehicle vehicle) {
        if (vehicle != null) {
            vehicle.setArchived(true);
            vehicleRepository.update(vehicle);
        }
    }
}
