package edu.nbd.managers;

import edu.nbd.model.Vehicle;
import edu.nbd.repositories.VehicleRepository;

import java.io.Serializable;

public class VehicleManager implements Serializable {
    private VehicleRepository vehicleRepository;

    public VehicleManager(VehicleRepository vehicleRepository) {
        if (vehicleRepository == null) {
            throw new NullPointerException("vehicleRepository is null");
        } else {
            this.vehicleRepository = vehicleRepository;
        }
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
