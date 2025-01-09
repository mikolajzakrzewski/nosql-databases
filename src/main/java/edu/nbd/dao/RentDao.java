package edu.nbd.dao;

import com.datastax.oss.driver.api.mapper.annotations.*;
import edu.nbd.model.Rent;
import edu.nbd.providers.RentProvider;

import java.util.List;

@Dao
public interface RentDao {
    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = RentProvider.class)
    List<Rent> findByClientId(String clientId);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = RentProvider.class)
    List<Rent> findByVehicleId(String vehicleId);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = RentProvider.class)
    Rent findById(long id);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentProvider.class)
    void add(Rent rent);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentProvider.class)
    void update(Rent rent);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentProvider.class)
    void delete(Rent rent);
}
