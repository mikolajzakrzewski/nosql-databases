package edu.nbd.dao;

import com.datastax.oss.driver.api.mapper.annotations.*;
import edu.nbd.model.Bicycle;
import edu.nbd.model.MotorVehicle;
import edu.nbd.model.Vehicle;
import edu.nbd.providers.VehicleProvider;

@Dao
public interface VehicleDao {
    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = VehicleProvider.class, entityHelpers = {Bicycle.class, MotorVehicle.class})
    Vehicle findById(String plateNumber);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = VehicleProvider.class, entityHelpers = {Bicycle.class, MotorVehicle.class})
    void add(Vehicle vehicle);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = VehicleProvider.class, entityHelpers = {Bicycle.class, MotorVehicle.class})
    void update(Vehicle vehicle);

    @Delete
    void delete(Vehicle vehicle);
}
